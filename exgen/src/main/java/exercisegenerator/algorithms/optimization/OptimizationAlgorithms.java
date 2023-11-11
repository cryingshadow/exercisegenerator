package exercisegenerator.algorithms.optimization;

import java.io.*;
import java.math.*;
import java.util.*;

import org.apache.commons.math3.fraction.*;

import exercisegenerator.*;
import exercisegenerator.io.*;

public abstract class OptimizationAlgorithms {

    static final Random RANDOM = new Random();

    static final Object VARIABLE_NAME = "x";

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

    static int generateNumberOfInequalitiesOrEquations() {
        return OptimizationAlgorithms.RANDOM.nextInt(5) + 2;
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
