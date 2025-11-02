package exercisegenerator.structures.simulator;

import java.util.*;

import exercisegenerator.structures.simulator.commands.*;
import exercisegenerator.structures.simulator.expressions.*;

public record ProgramMethodDefinition(
    String name,
    String returnType,
    List<ProgramVariable> parameters,
    List<ProgramCommand> commands
) {

    public boolean positionExists(final ProgramPositionIndex commandPosition) {
        return this.getCommandAt(commandPosition).isPresent();
    }

    public boolean isLoop(final ProgramPositionIndex commandPosition) {
        final Optional<ProgramCommand> command = this.getCommandAt(commandPosition);
        return command.isPresent() && ProgramMethodDefinition.isLoop(command.get());
    }

    private static boolean isLoop(final ProgramCommand command) {
        // TODO Auto-generated method stub
        return false;
    }

    private Optional<ProgramCommand> getCommandAt(final ProgramPositionIndex commandPosition) {
        // TODO Auto-generated method stub
        return Optional.empty();
    }

}
