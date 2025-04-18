package exercisegenerator.algorithms.optimization;

import java.io.*;
import java.math.*;
import java.util.*;
import java.util.stream.*;

import org.apache.commons.math3.fraction.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.algorithms.algebra.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.algebra.*;
import exercisegenerator.structures.optimization.*;
import exercisegenerator.util.*;

public class SimplexAlgorithm implements AlgorithmImplementation<SimplexProblem, SimplexSolution> {

    public static final SimplexAlgorithm INSTANCE = new SimplexAlgorithm();

    static SimplexAnswer simplexComputeAnswer(final SimplexTableau tableau) {
        final Matrix matrix = tableau.problem().conditions();
        if (SimplexAlgorithm.simplexHasNegativeLimit(matrix)) {
            if (SimplexAlgorithm.simplexHasNegativeLimitWithNonNegativeRow(matrix)) {
                return SimplexAnswer.UNSOLVABLE;
            } else {
                return SimplexAnswer.INCOMPLETE;
            }
        }
        if (SimplexAlgorithm.simplexPivotColumnExists(matrix)) {
            if (SimplexAlgorithm.simplexPivotRowExists(tableau.quotients())) {
                return SimplexAnswer.INCOMPLETE;
            } else {
                return SimplexAnswer.UNBOUNDED;
            }
        }
        return SimplexAnswer.SOLVED;
    }

    private static List<Integer> generateIntegralConditions(final int numberOfVariables, final Parameters<Flag> options) {
        if (options.getOrDefault(Flag.VARIANT, "").equals("1") ) {
            return IntStream.range(0, numberOfVariables)
                .filter(i -> Main.RANDOM.nextBoolean() ? true : false)
                .boxed()
                .toList();
        }
        return List.of();
    }

    private static BigFraction generateNonZeroCoefficient(final int oneToChanceForNegative) {
        return new BigFraction(
            (Main.RANDOM.nextInt(10) + 1)
            * (Main.RANDOM.nextInt(oneToChanceForNegative) == 0 ? -1 : 1)
        );
    }

    private static SimplexProblem generateSimplexProblem(final Parameters<Flag> options) {
        final int numberOfVariables = AlgebraAlgorithms.parseOrGenerateNumberOfVariables(options);
        final int numberOfInequalities = AlgebraAlgorithms.generateNumberOfInequalitiesOrEquations();
        final BigFraction[] target = SimplexAlgorithm.generateTargetFunction(numberOfVariables);
        final BigFraction[][] conditions =
            AlgebraAlgorithms.generateInequalitiesOrEquations(numberOfInequalities, numberOfVariables);
        final List<Integer> integral = SimplexAlgorithm.generateIntegralConditions(target.length, options);
        return new SimplexProblem(target, new Matrix(conditions, target.length), integral);
    }

    private static BigFraction[] generateTargetFunction(final int numberOfVariables) {
        final BigFraction[] target = new BigFraction[numberOfVariables];
        for (int i = 0; i < numberOfVariables; i++) {
            target[i] = SimplexAlgorithm.generateNonZeroCoefficient(4);
        }
        return target;
    }

