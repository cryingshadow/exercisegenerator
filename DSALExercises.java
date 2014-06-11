import java.io.*;
import java.util.*;

/**
 * Programm for creating solutions for DSAL exercises.
 * @author cryingshadow
 * @version $Id$
 */
public class DSALExercises {

    /**
     * The name of the center environment.
     */
    private static final String CENTER = "center";
    
    /**
     * The name of the enumerate environment.
     */
    private static final String ENUMERATE = "enumerate";
    
    /**
     * The item command.
     */
    private static final String ITEM = "\\item";
    
    /**
     * Since there are three different names for B-trees of degree 2, this String can be used to customize the output 
     * for a lecture.
     */
    private static final String NAME_OF_BTREE_WITH_DEGREE_2 = "2--3--4--Baum";

    /**
     * A number to uniquely identify nodes.
     */
    private static int number = 0;

    /**
     * Limit for random numbers in student mode.
     */
    private static final int NUMBER_LIMIT = 100;

    /**
     * Flag used to turn off some options for the student version.
     */
    private static final boolean STUDENT_MODE = true;
    
    /**
     * @param array An int array containing the keys in ascending order.
     * @param key The key to search.
     * @param from The start index to search from (inclusive).
     * @param to The end index to search to (inclusive).
     * @return If the array contains the key to search for within the interval specified by the start and end index, an 
     *         index within that interval where the array contains this key is returned. Otherwise the index of the 
     *         first element within the interval greater than the key to search for is returned. If no such element 
     *         exists, the returned index is one bigger than the end index.  
     */
    public static Integer binarySearch(Integer[] array, Integer key, Integer from, Integer to) {
        if (to - from <= 0) {
            if (to - from == 0 && array[from] < key) {
                return from + 1;
            }
            return from;
        }
        Integer index = from + ((to - from) / 2);
        Integer there = array[index];
        if (there == key) {
            return index;
        } else if (there < key) {
            return DSALExercises.binarySearch(array, key, index + 1, to);
        } else {
            return DSALExercises.binarySearch(array, key, from, index);
        }
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
                        DSALExercises.printTikzBeginning(TikZStyle.ARRAY, writerSpace);
                        anchor = DSALExercises.printArray(array, null, null, null, writerSpace);
                    }
                    rows += DSALExercises.selectionsort(array, writer);
                    if (options.containsKey(Flag.PREPRINT)) {
                        for (int i = 0; i < rows; i++) {
                            anchor = DSALExercises.printEmptyArray(array.length, anchor, writerSpace);
                        }
                        DSALExercises.printTikzEnd(writerSpace);
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
                        DSALExercises.printTikzBeginning(TikZStyle.ARRAY, writerSpace);
                        anchor = DSALExercises.printArray(array, null, null, null, writerSpace);
                    }
                    rows += DSALExercises.bubblesort(array, writer);
                    if (options.containsKey(Flag.PREPRINT)) {
                        for (int i = 0; i < rows; i++) {
                            anchor = DSALExercises.printEmptyArray(array.length, anchor, writerSpace);
                        }
                        DSALExercises.printTikzEnd(writerSpace);
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
                        DSALExercises.printTikzBeginning(TikZStyle.ARRAY, writerSpace);
                        anchor = DSALExercises.printArray(array, null, null, null, writerSpace);
                    }
                    rows += DSALExercises.insertionsort(array, writer);
                    if (options.containsKey(Flag.PREPRINT)) {
                        for (int i = 0; i < rows; i++) {
                            anchor = DSALExercises.printEmptyArray(array.length, anchor, writerSpace);
                        }
                        DSALExercises.printTikzEnd(writerSpace);
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
                        DSALExercises.printTikzBeginning(TikZStyle.ARRAY, writerSpace);
                        anchor = DSALExercises.printArray(array, null, null, null, writerSpace);
                    }
                    rows += DSALExercises.quicksort(array, writer);
                    if (options.containsKey(Flag.PREPRINT)) {
                        for (int i = 0; i < rows; i++) {
                            anchor = DSALExercises.printEmptyArray(array.length, anchor, writerSpace);
                        }
                        DSALExercises.printTikzEnd(writerSpace);
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
                        DSALExercises.printTikzBeginning(TikZStyle.ARRAY, writerSpace);
                        anchor = DSALExercises.printArray(array, null, null, null, writerSpace);
                    }
                    rows += DSALExercises.mergesort(array, false, writer);
                    if (options.containsKey(Flag.PREPRINT)) {
                        for (int i = 0; i < rows; i++) {
                            anchor = DSALExercises.printEmptyArray(array.length, anchor, writerSpace);
                        }
                        DSALExercises.printTikzEnd(writerSpace);
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
                        DSALExercises.printTikzBeginning(TikZStyle.ARRAY, writerSpace);
                        anchor = DSALExercises.printArray(array, null, null, null, writerSpace);
                    }
                    rows += DSALExercises.mergesort(array, true, writer);
                    if (options.containsKey(Flag.PREPRINT)) {
                        for (int i = 0; i < rows; i++) {
                            anchor = DSALExercises.printEmptyArray(array.length, anchor, writerSpace);
                        }
                        DSALExercises.printTikzEnd(writerSpace);
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
                        DSALExercises.printTikzBeginning(TikZStyle.ARRAY, writerSpace);
                        anchor = DSALExercises.printArray(array, null, null, null, writerSpace);
                    }
                    rows += DSALExercises.heapsort(array, writer);
                    if (options.containsKey(Flag.PREPRINT)) {
                        for (int i = 0; i < rows; i++) {
                            anchor = DSALExercises.printEmptyArray(array.length, anchor, writerSpace);
                        }
                        DSALExercises.printTikzEnd(writerSpace);
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
                        DSALExercises.printTikzBeginning(TikZStyle.ARRAY, writerSpace);
                        anchor = DSALExercises.printArray(array, null, null, null, writerSpace);
                    }
                    rows += DSALExercises.heapsortWithTrees(array, writer);
                    if (options.containsKey(Flag.PREPRINT)) {
                        for (int i = 0; i < rows; i++) {
                            anchor = DSALExercises.printEmptyArray(array.length, anchor, writerSpace);
                        }
                        DSALExercises.printTikzEnd(writerSpace);
                    }
                    break;
                case "btree":
                    DSALExercises.btree(
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
						if (DSALExercises.STUDENT_MODE) {
							writerSpace.write("\\noindent Fügen Sie die folgenden Werte in das unten stehende Array der Länge " + m +  " unter Verwendung von Division--Hashing ohne Sondierung ein:\\\\[2ex]");
							writerSpace.newLine();
							for(int i = 0; i<array.length - 1; ++i)
							{
								writerSpace.write(array[i] + ", ");
							}
							writerSpace.write(array[array.length-1] + ".");
							writerSpace.write("\\\\[2ex]");
							writerSpace.newLine();
						}
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
						if (DSALExercises.STUDENT_MODE) {
							writerSpace.write("\\noindent Fügen Sie die folgenden Werte in das unten stehende Array der Länge " + m +  " unter Verwendung von Division--Hashing mit linearer Sondierung ein:\\\\[2ex]");
							writerSpace.newLine();
							for(int i = 0; i<array.length - 1; ++i)
							{
								writerSpace.write(array[i] + ", ");
							}
							writerSpace.write(array[array.length-1] + ".");
							writerSpace.write("\\\\[2ex]");
							writerSpace.newLine();
						}
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
						if (DSALExercises.STUDENT_MODE) {
							writerSpace.write("\\noindent Fügen Sie die folgenden Werte in das unten stehende Array der Länge " + m +  " unter Verwendung von Division--Hashing mit quadratischer Sondierung ($c_1$= " + in.x[1] + ", $c_2$= "+ in.x[2] + " ) ein:\\\\[2ex]");
							writerSpace.newLine();
							for(int i = 0; i<array.length - 1; ++i)
							{
								writerSpace.write(array[i] + ", ");
							}
							writerSpace.write(array[array.length-1] + ".");
							writerSpace.write("\\\\[2ex]");
							writerSpace.newLine();
						}
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
						if (DSALExercises.STUDENT_MODE) {
							writerSpace.write("\\noindent Fügen Sie die folgenden Werte in das unten stehende Array der Länge " + m +  " unter Verwendung von Multiplication--Hashing (c = " + in.x[1] +") ohne Sondierung ein:\\\\[2ex]");
							writerSpace.newLine();
							for(int i = 0; i<array.length - 1; ++i)
							{
								writerSpace.write(array[i] + ", ");
							}
							writerSpace.write(array[array.length-1] + ".");
							writerSpace.write("\\\\[2ex]");
							writerSpace.newLine();
						}
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
						if (DSALExercises.STUDENT_MODE) {
							writerSpace.write("\\noindent Fügen Sie die folgenden Werte in das unten stehende Array der Länge " + m +  " unter Verwendung von Multiplication--Hashing (c = " + in.x[1] +") mit linearer Sondierung ein:\\\\[2ex]");
							writerSpace.newLine();
							for(int i = 0; i<array.length - 1; ++i)
							{
								writerSpace.write(array[i] + ", ");
							}
							writerSpace.write(array[array.length-1] + ".");
							writerSpace.write("\\\\[2ex]");
							writerSpace.newLine();
						}
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
						if (DSALExercises.STUDENT_MODE) {
							writerSpace.write("\\noindent Fügen Sie die folgenden Werte in das unten stehende Array der Länge " + m +  " unter Verwendung von Multiplication--Hashing (c = " + in.x[3] +") mit quadratischer Sondierung ($c_1$= " + in.x[1] + ", $c_2$= "+ in.x[2] + " ) ein:\\\\[2ex]");
							writerSpace.newLine();
							for(int i = 0; i<array.length - 1; ++i)
							{
								writerSpace.write(array[i] + ", ");
							}
							writerSpace.write(array[array.length-1] + ".");
							writerSpace.write("\\\\[2ex]");
							writerSpace.newLine();
						}
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
			DSALExercises.printTikzBeginning(TikZStyle.ARRAY, writerSpace);
			DSALExercises.printTikzBeginning(TikZStyle.ARRAY, writer);
			for(int i = 0; i < m; ++i)
			{
				indizes[i] = i;
			}
			anchor = DSALExercises.printArray(indizes, null, null, null, writerSpace);
			anchor = DSALExercises.printEmptyArray(m, anchor, writerSpace);
			
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
						pos = (int)Math.floor(m * ((in[i] * c) - (int)(in[i] * c)));
						break;
				}
				
