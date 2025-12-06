package exercisegenerator.structures.simulator.vonneumann.instructions;

import java.math.*;

import exercisegenerator.algorithms.simulator.*;
import exercisegenerator.structures.binary.*;
import exercisegenerator.structures.simulator.vonneumann.*;

public record VonNeumannEqInstruction(int register) implements VonNeumannInstruction {

    @Override
    public VonNeumannState execute(final VonNeumannState state) {
        return state.setAccumulatorRegister(
            state.registers().accumulator().equals(
                state.registers().gpRegisters().registers()[this.register()]
            ) ?
                BitString.create(BigInteger.ONE, VonNeumann.BIT_LENGTH) :
                    BitString.create(BigInteger.ZERO, VonNeumann.BIT_LENGTH)
        );
    }

}
