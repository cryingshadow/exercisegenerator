package exercisegenerator.algorithms;

import java.io.*;
import java.math.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.binary.*;
import exercisegenerator.structures.coding.*;

public abstract class CodingAlgorithms {

    public static final List<Character> BINARY_ALPHABET = Arrays.asList('0', '1');

    private static final String CODE_BOOK_FORMAT_ERROR_MESSAGE =
        "The specified code book does not match the expected format (entries of the form 'S':\"C\" for a symbol S and a code C, separated by commas)!";

    public static void decodeHamming(final AlgorithmInput input) throws IOException {
        final BitString code =
            new ParserAndGenerator<BitString>(
                BitString::parse,
                CodingAlgorithms::generateHammingCode
            ).getResult(input.options);
        final BitString message = CodingAlgorithms.decodeHamming(code);
        CodingAlgorithms.printHammingDecodingExerciseAndSolution(
            code,
            message,
            input.exerciseWriter,
            input.solutionWriter
        );
    }

    public static BitString decodeHamming(final BitString message) {
        final int codeLength = message.size();
        if (!CodingAlgorithms.isPositiveIntegerPowerOfTwo(codeLength + 1)) {
            throw new IllegalArgumentException("Code length must be one less than a power of two!");
        }
        final int numOfParityBits = CodingAlgorithms.log2(codeLength + 1);
        final BitString error = new BitString();
        for (int i = 0; i < numOfParityBits; i++) {
            error.addFirst(Bit.fromBoolean(CodingAlgorithms.oddOnes(message, i, numOfParityBits)));
        }
        final BitString result = new BitString(message);
        if (!error.isZero()) {
            result.invertBit(error.toUnsignedInt() - 1);
        }
        for (int i = numOfParityBits - 1; i >= 0; i--) {
            result.remove(BigInteger.TWO.pow(i).intValue() - 1);
        }
        return result;
    }

    public static void decodeHuffman(final AlgorithmInput input) throws IOException {
        final Map<Character, String> codeBook = CodingAlgorithms.parseCodeBook(input.options);
        final String targetText = CodingAlgorithms.parseOrGenerateTargetText(codeBook, input.options);
        final String result = CodingAlgorithms.decodeHuffman(targetText, new HuffmanTree(codeBook));
        CodingAlgorithms.printExerciseAndSolutionForHuffmanDecoding(
            targetText,
            codeBook,
            result,
            input.exerciseWriter,
            input.solutionWriter
        );
    }

    public static String decodeHuffman(final String targetText, final HuffmanTree tree) {
        return tree.decode(targetText);
    }

    public static void encodeHamming(final AlgorithmInput input) throws IOException {
        final BitString message =
            new ParserAndGenerator<BitString>(
                BitString::parse,
                CodingAlgorithms::generateHammingMessage
            ).getResult(input.options);
        final BitString code = CodingAlgorithms.encodeHamming(message);
        CodingAlgorithms.printHammingEncodingExerciseAndSolution(message, code, input.exerciseWriter, input.solutionWriter);
    }

    public static BitString encodeHamming(final BitString message) {
        final int messageLength = message.size();
        final int codeLength = CodingAlgorithms.findNextPowerOfTwo(messageLength) - 1;
        final int numOfParityBits = codeLength - messageLength;
        if (BigInteger.TWO.pow(numOfParityBits).intValue() - 1 != codeLength) {
            throw new IllegalArgumentException("Message length does not match code length!");
        }
        final BitString result = new BitString();
        int index = 1;
        final Iterator<Bit> data = message.iterator();
        while (data.hasNext()) {
            if (CodingAlgorithms.isPositiveIntegerPowerOfTwo(index)) {
                result.add(Bit.ZERO);
            } else {
                result.add(data.next());
            }
            index++;
        }
        for (int i = 3; i <= codeLength; i++) {
            if (!result.get(i - 1).isZero()) {
                CodingAlgorithms.invertParityBits(i, result, numOfParityBits);
            }
        }
        return result;
    }

    public static void encodeHuffman(final AlgorithmInput input) throws IOException {
        final String sourceText = CodingAlgorithms.parseOrGenerateSourceText(input.options);
        final List<Character> targetAlphabet = CodingAlgorithms.parseOrGenerateTargetAlphabet(input.options);
        final Pair<HuffmanTree, String> result = CodingAlgorithms.encodeHuffman(sourceText, targetAlphabet);
        CodingAlgorithms.printExerciseAndSolutionForHuffmanEncoding(
            sourceText,
            targetAlphabet,
            result,
            input.options,
            input.exerciseWriter,
            input.solutionWriter
        );
    }

