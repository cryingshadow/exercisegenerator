package exercisegenerator.structures.simulator;

import java.util.*;

public interface ProgramConstantValue extends ProgramValue, ProgramExpression {

    @Override
    default ProgramState apply(final ProgramState state) {
        throw new IllegalStateException("Constant value should never be executed!");
    }

    @Override
    default Optional<ProgramValue> evaluate(final ProgramState state) {
        return Optional.of(this);
    }

}
