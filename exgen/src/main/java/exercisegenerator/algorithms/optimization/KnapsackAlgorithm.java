package exercisegenerator.algorithms.optimization;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.optimization.*;

public class KnapsackAlgorithm implements AlgorithmImplementation<KnapsackProblem, int[][]> {

    public static final KnapsackAlgorithm INSTANCE = new KnapsackAlgorithm();

    static DPTracebackFunction traceback(final int[] weights) {
        return position -> {
            final List<DPDirection> result = new LinkedList<DPDirection>();
            if (position.row == 0) {
                for (int i = 0; i < position.column; i++) {
                    result.add(DPDirection.LEFT);
                }
                return result;
            }
            final int currentValue = position.solution[position.row][position.column];
            final int valueAbove = position.solution[position.row - 1][position.column];
            if (currentValue > valueAbove) {
                for (int i = 0; i < weights[position.row - 1]; i++) {
                    result.add(DPDirection.LEFT);
                }
            }
            result.add(DPDirection.UP);
            return result;
        };
    }

    private static KnapsackProblem generateKnapsackProblem(final Parameters options) {
        final int numberOfItems = KnapsackAlgorithm.parseOrGenerateNumberOfItems(options);
        final int[] weights = new int[numberOfItems];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = 1 + Main.RANDOM.nextInt(11);
        }
        final int[] values = new int[numberOfItems];
        for (int i = 0; i < values.length; i++) {
            values[i] = 1 + Main.RANDOM.nextInt(11);
        }
        final int capacity = 3 + Main.RANDOM.nextInt(6);
        return new KnapsackProblem(weights, values, capacity);
    }

    private static List<Integer> knapsackItems(final KnapsackProblem problem, final int[][] solution) {
        final List<Integer> result = new LinkedList<Integer>();
        int i = solution.length - 1;
        int j = solution[0].length - 1;
        while (i > 0) {
            final int valueAbove = i == 0 ? 0 : solution[i - 1][j];
            if (solution[i][j] > valueAbove) {
                result.add(i);
                j -= problem.weights[i - 1];
            }
            i--;
        }
        Collections.sort(result);
        return result;
    }

    private static KnapsackProblem parseKnapsackProblem(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        final String errorMessage =
            "You need to provide the same number of weights and values together with a capacity. The three parts need "
            + " to be separated by ';' while the numbers within the parts are separated by ','!";
        final String line = reader.readLine();
        final String[] parts = line.strip().split(";");
        if (parts.length < 3) {
            throw new IOException(errorMessage);
        }
        return new KnapsackProblem(
            KnapsackAlgorithm.toIntArray(parts[0]),
            KnapsackAlgorithm.toIntArray(parts[1]),
            Integer.parseInt(parts[2])
        );
    }

    private static int parseOrGenerateNumberOfItems(final Parameters options) {
        final int result = AlgorithmImplementation.parseOrGenerateLength(3, 6, options);
        return result > 0 ? result : Main.RANDOM.nextInt(4) + 3;
    }

    private static int[] toIntArray(final String line) {
        return Arrays.stream(line.split(",")).mapToInt(Integer::parseInt).toArray();
    }

    private KnapsackAlgorithm() {}

    @Override
    public int[][] apply(final KnapsackProblem problem) {
        final int[][] result = new int[problem.weights.length + 1][problem.capacity + 1];
        for (int item = 0; item < problem.weights.length; item++) {
            for (int remainingCapacity = 1; remainingCapacity <= problem.capacity; remainingCapacity++) {
                final int weight = problem.weights[item];
                final int valueOut = result[item][remainingCapacity];
                if (weight > remainingCapacity) {
                    result[item + 1][remainingCapacity] = valueOut;
                } else {
                    final int valueIn = result[item][remainingCapacity - weight] + problem.values[item];
                    result[item + 1][remainingCapacity] = Math.max(valueOut, valueIn);
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
    public KnapsackProblem parseOrGenerateProblem(final Parameters options) throws IOException {
        return new ParserAndGenerator<KnapsackProblem>(
            KnapsackAlgorithm::parseKnapsackProblem,
            KnapsackAlgorithm::generateKnapsackProblem
        ).getResult(options);
    }

    @Override
    public void printExercise(
        final KnapsackProblem problem,
        final int[][] solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        final LengthConfiguration configuration =
            OptimizationAlgorithms.parseOrGenerateLengthConfiguration(options, 2);
        final int numberOfItems = problem.weights.length;
        writer.write("Gegeben sei ein Rucksack mit ");
        writer.write(String.format("\\emphasize{maximaler Tragkraft} %d ", problem.capacity));
        writer.write(String.format("sowie %d \\emphasize{Gegenst\\\"ande}. ", numberOfItems));
        Main.newLine(writer);
        writer.write("Der $i$-te Gegenstand soll hierbei ein Gewicht von $w_i$ und einen Wert von $v_i$ haben. ");
        Main.newLine(writer);
        writer.write("Bestimmen Sie mit Hilfe des Algorithmus zum L\\\"osen ");
        writer.write("des Rucksackproblems mittels dynamischer Programmierung den maximalen ");
        writer.write("Gesamtwert der Gegenst\\\"ande, die der Rucksack tragen kann (das Gesamtgewicht der ");
        writer.write("mitgef\\\"uhrten Gegenst\\\"ande \\\"ubersteigt nicht die Tragkraft des Rucksacks). ");
        Main.newLine(writer);
        writer.write("Die \\emphasize{Gewichte} seien dabei $w_{1} = " + problem.weights[0] + "$");
        for (int i = 1; i < numberOfItems - 1; i++) {
            writer.write(", $w_{" + (i + 1) + "} = " + problem.weights[i] + "$");
        }
        writer.write(" und $w_{" + numberOfItems + "} = " + problem.weights[numberOfItems - 1]);
        writer.write("$. ");
        Main.newLine(writer);
        writer.write("Die \\emphasize{Werte} seien $v_{1} = " + problem.values[0] + "$");
        for (int i = 1; i < numberOfItems - 1; i++) {
            writer.write(", $v_{" + (i + 1) + "} = " + problem.values[i] + "$");
        }
        writer.write(" und $v_{" + numberOfItems + "} = " + problem.values[numberOfItems - 1] + "$. ");
        Main.newLine(writer);
        writer.write("Geben Sie zudem die vom Algorithmus bestimmte Tabelle ");
        writer.write("und die mitzunehmenden Gegenst\\\"ande an.");
        Main.newLine(writer);
        Main.newLine(writer);
        final PreprintMode mode = PreprintMode.parsePreprintMode(options);
        switch (mode) {
        case SOLUTION_SPACE:
            LaTeXUtils.printSolutionSpaceBeginning(Optional.empty(), options, writer);
            // fall-through
        case ALWAYS:
            OptimizationAlgorithms.printDPTable(
                solution,
                i -> i.toString(),
                i -> i.toString(),
                Optional.empty(),
                options,
                configuration,
                writer
            );
            writer.write("${}^*$ Gegenstand/Kapazit\\\"at");
            Main.newLine(writer);
            LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
            Main.newLine(writer);
            writer.write("Gegenst\\\"ande:\\\\[2ex]");
            Main.newLine(writer);
            writer.write("Wert:");
            Main.newLine(writer);
            if (mode == PreprintMode.SOLUTION_SPACE) {
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
        final KnapsackProblem problem,
        final int[][] solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        final LengthConfiguration configuration =
            OptimizationAlgorithms.parseOrGenerateLengthConfiguration(options, 2);
        writer.write("Die Tabelle wird vom Algorithmus wie folgt gef\\\"ullt:");
        Main.newLine(writer);
        Main.newLine(writer);
        OptimizationAlgorithms.printDPTable(
            solution,
            i -> i.toString(),
            i -> i.toString(),
            Optional.of(KnapsackAlgorithm.traceback(problem.weights)),
            options,
            configuration,
            writer
        );
        writer.write("${}^*$ Gegenstand/Kapazit\\\"at");
        Main.newLine(writer);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
        Main.newLine(writer);
        writer.write("Gegenst\\\"ande: $");
        writer.write(LaTeXUtils.mathematicalSet(KnapsackAlgorithm.knapsackItems(problem, solution)));
        writer.write("$\\\\[2ex]");
        Main.newLine(writer);
        writer.write("Wert: ");
        writer.write(String.valueOf(solution[solution.length - 1][solution[0].length - 1]));
        Main.newLine(writer);
        Main.newLine(writer);
    }

}
