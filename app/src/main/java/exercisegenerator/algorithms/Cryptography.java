package exercisegenerator.algorithms;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;

public abstract class Cryptography {

    public static final List<Character> ALPHABET26 =
        IntStream.range(65, 91).mapToObj(c -> Character.valueOf((char)c)).toList();

    public static void vigenereDecode(final AlgorithmInput input) throws IOException {
        Cryptography.vigenere(input, false);
    }

    public static String vigenereDecode(final String cipherText, final String keyword, final List<Character> alphabet) {
        return Cryptography.vigenereDecode(cipherText, keyword, new VigenereSquare(alphabet));
    }

    public static String vigenereDecode(final String cipherText, final String keyword, final VigenereSquare square) {
        return Cryptography.vigenere(cipherText, keyword, square::decode);
    }

    public static void vigenereEncode(final AlgorithmInput input) throws IOException {
        Cryptography.vigenere(input, true);
    }

    public static String vigenereEncode(final String sourceText, final String keyword, final List<Character> alphabet) {
        return Cryptography.vigenereEncode(sourceText, keyword, new VigenereSquare(alphabet));
    }

    public static String vigenereEncode(final String sourceText, final String keyword, final VigenereSquare square) {
        return Cryptography.vigenere(sourceText, keyword, square::encode);
    }

    private static List<Character> generateAlphabet(final Parameters options) {
        return Cryptography.ALPHABET26;
    }

    private static String generateInputText(final List<Character> alphabet, final Parameters flags) {
        final Random gen = new Random();
        final int size = gen.nextInt(26) + 5;
        return Cryptography.generateText(alphabet, size, gen);
    }

    private static String generateKeyword(final List<Character> alphabet, final Parameters flags) {
        final Random gen = new Random();
        final int size = gen.nextInt(17) + 4;
        return Cryptography.generateText(alphabet, size, gen);
    }

    private static String generateText(final List<Character> alphabet, final int size, final Random gen) {
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < size; i++) {
            result.append(alphabet.get(gen.nextInt(alphabet.size())));
        }
        return result.toString();
    }

    private static List<Character> parseAlphabet(final BufferedReader reader, final Parameters options)
    throws IOException {
        if (reader.readLine() == null) {
            return Cryptography.generateAlphabet(options);
        }
        if (reader.readLine() == null) {
            return Cryptography.generateAlphabet(options);
        }
        final String alphabet = reader.readLine();
        if (alphabet == null || alphabet.isBlank()) {
            return Cryptography.generateAlphabet(options);
        }
        return alphabet.chars().mapToObj(c -> (char)c).toList();
    }

    private static String parseInputText(
        final BufferedReader reader,
        final List<Character> alphabet,
        final Parameters options
    ) throws IOException {
        final String text = reader.readLine();
        if (text == null || text.isBlank()) {
            return Cryptography.generateInputText(alphabet, options);
        }
        return text;
    }

    private static String parseKeyword(
        final BufferedReader reader,
        final List<Character> alphabet,
        final Parameters options
    ) throws IOException {
        if (reader.readLine() == null) {
            return Cryptography.generateKeyword(alphabet, options);
        }
        final String keyword = reader.readLine();
        if (keyword == null || keyword.isBlank()) {
            return Cryptography.generateKeyword(alphabet, options);
        }
        return keyword;
    }

    private static List<Character> parseOrGenerateAlphabet(final Parameters options) throws IOException {
        return new ParserAndGenerator<List<Character>>(
            Cryptography::parseAlphabet,
            Cryptography::generateAlphabet
        ).getResult(options);
    }

    private static String parseOrGenerateInputText(final List<Character> alphabet, final Parameters options)
    throws IOException {
        return new ParserAndGenerator<String>(
            (reader, flags) -> Cryptography.parseInputText(reader, alphabet, flags),
            (flags) -> Cryptography.generateInputText(alphabet, flags)
        ).getResult(options);
    }

    private static String parseOrGenerateKeyword(final List<Character> alphabet, final Parameters options)
    throws IOException {
        return new ParserAndGenerator<String>(
            (reader, flags) -> Cryptography.parseKeyword(reader, alphabet, flags),
            (flags) -> Cryptography.generateKeyword(alphabet, flags)
        ).getResult(options);
    }

    private static void printExerciseAndSolutionForVigenere(
        final String inputText,
        final String keyword,
        final VigenereSquare square,
        final String result,
        final boolean encode,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) throws IOException {
        if (encode) {
            exerciseWriter.write("Ver");
        } else {
            exerciseWriter.write("Ent");
        }
        exerciseWriter.write("schl\\\"usseln Sie den Text");
        Main.newLine(exerciseWriter);
        TikZUtils.printBeginning(TikZUtils.CENTER, exerciseWriter);
        exerciseWriter.write(TikZUtils.code(inputText));
        Main.newLine(exerciseWriter);
        TikZUtils.printEnd(TikZUtils.CENTER, exerciseWriter);
        exerciseWriter.write("unter Benutzung des Schl\\\"usselworts");
        Main.newLine(exerciseWriter);
        TikZUtils.printBeginning(TikZUtils.CENTER, exerciseWriter);
        exerciseWriter.write(TikZUtils.code(keyword));
        Main.newLine(exerciseWriter);
        TikZUtils.printEnd(TikZUtils.CENTER, exerciseWriter);
        exerciseWriter.write("auf dem Alphabet");
        Main.newLine(exerciseWriter);
        TikZUtils.printBeginning(TikZUtils.CENTER, exerciseWriter);
        square.toLaTeX(exerciseWriter);
        TikZUtils.printEnd(TikZUtils.CENTER, exerciseWriter);
        exerciseWriter.write("mithilfe der Vigen\\'ere-Verschl\\\"usselung.");
        Main.newLine(exerciseWriter);
        Main.newLine(exerciseWriter);

        solutionWriter.write(TikZUtils.code(result));
        Main.newLine(solutionWriter);
        Main.newLine(solutionWriter);
    }

    private static void vigenere(final AlgorithmInput input, final boolean encode) throws IOException {
        final List<Character> alphabet = Cryptography.parseOrGenerateAlphabet(input.options);
        final String inputText = Cryptography.parseOrGenerateInputText(alphabet, input.options);
        final String keyword = Cryptography.parseOrGenerateKeyword(alphabet, input.options);
        final VigenereSquare square = new VigenereSquare(alphabet);
        final String result =
            encode ?
                Cryptography.vigenereEncode(inputText, keyword, square) :
                    Cryptography.vigenereDecode(inputText, keyword, square);
        Cryptography.printExerciseAndSolutionForVigenere(
            inputText,
            keyword,
            square,
            result,
            encode,
            input.exerciseWriter,
            input.solutionWriter
        );
    }

    private static String vigenere(
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

}
