package exercisegenerator.algorithms.cryptography;

public class VigenereEncryption implements VigenereAlgorithm {

    public static final VigenereEncryption INSTANCE = new VigenereEncryption();

    private VigenereEncryption() {}

    @Override
    public String commandPrefix() {
        return "ToVigenere";
    }

    @Override
    public boolean isEncoding() {
        return true;
    }

}
