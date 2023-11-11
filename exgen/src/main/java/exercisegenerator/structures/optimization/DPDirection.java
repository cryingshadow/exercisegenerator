package exercisegenerator.structures.optimization;

public enum DPDirection {

    LEFT("$\\leftarrow$", 1, 0),

    UP("$\\uparrow$", 0, 1),

    UPLEFT("$\\nwarrow$", 1, 1);

    public final int horizontalDiff;

    public final String symbol;

    public final int verticalDiff;

    private DPDirection(final String symbol, final int horizontalDiff, final int verticalDiff) {
        this.symbol = symbol;
        this.horizontalDiff = horizontalDiff;
        this.verticalDiff = verticalDiff;
    }

}