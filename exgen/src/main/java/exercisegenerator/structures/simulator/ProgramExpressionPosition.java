package exercisegenerator.structures.simulator;

import java.util.*;

public class ProgramExpressionPosition extends ArrayList<Integer> {

    public static final ProgramExpressionPosition EMPTY = new ProgramExpressionPosition();

    private static final long serialVersionUID = 1L;

    public ProgramExpressionPosition() {}

    public ProgramExpressionPosition(final List<Integer> position) {
        super(position);
    }

    public ProgramExpressionPosition ascend() {
        final ProgramExpressionPosition result = new ProgramExpressionPosition(this);
        result.removeLast();
        return result;
    }

    public ProgramExpressionPosition descend(final int index) {
        final ProgramExpressionPosition result = new ProgramExpressionPosition(this);
        result.add(index);
        return result;
    }

}
