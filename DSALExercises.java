import java.io.*;
import java.util.*;

/**
 * Programm for creating solutions for DSAL exercises.
 * @author cryingshadow
 */
public class DSALExercises {

    /**
     * Flag used to turn off some options for the student version.
     */
    public static final boolean STUDENT_MODE;

    /**
     * The set of (in student mode only enabled) hashing algorithms.
     */
    private static final Set<String> HASHING_ALGORITHMS;

    /**
     * The help text displayed when just called with -h. Each entry is separated by a newline.
     */
    private static final String[] HELP;

    /**
     * Limit for random numbers in student mode.
     */
    private static final int NUMBER_LIMIT;

    /**
     * The set of (in student mode only enabled) sorting algorithms.
     */
    private static final Set<String> SORTING_ALGORITHMS;

    /**
     * The set of (in student mode only enabled) tree algorithms.
     */
    private static final Set<String> TREE_ALGORITHMS;
    
    /**
     * The version of this program.
     */
    private static final String VERSION;

    static {
        VERSION = "1.0";
        NUMBER_LIMIT = 100;
        STUDENT_MODE = true;
        HASHING_ALGORITHMS = DSALExercises.initHashingAlgorithms();
        SORTING_ALGORITHMS = DSALExercises.initSortingAlgorithms();
        TREE_ALGORITHMS = DSALExercises.initTreeAlgorithms();
        HELP = DSALExercises.initHelpText();
    }
    
