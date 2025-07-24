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

}
