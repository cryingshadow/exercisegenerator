package exercisegenerator.algorithms.cryptography;

import java.io.*;
import java.util.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.structures.*;

public class VigenereEncryption implements AlgorithmImplementation {

    public static final VigenereEncryption INSTANCE = new VigenereEncryption();

    public static String vigenereEncode(final String sourceText, final String keyword, final List<Character> alphabet) {
        return VigenereEncryption.vigenereEncode(sourceText, keyword, new VigenereSquare(alphabet));
    }

    public static String vigenereEncode(final String sourceText, final String keyword, final VigenereSquare square) {
        return Cryptography.vigenere(sourceText, keyword, square::encode);
    }

    private VigenereEncryption() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        Cryptography.vigenere(input, true);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

}