    public static Pair<HuffmanTree, String> encodeHuffman(
        final String sourceText,
        final List<Character> targetAlphabet
    ) {
        final HuffmanTree tree = new HuffmanTree(sourceText, targetAlphabet);
        return new Pair<HuffmanTree, String>(tree, tree.toEncoder().encode(sourceText));
    }

    public static String[] generateTestParametersFrom() {
        final String[] result = new String[4];
        result[0] = "-o";
        result[1] = "'A':\"0\",'B':\"100\",'C':\"101\",'D':\"110\",'E':\"111\"";
        result[2] = "-l";
        result[3] = "5";
        return result; //TODO
    }

    public static String[] generateTestParametersTo() {
        final String[] result = new String[0];
        return result;
    }

    private static int findNextPowerOfTwo(final int number) {
        int result = 4;
        while (result <= number) {
            result = result * 2;
        }
        return result;
    }

    private static List<Character> generateAlphabet(final int alphabetSize, final Random gen) {
        final List<Character> biggestAlphabet = new ArrayList<Character>();
        for (int i = 32; i < 127; i++) {
            biggestAlphabet.add(Character.valueOf((char)i));
        }
        if (alphabetSize >= biggestAlphabet.size()) {
            return biggestAlphabet;
        }
        final List<Character> result = new ArrayList<Character>();
        for (int i = 0; i < alphabetSize; i++) {
            result.add(biggestAlphabet.remove(gen.nextInt(biggestAlphabet.size())));
        }
        return result;
    }

    private static BitString generateHammingCode(final Parameters options) {
        final int length = Integer.parseInt(options.getOrDefault(Flag.LENGTH, "7"));
        final int messageLength = CodingAlgorithms.hammingCodeLengthToMessageLength(length);
        final BitString message = CodingAlgorithms.generateHammingMessage(messageLength);
        final BitString result = CodingAlgorithms.encodeHamming(message);
        final Random gen = new Random();
        if (gen.nextBoolean()) {
            result.invertBit(gen.nextInt(result.size()));
        }
        return result;
    }

    private static BitString generateHammingMessage(final int length) {
        final Random gen = new Random();
        final BitString result = new BitString();
        for (int i = 0; i < length; i++) {
            result.add(Bit.fromBoolean(gen.nextBoolean()));
        }
        return result;
    }

    private static BitString generateHammingMessage(final Parameters options) {
        final int length = Integer.parseInt(options.getOrDefault(Flag.LENGTH, "4"));
        return CodingAlgorithms.generateHammingMessage(length);
    }

    private static String generateSourceText(final Parameters options) {
        final Random gen = new Random();
        final int alphabetSize = CodingAlgorithms.parseOrGenerateAlphabetSize(options, gen);
        final int textLength = CodingAlgorithms.parseOrGenerateTextLength(options, gen);
        final List<Character> alphabet = CodingAlgorithms.generateAlphabet(alphabetSize, gen);
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < textLength; i++) {
            result.append(alphabet.get(gen.nextInt(alphabetSize)));
        }
        return result.toString();
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

    private static int hammingCodeLengthToMessageLength(final int codeLength) {
        final int powerOfTwo = codeLength + 1;
        if (!CodingAlgorithms.isPositiveIntegerPowerOfTwo(powerOfTwo)) {
            throw new IllegalArgumentException("Code length must be one less than a power of two!");
        }
        return codeLength - CodingAlgorithms.log2(powerOfTwo);
    }

    private static void invertParityBits(final int index, final BitString code, final int numOfParityBits) {
        final BitString indexBits = CodingAlgorithms.toIndexBits(index, numOfParityBits);
        int bitIndex = 0;
        for (final Bit bit : indexBits) {
            if (!bit.isZero()) {
                code.invertBit(BigInteger.TWO.pow(bitIndex).intValue() - 1);
            }
            bitIndex++;
        }
    }

    private static boolean isPositiveIntegerPowerOfTwo(final int number) {
        if (number < 1) {
            return false;
        }
        int current = number;
        while (current > 1) {
            if (current % 2 != 0) {
                return false;
            }
            current = current / 2;
        }
        return true;
    }

    private static int log2(final int number) {
        int current = number;
        int result = 0;
        while (current > 1) {
            current = current / 2;
            result++;
        }
        return result;
    }

    private static boolean oddOnes(final BitString message, final int parityBit, final int numOfParityBits) {
        boolean result = false;
        int index = 1;
        for (final Bit bit : message) {
            if (!bit.isZero()) {
                final BitString indexBits = CodingAlgorithms.toIndexBits(index, numOfParityBits);
                if (!indexBits.get(parityBit).isZero()) {
                    result = !result;
                }
            }
            index++;
        }
        return result;
    }

