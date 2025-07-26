package exercisegenerator.structures.graphs.petrinets;

import java.util.*;
import java.util.stream.*;

public class PetriMarking extends LinkedHashMap<Integer, Optional<Integer>> {

    private static final long serialVersionUID = 1L;

    public static PetriMarking create(final Map<Integer, Integer> marking) {
        final PetriMarking result = new PetriMarking();
        for (final Map.Entry<Integer, Integer> entry : marking.entrySet()) {
            result.put(entry.getKey(), Optional.of(entry.getValue()));
        }
        return result;
    }

    public static Optional<Integer> omegaSum(final Optional<Integer> oldValue, final Optional<Integer> newValue) {
        if (oldValue.isEmpty() || newValue.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(oldValue.get() + newValue.get());
    }

    public PetriMarking() {
        super();
    }

    public PetriMarking(final Map<Integer, Optional<Integer>> marking) {
        super(marking);
    }

    public boolean covers(final PetriMarking other) {
        for (final Map.Entry<Integer, Optional<Integer>> entry : other.entrySet()) {
            if (!this.containsKey(entry.getKey())) {
                return false;
            }
            final Optional<Integer> thisValue = this.get(entry.getKey());
            if (entry.getValue().isEmpty()) {
                if (!thisValue.isEmpty()) {
                    return false;
                }
            } else if (!thisValue.isEmpty() && thisValue.get() < entry.getValue().get()) {
                return false;
            }
        }
        return true;
    }

    public boolean strictlyCovers(final PetriMarking other) {
        return this.covers(other) && !this.equals(other);
    }

    @Override
    public String toString() {
        final List<Integer> indices = new ArrayList<Integer>(this.keySet());
        Collections.sort(indices);
        return String.format(
            "<$%s$>",
            indices.stream()
            .map(i -> this.get(i).map(String::valueOf).orElse("\\infty"))
            .collect(Collectors.joining(","))
        );
    }

}
