package exercisegenerator.algorithms.coding;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.coding.*;

public class HuffmanDecoding implements AlgorithmImplementation {

    public static final HuffmanDecoding INSTANCE = new HuffmanDecoding();

    private static final String CODE_BOOK_FORMAT_ERROR_MESSAGE =
        "The specified code book does not match the expected format (entries of the form 'S':\"C\" for a symbol S and a code C, separated by commas)!";

    public static String decodeHuffman(final String targetText, final HuffmanTree tree) {
        return tree.decode(targetText);
    }

    private static String generateTargetText(final Map<Character, String> codeBook, final Parameters options) {
        final Random gen = new Random();
        final int length = CodingAlgorithms.parseOrGenerateTextLength(options, gen);
        final StringBuilder result = new StringBuilder();
        final List<String> samples = new ArrayList<String>(codeBook.values());
        for (int i = 0; i < length; i++) {
            result.append(samples.get(gen.nextInt(samples.size())));
        }
        return result.toString();
    }

    private static Map<Character, String> parseCodeBook(final Parameters options) {
        final String input = options.get(Flag.OPERATIONS);
        final Pattern pattern = Pattern.compile("'.':\"[^\"]+\"");
        if (!input.matches("'.':\"[^\"]+\"(,'.':\"[^\"]+\")*")) {
            throw new IllegalArgumentException(HuffmanDecoding.CODE_BOOK_FORMAT_ERROR_MESSAGE);
        }
        return pattern.matcher(input).results()
            .map(match -> {
                final String assignment = input.substring(match.start(), match.end());
                return new Pair<Character, String>(
                    assignment.charAt(1),
                    assignment.substring(5, assignment.length() - 1)
                );
            }).collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    private static String parseOrGenerateTargetText(
        final Map<Character, String> codeBook,
        final Parameters options
    ) throws IOException {
        return new ParserAndGenerator<String>(
            CodingAlgorithms::parseInputText,
            flags -> HuffmanDecoding.generateTargetText(codeBook, flags)
        ).getResult(options);
    }

    private static void printCodeBookForDecoding(final Map<Character, String> codeBook, final BufferedWriter writer)
    throws IOException {
        writer.write("\\textbf{Codebuch:}");
        Main.newLine(writer);
        LaTeXUtils.printBeginning("align*", writer);
        for (final Pair<Character, String> assignment : CodingAlgorithms.toSortedList(codeBook)) {
            writer.write(
                String.format(
                    "\\code{`%s'} &= \\code{\\textquotedbl{}%s\\textquotedbl{}}\\\\",
                    LaTeXUtils.escapeForLaTeX(assignment.x),
                    LaTeXUtils.escapeForLaTeX(assignment.y)
                )
            );
            Main.newLine(writer);
        }
        LaTeXUtils.printEnd("align*", writer);
    }

    private static void printExerciseAndSolutionForHuffmanDecoding(
        final String targetText,
        final Map<Character, String> codeBook,
        final String result,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) throws IOException {
        exerciseWriter.write(
            "Erzeugen Sie den Quelltext aus dem nachfolgenden \\emphasize{Huffman-Code} mit dem angegebenen Codebuch:\\\\[2ex]"
        );
        Main.newLine(exerciseWriter);
        exerciseWriter.write(LaTeXUtils.codeseq(LaTeXUtils.escapeForLaTeX(targetText)));
        Main.newLine(exerciseWriter);
        LaTeXUtils.printVerticalProtectedSpace(exerciseWriter);
        HuffmanDecoding.printCodeBookForDecoding(codeBook, exerciseWriter);
        LaTeXUtils.printVerticalProtectedSpace("-3ex", exerciseWriter);
        exerciseWriter.write("\\textbf{Quelltext:}\\\\[2ex]");
        Main.newLine(exerciseWriter);
        Main.newLine(exerciseWriter);

        solutionWriter.write(LaTeXUtils.code(result));
        Main.newLine(solutionWriter);
        Main.newLine(solutionWriter);
    }

    private HuffmanDecoding() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final Map<Character, String> codeBook = HuffmanDecoding.parseCodeBook(input.options);
        final String targetText = HuffmanDecoding.parseOrGenerateTargetText(codeBook, input.options);
        final String result = HuffmanDecoding.decodeHuffman(targetText, new HuffmanTree(codeBook));
        HuffmanDecoding.printExerciseAndSolutionForHuffmanDecoding(
            targetText,
            codeBook,
            result,
            input.exerciseWriter,
            input.solutionWriter
        );
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[4];
        result[0] = "-o";
        result[1] = "'A':\"0\",'B':\"100\",'C':\"101\",'D':\"110\",'E':\"111\"";
        result[2] = "-l";
        result[3] = "5";
        return result; //TODO
    }

}
