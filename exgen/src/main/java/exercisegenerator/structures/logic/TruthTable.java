package exercisegenerator.structures.logic;

import java.util.*;

public class TruthTable {

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

    @Override
    public int hashCode() {
        return 111 + this.variables.hashCode() * 17 + this.truthValues.hashCode() * 23;
    }

}
