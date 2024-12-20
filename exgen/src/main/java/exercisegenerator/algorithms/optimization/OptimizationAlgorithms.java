package exercisegenerator.algorithms.optimization;

import java.io.*;
import java.util.*;
import java.util.function.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.optimization.*;

public abstract class OptimizationAlgorithms {

    static void fillDPSolutionTable(
        final String[][] tableWithArrows,
        final int[][] solution,
        final DPTracebackFunction traceback
    ) {
        for (int i = 0; i < solution.length; i++) {
            for (int j = 0; j < solution[i].length; j++) {
                tableWithArrows[i + 1][2 * j + 1] = String.valueOf(solution[i][j]);
            }
        }
        int i = solution.length - 1;
        int j = solution[0].length - 1;
        List<DPDirection> directions = traceback.apply(new DPPosition(solution, i, j));
        while (!directions.isEmpty()) {
            for (final DPDirection direction : directions) {
                tableWithArrows[i + 1][2 * j + 2] = direction.symbol;
                i -= direction.verticalDiff;
                j -= direction.horizontalDiff;
            }
            directions = traceback.apply(new DPPosition(solution, i, j));
        }
    }

    static LengthConfiguration generateLengthConfiguration(final Parameters<Flag> options) {
        return new LengthConfiguration();
    }

    static LengthConfiguration parseLengthConfiguration(
        final BufferedReader reader,
        final Parameters<Flag> options,
        final int lineNumber
    ) throws IOException {
        for (int i = 1; i < lineNumber; i++) {
            reader.readLine();
        }
        final String line = reader.readLine();
        if (line == null) {
            return new LengthConfiguration();
        }
        final String[] parts = line.strip().split(";");
        if (parts.length != 3) {
            return new LengthConfiguration();
        }
        return new LengthConfiguration(parts[0], parts[1], parts[2]);

    }

    static LengthConfiguration parseOrGenerateLengthConfiguration(
        final Parameters<Flag> options,
        final int lineNumber
    ) throws IOException {
        return new ParserAndGenerator<LengthConfiguration>(
            (reader, flags) -> OptimizationAlgorithms.parseLengthConfiguration(reader, flags, lineNumber),
            OptimizationAlgorithms::generateLengthConfiguration
        ).getResult(options);
    }

    static void printDPTable(
        final int[][] solution,
        final Function<Integer, String> rowHeading,
        final Function<Integer, String> columnHeading,
        final Optional<DPTracebackFunction> traceback,
        final Parameters<Flag> options,
        final LengthConfiguration configuration,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
        if (solution[0].length > 6) {
            LaTeXUtils.resizeboxBeginning("0.9\\textwidth", "!", writer);
        }
        writer.write("{\\Large");
        Main.newLine(writer);
        LaTeXUtils.printTable(
            OptimizationAlgorithms.toDPSolutionTable(solution, rowHeading, columnHeading, traceback),
            Optional.empty(),
            cols -> OptimizationAlgorithms.dpTableColumnDefinition(configuration, cols),
            true,
            0,
            writer
        );
        Main.newLine(writer);
        if (solution[0].length > 6) {
            LaTeXUtils.resizeboxEnd(writer);
        }
        writer.write("}");
        Main.newLine(writer);
        Main.newLine(writer);
    }

    static String[][] toDPSolutionTable(
        final int[][] solution,
        final Function<Integer, String> rowHeading,
        final Function<Integer, String> columnHeading,
        final Optional<DPTracebackFunction> optionalTraceback
    ) {
        final String[][] tableWithArrows = new String[solution.length + 1][2 * solution[0].length + 1];
        tableWithArrows[0][0] = "${}^*$";
        for (int row = 0; row < solution.length; row++) {
            final String heading = rowHeading.apply(row);
            tableWithArrows[row + 1][0] =
                Optional.ofNullable(heading).map(String::isBlank).orElse(true) ? "" : LaTeXUtils.bold(heading);
        }
        for (int column = 0; column < solution[0].length; column++) {
            final String heading = columnHeading.apply(column);
            tableWithArrows[0][2 * column + 1] =
                Optional.ofNullable(heading).map(String::isBlank).orElse(true) ? "" : LaTeXUtils.bold(heading);
        }
        if (optionalTraceback.isPresent()) {
            OptimizationAlgorithms.fillDPSolutionTable(
                tableWithArrows,
                solution,
                optionalTraceback.get()
            );
        }
        return tableWithArrows;
    }

    private static String dpTableColumnDefinition(final LengthConfiguration configuration, final int cols) {
        return String.format(
            "|C{%s}|*{%d}{C{%s}C{%s}|}",
            configuration.headerColLength,
            cols / 2,
            configuration.numberLength,
            configuration.arrowLength
        );
    }

}
