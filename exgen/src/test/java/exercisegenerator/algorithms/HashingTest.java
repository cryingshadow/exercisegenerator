package exercisegenerator.algorithms;

import java.util.*;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.structures.*;
import exercisegenerator.util.*;

public class HashingTest {

    private static class HashingTestData {
        private final HashList[] expectedResult;
        private final CheckedSupplier<HashList[], HashException> hashingMethod;

        private HashingTestData(
            final CheckedSupplier<HashList[], HashException> hashingMethod,
            final HashList[] expectedResult
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
                new HashList[] {
                    new HashList(),
                    new HashList(1),
                    new HashList(),
                    new HashList(3),
                    new HashList(4),
                    new HashList(),
                    new HashList(),
                    new HashList(7),
                    new HashList(8),
                    new HashList(),
                    new HashList()
                }
            )},
            {new HashingTestData(
                () -> Hashing.hashingWithDivisionMethod(
                    Arrays.asList(7,4,3,8,1),
                    Hashing.createEmptyArray(7),
                    java.util.Optional.empty()
                ),
                new HashList[] {
                    new HashList(7),
                    new HashList(8,1),
                    new HashList(),
                    new HashList(3),
                    new HashList(4),
                    new HashList(),
                    new HashList()
                }
            )},
            {new HashingTestData(
                () -> Hashing.hashingWithDivisionMethod(
                    Arrays.asList(7,4,3,8,1),
                    Hashing.createEmptyArray(7),
                    java.util.Optional.of(Hashing.linearProbing())
                ),
                new HashList[] {
                    new HashList(7),
                    new HashList(8),
                    new HashList(1),
                    new HashList(3),
                    new HashList(4),
                    new HashList(),
                    new HashList()
                }
            )},
            {new HashingTestData(
                () -> Hashing.hashingWithDivisionMethod(
                    Arrays.asList(7,4,3,8,1,2),
                    Hashing.createEmptyArray(7),
                    java.util.Optional.of(Hashing.linearProbing())
                ),
                new HashList[] {
                    new HashList(7),
                    new HashList(8),
                    new HashList(1),
                    new HashList(3),
                    new HashList(4),
                    new HashList(2),
                    new HashList()
                }
            )},
            {new HashingTestData(
                () -> Hashing.hashingWithDivisionMethod(
                    Arrays.asList(7,4,3,8,1,2),
                    Hashing.createEmptyArray(7),
                    java.util.Optional.of(Hashing.quadraticProbing(3, 7))
                ),
                new HashList[] {
                    new HashList(7),
                    new HashList(8),
                    new HashList(2),
                    new HashList(3),
                    new HashList(4),
                    new HashList(),
                    new HashList(1)
                }
            )},
            {new HashingTestData(
                () -> Hashing.hashingWithDivisionMethod(
                    Arrays.asList(7,4,3,8,1,2),
                    Hashing.createEmptyArray(7),
                    java.util.Optional.of(Hashing.quadraticProbing(5, 2))
                ),
                new HashList[] {
                    new HashList(7),
                    new HashList(8),
                    new HashList(2),
                    new HashList(3),
                    new HashList(4),
                    new HashList(1),
                    new HashList()
                }
            )},
            {new HashingTestData(
                () -> Hashing.hashingWithMultiplicationMethod(
                    Arrays.asList(7,4,3,8,1),
                    Hashing.createEmptyArray(7),
                    0.3,
                    java.util.Optional.empty()
                ),
                new HashList[] {
                    new HashList(7),
                    new HashList(4),
                    new HashList(8,1),
                    new HashList(),
                    new HashList(),
                    new HashList(),
                    new HashList(3)
                }
            )},
            {new HashingTestData(
                () -> Hashing.hashingWithMultiplicationMethod(
                    Arrays.asList(7,4,3,8,1),
                    Hashing.createEmptyArray(7),
                    0.5,
                    java.util.Optional.of(Hashing.quadraticProbing(3, 2))
                ),
                new HashList[] {
                    new HashList(4),
                    new HashList(3),
                    new HashList(1),
                    new HashList(7),
                    new HashList(),
                    new HashList(8),
                    new HashList()
                }
            )}
        };
    }

    private static boolean equalHashResult(final HashList[] result, final HashList[] expectedResult) {
        if (result == null) {
            return expectedResult == null;
        }
        if (expectedResult == null || result.length != expectedResult.length) {
            return false;
        }
        for (int i = 0; i < result.length; i++) {
            final HashList resultList = result[i];
            final HashList expectedList = expectedResult[i];
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
