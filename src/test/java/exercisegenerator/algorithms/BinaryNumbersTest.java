package exercisegenerator.algorithms;

import java.io.*;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;

public class BinaryNumbersTest {

    public static void binaryEnd(final BufferedReader exReader, final BufferedReader solReader)
    throws IOException {
        Assert.assertEquals(exReader.readLine(), "\\fi");
        Assert.assertEquals(exReader.readLine(), "");
        Assert.assertNull(exReader.readLine());

        Assert.assertEquals(solReader.readLine(), "");
        Assert.assertNull(solReader.readLine());
    }

    public static void binaryMiddle(final BufferedReader exReader, final BufferedReader solReader)
    throws IOException {
        Assert.assertEquals(exReader.readLine(), "");
        Assert.assertEquals(exReader.readLine(), "\\vspace*{1ex}");
        Assert.assertEquals(exReader.readLine(), "");

        Assert.assertEquals(solReader.readLine(), "");
        Assert.assertEquals(solReader.readLine(), "\\vspace*{1ex}");
        Assert.assertEquals(solReader.readLine(), "");
    }

    public static void binaryStart(final BufferedReader exReader, final BufferedReader solReader)
    throws IOException {
        Assert.assertEquals(exReader.readLine(), "\\ifprintanswers");
        Assert.assertEquals(exReader.readLine(), "\\else");
    }

    public static int fromBinary(
        int currentNodeNumber,
        final String number,
        final int[] binaryNumber,
        final int contentLength,
        final BufferedReader exReader,
        final BufferedReader solReader
    ) throws IOException {
        Assert.assertEquals(exReader.readLine(), Patterns.beginMinipageForBinaryNumber(binaryNumber));
        Assert.assertEquals(exReader.readLine(), Patterns.forBinaryNumber(binaryNumber));
        Assert.assertEquals(exReader.readLine(), "\\end{minipage}");
        Assert.assertEquals(exReader.readLine(), Patterns.beginMinipageForBinaryNumberComplement(binaryNumber));
        Assert.assertEquals(exReader.readLine(), "\\begin{tikzpicture}");
        Assert.assertEquals(exReader.readLine(), Patterns.ARRAY_STYLE);
        Assert.assertEquals(exReader.readLine(), Patterns.singleEmptyNode(currentNodeNumber++, contentLength));
        Assert.assertEquals(exReader.readLine(), "\\end{tikzpicture}");
        Assert.assertEquals(exReader.readLine(), "\\end{minipage}");

        Assert.assertEquals(solReader.readLine(), Patterns.beginMinipageForBinaryNumber(binaryNumber));
        Assert.assertEquals(solReader.readLine(), Patterns.forBinaryNumber(binaryNumber));
        Assert.assertEquals(solReader.readLine(), "\\end{minipage}");
        Assert.assertEquals(solReader.readLine(), Patterns.beginMinipageForBinaryNumberComplement(binaryNumber));
        Assert.assertEquals(solReader.readLine(), "\\begin{tikzpicture}");
        Assert.assertEquals(solReader.readLine(), Patterns.ARRAY_STYLE);
        Assert.assertEquals(solReader.readLine(), Patterns.singleNode(currentNodeNumber++, number, contentLength));
        Assert.assertEquals(solReader.readLine(), "\\end{tikzpicture}");
        Assert.assertEquals(solReader.readLine(), "\\end{minipage}");
        return currentNodeNumber;
    }

    public static int toBinary(
        int currentNodeNumber,
        final String number,
        final int[] binaryNumber,
        final BufferedReader exReader,
        final BufferedReader solReader
    ) throws IOException {
        final int contentLength = 1;
        Assert.assertEquals(exReader.readLine(), Patterns.beginMinipageForNumber(number));
        Assert.assertEquals(exReader.readLine(), Patterns.forNumber(number));
        Assert.assertEquals(exReader.readLine(), "\\end{minipage}");
        Assert.assertEquals(exReader.readLine(), Patterns.beginMinipageForNumberComplement(number));
        Assert.assertEquals(exReader.readLine(), "\\begin{tikzpicture}");
        Assert.assertEquals(exReader.readLine(), Patterns.ARRAY_STYLE);
        Assert.assertEquals(exReader.readLine(), Patterns.singleEmptyNode(currentNodeNumber++, contentLength));
        for (int i = 1; i < binaryNumber.length; i++) {
            Assert.assertEquals(
                exReader.readLine(),
                Patterns.rightEmptyNodeToPredecessor(currentNodeNumber++, contentLength)
            );
        }
        Assert.assertEquals(exReader.readLine(), "\\end{tikzpicture}");
        Assert.assertEquals(exReader.readLine(), "\\end{minipage}");

        Assert.assertEquals(solReader.readLine(), Patterns.beginMinipageForNumber(number));
        Assert.assertEquals(solReader.readLine(), Patterns.forNumber(number));
        Assert.assertEquals(solReader.readLine(), "\\end{minipage}");
        Assert.assertEquals(solReader.readLine(), Patterns.beginMinipageForNumberComplement(number));
        Assert.assertEquals(solReader.readLine(), "\\begin{tikzpicture}");
        Assert.assertEquals(solReader.readLine(), Patterns.ARRAY_STYLE);
        Assert.assertEquals(solReader.readLine(), Patterns.singleNode(currentNodeNumber++, binaryNumber[0]));
        for (int i = 1; i < binaryNumber.length; i++) {
            Assert.assertEquals(
                solReader.readLine(),
                Patterns.rightNodeToPredecessor(currentNodeNumber++, binaryNumber[i])
            );
        }
        Assert.assertEquals(solReader.readLine(), "\\end{tikzpicture}");
        Assert.assertEquals(solReader.readLine(), "\\end{minipage}");
        return currentNodeNumber;
    }

