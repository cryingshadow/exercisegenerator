package exercisegenerator.algorithms.optimization;

import java.io.*;
import java.util.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.optimization.*;

public class LCSAlgorithm implements AlgorithmImplementation<LCSProblem, int[][]> {

    public static final LCSAlgorithm INSTANCE = new LCSAlgorithm();

    static final DPTracebackFunction TRACEBACK = position -> {
        if (position.row == 0 && position.column == 0) {
            return Collections.emptyList();
        }
        final int currentValue = position.solution[position.row][position.column];
        if (position.row > 0 && position.solution[position.row - 1][position.column] == currentValue) {
            return List.of(DPDirection.UP);
        }
        if (position.column > 0 && position.solution[position.row][position.column - 1] == currentValue) {
            return List.of(DPDirection.LEFT);
        }
        return List.of(DPDirection.UPLEFT);
    };

    private static LCSProblem generateLCSProblem(final Parameters<Flag> options) {
        final int length1 = LCSAlgorithm.parseOrGenerateLengthOfWords(options);
        final int length2 = LCSAlgorithm.parseOrGenerateLengthOfWords(options);
        final String word1 = LCSAlgorithm.generateRandomString(length1);
        final String word2 = LCSAlgorithm.generateRandomString(length2);
        return new LCSProblem(word1, word2);
    }

    private static String generateRandomString(final int length) {
        return
            Main
            .RANDOM
            .ints(65, 91)
            .limit(length)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    }

    private static String lcs(final LCSProblem problem, final int[][] table) {
        final StringBuilder result = new StringBuilder();
        int row = table.length - 1;
        int column = table[0].length - 1;
        List<DPDirection> directions = LCSAlgorithm.TRACEBACK.apply(new DPPosition(table, row, column));
        while (!directions.isEmpty()) {
            switch (directions.get(0)) {
            case LEFT:
                column--;
                break;
            case UP:
                row--;
                break;
            case UPLEFT:
                result.insert(0, problem.word1.charAt(row - 1));
                row--;
                column--;
                break;
            default:
                throw new IllegalStateException("LCS traceback resulted in undefined state!");
            }
            directions = LCSAlgorithm.TRACEBACK.apply(new DPPosition(table, row, column));
        }
        return result.toString();
    }

    private static LCSProblem parseLCSProblem(final BufferedReader reader, final Parameters<Flag> options)
    throws IOException {
        String wordA = null;
        String wordB = null;
        final String errorMessage = "You need to provide two lines each carrying exactly one non-empty word.";
        String line = null;
        int rowNum = 0;
        while ((line = reader.readLine()) != null) {
            if (rowNum == 0) {
                wordA = new String(line);
                if (wordA.length() == 0) {
                    System.out.println(errorMessage);
                    return null;
                }
            } else if (rowNum == 1) {
                wordB = new String(line);
                if (wordB.length() == 0) {
                    System.out.println(errorMessage);
                    return null;
                }
                return new LCSProblem(wordA, wordB);
            }
            rowNum++;
        }
        System.out.println(errorMessage);
        return null;
    }

    private static int parseOrGenerateLengthOfWords(final Parameters<Flag> options) {
        final int result = AlgorithmImplementation.parseOrGenerateLength(3, 10, options);
        return result > 0 ? result : Main.RANDOM.nextInt(8) + 3;
    }

    private LCSAlgorithm() {}

    @Override
    public int[][] apply(final LCSProblem problem) {
        final int rows = problem.word1.length();
        final int columns = problem.word2.length();
        final int[][] result = new int[rows + 1][columns + 1];
        result[0][0] = 0;
        for (int column = 1; column <= columns; column++) {
            result[0][column] = 0;
        }
        for (int row = 1; row <= rows; row++) {
            result[row][0] = 0;
        }
        for (int row = 1; row <= rows; row++) {
            for (int column = 1; column <= columns; column++) {
                final int valueAbove = result[row - 1][column];
                final int valueLeft = result[row][column - 1];
                final int max = Math.max(valueLeft, valueAbove);
                if (problem.word1.charAt(row - 1) == problem.word2.charAt(column - 1)) {
                    result[row][column] = Math.max(max, result[row - 1][column - 1] + 1);
                } else {
                    result[row][column] = max;
                }
            }
        }
        return result;
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    @Override
    public LCSProblem parseOrGenerateProblem(final Parameters<Flag> options) throws IOException {
        return new ParserAndGenerator<LCSProblem>(
            LCSAlgorithm::parseLCSProblem,
            LCSAlgorithm::generateLCSProblem
        ).getResult(options);
    }

    @Override
    public void printExercise(
        final LCSProblem problem,
        final int[][] solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        final LengthConfiguration configuration =
            OptimizationAlgorithms.parseOrGenerateLengthConfiguration(options, 3);
        writer.write("Bestimmen Sie die \\emphasize{l\\\"angste gemeinsame Teilfolge} der Folgen \\code{");
        writer.write(problem.word1);
        writer.write("} und \\code{");
        writer.write(problem.word2);
        writer.write("}. Benutzen Sie hierf\\\"ur den in der Vorlesung vorgestellten Algorithmus mit dynamischer ");
        writer.write("Programmierung und f\\\"ullen Sie die folgende Tabelle aus. Geben Sie au\\ss{}erdem die vom ");
        writer.write("Algorithmus bestimmte l\\\"angste gemeinsame Teilfolge an.");
        Main.newLine(writer);
        Main.newLine(writer);
        final SolutionSpaceMode mode = SolutionSpaceMode.parsePreprintMode(options);
        switch (mode) {
        case SOLUTION_SPACE:
            LaTeXUtils.printSolutionSpaceBeginning(Optional.empty(), options, writer);
            // fall-through
        case ALWAYS:
            OptimizationAlgorithms.printDPTable(
                solution,
                new DPHeading(i -> problem.rowHeading(i)),
                new DPHeading(i -> problem.columnHeading(i)),
                Optional.empty(),
                options,
                configuration,
                writer
            );
            writer.write("${}^*$ Folge 1/Folge 2\\\\[2ex]");
            Main.newLine(writer);
            writer.write("L\\\"angste gemeinsame Teilfolge:");
            Main.newLine(writer);
            if (mode == SolutionSpaceMode.SOLUTION_SPACE) {
                LaTeXUtils.printSolutionSpaceEnd(Optional.of("3ex"), options, writer);
            }
            Main.newLine(writer);
            break;
        default:
            //do nothing
        }
    }

    @Override
    public void printSolution(
        final LCSProblem problem,
        final int[][] solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        final LengthConfiguration configuration =
            OptimizationAlgorithms.parseOrGenerateLengthConfiguration(options, 3);
        writer.write("Die Tabelle wird vom Algorithmus wie folgt gef\\\"ullt:");
        Main.newLine(writer);
        Main.newLine(writer);
        OptimizationAlgorithms.printDPTable(
            solution,
            new DPHeading(i -> problem.rowHeading(i)),
            new DPHeading(i -> problem.columnHeading(i)),
            Optional.of(LCSAlgorithm.TRACEBACK),
            options,
            configuration,
            writer
        );
        writer.write("${}^*$ Folge 1/Folge 2\\\\[2ex]");
        Main.newLine(writer);
        writer.write("L\\\"angste gemeinsame Teilfolge: ");
        writer.write(LCSAlgorithm.lcs(problem, solution));
        Main.newLine(writer);
        Main.newLine(writer);
    }

}