    private static List<Integer> parseIntegralConditions(final String line) {
        final String[] numbers = line.split(",");
        final List<Integer> result = new LinkedList<Integer>();
        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i].toLowerCase().contains("i")) {
                result.add(i);
            }
        }
        return result;
    }

    private static SimplexProblem parseSimplexProblem(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        final List<String> rows = new ArrayList<String>();
        String line = reader.readLine();
        while (line != null) {
            rows.addAll(Arrays.stream(line.split(";")).filter(row -> !row.isBlank()).toList());
            line = reader.readLine();
        }
        final String firstRow = rows.getFirst();
        final BigFraction[] target = SimplexAlgorithm.parseTargetFunction(firstRow);
        final List<Integer> integral = SimplexAlgorithm.parseIntegralConditions(firstRow);
        final BigFraction[][] conditions = new BigFraction[rows.size() - 1][target.length + 1];
        for (int row = 0; row < conditions.length; row++) {
            final String[] numbers = rows.get(row + 1).split(",");
            if (numbers.length != conditions[row].length) {
                throw new IOException(
                    "The rows of the matrix must have exactly one more entry than the target function!"
                );
            }
            for (int col = 0; col < numbers.length; col++) {
                conditions[row][col] = AlgebraAlgorithms.parseRationalNumber(numbers[col]);
            }
        }
        return new SimplexProblem(target, new Matrix(conditions, target.length), integral);
    }

    private static BigFraction[] parseTargetFunction(final String line) {
        final String[] numbers = line.split(",");
        final BigFraction[] target = new BigFraction[numbers.length];
        for (int i = 0; i < target.length; i++) {
            target[i] = AlgebraAlgorithms.parseRationalNumber(numbers[i].replaceAll("i|I", ""));
        }
        return target;
    }

    private static void printSimplexProblem(final SimplexProblem problem, final BufferedWriter writer)
    throws IOException {
        writer.write("Maximiere $z(\\mathbf{x}) = ");
        int firstNonZeroIndex = 0;
        for (final BigFraction coefficient : problem.target()) {
            if (coefficient.compareTo(BigFraction.ZERO) != 0) {
                break;
            }
            firstNonZeroIndex++;
        }
        if (firstNonZeroIndex == problem.target().length) {
            writer.write("0");
        } else {
            writer.write(
                LaTeXUtils.toCoefficientWithVariable(
                    true,
                    false,
                    true,
                    firstNonZeroIndex + 1,
                    problem.target()[firstNonZeroIndex]
                )
            );
            for (int index = firstNonZeroIndex + 1; index < problem.target().length; index++) {
                writer.write(
                    LaTeXUtils.toCoefficientWithVariable(
                        false,
                        false,
                        false,
                        index + 1,
                        problem.target()[index]
                    )
                );
            }
        }
        writer.write("$\\\\");
        Main.newLine(writer);
        writer.write("unter den folgenden Nebenbedingungen:\\\\");
        Main.newLine(writer);
        AlgebraAlgorithms.printMatrixAsInequalitiesOrEquations(problem.conditions(), "\\leq", writer);
        writer.write(String.format("$%s_{1}", LaTeXUtils.MATH_VARIABLE_NAME));
        for (int index = 1; index < problem.target().length; index++) {
            writer.write(String.format(", %s_{%d}", LaTeXUtils.MATH_VARIABLE_NAME, index + 1));
        }
        writer.write(" \\geq 0$\\\\");
        if (!problem.integral().isEmpty()) {
            Main.newLine(writer);
            writer.write("$");
            writer.write(
                problem.integral()
                .stream()
                .map(index -> String.format("%s_{%d}", LaTeXUtils.MATH_VARIABLE_NAME, index + 1))
                .collect(Collectors.joining(","))
            );
            writer.write(" \\in \\ints$\\\\");
        }
        writer.write("[2ex]");
        Main.newLine(writer);
    }

    private static void printTableau(
        final SimplexTableau tableau,
        final boolean withSolution,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printAdjustboxBeginning(writer, "max width=0.9\\textwidth", "center");
        LaTeXUtils.printTable(
            SimplexAlgorithm.toSimplexTableau(tableau, withSolution),
            Optional.empty(),
            SimplexAlgorithm::simplexTableColumnDefinition,
            true,
            0,
            writer
        );
        LaTeXUtils.printAdjustboxEnd(writer);
    }

    private static void simplexBaseSwap(
        final int pivotRow,
        final int pivotColumn,
        final Matrix matrix,
        final int[] basicVariables,
        final BigFraction[] target
    ) {
        if (!matrix.isOne(pivotColumn, pivotRow)) {
            final BigFraction pivotElement = matrix.getCoefficient(pivotColumn, pivotRow);
            for (int column = 0; column < matrix.getNumberOfColumns(); column++) {
                matrix.setCoefficient(
                    column,
                    pivotRow,
                    matrix.getCoefficient(column, pivotRow).divide(pivotElement)
                );
            }
        }
        for (int row = 0; row < matrix.getNumberOfRows() - 2; row++) {
            if (row != pivotRow && !matrix.isZero(pivotColumn, row)) {
                final BigFraction factor = matrix.getCoefficient(pivotColumn, row);
                for (int column = 0; column < matrix.getNumberOfColumns(); column++) {
                    matrix.setCoefficient(
                        column,
                        row,
                        matrix.getCoefficient(column, row)
                        .subtract(matrix.getCoefficient(column, pivotRow).multiply(factor))
                    );
                }
            }
        }
        basicVariables[pivotRow] = pivotColumn;
        for (int column = 0; column < matrix.getNumberOfColumns(); column++) {
            BigFraction sum = BigFraction.ZERO;
            for (int row = 0; row < basicVariables.length; row++) {
                sum =
                    sum.add(
                        matrix.getCoefficient(column, row)
                        .multiply(SimplexAlgorithm.simplexTargetValue(target, basicVariables[row]))
                    );
            }
            matrix.setCoefficient(column, basicVariables.length, sum);
            if (column < target.length) {
                matrix.setCoefficient(column, basicVariables.length + 1, target[column].subtract(sum));
            } else if (column < matrix.getIndexOfLastColumn()) {
                matrix.setCoefficient(column, basicVariables.length + 1, sum.negate());
            } else {
                matrix.setCoefficient(column, basicVariables.length + 1, BigFraction.ZERO);
            }
        }
    }

    private static BigFraction[] simplexComputeQuotients(final Matrix matrix, final int pivotColumn) {
        if (pivotColumn < 0) {
            return null;
        }
        final BigFraction[] quotients = new BigFraction[matrix.getNumberOfRows() - 2];
        for (int row = 0; row < quotients.length; row++) {
            if (
                matrix.isPositive(pivotColumn, row)
                && matrix.isNonNegative(matrix.getIndexOfLastColumn(), row)
            ) {
                quotients[row] = matrix.getLastCoefficientOfRow(row).divide(matrix.getCoefficient(pivotColumn, row));
            } else {
                quotients[row] = null;
            }
        }
        return quotients;
    }

    private static int simplexFirstRowWithNegativeLimit(final Matrix matrix) {
        for (int row = 0; row < matrix.getNumberOfRows() - 2; row++) {
            if (matrix.isNegative(matrix.getIndexOfLastColumn(), row)) {
                return row;
            }
        }
        return -1;
    }

    private static boolean simplexHasNegativeLimit(final Matrix matrix) {
        for (int row = 0; row < matrix.getNumberOfRows() - 2; row++) {
            if (matrix.isNegative(matrix.getIndexOfLastColumn(), row)) {
                return true;
            }
        }
        return false;
    }

    private static boolean simplexHasNegativeLimitWithNonNegativeRow(final Matrix matrix) {
        for (int row = 0; row < matrix.getNumberOfRows() - 2; row++) {
            if (matrix.isNegative(matrix.getIndexOfLastColumn(), row)) {
                boolean notFound = true;
                for (int column = 0; column < matrix.getIndexOfLastColumn(); column++) {
                    if (matrix.isNegative(column, row)) {
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

    private static Matrix simplexInitializeMatrix(final SimplexProblem problem) {
        final int initialNumberOfRows = problem.conditions().getNumberOfRows();
        final int numberOfRows = initialNumberOfRows + 2;
        final int numberOfColumns = problem.conditions().getNumberOfColumns() + initialNumberOfRows;
        final Matrix result = new Matrix(numberOfColumns, numberOfRows, numberOfColumns - 1);
        final int initialRowLength = problem.conditions().getIndexOfLastColumn();
        final int newRowLength = result.getIndexOfLastColumn();
        for (int row = 0; row < initialNumberOfRows; row++) {
            for (int column = 0; column < initialRowLength; column++) {
                result.setCoefficient(column, row, problem.conditions().getCoefficient(column, row));
            }
            for (int column = initialRowLength; column < newRowLength; column++) {
                if (column - initialRowLength == row) {
                    result.setCoefficient(column, row, BigFraction.ONE);
                } else {
                    result.setCoefficient(column, row, BigFraction.ZERO);
                }
            }
            result.setCoefficient(newRowLength, row, problem.conditions().getCoefficient(initialRowLength, row));
        }
        for (int column = 0; column < result.getNumberOfColumns(); column++) {
            result.setCoefficient(column, initialNumberOfRows, BigFraction.ZERO);
        }
        for (int column = 0; column < initialRowLength; column++) {
            result.setCoefficient(column, initialNumberOfRows + 1, problem.target()[column]);
        }
        for (int column = initialRowLength; column < result.getNumberOfColumns(); column++) {
            result.setCoefficient(column, initialNumberOfRows + 1, BigFraction.ZERO);
        }
        return result;
    }

    private static SimplexTableau simplexInitializeTableau(final SimplexProblem problem) {
        final int numberOfInitialVariables = problem.target().length;
        final int numberOfAdditionalVariables = problem.conditions().getNumberOfRows();
        final Matrix newMatrix = SimplexAlgorithm.simplexInitializeMatrix(problem);
        final int[] basicVariables = new int[numberOfAdditionalVariables];
        for (int i = 0; i < numberOfAdditionalVariables; i++) {
            basicVariables[i] = i + numberOfInitialVariables;
        }
        final int pivotColumn = SimplexAlgorithm.simplexSelectPivotColumn(newMatrix);
        final BigFraction[] quotients = SimplexAlgorithm.simplexComputeQuotients(newMatrix, pivotColumn);
        return new SimplexTableau(
            new SimplexProblem(problem.target(), newMatrix, problem.integral()),
            basicVariables,
            quotients,
            SimplexAlgorithm.simplexSelectPivotRow(quotients),
            pivotColumn
        );
    }

    private static boolean simplexPivotColumnExists(final Matrix matrix) {
        for (int column = 0; column < matrix.getIndexOfLastColumn(); column++) {
            if (matrix.isPositive(column, matrix.getIndexOfLastRow())) {
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

    private static int simplexSelectPivotColumn(final Matrix matrix) {
        return SimplexAlgorithm.simplexHasNegativeLimit(matrix) ?
            SimplexAlgorithm.simplexSelectPivotColumnPhaseOne(matrix) :
                SimplexAlgorithm.simplexSelectPivotColumnPhaseTwo(matrix);
    }

    private static int simplexSelectPivotColumnPhaseOne(final Matrix matrix) {
        for (int row = 0; row < matrix.getNumberOfRows() - 2; row++) {
            if (matrix.isNegative(matrix.getIndexOfLastColumn(), row)) {
                BigFraction min = BigFraction.ZERO;
                int pivotColumn = -1;
                for (int column = 0; column < matrix.getIndexOfLastColumn(); column++) {
                    if (matrix.getCoefficient(column, row).compareTo(min) < 0) {
                        min = matrix.getCoefficient(column, row);
                        pivotColumn = column;
                    }
                }
                return pivotColumn;
            }
        }
        return -1;
    }

    private static int simplexSelectPivotColumnPhaseTwo(final Matrix matrix) {
        BigFraction max = BigFraction.ZERO;
        int pivotColumn = -1;
        for (int column = 0; column < matrix.getIndexOfLastColumn(); column++) {
            if (matrix.getLastCoefficientOfColumn(column).compareTo(max) > 0) {
                max = matrix.getLastCoefficientOfColumn(column);
                pivotColumn = column;
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
        final BigFraction[] target = tableau.problem().target();
        final Matrix matrix = tableau.problem().conditions().copy();
        final int[] basicVariables = ArrayUtils.copy(tableau.baseVariables());
        final int pivotRow =
            tableau.pivotRow() < 0 ? SimplexAlgorithm.simplexFirstRowWithNegativeLimit(matrix) : tableau.pivotRow();
        SimplexAlgorithm.simplexBaseSwap(pivotRow, tableau.pivotColumn(), matrix, basicVariables, target);
        final int pivotColumn = SimplexAlgorithm.simplexSelectPivotColumn(matrix);
        final BigFraction[] quotients = SimplexAlgorithm.simplexComputeQuotients(matrix, pivotColumn);
        return new SimplexTableau(
            new SimplexProblem(target, matrix, tableau.problem().integral()),
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
        final int numberOfRows = tableau.problem().conditions().getNumberOfRows() + 2;
        final int numberOfColumns = tableau.problem().conditions().getNumberOfColumns() + 3;
        final String[][] result = new String[numberOfRows][numberOfColumns];
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
            result[1][index + 1] = String.format("$%s_{%d}$", LaTeXUtils.MATH_VARIABLE_NAME, index);
        }
        for (int col = 0; col < tableau.problem().target().length; col++) {
            result[0][col + 2] =
                fill ? String.format("$%s$", LaTeXUtils.toCoefficient(tableau.problem().target()[col])) : "";
        }
        for (int col = tableau.problem().target().length + 2; col < result[0].length - 2; col++) {
            result[0][col] = fill ? "$0$" : "";
        }
        for (int row = 0; row < tableau.problem().conditions().getNumberOfRows(); row++) {
            for (int column = 0; column < tableau.problem().conditions().getNumberOfColumns(); column++) {
                result[row + 2][column + 2] =
                    fill ?
                        String.format(
                            "$%s$",
                            LaTeXUtils.toCoefficient(tableau.problem().conditions().getCoefficient(column, row))
                        ) :
                            "";
            }
        }
        for (int i = 0; i < tableau.baseVariables().length; i++) {
            if (fill) {
                final int variableIndex = tableau.baseVariables()[i];
                result[i + 2][0] =
                    variableIndex < tableau.problem().target().length ?
                        String.format(
                            "$%s$",
                            LaTeXUtils.toCoefficient(tableau.problem().target()[variableIndex])
                        ) :
                            "$0$";
                result[i + 2][1] = String.format("$%s_{%d}$", LaTeXUtils.MATH_VARIABLE_NAME, variableIndex + 1);
            } else {
                result[i + 2][0] = "";
                result[i + 2][1] = "";
            }
            if (tableau.quotients() == null || tableau.quotients()[i] == null) {
                result[i + 2][result[i + 2].length - 1] = "";
            } else {
                result[i + 2][result[i + 2].length - 1] =
                    fill ? String.format("$%s$", LaTeXUtils.toCoefficient(tableau.quotients()[i])) : "";
            }
        }
        result[result.length - 1][result[0].length - 2] = "";
        return result;
    }

    private SimplexAlgorithm() {}

    @Override
    public SimplexSolution apply(final SimplexProblem problem) {
        final List<Pair<SimplexProblem, List<SimplexTableau>>> branches =
            new LinkedList<Pair<SimplexProblem, List<SimplexTableau>>>();
        SimplexTableau tableau = SimplexAlgorithm.simplexInitializeTableau(problem);
        final List<SimplexTableau> firstBranch = new LinkedList<SimplexTableau>();
        firstBranch.add(tableau);
        branches.add(new Pair<SimplexProblem, List<SimplexTableau>>(problem, firstBranch));
        int branchIndex = 0;
        while (branchIndex < branches.size()) {
            final Pair<SimplexProblem, List<SimplexTableau>> branch = branches.get(branchIndex);
            tableau = branch.y.getLast();
            SimplexAnswer answer = SimplexAlgorithm.simplexComputeAnswer(tableau);
            while (answer == SimplexAnswer.INCOMPLETE) {
                tableau = SimplexAlgorithm.simplexStep(tableau);
                branch.y.add(tableau);
                answer = SimplexAlgorithm.simplexComputeAnswer(tableau);
            }
            final Optional<Pair<Integer, BigFraction>> violation = tableau.getIntegralViolation();
            if (violation.isPresent() && (answer == SimplexAnswer.SOLVED || answer == SimplexAnswer.UNBOUNDED)) {
                this.branchAndCut(tableau, violation.get(), branch, branchIndex, branches);
            } else {
                branchIndex++;
            }
        }
        return new SimplexSolution(branches, this.simplexComputeAnswer(branches));
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    @Override
    public SimplexProblem parseOrGenerateProblem(final Parameters<Flag> options) throws IOException {
        return new ParserAndGenerator<SimplexProblem>(
            SimplexAlgorithm::parseSimplexProblem,
            SimplexAlgorithm::generateSimplexProblem
        ).getResult(options);
    }

    @Override
    public void printExercise(
        final SimplexProblem problem,
        final SimplexSolution solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Gegeben sei das folgende \\emphasize{lineare Programm} in Standard-Maximum-Form:\\\\");
        Main.newLine(writer);
        SimplexAlgorithm.printSimplexProblem(problem, writer);
        writer.write("L\\\"osen Sie dieses lineare Programm mithilfe des \\emphasize{Simplex-Algorithmus}. ");
        writer.write("F\\\"ullen Sie dazu die nachfolgenden Simplex-Tableaus und geben Sie eine optimale Belegung ");
        writer.write(String.format("f\\\"ur die Variablen $%s_{1}", LaTeXUtils.MATH_VARIABLE_NAME));
        for (int index = 1; index < problem.target().length; index++) {
            writer.write(String.format(", %s_{%d}", LaTeXUtils.MATH_VARIABLE_NAME, index + 1));
        }
        writer.write("$ und den daraus resultierenden Wert der Zielfunktion an oder begr\\\"unden Sie, warum es ");
        writer.write("keine solche optimale Belegung gibt.");
        Main.newLine(writer);
        Main.newLine(writer);
        final SolutionSpaceMode mode = SolutionSpaceMode.parsePreprintMode(options);
        switch (mode) {
        case SOLUTION_SPACE:
            LaTeXUtils.printSolutionSpaceBeginning(Optional.empty(), options, writer);
            // fall-through
        case ALWAYS:
            writer.write("{\\renewcommand{\\arraystretch}{1.5}");
            Main.newLine(writer);
            boolean first = true;
            final int[] pagebreakCounters =
                LaTeXUtils.parsePagebreakCountersForExercise(options.getOrDefault(Flag.KEYVALUE, ""));
            int tableaus = 0;
            int counterIndex = 0;
            for (final Pair<SimplexProblem, List<SimplexTableau>> branch : solution.branches()) {
                if (first) {
                    first = false;
                } else {
                    Main.newLine(writer);
                    writer.write("Neuer Zweig:\\\\");
                }
                for (final SimplexTableau tableau : branch.y) {
                    Main.newLine(writer);
                    if (counterIndex < pagebreakCounters.length && tableaus >= pagebreakCounters[counterIndex]) {
                        writer.write("\\newpage");
                        Main.newLine(writer);
                        Main.newLine(writer);
                        tableaus = 0;
                        counterIndex++;
                    }
                    SimplexAlgorithm.printTableau(tableau, false, writer);
                    tableaus++;
                }
            }
            Main.newLine(writer);
            writer.write("\\renewcommand{\\arraystretch}{1}}");
            Main.newLine(writer);
            LaTeXUtils.printVerticalProtectedSpace(writer);
            writer.write("Ergebnis:");
            Main.newLine(writer);
            if (mode == SolutionSpaceMode.SOLUTION_SPACE) {
                LaTeXUtils.printSolutionSpaceEnd(Optional.of("2ex"), options, writer);
            }
            Main.newLine(writer);
            break;
        default:
            //do nothing
        }
    }

    @Override
    public void printSolution(
        final SimplexProblem problem,
        final SimplexSolution solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("{\\renewcommand{\\arraystretch}{1.5}");
        Main.newLine(writer);
        boolean first = true;
        final int[] pagebreakCounters =
            LaTeXUtils.parsePagebreakCountersForSolution(options.getOrDefault(Flag.KEYVALUE, ""));
        int tableaus = 0;
        int counterIndex = 0;
        for (final Pair<SimplexProblem, List<SimplexTableau>> branch : solution.branches()) {
            if (first) {
                first = false;
            } else {
                Main.newLine(writer);
                writer.write("Neuer Zweig:\\\\[2ex]");
            }
            for (final SimplexTableau tableau : branch.y) {
                Main.newLine(writer);
                if (counterIndex < pagebreakCounters.length && tableaus >= pagebreakCounters[counterIndex]) {
                    writer.write("\\newpage");
                    Main.newLine(writer);
                    Main.newLine(writer);
                    tableaus = 0;
                    counterIndex++;
                }
                SimplexAlgorithm.printTableau(tableau, true, writer);
                tableaus++;
            }
        }
        Main.newLine(writer);
        writer.write("\\renewcommand{\\arraystretch}{1}}");
        Main.newLine(writer);
        LaTeXUtils.printVerticalProtectedSpace(writer);
        writer.write("Ergebnis: ");
        switch (solution.answer()) {
        case UNBOUNDED:
            writer.write("unbeschr\\\"ankt, da wir eine Pivot-Spalte mit nicht-positiven Koeffizienten haben");
            break;
        case UNSOLVABLE:
            writer.write(
                "unl\\\"osbar, da wir eine Pivot-Zeile mit negativem Limit, aber nicht-negativen Koeffizienten haben"
            );
            break;
        default:
            final List<BigFraction> result = solution.getOptimalResult().get();
            writer.write(
                String.format(
                    "$%s_{1}^* = %s$",
                    LaTeXUtils.MATH_VARIABLE_NAME,
                    LaTeXUtils.toCoefficient(result.get(0))
                )
            );
            for (int i = 1; i < result.size() - 1; i++) {
                writer.write(
                    String.format(
                        ", $%s_{%d}^* = %s$",
                        LaTeXUtils.MATH_VARIABLE_NAME,
                        i + 1,
                        LaTeXUtils.toCoefficient(result.get(i))
                    )
                );
            }
            writer.write(String.format(", $z(\\mathbf{x}^*) = %s$", LaTeXUtils.toCoefficient(result.getLast())));
            break;
        }
        Main.newLine(writer);
        Main.newLine(writer);
    }

    private Pair<List<BigFraction[]>, List<BigFraction[]>> branchAndBound(
        final List<BigFraction[]> cuttingHyperPlanes,
        final Pair<Integer, BigFraction> violation,
        final int numberOfColumns
    ) {
        final BigFraction[] smallerRow = new BigFraction[numberOfColumns];
        final BigFraction[] biggerRow = new BigFraction[numberOfColumns];
        Arrays.fill(smallerRow, BigFraction.ZERO);
        Arrays.fill(biggerRow, BigFraction.ZERO);
        smallerRow[violation.x] = BigFraction.ONE;
        biggerRow[violation.x] = BigFraction.ONE.negate();
        final BigInteger floor = violation.y.getNumerator().divide(violation.y.getDenominator());
        smallerRow[smallerRow.length - 1] = new BigFraction(floor);
        biggerRow[biggerRow.length - 1] = new BigFraction(floor.add(BigInteger.ONE).negate());
        final List<BigFraction[]> smallerRows = new ArrayList<BigFraction[]>(cuttingHyperPlanes);
        final List<BigFraction[]> biggerRows = new ArrayList<BigFraction[]>(cuttingHyperPlanes);
        smallerRows.add(smallerRow);
        biggerRows.add(biggerRow);
        return new Pair<List<BigFraction[]>, List<BigFraction[]>>(smallerRows, biggerRows);
    }

    private void branchAndCut(
        final SimplexTableau tableau,
        final Pair<Integer, BigFraction> violation,
        final Pair<SimplexProblem, List<SimplexTableau>> branch,
        final int branchIndex,
        final List<Pair<SimplexProblem, List<SimplexTableau>>> branches
    ) {
        final SimplexProblem problem = tableau.problem();
        final List<BigFraction[]> cuttingHyperPlanes = this.computeCuttingHyperPlanes(problem);
        final Pair<List<BigFraction[]>, List<BigFraction[]>> branchRows =
            this.branchAndBound(cuttingHyperPlanes, violation, problem.target().length + 1);
        final Matrix originalMatrix = branch.x.conditions();
        final SimplexProblem smaller =
            new SimplexProblem(
                problem.target(),
                originalMatrix.insertRowsAtIndex(branchRows.x, originalMatrix.getNumberOfRows()),
                problem.integral()
            );
        final SimplexProblem bigger =
            new SimplexProblem(
                problem.target(),
                originalMatrix.insertRowsAtIndex(branchRows.y, originalMatrix.getNumberOfRows()),
                problem.integral()
            );
        branch.y.add(SimplexAlgorithm.simplexInitializeTableau(smaller));
        final List<SimplexTableau> nextBranch = new LinkedList<SimplexTableau>();
        nextBranch.add(SimplexAlgorithm.simplexInitializeTableau(bigger));
        branches.set(branchIndex, new Pair<SimplexProblem, List<SimplexTableau>>(smaller, branch.y));
        branches.add(new Pair<SimplexProblem, List<SimplexTableau>>(bigger, nextBranch));
    }

    private List<BigFraction[]> computeCuttingHyperPlanes(final SimplexProblem problem) {
        // TODO Auto-generated method stub
        final List<BigFraction[]> result = new LinkedList<BigFraction[]>();
        return result;
    }

    private SimplexAnswer simplexBestAnswer(final SimplexAnswer answer1, final SimplexAnswer answer2) {
        switch (answer1) {
        case UNSOLVABLE:
            return answer2;
        case INCOMPLETE:
        case UNBOUNDED:
            return answer2 == SimplexAnswer.INCOMPLETE ? answer2 : answer1;
        default:
            switch (answer2) {
            case INCOMPLETE:
            case UNBOUNDED:
                return answer2;
            default:
                return answer1;
            }
        }
    }

    private SimplexAnswer simplexComputeAnswer(final List<Pair<SimplexProblem, List<SimplexTableau>>> branches) {
        SimplexAnswer answer = SimplexAnswer.UNSOLVABLE;
        for (final Pair<SimplexProblem, List<SimplexTableau>> branch : branches) {
            answer = this.simplexBestAnswer(answer, SimplexAlgorithm.simplexComputeAnswer(branch.y.getLast()));
        }
        return answer;
    }

}
