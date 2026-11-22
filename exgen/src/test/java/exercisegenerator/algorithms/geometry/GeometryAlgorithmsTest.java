package exercisegenerator.algorithms.geometry;

import java.util.*;

import org.apache.commons.math3.fraction.*;
import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.structures.geometry.*;

public class GeometryAlgorithmsTest {

    @DataProvider
    public Object[][] grahamScanData() {
        return new Object[][] {
            {
                List.of(
                    new Point(BigFraction.ZERO, BigFraction.ZERO),
                    new Point(BigFraction.TWO, BigFraction.ZERO),
                    new Point(BigFraction.TWO, BigFraction.TWO),
                    new Point(BigFraction.ZERO, BigFraction.TWO),
                    new Point(BigFraction.ONE_HALF, BigFraction.ONE)
                ),
                List.of(
                    List.of(
                        new Point(BigFraction.TWO, BigFraction.ZERO),
                        new Point(BigFraction.ZERO, BigFraction.ZERO)
                    ),
                    List.of(
                        new Point(BigFraction.TWO, BigFraction.TWO),
                        new Point(BigFraction.TWO, BigFraction.ZERO),
                        new Point(BigFraction.ZERO, BigFraction.ZERO)
                    ),
                    List.of(
                        new Point(BigFraction.ONE_HALF, BigFraction.ONE),
                        new Point(BigFraction.TWO, BigFraction.TWO),
                        new Point(BigFraction.TWO, BigFraction.ZERO),
                        new Point(BigFraction.ZERO, BigFraction.ZERO)
                    ),
                    List.of(
                        new Point(BigFraction.ZERO, BigFraction.TWO),
                        new Point(BigFraction.ONE_HALF, BigFraction.ONE),
                        new Point(BigFraction.TWO, BigFraction.TWO),
                        new Point(BigFraction.TWO, BigFraction.ZERO),
                        new Point(BigFraction.ZERO, BigFraction.ZERO)
                    ),
                    List.of(
                        new Point(BigFraction.ZERO, BigFraction.TWO),
                        new Point(BigFraction.TWO, BigFraction.TWO),
                        new Point(BigFraction.TWO, BigFraction.ZERO),
                        new Point(BigFraction.ZERO, BigFraction.ZERO)
                    )
                )
            }
        };
    }

    @Test(dataProvider="grahamScanData")
    public void grahamScanTest(final List<Point> points, final List<List<Point>> expected) {
        Assert.assertEquals(GrahamsScan.INSTANCE.apply(points), expected);
    }
}
