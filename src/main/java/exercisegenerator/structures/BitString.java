package exercisegenerator.structures;

import java.util.*;
import java.util.stream.*;

public class BitString extends LinkedList<Bit> {

    private static final long serialVersionUID = -3160588348086277796L;

    public static BitString parse(final String bitString) {
        return
            bitString.chars()
                .mapToObj(c -> c == '0' ? Bit.ZERO : Bit.ONE)
                .collect(Collectors.toCollection(BitString::new));
    }

    public void append(final BitString toAppend) {
        for (final Bit bit : toAppend) {
            this.add(bit);
        }
    }

    public BitString increment() {
        final BitString result = new BitString();
        final Iterator<Bit> iterator = this.descendingIterator();
        boolean add = true;
        while (iterator.hasNext()) {
            final Bit current = iterator.next();
            if (add) {
                if (current.isZero()) {
                    add = false;
                }
                result.addFirst(current.invert());
            } else {
                result.addFirst(current);
            }
        }
        if (add) {
            result.addFirst(Bit.ONE);
        }
        return result;
    }

    public BitString invert() {
        return this.stream().map(b -> b.invert()).collect(Collectors.toCollection(BitString::new));
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        for (final Bit b : this) {
            result.append(b.toString());
        }
        return result.toString();
    }

    public int toUnsignedInt() {
        final Iterator<Bit> iterator = this.descendingIterator();
        int exponent = 1;
        int result = 0;
        while (iterator.hasNext()) {
            result += exponent * iterator.next().value;
            exponent *= 2;
        }
        return result;
    }

}
