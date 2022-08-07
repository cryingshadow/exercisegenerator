package exercisegenerator.util;

import java.util.function.*;

@FunctionalInterface
public interface CheckedConsumer<T, E extends Throwable> {

    static <T, E extends Throwable> Consumer<T> unchecked(final CheckedConsumer<T, E> c) {
        return t -> {
            try {
                c.accept(t);
            } catch (final Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }

    void accept(T t) throws E;

}
