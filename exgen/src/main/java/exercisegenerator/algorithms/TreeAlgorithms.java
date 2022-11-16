package exercisegenerator.algorithms;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.trees.*;

public abstract class TreeAlgorithms {

    /**
     * Flag to enable debug output.
     */
    private static final boolean DEBUG_OUTPUT = false;

    /**
     * Flag to enable assertions.
     */
    private static final boolean USE_ASSERTIONS = true;

    public static void avltree(final AlgorithmInput input) throws IOException {
        final Pair<IntAVLTree, Deque<Pair<Integer, Boolean>>> treeAndTasks =
            new ParserAndGenerator<Pair<IntAVLTree, Deque<Pair<Integer, Boolean>>>>(
                TreeAlgorithms::parseAVLTreeAndTasks,
                TreeAlgorithms::generateAVLTreeAndTasks
            ).getResult(input.options);
        TreeAlgorithms.avltree(
            treeAndTasks.x,
            treeAndTasks.y,
            input.solutionWriter,
            Algorithm.getOptionalSpaceWriter(input)
        );
    }

    public static void avltree(
        final IntAVLTree tree,
        final Deque<Pair<Integer, Boolean>> tasks,
        final BufferedWriter solutionWriter,
        final Optional<BufferedWriter> optionalWriterSpace
    ) throws IOException {
        if (TreeAlgorithms.USE_ASSERTIONS) {
            TreeAlgorithms.check(tree);
        }
        if (tasks.isEmpty()) {
            return;
        }
        if (TreeAlgorithms.DEBUG_OUTPUT) {
            System.out.println(tree.root == null ? "-1" : tree.root.printHeights());
        }
        if (optionalWriterSpace.isPresent()) {
            final BufferedWriter writerSpace = optionalWriterSpace.get();
            if (tasks.size() > 1) {
                if (tree.isEmpty()) {
                    writerSpace.write("F\\\"uhren Sie folgenden Operationen beginnend mit einem anfangs leeren ");
                    writerSpace.write("\\emphasize{AVL-Baum} aus und geben Sie die entstehenden B\\\"aume nach jeder ");
                    writerSpace.write("\\emphasize{Einf\\\"uge-} und \\emphasize{L\\\"oschoperation} sowie jeder ");
                    writerSpace.write("\\emphasize{Rotation} an:\\\\\\\\");
                    Main.newLine(writerSpace);
                } else {
                    writerSpace.write("Betrachten Sie den folgenden \\emphasize{AVL-Baum}:\\\\");
                    Main.newLine(writerSpace);
                    Main.newLine(writerSpace);
                    tree.print("", optionalWriterSpace);
                    Main.newLine(writerSpace);
                    Main.newLine(writerSpace);
                    writerSpace.write("\\vspace*{1ex}");
                    Main.newLine(writerSpace);
                    writerSpace.write("F\\\"uhren Sie beginnend mit diesem Baum die folgenden Operationen aus und ");
                    writerSpace.write("geben Sie die entstehenden B\\\"aume nach jeder \\emphasize{Einf\\\"uge-} und ");
                    writerSpace.write("\\emphasize{L\\\"oschoperation} sowie jeder \\emphasize{Rotation} an:\\\\\\\\");
                    Main.newLine(writerSpace);
                }
                LaTeXUtils.printBeginning(LaTeXUtils.ENUMERATE, writerSpace);
                for (final Pair<Integer, Boolean> task : tasks) {
                    if (task.y) {
                        writerSpace.write(LaTeXUtils.ITEM + " " + task.x + " einf\\\"ugen\\\\");
                    } else {
                        writerSpace.write(LaTeXUtils.ITEM + " " + task.x + " l\\\"oschen\\\\");
                    }
                    Main.newLine(writerSpace);
                }
                LaTeXUtils.printEnd(LaTeXUtils.ENUMERATE, writerSpace);
            } else {
                final Pair<Integer, Boolean> task = tasks.peek();
                if (tree.isEmpty()) {
                    if (task.y) {
                        writerSpace.write("F\\\"ugen Sie den Wert " + task.x);
                        writerSpace.write(" in einen leeren \\emphasize{AVL-Baum} ein und geben Sie die entstehenden ");
                        writerSpace.write("B\\\"aume nach jeder \\emphasize{Einf\\\"ugeoperation} sowie jeder ");
                        writerSpace.write("\\emphasize{Rotation} an.");
                    } else {
                        // this case is nonsense
                        return;
                    }
                } else {
                    if (task.y) {
                        writerSpace.write("F\\\"ugen Sie den Wert " + task.x);
                        writerSpace.write(" in den folgenden \\emphasize{AVL-Baum} ein und geben Sie die entstehenden");
                        writerSpace.write(" B\\\"aume nach jeder \\emphasize{Einf\\\"ugeoperation} sowie jeder ");
                        writerSpace.write("\\emphasize{Rotation} an:\\\\");
                    } else {
                        writerSpace.write("L\\\"oschen Sie den Wert " + task.x);
                        writerSpace.write(" aus dem folgenden \\emphasize{AVL-Baum} und geben Sie die entstehenden ");
                        writerSpace.write("B\\\"aume nach jeder \\emphasize{L\\\"oschoperation} sowie jeder ");
                        writerSpace.write("\\emphasize{Rotation} an:\\\\");
                    }
                    Main.newLine(writerSpace);
                    Main.newLine(writerSpace);
                    tree.print("", optionalWriterSpace);
                    Main.newLine(writerSpace);
                }
            }
        }
        tree.resetStepCounter();
        while (!tasks.isEmpty()) {
            final Pair<Integer, Boolean> task = tasks.poll();
            if (task.y) {
                if (TreeAlgorithms.DEBUG_OUTPUT) {
                    System.out.println("insert " + task.x + " in:");
                    System.out.println(tree.toString());
                }
                tree.add(task.x, Optional.of(solutionWriter));
            } else {
                if (TreeAlgorithms.DEBUG_OUTPUT) {
                    System.out.println("remove " + task.x + " in:");
                    System.out.println(tree.toString());
                }
                final Optional<AVLNode> toRemove = tree.find(task.x);
                if (toRemove.isPresent()) {
                    tree.remove(toRemove.get(), Optional.of(solutionWriter));
                } else {
                    tree.print(task.x + " kommt nicht vor", Optional.of(solutionWriter));
                }
            }
            if (TreeAlgorithms.USE_ASSERTIONS) {
                TreeAlgorithms.check(tree);
            }
            if (TreeAlgorithms.DEBUG_OUTPUT) {
                System.out.println("results in:");
                System.out.println(tree.toString());
            }
        }
    }

