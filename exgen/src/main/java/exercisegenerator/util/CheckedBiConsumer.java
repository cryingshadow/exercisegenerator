package exercisegenerator.util;

import java.util.function.*;

@FunctionalInterface
public interface CheckedBiConsumer<T, U, E extends Throwable> {

    static <T, U, E extends Throwable> BiConsumer<T, U> unchecked(final CheckedBiConsumer<T, U, E> c) {
        return (t, u) -> {
            try {
                c.accept(t, u);
            } catch (final Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }

    void accept(T t, U u) throws E;

}
