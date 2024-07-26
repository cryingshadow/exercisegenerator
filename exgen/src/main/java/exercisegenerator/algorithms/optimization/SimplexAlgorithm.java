package exercisegenerator.algorithms.optimization;

import java.io.*;
import java.util.*;

import org.apache.commons.math3.fraction.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.algorithms.algebra.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.optimization.*;
import exercisegenerator.util.*;

public class SimplexAlgorithm implements AlgorithmImplementation {

    public static final SimplexAlgorithm INSTANCE = new SimplexAlgorithm();

    public static SimplexSolution simplex(final SimplexProblem problem) {
        final List<SimplexTableau> tableaus = new LinkedList<SimplexTableau>();
        SimplexTableau tableau = SimplexAlgorithm.simplexInitializeTableau(problem);
        tableaus.add(tableau);
        SimplexAnswer answer = SimplexAlgorithm.simplexComputeAnswer(tableau);
        while (answer == SimplexAnswer.INCOMPLETE) {
            tableau = SimplexAlgorithm.simplexStep(tableau);
            tableaus.add(tableau);
            answer = SimplexAlgorithm.simplexComputeAnswer(tableau);
        }
        return new SimplexSolution(tableaus, answer);
    }

    static SimplexAnswer simplexComputeAnswer(final SimplexTableau tableau) {
        final BigFraction[][] matrix = tableau.problem.matrix;
        if (SimplexAlgorithm.simplexHasNegativeLimit(matrix)) {
            if (SimplexAlgorithm.simplexHasNegativeLimitWithNonNegativeRow(matrix)) {
                return SimplexAnswer.UNSOLVABLE;
            } else {
                return SimplexAnswer.INCOMPLETE;
            }
        }
        if (SimplexAlgorithm.simplexPivotColumnExists(matrix)) {
            if (SimplexAlgorithm.simplexPivotRowExists(tableau.quotients)) {
                return SimplexAnswer.INCOMPLETE;
            } else {
                return SimplexAnswer.UNBOUNDED;
            }
        }
        return SimplexAnswer.SOLVED;
    }

    private static BigFraction generateNonZeroCoefficient(final int oneToChanceForNegative) {
        return new BigFraction(
            (OptimizationAlgorithms.RANDOM.nextInt(10) + 1)
            * (OptimizationAlgorithms.RANDOM.nextInt(oneToChanceForNegative) == 0 ? -1 : 1)
        );
    }

    private static SimplexProblem generateSimplexProblem(final Parameters options) {
        final int numberOfVariables = AlgebraAlgorithms.parseOrGenerateNumberOfVariables(options);
        final int numberOfInequalities = AlgebraAlgorithms.generateNumberOfInequalitiesOrEquations();
        final BigFraction[] target = SimplexAlgorithm.generateTargetFunction(numberOfVariables);
        final BigFraction[][] matrix =
            AlgebraAlgorithms.generateInequalitiesOrEquations(numberOfInequalities, numberOfVariables);
        return new SimplexProblem(target, matrix);
    }

    private static BigFraction[] generateTargetFunction(final int numberOfVariables) {
        final BigFraction[] target = new BigFraction[numberOfVariables];
        for (int i = 0; i < numberOfVariables; i++) {
            target[i] = SimplexAlgorithm.generateNonZeroCoefficient(4);
        }
        return target;
    }

    private static SimplexProblem parseOrGenerateSimplexProblem(final Parameters options) throws IOException {
        return new ParserAndGenerator<SimplexProblem>(
            SimplexAlgorithm::parseSimplexProblem,
            SimplexAlgorithm::generateSimplexProblem
        ).getResult(options);
    }

    private static SimplexProblem parseSimplexProblem(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        final String line = reader.readLine();
        final String[] rows = line.split(";");
        final BigFraction[] target = SimplexAlgorithm.parseTargetFunction(rows[0]);
        final BigFraction[][] matrix = new BigFraction[rows.length - 1][target.length + 1];
        for (int row = 0; row < matrix.length; row++) {
            final String[] numbers = rows[row + 1].split(",");
            if (numbers.length != matrix[row].length) {
                throw new IOException(
                    "The rows of the matrix must have exactly one more entry than the target function!"
                );
            }
            for (int col = 0; col < numbers.length; col++) {
                matrix[row][col] = AlgebraAlgorithms.parseRationalNumber(numbers[col]);
            }
        }
        return new SimplexProblem(target, matrix);
    }

