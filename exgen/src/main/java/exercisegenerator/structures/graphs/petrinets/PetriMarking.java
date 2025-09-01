package exercisegenerator.structures.graphs.petrinets;

import java.util.*;
import java.util.stream.*;

public class PetriMarking extends ArrayList<Optional<Integer>> implements Comparable<PetriMarking> {

    private static final long serialVersionUID = 1L;

    public static PetriMarking create(final Collection<Optional<Integer>> marking) {
        final PetriMarking result = new PetriMarking();
        for (final Optional<Integer> marks : marking) {
            result.add(marks);
        }
        return result;
    }

    public static PetriMarking create(final Integer... marking) {
        return PetriMarking.create(Arrays.asList(marking));
    }

    public static PetriMarking create(final Iterable<Integer> marking) {
        final PetriMarking result = new PetriMarking();
        for (final Integer marks : marking) {
            result.add(Optional.of(marks));
        }
        return result;
    }

    @SafeVarargs
    public static PetriMarking create(final Optional<Integer>... marking) {
        final PetriMarking result = new PetriMarking();
        for (final Optional<Integer> marks : marking) {
            result.add(marks);
        }
        return result;
    }

    public static PetriMarking createZeroMarking(final int size) {
        return PetriMarking.create(Stream.generate(() -> Optional.of(0)).limit(size).toList());
    }

    public static int omegaCompare(final Optional<Integer> mark1, final Optional<Integer> mark2) {
        if (mark1.isEmpty()) {
            if (mark2.isEmpty()) {
                return 0;
            }
            return 1;
        }
        if (mark2.isEmpty()) {
            return -1;
        }
        return mark1.get().compareTo(mark2.get());
    }

    public static Optional<Integer> omegaSum(final Optional<Integer> oldValue, final Optional<Integer> newValue) {
        if (oldValue.isEmpty() || newValue.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(oldValue.get() + newValue.get());
    }

    private PetriMarking() {
        super();
    }

    private PetriMarking(final List<Optional<Integer>> marking) {
        super(marking);
    }

    @Override
    public int compareTo(final PetriMarking o) {
        if (this.size() != o.size()) {
            return Integer.compare(this.size(), o.size());
        }
        for (int i = 0; i < this.size(); i++) {
            final int compare = PetriMarking.omegaCompare(this.get(i), o.get(i));
            if (compare != 0) {
                return compare;
            }
        }
        return 0;
    }

    public boolean covers(final PetriMarking other) {
        if (this.size() != other.size()) {
            return false;
        }
        for (int i = 0; i < this.size(); i++) {
            final Optional<Integer> thisValue = this.get(i);
            final Optional<Integer> otherValue = other.get(i);
            if (otherValue.isEmpty()) {
                if (!thisValue.isEmpty()) {
                    return false;
                }
            } else if (!thisValue.isEmpty() && thisValue.get() < otherValue.get()) {
                return false;
            }
        }
        return true;
    }

    public void omegaMerge(final int index, final Optional<Integer> newValue) {
        this.set(index, PetriMarking.omegaSum(this.get(index), newValue));
    }

    public boolean strictlyCovers(final PetriMarking other) {
        return this.covers(other) && !this.equals(other);
    }

    @Override
    public String toString() {
        return String.format(
            "<$%s$>",
            this.stream()
            .map(i -> i.map(String::valueOf).orElse("\\infty"))
            .collect(Collectors.joining(","))
        );
    }

}
