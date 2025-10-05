package exercisegenerator.structures.simulator.commands;

import java.util.*;

import exercisegenerator.structures.simulator.*;

public record ProgramReturn(Optional<ProgramExpression> expression) implements ProgramCommand {

    @Override
    public ProgramState apply(final ProgramState state) {
        if (this.expression().isPresent()) {
            final Optional<ProgramValue> value = this.expression().get().evaluate(state);
            if (value.isEmpty()) {
                return this.expression().get().apply(state);
            }
            final MemoryStack nextStack = new MemoryStack(state.memory().stack());
            final MemoryFrame top = nextStack.pop();
            return new ProgramState(
                state.program(),
                new Memory(nextStack.update(top.returnPosition(), value.get()), state.memory().heap()),
                top.returnPosition()
            );
        }
        final MemoryStack nextStack = new MemoryStack(state.memory().stack());
        final MemoryFrame top = nextStack.pop();
        return new ProgramState(
            state.program(),
            new Memory(nextStack, state.memory().heap()),
            top.returnPosition()
        );
    }

}
