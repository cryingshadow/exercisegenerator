package exercisegenerator.structures.binary;

public enum Bit {

    ONE(1), ZERO(0);

    public static Bit fromBoolean(final boolean bit) {
        return bit ? Bit.ONE : Bit.ZERO;
    }

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

    public boolean isZero() {
        return this.value == 0;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

}
