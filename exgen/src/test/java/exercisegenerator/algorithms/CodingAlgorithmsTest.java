package exercisegenerator.algorithms;

import java.io.*;
import java.util.*;

import org.testng.*;
import org.testng.annotations.Test;

import exercisegenerator.structures.*;
import exercisegenerator.structures.coding.*;

public class CodingAlgorithmsTest {

    @Test
    public void decodeHuffmanTest() {
        final Map<Character, String> codeBook = new LinkedHashMap<Character, String>();
        codeBook.put(' ', "011");
        codeBook.put('.', "101010");
        codeBook.put('B', "101011");
        codeBook.put('F', "101100");
        codeBook.put('T', "101101");
        codeBook.put('a', "1110");
        codeBook.put('c', "101110");
        codeBook.put('d', "101111");
        codeBook.put('e', "1111");
        codeBook.put('g', "110000");
        codeBook.put('h', "01001");
        codeBook.put('i', "10000");
        codeBook.put('j', "110001");
        codeBook.put('k', "110010");
        codeBook.put('l', "10001");
        codeBook.put('m', "10010");
        codeBook.put('n', "0001");
        codeBook.put('o', "10011");
        codeBook.put('p', "110011");
        codeBook.put('q', "110100");
        codeBook.put('r', "001");
        codeBook.put('s', "110101");
        codeBook.put('t', "0101");
        codeBook.put('u', "10100");
        codeBook.put('v', "110110");
        codeBook.put('w', "110111");
        codeBook.put('x', "00000");
        codeBook.put('y', "00001");
        codeBook.put('z', "01000");
        final HuffmanTree tree = new HuffmanTree(codeBook);
        final String result =
            CodingAlgorithms.decodeHuffman(
                "10110000111100001010000111100011110110000010101110000100100111100101001110010110011100011111010101010111101101111001110111111001001001100011001111010101011111000101110110111100000010000011110100101001111001011101111101000011011100100101110101111100000111110010001101010",
                tree
            );
        Assert.assertEquals(result, "Franz jagt im komplett verwahrlosten Taxi quer durch Bayern.");
    }

    @Test
    public void encodeHuffmanTest() throws IOException {
        final Pair<HuffmanTree, String> result =
            CodingAlgorithms.encodeHuffman(
                "Franz jagt im komplett verwahrlosten Taxi quer durch Bayern.",
                CodingAlgorithms.BINARY_ALPHABET
            );
        final Map<Character, String> codeBook = result.x.toCodeBook();
        Assert.assertEquals(codeBook.size(), 29);
        Assert.assertEquals(codeBook.get(' '), "011");
        Assert.assertEquals(codeBook.get('.'), "101010");
        Assert.assertEquals(codeBook.get('B'), "101011");
        Assert.assertEquals(codeBook.get('F'), "101100");
        Assert.assertEquals(codeBook.get('T'), "101101");
        Assert.assertEquals(codeBook.get('a'), "1110");
        Assert.assertEquals(codeBook.get('c'), "101110");
        Assert.assertEquals(codeBook.get('d'), "101111");
        Assert.assertEquals(codeBook.get('e'), "1111");
        Assert.assertEquals(codeBook.get('g'), "110000");
        Assert.assertEquals(codeBook.get('h'), "01001");
        Assert.assertEquals(codeBook.get('i'), "10000");
        Assert.assertEquals(codeBook.get('j'), "110001");
        Assert.assertEquals(codeBook.get('k'), "110010");
        Assert.assertEquals(codeBook.get('l'), "10001");
        Assert.assertEquals(codeBook.get('m'), "10010");
        Assert.assertEquals(codeBook.get('n'), "0001");
        Assert.assertEquals(codeBook.get('o'), "10011");
        Assert.assertEquals(codeBook.get('p'), "110011");
        Assert.assertEquals(codeBook.get('q'), "110100");
        Assert.assertEquals(codeBook.get('r'), "001");
        Assert.assertEquals(codeBook.get('s'), "110101");
        Assert.assertEquals(codeBook.get('t'), "0101");
        Assert.assertEquals(codeBook.get('u'), "10100");
        Assert.assertEquals(codeBook.get('v'), "110110");
        Assert.assertEquals(codeBook.get('w'), "110111");
        Assert.assertEquals(codeBook.get('x'), "00000");
        Assert.assertEquals(codeBook.get('y'), "00001");
        Assert.assertEquals(codeBook.get('z'), "01000");
        Assert.assertEquals(
            result.y,
            "101100 001 1110 0001 01000 011 110001 1110 110000 0101 011 10000 10010 011 110010 10011 10010 110011 10001 1111 0101 0101 011 110110 1111 001 110111 1110 01001 001 10001 10011 110101 0101 1111 0001 011 101101 1110 00000 10000 011 110100 10100 1111 001 011 101111 10100 001 101110 01001 011 101011 1110 00001 1111 001 0001 101010"
        );
    }

}
