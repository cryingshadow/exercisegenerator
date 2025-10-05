package exercisegenerator.structures.simulator;

public record ProgramBoolean(boolean value) implements ProgramConstantValue {

    @Override
    public String type() {
        return "boolean";
    }

}
