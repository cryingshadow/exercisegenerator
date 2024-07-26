package exercisegenerator.algorithms.algebra;

import java.io.*;
import java.math.*;
import java.util.*;

import org.apache.commons.math3.fraction.*;

import exercisegenerator.*;
import exercisegenerator.io.*;

public abstract class AlgebraAlgorithms {

    public static final Object VARIABLE_NAME = "x";

    static final Random RANDOM = new Random();

    public static BigFraction[][] generateInequalitiesOrEquations(
        final int numberOfInequalitiesOrEquations,
        final int numberOfVariables
    ) {
        final BigFraction[][] coefficients = new BigFraction[numberOfInequalitiesOrEquations][numberOfVariables + 1];
        for (int row = 0; row < numberOfInequalitiesOrEquations; row++) {
            for (int col = 0; col < numberOfVariables; col++) {
                coefficients[row][col] = AlgebraAlgorithms.generateCoefficient(4);
            }
            coefficients[row][numberOfVariables] = AlgebraAlgorithms.generateCoefficient(8);
        }
        return coefficients;
    }

    public static int generateNumberOfInequalitiesOrEquations() {
        return AlgebraAlgorithms.RANDOM.nextInt(5) + 2;
    }

    public static int parseOrGenerateNumberOfVariables(final Parameters options) {
        if (options.containsKey(Flag.LENGTH)) {
            final int result = Integer.parseInt(options.get(Flag.LENGTH));
            if (result > 1) {
                return result;
            } else {
                return 2;
            }
        }
        return AlgebraAlgorithms.RANDOM.nextInt(3) + 2;
    }

