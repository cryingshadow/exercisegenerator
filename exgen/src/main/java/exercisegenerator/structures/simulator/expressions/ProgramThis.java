package exercisegenerator.structures.simulator.expressions;

import exercisegenerator.structures.simulator.*;

public class ProgramThis implements ProgramConstantValue, ProgramVariableExpression {

    @Override
    public HeapAddress getHeapAddress(final Memory memory) {
        return (HeapAddress)this.read(memory);
    }

    @Override
    public ProgramValue read(final Memory memory) {
        return memory.stack().peek().thisAddress();
    }

    @Override
    public String type(final ProgramState state) {
        return state.memory().stack().peek().thisType();
    }

    @Override
    public Memory write(final Memory memory, final ProgramValue value) {
        throw new IllegalStateException("This should never be written!");
    }

}
