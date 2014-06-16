import java.io.*;
import java.util.*;

/**
 * Programm for creating solutions for DSAL exercises.
 * @author cryingshadow
 * @version $Id$
 */
public class DSALExercises {

    /**
     * Limit for random numbers in student mode.
     */
    private static final int NUMBER_LIMIT = 100;

    /**
     * Flag used to turn off some options for the student version.
     */
    private static final boolean STUDENT_MODE = false;

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
        if (args == null || args.length < 4) {
            System.out.println("You need to provide at least an input and an algorithm!");
            return;
        }
        if (args.length % 2 != 0) {
            System.out.println("The number of arguments must be even (flag/value pairs)!");
            return;
        }
        Map<Flag, String> options = new LinkedHashMap<Flag, String>();
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
                if (options.containsKey(flag)) {
                    System.out.println(flag.longName + " flag must not be specified more than once!");
                    return;
                }
                switch (flag) {
                    case SOURCE:
                        if (options.containsKey(Flag.INPUT)) {
                            System.out.println("Input must not be specified by a file and a string together!");
                            return;
                        }
                        break;
                    case INPUT:
                        if (options.containsKey(Flag.SOURCE)) {
                            System.out.println("Input must not be specified by a file and a string together!");
                            return;
                        }
                        break;
                    default:
                        // do nothing
                }
                options.put(flag, args[i + 1]);
                continue outer;
            }
            System.out.println("Unknown option specified (" + option + ")!");
            return;
        }
        if (!options.containsKey(Flag.ALGORITHM)) {
            System.out.println("No algorithm specified!");
            return;
        }
        if (!DSALExercises.STUDENT_MODE && !options.containsKey(Flag.SOURCE) && !options.containsKey(Flag.INPUT)) {
            System.out.println("No input specified!");
            return;
        }
        int rows =
            !DSALExercises.STUDENT_MODE && options.containsKey(Flag.LINES) ?
                Integer.parseInt(options.get(Flag.LINES)) :
                    0;
        try (
            BufferedWriter writer =
                new BufferedWriter(
                    options.containsKey(Flag.TARGET) ?
                        new FileWriter(options.get(Flag.TARGET)) :
                            new OutputStreamWriter(System.out)
                );
            BufferedWriter writerSpace =
                new BufferedWriter(
                    options.containsKey(Flag.PREPRINT) ?
                        new FileWriter(options.get(Flag.PREPRINT)) :
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
                    if (options.containsKey(Flag.PREPRINT)) {
                        if (DSALExercises.STUDENT_MODE) {
                            writerSpace.write(
                                "\\noindent Sortieren Sie das folgende Array mithilfe von Selectionsort."
                            );
                            writerSpace.newLine();
                            writerSpace.write("Geben Sie dazu das Array nach jeder Swap-Operation an.\\\\[2ex]");
                            writerSpace.newLine();
                        }
                        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writerSpace);
                        anchor = ArrayUtils.printArray(array, null, null, null, writerSpace);
                    }
                    rows += Sorting.selectionsort(array, writer);
                    if (options.containsKey(Flag.PREPRINT)) {
                        for (int i = 0; i < rows; i++) {
                            anchor = ArrayUtils.printEmptyArray(array.length, anchor, writerSpace);
                        }
                        TikZUtils.printTikzEnd(writerSpace);
                    }
                    break;
                case "bubblesort":
                    array = (Integer[])input;
                    if (options.containsKey(Flag.PREPRINT)) {
                        if (DSALExercises.STUDENT_MODE) {
                            writerSpace.write("\\noindent Sortieren Sie das folgende Array mithilfe von Bubblesort.");
                            writerSpace.newLine();
                            writerSpace.write("Geben Sie dazu das Array nach jeder Swap-Operation an.\\\\[2ex]");
                            writerSpace.newLine();
                        }
                        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writerSpace);
                        anchor = ArrayUtils.printArray(array, null, null, null, writerSpace);
                    }
                    rows += Sorting.bubblesort(array, writer);
                    if (options.containsKey(Flag.PREPRINT)) {
                        for (int i = 0; i < rows; i++) {
                            anchor = ArrayUtils.printEmptyArray(array.length, anchor, writerSpace);
                        }
                        TikZUtils.printTikzEnd(writerSpace);
                    }
                    break;
                case "insertionsort":
                    array = (Integer[])input;
                    if (options.containsKey(Flag.PREPRINT)) {
                        if (DSALExercises.STUDENT_MODE) {
                            writerSpace.write(
                                "\\noindent Sortieren Sie das folgende Array mithilfe von Insertionsort."
                            );
                            writerSpace.newLine();
                            writerSpace.write(
                                "Geben Sie dazu das Array nach jeder Iteration der \\\"au\\ss{}eren Schleife "
                                + "an.\\\\[2ex]"
                            );
                            writerSpace.newLine();
                        }
                        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writerSpace);
                        anchor = ArrayUtils.printArray(array, null, null, null, writerSpace);
                    }
                    rows += Sorting.insertionsort(array, writer);
                    if (options.containsKey(Flag.PREPRINT)) {
                        for (int i = 0; i < rows; i++) {
                            anchor = ArrayUtils.printEmptyArray(array.length, anchor, writerSpace);
                        }
                        TikZUtils.printTikzEnd(writerSpace);
                    }
                    break;
                case "quicksort":
                    array = (Integer[])input;
                    if (options.containsKey(Flag.PREPRINT)) {
                        if (DSALExercises.STUDENT_MODE) {
                            writerSpace.write("\\noindent Sortieren Sie das folgende Array mithilfe von Quicksort.");
                            writerSpace.newLine();
                            writerSpace.write(
                                "Geben Sie dazu das Array nach jeder Partition-Operation an und markieren Sie das "
                                + "jeweils verwendete Pivot-Element.\\\\[2ex]"
                            );
                            writerSpace.newLine();
                        }
                        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writerSpace);
                        anchor = ArrayUtils.printArray(array, null, null, null, writerSpace);
                    }
                    rows += Sorting.quicksort(array, writer);
                    if (options.containsKey(Flag.PREPRINT)) {
                        for (int i = 0; i < rows; i++) {
                            anchor = ArrayUtils.printEmptyArray(array.length, anchor, writerSpace);
                        }
                        TikZUtils.printTikzEnd(writerSpace);
                    }
                    break;
                case "mergesort":
                    array = (Integer[])input;
                    if (options.containsKey(Flag.PREPRINT)) {
                        if (DSALExercises.STUDENT_MODE) {
                            writerSpace.write("\\noindent Sortieren Sie das folgende Array mithilfe von Mergesort.");
                            writerSpace.newLine();
                            writerSpace.write("Geben Sie dazu das Array nach jeder Merge-Operation an.\\\\[2ex]");
                            writerSpace.newLine();
                        }
                        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writerSpace);
                        anchor = ArrayUtils.printArray(array, null, null, null, writerSpace);
                    }
                    rows += Sorting.mergesort(array, false, writer);
                    if (options.containsKey(Flag.PREPRINT)) {
                        for (int i = 0; i < rows; i++) {
                            anchor = ArrayUtils.printEmptyArray(array.length, anchor, writerSpace);
                        }
                        TikZUtils.printTikzEnd(writerSpace);
                    }
                    break;
                case "mergesortWithSplitting":
                    array = (Integer[])input;
                    if (options.containsKey(Flag.PREPRINT)) {
                        if (DSALExercises.STUDENT_MODE) {
                            writerSpace.write("\\noindent Sortieren Sie das folgende Array mithilfe von Mergesort.");
                            writerSpace.newLine();
                            writerSpace.write("Geben Sie dazu das Array nach jeder Merge-Operation an.\\\\[2ex]");
                            writerSpace.newLine();
                        }
                        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writerSpace);
                        anchor = ArrayUtils.printArray(array, null, null, null, writerSpace);
                    }
                    rows += Sorting.mergesort(array, true, writer);
                    if (options.containsKey(Flag.PREPRINT)) {
                        for (int i = 0; i < rows; i++) {
                            anchor = ArrayUtils.printEmptyArray(array.length, anchor, writerSpace);
                        }
                        TikZUtils.printTikzEnd(writerSpace);
                    }
                    break;
                case "heapsort":
                    array = (Integer[])input;
                    if (options.containsKey(Flag.PREPRINT)) {
                        if (DSALExercises.STUDENT_MODE) {
                            writerSpace.write("\\noindent Sortieren Sie das folgende Array mithilfe von Heapsort.");
                            writerSpace.newLine();
                            writerSpace.write("Geben Sie dazu das Array nach jeder Swap-Operation an.\\\\[2ex]");
                            writerSpace.newLine();
                        }
                        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writerSpace);
                        anchor = ArrayUtils.printArray(array, null, null, null, writerSpace);
                    }
                    rows += Sorting.heapsort(array, writer);
                    if (options.containsKey(Flag.PREPRINT)) {
                        for (int i = 0; i < rows; i++) {
                            anchor = ArrayUtils.printEmptyArray(array.length, anchor, writerSpace);
                        }
                        TikZUtils.printTikzEnd(writerSpace);
                    }
                    break;
                case "heapsortWithTrees":
                    array = (Integer[])input;
                    if (options.containsKey(Flag.PREPRINT)) {
                        if (DSALExercises.STUDENT_MODE) {
                            writerSpace.write("\\noindent Sortieren Sie das folgende Array mithilfe von Heapsort.");
                            writerSpace.newLine();
                            writerSpace.write("Geben Sie dazu das Array nach jeder Swap-Operation an.\\\\[2ex]");
                            writerSpace.newLine();
                        }
                        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writerSpace);
                        anchor = ArrayUtils.printArray(array, null, null, null, writerSpace);
                    }
                    rows += Sorting.heapsortWithTrees(array, writer);
                    if (options.containsKey(Flag.PREPRINT)) {
                        for (int i = 0; i < rows; i++) {
                            anchor = ArrayUtils.printEmptyArray(array.length, anchor, writerSpace);
                        }
                        TikZUtils.printTikzEnd(writerSpace);
                    }
                    break;
                case "btree":
                    IntBTree.btree(
                        new IntBTree(
                            options.containsKey(Flag.DEGREE) ? Integer.parseInt(options.get(Flag.DEGREE)) : 2
                        ),
                        (Deque<Pair<Integer,Boolean>>)input,
                        DSALExercises.parseOperations(options),
                        writer,
                        options.containsKey(Flag.PREPRINT) ? writerSpace : null
                    );
                    break;
                case "hashDivision":
                    in = (Pair<double[], Integer[]>)input;
                    array = in.y;
                    m = (int)in.x[0];
                    
                    if (options.containsKey(Flag.PREPRINT)) {
                            writerSpace.write("\\noindent F\\\"ugen Sie die folgenden Werte in das unten stehende Array der L\\\"ange " + m +  " unter Verwendung der Divisionsmethode ohne Sondierung (also durch Verkettung) ein:\\\\[2ex]");
                            writerSpace.newLine();
                            for(int i = 0; i<array.length - 1; ++i)
                            {
                                writerSpace.write(array[i] + ", ");
                            }
                            writerSpace.write(array[array.length-1] + ".");
                            writerSpace.write("\\\\[2ex]");
                            writerSpace.newLine();
                        
                    }
                    params = new double[2];
                    params[0] = 1;
                    params[1] = 0;
                    DSALExercises.Hashing(array, m, params, writer, writerSpace);
                    break;
                case "hashDivisionLinear":
                    in = (Pair<double[], Integer[]>)input;
                    array = in.y;
                    m = (int)in.x[0];
                    
                    if (options.containsKey(Flag.PREPRINT)) {
                            writerSpace.write("\\noindent F\\\"ugen Sie die folgenden Werte in das unten stehende Array der L\\\"ange " + m +  " unter Verwendung der Divisionsmethode mit linearer Sondierung ein:\\\\[2ex]");
                            writerSpace.newLine();
                            for(int i = 0; i<array.length - 1; ++i)
                            {
                                writerSpace.write(array[i] + ", ");
                            }
                            writerSpace.write(array[array.length-1] + ".");
                            writerSpace.write("\\\\[2ex]");
                            writerSpace.newLine();
                        
                    }
                    params = new double[2];
                    params[0] = 1;
                    params[1] = 1;
                    DSALExercises.Hashing(array, m, params, writer, writerSpace);
                    break;
                case "hashDivisionQuadratic":
                    in = (Pair<double[], Integer[]>)input;
                    array = in.y;
                    m = (int)in.x[0];
                    
                    if (options.containsKey(Flag.PREPRINT)) {
                            writerSpace.write("\\noindent F\\\"ugen Sie die folgenden Werte in das unten stehende Array der L\\\"ange " + m +  " unter Verwendung der Divisionsmethode mit quadratischer Sondierung ($c_1$= " + in.x[1] + ", $c_2$= "+ in.x[2] + " ) ein:\\\\[2ex]");
                            writerSpace.newLine();
                            for(int i = 0; i<array.length - 1; ++i)
                            {
                                writerSpace.write(array[i] + ", ");
                            }
                            writerSpace.write(array[array.length-1] + ".");
                            writerSpace.write("\\\\[2ex]");
                            writerSpace.newLine();
                        
                    }
                    params = new double[4];
                    params[0] = 1;
                    params[1] = 2;
                    params[2] = in.x[1];
                    params[3] = in.x[2];
                    DSALExercises.Hashing(array, m, params, writer, writerSpace);
                    break;
                case "hashMultiplication":
                    in = (Pair<double[], Integer[]>)input;
                    array = in.y;
                    m = (int)in.x[0];
                    
                    if (options.containsKey(Flag.PREPRINT)) {
                            writerSpace.write("\\noindent F\\\"ugen Sie die folgenden Werte in das unten stehende Array der L\\\"ange " + m +  " unter Verwendung der Multiplikationsmethode (c = " + in.x[1] +") ohne Sondierung (also durch Verkettung) ein:\\\\[2ex]");
                            writerSpace.newLine();
                            for(int i = 0; i<array.length - 1; ++i)
                            {
                                writerSpace.write(array[i] + ", ");
                            }
                            writerSpace.write(array[array.length-1] + ".");
                            writerSpace.write("\\\\[2ex]");
                            writerSpace.newLine();
                        
                    }
                    params = new double[3];
                    params[0] = 2;
                    params[1] = 0;
                    params[2] = in.x[1];
                    DSALExercises.Hashing(array, m, params, writer, writerSpace);
                    break;
                case "hashMultiplicationLinear":
                    in = (Pair<double[], Integer[]>)input;
                    array = in.y;
                    m = (int)in.x[0];
                    
                    if (options.containsKey(Flag.PREPRINT)) {
                            writerSpace.write("\\noindent F\\\"ugen Sie die folgenden Werte in das unten stehende Array der L\\\"ange " + m +  " unter Verwendung der Multiplikationsmethode (c = " + in.x[1] +") mit linearer Sondierung ein:\\\\[2ex]");
                            writerSpace.newLine();
                            for(int i = 0; i<array.length - 1; ++i)
                            {
                                writerSpace.write(array[i] + ", ");
                            }
                            writerSpace.write(array[array.length-1] + ".");
                            writerSpace.write("\\\\[2ex]");
                            writerSpace.newLine();
                        
                    }
                    params = new double[3];
                    params[0] = 2;
                    params[1] = 1;
                    params[2] = in.x[1];
                    DSALExercises.Hashing(array, m, params, writer, writerSpace);
                    break;
                case "hashMultiplicationQuadratic":
                    in = (Pair<double[], Integer[]>)input;
                    array = in.y;
                    m = (int)in.x[0];
                    
                    if (options.containsKey(Flag.PREPRINT)) {
                            writerSpace.write("\\noindent F\\\"ugen Sie die folgenden Werte in das unten stehende Array der L\\\"ange " + m +  " unter Verwendung der Multiplikationsmethode (c = " + in.x[1] +") mit quadratischer Sondierung ($c_1$= " + in.x[2] + ", $c_2$= "+ in.x[3] + " ) ein:\\\\[2ex]");
                            writerSpace.newLine();
                            for(int i = 0; i<array.length - 1; ++i)
                            {
                                writerSpace.write(array[i] + ", ");
                            }
                            writerSpace.write(array[array.length-1] + ".");
                            writerSpace.write("\\\\[2ex]");
                            writerSpace.newLine();
                        
                    }
                    params = new double[5];
                    params[0] = 2;
                    params[1] = 2;
                    params[2] = in.x[1];
                    params[3] = in.x[2];
                    params[4] = in.x[3];
                    DSALExercises.Hashing(array, m, params, writer, writerSpace);
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
     * Performs hashing in different variations (divisionmethod(1), multiplicationmethod(2), linear probing(1), quadratic probing(2), no probing (0)).
     * @param in The array containing the values, which are to be hashed.
     * @param m The length of the hashtable.
     * @param params The array containing the parameters in this order: Algorithm (1,2), probe-mode (0,1,2), additional parameters: c, c1, c2
     * @param writer The writer for the solution.
     * @param writerSpace The writer for the exercise.
     * @throws IOException If some error occurs during output or if in.size > m or if c1 and c2 are badly chosen.
     */
    private static void Hashing(
        Integer[] in,
        int m,
        double[] params,
        BufferedWriter writer,
        BufferedWriter writerSpace
    ) throws IOException {
        int collisionCount = 0;
        Integer[] indizes = new Integer[m];
        int algorithm = (int)params[0];
        int probe = (int)params[1];
        String anchor = "";
        double c = 0.0;
        double c1 = 0.0;
        double c2 = 0.0;
        switch(probe)
        {
            case 2:
                switch(algorithm)
                {
                    case 1: // Division Hashing
                        c1 = params[2];
                        c2 = params[3];
                        writer.write("m = " + m + ", $c_1$ = " + c1 + ", $c_2$ = " + c2 + ":\\\\[2ex]");
                        break;
                    case 2: // Multiplication Hashing
                        c = params[2];
                        c1 = params[3];
                        c2 = params[4];
                        writer.write("m = " + m + ", c = " + c + ", $c_1$ = " + c1 + ", $c_2$ = " + c2 + ":\\\\[2ex]");
                        break;
                }
                break;
            default:
                switch(algorithm)
                {
                    case 1: // Division Hashing
                        writer.write("m = " + m + ":\\\\[2ex]");
                        break;
                    case 2: // Multiplication Hashing
                        c = params[2];
                        writer.write("m = " + m + ", c = " + c + ":\\\\[2ex]");
                        break;
                }
                break;
        }
        
        if( probe != 0) // probing
        {
            TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writerSpace);
            TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
            for(int i = 0; i < m; ++i)
            {
                indizes[i] = i;
            }
            anchor = ArrayUtils.printArray(indizes, null, null, null, writerSpace);
            anchor = ArrayUtils.printEmptyArray(m, anchor, writerSpace);
            
            Integer[] solution = new Integer[m];
            for(int i = 0; i < solution.length; ++i)
            {
                solution[i] = null;
            }
            for(int i = 0; i < in.length; ++i)
            {
                int pos = 0;
                switch(algorithm)
                {
                    case 1: // Division Hashing
                        pos = in[i]%m;
                        break;
                    case 2: // Multiplication Hashing
                        pos = (int)Math.floor(m * (Math.round(((in[i] * c) - (int)(in[i] * c)) * 100.0) / 100.0));
                        break;
                }
                
                if(solution[pos] == null)
                {
                    solution[pos] = in[i];
                }
                else
                {
                    int off = 1;
                    switch(probe)
                    {
                        case 1: // linear probing
                            while(solution[(pos+off)%m] != null && off < m)
                            {
                                ++off;
                                ++collisionCount;
                            }
                            if(solution[(pos+off)%m] != null)
                            {
                                throw new IOException("The array size was chosen too small!");
                            }
                            solution[(pos+off)%m] = in[i];
                            break;
                        case 2: // quadratic probing
                            while(solution[((int)Math.floor(pos + c1*off + c2*off*off))%m] != null && off < m)
                            {
                                ++off;
                                ++collisionCount;
                            }
                            if(solution[((int)Math.floor(pos + c1*off + c2*off*off))%m] != null)
                            {
                                String errormsg = "The array size was chosen too small or the constants for quadratic probing are chosen badly: Insertion of " + in[i] + " failed!";
                                throw new IOException(errormsg);
                            }
                            solution[((int)Math.floor(pos + c1*off + c2*off*off))%m] = in[i];
                            break;
                        case 3: // doppeltes hashing
                            break;
                        default:
                    }
                }
            }
            if(!DSALExercises.STUDENT_MODE)
            {
                System.out.println("#collisions by probing: " + collisionCount);
            }
            anchor = ArrayUtils.printArray(solution, null, null, null, writer);
        }
        else // probe == 0 -> no probing
        {
            TikZUtils.printTikzBeginning(TikZStyle.BORDERLESS, writerSpace);
            TikZUtils.printTikzBeginning(TikZStyle.BORDERLESS, writer);
            String[] solution = new String[m];
            for(int i = 0; i < m; ++i)
            {
                solution[i] = i + ":";
            }
            anchor = ArrayUtils.printVerticalStringArray(solution, null, null, null, writerSpace);
            
            
            for(int i = 0; i < in.length; ++i)
            {
                int pos = 0;
                switch(algorithm)
                {
                    case 1: // Division Hashing
                        pos = in[i]%m;
                        break;
                    case 2: // Multiplication Hashing
                        //System.out.println("Test: " + Math.round(((in[i] * c) - (int)(in[i] * c)) * 100.0) / 100.0 + ", m is: " + m);
                        pos = (int)Math.floor( m * Math.round(((in[i] * c) - (int)(in[i] * c)) * 100.0) / 100.0 );
                        break;
                }
                if(solution[pos].substring(solution[pos].length()-1).equals(":"))
                {
                    solution[pos] += " " + in[i];
                }
                else
                {
                    solution[pos] += ", " + in[i];
                }
            }
            anchor = ArrayUtils.printVerticalStringArray(solution, null, null, null, writer);
        }
        
        TikZUtils.printTikzEnd(writerSpace);
        TikZUtils.printTikzEnd(writer);
    }

    /**
     * @param options The option flags.
     * @return The input specified by the options.
     */
    private static Object parseInput(Map<Flag, String> options) {
        String[] nums = null;
        switch (options.get(Flag.ALGORITHM)) {
            case "selectionsort":
            case "bubblesort":
            case "insertionsort":
            case "quicksort":
            case "mergesort":
            case "mergesortWithSplitting":
            case "heapsort":
            case "heapsortWithTrees":
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
                    if (options.containsKey(Flag.LINES)) {
                        length = Integer.parseInt(options.get(Flag.LINES));
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
            case "hashDivision":
            case "hashDivisionLinear":
            case "hashDivisionQuadratic":
            case "hashMultiplication":
            case "hashMultiplicationLinear":
            case "hashMultiplicationQuadratic":
                Pair<double[], Integer[]> input = new Pair<double[], Integer[]>(null,null);
                String[] paramString = null;
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
                    if (options.containsKey(Flag.LINES)) {
                        length = Integer.parseInt(options.get(Flag.LINES));
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
                    int m = gen.nextInt(DSALExercises.NUMBER_LIMIT);
                    int c1 = gen.nextInt(DSALExercises.NUMBER_LIMIT);
                    int c2 = gen.nextInt(DSALExercises.NUMBER_LIMIT);
                    
                    while(!DSALExercises.arePrime(m,c1,c2))
                    {
                        m = gen.nextInt(DSALExercises.NUMBER_LIMIT);
                        if(DSALExercises.arePrime(m,c1,c2)) {
                            break;
                        }
                        c1 = gen.nextInt(DSALExercises.NUMBER_LIMIT);
                        if(DSALExercises.arePrime(m,c1,c2)) {
                            break;
                        }
                        c2 = gen.nextInt(DSALExercises.NUMBER_LIMIT);
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
                for(int i = 0; i < params.length; ++i)
                {
                    params[i] = Double.parseDouble(paramString[i].trim());
                }
                input = new Pair<double[], Integer[]>(params, array);
                return input;
            case "btree":
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
                    if (options.containsKey(Flag.LINES)) {
                        length = Integer.parseInt(options.get(Flag.LINES));
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
            default:
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
     * Checks for a given number if it is a prime.
     * @param num The number to be checked.
     * @return true, if the number is a prime, false otherwise.
     */
    private static boolean isPrime(int num) {
        int sqrt = (int) Math.sqrt(num) + 1;
        for (int i = 2; i < sqrt; i++) {
            if (num % i == 0) {
                // number is perfectly divisible - no prime
                return false;
            }
        }
        return true;
    }

    private static boolean arePrime(int a, int b, int c) {
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
     * Option flags for the program arguments.
     * @author cryingshadow
     * @version $Id$
     */
    private static enum Flag {

        /**
         * The algorithm to apply to the input. Currently, selectionsort, bubblesort, insertionsort, quicksort, 
         * mergesort, and heapsort are implemented. Must be specified.
         */
        ALGORITHM("-a", "Algorithm", true),

        /**
         * Degree (e.g., of a B-tree).
         */
        DEGREE("-d", "Degree", true),

        /**
         * Input directly specified as a String. Must not be specified together with -s, but one of them must be 
         * specified.
         */
        INPUT("-i", "Input", false),

        /**
         * Additional lines for pre-prints. Defaults to 0 if not set. Ignored if -p is not set.
         */
        LINES("-l", "Additional lines", true),

        /**
         * File containing operations used to construct a start structure.
         */
        OPERATIONS("-o", "Operations for start structure", false),

        /**
         * File to store LaTeX code for a pre-print where to solve an exercise. E.g., for sorting this might be the 
         * input array followed by a number of empty arrays. If not set, no pre-print will be generated.
         */
        PREPRINT("-p", "Pre-print file", true),

        /**
         * Source file containing the input. Must not be specified together with -i, but one of them must be specified.
         */
        SOURCE("-s", "Source file", true),

        /**
         * Target file to store the LaTeX code in. If not specified, the solution is sent to the standard output.
         */
        TARGET("-t", "Target file", true);

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
         * @param a Flag indicating whether this option is available for students.
         */
        private Flag(String s, String l, boolean a) {
            this.shortName = s;
            this.longName = l;
            this.forStudents = a;
        }

    }
    
}
