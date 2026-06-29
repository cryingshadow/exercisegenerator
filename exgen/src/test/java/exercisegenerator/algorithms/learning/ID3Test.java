package exercisegenerator.algorithms.learning;

import java.util.*;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.structures.learning.*;

public class ID3Test {

    private static final List<DecisionTreeData> DATA1 =
        List.of(
            new DecisionTreeData(Map.of("size", "big", "cost", "low"), "yes"),
            new DecisionTreeData(Map.of("size", "small", "cost", "high"), "no"),
            new DecisionTreeData(Map.of("size", "medium", "cost", "high"), "no"),
            new DecisionTreeData(Map.of("size", "small", "cost", "low"), "no"),
            new DecisionTreeData(Map.of("size", "small", "cost", "medium"), "no"),
            new DecisionTreeData(Map.of("size", "medium", "cost", "medium"), "yes"),
            new DecisionTreeData(Map.of("size", "big", "cost", "high"), "yes"),
            new DecisionTreeData(Map.of("size", "big", "cost", "medium"), "yes"),
            new DecisionTreeData(Map.of("size", "medium", "cost", "low"), "yes")
        );

    private static final List<DecisionTreeData> DATA2 =
        List.of(
            new DecisionTreeData(Map.of("size", "big", "cost", "low"), "yes"),
            new DecisionTreeData(Map.of("size", "small", "cost", "high"), "no"),
            new DecisionTreeData(Map.of("size", "medium", "cost", "high"), "no"),
            new DecisionTreeData(Map.of("size", "small", "cost", "low"), "yes"),
            new DecisionTreeData(Map.of("size", "small", "cost", "medium"), "no"),
            new DecisionTreeData(Map.of("size", "medium", "cost", "medium"), "yes"),
            new DecisionTreeData(Map.of("size", "big", "cost", "high"), "no"),
            new DecisionTreeData(Map.of("size", "big", "cost", "medium"), "yes"),
            new DecisionTreeData(Map.of("size", "medium", "cost", "low"), "yes")
        );

    @DataProvider
    public Object[][] applyData() {
        return new Object[][] {
            {
                ID3Test.DATA1,
                new DecisionTreeInnerNode(
                    Map.of(
                        "big", new DecisionTreeLeaf("yes"),
                        "medium", new DecisionTreeInnerNode(
                            Map.of(
                                "high", new DecisionTreeLeaf("no"),
                                "medium", new DecisionTreeLeaf("yes"),
                                "low", new DecisionTreeLeaf("yes")
                            ),
                            "cost"
                        ),
                        "small", new DecisionTreeLeaf("no")
                    ),
                    "size"
                )
            },
            {
                ID3Test.DATA2,
                new DecisionTreeInnerNode(
                    Map.of(
                        "high", new DecisionTreeLeaf("no"),
                        "medium", new DecisionTreeInnerNode(
                            Map.of(
                                "big", new DecisionTreeLeaf("yes"),
                                "medium", new DecisionTreeLeaf("yes"),
                                "small", new DecisionTreeLeaf("no")
                            ),
                            "size"
                        ),
                        "low", new DecisionTreeLeaf("yes")
                    ),
                    "cost"
                )
            }
        };
    }

    @Test(dataProvider="applyData")
    public void applyTest(final List<DecisionTreeData> data, final DecisionTree expected) {
        Assert.assertEquals(ID3.INSTANCE.apply(data), expected);
    }

    @DataProvider
    public Object[][] calculateAverageEntropyData() {
        return new Object[][] {
            {
                List.of(),
                0.0,
                0.0
            },
            {
                List.of(List.of(), List.of()),
                0.0,
                0.0
            },
            {
                List.of(
                    List.of(
                        new DecisionTreeData(Map.of("size", "big"), "yes"),
                        new DecisionTreeData(Map.of("size", "big"), "yes"),
                        new DecisionTreeData(Map.of("size", "big"), "yes"),
                        new DecisionTreeData(Map.of("size", "big"), "no"),
                        new DecisionTreeData(Map.of("size", "big"), "no"),
                        new DecisionTreeData(Map.of("size", "big"), "no")
                    ),
                    List.of(
                        new DecisionTreeData(Map.of("size", "big"), "yes"),
                        new DecisionTreeData(Map.of("size", "big"), "yes"),
                        new DecisionTreeData(Map.of("size", "big"), "yes"),
                        new DecisionTreeData(Map.of("size", "big"), "yes"),
                        new DecisionTreeData(Map.of("size", "big"), "yes"),
                        new DecisionTreeData(Map.of("size", "big"), "yes"),
                        new DecisionTreeData(Map.of("size", "big"), "no"),
                        new DecisionTreeData(Map.of("size", "big"), "no")
                    )
                ),
                0.892,
                0.001
            }
        };
    }

