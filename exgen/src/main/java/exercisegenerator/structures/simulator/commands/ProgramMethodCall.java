package exercisegenerator.structures.simulator.commands;

import java.util.*;

import exercisegenerator.structures.simulator.*;

public record ProgramMethodCall(
    ProgramVariableExpression callFrom,
    String method,
    String returnType,
    List<ProgramExpression> parameters
) implements ProgramCommand, ProgramExpression {

    @Override
    public ProgramState apply(final ProgramState state) {
        final List<Optional<ProgramValue>> evaluatedParameters =
            this.parameters().stream().map(parameter -> parameter.evaluate(state)).toList();
        for (int i = 0; i < evaluatedParameters.size(); i++) {
            final Optional<ProgramValue> value = evaluatedParameters.get(i);
            if (value.isEmpty()) {
                return this.parameters().get(i).apply(state.descendPosition(i)).ascendPosition();
            }
        }

        final ProgramPosition nextPosition =
            state.program().find(
                this.callFrom().type(),
                this.method(),
                this.parameters().stream().map(ProgramExpression::type).toList()
            );
        final List<ProgramVariable> parameterVariables = state.program().getParameters(nextPosition);
        final Map<ProgramVariable, ProgramValue> localVariables = new TreeMap<ProgramVariable, ProgramValue>();
        for (int i = 0; i < evaluatedParameters.size(); i++) {
            localVariables.put(parameterVariables.get(i), evaluatedParameters.get(i).get());
        }
        final MemoryStack stack = new MemoryStack(state.memory().stack());
        stack.push(new MemoryFrame(state.position(), localVariables, Map.of()));
        return new ProgramState(state.program(), new Memory(stack, state.memory().heap()), nextPosition);
    }

    @Override
    public Optional<ProgramValue> evaluate(final ProgramState state) {
        final Map<ProgramPosition, ProgramValue> intermediateValues =
            state.memory().stack().peek().intermediateValues();
        if (intermediateValues.containsKey(state.position())) {
            return Optional.of(intermediateValues.get(state.position()));
        }
        return Optional.empty();
    }

    @Override
    public String type() {
        return this.returnType();
    }

}
