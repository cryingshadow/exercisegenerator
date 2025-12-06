package exercisegenerator.structures.simulator.vonneumann.instructions;

import exercisegenerator.structures.simulator.vonneumann.*;

public record VonNeumannStoreInstruction(int contentRegister, int addressRegister) implements VonNeumannInstruction {

    @Override
    public VonNeumannState execute(final VonNeumannState state) {
        return state
            .setMemoryAddressRegister(this.addressRegister())
            .setMemoryBufferRegister(state.registers().gpRegisters().registers()[this.contentRegister()])
            .store();
    }

}
