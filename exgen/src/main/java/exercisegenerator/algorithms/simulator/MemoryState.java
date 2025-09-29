package exercisegenerator.algorithms.simulator;

import java.io.*;
import java.util.*;

import clit.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.simulator.*;

public class MemoryState implements AlgorithmImplementation<MemoryStateInput, List<Memory>> {

    @Override
    public List<Memory> apply(final MemoryStateInput t) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String commandPrefix() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MemoryStateInput generateProblem(final Parameters<Flag> options) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String[] generateTestParameters() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<MemoryStateInput> parseProblems(final BufferedReader reader, final Parameters<Flag> options) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<MemoryStateInput> problems,
        final List<List<Memory>> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void printBeforeSingleProblemInstance(
        final MemoryStateInput problem,
        final List<Memory> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void printProblemInstance(
        final MemoryStateInput problem,
        final List<Memory> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void printSolutionInstance(
        final MemoryStateInput problem,
        final List<Memory> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void printSolutionSpace(
        final MemoryStateInput problem,
        final List<Memory> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        // TODO Auto-generated method stub

    }

}
