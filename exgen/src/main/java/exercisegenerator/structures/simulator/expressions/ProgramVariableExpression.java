package exercisegenerator.structures.simulator.expressions;

import exercisegenerator.structures.simulator.*;

public interface ProgramVariableExpression extends TypedExpression {

    HeapAddress getHeapAddress(Memory memory);

    ProgramValue read(Memory memory);

    Memory write(Memory memory, ProgramValue value);

}
