package exercisegenerator.algorithms.logic;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.logic.*;

public class ConversionFromTruthTable implements AlgorithmImplementation {

    public static final ConversionFromTruthTable INSTANCE = new ConversionFromTruthTable();

    public static PropositionalFormula fromTruthTable(final TruthTable table) {
        final List<PropositionalFormula> disjuncts =
            table.getModels().stream().map(ConversionFromTruthTable::toConjunction).toList();
        return Disjunction.createDisjunction(disjuncts);
    }

    private static List<TruthTable> generateTruthTables(final Parameters options) {
        final Random gen = new Random();
        final List<String> variables = PropositionalLogic.generateVariables(options);
        final boolean[] truthValues = new boolean[(int)Math.pow(2, variables.size())];
        for (int i = 0; i < truthValues.length; i++) {
            truthValues[i] = gen.nextBoolean();
        }
        final TruthTable result = new TruthTable(variables, truthValues );
        return Collections.singletonList(result);
    }

    private static List<TruthTable> parseTruthTables(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        final List<TruthTable> result = new ArrayList<TruthTable>();
        String line = reader.readLine();
        while (line != null) {
            if (!line.isBlank()) {
                try {
                    result.add(TruthTable.parse(line));
                } catch (final TruthTableParseException e) {
                    throw new IOException(e);
                }
            }
            line = reader.readLine();
        }
        return result;
    }

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
                        public String onFalse() {
                            return "\\code{0}";
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

                    }
                )
            );
        }
        Main.newLine(writer);
        LaTeXUtils.printEnd("align*", writer);
    }

    private static void printToFormulaExerciseAndSolution(
        final List<TruthTable> truthTables,
        final List<PropositionalFormula> formulas,
        final AlgorithmInput input
    ) throws IOException {
        final BufferedWriter exWriter = input.exerciseWriter;
        final BufferedWriter solWriter = input.solutionWriter;
        final int size = formulas.size();
        if (size == 1) {
            exWriter.write("Geben Sie zu der folgenden Wahrheitstabelle eine aussagenlogische Formel an:\\\\");
        } else {
            exWriter.write(
                "Geben Sie zu den folgenden Wahrheitstabellen jeweils eine aussagenlogische Formel an:\\\\"
            );
        }
        Main.newLine(exWriter);
        LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), input.options, exWriter);
        boolean first = true;
        for (int i = 0; i < size; i++) {
            if (first) {
                first = false;
            } else {
                LaTeXUtils.printVerticalProtectedSpace("4cm", exWriter);
                LaTeXUtils.printVerticalProtectedSpace(solWriter);
           }
            final TruthTable table = truthTables.get(i);
            final PropositionalFormula formula = formulas.get(i);
            PropositionalLogic.printTruthTable(table, false, false, exWriter);
            PropositionalLogic.printTruthTable(table, false, false, solWriter);
            ConversionFromTruthTable.printDNFFormula(formula, solWriter);
        }
        LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), input.options, exWriter);
        Main.newLine(solWriter);
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
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final List<TruthTable> truthTables =
            new ParserAndGenerator<List<TruthTable>>(
                ConversionFromTruthTable::parseTruthTables,
                ConversionFromTruthTable::generateTruthTables
            ).getResult(input.options);
        final List<PropositionalFormula> formulas =
            truthTables.stream().map(ConversionFromTruthTable::fromTruthTable).toList();
        ConversionFromTruthTable.printToFormulaExerciseAndSolution(truthTables, formulas, input);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "3";
        return result; //TODO
    }

}
