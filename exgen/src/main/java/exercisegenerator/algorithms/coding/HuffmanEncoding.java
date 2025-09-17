package exercisegenerator.algorithms.coding;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.coding.*;

public class HuffmanEncoding implements AlgorithmImplementation<HuffmanProblem, HuffmanSolution> {

    public static final HuffmanEncoding INSTANCE = new HuffmanEncoding();

    private static List<Character> generateAlphabet(final int alphabetSize) {
        final List<Character> biggestAlphabet = new ArrayList<Character>();
        for (int i = 32; i < 127; i++) {
            biggestAlphabet.add(Character.valueOf((char)i));
        }
        if (alphabetSize >= biggestAlphabet.size()) {
            return biggestAlphabet;
        }
        final List<Character> result = new ArrayList<Character>();
        for (int i = 0; i < alphabetSize; i++) {
            result.add(biggestAlphabet.remove(Main.RANDOM.nextInt(biggestAlphabet.size())));
        }
        return result;
    }

    private static String generateSourceText(final Parameters<Flag> options) {
        final int alphabetSize = HuffmanEncoding.parseOrGenerateAlphabetSize(options);
        final int textLength = CodingAlgorithms.parseOrGenerateTextLength(options);
        final List<Character> alphabet = HuffmanEncoding.generateAlphabet(alphabetSize);
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < textLength; i++) {
            result.append(alphabet.get(Main.RANDOM.nextInt(alphabetSize)));
        }
        return result.toString();
    }

    private static int parseOrGenerateAlphabetSize(final Parameters<Flag> options) {
        if (options.containsKey(Flag.DEGREE)) {
            return Integer.parseInt(options.get(Flag.DEGREE));
        }
        return Main.RANDOM.nextInt(6) + 5;
    }

    private static List<Character> parseOrGenerateTargetAlphabet(final Parameters<Flag> options) throws IOException {
        if (options.containsKey(Flag.OPERATIONS)) {
            return options.get(Flag.OPERATIONS).chars().mapToObj(c -> (char)c).toList();
        }
        return CodingAlgorithms.BINARY_ALPHABET;
    }

    private static void printCodeBookForEncoding(
        final Map<Character, String> codeBook,
        final boolean exercise,
        final BufferedWriter writer
    ) throws IOException {
        final boolean big = codeBook.size() > 7;
        writer.write("\\textbf{Codebuch:}");
        if (big) {
            Main.newLine(writer);
            LaTeXUtils.beginMulticols(2, writer);
        } else {
            writer.write("\\\\[2ex]");
            Main.newLine(writer);
        }
        final int contentLength = codeBook.values().stream().mapToInt(String::length).max().getAsInt();
        for (final Pair<Character, String> assignment : CodingAlgorithms.toSortedList(codeBook)) {
            Algorithm.assignment(
                String.format("\\code{`%s'}", LaTeXUtils.escapeForLaTeX(assignment.x)),
                Collections.singletonList(
                    new ItemWithTikZInformation<String>(
                        Optional.of(LaTeXUtils.code(LaTeXUtils.escapeForLaTeX(assignment.y)))
                    )
                ),
                "\\code{`M'}",
                "=",
                contentLength,
                exercise,
                writer
            );
        }
        if (big) {
            LaTeXUtils.endMulticols(writer);
        }
    }

    private HuffmanEncoding() {}

    @Override
    public HuffmanSolution apply(final HuffmanProblem problem) {
        return HuffmanTree.toHuffmanTree(problem.message(), problem.alphabet());
    }

    @Override
    public String commandPrefix() {
        return "ToHuffman";
    }

    @Override
    public HuffmanProblem generateProblem(final Parameters<Flag> options) {
        return new HuffmanProblem(HuffmanEncoding.generateSourceText(options), CodingAlgorithms.BINARY_ALPHABET);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[0];
        return result;
    }

    @Override
    public List<HuffmanProblem> parseProblems(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        return List.of(
            new HuffmanProblem(
                CodingAlgorithms.parseInputText(reader, options),
                HuffmanEncoding.parseOrGenerateTargetAlphabet(options)
            )
        );
    }

    @Override
    public void printAfterSingleProblemInstance(
        final HuffmanProblem problem,
        final HuffmanSolution solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printVerticalProtectedSpace(writer);
        writer.write("Geben Sie zus\\\"atzlich zu dem erstellten Code das erzeugte Codebuch sowie die Liste der ");
        writer.write("Bin\\\"arb\\\"aume nach ihrer Erzeugung und nach jeder Kombination von Bin\\\"arb\\\"aumen ");
        writer.write("an.\\\\[2ex]");
        Main.newLine(writer);
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<HuffmanProblem> problems,
        final List<HuffmanSolution> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Erzeugen Sie den jeweiligen \\emphasize{Huffman-Code} f\\\"ur die folgenden Eingaben und ");
        writer.write("geben Sie zus\\\"atzlich zu dem erstellten Code das jeweils erzeugte Codebuch sowie die Liste ");
        writer.write("der Binärbäume nach ihrer Erzeugung und nach jeder Kombination von Binärbäumen an.\\\\");
    }

    @Override
    public void printBeforeSingleProblemInstance(
        final HuffmanProblem problem,
        final HuffmanSolution solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Erzeugen Sie den \\emphasize{Huffman-Code} f\\\"ur die folgende Eingabe:\\\\");
        Main.newLine(writer);
    }

    @Override
    public void printProblemInstance(
        final HuffmanProblem problem,
        final HuffmanSolution solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Zielalphabet: $\\{");
        writer.write(
            LaTeXUtils.escapeForLaTeX(problem.alphabet().stream().map(String::valueOf).collect(Collectors.joining(",")))
        );
        writer.write("\\}$\\\\");
        Main.newLine(writer);
        writer.write("Eingabetext:\\\\");
        Main.newLine(writer);
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
        writer.write(LaTeXUtils.escapeForLaTeX(problem.message()));
        Main.newLine(writer);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
    }

    @Override
    public void printSolutionInstance(
        final HuffmanProblem problem,
        final HuffmanSolution solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        HuffmanEncoding.printCodeBookForEncoding(solution.code().tree().toCodeBook(), false, writer);
        LaTeXUtils.printVerticalProtectedSpace(writer);
        writer.write("\\textbf{Code:}\\\\");
        Main.newLine(writer);
        writer.write(
            Arrays.stream(LaTeXUtils.escapeForLaTeX(solution.code().message()).split(" "))
                .map(LaTeXUtils::code)
                .collect(Collectors.joining(" "))
        );
        Main.newLine(writer);
        LaTeXUtils.printVerticalProtectedSpace(writer);
        writer.write("\\par\\noindent\\rule[5pt]{\\columnwidth}{1pt}");
        Main.newLine(writer);
        for (final List<HuffmanNode> trees : solution.trees()) {
            LaTeXUtils.printAdjustboxBeginning(writer, "max width=\\columnwidth", "center");
            for (final HuffmanNode root : trees) {
                LaTeXUtils.printTikzBeginning(TikZStyle.TREE, writer);
                new HuffmanTree(Optional.of(root)).toTikZ(writer);
                LaTeXUtils.printTikzEnd(writer);
            }
            LaTeXUtils.printAdjustboxEnd(writer);
            writer.write("\\par\\noindent\\rule[5pt]{\\columnwidth}{1pt}");
            Main.newLine(writer);
        }
        LaTeXUtils.printAdjustboxBeginning(writer, "max width=\\columnwidth", "center");
        LaTeXUtils.printTikzBeginning(TikZStyle.TREE, writer);
        solution.code().tree().toTikZ(writer);
        LaTeXUtils.printTikzEnd(writer);
        LaTeXUtils.printAdjustboxEnd(writer);
        Main.newLine(writer);
    }

    @Override
    public void printSolutionSpace(
        final HuffmanProblem problem,
        final HuffmanSolution solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, writer);
        HuffmanEncoding.printCodeBookForEncoding(solution.code().tree().toCodeBook(), true, writer);
        LaTeXUtils.printVerticalProtectedSpace(writer);
        writer.write("\\textbf{Code:}\\\\");
        Main.newLine(writer);
        LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), options, writer);
    }

}
