package exercisegenerator.algorithms.learning;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.graphs.*;
import exercisegenerator.structures.graphs.layout.*;
import exercisegenerator.structures.graphs.petrinets.*;

public class AlphaAlgorithm implements AlgorithmImplementation<EventLog, AlphaAlgorithmResult> {

    public static final String END_ACTIVITY = "\\blacksquare";

    public static final AlphaAlgorithm INSTANCE = new AlphaAlgorithm();

    public static final String START_ACTIVITY = "\\blacktriangleright";

    static Set<Set<String>> computeAdmissibleActivitySubsets(final FootprintMatrix footprint) {
        return AlphaAlgorithm.computeAdmissibleActivitySubsets(
            footprint.getActivities().stream().toList(),
            footprint
        ).stream().filter(set -> !set.isEmpty()).collect(Collectors.toSet());
    }

    static Set<PlaceCandidate> computeAdmissiblePlaces(
        final Set<Set<String>> firstCandidates,
        final FootprintMatrix footprint
    ) {
        final Set<PlaceCandidate> result = new LinkedHashSet<PlaceCandidate>();
        for (final Set<String> first : firstCandidates) {
            final Set<String> following = AlphaAlgorithm.computeFollowingActivities(first, footprint);
            for (final Set<String> admissible : AlphaAlgorithm.splitInAdmissibleSubsets(following, footprint)) {
                result.add(new PlaceCandidate(first, admissible));
            }
        }
        final Set<PlaceCandidate> tooSmall =
            result
            .stream()
            .filter(candidate ->
                result
                .stream()
                .anyMatch(other ->
                    !candidate.equals(other)
                    && other.followingActivities().containsAll(candidate.followingActivities())
                    && other.precedingActivities().containsAll(candidate.precedingActivities())
                )
            ).collect(Collectors.toSet());
        result.removeAll(tooSmall);
        return result;
    }

    static FootprintMatrix computeFootprint(final EventLog data) {
        final Set<String> activities =
            data.stream().flatMap(ActivityTrace::stream).collect(Collectors.toCollection(LinkedHashSet<String>::new));
        activities.add(AlphaAlgorithm.START_ACTIVITY);
        activities.add(AlphaAlgorithm.END_ACTIVITY);
        final FootprintMatrix result = new FootprintMatrix();
        for (final String firstActivity : activities) {
            for (final String secondActivity : activities) {
                result.put(firstActivity, secondActivity, FootprintRelation.EXCLUDED);
            }
        }
        for (final ActivityTrace trace : data) {
            if (trace.isEmpty()) {
                result.merge(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.END_ACTIVITY, FootprintRelation.FOLLOWED);
                result.merge(AlphaAlgorithm.END_ACTIVITY, AlphaAlgorithm.START_ACTIVITY, FootprintRelation.PRECDEDED);
            } else {
                result.merge(AlphaAlgorithm.START_ACTIVITY, trace.getFirst(), FootprintRelation.FOLLOWED);
                result.merge(trace.getFirst(), AlphaAlgorithm.START_ACTIVITY, FootprintRelation.PRECDEDED);
                result.merge(trace.getLast(), AlphaAlgorithm.END_ACTIVITY, FootprintRelation.FOLLOWED);
                result.merge(AlphaAlgorithm.END_ACTIVITY, trace.getLast(), FootprintRelation.PRECDEDED);
                for (int i = 0; i < trace.size() - 1; i++) {
                    final String firstActivity = trace.get(i);
                    final String secondActivity = trace.get(i + 1);
                    result.merge(firstActivity, secondActivity, FootprintRelation.FOLLOWED);
                    result.merge(secondActivity, firstActivity, FootprintRelation.PRECDEDED);
                }
            }
        }
        return result;
    }

    private static String columnDefinition(final int cols) {
        return String.format("|*{%d}{c|}", cols);
    }

    private static Set<Set<String>> computeAdmissibleActivitySubsets(
        final List<String> activities,
        final FootprintMatrix footprint
    ) {
        if (activities.isEmpty()) {
            return Set.of(Set.of());
        }
        final String activity = activities.getFirst();
        final List<String> remaining = activities.stream().skip(1).toList();
        final Set<Set<String>> subsets = AlphaAlgorithm.computeAdmissibleActivitySubsets(remaining, footprint);
        if (footprint.apply(activity, activity) != FootprintRelation.EXCLUDED) {
            return subsets;
        }
        return Stream.concat(
            subsets.stream(),
            subsets
            .stream()
            .filter(set -> !set.stream().anyMatch(a -> footprint.apply(a, activity) != FootprintRelation.EXCLUDED))
            .map(set -> Stream.concat(Stream.of(activity), set.stream()).collect(Collectors.toSet()))
        ).collect(Collectors.toSet());
    }

