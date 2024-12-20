package exercisegenerator.algorithms.algebra;

import java.io.*;
import java.util.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.algebra.*;

public class MatrixArithmeticAlgorithm implements AlgorithmImplementation<MatrixTerm, List<MatrixTerm>> {

    public static final MatrixArithmeticAlgorithm INSTANCE = new MatrixArithmeticAlgorithm();

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

    private static MatrixTerm generateMatrixTerm(final Parameters<Flag> options) {
        final int numberOfMatrices = MatrixArithmeticAlgorithm.parseOrGenerateNumberOfMatrices(options);
        final int dimensionOfMatrices = AlgebraAlgorithms.parseOrGenerateDimensionOfMatrices(options);
        final List<MatrixTerm> terms = new ArrayList<MatrixTerm>();
        for (int i = 0; i < numberOfMatrices; i++) {
            terms.add(AlgebraAlgorithms.generateQuadraticMatrix(dimensionOfMatrices));
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
        final String operator,
        final Object next,
        final List<Object> toParse
    ) {
        if (!"*".equals(operator) && !"+".equals(operator)) {
            throw new IllegalArgumentException("Only + and * are allowed as operators!");
        }
        if ("(".equals(next)) {
            final List<Object> parsedFurther =
                MatrixArithmeticAlgorithm.parseParanthesis(toParse.subList(1, toParse.size()));
            final MatrixTerm paranthesisTerm = (MatrixTerm)parsedFurther.get(0);
            if ("*".equals(operator)) {
                final List<Object> nextList = new ArrayList<Object>();
                nextList.add(new MatrixMultiplication(left, paranthesisTerm));
                nextList.addAll(parsedFurther.subList(1, parsedFurther.size()));
                return MatrixArithmeticAlgorithm.parseMatrixTerm(nextList);
            }
            if (parsedFurther.size() > 1) {
                if ("*".equals(parsedFurther.get(1))) {
                    final MatrixTerm right =
                        MatrixArithmeticAlgorithm.parseFirstOperation(
                            paranthesisTerm,
                            "*",
                            parsedFurther.get(2),
                            parsedFurther.subList(2, parsedFurther.size())
                        );
                    return new MatrixAddition(left, right);
                }
                if (!"+".equals(parsedFurther.get(1))) {
                    throw new IllegalArgumentException("Only + and * are allowed as operators!");
                }
                final List<Object> nextList = new ArrayList<Object>();
                nextList.add(new MatrixAddition(left, paranthesisTerm));
                nextList.addAll(parsedFurther.subList(1, parsedFurther.size()));
                return MatrixArithmeticAlgorithm.parseMatrixTerm(nextList);
            }
            return new MatrixAddition(left, paranthesisTerm);
        }
        if (next instanceof MatrixTerm) {
            if ("*".equals(operator)) {
                final List<Object> nextList = new ArrayList<Object>();
                nextList.add(new MatrixMultiplication(left, (MatrixTerm)next));
                nextList.addAll(toParse.subList(1, toParse.size()));
                return MatrixArithmeticAlgorithm.parseMatrixTerm(nextList);
            }
            if (toParse.size() > 1) {
                if ("*".equals(toParse.get(1))) {
                    final MatrixTerm right =
                        MatrixArithmeticAlgorithm.parseFirstOperation(
                            (MatrixTerm)next,
                            "*",
                            toParse.get(2),
                            toParse.subList(2, toParse.size())
                        );
                    return new MatrixAddition(left, right);
                }
                if (!"+".equals(toParse.get(1))) {
                    throw new IllegalArgumentException("Only + and * are allowed as operators!");
                }
                final List<Object> nextList = new ArrayList<Object>();
                nextList.add(new MatrixAddition(left, (MatrixTerm)next));
                nextList.addAll(toParse.subList(1, toParse.size()));
                return MatrixArithmeticAlgorithm.parseMatrixTerm(nextList);
            }
            return new MatrixAddition(left, (MatrixTerm)next);
        }
        if (!MatrixArithmeticAlgorithm.startsMatrix((String)next)) {
            throw new IllegalArgumentException("Parse error - matrix expected!");
        }
        final List<Object> parsedFurther = MatrixArithmeticAlgorithm.parseMatrix(toParse);
        if ("*".equals(operator)) {
            final List<Object> nextList = new ArrayList<Object>();
            nextList.add(new MatrixMultiplication(left, (MatrixTerm)parsedFurther.get(0)));
            nextList.addAll(parsedFurther.subList(1, parsedFurther.size()));
            return MatrixArithmeticAlgorithm.parseMatrixTerm(nextList);
        }
        if (parsedFurther.size() > 1) {
            if ("*".equals(parsedFurther.get(1))) {
                final MatrixTerm right =
                    MatrixArithmeticAlgorithm.parseFirstOperation(
                        (MatrixTerm)parsedFurther.get(0),
                        "*",
                        parsedFurther.get(2),
                        parsedFurther.subList(2, parsedFurther.size())
                    );
                return new MatrixAddition(left, right);
            }
            if (!"+".equals(parsedFurther.get(1))) {
                throw new IllegalArgumentException("Only + and * are allowed as operators!");
            }
            final List<Object> nextList = new ArrayList<Object>();
            nextList.add(new MatrixAddition(left, (MatrixTerm)parsedFurther.get(0)));
            nextList.addAll(parsedFurther.subList(1, parsedFurther.size()));
            return MatrixArithmeticAlgorithm.parseMatrixTerm(nextList);
        }
        return new MatrixAddition(left, (MatrixTerm)parsedFurther.get(0));
    }

    private static List<Object> parseMatrix(final List<Object> toParse) {
        int numberOfRows = 1;
        while (
            numberOfRows < toParse.size()
            && toParse.get(numberOfRows) instanceof String
            && !((String)toParse.get(numberOfRows)).matches("\\+|\\*|\\(")
        ) {
            numberOfRows++;
        }
        final Matrix matrix =
            AlgebraAlgorithms.parseMatrix(toParse.stream().limit(numberOfRows).map(o -> (String)o).toList());
        final List<Object> result = new ArrayList<Object>();
        result.add(matrix);
        result.addAll(toParse.subList(numberOfRows, toParse.size()));
        return result;
    }

    private static MatrixTerm parseMatrixTerm(
        final BufferedReader reader,
        final Parameters<Flag> options
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

    private static int parseOrGenerateNumberOfMatrices(final Parameters<Flag> options) {
        final int result = AlgorithmImplementation.parseOrGenerateLength(2, 4, options);
        return result > 1 ? result : 2;
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

    private static boolean startsMatrix(final String line) {
        final String[] split = line.split(" ");
        return split.length > 1 && split[0].matches("-?\\d.*");
    }

    private MatrixArithmeticAlgorithm() {}

    @Override
    public List<MatrixTerm> apply(final MatrixTerm term) {
        final List<MatrixTerm> solution = new LinkedList<MatrixTerm>();
        solution.add(term);
        MatrixTerm currentTerm = term;
        while (currentTerm.isCompound()) {
            currentTerm = MatrixArithmeticAlgorithm.evaluateOneStep(currentTerm);
            solution.add(currentTerm);
        }
        return solution;
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[4];
        result[0] = "-l";
        result[1] = "2";
        result[2] = "-d";
        result[3] = "3";
        return result;
    }

    @Override
    public MatrixTerm parseOrGenerateProblem(final Parameters<Flag> options)
    throws IOException {
        return new ParserAndGenerator<MatrixTerm>(
            MatrixArithmeticAlgorithm::parseMatrixTerm,
            MatrixArithmeticAlgorithm::generateMatrixTerm
        ).getResult(options);
    }

    @Override
    public void printExercise(
        final MatrixTerm problem,
        final List<MatrixTerm> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Berechnen Sie das Ergebnis der folgenden Matrix-Operationen:\\\\[2ex]");
        Main.newLine(writer);
        writer.write("$");
        writer.write(problem.toLaTeX());
        writer.write("$");
        Main.newLine(writer);
        Main.newLine(writer);
    }

    @Override
    public void printSolution(
        final MatrixTerm problem,
        final List<MatrixTerm> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("\\begin{align*}");
        Main.newLine(writer);
        boolean first = true;
        for (final MatrixTerm term : solution) {
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
        writer.write("\\end{align*}");
        Main.newLine(writer);
        Main.newLine(writer);
    }

}
