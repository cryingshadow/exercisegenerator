package exercisegenerator;

import java.io.*;
import java.util.*;

import exercisegenerator.algorithms.*;

/**
 * Class for testing the student mode of DSALExercises.
 * @author Thomas Stroeder
 * @version 1.1.0
 */
public class DSALExercisesTest {

    /**
     * Name of an algorithm to debug alone. Null to debug all enabled algorithms.
     */
    public static final String toDebug = null;

    /**
     * Runs all enabled algorithms 100 times and provides a make file.
     * @param args Contains the path where the generated .tex files should be stored as its only argument.
     */
    public static void main(final String[] args) {
        if (args == null || args.length != 1) {
            System.out.println("Path must be specified!");
            return;
        }
        if (!Main.STUDENT_MODE) {
            System.out.println("Student mode must be enabled!");
            return;
        }
        final String path = args[0];
        final String[] nextArgs = new String[6];
        nextArgs[0] = "-e";
        nextArgs[2] = "-t";
        nextArgs[4] = "-a";
        final List<String> tasks = new ArrayList<String>();
        for (final Algorithm alg : Algorithm.values()) {
            if (DSALExercisesTest.doNotConsider(alg)) {
                continue;
            }
            System.out.print("Testing: " + alg.name);
            for(int i = 0; i < 30; ++i) {
                if (i>alg.name.length()) {
                    System.out.print(" ");
                }
            }
            final String name = alg.name;
            nextArgs[5] = name;
            for (int i = 0; i < 100; i++) {
                final String exFile = name + "Ex" + i + ".tex";
                final String solFile = name + "Sol" + i + ".tex";
                nextArgs[1] = path + File.separator + exFile;
                nextArgs[3] = path + File.separator + solFile;
                tasks.add(exFile);
                tasks.add(solFile);
                Main.main(nextArgs);
            }
            System.out.println("done.");
        }
        final File make = new File(path + File.separator + "Makefile");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(make))) {
            writer.write("default:");
            writer.newLine();
            for (final String task : tasks) {
                writer.write("\t" + "pdflatex " + task);
                writer.newLine();
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param val The default value to set.
     * @param check The name of an algorithm to set the enabled value for.
     * @return If toDebug is null, val is returned. Otherwise true is returned iff check equals toDebug.
     */
    public static boolean set(final boolean val, final String check) {
        if (DSALExercisesTest.toDebug == null) {
            return val;
        }
        return DSALExercisesTest.toDebug.equals(check);
    }

    private static boolean doNotConsider(final Algorithm alg) {
        return !alg.enabled || (DSALExercisesTest.toDebug != null && !alg.name.equals(DSALExercisesTest.toDebug));
    }

}
