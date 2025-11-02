package exercisegenerator.structures.simulator;

import java.util.*;

public class ProgramPositionIndex extends ArrayList<Integer> {

    public static final ProgramPositionIndex EMPTY = new ProgramPositionIndex();

    public static final ProgramPositionIndex START = new ProgramPositionIndex(List.of(0));

    private static final long serialVersionUID = 1L;

    public ProgramPositionIndex() {}

    public ProgramPositionIndex(final List<Integer> position) {
        super(position);
    }

    public ProgramPositionIndex ascend() {
        final ProgramPositionIndex result = new ProgramPositionIndex(this);
        result.removeLast();
        return result;
    }

    public ProgramPositionIndex descend(final int index) {
        final ProgramPositionIndex result = new ProgramPositionIndex(this);
        result.add(index);
        return result;
    }

    public ProgramPositionIndex increment() {
        final ProgramPositionIndex result = new ProgramPositionIndex(this);
        final int last = result.removeLast();
        result.add(last + 1);
        return result;
    }

}
