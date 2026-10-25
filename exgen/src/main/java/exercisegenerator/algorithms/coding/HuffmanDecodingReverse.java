package exercisegenerator.algorithms.coding;

import java.io.*;
import java.util.*;

import clit.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.coding.*;

public class HuffmanDecodingReverse implements AlgorithmImplementation<HuffmanProblem, HuffmanSolution> {

    public static final HuffmanDecodingReverse INSTANCE = new HuffmanDecodingReverse();

    private static HuffmanCode toCode(final HuffmanSolution solution) {
        return new HuffmanCode(solution.code().message().replaceAll(" ", ""), solution.code().tree());
    }

    private HuffmanDecodingReverse() {}

    @Override
    public HuffmanSolution apply(final HuffmanProblem problem) {
        return HuffmanTree.toHuffmanTree(problem.message(), problem.alphabet());
    }

    @Override
    public String commandPrefix() {
        return "FromHuffman";
    }

    @Override
    public HuffmanProblem generateProblem(final Parameters<Flag> options) {
        return HuffmanEncoding.generateProblemStatically(options);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[0];
        return result;
    }

    @Override
    public List<HuffmanProblem> parseProblems(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        return HuffmanEncoding.parseProblemsStatically(reader, options);
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<HuffmanProblem> problems,
        final List<HuffmanSolution> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        HuffmanDecoding.printBeforeMultipleProblemInstancesStatically(writer);
    }

    @Override
    public void printBeforeSingleProblemInstance(
        final HuffmanProblem problem,
        final HuffmanSolution solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        HuffmanDecoding.printBeforeSingleProblemInstanceStatically(writer);
    }

    @Override
    public void printProblemInstance(
        final HuffmanProblem problem,
        final HuffmanSolution solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        HuffmanDecoding.printProblemInstanceStatically(HuffmanDecodingReverse.toCode(solution), writer);
    }

    @Override
    public void printSolutionInstance(
        final HuffmanProblem problem,
        final HuffmanSolution solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        HuffmanDecoding.printSolutionInstanceStatically(problem.message(), writer);
    }

    @Override
    public void printSolutionSpace(
        final HuffmanProblem problem,
        final HuffmanSolution solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        HuffmanDecoding.printSolutionSpaceStatically(writer);
    }

}
