package exercisegenerator.structures.simulator;

import java.util.*;

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
        throw new IllegalStateException("Variable should never be executed!");
    }

    @Override
    public Optional<ProgramValue> evaluate(final ProgramState state) {
        return Optional.of(this.read(state.memory()));
    }

}
