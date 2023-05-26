package exercisegenerator.algorithms.binary;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.io.*;
import exercisegenerator.structures.binary.*;

public class BinaryNumbersTest {

    @DataProvider
    public Object[][] ASCIIData() {
        return new Object[][] {
            {'A', "01000001"},
            {'a', "01100001"},
            {'Z', "01011010"},
            {'z', "01111010"},
            {'0', "00110000"},
            {'9', "00111001"},
            {' ', "00100000"},
            {'~', "01111110"}
        };
    }

    @Test(dataProvider="ASCIIData")
    public void fromASCII(
        final char character,
        final String expected
    ) {
        Assert.assertEquals(ConversionFromASCII.fromASCII(character).toString(), expected);
    }

    @Test(dataProvider="fromFloatData")
    public void fromFloat(
        final String bits,
        final int exponentLength,
        final int mantissaLength,
        final String expected
    ) {
        Assert.assertEquals(ConversionFromFloat.fromFloat(BitString.parse(bits), exponentLength, mantissaLength), expected);
    }

    @DataProvider
    public Object[][] fromFloatData() {
        return new Object[][] {
            {"00110110", 3, 4, "1,375"},
            {"01000001011011000000000000000000", 8, 23, "14,75"},
            {"10010000", 3, 4, "-0,25"},
            {"00000000", 3, 4, "0,0"},
            {"10000000", 3, 4, "-0,0"},
            {"00010000", 3, 4, "0,25"},
            {"01110000", 3, 4, "inf"},
            {"11110000", 3, 4, "-inf"},
            {"11111111", 3, 4, "NaN"},
            {"00001000", 3, 4, "0,125"},
            {"00000100", 3, 4, "0,0625"},
            {"00000010", 3, 4, "0,03125"},
            {"00000001", 3, 4, "0,015625"},
            {"01101111", 3, 4, "15,5"},
            {"0111011111", 4, 5, "252,0"},
            {"0000000001", 4, 5, "0,00048828125"}
        };
    }

    @Test(dataProvider="fromOnesData")
    public void fromOnesComplement(final String bits, final int expected) {
        Assert.assertEquals(ConversionFromOnesComplement.fromOnesComplement(BitString.parse(bits)), expected);
    }

    @DataProvider
    public Object[][] fromOnesData() {
        return new Object[][] {
            {"0011", 3},
            {"0100", 4},
            {"00", 0},
            {"000000000", 0},
            {"00000000001", 1},
            {"0111", 7},
            {"1010", -5},
            {"1000", -7},
            {"1111011", -4},
            {"110", -1},
            {"1", 0},
            {"0", 0}
        };
    }

    @Test(dataProvider="twosData")
    public void fromTwosComplement(final int expected, final int bitLength, final String bits) {
        Assert.assertEquals(ConversionFromTwosComplement.fromTwosComplement(BitString.parse(bits)), expected);
    }

    @BeforeMethod
    public void prepare() {
        LaTeXUtils.reset();
    }

    @Test(dataProvider="ASCIIData")
    public void toASCII(
        final char expected,
        final String bits
    ) {
        Assert.assertEquals(ConversionToASCII.toASCII(BitString.parse(bits)), expected);
    }

    @Test(dataProvider="toFloatData")
    public void toFloat(final String number, final int exponentLength, final int mantissaLength, final String expected) {
        Assert.assertEquals(ConversionToFloat.toFloat(number, exponentLength, mantissaLength).toString(), expected);
    }

    @DataProvider
    public Object[][] toFloatData() {
        return new Object[][] {
            {"1,4", 3, 4, "00110110"},
            {"18,4", 8, 23, "01000001100100110011001100110011"},
            {"-0,25", 3, 4, "10010000"},
            {"0", 3, 4, "00000000"},
            {"-0", 3, 4, "10000000"},
            {"-0,1", 3, 4, "10000110"},
            {"0,0", 3, 4, "00000000"},
            {"0,00", 3, 4, "00000000"},
            {"-00,0", 3, 4, "10000000"},
            {"0,2500", 3, 4, "00010000"},
            {"0,0625", 5, 10, "0010110000000000"},
            {"0,125", 3, 4, "00001000"},
            {"0,0625", 3, 4, "00000100"},
            {"0,015625", 3, 4, "00000001"},
            {"0,01", 3, 4, "00000000"}
        };
    }

    @Test(dataProvider="toOnesData")
    public void toOnesComplement(final int number, final int bitLength, final String expected) {
        Assert.assertEquals(ConversionToOnesComplement.toOnesComplement(number, bitLength).toString(), expected);
    }

    @Test(dataProvider="toOnesErrorData")
    public void toOnesComplementErrors(final int number, final int bitLength) {
        Assert.assertThrows(() -> ConversionToOnesComplement.toOnesComplement(number, bitLength));
    }

    @DataProvider
    public Object[][] toOnesData() {
        return new Object[][] {
            {3, 4, "0011"},
            {4, 4, "0100"},
            {0, 2, "00"},
            {0, 9, "000000000"},
            {1, 11, "00000000001"},
            {7, 4, "0111"},
            {-5, 4, "1010"},
            {-7, 4, "1000"},
            {-4, 7, "1111011"},
            {-1, 3, "110"}
        };
    }

    @DataProvider
    public Object[][] toOnesErrorData() {
        return new Object[][] {
            {13, 4},
            {8, 4},
            {-8, 4},
            {128, 8},
            {-128, 8}
        };
    }

    @Test(dataProvider="twosData")
    public void toTwosComplement(final int number, final int bitLength, final String expected) {
        Assert.assertEquals(ConversionToTwosComplement.toTwosComplement(number, bitLength).toString(), expected);
    }

    @Test(dataProvider="toTwosErrorData")
    public void toTwosComplementErrors(final int number, final int bitLength) {
        Assert.assertThrows(() -> ConversionToTwosComplement.toTwosComplement(number, bitLength));
    }

    @DataProvider
    public Object[][] toTwosErrorData() {
        return new Object[][] {
            {7, 3},
            {8, 4},
            {-9, 4},
            {128, 8},
            {-129, 8}
        };
    }

    @DataProvider
    public Object[][] twosData() {
        return new Object[][] {
            {3, 4, "0011"},
            {4, 4, "0100"},
            {0, 2, "00"},
            {0, 9, "000000000"},
            {1, 11, "00000000001"},
            {7, 4, "0111"},
            {-5, 4, "1011"},
            {-7, 4, "1001"},
            {-4, 7, "1111100"},
            {-1, 3, "111"},
            {-8, 4, "1000"},
            {-1, 1, "1"},
            {0, 1, "0"}
        };
    }

}
