package exercisegenerator.util;

import java.util.*;

public class LexicographicComparator<T> implements Comparator<T> {

    @SafeVarargs
    public static <T> int compare(final T o1, final T o2, final Comparator<T>... comparators) {
        return LexicographicComparator.compare(o1, o2, Arrays.asList(comparators));
    }

    public static <T> int compare(final T o1, final T o2, final List<Comparator<T>> comparators) {
        for (final Comparator<T> comparator : comparators) {
            final int compare = comparator.compare(o1, o2);
            if (compare != 0) {
                return compare;
            }
        }
        return 0;
    }

    private final List<Comparator<T>> comparators;

    @SafeVarargs
    public LexicographicComparator(final Comparator<T>... comparators) {
        this(Arrays.asList(comparators));
    }

    public LexicographicComparator(final List<Comparator<T>> comparators) {
        this.comparators = comparators;
    }

    @Override
    public int compare(final T o1, final T o2) {
        return LexicographicComparator.compare(o1, o2, this.comparators);
    }

}
