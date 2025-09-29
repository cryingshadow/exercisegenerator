package exercisegenerator.structures.simulator;

public interface ProgramVariableExpression {

    ProgramValue read(Memory memory);

    Memory write(Memory memory, ProgramValue value);

}
