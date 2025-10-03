package exercisegenerator.structures.simulator;

public record ProgramState(
    Program program,
    Memory memory,
    ProgramPosition position
) {

    public ProgramState intermediateValue(final ProgramPosition position, final ProgramValue value) {
        return new ProgramState(this.program(), this.memory().intermediateValue(position, value), this.position());
    }

}
