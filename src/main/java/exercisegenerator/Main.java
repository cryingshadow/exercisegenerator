package exercisegenerator;

import java.io.*;
import java.util.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;

/**
 * Programm for creating exercises and solutions for algorithmic problems.
 * @author Thomas Stroeder, Florian Corzilius, Stefan Schupp
 * @version {@value #VERSION}
 */
public class Main {

    /**
     * Limit for random numbers in student mode.
     */
    public static final int NUMBER_LIMIT;

    /**
     * Flag used to turn off some options for the student version.
     */
    public static final boolean STUDENT_MODE;

    /**
     * Text version.
     */
    public static final TextVersion TEXT_VERSION;

    /**
     * The help text displayed when just called with -h. Each entry is separated by a newline.
     */
    private static final String[] HELP;

    /**
     * The version of this program.
     */
    private static final String VERSION = "1.2.0";

    static {
        NUMBER_LIMIT = 100;
        STUDENT_MODE = false;
        TEXT_VERSION = TextVersion.GENERAL;
        HELP = Main.initHelpText();
    }

    /**
     * @return The names of the supported algorithms, separated by commas.
     */
    public static String algorithmNames() {
        return String.join(
            ", ",
            Arrays.stream(Algorithm.values())
                .filter(alg -> (!Main.STUDENT_MODE || alg.enabled))
                .map(alg -> alg.name)
                .toList()
        );
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
     *             -a : The algorithm to apply to the input. Must be specified.<br>
     *             -v : The variant of the algorithm.<br>
     *             -l : Additional lines for pre-prints. Defaults to 0 if not set. Ignored if -p is not set.<br>
     *             -d : Degree (e.g., of a B-tree).<br>
     *             -o : File containing operations used to construct a start structure.
     */
    public static void main(final String[] args) {
        if (args == null || args.length < 1) {
            System.out.println("You need to provide arguments! Type -h for help.");
            return;
        }
        if (Main.isHelpMode(args)) {
            Main.showHelp(args);
            return;
        }
        if (args.length % 2 != 0) {
            System.out.println("The number of arguments must be even (flag/value pairs)!");
            return;
        }
        final Map<Flag, String> options;
        try {
            options = Main.parseFlags(args);
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            return;
        }
        try (
            BufferedWriter solutionWriter = Main.getSolutionWriter(options);
            BufferedWriter exerciseWriter = Main.getExerciseWriter(options);
        ) {
            if (Main.STUDENT_MODE) {
                Main.printLaTeXBeginning(exerciseWriter, solutionWriter);
            }
            final Optional<Algorithm> algorithm = Algorithm.forName(options.get(Flag.ALGORITHM));
            if (algorithm.isEmpty()) {
                System.out.println("Unknown algorithm!");
                return;
            }
            algorithm.get().algorithm.accept(new AlgorithmInput(exerciseWriter, solutionWriter, options));
            if (Main.STUDENT_MODE) {
                TikZUtils.printLaTeXEnd(exerciseWriter);
                TikZUtils.printLaTeXEnd(solutionWriter);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private static BufferedWriter getExerciseWriter(final Map<Flag, String> options) throws IOException {
        return Main.getWriterByFlag(options, Flag.EXERCISE);
    }

    private static BufferedWriter getSolutionWriter(final Map<Flag, String> options) throws IOException {
        return Main.getWriterByFlag(options, Flag.TARGET);
    }

    private static BufferedWriter getWriterByFlag(final Map<Flag, String> options, final Flag flag) throws IOException {
        return
            new BufferedWriter(
                options.containsKey(flag) ?
                    new FileWriter(options.get(flag)) :
                        new OutputStreamWriter(System.out)
            );
    }

    /**
     * @return The general help text as String array.
     */
    private static String[] initHelpText() {
        final List<String> text = new ArrayList<String>();
        text.add(
            "This is ExerciseCreator version "
            + Main.VERSION
            + (Main.STUDENT_MODE ? " (student)." : ".")
        );
        text.add(
            "Please read the license contained in this JAR file. By using this software, you agree to this license."
        );
        text.add(
            "You can create exercises and solutions automatically using the following flags, where each flag needs to "
            + "be followed by exactly one argument:"
        );
        for (final Flag flag : Flag.values()) {
            if (!Main.STUDENT_MODE || flag.forStudents) {
                text.add("");
                text.add(flag.shortName);
                text.add(flag.docu);
            }
        }
        String[] res = new String[text.size()];
        res = text.toArray(res);
        return res;
    }

    private static boolean isHelpMode(final String[] args) {
        return "-h".equals(args[0]);
    }

    /**
     * @param args The program arguments.
     * @return A map from Flags to their values parsed from the program arguments.
     * @throws Exception If the program arguments are not of the desired form.
     */
    private static Map<Flag, String> parseFlags(final String[] args) throws Exception {
        final Map<Flag, String> res = new LinkedHashMap<Flag, String>();
        outer: for (int i = 0; i < args.length - 1; i += 2) {
            final String option = args[i];
            for (final Flag flag : Flag.values()) {
                if (Main.STUDENT_MODE) {
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
        if (!Main.STUDENT_MODE && !res.containsKey(Flag.SOURCE) && !res.containsKey(Flag.INPUT)) {
            throw new Exception("No input specified!");
        }
        if (!res.containsKey(Flag.TARGET) && !res.containsKey(Flag.EXERCISE)) {
            throw new Exception(
                "Cannot output both exercise and solution on stdout! Please specify a file for at least one of them."
            );
        }
        return res;
    }

    private static void printLaTeXBeginning(final BufferedWriter exerciseWriter, final BufferedWriter solutionWriter)
    throws IOException {
        TikZUtils.printLaTeXBeginning(exerciseWriter);
        exerciseWriter.write("{\\large Aufgabe}\\\\[3ex]");
        exerciseWriter.newLine();
        exerciseWriter.newLine();
        TikZUtils.printLaTeXBeginning(solutionWriter);
        solutionWriter.write("{\\large L\\\"osung}\\\\[3ex]");
        solutionWriter.newLine();
        solutionWriter.newLine();
    }

    private static void showHelp(final String[] args) {
        if (args.length == 1) {
            for (final String text : Main.HELP) {
                System.out.println(text);
            }
        } else if (args.length > 2) {
            System.out.println("You can only ask for help on one algorithm at a time!");
        } else {
            final Optional<Algorithm> alg = Algorithm.forName(args[1]);
            if (alg.isEmpty()) {
                System.out.println("Could not find any help for the algorithm you specified!");
            } else {
                for (final String text : alg.get().documentation) {
                    System.out.println(text);
                }
            }
        }
    }

}
