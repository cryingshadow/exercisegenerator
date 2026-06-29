package exercisegenerator.algorithms.learning;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.learning.*;

public class ID3 implements AlgorithmImplementation<List<DecisionTreeData>, DecisionTree>{

    public static final ID3 INSTANCE = new ID3();

    private static final Map<String, List<String>> CATEGORIES;

    private static final List<List<String>> LABELS;

    static {
        CATEGORIES = new LinkedHashMap<String, List<String>>();
        ID3.CATEGORIES.put("Wetter", List.of("Sonne", "Wolken", "Nebel", "Regen", "Gewitter", "Schnee", "Wind"));
        ID3.CATEGORIES.put(
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
        ID3.CATEGORIES.put("Geschlecht", List.of("männlich", "weiblich", "divers"));
        ID3.CATEGORIES.put(
            "Größe",
            List.of("unter 10 cm", "10 bis 49 cm", "50 bis 99 cm", "100 bis 200 cm", "über 200 cm")
        );
        ID3.CATEGORIES.put(
            "Farbe",
            List.of("rot", "grün", "blau", "gelb", "orange", "lila", "cyan", "schwarz", "weiß")
        );
        ID3.CATEGORIES.put("Herkunft", List.of("Europa", "Asien", "Amerika", "Afrika", "Australien"));
        ID3.CATEGORIES.put(
            "Lautstärke",
            List.of("unter 20 dB", "20 bis 59 dB", "60 bis 89 dB", "90 bis 120 dB", "über 120 dB")
        );
        ID3.CATEGORIES.put(
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
        ID3.CATEGORIES.put("Form", List.of("eckig", "rund", "gerade", "komplex"));
        ID3.CATEGORIES.put(
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
            List.of(
                List.of("Ja", "Nein"),
                List.of("Sehr gut", "Gut", "Befriedigend", "Ausreichend", "Mangelhaft"),
                List.of("Gut", "Mittel", "Schlecht"),
                List.of("spazieren", "arbeiten", "einkaufen", "ausruhen"),
                List.of("Tier", "Pflanze", "Pilz", "Protist")
            );
    }

    static double calculateAverageEntropy(final List<List<DecisionTreeData>> data) {
        final double total = data.stream().mapToInt(set -> set.size()).sum();
        if (total == 0.0) {
            return 0.0;
        }
        Double result = 0.0;
        for (final List<DecisionTreeData> subset : data) {
            final double entropy = ID3.calculateEntropy(subset);
            result += (subset.size() / total) * entropy;
        }
        return result;
    }

    static double calculateEntropy(final List<DecisionTreeData> data) {
        final Map<String, Integer> labelCounts = new LinkedHashMap<String, Integer>();
        for (final DecisionTreeData element : data) {
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

    static String selectAttribute(final List<DecisionTreeData> data) {
        final Set<String> attributes =
            data.stream().flatMap(element -> element.attributes().keySet().stream()).collect(Collectors.toSet());
        String bestAttribute = attributes.iterator().next();
        double minEntropy = ID3.calculateEntropy(data);
        for (final String attribute : attributes) {
            final double entropy =
                ID3.calculateAverageEntropy(ID3.splitByAttribute(attribute, data).values().stream().toList());
            if (entropy < minEntropy) {
                minEntropy = entropy;
                bestAttribute = attribute;
            }
        }
        return bestAttribute;
    }

    static Map<String, List<DecisionTreeData>> splitByAttribute(
        final String attribute,
        final List<DecisionTreeData> data
    ) {
        final Map<String, List<DecisionTreeData>> result = new LinkedHashMap<String, List<DecisionTreeData>>();
        for (final DecisionTreeData element : data) {
            result.merge(
                element.attributes().get(attribute),
                List.of(element),
                (x,y) -> Stream.of(x,y).flatMap(List::stream).toList()
            );
        }
        return result;
    }

    private ID3() {

    }

    @Override
    public DecisionTree apply(final List<DecisionTreeData> data) {
        final Set<String> labels = data.stream().map(DecisionTreeData::label).collect(Collectors.toSet());
        if (labels.size() == 1) {
            return new DecisionTreeLeaf(labels.iterator().next());
        }
        final String attribute = ID3.selectAttribute(data);
        return new DecisionTreeInnerNode(
            ID3
            .splitByAttribute(attribute, data)
            .entrySet()
            .stream()
            .collect(Collectors.toMap(entry -> entry.getKey(), entry -> this.apply(entry.getValue()))),
            attribute
        );
    }

    @Override
    public String commandPrefix() {
        return "idiii";
    }

    @Override
    public List<DecisionTreeData> generateProblem(final Parameters<Flag> options) {
        final int numberOfElements = AlgorithmImplementation.parseOrGenerateLength(10, 30, options);
        final List<String> attributes = new ArrayList<String>(ID3.CATEGORIES.keySet());
        final int numberOfAttributes = Main.RANDOM.nextInt(8) + 3;
        while (attributes.size() > numberOfAttributes) {
            attributes.remove(Main.RANDOM.nextInt(attributes.size()));
        }
        final List<String> labels = ID3.LABELS.get(Main.RANDOM.nextInt(ID3.LABELS.size()));
        final List<DecisionTreeData> result = new ArrayList<DecisionTreeData>();
        for (int i = 0; i < numberOfElements; i++) {
            final Map<String, String> currentAttributes = new LinkedHashMap<String, String>();
            for (final String attribute : attributes) {
                final List<String> categories = ID3.CATEGORIES.get(attribute);
                currentAttributes.put(attribute, categories.get(Main.RANDOM.nextInt(categories.size())));
            }
            result.add(new DecisionTreeData(currentAttributes, labels.get(Main.RANDOM.nextInt(labels.size()))));
        }
        return result;
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "10";
        return result;
    }

    @Override
    public List<List<DecisionTreeData>> parseProblems(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        final List<DecisionTreeData> result = new ArrayList<DecisionTreeData>();
        String line = reader.readLine();
        final String[] attributes = line.split(";");
        line = reader.readLine();
        while (line != null) {
            if (!line.isBlank()) {
                final String[] currentElement = line.split(";");
                final Map<String, String> currentAttributes = new LinkedHashMap<String, String>();
                for (int i = 0; i < attributes.length; i++) {
                    currentAttributes.put(attributes[i], currentElement[i]);
                }
                result.add(new DecisionTreeData(currentAttributes, currentElement[currentElement.length - 1]));
            }
            line = reader.readLine();
        }
        return List.of(result);
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<List<DecisionTreeData>> problems,
        final List<DecisionTree> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void printBeforeSingleProblemInstance(
        final List<DecisionTreeData> problem,
        final DecisionTree solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void printProblemInstance(
        final List<DecisionTreeData> problem,
        final DecisionTree solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void printSolutionInstance(
        final List<DecisionTreeData> problem,
        final DecisionTree solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void printSolutionSpace(
        final List<DecisionTreeData> problem,
        final DecisionTree solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        // TODO Auto-generated method stub

    }

}
