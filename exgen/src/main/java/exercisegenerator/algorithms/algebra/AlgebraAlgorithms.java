package exercisegenerator.algorithms.algebra;

import java.io.*;
import java.math.*;
import java.util.*;

import org.apache.commons.math3.fraction.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.algebra.*;

public abstract class AlgebraAlgorithms {

    public static final Object VARIABLE_NAME = "x";

    private static final int DEFAULT_BOUND = 11;

    public static BigFraction[][] generateInequalitiesOrEquations(
        final int numberOfInequalitiesOrEquations,
        final int numberOfVariables
    ) {
        final BigFraction[][] coefficients = new BigFraction[numberOfInequalitiesOrEquations][numberOfVariables + 1];
        for (int row = 0; row < numberOfInequalitiesOrEquations; row++) {
            for (int col = 0; col < numberOfVariables; col++) {
                coefficients[row][col] = AlgebraAlgorithms.generateCoefficient(AlgebraAlgorithms.DEFAULT_BOUND, 4);
            }
            coefficients[row][numberOfVariables] =
                AlgebraAlgorithms.generateCoefficient(AlgebraAlgorithms.DEFAULT_BOUND, 8);
        }
        return coefficients;
    }

    public static int generateNumberOfInequalitiesOrEquations() {
        return Main.RANDOM.nextInt(5) + 2;
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
        return Main.RANDOM.nextInt(3) + 2;
    }

    public static BigFraction parseRationalNumber(final String number) {
        if (number.contains(".")) {
            final String[] parts = number.split("\\.", -1);
            if (parts.length != 2) {
                throw new NumberFormatException(String.format("Number %s contains more than one dot!", number));
            }
            final int exponent = parts[1].length();
            final int denominator = Integer.parseInt("1" + "0".repeat(exponent));
            final int beforeComma = parts[0].length() == 0 ? 0 : Integer.parseInt(parts[0]);
            final int afterComma = parts[1].length() == 0 ? 0 : Integer.parseInt(parts[1]);
            return new BigFraction(beforeComma * denominator + afterComma, denominator);
        }
        final String[] parts = number.split("/");
        if (parts.length > 2) {
            throw new NumberFormatException(String.format("Number %s contains more than one slash!", number));
        }
        return parts.length == 1 ?
            new BigFraction(Integer.parseInt(parts[0])) :
                new BigFraction(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    public static void printMatrixAsInequalitiesOrEquations(
        final Matrix matrix,
        final String inequalityOrEquationSymbol,
        final BufferedWriter writer
    ) throws IOException {
        if (matrix.separatorIndex != matrix.getIndexOfLastColumn()) {
            throw new IllegalArgumentException("Matrix must be an assignment matrix!");
        }
        final int numberOfColumns = matrix.getNumberOfColumns();
        writer.write("$\\begin{array}{*{");
        writer.write(String.valueOf(numberOfColumns * 2 - 1));
        writer.write("}c}");
        Main.newLine(writer);
        for (int row = 0; row < matrix.getNumberOfRows(); row++) {
            boolean firstNonZero = true;
            for (int column = 0; column < numberOfColumns - 1; column++) {
                final BigFraction coefficient = matrix.getCoefficient(column, row);
                if (firstNonZero && coefficient.compareTo(BigFraction.ZERO) != 0) {
                    writer.write(
                        AlgebraAlgorithms.toCoefficientWithVariable(column == 0, true, true, column + 1, coefficient)
                    );
                    firstNonZero = false;
                } else {
                    writer.write(
                        AlgebraAlgorithms.toCoefficientWithVariable(
                            column == 0,
                            true,
                            firstNonZero,
                            column + 1,
                            coefficient
                        )
                    );
                }
            }
            writer.write(" & ");
            writer.write(inequalityOrEquationSymbol);
            writer.write(" & ");
            writer.write(AlgebraAlgorithms.toCoefficient(matrix.getLastCoefficientOfRow(row)));
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

    static BigFraction generateCoefficient(final int absoluteBound, final int oneToChanceForNegative) {
        return new BigFraction(
            Main.RANDOM.nextInt(absoluteBound)
            * (Main.RANDOM.nextInt(oneToChanceForNegative) == 0 ? -1 : 1)
        );
    }

    static Matrix generateQuadraticMatrix(final int dimension) {
        final Matrix result = new Matrix(dimension, dimension, dimension);
        for (int row = 0; row < dimension; row++) {
            for (int column = 0; column < dimension; column++) {
                result.setCoefficient(column, row, AlgebraAlgorithms.generateCoefficient(21, 2));
            }
        }
        return result;
    }

    static Matrix parseMatrix(final List<String> toParse) {
        final int numberOfRows = toParse.size();
        final int numberOfColumns = toParse.get(0).split(" ").length;
        final Matrix result = new Matrix(numberOfColumns, numberOfRows, numberOfColumns);
        for (int row = 0; row < numberOfRows; row++) {
            final String[] columns = toParse.get(row).split(" ");
            for (int column = 0; column < numberOfColumns; column++) {
                result.setCoefficient(column, row, AlgebraAlgorithms.parseRationalNumber(columns[column]));
            }
        }
        return result;
    }

    static int parseOrGenerateDimensionOfMatrices(final Parameters options) {
        if (options.containsKey(Flag.DEGREE)) {
            final int result = Integer.parseInt(options.get(Flag.DEGREE));
            if (result > 1) {
                return result;
            } else {
                return 2;
            }
        }
        return Main.RANDOM.nextInt(3) + 2;
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
