package exercisegenerator.algorithms.cryptography;

public class VigenereDecryption implements VigenereAlgorithm {

    public static final VigenereDecryption INSTANCE = new VigenereDecryption();

    private VigenereDecryption() {}

    @Override
    public boolean isEncoding() {
        return false;
    }

}
