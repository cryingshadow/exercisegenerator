package exercisegenerator.algorithms.hashing;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import org.apache.commons.math3.fraction.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.algebra.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.util.*;

abstract class Hashing {

    static class HashResult {
        final IntegerList[] result;
        final HashStatistics statistics;
        private HashResult(final IntegerList[] result, final HashStatistics statistics) {
            this.result = result;
            this.statistics = statistics;
        }
    }

    static class HashStatistics {
        int maxNumberOfProbingsForSameValue;
        int numberOfCollisions;
        private HashStatistics(
            final int numberOfCollisions,
            final int maxNumberOfProbingsForSameValue
        ) {
            this.numberOfCollisions = numberOfCollisions;
            this.maxNumberOfProbingsForSameValue = maxNumberOfProbingsForSameValue;
        }
        @Override
        public String toString() {
            return String.format(
                "collisions: %d, max. number of probings for same value: %d",
                this.numberOfCollisions,
                this.maxNumberOfProbingsForSameValue
            );
        }
    }

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

    private static final int[] CAPACITIES = new int[] {5, 7, 8, 11, 13, 16, 17, 19, 23, 29, 31, 32};

    private static final int[] VALUES;

    static {
        VALUES = Stream.iterate(1, x -> x + 1).limit(Main.NUMBER_LIMIT).mapToInt(x -> x).toArray();
    }

    static IntegerList[] createEmptyArray(final int length) {
        final IntegerList[] result = new IntegerList[length];
        for (int i = 0; i < length; i++) {
            result[i] = new IntegerList();
        }
        return result;
    }

    static HashResult hashing(
        final List<Integer> values,
        final IntegerList[] initialHashTable,
        final HashFunction hashFunction,
        final Optional<ProbingFunction> optionalProbingFunction
    ) throws HashException {
        final int capacity = initialHashTable.length;
        final IntegerList[] hashTable = Hashing.createEmptyArray(capacity);
        Hashing.copyValues(initialHashTable, hashTable);
        int numberOfCollisions = 0;
        int maxNumberOfProbingsForSameValue = 0;
        if (optionalProbingFunction.isPresent()) {
            final ProbingFunction probingFunction = optionalProbingFunction.get();
            for (final Integer value : values) {
                final int initialPos = hashFunction.apply(value);
                int pos = initialPos;
                int numberOfProbings = 0;
                while (!hashTable[pos].isEmpty()) {
                    final int offset = probingFunction.apply(++numberOfProbings);
                    pos = (initialPos + offset) % capacity;
                    if (numberOfProbings > capacity) {
                        throw new HashException(
                            String.format(
                                "The array is too small or the probing constants are chosen badly: Insertion of %d failed!",
                                value
                            )
                        );
                    }
                }
                numberOfCollisions += numberOfProbings;
                maxNumberOfProbingsForSameValue = Math.max(maxNumberOfProbingsForSameValue, numberOfProbings);
                hashTable[pos].add(value);
            }
        } else {
            for (final Integer value : values) {
                final Integer hashValue = hashFunction.apply(value);
                if (!hashTable[hashValue].isEmpty()) {
                    numberOfCollisions++;
                }
                hashTable[hashValue].add(value);
            }
        }
        return new HashResult(hashTable, new HashStatistics(numberOfCollisions, maxNumberOfProbingsForSameValue));
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
        final HashFunction hashFunction,
        final Optional<ProbingFunction> optionalProbingFunction,
        final Parameters options
    ) throws IOException {
        return new ParserAndGenerator<List<Integer>>(
            Hashing::parseValues,
            flags -> Hashing.generateValues(
                numOfValues,
                capacity,
                hashFunction,
                optionalProbingFunction,
                flags
            )
        ).getResult(options);
    }

