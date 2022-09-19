package exercisegenerator.structures;

import java.math.*;

import org.testng.*;
import org.testng.annotations.*;

public class NumberTimesDecimalPowerTest {

    @Test
    public void addTest() {
        Assert.assertEquals(
            new NumberTimesDecimalPower(BigInteger.ONE, 0)
            .add(new NumberTimesDecimalPower(BigInteger.ONE, 0)).toString(),
            "2,0"
        );
        Assert.assertEquals(
            new NumberTimesDecimalPower(BigInteger.ZERO, 0)
            .add(new NumberTimesDecimalPower(BigInteger.ONE, 0)).toString(),
            "1,0"
        );
        Assert.assertEquals(
            new NumberTimesDecimalPower(BigInteger.ONE, -1)
            .add(new NumberTimesDecimalPower(BigInteger.ONE, -1)).toString(),
            "0,2"
        );
        Assert.assertEquals(
            new NumberTimesDecimalPower(BigInteger.ONE, -2)
            .add(new NumberTimesDecimalPower(BigInteger.ONE, -1)).toString(),
            "0,11"
        );
        Assert.assertEquals(
            new NumberTimesDecimalPower(BigInteger.ONE, 1)
            .add(new NumberTimesDecimalPower(BigInteger.ONE, -1)).toString(),
            "10,1"
        );
        Assert.assertEquals(
            new NumberTimesDecimalPower(BigInteger.TWO.pow(3), -1)
            .add(new NumberTimesDecimalPower(BigInteger.TWO.pow(3), -1)).toString(),
            "1,6"
        );
    }

    @Test(dataProvider="data")
    public void constructorToStringTest(
        final NumberTimesDecimalPower number,
        final String expected,
        final String times2
    ) {
        Assert.assertEquals(number.toString(), expected);
    }

    @DataProvider
    public Object[][] data() {
        return new Object[][] {
            {new NumberTimesDecimalPower(BigInteger.ZERO, 0), "0,0", "0,0"},
            {new NumberTimesDecimalPower(BigInteger.ONE, 0), "1,0", "2,0"},
            {new NumberTimesDecimalPower(BigInteger.ONE, 1), "10,0", "20,0"},
            {new NumberTimesDecimalPower(BigInteger.ONE, -1), "0,1", "0,2"},
            {new NumberTimesDecimalPower(BigInteger.TEN, -1), "1,0", "2,0"},
            {new NumberTimesDecimalPower(BigInteger.TEN, -2), "0,1", "0,2"},
            {new NumberTimesDecimalPower(BigInteger.TEN, 2), "1000,0", "2000,0"},
            {new NumberTimesDecimalPower(BigInteger.TEN.add(BigInteger.TWO), -5), "0,00012", "0,00024"},
            {new NumberTimesDecimalPower(BigInteger.TWO.pow(3), -2), "0,08", "0,16"},
            {new NumberTimesDecimalPower(BigInteger.TWO.pow(3), -1), "0,8", "1,6"}
        };
    }

    @Test(dataProvider="data")
    public void getAfterCommaTest(
        final NumberTimesDecimalPower number,
        final String expected,
        final String times2
    ) {
        Assert.assertEquals(number.getAfterComma().toString(), expected.split(",")[1]);
    }

    @Test(dataProvider="data")
    public void getBeforeCommaTest(
        final NumberTimesDecimalPower number,
        final String expected,
        final String times2
    ) {
        Assert.assertEquals(number.getBeforeComma().toString(), expected.split(",")[0]);
    }

    @Test
    public void subtractOneTest() {
        Assert.assertEquals(
            new NumberTimesDecimalPower(BigInteger.ONE, 0).subtractOne().toString(),
            "0,0"
        );
        Assert.assertEquals(
            new NumberTimesDecimalPower(BigInteger.ONE, 1).subtractOne().toString(),
            "9,0"
        );
        Assert.assertEquals(
            new NumberTimesDecimalPower(BigInteger.TEN, -1).subtractOne().toString(),
            "0,0"
        );
        Assert.assertEquals(
            new NumberTimesDecimalPower(BigInteger.TEN, 2).subtractOne().toString(),
            "999,0"
        );
        Assert.assertEquals(
            new NumberTimesDecimalPower(BigInteger.TWO.pow(3), -1).times2().subtractOne().toString(),
            "0,6"
        );
        Assert.assertThrows(
            IllegalStateException.class,
            () -> new NumberTimesDecimalPower(BigInteger.ZERO, 0).subtractOne()
        );
        Assert.assertThrows(
            IllegalStateException.class,
            () -> new NumberTimesDecimalPower(BigInteger.ONE, -1).subtractOne()
        );
    }

    @Test(dataProvider="data")
    public void times2Test(
        final NumberTimesDecimalPower number,
        final String expected,
        final String times2
    ) {
        Assert.assertEquals(number.times2().toString(), times2);
    }

}
