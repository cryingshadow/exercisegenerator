package exercisegenerator.algorithms.hashing;

public class LinearProbing implements ProbingFunction {

    public static final LinearProbing INSTANCE = new LinearProbing();

    private LinearProbing() {}

    @Override
    public int apply(final int iteration) {
        return iteration;
    }

}
