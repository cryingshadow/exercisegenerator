package exercisegenerator;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.structures.*;

public class GenerateCompileTest {

    private static final boolean OFF = false;

    private static final Algorithm ONLY = null;

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
        final String exName = "exercise";
        final String solName = "solution";
        final String suffix = ".tex";
        for (final Algorithm alg : Algorithm.values()) {
            if (!alg.enabled || (GenerateCompileTest.ONLY != null && alg != GenerateCompileTest.ONLY)) {
                continue;
            }
            final List<Pair<Process, String>> processes = new LinkedList<Pair<Process, String>>();
            for (int i = 0; i < numberOfRunsForEachAlgorithm; i++) {
                final String exFileName = String.join("", exName, alg.name, String.valueOf(i), suffix);
                final String solFileName = String.join("", solName, alg.name, String.valueOf(i), suffix);
                final File exFile = new File(testDir, exFileName);
                final File solFile = new File(testDir, solFileName);
                Main.main(
                    GenerateCompileTest.toCLIArguments(alg, alg.implementation.generateTestParameters(), exFile, solFile)
                );
                final Process processExercise = GenerateCompileTest.buildAndStartProcess(exFileName, testDir);
                final Process processSolution = GenerateCompileTest.buildAndStartProcess(solFileName, testDir);
                processes.add(
                    new Pair<Process, String>(
                        processExercise,
                        String.format("%s yields non-compiling exercise! See: %s", alg.name, testDir.getAbsolutePath())
                    )
                );
                processes.add(
                    new Pair<Process, String>(
                        processSolution,
                        String.format("%s yields non-compiling solution! See: %s", alg.name, testDir.getAbsolutePath())
                    )
                );
            }
            for (final Pair<Process, String> pair : processes) {
                pair.x.waitFor(30, TimeUnit.SECONDS);
                Assert.assertEquals(pair.x.exitValue(), 0, pair.y);
            }
            GenerateCompileTest.cleanUp(testDir);
        }
        testDir.delete();
        locateTmp.delete();
    }

}
