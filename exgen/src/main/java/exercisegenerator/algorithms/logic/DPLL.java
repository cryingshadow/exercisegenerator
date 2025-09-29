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

    private static Optional<DPLLAssignment> deterministicAssignments(
        final ClauseSet clauses,
        final Predicate<ClauseSet> assignmentApplicable,
        final Function<ClauseSet, Literal> assignmentSelection
    ) {
        Optional<DPLLAssignment> result = Optional.empty();
        ClauseSet currentClauses = clauses;
        while (assignmentApplicable.test(currentClauses)) {
            if (currentClauses.contains(Clause.EMPTY)) {
                return result;
            }
            final Literal literal = assignmentSelection.apply(currentClauses);
            currentClauses = DPLL.setTruth(literal.variable(), !literal.negative(), currentClauses);
            result =
                result.isEmpty() ?
                    Optional.of(
                        new DPLLAssignment(literal.variable(), !literal.negative(), new DPLLNode(currentClauses))
                    ) :
                        Optional.of(
                            result.get().addLeftmost(
                                Optional.of(
                                    new DPLLAssignment(
                                        literal.variable(),
                                        !literal.negative(),
                                        new DPLLNode(currentClauses)
                                    )
                                )
                            )
                        );
        }
        return result;
    }

    private static Clause generateClause(final List<PropositionalVariable> variables) {
        final int numberOfLiterals = Main.RANDOM.nextInt(10) + 1;
        return Stream.generate(() -> DPLL.generateLiteral(variables))
            .limit(numberOfLiterals)
            .collect(Collectors.toCollection(Clause::new));
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

    private static Literal parseLiteral(final String toParse) {
        if (toParse.startsWith("!")) {
            return new Literal(new PropositionalVariable(toParse.substring(1)), true);
        }
        return new Literal(new PropositionalVariable(toParse), false);
    }

    private static Optional<DPLLAssignment> pureAssignment(final ClauseSet clauses) {
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

    private static Optional<DPLLAssignment> unitPropagation(final ClauseSet clauses) {
        return DPLL.deterministicAssignments(clauses, DPLL::containsUnitClause, DPLL::selectUnitLiteral);
    }

    private DPLL() {}

    @Override
    public DPLLNode apply(final ClauseSet clauses) {
        if (clauses.isEmpty() || clauses.contains(Clause.EMPTY)) {
            return new DPLLNode(clauses);
        }
        DPLLNode result = new DPLLNode(clauses).addLeftmost(DPLL.unitPropagation(clauses));
        result = result.addLeftmost(DPLL.pureAssignment(result.getLeftmostClauses()));
        final ClauseSet latest = result.getLeftmostClauses();
        if (latest.isEmpty() || latest.contains(Clause.EMPTY)) {
            return result;
        }
        final PropositionalVariable chosen = DPLL.chooseVariable(latest);
        final DPLLNode left = this.apply(DPLL.setTruth(chosen, true, latest));
        if (left.isSAT()) {
            return result.addLeftmost(Optional.of(new DPLLAssignment(chosen, true, left)));
        }
        return result
            .addRightToLeftmost(new DPLLAssignment(chosen, false, this.apply(DPLL.setTruth(chosen, false, latest))))
            .addLeftmost(Optional.of(new DPLLAssignment(chosen, true, left)));
    }

    @Override
    public String commandPrefix() {
        return "Dpll";
    }

    @Override
    public ClauseSet generateProblem(final Parameters<Flag> options) {
        final int numberOfVariables = AlgorithmImplementation.parseOrGenerateLength(2, 10, options);
        final List<PropositionalVariable> variables =
            Stream.iterate(65, x -> x + 1)
            .limit(numberOfVariables)
            .map(x -> new PropositionalVariable(Character.toString(x)))
            .toList();
        final int numberOfClauses = Main.RANDOM.nextInt(16) + 5;
        return Stream.generate(() -> DPLL.generateClause(variables))
            .limit(numberOfClauses)
            .collect(Collectors.toCollection(ClauseSet::new));
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "3";
        return result; //TODO
    }

    @Override
    public List<ClauseSet> parseProblems(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        final List<ClauseSet> result = new ArrayList<ClauseSet>();
        String line = reader.readLine();
        while (line != null) {
            if (!line.isBlank()) {
                result.add(DPLL.parseClauses(line));
            }
            line = reader.readLine();
        }
        return result;
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<ClauseSet> problems,
        final List<DPLLNode> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("\\\"Uberpr\\\"ufen Sie mithilfe des \\emphasize{DPLL-Algorithmus}, ob die folgenden ");
        writer.write("\\emphasize{Klauselmengen} jeweils erf\\\"ullbar sind. Geben Sie dazu auch den jeweiligen ");
        writer.write("DPLL-Baum an.\\\\");
        Main.newLine(writer);
    }

    @Override
    public void printBeforeSingleProblemInstance(
        final ClauseSet problem,
        final DPLLNode solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("\\\"Uberpr\\\"ufen Sie mithilfe des \\emphasize{DPLL-Algorithmus}, ob die folgende ");
        writer.write("\\emphasize{Klauselmenge} erf\\\"ullbar ist. Geben Sie dazu auch den zugeh\\\"origen DPLL-Baum ");
        writer.write("an.");
        Main.newLine(writer);
    }

    @Override
    public void printProblemInstance(
        final ClauseSet problem,
        final DPLLNode solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
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
    }

    @Override
    public void printSolutionInstance(
        final ClauseSet problem,
        final DPLLNode solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printAdjustboxBeginning(writer);
        LaTeXUtils.printTikzBeginning(TikZStyle.DPLLTREE, writer);
        writer.write(solution.toString());
        Main.newLine(writer);
        LaTeXUtils.printTikzEnd(writer);
        LaTeXUtils.printAdjustboxEnd(writer);
        LaTeXUtils.printVerticalProtectedSpace(writer);
        writer.write("Ergebnis: ");
        if (solution.isSAT()) {
            writer.write("erf\\\"ullbar");
        } else {
            writer.write("unerf\\\"ullbar");
        }
        Main.newLine(writer);
    }

    @Override
    public void printSolutionSpace(
        final ClauseSet problem,
        final DPLLNode solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {}

}
