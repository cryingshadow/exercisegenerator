package exercisegenerator.algorithms.hashing;

import java.io.*;
import java.util.*;

import org.apache.commons.math3.fraction.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.hashing.*;

public class HashingDivisionLinear implements Hashing {

    public static final HashingDivisionLinear INSTANCE = new HashingDivisionLinear();

    private HashingDivisionLinear() {}

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    @Override
    public HashFunctionWithParameters hashFunction(final int capacity, final Parameters options) throws IOException {
        return new HashFunctionWithParameters(
            new DivisionMethod(capacity),
            Hashing.DIVISION_METHOD,
            Map.of("m", new BigFraction(capacity)),
            text -> Main.TEXT_VERSION == TextVersion.ABRAHAM ?
                String.format(" ($f(n,i) = (n + i) \\mod %d$)", capacity) :
                    ""
        );
    }

    @Override
    public Optional<ProbingFunctionWithParameters> optionalProbingFunction(
        final int capacity,
        final Parameters options
    ) throws IOException {
        return Optional.of(
            new ProbingFunctionWithParameters(
                LinearProbing.INSTANCE,
                Hashing.LINEAR_PROBING,
                Map.of(),
                ""
            )
        );
    }

}
