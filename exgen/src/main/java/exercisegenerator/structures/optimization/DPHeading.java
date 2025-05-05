package exercisegenerator.structures.optimization;

import java.util.*;
import java.util.function.*;

public record DPHeading(int count, Function<Integer, String> titles, Function<Integer, List<String>> headings) {

    public DPHeading(final Function<Integer, String> headings) {
        this(1, i -> "", i -> List.of(headings.apply(i)));
    }

}
