package exercisegenerator.algorithms.hashing;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.apache.commons.math3.fraction.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.algebra.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.util.*;

abstract class Hashing {

    static class PrintOptions {
        private final String optionsText;
        private final String parameterText;
        private final PreprintMode preprintMode;
        private final boolean probing;

        PrintOptions(
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

    static class ProbingFactors {
        final BigFraction linearProbingFactor;
        final BigFraction quadraticProbingFactor;

        ProbingFactors(final BigFraction linearProbingFactor, final BigFraction quadraticProbingFactor) {
            this.linearProbingFactor = linearProbingFactor;
            this.quadraticProbingFactor = quadraticProbingFactor;
        }
    }

    static final String DIVISION_METHOD = "\\emphasize{Divisionsmethode}";

    static final String GENERAL_HASHING_EXERCISE_TEXT_END = " ein";

    static final String LINEAR_PROBING = " mit \\emphasize{linearer Sondierung}";

    static final String NO_PROBING = " \\emphasize{ohne Sondierung} (also durch Verkettung)";

    static final Object PARAMETER_STRING_END = ":\\\\[2ex]";

    private static final int[] PRIMES_5_101 =
        new int[]{5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101};

    private static final int[] PRIMES_5_19 = new int[]{5, 7, 11, 13, 17, 19};

    static IntegerList[] createEmptyArray(final int length) {
        final IntegerList[] result = new IntegerList[length];
        for (int i = 0; i < length; i++) {
            result[i] = new IntegerList();
        }
        return result;
    }

    static IntegerList[] hashingWithDivisionMethod(
        final List<Integer> values,
        final IntegerList[] initialHashTable,
        final Optional<ProbingFunction> optionalProbingFunction
    ) throws HashException {
        return Hashing.hashing(
            values,
            initialHashTable,
            (value, capacity) -> value % capacity,
            optionalProbingFunction
        );
    }

    static IntegerList[] hashingWithMultiplicationMethod(
        final List<Integer> values,
        final IntegerList[] initialHashTable,
        final BigFraction factor,
        final Optional<ProbingFunction> optionalProbingFunction
    ) throws HashException {
        return Hashing.hashing(
            values,
            initialHashTable,
            (value, capacity) ->
                factor
                .multiply(value)
                .subtract(factor.multiply(value).intValue())
                .multiply(capacity)
                .intValue(),
            optionalProbingFunction
        );
    }

    static ProbingFunction linearProbing() {
        return (value, initialPosition, numberOfCollisions, capacity) -> {
            if (numberOfCollisions > capacity) {
                throw new HashException(String.format("The array is too small: Insertion of %d failed!", value));
            }
            return (initialPosition + numberOfCollisions) % capacity;
        };
    }

    static IntegerList[] parseOrGenerateInitialArray(final int numberOfValues, final Parameters options)
    throws IOException {
        if (!options.containsKey(Flag.OPERATIONS)) {
            return Hashing.createEmptyArray(Hashing.parseOrGenerateCapacity(numberOfValues, options));
        }
        return Arrays.stream(options.get(Flag.OPERATIONS).split(";"))
            .map(
                text -> Arrays.stream(text.split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toCollection(IntegerList::new))
            ).toArray(IntegerList[]::new);
    }

    static BigFraction parseOrGenerateMultiplicationFactor(final Parameters options) throws IOException {
        return new ParserAndGenerator<BigFraction>(
            Hashing::parseMultiplicationFactor,
            flags -> Hashing.getRandomFactorBetweenZeroAndOne()
        ).getResult(options);
    }

    static Integer parseOrGenerateNumberOfValues(final Parameters options) throws IOException {
        return new ParserAndGenerator<Integer>(
            Hashing::parseNumberOfValues,
            Hashing::generateNumberOfValues
        ).getResult(options);
    }

    static ProbingFactors parseOrGenerateProbingFactors(
        final int capacity,
        final Parameters options
    ) throws IOException {
        return new ParserAndGenerator<ProbingFactors>(
            (reader, flags) -> Hashing.parseProbingFactors(reader, flags),
            flags -> Hashing.generateProbingFactors(capacity, flags)
        ).getResult(options);
    }

    static List<Integer> parseOrGenerateValues(
        final int numOfValues,
        final int capacity,
        final Optional<BigFraction> optionalMultiplicationFactor,
        final Optional<ProbingFactors> optionalProbingFactors,
        final Parameters options
    ) throws IOException {
        return new ParserAndGenerator<List<Integer>>(
            Hashing::parseValues,
            Hashing::generateValues
        ).getResult(options);
    }

    static void printHashingExerciseAndSolution(
        final List<Integer> values,
        final IntegerList[] initialArray,
        final IntegerList[] result,
        final PrintOptions printOptions,
        final Parameters options,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) throws IOException {
        Hashing.printHashingExercise(values, initialArray, result, printOptions, options, exerciseWriter);
        Hashing.printHashingSolution(result, printOptions, solutionWriter);
    }

    static ProbingFunction quadraticProbing(final BigFraction linearFactor, final BigFraction quadraticFactor) {
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
                    linearFactor
                    .multiply(numberOfCollisions)
                    .add(quadraticFactor.multiply(numberOfCollisions * numberOfCollisions))
                    .add(initialPosition)
                    .intValue()
                ) % capacity;
        };
    }

