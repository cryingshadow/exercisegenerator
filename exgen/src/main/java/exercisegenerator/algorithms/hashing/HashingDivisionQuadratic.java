package exercisegenerator.algorithms.hashing;

import java.io.*;
import java.util.*;

import org.apache.commons.math3.fraction.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.hashing.*;

public class HashingDivisionQuadratic implements Hashing {

    public static final HashingDivisionQuadratic INSTANCE = new HashingDivisionQuadratic();

    private HashingDivisionQuadratic() {}

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
                String.format(" ($f(n,i) = (n + %s) \\mod %d$)", text, capacity) :
                    ""
        );
    }

    @Override
    public Optional<ProbingFunctionWithParameters> optionalProbingFunction(
        final int capacity,
        final Parameters options
    ) throws IOException {
        final ProbingFactors probingFactors = Hashing.parseOrGenerateProbingFactors(capacity, options);
        final Map<String, BigFraction> parameters = new LinkedHashMap<String, BigFraction>();
        parameters.put("c_1", probingFactors.linearProbingFactor);
        parameters.put("c_2", probingFactors.quadraticProbingFactor);
        return Optional.of(
            new ProbingFunctionWithParameters(
                new QuadraticProbing(probingFactors),
                Hashing.toQuadraticProbingText(
                    probingFactors.linearProbingFactor,
                    probingFactors.quadraticProbingFactor
                ),
                parameters,
                String.format(
                    "\\lfloor %s \\cdot i + %s \\cdot i^2\\rfloor",
                    LaTeXUtils.toCoefficient(probingFactors.linearProbingFactor),
                    LaTeXUtils.toCoefficient(probingFactors.quadraticProbingFactor)
                )
            )
        );
    }

}
