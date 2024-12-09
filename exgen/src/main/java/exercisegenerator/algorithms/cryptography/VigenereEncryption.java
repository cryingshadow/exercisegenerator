package exercisegenerator.algorithms.cryptography;

public class VigenereEncryption implements VigenereAlgorithm {

    public static final VigenereEncryption INSTANCE = new VigenereEncryption();

    private VigenereEncryption() {}

    @Override
    public boolean isEncoding() {
        return true;
    }

}
