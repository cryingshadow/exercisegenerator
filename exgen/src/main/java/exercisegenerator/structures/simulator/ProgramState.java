package exercisegenerator.structures.simulator;

public record ProgramState(
    Program program,
    Memory memory,
    ProgramPosition position
) {

    public ProgramState intermediateValue(final ProgramPosition position, final ProgramValue value) {
        return new ProgramState(this.program(), this.memory().intermediateValue(position, value), this.position());
    }

    public ProgramState descendPosition(final int index) {
        return new ProgramState(this.program(), this.memory(), this.position().descend(index));
    }

    public ProgramState ascendPosition() {
        return new ProgramState(this.program(), this.memory(), this.position().ascend());
    }

    public ProgramState incrementPosition() {
        final ProgramPosition increment = this.position().increment();
        final ProgramDataStructure structure = this.program().get(increment.dataStructureName());
        final ProgramMethodDefinition method = structure.methods().get(increment.methodIndex());
        if (increment.commandIndex() < method.commands().size()) {
            return new ProgramState(this.program(), this.memory(), increment);
        }
        final MemoryStack nextStack = new MemoryStack(this.memory().stack());
        if (nextStack.isEmpty()) {
            return new ProgramState(this.program(), new Memory(nextStack, this.memory().heap()), null);
        }
        final MemoryFrame top = nextStack.pop();
        return new ProgramState(
            this.program(),
            new Memory(nextStack, this.memory().heap()),
            top.returnPosition()
        ).incrementPosition();
    }

}
