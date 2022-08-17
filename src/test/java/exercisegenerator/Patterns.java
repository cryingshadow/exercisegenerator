package exercisegenerator;

import java.util.*;
import java.util.stream.*;

public class Patterns {

    public static final String ARRAY_STYLE =
        "[node/.style={rectangle,draw=black,thick,inner sep=5pt,font={\\Huge}},node distance=0.25 and 0]";

    public static String beginMinipageForBinaryNumber(final int[] binaryNumber) {
        return String.format("\\begin{minipage}{\\widthof{%s}}", Patterns.forBinaryNumber(binaryNumber));
    }

    public static String beginMinipageForBinaryNumberComplement(final int[] binaryNumber) {
        return String.format("\\begin{minipage}{\\textwidth-\\widthof{%s}}", Patterns.forBinaryNumber(binaryNumber));
    }

    public static String beginMinipageForNumber(final String number) {
        return String.format("\\begin{minipage}{\\widthof{%s}}", Patterns.forNumber(number));
    }

    public static String beginMinipageForNumberComplement(final String number) {
        return String.format("\\begin{minipage}{\\textwidth-\\widthof{%s}}", Patterns.forNumber(number));
    }

    public static String forBinaryNumber(final int[] binaryNumber) {
        return String.format(
            "$\\code{%s} = {}$",
            Arrays.stream(binaryNumber).mapToObj(bit -> String.valueOf(bit)).collect(Collectors.joining())
        );
    }

    public static String forNumber(final String number) {
        return String.format("$%s = {}$", number);
    }

    public static String fromFloat(final int exponentLength, final int mantisseLength) {
        return String.format(
            "Geben Sie zu den folgenden 1.%d.%d Gleitkommazahlen nach IEEE 754 die jeweilige rationale Zahl an:\\\\[2ex]",
            exponentLength,
            mantisseLength
        );
    }

    public static String fromOnes(final int bitLength) {
        return String.format(
            "Geben Sie den Dezimalwert der folgenden Binärzahlen im %d-bit Einerkomplement an:\\\\[2ex]",
            bitLength
        );
    }

    public static String fromTwos(final int bitLength) {
        return String.format(
            "Geben Sie den Dezimalwert der folgenden Binärzahlen im %d-bit Zweierkomplement an:\\\\[2ex]",
            bitLength
        );
    }

    public static String phantom(final int length) {
        return String.format("\\phantom{%s}", "0".repeat(length));
    }

    public static String rightEmptyNode(final int number, final int rightOf, final int contentLength) {
        return Patterns.rightNode(
            "n" + String.valueOf(number),
            "n" + String.valueOf(rightOf),
            Patterns.phantom(contentLength)
        );
    }

    public static String rightEmptyNodeToPredecessor(final int number, final int contentLength) {
        return Patterns.rightEmptyNode(number, number - 1, contentLength);
    }

    public static String rightNode(final int number, final int rightOf, final int content) {
        return Patterns.rightNode("n" + String.valueOf(number), "n" + String.valueOf(rightOf), String.valueOf(content));
    }

    public static String rightNode(final String name, final String rightOf, final String content) {
        return String.format("\\node[node] (%s) [right=of %s] {%s};", name, rightOf, content);
    }

    public static String rightNodeToPredecessor(final int number, final int content) {
        return Patterns.rightNode(number, number - 1, content);
    }

    public static String singleEmptyNode(final int number, final int contentLength) {
        return Patterns.singleNode("n" + String.valueOf(number), Patterns.phantom(contentLength));
    }

    public static String singleNode(final int number, final int content) {
        return Patterns.singleNode("n" + String.valueOf(number), String.valueOf(content));
    }

    public static String singleNode(final int number, final String content, final int contentLength) {
        return Patterns.singleNode(
            "n" + String.valueOf(number),
            Patterns.padWithPhantom(content, contentLength)
        );
    }

    public static String singleNode(final String name, final String content) {
        return String.format("\\node[node] (%s) {%s};", name, content);
    }

    public static String toFloat(final int exponentLength, final int mantisseLength) {
        return String.format(
            "Geben Sie zu den folgenden rationalen Zahlen die jeweilige 1.%d.%d Gleitkommazahl nach IEEE 754 an:\\\\[2ex]",
            exponentLength,
            mantisseLength
        );
    }

    public static String toOnes(final int bitLength) {
        return String.format(
            "Stellen Sie die folgenden Dezimalzahlen im %d-bit Einerkomplement dar:\\\\[2ex]",
            bitLength
        );
    }

    public static String toTwos(final int bitLength) {
        return String.format(
            "Stellen Sie die folgenden Dezimalzahlen im %d-bit Zweierkomplement dar:\\\\[2ex]",
            bitLength
        );
    }

    private static String padWithPhantom(final String content, final int contentLength) {
        final int length = content.length();
        if (contentLength > length) {
            return Patterns.phantom(contentLength - length) + content;
        }
        return content;
    }

}
