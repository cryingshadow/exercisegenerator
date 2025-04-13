package exercisegenerator.algorithms.logic;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.logic.*;

public class DPLL implements AlgorithmImplementation<ClauseSet, DPLLNode> {

    public static final DPLL INSTANCE = new DPLL();

    static ClauseSet parseClauses(final String toParse) {
        if (toParse == null || toParse.isBlank()) {
            return ClauseSet.EMPTY;
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
        return clauses.stream().map(DPLL::parseClause).collect(Collectors.toCollection(ClauseSet::new));
    }

    private static PropositionalVariable chooseVariable(final ClauseSet clauses) {
        return clauses.stream().flatMap(Clause::stream).map(Literal::variable).findAny().get();
    }

    private static boolean containsPureLiteral(final ClauseSet clauses) {
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

    private static boolean containsUnitClause(final ClauseSet clauses) {
        return clauses.stream().anyMatch(clause -> clause.size() == 1);
    }

    private static Optional<DPLLNode> deterministicAssignments(
        final ClauseSet clauses,
        final Predicate<ClauseSet> assignmentApplicable,
        final Function<ClauseSet, Literal> assignmentSelection
    ) {
        Optional<DPLLNode> result = Optional.empty();
        ClauseSet currentClauses = clauses;
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

    private static Clause generateClause(final List<PropositionalVariable> variables) {
        final int numberOfLiterals = Main.RANDOM.nextInt(10) + 1;
        return Stream.generate(() -> DPLL.generateLiteral(variables))
            .limit(numberOfLiterals)
            .collect(Collectors.toCollection(Clause::new));
    }

    private static ClauseSet generateClauseSet(final Parameters<Flag> options) {
        final int numberOfVariables = AlgorithmImplementation.parseOrGenerateLength(2, 10, options);
        final List<PropositionalVariable> variables =
            Stream.iterate(65, x -> x + 1)
            .limit(numberOfVariables)
            .map(x -> new PropositionalVariable(Character.toString(x)))
            .toList();
        final int numberOfClauses = Main.RANDOM.nextInt(16) + 5;
        return Stream.generate(() -> DPLL.generateClause(variables)).limit(numberOfClauses).collect(Collectors.toCollection(ClauseSet::new));
    }

    private static Literal generateLiteral(final List<PropositionalVariable> variables) {
        return new Literal(variables.get(Main.RANDOM.nextInt(variables.size())), Main.RANDOM.nextBoolean());
    }

    private static Clause parseClause(final String toParse) {
        if (toParse == null || toParse.isBlank()) {
            return new Clause();
        }
        final String[] literals = toParse.replaceAll("\\s", "").split(",");
        return Arrays.stream(literals).map(DPLL::parseLiteral).collect(Collectors.toCollection(Clause::new));
    }

    private static ClauseSet parseClauseSet(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        return DPLL.parseClauses(reader.readLine());
    }

    private static Literal parseLiteral(final String toParse) {
        if (toParse.startsWith("!")) {
            return new Literal(new PropositionalVariable(toParse.substring(1)), true);
        }
        return new Literal(new PropositionalVariable(toParse), false);
    }

    private static Optional<DPLLNode> pureAssignment(final ClauseSet clauses) {
        return DPLL.deterministicAssignments(clauses, DPLL::containsPureLiteral, DPLL::selectPureLiteral);
    }

    private static Literal selectPureLiteral(final ClauseSet clauses) {
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

    private static Literal selectUnitLiteral(final ClauseSet clauses) {
        return clauses.stream().filter(clause -> clause.size() == 1).findAny().get().getFirst();
    }

    private static ClauseSet setTruth(
        final PropositionalVariable chosen,
        final boolean truth,
        final ClauseSet clauses
    ) {
        return clauses.stream()
            .map(clause -> clause.setTruth(chosen, truth))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toCollection(ClauseSet::new));
    }

    private static Optional<DPLLNode> unitPropagation(final ClauseSet clauses) {
        return DPLL.deterministicAssignments(clauses, DPLL::containsUnitClause, DPLL::selectUnitLiteral);
    }

    private DPLL() {}

    @Override
    public DPLLNode apply(final ClauseSet clauses) {
        DPLLNode result = new DPLLNode(clauses).addLeftmost(DPLL.unitPropagation(clauses));
        result = result.addLeftmost(DPLL.pureAssignment(result.getLeftmostClauses()));
        final ClauseSet latest = result.getLeftmostClauses();
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
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "3";
        return result; //TODO
    }

    @Override
    public ClauseSet parseOrGenerateProblem(final Parameters<Flag> options) throws IOException {
        return new ParserAndGenerator<ClauseSet>(
            DPLL::parseClauseSet,
            DPLL::generateClauseSet
        ).getResult(options);
    }

    @Override
    public void printExercise(
        final ClauseSet problem,
        final DPLLNode solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write(
            "Überprüfen Sie mithilfe des \\emphasize{DPLL-Algorithmus}, ob die folgende Klauselmenge erfüllbar ist:"
        );
        Main.newLine(writer);
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
        if (problem.isEmpty()) {
            writer.write("$\\{\\}$");
            Main.newLine(writer);
        } else {
            LaTeXUtils.printTikzBeginning(TikZStyle.CLAUSE_SET, writer);
            writer.write("\\node (start) {$\\{$};");
            Main.newLine(writer);
            final Iterator<Clause> iterator = problem.iterator();
            writer.write("\\node (c1) [below right=of start.south west,anchor=south west] {$");
            writer.write(iterator.next().toString());
            writer.write("$");
            int i = 2;
            while (iterator.hasNext()) {
                writer.write(",};");
                Main.newLine(writer);
                writer.write("\\node (c");
                writer.write(String.valueOf(i));
                writer.write(") [below=of c");
                writer.write(String.valueOf(i - 1));
                writer.write(".south west,anchor=south west] {$");
                writer.write(iterator.next().toString());
                writer.write("$");
                i++;
            }
            writer.write("};");
            Main.newLine(writer);
            writer.write("\\node (end) [below left=of c");
            writer.write(String.valueOf(i - 1));
            writer.write(".south west,anchor=south west] {$\\}$};");
            Main.newLine(writer);
            LaTeXUtils.printTikzEnd(writer);
        }
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
        writer.write("Geben Sie dazu auch den zugehörigen DPLL-Baum an.");
        Main.newLine(writer);
        Main.newLine(writer);
    }

    @Override
    public void printSolution(
        final ClauseSet problem,
        final DPLLNode solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printAdjustboxBeginning(writer, "max width=\\columnwidth", "center");
        LaTeXUtils.printTikzBeginning(TikZStyle.BTREE, writer);
        writer.write(solution.toString());
        Main.newLine(writer);
        LaTeXUtils.printTikzEnd(writer);
        LaTeXUtils.printAdjustboxEnd(writer);
        LaTeXUtils.printVerticalProtectedSpace(writer);
        writer.write("Ergebnis: ");
        if (solution.isSAT()) {
            writer.write("erfüllbar");
        } else {
            writer.write("unerfüllbar");
        }
        Main.newLine(writer);
        Main.newLine(writer);
    }

}
