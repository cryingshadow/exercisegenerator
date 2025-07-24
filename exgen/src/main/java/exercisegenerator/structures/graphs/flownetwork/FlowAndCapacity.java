package exercisegenerator.structures.graphs.flownetwork;

import exercisegenerator.*;

public record FlowAndCapacity(int flow, int capacity) {

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

}
