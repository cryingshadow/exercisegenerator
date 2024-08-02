package exercisegenerator.algorithms;

import java.util.*;
import java.util.stream.*;

import org.testng.*;
import org.testng.annotations.Test;

public class AlgorithmTest {

    @Test
    public void uniqueNamesTest() {
        final Set<String> names = Arrays.stream(Algorithm.values()).map(Algorithm::name).collect(Collectors.toSet());
        Assert.assertEquals(names.size(), Algorithm.values().length);
    }

}
