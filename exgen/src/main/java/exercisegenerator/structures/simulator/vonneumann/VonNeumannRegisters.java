package exercisegenerator.structures.simulator.vonneumann;

import exercisegenerator.structures.binary.*;

public record VonNeumannRegisters(
    BitString memoryBufferRegister,
    BitString memoryAddressRegister,
    BitString instructionRegister,
    BitString programCounter,
    BitString accumulator,
    VonNeumannGPRegisters gpRegisters
) {

    public VonNeumannRegisters setMemoryBufferRegister(final BitString memoryBufferRegister) {
        return new VonNeumannRegisters(
            memoryBufferRegister,
            this.memoryAddressRegister(),
            this.instructionRegister(),
            this.programCounter(),
            this.accumulator(),
            this.gpRegisters()
        );
    }

    public VonNeumannRegisters setStandardRegisters(
        final BitString memoryBufferRegister,
        final BitString memoryAddressRegister,
        final BitString instructionRegister,
        final BitString programCounter
    ) {
        return new VonNeumannRegisters(
            memoryBufferRegister,
            memoryAddressRegister,
            instructionRegister,
            programCounter,
            this.accumulator(),
            this.gpRegisters()
        );
    }

    public VonNeumannRegisters setGPRegisterFromMBR(final int register) {
        return new VonNeumannRegisters(
            this.memoryBufferRegister(),
            this.memoryAddressRegister(),
            this.instructionRegister(),
            this.programCounter(),
            this.accumulator(),
            this.gpRegisters().setRegister(register, this.memoryBufferRegister())
        );
    }

    public VonNeumannRegisters setGPRegisterFromACC(final int register) {
        return new VonNeumannRegisters(
            this.memoryBufferRegister(),
            this.memoryAddressRegister(),
            this.instructionRegister(),
            this.programCounter(),
            this.accumulator(),
            this.gpRegisters().setRegister(register, this.accumulator())
        );
    }

    public VonNeumannRegisters setMARFromRegister(final int register) {
        return new VonNeumannRegisters(
            this.memoryBufferRegister(),
            this.gpRegisters().registers()[register],
            this.instructionRegister(),
            this.programCounter(),
            this.accumulator(),
            this.gpRegisters()
        );
    }

    public VonNeumannRegisters setAccumulatorRegister(final BitString content) {
        return new VonNeumannRegisters(
            this.memoryBufferRegister(),
            this.memoryAddressRegister(),
            this.instructionRegister(),
            this.programCounter(),
            content,
            this.gpRegisters()
        );
    }

    public VonNeumannRegisters setGPRegisterFromConst(final int register, final BitString content) {
        return new VonNeumannRegisters(
            content,
            this.programCounter(),
            this.instructionRegister(),
            this.programCounter().increment(),
            this.accumulator(),
            this.gpRegisters().setRegister(register, content)
        );
    }

    public VonNeumannRegisters setProgramCounterRegister(final BitString content) {
        return new VonNeumannRegisters(
            this.memoryBufferRegister(),
            this.memoryAddressRegister(),
            this.instructionRegister(),
            content,
            this.accumulator(),
            this.gpRegisters()
        );
    }

}
