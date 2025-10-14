package exercisegenerator.structures.simulator;

import java.util.*;

import exercisegenerator.structures.simulator.commands.*;

public record ProgramMethodDefinition(
    String name,
    String returnType,
    List<ProgramVariable> parameters,
    List<ProgramCommand> commands
) {

}
