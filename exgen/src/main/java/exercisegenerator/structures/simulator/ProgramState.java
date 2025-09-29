package exercisegenerator.structures.simulator;

import java.util.*;

public record ProgramState(
    Program program,
    Memory memory,
    ProgramPosition position,
    Map<ProgramExpressionPosition, ProgramValue> indermediateValues
) {

    public ProgramState intermediateValue(final ProgramExpressionPosition position, final ProgramValue value) {
        final Map<ProgramExpressionPosition, ProgramValue> nextIndermediateValues =
            new TreeMap<ProgramExpressionPosition, ProgramValue>(this.indermediateValues());
        nextIndermediateValues.put(position, value);
        return new ProgramState(this.program(), this.memory(), this.position(), nextIndermediateValues);
    }

}
