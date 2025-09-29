package exercisegenerator;

import java.io.*;
import java.util.*;

import com.google.gson.*;

import clit.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.view.*;

public class Main {

    public static final String EMBEDDED;

    public static final String EMBEDDED_EXAM;

    public static final Gson GSON;

    public static String lineSeparator;

    public static final int NUMBER_LIMIT;

    public static final Random RANDOM;

    public static final String STANDALONE;

    public static final TextVersion TEXT_VERSION;

    private static final String[] HELP;

    private static final String VERSION = "5.1.7";

    static {
        RANDOM = new Random();
        GSON = new Gson();
        NUMBER_LIMIT = 100;
        EMBEDDED = "embedded";
        EMBEDDED_EXAM = "embeddedExam";
        STANDALONE = "standalone";
        TEXT_VERSION = TextVersion.GENERAL;
        HELP = Main.initHelpText();
        Main.lineSeparator = System.lineSeparator();
    }

    public static String algorithmNames() {
        return String.join(
            ", ",
            Algorithm.getEnabledAlgorithms()
            .stream()
            .map(alg -> alg.name)
            .toList()
        );
    }

    public static Process buildAndStartPDFLaTeXProcess(final String fileName, final File directory) throws IOException {
        return new ProcessBuilder(
            "pdflatex",
            fileName,
            "-interaction=nonstopmode",
            "-halt-on-error"
        ).inheritIO().directory(directory).start();
    }

    public static boolean embeddedExam(final Parameters<Flag> options) {
        return Main.EMBEDDED_EXAM.equals(options.get(Flag.EXECUTION_MODE));
    }

    public static void main(final Parameters<Flag> options) throws Exception {
        if (options.containsKey(Flag.WINDOWS)) {
            final boolean useWindowsLineSeparators = Boolean.parseBoolean(options.get(Flag.WINDOWS));
            Main.lineSeparator = useWindowsLineSeparators ? "\r\n" : "\n";
        }
        try (
            BufferedWriter solutionWriter = Main.getSolutionWriter(options);
            BufferedWriter exerciseWriter = Main.getExerciseWriter(options);
        ) {
            final boolean standalone = Main.standalone(options);
            final boolean multipleExercises = options.containsKey(Flag.NUMBER);
            if (standalone) {
                Main.printLaTeXBeginning(!multipleExercises, exerciseWriter, solutionWriter);
            }
            if (multipleExercises) {
                final List<Algorithm> algorithms = Main.parseAlgorithms(options.get(Flag.ALGORITHM));
                if (algorithms.isEmpty()) {
                    throw new Exception("No known algorithm has been specified!");
                }
                final int numberOfExercises = Integer.parseInt(options.get(Flag.NUMBER));
                for (int i = 0; i < numberOfExercises; i++) {
                    final Algorithm algorithm = algorithms.get(Main.RANDOM.nextInt(algorithms.size()));
                    final Parameters<Flag> singleAlgorithmOptions =
                        Main.parseFlags(
                            Main.toCLIArguments(algorithm, algorithm.implementation.generateTestParameters(), options)
                        );
                    exerciseWriter.write(String.format("{\\large Aufgabe %d}\\\\[3ex]", i + 1));
                    Main.newLine(exerciseWriter);
                    Main.newLine(exerciseWriter);
                    solutionWriter.write(String.format("{\\large L\\\"osung %d}\\\\[3ex]", i + 1));
                    Main.newLine(solutionWriter);
                    Main.newLine(solutionWriter);
                    algorithm.implementation.executeAlgorithm(
                        new AlgorithmInput(exerciseWriter, solutionWriter, singleAlgorithmOptions)
                    );
                    Main.newLine(exerciseWriter);
                    exerciseWriter.write("\\pagebreak");
                    Main.newLine(exerciseWriter);
                    Main.newLine(exerciseWriter);
                    Main.newLine(solutionWriter);
                    solutionWriter.write("\\pagebreak");
                    Main.newLine(solutionWriter);
                    Main.newLine(solutionWriter);
                }
            } else {
                final Optional<Algorithm> algorithm = Algorithm.forName(options.get(Flag.ALGORITHM));
                if (algorithm.isEmpty()) {
                    System.out.println(String.format("Unknown algorithm (%s)!", options.get(Flag.ALGORITHM)));
                    return;
                }
                algorithm.get().implementation.executeAlgorithm(
                    new AlgorithmInput(exerciseWriter, solutionWriter, options)
                );
            }
            if (standalone) {
                LaTeXUtils.printLaTeXEnd(exerciseWriter);
                LaTeXUtils.printLaTeXEnd(solutionWriter);
            }
        }
    }

