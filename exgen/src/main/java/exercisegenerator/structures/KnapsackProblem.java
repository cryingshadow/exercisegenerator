package exercisegenerator.structures;

public class KnapsackProblem {

    public final int capacity;
    public final int[] values;
    public final int[] weights;

    public KnapsackProblem(final int[] weights, final int[] values, final int capacity) {
        this.weights = weights;
        this.values = values;
        this.capacity = capacity;
    }

}
