package exercisegenerator.structures.simulator;

public record ProgramVariable(String name, String type) implements ProgramExpression, ProgramVariableExpression {

    @Override
    public ProgramValue read(final Memory memory) {
        return memory.stack().peek().localVariables().get(this);
    }

    @Override
    public Memory write(final Memory memory, final ProgramValue value) {
        return new Memory(memory.stack().update(this, value), memory.heap());
    }

    @Override
    public ProgramState apply(final ProgramState state) {
        return state.intermediateValue(ProgramExpressionPosition.EMPTY, this.read(state.memory()));
    }

}
