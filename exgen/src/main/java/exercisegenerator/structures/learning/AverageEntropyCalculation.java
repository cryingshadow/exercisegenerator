package exercisegenerator.structures.learning;

import java.util.*;
import java.util.stream.*;

public class AverageEntropyCalculation extends LinkedList<EntropyCalculation> {

    private static final long serialVersionUID = 1L;

    public AverageEntropyCalculation() {
        super();
    }

    public AverageEntropyCalculation(final Collection<? extends EntropyCalculation> c) {
        super(c);
    }

    public String toLaTeX() {
        return String.format(
            "%s$\\\\$\\approx %.3f",
            this.stream().map(EntropyCalculation::toLaTeX).collect(Collectors.joining("$\\\\${} + ")),
            this.value()
        );
    }

    public double value() {
        return this.stream().mapToDouble(EntropyCalculation::value).sum();
    }

}
