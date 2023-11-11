package exercisegenerator.structures.optimization;

public class DPPosition {

    public final int column;

    public final int row;

    public final int[][] solution;

    public DPPosition(final int[][] solution, final int row, final int column) {
        this.solution = solution;
        this.row = row;
        this.column = column;
    }

}