    public static void main(final String[] args) throws IOException {
        if (args == null || args.length < 1) {
            new MainFrame(Main.VERSION).setVisible(true);
            return;
        }
        if (Main.isHelpMode(args)) {
            Main.showHelp(args);
            return;
        }
        final boolean even = args.length % 2 == 0;
        if (!even) {
            System.out.println("The number of arguments must be even (flag/value pairs)!");
            return;
        }
        final boolean isBatchMode = Main.isBatchMode(args);
        final List<String[]> batchedArgs = isBatchMode ? Main.parseBatchArgs(args) : Collections.singletonList(args);
        for (final String[] batch : batchedArgs) {
            try {
                Main.main(Main.parseFlags(batch));
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void newLine(final BufferedWriter writer) throws IOException {
        writer.write(Main.lineSeparator);
    }

    public static boolean standalone(final Parameters<Flag> options) {
        return options.getOrDefault(Flag.EXECUTION_MODE, Main.STANDALONE).equals(Main.STANDALONE);
    }

    private static BufferedWriter getExerciseWriter(final Parameters<Flag> options) throws IOException {
        return Main.getWriterByFlag(options, Flag.EXERCISE);
    }

    private static BufferedWriter getSolutionWriter(final Parameters<Flag> options) throws IOException {
        return Main.getWriterByFlag(options, Flag.TARGET);
    }

    private static BufferedWriter getWriterByFlag(final Parameters<Flag> options, final Flag flag) throws IOException {
        return
            new BufferedWriter(
                options.containsKey(flag) ?
                    new FileWriter(options.get(flag)) :
                        new OutputStreamWriter(System.out)
            );
    }

    private static String[] initHelpText() {
        final List<String> text = new ArrayList<String>();
        text.add(String.format("This is ExerciseCreator version %s.", Main.VERSION));
        text.add(
            "Please read the license contained in this JAR file. By using this software, you agree to this license."
        );
        text.add(
            "You can create exercises and solutions automatically using the following flags, where each flag needs to "
            + "be followed by exactly one argument:"
        );
        for (final Flag flag : Flag.values()) {
            text.add("");
            text.add("-" + flag.shortName);
            text.add(flag.docu);
        }
        String[] res = new String[text.size()];
        res = text.toArray(res);
        return res;
    }

    private static boolean isBatchMode(final String[] args) {
        return "-b".equals(args[0]);
    }

    private static boolean isHelpMode(final String[] args) {
        return "-h".equals(args[0]);
    }

    private static List<Algorithm> parseAlgorithms(final String text) {
        if (text == null) {
            return Collections.emptyList();
        }
        final List<Algorithm> result = new ArrayList<Algorithm>();
        final String[] algorithms = text.split(",");
        for (final String algorithmName : algorithms) {
            final Optional<Algorithm> algorithm = Algorithm.forName(algorithmName.strip());
            if (algorithm.isPresent()) {
                result.add(algorithm.get());
            }
        }
        return result;
    }

    private static List<String[]> parseBatchArgs(final String[] args) throws IOException {
        final List<String[]> result = new LinkedList<String[]>();
        try (BufferedReader reader = new BufferedReader(new StringReader(args[1]))) {
            String batch = reader.readLine();
            while (batch != null) {
                if (!batch.isBlank()) {
                    final String[] batchedArgs =
                        Arrays.stream(batch.split(" ")).filter(s -> !s.isBlank()).toArray(String[]::new);
                    if (batchedArgs.length % 2 != 0) {
                        throw new IllegalArgumentException("The number of arguments must be even (flag/value pairs)!");
                    }
                    result.add(batchedArgs);
                }
                batch = reader.readLine();
            }
        }
        return result;
    }

    private static Parameters<Flag> parseFlags(final String[] args) throws Exception {
        final Parameters<Flag> res = new CLITamer<Flag>(Flag.class).parse(args);
        if (!res.containsKey(Flag.ALGORITHM)) {
            throw new Exception("No algorithm specified!");
        }
        if (res.containsKey(Flag.INPUT) && res.containsKey(Flag.SOURCE)) {
            throw new Exception("Input must not be specified by a file and a string together!");
        }
        if (res.containsKey(Flag.NUMBER)) {
            if (
                !List.of(Flag.NUMBER, Flag.ALGORITHM, Flag.EXERCISE, Flag.TARGET, Flag.WINDOWS)
                .containsAll(res.keySet())
            ) {
                throw new Exception("Number is only compatible with flags a, e, t, and w!");
            }
            if (!res.containsKey(Flag.TARGET) || !res.containsKey(Flag.EXERCISE)) {
                throw new Exception("Both exercise and solution files must be specified for multiple exercises!");
            }
        }
        if (!res.containsKey(Flag.TARGET) && !res.containsKey(Flag.EXERCISE)) {
            throw new Exception(
                "Cannot output both exercise and solution on stdout! Please specify a file for at least one of them."
            );
        }
        return res;
    }

    private static void printLaTeXBeginning(
        final boolean singleExercise,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) throws IOException {
        LaTeXUtils.printLaTeXBeginning(exerciseWriter);
        LaTeXUtils.printLaTeXBeginning(solutionWriter);
        if (singleExercise) {
            exerciseWriter.write("{\\large Aufgabe}\\\\[3ex]");
            Main.newLine(exerciseWriter);
            Main.newLine(exerciseWriter);
            solutionWriter.write("{\\large L\\\"osung}\\\\[3ex]");
            Main.newLine(solutionWriter);
            Main.newLine(solutionWriter);
        }
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

    private static String[] toCLIArguments(
        final Algorithm alg,
        final String[] generatedOptions,
        final Parameters<Flag> options
    ) {
        final int numOfAddedParameters = 6;
        final String[] result = new String[generatedOptions.length + numOfAddedParameters];
        result[0] = "-a";
        result[1] = alg.name;
        result[2] = "-e";
        result[3] = options.get(Flag.EXERCISE);
        result[4] = "-t";
        result[5] = options.get(Flag.TARGET);
        System.arraycopy(generatedOptions, 0, result, numOfAddedParameters, generatedOptions.length);
        return result;
    }

}
