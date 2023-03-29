package exercisegenerator.algorithms;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.util.*;

/**
 * This class provides methods for hashing.
 */
public abstract class Hashing {

    private static class HashResultWithProbingFactors {
        private final int linearProbingFactor;
        private final int quadraticProbingFactor;
        private final HashList[] result;

        private HashResultWithProbingFactors(
            final HashList[] result,
            final int linearProbingFactor,
            final int quadraticProbingFactor
        ) {
            this.result = result;
            this.linearProbingFactor = linearProbingFactor;
            this.quadraticProbingFactor = quadraticProbingFactor;
        }
    }

    private static class PrintOptions {
        private final String optionsText;
        private final String parameterText;
        private final PreprintMode preprintMode;
        private final boolean probing;

        private PrintOptions(
            final String optionsText,
            final String parameterText,
            final boolean probing,
            final PreprintMode preprintMode
        ) {
            this.optionsText = optionsText;
            this.parameterText = parameterText;
            this.probing = probing;
            this.preprintMode = preprintMode;
        }
    }

    private static final String DIVISION_METHOD = "\\emphasize{Divisionsmethode}";

    private static final String GENERAL_HASHING_EXERCISE_TEXT_END = " ein";

    private static final String LINEAR_PROBING = " mit \\emphasize{linearer Sondierung}";

    private static final String NO_PROBING = " \\emphasize{ohne Sondierung} (also durch Verkettung)";

    private static final Object PARAMETER_STRING_END = ":\\\\[2ex]";

    /**
     * Array containing all prime numbers between 5 and 101.
     */
    private static final int[] PRIMES_5_101 =
        new int[]{5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101};

    public static HashList[] createEmptyArray(final int length) {
        final HashList[] result = new HashList[length];
        for (int i = 0; i < length; i++) {
            result[i] = new HashList();
        }
        return result;
    }

    public static String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    public static void hashDiv(final AlgorithmInput input) throws IOException, HashException {
        final List<Integer> values = Hashing.parseOrGenerateValues(input.options);
        final HashList[] initialHashTable = Hashing.parseOrGenerateInitialArray(values.size(), input.options);
        final HashList[] result = Hashing.hashingWithDivisionMethod(values, initialHashTable, Optional.empty());
        Hashing.printHashingExerciseAndSolution(
            values,
            initialHashTable,
            result,
            new PrintOptions(
                Hashing.DIVISION_METHOD
                .concat(Hashing.NO_PROBING)
                .concat(Hashing.GENERAL_HASHING_EXERCISE_TEXT_END)
                .concat(Hashing.toAdditionalHint(initialHashTable.length)),
                Hashing.toParameterString(initialHashTable.length),
                false,
                PreprintMode.parsePreprintMode(input.options)
            ),
            input.options,
            input.exerciseWriter,
            input.solutionWriter
        );
    }

    public static void hashDivLin(final AlgorithmInput input) throws IOException, HashException {
        final List<Integer> values = Hashing.parseOrGenerateValues(input.options);
        final HashList[] initialHashTable = Hashing.parseOrGenerateInitialArray(values.size(), input.options);
        final HashList[] result =
            Hashing.hashingWithDivisionMethod(values, initialHashTable, Optional.of(Hashing.linearProbing()));
        Hashing.printHashingExerciseAndSolution(
            values,
            initialHashTable,
            result,
            new PrintOptions(
                Hashing.DIVISION_METHOD
                .concat(Hashing.LINEAR_PROBING)
                .concat(Hashing.GENERAL_HASHING_EXERCISE_TEXT_END)
                .concat(Hashing.toAdditionalHint(initialHashTable.length, initialHashTable.length)),
                Hashing.toParameterString(initialHashTable.length),
                true,
                PreprintMode.parsePreprintMode(input.options)
            ),
            input.options,
            input.exerciseWriter,
            input.solutionWriter
        );
    }

