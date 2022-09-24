package exercisegenerator.structures.logic;

import java.math.*;
import java.util.*;

import exercisegenerator.io.*;

public class TruthTable {

    public static List<PropositionalInterpretation> computeAllInterpretations(final List<String> variables) {
        final List<PropositionalInterpretation> result = new LinkedList<PropositionalInterpretation>();
        final BigInteger size = BigInteger.TWO.pow(variables.size());
        BigInteger current = BigInteger.ZERO;
        while (current.compareTo(size) < 0) {
            result.add(TruthTable.toInterpretation(current, variables));
            current = current.add(BigInteger.ONE);
        }
        return result;
    }

    public static TruthTable parse(final String line) throws TruthTableParseException {
        final String[] parts = line.strip().split(";");
        if (parts.length != 2) {
            throw new TruthTableParseException();
        }
        final List<String> variables =
            parts[0].isBlank() ?
                Collections.emptyList() :
                    Arrays.stream(parts[0].split(",")).map(String::strip).toList();
        final boolean[] truthValues = new boolean[(int)Math.pow(2, variables.size())];
        final char[] bits = parts[1].strip().toCharArray();
        if (bits.length != truthValues.length) {
            throw new TruthTableParseException();
        }
        for (int i = 0; i < truthValues.length; i++) {
            if (bits[i] != '0' && bits[i] != '1') {
                throw new TruthTableParseException();
            }
            truthValues[i] = bits[i] == '1';
        }
        return new TruthTable(variables, truthValues);
    }

    public static PropositionalInterpretation toInterpretation(
        final BigInteger current,
        final List<String> variables
    ) {
        final PropositionalInterpretation result = new PropositionalInterpretation();
        int i = variables.size() - 1;
        for (final String variable : variables) {
            result.put(variable, current.testBit(i--));
        }
        return result;
    }

    private static String toString(final PropositionalInterpretation interpretation) {
        final StringBuilder result = new StringBuilder();
        result.append("|");
        for (final Map.Entry<String, Boolean> entry : interpretation.entrySet()) {
            result.append(entry.getValue() ? "1" : "0");
            result.append("|");
        }
        return result.toString();
    }

    public final boolean[] truthValues;

    public final List<String> variables;

    public TruthTable(final List<String> variables, final boolean[] truthValues) {
        this.variables = variables;
        this.truthValues = truthValues;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof TruthTable)) {
            return false;
        }
        final TruthTable other = (TruthTable)o;
        return Arrays.equals(this.truthValues, other.truthValues) && this.variables.equals(other.variables);
    }

    public Map<PropositionalInterpretation, Boolean> getAllInterpretationsAndValues() {
        final Map<PropositionalInterpretation, Boolean> result =
            new LinkedHashMap<PropositionalInterpretation, Boolean>();
        int index = 0;
        for (final PropositionalInterpretation interpretation : TruthTable.computeAllInterpretations(this.variables)) {
            result.put(interpretation, this.truthValues[index++]);
        }
        return result;
    }

    public List<PropositionalInterpretation> getModels() {
        final List<PropositionalInterpretation> result = new LinkedList<PropositionalInterpretation>();
        BigInteger index = BigInteger.ZERO;
        while (index.intValue() < this.truthValues.length) {
            if (this.truthValues[index.intValue()]) {
                result.add(TruthTable.toInterpretation(index, this.variables));
            }
            index = index.add(BigInteger.ONE);
        }
        return result;
    }

    @Override
    public int hashCode() {
        return 111 + this.variables.hashCode() * 17 + this.truthValues.hashCode() * 23;
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        final String line = "-".repeat((this.variables.size() + 1) * 2 + 1) + "\n";
        result.append(line);
        result.append("|");
        for (final String name : this.variables) {
            result.append(name);
            result.append("|");
        }
        result.append("\n");
        result.append(line);
        for (final Map.Entry<PropositionalInterpretation, Boolean> entry : this.getAllInterpretationsAndValues().entrySet()) {
            result.append("|");
            result.append(TruthTable.toString(entry.getKey()));
            result.append("|");
            result.append(entry.getValue() ? "1" : "0");
            result.append("|");
            result.append("\n");
            result.append(line);
        }
        return result.toString();
    }

}
