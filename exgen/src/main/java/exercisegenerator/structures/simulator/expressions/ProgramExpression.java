package exercisegenerator.structures.simulator.expressions;

import java.util.*;

import exercisegenerator.structures.simulator.*;
import exercisegenerator.structures.simulator.commands.*;

public interface ProgramExpression extends ProgramCommand, TypedExpression {

    Optional<ProgramValue> evaluate(ProgramState state);

}
