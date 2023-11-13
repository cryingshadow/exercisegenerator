package exercisegenerator.algorithms.optimization;

import java.io.*;
import java.math.*;
import java.util.*;
import java.util.function.*;

import org.apache.commons.math3.fraction.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.optimization.*;

public abstract class OptimizationAlgorithms {

    static final Random RANDOM = new Random();

    static final Object VARIABLE_NAME = "x";

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

    static BigFraction[][] generateInequalitiesOrEquations(
        final int numberOfInequalitiesOrEquations,
        final int numberOfVariables
    ) {
        final BigFraction[][] matrix = new BigFraction[numberOfInequalitiesOrEquations][numberOfVariables + 1];
        for (int row = 0; row < numberOfInequalitiesOrEquations; row++) {
            for (int col = 0; col < numberOfVariables; col++) {
                matrix[row][col] = OptimizationAlgorithms.generateCoefficient(4);
            }
            matrix[row][numberOfVariables] = OptimizationAlgorithms.generateCoefficient(8);
        }
        return matrix;
    }

    static LengthConfiguration generateLengthConfiguration(final Parameters options) {
        return new LengthConfiguration();
    }

    static int generateNumberOfInequalitiesOrEquations() {
        return OptimizationAlgorithms.RANDOM.nextInt(5) + 2;
    }

    static LengthConfiguration parseLengthConfiguration(
        final BufferedReader reader,
        final Parameters options,
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
        final Parameters options,
        final int lineNumber
    ) throws IOException {
        return new ParserAndGenerator<LengthConfiguration>(
            (reader, flags) -> OptimizationAlgorithms.parseLengthConfiguration(reader, flags, lineNumber),
            OptimizationAlgorithms::generateLengthConfiguration
        ).getResult(options);
    }

    static int parseOrGenerateNumberOfVariables(final Parameters options) {
        if (options.containsKey(Flag.LENGTH)) {
            final int result = Integer.parseInt(options.get(Flag.LENGTH));
            if (result > 1) {
                return result;
            } else {
                return 2;
            }
        }
        return OptimizationAlgorithms.RANDOM.nextInt(3) + 2;
    }