    static final BinaryTreeFactory<Integer> binaryTreeFactory = new BinaryTreeFactory<Integer>();
    
    public static void bstree(final AlgorithmInput input) throws IOException {
        final Pair<Deque<Pair<Integer, Boolean>>, Deque<Pair<Integer, Boolean>>> constructionAndTasks =
            new ParserAndGenerator<Pair<Deque<Pair<Integer, Boolean>>, Deque<Pair<Integer, Boolean>>>>(
                TreeAlgorithms::parseConstructionAndTasks,
                TreeAlgorithms::generateConstructionAndTasks
            ).getResult(input.options);
        final BinaryTree<Integer> tree = binaryTreeFactory.create(constructionAndTasks.x);
        final BinaryTreeSteps<Integer> steps =
            TreeAlgorithms.bstree(tree, constructionAndTasks.y);
        TreeAlgorithms.printTreeExercise(tree, constructionAndTasks.y, input.exerciseWriter);
        TreeAlgorithms.printTreeSolution(steps, input.solutionWriter);
    }

    public static <T extends Comparable<T>> BinaryTreeSteps<T> bstree(
        final BinaryTree<T> tree,
        final Deque<Pair<T, Boolean>> tasks
    ) {
        final BinaryTreeSteps<T> result = new BinaryTreeSteps<T>();
        BinaryTree<T> currentTree = tree;
        for (final Pair<T, Boolean> task : tasks) {
            result.addAll(
                task.y ? currentTree.addWithSteps(task.x) : currentTree.removeWithSteps(task.x)
            );
            currentTree = result.getLast().x;
        }
        return result;
    }

