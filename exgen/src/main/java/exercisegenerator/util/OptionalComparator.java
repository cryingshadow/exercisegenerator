package exercisegenerator.util;

import java.util.*;

public class OptionalComparator<T extends Comparable<T>> implements Comparator<Optional<T>> {

    public static <T extends Comparable<T>> int compareOptional(final Optional<T> o1, final Optional<T> o2) {
        if (o1.isEmpty()) {
            if (o2.isEmpty()) {
                return 0;
            }
            return -1;
        }
        if (o2.isEmpty()) {
            return 1;
        }
        return o1.get().compareTo(o2.get());
    }

    @Override
    public int compare(final Optional<T> o1, final Optional<T> o2) {
        return OptionalComparator.compareOptional(o1, o2);
    }

}
