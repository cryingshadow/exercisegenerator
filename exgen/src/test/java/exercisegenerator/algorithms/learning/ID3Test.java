package exercisegenerator.algorithms.learning;

import java.util.*;

import org.apache.commons.math3.fraction.*;
import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.structures.learning.*;

public class ID3Test {

    private static final List<DecisionTreeDataElement> DATA1 =
        List.of(
            new DecisionTreeDataElement(Map.of("size", "big", "cost", "low"), "yes"),
            new DecisionTreeDataElement(Map.of("size", "small", "cost", "high"), "no"),
            new DecisionTreeDataElement(Map.of("size", "medium", "cost", "high"), "no"),
            new DecisionTreeDataElement(Map.of("size", "small", "cost", "low"), "no"),
            new DecisionTreeDataElement(Map.of("size", "small", "cost", "medium"), "no"),
            new DecisionTreeDataElement(Map.of("size", "medium", "cost", "medium"), "yes"),
            new DecisionTreeDataElement(Map.of("size", "big", "cost", "high"), "yes"),
            new DecisionTreeDataElement(Map.of("size", "big", "cost", "medium"), "yes"),
            new DecisionTreeDataElement(Map.of("size", "medium", "cost", "low"), "yes")
        );

    private static final List<DecisionTreeDataElement> DATA2 =
        List.of(
            new DecisionTreeDataElement(Map.of("size", "big", "cost", "low"), "yes"),
            new DecisionTreeDataElement(Map.of("size", "small", "cost", "high"), "no"),
            new DecisionTreeDataElement(Map.of("size", "medium", "cost", "high"), "no"),
            new DecisionTreeDataElement(Map.of("size", "small", "cost", "low"), "yes"),
            new DecisionTreeDataElement(Map.of("size", "small", "cost", "medium"), "no"),
            new DecisionTreeDataElement(Map.of("size", "medium", "cost", "medium"), "yes"),
            new DecisionTreeDataElement(Map.of("size", "big", "cost", "high"), "no"),
            new DecisionTreeDataElement(Map.of("size", "big", "cost", "medium"), "yes"),
            new DecisionTreeDataElement(Map.of("size", "medium", "cost", "low"), "yes")
        );

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
                        new DecisionTreeDataElement(Map.of("size", "big"), "yes"),
                        new DecisionTreeDataElement(Map.of("size", "big"), "yes"),
                        new DecisionTreeDataElement(Map.of("size", "big"), "yes"),
                        new DecisionTreeDataElement(Map.of("size", "big"), "no"),
                        new DecisionTreeDataElement(Map.of("size", "big"), "no"),
                        new DecisionTreeDataElement(Map.of("size", "big"), "no")
                    ),
                    List.of(
                        new DecisionTreeDataElement(Map.of("size", "big"), "yes"),
                        new DecisionTreeDataElement(Map.of("size", "big"), "yes"),
                        new DecisionTreeDataElement(Map.of("size", "big"), "yes"),
                        new DecisionTreeDataElement(Map.of("size", "big"), "yes"),
                        new DecisionTreeDataElement(Map.of("size", "big"), "yes"),
                        new DecisionTreeDataElement(Map.of("size", "big"), "yes"),
                        new DecisionTreeDataElement(Map.of("size", "big"), "no"),
                        new DecisionTreeDataElement(Map.of("size", "big"), "no")
                    )
                ),
                0.892,
                0.001
            }
        };
    }

    @Test(dataProvider="calculateAverageEntropyData")
    public void calculateAverageEntropyTest(
        final List<List<DecisionTreeDataElement>> data,
        final double expected,
        final double delta
    ) {
        Assert.assertEquals(ID3Algorithm.calculateAverageEntropy(data).value(), expected, delta);
    }

    @DataProvider
    public Object[][] calculateEntropyData() {
        return new Object[][] {
            {List.of(), List.of()},
            {List.of(new DecisionTreeDataElement(Map.of(), "yes")), List.of()},
            {List.of(new DecisionTreeDataElement(Map.of("size", "big"), "yes")), List.of()},
            {
                List.of(
                    new DecisionTreeDataElement(Map.of("size", "big"), "yes"),
                    new DecisionTreeDataElement(Map.of("size", "small"), "yes")
                ),
                List.of()
            },
            {
                List.of(
                    new DecisionTreeDataElement(Map.of("size", "big"), "yes"),
                    new DecisionTreeDataElement(Map.of("size", "small"), "no")
                ),
                List.of(BigFraction.ONE_HALF, BigFraction.ONE_HALF)
            },
            {
                List.of(
                    new DecisionTreeDataElement(Map.of("size", "big"), "yes"),
                    new DecisionTreeDataElement(Map.of("size", "medium"), "yes"),
                    new DecisionTreeDataElement(Map.of("size", "tiny"), "yes"),
                    new DecisionTreeDataElement(Map.of("size", "small"), "no")
                ),
                List.of(BigFraction.THREE_QUARTERS, BigFraction.ONE_QUARTER)
            }
        };
    }

    @Test(dataProvider="calculateEntropyData")
    public void calculateEntropyTest(final List<DecisionTreeDataElement> data, final List<BigFraction> expected) {
        Assert.assertEquals(ID3Algorithm.calculateEntropy(data), expected);
    }

    @DataProvider
    public Object[][] id3Data() {
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
                            "cost",
                            Map.of(
                                "cost",
                                new AverageEntropyCalculation(
                                    List.of(
                                        new EntropyCalculation(BigFraction.ONE_THIRD, List.of()),
                                        new EntropyCalculation(BigFraction.ONE_THIRD, List.of()),
                                        new EntropyCalculation(BigFraction.ONE_THIRD, List.of())
                                    )
                                )
                            ),
                            Set.of("size", "cost")
                        ),
                        "small", new DecisionTreeLeaf("no")
                    ),
                    "size",
                    Map.of(
                        "size",
                        new AverageEntropyCalculation(
                            List.of(
                                new EntropyCalculation(new BigFraction(3, 9), List.of()),
                                new EntropyCalculation(
                                    new BigFraction(3, 9),
                                    List.of(BigFraction.ONE_THIRD, BigFraction.TWO_THIRDS)
                                ),
                                new EntropyCalculation(new BigFraction(3, 9), List.of())
                            )
                        ),
                        "cost",
                        new AverageEntropyCalculation(
                            List.of(
                                new EntropyCalculation(
                                    new BigFraction(3, 9),
                                    List.of(BigFraction.TWO_THIRDS, BigFraction.ONE_THIRD)
                                ),
                                new EntropyCalculation(
                                    new BigFraction(3, 9),
                                    List.of(BigFraction.TWO_THIRDS, BigFraction.ONE_THIRD)
                                ),
                                new EntropyCalculation(
                                    new BigFraction(3, 9),
                                    List.of(BigFraction.ONE_THIRD, BigFraction.TWO_THIRDS)
                                )
                            )
                        )
                    ),
                    Set.of("size")
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
                            "size",
                            Map.of(
                                "size",
                                new AverageEntropyCalculation(
                                    List.of(
                                        new EntropyCalculation(BigFraction.ONE_THIRD, List.of()),
                                        new EntropyCalculation(BigFraction.ONE_THIRD, List.of()),
                                        new EntropyCalculation(BigFraction.ONE_THIRD, List.of())
                                    )
                                )
                            ),
                            Set.of("size", "cost")
                        ),
                        "low", new DecisionTreeLeaf("yes")
                    ),
                    "cost",
                    Map.of(
                        "size",
                        new AverageEntropyCalculation(
                            List.of(
                                new EntropyCalculation(
                                    new BigFraction(3, 9),
                                    List.of(BigFraction.TWO_THIRDS, BigFraction.ONE_THIRD)
                                ),
                                new EntropyCalculation(
                                    new BigFraction(3, 9),
                                    List.of(BigFraction.ONE_THIRD, BigFraction.TWO_THIRDS)
                                ),
                                new EntropyCalculation(
                                    new BigFraction(3, 9),
                                    List.of(BigFraction.TWO_THIRDS, BigFraction.ONE_THIRD)
                                )
                            )
                        ),
                        "cost",
                        new AverageEntropyCalculation(
                            List.of(
                                new EntropyCalculation(new BigFraction(3, 9), List.of()),
                                new EntropyCalculation(new BigFraction(3, 9), List.of()),
                                new EntropyCalculation(
                                    new BigFraction(3, 9),
                                    List.of(BigFraction.ONE_THIRD, BigFraction.TWO_THIRDS)
                                )
                            )
                        )
                    ),
                    Set.of("cost")
                )
            }
        };
    }

    @Test(dataProvider="id3Data")
    public void id3Test(final List<DecisionTreeDataElement> data, final DecisionTree expected) {
        this.assertEquals(ID3Algorithm.id3(data, Set.of()), expected);
    }

    @DataProvider
    public Object[][] splitByAttributeData() {
        return new Object[][] {
            {
                "size",
                ID3Test.DATA1,
                Map.of(
                    "big", List.of(
                        new DecisionTreeDataElement(Map.of("size", "big", "cost", "low"), "yes"),
                        new DecisionTreeDataElement(Map.of("size", "big", "cost", "high"), "yes"),
                        new DecisionTreeDataElement(Map.of("size", "big", "cost", "medium"), "yes")
                    ),
                    "medium", List.of(
                        new DecisionTreeDataElement(Map.of("size", "medium", "cost", "high"), "no"),
                        new DecisionTreeDataElement(Map.of("size", "medium", "cost", "medium"), "yes"),
                        new DecisionTreeDataElement(Map.of("size", "medium", "cost", "low"), "yes")
                    ),
                    "small", List.of(
                        new DecisionTreeDataElement(Map.of("size", "small", "cost", "high"), "no"),
                        new DecisionTreeDataElement(Map.of("size", "small", "cost", "low"), "no"),
                        new DecisionTreeDataElement(Map.of("size", "small", "cost", "medium"), "no")
                    )
                )
            },
            {
                "cost",
                ID3Test.DATA1,
                Map.of(
                    "high", List.of(
                        new DecisionTreeDataElement(Map.of("size", "small", "cost", "high"), "no"),
                        new DecisionTreeDataElement(Map.of("size", "medium", "cost", "high"), "no"),
                        new DecisionTreeDataElement(Map.of("size", "big", "cost", "high"), "yes")
                    ),
                    "medium", List.of(
                        new DecisionTreeDataElement(Map.of("size", "small", "cost", "medium"), "no"),
                        new DecisionTreeDataElement(Map.of("size", "medium", "cost", "medium"), "yes"),
                        new DecisionTreeDataElement(Map.of("size", "big", "cost", "medium"), "yes")
                    ),
                    "low", List.of(
                        new DecisionTreeDataElement(Map.of("size", "big", "cost", "low"), "yes"),
                        new DecisionTreeDataElement(Map.of("size", "small", "cost", "low"), "no"),
                        new DecisionTreeDataElement(Map.of("size", "medium", "cost", "low"), "yes")
                    )
                )
            }
        };
    }

    @Test(dataProvider="splitByAttributeData")
    public void splitByAttributeTest(
        final String attribute,
        final List<DecisionTreeDataElement> data,
        final Map<String, List<DecisionTreeDataElement>> expected
    ) {
        Assert.assertEquals(ID3Algorithm.splitByAttribute(attribute, data), expected);
    }

    private void assertEquals(final DecisionTree actual, final DecisionTree expected) {
        if (actual instanceof final DecisionTreeInnerNode actualNode) {
            if (expected instanceof final DecisionTreeInnerNode expectedNode) {
                Assert.assertEquals(actualNode.selector(), expectedNode.selector());
                Assert.assertEquals(actualNode.children().keySet(), expectedNode.children().keySet());
                Assert.assertEquals(actualNode.entropies().keySet(), expectedNode.entropies().keySet());
                for (final String key : actualNode.children().keySet()) {
                    this.assertEquals(actualNode.children().get(key), expectedNode.children().get(key));
                }
                for (final String key : actualNode.entropies().keySet()) {
                    Assert.assertEquals(actualNode.entropies().get(key), expectedNode.entropies().get(key));
                }
            } else {
                throw new AssertionError(String.format("Actual: %s, Expected: %s", actual, expected));
            }
        } else {
            Assert.assertEquals(actual, expected);
        }
    }

}
