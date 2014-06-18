import java.io.*;

/**
 * This class provides methods for hashing.
 * @author Stefan Schupp
 * @version $Id$
 */
public abstract class Hashing {

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
//        String anchor = "";
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
                        pos = (int)Math.floor(m * (Math.round(((in[i] * c) - (int)(in[i] * c)) * 100.0) / 100.0));
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
            TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
            ArrayUtils.printArray(solution, null, null, null, writer);
        } else {
            // probe == 0 -> no probing
            writer.write(init);
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
//                        System.out.println(
//                            "Test: "
//                            + (Math.round(((in[i] * c) - (int)(in[i] * c)) * 100.0) / 100.0)
//                            + ", m is: "
//                            + m
//                        );
                        pos = (int)Math.floor( m * Math.round(((in[i] * c) - (int)(in[i] * c)) * 100.0) / 100.0 );
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
    }

    /**
     * Prints the exercise text for this hashing exercise.
     * @param input 
     * @param size The size of the hash table.
     * @param probing Flag indicating whether probing is used.
     * @param longname 
     * @param constants 
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printExercise(
        Integer[] input,
        int size,
        boolean probing,
        BufferedWriter writer
    ) throws IOException {
        writer.newLine();
        for (int i = 0; i < input.length - 1; ++i) {
            writer.write(input[i] + ", ");
        }
        writer.write(input[input.length-1] + ".");
        writer.write("\\\\[2ex]");
        writer.newLine();
        if (probing) {
            Integer[] indizes = new Integer[size];
            for (int i = 0; i < size; ++i) {
                indizes[i] = i;
            }
            TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
            ArrayUtils.printEmptyArray(size, ArrayUtils.printArray(indizes, null, null, null, writer), writer);
        } else {
            String[] solution = new String[size];
            for (int i = 0; i < size; ++i) {
                solution[i] = i + ":";
            }
            TikZUtils.printTikzBeginning(TikZStyle.BORDERLESS, writer);
            ArrayUtils.printVerticalStringArray(solution, null, null, null, writer);
        }
        TikZUtils.printTikzEnd(writer);
    }

}
