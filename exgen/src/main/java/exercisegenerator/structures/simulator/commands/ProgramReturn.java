package exercisegenerator.structures.simulator.commands;

import java.util.*;

import exercisegenerator.structures.simulator.*;

public record ProgramReturn(ProgramExpression expression) implements ProgramCommand {

    @Override
    public ProgramState apply(final ProgramState state) {
        final ProgramState nextState = this.expression.apply(state);
        if (nextState.indermediateValues().containsKey(ProgramExpressionPosition.EMPTY)) {
            final MemoryStack nextStack = new MemoryStack(nextState.memory().stack());
            final MemoryFrame top = nextStack.pop();
            return new ProgramState(
                nextState.program(),
                new Memory(nextStack, nextState.memory().heap()),
                top.returnPosition(),
                Map.of(
                    top.returnPosition().position(),
                    nextState.indermediateValues().get(ProgramExpressionPosition.EMPTY)
                )
            );
        }
        return nextState;
    }

}
