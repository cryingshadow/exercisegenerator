package exercisegenerator.structures.optimization;

public record LengthConfiguration(String headerColLength, String numberLength, String arrowLength) {

    public LengthConfiguration() {
        this("5mm", "5mm", "7mm");
    }

}
