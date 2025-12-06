package exercisegenerator.structures.simulator.vonneumann.instructions;

import exercisegenerator.structures.simulator.vonneumann.*;

public record VonNeumannJumpInstruction(int register) implements VonNeumannInstruction {

    @Override
    public VonNeumannState execute(final VonNeumannState state) {
        return state.setProgramCounterRegister(
            state.registers().accumulator().isZero() ?
                state.registers().programCounter().increment() :
                    state.registers().gpRegisters().registers()[this.register()]
        );
    }

}
