package exercisegenerator.structures.simulator;

public record ProgramString(String value) implements ProgramConstantValue {

    @Override
    public String type() {
        return "String";
    }

}