    public static BigFraction parseRationalNumber(final String number) {
        final String[] parts = number.split("/");
        return parts.length == 1 ?
            new BigFraction(Integer.parseInt(parts[0])) :
                new BigFraction(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    public static void printMatrixAsInequalitiesOrEquations(
        final BigFraction[][] coefficients,
        final int numberOfColumns,
        final String inequalityOrEquationSymbol,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("$\\begin{array}{*{");
        writer.write(String.valueOf(numberOfColumns * 2 - 1));
        writer.write("}c}");
        Main.newLine(writer);
        for (int row = 0; row < coefficients.length; row++) {
            boolean firstNonZero = true;
            for (int col = 0; col < numberOfColumns - 1; col++) {
                final BigFraction coefficient = coefficients[row][col];
                if (firstNonZero && coefficient.compareTo(BigFraction.ZERO) != 0) {
                    writer.write(
                        AlgebraAlgorithms.toCoefficientWithVariable(col == 0, true, true, col + 1, coefficient)
                    );
                    firstNonZero = false;
                } else {
                    writer.write(
                        AlgebraAlgorithms.toCoefficientWithVariable(
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
            writer.write(AlgebraAlgorithms.toCoefficient(coefficients[row][numberOfColumns - 1]));
            writer.write("\\\\");
            Main.newLine(writer);
        }
        writer.write("\\end{array}$\\\\");
        Main.newLine(writer);
    }

    public static String toCoefficient(final BigFraction coefficient) {
        if (coefficient.getDenominator().compareTo(BigInteger.ONE) == 0) {
            return String.valueOf(coefficient.intValue());
        }
        return AlgebraAlgorithms.toFractionString(coefficient);
    }

    public static String toCoefficientWithVariable(
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
                    return String.format("%s_{%d}", AlgebraAlgorithms.VARIABLE_NAME, variableIndex);
                }
                if (matrix) {
                    if (firstNonZero) {
                        return String.format(" &  & %s_{%d}", AlgebraAlgorithms.VARIABLE_NAME, variableIndex);
                    }
                    return String.format(" & + & %s_{%d}", AlgebraAlgorithms.VARIABLE_NAME, variableIndex);
                }
                return String.format(" + %s_{%d}", AlgebraAlgorithms.VARIABLE_NAME, variableIndex);
            }
            if (value == -1) {
                if (first) {
                    return String.format("-%s_{%d}", AlgebraAlgorithms.VARIABLE_NAME, variableIndex);
                }
                if (matrix) {
                    if (firstNonZero) {
                        return String.format(" &  & -%s_{%d}", AlgebraAlgorithms.VARIABLE_NAME, variableIndex);
                    }
                    return String.format(" & - & %s_{%d}", AlgebraAlgorithms.VARIABLE_NAME, variableIndex);
                }
                return String.format(" - %s_{%d}", AlgebraAlgorithms.VARIABLE_NAME, variableIndex);
            }
            if (first) {
                return String.format("%d%s_{%d}", value, AlgebraAlgorithms.VARIABLE_NAME, variableIndex);
            }
            if (value < 0) {
                if (matrix) {
                    if (firstNonZero) {
                        return String.format(
                            " &  & %d%s_{%d}",
                            value,
                            AlgebraAlgorithms.VARIABLE_NAME,
                            variableIndex
                        );
                    }
                    return String.format(
                        " & - & %d%s_{%d}",
                        -value,
                        AlgebraAlgorithms.VARIABLE_NAME,
                        variableIndex
                    );
                }
                return String.format(" - %d%s_{%d}", -value, AlgebraAlgorithms.VARIABLE_NAME, variableIndex);
            }
            if (matrix) {
                if (firstNonZero) {
                    return String.format(" &  & %d%s_{%d}", value, AlgebraAlgorithms.VARIABLE_NAME, variableIndex);
                }
                return String.format(" & + & %d%s_{%d}", value, AlgebraAlgorithms.VARIABLE_NAME, variableIndex);
            }
            return String.format(" + %d%s_{%d}", value, AlgebraAlgorithms.VARIABLE_NAME, variableIndex);
        }
        if (first) {
            return String.format(
                "%s%s_{%d}",
                AlgebraAlgorithms.toFractionString(coefficient),
                AlgebraAlgorithms.VARIABLE_NAME,
                variableIndex
            );
        }
        if (coefficient.compareTo(BigFraction.ZERO) < 0) {
            if (matrix) {
                if (firstNonZero) {
                    return String.format(
                        " &  & %s%s_{%d}",
                        AlgebraAlgorithms.toFractionString(coefficient),
                        AlgebraAlgorithms.VARIABLE_NAME,
                        variableIndex
                    );
                }
                return String.format(
                    " & - & %s%s_{%d}",
                    AlgebraAlgorithms.toFractionString(coefficient.negate()),
                    AlgebraAlgorithms.VARIABLE_NAME,
                    variableIndex
                );
            }
            return String.format(
                " - %s%s_{%d}",
                AlgebraAlgorithms.toFractionString(coefficient.negate()),
                AlgebraAlgorithms.VARIABLE_NAME,
                variableIndex
            );
        }
        if (matrix) {
            if (firstNonZero) {
                return String.format(
                    " &  & %s%s_{%d}",
                    AlgebraAlgorithms.toFractionString(coefficient),
                    AlgebraAlgorithms.VARIABLE_NAME,
                    variableIndex
                );
            }
            return String.format(
                " & + & %s%s_{%d}",
                AlgebraAlgorithms.toFractionString(coefficient),
                AlgebraAlgorithms.VARIABLE_NAME,
                variableIndex
            );
        }
        return String.format(
            " + %s%s_{%d}",
            AlgebraAlgorithms.toFractionString(coefficient),
            AlgebraAlgorithms.VARIABLE_NAME,
            variableIndex
        );
    }

    private static BigFraction generateCoefficient(final int oneToChanceForNegative) {
        return new BigFraction(
            AlgebraAlgorithms.RANDOM.nextInt(11)
            * (AlgebraAlgorithms.RANDOM.nextInt(oneToChanceForNegative) == 0 ? -1 : 1)
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