    private static BigFraction[] parseTargetFunction(final String line) {
        final String[] numbers = line.split(",");
        final BigFraction[] target = new BigFraction[numbers.length];
        for (int i = 0; i < target.length; i++) {
            target[i] = AlgebraAlgorithms.parseRationalNumber(numbers[i]);
        }
        return target;
    }

    private static void printSimplexExercise(
        final SimplexProblem problem,
        final SimplexSolution solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Gegeben sei das folgende \\emphasize{lineare Programm} in Standard-Maximum-Form:\\\\");
        Main.newLine(writer);
        SimplexAlgorithm.printSimplexProblem(problem, writer);
        writer.write("L\\\"osen Sie dieses lineare Programm mithilfe des \\emphasize{Simplex-Algorithmus}. ");
        writer.write("F\\\"ullen Sie dazu die nachfolgenden Simplex-Tableaus und geben Sie eine optimale Belegung ");
        writer.write(String.format("f\\\"ur die Variablen $%s_{1}", AlgebraAlgorithms.VARIABLE_NAME));
        for (int index = 1; index < problem.target.length; index++) {
            writer.write(String.format(", %s_{%d}", AlgebraAlgorithms.VARIABLE_NAME, index + 1));
        }
        writer.write("$ an oder begr\\\"unden Sie, warum es keine solche optimale Belegung gibt.");
        Main.newLine(writer);
        Main.newLine(writer);
        final PreprintMode mode = PreprintMode.parsePreprintMode(options);
        switch (mode) {
        case SOLUTION_SPACE:
            LaTeXUtils.printSolutionSpaceBeginning(Optional.empty(), options, writer);
            // fall-through
        case ALWAYS:
            writer.write("{\\renewcommand{\\arraystretch}{1.5}");
            Main.newLine(writer);
            for (final SimplexTableau tableau : solution.tableaus) {
                Main.newLine(writer);
                LaTeXUtils.printTable(
                    SimplexAlgorithm.toSimplexTableau(tableau, false),
                    Optional.empty(),
                    SimplexAlgorithm::simplexTableColumnDefinition,
                    true,
                    0,
                    writer
                );
            }
            Main.newLine(writer);
            writer.write("\\renewcommand{\\arraystretch}{1}}");
            Main.newLine(writer);
            LaTeXUtils.printVerticalProtectedSpace(writer);
            writer.write("Ergebnis:");
            Main.newLine(writer);
            if (mode == PreprintMode.SOLUTION_SPACE) {
                LaTeXUtils.printSolutionSpaceEnd(Optional.of("2ex"), options, writer);
            }
            Main.newLine(writer);
            break;
        default:
            //do nothing
        }
    }

    private static void printSimplexProblem(final SimplexProblem problem, final BufferedWriter writer)
    throws IOException {
        writer.write("Maximiere $z(\\mathbf{x}) = ");
        int firstNonZeroIndex = 0;
        for (final BigFraction coefficient : problem.target) {
            if (coefficient.compareTo(BigFraction.ZERO) != 0) {
                break;
            }
            firstNonZeroIndex++;
        }
        if (firstNonZeroIndex == problem.target.length) {
            writer.write("0");
        } else {
            writer.write(
                AlgebraAlgorithms.toCoefficientWithVariable(
                    true,
                    false,
                    true,
                    firstNonZeroIndex + 1,
                    problem.target[firstNonZeroIndex]
                )
            );
            for (int index = firstNonZeroIndex + 1; index < problem.target.length; index++) {
                writer.write(
                    AlgebraAlgorithms.toCoefficientWithVariable(
                        false,
                        false,
                        false,
                        index + 1,
                        problem.target[index]
                    )
                );
            }
        }
        writer.write("$\\\\");
        Main.newLine(writer);
        writer.write("unter den folgenden Nebenbedingungen:\\\\");
        Main.newLine(writer);
        AlgebraAlgorithms.printMatrixAsInequalitiesOrEquations(
            problem.matrix,
            problem.matrix[0].length,
            "\\leq",
            writer
        );
        writer.write(String.format("$%s_{1}", AlgebraAlgorithms.VARIABLE_NAME));
        for (int index = 1; index < problem.target.length; index++) {
            writer.write(String.format(", %s_{%d}", AlgebraAlgorithms.VARIABLE_NAME, index + 1));
        }
        writer.write(" \\geq 0$\\\\[2ex]");
        Main.newLine(writer);
    }

