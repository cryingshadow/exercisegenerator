package exercisegenerator.structures.simulator.vonneumann;

import java.util.*;

import exercisegenerator.structures.binary.*;

public record VonNeumannGPRegisters(BitString[] registers) {

    private static final int NUM_OF_REGISTERS = 4;

    public VonNeumannGPRegisters setRegister(final int index, final BitString content) {
        final BitString[] registers = new BitString[VonNeumannGPRegisters.NUM_OF_REGISTERS];
        System.arraycopy(this.registers(), 0, registers, 0, VonNeumannGPRegisters.NUM_OF_REGISTERS);
        registers[index] = content;
        return new VonNeumannGPRegisters(registers);
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof final VonNeumannGPRegisters other) {
            return Arrays.deepEquals(this.registers(), other.registers());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.registers()) * 31;
    }

    @Override
    public String toString() {
        return Arrays.toString(this.registers());
    }

}
