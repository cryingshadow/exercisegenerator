package exercisegenerator.algorithms;

import java.io.*;
import java.util.*;

import exercisegenerator.io.*;
import exercisegenerator.structures.*;

public abstract class CodingAlgorithms {

    public static final List<Character> BINARY_ALPHABET = Arrays.asList('0', '1');

    public static void decodeHuffman(final AlgorithmInput input) {

    }

    public static void encodeHuffman(final AlgorithmInput input) {
        final String sourceText = CodingAlgorithms.parseOrGenerateInputText(input.options);
        final List<Character> targetAlphabet = CodingAlgorithms.parseOrGenerateTargetAlphabet(input.options);
        final Pair<HuffmanTree, String> result = CodingAlgorithms.encodeHuffman(sourceText, targetAlphabet);
        CodingAlgorithms.printExerciseAndSolution(
            sourceText,
            targetAlphabet,
            result,
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

    private static String parseOrGenerateInputText(final Map<Flag, String> options) {
        // TODO Auto-generated method stub
        return null;
    }

    private static List<Character> parseOrGenerateTargetAlphabet(final Map<Flag, String> options) {
        // TODO Auto-generated method stub
        return null;
    }

    private static void printExerciseAndSolution(
        final String sourceText,
        final List<Character> targetAlphabet,
        final Pair<HuffmanTree, String> result,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) {
        // TODO Auto-generated method stub

    }

}
