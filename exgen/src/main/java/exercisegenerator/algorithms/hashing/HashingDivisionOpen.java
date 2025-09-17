package exercisegenerator.algorithms.hashing;

import java.io.*;
import java.util.*;

import org.apache.commons.math3.fraction.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.hashing.*;

public class HashingDivisionOpen implements Hashing {

    public static final HashingDivisionOpen INSTANCE = new HashingDivisionOpen();

    private HashingDivisionOpen() {}

    @Override
    public String commandPrefix() {
        return "HashingDivisionOpen";
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    @Override
    public HashFunctionWithParameters hashFunction(
        final int capacity,
        final Parameters<Flag> options
    ) throws IOException {
        return new HashFunctionWithParameters(
            new DivisionMethod(capacity),
            Hashing.DIVISION_METHOD,
            Map.of("m", new BigFraction(capacity)),
            text -> Main.TEXT_VERSION == TextVersion.ABRAHAM ? String.format(" ($f(n) = n \\mod %d$)", capacity) : ""
        );
    }

    @Override
    public Optional<ProbingFunctionWithParameters> optionalProbingFunction(
        final int capacity,
        final Parameters<Flag> options
    ) throws IOException {
        return Optional.empty();
    }

}
