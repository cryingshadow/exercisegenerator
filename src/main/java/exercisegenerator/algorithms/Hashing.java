package exercisegenerator.algorithms;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.util.*;

/**
 * This class provides methods for hashing.
 * @author Stefan Schupp, Thomas Stroeder
 * @version 1.1.0
 */
public abstract class Hashing {

    private static final String div = "\\emphasize{Divisionsmethode}";

    private static final String hash1 =
        "F\\\"ugen Sie die folgenden Werte in das unten stehende Array \\texttt{a} der L\\\"ange ";

    private static final String hash2 = " unter Verwendung der ";

    private static final String hash3 = " ein";

    private static final String hash4 = ":\\\\[2ex]";

    private static final String linProb = " mit \\emphasize{linearer Sondierung}";

    private static final String mult1 = "\\emphasize{Multiplikationsmethode} ($c = ";

    private static final String mult2 = "$)";

    private static final String noProb = " \\emphasize{ohne Sondierung} (also durch Verkettung)";

    /**
     * Array containing all prime numbers between 5 and 101.
     */
    private static final int[] PRIMES_5_101 =
        new int[]{5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101};

    private static final String quadProb1 = " mit \\emphasize{quadratischer Sondierung} ($c_1 = ";

    private static final String quadProb2 = "$, $c_2 = ";

    private static final String quadProb3 = "$)";

    /**
     * @param gen A random number generator.
     * @param length The number of elements to hash in.
     * @param alg The name of the algorithm to use for hashing.
     * @return A pair of parameters and the values to hash in.
     */
    public static Pair<double[], Integer[]> createRandomInput(final Random gen, final int length, final String alg) {
        final Integer[] array = new Integer[length];
        final int extendedLength = (int)(length * 1.25);
        for (int i = 0; i < array.length; i++) {
            array[i] = gen.nextInt(Main.NUMBER_LIMIT);
        }
        final double[] params = new double[4];
        // create all possible constants per default.
        int m = 0;
        if (alg == "hashDivision" || alg == "hashMultiplication") {
            final Integer[] primes = Hashing.getAllUpToNextPrimes(extendedLength);
            final int index = gen.nextInt(primes.length);
            m = primes[index];
        } else {
            m = Hashing.getNextPrime(extendedLength);
            while (gen.nextBoolean()) {
                m = Hashing.getNextPrime(m+1);
            }
        }
        int c1 = gen.nextInt(m);
        // c2 must not be 0
        int c2 = gen.nextInt(m - 1) + 1;
        if (alg == "hashDivisionQuadratic" || alg == "hashMultiplicationQuadratic") {
            while (!Hashing.areCoprime(m, c1, c2)) {
                c1 = gen.nextInt(m);
                if (Hashing.areCoprime(m, c1, c2)) {
                    break;
                }
                c2 = gen.nextInt(m - 1) + 1;
                if (Hashing.areCoprime(m, c1, c2)) {
                    break;
                }
            }
        }
        final double c = Math.round(100.0 * gen.nextDouble()) / 100.0;
        params[0] = m;
        params[1] = c;
        params[2] = c1;
        params[3] = c2;
        return new Pair<double[], Integer[]>(params, array);
    }

    public static void hashDiv(final AlgorithmInput input) throws Exception {
        final Pair<double[], Integer[]> paramsAndArray = Hashing.parseOrGenerateParamsAndArray(input.options);
        final Integer m = (int)paramsAndArray.x[0];
        final double[] params = new double[5];
        params[0] = 1;
        params[1] = 0;
        params[2] = 0;
        params[3] = 0;
        params[4] = 0;
        try {
            Hashing.hashing(paramsAndArray.y, m, params, !Main.STUDENT_MODE, input.solutionWriter);
        } catch (final HashException e) {
            throw new IllegalStateException("Could not hash without probing - this should be impossible...");
        }
        input.exerciseWriter.write(Hashing.hash1 + m);
        input.exerciseWriter.write(Hashing.hash2);
        input.exerciseWriter.write(Hashing.div);
        input.exerciseWriter.write(Hashing.noProb);
        input.exerciseWriter.write(Hashing.hash3);
        switch (Main.TEXT_VERSION) {
            case ABRAHAM:
                input.exerciseWriter.write(" ($f(n) = n \\mod " + m);
                input.exerciseWriter.write("$)");
                break;
            case GENERAL:
                break;
            default:
                throw new IllegalStateException("Unkown text version!");
        }
        input.exerciseWriter.write(Hashing.hash4);
        Hashing.printExercise(
            paramsAndArray.y,
            m,
            false,
            Algorithm.parsePreprintMode(input.options),
            input.exerciseWriter
        );
    }

