package exercisegenerator.algorithms.coding;

import java.io.*;
import java.math.*;
import java.util.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.binary.*;

abstract class CodingAlgorithms {

    public static final List<Character> BINARY_ALPHABET = Arrays.asList('0', '1');

    static BitString generateHammingMessage(final int length) {
        final BitString result = new BitString();
        for (int i = 0; i < length; i++) {
            result.add(Bit.fromBoolean(Main.RANDOM.nextBoolean()));
        }
        return result;
    }

    static boolean isPositiveIntegerPowerOfTwo(final int number) {
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

    static String parseInputText(final BufferedReader reader, final Parameters<Flag> options)
    throws IOException {
        return reader.readLine();
    }

    static int parseOrGenerateTextLength(final Parameters<Flag> options) {
        return AlgorithmImplementation.parseOrGenerateLength(5, 20, options);
    }

    static BitString toIndexBits(final int index, final int numOfParityBits) {
        return BitString.create(BigInteger.valueOf(index), numOfParityBits).reverse();
    }

    static List<Pair<Character, String>> toSortedList(final Map<Character, String> codeBook) {
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
