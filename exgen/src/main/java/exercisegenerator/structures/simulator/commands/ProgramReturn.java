package exercisegenerator.structures.simulator.commands;

import java.util.*;

import exercisegenerator.structures.simulator.*;

public record ProgramReturn(ProgramExpression expression) implements ProgramCommand {

    @Override
    public ProgramState apply(final ProgramState state) {
        final Optional<ProgramValue> value = this.expression().evaluate(state);
        if (value.isPresent()) {
            final MemoryStack nextStack = new MemoryStack(state.memory().stack());
            final MemoryFrame top = nextStack.pop();
            return new ProgramState(
                state.program(),
                new Memory(nextStack.update(top.returnPosition(), value.get()), state.memory().heap()),
                top.returnPosition()
            );
        }
        return this.expression.apply(state);
    }

}
