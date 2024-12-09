package exercisegenerator.algorithms.logic;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.logic.*;

public class DPLL implements AlgorithmImplementation<Set<Clause>, DPLLNode> {

    public static final DPLL INSTANCE = new DPLL();

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

    private DPLL() {}

    @Override
    public DPLLNode apply(final Set<Clause> clauses) {
        DPLLNode result = new DPLLNode(clauses).addLeftmost(DPLL.unitPropagation(clauses));
        result = result.addLeftmost(DPLL.pureAssignment(result.getLeftmostClauses()));
        final Set<Clause> latest = result.getLeftmostClauses();
        if (latest.isEmpty() || latest.contains(Clause.EMPTY)) {
            return result;
        }
        final PropositionalVariable chosen = DPLL.chooseVariable(latest);
        final DPLLNode left = this.apply(DPLL.setTruth(chosen, true, latest));
        if (left.isSAT()) {
            return result.addLeftmost(Optional.of(left));
        }
        return result
            .addRightToLeftmost(this.apply(DPLL.setTruth(chosen, false, latest)))
            .addLeftmost(Optional.of(left));
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

    @Override
    public Set<Clause> parseOrGenerateProblem(final Parameters options) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void printExercise(
        final Set<Clause> problem,
        final DPLLNode solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void printSolution(
        final Set<Clause> problem,
        final DPLLNode solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        // TODO Auto-generated method stub

    }

}
