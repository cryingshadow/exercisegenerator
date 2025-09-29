package exercisegenerator.structures.simulator;

public record ProgramBoolean(boolean value) implements ProgramValue, ProgramExpression {

    @Override
    public ProgramState apply(final ProgramState state) {
        return state.intermediateValue(ProgramExpressionPosition.EMPTY, this);
    }

}
