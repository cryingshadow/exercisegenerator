package exercisegenerator.algorithms.optimization;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.optimization.*;

public class LCSAlgorithm implements AlgorithmImplementation {

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

    public static int[][] lcs(final LCSProblem problem) {
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

    private static LCSProblem generateLCSProblem(final Parameters options) {
        final int length1 = LCSAlgorithm.parseOrGenerateLengthOfWords(options);
        final int length2 = LCSAlgorithm.parseOrGenerateLengthOfWords(options);
        final String word1 = LCSAlgorithm.generateRandomString(length1);
        final String word2 = LCSAlgorithm.generateRandomString(length2);
        return new LCSProblem(word1, word2);
    }

    private static String generateRandomString(final int length) {
        return
            OptimizationAlgorithms
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

    private static LCSProblem parseLCSProblem(final BufferedReader reader, final Parameters options)
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

    private static LCSProblem parseOrGenerateLCSProblem(final Parameters options) throws IOException {
        return new ParserAndGenerator<LCSProblem>(
            LCSAlgorithm::parseLCSProblem,
            LCSAlgorithm::generateLCSProblem
        ).getResult(options);
    }

    private static int parseOrGenerateLengthOfWords(final Parameters options) {
        if (options.containsKey(Flag.LENGTH)) {
            final int result = Integer.parseInt(options.get(Flag.LENGTH));
            if (result > 0) {
                return result;
            }
        }
        return OptimizationAlgorithms.RANDOM.nextInt(8) + 3;
    }

    private static void printLCSExercise(
        final LCSProblem problem,
        final int[][] table,
        final Parameters options,
        final LengthConfiguration configuration,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Bestimmen Sie die \\emphasize{l\\\"angste gemeinsame Teilfolge} der Folgen \\code{");
        writer.write(problem.word1);
        writer.write("} und \\code{");
        writer.write(problem.word2);
        writer.write("}. Benutzen Sie hierf\\\"ur den in der Vorlesung vorgestellten Algorithmus mit dynamischer ");
        writer.write("Programmierung und f\\\"ullen Sie die folgende Tabelle aus. Geben Sie au\\ss{}erdem die vom ");
        writer.write("Algorithmus bestimmte l\\\"angste gemeinsame Teilfolge an.");
        Main.newLine(writer);
        Main.newLine(writer);
        final PreprintMode mode = PreprintMode.parsePreprintMode(options);
        switch (mode) {
        case SOLUTION_SPACE:
            LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, writer);
            // fall-through
        case ALWAYS:
            OptimizationAlgorithms.printDPTable(
                table,
                i -> problem.rowHeading(i),
                i -> problem.columnHeading(i),
                Optional.empty(),
                options,
                configuration,
                writer
            );
            writer.write("${}^*$ Folge 1/Folge 2");
            Main.newLine(writer);
            LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
            Main.newLine(writer);
            writer.write("L\\\"angste gemeinsame Teilfolge:\\\\[2ex]");
            Main.newLine(writer);
            if (mode == PreprintMode.SOLUTION_SPACE) {
                LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), options, writer);
            }
            Main.newLine(writer);
            break;
        default:
            //do nothing
        }
    }

    private static void printLCSSolution(
        final LCSProblem problem,
        final int[][] table,
        final Parameters options,
        final LengthConfiguration configuration,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Die Tabelle wird vom Algorithmus wie folgt gef\\\"ullt:");
        Main.newLine(writer);
        Main.newLine(writer);
        OptimizationAlgorithms.printDPTable(
            table,
            i -> problem.rowHeading(i),
            i -> problem.columnHeading(i),
            Optional.of(LCSAlgorithm.TRACEBACK),
            options,
            configuration,
            writer
        );
        writer.write("${}^*$ Folge 1/Folge 2");
        Main.newLine(writer);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
        Main.newLine(writer);
        writer.write("L\\\"angste gemeinsame Teilfolge: ");
        writer.write(LCSAlgorithm.lcs(problem, table));
        writer.write("\\\\[2ex]");
        Main.newLine(writer);
        Main.newLine(writer);
    }

    private LCSAlgorithm() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final LCSProblem problem = LCSAlgorithm.parseOrGenerateLCSProblem(input.options);
        final LengthConfiguration configuration =
            OptimizationAlgorithms.parseOrGenerateLengthConfiguration(input.options, 3);
        final int[][] table = LCSAlgorithm.lcs(problem);
        LCSAlgorithm.printLCSExercise(problem, table, input.options, configuration, input.exerciseWriter);
        LCSAlgorithm.printLCSSolution(problem, table, input.options, configuration, input.solutionWriter);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

}