    /**
     * Reads an input from a source file, executes the specified algorithm on this input, and outputs the solution in 
     * LaTeX code to the specified target file(s).
     * @param args The program arguments as flag/value pairs. The following flags are currently implemented:<br>
     *             -s : Source file containing the input. Must not be specified together with -i, but one of them must 
     *                  be specified.<br>
     *             -i : Input directly specified as a String. Must not be specified together with -s, but one of them 
     *                  must be specified.<br>
     *             -t : Target file to store the LaTeX code in. If not specified, the solution is sent to the standard 
     *                  output.<br>
     *             -p : File to store LaTeX code for a pre-print where to solve an exercise. E.g., for sorting this 
     *                  might be the input array followed by a number of empty arrays. If not set, no pre-print will be 
     *                  generated.<br>
     *             -a : The algorithm to apply to the input. Currently, selectionsort, bubblesort, insertionsort, 
     *                  quicksort, mergesort, and heapsort are implemented. Must be specified.<br>
     *             -l : Additional lines for pre-prints. Defaults to 0 if not set. Ignored if -p is not set.<br>
     *             -d : Degree (e.g., of a B-tree).<br>
     *             -o : File containing operations used to construct a start structure.
     */
    @SuppressWarnings({"resource", "unchecked"})
    public static void main(String[] args) {
        if (args == null || args.length < 1) {
            System.out.println("You need to provide arguments! Type -h for help.");
            return;
        }
        if ("-h".equals(args[0])) {
            if (args.length == 1) {
                for (String text : DSALExercises.HELP) {
                    System.out.println(text);
                }
            } else if (args.length > 2) {
                System.out.println("You can only ask for help on one algorithm at a time!");
            } else {
                String input = args[1];
                for (Algorithm alg : Algorithm.values()) {
                    if (alg.name.equals(input)) {
                        for (String text : alg.docu) {
                            System.out.println(text);
                        }
                        break;
                    }
                }
            }
            return;
        }
        if (args.length % 2 != 0) {
            System.out.println("The number of arguments must be even (flag/value pairs)!");
            return;
        }
        Map<Flag, String> options;
        try {
            options = DSALExercises.parseFlags(args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }
        int rows =
            !DSALExercises.STUDENT_MODE && options.containsKey(Flag.LENGTH) ?
                Integer.parseInt(options.get(Flag.LENGTH)) :
                    0;
        try (
            BufferedWriter solutionWriter =
                new BufferedWriter(
                    options.containsKey(Flag.TARGET) ?
                        new FileWriter(options.get(Flag.TARGET)) :
                            new OutputStreamWriter(System.out)
                );
            BufferedWriter exerciseWriter =
                new BufferedWriter(
                    options.containsKey(Flag.EXERCISE) ?
                        new FileWriter(options.get(Flag.EXERCISE)) :
                            new OutputStreamWriter(System.out)
                );    
        ) {
            Object input = DSALExercises.parseInput(options);
            Integer[] array = null;
            Integer m = 0;
            double[] params = null;
            Pair<double[], Integer[]> in = new Pair<double[], Integer[]>(null, null);
            String anchor = null;
            switch (options.get(Flag.ALGORITHM)) {
                case "selectionsort":
                    array = (Integer[])input;
                    if (options.containsKey(Flag.EXERCISE)) {
                        if (DSALExercises.STUDENT_MODE) {
                            exerciseWriter.write(
                                "\\noindent Sortieren Sie das folgende Array mithilfe von Selectionsort."
                            );
                            exerciseWriter.newLine();
                            exerciseWriter.write("Geben Sie dazu das Array nach jeder Swap-Operation an.\\\\[2ex]");
                            exerciseWriter.newLine();
                        }
                        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, exerciseWriter);
                        anchor = ArrayUtils.printArray(array, null, null, null, exerciseWriter);
                    }
                    rows += Sorting.selectionsort(array, solutionWriter);
                    if (options.containsKey(Flag.EXERCISE)) {
                        for (int i = 0; i < rows; i++) {
                            anchor = ArrayUtils.printEmptyArray(array.length, anchor, exerciseWriter);
                        }
                        TikZUtils.printTikzEnd(exerciseWriter);
                    }
                    break;
                case "bubblesort":
                    array = (Integer[])input;
                    if (options.containsKey(Flag.EXERCISE)) {
                        if (DSALExercises.STUDENT_MODE) {
                            exerciseWriter.write("\\noindent Sortieren Sie das folgende Array mithilfe von Bubblesort.");
                            exerciseWriter.newLine();
                            exerciseWriter.write("Geben Sie dazu das Array nach jeder Swap-Operation an.\\\\[2ex]");
                            exerciseWriter.newLine();
                        }
                        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, exerciseWriter);
                        anchor = ArrayUtils.printArray(array, null, null, null, exerciseWriter);
                    }
                    rows += Sorting.bubblesort(array, solutionWriter);
                    if (options.containsKey(Flag.EXERCISE)) {
                        for (int i = 0; i < rows; i++) {
                            anchor = ArrayUtils.printEmptyArray(array.length, anchor, exerciseWriter);
                        }
                        TikZUtils.printTikzEnd(exerciseWriter);
                    }
                    break;
                case "insertionsort":
                    array = (Integer[])input;
                    if (options.containsKey(Flag.EXERCISE)) {
                        if (DSALExercises.STUDENT_MODE) {
                            exerciseWriter.write(
                                "\\noindent Sortieren Sie das folgende Array mithilfe von Insertionsort."
                            );
                            exerciseWriter.newLine();
                            exerciseWriter.write(
                                "Geben Sie dazu das Array nach jeder Iteration der \\\"au\\ss{}eren Schleife "
                                + "an.\\\\[2ex]"
                            );
                            exerciseWriter.newLine();
                        }
                        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, exerciseWriter);
                        anchor = ArrayUtils.printArray(array, null, null, null, exerciseWriter);
                    }
                    rows += Sorting.insertionsort(array, solutionWriter);
                    if (options.containsKey(Flag.EXERCISE)) {
                        for (int i = 0; i < rows; i++) {
                            anchor = ArrayUtils.printEmptyArray(array.length, anchor, exerciseWriter);
                        }
                        TikZUtils.printTikzEnd(exerciseWriter);
                    }
                    break;
                case "quicksort":
                    array = (Integer[])input;
                    if (options.containsKey(Flag.EXERCISE)) {
                        if (DSALExercises.STUDENT_MODE) {
                            exerciseWriter.write("\\noindent Sortieren Sie das folgende Array mithilfe von Quicksort.");
                            exerciseWriter.newLine();
                            exerciseWriter.write(
                                "Geben Sie dazu das Array nach jeder Partition-Operation an und markieren Sie das "
                                + "jeweils verwendete Pivot-Element.\\\\[2ex]"
                            );
                            exerciseWriter.newLine();
                        }
                        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, exerciseWriter);
                        anchor = ArrayUtils.printArray(array, null, null, null, exerciseWriter);
                    }
                    rows += Sorting.quicksort(array, solutionWriter);
                    if (options.containsKey(Flag.EXERCISE)) {
                        for (int i = 0; i < rows; i++) {
                            anchor = ArrayUtils.printEmptyArray(array.length, anchor, exerciseWriter);
                        }
                        TikZUtils.printTikzEnd(exerciseWriter);
                    }
                    break;
                case "mergesort":
                    array = (Integer[])input;
                    if (options.containsKey(Flag.EXERCISE)) {
                        if (DSALExercises.STUDENT_MODE) {
                            exerciseWriter.write("\\noindent Sortieren Sie das folgende Array mithilfe von Mergesort.");
                            exerciseWriter.newLine();
                            exerciseWriter.write("Geben Sie dazu das Array nach jeder Merge-Operation an.\\\\[2ex]");
                            exerciseWriter.newLine();
                        }
                        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, exerciseWriter);
                        anchor = ArrayUtils.printArray(array, null, null, null, exerciseWriter);
                    }
                    rows += Sorting.mergesort(array, false, solutionWriter);
                    if (options.containsKey(Flag.EXERCISE)) {
                        for (int i = 0; i < rows; i++) {
                            anchor = ArrayUtils.printEmptyArray(array.length, anchor, exerciseWriter);
                        }
                        TikZUtils.printTikzEnd(exerciseWriter);
                    }
                    break;
                case "mergesortWithSplitting":
                    array = (Integer[])input;
                    if (options.containsKey(Flag.EXERCISE)) {
                        if (DSALExercises.STUDENT_MODE) {
                            exerciseWriter.write("\\noindent Sortieren Sie das folgende Array mithilfe von Mergesort.");
                            exerciseWriter.newLine();
                            exerciseWriter.write("Geben Sie dazu das Array nach jeder Merge-Operation an.\\\\[2ex]");
                            exerciseWriter.newLine();
                        }
                        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, exerciseWriter);
                        anchor = ArrayUtils.printArray(array, null, null, null, exerciseWriter);
                    }
                    rows += Sorting.mergesort(array, true, solutionWriter);
                    if (options.containsKey(Flag.EXERCISE)) {
                        for (int i = 0; i < rows; i++) {
                            anchor = ArrayUtils.printEmptyArray(array.length, anchor, exerciseWriter);
                        }
                        TikZUtils.printTikzEnd(exerciseWriter);
                    }
                    break;
                case "heapsort":
                    array = (Integer[])input;
                    if (options.containsKey(Flag.EXERCISE)) {
                        if (DSALExercises.STUDENT_MODE) {
                            exerciseWriter.write("\\noindent Sortieren Sie das folgende Array mithilfe von Heapsort.");
                            exerciseWriter.newLine();
                            exerciseWriter.write("Geben Sie dazu das Array nach jeder Swap-Operation an.\\\\[2ex]");
                            exerciseWriter.newLine();
                        }
                        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, exerciseWriter);
                        anchor = ArrayUtils.printArray(array, null, null, null, exerciseWriter);
                    }
                    rows += Sorting.heapsort(array, solutionWriter);
                    if (options.containsKey(Flag.EXERCISE)) {
                        for (int i = 0; i < rows; i++) {
                            anchor = ArrayUtils.printEmptyArray(array.length, anchor, exerciseWriter);
                        }
                        TikZUtils.printTikzEnd(exerciseWriter);
                    }
                    break;
                case "heapsortWithTrees":
                    array = (Integer[])input;
                    if (options.containsKey(Flag.EXERCISE)) {
                        if (DSALExercises.STUDENT_MODE) {
                            exerciseWriter.write("\\noindent Sortieren Sie das folgende Array mithilfe von Heapsort.");
                            exerciseWriter.newLine();
                            exerciseWriter.write("Geben Sie dazu das Array nach jeder Swap-Operation an.\\\\[2ex]");
                            exerciseWriter.newLine();
                        }
                        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, exerciseWriter);
                        anchor = ArrayUtils.printArray(array, null, null, null, exerciseWriter);
                    }
                    rows += Sorting.heapsortWithTrees(array, solutionWriter);
                    if (options.containsKey(Flag.EXERCISE)) {
                        for (int i = 0; i < rows; i++) {
                            anchor = ArrayUtils.printEmptyArray(array.length, anchor, exerciseWriter);
                        }
                        TikZUtils.printTikzEnd(exerciseWriter);
                    }
                    break;
                case "btree":
                    IntBTree.btree(
                        new IntBTree(
                            options.containsKey(Flag.DEGREE) ? Integer.parseInt(options.get(Flag.DEGREE)) : 2
                        ),
                        (Deque<Pair<Integer,Boolean>>)input,
                        DSALExercises.parseOperations(options),
                        solutionWriter,
                        options.containsKey(Flag.EXERCISE) ? exerciseWriter : null
                    );
                    break;
                case "rbtree":
                    IntRBTree.rbtree(
                        new IntRBTree(),
                        (Deque<Pair<Integer,Boolean>>)input,
                        DSALExercises.parseOperations(options),
                        solutionWriter,
                        options.containsKey(Flag.EXERCISE) ? exerciseWriter : null
                    );
                    break;
                case "avltree":
                    IntAVLTree.avltree(
                        new IntAVLTree(),
                        (Deque<Pair<Integer,Boolean>>)input,
                        DSALExercises.parseOperations(options),
                        solutionWriter,
                        options.containsKey(Flag.EXERCISE) ? exerciseWriter : null
                    );
                    break;
                case "hashDivision":
                    in = (Pair<double[], Integer[]>)input;
                    array = in.y;
                    m = (int)in.x[0];
                        exerciseWriter.write(
                            "\\noindent F\\\"ugen Sie die folgenden Werte in das unten stehende Array der L\\\"ange "
                            + m
                            + " unter Verwendung der Divisionsmethode ohne Sondierung (also durch Verkettung) "
                            + "ein:\\\\[2ex]"
                        );
                        exerciseWriter.newLine();
                        for (int i = 0; i < array.length - 1; ++i) {
                            exerciseWriter.write(array[i] + ", ");
                        }
                        exerciseWriter.write(array[array.length-1] + ".");
                        exerciseWriter.write("\\\\[2ex]");
                        exerciseWriter.newLine();
                    params = new double[2];
                    params[0] = 1;
                    params[1] = 0;
                    Hashing.hashing(array, m, params, !DSALExercises.STUDENT_MODE, solutionWriter, exerciseWriter);
                    break;
                case "hashDivisionLinear":
                    in = (Pair<double[], Integer[]>)input;
                    array = in.y;
                    m = (int)in.x[0];
                        exerciseWriter.write(
                            "\\noindent F\\\"ugen Sie die folgenden Werte in das unten stehende Array der L\\\"ange "
                            + m
                            + " unter Verwendung der Divisionsmethode mit linearer Sondierung ein:\\\\[2ex]"
                        );
                        exerciseWriter.newLine();
                        for (int i = 0; i < array.length - 1; ++i) {
                            exerciseWriter.write(array[i] + ", ");
                        }
                        exerciseWriter.write(array[array.length-1] + ".");
                        exerciseWriter.write("\\\\[2ex]");
                        exerciseWriter.newLine();
                    params = new double[2];
                    params[0] = 1;
                    params[1] = 1;
                    Hashing.hashing(array, m, params, !DSALExercises.STUDENT_MODE, solutionWriter, exerciseWriter);
                    break;
                case "hashDivisionQuadratic":
                    in = (Pair<double[], Integer[]>)input;
                    array = in.y;
                    m = (int)in.x[0];
                        exerciseWriter.write(
                            "\\noindent F\\\"ugen Sie die folgenden Werte in das unten stehende Array der L\\\"ange "
                            + m
                            + " unter Verwendung der Divisionsmethode mit quadratischer Sondierung ($c_1$= "
                            + in.x[1]
                            + ", $c_2$= "
                            + in.x[2]
                            + " ) ein:\\\\[2ex]"
                        );
                        exerciseWriter.newLine();
                        for (int i = 0; i < array.length - 1; ++i) {
                            exerciseWriter.write(array[i] + ", ");
                        }
                        exerciseWriter.write(array[array.length-1] + ".");
                        exerciseWriter.write("\\\\[2ex]");
                        exerciseWriter.newLine();
                    params = new double[4];
                    params[0] = 1;
                    params[1] = 2;
                    params[2] = in.x[1];
                    params[3] = in.x[2];
                    Hashing.hashing(array, m, params, !DSALExercises.STUDENT_MODE, solutionWriter, exerciseWriter);
                    break;
                case "hashMultiplication":
                    in = (Pair<double[], Integer[]>)input;
                    array = in.y;
                    m = (int)in.x[0];
                        exerciseWriter.write(
                            "\\noindent F\\\"ugen Sie die folgenden Werte in das unten stehende Array der L\\\"ange "
                            + m
                            + " unter Verwendung der Multiplikationsmethode (c = "
                            + in.x[1]
                            + ") ohne Sondierung (also durch Verkettung) ein:\\\\[2ex]"
                        );
                        exerciseWriter.newLine();
                        for (int i = 0; i < array.length - 1; ++i) {
                            exerciseWriter.write(array[i] + ", ");
                        }
                        exerciseWriter.write(array[array.length-1] + ".");
                        exerciseWriter.write("\\\\[2ex]");
                        exerciseWriter.newLine();
                    params = new double[3];
                    params[0] = 2;
                    params[1] = 0;
                    params[2] = in.x[1];
                    Hashing.hashing(array, m, params, !DSALExercises.STUDENT_MODE, solutionWriter, exerciseWriter);
                    break;
                case "hashMultiplicationLinear":
                    in = (Pair<double[], Integer[]>)input;
                    array = in.y;
                    m = (int)in.x[0];
                        exerciseWriter.write(
                            "\\noindent F\\\"ugen Sie die folgenden Werte in das unten stehende Array der L\\\"ange "
                            + m
                            + " unter Verwendung der Multiplikationsmethode (c = "
                            + in.x[1]
                            + ") mit linearer Sondierung ein:\\\\[2ex]"
                        );
                        exerciseWriter.newLine();
                        for (int i = 0; i < array.length - 1; ++i) {
                            exerciseWriter.write(array[i] + ", ");
                        }
                        exerciseWriter.write(array[array.length-1] + ".");
                        exerciseWriter.write("\\\\[2ex]");
                        exerciseWriter.newLine();
                    params = new double[3];
                    params[0] = 2;
                    params[1] = 1;
                    params[2] = in.x[1];
                    Hashing.hashing(array, m, params, !DSALExercises.STUDENT_MODE, solutionWriter, exerciseWriter);
                    break;
                case "hashMultiplicationQuadratic":
                    in = (Pair<double[], Integer[]>)input;
                    array = in.y;
                    m = (int)in.x[0];
                        exerciseWriter.write(
                            "\\noindent F\\\"ugen Sie die folgenden Werte in das unten stehende Array der L\\\"ange "
                            + m
                            + " unter Verwendung der Multiplikationsmethode (c = "
                            + in.x[1]
                            + ") mit quadratischer Sondierung ($c_1$= "
                            + in.x[2]
                            + ", $c_2$= "
                            + in.x[3]
                            + " ) ein:\\\\[2ex]"
                        );
                        exerciseWriter.newLine();
                        for (int i = 0; i < array.length - 1; ++i) {
                            exerciseWriter.write(array[i] + ", ");
                        }
                        exerciseWriter.write(array[array.length-1] + ".");
                        exerciseWriter.write("\\\\[2ex]");
                        exerciseWriter.newLine();
                    params = new double[5];
                    params[0] = 2;
                    params[1] = 2;
                    params[2] = in.x[1];
                    params[3] = in.x[2];
                    params[4] = in.x[3];
                    Hashing.hashing(array, m, params, !DSALExercises.STUDENT_MODE, solutionWriter, exerciseWriter);
                    break;
                default:
                    System.out.println("Unknown algorithm!");
                    return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return The names of the supported algorithms, separated by commas.
     */
    private static String algorithmNames() {
        StringBuilder res = new StringBuilder();
        boolean first = true;
        for (Algorithm alg : Algorithm.values()) {
            if (DSALExercises.STUDENT_MODE && !alg.enabled) {
                continue;
            }
            if (first) {
                first = false;
            } else {
                res.append(", ");
            }
            res.append(alg.name);
        }
        return res.toString();
    }

    /**
     * @param a Some number.
     * @param b Another number.
     * @param c Yet another number.
     * @return True if the three numbers are coprime to each other. False otherwise.
     */
    private static boolean areCoprime(int a, int b, int c) {
        return DSALExercises.gcd(a,b) == 1 && DSALExercises.gcd(b,c) == 1 && DSALExercises.gcd(a,c) == 1;
    }

    /**
     * Computes the gcd of two numbers by using the Eucilidian algorithm.
     * @param number1 The first of the two numbers.
     * @param number2 The second of the two numbers.
     * @return The greates common divisor of number1 and number2.
     */
    private static int gcd(int number1, int number2) {
        //base case
        if(number2 == 0){
            return number1;
        }
        return DSALExercises.gcd(number2, number1%number2);
    }

    /**
     * @return The set of (in student mode only enabled) hashing algorithms.
     */
    private static Set<String> initHashingAlgorithms() {
        Set<String> res = new LinkedHashSet<String>();
        if (!DSALExercises.STUDENT_MODE || Algorithm.HASH_DIV.enabled) {
            res.add(Algorithm.HASH_DIV.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.HASH_DIV_LIN.enabled) {
            res.add(Algorithm.HASH_DIV_LIN.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.HASH_DIV_QUAD.enabled) {
            res.add(Algorithm.HASH_DIV_QUAD.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.HASH_MULT.enabled) {
            res.add(Algorithm.HASH_MULT.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.HASH_MULT_LIN.enabled) {
            res.add(Algorithm.HASH_MULT_LIN.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.HASH_MULT_QUAD.enabled) {
            res.add(Algorithm.HASH_MULT_QUAD.name);
        }
        return res;
    }

    /**
     * @return The general help text as String array.
     */
    private static String[] initHelpText() {
        List<String> text = new ArrayList<String>();
        text.add(
            "This is ExerciseCreator version "
            + DSALExercises.VERSION
            + (DSALExercises.STUDENT_MODE ? " (student)." : ".")
        );
        text.add(
            "You can create exercises and solutions automatically using the following flags, where each flag needs to "
            + "be followed by exactly one argument:"
        );
        for (Flag flag : Flag.values()) {
            if (!DSALExercises.STUDENT_MODE || flag.forStudents) {
                text.add("");
                text.add(flag.shortName);
                text.add(flag.docu);
            }
        }
        String[] res = new String[text.size()];
        res = text.toArray(res);
        return res;
    }
    
    /**
     * @return The set of (in student mode only enabled) sorting algorithms.
     */
    private static Set<String> initSortingAlgorithms() {
        Set<String> res = new LinkedHashSet<String>();
        if (!DSALExercises.STUDENT_MODE || Algorithm.BUBBLESORT.enabled) {
            res.add(Algorithm.BUBBLESORT.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.HEAPSORT.enabled) {
            res.add(Algorithm.HEAPSORT.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.HEAPSORT_TREE.enabled) {
            res.add(Algorithm.HEAPSORT_TREE.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.INSERTIONSORT.enabled) {
            res.add(Algorithm.INSERTIONSORT.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.MERGESORT.enabled) {
            res.add(Algorithm.MERGESORT.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.MERGESORT_SPLIT.enabled) {
            res.add(Algorithm.MERGESORT_SPLIT.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.QUICKSORT.enabled) {
            res.add(Algorithm.QUICKSORT.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.SELECTIONSORT.enabled) {
            res.add(Algorithm.SELECTIONSORT.name);
        }
        return res;
    }

    /**
     * @return The set of (in student mode only enabled) tree algorithms.
     */
    private static Set<String> initTreeAlgorithms() {
        Set<String> res = new LinkedHashSet<String>();
        if (!DSALExercises.STUDENT_MODE || Algorithm.BTREE.enabled) {
            res.add(Algorithm.BTREE.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.RBTREE.enabled) {
            res.add(Algorithm.RBTREE.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.AVLTREE.enabled) {
            res.add(Algorithm.AVLTREE.name);
        }
        return res;
    }

    /**
     * @param args The program arguments.
     * @return A map from Flags to their values parsed from the program arguments.
     * @throws Exception If the program arguments are not of the desired form.
     */
    private static Map<Flag, String> parseFlags(String[] args) throws Exception {
        Map<Flag, String> res = new LinkedHashMap<Flag, String>();
        outer: for (int i = 0; i < args.length - 1; i += 2) {
            String option = args[i];
            for (Flag flag : Flag.values()) {
                if (DSALExercises.STUDENT_MODE) {
                    if (!flag.forStudents) {
                        continue;
                    }
                }
                if (!flag.shortName.equals(option)) {
                    continue;
                }
                if (res.containsKey(flag)) {
                    throw new Exception(flag.longName + " flag must not be specified more than once!");
                }
                switch (flag) {
                    case SOURCE:
                        if (res.containsKey(Flag.INPUT)) {
                            throw new Exception("Input must not be specified by a file and a string together!");
                        }
                        break;
                    case INPUT:
                        if (res.containsKey(Flag.SOURCE)) {
                            throw new Exception("Input must not be specified by a file and a string together!");
                        }
                        break;
                    default:
                        // do nothing
                }
                res.put(flag, args[i + 1]);
                continue outer;
            }
            throw new Exception("Unknown option specified (" + option + ")!");
        }
        if (!res.containsKey(Flag.ALGORITHM)) {
            throw new Exception("No algorithm specified!");
        }
        if (!DSALExercises.STUDENT_MODE && !res.containsKey(Flag.SOURCE) && !res.containsKey(Flag.INPUT)) {
            throw new Exception("No input specified!");
        }
        return res;
    }

    /**
     * @param options The option flags.
     * @return The input specified by the options.
     */
    private static Object parseInput(Map<Flag, String> options) {
        String[] nums = null;
        String alg = options.get(Flag.ALGORITHM);
        if (DSALExercises.SORTING_ALGORITHMS.contains(alg)) {
            if (options.containsKey(Flag.SOURCE)) {
                try (BufferedReader reader = new BufferedReader(new FileReader(options.get(Flag.SOURCE)))) {
                    nums = reader.readLine().split(",");
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } else if (DSALExercises.STUDENT_MODE) {
                final int length;
                Random gen = new Random();
                if (options.containsKey(Flag.LENGTH)) {
                    length = Integer.parseInt(options.get(Flag.LENGTH));
                } else {
                    length = gen.nextInt(16) + 5;
                }
                Integer[] array = new Integer[length];
                for (int i = 0; i < array.length; i++) {
                    array[i] = gen.nextInt(DSALExercises.NUMBER_LIMIT);
                }
                return array;
            } else {
                nums = options.get(Flag.INPUT).split(",");
            }
            Integer[] array = new Integer[nums.length];
            for (int i = 0; i < array.length; i++) {
                array[i] = Integer.parseInt(nums[i].trim());
            }
            return array;
        } else if (DSALExercises.HASHING_ALGORITHMS.contains(alg)) {
            Pair<double[], Integer[]> input = new Pair<double[], Integer[]>(null,null);
            String[] paramString = null;
            Integer[] array;
            if (options.containsKey(Flag.SOURCE)) {
                try (BufferedReader reader = new BufferedReader(new FileReader(options.get(Flag.SOURCE)))) {
                    paramString = reader.readLine().split(",");
                    nums = reader.readLine().split(",");
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } else if (DSALExercises.STUDENT_MODE) {
                final int length;
                Random gen = new Random();
                if (options.containsKey(Flag.LENGTH)) {
                    length = Integer.parseInt(options.get(Flag.LENGTH));
                    System.out.println("Lines set to: " + length);
                } else {
                    length = gen.nextInt(16) + 5;
                    System.out.println("Lines chosen to: " + length);
                }
                array = new Integer[length];
                for (int i = 0; i < array.length; i++) {
                    array[i] = gen.nextInt(DSALExercises.NUMBER_LIMIT);
                }
                double[] params = new double[4]; // create all possible constants per default.
                int m = 0;
				if(alg == "hashDivision" || alg == "hashMultiplication")
				{
					m = gen.nextInt(DSALExercises.NUMBER_LIMIT);
				}
				else
				{
					m = length;
				}
                int c1 = gen.nextInt(DSALExercises.NUMBER_LIMIT);
                int c2 = gen.nextInt(DSALExercises.NUMBER_LIMIT);
				if(alg == "hashDivisionQuadratic" || alg == "hashMultiplicationQuadratic")
				{
					while (!DSALExercises.areCoprime(m,c1,c2)) {
						c1 = gen.nextInt(DSALExercises.NUMBER_LIMIT);
						if (DSALExercises.areCoprime(m,c1,c2)) {
							break;
						}
						c2 = gen.nextInt(DSALExercises.NUMBER_LIMIT);
						if (DSALExercises.areCoprime(m,c1,c2)) {
							break;
						}
					}
				}
                double c = gen.nextDouble();
                params[0] = m;
                params[1] = c;
                params[2] = c1;
                params[3] = c2;
                input = new Pair<double[], Integer[]>(params,array);
                return input;
            } else {
                nums = options.get(Flag.INPUT).split(",");
                paramString = options.get(Flag.DEGREE).split(",");
            }
            array = new Integer[nums.length];
            for (int i = 0; i < array.length; i++) {
                array[i] = Integer.parseInt(nums[i].trim());
            }
            double[] params = new double[paramString.length];
            for (int i = 0; i < params.length; ++i) {
                params[i] = Double.parseDouble(paramString[i].trim());
            }
            input = new Pair<double[], Integer[]>(params, array);
            return input;
        } else if (DSALExercises.TREE_ALGORITHMS.contains(alg)) {
            if (options.containsKey(Flag.SOURCE)) {
                try (BufferedReader reader = new BufferedReader(new FileReader(options.get(Flag.SOURCE)))) {
                    nums = reader.readLine().split(",");
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } else if (DSALExercises.STUDENT_MODE) {
                final int length;
                Random gen = new Random();
                if (options.containsKey(Flag.LENGTH)) {
                    length = Integer.parseInt(options.get(Flag.LENGTH));
                } else {
                    length = gen.nextInt(16) + 5;
                }
                Deque<Pair<Integer, Boolean>> deque = new ArrayDeque<Pair<Integer, Boolean>>();
                List<Integer> in = new ArrayList<Integer>();
                for (int i = 0; i < length; i++) {
                    if (in.isEmpty() || gen.nextInt(3) > 0) {
                        int next = gen.nextInt(DSALExercises.NUMBER_LIMIT);
                        deque.offer(new Pair<Integer, Boolean>(next, true));
                        in.add(next);
                    } else {
                        deque.offer(new Pair<Integer, Boolean>(in.remove(gen.nextInt(in.size())), false));
                    }
                }
                return deque;
            } else {
                nums = options.get(Flag.INPUT).split(",");
            }
            Deque<Pair<Integer, Boolean>> deque = new ArrayDeque<Pair<Integer, Boolean>>();
            for (String num : nums) {
                String trimmed = num.trim();
                if (trimmed.startsWith("~")) {
                    deque.offer(new Pair<Integer, Boolean>(Integer.parseInt(trimmed.substring(1)), false));
                } else {
                    deque.offer(new Pair<Integer, Boolean>(Integer.parseInt(trimmed), true));
                }
            }
            return deque;
        } else {
            return null;
        }
    }

    /**
     * @param options The program arguments.
     * @return The operations specified in the operation file or null if no such file is specified in the program 
     *         arguments.
     */
    private static Deque<Pair<Integer, Boolean>> parseOperations(Map<Flag, String> options) {
        if (!options.containsKey(Flag.OPERATIONS)) {
            if (DSALExercises.STUDENT_MODE) {
                Random gen = new Random();
                final int length = gen.nextInt(20);
                Deque<Pair<Integer, Boolean>> deque = new ArrayDeque<Pair<Integer, Boolean>>();
                List<Integer> in = new ArrayList<Integer>();
                for (int i = 0; i < length; i++) {
                    if (in.isEmpty() || gen.nextInt(3) > 0) {
                        int next = gen.nextInt(DSALExercises.NUMBER_LIMIT);
                        deque.offer(new Pair<Integer, Boolean>(next, true));
                        in.add(next);
                    } else {
                        deque.offer(new Pair<Integer, Boolean>(in.remove(gen.nextInt(in.size())), false));
                    }
                }
                return deque;
            }
            return new ArrayDeque<Pair<Integer, Boolean>>();
        }
        String[] nums = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(options.get(Flag.OPERATIONS)))) {
            nums = reader.readLine().split(",");
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayDeque<Pair<Integer, Boolean>>();
        }
        Deque<Pair<Integer, Boolean>> deque = new ArrayDeque<Pair<Integer, Boolean>>();
        for (String num : nums) {
            String trimmed = num.trim();
            if (trimmed.startsWith("~")) {
                deque.offer(new Pair<Integer, Boolean>(Integer.parseInt(trimmed.substring(1)), false));
            } else {
                deque.offer(new Pair<Integer, Boolean>(Integer.parseInt(trimmed), true));
            }
        }
        return deque;
    }

    /**
     * Algorithms supported by the current version. Can be used to switch on/off certain algorithms.
     * @author cryingshadow
     * @version $Id$
     */
    private static enum Algorithm {

        /**
         * Insertion and deletion in AVL-trees with int values.
         */
        AVLTREE("avltree", new String[]{"TODO"}, true),

        /**
         * Insertion and deletion in B-trees with int values.
         */
        BTREE(
            "btree",
            new String[]{
                "Insertion and deletion of keys in a B-tree. The flag -d can be used to set the degree of the B-tree "
                + "(an integer greater than 1, if not specified, the degree defaults to 2).",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies how many operations should be performed on the B-tree." :
                            "TODO"
                )
            },
            true
        ),

        /**
         * Bubblesort on Integer arrays.
         */
        BUBBLESORT(
            "bubblesort",
            new String[]{
                "Perform Bubblesort on an array of integers.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies the length of the array to sort." :
                            "TODO"
                )
            },
            true
        ),

        /**
         * Linked hashing on Integer arrays with the division method.
         */
        HASH_DIV(
            "hashDivision",
            new String[]{
                "Use the division method in combination with linking for hashing into integer arrays.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies how many elements will be added to the hash table." :
                            "Parameters are: m (size of the hashmap)"
                )
            },
            true
        ),

        /**
         * Hashing on Integer arrays with the division method and linear probing.
         */
        HASH_DIV_LIN(
            "hashDivisionLinear",
            new String[]{
                "Use the division method in combination with linear probing for hashing into integer arrays.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies how many elements will be added to the hash table." :
                            "Parameters are: m (size of the hashmap)"
                )
            },
            true
        ),

        /**
         * Hashing on Integer arrays with the division method and quadratic probing.
         */
        HASH_DIV_QUAD(
            "hashDivisionQuadratic",
            new String[]{
                "Use the division method in combination with quadratic probing for hashing into integer arrays.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies how many elements will be added to the hash table." :
                            "Parameters are: m (size of the hashmap), c1 and c2 (constants for quadratic probing)"
                )
            },
            true
        ),

        /**
         * Linked hashing on Integer arrays with the multiplication method.
         */
        HASH_MULT(
            "hashMultiplication",
            new String[]{
                "Use the multiplication method in combination with linking for hashing into integer arrays.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies how many elements will be added to the hash table." :
                            "Parameters are: m (size of the hashmap), c (constant between 0 and 1 for the "
                            + "multiplication method)"
                )
            },
            true
        ),

        /**
         * Hashing on Integer arrays with the multiplication method and linear probing.
         */
        HASH_MULT_LIN(
            "hashMultiplicationLinear",
            new String[]{
                "Use the multiplication method in combination with linear probing for hashing into integer arrays.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies how many elements will be added to the hash table." :
                            "Parameters are: m (size of the hashmap), c (constant between 0 and 1 for the "
                            + "multiplication method)"
                )
            },
            true
        ),

        /**
         * Hashing on Integer arrays with the multiplication method and quadratic probing.
         */
        HASH_MULT_QUAD(
            "hashMultiplicationQuadratic",
            new String[]{
                "Use the multiplication method in combination with quadratic probing for hashing into integer arrays.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies how many elements will be added to the hash table." :
                            "Parameters are: m (size of the hashmap), c (constant between 0 and 1 for the "
                            + "multiplication method), c1 and c2 (constants for quadratic probing)"
                )
            },
            true
        ),

        /**
         * Heapsort on Integer arrays.
         */
        HEAPSORT(
            "heapsort",
            new String[]{
                "Perform Heapsort on an array of integers.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies the length of the array to sort." :
                            "TODO"
                )
            },
            true
        ),

        /**
         * Heapsort on Integer arrays where the tree interpretation of the current array is explicitly displayed.
         */
        HEAPSORT_TREE(
            "heapsortWithTrees",
            new String[]{
                "Perform Heapsort on an array of integers. Additionally output the heap interpretation of each array "
                + "in the solution as trees.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies the length of the array to sort." :
                            "TODO"
                )
            },
            true
        ),

        /**
         * Insertionsort on Integer arrays.
         */
        INSERTIONSORT(
            "insertionsort",
            new String[]{
                "Perform Insertionsort on an array of integers.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies the length of the array to sort." :
                            "TODO"
                )
            },
            true
        ),

        /**
         * Mergesort on Integer arrays.
         */
        MERGESORT(
            "mergesort",
            new String[]{
                "Perform Mergesort on an array of integers.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies the length of the array to sort." :
                            "TODO"
                )
            },
            true
        ),

        /**
         * Mergesort on Integer arrays where splitting is explicitly displayed.
         */
        MERGESORT_SPLIT(
            "mergesortWithSplitting",
            new String[]{
                "Perform Mergesort on an array of integers. Additionally output the split operations in the solution, "
                + "although they do not change the array content.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies the length of the array to sort." :
                            "TODO"
                )
            },
            true
        ),

        /**
         * Quicksort on Integer arrays.
         */
        QUICKSORT(
            "quicksort",
            new String[]{
                "Perform Quicksort on an array of integers.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies the length of the array to sort." :
                            "TODO"
                )
            },
            true
        ),

        /**
         * Insertion and deletion in Red-Black-trees with int values.
         */
        RBTREE("rbtree", new String[]{"TODO"}, true),

        /**
         * Selectionsort on Integer arrays.
         */
        SELECTIONSORT(
            "selectionsort",
            new String[]{
                "Perform Selectionsort on an array of integers.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies the length of the array to sort." :
                            "TODO"
                )
            },
            true
        );

        /**
         * The documentation for this algorithm.
         */
        private String[] docu;

        /**
         * Flag indicating whether the algorithm is enabled in student mode.
         */
        private boolean enabled;

        /**
         * The name of the algorithm.
         */
        private String name;

        /**
         * @param nameParam The name of the algorithm.
         * @param docuParam The documentation for this algorithm.
         * @param enabledParam Flag indicating whether the algorithm is enabled in student mode.
         */
        private Algorithm(String nameParam, String[] docuParam, boolean enabledParam) {
            this.name = nameParam;
            this.docu = docuParam;
            this.enabled = enabledParam;
        }

    }

    /**
     * Option flags for the program arguments.
     * @author cryingshadow
     * @version $Id$
     */
    private static enum Flag {

        /**
         * The algorithm to apply to the input. Must be specified.
         */
        ALGORITHM(
            "-a",
            "Algorithm",
            "The algorithm. Currently, the supported algorithms are: "
            + DSALExercises.algorithmNames()
            + ". To see detailed help for one of the supported algorithms, call this program with \"-h <alg>\" where "
            + "<alg> is the name of the algorithm.",
            true
        ),

        /**
         * Degree (e.g., of a B-tree).
         */
        DEGREE("-d", "Degree", "Used to specify the degree, e.g., of a B-tree. Not relevant for all algorithms.", true),

        /**
         * File to store LaTeX code for the exercise. E.g., for sorting this might be the 
         * input array followed by a number of empty arrays. If not set, no exercise will be generated.
         */
        EXERCISE("-e", "Exercise file", "Path to the file where to store the exercise text in LaTeX code.", true),

        /**
         * Input directly specified as a String. Must not be specified together with -s, but one of them must be 
         * specified.
         */
        INPUT("-i", "Input", "TODO", false),

        /**
         * Length used for several purposes. Its use depends on the algorithm.
         */
        LENGTH(
            "-l",
            "Length",
            "Used to specify a length, e.g., of an array. Its use depends on the chosen algorithm.",
            true
        ),

        /**
         * File containing operations used to construct a start structure.
         */
        OPERATIONS("-o", "Operations for start structure", "TODO", false),

        /**
         * Source file containing the input. Must not be specified together with -i, but one of them must be specified.
         */
        SOURCE("-s", "Source file", "TODO", false),

        /**
         * Target file to store the LaTeX code in. If not specified, the solution is sent to the standard output.
         */
        TARGET("-t", "Target file", "Path to the file where to store the solution text in LaTeX code.", true);

        /**
         * The docu for this flag.
         */
        private final String docu;

        /**
         * Flag indicating whether this option is available for students.
         */
        private final boolean forStudents;

        /**
         * The name of the option flag in readable form.
         */
        private final String longName;

        /**
         * The name of the option flag as to be given by the user.
         */
        private final String shortName;

        /**
         * Creates an option flag with the specified short and long names.
         * @param s The short name.
         * @param l The long name.
         * @param d The documentation.
         * @param a Flag indicating whether this option is available for students.
         */
        private Flag(String s, String l, String d, boolean a) {
            this.shortName = s;
            this.longName = l;
            this.docu = d;
            this.forStudents = a;
        }

    }

}