    private static Set<String> computeFollowingActivities(
        final Set<String> activities,
        final FootprintMatrix footprint
    ) {
        return footprint
            .getActivities()
            .stream()
            .filter(secondActivity ->
                activities
                .stream()
                .allMatch(firstActivity -> footprint.apply(firstActivity, secondActivity) == FootprintRelation.FOLLOWED)
            ).collect(Collectors.toSet());
    }

    private static AlphaAlgorithmResult computePetriNetAndLabeling(final FootprintMatrix footprint) {
        final Set<PlaceCandidate> places =
            AlphaAlgorithm.computeAdmissiblePlaces(
                AlphaAlgorithm.computeAdmissibleActivitySubsets(footprint),
                footprint
            );
        final PetriNetInput net =
            new PetriNetInput(
                new ArrayList<PetriPlace>(),
                new ArrayList<PetriTransition>(),
                List.of()
            );
        final Map<String, PetriTransition> transitions = new LinkedHashMap<String, PetriTransition>();
        for (final String activity : footprint.getActivities()) {
            final PetriTransition transition =
                new PetriTransition(
                    AlphaAlgorithm.isSpecial(activity) ? String.format("$%s$", activity) : activity,
                    0,
                    0,
                    new LinkedHashMap<Integer, Integer>(),
                    new LinkedHashMap<Integer, Integer>()
                );
            net.transitions().add(transition);
            transitions.put(activity, transition);
        }
        net.places().add(new PetriPlace(String.format("$p_{%s}$", AlphaAlgorithm.START_ACTIVITY), 0, 0, 270));
        net.places().add(new PetriPlace(String.format("$p_{%s}$", AlphaAlgorithm.END_ACTIVITY), 0, 0, 270));
        transitions.get(AlphaAlgorithm.START_ACTIVITY).from().put(0, 1);
        transitions.get(AlphaAlgorithm.END_ACTIVITY).to().put(1, 1);
        int index = 2;
        for (final PlaceCandidate candidate : places) {
            net.places().add(
                new PetriPlace(
                    String.format(
                        "$p_{\\{%s\\}, \\{%s\\}}$",
                        candidate
                        .precedingActivities()
                        .stream()
                        .map(s -> AlphaAlgorithm.isSpecial(s) ? s : String.format("\\text{%s}", s))
                        .collect(Collectors.joining(", ")),
                        candidate
                        .followingActivities()
                        .stream()
                        .map(s -> AlphaAlgorithm.isSpecial(s) ? s : String.format("\\text{%s}", s))
                        .collect(Collectors.joining(", "))
                    ),
                    0,
                    0,
                    270
                )
            );
            for (final String activity : candidate.precedingActivities()) {
                transitions.get(activity).to().put(index, 1);
            }
            for (final String activity : candidate.followingActivities()) {
                transitions.get(activity).from().put(index, 1);
            }
            index++;
        }
        return new AlphaAlgorithmResult(footprint, new PetriNet(net));
    }

    private static boolean isSpecial(final String activity) {
        return AlphaAlgorithm.START_ACTIVITY.equals(activity) || AlphaAlgorithm.END_ACTIVITY.equals(activity);
    }

    private static ActivityTrace parseTrace(final String line) {
        return Arrays.stream(line.split(",")).collect(Collectors.toCollection(ActivityTrace::new));
    }

    private static Set<Set<String>> splitInAdmissibleSubsets(
        final Set<String> activities,
        final FootprintMatrix footprint
    ) {
        if (activities.isEmpty()) {
            return Set.of();
        }
        final Set<String> conflicts =
            activities
            .stream()
            .filter(a -> !activities.stream().allMatch(b -> footprint.apply(a, b) == FootprintRelation.EXCLUDED))
            .collect(Collectors.toSet());
        if (conflicts.isEmpty()) {
            return Set.of(activities);
        }
        final Set<Set<String>> result = new LinkedHashSet<Set<String>>();
        for (final String conflict : conflicts) {
            result.addAll(
                AlphaAlgorithm.splitInAdmissibleSubsets(
                    activities.stream().filter(a -> !a.equals(conflict)).collect(Collectors.toSet()),
                    footprint
                )
            );
        }
        return result;
    }

    private AlphaAlgorithm() {}

    @Override
    public AlphaAlgorithmResult apply(final EventLog data) {
        return AlphaAlgorithm.computePetriNetAndLabeling(AlphaAlgorithm.computeFootprint(data));
    }

    @Override
    public String commandPrefix() {
        return "alpha";
    }