    private static void printSimplexSolution(
        final SimplexProblem problem,
        final SimplexSolution solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("{\\renewcommand{\\arraystretch}{1.5}");
        Main.newLine(writer);
        for (final SimplexTableau tableau : solution.tableaus) {
            Main.newLine(writer);
            LaTeXUtils.printTable(
                SimplexAlgorithm.toSimplexTableau(tableau, true),
                Optional.empty(),
                SimplexAlgorithm::simplexTableColumnDefinition,
                true,
                0,
                writer
            );
        }
        Main.newLine(writer);
        writer.write("\\renewcommand{\\arraystretch}{1}}");
        Main.newLine(writer);
        LaTeXUtils.printVerticalProtectedSpace(writer);
        writer.write("Ergebnis: ");
        switch (solution.answer) {
        case UNBOUNDED:
            writer.write("unbeschr\\\"ankt, da wir eine Pivot-Spalte mit nicht-positiven Koeffizienten haben");
            break;
        case UNSOLVABLE:
            writer.write(
                "unl\\\"osbar, da wir eine Pivot-Zeile mit negativem Limit, aber nicht-negativen Koeffizienten haben"
            );
            break;
        default:
            final SimplexTableau lastTableau = solution.tableaus.get(solution.tableaus.size() - 1);
            writer.write(
                String.format(
                    "$%s_{1}^* = %s$",
                    AlgebraAlgorithms.VARIABLE_NAME,
                    AlgebraAlgorithms.toCoefficient(SimplexAlgorithm.simplexGetCurrentValue(0, lastTableau))
                )
            );
            for (int i = 1; i < lastTableau.problem.target.length; i++) {
                writer.write(
                    String.format(
                        ", $%s_{%d}^* = %s$",
                        AlgebraAlgorithms.VARIABLE_NAME,
                        i + 1,
                        AlgebraAlgorithms.toCoefficient(SimplexAlgorithm.simplexGetCurrentValue(i, lastTableau))
                    )
                );
            }
            break;
        }
        Main.newLine(writer);
        Main.newLine(writer);
    }

    private static void simplexBaseSwap(
        final int pivotRow,
        final int pivotColumn,
        final BigFraction[][] matrix,
        final int[] basicVariables,
        final BigFraction[] target
    ) {
        final BigFraction pivotElement = matrix[pivotRow][pivotColumn];
        if (pivotElement.compareTo(BigFraction.ONE) != 0) {
            for (int col = 0; col < matrix[pivotRow].length; col++) {
                matrix[pivotRow][col] = matrix[pivotRow][col].divide(pivotElement);
            }
        }
        for (int row = 0; row < matrix.length - 2; row++) {
            if (row != pivotRow && matrix[row][pivotColumn].compareTo(BigFraction.ZERO) != 0) {
                final BigFraction factor = matrix[row][pivotColumn];
                for (int col = 0; col < matrix[row].length; col++) {
                    matrix[row][col] = matrix[row][col].subtract(matrix[pivotRow][col].multiply(factor));
                }
            }
        }
        basicVariables[pivotRow] = pivotColumn;
        for (int col = 0; col < matrix[basicVariables.length].length; col++) {
            BigFraction sum = BigFraction.ZERO;
            for (int row = 0; row < basicVariables.length; row++) {
                sum =
                    sum.add(
                        matrix[row][col].multiply(SimplexAlgorithm.simplexTargetValue(target, basicVariables[row]))
                    );
            }
            matrix[basicVariables.length][col] = sum;
            if (col < target.length) {
                matrix[basicVariables.length + 1][col] = target[col].subtract(sum);
            } else if (col < matrix[basicVariables.length].length - 1) {
                matrix[basicVariables.length + 1][col] = sum.negate();
            } else {
                matrix[basicVariables.length + 1][col] = BigFraction.ZERO;
            }
        }
    }

