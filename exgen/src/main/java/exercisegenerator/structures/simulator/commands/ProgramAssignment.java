package exercisegenerator.structures.simulator.commands;

import java.util.*;

import exercisegenerator.structures.simulator.*;

public record ProgramAssignment(
    ProgramVariableExpression variable,
    ProgramExpression expression
) implements ProgramCommand {

    @Override
    public ProgramState apply(final ProgramState state) {
        final ProgramState nextState = this.expression.apply(state);
        if (nextState.indermediateValues().containsKey(ProgramExpressionPosition.EMPTY)) {
            return new ProgramState(
                nextState.program(),
                this.variable.write(
                    nextState.memory(),
                    nextState.indermediateValues().get(ProgramExpressionPosition.EMPTY)
                ),
                state.position().increment(),
                Map.of()
            );
        }
        return nextState;
    }

}
