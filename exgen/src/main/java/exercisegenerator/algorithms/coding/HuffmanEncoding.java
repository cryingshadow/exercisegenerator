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

public class HuffmanEncoding implements AlgorithmImplementation<HuffmanProblem, HuffmanCode> {

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

    private static String parseOrGenerateSourceText(final Parameters<Flag> options) throws IOException {
        return new ParserAndGenerator<String>(
            CodingAlgorithms::parseInputText,
            HuffmanEncoding::generateSourceText
        ).getResult(options);
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
    public HuffmanCode apply(final HuffmanProblem problem) {
        final HuffmanTree tree = new HuffmanTree(problem.message(), problem.alphabet());
        return new HuffmanCode(tree.toEncoder().encode(problem.message()), tree);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[0];
        return result;
    }

    @Override
    public HuffmanProblem parseOrGenerateProblem(final Parameters<Flag> options) throws IOException {
        return new HuffmanProblem(
            HuffmanEncoding.parseOrGenerateSourceText(options),
            HuffmanEncoding.parseOrGenerateTargetAlphabet(options)
        );
    }

    @Override
    public void printExercise(
        final HuffmanProblem problem,
        final HuffmanCode solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Erzeugen Sie den \\emphasize{Huffman-Code} f\\\"ur das Zielalphabet $\\{");
        writer.write(
            LaTeXUtils.escapeForLaTeX(problem.alphabet().stream().map(String::valueOf).collect(Collectors.joining(",")))
        );
        writer.write("\\}$ und den folgenden Eingabetext:\\\\");
        Main.newLine(writer);
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
        writer.write(LaTeXUtils.escapeForLaTeX(problem.message()));
        Main.newLine(writer);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
        LaTeXUtils.printVerticalProtectedSpace(writer);
        writer.write("Geben Sie zus\\\"atzlich zu dem erstellten Code das erzeugte Codebuch an.\\\\[2ex]");
        Main.newLine(writer);
        LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, writer);
        HuffmanEncoding.printCodeBookForEncoding(solution.tree().toCodeBook(), true, writer);
        LaTeXUtils.printVerticalProtectedSpace(writer);
        writer.write("\\textbf{Code:}\\\\");
        Main.newLine(writer);
        LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), options, writer);
    }

    @Override
    public void printSolution(
        final HuffmanProblem problem,
        final HuffmanCode solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        HuffmanEncoding.printCodeBookForEncoding(solution.tree().toCodeBook(), false, writer);
        LaTeXUtils.printVerticalProtectedSpace(writer);
        writer.write("\\textbf{Code:}\\\\");
        Main.newLine(writer);
        writer.write(
            Arrays.stream(LaTeXUtils.escapeForLaTeX(solution.message()).split(" "))
                .map(LaTeXUtils::code)
                .collect(Collectors.joining(" "))
        );
        Main.newLine(writer);
        LaTeXUtils.printVerticalProtectedSpace(writer);
        writer.write("\\resizebox{\\textwidth}{!}{");
        Main.newLine(writer);
        LaTeXUtils.printTikzBeginning(TikZStyle.TREE, writer);
        solution.tree().toTikZ(writer);
        LaTeXUtils.printTikzEnd(writer);
        writer.write("}");
        Main.newLine(writer);
        Main.newLine(writer);
    }

}
