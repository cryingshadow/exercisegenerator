package exercisegenerator.structures.simulator;

import java.util.*;

public record ProgramPosition(Optional<Integer> line, ProgramExpressionPosition position) {

    public ProgramPosition increment() {
        return new ProgramPosition(this.line().map(i -> i + 1), ProgramExpressionPosition.EMPTY);
    }

    public ProgramPosition descend(final int index) {
        return new ProgramPosition(this.line(), this.position().descend(index));
    }

    public ProgramPosition ascend() {
        return new ProgramPosition(this.line(), this.position().ascend());
    }

}
