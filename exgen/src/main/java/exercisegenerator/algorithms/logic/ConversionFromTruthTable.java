package exercisegenerator.algorithms.logic;

import java.io.*;
import java.util.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.logic.*;

public class ConversionFromTruthTable implements AlgorithmImplementation<TruthTable, PropositionalFormula> {

    public static final ConversionFromTruthTable INSTANCE = new ConversionFromTruthTable();

    private static void printDNFFormula(final PropositionalFormula formula, final BufferedWriter writer)
    throws IOException {
        LaTeXUtils.printBeginning("align*", writer);
        writer.write(" & ");
        if (formula.isConstant()) {
            writer.write(formula.evaluate(null) ? "\\code{1}" : "\\code{0}");
        } else {
            writer.write(
                formula.visit(
                    new FormulaVisitor<String>() {

                        @Override
                        public String onConjunction(final List<String> children) {
                            return String.format("%s", String.join(" \\wedge ", children));
                        }

                        @Override
                        public String onDisjunction(final List<String> children) {
                            return String.format("%s", String.join("\\\\" + Main.lineSeparator + "\\vee & ", children));
                        }

                        @Override
                        public String onEquivalence(final String left, final String right) {
                            throw new IllegalStateException("An equivalence should not appear in a DNF!");
                        }

                        @Override
                        public String onFalse() {
                            return "\\code{0}";
                        }

                        @Override
                        public String onImplication(final String antecedence, final String consequence) {
                            throw new IllegalStateException("An implication should not appear in a DNF!");
                        }

                        @Override
                        public String onNegation(final String child) {
                            return "\\neg" + child;
                        }

                        @Override
                        public String onTrue() {
                            return "\\code{1}";
                        }

                        @Override
                        public String onVariable(final String name) {
                            return String.format("\\var{%s}", name);
                        }

                        @Override
                        public String onXor(final String left, final String right) {
                            throw new IllegalStateException("An XOR should not appear in a DNF!");
                        }

                    }
                )
            );
        }
        Main.newLine(writer);
        LaTeXUtils.printEnd("align*", writer);
    }

    private static PropositionalFormula toConjunction(final PropositionalInterpretation model) {
        final List<String> names = new ArrayList<String>(model.keySet());
        Collections.sort(names);
        return Conjunction.createConjunction(
            names.stream()
                .map(name -> new PropositionalVariable(name))
                .map(var -> model.get(var.name) ? var : var.negate())
                .toList()
        );
    }

    private ConversionFromTruthTable() {}

    @Override
    public PropositionalFormula apply(final TruthTable table) {
        return Disjunction.createDisjunction(table.getModels().stream().map(ConversionFromTruthTable::toConjunction));
    }

    @Override
    public String commandPrefix() {
        return "FromTruthTable";
    }

    @Override
    public TruthTable generateProblem(final Parameters<Flag> options) {
        final Set<String> variables = new TreeSet<String>(PropositionalLogic.generateVariables(options));
        final boolean[] truthValues = new boolean[(int)Math.pow(2, variables.size())];
        for (int i = 0; i < truthValues.length; i++) {
            truthValues[i] = Main.RANDOM.nextBoolean();
        }
        final TruthTable result = new TruthTable(variables, truthValues );
        return result;
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "3";
        return result; //TODO
    }

    @Override
    public List<TruthTable> parseProblems(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        final List<TruthTable> result = new ArrayList<TruthTable>();
        try {
            String line = reader.readLine();
            while (line != null) {
                if (!line.isBlank()) {
                    result.add(TruthTable.parse(line));
                }
                line = reader.readLine();
            }
        } catch (final TruthTableParseException e) {
            throw new IOException(e);
        }
        return result;
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<TruthTable> problems,
        final List<PropositionalFormula> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write(
            "Geben Sie zu den folgenden Wahrheitstabellen jeweils eine aussagenlogische Formel an.\\\\[1.5ex]"
        );
        Main.newLine(writer);
    }

    @Override
    public void printBeforeSingleProblemInstance(
        final TruthTable problem,
        final PropositionalFormula solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Geben Sie zu der folgenden Wahrheitstabelle eine aussagenlogische Formel an:\\\\[1.5ex]");
        Main.newLine(writer);
    }

    @Override
    public void printProblemInstance(
        final TruthTable problem,
        final PropositionalFormula solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        PropositionalLogic.printTruthTable(problem, false, false, writer);
    }

    @Override
    public void printSolutionInstance(
        final TruthTable problem,
        final PropositionalFormula solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printVerticalProtectedSpace("-10ex", writer);
        ConversionFromTruthTable.printDNFFormula(solution, writer);
        Main.newLine(writer);
    }

    @Override
    public void printSolutionSpace(
        final TruthTable problem,
        final PropositionalFormula solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {}

}
