package exercisegenerator.structures.simulator.commands;

import java.util.*;

import exercisegenerator.structures.simulator.*;

public record ProgramMethodDefinition(
    String method,
    String returnType,
    List<ProgramVariable> parameters
) implements ProgramCommand {

    @Override
    public ProgramState apply(final ProgramState state) {
        return state.incrementPosition();
    }

}
