package exercisegenerator.structures.simulator.expressions;

import exercisegenerator.structures.simulator.*;

public record ProgramHeapAccess(
    ProgramVariableExpression from,
    ProgramVariable to
) implements ProgramVariableExpression {

    @Override
    public ProgramValue read(final Memory memory) {
        final SymbolicVariable fromAddress = (SymbolicVariable)this.from().getHeapAddress(memory);
        return memory.heap().read(fromAddress, this.to());
    }

    @Override
    public String type(final ProgramState state) {
        return this.to().type();
    }

    @Override
    public Memory write(final Memory memory, final ProgramValue value) {
        final SymbolicVariable fromAddress = (SymbolicVariable)this.from().getHeapAddress(memory);
        return new Memory(memory.stack(), memory.heap().write(fromAddress, this.to(), value));
    }

    @Override
    public HeapAddress getHeapAddress(final Memory memory) {
        final SymbolicVariable fromAddress = (SymbolicVariable)this.from().getHeapAddress(memory);
        return (HeapAddress)((ObjectFields)memory.heap().get(fromAddress)).get(this.to());
    }

}
