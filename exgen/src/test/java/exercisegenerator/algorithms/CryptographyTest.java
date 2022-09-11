package exercisegenerator.algorithms;

import java.util.*;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.structures.*;

public class CryptographyTest {

    @Test( dataProvider = "vigenereTestData" )
    public void vigenere(final String source, final String key, final String cipher, final List<Character> alphabet) {
        Assert.assertEquals(Cryptography.vigenereEncode(source, key, alphabet), cipher);
        Assert.assertEquals(Cryptography.vigenereDecode(cipher, key, alphabet), source);
        Assert.assertEquals(Cryptography.vigenereEncode(source, key, new VigenereSquare(alphabet)), cipher);
        Assert.assertEquals(Cryptography.vigenereDecode(cipher, key, new VigenereSquare(alphabet)), source);
    }

    @DataProvider
    public Object[][] vigenereTestData() {
        return new Object[][] {
            {"WERDEMITGLIEDBEIWIKIPEDIA", "WILLKOMMEN", "SMCOOAUFKYEMOMOWIUOVLMOTK", Cryptography.ALPHABET26},
            {"ATTACKATDAWN", "LEMON", "LXFOPVEFRNHR", Cryptography.ALPHABET26},
            {
                "THEQUICKBROWNFOXJUMPSOVERTHELAZYDOG",
                "LION",
                "EPSDFQQXMZCJYNCKUCACDWJRCBVRWINLOWU",
                Cryptography.ALPHABET26
            },
            {"SAKRAL", "KLAUSUR", "ULKLSK", Arrays.asList('A', 'K', 'L', 'R', 'S', 'U')},
            {"SAKRAL", "KLAUSUR", "CLKLSF", Cryptography.ALPHABET26},
            {"ABBA", "OTTO", "OAAO", Arrays.asList('A', 'B', 'O', 'T')},
            {"ABBA", "OTTO", "OUUO", Cryptography.ALPHABET26}
        };
    }

}
