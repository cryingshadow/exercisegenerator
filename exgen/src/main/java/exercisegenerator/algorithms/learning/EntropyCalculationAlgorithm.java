package exercisegenerator.algorithms.learning;

import java.io.*;
import java.util.*;
import java.util.stream.*;

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
        final Map<String, String> attributeValues = Map.of();
        final String attribute = "";
        //TODO
        return new EntropyCalculationData(data, attributeValues, attribute);
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
        //TODO
        writer.write("Geben Sie jeweils den \\emphasize{Entscheidungsbaum} an, den der \\emphasize{ID3-Algorithmus} ");
        writer.write("zu den folgenden Trainingsdaten berechnet. Geben Sie dabei f\\\"ur jeden inneren Knoten die ");
        writer.write("gewichtete Entropie f\\\"ur jedes verf\\\"ugbare Attribut gerundet auf drei Nachkommastellen ");
        writer.write("an (also auch f\\\"ur diejenigen, die jeweils nicht als Selektionskriterium ausgew\\\"ahlt ");
        writer.write("werden). Unterstreichen Sie das jeweils gew\\\"ahlte Attribut:\\\\[2ex]");
        Main.newLine(writer);
    }

    @Override
    public void printBeforeSingleProblemInstance(
        final EntropyCalculationData problem,
        final SummedEntropyCalculation solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        //TODO
        writer.write("Geben Sie den \\emphasize{Entscheidungsbaum} an, den der \\emphasize{ID3-Algorithmus} zu den ");
        writer.write("folgenden Trainingsdaten berechnet. Geben Sie dabei f\\\"ur jeden inneren Knoten die ");
        writer.write("gewichtete Entropie f\\\"ur jedes verf\\\"ugbare Attribut gerundet auf drei Nachkommastellen ");
        writer.write("an (also auch f\\\"ur diejenigen, die jeweils nicht als Selektionskriterium ausgew\\\"ahlt ");
        writer.write("werden). Unterstreichen Sie das jeweils gew\\\"ahlte Attribut:\\\\[2ex]");
        Main.newLine(writer);
    }

    @Override
    public void printProblemInstance(
        final EntropyCalculationData problem,
        final SummedEntropyCalculation solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printAdjustboxBeginning(writer);
        writer.write("\\begin{tabular}{|*{");
        final Set<String> attributes =
            problem
            .data()
            .elements()
            .stream()
            .flatMap(element -> element.attributes().keySet().stream())
            .collect(Collectors.toCollection(TreeSet<String>::new));
        writer.write(String.valueOf(attributes.size() + 1));
        writer.write("}{c|}}");
        Main.newLine(writer);
        writer.write("\\hline");
        Main.newLine(writer);
        writer.write("\\multicolumn{");
        writer.write(String.valueOf(attributes.size()));
        writer.write("}{|c|}{Attribute} & Klassifikation\\\\\\hline");
        Main.newLine(writer);
        for (final String attribute : attributes) {
            writer.write("\\textbf{");
            writer.write(attribute);
            writer.write("} & ");
        }
        writer.write("\\textbf{");
        writer.write(problem.data().labelTitle());
        writer.write("}\\\\\\hline");
        Main.newLine(writer);
        for (final DecisionTreeDataElement element : problem.data().elements()) {
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
