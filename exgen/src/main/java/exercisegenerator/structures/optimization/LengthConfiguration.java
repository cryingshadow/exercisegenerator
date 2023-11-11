package exercisegenerator.structures.optimization;

public class LengthConfiguration {

    public final String arrowLength;

    public final String headerColLength;

    public final String numberLength;

    public LengthConfiguration() {
        this("5mm", "5mm", "7mm");
    }

    public LengthConfiguration(
        final String headerColLength,
        final String numberLength,
        final String arrowLength
    ) {
        this.headerColLength = headerColLength;
        this.numberLength = numberLength;
        this.arrowLength = arrowLength;
    }

}
