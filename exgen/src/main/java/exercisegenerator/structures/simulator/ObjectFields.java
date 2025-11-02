package exercisegenerator.structures.simulator;

import java.util.*;

import exercisegenerator.structures.simulator.expressions.*;

public class ObjectFields extends TreeMap<ProgramVariable, ProgramValue> implements HeapFields {

    private static final long serialVersionUID = 1L;

    public ObjectFields() {
        super();
    }

    public ObjectFields(final Map<ProgramVariable, ProgramValue> fields) {
        super(fields);
    }

}