    @Test
    public void fromFloatUnit() {
        Assert.assertEquals(BinaryNumbers.fromFloat(BitString.parse("00110110"), 3, 4), "1,375");
        Assert.assertEquals(
            BinaryNumbers.fromFloat(BitString.parse("01000001011011000000000000000000"), 8, 23),
            "14,75"
        );
        Assert.assertEquals(BinaryNumbers.fromFloat(BitString.parse("10010000"), 3, 4), "-0,25");
        Assert.assertEquals(BinaryNumbers.fromFloat(BitString.parse("00000000"), 3, 4), "0,0");
        Assert.assertEquals(BinaryNumbers.fromFloat(BitString.parse("10000000"), 3, 4), "-0,0");
        Assert.assertEquals(BinaryNumbers.fromFloat(BitString.parse("00010000"), 3, 4), "0,25");
    }

    @Test
    public void fromOnesComplementUnit() {
        Assert.assertEquals(BinaryNumbers.fromOnesComplement(BitString.parse("0011")), 3);
        Assert.assertEquals(BinaryNumbers.fromOnesComplement(BitString.parse("0100")), 4);
        Assert.assertEquals(BinaryNumbers.fromOnesComplement(BitString.parse("00")), 0);
        Assert.assertEquals(BinaryNumbers.fromOnesComplement(BitString.parse("000000000")), 0);
        Assert.assertEquals(BinaryNumbers.fromOnesComplement(BitString.parse("00000000001")), 1);
        Assert.assertEquals(BinaryNumbers.fromOnesComplement(BitString.parse("0111")), 7);
        Assert.assertEquals(BinaryNumbers.fromOnesComplement(BitString.parse("1010")), -5);
        Assert.assertEquals(BinaryNumbers.fromOnesComplement(BitString.parse("1000")), -7);
        Assert.assertEquals(BinaryNumbers.fromOnesComplement(BitString.parse("1111011")), -4);
        Assert.assertEquals(BinaryNumbers.fromOnesComplement(BitString.parse("110")), -1);
        Assert.assertEquals(BinaryNumbers.fromOnesComplement(BitString.parse("1")), 0);
        Assert.assertEquals(BinaryNumbers.fromOnesComplement(BitString.parse("0")), 0);
    }

    @Test
    public void fromTwosComplementUnit() {
        Assert.assertEquals(BinaryNumbers.fromTwosComplement(BitString.parse("0011")), 3);
        Assert.assertEquals(BinaryNumbers.fromTwosComplement(BitString.parse("0100")), 4);
        Assert.assertEquals(BinaryNumbers.fromTwosComplement(BitString.parse("00")), 0);
        Assert.assertEquals(BinaryNumbers.fromTwosComplement(BitString.parse("000000000")), 0);
        Assert.assertEquals(BinaryNumbers.fromTwosComplement(BitString.parse("00000000001")), 1);
        Assert.assertEquals(BinaryNumbers.fromTwosComplement(BitString.parse("0111")), 7);
        Assert.assertEquals(BinaryNumbers.fromTwosComplement(BitString.parse("1011")), -5);
        Assert.assertEquals(BinaryNumbers.fromTwosComplement(BitString.parse("1001")), -7);
        Assert.assertEquals(BinaryNumbers.fromTwosComplement(BitString.parse("1111100")), -4);
        Assert.assertEquals(BinaryNumbers.fromTwosComplement(BitString.parse("111")), -1);
        Assert.assertEquals(BinaryNumbers.fromTwosComplement(BitString.parse("1000")), -8);
        Assert.assertEquals(BinaryNumbers.fromTwosComplement(BitString.parse("1")), -1);
        Assert.assertEquals(BinaryNumbers.fromTwosComplement(BitString.parse("0")), 0);
    }

