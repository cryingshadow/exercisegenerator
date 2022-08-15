package exercisegenerator.algorithms;

import java.io.*;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.*;
import exercisegenerator.io.*;

public class BinaryNumbersTest {

    public static void complementEnd(final BufferedReader exReader, final BufferedReader solReader)
    throws IOException {
        Assert.assertEquals(exReader.readLine(), "\\fi");
        Assert.assertEquals(exReader.readLine(), "");
        Assert.assertNull(exReader.readLine());

        Assert.assertEquals(solReader.readLine(), "");
        Assert.assertNull(solReader.readLine());
    }

    public static void complementMiddle(final BufferedReader exReader, final BufferedReader solReader)
    throws IOException {
        Assert.assertEquals(exReader.readLine(), "");
        Assert.assertEquals(exReader.readLine(), "\\vspace*{1ex}");
        Assert.assertEquals(exReader.readLine(), "");

        Assert.assertEquals(solReader.readLine(), "");
        Assert.assertEquals(solReader.readLine(), "\\vspace*{1ex}");
        Assert.assertEquals(solReader.readLine(), "");
    }

    public static void complementStart(final BufferedReader exReader, final BufferedReader solReader)
    throws IOException {
        Assert.assertEquals(exReader.readLine(), "\\ifprintanswers");
        Assert.assertEquals(exReader.readLine(), "\\else");
    }

    public static int fromComplement(
        int currentNodeNumber,
        final int number,
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

    public static int toComplement(
        int currentNodeNumber,
        final int number,
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

    @BeforeMethod
    public void prepare() {
        TikZUtils.reset();
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
