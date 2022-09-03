package exercisegenerator.algorithms;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;

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
        TreeAlgorithms.avltree(
            new IntAVLTree(),
            TreeAlgorithms.parseOrGenerateTreeValues(input.options),
            TreeAlgorithms.parseOrGenerateOperations(input.options),
            input.solutionWriter,
            Algorithm.getOptionalSpaceWriter(input)
        );
    }

    /**
     * Performs the operations specified by <code>construction</code> and <code>ops</code> on the specified AVL-tree and
     * prints the results to the specified writer. The <code>construction</code> operations are not displayed.
     * @param tree The AVL-tree.
     * @param ops The operations.
     * @param construction The operations used to construct the start structure.
     * @param writer The writer for the solution.
     * @param writerSpace The writer for the tree to start with (the one reached after the <code>construction</code>
     *                    operations). May be null if this tree should not be displayed separately.
     * @throws IOException If some error occurs during output.
     */
    public static void avltree(
        final IntAVLTree tree,
        final Deque<Pair<Integer, Boolean>> ops,
        final Deque<Pair<Integer, Boolean>> construction,
        final BufferedWriter writer,
        final Optional<BufferedWriter> optionalWriterSpace
    ) throws IOException {
        if (TreeAlgorithms.USE_ASSERTIONS) {
            TreeAlgorithms.check(tree);
        }
        if (ops.isEmpty()) {
            return;
        }
        while (!construction.isEmpty()) {
            final Pair<Integer, Boolean> operation = construction.poll();
            if (operation.y) {
                tree.add(operation.x, null);
            } else {
                final AVLNode toRemove = tree.find(operation.x);
                if (toRemove != null) {
                    tree.remove(toRemove, null);
                }
            }
            if (TreeAlgorithms.USE_ASSERTIONS) {
                TreeAlgorithms.check(tree);
            }
        }
        if (TreeAlgorithms.DEBUG_OUTPUT) {
            System.out.println(tree.root == null ? "-1" : tree.root.printHeights());
        }
        if (optionalWriterSpace.isPresent()) {
            final BufferedWriter writerSpace = optionalWriterSpace.get();
            if (ops.size() > 1) {
                if (tree.isEmpty()) {
                    writerSpace.write("F\\\"uhren Sie folgenden Operationen beginnend mit einem anfangs leeren ");
                    writerSpace.write("\\emphasize{AVL-Baum} aus und geben Sie die entstehenden B\\\"aume nach jeder ");
                    writerSpace.write("\\emphasize{Einf\\\"uge-} und \\emphasize{L\\\"oschoperation} sowie jeder ");
                    writerSpace.write("\\emphasize{Rotation} an:\\\\\\\\");
                    Main.newLine(writerSpace);
                } else {
                    writerSpace.write("Betrachten Sie den folgenden \\emphasize{AVL-Baum}:\\\\[2ex]");
                    Main.newLine(writerSpace);
                    Main.newLine(writerSpace);
                    tree.print("", writerSpace);
                    Main.newLine(writerSpace);
                    Main.newLine(writerSpace);
                    writerSpace.write("\\vspace*{1ex}");
                    Main.newLine(writerSpace);
                    writerSpace.write("F\\\"uhren Sie beginnend mit diesem Baum die folgenden Operationen aus und ");
                    writerSpace.write("geben Sie die entstehenden B\\\"aume nach jeder \\emphasize{Einf\\\"uge-} und ");
                    writerSpace.write("\\emphasize{L\\\"oschoperation} sowie jeder \\emphasize{Rotation} an:\\\\\\\\");
                    Main.newLine(writerSpace);
                }
                TikZUtils.printBeginning(TikZUtils.ENUMERATE, writerSpace);
                for (final Pair<Integer, Boolean> op : ops) {
                    if (op.y) {
                        writerSpace.write(TikZUtils.ITEM + " " + op.x + " einf\\\"ugen\\\\");
                    } else {
                        writerSpace.write(TikZUtils.ITEM + " " + op.x + " l\\\"oschen\\\\");
                    }
                    Main.newLine(writerSpace);
                }
                TikZUtils.printEnd(TikZUtils.ENUMERATE, writerSpace);
            } else {
                final Pair<Integer, Boolean> op = ops.peek();
                if (tree.isEmpty()) {
                    if (op.y) {
                        writerSpace.write("F\\\"ugen Sie den Wert " + op.x);
                        writerSpace.write(" in einen leeren \\emphasize{AVL-Baum} ein und geben Sie die entstehenden ");
                        writerSpace.write("B\\\"aume nach jeder \\emphasize{Einf\\\"ugeoperation} sowie jeder ");
                        writerSpace.write("\\emphasize{Rotation} an.");
                    } else {
                        // this case is nonsense
                        return;
                    }
                } else {
                    if (op.y) {
                        writerSpace.write("F\\\"ugen Sie den Wert " + op.x);
                        writerSpace.write(" in den folgenden \\emphasize{AVL-Baum} ein und geben Sie die entstehenden");
                        writerSpace.write(" B\\\"aume nach jeder \\emphasize{Einf\\\"ugeoperation} sowie jeder ");
                        writerSpace.write("\\emphasize{Rotation} an:\\\\[2ex]");
                    } else {
                        writerSpace.write("L\\\"oschen Sie den Wert " + op.x);
                        writerSpace.write(" aus dem folgenden \\emphasize{AVL-Baum} und geben Sie die entstehenden ");
                        writerSpace.write("B\\\"aume nach jeder \\emphasize{L\\\"oschoperation} sowie jeder ");
                        writerSpace.write("\\emphasize{Rotation} an:\\\\[2ex]");
                    }
                    Main.newLine(writerSpace);
                    Main.newLine(writerSpace);
                    tree.print("", writerSpace);
                    Main.newLine(writerSpace);
                }
            }
        }
        tree.resetStepCounter();
        while (!ops.isEmpty()) {
            final Pair<Integer, Boolean> operation = ops.poll();
            if (operation.y) {
                if (TreeAlgorithms.DEBUG_OUTPUT) {
                    System.out.println("insert " + operation.x + " in:");
                    System.out.println(tree.toString());
                }
                tree.add(operation.x, writer);
            } else {
                if (TreeAlgorithms.DEBUG_OUTPUT) {
                    System.out.println("remove " + operation.x + " in:");
                    System.out.println(tree.toString());
                }
                final AVLNode toRemove = tree.find(operation.x);
                if (toRemove != null) {
                    tree.remove(toRemove, writer);
                } else {
                    tree.print(operation.x + " kommt nicht vor", writer);
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

    public static void btree(final AlgorithmInput input) throws NumberFormatException, IOException {
        TreeAlgorithms.btree(
            new IntBTree(
                input.options.containsKey(Flag.DEGREE) ? Integer.parseInt(input.options.get(Flag.DEGREE)) : 2
            ),
            TreeAlgorithms.parseOrGenerateTreeValues(input.options),
            TreeAlgorithms.parseOrGenerateOperations(input.options),
            input.solutionWriter,
            Algorithm.getOptionalSpaceWriter(input)
        );
    }

    /**
     * Performs the operations specified by <code>construction</code> and <code>ops</code> on the specified B-tree and
     * prints the results to the specified writer. The <code>construction</code> operations are not displayed.
     * @param tree The B-tree.
     * @param ops The operations.
     * @param construction The operations used to construct the start structure.
     * @param writer The writer for the solution.
     * @param writerSpace The writer for the tree to start with (the one reached after the <code>construction</code>
     *                    operations). May be null if this tree should not be displayed separately.
     * @throws IOException If some error occurs during output.
     */
    public static void btree(
        final IntBTree tree,
        final Deque<Pair<Integer, Boolean>> ops,
        final Deque<Pair<Integer, Boolean>> construction,
        final BufferedWriter writer,
        final Optional<BufferedWriter> optionalWriterSpace
    ) throws IOException {
        if (ops.isEmpty()) {
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
            if (ops.size() > 1) {
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
                    TikZUtils.printBeginning(TikZUtils.CENTER, writerSpace);
                    TikZUtils.printTikzBeginning(TikZStyle.BTREE, writerSpace);
                    TikZUtils.printBTree(tree, writerSpace);
                    TikZUtils.printTikzEnd(writerSpace);
                    TikZUtils.printEnd(TikZUtils.CENTER, writerSpace);
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
                TikZUtils.printBeginning(TikZUtils.ENUMERATE, writerSpace);
                for (final Pair<Integer, Boolean> op : ops) {
                    if (op.y) {
                        writerSpace.write(TikZUtils.ITEM + " " + op.x + " einf\\\"ugen\\\\");
                    } else {
                        writerSpace.write(TikZUtils.ITEM + " " + op.x + " l\\\"oschen\\\\");
                    }
                    Main.newLine(writerSpace);
                }
                TikZUtils.printEnd(TikZUtils.ENUMERATE, writerSpace);
            } else {
                final Pair<Integer, Boolean> op = ops.peek();
                if (tree.isEmpty()) {
                    if (op.y) {
                        writerSpace.write(
                            "F\\\"ugen Sie den Wert "
                            + op.x
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
                    if (op.y) {
                        writerSpace.write(
                            "F\\\"ugen Sie den Wert "
                            + op.x
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
                            + op.x
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
                    TikZUtils.printBeginning(TikZUtils.CENTER, writerSpace);
                    TikZUtils.printTikzBeginning(TikZStyle.BTREE, writerSpace);
                    TikZUtils.printBTree(tree, writerSpace);
                    TikZUtils.printTikzEnd(writerSpace);
                    TikZUtils.printEnd(TikZUtils.CENTER, writerSpace);
                    Main.newLine(writerSpace);
                }
            }
        }
        int step = 1;
        while (!ops.isEmpty()) {
            final Pair<Integer, Boolean> operation = ops.poll();
            if (operation.y) {
                tree.add(operation.x);
            } else {
                tree.remove(operation.x);
            }
            TikZUtils.printSamePageBeginning(step++, operation, writer);
            TikZUtils.printTikzBeginning(TikZStyle.BTREE, writer);
            TikZUtils.printBTree(tree, writer);
            TikZUtils.printTikzEnd(writer);
            TikZUtils.printSamePageEnd(writer);
        }
    }

    public static void rbtree(final AlgorithmInput input) throws IOException {
        TreeAlgorithms.rbtree(
            new IntRBTree(),
            TreeAlgorithms.parseOrGenerateTreeValues(input.options),
            TreeAlgorithms.parseOrGenerateOperations(input.options),
            input.solutionWriter,
            Algorithm.getOptionalSpaceWriter(input)
        );
    }

    /**
     * Performs the operations specified by <code>construction</code> and <code>ops</code> on the specified RB-tree and
     * prints the results to the specified writer. The <code>construction</code> operations are not displayed.
     * @param tree The RB-tree.
     * @param ops The operations.
     * @param construction The operations used to construct the start structure.
     * @param writer The writer for the solution.
     * @param writerSpace The writer for the tree to start with (the one reached after the <code>construction</code>
     *                    operations). May be null if this tree should not be displayed separately.
     * @throws IOException If some error occurs during output.
     */
    public static void rbtree(
        final IntRBTree tree,
        final Deque<Pair<Integer, Boolean>> ops,
        final Deque<Pair<Integer, Boolean>> construction,
        final BufferedWriter writer,
        final Optional<BufferedWriter> optionalWriterSpace
    ) throws IOException {
        if (ops.isEmpty()) {
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
            if (ops.size() > 1) {
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
                TikZUtils.printBeginning(TikZUtils.ENUMERATE, writerSpace);
                for (final Pair<Integer, Boolean> op : ops) {
                    if (op.y) {
                        writerSpace.write(TikZUtils.ITEM + " " + op.x + " einf\\\"ugen\\\\");
                    } else {
                        writerSpace.write(TikZUtils.ITEM + " " + op.x + " l\\\"oschen\\\\");
                    }
                    Main.newLine(writerSpace);
                }
                TikZUtils.printEnd(TikZUtils.ENUMERATE, writerSpace);
            } else {
                final Pair<Integer, Boolean> op = ops.peek();
                if (tree.isEmpty()) {
                    if (op.y) {
                        writerSpace.write("F\\\"ugen Sie den Wert " + op.x);
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
                    if (op.y) {
                        writerSpace.write("F\\\"ugen Sie den Wert " + op.x);
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
                        writerSpace.write("L\\\"oschen Sie den Wert " + op.x);
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
        while (!ops.isEmpty()) {
            final Pair<Integer, Boolean> operation = ops.poll();
            if (operation.y) {
                tree.rbInsert(operation.x, writer, true);
            } else {
                final RBNode toRemove = tree.find(operation.x);
                if (toRemove != null) {
                    tree.remove(toRemove, writer, true);
                } else {
                    tree.print(operation.x + " kommt nicht vor", writer);
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

    /**
     * @param options The program arguments.
     * @return The operations specified in the operation file or null if no such file is specified in the program
     *         arguments.
     */
    private static Deque<Pair<Integer, Boolean>> parseOrGenerateOperations(final Map<Flag, String> options) {
        if (!options.containsKey(Flag.OPERATIONS)) {
            if (Main.STUDENT_MODE) {
                final Random gen = new Random();
                final int length = gen.nextInt(20);
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
            return new ArrayDeque<Pair<Integer, Boolean>>();
        }
        String[] nums = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(options.get(Flag.OPERATIONS)))) {
            nums = reader.readLine().split(",");
        } catch (final IOException e) {
            e.printStackTrace();
            return new ArrayDeque<Pair<Integer, Boolean>>();
        }
        final Deque<Pair<Integer, Boolean>> deque = new ArrayDeque<Pair<Integer, Boolean>>();
        for (final String num : nums) {
            final String trimmed = num.trim();
            if (trimmed.startsWith("~")) {
                deque.offer(new Pair<Integer, Boolean>(Integer.parseInt(trimmed.substring(1)), false));
            } else {
                deque.offer(new Pair<Integer, Boolean>(Integer.parseInt(trimmed), true));
            }
        }
        return deque;
    }

    private static Deque<Pair<Integer, Boolean>> parseOrGenerateTreeValues(final Map<Flag, String> options) {
        final String[] nums;
        if (options.containsKey(Flag.SOURCE)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(options.get(Flag.SOURCE)))) {
                nums = reader.readLine().split(",");
            } catch (final IOException e) {
                e.printStackTrace();
                return null;
            }
        } else if (Main.STUDENT_MODE) {
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
        } else {
            nums = options.get(Flag.INPUT).split(",");
        }
        final Deque<Pair<Integer, Boolean>> deque = new ArrayDeque<Pair<Integer, Boolean>>();
        for (final String num : nums) {
            final String trimmed = num.trim();
            if (trimmed.startsWith("~")) {
                deque.offer(new Pair<Integer, Boolean>(Integer.parseInt(trimmed.substring(1)), false));
            } else {
                deque.offer(new Pair<Integer, Boolean>(Integer.parseInt(trimmed), true));
            }
        }
        return deque;
    }

}