    private static BigFraction[] simplexComputeQuotients(final BigFraction[][] matrix, final int pivotColumn) {
        if (pivotColumn < 0) {
            return null;
        }
        final BigFraction[] quotients = new BigFraction[matrix.length - 2];
        for (int row = 0; row < quotients.length; row++) {
            if (
                matrix[row][pivotColumn].compareTo(BigFraction.ZERO) > 0
                && matrix[row][matrix[row].length - 1].compareTo(BigFraction.ZERO) >= 0
            ) {
                quotients[row] = matrix[row][matrix[row].length - 1].divide(matrix[row][pivotColumn]);
            } else {
                quotients[row] = null;
            }
        }
        return quotients;
    }

    private static int simplexFirstRowWithNegativeLimit(final BigFraction[][] matrix) {
        for (int row = 0; row < matrix.length - 2; row++) {
            if (matrix[row][matrix[row].length - 1].compareTo(BigFraction.ZERO) < 0) {
                return row;
            }
        }
        return -1;
    }

    private static BigFraction simplexGetCurrentValue(final int variableIndex, final SimplexTableau tableau) {
        int index = -1;
        for (int i = 0; i < tableau.basicVariables.length; i++) {
            if (tableau.basicVariables[i] == variableIndex) {
                index = i;
                break;
            }
        }
        if (index < 0) {
            return BigFraction.ZERO;
        }
        return tableau.problem.matrix[index][tableau.problem.matrix[index].length - 1];
    }

    private static boolean simplexHasNegativeLimit(final BigFraction[][] matrix) {
        for (int i = 0; i < matrix.length - 2; i++) {
            if (matrix[i][matrix[i].length - 1].compareTo(BigFraction.ZERO) < 0) {
                return true;
            }
        }
        return false;
    }

    private static boolean simplexHasNegativeLimitWithNonNegativeRow(final BigFraction[][] matrix) {
        for (int i = 0; i < matrix.length - 2; i++) {
            if (matrix[i][matrix[i].length - 1].compareTo(BigFraction.ZERO) < 0) {
                boolean notFound = true;
                for (int j = 0; j < matrix.length - 1; j++) {
                    if (matrix[i][j].compareTo(BigFraction.ZERO) < 0) {
                        notFound = false;
                        break;
                    }
                }
                if (notFound) {
                    return true;
                }
            }
        }
        return false;
    }

    private static BigFraction[][] simplexInitializeMatrix(
        final BigFraction[][] initialMatrix,
        final BigFraction[] target
    ) {
        final int initialColumnLength = initialMatrix.length;
        final BigFraction[][] newMatrix =
            new BigFraction[initialColumnLength + 2][initialMatrix[0].length + initialColumnLength];
        for (int row = 0; row < initialColumnLength; row++) {
            final int initialRowLength = initialMatrix[row].length - 1;
            for (int col = 0; col < initialRowLength; col++) {
                newMatrix[row][col] = initialMatrix[row][col];
            }
            final int newRowLength = newMatrix[row].length - 1;
            for (int col = initialRowLength; col < newRowLength; col++) {
                if (col - initialRowLength == row) {
                    newMatrix[row][col] = BigFraction.ONE;
                } else {
                    newMatrix[row][col] = BigFraction.ZERO;
                }
            }
            newMatrix[row][newRowLength] = initialMatrix[row][initialRowLength];
        }
        for (int col = 0; col < newMatrix[initialColumnLength].length; col++) {
            newMatrix[initialColumnLength][col] = BigFraction.ZERO;
        }
        final int targetLength = target.length;
        for (int col = 0; col < targetLength; col++) {
            newMatrix[initialColumnLength + 1][col] = target[col];
        }
        for (int col = targetLength; col < newMatrix[initialColumnLength + 1].length; col++) {
            newMatrix[initialColumnLength + 1][col] = BigFraction.ZERO;
        }
        return newMatrix;
    }