    public static void hashDivLin(final AlgorithmInput input) throws Exception {
        final Pair<double[], Integer[]> paramsAndArray = Hashing.parseOrGenerateParamsAndArray(input.options);
        final Integer m = (int)paramsAndArray.x[0];
        final double[] params = new double[5];
        params[0] = 1;
        params[1] = 1;
        params[2] = 0;
        params[3] = 0;
        params[4] = 0;
        try {
            Hashing.hashing(paramsAndArray.y, m, params, !Main.STUDENT_MODE, input.solutionWriter);
        } catch (final HashException e) {
            throw new IllegalStateException("Could not hash with linear probing - this should not happen...");
        }
        input.exerciseWriter.write(Hashing.hash1 + m);
        input.exerciseWriter.write(Hashing.hash2);
        input.exerciseWriter.write(Hashing.div);
        input.exerciseWriter.write(Hashing.linProb);
        input.exerciseWriter.write(Hashing.hash3);
        switch (Main.TEXT_VERSION) {
            case ABRAHAM:
                input.exerciseWriter.write(" ($f(n,i) = ((n \\mod " + m);
                input.exerciseWriter.write(") + i) \\mod " + m);
                input.exerciseWriter.write("$)");
                break;
            case GENERAL:
                break;
            default:
                throw new IllegalStateException("Unkown text version!");
        }
        input.exerciseWriter.write(Hashing.hash4);
        Hashing.printExercise(
            paramsAndArray.y,
            m,
            true,
            Algorithm.parsePreprintMode(input.options),
            input.exerciseWriter
        );
    }

    public static void hashDivQuad(final AlgorithmInput input) throws Exception {
        final Pair<double[], Integer[]> paramsAndArray = Hashing.parseOrGenerateParamsAndArray(input.options);
        final Integer m = (int)paramsAndArray.x[0];
        double c1 = paramsAndArray.x[2];
        double c2 = paramsAndArray.x[3];
        final double[] params = new double[5];
        params[0] = 1;
        params[1] = 2;
        params[2] = 0;
        params[3] = c1;
        params[4] = c2;
        boolean fail;
        do {
            try {
                fail = false;
                Hashing.hashing(paramsAndArray.y, m, params, !Main.STUDENT_MODE, input.solutionWriter);
            } catch (final HashException e) {
                final Random gen = new Random();
                final Pair<Double, Double> cs = Hashing.newDivQuadInstance(gen, m, params);
                c1 = cs.x;
                c2 = cs.y;
                params[3] = c1;
                params[4] = c2;
                fail = true;
            }
        } while (fail);
        input.exerciseWriter.write(Hashing.hash1 + m);
        input.exerciseWriter.write(Hashing.hash2);
        input.exerciseWriter.write(Hashing.div);
        input.exerciseWriter.write(Hashing.quadProb1 + c1);
        input.exerciseWriter.write(Hashing.quadProb2 + c2);
        input.exerciseWriter.write(Hashing.quadProb3);
        input.exerciseWriter.write(Hashing.hash3);
        switch (Main.TEXT_VERSION) {
            case ABRAHAM:
                input.exerciseWriter.write(" ($f(n,i) = ((n \\mod " + m);
                input.exerciseWriter.write(") + " + c1);
                input.exerciseWriter.write(" \\cdot i + " + c2);
                input.exerciseWriter.write(" \\cdot i^2) \\mod " + m);
                input.exerciseWriter.write("$)");
                break;
            case GENERAL:
                break;
            default:
                throw new IllegalStateException("Unkown text version!");
        }
        input.exerciseWriter.write(Hashing.hash4);
        Hashing.printExercise(
            paramsAndArray.y,
            m,
            true,
            Algorithm.parsePreprintMode(input.options),
            input.exerciseWriter
        );
    }

