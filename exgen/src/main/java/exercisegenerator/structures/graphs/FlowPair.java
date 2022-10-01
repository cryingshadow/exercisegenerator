package exercisegenerator.structures.graphs;

import exercisegenerator.*;
import exercisegenerator.structures.*;

/**
 * Pair used for flow networks. Just overrides the toString method.
 */
public class FlowPair extends Pair<Integer, Integer> {

    private static final long serialVersionUID = -4603924585579470577L;

    public FlowPair(final Integer flow, final Integer capacity) {
        super(flow, capacity);
    }

    @Override
    public String toString() {
        switch (Main.TEXT_VERSION) {
            case ABRAHAM:
                return (this.x > 0 ? this.x + "/" : "") + this.y;
            case GENERAL:
                return this.x + "/" + this.y;
            default:
                throw new IllegalStateException("Unkown text version!");
        }
    }

}
