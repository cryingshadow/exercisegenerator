package exercisegenerator.structures;

import java.util.*;
import java.util.stream.*;

public class BitString extends LinkedList<Bit> {

    private static final long serialVersionUID = -3160588348086277796L;

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

}
