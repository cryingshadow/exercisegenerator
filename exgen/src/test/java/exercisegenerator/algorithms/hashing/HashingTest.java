package exercisegenerator.algorithms.hashing;

import java.io.*;
import java.util.*;
import java.util.Optional;

import org.apache.commons.math3.fraction.*;
import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.io.Parameters;
import exercisegenerator.structures.*;
import exercisegenerator.structures.hashing.*;

public class HashingTest {

    @DataProvider
    public static Object[][] testData() throws IOException {
        final Parameters options = new Parameters();
        final HashFunctionWithParameters div7 = HashingDivisionOpen.INSTANCE.hashFunction(7, options);
        return new Object[][] {
            {
                new HashProblem(
                    Hashing.createEmptyArray(11),
                    Arrays.asList(7,4,3,8,1),
                    HashingDivisionOpen.INSTANCE.hashFunction(11, options),
                    Optional.empty()
                ),
                new IntegerList[] {
                    new IntegerList(),
                    new IntegerList(1),
                    new IntegerList(),
                    new IntegerList(3),
                    new IntegerList(4),
                    new IntegerList(),
                    new IntegerList(),
                    new IntegerList(7),
                    new IntegerList(8),
                    new IntegerList(),
                    new IntegerList()
                }
            },
            {
                new HashProblem(
                    Hashing.createEmptyArray(7),
                    Arrays.asList(7,4,3,8,1),
                    div7,
                    Optional.empty()
                ),
                new IntegerList[] {
                    new IntegerList(7),
                    new IntegerList(8,1),
                    new IntegerList(),
                    new IntegerList(3),
                    new IntegerList(4),
                    new IntegerList(),
                    new IntegerList()
                }
            },
            {
                new HashProblem(
                    Hashing.createEmptyArray(7),
                    Arrays.asList(7,4,3,8,1),
                    div7,
                    Optional.of(new ProbingFunctionWithParameters(LinearProbing.INSTANCE, "", Map.of(), ""))
                ),
                new IntegerList[] {
                    new IntegerList(7),
                    new IntegerList(8),
                    new IntegerList(1),
                    new IntegerList(3),
                    new IntegerList(4),
                    new IntegerList(),
                    new IntegerList()
                }
            },
            {
                new HashProblem(
                    Hashing.createEmptyArray(7),
                    Arrays.asList(7,4,3,8,1,2),
                    div7,
                    Optional.of(new ProbingFunctionWithParameters(LinearProbing.INSTANCE, "", Map.of(), ""))
                ),
                new IntegerList[] {
                    new IntegerList(7),
                    new IntegerList(8),
                    new IntegerList(1),
                    new IntegerList(3),
                    new IntegerList(4),
                    new IntegerList(2),
                    new IntegerList()
                }
            },
            {
                new HashProblem(
                    Hashing.createEmptyArray(7),
                    Arrays.asList(7,4,3,8,1,2),
                    div7,
                    Optional.of(
                        new ProbingFunctionWithParameters(
                            new QuadraticProbing(new BigFraction(3), new BigFraction(7)),
                            "",
                            Map.of(),
                            ""
                        )
                    )
                ),
                new IntegerList[] {
                    new IntegerList(7),
                    new IntegerList(8),
                    new IntegerList(2),
                    new IntegerList(3),
                    new IntegerList(4),
                    new IntegerList(),
                    new IntegerList(1)
                }
            },
            {
                new HashProblem(
                    Hashing.createEmptyArray(7),
                    Arrays.asList(7,4,3,8,1,2),
                    div7,
                    Optional.of(
                        new ProbingFunctionWithParameters(
                            new QuadraticProbing(new BigFraction(5), new BigFraction(2)),
                            "",
                            Map.of(),
                            ""
                        )
                    )
                ),
                new IntegerList[] {
                    new IntegerList(7),
                    new IntegerList(8),
                    new IntegerList(2),
                    new IntegerList(3),
                    new IntegerList(4),
                    new IntegerList(1),
                    new IntegerList()
                }
            },
            {
                new HashProblem(
                    Hashing.createEmptyArray(7),
                    Arrays.asList(7,4,3,8,1),
                    new HashFunctionWithParameters(
                        new MultiplicationMethod(7, new BigFraction(3, 10)),
                        "",
                        Map.of(),
                        text -> ""
                    ),
                    Optional.empty()
                ),
                new IntegerList[] {
                    new IntegerList(7),
                    new IntegerList(4),
                    new IntegerList(8,1),
                    new IntegerList(),
                    new IntegerList(),
                    new IntegerList(),
                    new IntegerList(3)
                }
            },
            {
                new HashProblem(
                    Hashing.createEmptyArray(7),
                    Arrays.asList(7,4,3,8,1),
                    new HashFunctionWithParameters(
                        new MultiplicationMethod(7, BigFraction.ONE_HALF),
                        "",
                        Map.of(),
                        text -> ""
                    ),
                    Optional.of(
                        new ProbingFunctionWithParameters(
                            new QuadraticProbing(new BigFraction(3), new BigFraction(2)),
                            "",
                            Map.of(),
                            ""
                        )
                    )
                ),
                new IntegerList[] {
                    new IntegerList(4),
                    new IntegerList(3),
                    new IntegerList(1),
                    new IntegerList(7),
                    new IntegerList(),
                    new IntegerList(8),
                    new IntegerList()
                }
            }
        };
    }

