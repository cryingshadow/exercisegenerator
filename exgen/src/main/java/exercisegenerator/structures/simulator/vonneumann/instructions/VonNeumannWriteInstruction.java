package exercisegenerator.structures.simulator.vonneumann.instructions;

import exercisegenerator.structures.simulator.vonneumann.*;

public record VonNeumannWriteInstruction(int register) implements VonNeumannInstruction {

    @Override
    public VonNeumannState execute(final VonNeumannState state) {
        return state
            .setMemoryBufferRegister(state.registers().gpRegisters().registers()[this.register()])
            .setOutputFromMBR();
    }

}
