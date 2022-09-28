package exercisegenerator.structures.trees;

public class BinaryTreeStep<T> {

    public final BinaryTreeStepType type;

    public final T value;

    public BinaryTreeStep(final BinaryTreeStepType type, final T value) {
        this.type = type;
        this.value = value;
    }

    public String toLaTeX() {
        switch (this.type) {
        case ADD:
            return String.format("f\\\"uge %s ein", this.value.toString());
        case REMOVE:
            return String.format("entferne %s", this.value.toString());
        case REPLACE:
            return String.format("ersetze %s", this.value.toString());
        case ROTATE_LEFT:
            return String.format("rotiere %s nach links", this.value.toString());
        case ROTATE_RIGHT:
            return String.format("rotiere %s nach rechts", this.value.toString());
        default:
            throw new IllegalStateException("Someone added a new step type but did not extend toLaTeX accordingly!");
        }
    }

    @Override
    public String toString() {
        return this.type.toString() + this.value.toString();
    }

}
