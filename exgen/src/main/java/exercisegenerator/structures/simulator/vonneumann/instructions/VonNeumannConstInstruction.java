package exercisegenerator.structures.simulator.vonneumann.instructions;

import exercisegenerator.structures.simulator.vonneumann.*;

public record VonNeumannConstInstruction(int register) implements VonNeumannInstruction {

    @Override
    public VonNeumannState execute(final VonNeumannState state) {
        return state.setGPRegisterFromConst(this.register());
    }

}
