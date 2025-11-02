package exercisegenerator.structures.simulator.commands;

import java.util.*;

import exercisegenerator.structures.simulator.*;
import exercisegenerator.structures.simulator.expressions.*;

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
                return this.parameters().get(i).apply(state.descendExpressionPosition(i)).ascendExpressionPosition();
            }
        }
        final ProgramPosition nextPosition =
            state.program().findFirstPositionInMethod(
                this.callFrom().type(state),
                this.method(),
                this.parameters().stream().map(parameter -> parameter.type(state)).toList()
            );
        final List<ProgramVariable> parameterVariables = state.program().getParameters(nextPosition);
        final Map<ProgramVariable, ProgramValue> localVariables = new TreeMap<ProgramVariable, ProgramValue>();
        for (int i = 0; i < evaluatedParameters.size(); i++) {
            localVariables.put(parameterVariables.get(i), evaluatedParameters.get(i).get());
        }
        final MemoryStack stack = new MemoryStack(state.memory().stack());
        stack.push(
            new MemoryFrame(
                state.position(),
                this.callFrom().getHeapAddress(state.memory()),
                this.callFrom().type(state),
                localVariables,
                Map.of()
            )
        );
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
    public String type(final ProgramState state) {
        return this.returnType();
    }

}
