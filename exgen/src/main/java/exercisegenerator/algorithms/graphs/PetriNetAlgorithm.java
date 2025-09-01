package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.graphs.petrinets.*;

public abstract class PetriNetAlgorithm<S> implements AlgorithmImplementation<PetriNetInput, S> {

    private static record TransitionSkeleton(int from, int to) {}

    private static final int SIZE_OF_PLACE_SQUARE_SIDE = 3;

    static int computePlaceIndex(final int transitionIndex, final boolean from) {
        final int level = PetriNetAlgorithm.levelForTransitionIndex(transitionIndex);
        final int before = PetriNetAlgorithm.transitionsBeforeCurrentLevel(transitionIndex);
        final int indexWithinLevel = transitionIndex - before;
        final int transitionsWithinLevel = (level - 1) * 8;
        final boolean betweenLevels = indexWithinLevel < transitionsWithinLevel / 2;
        if (betweenLevels) {
            final boolean firstHalf = indexWithinLevel < transitionsWithinLevel / 4;
            if (firstHalf) {
                final int toPlace = (level - 1) * (level - 1) + indexWithinLevel / 2;
                final int fromPlace = (level - 2) * (level - 2) + indexWithinLevel / 2;
                return transitionIndex % 2 == 0 ? (from ? fromPlace : toPlace) : (from ? toPlace : fromPlace);
            }
            final int toPlace = (level - 1) * (level - 1) + indexWithinLevel / 2 + 1;
            final int fromPlace = (level - 2) * (level - 2) + indexWithinLevel / 2 - 1;
            return transitionIndex % 2 == 0 ? (from ? fromPlace : toPlace) : (from ? toPlace : fromPlace);
        }
        final int fromPlace = (level - 1) * (level - 1) + (indexWithinLevel - transitionsWithinLevel / 2) / 2;
        final int toPlace = fromPlace + 1;
        return transitionIndex % 2 == 0 ? (from ? fromPlace : toPlace) : (from ? toPlace : fromPlace);
    }

    static int computeXforPlace(final int placeIndex) {
        final int level = PetriNetAlgorithm.levelForPlaceIndex(placeIndex);
        final int before = (level - 1) * (level - 1);
        final int indexWithinLevel = placeIndex - before;
        final int placesWithinLevel = (level - 1) * 2 + 1;
        final boolean firstHalf = indexWithinLevel <= placesWithinLevel / 2;
        if (firstHalf) {
            return (level - 1) * 4;
        }
        return (placesWithinLevel - indexWithinLevel - 1) * 4;
    }

    static int computeYforPlace(final int placeIndex) {
        final int level = PetriNetAlgorithm.levelForPlaceIndex(placeIndex);
        final int before = (level - 1) * (level - 1);
        final int indexWithinLevel = placeIndex - before;
        final int placesWithinLevel = (level - 1) * 2 + 1;
        final boolean firstHalf = indexWithinLevel <= placesWithinLevel / 2;
        if (firstHalf) {
            return (PetriNetAlgorithm.SIZE_OF_PLACE_SQUARE_SIDE - indexWithinLevel - 1) * 4;
        }
        return (PetriNetAlgorithm.SIZE_OF_PLACE_SQUARE_SIDE - level) * 4;
    }

    private static int computeXforTransition(final int placeFrom, final int placeTo) {
        final int fromX = PetriNetAlgorithm.computeXforPlace(placeFrom);
        final int fromY = PetriNetAlgorithm.computeYforPlace(placeFrom);
        final int toX = PetriNetAlgorithm.computeXforPlace(placeTo);
        final int toY = PetriNetAlgorithm.computeYforPlace(placeTo);
        if (fromX == toX) {
            return toY > fromY ? fromX - 1 : fromX + 1;
        }
        return (fromX + toX) / 2;
    }

    private static int computeYforTransition(final int placeFrom, final int placeTo) {
        final int fromX = PetriNetAlgorithm.computeXforPlace(placeFrom);
        final int fromY = PetriNetAlgorithm.computeYforPlace(placeFrom);
        final int toX = PetriNetAlgorithm.computeXforPlace(placeTo);
        final int toY = PetriNetAlgorithm.computeYforPlace(placeTo);
        if (fromX == toX) {
            return (fromY + toY) / 2;
        }
        return toX > fromX ? fromY + 1 : fromY - 1;
    }

