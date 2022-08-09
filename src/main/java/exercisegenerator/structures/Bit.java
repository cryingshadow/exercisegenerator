package exercisegenerator.structures;

public enum Bit {

    ONE(1), ZERO(0);

    public final int value;

    private Bit(final int value) {
        this.value = value;
    }

    public Bit invert() {
        if (this.isZero()) {
            return Bit.ONE;
        }
        return Bit.ZERO;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    boolean isZero() {
        return this.value == 0;
    }

}
