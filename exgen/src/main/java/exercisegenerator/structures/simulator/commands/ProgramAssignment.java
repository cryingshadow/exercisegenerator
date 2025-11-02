package exercisegenerator.structures.simulator.commands;

import java.util.*;

import exercisegenerator.structures.simulator.*;
import exercisegenerator.structures.simulator.expressions.*;

public record ProgramAssignment(
    ProgramVariableExpression variable,
    ProgramExpression expression
) implements ProgramCommand {

    @Override
    public ProgramState apply(final ProgramState state) {
        final Optional<ProgramValue> value = this.expression().evaluate(state);
        if (value.isPresent()) {
            return new ProgramState(
                state.program(),
                this.variable().write(state.memory(), value.get()).clearIntermediateValues(),
                state.position()
            ).incrementCommandPosition();
        }
        return this.expression().apply(state);
    }

}