    public static void hashDivQuad(final AlgorithmInput input) throws IOException, HashException {
        final List<Integer> values = Hashing.parseOrGenerateValues(input.options);
        final HashList[] initialHashTable = Hashing.parseOrGenerateInitialArray(values.size(), input.options);
        final HashResultWithProbingFactors resultWithProbingFactors =
            Hashing.parseOrGenerateProbingFactorsAndComputeResult(
                initialHashTable.length,
                (linearProbingFactor, quadraticProbingFactor) ->
                    Hashing.hashingWithDivisionMethod(
                        values,
                        initialHashTable,
                        Optional.of(Hashing.quadraticProbing(linearProbingFactor, quadraticProbingFactor))
                    ),
                input.options
            );
        Hashing.printHashingExerciseAndSolution(
            values,
            initialHashTable,
            resultWithProbingFactors.result,
            new PrintOptions(
                Hashing.DIVISION_METHOD
                .concat(
                    Hashing.toQuadraticProbingText(
                        resultWithProbingFactors.linearProbingFactor,
                        resultWithProbingFactors.quadraticProbingFactor
                    )
                ).concat(Hashing.GENERAL_HASHING_EXERCISE_TEXT_END)
                .concat(
                    Hashing.toAdditionalHint(
                        initialHashTable.length,
                        resultWithProbingFactors.linearProbingFactor,
                        resultWithProbingFactors.quadraticProbingFactor,
                        initialHashTable.length
                    )
                ),
                Hashing.toParameterString(
                    initialHashTable.length,
                    resultWithProbingFactors.linearProbingFactor,
                    resultWithProbingFactors.quadraticProbingFactor
                ),
                true,
                PreprintMode.parsePreprintMode(input.options)
            ),
            input.options,
            input.exerciseWriter,
            input.solutionWriter
        );
    }

    public static HashList[] hashingWithDivisionMethod(
        final List<Integer> values,
        final HashList[] initialHashTable,
        final Optional<ProbingFunction> optionalProbingFunction
    ) throws HashException {
        return Hashing.hashing(
            values,
            initialHashTable,
            (value, capacity) -> value % capacity,
            optionalProbingFunction
        );
    }

    public static HashList[] hashingWithMultiplicationMethod(
        final List<Integer> values,
        final HashList[] initialHashTable,
        final double factor,
        final Optional<ProbingFunction> optionalProbingFunction
    ) throws HashException {
        return Hashing.hashing(
            values,
            initialHashTable,
            (value, capacity) -> (int)Math.floor(capacity * ((value * factor) - Math.floor(value * factor))),
            optionalProbingFunction
        );
    }

    public static void hashMult(final AlgorithmInput input) throws IOException, HashException {
        final List<Integer> values = Hashing.parseOrGenerateValues(input.options);
        final HashList[] initialHashTable = Hashing.parseOrGenerateInitialArray(values.size(), input.options);
        final double factor = Hashing.parseOrGenerateMultiplicationFactor(input.options);
        final HashList[] result =
            Hashing.hashingWithMultiplicationMethod(values, initialHashTable, factor, Optional.empty());
        Hashing.printHashingExerciseAndSolution(
            values,
            initialHashTable,
            result,
            new PrintOptions(
                Hashing.toMultiplicationMethodExerciseText(factor)
                .concat(Hashing.NO_PROBING)
                .concat(Hashing.GENERAL_HASHING_EXERCISE_TEXT_END)
                .concat(Hashing.toAdditionalHint(initialHashTable.length, factor)),
                Hashing.toParameterString(initialHashTable.length, factor),
                false,
                PreprintMode.parsePreprintMode(input.options)
            ),
            input.options,
            input.exerciseWriter,
            input.solutionWriter
        );
    }

