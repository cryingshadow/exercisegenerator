package exercisegenerator.structures.simulator;

import exercisegenerator.structures.simulator.expressions.*;

public record ProgramState(
    Program program,
    Memory memory,
    ProgramPosition position
) {

    public ProgramState intermediateValue(final ProgramPosition position, final ProgramValue value) {
        return new ProgramState(this.program(), this.memory().intermediateValue(position, value), this.position());
    }

    public ProgramState descendExpressionPosition(final int index) {
        return new ProgramState(this.program(), this.memory(), this.position().descendExpression(index));
    }

    public ProgramState ascendExpressionPosition() {
        return new ProgramState(this.program(), this.memory(), this.position().ascendExpression());
    }

    public ProgramState incrementCommandPosition() {
        final ProgramPosition increment = this.position().increment();
        final ProgramDataStructure structure = this.program().get(increment.dataStructureName());
        final ProgramMethodDefinition method = structure.methods().get(increment.methodIndex());
        if (method.positionExists(increment.commandPosition())) {
            return new ProgramState(this.program(), this.memory(), increment);
        }
        if (increment.commandPosition().size() > 1) {
            final ProgramPosition ascend = increment.ascendBlock();
            if (method.isLoop(ascend.commandPosition())) {
                return new ProgramState(this.program(), this.memory(), ascend);
            }
            return new ProgramState(this.program(), this.memory(), ascend).incrementCommandPosition();
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
        ).incrementCommandPosition();
    }

}
