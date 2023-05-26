package exercisegenerator.algorithms.logic;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.logic.*;

abstract class PropositionalLogic {

    static List<String> generateVariables(final Parameters options) {
        final List<String> variables = new ArrayList<String>();
        final int size = Integer.parseInt(options.getOrDefault(Flag.LENGTH, "3"));
        if (size > 26) {
            throw new IllegalArgumentException("Formulas with more than 26 variables are overkill, really!");
        }
        for (int i = 0; i < size; i++) {
            variables.add(String.valueOf((char)(65 + i)));
        }
        return variables;
    }

    static void printTruthTable(
        final TruthTable table,
        final boolean empty,
        final boolean large,
        final BufferedWriter writer
    ) throws IOException {
        final Map<PropositionalInterpretation, Boolean> values = table.getAllInterpretationsAndValues();
        final String[][] tableForLaTeX = new String[table.variables.size() + 1][values.size() + 1];
        int column = 0;
        for (final String name : table.variables) {
            tableForLaTeX[column++][0] = String.format("\\var{%s}", name);
        }
        tableForLaTeX[column][0] = "\\textit{Formel}";
        int row = 1;
        if (!empty) {
            for (final Map.Entry<PropositionalInterpretation, Boolean> entry : values.entrySet()) {
                column = 0;
                final PropositionalInterpretation interpretation = entry.getKey();
                for (final String name : table.variables) {
                    tableForLaTeX[column++][row] = interpretation.get(name) ? "\\code{1}" : "\\code{0}";
                }
                tableForLaTeX[column][row] = entry.getValue() ? "\\code{1}" : "\\code{0}";
                row++;
            }
        }
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
        if (large) {
            writer.write("{\\Large");
            Main.newLine(writer);
        }
        LaTeXUtils.printTable(
            tableForLaTeX,
            Optional.empty(),
            cols -> cols > 1 ? String.format("|*{%d}{C{1em}|}C{4em}|", cols - 1) : "|C{4em}|",
            false,
            0,
            writer
        );
        if (large) {
            writer.write("}");
            Main.newLine(writer);
        }
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
    }

}
