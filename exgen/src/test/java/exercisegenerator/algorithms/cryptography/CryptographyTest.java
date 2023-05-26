package exercisegenerator.algorithms.cryptography;

import java.util.*;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.structures.*;

public class CryptographyTest {

    @Test( dataProvider = "vigenereTestData" )
    public void vigenere(final String source, final String key, final String cipher, final List<Character> alphabet) {
        Assert.assertEquals(VigenereEncryption.vigenereEncode(source, key, alphabet), cipher);
        Assert.assertEquals(VigenereDecryption.vigenereDecode(cipher, key, alphabet), source);
        Assert.assertEquals(VigenereEncryption.vigenereEncode(source, key, new VigenereSquare(alphabet)), cipher);
        Assert.assertEquals(VigenereDecryption.vigenereDecode(cipher, key, new VigenereSquare(alphabet)), source);
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
