package exercisegenerator.algorithms.learning;

import java.io.*;
import java.util.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.learning.*;

public class EntropyCalculationAlgorithm
implements AlgorithmImplementation<EntropyCalculationData, SummedEntropyCalculation> {

    public static final EntropyCalculationAlgorithm INSTANCE = new EntropyCalculationAlgorithm();

    private EntropyCalculationAlgorithm() {

    }

    @Override
    public SummedEntropyCalculation apply(final EntropyCalculationData data) {
        final List<DecisionTreeDataElement> elements =
            data
            .data()
            .elements()
            .stream()
            .filter(element ->
                element
                .attributes()
                .entrySet()
                .stream()
                .allMatch(entry ->
                    !data.attributeValues().containsKey(entry.getKey())
                    || data.attributeValues().get(entry.getKey()).equals(entry.getValue())
                )
            ).toList();
        return ID3Algorithm.calculateAverageEntropy(
            ID3Algorithm.splitByAttribute(data.attribute(), elements).values().stream().toList()
        );
    }

    @Override
    public String commandPrefix() {
        return "entropycalculation";
    }

    @Override
    public EntropyCalculationData generateProblem(final Parameters<Flag> options) {
        final DecisionTreeData data = ID3Algorithm.INSTANCE.generateProblem(options);
        final DecisionTreeDataElement element = data.elements().get(Main.RANDOM.nextInt(data.elements().size()));
        final List<String> attributes = new ArrayList<String>(element.attributes().keySet());
        Collections.shuffle(attributes);
        final int fixed = attributes.size() > 2 ? Main.RANDOM.nextInt(attributes.size() - 2) : 0;
        final Map<String, String> attributeValues = new TreeMap<String, String>();
        for (int i = 0; i < fixed; i++) {
            final String attribute = attributes.get(i);
            attributeValues.put(attribute, element.attributes().get(attribute));
        }
        return new EntropyCalculationData(data, attributeValues, attributes.get(fixed));
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "10";
        return result;
    }

    @Override
    public List<EntropyCalculationData> parseProblems(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        final List<DecisionTreeData> problems = ID3Algorithm.INSTANCE.parseProblems(reader, options);
        final String[] attributeSplit = options.get(Flag.OPERATIONS).split(";");
        final Map<String, String> attributeValues = new LinkedHashMap<String, String>();
        for (int i = 0; i < attributeSplit.length - 1; i++) {
            final String[] split = attributeSplit[i].split("=");
            attributeValues.put(split[0], split[1]);
        }
        return List.of(
            new EntropyCalculationData(problems.getFirst(), attributeValues, attributeSplit[attributeSplit.length - 1])
        );
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<EntropyCalculationData> problems,
        final List<SummedEntropyCalculation> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Berechnen Sie die gewichtete Entropie f\\\"ur das jeweils angegebene Merkmal in den jeweiligen ");
        writer.write("Datenmengen unter Ber\\\"ucksichtigung der angegebenen festgelegten Merkmale.");
        Main.newLine(writer);
    }

    @Override
    public void printBeforeSingleProblemInstance(
        final EntropyCalculationData problem,
        final SummedEntropyCalculation solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write(
            "Berechnen Sie die gewichtete Entropie f\\\"ur das angegebene Merkmal in der folgenden Datenmenge"
        );
        if (!problem.attributeValues().isEmpty()) {
            writer.write(" unter Ber\\\"ucksichtigung der angegebenen festgelegten Merkmale");
        }
        writer.write(".");
        Main.newLine(writer);
    }

    @Override
    public void printProblemInstance(
        final EntropyCalculationData problem,
        final SummedEntropyCalculation solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        ID3Algorithm.INSTANCE.printProblemInstance(problem.data(), new DecisionTreeLeaf(""), options, writer);
        LaTeXUtils.printVerticalProtectedSpace(writer);
        if (!problem.attributeValues().isEmpty()) {
            writer.write("Festgelegte Merkmale:\\\\");
            Main.newLine(writer);
            for (final Map.Entry<String, String> entry : problem.attributeValues().entrySet()) {
                writer.write(entry.getKey());
                writer.write(" = ");
                writer.write(entry.getValue());
                writer.write("\\\\");
                Main.newLine(writer);
            }
            LaTeXUtils.printVerticalProtectedSpace(writer);
        }
        writer.write("Entropie-Merkmal: ");
        writer.write(problem.attribute());
        Main.newLine(writer);
    }

    @Override
    public void printSolutionInstance(
        final EntropyCalculationData problem,
        final SummedEntropyCalculation solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write(LaTeXUtils.inlineMath(solution.toLaTeX()));
        Main.newLine(writer);
    }

    @Override
    public void printSolutionSpace(
        final EntropyCalculationData problem,
        final SummedEntropyCalculation solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        // do nothing
    }

}
