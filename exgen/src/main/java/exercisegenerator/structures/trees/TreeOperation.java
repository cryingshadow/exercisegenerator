package exercisegenerator.structures.trees;

public record TreeOperation<T extends Comparable<T>>(T value, boolean add) {

    @Override
    public String toString() {
        return String.format("%s%s", this.value.toString(), this.add ? "+" : "-");
    }

}
