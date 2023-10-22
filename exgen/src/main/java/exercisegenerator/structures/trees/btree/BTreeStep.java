package exercisegenerator.structures.trees.btree;

public class BTreeStep<T> {

    public final BTreeStepType type;

    public final T value;

    public BTreeStep(final BTreeStepType type, final T value) {
        this.type = type;
        this.value = value;
    }

    public String toLaTeX() {
        switch (this.type) {
        case ADD:
            return String.format("f\\\"uge %s ein", this.value.toString());
        case MERGE:
            return String.format("verschmelze die Kinder von %s", this.value.toString());
        case REMOVE:
            return String.format("entferne %s", this.value.toString());
        case ROTATE_LEFT:
            return String.format("rotiere %s nach links", this.value.toString());
        case ROTATE_RIGHT:
            return String.format("rotiere %s nach rechts", this.value.toString());
        case SPLIT:
            return String.format("spalte %s auf", this.value.toString());
        case STEAL_LEFT:
            return String.format("stehle Vorg\\\"anger von %s", this.value.toString());
        case STEAL_RIGHT:
            return String.format("stehle Nachfolger von %s", this.value.toString());
        default:
            throw new IllegalStateException("Someone added a new step type but did not extend toLaTeX accordingly!");
        }
    }

    @Override
    public String toString() {
        return this.type.toString() + this.value.toString();
    }

}
