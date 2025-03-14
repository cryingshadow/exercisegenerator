package exercisegenerator.structures.logic;

import java.util.*;
import java.util.stream.*;

public class ClauseSet extends TreeSet<Clause> {

    public static final ClauseSet EMPTY = new ClauseSet();

    public static final ClauseSet FALSE = new ClauseSet(Clause.EMPTY);

    private static final long serialVersionUID = 1L;

    public ClauseSet() {}

    public ClauseSet(final Clause... clauses) {
        super(Arrays.stream(clauses).toList());
    }

    public ClauseSet(final Collection<Clause> collection) {
        super(collection);
    }

    public ClauseSet(final Stream<Clause> clauses) {
        super(clauses.toList());
    }

}
