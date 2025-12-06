package exercisegenerator.structures.simulator.vonneumann;

import java.util.*;

import exercisegenerator.structures.binary.*;

public record VonNeumannState(
    List<BitString> memory,
    VonNeumannIOState ioState,
    VonNeumannRegisters registers,
    boolean halted,
    boolean error
) {

    public VonNeumannState halt() {
        return new VonNeumannState(this.memory(), this.ioState(), this.registers(), true, this.error());
    }

    public VonNeumannState hasError() {
        return new VonNeumannState(this.memory(), this.ioState(), this.registers(), true, true);
    }

    public VonNeumannState setInput(final BitString input) {
        return new VonNeumannState(
            this.memory(),
            this.ioState().setInput(input),
            this.registers(),
            this.halted(),
            this.error()
        );
    }

    public VonNeumannState setRegisters(
        final BitString memoryAddressRegister,
        final BitString memoryBufferRegister,
        final BitString instructionRegister,
        final BitString programCounter
    ) {
        return new VonNeumannState(
            this.memory(),
            this.ioState(),
            this.registers().setStandardRegisters(
                memoryBufferRegister,
                memoryAddressRegister,
                instructionRegister,
                programCounter
            ),
            this.halted(),
            this.error()
        );
    }

    public VonNeumannState setMemoryBufferRegister(final BitString memoryBufferRegister) {
        return new VonNeumannState(
            this.memory(),
            this.ioState(),
            this.registers().setMemoryBufferRegister(memoryBufferRegister),
            this.halted(),
            this.error()
        );
    }

    public VonNeumannState setOutput(final BitString output) {
        return new VonNeumannState(
            this.memory(),
            this.ioState().setOutput(output),
            this.registers(),
            this.halted(),
            this.error()
        );
    }

    public VonNeumannState setGPRegisterFromMBR(final int register) {
        return new VonNeumannState(
            this.memory(),
            this.ioState(),
            this.registers().setGPRegisterFromMBR(register),
            this.halted(),
            this.error()
        );
    }

    public VonNeumannState setOutputFromMBR() {
        return new VonNeumannState(
            this.memory(),
            this.ioState().setOutput(this.registers().memoryBufferRegister()),
            this.registers(),
            this.halted(),
            this.error()
        );
    }

    public VonNeumannState setMemoryAddressRegister(final int register) {
        return new VonNeumannState(
            this.memory(),
            this.ioState(),
            this.registers().setMARFromRegister(register),
            this.halted(),
            this.error()
        );
    }

    public VonNeumannState load() {
        return new VonNeumannState(
            this.memory(),
            this.ioState(),
            this.registers().setMemoryBufferRegister(
                this.memory().get(this.registers().memoryAddressRegister().toUnsignedInt())
            ),
            this.halted(),
            this.error()
        );
    }

    public VonNeumannState store() {
        final List<BitString> newMemory = new ArrayList<BitString>(this.memory());
        newMemory.set(
            this.registers().memoryAddressRegister().toUnsignedInt(),
            this.registers().memoryBufferRegister()
        );
        return new VonNeumannState(
            newMemory,
            this.ioState(),
            this.registers(),
            this.halted(),
            this.error()
        );
    }

    public VonNeumannState setAccumulatorRegister(final BitString content) {
        return new VonNeumannState(
            this.memory(),
            this.ioState(),
            this.registers().setAccumulatorRegister(content),
            this.halted(),
            this.error()
        );
    }

    public VonNeumannState setGPRegisterFromACC(final int register) {
        return new VonNeumannState(
            this.memory(),
            this.ioState(),
            this.registers().setGPRegisterFromACC(register),
            this.halted(),
            this.error()
        );
    }

    public VonNeumannState setGPRegisterFromConst(final int register) {
        return new VonNeumannState(
            this.memory(),
            this.ioState(),
            this.registers().setGPRegisterFromConst(
                register,
                this.memory().get(this.registers().programCounter().toUnsignedInt())
            ),
            this.halted(),
            this.error()
        );
    }

    public VonNeumannState setProgramCounterRegister(final BitString content) {
        return new VonNeumannState(
            this.memory(),
            this.ioState(),
            this.registers().setProgramCounterRegister(content),
            this.halted(),
            this.error()
        );
    }

}