    /**
     * Performs hashing in different variations (divisionmethod(1), multiplicationmethod(2), linear probing(1),
     * quadratic probing(2), no probing (0)).
     * @param in The array containing the values, which are to be hashed.
     * @param m The length of the hashtable.
     * @param params The array containing the parameters in this order: Algorithm (1,2), probe-mode (0,1,2), additional
     *               parameters: c, c1, c2
     * @param debug Flag indicating whether debug output should be printed on stdout.
     * @param writer The writer for the solution.
     * @throws IOException If some error occurs during output.
     * @throws HashException If in.size > m or if c1 and c2 are badly chosen.
     */
    public static void hashing(
        final Integer[] in,
        final int m,
        final double[] params,
        final boolean debug,
        final BufferedWriter writer
    ) throws IOException, HashException {
        int collisionCount = 0;
        final int algorithm = (int)params[0];
        final int probe = (int)params[1];
        double c = 0.0;
        double c1 = 0.0;
        double c2 = 0.0;
        String init = "";
        switch (probe) {
            case 2:
                switch (algorithm) {
                    case 1:
                        // Division Hashing
                        c1 = params[3];
                        c2 = params[4];
                        init ="m = " + m + ", $c_1$ = " + c1 + ", $c_2$ = " + c2 + ":\\\\[2ex]";
                        break;
                    case 2:
                        // Multiplication Hashing
                        c = params[2];
                        c1 = params[3];
                        c2 = params[4];
                        init = "m = " + m + ", c = " + c + ", $c_1$ = " + c1 + ", $c_2$ = " + c2 + ":\\\\[2ex]";
                        break;
                }
                break;
            default:
                switch (algorithm) {
                    case 1:
                        // Division Hashing
                        init = "m = " + m + ":\\\\[2ex]";
                        break;
                    case 2:
                        // Multiplication Hashing
                        c = params[2];
                        init = "m = " + m + ", c = " + c + ":\\\\[2ex]";
                        break;
                }
                break;
        }
        if (probe != 0) {
            // probing
            final Integer[] solution = new Integer[m];
            for (int i = 0; i < solution.length; ++i) {
                solution[i] = null;
            }
            for (int i = 0; i < in.length; ++i) {
                int pos = 0;
                switch (algorithm) {
                    case 1:
                        // Division Hashing
                        pos = in[i]%m;
                        break;
                    case 2:
                        // Multiplication Hashing
                        pos = (int)Math.floor(m * (Math.round(((in[i] * c) - (int)(in[i] * c)) * 100.0) / 100.0)) % m;
//                        System.out.println(
//                            "Parameters: c="
//                            + c
//                            + ", m="
//                            + m
//                            + ", in[i]="
//                            + in[i]
//                            + ", pos="
//                            + pos
//                            + " k*c="
//                            + (Math.round(((in[i] * c) - (int)(in[i] * c)) * 100.0) / 100.0)
//                        );
                        break;
                }
                if (solution[pos] == null) {
                    solution[pos] = in[i];
                } else {
                    int off = 1;
                    switch (probe) {
                        case 1:
                            // linear probing
                            while (solution[(pos+off)%m] != null && off < m) {
                                ++off;
                                ++collisionCount;
                            }
                            if (solution[(pos+off)%m] != null) {
                                throw new HashException("The array size was chosen too small!");
                            }
                            solution[(pos+off)%m] = in[i];
                            break;
                        case 2:
                            // quadratic probing
                            while (solution[((int)Math.floor(pos + c1*off + c2*off*off))%m] != null && off < m) {
                                ++off;
                                ++collisionCount;
                            }
                            if (solution[((int)Math.floor(pos + c1*off + c2*off*off))%m] != null) {
                                throw new HashException(
                                    "The array size was chosen too small or the constants for quadratic probing are "
                                    + "chosen badly: Insertion of "
                                    + in[i]
                                    + " failed!"
                                );
                            }
                            solution[((int)Math.floor(pos + c1*off + c2*off*off))%m] = in[i];
                            break;
                        case 3:
                            // doppeltes hashing
                            break;
                        default:
                    }
                }
            }
            if (debug) {
                System.out.println("#collisions by probing: " + collisionCount);
            }
            writer.write(init);
            Main.newLine(writer);
            writer.write("{\\Large");
            Main.newLine(writer);
            TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
            TikZUtils.printArray(solution, null, null, null, writer);
        } else {
            // probe == 0 -> no probing
            writer.write(init);
            Main.newLine(writer);
            writer.write("{\\Large");
            Main.newLine(writer);
            TikZUtils.printTikzBeginning(TikZStyle.BORDERLESS, writer);
            final String[] solution = new String[m];
            for (int i = 0; i < m; ++i) {
                solution[i] = i + ":";
            }
            for (int i = 0; i < in.length; ++i) {
                int pos = 0;
                switch (algorithm) {
                    case 1:
                        // Division Hashing
                        pos = in[i]%m;
                        break;
                    case 2:
                        // Multiplication Hashing
                        pos = (int)Math.floor( m * Math.round(((in[i] * c) - (int)(in[i] * c)) * 100.0) / 100.0 ) % m;
                        break;
                }
                if (solution[pos].substring(solution[pos].length()-1).equals(":")) {
                    solution[pos] += " " + in[i];
                } else {
                    solution[pos] += ", " + in[i];
                }
            }
            TikZUtils.printVerticalStringArray(solution, null, null, null, writer);
        }
        TikZUtils.printTikzEnd(writer);
        writer.write("}");
        Main.newLine(writer);
    }

