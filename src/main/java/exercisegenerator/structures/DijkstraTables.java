package exercisegenerator.structures;

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
}