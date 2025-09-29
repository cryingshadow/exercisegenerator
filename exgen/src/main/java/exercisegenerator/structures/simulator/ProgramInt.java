package exercisegenerator.structures.simulator;

public record ProgramInt(int value) implements ProgramValue, ProgramExpression {

    @Override
    public ProgramState apply(final ProgramState state) {
        return state.intermediateValue(ProgramExpressionPosition.EMPTY, this);
    }

}
