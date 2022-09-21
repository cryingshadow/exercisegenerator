package exercisegenerator.structures.logic;

import java.math.*;
import java.util.*;

public class TruthTable {

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

    private final boolean[] truthValues;

    private final List<String> variables;

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

}
