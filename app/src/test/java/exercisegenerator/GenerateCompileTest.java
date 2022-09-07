package exercisegenerator;

import java.io.*;
import java.util.concurrent.*;

import org.testng.*;
import org.testng.annotations.Test;

import exercisegenerator.algorithms.*;

public class GenerateCompileTest {

    private static String[] toCLIArguments(final Algorithm alg, final String[] testOptions) {
        final int numOfAddedParameters = 8;
        final String[] result = new String[testOptions.length + numOfAddedParameters];
        result[0] = "-a";
        result[1] = alg.name;
        result[2] = "-x";
        result[3] = "standalone";
        result[4] = "-e";
        result[5] = MainTest.EX_FILE;
        result[6] = "-t";
        result[7] = MainTest.SOL_FILE;
        System.arraycopy(testOptions, 0, result, numOfAddedParameters, testOptions.length);
        return result;
    }

    @Test
    public void generateCompile() throws IOException, InterruptedException {
        MainTest.prepare();
        final int numberOfRunsForEachAlgorithm = 2;
        for (final Algorithm alg : Algorithm.values()) {
            if (!alg.enabled) {
                continue;
            }
            for (int i = 0; i < numberOfRunsForEachAlgorithm; i++) {
                Main.main(GenerateCompileTest.toCLIArguments(alg, alg.generateTestParameters.get()));
                final ProcessBuilder processBuilderExercise =
                    new ProcessBuilder(
                        "pdflatex",
                        MainTest.EX_FILE_NAME,
                        "-interaction=nonstopmode",
                        "-halt-on-error"
                    ).inheritIO().directory(new File(MainTest.TEST_DIR));
                final Process processExercise = processBuilderExercise.start();
                final ProcessBuilder processBuilderSolution =
                    new ProcessBuilder(
                        "pdflatex",
                        MainTest.SOL_FILE_NAME,
                        "-interaction=nonstopmode",
                        "-halt-on-error"
                    ).inheritIO().directory(new File(MainTest.TEST_DIR));
                final Process processSolution = processBuilderSolution.start();
                processExercise.waitFor(30, TimeUnit.SECONDS);
                processSolution.waitFor(30, TimeUnit.SECONDS);
                Assert.assertEquals(processExercise.exitValue(), 0, alg.name + " yields non-compiling exercise!");
                Assert.assertEquals(processSolution.exitValue(), 0, alg.name + " yields non-compiling solution!");
                MainTest.cleanUp();
            }
        }
    }

}
