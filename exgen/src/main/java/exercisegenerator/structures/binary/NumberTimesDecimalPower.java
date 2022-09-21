package exercisegenerator.structures.binary;

import java.math.*;

public class NumberTimesDecimalPower {

    public final int exponent;

    public final BigInteger number;

    public NumberTimesDecimalPower(final BigInteger number, final int exponent) {
        if (number.compareTo(BigInteger.ZERO) < 0) {
            throw new IllegalArgumentException("Number must not be negative!");
        }
        int currentExponent = exponent;
        BigInteger currentNumber = number;
        while (
            currentNumber.compareTo(BigInteger.TEN) >= 0
            && currentNumber.mod(BigInteger.TEN).compareTo(BigInteger.ZERO) == 0
        ) {
            currentExponent++;
            currentNumber = currentNumber.divide(BigInteger.TEN);
        }
        this.number = currentNumber;
        this.exponent = currentExponent;
    }

    public NumberTimesDecimalPower add(final NumberTimesDecimalPower other) {
        final int diff = Math.abs(this.exponent - other.exponent);
        if (other.exponent < this.exponent) {
            return new NumberTimesDecimalPower(
                this.number.multiply(BigInteger.TEN.pow(diff)).add(other.number),
                other.exponent
            );
        }
        return new NumberTimesDecimalPower(
            other.number.multiply(BigInteger.TEN.pow(diff)).add(this.number),
            this.exponent
        );
    }

    public String getAfterComma() {
        if (this.exponent < 0) {
            final String numberString = this.number.toString();
            final int length = numberString.length();
            if (-this.exponent < length) {
                return numberString.substring(length + this.exponent, length);
            }
            return "0".repeat(-length - this.exponent) + numberString;
        }
        return "0";
    }

    public BigInteger getBeforeComma() {
        final String numberString = this.number.toString();
        final int length = numberString.length();
        if (-this.exponent >= length) {
            return BigInteger.ZERO;
        }
        if (this.exponent < 0) {
            return new BigInteger(numberString.substring(0, length + this.exponent));
        }
        return this.number.multiply(BigInteger.TEN.pow(this.exponent));
    }

    public boolean lessThanOne() {
        return this.getBeforeComma().compareTo(BigInteger.ONE) < 0;
    }

    public NumberTimesDecimalPower subtractOne() {
        if (this.lessThanOne()) {
            throw new IllegalStateException("Subtracting one from this number would yield a negative number!");
        }
        if (this.exponent < 0) {
            return new NumberTimesDecimalPower(
                this.number.subtract(BigInteger.TEN.pow(-this.exponent)),
                this.exponent
            );
        }
        return new NumberTimesDecimalPower(
            this.number.multiply(BigInteger.TEN.pow(this.exponent)).subtract(BigInteger.ONE),
            0
        );
    }

    public NumberTimesDecimalPower times2() {
        return new NumberTimesDecimalPower(this.number.multiply(BigInteger.TWO), this.exponent);
    }

    @Override
    public String toString() {
        return String.format("%s,%s", this.getBeforeComma().toString(), this.getAfterComma());
    }

}
