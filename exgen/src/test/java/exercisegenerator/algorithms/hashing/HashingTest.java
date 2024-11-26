package exercisegenerator.algorithms.hashing;

import java.util.*;

import org.apache.commons.math3.fraction.*;
import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.structures.*;
import exercisegenerator.util.*;

public class HashingTest {

    private static class HashingTestData {
        private final IntegerList[] expectedResult;
        private final CheckedSupplier<IntegerList[], HashException> hashingMethod;

        private HashingTestData(
            final CheckedSupplier<IntegerList[], HashException> hashingMethod,
            final IntegerList[] expectedResult
        ) {
            this.hashingMethod = hashingMethod;
            this.expectedResult = expectedResult;
        }
    }

    @DataProvider
    public static Object[][] testData() {
        return new Object[][] {
            {new HashingTestData(
                () -> Hashing.hashingWithDivisionMethod(
                    Arrays.asList(7,4,3,8,1),
                    Hashing.createEmptyArray(11),
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
                () -> Hashing.hashingWithDivisionMethod(
                    Arrays.asList(7,4,3,8,1),
                    Hashing.createEmptyArray(7),
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
                () -> Hashing.hashingWithDivisionMethod(
                    Arrays.asList(7,4,3,8,1),
                    Hashing.createEmptyArray(7),
                    java.util.Optional.of(Hashing.linearProbing())
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
                () -> Hashing.hashingWithDivisionMethod(
                    Arrays.asList(7,4,3,8,1,2),
                    Hashing.createEmptyArray(7),
                    java.util.Optional.of(Hashing.linearProbing())
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
                () -> Hashing.hashingWithDivisionMethod(
                    Arrays.asList(7,4,3,8,1,2),
                    Hashing.createEmptyArray(7),
                    java.util.Optional.of(Hashing.quadraticProbing(new BigFraction(3), new BigFraction(7)))
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
                () -> Hashing.hashingWithDivisionMethod(
                    Arrays.asList(7,4,3,8,1,2),
                    Hashing.createEmptyArray(7),
                    java.util.Optional.of(Hashing.quadraticProbing(new BigFraction(5), new BigFraction(2)))
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
                () -> Hashing.hashingWithMultiplicationMethod(
                    Arrays.asList(7,4,3,8,1),
                    Hashing.createEmptyArray(7),
                    new BigFraction(3, 10),
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
                () -> Hashing.hashingWithMultiplicationMethod(
                    Arrays.asList(7,4,3,8,1),
                    Hashing.createEmptyArray(7),
                    new BigFraction(1, 2),
                    java.util.Optional.of(Hashing.quadraticProbing(new BigFraction(3), new BigFraction(2)))
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
        Assert.assertTrue(HashingTest.equalHashResult(data.hashingMethod.get(), data.expectedResult));
    }

}
