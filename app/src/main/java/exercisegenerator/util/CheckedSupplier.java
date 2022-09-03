package exercisegenerator.util;

import java.util.function.*;

@FunctionalInterface
public interface CheckedSupplier<T, E extends Throwable> {

    static <T, E extends Throwable> Supplier<T> unchecked(final CheckedSupplier<T, E> c) {
        return () -> {
            try {
                return c.get();
            } catch (final Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }

    T get() throws E;

}
