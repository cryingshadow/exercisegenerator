package exercisegenerator.algorithms.optimization;

import java.io.*;
import java.util.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.optimization.*;
import exercisegenerator.util.*;

public abstract class OptimizationAlgorithms {

    static void fillDPSolutionTable(
        final String[][] tableWithArrows,
        final int[][] solution,
        final int rowHeadings,
        final int columnHeadings,
        final DPTracebackFunction traceback
    ) {
        for (int i = 0; i < solution.length; i++) {
            for (int j = 0; j < solution[i].length; j++) {
                tableWithArrows[i + columnHeadings][2 * j + rowHeadings] = String.valueOf(solution[i][j]);
            }
        }
        int i = solution.length - 1;
        int j = solution[0].length - 1;
        List<DPDirection> directions = traceback.apply(new DPPosition(solution, i, j));
        while (!directions.isEmpty()) {
            for (final DPDirection direction : directions) {
                tableWithArrows[i + columnHeadings][2 * j + rowHeadings + 1] = direction.symbol;
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
        final int lineNumber,
        final CheckedFunction<Parameters<Flag>, LengthConfiguration, IOException> generateLengthConfiguration
    ) throws IOException {
        for (int i = 1; i < lineNumber; i++) {
            reader.readLine();
        }
        final String line = reader.readLine();
        if (line == null) {
            return generateLengthConfiguration.apply(options);
        }
        final String[] parts = line.strip().split(";");
        if (parts.length != 3) {
            return generateLengthConfiguration.apply(options);
        }
        return new LengthConfiguration(parts[0], parts[1], parts[2]);

    }

    static LengthConfiguration parseOrGenerateLengthConfiguration(
        final Parameters<Flag> options,
        final int lineNumber
    ) throws IOException {
        return OptimizationAlgorithms.parseOrGenerateLengthConfiguration(
            options,
            lineNumber,
            OptimizationAlgorithms::generateLengthConfiguration
        );
    }

    static LengthConfiguration parseOrGenerateLengthConfiguration(
        final Parameters<Flag> options,
        final int lineNumber,
        final CheckedFunction<Parameters<Flag>, LengthConfiguration, IOException> generateLengthConfiguration
    ) throws IOException {
        return new ParserAndGenerator<LengthConfiguration>(
            (reader, flags) -> OptimizationAlgorithms.parseLengthConfiguration(
                reader,
                flags,
                lineNumber,
                generateLengthConfiguration
            ),
            generateLengthConfiguration
        ).getResult(options);
    }

    static void printDPTable(
        final int[][] solution,
        final DPHeading rowHeading,
        final DPHeading columnHeading,
        final Optional<DPTracebackFunction> traceback,
        final Parameters<Flag> options,
        final LengthConfiguration configuration,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("{\\Large");
        Main.newLine(writer);
        LaTeXUtils.printTable(
            OptimizationAlgorithms.toDPSolutionTable(solution, rowHeading, columnHeading, traceback),
            Optional.empty(),
            cols -> OptimizationAlgorithms.dpTableColumnDefinition(configuration, rowHeading.count(), cols),
            true,
            0,
            rowHeading.count(),
            writer
        );
        writer.write("}");
        Main.newLine(writer);
    }

    static String[][] toDPSolutionTable(
        final int[][] solution,
        final DPHeading rowHeading,
        final DPHeading columnHeading,
        final Optional<DPTracebackFunction> optionalTraceback
    ) {
        final String[][] tableWithArrows =
            new String[solution.length + columnHeading.count()][2 * solution[0].length + rowHeading.count()];
        for (int row = 0; row < columnHeading.count() - 1; row++) {
            tableWithArrows[row][rowHeading.count() - 1] = columnHeading.titles().apply(row);
        }
        for (int row = 0; row < solution.length; row++) {
            final List<String> headings = rowHeading.headings().apply(row);
            for (int column = 0; column < rowHeading.count(); column++) {
                tableWithArrows[row + columnHeading.count()][column] = headings.get(column);
            }
        }
        for (int column = 0; column < rowHeading.count() - 1; column++) {
            tableWithArrows[columnHeading.count() - 1][column] = rowHeading.titles().apply(column);
        }
        for (int column = 0; column < solution[0].length; column++) {
            final List<String> headings = columnHeading.headings().apply(column);
            for (int row = 0; row < columnHeading.count(); row++) {
                tableWithArrows[row][2 * column + rowHeading.count()] = headings.get(row);
            }
        }
        tableWithArrows[columnHeading.count() - 1][rowHeading.count() - 1] = "${}^*$";
        if (optionalTraceback.isPresent()) {
            OptimizationAlgorithms.fillDPSolutionTable(
                tableWithArrows,
                solution,
                rowHeading.count(),
                columnHeading.count(),
                optionalTraceback.get()
            );
        }
        return tableWithArrows;
    }

    private static String dpTableColumnDefinition(
        final LengthConfiguration configuration,
        final int rowHeaders,
        final int columns
    ) {
        return String.format(
            "|*{%d}{C{%s}|}*{%d}{C{%s}C{%s}|}",
            rowHeaders,
            configuration.headerColLength(),
            columns / 2,
            configuration.numberLength(),
            configuration.arrowLength()
        );
    }

}
