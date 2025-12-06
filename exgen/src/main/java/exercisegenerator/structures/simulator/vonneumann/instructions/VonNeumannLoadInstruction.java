package exercisegenerator.structures.simulator.vonneumann.instructions;

import exercisegenerator.structures.simulator.vonneumann.*;

public record VonNeumannLoadInstruction(int contentRegister, int addressRegister) implements VonNeumannInstruction {

    @Override
    public VonNeumannState execute(final VonNeumannState state) {
        return state
            .setMemoryAddressRegister(this.addressRegister())
            .load()
            .setGPRegisterFromMBR(this.contentRegister());
    }

}