    public static void hashMultLin(final AlgorithmInput input) throws IOException, HashException {
        final List<Integer> values = Hashing.parseOrGenerateValues(input.options);
        final HashList[] initialHashTable = Hashing.parseOrGenerateInitialArray(values.size(), input.options);
        final double factor = Hashing.parseOrGenerateMultiplicationFactor(input.options);
        final HashList[] result =
            Hashing.hashingWithMultiplicationMethod(
                values,
                initialHashTable,
                factor,
                Optional.of(Hashing.linearProbing())
            );
        Hashing.printHashingExerciseAndSolution(
            values,
            initialHashTable,
            result,
            new PrintOptions(
                Hashing.toMultiplicationMethodExerciseText(factor)
                .concat(Hashing.LINEAR_PROBING)
                .concat(Hashing.GENERAL_HASHING_EXERCISE_TEXT_END)
                .concat(Hashing.toAdditionalHint(initialHashTable.length, factor, initialHashTable.length)),
                Hashing.toParameterString(initialHashTable.length, factor),
                true,
                PreprintMode.parsePreprintMode(input.options)
            ),
            input.options,
            input.exerciseWriter,
            input.solutionWriter
        );
    }

    public static void hashMultQuad(final AlgorithmInput input) throws IOException, HashException {
        final List<Integer> values = Hashing.parseOrGenerateValues(input.options);
        final HashList[] initialHashTable = Hashing.parseOrGenerateInitialArray(values.size(), input.options);
        final double factor = Hashing.parseOrGenerateMultiplicationFactor(input.options);
        final HashResultWithProbingFactors resultWithProbingFactors =
            Hashing.parseOrGenerateProbingFactorsAndComputeResult(
                initialHashTable.length,
                (linearProbingFactor, quadraticProbingFactor) ->
                    Hashing.hashingWithMultiplicationMethod(
                        values,
                        initialHashTable,
                        factor,
                        Optional.of(Hashing.quadraticProbing(linearProbingFactor, quadraticProbingFactor))
                    ),
                input.options
            );
        Hashing.printHashingExerciseAndSolution(
            values,
            initialHashTable,
            resultWithProbingFactors.result,
            new PrintOptions(
                Hashing.toMultiplicationMethodExerciseText(factor)
                .concat(
                    Hashing.toQuadraticProbingText(
                        resultWithProbingFactors.linearProbingFactor,
                        resultWithProbingFactors.quadraticProbingFactor
                    )
                ).concat(Hashing.GENERAL_HASHING_EXERCISE_TEXT_END)
                .concat(
                    Hashing.toAdditionalHint(
                        initialHashTable.length,
                        factor,
                        resultWithProbingFactors.linearProbingFactor,
                        resultWithProbingFactors.quadraticProbingFactor
                    )
                ),
                Hashing.toParameterString(
                    initialHashTable.length,
                    factor,
                    resultWithProbingFactors.linearProbingFactor,
                    resultWithProbingFactors.quadraticProbingFactor
                ),
                true,
                PreprintMode.parsePreprintMode(input.options)
            ),
            input.options,
            input.exerciseWriter,
            input.solutionWriter
        );
    }

    public static ProbingFunction linearProbing() {
        return (value, initialPosition, numberOfCollisions, capacity) -> {
            if (numberOfCollisions > capacity) {
                throw new HashException(String.format("The array is too small: Insertion of %d failed!", value));
            }
            return (initialPosition + numberOfCollisions) % capacity;
        };
    }

