package exercisegenerator.algorithms.coding;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.coding.*;

public class HuffmanEncoding implements AlgorithmImplementation {

    public static final HuffmanEncoding INSTANCE = new HuffmanEncoding();

    public static Pair<HuffmanTree, String> encodeHuffman(
        final String sourceText,
        final List<Character> targetAlphabet
    ) {
        final HuffmanTree tree = new HuffmanTree(sourceText, targetAlphabet);
        return new Pair<HuffmanTree, String>(tree, tree.toEncoder().encode(sourceText));
    }

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

    private static String generateSourceText(final Parameters options) {
        final int alphabetSize = HuffmanEncoding.parseOrGenerateAlphabetSize(options);
        final int textLength = CodingAlgorithms.parseOrGenerateTextLength(options);
        final List<Character> alphabet = HuffmanEncoding.generateAlphabet(alphabetSize);
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < textLength; i++) {
            result.append(alphabet.get(Main.RANDOM.nextInt(alphabetSize)));
        }
        return result.toString();
    }

    private static int parseOrGenerateAlphabetSize(final Parameters options) {
        if (options.containsKey(Flag.DEGREE)) {
            return Integer.parseInt(options.get(Flag.DEGREE));
        }
        return Main.RANDOM.nextInt(6) + 5;
    }

    private static String parseOrGenerateSourceText(final Parameters options) throws IOException {
        return new ParserAndGenerator<String>(
            CodingAlgorithms::parseInputText,
            HuffmanEncoding::generateSourceText
        ).getResult(options);
    }

    private static List<Character> parseOrGenerateTargetAlphabet(final Parameters options) throws IOException {
        if (options.containsKey(Flag.OPERATIONS)) {
            return options.get(Flag.OPERATIONS).chars().mapToObj(c -> (char)c).toList();
        }
        return CodingAlgorithms.BINARY_ALPHABET;
    }

    private static void printCode(
        final String code,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) throws IOException {
        exerciseWriter.write("\\textbf{Code:}\\\\");
        Main.newLine(exerciseWriter);
        solutionWriter.write("\\textbf{Code:}\\\\");
        Main.newLine(solutionWriter);
        solutionWriter.write(
            Arrays.stream(LaTeXUtils.escapeForLaTeX(code).split(" "))
                .map(LaTeXUtils::code)
                .collect(Collectors.joining(" "))
        );
        Main.newLine(solutionWriter);
    }

    private static void printCodeBookForEncoding(
        final Map<Character, String> codeBook,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) throws IOException {
        final boolean big = codeBook.size() > 7;
        exerciseWriter.write("\\textbf{Codebuch:}");
        solutionWriter.write("\\textbf{Codebuch:}");
        if (big) {
            Main.newLine(exerciseWriter);
            Main.newLine(solutionWriter);
            LaTeXUtils.beginMulticols(2, exerciseWriter);
            LaTeXUtils.beginMulticols(2, solutionWriter);
        } else {
            exerciseWriter.write("\\\\[2ex]");
            solutionWriter.write("\\\\[2ex]");
            Main.newLine(exerciseWriter);
            Main.newLine(solutionWriter);
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
                exerciseWriter,
                solutionWriter
            );
        }
        if (big) {
            LaTeXUtils.endMulticols(exerciseWriter);
            LaTeXUtils.endMulticols(solutionWriter);
        }
    }

    private static void printExerciseAndSolutionForHuffmanEncoding(
        final String sourceText,
        final List<Character> targetAlphabet,
        final Pair<HuffmanTree, String> result,
        final Parameters options,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) throws IOException {
        exerciseWriter.write("Erzeugen Sie den \\emphasize{Huffman-Code} f\\\"ur das Zielalphabet $\\{");
        exerciseWriter.write(
            LaTeXUtils.escapeForLaTeX(targetAlphabet.stream().map(String::valueOf).collect(Collectors.joining(",")))
        );
        exerciseWriter.write("\\}$ und den folgenden Eingabetext:\\\\");
        Main.newLine(exerciseWriter);
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, exerciseWriter);
        exerciseWriter.write(LaTeXUtils.escapeForLaTeX(sourceText));
        Main.newLine(exerciseWriter);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, exerciseWriter);
        LaTeXUtils.printVerticalProtectedSpace(exerciseWriter);
        exerciseWriter.write("Geben Sie zus\\\"atzlich zu dem erstellten Code das erzeugte Codebuch an.\\\\[2ex]");
        Main.newLine(exerciseWriter);
        LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, exerciseWriter);
        HuffmanEncoding.printCodeBookForEncoding(result.x.toCodeBook(), exerciseWriter, solutionWriter);
        LaTeXUtils.printVerticalProtectedSpace(exerciseWriter);
        LaTeXUtils.printVerticalProtectedSpace(solutionWriter);
        HuffmanEncoding.printCode(result.y, exerciseWriter, solutionWriter);
        LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), options, exerciseWriter);
        LaTeXUtils.printVerticalProtectedSpace(solutionWriter);
        solutionWriter.write("\\resizebox{\\textwidth}{!}{");
        Main.newLine(solutionWriter);
        LaTeXUtils.printTikzBeginning(TikZStyle.TREE, solutionWriter);
        result.x.toTikZ(solutionWriter);
        LaTeXUtils.printTikzEnd(solutionWriter);
        solutionWriter.write("}");
        Main.newLine(solutionWriter);
        Main.newLine(solutionWriter);
    }

    private HuffmanEncoding() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final String sourceText = HuffmanEncoding.parseOrGenerateSourceText(input.options);
        final List<Character> targetAlphabet = HuffmanEncoding.parseOrGenerateTargetAlphabet(input.options);
        final Pair<HuffmanTree, String> result = HuffmanEncoding.encodeHuffman(sourceText, targetAlphabet);
        HuffmanEncoding.printExerciseAndSolutionForHuffmanEncoding(
            sourceText,
            targetAlphabet,
            result,
            input.options,
            input.exerciseWriter,
            input.solutionWriter
        );
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[0];
        return result;
    }

}
