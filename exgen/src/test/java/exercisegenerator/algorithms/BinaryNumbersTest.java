package exercisegenerator.algorithms;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.binary.*;

public class BinaryNumbersTest {

    @Test
    public void fromFloat() {
        Assert.assertEquals(BinaryNumbers.fromFloat(BitString.parse("00110110"), 3, 4), "1,375");
        Assert.assertEquals(
            BinaryNumbers.fromFloat(BitString.parse("01000001011011000000000000000000"), 8, 23),
            "14,75"
        );
        Assert.assertEquals(BinaryNumbers.fromFloat(BitString.parse("10010000"), 3, 4), "-0,25");
        Assert.assertEquals(BinaryNumbers.fromFloat(BitString.parse("00000000"), 3, 4), "0,0");
        Assert.assertEquals(BinaryNumbers.fromFloat(BitString.parse("10000000"), 3, 4), "-0,0");
        Assert.assertEquals(BinaryNumbers.fromFloat(BitString.parse("00010000"), 3, 4), "0,25");
        Assert.assertEquals(BinaryNumbers.fromFloat(BitString.parse("01110000"), 3, 4), "inf");
        Assert.assertEquals(BinaryNumbers.fromFloat(BitString.parse("11110000"), 3, 4), "-inf");
        Assert.assertEquals(BinaryNumbers.fromFloat(BitString.parse("11111111"), 3, 4), "NaN");
        Assert.assertEquals(BinaryNumbers.fromFloat(BitString.parse("00001000"), 3, 4), "0,125");
        Assert.assertEquals(BinaryNumbers.fromFloat(BitString.parse("00000100"), 3, 4), "0,0625");
        Assert.assertEquals(BinaryNumbers.fromFloat(BitString.parse("00000010"), 3, 4), "0,03125");
        Assert.assertEquals(BinaryNumbers.fromFloat(BitString.parse("00000001"), 3, 4), "0,015625");
        Assert.assertEquals(BinaryNumbers.fromFloat(BitString.parse("01101111"), 3, 4), "15,5");
    }

    @Test
    public void fromOnesComplement() {
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
    public void fromTwosComplement() {
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
        LaTeXUtils.reset();
    }

    @Test
    public void toFloat() {
        Assert.assertEquals(BinaryNumbers.toFloat("1,4", 3, 4).toString(), "00110110");
        Assert.assertEquals(BinaryNumbers.toFloat("18,4", 8, 23).toString(), "01000001100100110011001100110011");
        Assert.assertEquals(BinaryNumbers.toFloat("-0,25", 3, 4).toString(), "10010000");
        Assert.assertEquals(BinaryNumbers.toFloat("0", 3, 4).toString(), "00000000");
        Assert.assertEquals(BinaryNumbers.toFloat("-0", 3, 4).toString(), "10000000");
        Assert.assertEquals(BinaryNumbers.toFloat("-0,1", 3, 4).toString(), "10000110");
        Assert.assertEquals(BinaryNumbers.toFloat("0,0", 3, 4).toString(), "00000000");
        Assert.assertEquals(BinaryNumbers.toFloat("0,00", 3, 4).toString(), "00000000");
        Assert.assertEquals(BinaryNumbers.toFloat("-00,0", 3, 4).toString(), "10000000");
        Assert.assertEquals(BinaryNumbers.toFloat("0,2500", 3, 4).toString(), "00010000");
        Assert.assertEquals(BinaryNumbers.toFloat("0,0625", 5, 10).toString(), "0010110000000000");
        Assert.assertEquals(BinaryNumbers.toFloat("0,125", 3, 4).toString(), "00001000");
        Assert.assertEquals(BinaryNumbers.toFloat("0,0625", 3, 4).toString(), "00000100");
        Assert.assertEquals(BinaryNumbers.toFloat("0,015625", 3, 4).toString(), "00000001");
        Assert.assertEquals(BinaryNumbers.toFloat("0,01", 3, 4).toString(), "00000000");
    }

    @Test
    public void toOnesComplement() {
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
    public void toTwosComplement() {
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
