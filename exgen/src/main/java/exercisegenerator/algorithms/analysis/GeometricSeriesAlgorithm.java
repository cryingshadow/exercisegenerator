package exercisegenerator.algorithms.analysis;

import java.io.*;
import java.util.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.analysis.*;

public class GeometricSeriesAlgorithm implements AlgorithmImplementation<GeometricSeries, List<ArithmeticTerm>> {

    public static final GeometricSeriesAlgorithm INSTANCE = new GeometricSeriesAlgorithm();

    @Override
    public List<ArithmeticTerm> apply(final GeometricSeries problem) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String[] generateTestParameters() {
        return new String[] {}; //TODO
    }

    @Override
    public GeometricSeries parseOrGenerateProblem(final Parameters options) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void printExercise(
        final GeometricSeries problem,
        final List<ArithmeticTerm> solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void printSolution(
        final GeometricSeries problem,
        final List<ArithmeticTerm> solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        // TODO Auto-generated method stub

    }

}
