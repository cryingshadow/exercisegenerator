package exercisegenerator.structures.simulator;

public record ProgramInt(int value) implements ProgramConstantValue {

    @Override
    public String type() {
        return "int";
    }

}
