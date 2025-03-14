package exercisegenerator.structures.logic;

import java.util.*;
import java.util.stream.*;

public class Clause extends TreeSet<Literal> implements Comparable<Clause> {

    public static final Clause EMPTY = new Clause();

    private static final long serialVersionUID = 1L;

    public Clause() {}

    public Clause(final Collection<Literal> collection) {
        super(collection);
    }

    public Clause(final Literal... literals) {
        super(Arrays.stream(literals).toList());
    }

    public Clause(final Stream<Literal> literals) {
        super(literals.toList());
    }

    @Override
    public int compareTo(final Clause o) {
        final Iterator<Literal> i1 = this.iterator();
        final Iterator<Literal> i2 = o.iterator();
        while (i1.hasNext() && i2.hasNext()) {
            final Literal l1 = i1.next();
            final Literal l2 = i2.next();
            final int result = l1.compareTo(l2);
            if (result != 0) {
                return result;
            }
        }
        if (i1.hasNext()) {
            return 1;
        }
        if (i2.hasNext()) {
            return -1;
        }
        return 0;
    }

    public Optional<Clause> setTruth(final PropositionalVariable variable, final boolean truth) {
        if (this.stream().anyMatch(literal -> variable.equals(literal.variable()))) {
            if (this.stream().anyMatch(literal -> variable.equals(literal.variable()) && truth != literal.negative())) {
                return Optional.empty();
            }
            return Optional.of(
                this.stream()
                .filter(literal -> !variable.equals(literal.variable()))
                .collect(Collectors.toCollection(Clause::new))
            );
        }
        return Optional.of(this);
    }

    @Override
    public String toString() {
        return String.format("\\{%s\\}", this.stream().map(Literal::toString).collect(Collectors.joining(",")));
    }

}
