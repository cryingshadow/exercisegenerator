package exercisegenerator;

import java.io.*;
import java.util.concurrent.*;

import org.testng.*;
import org.testng.annotations.Test;

import exercisegenerator.algorithms.*;

public class GenerateCompileTest {

    private static final boolean OFF = false;

    private static Process buildAndStartProcess(final String fileName, final File testDir) throws IOException {
        return new ProcessBuilder(
            "pdflatex",
            fileName,
            "-interaction=nonstopmode",
            "-halt-on-error"
        ).inheritIO().directory(testDir).start();
    }

    private static void cleanUp(final File testDir) {
        for (final File file : testDir.listFiles()) {
            file.delete();
        }
    }

    private static String[] toCLIArguments(
        final Algorithm alg,
        final String[] testOptions,
        final File exFile,
        final File solFile
    ) {
        final int numOfAddedParameters = 8;
        final String[] result = new String[testOptions.length + numOfAddedParameters];
        result[0] = "-a";
        result[1] = alg.name;
        result[2] = "-x";
        result[3] = "standalone";
        result[4] = "-e";
        result[5] = exFile.getAbsolutePath();
        result[6] = "-t";
        result[7] = solFile.getAbsolutePath();
        System.arraycopy(testOptions, 0, result, numOfAddedParameters, testOptions.length);
        return result;
    }

    @Test
    public void generateCompile() throws IOException, InterruptedException {
        if (GenerateCompileTest.OFF) {
            return;
        }
        final int numberOfRunsForEachAlgorithm = 3;
        final File locateTmp = File.createTempFile("locate", "tmp");
        final File testDir = new File(locateTmp.getParentFile().getAbsolutePath(), "gencomp");
        testDir.mkdir();
        final String exFileName = "exercise.tex";
        final String solFileName = "solution.tex";
        final File exFile = new File(testDir, exFileName);
        final File solFile = new File(testDir, solFileName);
        for (final Algorithm alg : Algorithm.values()) {
            if (!alg.enabled) {
                continue;
            }
            for (int i = 0; i < numberOfRunsForEachAlgorithm; i++) {
                Main.main(GenerateCompileTest.toCLIArguments(alg, alg.generateTestParameters.get(), exFile, solFile));
                final Process processExercise = GenerateCompileTest.buildAndStartProcess(exFileName, testDir);
                final Process processSolution = GenerateCompileTest.buildAndStartProcess(solFileName, testDir);
                processExercise.waitFor(30, TimeUnit.SECONDS);
                processSolution.waitFor(30, TimeUnit.SECONDS);
                Assert.assertEquals(processExercise.exitValue(), 0, alg.name + " yields non-compiling exercise!");
                Assert.assertEquals(processSolution.exitValue(), 0, alg.name + " yields non-compiling solution!");
                GenerateCompileTest.cleanUp(testDir);
            }
        }
        testDir.delete();
        locateTmp.delete();
    }

}