    private static SimplexTableau simplexInitializeTableau(final SimplexProblem problem) {
        final BigFraction[][] initialMatrix = problem.matrix;
        final int initialColumnLength = initialMatrix.length;
        final BigFraction[][] newMatrix = SimplexAlgorithm.simplexInitializeMatrix(initialMatrix, problem.target);
        final int[] basicVariables = new int[initialColumnLength];
        for (int i = 0; i < initialColumnLength; i++) {
            basicVariables[i] = i + problem.target.length;
        }
        final int pivotColumn = SimplexAlgorithm.simplexSelectPivotColumn(newMatrix);
        final BigFraction[] quotients = SimplexAlgorithm.simplexComputeQuotients(newMatrix, pivotColumn);
        return new SimplexTableau(
            new SimplexProblem(problem.target, newMatrix),
            basicVariables,
            quotients,
            SimplexAlgorithm.simplexSelectPivotRow(quotients),
            pivotColumn
        );
    }

    private static boolean simplexPivotColumnExists(final BigFraction[][] matrix) {
        for (int col = 0; col < matrix[matrix.length - 1].length - 1; col++) {
            if (matrix[matrix.length - 1][col].compareTo(BigFraction.ZERO) > 0) {
                return true;
            }
        }
        return false;
    }

    private static boolean simplexPivotRowExists(final BigFraction[] quotients) {
        if (quotients == null) {
            return false;
        }
        for (final BigFraction quotient : quotients) {
            if (quotient != null) {
                return true;
            }
        }
        return false;
    }

    private static int simplexSelectPivotColumn(final BigFraction[][] matrix) {
        return SimplexAlgorithm.simplexHasNegativeLimit(matrix) ?
            SimplexAlgorithm.simplexSelectPivotColumnPhaseOne(matrix) :
                SimplexAlgorithm.simplexSelectPivotColumnPhaseTwo(matrix);
    }

    private static int simplexSelectPivotColumnPhaseOne(final BigFraction[][] matrix) {
        for (int row = 0; row < matrix.length - 2; row++) {
            if (matrix[row][matrix[row].length - 1].compareTo(BigFraction.ZERO) < 0) {
                BigFraction min = BigFraction.ZERO;
                int pivotColumn = -1;
                for (int col = 0; col < matrix[row].length - 1; col++) {
                    if (matrix[row][col].compareTo(min) < 0) {
                        min = matrix[row][col];
                        pivotColumn = col;
                    }
                }
                return pivotColumn;
            }
        }
        return -1;
    }

    private static int simplexSelectPivotColumnPhaseTwo(final BigFraction[][] matrix) {
        BigFraction max = BigFraction.ZERO;
        int pivotColumn = -1;
        for (int col = 0; col < matrix[matrix.length - 1].length - 1; col++) {
            if (matrix[matrix.length - 1][col].compareTo(max) > 0) {
                max = matrix[matrix.length - 1][col];
                pivotColumn = col;
            }
        }
        return pivotColumn;
    }

    private static int simplexSelectPivotRow(final BigFraction[] quotients) {
        if (quotients == null) {
            return -1;
        }
        BigFraction min = null;
        int pivotRow = -1;
        for (int row = 0; row < quotients.length; row++) {
            if (quotients[row] != null && (min == null || quotients[row].compareTo(min) < 0)) {
                min = quotients[row];
                pivotRow = row;
            }
        }
        return pivotRow;
    }

    private static SimplexTableau simplexStep(final SimplexTableau tableau) {
        final BigFraction[] target = tableau.problem.target;
        final BigFraction[][] matrix = ArrayUtils.copy(tableau.problem.matrix);
        final int[] basicVariables = ArrayUtils.copy(tableau.basicVariables);
        final int pivotRow =
            tableau.pivotRow < 0 ? SimplexAlgorithm.simplexFirstRowWithNegativeLimit(matrix) : tableau.pivotRow;
        SimplexAlgorithm.simplexBaseSwap(pivotRow, tableau.pivotColumn, matrix, basicVariables, target);
        final int pivotColumn = SimplexAlgorithm.simplexSelectPivotColumn(matrix);
        final BigFraction[] quotients = SimplexAlgorithm.simplexComputeQuotients(matrix, pivotColumn);
        return new SimplexTableau(
            new SimplexProblem(target, matrix),
            basicVariables,
            quotients,
            SimplexAlgorithm.simplexSelectPivotRow(quotients),
            pivotColumn
        );
    }

