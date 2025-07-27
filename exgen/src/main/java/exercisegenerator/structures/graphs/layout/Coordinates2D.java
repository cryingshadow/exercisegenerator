package exercisegenerator.structures.graphs.layout;

import java.util.function.*;

public record Coordinates2D<T extends Number>(T x, T y) {

    public Coordinates2D<T> plus(final T x, final T y, final BiFunction<T, T, T> sum) {
        return new Coordinates2D<T>(sum.apply(this.x(), x), sum.apply(this.y(), y));
    }

    public Coordinates2D<Double> plus(final Double x, final Double y) {
        return new Coordinates2D<Double>(this.x().doubleValue() + x, this.y().doubleValue() + y);
    }

    public Coordinates2D<Integer> plus(final Integer x, final Integer y) {
        return new Coordinates2D<Integer>(this.x().intValue() + x, this.y().intValue() + y);
    }

    public Coordinates2D<Double> minus(final Coordinates2D<Double> coordinates) {
        return this.plus(coordinates.negate());
    }

    public Coordinates2D<Double> negate() {
        return new Coordinates2D<Double>(-this.x().doubleValue(), -this.y().doubleValue());
    }

    public double getAngle(final Coordinates2D<T> target) {
        double angle =
            Math.toDegrees(
                Math.atan2(
                    target.y().doubleValue() - this.y().doubleValue(),
                    target.x().doubleValue() - this.x().doubleValue()
                )
            );
        while (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    public Coordinates2D<Double> plus(final Coordinates2D<Double> coordinates) {
        return new Coordinates2D<Double>(
            this.x().doubleValue() + coordinates.x(),
            this.y().doubleValue() + coordinates.y()
        );
    }

    public Coordinates2D<Double> multiply(final double scalar) {
        return new Coordinates2D<Double>(
            this.x().doubleValue() * scalar,
            this.y().doubleValue() * scalar
        );
    }

    public double euclideanSize() {
        final double x = this.x().doubleValue();
        final double y = this.y().doubleValue();
        return Math.sqrt(x * x + y * y);
    }

}
