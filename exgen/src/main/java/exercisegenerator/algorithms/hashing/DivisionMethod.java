package exercisegenerator.algorithms.hashing;

public class DivisionMethod implements HashFunction {

    private final int capacity;

    public DivisionMethod(final int capacity) {
        this.capacity = capacity;
    }

    @Override
    public int apply(final int value) {
        return value % this.capacity;
    }

}
