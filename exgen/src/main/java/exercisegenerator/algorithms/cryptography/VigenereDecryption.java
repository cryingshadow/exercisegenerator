package exercisegenerator.algorithms.cryptography;

import java.io.*;
import java.util.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.structures.*;

public class VigenereDecryption implements AlgorithmImplementation {

    public static final VigenereDecryption INSTANCE = new VigenereDecryption();

    public static String vigenereDecode(final String cipherText, final String keyword, final List<Character> alphabet) {
        return VigenereDecryption.vigenereDecode(cipherText, keyword, new VigenereSquare(alphabet));
    }

    public static String vigenereDecode(final String cipherText, final String keyword, final VigenereSquare square) {
        return Cryptography.vigenere(cipherText, keyword, square::decode);
    }

    private VigenereDecryption() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        Cryptography.vigenere(input, false);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

}
