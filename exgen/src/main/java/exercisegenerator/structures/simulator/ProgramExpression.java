package exercisegenerator.structures.simulator;

import java.util.*;

import exercisegenerator.structures.simulator.commands.*;

public interface ProgramExpression extends ProgramCommand {

    Optional<ProgramValue> evaluate(ProgramState state);

}
