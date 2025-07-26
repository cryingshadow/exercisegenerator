package exercisegenerator.structures.graphs.layout;

import org.testng.*;
import org.testng.annotations.*;

public class Coordinates2DTest {

    @DataProvider
    public Object[][] angleData() {
        return new Object[][] {
            {new Coordinates2D<Double>(0.0, 0.0), new Coordinates2D<Double>(1.0, 0.0), 0.0},
            {new Coordinates2D<Double>(0.0, 0.0), new Coordinates2D<Double>(0.0, 1.0), 90.0},
            {new Coordinates2D<Double>(0.0, 0.0), new Coordinates2D<Double>(-1.0, 0.0), 180.0},
            {new Coordinates2D<Double>(0.0, 0.0), new Coordinates2D<Double>(0.0, -1.0), 270.0}
        };
    }

    @Test(dataProvider="angleData")
    public void angleTest(final Coordinates2D<Double> v1, final Coordinates2D<Double> v2, final double expectedAngle) {
        Assert.assertEquals(v1.getAngle(v2), expectedAngle, 0.001);
    }

}
