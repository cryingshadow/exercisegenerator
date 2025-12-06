package exercisegenerator.structures.simulator.vonneumann.instructions;

import exercisegenerator.algorithms.simulator.*;
import exercisegenerator.structures.simulator.vonneumann.*;

public class VonNeumannNotInstruction implements VonNeumannInstruction {

    @Override
    public VonNeumannState execute(final VonNeumannState state) {
        return state.setAccumulatorRegister(state.registers().accumulator().negate(VonNeumann.BIT_LENGTH));
    }

}
