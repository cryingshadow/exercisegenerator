package exercisegenerator.algorithms.cryptography;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.cryptography.*;

interface VigenereAlgorithm extends AlgorithmImplementation<VigenereProblem, String> {

    public static final List<Character> ALPHABET26 =
        IntStream.range(65, 91).mapToObj(c -> Character.valueOf((char)c)).toList();

    static String vigenere(
        final String text,
        final String keyword,
        final BiFunction<Character, Character, Character> coder
    ) {
        final char[] keyArray = keyword.toCharArray();
        final char[] textArray = text.toCharArray();
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < textArray.length; i++) {
            final char letter = textArray[i];
            final char key = keyArray[i % keyArray.length];
            result.append(coder.apply(letter, key));
        }
        return result.toString();
    }

    private static List<Character> generateAlphabet(final Parameters<Flag> options) {
        return VigenereAlgorithm.ALPHABET26;
    }

    private static String generateInputText(final List<Character> alphabet, final Parameters<Flag> flags) {
        final int size = Main.RANDOM.nextInt(26) + 5;
        return VigenereAlgorithm.generateText(alphabet, size);
    }

    private static String generateKeyword(final List<Character> alphabet, final Parameters<Flag> flags) {
        final int size = Main.RANDOM.nextInt(17) + 4;
        return VigenereAlgorithm.generateText(alphabet, size);
    }

    private static String generateText(final List<Character> alphabet, final int size) {
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < size; i++) {
            result.append(alphabet.get(Main.RANDOM.nextInt(alphabet.size())));
        }
        return result.toString();
    }

    private static List<Character> parseAlphabet(final BufferedReader reader, final Parameters<Flag> options)
    throws IOException {
        if (reader.readLine() == null) {
            return VigenereAlgorithm.generateAlphabet(options);
        }
        if (reader.readLine() == null) {
            return VigenereAlgorithm.generateAlphabet(options);
        }
        final String alphabet = reader.readLine();
        if (alphabet == null || alphabet.isBlank()) {
            return VigenereAlgorithm.generateAlphabet(options);
        }
        return alphabet.chars().mapToObj(c -> (char)c).toList();
    }

    private static String parseInputText(
        final BufferedReader reader,
        final List<Character> alphabet,
        final Parameters<Flag> options
    ) throws IOException {
        final String text = reader.readLine();
        if (text == null || text.isBlank()) {
            return VigenereAlgorithm.generateInputText(alphabet, options);
        }
        return text;
    }

    private static String parseKeyword(
        final BufferedReader reader,
        final List<Character> alphabet,
        final Parameters<Flag> options
    ) throws IOException {
        if (reader.readLine() == null) {
            return VigenereAlgorithm.generateKeyword(alphabet, options);
        }
        final String keyword = reader.readLine();
        if (keyword == null || keyword.isBlank()) {
            return VigenereAlgorithm.generateKeyword(alphabet, options);
        }
        return keyword;
    }

    private static List<Character> parseOrGenerateAlphabet(final Parameters<Flag> options) throws IOException {
        return new ParserAndGenerator<List<Character>>(
            VigenereAlgorithm::parseAlphabet,
            VigenereAlgorithm::generateAlphabet
        ).getResult(options);
    }

    private static String parseOrGenerateInputText(final List<Character> alphabet, final Parameters<Flag> options)
    throws IOException {
        return new ParserAndGenerator<String>(
            (reader, flags) -> VigenereAlgorithm.parseInputText(reader, alphabet, flags),
            (flags) -> VigenereAlgorithm.generateInputText(alphabet, flags)
        ).getResult(options);
    }

    private static String parseOrGenerateKeyword(final List<Character> alphabet, final Parameters<Flag> options)
    throws IOException {
        return new ParserAndGenerator<String>(
            (reader, flags) -> VigenereAlgorithm.parseKeyword(reader, alphabet, flags),
            (flags) -> VigenereAlgorithm.generateKeyword(alphabet, flags)
        ).getResult(options);
    }

    @Override
    default public String apply(final VigenereProblem problem) {
        final VigenereSquare square = new VigenereSquare(problem.alphabet());
        return
            this.isEncoding() ?
                VigenereAlgorithm.vigenere(problem.message(), problem.keyword(), square::encode) :
                    VigenereAlgorithm.vigenere(problem.message(), problem.keyword(), square::decode);
    }

    @Override
    default public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    @Override
    default public VigenereProblem parseOrGenerateProblem(final Parameters<Flag> options) throws IOException {
        final List<Character> alphabet = VigenereAlgorithm.parseOrGenerateAlphabet(options);
        return new VigenereProblem(
            VigenereAlgorithm.parseOrGenerateInputText(alphabet, options),
            VigenereAlgorithm.parseOrGenerateKeyword(alphabet, options),
            alphabet
        );
    }

    @Override
    default public void printExercise(
        final VigenereProblem problem,
        final String solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        if (this.isEncoding()) {
            writer.write("Ver");
        } else {
            writer.write("Ent");
        }
        writer.write("schl\\\"usseln Sie den Text");
        Main.newLine(writer);
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
        writer.write(LaTeXUtils.code(problem.message()));
        Main.newLine(writer);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
        writer.write("unter Benutzung des Schl\\\"usselworts");
        Main.newLine(writer);
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
        writer.write(LaTeXUtils.code(problem.keyword()));
        Main.newLine(writer);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
        writer.write("auf dem Alphabet");
        Main.newLine(writer);
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
        new VigenereSquare(problem.alphabet()).toLaTeX(writer);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
        writer.write("mithilfe der Vigen\\'ere-Verschl\\\"usselung.");
        Main.newLine(writer);
        Main.newLine(writer);
    }

    @Override
    default public void printSolution(
        final VigenereProblem problem,
        final String solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write(LaTeXUtils.code(solution));
        Main.newLine(writer);
        Main.newLine(writer);
    }

    boolean isEncoding();

}
