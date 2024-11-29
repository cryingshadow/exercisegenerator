package exercisegenerator.algorithms.hashing;

import org.apache.commons.math3.fraction.*;

import exercisegenerator.algorithms.hashing.Hashing.*;

public class QuadraticProbing implements ProbingFunction {

    private final BigFraction linearFactor;

    private final BigFraction quadraticFactor;

    public QuadraticProbing(final BigFraction linearFactor, final BigFraction quadraticFactor) {
        this.linearFactor = linearFactor;
        this.quadraticFactor = quadraticFactor;
    }

    public QuadraticProbing(final ProbingFactors factors) {
        this(factors.linearProbingFactor, factors.quadraticProbingFactor);
    }

    @Override
    public int apply(final int iteration) {
        return this.linearFactor
            .multiply(iteration)
            .add(this.quadraticFactor.multiply(iteration * iteration))
            .intValue();
    }

}
