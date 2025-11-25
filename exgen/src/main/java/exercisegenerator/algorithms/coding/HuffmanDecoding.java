package exercisegenerator.algorithms.coding;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.coding.*;

public class HuffmanDecoding implements AlgorithmImplementation<HuffmanCode, String> {

    public static final HuffmanDecoding INSTANCE = new HuffmanDecoding();

    private static final String CODE_BOOK_FORMAT_ERROR_MESSAGE =
        "The specified code book does not match the expected format (entries of the form 'S':\"C\" for a symbol S and a code C, separated by commas)!";

    static void printBeforeMultipleProblemInstancesStatically(
        final List<HuffmanCode> problems,
        final List<String> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Erzeugen Sie den jeweiligen Quelltext aus den nachfolgenden \\emphasize{Huffman-Codes} mit dem ");
        writer.write("jeweils angegebenen Codebuch.\\\\");
        Main.newLine(writer);
    }

    static void printBeforeSingleProblemInstanceStatically(
        final HuffmanCode problem,
        final String solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Erzeugen Sie den Quelltext aus dem nachfolgenden \\emphasize{Huffman-Code} mit dem angegebenen ");
        writer.write("Codebuch:\\\\[2ex]");
        Main.newLine(writer);
    }

    static void printProblemInstanceStatically(
        final HuffmanCode problem,
        final String solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write(LaTeXUtils.codeseq(LaTeXUtils.escapeForLaTeX(problem.message())));
        Main.newLine(writer);
        LaTeXUtils.printVerticalProtectedSpace(writer);
        HuffmanDecoding.printCodeBookForDecoding(problem.tree().toCodeBook(), writer);
    }

    static void printSolutionInstanceStatically(
        final HuffmanCode problem,
        final String solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write(LaTeXUtils.code(LaTeXUtils.escapeForLaTeX(solution)));
        Main.newLine(writer);
    }

    static void printSolutionSpaceStatically(
        final HuffmanCode problem,
        final String solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printVerticalProtectedSpace("-3ex", writer);
        writer.write("\\textbf{Quelltext:}\\\\[2ex]");
        Main.newLine(writer);
    }

    private static String generateTargetText(final Map<Character, String> codeBook, final Parameters<Flag> options) {
        final int length = CodingAlgorithms.parseOrGenerateTextLength(options);
        final StringBuilder result = new StringBuilder();
        final List<String> samples = new ArrayList<String>(codeBook.values());
        for (int i = 0; i < length; i++) {
            result.append(samples.get(Main.RANDOM.nextInt(samples.size())));
        }
        return result.toString();
    }

    private static HuffmanCodeBook parseCodeBook(final Parameters<Flag> options) {
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
            }).collect(
                Collectors.toMap(
                    Pair::getKey,
                    Pair::getValue,
                    (x, y) -> {
                        throw new IllegalArgumentException("Codebook has multiple entries for same key!");
                    },
                    HuffmanCodeBook::new
                )
            );
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

    private HuffmanDecoding() {}

    @Override
    public String apply(final HuffmanCode problem) {
        return problem.tree().decode(problem.message());
    }

    @Override
    public String commandPrefix() {
        return "FromHuffman";
    }

    @Override
    public HuffmanCode generateProblem(final Parameters<Flag> options) {
        final HuffmanCodeBook codeBook = HuffmanDecoding.parseCodeBook(options);
        return new HuffmanCode(HuffmanDecoding.generateTargetText(codeBook, options), new HuffmanTree(codeBook));
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

    @Override
    public List<HuffmanCode> parseProblems(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        return List.of(
            new HuffmanCode(
                CodingAlgorithms.parseInputText(reader, options),
                new HuffmanTree(HuffmanDecoding.parseCodeBook(options))
            )
        );
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<HuffmanCode> problems,
        final List<String> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        HuffmanDecoding.printBeforeMultipleProblemInstancesStatically(problems, solutions, options, writer);
    }

    @Override
    public void printBeforeSingleProblemInstance(
        final HuffmanCode problem,
        final String solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        HuffmanDecoding.printBeforeSingleProblemInstanceStatically(problem, solution, options, writer);
    }

    @Override
    public void printProblemInstance(
        final HuffmanCode problem,
        final String solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        HuffmanDecoding.printProblemInstanceStatically(problem, solution, options, writer);
    }

    @Override
    public void printSolutionInstance(
        final HuffmanCode problem,
        final String solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        HuffmanDecoding.printSolutionInstanceStatically(problem, solution, options, writer);
    }

    @Override
    public void printSolutionSpace(
        final HuffmanCode problem,
        final String solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        HuffmanDecoding.printSolutionSpaceStatically(problem, solution, options, writer);
    }

}