    private static PetriNetInput generateProblem(final Parameters<Flag> options) {
        final List<PetriPlace> places = new LinkedList<PetriPlace>();
        List<PetriTransition> transitions = new LinkedList<PetriTransition>();
        final List<Integer> tokens = new ArrayList<Integer>();
        final int numberOfTransitionsForGeneration =
            PetriNetAlgorithm.numberOfTransitionsForGeneration(PetriNetAlgorithm.SIZE_OF_PLACE_SQUARE_SIDE);
        final TransitionSkeleton[] randomSkeleton =
            PetriNetAlgorithm.generateTransitions(numberOfTransitionsForGeneration);
        final boolean[] placeExistence =
            new boolean[PetriNetAlgorithm.SIZE_OF_PLACE_SQUARE_SIDE * PetriNetAlgorithm.SIZE_OF_PLACE_SQUARE_SIDE];
        Arrays.fill(placeExistence, false);
        int transitionLabelIndex = 1;
        for (int transitionIndex = 0; transitionIndex < numberOfTransitionsForGeneration; transitionIndex++) {
            final TransitionSkeleton skeleton = randomSkeleton[transitionIndex];
            if (skeleton == null) {
                continue;
            }
            final int fromIndex = PetriNetAlgorithm.computePlaceIndex(transitionIndex, true);
            final int toIndex = PetriNetAlgorithm.computePlaceIndex(transitionIndex, false);
            placeExistence[fromIndex] = true;
            placeExistence[toIndex] = true;
            transitions.add(
                new PetriTransition(
                    String.format("$t_{%d}$", transitionLabelIndex),
                    PetriNetAlgorithm.computeXforTransition(fromIndex, toIndex),
                    PetriNetAlgorithm.computeYforTransition(fromIndex, toIndex),
                    Map.of(fromIndex, skeleton.from),
                    Map.of(toIndex, skeleton.to)
                )
            );
            transitionLabelIndex++;
        }
        int placeLabelIndex = 1;
        for (int placeIndex = 0; placeIndex < placeExistence.length; placeIndex++) {
            if (placeExistence[placeIndex]) {
                places.add(
                    new PetriPlace(
                        String.format("$p_{%d}$", placeLabelIndex),
                        PetriNetAlgorithm.computeXforPlace(placeIndex),
                        PetriNetAlgorithm.computeYforPlace(placeIndex),
                        135
                    )
                );
                placeLabelIndex++;
            } else {
                final int reduceFromIndex = placeLabelIndex - 1;
                transitions =
                    transitions.stream()
                    .map(
                        t -> new PetriTransition(
                            t.label(),
                            t.x(),
                            t.y(),
                            PetriNetAlgorithm.reduceKeyIndices(t.from(), reduceFromIndex),
                            PetriNetAlgorithm.reduceKeyIndices(t.to(), reduceFromIndex)
                        )
                    ).toList();
            }
        }
        for (int i = 0; i < places.size(); i++) {
            final int numberOfTokens = Main.RANDOM.nextInt(11) - 5;
            tokens.add(numberOfTokens > 0 ? numberOfTokens : 0);
        }
        return new PetriNetInput(places, transitions, tokens);
    }

    private static TransitionSkeleton[] generateTransitions(final int numberOfTransitionsForGeneration) {
        final int maxWeight = 5;
        final TransitionSkeleton[] result =
            new TransitionSkeleton[numberOfTransitionsForGeneration];
        final List<Integer> indices =
            IntStream.range(0, numberOfTransitionsForGeneration)
            .boxed()
            .collect(Collectors.toCollection(ArrayList::new));
        for (int i = 0; i < 4; i++) {
            result[indices.remove(Main.RANDOM.nextInt(indices.size()))] =
                new TransitionSkeleton(Main.RANDOM.nextInt(maxWeight) + 1, Main.RANDOM.nextInt(maxWeight) + 1);
        }
        int additionalTransitions = 0;
        while (additionalTransitions < numberOfTransitionsForGeneration - 4 && Main.RANDOM.nextBoolean()) {
            additionalTransitions++;
        }
        for (int i = 0; i < additionalTransitions; i++) {
            result[indices.remove(Main.RANDOM.nextInt(indices.size()))] =
                new TransitionSkeleton(Main.RANDOM.nextInt(maxWeight) + 1, Main.RANDOM.nextInt(maxWeight) + 1);
        }
        return result;
    }

    private static int levelForPlaceIndex(final int placeIndex) {
        return ((int)Math.floor(Math.sqrt(placeIndex))) + 1;
    }

    private static int levelForTransitionIndex(final int transitionIndex) {
        if (transitionIndex == 0) {
            return 1;
        }
        int level = 1;
        int current = 0;
        while (true) {
            if (transitionIndex < current) {
                return level;
            }
            current += 8 * level;
            level++;
        }
    }

    private static int numberOfTransitionsForGeneration(final int level) {
        int result = 0;
        for (int i = 1; i < level; i++) {
            result += 8 * i;
        }
        return result;
    }

    private static PetriNetInput parseProblem(final BufferedReader reader, final Parameters<Flag> options) {
        return Main.GSON.fromJson(reader, PetriNetInput.class);
    }

    private static Map<Integer, Integer> reduceKeyIndices(final Map<Integer, Integer> weights, final int fromIndex) {
        return weights.entrySet()
            .stream()
            .map(entry -> entry.getKey() >= fromIndex ? Map.entry(entry.getKey() - 1, entry.getValue()) : entry)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static int transitionsBeforeCurrentLevel(final int transitionIndex) {
        if (transitionIndex < 8) {
            return 0;
        }
        int level = 1;
        int current = 0;
        while (true) {
            if (transitionIndex < current) {
                return current - 8 * (level - 1);
            }
            current += 8 * level;
            level++;
        }
    }

    @Override
    public PetriNetInput parseOrGenerateProblem(final Parameters<Flag> options) throws IOException {
        return new ParserAndGenerator<PetriNetInput>(
            PetriNetAlgorithm::parseProblem,
            PetriNetAlgorithm::generateProblem
        ).getResult(options);
    }

}
