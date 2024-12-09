package exercisegenerator.algorithms.cryptography;

import java.util.*;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.structures.cryptography.*;

public class VigenereTest {

    @Test( dataProvider = "vigenereTestData" )
    public void vigenere(final String source, final String key, final String cipher, final List<Character> alphabet) {
        Assert.assertEquals(VigenereEncryption.INSTANCE.apply(new VigenereProblem(source, key, alphabet)), cipher);
        Assert.assertEquals(VigenereDecryption.INSTANCE.apply(new VigenereProblem(cipher, key, alphabet)), source);
    }

    @DataProvider
    public Object[][] vigenereTestData() {
        return new Object[][] {
            {"WERDEMITGLIEDBEIWIKIPEDIA", "WILLKOMMEN", "SMCOOAUFKYEMOMOWIUOVLMOTK", VigenereAlgorithm.ALPHABET26},
            {"ATTACKATDAWN", "LEMON", "LXFOPVEFRNHR", VigenereAlgorithm.ALPHABET26},
            {
                "THEQUICKBROWNFOXJUMPSOVERTHELAZYDOG",
                "LION",
                "EPSDFQQXMZCJYNCKUCACDWJRCBVRWINLOWU",
                VigenereAlgorithm.ALPHABET26
            },
            {"SAKRAL", "KLAUSUR", "ULKLSK", Arrays.asList('A', 'K', 'L', 'R', 'S', 'U')},
            {"SAKRAL", "KLAUSUR", "CLKLSF", VigenereAlgorithm.ALPHABET26},
            {"ABBA", "OTTO", "OAAO", Arrays.asList('A', 'B', 'O', 'T')},
            {"ABBA", "OTTO", "OUUO", VigenereAlgorithm.ALPHABET26}
        };
    }

}
