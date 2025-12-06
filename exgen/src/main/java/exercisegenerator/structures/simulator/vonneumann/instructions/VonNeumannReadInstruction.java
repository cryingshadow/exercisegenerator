package exercisegenerator.structures.simulator.vonneumann.instructions;

import exercisegenerator.structures.simulator.vonneumann.*;

public record VonNeumannReadInstruction(int register) implements VonNeumannInstruction {

    @Override
    public VonNeumannState execute(final VonNeumannState state) {
        return state.setMemoryBufferRegister(state.ioState().input()).setGPRegisterFromMBR(this.register());
    }

}
