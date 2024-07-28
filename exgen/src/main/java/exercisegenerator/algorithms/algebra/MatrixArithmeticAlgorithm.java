package exercisegenerator.algorithms.algebra;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.algebra.*;

public class MatrixArithmeticAlgorithm implements AlgorithmImplementation {

    public static final MatrixArithmeticAlgorithm INSTANCE = new MatrixArithmeticAlgorithm();

    public static List<MatrixTerm> applyMatrixArithmetic(final MatrixTerm term) {
        final List<MatrixTerm> solution = new LinkedList<MatrixTerm>();
        solution.add(term);
        MatrixTerm currentTerm = term;
        while (currentTerm.isCompound()) {
            currentTerm = MatrixArithmeticAlgorithm.evaluateOneStep(currentTerm);
            solution.add(currentTerm);
        }
        return solution;
    }

    private static MatrixTerm evaluateOneStep(final MatrixTerm term) {
        if (term.isDirectlyEvaluable()) {
            return term.toMatrix();
        }
        if (term instanceof Matrix) {
            return null;
        }
        if (term instanceof MatrixMultiplication) {
            final MatrixMultiplication multiplication = (MatrixMultiplication)term;
            final MatrixTerm left = MatrixArithmeticAlgorithm.evaluateOneStep(multiplication.left());
            if (left != null) {
                return new MatrixMultiplication(left, multiplication.right());
            }
            final MatrixTerm right = MatrixArithmeticAlgorithm.evaluateOneStep(multiplication.right());
            if (right != null) {
                return new MatrixMultiplication(multiplication.left(), right);
            }
        }
        if (term instanceof MatrixAddition) {
            final MatrixAddition addition = (MatrixAddition)term;
            final MatrixTerm left = MatrixArithmeticAlgorithm.evaluateOneStep(addition.left());
            if (left != null) {
                return new MatrixAddition(left, addition.right());
            }
            final MatrixTerm right = MatrixArithmeticAlgorithm.evaluateOneStep(addition.right());
            if (right != null) {
                return new MatrixAddition(addition.left(), right);
            }
        }
        return null;
    }

    private static Matrix generateMatrix(final int dimension) {
        final Matrix result = new Matrix(dimension, dimension, dimension);
        for (int row = 0; row < dimension; row++) {
            for (int column = 0; column < dimension; column++) {
                result.setCoefficient(column, row, AlgebraAlgorithms.generateCoefficient(21, 2));
            }
        }
        return result;
    }

    private static MatrixTerm generateMatrixTerm(final Parameters options) {
        final int numberOfMatrices = MatrixArithmeticAlgorithm.parseOrGenerateNumberOfMatrices(options);
        final int dimensionOfMatrices = MatrixArithmeticAlgorithm.parseOrGenerateDimensionOfMatrices(options);
        final List<MatrixTerm> terms = new ArrayList<MatrixTerm>();
        for (int i = 0; i < numberOfMatrices; i++) {
            terms.add(MatrixArithmeticAlgorithm.generateMatrix(dimensionOfMatrices));
        }
        while (terms.size() > 1) {
            final MatrixTerm left = terms.remove(Main.RANDOM.nextInt(terms.size()));
            final MatrixTerm right = terms.remove(Main.RANDOM.nextInt(terms.size()));
            terms.add(
                Main.RANDOM.nextInt(4) == 0 ? new MatrixAddition(left, right) : new MatrixMultiplication(left, right)
            );
        }
        return terms.get(0);
    }

    private static MatrixTerm parseFirstOperation(
        final MatrixTerm left,
        final String first,
        final Object next,
        final List<Object> toParse
    ) {
        if ("(".equals(next)) {
            final List<Object> parsedFurther =
                MatrixArithmeticAlgorithm.parseParanthesis(toParse.subList(1, toParse.size()));
            final MatrixTerm right = (MatrixTerm)parsedFurther.get(0);
            final List<Object> nextList = new ArrayList<Object>();
            nextList.add(
                "*".equals(first) ? new MatrixMultiplication(left, right) : new MatrixAddition(left, right)
            );
            nextList.addAll(parsedFurther.subList(1, parsedFurther.size()));
            return MatrixArithmeticAlgorithm.parseMatrixTerm(nextList);
        }
        if (next instanceof MatrixTerm) {
            final List<Object> nextList = new ArrayList<Object>();
            final MatrixTerm right = (MatrixTerm)next;
            nextList.add(
                "*".equals(first) ? new MatrixMultiplication(left, right) : new MatrixAddition(left, right)
            );
            nextList.addAll(toParse.subList(1, toParse.size()));
            return MatrixArithmeticAlgorithm.parseMatrixTerm(nextList);
        }
        if (!MatrixArithmeticAlgorithm.startsMatrix((String)next)) {
            throw new IllegalArgumentException("Parse error - matrix expected!");
        }
        final List<Object> parsedFurther = MatrixArithmeticAlgorithm.parseMatrix(toParse);
        final List<Object> nextList = new ArrayList<Object>();
        final MatrixTerm right = (MatrixTerm)parsedFurther.get(0);
        nextList.add(
            "*".equals(first) ? new MatrixMultiplication(left, right) : new MatrixAddition(left, right)
        );
        nextList.addAll(parsedFurther.subList(1, parsedFurther.size()));
        return MatrixArithmeticAlgorithm.parseMatrixTerm(nextList);
    }

