package exercisegenerator.structures.simulator;

import java.util.*;

public record MemoryFrame(ProgramPosition returnPosition, Map<ProgramVariable, ProgramValue> localVariables) {

    public MemoryFrame update(final ProgramVariable variable, final ProgramValue value) {
        final Map<ProgramVariable, ProgramValue> localVariables =
            new TreeMap<ProgramVariable, ProgramValue>(this.localVariables());
        localVariables.put(variable, value);
        return new MemoryFrame(this.returnPosition(), localVariables);
    }

}
