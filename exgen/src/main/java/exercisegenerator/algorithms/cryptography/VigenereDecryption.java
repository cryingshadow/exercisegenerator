package exercisegenerator.algorithms.cryptography;

public class VigenereDecryption implements VigenereAlgorithm {

    public static final VigenereDecryption INSTANCE = new VigenereDecryption();

    private VigenereDecryption() {}

    @Override
    public String commandPrefix() {
        return "FromVigenere";
    }

    @Override
    public boolean isEncodingAlgorithm() {
        return false;
    }

    @Override
    public boolean isEncodingTask() {
        return false;
    }

}
