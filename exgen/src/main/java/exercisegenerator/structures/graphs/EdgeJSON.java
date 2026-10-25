package exercisegenerator.structures.graphs;

public record EdgeJSON(
    String from,
    String label,
    String to,
    String labelStyle,
    String edgeStyle
) {

    @Override
    public String labelStyle() {
        return this.labelStyle == null ? "" : this.labelStyle;
    }

    @Override
    public String edgeStyle() {
        return this.edgeStyle == null ? "" : this.edgeStyle;
    }

}