    public static void hashMult(final AlgorithmInput input) throws Exception {
        final Pair<double[], Integer[]> paramsAndArray = Hashing.parseOrGenerateParamsAndArray(input.options);
        final Integer m = (int)paramsAndArray.x[0];
        final double c = paramsAndArray.x[1];
        final double[] params = new double[5];
        params[0] = 2;
        params[1] = 0;
        params[2] = c;
        params[3] = 0;
        params[4] = 0;
        try {
            Hashing.hashing(paramsAndArray.y, m, params, !Main.STUDENT_MODE, input.solutionWriter);
        } catch (final HashException e) {
            throw new IllegalStateException("Could not hash without probing - this should be impossible...");
        }
        input.exerciseWriter.write(Hashing.hash1 + m);
        input.exerciseWriter.write(Hashing.hash2);
        input.exerciseWriter.write(Hashing.mult1 + c);
        input.exerciseWriter.write(Hashing.mult2);
        input.exerciseWriter.write(Hashing.noProb);
        input.exerciseWriter.write(Hashing.hash3);
        switch (Main.TEXT_VERSION) {
            case ABRAHAM:
                input.exerciseWriter.write(" ($f(n,i) = \\left \\lfloor{" + m);
                input.exerciseWriter.write(" \\cdot ( n \\cdot" + c);
                input.exerciseWriter.write(
                    " \\mod 1 )\\right \\rfloor $), wobei $x \\mod 1$ den Nachkommateil von $x$ bezeichnet"
                );
                break;
            case GENERAL:
                break;
            default:
                throw new IllegalStateException("Unkown text version!");
        }
        input.exerciseWriter.write(Hashing.hash4);
        Hashing.printExercise(
            paramsAndArray.y,
            m,
            false,
            Algorithm.parsePreprintMode(input.options),
            input.exerciseWriter
        );
    }

