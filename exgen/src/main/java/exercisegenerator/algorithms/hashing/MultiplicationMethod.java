package exercisegenerator.algorithms.hashing;

import org.apache.commons.math3.fraction.*;

import exercisegenerator.structures.hashing.*;

public class MultiplicationMethod implements HashFunction {

    private final int capacity;

    private final BigFraction factor;

    public MultiplicationMethod(final int capacity, final BigFraction factor) {
        this.capacity = capacity;
        this.factor = factor;
    }

    @Override
    public int apply(final int value) {
        return this.factor
            .multiply(value)
            .subtract(this.factor.multiply(value).intValue())
            .multiply(this.capacity)
            .intValue();
    }

}