    @BeforeMethod
    public void prepare() {
        TikZUtils.reset();
    }

    @Test
    public void toFloatUnit() {
        Assert.assertEquals(BinaryNumbers.toFloat("1,4", 3, 4).toString(), "00110110");
        Assert.assertEquals(BinaryNumbers.toFloat("18,4", 8, 23).toString(), "01000001100100110011001100110011");
        Assert.assertEquals(BinaryNumbers.toFloat("-0,25", 3, 4).toString(), "10010000");
        Assert.assertEquals(BinaryNumbers.toFloat("0", 3, 4).toString(), "00000000");
        Assert.assertEquals(BinaryNumbers.toFloat("-0", 3, 4).toString(), "10000000");
        Assert.assertEquals(BinaryNumbers.toFloat("-0,1", 3, 4).toString(), "10000000");
        Assert.assertEquals(BinaryNumbers.toFloat("0,0", 3, 4).toString(), "00000000");
        Assert.assertEquals(BinaryNumbers.toFloat("0,00", 3, 4).toString(), "00000000");
        Assert.assertEquals(BinaryNumbers.toFloat("-00,0", 3, 4).toString(), "10000000");
        Assert.assertEquals(BinaryNumbers.toFloat("0,2500", 3, 4).toString(), "00010000");
    }

    @Test
    public void toOnesComplementUnit() {
        Assert.assertEquals(BinaryNumbers.toOnesComplement(3, 4).toString(), "0011");
        Assert.assertEquals(BinaryNumbers.toOnesComplement(4, 4).toString(), "0100");
        Assert.assertEquals(BinaryNumbers.toOnesComplement(0, 2).toString(), "00");
        Assert.assertEquals(BinaryNumbers.toOnesComplement(0, 9).toString(), "000000000");
        Assert.assertEquals(BinaryNumbers.toOnesComplement(1, 11).toString(), "00000000001");
        Assert.assertEquals(BinaryNumbers.toOnesComplement(7, 4).toString(), "0111");
        Assert.assertEquals(BinaryNumbers.toOnesComplement(-5, 4).toString(), "1010");
        Assert.assertEquals(BinaryNumbers.toOnesComplement(-7, 4).toString(), "1000");
        Assert.assertEquals(BinaryNumbers.toOnesComplement(-4, 7).toString(), "1111011");
        Assert.assertEquals(BinaryNumbers.toOnesComplement(-1, 3).toString(), "110");
        Assert.assertThrows(() -> BinaryNumbers.toOnesComplement(13, 4));
        Assert.assertThrows(() -> BinaryNumbers.toOnesComplement(8, 4));
        Assert.assertThrows(() -> BinaryNumbers.toOnesComplement(-8, 4));
        Assert.assertThrows(() -> BinaryNumbers.toOnesComplement(128, 8));
        Assert.assertThrows(() -> BinaryNumbers.toOnesComplement(-128, 8));
    }

    @Test
    public void toTwosComplementUnit() {
        Assert.assertEquals(BinaryNumbers.toTwosComplement(3, 4).toString(), "0011");
        Assert.assertEquals(BinaryNumbers.toTwosComplement(4, 4).toString(), "0100");
        Assert.assertEquals(BinaryNumbers.toTwosComplement(0, 2).toString(), "00");
        Assert.assertEquals(BinaryNumbers.toTwosComplement(0, 9).toString(), "000000000");
        Assert.assertEquals(BinaryNumbers.toTwosComplement(1, 11).toString(), "00000000001");
        Assert.assertEquals(BinaryNumbers.toTwosComplement(7, 4).toString(), "0111");
        Assert.assertEquals(BinaryNumbers.toTwosComplement(-5, 4).toString(), "1011");
        Assert.assertEquals(BinaryNumbers.toTwosComplement(-7, 4).toString(), "1001");
        Assert.assertEquals(BinaryNumbers.toTwosComplement(-4, 7).toString(), "1111100");
        Assert.assertEquals(BinaryNumbers.toTwosComplement(-1, 3).toString(), "111");
        Assert.assertEquals(BinaryNumbers.toTwosComplement(-8, 4).toString(), "1000");
        Assert.assertThrows(() -> BinaryNumbers.toTwosComplement(7, 3));
        Assert.assertThrows(() -> BinaryNumbers.toTwosComplement(8, 4));
        Assert.assertThrows(() -> BinaryNumbers.toTwosComplement(-9, 4));
        Assert.assertThrows(() -> BinaryNumbers.toTwosComplement(128, 8));
        Assert.assertThrows(() -> BinaryNumbers.toTwosComplement(-129, 8));
    }

}