    public static void hashMultLin(final AlgorithmInput input) throws Exception {
        final Pair<double[], Integer[]> paramsAndArray = Hashing.parseOrGenerateParamsAndArray(input.options);
        final Integer m = (int)paramsAndArray.x[0];
        final double c = paramsAndArray.x[1];
        final double[] params = new double[5];
        params[0] = 2;
        params[1] = 1;
        params[2] = c;
        params[3] = 0;
        params[4] = 0;
        try {
            Hashing.hashing(paramsAndArray.y, m, params, !Main.STUDENT_MODE, input.solutionWriter);
        } catch (final HashException e) {
            throw new IllegalStateException("Could not hash with linear probing - this should not happen...");
        }
        input.exerciseWriter.write(Hashing.hash1 + m);
        input.exerciseWriter.write(Hashing.hash2);
        input.exerciseWriter.write(Hashing.mult1 + c);
        input.exerciseWriter.write(Hashing.mult2);
        input.exerciseWriter.write(Hashing.linProb);
        input.exerciseWriter.write(Hashing.hash3);
        switch (Main.TEXT_VERSION) {
            case ABRAHAM:
                input.exerciseWriter.write(" ($f(n,i) = \\left \\lfloor{" + m);
                input.exerciseWriter.write(" \\cdot ( n \\cdot " + c);
                input.exerciseWriter.write(" \\mod 1 )}\\right \\rfloor~ + i \\mod " + m);
                input.exerciseWriter.write(" $), wobei $x \\mod 1$ den Nachkommateil von $x$ bezeichnet");
                break;
            case GENERAL:
                break;
            default:
                throw new IllegalStateException("Unkown text version!");
        }
        input.exerciseWriter.write(Hashing.hash4);
        Hashing.printExercise(
            paramsAndArray.y,
            m,
            true,
            Algorithm.parsePreprintMode(input.options),
            input.exerciseWriter
        );
    }

    public static void hashMultQuad(final AlgorithmInput input) throws Exception {
        final Pair<double[], Integer[]> paramsAndArray = Hashing.parseOrGenerateParamsAndArray(input.options);
        final Integer m = (int)paramsAndArray.x[0];
        double c = paramsAndArray.x[1];
        double c1 = paramsAndArray.x[2];
        double c2 = paramsAndArray.x[3];
        final double[] params = new double[5];
        params[0] = 2;
        params[1] = 2;
        params[2] = c;
        params[3] = c1;
        params[4] = c2;
        boolean fail;
        do {
            try {
                fail = false;
                Hashing.hashing(paramsAndArray.y, m, params, !Main.STUDENT_MODE, input.solutionWriter);
            } catch (final HashException e) {
                final Random gen = new Random();
                final Pair<Double, Pair<Double, Double>> cs = Hashing.newMultQuadInstance(gen, m, params);
                c = cs.x;
                c1 = cs.y.x;
                c2 = cs.y.y;
                fail = true;
            }
        } while (fail);
        input.exerciseWriter.write(Hashing.hash1 + m);
        input.exerciseWriter.write(Hashing.hash2);
        input.exerciseWriter.write(Hashing.mult1 + c);
        input.exerciseWriter.write(Hashing.mult2);
        input.exerciseWriter.write(Hashing.quadProb1 + c1);
        input.exerciseWriter.write(Hashing.quadProb2 + c2);
        input.exerciseWriter.write(Hashing.quadProb3);
        input.exerciseWriter.write(Hashing.hash3);
        switch (Main.TEXT_VERSION) {
            case ABRAHAM:
                input.exerciseWriter.write(" ($f(n,i) = \\left \\lfloor{" + m);
                input.exerciseWriter.write(" \\cdot ( n \\cdot" + c);
                input.exerciseWriter.write(" \\mod 1 )}\\right \\rfloor + " + c1);
                input.exerciseWriter.write(" \\cdot i + " + c2);
                input.exerciseWriter.write(" \\cdot i^2 $), wobei $x \\mod 1$ den Nachkommateil von $x$ bezeichnet");
                break;
            case GENERAL:
                break;
            default:
                throw new IllegalStateException("Unkown text version!");
        }
        input.exerciseWriter.write(Hashing.hash4);
        Hashing.printExercise(
            paramsAndArray.y,
            m,
            true,
            Algorithm.parsePreprintMode(input.options),
            input.exerciseWriter
        );
    }

