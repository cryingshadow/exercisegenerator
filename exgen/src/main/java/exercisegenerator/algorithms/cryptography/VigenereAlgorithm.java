package exercisegenerator.algorithms.cryptography;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
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
        final String alphabet = reader.readLine();
        if (alphabet == null || alphabet.isBlank()) {
            return VigenereAlgorithm.generateAlphabet(options);
        }
        return alphabet.chars().mapToObj(c -> (char)c).toList();
    }

    private static List<Character> parseOrGenerateAlphabet(final Parameters<Flag> options) throws IOException {
        return new ParserAndGenerator<List<Character>>(
            VigenereAlgorithm::parseAlphabet,
            VigenereAlgorithm::generateAlphabet
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
    default VigenereProblem generateProblem(final Parameters<Flag> options) {
        return new VigenereProblem(
            VigenereAlgorithm.generateInputText(VigenereAlgorithm.ALPHABET26, options),
            VigenereAlgorithm.generateKeyword(VigenereAlgorithm.ALPHABET26, options),
            VigenereAlgorithm.ALPHABET26
        );
    }

    boolean isEncoding();

    @Override
    default List<VigenereProblem> parseProblems(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        final List<Character> alphabet = VigenereAlgorithm.parseOrGenerateAlphabet(options);
        final List<VigenereProblem> result = new ArrayList<VigenereProblem>();
        reader.readLine();
        String line = reader.readLine();
        while (line != null) {
            if (!line.isBlank()) {
                result.add(new VigenereProblem(line, reader.readLine(), alphabet));
            }
            line = reader.readLine();
        }
        return result;
    }

    @Override
    default void printBeforeMultipleProblemInstances(
        final List<VigenereProblem> problems,
        final List<String> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        if (this.isEncoding()) {
            writer.write("Ver");
        } else {
            writer.write("Ent");
        }
        writer.write("schl\\\"usseln Sie die folgenden Texte unter Benutzung des jeweils angegebenen ");
        writer.write("Schl\\\"usselworts auf dem jeweils angegebenen Alphabet mithilfe der ");
        writer.write("\\emphasize{Vigen\\'ere-Verschl\\\"usselung}.\\\\[1.5ex]");
        Main.newLine(writer);
    }

    @Override
    default void printBeforeSingleProblemInstance(
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
        writer.write("schl\\\"usseln Sie den folgenden Text unter Benutzung des folgenden Schl\\\"usselworts auf dem ");
        writer.write("folgenden Alphabet mithilfe der \\emphasize{Vigen\\'ere-Verschl\\\"usselung}:\\\\[1.5ex]");
        Main.newLine(writer);
    }

    @Override
    default void printProblemInstance(
        final VigenereProblem problem,
        final String solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Alphabet:\\\\");
        Main.newLine(writer);
        new VigenereSquare(problem.alphabet()).toLaTeX(writer);
        Main.newLine(writer);
        writer.write("\\noindent{}Nachricht: ");
        writer.write(LaTeXUtils.code(problem.message()));
        writer.write("\\\\");
        Main.newLine(writer);
        writer.write("Schl\\\"usselwort: ");
        writer.write(LaTeXUtils.code(problem.keyword()));
        writer.write("\\\\");
        Main.newLine(writer);
    }

    @Override
    default void printSolutionInstance(
        final VigenereProblem problem,
        final String solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write(LaTeXUtils.code(solution));
        Main.newLine(writer);
    }

    @Override
    default void printSolutionSpace(
        final VigenereProblem problem,
        final String solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {}

}
