package exercisegenerator.structures.simulator;

import java.util.*;

public record MemoryFrame(
    ProgramPosition returnPosition,
    Map<ProgramVariable, ProgramValue> localVariables,
    Map<ProgramPosition, ProgramValue> intermediateValues
) {

    public MemoryFrame update(final ProgramVariable variable, final ProgramValue value) {
        final Map<ProgramVariable, ProgramValue> localVariables =
            new TreeMap<ProgramVariable, ProgramValue>(this.localVariables());
        localVariables.put(variable, value);
        return new MemoryFrame(this.returnPosition(), localVariables, this.intermediateValues());
    }

    public MemoryFrame update(final ProgramPosition position, final ProgramValue value) {
        final Map<ProgramPosition, ProgramValue> intermediateValues =
            new TreeMap<ProgramPosition, ProgramValue>(this.intermediateValues());
        intermediateValues.put(position, value);
        return new MemoryFrame(this.returnPosition(), this.localVariables(), intermediateValues);
    }

    public MemoryFrame clearIntermediateValues() {
        return new MemoryFrame(this.returnPosition(), this.localVariables(), Map.of());
    }

}
