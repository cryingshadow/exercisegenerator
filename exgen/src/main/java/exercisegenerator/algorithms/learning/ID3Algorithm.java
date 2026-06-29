package exercisegenerator.algorithms.learning;

import java.io.*;
import java.util.*;
import java.util.Map.*;
import java.util.stream.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.learning.*;

public class ID3Algorithm implements AlgorithmImplementation<DecisionTreeData, DecisionTree>{

    public static final ID3Algorithm INSTANCE = new ID3Algorithm();

    private static final Map<String, List<String>> CATEGORIES;

    private static final Map<String, List<String>> LABELS;

    static {
        CATEGORIES = new LinkedHashMap<String, List<String>>();
        ID3Algorithm.CATEGORIES.put(
            "Wetter",
            List.of("Sonne", "Wolken", "Nebel", "Regen", "Gewitter", "Schnee", "Wind")
        );
        ID3Algorithm.CATEGORIES.put(
            "Alter",
            List.of(
                "unter 3 Jahre",
                "3 bis 11 Jahre",
                "12 bis 17 Jahre",
                "18 bis 29 Jahre",
                "30 bis 59 Jahre",
                "60 bis 80 Jahre",
                "über 80 Jahre"
            )
        );
        ID3Algorithm.CATEGORIES.put("Geschlecht", List.of("männlich", "weiblich", "divers"));
        ID3Algorithm.CATEGORIES.put(
            "Größe",
            List.of("unter 10 cm", "10 bis 49 cm", "50 bis 99 cm", "100 bis 200 cm", "über 200 cm")
        );
        ID3Algorithm.CATEGORIES.put(
            "Farbe",
            List.of("rot", "grün", "blau", "gelb", "orange", "lila", "cyan", "schwarz", "weiß")
        );
        ID3Algorithm.CATEGORIES.put("Herkunft", List.of("Europa", "Asien", "Amerika", "Afrika", "Australien"));
        ID3Algorithm.CATEGORIES.put(
            "Lautstärke",
            List.of("unter 20 dB", "20 bis 59 dB", "60 bis 89 dB", "90 bis 120 dB", "über 120 dB")
        );
        ID3Algorithm.CATEGORIES.put(
            "Gewicht",
            List.of(
                "unter 1 kg",
                "1 bis 4 kg",
                "5 bis 9 kg",
                "10 bis 19 kg",
                "20 bis 49 kg",
                "50 bis 100 kg",
                "über 100 kg"
            )
        );
        ID3Algorithm.CATEGORIES.put("Form", List.of("eckig", "rund", "gerade", "komplex"));
        ID3Algorithm.CATEGORIES.put(
            "Geschwindigkeit",
            List.of(
                "unter 5 km/h",
                "5 bis 19 km/h",
                "20 bis 59 km/h",
                "60 bis 120 km/h",
                "über 120 km/h"
            )
        );
        LABELS =
            Map.of(
                "Antwort", List.of("Ja", "Nein"),
                "Note", List.of("Sehr gut", "Gut", "Befriedigend", "Ausreichend", "Mangelhaft"),
                "Qualität", List.of("Gut", "Mittel", "Schlecht"),
                "Aktion", List.of("spazieren", "arbeiten", "einkaufen", "ausruhen"),
                "Art", List.of("Tier", "Pflanze", "Pilz", "Protist")
            );
    }

    static Map<String, Double> calculateAverageEntropies(
        final List<DecisionTreeDataElement> data,
        final Set<String> used
    ) {
        final Map<String, Double> result = new TreeMap<String, Double>();
        final Set<String> attributes =
            data
            .stream()
            .flatMap(element -> element.attributes().keySet().stream())
            .filter(element -> !used.contains(element))
            .collect(Collectors.toSet());
        for (final String attribute : attributes) {
            result.put(
                attribute,
                ID3Algorithm.calculateAverageEntropy(
                    ID3Algorithm.splitByAttribute(attribute, data).values().stream().toList()
                )
            );
        }
        return result;
    }

    static double calculateAverageEntropy(final List<List<DecisionTreeDataElement>> data) {
        final double total = data.stream().mapToInt(set -> set.size()).sum();
        if (total == 0.0) {
            return 0.0;
        }
        Double result = 0.0;
        for (final List<DecisionTreeDataElement> subset : data) {
            final double entropy = ID3Algorithm.calculateEntropy(subset);
            result += (subset.size() / total) * entropy;
        }
        return result;
    }

