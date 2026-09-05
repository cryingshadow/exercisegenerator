package exercisegenerator.structures.learning;

import java.util.*;
import java.util.stream.*;

import org.apache.commons.math3.fraction.*;

public record EntropyCalculation(BigFraction fraction, List<BigFraction> classifications) {

    public String toLaTeX() {
        if (this.classifications().isEmpty()) {
            return "0";
        }
        final StringBuilder result = new StringBuilder();
        result.append("\\frac{");
        result.append(this.fraction().getNumeratorAsInt());
        result.append("}{");
        result.append(this.fraction().getDenominatorAsInt());
        result.append("} \\cdot \\left(");
        result.append(
            this
            .classifications()
            .stream()
            .map(classification ->
                String.format(
                    "\\frac{%d}{%d} \\log_2\\left(\\frac{%d}{%d}\\right)",
                    classification.getNumeratorAsInt(),
                    classification.getDenominatorAsInt(),
                    classification.getNumeratorAsInt(),
                    classification.getDenominatorAsInt()
                )
            ).collect(Collectors.joining(" + "))
        );
        result.append("\\right)");
        return result.toString();
    }

    public double value() {
        return -this.fraction().doubleValue()
            * this.classifications().stream().mapToDouble(v -> v.doubleValue() * Math.log(v.doubleValue()) / Math.log(2)).sum();
    }

}