				System.out.println("Insert: " + in[i] + " at " + pos);
				
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
			anchor = DSALExercises.printArray(solution, null, null, null, writer);
		}
		else // probe == 0 -> no probing
		{
			DSALExercises.printTikzBeginning(TikZStyle.BORDERLESS, writerSpace);
			DSALExercises.printTikzBeginning(TikZStyle.BORDERLESS, writer);
			String[] solution = new String[m];
			for(int i = 0; i < m; ++i)
			{
				solution[i] = i + ":";
			}
			anchor = DSALExercises.printVerticalStringArray(solution, null, null, null, writerSpace);
			
			
			for(int i = 0; i < in.length; ++i)
			{
				int pos = 0;
				switch(algorithm)
				{
					case 1: // Division Hashing
						pos = in[i]%m;
						break;
					case 2: // Multiplication Hashing
						pos = (int)Math.floor(m * ((in[i] * c) - (int)(in[i] * c)));
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
			anchor = DSALExercises.printVerticalStringArray(solution, null, null, null, writer);
		}
		
		DSALExercises.printTikzEnd(writerSpace);
		DSALExercises.printTikzEnd(writer);
	}
	
    /**
     * Performs the operations specified by <code>construction</code> and <code>ops</code> on the specified B-tree and
     * prints the results to the specified writer. The <code>construction</code> operations are not displayed.
     * @param tree The B-tree.
     * @param ops The operations.
     * @param construction The operations used to construct the start structure.
     * @param writer The writer for the solution.
     * @param writerSpace The writer for the tree to start with (the one reached after the <code>construction</code> 
     *                    operations). May be null if this tree should not be displayed separately.
     * @throws IOException If some error occurs during output.
     */
    private static void btree(
        IntBTree tree,
        Deque<Pair<Integer, Boolean>> ops,
        Deque<Pair<Integer, Boolean>> construction,
        BufferedWriter writer,
        BufferedWriter writerSpace
    ) throws IOException {
        if (ops.isEmpty()) {
            return;
        }
        while (!construction.isEmpty()) {
            Pair<Integer, Boolean> operation = construction.poll();
            if (operation.y) {
                tree.add(operation.x);
            } else {
                tree.remove(operation.x);
            }
        }
        if (writerSpace != null) {
            int degree = tree.getDegree();
            if (ops.size() > 1) {
                if (tree.isEmpty()) {
                    writerSpace.write(
                        "\\noindent F\\\"uhren Sie folgenden Operationen beginnend mit einem anfangs leeren "
                        + (
                            degree == 2 ?
                                DSALExercises.NAME_OF_BTREE_WITH_DEGREE_2 :
                                    "B-Baum mit Grad $t = " + degree + "$"
                        ) + " aus und geben Sie die dabei jeweils entstehenden B\\\"aume an:\\\\\\\\"
                    );
                    writerSpace.newLine();
                } else {
                    writerSpace.write(
                        "\\noindent Betrachten Sie den folgenden "
                        + (
                            degree == 2 ?
                                DSALExercises.NAME_OF_BTREE_WITH_DEGREE_2 :
                                    "B-Baum mit Grad $t = " + degree + "$"
                        ) + ":\\\\[2ex]"
                    );
                    writerSpace.newLine();
                    writerSpace.newLine();
                    DSALExercises.printBeginning(DSALExercises.CENTER, writerSpace);
                    DSALExercises.printTikzBeginning(TikZStyle.BTREE, writerSpace);
                    DSALExercises.printBTree(tree, writerSpace);
                    DSALExercises.printTikzEnd(writerSpace);
                    DSALExercises.printEnd(DSALExercises.CENTER, writerSpace);
                    writerSpace.newLine();
                    writerSpace.newLine();
                    writerSpace.write("\\vspace*{1ex}");
                    writerSpace.newLine();
                    writerSpace.write(
                        "\\noindent F\\\"uhren Sie beginnend mit diesem Baum die folgenden Operationen aus "
                        + "und geben Sie die dabei jeweils entstehenden B\\\"aume an:\\\\\\\\"
                    );
                    writerSpace.newLine();
                }
                DSALExercises.printBeginning(DSALExercises.ENUMERATE, writerSpace);
                for (Pair<Integer, Boolean> op : ops) {
                    if (op.y) {
                        writerSpace.write(DSALExercises.ITEM + " " + op.x + " einf\\\"ugen\\\\");
                    } else {
                        writerSpace.write(DSALExercises.ITEM + " " + op.x + " l\\\"oschen\\\\");
                    }
                    writerSpace.newLine();
                }
                DSALExercises.printEnd(DSALExercises.ENUMERATE, writerSpace);
            } else {
                Pair<Integer, Boolean> op = ops.peek();
                if (tree.isEmpty()) {
                    if (op.y) {
                        writerSpace.write(
                            "\\noindent F\\\"ugen Sie den Wert "
                            + op.x
                            + " in einen leeren "
                            + (
                                degree == 2 ?
                                    DSALExercises.NAME_OF_BTREE_WITH_DEGREE_2 :
                                        "B-Baum mit Grad $t = " + degree + "$"
                            ) + " ein und geben Sie den dabei entstehenden Baum an."
                        );
                    } else {
                        // this case is nonsense 
                        return;
                    }
                } else {
                    if (op.y) {
                        writerSpace.write(
                            "\\noindent F\\\"ugen Sie den Wert "
                            + op.x
                            + " in den folgenden "
                            + (
                                degree == 2 ?
                                    DSALExercises.NAME_OF_BTREE_WITH_DEGREE_2 :
                                        "B-Baum mit Grad $t = " + degree + "$"
                            ) + " ein und geben Sie den dabei entstehenden Baum an:\\\\[2ex]"
                        );
                    } else {
                        writerSpace.write(
                            "\\noindent L\\\"oschen Sie den Wert "
                            + op.x
                            + " aus dem folgenden "
                            + (
                                degree == 2 ?
                                    DSALExercises.NAME_OF_BTREE_WITH_DEGREE_2 :
                                        "B-Baum mit Grad $t = " + degree + "$"
                            ) + " und geben Sie den dabei entstehenden Baum an:\\\\[2ex]"
                        );
                    }
                    writerSpace.newLine();
                    writerSpace.newLine();
                    DSALExercises.printBeginning(DSALExercises.CENTER, writerSpace);
                    DSALExercises.printTikzBeginning(TikZStyle.BTREE, writerSpace);
                    DSALExercises.printBTree(tree, writerSpace);
                    DSALExercises.printTikzEnd(writerSpace);
                    DSALExercises.printEnd(DSALExercises.CENTER, writerSpace);
                    writerSpace.newLine();
                }
            }
        }
        int step = 1;
        while (!ops.isEmpty()) {
            Pair<Integer, Boolean> operation = ops.poll();
            if (operation.y) {
                tree.add(operation.x);
            } else {
                tree.remove(operation.x);
            }
            DSALExercises.printSamePageBeginning(step++, operation, writer);
            DSALExercises.printTikzBeginning(TikZStyle.BTREE, writer);
            DSALExercises.printBTree(tree, writer);
            DSALExercises.printTikzEnd(writer);
            DSALExercises.printSamePageEnd(writer);
        }
    }

    /**
     * Sorts the specified array using bubblesort and outputs the solution as a TikZ picture to the specified writer.
     * @param array The array to sort.
     * @param writer The writer for outputting the solution.
     * @return The number of rows needed for the solution (excluding the original array).
     * @throws IOException If some error occurs while outputting the solution.
     */
    private static int bubblesort(Integer[] array, BufferedWriter writer) throws IOException {
        DSALExercises.printTikzBeginning(TikZStyle.ARRAY, writer);
        String anchor = DSALExercises.printArray(array, null, null, null, writer);
        int res = 0;
        int length = array.length;
        while (length > 1) {
            int n = 1;
            for (int i = 0; i < length - 1; i++) {
                if (array[i] > array[i + 1]) {
                    DSALExercises.swap(array, i, i + 1);
                    anchor = DSALExercises.printArray(array, null, null, anchor, writer);
                    res++;
                    n = i + 1;
                }
            }
            length = n;
        }
//        for (int i = 0; i < array.length - 1; i++) {
//            for (int j = array.length - 1; j > i; j--) {
//                if (array[j] < array[j - 1]) {
//                    SortingExercise.swap(array, j, j - 1);
//                    anchor = SortingExercise.printArray(array, null, null, anchor, writer);
//                    res++;
//                }
//            }
//        }
        DSALExercises.printTikzEnd(writer);
        return res;
    }

    /**
     * Establishes the heap property on the branch starting at index <code>from</code> when interpreting the array up 
     * to index <code>to</code> as a heap by sinking the element at index <code>from</code> as far as necessary. 
     * Moreover, the current state of the array is output to writer after each swap operation.
     * @param array The array.
     * @param from The index of the element to sink.
     * @param to The length of the array part to be interpreted as a heap.
     * @param anchor The name of the left-most node of the recent row in the TikZ array output.
     * @param separate Indicates which nodes in the array output should be separated horizontally.
     * @param writer The writer to send the output to.
     * @return The number of rows needed for the solution and the name of the left-most node of the most recent row in 
     *         the TikZ array output.
     * @throws IOException If some error occurs during solution output.
     */
    private static Object[] heapify(
        Integer[] array,
        int from,
        int to,
        String anchor,
        boolean[] separate,
        BufferedWriter writer
    ) throws IOException {
        int i = from;
        String newAnchor = anchor;
        int res = 0;
        while (i <= to / 2) {
            int j = 2 * i;
            if (j < to && array[j] > array[j - 1]) {
                j++;
            }
            if (array[j - 1] <= array[i - 1]) {
                break;
            }
            DSALExercises.swap(array, j - 1, i - 1);
            newAnchor = DSALExercises.printArray(array, separate, null, newAnchor, writer);
            res++;
            i = j;
        }
        return new Object[]{res, newAnchor};
    }

    /**
     * Establishes the heap property on the branch starting at index <code>from</code> when interpreting the array up 
     * to index <code>to</code> as a heap by sinking the element at index <code>from</code> as far as necessary. 
     * Moreover, the current state of the array is output both as tree and array to writer after each swap operation.
     * @param array The array.
     * @param from The index of the element to sink.
     * @param to The length of the array part to be interpreted as a heap.
     * @param separate Indicates which nodes in the array output should be separated horizontally.
     * @param step The current evaluation step.
     * @param writer The writer to send the output to.
     * @return The next evaluation step after this heapification has been performed.
     * @throws IOException If some error occurs during solution output.
     */
    private static int heapifyWithTrees(
        Integer[] array,
        int from,
        int to,
        boolean[] separate,
        int step,
        BufferedWriter writer
    ) throws IOException {
        int res = step;
        int i = from;
        while (i <= to / 2) {
            int j = 2 * i;
            if (j < to && array[j] > array[j - 1]) {
                j++;
            }
            if (array[j - 1] <= array[i - 1]) {
                break;
            }
            DSALExercises.swap(array, j - 1, i - 1);
            DSALExercises.printSamePageBeginning(res++, writer);
            DSALExercises.printTree(array, to - 1, writer);
            DSALExercises.printProtectedNewline(writer);
            DSALExercises.printTikzBeginning(TikZStyle.ARRAY, writer);
            DSALExercises.printArray(array, separate, null, null, writer);
            DSALExercises.printTikzEnd(writer);
            DSALExercises.printSamePageEnd(writer);
            DSALExercises.printVerticalSpace(res, writer);
            i = j;
        }
        return res;
    }

    /**
     * Sorts the specified array using heapsort and outputs the solution as a TikZ picture to the specified writer.
     * @param array The array to sort.
     * @param writer The writer for outputting the solution.
     * @return The number of rows needed for the solution (excluding the original array).
     * @throws IOException If some error occurs while outputting the solution.
     */
    private static int heapsort(Integer[] array, BufferedWriter writer) throws IOException {
        DSALExercises.printTikzBeginning(TikZStyle.ARRAY, writer);
        String anchor = DSALExercises.printArray(array, null, null, null, writer);
        int res = 0;
        boolean[] separate = new boolean[array.length - 1];
        Arrays.fill(separate, false);
        for (int i = array.length / 2; i > 0; i--) {
            Object[] heapified = DSALExercises.heapify(array, i, array.length, anchor, separate, writer);
            res += (Integer)heapified[0];
            anchor = (String)heapified[1];
        }
        for (int i = array.length - 1; i > 0; i--) {
            DSALExercises.swap(array, 0, i);
            res++;
            if (i < separate.length) {
                separate[i] = false;
            }
            if (i > 0) {
                separate[i - 1] = true;
            }
            Object[] heapified =
                DSALExercises.heapify(
                    array,
                    1,
                    i,
                    DSALExercises.printArray(array, separate, null, anchor, writer),
                    separate,
                    writer
                );
            res += (Integer)heapified[0];
            anchor = (String)heapified[1];
        }
        DSALExercises.printTikzEnd(writer);
        return res;
    }

    /**
     * Sorts the specified array using heapsort and outputs the solution as a TikZ picture to the specified writer. 
     * Here, in addition to the arrays, a tree interpretation of the arrays is output.
     * @param array The array to sort.
     * @param writer The writer for outputting the solution.
     * @return The number of rows needed for the solution (excluding the original array).
     * @throws IOException If some error occurs while outputting the solution.
     */
    private static int heapsortWithTrees(Integer[] array, BufferedWriter writer) throws IOException {
        int step = 0;
        DSALExercises.printSamePageBeginning(step++, writer);
        DSALExercises.printTree(array, array.length - 1, writer);
        DSALExercises.printProtectedNewline(writer);
        DSALExercises.printTikzBeginning(TikZStyle.ARRAY, writer);
        DSALExercises.printArray(array, null, null, null, writer);
        DSALExercises.printTikzEnd(writer);
        DSALExercises.printSamePageEnd(writer);
        DSALExercises.printVerticalSpace(step, writer);
        boolean[] separate = new boolean[array.length - 1];
        Arrays.fill(separate, false);
        for (int i = array.length / 2; i > 0; i--) {
            step = DSALExercises.heapifyWithTrees(array, i, array.length, separate, step, writer);
        }
        for (int i = array.length - 1; i > 0; i--) {
            DSALExercises.swap(array, 0, i);
            if (i < separate.length) {
                separate[i] = false;
            }
            DSALExercises.printSamePageBeginning(step++, writer);
            if (i > 1) {
                separate[i - 1] = true;
                DSALExercises.printTree(array, i - 1, writer);
                DSALExercises.printProtectedNewline(writer);
            }
            DSALExercises.printTikzBeginning(TikZStyle.ARRAY, writer);
            DSALExercises.printArray(array, separate, null, null, writer);
            DSALExercises.printTikzEnd(writer);
            DSALExercises.printSamePageEnd(writer);
            DSALExercises.printVerticalSpace(step, writer);
            step = DSALExercises.heapifyWithTrees(array, 1, i, separate, step, writer);
        }
        return step - 1;
    }

    /**
     * Sorts the specified array using insertionsort and outputs the solution as a TikZ picture to the specified writer.
     * @param array The array to sort.
     * @param writer The writer for outputting the solution.
     * @return The number of rows needed for the solution (excluding the original array).
     * @throws IOException If some error occurs while outputting the solution.
     */
    private static int insertionsort(Integer[] array, BufferedWriter writer) throws IOException {
        DSALExercises.printTikzBeginning(TikZStyle.ARRAY, writer);
        String anchor = DSALExercises.printArray(array, null, null, null, writer);
        int res = 0;
        for (int i = 1; i < array.length; i++) {
            int insert = array[i];
            int j = i;
            while (j > 0 && array[j - 1] > insert) {
                array[j] = array[j - 1];
                j--;
            }
            array[j] = insert;
            anchor = DSALExercises.printArray(array, null, null, anchor, writer);
            res++;
        }
        DSALExercises.printTikzEnd(writer);
        return res;
    }

    /**
     * Merges two sorted array parts (between start and middle and between middle + 1 and end) to one sorted array part 
     * (from start to end).
     * @param array The array.
     * @param start The start index.
     * @param middle The middle index.
     * @param end The end index.
     */
    private static void merge(Integer[] array, int start, int middle, int end) {
        Integer[] copy = new Integer[end - start + 1];
        int i = 0;
        int j = start;
        int k = middle + 1;
        while (j <= middle && k <= end) {
            if (array[j] <= array[k]) {
                copy[i++] = array[j++];
            } else {
                copy[i++] = array[k++];
            }
        }
        while (j <= middle) {
            copy[i++] = array[j++];
        }
        while (k <= end) {
            copy[i++] = array[k++];
        }
        System.arraycopy(copy, 0, array, start, copy.length);
    }

    /**
     * Sorts the specified array using mergesort and outputs the solution as a TikZ picture to the specified writer.
     * @param array The array to sort.
     * @param printSplitting Flag indicating whether to print the splitting in the beginning.
     * @param writer The writer for outputting the solution.
     * @return The number of rows needed for the solution (excluding the original array).
     * @throws IOException If some error occurs while outputting the solution.
     */
    private static int mergesort(Integer[] array, boolean printSplitting, BufferedWriter writer) throws IOException {
        DSALExercises.printTikzBeginning(TikZStyle.ARRAY, writer);
        boolean[] separate = new boolean[array.length - 1];
        Arrays.fill(separate, !printSplitting);
        boolean[] mark = new boolean[array.length];
        Arrays.fill(mark, true);
        int res =
            (Integer)DSALExercises.mergesort(
                array,
                0,
                array.length - 1,
                DSALExercises.printArray(array, separate, null, null, writer),
                separate,
                mark,
                printSplitting,
                writer
            )[0];
        DSALExercises.printTikzEnd(writer);
        return res;
    }

    /**
     * The actual recursive mergesort algorithm. It sorts the array part from start to end using mergesort while 
     * outputting the solution as a TikZ picture to the specified writer.
     * @param array The array.
     * @param start The start index.
     * @param end The end index.
     * @param anchor The name of the left-most node of the recent row in the TikZ array output.
     * @param separate Indicates which nodes should be separated horizontally in the array output.
     * @param mark An array of equal length to <code>array</code> and all entries set to true. Just passed to avoid 
     *             re-allocation of the same array over and over again.
     * @param printSplitting Flag indicating whether to print the splitting in the beginning.
     * @param writer The writer to send the output to.
     * @return The number of rows needed for the solution and the name of the left-most node of the most recent row in 
     *         the TikZ array output.
     * @throws IOException If some error occurs during solution output.
     */
    private static Object[] mergesort(
        Integer[] array,
        int start,
        int end,
        String anchor,
        boolean[] separate,
        boolean[] mark,
        boolean printSplitting,
        BufferedWriter writer
    ) throws IOException {
        if (start < end) {
            int middle = (start + end) / 2;
            String newAnchor = anchor;
            if (printSplitting) {
                separate[middle] = true;
                newAnchor = DSALExercises.printArray(array, separate, mark, newAnchor, writer);
            }
            Object[] firstStep =
                DSALExercises.mergesort(array, start, middle, newAnchor, separate, mark, printSplitting, writer);
            Object[] secondStep =
                DSALExercises.mergesort(
                    array,
                    middle + 1,
                    end,
                    (String)firstStep[1],
                    separate,
                    mark,
                    printSplitting,
                    writer
                );
            DSALExercises.merge(array, start, middle, end);
            separate[middle] = false;
            return new Object[]{
                ((Integer)firstStep[0]) + ((Integer)secondStep[0]) + 1,
                DSALExercises.printArray(array, separate, null, (String)secondStep[1], writer)
            };
        } else {
            return new Object[]{0, anchor};
        }
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
						if(DSALExercises.arePrime(m,c1,c2))
							break;
						c1 = gen.nextInt(DSALExercises.NUMBER_LIMIT);
						if(DSALExercises.arePrime(m,c1,c2))
							break;
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
     * Partitions the array part between <code>start</code> and <code>end</code> using the element at <code>end</code> 
     * as Pivot element. It returns the resulting index of the Pivot element. So after this method call, all elements 
     * from <code>start</code> to the returned index minus one are less than or equal to the element at the returned 
     * index and all elements from the returned index plus one to <code>end</code> are greater than or equal to the 
     * element at the returned index.
     * @param array The array.
     * @param start The start index.
     * @param end The end index.
     * @return The index of the Pivot element after partitioning.
     */
    private static int partition(Integer[] array, int start, int end) {
        int i = start - 1;
        int j = end;
        while (i < j) {
            i++;
            while (array[i] < array[end]) {
                i++;
            }
            j--;
            while (j > start - 1 && array[j] > array[end]) {
                j--;
            }
            DSALExercises.swap(array, i, j);
        }
        DSALExercises.swap(array, i, j);
        DSALExercises.swap(array, i, end);
        return i;
    }

    /**
     * Prints a row of nodes with the contents of the array.
     * @param array The array of values.
     * @param separate An array indicating which nodes should be separated horizontally. Must have a size exactly one 
     *                 less than array or be null.
     * @param mark An array indicating which node should be marked by a grey background. Must have the same size as 
     *             array or be null.
     * @param below The name of the left-most node in the row above the current row.
     * @param writer The writer to send the output to.
     * @return The name of the left-most node of the current row.
     * @throws IOException If some I/O error occurs.
     */
    private static String printArray(
        Integer[] array,
        boolean[] separate,
        boolean[] mark,
        String below,
        BufferedWriter writer
    ) throws IOException {
        String firstName = "n" + DSALExercises.number++;
        if (below == null) {
            writer.write("\\node[node");
            if (mark != null && mark[0]) {
                writer.write(",fill=black!20");
            }
            writer.write("] (");
            writer.write(firstName);
			if(array[0] != null)
			{
				int val = array[0];
				writer.write(") {" + (val < 10 ? "\\phantom{0}" : "") + val);
			}
            else
			{
				writer.write(") {\\phantom{00}");
			}
            writer.write("};");
            writer.newLine();
        } else {
            writer.write("\\node[node");
            if (mark != null && mark[0]) {
                writer.write(",fill=black!20");
            }
            writer.write("] (");
            writer.write(firstName);
            writer.write(") [below=of ");
            writer.write(below);
            if(array[0] != null)
			{
				int val = array[0];
				writer.write("] {" + (val < 10 ? "\\phantom{0}" : "") + val);
			}
            else
			{
				writer.write("] {\\phantom{00}");
			}
            writer.write("};");
            writer.newLine();
        }
        for (int i = 1; i < array.length; i++) {
            writer.write("\\node[node");
            if (mark != null && mark[i]) {
                writer.write(",fill=black!20");
            }
            writer.write("] (");
            writer.write("n" + DSALExercises.number++);
            writer.write(") [right=");
            if (separate != null && separate[i - 1]) {
                writer.write("0.1 ");
            }
            writer.write("of ");
            writer.write("n" + (DSALExercises.number - 2));
            if(array[i] != null)
			{
				int val = array[i];
				writer.write("] {" + (val < 10 ? "\\phantom{0}" : "") + val);
			}
            else
			{
				writer.write("] {\\phantom{00}");
			}
            writer.write("};");
            writer.newLine();
        }
        return firstName;
    }

    /**
     * Prints the beginning of the specified environment.
     * @param environment The environment.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private static void printBeginning(String environment, BufferedWriter writer) throws IOException {
        writer.write("\\begin{" + environment + "}");
        writer.newLine();
    }

    /**
     * Prints a B-tree to the specified writer.
     * @param tree The B-tree.
     * @param writer The writer.
     * @throws IOException If some error occurs during output.
     */
    private static void printBTree(IntBTree tree, BufferedWriter writer) throws IOException {
        if (tree.hasJustRoot()) {
            writer.write("\\node[draw=black,rounded corners,thick,inner sep=5pt] " + tree.toString() + ";");
        } else {
            writer.write("\\Tree " + tree.toString() + ";");
        }
        writer.newLine();
    }

    /**
     * Prints a row of empty nodes as solution space for the contents of the array.
     * @param length The length of the array.
     * @param below The name of the left-most node in the row above the current row.
     * @param writer The writer to send the output to.
     * @return The name of the left-most node of the current row.
     * @throws IOException If some I/O error occurs.
     */
    private static String printEmptyArray(
        int length,
        String below,
        BufferedWriter writer
    ) throws IOException {
        String firstName = "n" + DSALExercises.number++;
        if (below == null) {
            writer.write("\\node[node] (");
            writer.write(firstName);
            writer.write(") {\\phantom{00}};");
            writer.newLine();
        } else {
            writer.write("\\node[node] (");
            writer.write(firstName);
            writer.write(") [below=of ");
            writer.write(below);
            writer.write("] {\\phantom{00}};");
            writer.newLine();
        }
        for (int i = 1; i < length; i++) {
            writer.write("\\node[node] (n" + DSALExercises.number++);
            writer.write(") [right=of n" + (DSALExercises.number - 2));
            writer.write("] {\\phantom{00}};");
            writer.newLine();
        }
        return firstName;
    }
	
	/**
     * Prints a colum of nodes with the contents of the array.
     * @param array The array of values.
     * @param separate An array indicating which nodes should be separated vertically. Must have a size exactly one
     *                 less than array or be null.
     * @param mark An array indicating which node should be marked by a grey background. Must have the same size as
     *             array or be null.
     * @param left The name of the top-most node in the colum left of the current colum.
     * @param writer The writer to send the output to.
     * @return The name of the left-most node of the current row.
     * @throws IOException If some I/O error occurs.
     */
	private static String printVerticalArray(
		Integer[] array,
		boolean[] separate,
		boolean[] mark,
		String left,
		BufferedWriter writer
	) throws IOException {
		String firstName = "n" + DSALExercises.number++;
		if( left == null )
		{
			writer.write("\\node[node");
            if (mark != null && mark[0]) {
                writer.write(",fill=black!20");
            }
            writer.write("] (");
            writer.write(firstName);
            int val = array[0];
            writer.write(") {" + (val < 10 ? "\\phantom{0}" : "") + val);
            writer.write("};");
            writer.newLine();
        } else {
            writer.write("\\node[node");
            if (mark != null && mark[0]) {
                writer.write(",fill=black!20");
            }
            writer.write("] (");
            writer.write(firstName);
            writer.write(") [right=of ");
            writer.write(left);
            int val = array[0];
            writer.write("] {" + (val < 10 ? "\\phantom{0}" : "") + val);
            writer.write("};");
            writer.newLine();
		}
		for (int i = 1; i < array.length; i++) {
            writer.write("\\node[node");
            if (mark != null && mark[i]) {
                writer.write(",fill=black!20");
            }
            writer.write("] (");
            writer.write("n" + DSALExercises.number++);
            writer.write(") [below=");
            if (separate != null && separate[i - 1]) {
                writer.write("0.1 ");
            }
            writer.write("of ");
            writer.write("n" + (DSALExercises.number - 2));
            int val = array[i];
            writer.write("] {" + (val < 10 ? "\\phantom{0}" : "") + val);
            writer.write("};");
            writer.newLine();
        }
		return firstName;
	}
	
	/**
     * Prints a colum of nodes with the contents of the array.
     * @param array The array of values.
     * @param separate An array indicating which nodes should be separated vertically. Must have a size exactly one
     *                 less than array or be null.
     * @param mark An array indicating which node should be marked by a grey background. Must have the same size as
     *             array or be null.
     * @param left The name of the top-most node in the colum left of the current colum.
     * @param writer The writer to send the output to.
     * @return The name of the left-most node of the current row.
     * @throws IOException If some I/O error occurs.
     */
	private static String printVerticalStringArray(
		String[] array,
		boolean[] separate,
		boolean[] mark,
		String left,
		BufferedWriter writer
		) throws IOException {
		String firstName = "n" + DSALExercises.number++;
		if( left == null )
		{
			writer.write("\\node[node");
            if (mark != null && mark[0]) {
                writer.write(",fill=black!20");
            }
            writer.write("] (");
            writer.write(firstName);
            String val = array[0];
            writer.write(") {" + val);
            writer.write("};");
            writer.newLine();
        } else {
            writer.write("\\node[node");
            if (mark != null && mark[0]) {
                writer.write(",fill=black!20");
            }
            writer.write("] (");
            writer.write(firstName);
            writer.write(") [right=of ");
            writer.write(left);
            String val = array[0];
            writer.write("] {" + val);
            writer.write("};");
            writer.newLine();
		}
		for (int i = 1; i < array.length; i++) {
            writer.write("\\node[node");
            if (mark != null && mark[i]) {
                writer.write(",fill=black!20");
            }
            writer.write("] (");
            writer.write("n" + DSALExercises.number++);
            writer.write(") [below=");
            if (separate != null && separate[i - 1]) {
                writer.write("0.1 ");
            }
            writer.write("of ");
            writer.write("n" + (DSALExercises.number - 2));
            String val = array[i];
            writer.write("] {" + val);
            writer.write("};");
            writer.newLine();
        }
		return firstName;
	}
	
	/**
     * Prints a colum of empty nodes as solution space for the contents of the array.
     * @param length The length of the array.
     * @param left The name of the top-most node in the colum left of the current colum.
     * @param writer The writer to send the output to.
     * @return The name of the top-most node of the current colum.
     * @throws IOException If some I/O error occurs.
     */
    private static String printEmptyVerticalArray(
		int length,
		String left,
		BufferedWriter writer
		) throws IOException {
        String firstName = "n" + DSALExercises.number++;
        if (left == null) {
            writer.write("\\node[node] (");
            writer.write(firstName);
            writer.write(") {\\phantom{00}};");
            writer.newLine();
        } else {
            writer.write("\\node[node] (");
            writer.write(firstName);
            writer.write(") [right=of ");
            writer.write(left);
            writer.write("] {\\phantom{00}};");
            writer.newLine();
        }
        for (int i = 1; i < length; i++) {
            writer.write("\\node[node] (n" + DSALExercises.number++);
            writer.write(") [below=of n" + (DSALExercises.number - 2));
            writer.write("] {\\phantom{00}};");
            writer.newLine();
        }
        return firstName;
    }

    /**
     * Prints the end of the specified environment.
     * @param environment The environment.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private static void printEnd(String environment, BufferedWriter writer) throws IOException {
        writer.write("\\end{" + environment + "}");
        writer.newLine();
    }

    /**
     * Prints a protected whitespace and a line terminator to the specified writer.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private static void printProtectedNewline(BufferedWriter writer) throws IOException {
        writer.write("~\\\\*\\vspace*{1ex}");
        writer.newLine();
    }
    
    /**
     * Prints the beginning of a samepage environment.
     * @param step The current evaluation step.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private static void printSamePageBeginning(int step, BufferedWriter writer) throws IOException {
        writer.write("\\begin{minipage}{\\columnwidth}");
        writer.newLine();
        writer.write("\\vspace*{1ex}");
        writer.newLine();
        writer.write("Schritt " + step + ":\\\\[-2ex]");
        writer.newLine();
        DSALExercises.printBeginning(DSALExercises.CENTER, writer);
    }
    
    /**
     * Prints the beginning of a samepage environment.
     * @param step The current evaluation step.
     * @param op The current operation.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private static void printSamePageBeginning(int step, Pair<Integer, Boolean> op, BufferedWriter writer)
    throws IOException {
        writer.write("\\begin{minipage}{\\columnwidth}");
        writer.newLine();
        writer.write("\\vspace*{2ex}");
        writer.newLine();
        writer.newLine();
        if (op.y) {
            writer.write("Schritt " + step + ": F\\\"uge " + op.x + " ein\\\\[-2ex]");
        } else {
            writer.write("Schritt " + step + ": L\\\"osche " + op.x + "\\\\[-2ex]");
        }
        writer.newLine();
        DSALExercises.printBeginning(DSALExercises.CENTER, writer);
    }

    /**
     * Prints the end of a samepage environment.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private static void printSamePageEnd(BufferedWriter writer) throws IOException {
        DSALExercises.printEnd(DSALExercises.CENTER, writer);
        writer.write("\\end{minipage}");
        writer.newLine();
    }

    /**
     * Prints the beginning of the TikZ picture environment to the specified writer, including style settings for 
     * arrays or trees.
     * @param style The style to use.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private static void printTikzBeginning(TikZStyle style, BufferedWriter writer) throws IOException {
        writer.write("\\begin{tikzpicture}");
        writer.newLine();
        writer.write(style.style);
        writer.newLine();
    }

    /**
     * Prints the end of the TikZ picture environment to the specified writer.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private static void printTikzEnd(BufferedWriter writer) throws IOException {
        writer.write("\\end{tikzpicture}");
        writer.newLine();
    }

    /**
     * Prints the specified array interpreted as binary tree up to the specified index.
     * @param array The array.
     * @param to The index to which the tree should be printed.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private static void printTree(Integer[] array, int to, BufferedWriter writer) throws IOException {
        if (to < 0) {
            return;
        }
        DSALExercises.printTikzBeginning(TikZStyle.TREE, writer);
        if (to > 0) {
            writer.write("\\Tree");
            DSALExercises.printTree(array, 0, to, writer);
        } else {
            writer.write("\\node[circle,draw=black,thick,inner sep=5pt] {" + array[0] + "};");
        }
        writer.newLine();
        DSALExercises.printTikzEnd(writer);
    }

    /**
     * Prints the specified array interpreted as binary tree from the specified start index (i.e., it prints the 
     * subtree starting with the element at the specified start index) to the specified end index.
     * @param array The array.
     * @param start The start index.
     * @param end The end index.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private static void printTree(Integer[] array, int start, int end, BufferedWriter writer) throws IOException {
        final int next = 2 * start + 1;
        if (next <= end) {
            writer.write(" [." + array[start]);
            DSALExercises.printTree(array, next, end, writer);
            if (next + 1 <= end) {
                DSALExercises.printTree(array, next + 1, end, writer);
            }
            writer.write(" ]");
        } else {
            writer.write(" " + array[start]);
        }
    }

    /**
     * Prints vertical space
     * @param step The next evaluation step.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private static void printVerticalSpace(int step, BufferedWriter writer) throws IOException {
        if (step % 3 == 0) {
            writer.newLine();
            writer.write("~\\\\");
            writer.newLine();
            writer.newLine();
        }
    }

    /**
     * Sorts the specified array using quicksort and outputs the solution as a TikZ picture to the specified writer.
     * @param array The array to sort.
     * @param writer The writer for outputting the solution.
     * @return The number of rows needed for the solution (excluding the original array).
     * @throws IOException If some error occurs while outputting the solution.
     */
    private static int quicksort(Integer[] array, BufferedWriter writer) throws IOException {
        DSALExercises.printTikzBeginning(TikZStyle.ARRAY, writer);
        boolean[] separate = new boolean[array.length - 1];
        boolean[] mark = new boolean[array.length];
        Arrays.fill(separate, false);
        Arrays.fill(mark, false);
        int res =
            (Integer)DSALExercises.quicksort(
                array,
                0,
                array.length - 1,
                DSALExercises.printArray(array, separate, mark, null, writer),
                separate,
                mark,
                writer
            )[0];
        DSALExercises.printTikzEnd(writer);
        return res;
    }

    /**
     * The actual quicksort algorithm. It sorts the array part from start to end using quicksort while outputting the 
     * solution as a TikZ picture to the specified writer.
     * @param array The array.
     * @param start The start index.
     * @param end The end index.
     * @param anchor The name of the left-most node of the recent row in the TikZ array output.
     * @param separate Indicates which nodes should be separated horizontally in the array output.
     * @param mark Indicates which nodes should be marked by a grey background in the TikZ array output (these are the 
     *             Pivot elements used).
     * @param writer The writer to send the output to.
     * @return The number of rows needed for the solution and the name of the left-most node of the most recent row in 
     *         the TikZ array output.
     * @throws IOException If some error occurs during solution output.
     */
    private static Object[] quicksort(
        Integer[] array,
        int start,
        int end,
        String anchor,
        boolean[] separate,
        boolean[] mark,
        BufferedWriter writer
    ) throws IOException {
        if (start < end) {
            int middle = DSALExercises.partition(array, start, end);
            if (middle > 0) {
                separate[middle - 1] = true;
            }
            if (middle < array.length - 1) {
                separate[middle] = true;
            }
            Arrays.fill(mark, false);
            mark[middle] = true;
            Object[] firstStep =
                DSALExercises.quicksort(
                    array,
                    start,
                    middle - 1,
                    DSALExercises.printArray(array, separate, mark, anchor, writer),
                    separate,
                    mark,
                    writer
                );
            Object[] secondStep =
                DSALExercises.quicksort(
                    array,
                    middle + 1,
                    end,
                    (String)firstStep[1],
                    separate,
                    mark,
                    writer
                );
            return new Object[]{((Integer)secondStep[0]) + ((Integer)firstStep[0]) + 1, secondStep[1]};
        } else {
            return new Object[]{0, anchor};
        }
    }

    /**
     * Sorts the specified array using selectionsort and outputs the solution as a TikZ picture to the specified writer.
     * @param array The array to sort.
     * @param writer The writer for outputting the solution.
     * @return The number of rows needed for the solution (excluding the original array).
     * @throws IOException If some error occurs while outputting the solution.
     */
    private static int selectionsort(Integer[] array, BufferedWriter writer)
    throws IOException {
        DSALExercises.printTikzBeginning(TikZStyle.ARRAY, writer);
        String anchor = DSALExercises.printArray(array, null, null, null, writer);
        int res = 0;
        for (int i = 0; i < array.length - 1; i++) {
            int min = i;
            for (int j = i + 1; j < array.length; j++) {
                if (array[j] < array[min]) {
                    min = j;
                }
            }
            if (i != min) {
                DSALExercises.swap(array, i, min);
                anchor = DSALExercises.printArray(array, null, null, anchor, writer);
                res++;
            }
        }
        DSALExercises.printTikzEnd(writer);
        return res;
    }

    /**
     * Swaps the array elements at indices a and b if both indices are greater than or equal to 0. If one of the 
     * indices is negative, this method does nothing. If otherwise one of the indices is bigger than the array length 
     * minus one, an ArrayOutOfBoundsException is thrown.
     * @param array The array.
     * @param a The first index.
     * @param b The second index.
     */
    private static void swap(Integer[] array, int a, int b) {
        if (a >= 0 && b >= 0) {
            int store = array[a];
            array[a] = array[b];
            array[b] = store;
        }
    }
	
	/**
	 * Checks for a given number if it is a prime.
	 * @param number The number to be checked.
	 * @return true, if the number is a prime, false otherwise.
	 */
	private static boolean isPrime(int number) {
        int sqrt = (int) Math.sqrt(number) + 1;
        for (int i = 2; i < sqrt; i++) {
            if (number % i == 0) {
                // number is perfectly divisible - no prime
                return false;
            }
        }
        return true;
    }
	
	private static boolean arePrime(int a, int b, int c)
	{
		return DSALExercises.gcd(a,b) == 1 && DSALExercises.gcd(b,c) == 1 && DSALExercises.gcd(a,c);
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
        return gcd(number2, number1%number2);
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
    
    /**
     * Styles for TikZ environments.
     * @author cryingshadow
     * @version $Id$
     */
    private static enum TikZStyle {
        
        /**
         * Array style.
         */
        ARRAY("[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]"),
		
		/**
		 * Borderless Array style (for String-Arrays).
		 */
		BORDERLESS("[node/.style={draw=none,thick,inner sep=5pt, text width = 10cm}, node distance=0.25 and 0]"),
        
        /**
         * B-tree style.
         */
        BTREE(
            "[every tree node/.style={rounded corners,draw=black,thick,inner sep=5pt}, "
            + "sibling distance=10pt, level distance=30pt, edge from parent/.style="
            + "{draw, edge from parent path={(\\tikzparentnode) -- (\\tikzchildnode)}}]"
        ),
        
        /**
         * Tree style.
         */
        TREE(
            "[every tree node/.style={circle,draw=black,thick,inner sep=5pt}, "
            + "sibling distance=10pt, level distance=30pt, edge from parent/.style="
            + "{draw, edge from parent path={(\\tikzparentnode) -- (\\tikzchildnode)}}]"
        );

        /**
         * The style definition.
         */
        private final String style;
        
        /**
         * Creates a TikZStyle with the specified style definition.
         * @param s The style definition.
         */
        private TikZStyle(String s) {
            this.style = s;
        }
    }
    
}
