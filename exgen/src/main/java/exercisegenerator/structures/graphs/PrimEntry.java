package exercisegenerator.structures.graphs;

public record PrimEntry(int value, boolean infinity, boolean done) implements Comparable<Integer> {

    public PrimEntry(final int value) {
        this(value, false, false);
    }

    @Override
    public int compareTo(final Integer value) {
        if (this.infinity) {
            return value == null ? 0 : 1;
        }
        if (value == null) {
            return -1;
        }
        return Integer.compare(this.value, value);
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof PrimEntry) {
            final PrimEntry other = (PrimEntry)o;
            if (this.infinity) {
                return other.infinity;
            }
            return this.value == other.value && this.done == other.done;
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (this.infinity) {
            return 42;
        }
        return this.value + (this.done ? 13 : 0);
    }

    @Override
    public String toString() {
        return this.infinity ?
            "$\\infty$" :
                (this.done ? String.format("\\underline{%d}", this.value) : String.valueOf(this.value));
    }

    public PrimEntry toDone() {
        return new PrimEntry(this.value, this.infinity, true);
    }

}