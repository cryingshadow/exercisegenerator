package exercisegenerator.structures.binary;

import java.io.*;
import java.math.*;
import java.util.*;
import java.util.stream.*;

import clit.*;
import exercisegenerator.io.*;

public class BitString extends LinkedList<Bit> {

    private static final long serialVersionUID = -3160588348086277796L;

    public static BitString create(final BigInteger value, final int bitLength) {
        return BitString.parse(BitString.toBitLength(value.toString(2), bitLength));
    }

    public static BitString parse(final String bitString) {
        return
            bitString.chars()
                .mapToObj(c -> c == '0' ? Bit.ZERO : Bit.ONE)
                .collect(Collectors.toCollection(BitString::new));
    }

    public static List<BitString> parseBitStringProblems(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        return Arrays.stream(reader.readLine().split(";"))
            .map(BitString::parse)
            .toList();
    }

    private static String toBitLength(final String bits, final int bitLength) {
        final int length = bits.length();
        if (length > bitLength) {
            return bits.substring(length - bitLength);
        }
        return "0".repeat(bitLength - length) + bits;
    }

    public BitString() {
        super();
    }

    public BitString(final List<? extends Bit> bitString) {
        super(bitString);
    }

    public BitString add(final BitString other, final int length) {
        final Iterator<Bit> thisIterator = this.descendingIterator();
        final Iterator<Bit> otherIterator = other.descendingIterator();
        int carry = 0;
        final BitString result = new BitString();
        while (thisIterator.hasNext() && otherIterator.hasNext()) {
            final Bit thisBit = thisIterator.next();
            final Bit otherBit = otherIterator.next();
            carry += thisBit.value;
            carry += otherBit.value;
            if (carry % 2 == 0) {
                result.addFirst(Bit.ZERO);
            } else {
                result.addFirst(Bit.ONE);
            }
            carry /= 2;
        }
        while (thisIterator.hasNext()) {
            carry += thisIterator.next().value;
            if (carry % 2 == 0) {
                result.addFirst(Bit.ZERO);
            } else {
                result.addFirst(Bit.ONE);
            }
            carry /= 2;
        }
        while (otherIterator.hasNext()) {
            carry += otherIterator.next().value;
            if (carry % 2 == 0) {
                result.addFirst(Bit.ZERO);
            } else {
                result.addFirst(Bit.ONE);
            }
            carry /= 2;
        }
        while (carry > 0) {
            if (carry % 2 == 0) {
                result.addFirst(Bit.ZERO);
            } else {
                result.addFirst(Bit.ONE);
            }
            carry /= 2;
        }
        while (result.size() < length) {
            result.addFirst(Bit.ZERO);
        }
        while (result.size() > length) {
            result.removeFirst();
        }
        return result;
    }

    public boolean and(final BitString other) {
        return !this.isZero() && !other.isZero();
    }

    public void append(final BitString toAppend) {
        for (final Bit bit : toAppend) {
            this.add(bit);
        }
    }

    public BitString increment() {
        final BitString result = new BitString();
        final Iterator<Bit> iterator = this.descendingIterator();
        boolean add = true;
        while (iterator.hasNext()) {
            final Bit current = iterator.next();
            if (add) {
                if (current.isZero()) {
                    add = false;
                }
                result.addFirst(current.invert());
            } else {
                result.addFirst(current);
            }
        }
        if (add) {
            result.addFirst(Bit.ONE);
        }
        return result;
    }

    public BitString invert() {
        return this.stream().map(b -> b.invert()).collect(Collectors.toCollection(BitString::new));
    }

    public void invertBit(final int index) {
        final Bit bit = this.get(index);
        this.set(index, bit.invert());
    }

    public boolean isNegativeInTwosComplement() {
        return !this.getFirst().isZero();
    }

    public boolean isPositiveInTwosComplement() {
        return this.getFirst().isZero() && ! this.isZero();
    }

    public boolean isZero() {
        return this.toUnsignedInt() == 0;
    }

    public boolean lessThanInTwosComplement(final BitString other, final int length) {
        if (this.size() != other.size() || this.size() != length) {
            throw new IllegalArgumentException("Length does not match!");
        }
        if (this.isNegativeInTwosComplement()) {
            if (!other.isNegativeInTwosComplement()) {
                return true;
            }
        } else if (other.isNegativeInTwosComplement()) {
            return false;
        }
        return other.subtract(this, length).isPositiveInTwosComplement();
    }

    public BitString negate(final int length) {
        return this.isZero() ? BitString.create(BigInteger.ONE, length) : BitString.create(BigInteger.ZERO, length);
    }

    public boolean or(final BitString other) {
        return !this.isZero() || !other.isZero();
    }

    public BitString reverse() {
        final BitString result = new BitString();
        for (final Bit bit : this) {
            result.addFirst(bit);
        }
        return result;
    }

    public BitString subString(final int fromIndex) {
        return this.subString(fromIndex, this.size());
    }

    public BitString subString(final int fromIndex, final int toIndexExclusive) {
        return new BitString(this.subList(fromIndex, toIndexExclusive));
    }

    public BitString subtract(final BitString other, final int length) {
        return this.add(other.invert().increment(), length);
    }

    public BigInteger toNonNegativeBigInteger() {
        return new BigInteger(this.toString(), 2);
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        for (final Bit b : this) {
            result.append(b.toString());
        }
        return result.toString();
    }

    public int toUnsignedInt() {
        final Iterator<Bit> iterator = this.descendingIterator();
        int exponent = 1;
        int result = 0;
        while (iterator.hasNext()) {
            result += exponent * iterator.next().value;
            exponent *= 2;
        }
        return result;
    }

}