    @Override
    public EventLog generateProblem(final Parameters<Flag> options) {
        final int numberOfActivities = AlgorithmImplementation.parseOrGenerateLength(2, 5, options);
        final int numberOfTraces = Main.RANDOM.nextInt(9) + 2;
        final EventLog result = new EventLog();
        for (int i = 0; i < numberOfTraces; i++) {
            final int traceLength = Main.RANDOM.nextInt(11);
            final ActivityTrace trace = new ActivityTrace();
            for (int j = 0; j < traceLength; j++) {
                trace.add(String.valueOf((char)('a' + Main.RANDOM.nextInt(numberOfActivities))));
            }
            if (result.contains(trace)) {
                i--;
            } else {
                result.add(trace);
            }
        }
        return result;
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "4";
        return result;
    }

    @Override
    public List<EventLog> parseProblems(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        final EventLog result = new EventLog();
        String line = reader.readLine();
        while (line != null) {
            if (!line.isBlank()) {
                result.add(AlphaAlgorithm.parseTrace(line));
            }
            line = reader.readLine();
        }
        return List.of(result);
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<EventLog> problems,
        final List<AlphaAlgorithmResult> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Geben Sie jeweils die \\emphasize{Footprint-Matrix} sowie das \\emphasize{Petrinetz} an, ");
        writer.write("welche der \\emphasize{Alpha-Algorithmus} zu den folgenden Eventlogs berechnet:\\\\[2ex]");
        Main.newLine(writer);
    }

    @Override
    public void printBeforeSingleProblemInstance(
        final EventLog problem,
        final AlphaAlgorithmResult solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Geben Sie die \\emphasize{Footprint-Matrix} sowie das \\emphasize{Petrinetz} an, welche der ");
        writer.write("\\emphasize{Alpha-Algorithmus} zum folgenden Eventlog berechnet:\\\\[2ex]");
        Main.newLine(writer);
    }

    @Override
    public void printProblemInstance(
        final EventLog problem,
        final AlphaAlgorithmResult solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printAdjustboxBeginning(writer);
        LaTeXUtils.printMinipageBeginning(LaTeXUtils.LINE_WIDTH, writer);
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
        for (final ActivityTrace trace : problem) {
            writer.write("$\\langle ");
            writer.write(trace.stream().map(s -> String.format("\\text{%s}", s)).collect(Collectors.joining(", ")));
            writer.write(" \\rangle$\\\\");
            Main.newLine(writer);
        }
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
        LaTeXUtils.printMinipageEnd(writer);
        LaTeXUtils.printAdjustboxEnd(writer);
    }

    @Override
    public void printSolutionInstance(
        final EventLog problem,
        final AlphaAlgorithmResult solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Footprint:\\\\");
        Main.newLine(writer);
        LaTeXUtils.printTable(
            solution.footprint().toMatrix(),
            Optional.empty(),
            AlphaAlgorithm::columnDefinition,
            true,
            0,
            writer
        );
        LaTeXUtils.printVerticalProtectedSpace(writer);
        writer.write("Petrinetz:\\\\");
        Main.newLine(writer);
        final GraphWithVertexMappings graphWithVertexMappings = solution.net().toGraphWithVertexMappings();
        final ForceGraphLayout<String, Integer> layout =
            new ForceGraphLayout<String, Integer>(
                graphWithVertexMappings.graph(),
                TikZStyle.FORCE_GRAPH,
                1,
                1,
                2,
                2,
                100,
                3.0
            );
        final PetriNetInput net =
            new PetriNetInput(
                new ArrayList<PetriPlace>(),
                new ArrayList<PetriTransition>(),
                new ArrayList<Integer>()
            );
        final PetriPlace[] places = new PetriPlace[solution.net().getZeroMarking().size()];
        for (final Vertex<String> vertex : graphWithVertexMappings.graph().getVertices()) {
            final Object backwardMapping = graphWithVertexMappings.backwardsMapping().get(vertex);
            final Coordinates2D<Double> coordinates = layout.getPosition(vertex);
            if (backwardMapping instanceof final PetriPlace place) {
                places[graphWithVertexMappings.placeIndex().get(vertex)] =
                    new PetriPlace(place.label(), coordinates.x(), coordinates.y(), 270);
            } else {
                final PetriTransition transition = (PetriTransition)backwardMapping;
                net.transitions().add(
                    new PetriTransition(
                        transition.label(),
                        coordinates.x(),
                        coordinates.y(),
                        transition.from(),
                        transition.to()
                    )
                );
            }
        }
        for (int i = 0; i < places.length; i++) {
            if (i == 0) {
                net.tokens().add(1);
            } else {
                net.tokens().add(0);
            }
            net.places().add(places[i]);
        }
        LaTeXUtils.printAdjustboxBeginning(writer);
        new PetriNet(net).toTikz(PetriMarking.create(net.tokens()), writer);
        LaTeXUtils.printAdjustboxEnd(writer);
    }

    @Override
    public void printSolutionSpace(
        final EventLog problem,
        final AlphaAlgorithmResult solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        // do nothing
    }

}
