package exercisegenerator.algorithms.trees;

import java.io.*;
import java.util.*;

import org.apache.commons.math3.fraction.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.trees.*;

interface SearchTreeAlgorithm extends AlgorithmImplementation<SearchTreeProblem, SearchTreeSteps<Integer>> {

    static ConstructionAndTasks<Integer> generateConstructionAndTasks(
        final Parameters<Flag> options
    ) {
        return new ConstructionAndTasks<Integer>(
            SearchTreeAlgorithm.generateTasks(options),
            SearchTreeAlgorithm.generateConstruction(options)
        );
    }

    static List<ConstructionAndTasks<Integer>> parseConstructionAndTasks(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        final List<ConstructionAndTasks<Integer>> result = new LinkedList<ConstructionAndTasks<Integer>>();
        String line = reader.readLine();
        while (line != null) {
            if (!line.isBlank()) {
                final String[] parts = line.split(";");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Input must contain exactly two parts: construction and tasks!");
                }
                result.add(
                    new ConstructionAndTasks<Integer>(
                        SearchTreeAlgorithm.parseOperations(parts[0].split(",")),
                        SearchTreeAlgorithm.parseOperations(parts[1].split(","))
                    )
                );
            }
            line = reader.readLine();
        }
        return result;
    }

    static Deque<TreeOperation<Integer>> parseOrGenerateConstruction(
        final String line,
        final Parameters<Flag> options
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

    static Deque<TreeOperation<Integer>> parseOrGenerateTasks(final Parameters<Flag> options)
    throws IOException {
        return new ParserAndGenerator<Deque<TreeOperation<Integer>>>(
            SearchTreeAlgorithm::parseTasks,
            SearchTreeAlgorithm::generateTasks
        ).getResult(options);
    }

    private static Deque<TreeOperation<Integer>> generateConstruction(final Parameters<Flag> options) {
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

    private static Deque<TreeOperation<Integer>> generateTasks(final Parameters<Flag> options) {
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

    private static boolean parseFillingDegreeUsage(final String keyValues) {
        final Optional<String> fillingDegreeUsage =
            Arrays.stream(keyValues.split(",")).filter(entry -> entry.startsWith("useFillingDegree=")).findAny();
        if (fillingDegreeUsage.isEmpty()) {
            return true;
        }
        return Boolean.parseBoolean(fillingDegreeUsage.get().split("=")[1]);
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

    private static Deque<TreeOperation<Integer>> parseTasks(final BufferedReader reader, final Parameters<Flag> options)
    throws IOException {
        final String[] nums = reader.readLine().split(",");
        return SearchTreeAlgorithm.parseOperations(nums);
    }

    private static void printOneOfManyProblemInstances(
        final SearchTreeProblem problem,
        final BufferedWriter writer
    ) throws IOException {
        if (problem.tree().isEmpty()) {
            writer.write("leerer Baum\\\\");
        } else {
            writer.write("~\\\\[-2ex]");
            Main.newLine(writer);
            SearchTreeAlgorithm.printTreeAndReturnHorizontalFillingDegree(BigFraction.ZERO, "", problem.tree(), writer);
        }
        Main.newLine(writer);
        Main.newLine(writer);
        if (problem.operations().size() == 1) {
            final TreeOperation<Integer> op = problem.operations().peek();
            if (op.add()) {
                writer.write("F\\\"ugen Sie den Wert ");
                writer.write(String.valueOf(op.value()));
                writer.write(" ein.");
            } else {
                writer.write("L\\\"oschen Sie den Wert ");
                writer.write(String.valueOf(op.value()));
                writer.write(".");
            }
            Main.newLine(writer);
        } else {
            writer.write("Operationen:\\\\");
            Main.newLine(writer);
            LaTeXUtils.printBeginning(LaTeXUtils.ENUMERATE, writer);
            for (final TreeOperation<Integer> op : problem.operations()) {
                writer.write("\\item Den Wert ");
                writer.write(String.valueOf(op.value()));
                writer.write(" ");
                if (op.add()) {
                    writer.write("einf\\\"ugen.");
                } else {
                    writer.write("l\\\"oschen.");
                }
                Main.newLine(writer);
            }
            LaTeXUtils.printBeginning(LaTeXUtils.ENUMERATE, writer);
        }
    }

    private static void printSamePageBeginning(
        final Optional<String> width,
        final String headline,
        final BufferedWriter writer
    ) throws IOException {
        final String widthText = width.orElse(LaTeXUtils.LINE_WIDTH);
        writer.write(String.format("\\begin{minipage}[t]{%s}", widthText));
        Main.newLine(writer);
        if (headline != null && !headline.isBlank()) {
            writer.write(headline);
            writer.write("\\\\[1.2ex]");
            Main.newLine(writer);
        }
        LaTeXUtils.printAdjustboxBeginning(writer, String.format("max width=%s", widthText), "center");
    }

    private static void printSamePageEnd(final BufferedWriter writer) throws IOException {
        LaTeXUtils.printAdjustboxEnd(writer);
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
                operation.add() ?
                    currentTree.addWithSteps(operation.value()) :
                        currentTree.removeWithSteps(operation.value())
            );
            currentTree = result.getLast().tree();
        }
        return result;
    }

    @Override
    default SearchTreeProblem generateProblem(final Parameters<Flag> options) {
        final ConstructionAndTasks<Integer> constructionAndTasks =
            SearchTreeAlgorithm.generateConstructionAndTasks(options);
        final SearchTreeFactory<Integer> factory = this.parseOrGenerateTreeFactory(options);
        return new SearchTreeProblem(
            factory.create(constructionAndTasks.construction()),
            constructionAndTasks.tasks(),
            factory
        );
    }

    SearchTreeFactory<Integer> parseOrGenerateTreeFactory(final Parameters<Flag> options);

    @Override
    default List<SearchTreeProblem> parseProblems(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        final List<ConstructionAndTasks<Integer>> constructionAndTasks =
            SearchTreeAlgorithm.parseConstructionAndTasks(reader, options);
        final SearchTreeFactory<Integer> factory = this.parseOrGenerateTreeFactory(options);
        return constructionAndTasks.stream()
            .map(cat -> new SearchTreeProblem(factory.create(cat.construction()), cat.tasks(), factory))
            .toList();
    }

    @Override
    default void printAfterEachOfMultipleProblemInstances(
        final SearchTreeProblem problem,
        final SearchTreeSteps<Integer> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {}

    @Override
    default void printAfterMultipleProblemInstances(
        final List<SearchTreeProblem> problems,
        final List<SearchTreeSteps<Integer>> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {}

    @Override
    default void printBeforeEachOfMultipleProblemInstances(
        final int number,
        final SearchTreeProblem problem,
        final SearchTreeSteps<Integer> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {}

    @Override
    default void printBeforeMultipleProblemInstances(
        final List<SearchTreeProblem> problems,
        final List<SearchTreeSteps<Integer>> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        final SearchTree<Integer> tree = problems.getFirst().tree();
        writer.write("F\\\"uhren Sie jeweils beginnend mit den folgenden Instanzen eines \\emphasize{");
        writer.write(tree.getName(true));
        writer.write("} die jeweils darunter aufgef\\\"uhrten Operationen aus und ");
        writer.write("geben Sie die dabei jeweils entstehenden B\\\"aume nach jeder ");
        Main.newLine(writer);
        writer.write(tree.getOperations());
        Main.newLine(writer);
        writer.write("an.\\\\");
        Main.newLine(writer);
        if (Main.embeddedExam(options)) {
            int number = 1;
            for (final SearchTreeProblem problem : problems) {
                writer.write("\\newcommand{\\exercise");
                writer.write(this.commandPrefix());
                writer.write(LaTeXUtils.toRomanNumeral(number));
                writer.write("}{%");
                Main.newLine(writer);
                SearchTreeAlgorithm.printOneOfManyProblemInstances(problem, writer);
                writer.write("}");
                Main.newLine(writer);
                number++;
            }
        } else {
            LaTeXUtils.printBeginning(LaTeXUtils.ENUMERATE, writer);
            for (final SearchTreeProblem problem : problems) {
                writer.write("\\item ");
                SearchTreeAlgorithm.printOneOfManyProblemInstances(problem, writer);
            }
            LaTeXUtils.printEnd(LaTeXUtils.ENUMERATE, writer);
        }

    }

    @Override
    default void printBeforeSingleProblemInstance(
        final SearchTreeProblem problem,
        final SearchTreeSteps<Integer> solution,
        final Parameters<Flag> options,
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
                if (task.add()) {
                    writer.write(LaTeXUtils.ITEM);
                    writer.write(" ");
                    writer.write(String.valueOf(task.value()));
                    writer.write(" einf\\\"ugen\\\\");
                } else {
                    writer.write(LaTeXUtils.ITEM);
                    writer.write(" ");
                    writer.write(String.valueOf(task.value()));
                    writer.write(" l\\\"oschen\\\\");
                }
                Main.newLine(writer);
            }
            LaTeXUtils.printEnd(LaTeXUtils.ENUMERATE, writer);
        } else {
            final TreeOperation<Integer> op = operations.peek();
            if (tree.isEmpty()) {
                if (op.add()) {
                    writer.write("F\\\"ugen Sie den Wert ");
                    writer.write(String.valueOf(op.value()));
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
                if (op.add()) {
                    writer.write("F\\\"ugen Sie den Wert ");
                    writer.write(String.valueOf(op.value()));
                    writer.write(" in den folgenden \\emphasize{");
                    writer.write(tree.getName());
                    writer.write("} ein und geben Sie die dabei jeweils entstehenden B\\\"aume nach jeder ");
                    Main.newLine(writer);
                    writer.write(tree.getOperations());
                    Main.newLine(writer);
                    writer.write("an:\\\\");
                } else {
                    writer.write("L\\\"oschen Sie den Wert ");
                    writer.write(String.valueOf(op.value()));
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
    }

    @Override
    default void printProblemInstance(
        final SearchTreeProblem problem,
        final SearchTreeSteps<Integer> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {}

    @Override
    default void printSolutionInstance(
        final SearchTreeProblem problem,
        final SearchTreeSteps<Integer> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        int stepNumber = 1;
        final String keyValues = options.getOrDefault(Flag.KEYVALUE, "");
        final int[] pagebreakCounters = LaTeXUtils.parsePagebreakCountersForSolution(keyValues);
        final boolean useFillingDegree = SearchTreeAlgorithm.parseFillingDegreeUsage(keyValues);
        int trees = 0;
        int counterIndex = 0;
        BigFraction horizontalFillingDegree = BigFraction.ZERO;
        for (final SearchTreeAndStep<Integer> step : solution) {
            if (counterIndex < pagebreakCounters.length && trees >= pagebreakCounters[counterIndex]) {
                writer.write("\\newpage");
                Main.newLine(writer);
                Main.newLine(writer);
                trees = 0;
                counterIndex++;
            }
            horizontalFillingDegree =
                SearchTreeAlgorithm.printTreeAndReturnHorizontalFillingDegree(
                    useFillingDegree ? horizontalFillingDegree : BigFraction.TWO,
                    String.format("Schritt %d: %s", stepNumber, step.step().toLaTeX()),
                    step.tree(),
                    writer
                );
            stepNumber++;
            trees++;
        }
    }

    @Override
    default void printSolutionSpace(
        final SearchTreeProblem problem,
        final SearchTreeSteps<Integer> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {}

    @Override
    default void printStartOfMultipleProblemInstances(
        final List<SearchTreeProblem> problems,
        final List<SearchTreeSteps<Integer>> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {}

}