    static String toMultiplicationMethodExerciseText(final BigFraction factor) {
        return String.format(
            Locale.GERMANY,
            "\\emphasize{Multiplikationsmethode} ($c = %s$)",
            AlgebraAlgorithms.toCoefficient(factor)
        );
    }

    static String toParameterString(final int capacity) {
        return String.format("$m = %d$%s", capacity, Hashing.PARAMETER_STRING_END);
    }

    static String toParameterString(final int capacity, final BigFraction multiplicationFactor) {
        return String.format(
            Locale.GERMANY,
            "$m = %d$, $c = %s$%s",
            capacity,
            AlgebraAlgorithms.toCoefficient(multiplicationFactor),
            Hashing.PARAMETER_STRING_END
        );
    }

    static String toQuadraticProbingText(
        final BigFraction linearProbingFactor,
        final BigFraction quadraticProbingFactor
    ) {
        return String.format(
            " mit \\emphasize{quadratischer Sondierung} ($c_1 = %s$, $c_2 = %s$)",
            AlgebraAlgorithms.toCoefficient(linearProbingFactor),
            AlgebraAlgorithms.toCoefficient(quadraticProbingFactor)
        );
    }

    private static int computeContentLength(final IntegerList[] array) {
        return Arrays.stream(array)
            .mapToInt(list -> list.stream().mapToInt(x -> String.valueOf(x).length()).max().orElse(1))
            .max()
            .orElse(1);
    }

    private static void copyValues(final IntegerList[] initialHashTable, final IntegerList[] hashTable) {
        for (int i = 0; i < hashTable.length; i++) {
            hashTable[i].addAll(initialHashTable[i]);
        }
    }

    private static int generateCapacity(final int numberOfValues, final Parameters options) {
        final int length = (int)(numberOfValues * 1.25);
        final String alg = options.get(Flag.ALGORITHM);
        if ("hashDivision".equals(alg) || "hashMultiplication".equals(alg)) {
            final Integer[] primes = Hashing.getAllUpToNextPrimes(length);
            final int index = Main.RANDOM.nextInt(primes.length);
            return primes[index];
        } else if (
            ("hashDivisionQuadratic".equals(alg) || "hashMultiplicationQuadratic".equals(alg))
            && Main.RANDOM.nextBoolean()
        ) {
            int result = Hashing.getNextPowerOf2(length);
            while (Main.RANDOM.nextBoolean()) {
                result = Hashing.getNextPowerOf2(result + 1);
            }
            return result;
        } else {
            int result = Hashing.getNextPrime(length);
            while (Main.RANDOM.nextBoolean()) {
                result = Hashing.getNextPrime(result + 1);
            }
            return result;
        }
    }

    private static Integer generateNumberOfValues(final Parameters options) {
        return
            options.containsKey(Flag.LENGTH) ?
                Integer.parseInt(options.get(Flag.LENGTH)) :
                    Main.RANDOM.nextInt(11) + 5;
    }

    private static ProbingFactors generateProbingFactors(final int capacity, final Parameters options) {
        if (Hashing.isPowerOf2(capacity)) {
            return new ProbingFactors(BigFraction.ONE_HALF, BigFraction.ONE_HALF);
        }
        return new ProbingFactors(
            new BigFraction(Main.RANDOM.nextInt(11)),
            new BigFraction(Main.RANDOM.nextInt(10) + 1)
        );
    }

