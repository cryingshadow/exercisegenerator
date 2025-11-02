package exercisegenerator.structures.simulator.expressions;

import java.util.*;

import exercisegenerator.structures.simulator.*;

public abstract class ProgramOperator implements ProgramExpression {

    public final List<ProgramExpression> arguments;

    public ProgramOperator(final List<ProgramExpression> arguments) {
        this.arguments = arguments;
    }

    @Override
    public ProgramState apply(final ProgramState state) {
        final List<Optional<ProgramValue>> evaluatedArguments =
            this.arguments.stream().map(argument -> argument.evaluate(state)).toList();
        for (int i = 0; i < evaluatedArguments.size(); i++) {
            final Optional<ProgramValue> value = evaluatedArguments.get(i);
            if (value.isEmpty()) {
                return this.arguments.get(i).apply(state.descendExpressionPosition(i)).ascendExpressionPosition();
            }
        }
        throw new IllegalStateException("An operator expression with evaluated arguments should not be executed!");
    }

    @Override
    public Optional<ProgramValue> evaluate(final ProgramState state) {
        final List<Optional<ProgramValue>> evaluatedArguments =
            this.arguments.stream().map(argument -> argument.evaluate(state)).toList();
        if (evaluatedArguments.stream().anyMatch(Optional::isEmpty)) {
            return Optional.empty();
        }
        return Optional.of(this.operator(evaluatedArguments.stream().map(Optional::get).toList()));
    }

    @Override
    public String toString() {
        return this.display(this.arguments);
    }

    @Override
    public String type(final ProgramState state) {
        return this.type(this.arguments, state);
    }

    protected abstract String display(List<ProgramExpression> arguments);

    protected abstract ProgramValue operator(List<ProgramValue> arguments);

    protected abstract String type(List<ProgramExpression> arguments, ProgramState state);

}
