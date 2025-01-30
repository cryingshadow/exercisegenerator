package exercisegenerator.algorithms.hashing;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import org.apache.commons.math3.fraction.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.algorithms.algebra.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.hashing.*;

interface Hashing extends AlgorithmImplementation<HashProblem, HashResult> {

    static class PrintOptions {
        private final String optionsText;
        private final String parameterText;
        private final SolutionSpaceMode preprintMode;
        private final boolean probing;

        PrintOptions(
            final String optionsText,
            final String parameterText,
            final boolean probing,
            final SolutionSpaceMode preprintMode
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

    static final int[] CAPACITIES = new int[] {5, 7, 8, 11, 13, 16, 17, 19, 23, 29, 31, 32};

    static final String DIVISION_METHOD = "\\emphasize{Divisionsmethode}";

    static final String GENERAL_HASHING_EXERCISE_TEXT_END = " ein";

    static final String LINEAR_PROBING = " mit \\emphasize{linearer Sondierung}";

    static final String NO_PROBING = " \\emphasize{ohne Sondierung} (also durch Verkettung)";

    static final String PARAMETER_STRING_END = ":\\\\[2ex]";

    static IntegerList[] createEmptyArray(final int length) {
        final IntegerList[] result = new IntegerList[length];
        for (int i = 0; i < length; i++) {
            result[i] = new IntegerList();
        }
        return result;
    }

    static HashResult hashing(final HashProblem problem) {
        final int capacity = problem.initialHashTable().length;
        final IntegerList[] hashTable = Hashing.createEmptyArray(capacity);
        Hashing.copyValues(problem.initialHashTable(), hashTable);
        int numberOfCollisions = 0;
        int maxNumberOfProbingsForSameValue = 0;
        if (problem.optionalProbingFunction().isPresent()) {
            final ProbingFunction probingFunction = problem.optionalProbingFunction().get().probingFunction();
            for (final Integer value : problem.values()) {
                final int initialPos = problem.hashFunction().hashFunction().apply(value);
                int pos = initialPos;
                int numberOfProbings = 0;
                while (!hashTable[pos].isEmpty()) {
                    final int offset = probingFunction.apply(++numberOfProbings);
                    pos = (initialPos + offset) % capacity;
                    if (numberOfProbings > capacity) {
                        throw new IllegalArgumentException(
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
            for (final Integer value : problem.values()) {
                final Integer hashValue = problem.hashFunction().hashFunction().apply(value);
                if (!hashTable[hashValue].isEmpty()) {
                    numberOfCollisions++;
                }
                hashTable[hashValue].add(value);
            }
        }
        return new HashResult(hashTable, new HashStatistics(numberOfCollisions, maxNumberOfProbingsForSameValue));
    }

    static IntegerList[] parseOrGenerateInitialArray(final int numberOfValues, final Parameters<Flag> options)
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

    static BigFraction parseOrGenerateMultiplicationFactor(final Parameters<Flag> options) throws IOException {
        return new ParserAndGenerator<BigFraction>(
            Hashing::parseMultiplicationFactor,
            flags -> Hashing.getRandomFactorBetweenZeroAndOne()
        ).getResult(options);
    }

    static Integer parseOrGenerateNumberOfValues(final Parameters<Flag> options) throws IOException {
        return new ParserAndGenerator<Integer>(
            Hashing::parseNumberOfValues,
            Hashing::generateNumberOfValues
        ).getResult(options);
    }

    static ProbingFactors parseOrGenerateProbingFactors(
        final int capacity,
        final Parameters<Flag> options
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
        final Optional<ProbingFunctionWithParameters> optionalProbingFunction,
        final Parameters<Flag> options
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

//    static String toParameterString(final int capacity) {
//        return String.format("$m = %d$%s", capacity, Hashing.PARAMETER_STRING_END);
//    }
//
//    static String toParameterString(final int capacity, final BigFraction multiplicationFactor) {
//        return String.format(
//            Locale.GERMANY,
//            "$m = %d$, $c = %s$%s",
//            capacity,
//            AlgebraAlgorithms.toCoefficient(multiplicationFactor),
//            Hashing.PARAMETER_STRING_END
//        );
//    }

    static String toMultiplicationMethodExerciseText(final BigFraction factor) {
        return String.format(
            Locale.GERMANY,
            "\\emphasize{Multiplikationsmethode} ($c = %s$)",
            LaTeXUtils.toCoefficient(factor)
        );
    }

    static String toQuadraticProbingText(
        final BigFraction linearProbingFactor,
        final BigFraction quadraticProbingFactor
    ) {
        return String.format(
            " mit \\emphasize{quadratischer Sondierung} ($c_1 = %s$, $c_2 = %s$)",
            LaTeXUtils.toCoefficient(linearProbingFactor),
            LaTeXUtils.toCoefficient(quadraticProbingFactor)
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

    private static int generateCapacity(final int numberOfValues, final Parameters<Flag> options) {
        final int length = (int)(numberOfValues * 1.25);
        final List<Integer> candidates =
            Arrays.stream(Hashing.CAPACITIES).filter(value -> value >= length).boxed().toList();
        if (candidates.isEmpty()) {
            return Hashing.CAPACITIES[Hashing.CAPACITIES.length - 1];
        }
        return candidates.get(Main.RANDOM.nextInt(candidates.size()));
    }

    private static Integer generateNumberOfValues(final Parameters<Flag> options) {
        return AlgorithmImplementation.parseOrGenerateLength(5, 15, options);
    }

    private static ProbingFactors generateProbingFactors(final int capacity, final Parameters<Flag> options) {
        if (Hashing.isPowerOf2(capacity)) {
            return new ProbingFactors(BigFraction.ONE_HALF, BigFraction.ONE_HALF);
        }
        int linearFactor;
        do {
            linearFactor = Main.RANDOM.nextInt(11);
        } while (linearFactor == capacity);
        int quadraticFactor;
        do {
            quadraticFactor = Main.RANDOM.nextInt(10) + 1;
        } while (quadraticFactor == capacity);
        return new ProbingFactors(new BigFraction(linearFactor), new BigFraction(quadraticFactor));
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
        final Optional<ProbingFunctionWithParameters> optionalProbingFunction,
        final Parameters<Flag> options
    ) {
        if (numberOfValues < 5 || optionalProbingFunction.isEmpty()) {
            return Hashing.generateRandomValues(numberOfValues, hashFunction, Collections.emptyList()).toList();
        }
        final int choice = Hashing.parseOrGenerateChoice(options);
        final boolean multipleProbingsForSameValue = (4 & choice) > 0;
        final boolean probingOverflow = (2 & choice) > 0;
        final boolean probingForProbedValue = (1 & choice) > 0;
        final ProbingFunction probingFunction = optionalProbingFunction.get().probingFunction();
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
            Stream
            .iterate(1, x -> x + 1)
            .limit(Main.NUMBER_LIMIT)
            .filter(value -> hashFunction.apply(value) == index)
            .toList();
        if (candidates.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(candidates.get(Main.RANDOM.nextInt(candidates.size())));
    }

    private static boolean isPowerOf2(final int capacity) {
        return Integer.numberOfLeadingZeros(capacity) + Integer.numberOfTrailingZeros(capacity) == 31;
    }

    private static int parseCapacity(final BufferedReader reader, final Parameters<Flag> options)
    throws NumberFormatException, IOException {
        return Integer.parseInt(reader.readLine().split(",")[0]);
    }

    private static BigFraction parseMultiplicationFactor(final BufferedReader reader, final Parameters<Flag> options)
    throws NumberFormatException, IOException {
        return AlgebraAlgorithms.parseRationalNumber(reader.readLine().split(",")[1]);
    }

    private static Integer parseNumberOfValues(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        reader.readLine();
        return (int)Arrays.stream(reader.readLine().split(",")).count();
    }

    private static int parseOrGenerateCapacity(final int numberOfValues, final Parameters<Flag> options)
    throws IOException {
        return new ParserAndGenerator<Integer>(
            Hashing::parseCapacity,
            flags -> Hashing.generateCapacity(numberOfValues, flags)
        ).getResult(options);
    }

    private static int parseOrGenerateChoice(final Parameters<Flag> options) {
        if (options.containsKey(Flag.CAPACITY)) {
            return options.getAsInt(Flag.CAPACITY);
        }
        return Main.RANDOM.nextInt(7) + 1;
    }

    private static ProbingFactors parseProbingFactors(final BufferedReader reader, final Parameters<Flag> options)
    throws IOException {
        final String[] parameters = reader.readLine().split(",");
        return new ProbingFactors(
            AlgebraAlgorithms.parseRationalNumber(parameters[parameters.length - 2]),
            AlgebraAlgorithms.parseRationalNumber(parameters[parameters.length - 1])
        );
    }

    private static List<Integer> parseValues(
        final BufferedReader reader,
        final Parameters<Flag> options
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

    private static PrintOptions printOptions(
        final HashProblem problem,
        final Parameters<Flag> options
    ) throws IOException {
        return new PrintOptions(
            problem
            .hashFunction()
            .exerciseText()
            .concat(
                problem.optionalProbingFunction().isEmpty() ?
                    Hashing.NO_PROBING :
                        problem.optionalProbingFunction().get().exerciseText()
            ).concat(Hashing.GENERAL_HASHING_EXERCISE_TEXT_END)
            .concat(
                problem.hashFunction().additionalHint().apply(
                    problem.optionalProbingFunction().isEmpty() ?
                        "" :
                            problem.optionalProbingFunction().get().additionalProbingHint()
                )
            ),
            Hashing.toParameterString(problem),
            problem.optionalProbingFunction().isPresent(),
            SolutionSpaceMode.parsePreprintMode(options)
        );
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

    private static String toParameterString(final HashProblem problem) {
        final String result = Hashing.toParameterString(problem.hashFunction().parameters());
        if (problem.optionalProbingFunction().isEmpty()) {
            return result + Hashing.PARAMETER_STRING_END;
        }
        final String probingPart = Hashing.toParameterString(problem.optionalProbingFunction().get().parameters());
        if (probingPart.isBlank()) {
            return result + Hashing.PARAMETER_STRING_END;
        }
        return String.format("%s, %s%s", result, probingPart, Hashing.PARAMETER_STRING_END);
    }

    private static String toParameterString(final Map<String, BigFraction> parameters) {
        return parameters
            .entrySet()
            .stream()
            .map(entry -> String.format("$%s = %s$", entry.getKey(), LaTeXUtils.toCoefficient(entry.getValue())))
            .collect(Collectors.joining(", "));
    }

    @Override
    default public HashResult apply(final HashProblem problem) {
        return Hashing.hashing(problem);
    }

    @Override
    default public HashProblem parseOrGenerateProblem(final Parameters<Flag> options) throws IOException {
        final int numOfValues = Hashing.parseOrGenerateNumberOfValues(options);
        final IntegerList[] initialHashTable = Hashing.parseOrGenerateInitialArray(numOfValues, options);
        final int capacity = initialHashTable.length;
        final HashFunctionWithParameters hashFunction = this.hashFunction(capacity, options);
        final Optional<ProbingFunctionWithParameters> optionalProbingFunction =
            this.optionalProbingFunction(capacity, options);
        final List<Integer> values =
            Hashing.parseOrGenerateValues(
                numOfValues,
                capacity,
                hashFunction.hashFunction(),
                optionalProbingFunction,
                options
            );
        return new HashProblem(initialHashTable, values, hashFunction, optionalProbingFunction);
    }

    @Override
    default public void printExercise(
        final HashProblem problem,
        final HashResult solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        final PrintOptions printOptions = Hashing.printOptions(problem, options);
        final int capacity = solution.result().length;
        final int contentLength = Hashing.computeContentLength(solution.result());
        writer.write(
            "F\\\"ugen Sie die folgenden Werte nacheinander in das unten stehende Array der L\\\"ange "
        );
        writer.write(String.valueOf(capacity));
        writer.write(" unter Verwendung der ");
        writer.write(printOptions.optionsText);
        writer.write(":\\\\");
        Main.newLine(writer);
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
        writer.write(problem.values().stream().map(String::valueOf).collect(Collectors.joining(", ")));
        writer.write(".");
        Main.newLine(writer);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
        switch (printOptions.preprintMode) {
            case ALWAYS:
            case SOLUTION_SPACE:
                LaTeXUtils.printVerticalProtectedSpace("2ex", writer);
                if (printOptions.preprintMode == SolutionSpaceMode.SOLUTION_SPACE) {
                    LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, writer);
                }
                LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
                Hashing.printArray(problem.initialHashTable(), contentLength, printOptions.probing, writer);
                LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
                if (printOptions.preprintMode == SolutionSpaceMode.SOLUTION_SPACE) {
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

    @Override
    default public void printSolution(
        final HashProblem problem,
        final HashResult solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        final PrintOptions printOptions = Hashing.printOptions(problem, options);
        final int contentLength = Hashing.computeContentLength(solution.result());
        if (Main.EMBEDDED_EXAM.equals(options.get(Flag.EXECUTION_MODE))) {
            LaTeXUtils.printVerticalProtectedSpace("-3ex", writer);
        }
        writer.write(String.format("%% hashing statistics: %s", solution.statistics().toString()));
        Main.newLine(writer);
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
        writer.write(printOptions.parameterText);
        Main.newLine(writer);
        Hashing.printArray(solution.result(), contentLength, printOptions.probing, writer);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
        Main.newLine(writer);
    }

    HashFunctionWithParameters hashFunction(final int capacity, final Parameters<Flag> options) throws IOException;

    Optional<ProbingFunctionWithParameters> optionalProbingFunction(
        final int capacity,
        final Parameters<Flag> options
    ) throws IOException;

}
