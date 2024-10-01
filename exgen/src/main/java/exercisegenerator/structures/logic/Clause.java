package exercisegenerator.structures.logic;

import java.util.*;
import java.util.stream.*;

public class Clause extends LinkedHashSet<Literal> {

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

}
