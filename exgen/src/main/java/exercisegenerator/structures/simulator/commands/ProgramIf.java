package exercisegenerator.structures.simulator.commands;

import java.util.*;

import exercisegenerator.structures.simulator.*;
import exercisegenerator.structures.simulator.expressions.*;

public record ProgramIf(
    ProgramExpression condition,
    List<ProgramCommand> ifBlock,
    List<ProgramCommand> elseBlock
) implements ProgramCommand {

    @Override
    public ProgramState apply(final ProgramState state) {
        final Optional<ProgramValue> value = this.condition().evaluate(state);
        if (value.isPresent()) {
            if (((ProgramBoolean)value.get()).value()) {
                return new ProgramState(
                    state.program(),
                    state.memory(),
                    state.position().descendBlock(0)
                );
            } else if (this.elseBlock().isEmpty()) {
                return state.incrementCommandPosition();
            } else {
                return new ProgramState(
                    state.program(),
                    state.memory(),
                    state.position().descendBlock(1)
                );
            }
        }
        return this.condition().apply(state);
    }

}
