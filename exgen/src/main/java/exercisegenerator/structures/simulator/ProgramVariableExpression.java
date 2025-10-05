package exercisegenerator.structures.simulator;

public interface ProgramVariableExpression {

    ProgramValue read(Memory memory);

    String type();

    Memory write(Memory memory, ProgramValue value);

}