    static BigFraction parseRationalNumber(final String number) {
        final String[] parts = number.split("/");
        return parts.length == 1 ?
            new BigFraction(Integer.parseInt(parts[0])) :
                new BigFraction(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    static void printDPTable(
        final int[][] solution,
        final Function<Integer, String> rowHeading,
        final Function<Integer, String> columnHeading,
        final Optional<DPTracebackFunction> traceback,
        final Parameters options,
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

    static void printMatrixAsInequalitiesOrEquations(
        final BigFraction[][] matrix,
        final int numberOfColumns,
        final String inequalityOrEquationSymbol,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("$\\begin{array}{*{");
        writer.write(String.valueOf(numberOfColumns * 2 - 1));
        writer.write("}c}");
        Main.newLine(writer);
        for (int row = 0; row < matrix.length; row++) {
            boolean firstNonZero = true;
            for (int col = 0; col < numberOfColumns - 1; col++) {
                final BigFraction coefficient = matrix[row][col];
                if (firstNonZero && coefficient.compareTo(BigFraction.ZERO) != 0) {
                    writer.write(
                        OptimizationAlgorithms.toCoefficientWithVariable(col == 0, true, true, col + 1, coefficient)
                    );
                    firstNonZero = false;
                } else {
                    writer.write(
                        OptimizationAlgorithms.toCoefficientWithVariable(
                            col == 0,
                            true,
                            firstNonZero,
                            col + 1,
                            coefficient
                        )
                    );
                }
            }
            writer.write(" & ");
            writer.write(inequalityOrEquationSymbol);
            writer.write(" & ");
            writer.write(OptimizationAlgorithms.toCoefficient(matrix[row][numberOfColumns - 1]));
            writer.write("\\\\");
            Main.newLine(writer);
        }
        writer.write("\\end{array}$\\\\");
        Main.newLine(writer);
    }

    static String toCoefficient(final BigFraction coefficient) {
        if (coefficient.getDenominator().compareTo(BigInteger.ONE) == 0) {
            return String.valueOf(coefficient.intValue());
        }
        return OptimizationAlgorithms.toFractionString(coefficient);
    }

    static String toCoefficientWithVariable(
        final boolean first,
        final boolean matrix,
        final boolean firstNonZero,
        final int variableIndex,
        final BigFraction coefficient
    ) {
        if (coefficient.getDenominator().compareTo(BigInteger.ONE) == 0) {
            final int value = coefficient.intValue();
            if (value == 0) {
                if (matrix && !first) {
                    return " &  & ";
                }
                return "";
            }
            if (value == 1) {
                if (first) {
                    return String.format("%s_{%d}", OptimizationAlgorithms.VARIABLE_NAME, variableIndex);
                }
                if (matrix) {
                    if (firstNonZero) {
                        return String.format(" &  & %s_{%d}", OptimizationAlgorithms.VARIABLE_NAME, variableIndex);
                    }
                    return String.format(" & + & %s_{%d}", OptimizationAlgorithms.VARIABLE_NAME, variableIndex);
                }
                return String.format(" + %s_{%d}", OptimizationAlgorithms.VARIABLE_NAME, variableIndex);
            }
            if (value == -1) {
                if (first) {
                    return String.format("-%s_{%d}", OptimizationAlgorithms.VARIABLE_NAME, variableIndex);
                }
                if (matrix) {
                    if (firstNonZero) {
                        return String.format(" &  & -%s_{%d}", OptimizationAlgorithms.VARIABLE_NAME, variableIndex);
                    }
                    return String.format(" & - & %s_{%d}", OptimizationAlgorithms.VARIABLE_NAME, variableIndex);
                }
                return String.format(" - %s_{%d}", OptimizationAlgorithms.VARIABLE_NAME, variableIndex);
            }
            if (first) {
                return String.format("%d%s_{%d}", value, OptimizationAlgorithms.VARIABLE_NAME, variableIndex);
            }
            if (value < 0) {
                if (matrix) {
                    if (firstNonZero) {
                        return String.format(
                            " &  & %d%s_{%d}",
                            value,
                            OptimizationAlgorithms.VARIABLE_NAME,
                            variableIndex
                        );
                    }
                    return String.format(
                        " & - & %d%s_{%d}",
                        -value,
                        OptimizationAlgorithms.VARIABLE_NAME,
                        variableIndex
                    );
                }
                return String.format(" - %d%s_{%d}", -value, OptimizationAlgorithms.VARIABLE_NAME, variableIndex);
            }
            if (matrix) {
                if (firstNonZero) {
                    return String.format(" &  & %d%s_{%d}", value, OptimizationAlgorithms.VARIABLE_NAME, variableIndex);
                }
                return String.format(" & + & %d%s_{%d}", value, OptimizationAlgorithms.VARIABLE_NAME, variableIndex);
            }
            return String.format(" + %d%s_{%d}", value, OptimizationAlgorithms.VARIABLE_NAME, variableIndex);
        }
        if (first) {
            return String.format(
                "%s%s_{%d}",
                OptimizationAlgorithms.toFractionString(coefficient),
                OptimizationAlgorithms.VARIABLE_NAME,
                variableIndex
            );
        }
        if (coefficient.compareTo(BigFraction.ZERO) < 0) {
            if (matrix) {
                if (firstNonZero) {
                    return String.format(
                        " &  & %s%s_{%d}",
                        OptimizationAlgorithms.toFractionString(coefficient),
                        OptimizationAlgorithms.VARIABLE_NAME,
                        variableIndex
                    );
                }
                return String.format(
                    " & - & %s%s_{%d}",
                    OptimizationAlgorithms.toFractionString(coefficient.negate()),
                    OptimizationAlgorithms.VARIABLE_NAME,
                    variableIndex
                );
            }
            return String.format(
                " - %s%s_{%d}",
                OptimizationAlgorithms.toFractionString(coefficient.negate()),
                OptimizationAlgorithms.VARIABLE_NAME,
                variableIndex
            );
        }
        if (matrix) {
            if (firstNonZero) {
                return String.format(
                    " &  & %s%s_{%d}",
                    OptimizationAlgorithms.toFractionString(coefficient),
                    OptimizationAlgorithms.VARIABLE_NAME,
                    variableIndex
                );
            }
            return String.format(
                " & + & %s%s_{%d}",
                OptimizationAlgorithms.toFractionString(coefficient),
                OptimizationAlgorithms.VARIABLE_NAME,
                variableIndex
            );
        }
        return String.format(
            " + %s%s_{%d}",
            OptimizationAlgorithms.toFractionString(coefficient),
            OptimizationAlgorithms.VARIABLE_NAME,
            variableIndex
        );
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

    private static BigFraction generateCoefficient(final int oneToChanceForNegative) {
        return new BigFraction(
            OptimizationAlgorithms.RANDOM.nextInt(11)
            * (OptimizationAlgorithms.RANDOM.nextInt(oneToChanceForNegative) == 0 ? -1 : 1)
        );
    }

    private static String toFractionString(final BigFraction coefficient) {
        final BigInteger numerator = coefficient.getNumerator();
        return String.format(
            "%s\\frac{%s}{%s}",
            numerator.compareTo(BigInteger.ZERO) < 0 ? "-" : "",
            numerator.abs().toString(),
            coefficient.getDenominator().toString()
        );
    }

}