    private static List<Object> parseMatrix(final List<Object> toParse) {
        int numberOfRows = 1;
        while (
            toParse.get(numberOfRows) instanceof String
            && !((String)toParse.get(numberOfRows)).matches("\\+|\\*|\\(")
        ) {
            numberOfRows++;
        }
        final int numberOfColumns = ((String)toParse.get(0)).split(" ").length;
        final Matrix matrix = new Matrix(numberOfColumns, numberOfRows, numberOfColumns);
        for (int row = 0; row < numberOfRows; row++) {
            final String[] columns = ((String)toParse.get(row)).split(" ");
            for (int column = 0; column < numberOfColumns; column++) {
                matrix.setCoefficient(column, row, AlgebraAlgorithms.parseRationalNumber(columns[column]));
            }
        }
        final List<Object> result = new ArrayList<Object>();
        result.add(matrix);
        result.addAll(toParse.subList(numberOfRows, toParse.size()));
        return result;
    }

    private static MatrixTerm parseMatrixTerm(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        final List<Object> text = new ArrayList<Object>();
        String line = reader.readLine();
        while (line != null && !line.isBlank()) {
            text.add(line);
            line = reader.readLine();
        }
        return MatrixArithmeticAlgorithm.parseMatrixTerm(text);
    }

    private static MatrixTerm parseMatrixTerm(final List<Object> toParse) {
        if (toParse.size() == 1) {
            return (MatrixTerm)toParse.get(0);
        }
        int index = 0;
        while (toParse.get(index) instanceof MatrixTerm) {
            index++;
        }
        final String first = (String)toParse.get(index);
        if ("(".equals(first)) {
            if (index != 0) {
                throw new IllegalArgumentException("Found compound expression without operator!");
            }
            return MatrixArithmeticAlgorithm.parseMatrixTerm(
                MatrixArithmeticAlgorithm.parseParanthesis(toParse.subList(1, toParse.size()))
            );
        }
        if ("*".equals(first) || "+".equals(first)) {
            if (index != 1) {
                throw new IllegalArgumentException("Found operator without proper left argument!");
            }
            return MatrixArithmeticAlgorithm.parseFirstOperation(
                (MatrixTerm)toParse.get(0),
                first,
                toParse.get(2),
                toParse.subList(2, toParse.size())
            );
        }
        if (!MatrixArithmeticAlgorithm.startsMatrix(first)) {
            throw new IllegalArgumentException("Parse error - matrix expected!");
        }
        if (index != 0) {
            throw new IllegalArgumentException("Found compound expression without operator!");
        }
        return MatrixArithmeticAlgorithm.parseMatrixTerm(MatrixArithmeticAlgorithm.parseMatrix(toParse));
    }

    private static int parseOrGenerateDimensionOfMatrices(final Parameters options) {
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

    private static MatrixTerm parseOrGenerateMatrixTerm(final Parameters options)
    throws IOException {
        return new ParserAndGenerator<MatrixTerm>(
            MatrixArithmeticAlgorithm::parseMatrixTerm,
            MatrixArithmeticAlgorithm::generateMatrixTerm
        ).getResult(options);
    }

    private static int parseOrGenerateNumberOfMatrices(final Parameters options) {
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

    private static List<Object> parseParanthesis(final List<Object> toParse) {
        int parantheses = 1;
        int index = 1;
        while (parantheses > 0) {
            if (toParse.get(index) instanceof String) {
                final String current = (String)toParse.get(index);
                if ("(".equals(current)) {
                    parantheses++;
                } else if (")".equals(current)) {
                    parantheses--;
                }
            }
            index++;
        }
        final MatrixTerm term = MatrixArithmeticAlgorithm.parseMatrixTerm(toParse.subList(0, index - 1));
        if (toParse.size() == index) {
            return List.of(term);
        }
        if ("*".equals(toParse.get(index)) || "+".equals(toParse.get(index))) {
            final List<Object> result = new ArrayList<Object>();
            result.add(term);
            result.addAll(toParse.subList(index, toParse.size()));
            return result;
        }
        throw new IllegalArgumentException("Only addition and multiplication operations are allowed!");
    }

    private static void printMatrixArithmeticExercise(
        final MatrixTerm problem,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Berechnen Sie das Ergebnis der folgenden Matrix-Operationen:\\\\[2ex]");
        Main.newLine(writer);
        writer.write(problem.toLaTeX());
        Main.newLine(writer);
        Main.newLine(writer);
    }

    private static void printMatrixArithmeticSolution(
        final List<MatrixTerm> solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("{\\renewcommand{\\arraystretch}{1.2}");
        Main.newLine(writer);
        Main.newLine(writer);
        writer.write("$\\begin{align}");
        Main.newLine(writer);
        boolean first = true;
        for (final MatrixTerm term : solution) {
            writer.write("&");
            if (first) {
                first = false;
            } else {
                writer.write("=");
            }
            writer.write("&");
            writer.write(term.toLaTeX());
            writer.write("\\\\");
            Main.newLine(writer);
        }
        writer.write("\\end{align}$");
        Main.newLine(writer);
        Main.newLine(writer);
        writer.write("\\renewcommand{\\arraystretch}{1}}");
        Main.newLine(writer);
        Main.newLine(writer);
    }

    private static boolean startsMatrix(final String line) {
        final String[] split = line.split(" ");
        return split.length > 1 && split[0].matches("-?\\d.*");
    }

    private MatrixArithmeticAlgorithm() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final MatrixTerm problem =
            MatrixArithmeticAlgorithm.parseOrGenerateMatrixTerm(input.options);
        final List<MatrixTerm> solution = MatrixArithmeticAlgorithm.applyMatrixArithmetic(problem);
        MatrixArithmeticAlgorithm.printMatrixArithmeticExercise(problem, input.options, input.exerciseWriter);
        MatrixArithmeticAlgorithm.printMatrixArithmeticSolution(solution, input.options, input.solutionWriter);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "2";
        result[2] = "-d";
        result[3] = "3";
        return result;
    }

}
