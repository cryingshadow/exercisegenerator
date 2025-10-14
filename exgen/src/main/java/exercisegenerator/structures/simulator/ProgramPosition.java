package exercisegenerator.structures.simulator;

public record ProgramPosition(
    String dataStructureName,
    int methodIndex,
    int commandIndex,
    ProgramExpressionPosition position
) {

    public ProgramPosition increment() {
        return new ProgramPosition(
            this.dataStructureName(),
            this.methodIndex(),
            this.commandIndex() + 1,
            ProgramExpressionPosition.EMPTY
        );
    }

    public ProgramPosition descend(final int index) {
        return new ProgramPosition(
            this.dataStructureName(),
            this.methodIndex(),
            this.commandIndex(),
            this.position().descend(index)
        );
    }

    public ProgramPosition ascend() {
        return new ProgramPosition(
            this.dataStructureName(),
            this.methodIndex(),
            this.commandIndex(),
            this.position().ascend()
        );
    }

}
