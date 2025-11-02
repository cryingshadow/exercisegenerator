package exercisegenerator.structures.simulator.expressions;

import exercisegenerator.structures.simulator.*;

public class ProgramNull implements ProgramConstantValue, HeapAddress {

    @Override
    public String type(final ProgramState state) {
        return "Object";
    }

}