    public static ProbingFunction quadraticProbing(final int linearFactor, final int quadraticFactor) {
        return (value, initialPosition, numberOfCollisions, capacity) -> {
            if (numberOfCollisions >= capacity) {
                throw new HashException(
                    String.format(
                        "The array is too small or the probing constants are chosen badly: Insertion of %d failed!",
                        value
                    )
                );
            }
            return
                (
                    initialPosition
                    + linearFactor * numberOfCollisions
                    + quadraticFactor * numberOfCollisions * numberOfCollisions
                ) % capacity;
        };
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

    private static int computeContentLength(final HashList[] array) {
        return Arrays.stream(array)
            .mapToInt(list -> list.stream().mapToInt(x -> String.valueOf(x).length()).max().orElse(1))
            .max()
            .orElse(1);
    }

    private static Pair<Integer, Integer> computeCoprimeProbingFactors(final int capacity, final Random gen) {
        int coprimeLinearProbingFactor = Hashing.getRandomProbingFactor(capacity, gen);
        int coprimeQuadraticProbingFactor = Hashing.getRandomProbingFactor(capacity, gen);
        while (!Hashing.areCoprime(capacity, coprimeLinearProbingFactor, coprimeQuadraticProbingFactor)) {
            coprimeLinearProbingFactor = Hashing.getRandomProbingFactor(capacity, gen);
            if (Hashing.areCoprime(capacity, coprimeLinearProbingFactor, coprimeQuadraticProbingFactor)) {
                break;
            }
            coprimeQuadraticProbingFactor = Hashing.getRandomProbingFactor(capacity, gen);
        }
        return new Pair<Integer, Integer>(coprimeLinearProbingFactor, coprimeQuadraticProbingFactor);
    }

    private static void copyValues(final HashList[] initialHashTable, final HashList[] hashTable) {
        for (int i = 0; i < hashTable.length; i++) {
            hashTable[i].addAll(initialHashTable[i]);
        }
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

    private static int generateCapacity(final int numberOfValues, final Parameters options) {
        final Random gen = new Random();
        final int length = (int)(numberOfValues * 1.25);
        final String alg = options.get(Flag.ALGORITHM);
        if (alg == "hashDivision" || alg == "hashMultiplication") {
            final Integer[] primes = Hashing.getAllUpToNextPrimes(length);
            final int index = gen.nextInt(primes.length);
            return primes[index];
        } else {
            int result = Hashing.getNextPrime(length);
            while (gen.nextBoolean()) {
                result = Hashing.getNextPrime(result + 1);
            }
            return result;
        }
    }

    private static HashResultWithProbingFactors generateProbingFactorsAndComputeResult(
        final int capacity,
        final CheckedBiFunction<Integer, Integer, HashList[], HashException> hashingAlgorithm,
        final Parameters options
    ) {
        final Random gen = new Random();
        HashList[] result = null;
        int linearProbingFactor = 0;
        int quadraticProbingFactor = 0;
        boolean failed = false;
        do {
            try {
                final Pair<Integer, Integer> coprimeProbingFactors =
                    Hashing.computeCoprimeProbingFactors(capacity, gen);
                linearProbingFactor = coprimeProbingFactors.x;
                quadraticProbingFactor = coprimeProbingFactors.y;
                result = hashingAlgorithm.apply(linearProbingFactor, quadraticProbingFactor);
                failed = false;
            } catch (final HashException e) {
                failed = true;
            }
        } while (failed);
        return new HashResultWithProbingFactors(result, linearProbingFactor, quadraticProbingFactor);
    }

    private static List<Integer> generateValues(final Parameters options) {
        final Random gen = new Random();
        final int length =
            options.containsKey(Flag.LENGTH) ? Integer.parseInt(options.get(Flag.LENGTH)) : gen.nextInt(16) + 5;
        return Stream.generate(() -> gen.nextInt(Main.NUMBER_LIMIT)).limit(length).toList();
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

    private static double getRandomFactorBetweenZeroAndOne(final Random gen) {
        final double result = Math.round((gen.nextDouble()) * 100.0) / 100.0;
        if (Double.compare(result, 0.0) == 0) {
            return 0.01;
        }
        return result;
    }

    private static int getRandomProbingFactor(final int capacity, final Random gen) {
        return gen.nextInt(capacity - 1) + 1;
    }

    private static HashList[] hashing(
        final List<Integer> values,
        final HashList[] initialHashTable,
        final BiFunction<Integer, Integer, Integer> hashFunction,
        final Optional<ProbingFunction> optionalProbingFunction
    ) throws HashException {
        final int capacity = initialHashTable.length;
        final HashList[] hashTable = Hashing.createEmptyArray(capacity);
        Hashing.copyValues(initialHashTable, hashTable);
        if (optionalProbingFunction.isPresent()) {
            final ProbingFunction probingFunction = optionalProbingFunction.get();
            for (final Integer value : values) {
                final int initialPos = hashFunction.apply(value, capacity);
                int pos = initialPos;
                int numberOfCollisions = 0;
                while (!hashTable[pos].isEmpty()) {
                    pos = probingFunction.apply(value, initialPos, ++numberOfCollisions, capacity);
                }
                hashTable[pos].add(value);
            }
        } else {
            for (final Integer value : values) {
                hashTable[hashFunction.apply(value, capacity)].add(value);
            }
        }
        return hashTable;
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

    private static int parseCapacity(final BufferedReader reader, final Parameters options)
    throws NumberFormatException, IOException {
        return Integer.parseInt(reader.readLine().split(",")[0]);
    }

    private static double parseMultiplicationFactor(final BufferedReader reader, final Parameters options)
    throws NumberFormatException, IOException {
        return Double.parseDouble(reader.readLine().split(",")[1]);
    }

    private static int parseOrGenerateCapacity(final int numberOfValues, final Parameters options)
    throws IOException {
        return new ParserAndGenerator<Integer>(
            Hashing::parseCapacity,
            flags -> Hashing.generateCapacity(numberOfValues, flags)
        ).getResult(options);
    }

    private static HashList[] parseOrGenerateInitialArray(final int numberOfValues, final Parameters options)
    throws IOException {
        if (!options.containsKey(Flag.OPERATIONS)) {
            return Hashing.createEmptyArray(Hashing.parseOrGenerateCapacity(numberOfValues, options));
        }
        return Arrays.stream(options.get(Flag.OPERATIONS).split(";"))
            .map(
                text -> Arrays.stream(text.split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toCollection(HashList::new))
            ).toArray(HashList[]::new);
    }

    private static double parseOrGenerateMultiplicationFactor(final Parameters options) throws IOException {
        return new ParserAndGenerator<Double>(
            Hashing::parseMultiplicationFactor,
            flags -> Hashing.getRandomFactorBetweenZeroAndOne(new Random())
        ).getResult(options);
    }

    private static HashResultWithProbingFactors parseOrGenerateProbingFactorsAndComputeResult(
        final int capacity,
        final CheckedBiFunction<Integer, Integer, HashList[], HashException> hashingAlgorithm,
        final Parameters options
    ) throws IOException {
        return new ParserAndGenerator<HashResultWithProbingFactors>(
            (reader, flags) -> Hashing.parseProbingFactorsAndComputeResult(reader, hashingAlgorithm, flags),
            flags -> Hashing.generateProbingFactorsAndComputeResult(capacity, hashingAlgorithm, flags)
        ).getResult(options);
    }

    private static List<Integer> parseOrGenerateValues(final Parameters options) throws IOException {
        return new ParserAndGenerator<List<Integer>>(
            Hashing::parseValues,
            Hashing::generateValues
        ).getResult(options);
    }

    private static HashResultWithProbingFactors parseProbingFactorsAndComputeResult(
        final BufferedReader reader,
        final CheckedBiFunction<Integer, Integer, HashList[], HashException> hashingAlgorithm,
        final Parameters options
    ) throws IOException {
        final String[] parameters = reader.readLine().split(",");
        final int linearProbingFactor = Integer.parseInt(parameters[parameters.length - 2]);
        final int quadraticProbingFactor = Integer.parseInt(parameters[parameters.length - 1]);
        try {
            return new HashResultWithProbingFactors(
                hashingAlgorithm.apply(linearProbingFactor, quadraticProbingFactor),
                linearProbingFactor,
                quadraticProbingFactor
            );
        } catch (final HashException e) {
            throw new IOException(e);
        }
    }

    private static List<Integer> parseValues(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        reader.readLine();
        return Arrays.stream(reader.readLine().split(",")).map(Integer::parseInt).toList();
    }

    private static void printArray(
        final HashList[] array,
        final int contentLength,
        final boolean probing,
        final BufferedWriter writer
    ) throws IOException {
        if (probing) {
            LaTeXUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
            LaTeXUtils.printListAndReturnLeftmostNodesName(
                Hashing.toTikZList(array),
                Optional.empty(),
                contentLength,
                writer
            );
        } else {
            LaTeXUtils.printTikzBeginning(TikZStyle.BORDERLESS, writer);
            LaTeXUtils.printVerticalStringArray(Hashing.toVerticalStringArray(array), null, null, null, writer);
        }
        LaTeXUtils.printTikzEnd(writer);
    }

    private static void printHashingExerciseAndSolution(
        final List<Integer> values,
        final HashList[] initialArray,
        final HashList[] result,
        final PrintOptions printOptions,
        final Parameters options,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) throws IOException {
        final int capacity = result.length;
        final int contentLength = Hashing.computeContentLength(result);
        exerciseWriter.write("F\\\"ugen Sie die folgenden Werte nacheinander in das unten stehende Array \\code{a} der L\\\"ange ");
        exerciseWriter.write(String.valueOf(capacity));
        exerciseWriter.write(" unter Verwendung der ");
        exerciseWriter.write(printOptions.optionsText);
        exerciseWriter.write(":\\\\");
        Main.newLine(exerciseWriter);
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, exerciseWriter);
        exerciseWriter.write(values.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        exerciseWriter.write(".");
        Main.newLine(exerciseWriter);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, exerciseWriter);
        switch (printOptions.preprintMode) {
            case ALWAYS:
            case SOLUTION_SPACE:
                LaTeXUtils.printVerticalProtectedSpace("3ex", exerciseWriter);
                if (printOptions.preprintMode == PreprintMode.SOLUTION_SPACE) {
                    LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, exerciseWriter);
                }
                LaTeXUtils.printBeginning(LaTeXUtils.CENTER, exerciseWriter);
                Hashing.printArray(initialArray, contentLength, printOptions.probing, exerciseWriter);
                LaTeXUtils.printEnd(LaTeXUtils.CENTER, exerciseWriter);
                if (printOptions.preprintMode == PreprintMode.SOLUTION_SPACE) {
                    LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), options, exerciseWriter);
                } else {
                    Main.newLine(exerciseWriter);
                    Main.newLine(exerciseWriter);
                }
                break;
            case NEVER:
                Main.newLine(exerciseWriter);
                Main.newLine(exerciseWriter);
        }

        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, solutionWriter);
        solutionWriter.write(printOptions.parameterText);
        Main.newLine(solutionWriter);
        Hashing.printArray(result, contentLength, printOptions.probing, solutionWriter);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, solutionWriter);
        Main.newLine(solutionWriter);
    }

    private static String toAdditionalHint(final int length) {
        return Main.TEXT_VERSION == TextVersion.ABRAHAM ? String.format(" ($f(n) = n \\mod %d$)", length) : "";
    }

    private static String toAdditionalHint(final int length, final double factor) {
        return Main.TEXT_VERSION == TextVersion.ABRAHAM ?
            String.format(
                Locale.GERMANY,
                " ($f(n,i) = \\left \\lfloor{%d \\cdot ( n \\cdot %.2f \\mod 1 )\\right \\rfloor $), wobei $x \\mod 1$ den Nachkommateil von $x$ bezeichnet",
                length,
                factor
            ) :
                "";
    }

    private static String toAdditionalHint(final int length, final double factor, final int length2) {
        return Main.TEXT_VERSION == TextVersion.ABRAHAM ?
            String.format(
                Locale.GERMANY,
                " ($f(n,i) = \\left \\lfloor{%d \\cdot ( n \\cdot %.2f \\mod 1 )}\\right \\rfloor~ + i \\mod %d$), wobei $x \\mod 1$ den Nachkommateil von $x$ bezeichnet",
                length,
                factor,
                length2
            ) :
                "";
    }

    private static String toAdditionalHint(
        final int capacity,
        final double multiplicationFactor,
        final int linearProbingFactor,
        final int quadraticProbingFactor
    ) {
        return Main.TEXT_VERSION == TextVersion.ABRAHAM ?
            String.format(
                Locale.GERMANY,
                " ($f(n,i) = \\left \\lfloor{%d \\cdot ( n \\cdot %.2f \\mod 1 )}\\right \\rfloor + %d \\cdot i + %d \\cdot i^2 $), wobei $x \\mod 1$ den Nachkommateil von $x$ bezeichnet",
                capacity,
                multiplicationFactor,
                linearProbingFactor,
                quadraticProbingFactor
            ):
                "";
    }

    private static String toAdditionalHint(final int length, final int length2) {
        return Main.TEXT_VERSION == TextVersion.ABRAHAM ?
            String.format(" ($f(n,i) = ((n \\mod %d) + i) \\mod %d$)", length, length2) :
                "";
    }

    private static String toAdditionalHint(
        final int length,
        final int linearProbingFactor,
        final int quadraticProbingFactor,
        final int length2
    ) {
        return Main.TEXT_VERSION == TextVersion.ABRAHAM ?
            String.format(
                " ($f(n,i) = ((n \\mod %d) + %d \\cdot i + %d \\cdot i^2) \\mod %d$)",
                length,
                linearProbingFactor,
                quadraticProbingFactor,
                length2
            ) :
                "";
    }

    private static String toMultiplicationMethodExerciseText(final double factor) {
        return String.format(Locale.GERMANY, "\\emphasize{Multiplikationsmethode} ($c = %.2f$)", factor);
    }

    private static String toParameterString(final int capacity) {
        return String.format("m = %d%s", capacity, Hashing.PARAMETER_STRING_END);
    }

    private static String toParameterString(final int capacity, final double multiplicationFactor) {
        return String.format(
            Locale.GERMANY,
            "m = %d, c = %.2f%s",
            capacity,
            multiplicationFactor,
            Hashing.PARAMETER_STRING_END
        );
    }

    private static String toParameterString(
        final int capacity,
        final double multiplicationFactor,
        final int linearProbingFactor,
        final int quadraticProbingFactor
    ) {
        return String.format(
            Locale.GERMANY,
            "m = %d, c = %.2f, $c_1$ = %d, $c_2$ = %d%s",
            capacity,
            multiplicationFactor,
            linearProbingFactor,
            quadraticProbingFactor,
            Hashing.PARAMETER_STRING_END
        );
    }

    private static String toParameterString(
        final int capacity,
        final int linearProbingFactor,
        final int quadraticProbingFactor
    ) {
        return String.format(
            "m = %d, $c_1$ = %d, $c_2$ = %d%s",
            capacity,
            linearProbingFactor,
            quadraticProbingFactor,
            Hashing.PARAMETER_STRING_END
        );
    }

    private static String toQuadraticProbingText(
        final int linearProbingFactor,
        final int quadraticProbingFactor
    ) {
        return String.format(
            " mit \\emphasize{quadratischer Sondierung} ($c_1 = %d$, $c_2 = %d$)",
            linearProbingFactor,
            quadraticProbingFactor
        );
    }

    private static List<ItemWithTikZInformation<Integer>> toTikZList(final HashList[] array) {
        return Arrays.stream(array)
            .map(list -> list.isEmpty() ?
                new ItemWithTikZInformation<Integer>() :
                    new ItemWithTikZInformation<Integer>(Optional.of(list.get(0))))
            .toList();
    }

    private static String[] toVerticalStringArray(final HashList[] array) {
        final String[] result = new String[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] =
                String.format("%d: %s", i, array[i].stream().map(String::valueOf).collect(Collectors.joining(", ")));
        }
        return result;
    }

}
