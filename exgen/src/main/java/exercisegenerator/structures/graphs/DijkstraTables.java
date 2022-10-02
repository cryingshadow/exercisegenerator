package exercisegenerator.structures.graphs;

import java.util.*;

import exercisegenerator.util.*;

public class DijkstraTables {

    public final String[][] exColor;
    public final String[][] exTable;
    public final String[][] solColor;
    public final String[][] solTable;

    public DijkstraTables(
        final String[][] exTable,
        final String[][] exColor,
        final String[][] solTable,
        final String[][] solColor
    ) {
        this.exTable = exTable;
        this.exColor = exColor;
        this.solTable = solTable;
        this.solColor = solColor;
    }

    public DijkstraTables(
        final String[][] exTable,
        final String[][] exColor,
        final String[][] solTable,
        final String[][] solColor,
        final boolean transpose
    ) {
        this(
            ArrayUtils.transpose(exTable, transpose),
            ArrayUtils.transpose(exColor, transpose),
            ArrayUtils.transpose(solTable, transpose),
            ArrayUtils.transpose(solColor, transpose)
        );
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof DijkstraTables)) {
            return false;
        }
        final DijkstraTables other = (DijkstraTables)o;
        if (
            this.exTable.length != other.exTable.length
            || this.solTable.length != other.solTable.length
            || this.exColor.length != other.exColor.length
            || this.solColor.length != other.solColor.length
        ) {
            return false;
        }
        for (int i = 0; i < this.exTable.length; i++) {
            if (!Arrays.equals(this.exTable[i], other.exTable[i])) {
                return false;
            }
        }
        for (int i = 0; i < this.solTable.length; i++) {
            if (!Arrays.equals(this.solTable[i], other.solTable[i])) {
                return false;
            }
        }
        for (int i = 0; i < this.exColor.length; i++) {
            if (!Arrays.equals(this.exColor[i], other.exColor[i])) {
                return false;
            }
        }
        for (int i = 0; i < this.solColor.length; i++) {
            if (!Arrays.equals(this.solColor[i], other.solColor[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append("exTable: [\n");
        for (int i = 0; i < this.exTable.length; i++) {
            result.append(Arrays.toString(this.exTable[i]));
            result.append("\n");
        }
        result.append("]\n");
        result.append("solTable: [\n");
        for (int i = 0; i < this.solTable.length; i++) {
            result.append(Arrays.toString(this.solTable[i]));
            result.append("\n");
        }
        result.append("]\n");
        result.append("solColor: [\n");
        for (int i = 0; i < this.solColor.length; i++) {
            result.append(Arrays.toString(this.solColor[i]));
            result.append("\n");
        }
        result.append("]\n");
        result.append("exColor: [\n");
        for (int i = 0; i < this.exColor.length; i++) {
            result.append(Arrays.toString(this.exColor[i]));
            result.append("\n");
        }
        result.append("]");
        return result.toString();
    }

}