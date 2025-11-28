package exercisegenerator.algorithms.cryptography;

public class VigenereDecryptionReverse implements VigenereAlgorithm {

    public static final VigenereDecryptionReverse INSTANCE = new VigenereDecryptionReverse();

    private VigenereDecryptionReverse() {}

    @Override
    public String commandPrefix() {
        return "FromVigenere";
    }

    @Override
    public boolean isEncodingAlgorithm() {
        return true;
    }

    @Override
    public boolean isEncodingTask() {
        return false;
    }

}
