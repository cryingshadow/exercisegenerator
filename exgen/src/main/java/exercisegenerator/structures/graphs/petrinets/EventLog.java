package exercisegenerator.structures.graphs.petrinets;

import java.util.*;
import java.util.stream.*;

public class EventLog extends TreeSet<ActivityTrace> {

    private static final long serialVersionUID = 1L;

    public EventLog append(final ActivityTrace trace) {
        return Stream.concat(this.stream(), Stream.of(trace)).collect(Collectors.toCollection(EventLog::new));
    }

}
