package exercisegenerator.algorithms.logic;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.logic.*;

public class DPLL implements AlgorithmImplementation {

    public static DPLLNode dpll(final Set<Clause> clauses) {
        final Optional<DPLLNode> propagated = DPLL.unitPropagation(clauses);
        final Optional<DPLLNode> assigned =
            propagated.isEmpty() ?
                DPLL.pureAssignment(clauses) :
                    Optional.of(
                        propagated.get().addLeftmost(DPLL.pureAssignment(propagated.get().getLeftmostClauses()))
                    );
        final Set<Clause> latest = assigned.isEmpty() ? clauses : assigned.get().getLeftmostClauses();
        if (latest.isEmpty() || latest.contains(Clause.EMPTY)) {
            return assigned.isEmpty() ?
                new DPLLNode(clauses) :
                    assigned.get().addLeftmost(Optional.of(new DPLLNode(latest)));
        }
        if (assigned.isEmpty()) {
            final PropositionalVariable chosen = DPLL.chooseVariable(clauses);
            final DPLLNode left = DPLL.dpll(DPLL.setTruth(chosen, true, clauses));
            if (left.isSAT()) {
                return new DPLLNode(clauses, Optional.of(left));
            }
            return new DPLLNode(
                clauses,
                Optional.of(left),
                Optional.of(DPLL.dpll(DPLL.setTruth(chosen, false, clauses)))
            );
        }
        final PropositionalVariable chosen = DPLL.chooseVariable(latest);
        final DPLLNode left = DPLL.dpll(DPLL.setTruth(chosen, true, latest));
        if (left.isSAT()) {
            return assigned.get().addLeftmost(Optional.of(left));
        }
        return assigned.get()
            .addRightToLeftmost(DPLL.dpll(DPLL.setTruth(chosen, false, latest)))
            .addLeftmost(Optional.of(left));
    }

    static Set<Clause> parseClauses(final String toParse) {
        if (toParse == null || toParse.isBlank()) {
            return Collections.emptySet();
        }
        final Set<String> clauses = new LinkedHashSet<String>();
        final int length = toParse.length();
        int clauseStart = -1;
        for (int i = 0; i < length; i++) {
            switch (toParse.charAt(i)) {
            case '{':
                clauseStart = i + 1;
                break;
            case '}':
                clauses.add(toParse.substring(clauseStart, i));
                break;
            }
        }
        return clauses.stream().map(DPLL::parseClause).collect(Collectors.toSet());
    }

    private static PropositionalVariable chooseVariable(final Set<Clause> clauses) {
        return clauses.stream().flatMap(Clause::stream).map(Literal::variable).findAny().get();
    }

    private static boolean containsPureLiteral(final Set<Clause> clauses) {
        final Set<PropositionalVariable> positive = new LinkedHashSet<PropositionalVariable>();
        final Set<PropositionalVariable> negative = new LinkedHashSet<PropositionalVariable>();
        for (final Clause clause : clauses) {
            for (final Literal literal : clause) {
                if (literal.negative()) {
                    negative.add(literal.variable());
                } else {
                    positive.add(literal.variable());
                }
            }
        }
        return positive.stream().filter(v -> !negative.contains(v)).findAny().isPresent()
            || negative.stream().filter(v -> !positive.contains(v)).findAny().isPresent();
    }

    private static boolean containsUnitClause(final Set<Clause> clauses) {
        return clauses.stream().anyMatch(clause -> clause.size() == 1);
    }

    private static Optional<DPLLNode> deterministicAssignments(
        final Set<Clause> clauses,
        final Predicate<Set<Clause>> assignmentApplicable,
        final Function<Set<Clause>, Literal> assignmentSelection
    ) {
        Optional<DPLLNode> result = Optional.empty();
        Set<Clause> currentClauses = clauses;
        while (assignmentApplicable.test(currentClauses)) {
            final Literal literal = assignmentSelection.apply(currentClauses);
            currentClauses = DPLL.setTruth(literal.variable(), !literal.negative(), currentClauses);
            result =
                result.isEmpty() ?
                    Optional.of(new DPLLNode(currentClauses)) :
                        Optional.of(result.get().addLeftmost(Optional.of(new DPLLNode(currentClauses))));
        }
        return result;
    }

    private static Clause parseClause(final String toParse) {
        if (toParse == null || toParse.isBlank()) {
            return new Clause();
        }
        final String[] literals = toParse.replaceAll("\\s", "").split(",");
        return Arrays.stream(literals).map(DPLL::parseLiteral).collect(Collectors.toCollection(Clause::new));
    }

    private static Literal parseLiteral(final String toParse) {
        if (toParse.startsWith("!")) {
            return new Literal(new PropositionalVariable(toParse.substring(1)), true);
        }
        return new Literal(new PropositionalVariable(toParse), false);
    }

    private static Optional<DPLLNode> pureAssignment(final Set<Clause> clauses) {
        return DPLL.deterministicAssignments(clauses, DPLL::containsPureLiteral, DPLL::selectPureLiteral);
    }

    private static Literal selectPureLiteral(final Set<Clause> clauses) {
        final Set<PropositionalVariable> positive = new LinkedHashSet<PropositionalVariable>();
        final Set<PropositionalVariable> negative = new LinkedHashSet<PropositionalVariable>();
        for (final Clause clause : clauses) {
            for (final Literal literal : clause) {
                if (literal.negative()) {
                    negative.add(literal.variable());
                } else {
                    positive.add(literal.variable());
                }
            }
        }
        final Optional<PropositionalVariable> pure = positive.stream().filter(v -> !negative.contains(v)).findAny();
        if (pure.isPresent()) {
            return new Literal(pure.get(), false);
        }
        return new Literal(negative.stream().filter(v -> !positive.contains(v)).findAny().get(), true);
    }

    private static Literal selectUnitLiteral(final Set<Clause> clauses) {
        return clauses.stream().filter(clause -> clause.size() == 1).findAny().get().getFirst();
    }

    private static Set<Clause> setTruth(
        final PropositionalVariable chosen,
        final boolean truth,
        final Set<Clause> clauses
    ) {
        return clauses.stream()
            .map(clause -> clause.setTruth(chosen, truth))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toSet());
    }

    private static Optional<DPLLNode> unitPropagation(final Set<Clause> clauses) {
        return DPLL.deterministicAssignments(clauses, DPLL::containsUnitClause, DPLL::selectUnitLiteral);
    }

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "3";
        return result; //TODO
    }

}
