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

    private static TruthTable generateTruthTable(final Parameters options) {
        final List<String> variables = PropositionalLogic.generateVariables(options);
        final boolean[] truthValues = new boolean[(int)Math.pow(2, variables.size())];
        for (int i = 0; i < truthValues.length; i++) {
            truthValues[i] = Main.RANDOM.nextBoolean();
        }
        final TruthTable result = new TruthTable(variables, truthValues );
        return result;
    }

    private static TruthTable parseTruthTable(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        try {
            return TruthTable.parse(reader.readLine());
        } catch (final TruthTableParseException e) {
            throw new IOException(e);
        }
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

    private static void printToFormulaExercise(
        final TruthTable truthTable,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Geben Sie zu der folgenden Wahrheitstabelle eine aussagenlogische Formel an:\\\\");
        Main.newLine(writer);
        PropositionalLogic.printTruthTable(truthTable, false, false, writer);
        Main.newLine(writer);
    }

    private static void printToFormulaSolution(
        final PropositionalFormula formula,
        final BufferedWriter writer
    ) throws IOException {
        ConversionFromTruthTable.printDNFFormula(formula, writer);
        Main.newLine(writer);
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
        final TruthTable truthTable =
            new ParserAndGenerator<TruthTable>(
                ConversionFromTruthTable::parseTruthTable,
                ConversionFromTruthTable::generateTruthTable
            ).getResult(input.options);
        final PropositionalFormula formula = ConversionFromTruthTable.fromTruthTable(truthTable);
        ConversionFromTruthTable.printToFormulaExercise(truthTable, input.exerciseWriter);
        ConversionFromTruthTable.printToFormulaSolution(formula, input.solutionWriter);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "3";
        return result; //TODO
    }

}