    private static List<Integer> generateValues(final Parameters options) {
        final int length =
            options.containsKey(Flag.LENGTH) ?
                Integer.parseInt(options.get(Flag.LENGTH)) :
                    Hashing.PRIMES_5_19[Main.RANDOM.nextInt(Hashing.PRIMES_5_19.length)];
        return Stream.generate(() -> Main.RANDOM.nextInt(Main.NUMBER_LIMIT)).limit(length).toList();
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

    private static int getNextPowerOf2(final int number) {
        final int result = 1 << (31 - Integer.numberOfLeadingZeros(number));
        if (result >= number) {
            return result;
        }
        return result << 1;
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

    private static BigFraction getRandomFactorBetweenZeroAndOne() {
        return new BigFraction(Main.RANDOM.nextInt(99) + 1, 100);
    }

    private static IntegerList[] hashing(
        final List<Integer> values,
        final IntegerList[] initialHashTable,
        final BiFunction<Integer, Integer, Integer> hashFunction,
        final Optional<ProbingFunction> optionalProbingFunction
    ) throws HashException {
        final int capacity = initialHashTable.length;
        final IntegerList[] hashTable = Hashing.createEmptyArray(capacity);
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

    private static boolean isPowerOf2(final int capacity) {
        // TODO Auto-generated method stub
        return false;
    }

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

    private static BigFraction parseMultiplicationFactor(final BufferedReader reader, final Parameters options)
    throws NumberFormatException, IOException {
        return AlgebraAlgorithms.parseRationalNumber(reader.readLine().split(",")[1]);
    }

    private static Integer parseNumberOfValues(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        reader.readLine();
        return (int)Arrays.stream(reader.readLine().split(",")).count();
    }

    private static int parseOrGenerateCapacity(final int numberOfValues, final Parameters options)
    throws IOException {
        return new ParserAndGenerator<Integer>(
            Hashing::parseCapacity,
            flags -> Hashing.generateCapacity(numberOfValues, flags)
        ).getResult(options);
    }

    private static ProbingFactors parseProbingFactors(final BufferedReader reader, final Parameters options)
    throws IOException {
        final String[] parameters = reader.readLine().split(",");
        return new ProbingFactors(
            AlgebraAlgorithms.parseRationalNumber(parameters[parameters.length - 2]),
            AlgebraAlgorithms.parseRationalNumber(parameters[parameters.length - 1])
        );
    }

    private static List<Integer> parseValues(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        reader.readLine();
        return Arrays.stream(reader.readLine().split(",")).map(Integer::parseInt).toList();
    }

    private static void printArray(
        final IntegerList[] array,
        final int contentLength,
        final boolean probing,
        final BufferedWriter writer
    ) throws IOException {
        if (probing) {
            LaTeXUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
            LaTeXUtils.printListAndReturnLeftmostNodesName(
                IntegerList.toTikZList(array),
                Optional.empty(),
                contentLength,
                writer
            );
        } else {
            LaTeXUtils.printTikzBeginning(TikZStyle.BORDERLESS, writer);
            LaTeXUtils.printVerticalStringArray(IntegerList.toVerticalStringArray(array), null, null, null, writer);
        }
        LaTeXUtils.printTikzEnd(writer);
    }

    private static void printHashingExercise(
        final List<Integer> values,
        final IntegerList[] initialArray,
        final IntegerList[] result,
        final PrintOptions printOptions,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        final int capacity = result.length;
        final int contentLength = Hashing.computeContentLength(result);
        writer.write(
            "F\\\"ugen Sie die folgenden Werte nacheinander in das unten stehende Array der L\\\"ange "
        );
        writer.write(String.valueOf(capacity));
        writer.write(" unter Verwendung der ");
        writer.write(printOptions.optionsText);
        writer.write(":\\\\");
        Main.newLine(writer);
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
        writer.write(values.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        writer.write(".");
        Main.newLine(writer);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
        switch (printOptions.preprintMode) {
            case ALWAYS:
            case SOLUTION_SPACE:
                LaTeXUtils.printVerticalProtectedSpace("2ex", writer);
                if (printOptions.preprintMode == PreprintMode.SOLUTION_SPACE) {
                    LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, writer);
                }
                LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
                Hashing.printArray(initialArray, contentLength, printOptions.probing, writer);
                LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
                if (printOptions.preprintMode == PreprintMode.SOLUTION_SPACE) {
                    LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), options, writer);
                } else {
                    Main.newLine(writer);
                    Main.newLine(writer);
                }
                break;
            case NEVER:
                Main.newLine(writer);
                Main.newLine(writer);
        }
    }

    private static void printHashingSolution(
        final IntegerList[] result,
        final PrintOptions printOptions,
        final BufferedWriter writer
    ) throws IOException {
        final int contentLength = Hashing.computeContentLength(result);
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
        writer.write(printOptions.parameterText);
        Main.newLine(writer);
        Hashing.printArray(result, contentLength, printOptions.probing, writer);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
        Main.newLine(writer);
    }

}