    static double calculateEntropy(final List<DecisionTreeDataElement> data) {
        final Map<String, Integer> labelCounts = new LinkedHashMap<String, Integer>();
        for (final DecisionTreeDataElement element : data) {
            labelCounts.merge(element.label(), 1, Integer::sum);
        }
        if (labelCounts.size() < 2) {
            return 0.0;
        }
        final double total = data.size();
        double result = 0.0;
        for (final Integer count : labelCounts.values()) {
            final double frac = count / total;
            result += -frac * Math.log(frac) / Math.log(2);
        }
        return result;
    }

    static DecisionTree id3(final List<DecisionTreeDataElement> data, final Set<String> used) {
        final Set<String> labels = data.stream().map(DecisionTreeDataElement::label).collect(Collectors.toSet());
        if (labels.size() == 1) {
            return new DecisionTreeLeaf(labels.iterator().next());
        }
        final Map<String, Double> entropies = ID3Algorithm.calculateAverageEntropies(data, used);
        if (entropies.isEmpty()) {
            return new DecisionTreeLeaf(ID3Algorithm.majorityVote(data));
        }
        final String attribute =
            entropies.entrySet().stream().sorted(
                new Comparator<Map.Entry<String, Double>>() {

                    @Override
                    public int compare(final Map.Entry<String, Double> o1, final Map.Entry<String, Double> o2) {
                        return o1.getValue().compareTo(o2.getValue());
                    }

                }
            ).findFirst().get().getKey();
        final Set<String> nextUsed = Stream.concat(Stream.of(attribute), used.stream()).collect(Collectors.toSet());
        return new DecisionTreeInnerNode(
            ID3Algorithm
            .splitByAttribute(attribute, data)
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    entry -> entry.getKey(),
                    entry -> ID3Algorithm.id3(entry.getValue(), nextUsed),
                    (x, y) -> x,
                    TreeMap<String, DecisionTree>::new
                )
            ),
            attribute,
            entropies,
            nextUsed
        );
    }

    static Map<String, List<DecisionTreeDataElement>> splitByAttribute(
        final String attribute,
        final List<DecisionTreeDataElement> data
    ) {
        final Map<String, List<DecisionTreeDataElement>> result =
            new TreeMap<String, List<DecisionTreeDataElement>>();
        for (final DecisionTreeDataElement element : data) {
            result.merge(
                element.attributes().get(attribute),
                List.of(element),
                (x,y) -> Stream.of(x,y).flatMap(List::stream).toList()
            );
        }
        return result;
    }

    private static String majorityVote(final List<DecisionTreeDataElement> data) {
        final Map<String, Integer> count = new LinkedHashMap<String, Integer>();
        for (final DecisionTreeDataElement element : data) {
            count.merge(element.label(), 1, Integer::sum);
        }
        return count.entrySet().stream().sorted(
            new Comparator<Map.Entry<String, Integer>>() {

                @Override
                public int compare(final Entry<String, Integer> o1, final Entry<String, Integer> o2) {
                    return o1.getValue().compareTo(o2.getValue());
                }

            }
        ).findFirst().get().getKey();
    }

    private ID3Algorithm() {

    }

    @Override
    public DecisionTree apply(final DecisionTreeData data) {
        return ID3Algorithm.id3(data.elements(), Set.of());
    }

    @Override
    public String commandPrefix() {
        return "idiii";
    }

    @Override
    public DecisionTreeData generateProblem(final Parameters<Flag> options) {
        final int numberOfElements = AlgorithmImplementation.parseOrGenerateLength(10, 30, options);
        final List<String> attributes = new ArrayList<String>(ID3Algorithm.CATEGORIES.keySet());
        final int numberOfAttributes = Main.RANDOM.nextInt(8) + 3;
        while (attributes.size() > numberOfAttributes) {
            attributes.remove(Main.RANDOM.nextInt(attributes.size()));
        }
        final List<Entry<String, List<String>>> labelsAsList = ID3Algorithm.LABELS.entrySet().stream().toList();
        final Map.Entry<String, List<String>> labels = labelsAsList.get(Main.RANDOM.nextInt(labelsAsList.size()));
        final List<DecisionTreeDataElement> result = new ArrayList<DecisionTreeDataElement>();
        for (int i = 0; i < numberOfElements; i++) {
            final Map<String, String> currentAttributes = new TreeMap<String, String>();
            for (final String attribute : attributes) {
                final List<String> categories = ID3Algorithm.CATEGORIES.get(attribute);
                currentAttributes.put(attribute, categories.get(Main.RANDOM.nextInt(categories.size())));
            }
            result.add(
                new DecisionTreeDataElement(
                    currentAttributes,
                    labels.getValue().get(Main.RANDOM.nextInt(labels.getValue().size()))
                )
            );
        }
        return new DecisionTreeData(result, labels.getKey());
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "10";
        return result;
    }

    @Override
    public List<DecisionTreeData> parseProblems(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        final List<DecisionTreeDataElement> result = new ArrayList<DecisionTreeDataElement>();
        String line = reader.readLine();
        final String[] attributes = line.split(";");
        line = reader.readLine();
        while (line != null) {
            if (!line.isBlank()) {
                final String[] currentElement = line.split(";");
                final Map<String, String> currentAttributes = new TreeMap<String, String>();
                for (int i = 0; i < attributes.length - 1; i++) {
                    currentAttributes.put(attributes[i], currentElement[i]);
                }
                result.add(
                    new DecisionTreeDataElement(
                        currentAttributes,
                        currentElement[currentElement.length - 1]
                    )
                );
            }
            line = reader.readLine();
        }
        return List.of(new DecisionTreeData(result, attributes[attributes.length - 1]));
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<DecisionTreeData> problems,
        final List<DecisionTree> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Geben Sie jeweils den \\emphasize{Entscheidungsbaum} an, den der ID3-Algorithmus zu den ");
        writer.write("folgenden Trainingsdaten berechnet. Geben Sie dabei f\\\"ur jeden inneren Knoten die ");
        writer.write("gewichtete Entropie f\\\"ur jedes verf\\\"ugbare Attribut gerundet auf drei Nachkommastellen ");
        writer.write("an (also auch f\\\"ur diejenigen, die jeweils nicht als Selektionskriterium ausgew\\\"ahlt ");
        writer.write("werden). Unterstreichen Sie das jeweils gew\\\"ahlte Attribut:\\\\[2ex]");
        Main.newLine(writer);
    }

    @Override
    public void printBeforeSingleProblemInstance(
        final DecisionTreeData problem,
        final DecisionTree solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Geben Sie den \\emphasize{Entscheidungsbaum} an, den der ID3-Algorithmus zu den folgenden ");
        writer.write("Trainingsdaten berechnet. Geben Sie dabei f\\\"ur jeden inneren Knoten die gewichtete Entropie ");
        writer.write("f\\\"ur jedes verf\\\"ugbare Attribut gerundet auf drei Nachkommastellen an (also auch f\\\"ur ");
        writer.write("diejenigen, die jeweils nicht als Selektionskriterium ausgew\\\"ahlt werden). Unterstreichen ");
        writer.write("Sie das jeweils gew\\\"ahlte Attribut:\\\\[2ex]");
        Main.newLine(writer);
    }

    @Override
    public void printProblemInstance(
        final DecisionTreeData problem,
        final DecisionTree solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printAdjustboxBeginning(writer);
        writer.write("\\begin{tabular}{|*{");
        final Set<String> attributes =
            problem
            .elements()
            .stream()
            .flatMap(element -> element.attributes().keySet().stream())
            .collect(Collectors.toCollection(TreeSet<String>::new));
        writer.write(String.valueOf(attributes.size() + 1));
        writer.write("}{c|}}");
        Main.newLine(writer);
        writer.write("\\hline");
        Main.newLine(writer);
        for (final String attribute : attributes) {
            writer.write("\\textbf{");
            writer.write(attribute);
            writer.write("} & ");
        }
        writer.write("\\textbf{");
        writer.write(problem.labelTitle());
        writer.write("}\\\\\\hline");
        Main.newLine(writer);
        for (final DecisionTreeDataElement element : problem.elements()) {
            for (final Map.Entry<String, String> attributeEntry : element.attributes().entrySet()) {
                writer.write(attributeEntry.getValue());
                writer.write(" & ");
            }
            writer.write(element.label());
            writer.write("\\\\\\hline");
            Main.newLine(writer);
        }
        writer.write("\\end{tabular}");
        Main.newLine(writer);
        LaTeXUtils.printAdjustboxEnd(writer);
    }

    @Override
    public void printSolutionInstance(
        final DecisionTreeData problem,
        final DecisionTree solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printAdjustboxBeginning(writer);
        LaTeXUtils.printTikzBeginning(TikZStyle.DPLLTREE, writer);
        writer.write(solution.toString());
        Main.newLine(writer);
        LaTeXUtils.printTikzEnd(writer);
        LaTeXUtils.printAdjustboxEnd(writer);
    }

    @Override
    public void printSolutionSpace(
        final DecisionTreeData problem,
        final DecisionTree solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        // do nothing
    }

}