    private static boolean equalHashResult(final IntegerList[] result, final IntegerList[] expectedResult) {
        if (result == null) {
            return expectedResult == null;
        }
        if (expectedResult == null || result.length != expectedResult.length) {
            return false;
        }
        for (int i = 0; i < result.length; i++) {
            final IntegerList resultList = result[i];
            final IntegerList expectedList = expectedResult[i];
            if (
                resultList == null && expectedList != null
                || resultList != null && expectedList == null
                || !resultList.equals(expectedList)
            ) {
                return false;
            }
        }
        return true;
    }


    @Test(dataProvider = "testData")
    public void hashing(final HashProblem problem, final IntegerList[] expected) {
        Assert.assertTrue(HashingTest.equalHashResult(Hashing.hashing(problem).result(), expected));
    }

    @Test
    public void hashingWithException() {
        final Parameters options = new Parameters();
        Assert.assertThrows(
            IllegalArgumentException.class,
            () -> Hashing.hashing(
                new HashProblem(
                    Hashing.createEmptyArray(5),
                    List.of(1, 2, 3, 4, 5, 6),
                    HashingDivisionOpen.INSTANCE.hashFunction(5, options),
                    Optional.of(new ProbingFunctionWithParameters(LinearProbing.INSTANCE, "", Map.of(), ""))
                )
            )
        );
        Assert.assertThrows(
            IllegalArgumentException.class,
            () -> Hashing.hashing(
                new HashProblem(
                    Hashing.createEmptyArray(5),
                    List.of(1, 2, 3, 4, 5, 6),
                    new HashFunctionWithParameters(
                        new MultiplicationMethod(5, BigFraction.ONE_HALF),
                        "",
                        Map.of(),
                        text -> ""
                    ),
                    Optional.of(new ProbingFunctionWithParameters(LinearProbing.INSTANCE, "", Map.of(), ""))
                )
            )
        );
        Assert.assertThrows(
            IllegalArgumentException.class,
            () -> Hashing.hashing(
                new HashProblem(
                    Hashing.createEmptyArray(5),
                    List.of(1, 2, 3, 4, 5, 6),
                    HashingDivisionOpen.INSTANCE.hashFunction(5, options),
                    Optional.of(
                        new ProbingFunctionWithParameters(
                            new QuadraticProbing(BigFraction.ONE, BigFraction.ONE),
                            "",
                            Map.of(),
                            ""
                        )
                    )
                )
            )
        );
        Assert.assertThrows(
            IllegalArgumentException.class,
            () -> Hashing.hashing(
                new HashProblem(
                    Hashing.createEmptyArray(5),
                    List.of(1, 2, 3, 4, 5, 6),
                    new HashFunctionWithParameters(
                        new MultiplicationMethod(5, BigFraction.ONE_HALF),
                        "",
                        Map.of(),
                        text -> ""
                    ),
                    Optional.of(
                        new ProbingFunctionWithParameters(
                            new QuadraticProbing(BigFraction.ONE, BigFraction.ONE),
                            "",
                            Map.of(),
                            ""
                        )
                    )
                )
            )
        );
    }

}
