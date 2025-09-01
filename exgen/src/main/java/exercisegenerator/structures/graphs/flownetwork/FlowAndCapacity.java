package exercisegenerator.structures.graphs.flownetwork;

import exercisegenerator.*;
import exercisegenerator.util.*;

public record FlowAndCapacity(int flow, int capacity) implements Comparable<FlowAndCapacity> {

    @Override
    public String toString() {
        switch (Main.TEXT_VERSION) {
            case ABRAHAM:
                return (this.flow() > 0 ? this.flow() + "/" : "") + this.capacity();
            case GENERAL:
                return this.flow() + "/" + this.capacity();
            default:
                throw new IllegalStateException("Unkown text version!");
        }
    }

    @Override
    public int compareTo(final FlowAndCapacity o) {
        return LexicographicComparator.compare(
            this,
            o,
            (o1, o2) -> Integer.compare(o1.flow(), o2.flow()),
            (o1, o2) -> Integer.compare(o1.capacity(), o2.capacity())
        );
    }

}