    /**
     * Changes the parameters for hashing to a new instance for the division method with quadratic probing.
     * @param gen A random number generator.
     * @param m The size of the hash table.
     * @param params The parameters for hashing.
     * @return The pair of new parameters.
     */
    public static Pair<Double, Double> newDivQuadInstance(final Random gen, final int m, final double[] params) {
        int c1 = gen.nextInt(m);
        int c2 = gen.nextInt(m);
        while (!Hashing.areCoprime(m, c1, c2)) {
            c1 = gen.nextInt(m);
            if (Hashing.areCoprime(m, c1, c2)) {
                break;
            }
            c2 = gen.nextInt(m);
            if (Hashing.areCoprime(m, c1, c2)) {
                break;
            }
        }
        params[3] = c1;
        params[4] = c2;
        return new Pair<Double, Double>((double)c1, (double)c2);
    }

    /**
     * Changes the parameters for hashing to a new instance for the multiplication method with quadratic probing.
     * @param gen A random number generator.
     * @param m The size of the hash table.
     * @param params The parameters for hashing.
     * @return The pair of new parameters.
     */
    public static Pair<Double, Pair<Double, Double>> newMultQuadInstance(final Random gen, final int m, final double[] params) {
        int c1 = gen.nextInt(m);
        int c2 = gen.nextInt(m);
        while (!Hashing.areCoprime(m, c1, c2)) {
            c1 = gen.nextInt(m);
            if (Hashing.areCoprime(m, c1, c2)) {
                break;
            }
            c2 = gen.nextInt(m);
            if (Hashing.areCoprime(m, c1, c2)) {
                break;
            }
        }
        final double c = Math.round(100.0 * gen.nextDouble()) / 100.0;
        params[2] = c;
        params[3] = c1;
        params[4] = c2;
        return new Pair<Double, Pair<Double, Double>>(c, new Pair<Double, Double>((double)c1, (double)c2));
    }

    /**
     * Prints the exercise text for this hashing exercise.
     * @param input The values to hash.
     * @param size The size of the hash table.
     * @param probing Flag indicating whether probing is used.
     * @param mode The preprint mode.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printExercise(
        final Integer[] input,
        final int size,
        final boolean probing,
        final PreprintMode mode,
        final BufferedWriter writer
    ) throws IOException {
        Main.newLine(writer);
        for (int i = 0; i < input.length - 1; ++i) {
            writer.write(input[i] + ", ");
        }
        writer.write(input[input.length-1] + ".");
        switch (mode) {
            case ALWAYS:
            case SOLUTION_SPACE:
                writer.write("\\\\[4ex]");
                Main.newLine(writer);
                if (mode == PreprintMode.SOLUTION_SPACE) {
                    TikZUtils.printSolutionSpaceBeginning(writer);
                }
                writer.write("{\\Large");
                Main.newLine(writer);
                if (probing) {
                    TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
                    TikZUtils.printEmptyArrayWithIndex(size, writer);
                } else {
                    final String[] solution = new String[size];
                    for (int i = 0; i < size; ++i) {
                        solution[i] = i + ":";
                    }
                    TikZUtils.printTikzBeginning(TikZStyle.BORDERLESS, writer);
                    TikZUtils.printVerticalStringArray(solution, null, null, null, writer);
                }
                TikZUtils.printTikzEnd(writer);
                writer.write("}");
                Main.newLine(writer);
                if (mode == PreprintMode.SOLUTION_SPACE) {
                    TikZUtils.printSolutionSpaceEnd(writer);
                }
                break;
            case NEVER:
                Main.newLine(writer);
        }
    }

    /**
     * @param a Some number.
     * @param b Another number.
     * @param c Yet another number.
     * @return True if the three numbers are coprime to each other. False otherwise.
     */
    private static boolean areCoprime(final int a, final int b, final int c) {
        return Hashing.gcd(a,b) == 1 && Hashing.gcd(b,c) == 1 && Hashing.gcd(a,c) == 1;
    }

