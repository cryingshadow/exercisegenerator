package exercisegenerator.algorithms.trees;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.trees.*;

public class RedBlackTreeAlgorithm implements AlgorithmImplementation {

    public static final RedBlackTreeAlgorithm INSTANCE = new RedBlackTreeAlgorithm();

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
        final Deque<TreeOperation<Integer>> tasks,
        final Deque<TreeOperation<Integer>> construction,
        final BufferedWriter writer,
        final Optional<BufferedWriter> optionalWriterSpace
    ) throws IOException {
        if (tasks.isEmpty()) {
            return;
        }
        while (!construction.isEmpty()) {
            final TreeOperation<Integer> operation = construction.poll();
            if (operation.add) {
                tree.rbInsert(operation.value, writer, false);
            } else {
                final RBNode toRemove = tree.find(operation.value);
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
                for (final TreeOperation<Integer> task : tasks) {
                    if (task.add) {
                        writerSpace.write(LaTeXUtils.ITEM + " " + task.value + " einf\\\"ugen\\\\");
                    } else {
                        writerSpace.write(LaTeXUtils.ITEM + " " + task.value + " l\\\"oschen\\\\");
                    }
                    Main.newLine(writerSpace);
                }
                LaTeXUtils.printEnd(LaTeXUtils.ENUMERATE, writerSpace);
            } else {
                final TreeOperation<Integer> task = tasks.peek();
                if (tree.isEmpty()) {
                    if (task.add) {
                        writerSpace.write("F\\\"ugen Sie den Wert " + task.value);
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
                    if (task.add) {
                        writerSpace.write("F\\\"ugen Sie den Wert " + task.value);
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
                        writerSpace.write("L\\\"oschen Sie den Wert " + task.value);
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
            final TreeOperation<Integer> task = tasks.poll();
            if (task.add) {
                tree.rbInsert(task.value, writer, true);
            } else {
                final RBNode toRemove = tree.find(task.value);
                if (toRemove != null) {
                    tree.remove(toRemove, writer, true);
                } else {
                    tree.print(task.value + " kommt nicht vor", writer);
                }
            }
        }
    }

    private RedBlackTreeAlgorithm() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final Pair<Deque<TreeOperation<Integer>>, Deque<TreeOperation<Integer>>> constructionAndTasks =
            new ParserAndGenerator<Pair<Deque<TreeOperation<Integer>>, Deque<TreeOperation<Integer>>>>(
                TreeAlgorithms::parseConstructionAndTasks,
                TreeAlgorithms::generateConstructionAndTasks
            ).getResult(input.options);
        RedBlackTreeAlgorithm.rbtree(
            new IntRBTree(),
            constructionAndTasks.y,
            constructionAndTasks.x,
            input.solutionWriter,
            Algorithm.getOptionalSpaceWriter(input)
        );
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

}
