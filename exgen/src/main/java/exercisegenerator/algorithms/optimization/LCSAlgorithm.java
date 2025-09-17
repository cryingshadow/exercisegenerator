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
    public String commandPrefix() {
        return "Lcs";
    }

    @Override
    public LCSProblem generateProblem(final Parameters<Flag> options) {
        final int length1 = LCSAlgorithm.parseOrGenerateLengthOfWords(options);
        final int length2 = LCSAlgorithm.parseOrGenerateLengthOfWords(options);
        final String word1 = LCSAlgorithm.generateRandomString(length1);
        final String word2 = LCSAlgorithm.generateRandomString(length2);
        return new LCSProblem(word1, word2);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    @Override
    public List<LCSProblem> parseProblems(final BufferedReader reader, final Parameters<Flag> options)
    throws IOException {
        final String wordA = reader.readLine();
        final String wordB = reader.readLine();
        if (wordA.isBlank() || wordB.isBlank()) {
            throw new IllegalArgumentException(
                "You need to provide two lines each carrying exactly one non-empty word."
            );
        }
        return List.of(new LCSProblem(wordA, wordB));
    }

    @Override
    public void printAfterSingleProblemInstance(
        final LCSProblem problem,
        final int[][] solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write(". Benutzen Sie hierf\\\"ur den in der Vorlesung vorgestellten Algorithmus mit dynamischer ");
        writer.write("Programmierung und f\\\"ullen Sie die folgende Tabelle aus. Geben Sie au\\ss{}erdem die vom ");
        writer.write("Algorithmus bestimmte l\\\"angste gemeinsame Teilfolge an.");
        Main.newLine(writer);
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<LCSProblem> problems,
        final List<int[][]> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Bestimmen Sie die \\emphasize{l\\\"angste gemeinsame Teilfolge} der folgenden Paare von ");
        writer.write("Zeichenfolgen. Benutzen Sie hierf\\\"ur den in der Vorlesung vorgestellten Algorithmus mit ");
        writer.write("dynamischer Programmierung und f\\\"ullen Sie die jeweilige Tabelle aus. Geben Sie ");
        writer.write("au\\ss{}erdem jeweils die vom Algorithmus bestimmte l\\\"angste gemeinsame Teilfolge an.");
        Main.newLine(writer);
    }

    @Override
    public void printBeforeSingleProblemInstance(
        final LCSProblem problem,
        final int[][] solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Bestimmen Sie die \\emphasize{l\\\"angste gemeinsame Teilfolge} der Folgen ");
    }

    @Override
    public void printProblemInstance(
        final LCSProblem problem,
        final int[][] solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("\\code{");
        writer.write(problem.word1);
        writer.write("} und \\code{");
        writer.write(problem.word2);
        writer.write("}");
    }

    @Override
    public void printSolutionInstance(
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
    }

    @Override
    public void printSolutionSpace(
        final LCSProblem problem,
        final int[][] solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException{
        final LengthConfiguration configuration =
            OptimizationAlgorithms.parseOrGenerateLengthConfiguration(options, 3);
        final SolutionSpaceMode mode = SolutionSpaceMode.parsePreprintMode(options);
        switch (mode) {
        case SOLUTION_SPACE:
            Main.newLine(writer);
            LaTeXUtils.printSolutionSpaceBeginning(Optional.empty(), options, writer);
            // fall-through
        case ALWAYS:
            if (mode != SolutionSpaceMode.SOLUTION_SPACE) {
                Main.newLine(writer);
            }
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
            break;
        default:
            //do nothing
        }
    }

}