    @Test(dataProvider="calculateAverageEntropyData")
    public void calculateAverageEntropyTest(
        final List<List<DecisionTreeData>> data,
        final double expected,
        final double delta
    ) {
        Assert.assertEquals(ID3.calculateAverageEntropy(data), expected, delta);
    }

    @DataProvider
    public Object[][] calculateEntropyData() {
        return new Object[][] {
            {List.of(), 0.0, 0.0},
            {List.of(new DecisionTreeData(Map.of(), "yes")), 0.0, 0.0},
            {List.of(new DecisionTreeData(Map.of("size", "big"), "yes")), 0.0, 0.0},
            {
                List.of(
                    new DecisionTreeData(Map.of("size", "big"), "yes"),
                    new DecisionTreeData(Map.of("size", "small"), "yes")
                ),
                0.0,
                0.0
            },
            {
                List.of(
                    new DecisionTreeData(Map.of("size", "big"), "yes"),
                    new DecisionTreeData(Map.of("size", "small"), "no")
                ),
                1.0,
                0.001
            },
            {
                List.of(
                    new DecisionTreeData(Map.of("size", "big"), "yes"),
                    new DecisionTreeData(Map.of("size", "medium"), "yes"),
                    new DecisionTreeData(Map.of("size", "tiny"), "yes"),
                    new DecisionTreeData(Map.of("size", "small"), "no")
                ),
                0.811,
                0.001
            }
        };
    }

    @Test(dataProvider="calculateEntropyData")
    public void calculateEntropyTest(final List<DecisionTreeData> data, final Double expected, final double delta) {
        Assert.assertEquals(ID3.calculateEntropy(data), expected, delta);
    }

    @DataProvider
    public Object[][] selectAttributeData() {
        return new Object[][] {
            {ID3Test.DATA1, "size"},
            {ID3Test.DATA2, "cost"}
        };
    }

    @Test(dataProvider="selectAttributeData")
    public void selectAttributeTest(final List<DecisionTreeData> data, final String expected) {
        Assert.assertEquals(ID3.selectAttribute(data), expected);
    }

    @DataProvider
    public Object[][] splitByAttributeData() {
        return new Object[][] {
            {
                "size",
                ID3Test.DATA1,
                Map.of(
                    "big", List.of(
                        new DecisionTreeData(Map.of("size", "big", "cost", "low"), "yes"),
                        new DecisionTreeData(Map.of("size", "big", "cost", "high"), "yes"),
                        new DecisionTreeData(Map.of("size", "big", "cost", "medium"), "yes")
                    ),
                    "medium", List.of(
                        new DecisionTreeData(Map.of("size", "medium", "cost", "high"), "no"),
                        new DecisionTreeData(Map.of("size", "medium", "cost", "medium"), "yes"),
                        new DecisionTreeData(Map.of("size", "medium", "cost", "low"), "yes")
                    ),
                    "small", List.of(
                        new DecisionTreeData(Map.of("size", "small", "cost", "high"), "no"),
                        new DecisionTreeData(Map.of("size", "small", "cost", "low"), "no"),
                        new DecisionTreeData(Map.of("size", "small", "cost", "medium"), "no")
                    )
                )
            },
            {
                "cost",
                ID3Test.DATA1,
                Map.of(
                    "high", List.of(
                        new DecisionTreeData(Map.of("size", "small", "cost", "high"), "no"),
                        new DecisionTreeData(Map.of("size", "medium", "cost", "high"), "no"),
                        new DecisionTreeData(Map.of("size", "big", "cost", "high"), "yes")
                    ),
                    "medium", List.of(
                        new DecisionTreeData(Map.of("size", "small", "cost", "medium"), "no"),
                        new DecisionTreeData(Map.of("size", "medium", "cost", "medium"), "yes"),
                        new DecisionTreeData(Map.of("size", "big", "cost", "medium"), "yes")
                    ),
                    "low", List.of(
                        new DecisionTreeData(Map.of("size", "big", "cost", "low"), "yes"),
                        new DecisionTreeData(Map.of("size", "small", "cost", "low"), "no"),
                        new DecisionTreeData(Map.of("size", "medium", "cost", "low"), "yes")
                    )
                )
            }
        };
    }

    @Test(dataProvider="splitByAttributeData")
    public void splitByAttributeTest(
        final String attribute,
        final List<DecisionTreeData> data,
        final Map<String, List<DecisionTreeData>> expected
    ) {
        Assert.assertEquals(ID3.splitByAttribute(attribute, data), expected);
    }

}