    static void printHashingExerciseAndSolution(
        final List<Integer> values,
        final IntegerList[] initialArray,
        final HashResult result,
        final PrintOptions printOptions,
        final Parameters options,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) throws IOException {
        Hashing.printHashingExercise(values, initialArray, result.result, printOptions, options, exerciseWriter);
        Hashing.printHashingSolution(result, printOptions, options, solutionWriter);
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

    private static boolean addRandomValueForIndex(
        final int capacity,
        final int index,
        final HashFunction hashFunction,
        final ProbingFunction probingFunction,
        final List<Integer> values,
        final List<Integer> indices
    ) {
        int secondIndex = index;
        int iteration = 1;
        while (indices.contains(secondIndex)) {
            if (iteration > capacity) {
                return false;
            }
            secondIndex = (index + probingFunction.apply(iteration)) % capacity;
            iteration++;
        }
        final Optional<Integer> optionalValue = Hashing.getRandomValueForIndex(index, hashFunction);
        if (optionalValue.isEmpty()) {
            return false;
        }
        indices.add(secondIndex);
        values.add(optionalValue.get());
        return true;
    }

    private static boolean addRandomValuesAndIndicesForMultipleProbingsForSameValue(
        final int capacity,
        final HashFunction hashFunction,
        final ProbingFunction probingFunction,
        final List<Integer> values,
        final List<Integer> indices
    ) {
        final int firstIndex;
        if (indices.isEmpty()) {
            final int firstValue = Hashing.generateRandomValue();
            values.add(firstValue);
            firstIndex = hashFunction.apply(firstValue);
            indices.add(firstIndex);
        } else {
            firstIndex = indices.get(Main.RANDOM.nextInt(indices.size()));
        }
        final int secondIndex = (firstIndex + probingFunction.apply(1)) % capacity;
        if (!indices.contains(secondIndex)) {
            final Optional<Integer> optionalValue = Hashing.getRandomValueForIndex(secondIndex, hashFunction);
            if (optionalValue.isEmpty()) {
                return false;
            }
            indices.add(secondIndex);
            values.add(optionalValue.get());
        }
        return Hashing.addRandomValueForIndex(capacity, firstIndex, hashFunction, probingFunction, values, indices);
    }

    private static boolean addRandomValuesAndIndicesForProbingForProbedValue(
        final int capacity,
        final HashFunction hashFunction,
        final ProbingFunction probingFunction,
        final List<Integer> values,
        final List<Integer> indices
    ) {
        if (indices.isEmpty()) {
            final int firstValue = Hashing.generateRandomValue();
            values.add(firstValue);
            indices.add(hashFunction.apply(firstValue));
        }
        final int index = indices.get(Main.RANDOM.nextInt(indices.size()));
        if (Hashing.addRandomValueForIndex(capacity, index, hashFunction, probingFunction, values, indices)) {
            return Hashing.addRandomValueForIndex(
                capacity,
                indices.get(indices.size() - 1),
                hashFunction,
                probingFunction,
                values,
                indices
            );
        }
        return false;
    }

    private static boolean addRandomValuesAndIndicesForProbingOverflow(
        final int capacity,
        final HashFunction hashFunction,
        final ProbingFunction probingFunction,
        final List<Integer> values,
        final List<Integer> indices
    ) {
        final int lastIndex = capacity - 1;
        if (!indices.contains(lastIndex)) {
            final Optional<Integer> optionalValue = Hashing.getRandomValueForIndex(lastIndex, hashFunction);
            if (optionalValue.isEmpty()) {
                return false;
            }
            indices.add(lastIndex);
            values.add(optionalValue.get());
        }
        return Hashing.addRandomValueForIndex(capacity, lastIndex, hashFunction, probingFunction, values, indices);
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
        final List<Integer> candidates =
            Arrays.stream(Hashing.CAPACITIES).filter(value -> value >= length).boxed().toList();
        if (candidates.isEmpty()) {
            return Hashing.CAPACITIES[Hashing.CAPACITIES.length - 1];
        }
        return candidates.get(Main.RANDOM.nextInt(candidates.size()));
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

    private static int generateRandomValue() {
        return Main.RANDOM.nextInt(Main.NUMBER_LIMIT);
    }

    private static Stream<Integer> generateRandomValues(
        final int numberOfValues,
        final HashFunction hashFunction,
        final Collection<Integer> forbiddenIndices
    ) {
        if (forbiddenIndices.isEmpty()) {
            return Stream.generate(Hashing::generateRandomValue).limit(numberOfValues);
        }
        final List<Integer> currentForbiddenIndices = new LinkedList<Integer>(forbiddenIndices);
        final Stream.Builder<Integer> builder = Stream.builder();
        for (int i = 0; i < numberOfValues; i++) {
            final int value = Hashing.generateRandomValue();
            final int index = hashFunction.apply(value);
            if (currentForbiddenIndices.contains(index)) {
                i--;
            } else {
                builder.accept(value);
                currentForbiddenIndices.add(index);
            }
        }
        return builder.build();
    }

    private static List<Integer> generateValues(
        final int numberOfValues,
        final int capacity,
        final HashFunction hashFunction,
        final Optional<ProbingFunction> optionalProbingFunction,
        final Parameters options
    ) {
        if (numberOfValues < 5 || optionalProbingFunction.isEmpty()) {
            return Hashing.generateRandomValues(numberOfValues, hashFunction, Collections.emptyList()).toList();
        }
        final int choice = Hashing.parseOrGenerateChoice(options);
        final boolean multipleProbingsForSameValue = (4 & choice) > 0;
        final boolean probingOverflow = (2 & choice) > 0;
        final boolean probingForProbedValue = (1 & choice) > 0;
        final ProbingFunction probingFunction = optionalProbingFunction.get();
        final List<Integer> values = new LinkedList<Integer>();
        final List<Integer> indices = new LinkedList<Integer>();
        if (multipleProbingsForSameValue) {
            Hashing.addRandomValuesAndIndicesForMultipleProbingsForSameValue(
                capacity,
                hashFunction,
                probingFunction,
                values,
                indices
            );
        }
        if (probingOverflow) {
            Hashing.addRandomValuesAndIndicesForProbingOverflow(
                capacity,
                hashFunction,
                probingFunction,
                values,
                indices
            );
        }
        if (probingForProbedValue) {
            Hashing.addRandomValuesAndIndicesForProbingForProbedValue(
                capacity,
                hashFunction,
                probingFunction,
                values,
                indices
            );
        }
        final Stream<Integer> randomValues =
            Hashing.generateRandomValues(Math.max(numberOfValues - values.size(), 0), hashFunction, indices);
        final List<Integer> result = new LinkedList<Integer>();
        final Iterator<Integer> iterator = Hashing.randomFlatteningZip(values.iterator(), randomValues.iterator());
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result;
    }

    private static BigFraction getRandomFactorBetweenZeroAndOne() {
        return new BigFraction(Main.RANDOM.nextInt(99) + 1, 100);
    }

    private static Optional<Integer> getRandomValueForIndex(final int index, final HashFunction hashFunction) {
        final List<Integer> candidates =
            Arrays.stream(Hashing.VALUES).filter(value -> hashFunction.apply(value) == index).boxed().toList();
        if (candidates.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(candidates.get(Main.RANDOM.nextInt(candidates.size())));
    }

    private static boolean isPowerOf2(final int capacity) {
        return Integer.numberOfLeadingZeros(capacity) + Integer.numberOfTrailingZeros(capacity) == 31;
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

    private static int parseOrGenerateChoice(final Parameters options) {
        if (options.containsKey(Flag.CAPACITY)) {
            return Integer.parseInt(options.get(Flag.CAPACITY));
        }
        return Main.RANDOM.nextInt(7) + 1;
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
            LaTeXUtils.printTikzBeginning(TikZStyle.ARRAY_WITH_INDICES, writer);
            LaTeXUtils.printListAndReturnLowestLeftmostNodesName(
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
        final HashResult result,
        final PrintOptions printOptions,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        final int contentLength = Hashing.computeContentLength(result.result);
        if (Main.EMBEDDED_EXAM.equals(options.get(Flag.EXECUTION_MODE))) {
            LaTeXUtils.printVerticalProtectedSpace("-3ex", writer);
        }
        writer.write(String.format("%% hashing statistics: %s", result.statistics.toString()));
        Main.newLine(writer);
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
        writer.write(printOptions.parameterText);
        Main.newLine(writer);
        Hashing.printArray(result.result, contentLength, printOptions.probing, writer);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
        Main.newLine(writer);
    }

    private static Iterator<Integer> randomFlatteningZip(
        final Iterator<Integer> iterator1,
        final Iterator<Integer> iterator2
    ) {
        return new Iterator<Integer>() {

            @Override
            public boolean hasNext() {
                return iterator1.hasNext() || iterator2.hasNext();
            }

            @Override
            public Integer next() {
                if (iterator1.hasNext()) {
                    if (iterator2.hasNext() && Main.RANDOM.nextBoolean()) {
                        return iterator2.next();
                    }
                    return iterator1.next();
                }
                return iterator2.next();
            }

        };
    }

}
