package exercisegenerator.structures.analysis;

import java.util.*;

import org.apache.commons.math3.fraction.*;

import exercisegenerator.io.*;

public record GeometricSeries(BigFraction factor, BigFraction base) {

    @Override
    public String toString() {
        return String.format("\\sum\\limits_{i = 1}^{\\infty} %s", this.summand());
    }

    public String summand() {
        return this.factor().compareTo(BigFraction.ONE) == 0 ?
            String.format("\\left(%s\\right)^{i - 1}", LaTeXUtils.toCoefficient(this.base())) :
                String.format(
                    "\\left(%s \\cdot (%s)^{i - 1}\\right)",
                    LaTeXUtils.toCoefficient(this.factor()),
                    LaTeXUtils.toCoefficient(this.base())
                );
    }

    public boolean converges() {
        return this.base().abs().compareTo(BigFraction.ONE) < 0;
    }

    public Optional<String> sumTerm() {
        if (this.converges()) {
            return Optional.of(
                String.format(
                    "\\frac{%s}{1 - %s}",
                    LaTeXUtils.toCoefficient(this.factor()),
                    LaTeXUtils.toCoefficient(this.base())
                )
            );
        }
        return Optional.empty();
    }

    public Optional<BigFraction> sumValue() {
        if (this.converges()) {
            return Optional.of(this.factor().divide(BigFraction.ONE.subtract(this.base())));
        }
        return Optional.empty();
    }

}
