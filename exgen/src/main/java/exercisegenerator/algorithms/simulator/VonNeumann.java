package exercisegenerator.algorithms.simulator;

import java.io.*;
import java.math.*;
import java.util.*;

import clit.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.binary.*;
import exercisegenerator.structures.simulator.vonneumann.*;
import exercisegenerator.structures.simulator.vonneumann.instructions.*;

public class VonNeumann implements AlgorithmImplementation<VonNeumannInput, List<VonNeumannState>> {

    public static final int BIT_LENGTH = 32;

    static VonNeumannState cycle(final VonNeumannState state, final BitString nextInput) {
        return VonNeumann.execute(VonNeumann.fetch(state, nextInput));
    }

    private static VonNeumannState execute(final VonNeumannState state) {
        if (state.error()) {
            return state;
        }
        return VonNeumannInstruction.fromRegister(state.registers().instructionRegister()).execute(state);
    }

    private static VonNeumannState fetch(final VonNeumannState state, final BitString nextInput) {
        final BitString memoryAddressRegister = state.registers().programCounter();
        final int address = memoryAddressRegister.toUnsignedInt();
        if (state.memory().size() <= address) {
            return state.hasError();
        }
        final BitString memoryBufferRegister = state.memory().get(address);
        final BitString instructionRegister = memoryBufferRegister;
        final BitString programCounter =
            VonNeumannInstruction.isJumpInstruction(instructionRegister) ?
                state.registers().programCounter() :
                    state.registers().programCounter().increment();
        return state
            .setRegisters(memoryAddressRegister, memoryBufferRegister, instructionRegister, programCounter)
            .setInput(nextInput);
    }

    private static BitString getInput(final int cycle, final SortedMap<Integer, BitString> inputs) {
        Integer currentKey = -1;
        for (final Integer key : inputs.keySet()) {
            if (key < cycle) {
                continue;
            }
            currentKey = key;
            break;
        }
        if (currentKey < 0) {
            return BitString.create(BigInteger.ZERO, VonNeumann.BIT_LENGTH);
        }
        return inputs.get(currentKey);
    }

    @Override
    public List<VonNeumannState> apply(final VonNeumannInput input) {
        VonNeumannState currentState = input.initialState();
        final List<VonNeumannState> result = new LinkedList<VonNeumannState>();
        result.add(currentState);
        int cycle = 0;
        while (!currentState.halted()) {
            currentState = VonNeumann.cycle(currentState, VonNeumann.getInput(cycle, input.inputs()));
            result.add(currentState);
            cycle++;
        }
        return result;
    }

    @Override
    public String commandPrefix() {
        return "vonneumann";
    }

    @Override
    public VonNeumannInput generateProblem(final Parameters<Flag> options) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    @Override
    public List<VonNeumannInput> parseProblems(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<VonNeumannInput> problems,
        final List<List<VonNeumannState>> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void printBeforeSingleProblemInstance(
        final VonNeumannInput problem,
        final List<VonNeumannState> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void printProblemInstance(
        final VonNeumannInput problem,
        final List<VonNeumannState> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void printSolutionInstance(
        final VonNeumannInput problem,
        final List<VonNeumannState> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void printSolutionSpace(
        final VonNeumannInput problem,
        final List<VonNeumannState> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        // TODO Auto-generated method stub

    }

}
