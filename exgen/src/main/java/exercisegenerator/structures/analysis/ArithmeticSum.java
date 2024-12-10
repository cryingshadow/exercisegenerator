package exercisegenerator.structures.analysis;

import org.apache.commons.math3.fraction.*;

import exercisegenerator.io.*;

public record ArithmeticSum(BigFraction start, BigFraction difference, int limit) {

    @Override
    public String toString() {
        return String.format(
            "\\sum\\limits_{i = 1}^{%d} %s(i - 1)%s%s",
            this.limit(),
            this.startIsZero() ? "" : String.format("\\left(%s + ", LaTeXUtils.toCoefficient(this.start())),
            this.differenceIsOne() ? "" : String.format(" \\cdot %s", LaTeXUtils.toCoefficient(this.difference())),
            this.startIsZero() ? "" : "\\right)"
        );
    }

    private boolean startIsZero() {
        return this.start().compareTo(BigFraction.ZERO) == 0;
    }

    private boolean differenceIsOne() {
        return this.difference().compareTo(BigFraction.ONE) == 0;
    }

    public String sumTerm() {
        return String.format(
            "\\frac{%d \\cdot \\left(%s%s%d - 1%s\\right)}{2}",
            this.limit(),
            this.startIsZero() ? "" : String.format("2 \\cdot %s + ", LaTeXUtils.toCoefficient(this.start())),
            this.differenceIsOne() ? "" : "(",
            this.limit(),
            this.differenceIsOne() ? "" : String.format(") \\cdot %s", LaTeXUtils.toCoefficient(this.difference()))
        );
    }

    public BigFraction sumValue() {
        return this.start()
            .multiply(2)
            .add(this.difference().multiply(this.limit() - 1))
            .multiply(this.limit())
            .divide(2);
    }

}
