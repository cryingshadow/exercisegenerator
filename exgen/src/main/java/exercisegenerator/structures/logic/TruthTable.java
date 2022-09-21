package exercisegenerator.structures.logic;

import java.math.*;
import java.util.*;

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
