package exercisegenerator.util;

import java.util.function.*;

@FunctionalInterface
public interface CheckedFunction<T, R, E extends Throwable> {

    static <T, R, E extends Throwable> Function<T, R> unchecked(final CheckedFunction<T, R, E> f) {
        return t -> {
            try {
                return f.apply(t);
            } catch (final Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }

    R apply(T t) throws E;

}
