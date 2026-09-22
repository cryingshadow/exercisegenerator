package exercisegenerator.algorithms.optimization;

import java.io.*;
import java.util.*;

import org.apache.commons.math3.fraction.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.algorithms.algebra.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.algebra.*;
import exercisegenerator.structures.optimization.*;

public class SimplexStepAlgorithm implements AlgorithmImplementation<SimplexStepProblem, Matrix> {

    public static final SimplexStepAlgorithm INSTANCE = new SimplexStepAlgorithm();

    public static void simplexBaseSwap(
        final int pivotRow,
        final int pivotColumn,
        final Matrix matrix,
        final int numberOfExcludedRows
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
        for (int row = 0; row < matrix.getNumberOfRows() - numberOfExcludedRows; row++) {
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
    }

    private SimplexStepAlgorithm() {}

    @Override
    public Matrix apply(final SimplexStepProblem problem) {
        final Matrix result = problem.matrix().copy();
        SimplexStepAlgorithm.simplexBaseSwap(problem.pivotRow(), problem.pivotColumn(), result, 0);
        return result;
    }

    @Override
    public String commandPrefix() {
        return "SimplexStep";
    }

    @Override
    public SimplexStepProblem generateProblem(final Parameters<Flag> options) {
        final SimplexProblem originalProblem = SimplexAlgorithm.INSTANCE.generateProblem(options);
        final SimplexSolution originalSolution = SimplexAlgorithm.INSTANCE.apply(originalProblem);
        final Pair<SimplexProblem, List<SimplexTableau>> pair =
            originalSolution.branches().get(Main.RANDOM.nextInt(originalSolution.branches().size()));
        final SimplexTableau tableau = pair.y.get(pair.y.size() > 1 ? Main.RANDOM.nextInt(pair.y.size() - 1) : 0);
        final int pivotRow;
        final int pivotColumn;
        if (tableau.pivotColumn() < 0 || tableau.pivotRow() < 0) {
            pivotColumn = Main.RANDOM.nextInt(tableau.problem().conditions().getNumberOfColumns());
            pivotRow = Main.RANDOM.nextInt(tableau.problem().conditions().getNumberOfRows());
        } else {
            pivotColumn = tableau.pivotColumn();
            pivotRow = tableau.pivotRow();
        }
        return new SimplexStepProblem(tableau.problem().conditions(), pivotColumn, pivotRow);
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
        final List<String> text = new LinkedList<String>();
        final int pivotColumn = Integer.parseInt(reader.readLine());
        final int pivotRow = Integer.parseInt(reader.readLine());
        String line = reader.readLine();
        while (line != null && !line.isBlank()) {
            text.add(line);
            line = reader.readLine();
        }
        return List.of(new SimplexStepProblem(AlgebraAlgorithms.parseMatrix(text), pivotColumn, pivotRow));
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<SimplexStepProblem> problems,
        final List<Matrix> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Transformieren Sie die folgenden Matrizen mithilfe von Gau\\ss{}-Jordan-Schritten so, dass die jeweils ");
        writer.write("angegebene Pivot-Spalte zu einem Einheitsvektor mit der 1 in der jeweils angegebenen Pivot-Zeile ");
        writer.write("wird:\\\\");
        Main.newLine(writer);
    }


    @Override
    public void printBeforeSingleProblemInstance(
        final SimplexStepProblem problem,
        final Matrix solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Transformieren Sie die folgende Matrix mithilfe von Gau\\ss{}-Jordan-Schritten so, dass die ");
        writer.write("Pivot-Spalte zu einem Einheitsvektor mit der 1 in der Pivot-Zeile wird:\\\\");
        Main.newLine(writer);
    }

    @Override
    public void printProblemInstance(
        final SimplexStepProblem problem,
        final Matrix solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write(LaTeXUtils.displayMath(problem.matrix().toLaTeX()));
        Main.newLine(writer);
        writer.write("Pivot-Spalte: ");
        writer.write(String.valueOf(problem.pivotColumn() + 1));
        writer.write("\\\\");
        Main.newLine(writer);
        writer.write("Pivot-Zeile: ");
        writer.write(String.valueOf(problem.pivotRow() + 1));
        writer.write("\\\\");
        Main.newLine(writer);
    }

    @Override
    public void printSolutionInstance(
        final SimplexStepProblem problem,
        final Matrix solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write(LaTeXUtils.displayMath(solution.toLaTeX()));
        Main.newLine(writer);
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
