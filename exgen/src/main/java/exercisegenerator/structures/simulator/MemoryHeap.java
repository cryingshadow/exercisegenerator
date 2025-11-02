package exercisegenerator.structures.simulator;

import java.util.*;

import exercisegenerator.structures.simulator.expressions.*;

public class MemoryHeap extends TreeMap<SymbolicVariable, HeapFields> {

    private static final long serialVersionUID = 1L;

    public MemoryHeap() {
        super();
    }

    public MemoryHeap(final Map<SymbolicVariable, HeapFields> heap) {
        super(heap);
    }

    public ProgramValue read(final SymbolicVariable address, final ProgramVariable field) {
        final ObjectFields fields = (ObjectFields)this.get(address);
        return fields.get(field);
    }

    public MemoryHeap write(final SymbolicVariable address, final ProgramVariable field, final ProgramValue value) {
        final ObjectFields fields = (ObjectFields)this.get(address);
        final MemoryHeap result = new MemoryHeap(this);
        final ObjectFields newFields = new ObjectFields(fields);
        newFields.put(field, value);
        result.put(address, newFields);
        return result;
    }

}
