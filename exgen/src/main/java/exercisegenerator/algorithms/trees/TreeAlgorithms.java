package exercisegenerator.algorithms.trees;

import java.io.*;
import java.util.*;
import java.util.function.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.trees.*;

abstract class TreeAlgorithms {

    static final BinaryTreeFactory<Integer> AVL_TREE_FACTORY =
        new AVLTreeFactory<Integer>(new AVLTreeNodeFactory<Integer>());

    static final BinaryTreeFactory<Integer> BINARY_TREE_FACTORY =
        new BinaryTreeFactory<Integer>(new BinaryTreeNodeFactory<Integer>());

    static Pair<Deque<TreeOperation<Integer>>, Deque<TreeOperation<Integer>>> generateConstructionAndTasks(
        final Parameters options
    ) {
        return new Pair<Deque<TreeOperation<Integer>>, Deque<TreeOperation<Integer>>>(
            TreeAlgorithms.generateTasks(options),
            TreeAlgorithms.generateConstruction(options)
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
            TreeAlgorithms.parseOperations(parts[0].split(",")),
            TreeAlgorithms.parseOperations(parts[1].split(","))
        );
    }

    static Deque<TreeOperation<Integer>> parseOrGenerateConstruction(
        final String line,
        final Parameters options
    ) {
        final String[] parts = line.split(";");
        if (parts.length == 2) {
            return TreeAlgorithms.parseOperations(parts[0].split(","));
        }
        if (!options.containsKey(Flag.OPERATIONS)) {
            return TreeAlgorithms.generateConstruction(options);
        }
        final String[] nums;
        try (BufferedReader reader = new BufferedReader(new FileReader(options.get(Flag.OPERATIONS)))) {
            nums = reader.readLine().split(",");
        } catch (final IOException e) {
            e.printStackTrace();
            return new ArrayDeque<TreeOperation<Integer>>();
        }
        return TreeAlgorithms.parseOperations(nums);
    }

    static Deque<TreeOperation<Integer>> parseOrGenerateTasks(final Parameters options)
    throws IOException {
        return new ParserAndGenerator<Deque<TreeOperation<Integer>>>(
            TreeAlgorithms::parseTasks,
            TreeAlgorithms::generateTasks
        ).getResult(options);
    }

    static void treeAlgorithm(
        final AlgorithmInput input,
        final BinaryTreeFactory<Integer> factory,
        final BiFunction<BinaryTree<Integer>, Deque<TreeOperation<Integer>>, BinaryTreeSteps<Integer>> algorithm
    ) throws IOException {
        final Pair<Deque<TreeOperation<Integer>>, Deque<TreeOperation<Integer>>> constructionAndTasks =
            new ParserAndGenerator<Pair<Deque<TreeOperation<Integer>>, Deque<TreeOperation<Integer>>>>(
                TreeAlgorithms::parseConstructionAndTasks,
                TreeAlgorithms::generateConstructionAndTasks
            ).getResult(input.options);
        final BinaryTree<Integer> tree = factory.create(constructionAndTasks.x);
        final BinaryTreeSteps<Integer> steps = algorithm.apply(tree, constructionAndTasks.y);
        TreeAlgorithms.printTreeExercise(tree, constructionAndTasks.y, input.exerciseWriter);
        TreeAlgorithms.printTreeSolution(steps, input.solutionWriter);
    }

    private static Deque<TreeOperation<Integer>> generateConstruction(final Parameters options) {
        final Random gen = new Random();
        final int length = gen.nextInt(20) + 1;
        final Deque<TreeOperation<Integer>> deque = new ArrayDeque<TreeOperation<Integer>>();
        final List<Integer> in = new ArrayList<Integer>();
        for (int i = 0; i < length; i++) {
            if (in.isEmpty() || gen.nextInt(3) > 0) {
                final int next = gen.nextInt(Main.NUMBER_LIMIT);
                deque.offer(new TreeOperation<Integer>(next, true));
                in.add(next);
            } else {
                deque.offer(new TreeOperation<Integer>(in.remove(gen.nextInt(in.size())), false));
            }
        }
        return deque;
    }

    private static Deque<TreeOperation<Integer>> generateTasks(final Parameters options) {
        final int length;
        final Random gen = new Random();
        if (options.containsKey(Flag.LENGTH)) {
            length = Integer.parseInt(options.get(Flag.LENGTH));
        } else {
            length = gen.nextInt(16) + 5;
        }
        final Deque<TreeOperation<Integer>> deque = new ArrayDeque<TreeOperation<Integer>>();
        final List<Integer> in = new ArrayList<Integer>();
        for (int i = 0; i < length; i++) {
            if (
                (options.containsKey(Flag.VARIANT) && options.get(Flag.VARIANT).equals("1"))
                || in.isEmpty()
                || gen.nextInt(3) > 0
            ) {
                final int next = gen.nextInt(Main.NUMBER_LIMIT);
                deque.offer(new TreeOperation<Integer>(next, true));
                in.add(next);
            } else {
                deque.offer(new TreeOperation<Integer>(in.remove(gen.nextInt(in.size())), false));
            }
        }
        return deque;
    }

    private static int heightToSteps(final int height) {
        if (height < 3) {
            return 3;
        }
        if (height < 5) {
            return 5;
        }
        return height;
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
        return TreeAlgorithms.parseOperations(nums);
    }

