package exercisegenerator.algorithms.optimization;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.optimization.*;

public class KnapsackAlgorithm implements AlgorithmImplementation {

    public static final KnapsackAlgorithm INSTANCE = new KnapsackAlgorithm();

    public static int[][] knapsack(final KnapsackProblem problem) {
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

    private static void fillKnapsackSolutionTable(
        final String[][] tableWithArrows,
        final int[][] solution,
        final int[] weights
    ) {
        for (int i = 0; i < solution.length; i++) {
            for (int j = 0; j < solution[i].length; j++) {
                tableWithArrows[i + 1][2 * j + 1] = String.valueOf(solution[i][j]);
            }
        }
        int i = solution.length - 1;
        int j = solution[0].length - 1;
        while (i > 0) {
            final int valueAbove = i == 0 ? 0 : solution[i - 1][j];
            if (solution[i][j] > valueAbove) {
                for (int k = 0; k < weights[i - 1]; k++) {
                    tableWithArrows[i + 1][2 * j + 2] = "$\\leftarrow$";
                    j--;
                }
            }
            tableWithArrows[i + 1][2 * j + 2] = "$\\uparrow$";
            i--;
        }
        while (j > 0) {
            tableWithArrows[0][2 * j + 2] = "$\\leftarrow$";
            j--;
        }
    }

    private static KnapsackProblem generateKnapsackProblem(final Parameters options) {
        final int numberOfItems = KnapsackAlgorithm.parseOrGenerateNumberOfItems(options);
//        int sumOfWeights = 0;
        final int[] weights = new int[numberOfItems];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = 1 + OptimizationAlgorithms.RANDOM.nextInt(11);
//            sumOfWeights += weights[i];
        }
        final int[] values = new int[numberOfItems];
        for (int i = 0; i < values.length; i++) {
            values[i] = 1 + OptimizationAlgorithms.RANDOM.nextInt(11);
        }
//        final int p = 35 + OptimizationAlgorithms.RANDOM.nextInt(30);
//        final int capacity = (sumOfWeights * p) / 100;
        final int capacity = 3 + OptimizationAlgorithms.RANDOM.nextInt(6);
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

    private static String knapsackTableColumnDefinition(final int cols) {
        return String.format("|C{5mm}|*{%d}{C{5mm}C{7mm}|}", cols / 2);
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
        if (parts.length != 3) {
            throw new IOException(errorMessage);
        }
        return new KnapsackProblem(
            KnapsackAlgorithm.toIntArray(parts[0]),
            KnapsackAlgorithm.toIntArray(parts[1]),
            Integer.parseInt(parts[2])
        );
    }

    private static KnapsackProblem parseOrGenerateKnapsackProblem(final Parameters options)
    throws IOException {
        return new ParserAndGenerator<KnapsackProblem>(
            KnapsackAlgorithm::parseKnapsackProblem,
            KnapsackAlgorithm::generateKnapsackProblem
        ).getResult(options);
    }

    private static int parseOrGenerateNumberOfItems(final Parameters options) {
        if (options.containsKey(Flag.LENGTH)) {
            final int result = Integer.parseInt(options.get(Flag.LENGTH));
            if (result > 0) {
                return result;
            }
        }
        return OptimizationAlgorithms.RANDOM.nextInt(4) + 3;
    }

    private static void printKnapsackExercise(
        final KnapsackProblem problem,
        final int[][] solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        final int numberOfItems = problem.weights.length;
        writer.write(
            String.format(
                "Gegeben sei ein Rucksack mit \\emphasize{maximaler Tragkraft} %d sowie %d \\emphasize{Gegenst\\\"ande}. ",
                problem.capacity,
                numberOfItems
            )
        );
        Main.newLine(writer);
        writer.write("Der $i$-te Gegenstand soll hierbei ein Gewicht von $w_i$ und einen Wert von $c_i$ haben. ");
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
        writer.write("Die \\emphasize{Werte} seien $c_{1} = " + problem.values[0] + "$");
        for (int i = 1; i < numberOfItems - 1; i++) {
            writer.write(", $c_{" + (i + 1) + "} = " + problem.values[i] + "$");
        }
        writer.write(" und $c_{" + numberOfItems + "} = " + problem.values[numberOfItems - 1] + "$. ");
        Main.newLine(writer);
        writer.write("Geben Sie zudem die vom Algorithmus bestimmte Tabelle \\texttt{C} ");
        writer.write("und die mitzunehmenden Gegenst\\\"ande an.");
        Main.newLine(writer);
        Main.newLine(writer);
        final PreprintMode mode = PreprintMode.parsePreprintMode(options);
        switch (mode) {
        case SOLUTION_SPACE:
            LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, writer);
            // fall-through
        case ALWAYS:
            LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
            writer.write("{\\Large");
            Main.newLine(writer);
            LaTeXUtils.printTable(
                KnapsackAlgorithm.toKnapsackSolutionTable(solution, Optional.empty()),
                Optional.empty(),
                KnapsackAlgorithm::knapsackTableColumnDefinition,
                true,
                0,
                writer
            );
            Main.newLine(writer);
            writer.write("}");
            Main.newLine(writer);
            writer.write("${}^*$ Gegenstand/Kapazit\\\"at");
            Main.newLine(writer);
            LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
            Main.newLine(writer);
            writer.write("Gegenst\\\"ande:\\\\[2ex]");
            Main.newLine(writer);
            writer.write("Wert:\\\\[2ex]");
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

    private static void printKnapsackSolution(
        final KnapsackProblem problem,
        final int[][] solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Die Tabelle \\texttt{C} wird vom Algorithmus wie folgt gef\\\"ullt:");
        Main.newLine(writer);
        Main.newLine(writer);
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
        writer.write("{\\Large");
        Main.newLine(writer);
        LaTeXUtils.printTable(
            KnapsackAlgorithm.toKnapsackSolutionTable(solution, Optional.of(problem.weights)),
            Optional.empty(),
            KnapsackAlgorithm::knapsackTableColumnDefinition,
            true,
            0,
            writer
        );
        Main.newLine(writer);
        writer.write("}");
        Main.newLine(writer);
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
        writer.write("\\\\[2ex]");
        Main.newLine(writer);
        Main.newLine(writer);
    }

    private static int[] toIntArray(final String line) {
        return Arrays.stream(line.split(",")).mapToInt(Integer::parseInt).toArray();
    }

    private static String[][] toKnapsackSolutionTable(
        final int[][] solution,
        final Optional<int[]> optionalWeightsForFilling
    ) {
        final String[][] tableWithArrows = new String[solution.length + 1][2 * solution[0].length + 1];
        tableWithArrows[0][0] = "${}^*$";
        for (int i = 0; i < solution.length; i++) {
            tableWithArrows[i + 1][0] = LaTeXUtils.bold(String.valueOf(i));
        }
        for (int i = 0; i < solution[0].length; i++) {
            tableWithArrows[0][2 * i + 1] = LaTeXUtils.bold(String.valueOf(i));
        }
        if (optionalWeightsForFilling.isPresent()) {
            KnapsackAlgorithm.fillKnapsackSolutionTable(
                tableWithArrows,
                solution,
                optionalWeightsForFilling.get()
            );
        }
        return tableWithArrows;
    }

    private KnapsackAlgorithm() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final KnapsackProblem problem = KnapsackAlgorithm.parseOrGenerateKnapsackProblem(input.options);
        final int[][] table = KnapsackAlgorithm.knapsack(problem);
        KnapsackAlgorithm.printKnapsackExercise(problem, table, input.options, input.exerciseWriter);
        KnapsackAlgorithm.printKnapsackSolution(problem, table, input.options, input.solutionWriter);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

}
