package exercisegenerator.structures.simulator.expressions;

import exercisegenerator.structures.simulator.*;

public record ProgramBoolean(boolean value) implements ProgramConstantValue {

    @Override
    public String type(final ProgramState state) {
        return "boolean";
    }

}