    private static void printSamePageBeginning(final int height, final String headline, final BufferedWriter writer)
    throws IOException {
        if (height < 3) {
            writer.write("\\begin{minipage}[t]{4cm}");
        } else if (height < 5) {
            writer.write("\\begin{minipage}[t]{7cm}");
        } else {
            Main.newLine(writer);
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

    private static void printSamePageEnd(final int height, final BufferedWriter writer) throws IOException {
        writer.write("\\end{center}");
        Main.newLine(writer);
        if (height < 5) {
            writer.write("\\end{minipage}");
            Main.newLine(writer);
        }
    }

    private static <T extends Comparable<T>> int printTreeAndReturnStepCounter(
        final int stepCounter,
        final String headline,
        final BinaryTree<T> tree,
        final BufferedWriter writer
    ) throws IOException {
        final int height = tree.getHeight();
        final int newStepCounter = TreeAlgorithms.printVerticalSpaceAndReturnStepCounter(stepCounter, height, writer);
        TreeAlgorithms.printSamePageBeginning(height, headline, writer);
        LaTeXUtils.printTikzBeginning(TikZStyle.TREE, writer);
        writer.write(tree.toTikZ());
        Main.newLine(writer);
        LaTeXUtils.printTikzEnd(writer);
        LaTeXUtils.printProtectedNewline(writer);
        TreeAlgorithms.printSamePageEnd(height, writer);
        return newStepCounter;
    }

    private static void printTreeExercise(
        final BinaryTree<Integer> tree,
        final Deque<TreeOperation<Integer>> tasks,
        final BufferedWriter writer
    ) throws IOException {
        if (tasks.size() > 1) {
            if (tree.isEmpty()) {
                writer.write("F\\\"uhren Sie folgenden Operationen beginnend mit einem anfangs leeren \\emphasize{");
                writer.write(tree.getName());
                writer.write("} aus und geben Sie die entstehenden B\\\"aume nach jeder ");
                writer.write(tree.getOperations());
                writer.write(" an:\\\\[2ex]");
                Main.newLine(writer);
            } else {
                writer.write("Betrachten Sie den folgenden \\emphasize{");
                writer.write(tree.getName());
                writer.write("}:\\\\");
                Main.newLine(writer);
                TreeAlgorithms.printTreeAndReturnStepCounter(0, "", tree, writer);
                LaTeXUtils.printVerticalProtectedSpace(writer);
                writer.write("F\\\"uhren Sie beginnend mit diesem Baum die folgenden Operationen aus und ");
                writer.write("geben Sie die entstehenden B\\\"aume nach jeder ");
                writer.write(tree.getOperations());
                writer.write(" an:\\\\[2ex]");
                Main.newLine(writer);
            }
            LaTeXUtils.printBeginning(LaTeXUtils.ENUMERATE, writer);
            for (final TreeOperation<Integer> task : tasks) {
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
            final TreeOperation<Integer> op = tasks.peek();
            if (tree.isEmpty()) {
                if (op.add) {
                    writer.write("F\\\"ugen Sie den Wert ");
                    writer.write(String.valueOf(op.value));
                    writer.write(" in einen leeren \\emphasize{");
                    writer.write(tree.getName());
                    writer.write("} ein und geben Sie die entstehenden B\\\"aume nach jeder ");
                    writer.write(tree.getOperations());
                    writer.write(" an.\\\\");
                } else {
                    throw new IllegalArgumentException("Deleting a value from an empty tree makes no sense!");
                }
            } else {
                if (op.add) {
                    writer.write("F\\\"ugen Sie den Wert ");
                    writer.write(String.valueOf(op.value));
                    writer.write(" in den folgenden \\emphasize{");
                    writer.write(tree.getName());
                    writer.write("} ein und geben Sie die entstehenden B\\\"aume nach jeder ");
                    writer.write(tree.getOperations());
                    writer.write(" an:\\\\");
                } else {
                    writer.write("L\\\"oschen Sie den Wert ");
                    writer.write(String.valueOf(op.value));
                    writer.write(" aus dem folgenden \\emphasize{");
                    writer.write(tree.getName());
                    writer.write("} und geben Sie die entstehenden B\\\"aume nach jeder ");
                    writer.write(tree.getOperations());
                    writer.write(" an:\\\\");
                }
                Main.newLine(writer);
                Main.newLine(writer);
                TreeAlgorithms.printTreeAndReturnStepCounter(0, "", tree, writer);
            }
        }
        Main.newLine(writer);
    }

    private static <T extends Comparable<T>> void printTreeSolution(
        final BinaryTreeSteps<T> steps,
        final BufferedWriter writer
    ) throws IOException {
        int stepCounter = 0;
        for (final BinaryTreeAndStep<T> step : steps) {
            stepCounter = TreeAlgorithms.printTreeAndReturnStepCounter(stepCounter, step.y.toLaTeX(), step.x, writer);
        }
        Main.newLine(writer);
    }

    private static int printVerticalSpaceAndReturnStepCounter(
        final int stepCounter,
        final int height,
        final BufferedWriter writer
    ) throws IOException {
        final int steps = TreeAlgorithms.heightToSteps(height);
        final int newStepCounter = stepCounter + steps;
        if (newStepCounter >= 12) {
            Main.newLine(writer);
            writer.write("~\\\\");
            Main.newLine(writer);
            Main.newLine(writer);
            return steps;
        }
        return newStepCounter;
    }

}
