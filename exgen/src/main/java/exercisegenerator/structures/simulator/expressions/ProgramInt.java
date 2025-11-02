package exercisegenerator.structures.simulator.expressions;

import exercisegenerator.structures.simulator.*;

public record ProgramInt(int value) implements ProgramConstantValue {

    @Override
    public String type(final ProgramState state) {
        return "int";
    }

}