    private static Map<Character, String> parseCodeBook(final Parameters options) {
        final String input = options.get(Flag.OPERATIONS);
        final Pattern pattern = Pattern.compile("'.':\"[^\"]+\"");
        if (!input.matches("'.':\"[^\"]+\"(,'.':\"[^\"]+\")*")) {
            throw new IllegalArgumentException(CodingAlgorithms.CODE_BOOK_FORMAT_ERROR_MESSAGE);
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

    private static String parseInputText(final BufferedReader reader, final Parameters options)
    throws IOException {
        return reader.readLine();
    }

    private static int parseOrGenerateAlphabetSize(final Parameters options, final Random gen) {
        if (options.containsKey(Flag.DEGREE)) {
            return Integer.parseInt(options.get(Flag.DEGREE));
        }
        return gen.nextInt(6) + 5;
    }

    private static String parseOrGenerateSourceText(final Parameters options) throws IOException {
        return new ParserAndGenerator<String>(
            CodingAlgorithms::parseInputText,
            CodingAlgorithms::generateSourceText
        ).getResult(options);
    }

    private static List<Character> parseOrGenerateTargetAlphabet(final Parameters options) throws IOException {
        if (options.containsKey(Flag.OPERATIONS)) {
            return options.get(Flag.OPERATIONS).chars().mapToObj(c -> (char)c).toList();
        }
        return CodingAlgorithms.BINARY_ALPHABET;
    }

    private static String parseOrGenerateTargetText(
        final Map<Character, String> codeBook,
        final Parameters options
    ) throws IOException {
        return new ParserAndGenerator<String>(
            CodingAlgorithms::parseInputText,
            flags -> CodingAlgorithms.generateTargetText(codeBook, flags)
        ).getResult(options);
    }

    private static int parseOrGenerateTextLength(final Parameters options, final Random gen) {
        if (options.containsKey(Flag.LENGTH)) {
            return Integer.parseInt(options.get(Flag.LENGTH));
        }
        return gen.nextInt(16) + 5;
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
        CodingAlgorithms.printCodeBookForDecoding(codeBook, exerciseWriter);
        LaTeXUtils.printVerticalProtectedSpace("-3ex", exerciseWriter);
        exerciseWriter.write("\\textbf{Quelltext:}\\\\[2ex]");
        Main.newLine(exerciseWriter);
        Main.newLine(exerciseWriter);

        solutionWriter.write(LaTeXUtils.code(result));
        Main.newLine(solutionWriter);
        Main.newLine(solutionWriter);
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
        CodingAlgorithms.printCodeBookForEncoding(result.x.toCodeBook(), exerciseWriter, solutionWriter);
        LaTeXUtils.printVerticalProtectedSpace(exerciseWriter);
        LaTeXUtils.printVerticalProtectedSpace(solutionWriter);
        CodingAlgorithms.printCode(result.y, exerciseWriter, solutionWriter);
        LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), options, exerciseWriter);
        Main.newLine(solutionWriter);
    }

    private static void printHammingDecodingExerciseAndSolution(
        final BitString code,
        final BitString message,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) throws IOException {
        exerciseWriter.write("Dekodieren Sie den folgenden \\emphasize{Hamming-Code}:\\\\[2ex]");
        Main.newLine(exerciseWriter);
        exerciseWriter.write(LaTeXUtils.codeseq(code.toString()));
        Main.newLine(exerciseWriter);
        Main.newLine(exerciseWriter);
        solutionWriter.write(LaTeXUtils.codeseq(message.toString()));
        Main.newLine(solutionWriter);
        Main.newLine(solutionWriter);
    }

    private static void printHammingEncodingExerciseAndSolution(
        final BitString message,
        final BitString code,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) throws IOException {
        exerciseWriter.write(
            "Geben Sie den \\emphasize{Hamming-Code} f\\\"ur die folgende bin\\\"are Nachricht an:\\\\[2ex]"
        );
        Main.newLine(exerciseWriter);
        exerciseWriter.write(LaTeXUtils.codeseq(message.toString()));
        Main.newLine(exerciseWriter);
        Main.newLine(exerciseWriter);
        solutionWriter.write(LaTeXUtils.codeseq(code.toString()));
        Main.newLine(solutionWriter);
        Main.newLine(solutionWriter);
    }

    private static BitString toIndexBits(final int index, final int numOfParityBits) {
        return BitString.create(BigInteger.valueOf(index), numOfParityBits).reverse();
    }

    private static List<Pair<Character, String>> toSortedList(final Map<Character, String> codeBook) {
        return codeBook.entrySet()
            .stream()
            .map(entry -> new Pair<Character, String>(entry.getKey(), entry.getValue()))
            .sorted(
                new Comparator<Pair<Character, String>>() {

                    @Override
                    public int compare(final Pair<Character, String> pair1, final Pair<Character, String> pair2) {
                        return Character.compare(pair1.x, pair2.x);
                    }

                }
            ).toList();
    }

}
