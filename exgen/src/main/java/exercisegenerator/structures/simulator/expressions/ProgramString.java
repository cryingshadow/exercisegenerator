package exercisegenerator.structures.simulator.expressions;

import exercisegenerator.structures.simulator.*;

public record ProgramString(String value) implements ProgramConstantValue {

    @Override
    public String type(final ProgramState state) {
        return "String";
    }

}
