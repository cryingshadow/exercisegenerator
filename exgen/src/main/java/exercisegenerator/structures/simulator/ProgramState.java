package exercisegenerator.structures.simulator;

public record ProgramState(
    Program program,
    Memory memory,
    ProgramPosition position
) {

    public ProgramState intermediateValue(final ProgramPosition position, final ProgramValue value) {
        return new ProgramState(this.program(), this.memory().intermediateValue(position, value), this.position());
    }

    public ProgramState descendPosition(final int index) {
        return new ProgramState(this.program(), this.memory(), this.position().descend(index));
    }

    public ProgramState ascendPosition() {
        return new ProgramState(this.program(), this.memory(), this.position().ascend());
    }

    public ProgramState incrementPosition() {
        return new ProgramState(this.program(), this.memory(), this.position().increment());
    }

}
