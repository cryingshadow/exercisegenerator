package exercisegenerator.structures.simulator;

public record Memory(MemoryStack stack, MemoryHeap heap) {

    public Memory intermediateValue(final ProgramPosition position, final ProgramValue value) {
        return new Memory(this.stack().update(position, value), this.heap());
    }

    public Memory clearIntermediateValues() {
        return new Memory(this.stack().clearIntermediateValues(), this.heap());
    }

}
