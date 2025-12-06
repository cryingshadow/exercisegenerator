package exercisegenerator.structures.simulator.vonneumann.instructions;

import exercisegenerator.structures.binary.*;
import exercisegenerator.structures.simulator.vonneumann.*;

@FunctionalInterface
public interface VonNeumannInstruction {

    public static final int INSTRUCTION_LENGTH = 4;

    public static final int REGISTER_LENGTH = 2;

    public static VonNeumannInstruction fromRegister(final BitString instructionRegister) {
        switch (instructionRegister.subString(0, VonNeumannInstruction.INSTRUCTION_LENGTH).toUnsignedInt()) {
        case 0:
            return new VonNeumannHaltInstruction();
        case 1:
            return new VonNeumannReadInstruction(
                instructionRegister.subString(
                    VonNeumannInstruction.INSTRUCTION_LENGTH,
                    VonNeumannInstruction.INSTRUCTION_LENGTH + VonNeumannInstruction.REGISTER_LENGTH
                ).toUnsignedInt()
            );
        case 2:
            return new VonNeumannWriteInstruction(
                instructionRegister.subString(
                    VonNeumannInstruction.INSTRUCTION_LENGTH,
                    VonNeumannInstruction.INSTRUCTION_LENGTH + VonNeumannInstruction.REGISTER_LENGTH
                ).toUnsignedInt()
            );
        case 3:
            return new VonNeumannLoadInstruction(
                instructionRegister.subString(
                    VonNeumannInstruction.INSTRUCTION_LENGTH,
                    VonNeumannInstruction.INSTRUCTION_LENGTH + VonNeumannInstruction.REGISTER_LENGTH
                ).toUnsignedInt(),
                instructionRegister.subString(
                    VonNeumannInstruction.INSTRUCTION_LENGTH + VonNeumannInstruction.REGISTER_LENGTH,
                    VonNeumannInstruction.INSTRUCTION_LENGTH + 2 * VonNeumannInstruction.REGISTER_LENGTH
                ).toUnsignedInt()
            );
        case 4:
            return new VonNeumannStoreInstruction(
                instructionRegister.subString(
                    VonNeumannInstruction.INSTRUCTION_LENGTH,
                    VonNeumannInstruction.INSTRUCTION_LENGTH + VonNeumannInstruction.REGISTER_LENGTH
                ).toUnsignedInt(),
                instructionRegister.subString(
                    VonNeumannInstruction.INSTRUCTION_LENGTH + VonNeumannInstruction.REGISTER_LENGTH,
                    VonNeumannInstruction.INSTRUCTION_LENGTH + 2 * VonNeumannInstruction.REGISTER_LENGTH
                ).toUnsignedInt()
            );
        case 5:
            return new VonNeumannSetAccumulatorInstruction(
                instructionRegister.subString(
                    VonNeumannInstruction.INSTRUCTION_LENGTH,
                    VonNeumannInstruction.INSTRUCTION_LENGTH + VonNeumannInstruction.REGISTER_LENGTH
                ).toUnsignedInt()
            );
        case 6:
            return new VonNeumannGetAccumulatorInstruction(
                instructionRegister.subString(
                    VonNeumannInstruction.INSTRUCTION_LENGTH,
                    VonNeumannInstruction.INSTRUCTION_LENGTH + VonNeumannInstruction.REGISTER_LENGTH
                ).toUnsignedInt()
            );
        case 7:
            return new VonNeumannConstInstruction(
                instructionRegister.subString(
                    VonNeumannInstruction.INSTRUCTION_LENGTH,
                    VonNeumannInstruction.INSTRUCTION_LENGTH + VonNeumannInstruction.REGISTER_LENGTH
                ).toUnsignedInt()
            );
        case 8:
            return new VonNeumannAddInstruction(
                instructionRegister.subString(
                    VonNeumannInstruction.INSTRUCTION_LENGTH,
                    VonNeumannInstruction.INSTRUCTION_LENGTH + VonNeumannInstruction.REGISTER_LENGTH
                ).toUnsignedInt()
            );
        case 9:
            return new VonNeumannEqInstruction(
                instructionRegister.subString(
                    VonNeumannInstruction.INSTRUCTION_LENGTH,
                    VonNeumannInstruction.INSTRUCTION_LENGTH + VonNeumannInstruction.REGISTER_LENGTH
                ).toUnsignedInt()
            );
        case 10:
            return new VonNeumannLtInstruction(
                instructionRegister.subString(
                    VonNeumannInstruction.INSTRUCTION_LENGTH,
                    VonNeumannInstruction.INSTRUCTION_LENGTH + VonNeumannInstruction.REGISTER_LENGTH
                ).toUnsignedInt()
            );
        case 11:
            return new VonNeumannNotInstruction();
        case 12:
            return new VonNeumannAndInstruction(
                instructionRegister.subString(
                    VonNeumannInstruction.INSTRUCTION_LENGTH,
                    VonNeumannInstruction.INSTRUCTION_LENGTH + VonNeumannInstruction.REGISTER_LENGTH
                ).toUnsignedInt()
            );
        case 13:
            return new VonNeumannOrInstruction(
                instructionRegister.subString(
                    VonNeumannInstruction.INSTRUCTION_LENGTH,
                    VonNeumannInstruction.INSTRUCTION_LENGTH + VonNeumannInstruction.REGISTER_LENGTH
                ).toUnsignedInt()
            );
        case 14:
            return new VonNeumannJumpInstruction(
                instructionRegister.subString(
                    VonNeumannInstruction.INSTRUCTION_LENGTH,
                    VonNeumannInstruction.INSTRUCTION_LENGTH + VonNeumannInstruction.REGISTER_LENGTH
                ).toUnsignedInt()
            );
        }
        throw new IllegalArgumentException("Unknown instruction!");
    }

    public static boolean isJumpInstruction(final BitString instructionRegister) {
        return instructionRegister.subString(0, VonNeumannInstruction.INSTRUCTION_LENGTH).toUnsignedInt() == 14;
    }

    VonNeumannState execute(VonNeumannState state);

}
