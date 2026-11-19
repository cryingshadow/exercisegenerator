package exercisegenerator.structures.geometry;

import org.apache.commons.math3.fraction.*;

import exercisegenerator.io.*;

public record Point(BigFraction x, BigFraction y) {

    public double angleTo(final Point p) {
        final BigFraction diffX = p.x.subtract(this.x);
        final BigFraction diffY = p.y.subtract(this.y);
        return Math.acos(
            diffX.doubleValue() / Math.sqrt(diffX.multiply(diffX).add(diffY.multiply(diffY)).doubleValue())
        );
    }

    public String toLaTeX() {
        return String.format("(%s, %s)", LaTeXUtils.toCoefficient(this.x()), LaTeXUtils.toCoefficient(this.y()));
    }

}
