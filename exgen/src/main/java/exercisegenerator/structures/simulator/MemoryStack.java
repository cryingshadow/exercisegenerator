package exercisegenerator.structures.simulator;

import java.util.*;

public class MemoryStack extends ArrayDeque<MemoryFrame> {

    private static final long serialVersionUID = 1L;

    public MemoryStack(final MemoryStack stack) {
        super(stack);
    }

    public MemoryStack update(final ProgramVariable variable, final ProgramValue value) {
        final MemoryStack result = new MemoryStack(this);
        final MemoryFrame top = result.pop();
        result.push(top.update(variable, value));
        return result;
    }

}