    public static void btree(final AlgorithmInput input) throws NumberFormatException, IOException {
        TreeAlgorithms.btree(
            new IntBTree(
                input.options.containsKey(Flag.DEGREE) ? Integer.parseInt(input.options.get(Flag.DEGREE)) : 2
            ),
            TreeAlgorithms.parseOrGenerateTasks(input.options),
            TreeAlgorithms.parseOrGenerateConstruction("", input.options),
            input.solutionWriter,
            Algorithm.getOptionalSpaceWriter(input)
        );
    }

    /**
     * Performs the operations specified by <code>construction</code> and <code>ops</code> on the specified B-tree and
     * prints the results to the specified writer. The <code>construction</code> operations are not displayed.
     * @param tree The B-tree.
     * @param tasks The operations.
     * @param construction The operations used to construct the start structure.
     * @param writer The writer for the solution.
     * @param writerSpace The writer for the tree to start with (the one reached after the <code>construction</code>
     *                    operations). May be null if this tree should not be displayed separately.
     * @throws IOException If some error occurs during output.
     */
    public static void btree(
        final IntBTree tree,
        final Deque<Pair<Integer, Boolean>> tasks,
        final Deque<Pair<Integer, Boolean>> construction,
        final BufferedWriter writer,
        final Optional<BufferedWriter> optionalWriterSpace
    ) throws IOException {
        if (tasks.isEmpty()) {
            return;
        }
        while (!construction.isEmpty()) {
            final Pair<Integer, Boolean> operation = construction.poll();
            if (operation.y) {
                tree.add(operation.x);
            } else {
                tree.remove(operation.x);
            }
        }
        if (optionalWriterSpace.isPresent()) {
            final BufferedWriter writerSpace = optionalWriterSpace.get();
            final int degree = tree.getDegree();
            if (tasks.size() > 1) {
                if (tree.isEmpty()) {
                    writerSpace.write(
                        "F\\\"uhren Sie folgenden Operationen beginnend mit einem anfangs leeren \\emphasize{"
                        + (
                            degree == 2 ?
                                IntBTree.NAME_OF_BTREE_WITH_DEGREE_2 + "}" :
                                    "B-Baum} mit Grad $t = " + degree + "$"
                        ) + " aus und geben Sie die dabei jeweils entstehenden B\\\"aume an:\\\\\\\\"
                    );
                    Main.newLine(writerSpace);
                } else {
                    writerSpace.write(
                        "Betrachten Sie den folgenden \\emphasize{"
                        + (
                            degree == 2 ?
                                IntBTree.NAME_OF_BTREE_WITH_DEGREE_2 + "}" :
                                    "B-Baum} mit Grad $t = " + degree + "$"
                        ) + ":\\\\[2ex]"
                    );
                    Main.newLine(writerSpace);
                    Main.newLine(writerSpace);
                    LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writerSpace);
                    LaTeXUtils.printTikzBeginning(TikZStyle.BTREE, writerSpace);
                    LaTeXUtils.printBTree(tree, writerSpace);
                    LaTeXUtils.printTikzEnd(writerSpace);
                    LaTeXUtils.printEnd(LaTeXUtils.CENTER, writerSpace);
                    Main.newLine(writerSpace);
                    Main.newLine(writerSpace);
                    writerSpace.write("\\vspace*{1ex}");
                    Main.newLine(writerSpace);
                    writerSpace.write(
                        "F\\\"uhren Sie beginnend mit diesem Baum die folgenden Operationen aus "
                        + "und geben Sie die dabei jeweils entstehenden B\\\"aume an:\\\\\\\\"
                    );
                    Main.newLine(writerSpace);
                }
                LaTeXUtils.printBeginning(LaTeXUtils.ENUMERATE, writerSpace);
                for (final Pair<Integer, Boolean> task : tasks) {
                    if (task.y) {
                        writerSpace.write(LaTeXUtils.ITEM + " " + task.x + " einf\\\"ugen\\\\");
                    } else {
                        writerSpace.write(LaTeXUtils.ITEM + " " + task.x + " l\\\"oschen\\\\");
                    }
                    Main.newLine(writerSpace);
                }
                LaTeXUtils.printEnd(LaTeXUtils.ENUMERATE, writerSpace);
            } else {
                final Pair<Integer, Boolean> task = tasks.peek();
                if (tree.isEmpty()) {
                    if (task.y) {
                        writerSpace.write(
                            "F\\\"ugen Sie den Wert "
                            + task.x
                            + " in einen leeren \\emphasize{"
                            + (
                                degree == 2 ?
                                    IntBTree.NAME_OF_BTREE_WITH_DEGREE_2 + "}" :
                                        "B-Baum} mit Grad $t = " + degree + "$"
                            ) + " ein und geben Sie den dabei entstehenden Baum an."
                        );
                    } else {
                        // this case is nonsense
                        return;
                    }
                } else {
                    if (task.y) {
                        writerSpace.write(
                            "F\\\"ugen Sie den Wert "
                            + task.x
                            + " in den folgenden \\emphasize{"
                            + (
                                degree == 2 ?
                                    IntBTree.NAME_OF_BTREE_WITH_DEGREE_2 + "}":
                                        "B-Baum} mit Grad $t = " + degree + "$"
                            ) + " ein und geben Sie den dabei entstehenden Baum an:\\\\[2ex]"
                        );
                    } else {
                        writerSpace.write(
                            "L\\\"oschen Sie den Wert "
                            + task.x
                            + " aus dem folgenden \\emphasize{"
                            + (
                                degree == 2 ?
                                    IntBTree.NAME_OF_BTREE_WITH_DEGREE_2 + "}" :
                                        "B-Baum} mit Grad $t = " + degree + "$"
                            ) + " und geben Sie den dabei entstehenden Baum an:\\\\[2ex]"
                        );
                    }
                    Main.newLine(writerSpace);
                    Main.newLine(writerSpace);
                    LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writerSpace);
                    LaTeXUtils.printTikzBeginning(TikZStyle.BTREE, writerSpace);
                    LaTeXUtils.printBTree(tree, writerSpace);
                    LaTeXUtils.printTikzEnd(writerSpace);
                    LaTeXUtils.printEnd(LaTeXUtils.CENTER, writerSpace);
                    Main.newLine(writerSpace);
                }
            }
        }
        int step = 1;
        while (!tasks.isEmpty()) {
            final Pair<Integer, Boolean> task = tasks.poll();
            if (task.y) {
                tree.add(task.x);
            } else {
                tree.remove(task.x);
            }
            LaTeXUtils.printSamePageBeginning(step++, task, writer);
            LaTeXUtils.printTikzBeginning(TikZStyle.BTREE, writer);
            LaTeXUtils.printBTree(tree, writer);
            LaTeXUtils.printTikzEnd(writer);
            LaTeXUtils.printSamePageEnd(writer);
        }
    }

    public static String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    public static void rbtree(final AlgorithmInput input) throws IOException {
        TreeAlgorithms.rbtree(
            new IntRBTree(),
            TreeAlgorithms.parseOrGenerateTasks(input.options),
            TreeAlgorithms.parseOrGenerateConstruction("", input.options),
            input.solutionWriter,
            Algorithm.getOptionalSpaceWriter(input)
        );
    }

    /**
     * Performs the operations specified by <code>construction</code> and <code>ops</code> on the specified RB-tree and
     * prints the results to the specified writer. The <code>construction</code> operations are not displayed.
     * @param tree The RB-tree.
     * @param tasks The operations.
     * @param construction The operations used to construct the start structure.
     * @param writer The writer for the solution.
     * @param writerSpace The writer for the tree to start with (the one reached after the <code>construction</code>
     *                    operations). May be null if this tree should not be displayed separately.
     * @throws IOException If some error occurs during output.
     */
    public static void rbtree(
        final IntRBTree tree,
        final Deque<Pair<Integer, Boolean>> tasks,
        final Deque<Pair<Integer, Boolean>> construction,
        final BufferedWriter writer,
        final Optional<BufferedWriter> optionalWriterSpace
    ) throws IOException {
        if (tasks.isEmpty()) {
            return;
        }
        while (!construction.isEmpty()) {
            final Pair<Integer, Boolean> operation = construction.poll();
            if (operation.y) {
                tree.rbInsert(operation.x, writer, false);
            } else {
                final RBNode toRemove = tree.find(operation.x);
                if (toRemove != null) {
                    tree.remove(toRemove, writer, false);
                }
            }
        }
        final String note = "Beachten Sie, dass rote Knoten rund und schwarze Knoten eckig dargestellt werden.";
        if (optionalWriterSpace.isPresent()) {
            final BufferedWriter writerSpace = optionalWriterSpace.get();
            if (tasks.size() > 1) {
                if (tree.isEmpty()) {
                    writerSpace.write("F\\\"uhren Sie die folgenden Operationen beginnend mit einem anfangs leeren ");
                    writerSpace.write("\\emphasize{Rot-Schwarz-Baum} aus und geben Sie die entstehenden B\\\"aume ");
                    writerSpace.write("nach jeder \\emphasize{Einf\\\"uge-} und \\emphasize{L\\\"oschoperation}, ");
                    writerSpace.write("jeder \\emphasize{Rotation} und jeder \\emphasize{Umf\\\"arbung} an. ");
                    writerSpace.write(note);
                    writerSpace.write("\\\\\\\\");
                    Main.newLine(writerSpace);
                } else {
                    writerSpace.write("Betrachten Sie den folgenden \\emphasize{Rot-Schwarz-Baum}:\\\\[2ex]");
                    Main.newLine(writerSpace);
                    Main.newLine(writerSpace);
                    tree.print("", writerSpace);
                    Main.newLine(writerSpace);
                    Main.newLine(writerSpace);
                    writerSpace.write("\\vspace*{1ex}");
                    Main.newLine(writerSpace);
                    writerSpace.write("F\\\"uhren Sie beginnend mit diesem Rot-Schwarz-Baum die folgenden ");
                    writerSpace.write("Operationen aus und geben Sie die entstehenden B\\\"aume nach jeder ");
                    writerSpace.write("\\emphasize{Einf\\\"uge-} und \\emphasize{L\\\"oschoperation}, jeder ");
                    writerSpace.write("\\emphasize{Rotation} und jeder \\emphasize{Umf\\\"arbung} an. ");
                    writerSpace.write(note);
                    writerSpace.write("\\\\\\\\");
                    Main.newLine(writerSpace);
                }
                LaTeXUtils.printBeginning(LaTeXUtils.ENUMERATE, writerSpace);
                for (final Pair<Integer, Boolean> task : tasks) {
                    if (task.y) {
                        writerSpace.write(LaTeXUtils.ITEM + " " + task.x + " einf\\\"ugen\\\\");
                    } else {
                        writerSpace.write(LaTeXUtils.ITEM + " " + task.x + " l\\\"oschen\\\\");
                    }
                    Main.newLine(writerSpace);
                }
                LaTeXUtils.printEnd(LaTeXUtils.ENUMERATE, writerSpace);
            } else {
                final Pair<Integer, Boolean> task = tasks.peek();
                if (tree.isEmpty()) {
                    if (task.y) {
                        writerSpace.write("F\\\"ugen Sie den Wert " + task.x);
                        writerSpace.write(" in einen leeren \\emphasize{Rot-Schwarz-Baum} ein und geben Sie die ");
                        writerSpace.write("entstehenden B\\\"aume nach");
                        Main.newLine(writerSpace);
                        writerSpace.write("\\begin{itemize}");
                        Main.newLine(writerSpace);
                        writerSpace.write("    \\item jeder \\emphasize{Einf\\\"ugeoperation},");
                        Main.newLine(writerSpace);
                        writerSpace.write("    \\item jeder \\emphasize{Rotation} sowie");
                        Main.newLine(writerSpace);
                        writerSpace.write("    \\item jeder \\emphasize{Umf\\\"arbung} an.");
                        Main.newLine(writerSpace);
                        writerSpace.write("\\end{itemize}");
                        Main.newLine(writerSpace);
                        writerSpace.write(note);
                        Main.newLine(writerSpace);
                    } else {
                        throw new IllegalArgumentException("Deletion from an empty tree makes no sense!");
                    }
                } else {
                    if (task.y) {
                        writerSpace.write("F\\\"ugen Sie den Wert " + task.x);
                        writerSpace.write(" in den folgenden \\emphasize{Rot-Schwarz-Baum} ein und geben Sie die ");
                        writerSpace.write("entstehenden B\\\"aume nach");
                        Main.newLine(writerSpace);
                        writerSpace.write("\\begin{itemize}");
                        Main.newLine(writerSpace);
                        writerSpace.write("    \\item jeder \\emphasize{Einf\\\"ugeoperation},");
                        Main.newLine(writerSpace);
                        writerSpace.write("    \\item jeder \\emphasize{Rotation} sowie");
                        Main.newLine(writerSpace);
                        writerSpace.write("    \\item jeder \\emphasize{Umf\\\"arbung} an.");
                        Main.newLine(writerSpace);
                        writerSpace.write("\\end{itemize}");
                        Main.newLine(writerSpace);
                        writerSpace.write(note);
                        writerSpace.write("\\\\[2ex]");
                    } else {
                        writerSpace.write("L\\\"oschen Sie den Wert " + task.x);
                        writerSpace.write(" aus dem folgenden \\emphasize{Rot-Schwarz-Baum} und geben Sie die ");
                        writerSpace.write("entstehenden B\\\"aume nach");
                        Main.newLine(writerSpace);
                        writerSpace.write("\\begin{itemize}");
                        Main.newLine(writerSpace);
                        writerSpace.write("    \\item jeder \\emphasize{L\\\"oschoperation},");
                        Main.newLine(writerSpace);
                        writerSpace.write("    \\item jeder \\emphasize{Rotation} sowie");
                        Main.newLine(writerSpace);
                        writerSpace.write("    \\item jeder \\emphasize{Umf\\\"arbung} an.");
                        Main.newLine(writerSpace);
                        writerSpace.write("\\end{itemize}");
                        Main.newLine(writerSpace);
                        writerSpace.write(note);
                        writerSpace.write("\\\\[2ex]");
                    }
                    Main.newLine(writerSpace);
                    Main.newLine(writerSpace);
                    tree.print("", writerSpace);
                    Main.newLine(writerSpace);
                }
            }
        }
        tree.resetStepCounter();
        while (!tasks.isEmpty()) {
            final Pair<Integer, Boolean> task = tasks.poll();
            if (task.y) {
                tree.rbInsert(task.x, writer, true);
            } else {
                final RBNode toRemove = tree.find(task.x);
                if (toRemove != null) {
                    tree.remove(toRemove, writer, true);
                } else {
                    tree.print(task.x + " kommt nicht vor", writer);
                }
            }
        }
    }

    /**
     * Asserts desired properties of the specified AVL tree.
     * @param tree The tree to check.
     */
    private static void check(final IntAVLTree tree) {
        assert (tree.isWellFormed()) : "AVL tree is not well-formed!";
        assert (tree.isBalanced()) : "AVL tree is not balanced!";
    }

    private static IntAVLTree constructTree(final Deque<Pair<Integer, Boolean>> construction) throws IOException {
        final IntAVLTree tree = new IntAVLTree();
        while (!construction.isEmpty()) {
            final Pair<Integer, Boolean> operation = construction.poll();
            if (operation.y) {
                tree.add(operation.x, Optional.empty());
            } else {
                final Optional<AVLNode> toRemove = tree.find(operation.x);
                if (toRemove.isPresent()) {
                    tree.remove(toRemove.get(), Optional.empty());
                }
            }
        }
        return tree;
    }

    private static Pair<IntAVLTree, Deque<Pair<Integer, Boolean>>> generateAVLTreeAndTasks(final Parameters options)
    throws IOException {
        return new Pair<IntAVLTree, Deque<Pair<Integer, Boolean>>>(
            TreeAlgorithms.constructTree(TreeAlgorithms.generateConstruction(options)),
            TreeAlgorithms.generateTasks(options)
        );
    }

    private static Deque<Pair<Integer, Boolean>> generateConstruction(final Parameters options) {
        final Random gen = new Random();
        final int length = gen.nextInt(20) + 1;
        final Deque<Pair<Integer, Boolean>> deque = new ArrayDeque<Pair<Integer, Boolean>>();
        final List<Integer> in = new ArrayList<Integer>();
        for (int i = 0; i < length; i++) {
            if (in.isEmpty() || gen.nextInt(3) > 0) {
                final int next = gen.nextInt(Main.NUMBER_LIMIT);
                deque.offer(new Pair<Integer, Boolean>(next, true));
                in.add(next);
            } else {
                deque.offer(new Pair<Integer, Boolean>(in.remove(gen.nextInt(in.size())), false));
            }
        }
        return deque;
    }

    private static Pair<Deque<Pair<Integer, Boolean>>, Deque<Pair<Integer, Boolean>>> generateConstructionAndTasks(
        final Parameters options
    ) {
        return new Pair<Deque<Pair<Integer, Boolean>>, Deque<Pair<Integer, Boolean>>>(
            TreeAlgorithms.generateTasks(options),
            TreeAlgorithms.generateConstruction(options)
        );
    }

    private static Deque<Pair<Integer, Boolean>> generateTasks(final Parameters options) {
        final int length;
        final Random gen = new Random();
        if (options.containsKey(Flag.LENGTH)) {
            length = Integer.parseInt(options.get(Flag.LENGTH));
        } else {
            length = gen.nextInt(16) + 5;
        }
        final Deque<Pair<Integer, Boolean>> deque = new ArrayDeque<Pair<Integer, Boolean>>();
        final List<Integer> in = new ArrayList<Integer>();
        for (int i = 0; i < length; i++) {
            if (
                (options.containsKey(Flag.VARIANT) && options.get(Flag.VARIANT).equals("1"))
                || in.isEmpty()
                || gen.nextInt(3) > 0
            ) {
                final int next = gen.nextInt(Main.NUMBER_LIMIT);
                deque.offer(new Pair<Integer, Boolean>(next, true));
                in.add(next);
            } else {
                deque.offer(new Pair<Integer, Boolean>(in.remove(gen.nextInt(in.size())), false));
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

    private static Pair<IntAVLTree, Deque<Pair<Integer, Boolean>>> parseAVLTreeAndTasks(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        final String line = reader.readLine();
        return new Pair<IntAVLTree, Deque<Pair<Integer, Boolean>>>(
            TreeAlgorithms.constructTree(TreeAlgorithms.parseOrGenerateConstruction(line, options)),
            TreeAlgorithms.parseTasks(line, options)
        );
    }

    private static Pair<Deque<Pair<Integer, Boolean>>, Deque<Pair<Integer, Boolean>>> parseConstructionAndTasks(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        final String[] parts = reader.readLine().split(";");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Input must contain exactly two parts: construction and tasks!");
        }
        return new Pair<Deque<Pair<Integer, Boolean>>, Deque<Pair<Integer, Boolean>>>(
            TreeAlgorithms.parseOperations(parts[0].split(",")),
            TreeAlgorithms.parseOperations(parts[1].split(","))
        );
    }

    private static Deque<Pair<Integer, Boolean>> parseOperations(final String[] operations) {
        final Deque<Pair<Integer, Boolean>> deque = new ArrayDeque<Pair<Integer, Boolean>>();
        for (final String num : operations) {
            if (num.isBlank()) {
                continue;
            }
            final String stripped = num.strip();
            if (stripped.startsWith("~")) {
                deque.offer(new Pair<Integer, Boolean>(Integer.parseInt(stripped.substring(1)), false));
            } else {
                deque.offer(new Pair<Integer, Boolean>(Integer.parseInt(stripped), true));
            }
        }
        return deque;
    }

    private static Deque<Pair<Integer, Boolean>> parseOrGenerateConstruction(
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
            return new ArrayDeque<Pair<Integer, Boolean>>();
        }
        return TreeAlgorithms.parseOperations(nums);
    }

    private static Deque<Pair<Integer, Boolean>> parseOrGenerateTasks(final Parameters options)
    throws IOException {
        return new ParserAndGenerator<Deque<Pair<Integer, Boolean>>>(
            TreeAlgorithms::parseTasks,
            TreeAlgorithms::generateTasks
        ).getResult(options);
    }

    private static Deque<Pair<Integer, Boolean>> parseTasks(final BufferedReader reader, final Parameters options)
    throws IOException {
        final String[] nums = reader.readLine().split(",");
        return TreeAlgorithms.parseOperations(nums);
    }

    private static Deque<Pair<Integer, Boolean>> parseTasks(final String line, final Parameters options) {
        final String[] parts = line.split(";");
        if (parts.length > 2) {
            throw new IllegalArgumentException("Input should include at most 2 parts!");
        }
        if (parts.length == 1) {
            return TreeAlgorithms.parseOperations(parts[0].split(","));
        }
        return TreeAlgorithms.parseOperations(parts[1].split(","));
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
        final Deque<Pair<Integer, Boolean>> tasks,
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
            for (final Pair<Integer, Boolean> task : tasks) {
                if (task.y) {
                    writer.write(LaTeXUtils.ITEM);
                    writer.write(" ");
                    writer.write(String.valueOf(task.x));
                    writer.write(" einf\\\"ugen\\\\");
                } else {
                    writer.write(LaTeXUtils.ITEM);
                    writer.write(" ");
                    writer.write(String.valueOf(task.x));
                    writer.write(" l\\\"oschen\\\\");
                }
                Main.newLine(writer);
            }
            LaTeXUtils.printEnd(LaTeXUtils.ENUMERATE, writer);
        } else {
            final Pair<Integer, Boolean> op = tasks.peek();
            if (tree.isEmpty()) {
                if (op.y) {
                    writer.write("F\\\"ugen Sie den Wert ");
                    writer.write(String.valueOf(op.x));
                    writer.write(" in einen leeren \\emphasize{");
                    writer.write(tree.getName());
                    writer.write("} ein und geben Sie die entstehenden B\\\"aume nach jeder ");
                    writer.write(tree.getOperations());
                    writer.write(" an.\\\\");
                } else {
                    throw new IllegalArgumentException("Deleting a value from an empty tree makes no sense!");
                }
            } else {
                if (op.y) {
                    writer.write("F\\\"ugen Sie den Wert ");
                    writer.write(String.valueOf(op.x));
                    writer.write(" in den folgenden \\emphasize{");
                    writer.write(tree.getName());
                    writer.write("} ein und geben Sie die entstehenden B\\\"aume nach jeder ");
                    writer.write(tree.getOperations());
                    writer.write(" an:\\\\");
                } else {
                    writer.write("L\\\"oschen Sie den Wert ");
                    writer.write(String.valueOf(op.x));
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
