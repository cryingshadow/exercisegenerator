package exercisegenerator;

import java.util.*;
import java.util.stream.*;

public class Patterns {

    public static final String ARRAY_STYLE =
        "[node/.style={rectangle,draw=black,thick,inner sep=5pt,font={\\Large}},node distance=0.25 and 0]";

    public static final String BORDERLESS_STYLE =
        "[node/.style={draw=none,thick,inner sep=5pt, text width = 10cm,font={\\Large}}, node distance=0.25 and 0]";

    public static final String FROM_ASCII =
        "Geben Sie zu den folgenden ASCII Zeichen das jeweilige Bitmuster an:\\\\[2ex]";

    public static final List<String> MIDDLE_SPACE = List.of("", "\\vspace*{1ex}", "");

    public static final List<String> SOLUTION_SPACE_BEGINNING =
        List.of("\\ifprintanswers", "", "\\vspace*{-3ex}", "", "\\else");

    public static final List<String> SOLUTION_SPACE_END = List.of("", "\\vspace*{1ex}", "", "\\fi");

    public static final String TO_ASCII =
        "Geben Sie zu den folgenden Bitmustern das jeweilige ASCII Zeichen an:\\\\[2ex]";

    public static String beginMinipageForAssignmentLeftHandSide(final String longestLeftHandSide) {
        return String.format(
            "\\begin{minipage}{\\widthof{%s}}",
            Patterns.forAssignmentLeftHandSide(longestLeftHandSide)
        );
    }

    public static String beginMinipageForAssignmentRightHandSide(final String longestLeftHandSide) {
        return String.format(
            "\\begin{minipage}{\\textwidth-\\widthof{%s}}",
            Patterns.forAssignmentLeftHandSide(longestLeftHandSide)
        );
    }

    public static String belowEmptyNode(final int number, final int belowOf, final int contentLength) {
        return Patterns.belowNode(
            "n" + String.valueOf(number),
            "n" + String.valueOf(belowOf),
            Patterns.phantom(contentLength)
        );
    }

    public static String belowNode(final int number, final int belowOf, final int content, final int contentLength) {
        return Patterns.belowNode(number, belowOf, Patterns.padWithPhantom(String.valueOf(content), contentLength));
    }

    public static String belowNode(final int number, final int belowOf, final String content) {
        return Patterns.belowNode("n" + number, "n" + belowOf, content);
    }

    public static String belowNode(final String name, final String belowOf, final String content) {
        return String.format("\\node[node] (%s) [below=of %s] {%s};", name, belowOf, content);
    }

    public static String binaryNumberToString(final int[] binaryNumber) {
        return Arrays.stream(binaryNumber).mapToObj(bit -> String.valueOf(bit)).collect(Collectors.joining());
    }

    public static String forAssignmentLeftHandSide(final String leftHandSide) {
        return String.format("$%s = {}$", leftHandSide);
    }

    public static String fromFloat(final int exponentLength, final int mantisseLength) {
        return String.format(
            "Geben Sie zu den folgenden 1.%d.%d Gleitkommazahlen die jeweilige rationale Zahl an:\\\\[2ex]",
            exponentLength,
            mantisseLength
        );
    }

    public static String fromOnes(final int bitLength) {
        return String.format(
            "Geben Sie den Dezimalwert der folgenden Bin\\\"arzahlen im %d-Bit Einerkomplement an:\\\\[2ex]",
            bitLength
        );
    }

    public static String fromTwos(final int bitLength) {
        return String.format(
            "Geben Sie den Dezimalwert der folgenden Bin\\\"arzahlen im %d-Bit Zweierkomplement an:\\\\[2ex]",
            bitLength
        );
    }

    public static List<String> middleSpace(final String space) {
        return List.of("", String.format("\\vspace*{%s}", space), "");
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

    public static String rightNode(final int number, final int rightOf, final String content) {
        return Patterns.rightNode("n" + String.valueOf(number), "n" + String.valueOf(rightOf), content);
    }

    public static String rightNode(final String name, final String rightOf, final String content) {
        return String.format("\\node[node] (%s) [right=of %s] {%s};", name, rightOf, content);
    }

    public static String rightNodeToPredecessor(final int number, final String content) {
        return Patterns.rightNode(number, number - 1, content);
    }

    public static String singleEmptyNode(final int number, final int contentLength) {
        return Patterns.singleNode("n" + String.valueOf(number), Patterns.phantom(contentLength));
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

    public static String toCode(final int[] binaryNumber) {
        return Patterns.toCode(Patterns.binaryNumberToString(binaryNumber));
    }

    public static String toCode(final String text) {
        return String.format("\\code{%s}", text);
    }

    public static String toFloat(final int exponentLength, final int mantisseLength) {
        return String.format(
            "Geben Sie zu den folgenden rationalen Zahlen die jeweilige 1.%d.%d Gleitkommazahl an:\\\\[2ex]",
            exponentLength,
            mantisseLength
        );
    }

    public static String toOnes(final int bitLength) {
        return String.format(
            "Stellen Sie die folgenden Dezimalzahlen im %d-Bit Einerkomplement dar:\\\\[2ex]",
            bitLength
        );
    }

    public static List<String> toRightHandSide(final String bitString) {
        return bitString.chars().mapToObj(c -> String.valueOf((char)c)).toList();
    }

    public static String toTwos(final int bitLength) {
        return String.format(
            "Stellen Sie die folgenden Dezimalzahlen im %d-Bit Zweierkomplement dar:\\\\[2ex]",
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