    private static String simplexTableColumnDefinition(final int cols) {
        return String.format("|*{%d}{C{9mm}|}", cols);
    }

    private static BigFraction simplexTargetValue(final BigFraction[] target, final int index) {
        return index < target.length ? target[index] : BigFraction.ZERO;
    }

    private static String[][] toSimplexTableau(final SimplexTableau tableau, final boolean fill) {
        final String[][] result = new String[tableau.problem.matrix.length + 2][tableau.problem.matrix[0].length + 3];
        result[0][0] = "";
        result[0][1] = "$c_j$";
        result[0][result[0].length - 2] = "";
        result[0][result[0].length - 1] = "";
        result[1][0] = "$c_i^B\\downarrow$";
        result[1][1] = "$B$";
        result[1][result[0].length - 2] = "$b_i$";
        result[1][result[0].length - 1] = "$q_i$";
        result[result.length - 2][0] = "";
        result[result.length - 1][0] = "";
        result[result.length - 2][1] = "$z_j$";
        result[result.length - 1][1] = "{\\scriptsize $c_j - z_j$}";
        result[result.length - 2][result[0].length - 1] = "";
        result[result.length - 1][result[0].length - 1] = "";
        for (int index = 1; index < result[0].length - 3; index++) {
            result[1][index + 1] = String.format("$%s_{%d}$", AlgebraAlgorithms.VARIABLE_NAME, index);
        }
        for (int col = 0; col < tableau.problem.target.length; col++) {
            result[0][col + 2] =
                fill ? String.format("$%s$", AlgebraAlgorithms.toCoefficient(tableau.problem.target[col])) : "";
        }
        for (int col = tableau.problem.target.length + 2; col < result[0].length - 2; col++) {
            result[0][col] = fill ? "$0$" : "";
        }
        for (int row = 0; row < tableau.problem.matrix.length; row++) {
            for (int col = 0; col < tableau.problem.matrix[row].length; col++) {
                result[row + 2][col + 2] =
                    fill ?
                        String.format("$%s$", AlgebraAlgorithms.toCoefficient(tableau.problem.matrix[row][col])) :
                            "";
            }
        }
        for (int i = 0; i < tableau.basicVariables.length; i++) {
            if (fill) {
                final int variableIndex = tableau.basicVariables[i];
                result[i + 2][0] =
                    variableIndex < tableau.problem.target.length ?
                        String.format(
                            "$%s$",
                            AlgebraAlgorithms.toCoefficient(tableau.problem.target[variableIndex])
                        ) :
                            "$0$";
                result[i + 2][1] = String.format("$%s_{%d}$", AlgebraAlgorithms.VARIABLE_NAME, variableIndex + 1);
            } else {
                result[i + 2][0] = "";
                result[i + 2][1] = "";
            }
            if (tableau.quotients == null || tableau.quotients[i] == null) {
                result[i + 2][result[i + 2].length - 1] = "";
            } else {
                result[i + 2][result[i + 2].length - 1] =
                    fill ? String.format("$%s$", AlgebraAlgorithms.toCoefficient(tableau.quotients[i])) : "";
            }
        }
        result[result.length - 1][result[0].length - 2] = "";
        return result;
    }

    private SimplexAlgorithm() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final SimplexProblem problem = SimplexAlgorithm.parseOrGenerateSimplexProblem(input.options);
        final SimplexSolution solution = SimplexAlgorithm.simplex(problem);
        SimplexAlgorithm.printSimplexExercise(problem, solution, input.options, input.exerciseWriter);
        SimplexAlgorithm.printSimplexSolution(problem, solution, input.options, input.solutionWriter);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

}
