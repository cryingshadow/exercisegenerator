package exercisegenerator.structures.simulator.vonneumann.instructions;

import exercisegenerator.algorithms.simulator.*;
import exercisegenerator.structures.simulator.vonneumann.*;

public record VonNeumannAddInstruction(int register) implements VonNeumannInstruction {

    @Override
    public VonNeumannState execute(final VonNeumannState state) {
        return state.setAccumulatorRegister(
            state.registers().accumulator().add(
                state.registers().gpRegisters().registers()[this.register()],
                VonNeumann.BIT_LENGTH
            )
        );
    }

}
