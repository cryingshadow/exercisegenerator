package exercisegenerator.algorithms.hashing;

import java.util.*;

import org.apache.commons.math3.fraction.*;
import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.algorithms.hashing.Hashing.*;
import exercisegenerator.structures.*;
import exercisegenerator.util.*;

public class HashingTest {

    private static class HashingTestData {
        private final IntegerList[] expectedResult;
        private final CheckedSupplier<HashResult, HashException> hashingMethod;

        private HashingTestData(
            final CheckedSupplier<HashResult, HashException> hashingMethod,
            final IntegerList[] expectedResult
        ) {
            this.hashingMethod = hashingMethod;
            this.expectedResult = expectedResult;
        }
    }

    @DataProvider
    public static Object[][] testData() {
        final HashFunction div7 = new DivisionMethod(7);
        return new Object[][] {
            {new HashingTestData(
                () -> Hashing.hashing(
                    Arrays.asList(7,4,3,8,1),
                    Hashing.createEmptyArray(11),
                    new DivisionMethod(11),
                    java.util.Optional.empty()
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
            )},
            {new HashingTestData(
                () -> Hashing.hashing(
                    Arrays.asList(7,4,3,8,1),
                    Hashing.createEmptyArray(7),
                    div7,
                    java.util.Optional.empty()
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
            )},
            {new HashingTestData(
                () -> Hashing.hashing(
                    Arrays.asList(7,4,3,8,1),
                    Hashing.createEmptyArray(7),
                    div7,
                    java.util.Optional.of(LinearProbing.INSTANCE)
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
            )},
            {new HashingTestData(
                () -> Hashing.hashing(
                    Arrays.asList(7,4,3,8,1,2),
                    Hashing.createEmptyArray(7),
                    div7,
                    java.util.Optional.of(LinearProbing.INSTANCE)
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
            )},
            {new HashingTestData(
                () -> Hashing.hashing(
                    Arrays.asList(7,4,3,8,1,2),
                    Hashing.createEmptyArray(7),
                    div7,
                    java.util.Optional.of(new QuadraticProbing(new BigFraction(3), new BigFraction(7)))
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
            )},
            {new HashingTestData(
                () -> Hashing.hashing(
                    Arrays.asList(7,4,3,8,1,2),
                    Hashing.createEmptyArray(7),
                    div7,
                    java.util.Optional.of(new QuadraticProbing(new BigFraction(5), new BigFraction(2)))
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
            )},
            {new HashingTestData(
                () -> Hashing.hashing(
                    Arrays.asList(7,4,3,8,1),
                    Hashing.createEmptyArray(7),
                    new MultiplicationMethod(7, new BigFraction(3, 10)),
                    java.util.Optional.empty()
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
            )},
            {new HashingTestData(
                () -> Hashing.hashing(
                    Arrays.asList(7,4,3,8,1),
                    Hashing.createEmptyArray(7),
                    new MultiplicationMethod(7, new BigFraction(1, 2)),
                    java.util.Optional.of(new QuadraticProbing(new BigFraction(3), new BigFraction(2)))
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
            )}
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
    public void hashing(final HashingTestData data) throws HashException {
        Assert.assertTrue(HashingTest.equalHashResult(data.hashingMethod.get().result, data.expectedResult));
    }

    @Test
    public void hashingWithException() throws HashException {
        Assert.assertThrows(
            HashException.class,
            () -> Hashing.hashing(
                List.of(1, 2, 3, 4, 5, 6),
                Hashing.createEmptyArray(5),
                new DivisionMethod(5),
                java.util.Optional.of(LinearProbing.INSTANCE)
            )
        );
        Assert.assertThrows(
            HashException.class,
            () -> Hashing.hashing(
                List.of(1, 2, 3, 4, 5, 6),
                Hashing.createEmptyArray(5),
                new MultiplicationMethod(5, BigFraction.ONE_HALF),
                java.util.Optional.of(LinearProbing.INSTANCE)
            )
        );
        Assert.assertThrows(
            HashException.class,
            () -> Hashing.hashing(
                List.of(1, 2, 3, 4, 5, 6),
                Hashing.createEmptyArray(5),
                new DivisionMethod(5),
                java.util.Optional.of(new QuadraticProbing(BigFraction.ONE, BigFraction.ONE))
            )
        );
        Assert.assertThrows(
            HashException.class,
            () -> Hashing.hashing(
                List.of(1, 2, 3, 4, 5, 6),
                Hashing.createEmptyArray(5),
                new MultiplicationMethod(5, BigFraction.ONE_HALF),
                java.util.Optional.of(new QuadraticProbing(BigFraction.ONE, BigFraction.ONE))
            )
        );
    }

}
