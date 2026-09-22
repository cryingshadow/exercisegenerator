package exercisegenerator.algorithms.optimization;

import java.io.*;
import java.util.*;

import org.apache.commons.math3.fraction.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.algebra.*;
import exercisegenerator.structures.optimization.*;

public class SimplexStepAlgorithm implements AlgorithmImplementation<SimplexStepProblem, Matrix> {

    public static final SimplexStepAlgorithm INSTANCE = new SimplexStepAlgorithm();

    public static void simplexBaseSwap(
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
            final BigFraction sum = BigFraction.ZERO;
//            for (int row = 0; row < basicVariables.length; row++) {
//                sum =
//                    sum.add(
//                        matrix.getCoefficient(column, row)
//                        .multiply(SimplexStepAlgorithm.simplexTargetValue(target, basicVariables[row]))
//                    );
//            }
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

    private SimplexStepAlgorithm() {}

    @Override
    public Matrix apply(final SimplexStepProblem problem) {
        return new Matrix(0,0,0);
//        final List<Pair<SimplexProblem, List<SimplexTableau>>> branches =
//            new LinkedList<Pair<SimplexProblem, List<SimplexTableau>>>();
//        SimplexTableau tableau = SimplexStepAlgorithm.simplexInitializeTableau(problem);
//        final List<SimplexTableau> firstBranch = new LinkedList<SimplexTableau>();
//        firstBranch.add(tableau);
//        branches.add(new Pair<SimplexProblem, List<SimplexTableau>>(problem, firstBranch));
//        int branchIndex = 0;
//        while (branchIndex < branches.size()) {
//            final Pair<SimplexProblem, List<SimplexTableau>> branch = branches.get(branchIndex);
//            tableau = branch.y.getLast();
//            SimplexAnswer answer = SimplexStepAlgorithm.simplexComputeAnswer(tableau);
//            while (answer == SimplexAnswer.INCOMPLETE) {
//                tableau = SimplexStepAlgorithm.simplexStep(tableau);
//                branch.y.add(tableau);
//                answer = SimplexStepAlgorithm.simplexComputeAnswer(tableau);
//            }
//            final Optional<Pair<Integer, BigFraction>> violation = tableau.getIntegralViolation();
//            if (violation.isPresent() && (answer == SimplexAnswer.SOLVED || answer == SimplexAnswer.UNBOUNDED)) {
//                SimplexStepAlgorithm.branchAndCut(tableau, violation.get(), branch, branchIndex, branches);
//            } else {
//                branchIndex++;
//            }
//        }
//        return new SimplexSolution(branches, SimplexStepAlgorithm.simplexComputeAnswer(branches));
    }

    @Override
    public String commandPrefix() {
        return "SimplexStep";
    }

    @Override
    public SimplexStepProblem generateProblem(final Parameters<Flag> options) {
//        final int numberOfVariables = AlgebraAlgorithms.parseOrGenerateNumberOfVariables(options);
//        final int numberOfInequalities = AlgebraAlgorithms.generateNumberOfInequalitiesOrEquations();
//        final BigFraction[] target = SimplexStepAlgorithm.generateTargetFunction(numberOfVariables);
//        final BigFraction[][] conditions =
//            AlgebraAlgorithms.generateInequalitiesOrEquations(numberOfInequalities, numberOfVariables);
//        final List<Integer> integral = SimplexStepAlgorithm.generateIntegralConditions(target.length, options);
        return new SimplexStepProblem(new Matrix(0, 0, 0), 0, 0);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    @Override
    public List<SimplexStepProblem> parseProblems(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        return List.of();
//        final List<String> rows = new ArrayList<String>();
//        String line = reader.readLine();
//        while (line != null) {
//            rows.addAll(Arrays.stream(line.split(";")).filter(row -> !row.isBlank()).toList());
//            line = reader.readLine();
//        }
//        final String firstRow = rows.getFirst();
//        final BigFraction[] target = SimplexStepAlgorithm.parseTargetFunction(firstRow);
//        final List<Integer> integral = SimplexStepAlgorithm.parseIntegralConditions(firstRow);
//        final BigFraction[][] conditions = new BigFraction[rows.size() - 1][target.length + 1];
//        for (int row = 0; row < conditions.length; row++) {
//            final String[] numbers = rows.get(row + 1).split(",");
//            if (numbers.length != conditions[row].length) {
//                throw new IOException(
//                    "The rows of the matrix must have exactly one more entry than the target function!"
//                );
//            }
//            for (int col = 0; col < numbers.length; col++) {
//                conditions[row][col] = AlgebraAlgorithms.parseRationalNumber(numbers[col]);
//            }
//        }
//        return List.of(new SimplexProblem(target, new Matrix(conditions, target.length), integral));
    }

    @Override
    public void printAfterSingleProblemInstance(
        final SimplexStepProblem problem,
        final Matrix solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        if (options.hasKeySetToValue(Flag.VARIANT, 2)) {
            writer.write("Welche beiden linearen Programme in Standard-Maximum-Form m\\\"ussen nun gem\\\"a\\ss{} ");
            writer.write("dem Branch-And-Cut-Verfahren im n\\\"achsten Schritt gel\\\"ost werden, um die in dieser ");
            writer.write("L\\\"osung enthaltene Verletzung der Ganzzahligkeitsbedingungen zu verhindern?");
            Main.newLine(writer);
        } else {
            LaTeXUtils.printVerticalProtectedSpace(writer);
            writer.write("L\\\"osen Sie dieses lineare Programm mithilfe des \\emphasize{Simplex-Algorithmus}. ");
            writer.write("F\\\"ullen Sie dazu die nachfolgenden Simplex-Tableaus aus und geben Sie eine optimale ");
            writer.write(String.format("Belegung f\\\"ur die Variablen $%s_{1}", LaTeXUtils.MATH_VARIABLE_NAME));
//            for (int index = 1; index < problem.target().length; index++) {
//                writer.write(String.format(", %s_{%d}", LaTeXUtils.MATH_VARIABLE_NAME, index + 1));
//            }
            writer.write("$ und den daraus resultierenden Wert der Zielfunktion an oder begr\\\"unden Sie, warum es ");
            writer.write("keine solche optimale Belegung gibt.");
            Main.newLine(writer);
        }
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<SimplexStepProblem> problems,
        final List<Matrix> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        if (options.hasKeySetToValue(Flag.VARIANT, 2)) {
            writer.write("Geben Sie zu jedem der folgenden \\emphasize{linearen Programme} in Standard-Maximum-Form ");
            writer.write("und deren zugeh\\\"origen L\\\"osungen ohne Branch-And-Cut an, welche beiden linearen ");
            writer.write("Programme in Standard-Maximum-Form gem\\\"a\\ss{} dem Branch-And-Cut-Verfahren im ");
            writer.write("n\\\"achsten Schritt gel\\\"ost werden m\\\"ussen, um die in der jeweiligen L\\\"osung ");
            writer.write("enthaltene Verletzung der Ganzzahligkeitsbedingungen zu verhindern.");
        } else {
            writer.write("L\\\"osen Sie die folgenden \\emphasize{linearen Programme} in Standard-Maximum-Form ");
            writer.write("mithilfe des \\emphasize{Simplex-Algorithmus}. F\\\"ullen Sie dazu die jeweils ");
            writer.write("nachfolgenden Simplex-Tableaus aus und geben Sie eine optimale Belegung f\\\"ur die ");
            writer.write("Variablen der jeweiligen Zielfunktion sowie den daraus resultierenden Wert dieser ");
            writer.write("Zielfunktion an oder begr\\\"unden Sie, warum es keine solche optimale Belegung gibt.");
            Main.newLine(writer);
        }
    }


    @Override
    public void printBeforeSingleProblemInstance(
        final SimplexStepProblem problem,
        final Matrix solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Gegeben sei das folgende \\emphasize{lineare Programm} in Standard-Maximum-Form:\\\\");
        Main.newLine(writer);
    }

    @Override
    public void printProblemInstance(
        final SimplexStepProblem problem,
        final Matrix solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
//        SimplexStepAlgorithm.printSimplexProblem(problem, SimplexStepAlgorithm.parseMaxBreak(options), writer);
        if (options.hasKeySetToValue(Flag.VARIANT, 2)) {
            LaTeXUtils.printVerticalProtectedSpace(writer);
            writer.write("Der Simplex-Algorithmus (ohne Branch-And-Cut) liefert f\\\"ur dieses lineare Programm die ");
            writer.write("folgende optimale L\\\"osung:");
            Main.newLine(writer);
//            final SimplexTableau beforeBranch = SimplexStepAlgorithm.getTableauBeforeFirstBranch(solution);
//            final List<BigFraction> result = beforeBranch.getResult().get();
            writer.write("\\[");
//            writer.write(
//                String.format(
//                    "%s_{1}^* = %s",
//                    LaTeXUtils.MATH_VARIABLE_NAME,
//                    LaTeXUtils.toCoefficient(result.get(0))
//                )
//            );
//            for (int i = 1; i < result.size() - 1; i++) {
//                writer.write(
//                    String.format(
//                        ", %s_{%d}^* = %s",
//                        LaTeXUtils.MATH_VARIABLE_NAME,
//                        i + 1,
//                        LaTeXUtils.toCoefficient(result.get(i))
//                    )
//                );
//            }
            writer.write("\\]");
            Main.newLine(writer);
        }
    }

    @Override
    public void printSolutionInstance(
        final SimplexStepProblem problem,
        final Matrix solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
    }

    @Override
    public void printSolutionSpace(
        final SimplexStepProblem problem,
        final Matrix solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
    }

}
