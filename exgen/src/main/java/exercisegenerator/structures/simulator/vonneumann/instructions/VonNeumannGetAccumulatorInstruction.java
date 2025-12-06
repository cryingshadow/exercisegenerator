package exercisegenerator.structures.simulator.vonneumann.instructions;

import exercisegenerator.structures.simulator.vonneumann.*;

public record VonNeumannGetAccumulatorInstruction(int register) implements VonNeumannInstruction {

    @Override
    public VonNeumannState execute(final VonNeumannState state) {
        return state.setAccumulatorRegister(state.registers().gpRegisters().registers()[this.register()]);
    }

}
