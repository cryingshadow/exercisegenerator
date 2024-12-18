package exercisegenerator.algorithms.trees;

import java.io.*;
import java.util.*;

import org.apache.commons.math3.fraction.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.trees.*;

interface SearchTreeAlgorithm extends AlgorithmImplementation<SearchTreeProblem, SearchTreeSteps<Integer>> {

    static Pair<Deque<TreeOperation<Integer>>, Deque<TreeOperation<Integer>>> generateConstructionAndTasks(
        final Parameters options
    ) {
        return new Pair<Deque<TreeOperation<Integer>>, Deque<TreeOperation<Integer>>>(
            SearchTreeAlgorithm.generateTasks(options),
            SearchTreeAlgorithm.generateConstruction(options)
        );
    }

    static Pair<Deque<TreeOperation<Integer>>, Deque<TreeOperation<Integer>>> parseConstructionAndTasks(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        final String[] parts = reader.readLine().split(";");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Input must contain exactly two parts: construction and tasks!");
        }
        return new Pair<Deque<TreeOperation<Integer>>, Deque<TreeOperation<Integer>>>(
            SearchTreeAlgorithm.parseOperations(parts[0].split(",")),
            SearchTreeAlgorithm.parseOperations(parts[1].split(","))
        );
    }

    static Deque<TreeOperation<Integer>> parseOrGenerateConstruction(
        final String line,
        final Parameters options
    ) {
        final String[] parts = line.split(";");
        if (parts.length == 2) {
            return SearchTreeAlgorithm.parseOperations(parts[0].split(","));
        }
        if (!options.containsKey(Flag.OPERATIONS)) {
            return SearchTreeAlgorithm.generateConstruction(options);
        }
        final String[] nums;
        try (BufferedReader reader = new BufferedReader(new FileReader(options.get(Flag.OPERATIONS)))) {
            nums = reader.readLine().split(",");
        } catch (final IOException e) {
            e.printStackTrace();
            return new ArrayDeque<TreeOperation<Integer>>();
        }
        return SearchTreeAlgorithm.parseOperations(nums);
    }

    static Deque<TreeOperation<Integer>> parseOrGenerateTasks(final Parameters options)
    throws IOException {
        return new ParserAndGenerator<Deque<TreeOperation<Integer>>>(
            SearchTreeAlgorithm::parseTasks,
            SearchTreeAlgorithm::generateTasks
        ).getResult(options);
    }

    private static Deque<TreeOperation<Integer>> generateConstruction(final Parameters options) {
        final int length = Main.RANDOM.nextInt(20) + 1;
        final Deque<TreeOperation<Integer>> deque = new ArrayDeque<TreeOperation<Integer>>();
        final List<Integer> in = new ArrayList<Integer>();
        for (int i = 0; i < length; i++) {
            if (in.isEmpty() || Main.RANDOM.nextInt(3) > 0) {
                final int next = Main.RANDOM.nextInt(Main.NUMBER_LIMIT);
                deque.offer(new TreeOperation<Integer>(next, true));
                in.add(next);
            } else {
                deque.offer(new TreeOperation<Integer>(in.remove(Main.RANDOM.nextInt(in.size())), false));
            }
        }
        return deque;
    }

    private static Deque<TreeOperation<Integer>> generateTasks(final Parameters options) {
        final int length = AlgorithmImplementation.parseOrGenerateLength(5, 20, options);
        final Deque<TreeOperation<Integer>> deque = new ArrayDeque<TreeOperation<Integer>>();
        final List<Integer> in = new ArrayList<Integer>();
        for (int i = 0; i < length; i++) {
            if (
                (options.containsKey(Flag.VARIANT) && options.get(Flag.VARIANT).equals("1"))
                || in.isEmpty()
                || Main.RANDOM.nextInt(3) > 0
            ) {
                final int next = Main.RANDOM.nextInt(Main.NUMBER_LIMIT);
                deque.offer(new TreeOperation<Integer>(next, true));
                in.add(next);
            } else {
                deque.offer(new TreeOperation<Integer>(in.remove(Main.RANDOM.nextInt(in.size())), false));
            }
        }
        return deque;
    }

    private static Deque<TreeOperation<Integer>> parseOperations(final String[] operations) {
        final Deque<TreeOperation<Integer>> deque = new ArrayDeque<TreeOperation<Integer>>();
        for (final String num : operations) {
            if (num.isBlank()) {
                continue;
            }
            final String stripped = num.strip();
            if (stripped.startsWith("~")) {
                deque.offer(new TreeOperation<Integer>(Integer.parseInt(stripped.substring(1)), false));
            } else {
                deque.offer(new TreeOperation<Integer>(Integer.parseInt(stripped), true));
            }
        }
        return deque;
    }

    private static Deque<TreeOperation<Integer>> parseTasks(final BufferedReader reader, final Parameters options)
    throws IOException {
        final String[] nums = reader.readLine().split(",");
        return SearchTreeAlgorithm.parseOperations(nums);
    }

    private static void printSamePageBeginning(
        final Optional<String> width,
        final String headline,
        final BufferedWriter writer
    ) throws IOException {
        if (width.isEmpty()) {
            writer.write("\\begin{minipage}[t]{\\columnwidth}");
        } else {
            writer.write(String.format("\\begin{minipage}[t]{%s}", width.get()));
        }
        Main.newLine(writer);
        if (headline != null && !headline.isBlank()) {
            writer.write(headline);
            writer.write("\\\\[-2ex]");
            Main.newLine(writer);
        }
        writer.write("\\begin{center}");
        Main.newLine(writer);
    }

    private static void printSamePageEnd(final BufferedWriter writer) throws IOException {
        writer.write("\\end{center}");
        Main.newLine(writer);
        writer.write("\\end{minipage}");
        Main.newLine(writer);
    }

    private static <T extends Comparable<T>> BigFraction printTreeAndReturnHorizontalFillingDegree(
        final BigFraction horizontalFillingDegree,
        final String headline,
        final SearchTree<T> tree,
        final BufferedWriter writer
    ) throws IOException {
        final BigFraction nextHorizontalFilligDegree = tree.getHorizontalFillingDegree();
        final BigFraction sumHorizontalFillingDegree = horizontalFillingDegree.add(nextHorizontalFilligDegree);
        final BigFraction resultingHorizontalFillingDegree;
        if (sumHorizontalFillingDegree.compareTo(BigFraction.ONE) > 0) {
            SearchTreeAlgorithm.printVerticalSpace(writer);
            resultingHorizontalFillingDegree = nextHorizontalFilligDegree;
        } else {
            resultingHorizontalFillingDegree = sumHorizontalFillingDegree;
        }
        SearchTreeAlgorithm.printSamePageBeginning(tree.getSamePageWidth(), headline, writer);
        LaTeXUtils.printTikzBeginning(tree.getTikZStyle(), writer);
        writer.write(tree.toTikZ());
        Main.newLine(writer);
        LaTeXUtils.printTikzEnd(writer);
        LaTeXUtils.printProtectedNewline(writer);
        SearchTreeAlgorithm.printSamePageEnd(writer);
        return resultingHorizontalFillingDegree;
    }

    private static void printVerticalSpace(final BufferedWriter writer) throws IOException {
        Main.newLine(writer);
        writer.write("~\\\\");
        Main.newLine(writer);
        Main.newLine(writer);
    }

    @Override
    default public SearchTreeSteps<Integer> apply(final SearchTreeProblem problem) {
        final SearchTreeSteps<Integer> result = new SearchTreeSteps<Integer>();
        SearchTree<Integer> currentTree = problem.tree();
        for (final TreeOperation<Integer> operation : problem.operations()) {
            result.addAll(
                operation.add ? currentTree.addWithSteps(operation.value) : currentTree.removeWithSteps(operation.value)
            );
            currentTree = result.getLast().tree();
        }
        return result;
    }

    @Override
    default public SearchTreeProblem parseOrGenerateProblem(final Parameters options) throws IOException {
        final Pair<Deque<TreeOperation<Integer>>, Deque<TreeOperation<Integer>>> constructionAndTasks =
            new ParserAndGenerator<Pair<Deque<TreeOperation<Integer>>, Deque<TreeOperation<Integer>>>>(
                SearchTreeAlgorithm::parseConstructionAndTasks,
                SearchTreeAlgorithm::generateConstructionAndTasks
            ).getResult(options);
        final SearchTreeFactory<Integer> factory = this.parseOrGenerateTreeFactory(options);
        return new SearchTreeProblem(factory.create(constructionAndTasks.x), constructionAndTasks.y, factory);
    }

    @Override
    default public void printExercise(
        final SearchTreeProblem problem,
        final SearchTreeSteps<Integer> solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        final SearchTree<Integer> tree = problem.tree();
        final Deque<TreeOperation<Integer>> operations = problem.operations();
        if (operations.size() > 1) {
            if (tree.isEmpty()) {
                writer.write("F\\\"uhren Sie folgenden Operationen beginnend mit einem anfangs leeren \\emphasize{");
                writer.write(tree.getName());
                writer.write("} aus und geben Sie die dabei jeweils entstehenden B\\\"aume nach jeder ");
                Main.newLine(writer);
                writer.write(tree.getOperations());
                Main.newLine(writer);
                writer.write("an:\\\\[2ex]");
                Main.newLine(writer);
            } else {
                writer.write("Betrachten Sie den folgenden \\emphasize{");
                writer.write(tree.getName());
                writer.write("}:\\\\");
                Main.newLine(writer);
                SearchTreeAlgorithm.printTreeAndReturnHorizontalFillingDegree(BigFraction.ZERO, "", tree, writer);
                LaTeXUtils.printVerticalProtectedSpace(writer);
                writer.write("F\\\"uhren Sie beginnend mit diesem Baum die folgenden Operationen aus und ");
                writer.write("geben Sie die dabei jeweils entstehenden B\\\"aume nach jeder ");
                Main.newLine(writer);
                writer.write(tree.getOperations());
                Main.newLine(writer);
                writer.write("an:\\\\[2ex]");
                Main.newLine(writer);
            }
            LaTeXUtils.printBeginning(LaTeXUtils.ENUMERATE, writer);
            for (final TreeOperation<Integer> task : operations) {
                if (task.add) {
                    writer.write(LaTeXUtils.ITEM);
                    writer.write(" ");
                    writer.write(String.valueOf(task.value));
                    writer.write(" einf\\\"ugen\\\\");
                } else {
                    writer.write(LaTeXUtils.ITEM);
                    writer.write(" ");
                    writer.write(String.valueOf(task.value));
                    writer.write(" l\\\"oschen\\\\");
                }
                Main.newLine(writer);
            }
            LaTeXUtils.printEnd(LaTeXUtils.ENUMERATE, writer);
        } else {
            final TreeOperation<Integer> op = operations.peek();
            if (tree.isEmpty()) {
                if (op.add) {
                    writer.write("F\\\"ugen Sie den Wert ");
                    writer.write(String.valueOf(op.value));
                    writer.write(" in einen leeren \\emphasize{");
                    writer.write(tree.getName());
                    writer.write("} ein und geben Sie die dabei jeweils entstehenden B\\\"aume nach jeder ");
                    Main.newLine(writer);
                    writer.write(tree.getOperations());
                    Main.newLine(writer);
                    writer.write("an.\\\\");
                } else {
                    throw new IllegalArgumentException("Deleting a value from an empty tree makes no sense!");
                }
            } else {
                if (op.add) {
                    writer.write("F\\\"ugen Sie den Wert ");
                    writer.write(String.valueOf(op.value));
                    writer.write(" in den folgenden \\emphasize{");
                    writer.write(tree.getName());
                    writer.write("} ein und geben Sie die dabei jeweils entstehenden B\\\"aume nach jeder ");
                    Main.newLine(writer);
                    writer.write(tree.getOperations());
                    Main.newLine(writer);
                    writer.write("an:\\\\");
                } else {
                    writer.write("L\\\"oschen Sie den Wert ");
                    writer.write(String.valueOf(op.value));
                    writer.write(" aus dem folgenden \\emphasize{");
                    writer.write(tree.getName());
                    writer.write("} und geben Sie die dabei jeweils entstehenden B\\\"aume nach jeder ");
                    Main.newLine(writer);
                    writer.write(tree.getOperations());
                    Main.newLine(writer);
                    writer.write("an:\\\\");
                }
                Main.newLine(writer);
                Main.newLine(writer);
                SearchTreeAlgorithm.printTreeAndReturnHorizontalFillingDegree(BigFraction.ZERO, "", tree, writer);
            }
        }
        Main.newLine(writer);
    }

    @Override
    default public void printSolution(
        final SearchTreeProblem problem,
        final SearchTreeSteps<Integer> solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        int stepNumber = 1;
        BigFraction horizontalFillingDegree = BigFraction.ZERO;
        for (final SearchTreeAndStep<Integer> step : solution) {
            horizontalFillingDegree =
                SearchTreeAlgorithm.printTreeAndReturnHorizontalFillingDegree(
                    horizontalFillingDegree,
                    String.format("Schritt %d: %s", stepNumber, step.step().toLaTeX()),
                    step.tree(),
                    writer
                );
            stepNumber++;
        }
        Main.newLine(writer);
    }

    SearchTreeFactory<Integer> parseOrGenerateTreeFactory(final Parameters options);

}
