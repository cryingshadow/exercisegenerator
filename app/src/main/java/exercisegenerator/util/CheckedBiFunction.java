package exercisegenerator.util;

import java.util.function.*;

@FunctionalInterface
public interface CheckedBiFunction<T, U, R, E extends Throwable> {

    static <T, U, R, E extends Throwable> BiFunction<T, U, R> unchecked(final CheckedBiFunction<T, U, R, E> f) {
        return (t, u) -> {
            try {
                return f.apply(t, u);
            } catch (final Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }

    R apply(T t, U u) throws E;

}
