package exercisegenerator.structures.simulator;

public record ProgramPosition(
    String dataStructureName,
    int methodIndex,
    ProgramPositionIndex commandPosition,
    ProgramPositionIndex expressionPosition
) {

    public ProgramPosition increment() {
        return new ProgramPosition(
            this.dataStructureName(),
            this.methodIndex(),
            this.commandPosition().increment(),
            ProgramPositionIndex.EMPTY
        );
    }

    public ProgramPosition descendExpression(final int index) {
        return new ProgramPosition(
            this.dataStructureName(),
            this.methodIndex(),
            this.commandPosition(),
            this.expressionPosition().descend(index)
        );
    }

    public ProgramPosition ascendExpression() {
        return new ProgramPosition(
            this.dataStructureName(),
            this.methodIndex(),
            this.commandPosition(),
            this.expressionPosition().ascend()
        );
    }

    public ProgramPosition ascendBlock() {
        return new ProgramPosition(
            this.dataStructureName(),
            this.methodIndex(),
            this.commandPosition().ascend(),
            this.expressionPosition()
        );
    }

    public ProgramPosition descendBlock(final int index) {
        return new ProgramPosition(
            this.dataStructureName(),
            this.methodIndex(),
            this.commandPosition().descend(index),
            this.expressionPosition()
        );
    }

}
