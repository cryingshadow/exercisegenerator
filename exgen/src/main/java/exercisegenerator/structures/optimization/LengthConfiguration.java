package exercisegenerator.structures.optimization;

public record LengthConfiguration(String headerColLength, String numberLength, String arrowLength) {

    public LengthConfiguration() {
        this("6mm", "6mm", "7mm");
    }

}
