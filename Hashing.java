import java.io.*;
import java.util.*;

/**
 * This class provides methods for hashing.
 * @author Stefan Schupp
 * @version $Id$
 */
public abstract class Hashing {

    /**
     * Array containing all prime numbers between 5 and 101.
     */
    private static final int[] PRIMES_5_101 =
        new int[]{5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101};

    /**
     * @param gen A random number generator.
     * @param length The number of elements to hash in.
     * @param alg The name of the algorithm to use for hashing.
     * @return A pair of parameters and the values to hash in.
     */
    public static Pair<double[], Integer[]> createRandomInput(Random gen, int length, String alg) {
        Integer[] array = new Integer[(int)(length*0.75)];
        for (int i = 0; i < array.length; i++) {
            array[i] = gen.nextInt(DSALExercises.NUMBER_LIMIT);
        }
        double[] params = new double[4];
        // create all possible constants per default.
        int m = 0;
        if (alg == "hashDivision" || alg == "hashMultiplication") {
            Integer[] primes = Hashing.getAllUpToNextPrimes(length);
            int index = gen.nextInt(primes.length);
            m = primes[index];
        } else {
            m = Hashing.getNextPrime(length);
            while (gen.nextBoolean()) {
                m = Hashing.getNextPrime(m+1);
            }
        }
        int c1 = gen.nextInt(m);
        int c2 = gen.nextInt(m);
        if (alg == "hashDivisionQuadratic" || alg == "hashMultiplicationQuadratic") {
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
        }
        double c = Math.round(100.0 * gen.nextDouble()) / 100.0;
        params[0] = m;
        params[1] = c;
        params[2] = c1;
        params[3] = c2;
        return new Pair<double[], Integer[]>(params, array);
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
        Integer[] in,
        int m,
        double[] params,
        boolean debug,
        BufferedWriter writer
    ) throws IOException, HashException {
        int collisionCount = 0;
        int algorithm = (int)params[0];
        int probe = (int)params[1];
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
            Integer[] solution = new Integer[m];
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
            writer.newLine();
            writer.write("{\\Large");
            writer.newLine();
            TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
            ArrayUtils.printArray(solution, null, null, null, writer);
        } else {
            // probe == 0 -> no probing
            writer.write(init);
            writer.newLine();
            writer.write("{\\Large");
            writer.newLine();
            TikZUtils.printTikzBeginning(TikZStyle.BORDERLESS, writer);
            String[] solution = new String[m];
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
            ArrayUtils.printVerticalStringArray(solution, null, null, null, writer);
        }
        TikZUtils.printTikzEnd(writer);
        writer.write("}");
        writer.newLine();
    }

    /**
     * Changes the parameters for hashing to a new instance for the division method with quadratic probing.
     * @param gen A random number generator.
     * @param m The size of the hash table.
     * @param params The parameters for hashing.
     * @return The pair of new parameters.
     */
    public static Pair<Double, Double> newDivQuadInstance(Random gen, int m, double[] params) {
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
    public static Pair<Double, Pair<Double, Double>> newMultQuadInstance(Random gen, int m, double[] params) {
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
        double c = Math.round(100.0 * gen.nextDouble()) / 100.0;
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
        Integer[] input,
        int size,
        boolean probing,
        PreprintMode mode,
        BufferedWriter writer
    ) throws IOException {
        writer.newLine();
        for (int i = 0; i < input.length - 1; ++i) {
            writer.write(input[i] + ", ");
        }
        writer.write(input[input.length-1] + ".");
        switch (mode) {
            case ALWAYS:
            case SOLUTION_SPACE:
                writer.write("\\\\[4ex]");
                writer.newLine();
                if (mode == PreprintMode.SOLUTION_SPACE) {
                    TikZUtils.printSolutionSpaceBeginning(writer);
                }
                writer.write("{\\Large");
                writer.newLine();
                if (probing) {
                    TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
                    ArrayUtils.printEmptyArrayWithIndex(size, writer);
                } else {
                    String[] solution = new String[size];
                    for (int i = 0; i < size; ++i) {
                        solution[i] = i + ":";
                    }
                    TikZUtils.printTikzBeginning(TikZStyle.BORDERLESS, writer);
                    ArrayUtils.printVerticalStringArray(solution, null, null, null, writer);
                }
                TikZUtils.printTikzEnd(writer);
                writer.write("}");
                writer.newLine();
                if (mode == PreprintMode.SOLUTION_SPACE) {
                    TikZUtils.printSolutionSpaceEnd(writer);
                }
                break;
            case NEVER:
                writer.newLine();
        }
    }

    /**
     * @param a Some number.
     * @param b Another number.
     * @param c Yet another number.
     * @return True if the three numbers are coprime to each other. False otherwise.
     */
    private static boolean areCoprime(int a, int b, int c) {
        return Hashing.gcd(a,b) == 1 && Hashing.gcd(b,c) == 1 && Hashing.gcd(a,c) == 1;
    }

    /**
     * Computes the gcd of two numbers by using the Eucilidian algorithm.
     * @param number1 The first of the two numbers.
     * @param number2 The second of the two numbers.
     * @return The greates common divisor of number1 and number2.
     */
    private static int gcd(int number1, int number2) {
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
    private static Integer[] getAllUpToNextPrimes(int value) {
        List<Integer> result = new ArrayList<Integer>();
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
    private static int getNextPrime(int start) {
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
    private static boolean isPrime(int value) {
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

}