    /**
     * Computes the gcd of two numbers by using the Eucilidian algorithm.
     * @param number1 The first of the two numbers.
     * @param number2 The second of the two numbers.
     * @return The greates common divisor of number1 and number2.
     */
    private static int gcd(final int number1, final int number2) {
        //base case
        if (number2 == 0) {
            return number1;
        }
        return Hashing.gcd(number2, number1%number2);
    }

    /**
     * @param value Some value.
     * @return The prime numbers from 5 to the smallest prime number being greater than or equal to the specified value
     *         (if that value is at most 101). If the specified value is bigger than 101, all prime numbers between 5
     *         and 101 are returned.
     */
    private static Integer[] getAllUpToNextPrimes(final int value) {
        final List<Integer> result = new ArrayList<Integer>();
        int current = 0;
        while (Hashing.PRIMES_5_101[current] < value && current < Hashing.PRIMES_5_101.length - 1) {
            result.add(Hashing.PRIMES_5_101[current]);
            current++;
        }
        result.add(Hashing.PRIMES_5_101[current]);
        return result.toArray(new Integer[result.size()]);
    }

    /**
     * @param start Value which defines the lower bound for the resulting prime.
     * @return The next largest prime greater than or equal to the input.
     */
    private static int getNextPrime(final int start) {
        int current = start;
        while (!Hashing.isPrime(current)) {
            current++;
        }
        return current;
    }

    /**
     * @param value Some value.
     * @return True iff the specified value is prime.
     */
    private static boolean isPrime(final int value) {
        if (value < 2) {
            return false;
        }
        if (value == 2) {
            return true;
        }
        if (value % 2 == 0) {
            return false;
        }
        for (int i = 3; i <= Math.sqrt(value) + 1; i = i + 2) {
            if (value % i == 0) {
                return false;
            }
        }
        return true;
    }

    private static Pair<double[], Integer[]> parseOrGenerateParamsAndArray(final Map<Flag, String> options) {
        Pair<double[], Integer[]> input = new Pair<double[], Integer[]>(null,null);
        final String alg = options.get(Flag.ALGORITHM);
        String[] paramString = null;
        Integer[] array;
        final String[] nums;
        if (options.containsKey(Flag.SOURCE)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(options.get(Flag.SOURCE)))) {
                paramString = reader.readLine().split(",");
                nums = reader.readLine().split(",");
            } catch (final IOException e) {
                e.printStackTrace();
                return null;
            }
        } else if (Main.STUDENT_MODE) {
            final int length;
            final Random gen = new Random();
            if (options.containsKey(Flag.LENGTH)) {
                length = Integer.parseInt(options.get(Flag.LENGTH));
            } else {
                length = gen.nextInt(16) + 5;
            }
            return Hashing.createRandomInput(gen, length, alg);
        } else {
            nums = options.get(Flag.INPUT).split(",");
            paramString = options.get(Flag.DEGREE).split(",");
        }
        array = new Integer[nums.length];
        for (int i = 0; i < array.length; i++) {
            array[i] = Integer.parseInt(nums[i].trim());
        }
        final double[] params = new double[4];
        for (int i = 0; i < paramString.length; ++i) {
            params[i] = Double.parseDouble(paramString[i].trim());
        }
        switch(alg)
        {
            case "hashDivision":
            case "hashDivisionLinear":
                params[1] = 0;
                params[2] = 0;
                params[3] = 0;
                break;
            case "hashMultiplication":
            case "hashMultiplicationLinear":
                params[2] = 0;
                params[3] = 0;
                break;
            case "hashDivisionQuadratic":
                params[3] = params[2];
                params[2] = params[1];
                params[1] = 0;
                break;
            default:
        }
        input = new Pair<double[], Integer[]>(params, array);
        return input;
    }

}
