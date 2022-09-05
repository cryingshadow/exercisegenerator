package exercisegenerator;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.io.*;
import exercisegenerator.util.*;

public class MainTest {

    private static class BinaryInput {
        private final int currentNodeNumber;
        private final BufferedReader exReader;
        private final BufferedReader solReader;
        private final BinaryTestCase test;

        private BinaryInput(
            final int currentNodeNumber,
            final BinaryTestCase test,
            final BufferedReader exReader,
            final BufferedReader solReader
        ) {
            this.currentNodeNumber = currentNodeNumber;
            this.test = test;
            this.exReader = exReader;
            this.solReader = solReader;
        }
    }

    private static class BinaryTestCase {
        private final int[] binaryNumber;
        private final String number;

        private BinaryTestCase(final String number, final int[] binaryNumber) {
            this.number = number;
            this.binaryNumber = binaryNumber;
        }
    }

    private static final String EX_FILE;

    private static final String SOL_FILE;

    private static final String TEST_DIR;

    static {
        TEST_DIR = "C:\\Daten\\Test\\exgen";
        EX_FILE = MainTest.TEST_DIR + "\\ex.tex";
        SOL_FILE = MainTest.TEST_DIR + "\\sol.tex";
    }

    private static void assignmentEnd(final BufferedReader exReader, final BufferedReader solReader)
    throws IOException {
        Assert.assertEquals(exReader.readLine(), "");
        Assert.assertEquals(exReader.readLine(), "\\vspace*{1ex}");
        Assert.assertEquals(exReader.readLine(), "");
        Assert.assertEquals(exReader.readLine(), "\\fi");
        Assert.assertEquals(exReader.readLine(), "");
        Assert.assertNull(exReader.readLine());

        Assert.assertEquals(solReader.readLine(), "");
        Assert.assertNull(solReader.readLine());
    }

    private static void assignmentMiddle(final BufferedReader exReader, final BufferedReader solReader)
    throws IOException {
        Assert.assertEquals(exReader.readLine(), "");
        Assert.assertEquals(exReader.readLine(), "\\vspace*{1ex}");
        Assert.assertEquals(exReader.readLine(), "");

        Assert.assertEquals(solReader.readLine(), "");
        Assert.assertEquals(solReader.readLine(), "\\vspace*{1ex}");
        Assert.assertEquals(solReader.readLine(), "");
    }

    private static void assignmentStart(final BufferedReader exReader, final BufferedReader solReader)
    throws IOException {
        Assert.assertEquals(exReader.readLine(), "\\ifprintanswers");
        Assert.assertEquals(exReader.readLine(), "");
        Assert.assertEquals(exReader.readLine(), "\\vspace*{-3ex}");
        Assert.assertEquals(exReader.readLine(), "");
        Assert.assertEquals(exReader.readLine(), "\\else");
    }

    private static void binaryTest(
        final CheckedFunction<BinaryInput, Integer, IOException> algorithm,
        final BinaryTestCase[] cases,
        final BufferedReader exReader,
        final BufferedReader solReader
    ) throws IOException {
        int currentNodeNumber = 0;
        MainTest.assignmentStart(exReader, solReader);
        boolean first = true;
        for (final BinaryTestCase test : cases) {
            if (first) {
                first = false;
            } else {
                MainTest.assignmentMiddle(exReader, solReader);
            }
            currentNodeNumber =
                algorithm.apply(new BinaryInput(currentNodeNumber, test, exReader, solReader));
        }
        MainTest.assignmentEnd(exReader, solReader);
    }

    private static int checkAssignment(
        int currentNodeNumber,
        final String leftHandSide,
        final List<String> rightHandSide,
        final int contentLength,
        final String longestLeftHandSide,
        final BufferedReader exReader,
        final BufferedReader solReader
    ) throws IOException {
        Assert.assertEquals(exReader.readLine(), Patterns.beginMinipageForAssignmentLeftHandSide(longestLeftHandSide));
        Assert.assertEquals(exReader.readLine(), "\\begin{flushright}");
        Assert.assertEquals(exReader.readLine(), Patterns.forAssignmentLeftHandSide(leftHandSide));
        Assert.assertEquals(exReader.readLine(), "\\end{flushright}");
        Assert.assertEquals(exReader.readLine(), "\\end{minipage}");
        Assert.assertEquals(exReader.readLine(), Patterns.beginMinipageForAssignmentRightHandSide(longestLeftHandSide));
        Assert.assertEquals(exReader.readLine(), "\\begin{tikzpicture}");
        Assert.assertEquals(exReader.readLine(), Patterns.ARRAY_STYLE);
        Assert.assertEquals(exReader.readLine(), Patterns.singleEmptyNode(currentNodeNumber++, contentLength));
        for (int i = 1; i < rightHandSide.size(); i++) {
            Assert.assertEquals(
                exReader.readLine(),
                Patterns.rightEmptyNodeToPredecessor(currentNodeNumber++, contentLength)
            );
        }
        Assert.assertEquals(exReader.readLine(), "\\end{tikzpicture}");
        Assert.assertEquals(exReader.readLine(), "\\end{minipage}");

        Assert.assertEquals(solReader.readLine(), Patterns.beginMinipageForAssignmentLeftHandSide(longestLeftHandSide));
        Assert.assertEquals(solReader.readLine(), "\\begin{flushright}");
        Assert.assertEquals(solReader.readLine(), Patterns.forAssignmentLeftHandSide(leftHandSide));
        Assert.assertEquals(solReader.readLine(), "\\end{flushright}");
        Assert.assertEquals(solReader.readLine(), "\\end{minipage}");
        Assert.assertEquals(
            solReader.readLine(),
            Patterns.beginMinipageForAssignmentRightHandSide(longestLeftHandSide)
        );
        Assert.assertEquals(solReader.readLine(), "\\begin{tikzpicture}");
        Assert.assertEquals(solReader.readLine(), Patterns.ARRAY_STYLE);
        Assert.assertEquals(
            solReader.readLine(),
            Patterns.singleNode(currentNodeNumber++, rightHandSide.get(0), contentLength)
        );
        for (int i = 1; i < rightHandSide.size(); i++) {
            Assert.assertEquals(
                solReader.readLine(),
                Patterns.rightNodeToPredecessor(currentNodeNumber++, rightHandSide.get(i))
            );
        }
        Assert.assertEquals(solReader.readLine(), "\\end{tikzpicture}");
        Assert.assertEquals(solReader.readLine(), "\\end{minipage}");
        return currentNodeNumber;
    }

    private static void fromBinary(
        final BinaryTestCase[] cases,
        final BufferedReader exReader,
        final BufferedReader solReader
    ) throws IOException {
        final String longestNumber =
            "-" +
            Arrays.stream(cases)
            .map(c -> Patterns.toCode(c.binaryNumber))
            .sorted((s1, s2) -> Integer.compare(s2.length(), s1.length()))
            .findFirst()
            .get();
        MainTest.binaryTest(
            input -> MainTest.checkAssignment(
                input.currentNodeNumber,
                Patterns.toCode(input.test.binaryNumber),
                Collections.singletonList(input.test.number),
                Arrays.stream(cases).mapToInt(test -> String.valueOf(test.number).length()).max().getAsInt(),
                longestNumber,
                input.exReader,
                input.solReader
            ),
            cases,
            exReader,
            solReader
        );
    }

    private static void toBinary(
        final BinaryTestCase[] cases,
        final BufferedReader exReader,
        final BufferedReader solReader
    ) throws IOException {
        final String longestNumber =
            "-" +
            Arrays.stream(cases)
            .map(c -> c.number)
            .sorted((s1, s2) -> Integer.compare(s2.length(), s1.length()))
            .findFirst()
            .get();
        MainTest.binaryTest(
            input -> MainTest.checkAssignment(
                input.currentNodeNumber,
                input.test.number,
                Patterns.toRightHandSide(input.test.binaryNumber),
                1,
                longestNumber,
                input.exReader,
                input.solReader
            ),
            cases,
            exReader,
            solReader
        );
    }

    private static String toBitStringInput(final BinaryTestCase[] cases) {
        return Arrays.stream(cases)
            .map(c -> Arrays.stream(c.binaryNumber).mapToObj(n -> String.valueOf(n)).collect(Collectors.joining()))
            .collect(Collectors.joining(";"));
    }

    private static String toNumberInput(final BinaryTestCase[] cases) {
        return Arrays.stream(cases).map(c -> c.number).collect(Collectors.joining(";"));
    }

    @AfterMethod
    public void cleanUp() {
        final File exFile = new File(MainTest.EX_FILE);
        final File solFile = new File(MainTest.SOL_FILE);
        if (exFile.exists()) {
            exFile.delete();
        }
        if (solFile.exists()) {
            solFile.delete();
        }
    }

    @Test
    public void decodeHuffman() throws IOException {
        if (Main.STUDENT_MODE) {
            return;
        }
        Main.main(
            new String[]{
                "-a", "fromhuff",
                "-e", MainTest.EX_FILE,
                "-t", MainTest.SOL_FILE,
                "-i", "01100001100001111101",
                "-o", "'I':\"00\",'K':\"01\",'L':\"100\",'M':\"101\",'N':\"110\",'U':\"111\""
            }
        );
        try (
            BufferedReader exReader = new BufferedReader(new FileReader(MainTest.EX_FILE));
            BufferedReader solReader = new BufferedReader(new FileReader(MainTest.SOL_FILE));
        ) {
            Assert.assertEquals(exReader.readLine(), "Erzeugen Sie den Quelltext aus dem nachfolgenden Huffman Code mit dem angegebenen Codebuch:\\\\");
            Assert.assertEquals(exReader.readLine(), "\\begin{center}");
            Assert.assertEquals(exReader.readLine(), "\\code{01100001100001111101}");
            Assert.assertEquals(exReader.readLine(), "\\end{center}");
            Assert.assertEquals(exReader.readLine(), "");
            Assert.assertEquals(exReader.readLine(), "\\vspace*{1ex}");
            Assert.assertEquals(exReader.readLine(), "");
            Assert.assertEquals(exReader.readLine(), "\\textbf{Codebuch:}");
            Assert.assertEquals(exReader.readLine(), "\\begin{align*}");
            Assert.assertEquals(exReader.readLine(), "\\code{`I'} &= \\code{\\textquotedbl{}00\\textquotedbl{}}\\\\");
            Assert.assertEquals(exReader.readLine(), "\\code{`K'} &= \\code{\\textquotedbl{}01\\textquotedbl{}}\\\\");
            Assert.assertEquals(exReader.readLine(), "\\code{`L'} &= \\code{\\textquotedbl{}100\\textquotedbl{}}\\\\");
            Assert.assertEquals(exReader.readLine(), "\\code{`M'} &= \\code{\\textquotedbl{}101\\textquotedbl{}}\\\\");
            Assert.assertEquals(exReader.readLine(), "\\code{`N'} &= \\code{\\textquotedbl{}110\\textquotedbl{}}\\\\");
            Assert.assertEquals(exReader.readLine(), "\\code{`U'} &= \\code{\\textquotedbl{}111\\textquotedbl{}}\\\\");
            Assert.assertEquals(exReader.readLine(), "\\end{align*}");
            Assert.assertEquals(exReader.readLine(), "");
            Assert.assertEquals(exReader.readLine(), "\\vspace*{-3ex}");
            Assert.assertEquals(exReader.readLine(), "");
            Assert.assertEquals(exReader.readLine(), "\\textbf{Quelltext:}\\\\[2ex]");
            Assert.assertEquals(exReader.readLine(), "");
            Assert.assertNull(exReader.readLine());

            Assert.assertEquals(solReader.readLine(), "\\code{KLINIKUM}");
            Assert.assertEquals(solReader.readLine(), "");
            Assert.assertNull(solReader.readLine());
        }
    }

    @Test
    public void dijkstra() throws IOException {
        if (Main.STUDENT_MODE) {
            return;
        }
        Main.main(
            new String[]{
                "-a", "dijkstra",
                "-e", MainTest.EX_FILE,
                "-t", MainTest.SOL_FILE,
                "-p", "solutionSpace",
                "-i", " A , |2, B \n5| , | , |3\n C ,4| , D",
                "-o", "A"
            }
        );
        try (
            BufferedReader exReader = new BufferedReader(new FileReader(MainTest.EX_FILE));
            BufferedReader solReader = new BufferedReader(new FileReader(MainTest.SOL_FILE));
        ) {
            Assert.assertEquals(exReader.readLine(), "Betrachten Sie den folgenden Graphen:\\\\[2ex]");
            Assert.assertEquals(exReader.readLine(), "\\begin{center}");
            Assert.assertEquals(exReader.readLine(), "\\begin{tikzpicture}");
            Assert.assertEquals(exReader.readLine(), "[scale=2.4, node/.style={circle,draw=black,thin,inner sep=5pt}, >=stealth, p/.style={->, thin, shorten <=2pt, shorten >=2pt}]");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n1) at (0.0,1.0) {A};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n2) at (1.0,1.0) {B};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n3) at (0.0,0.0) {C};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n4) at (1.0,0.0) {D};");
            Assert.assertEquals(exReader.readLine(), "\\draw[p, bend right = 10] (n1) to node[auto, swap] {5} (n3);");
            Assert.assertEquals(exReader.readLine(), "\\draw[p, bend right = 10] (n2) to node[auto, swap] {2} (n1);");
            Assert.assertEquals(exReader.readLine(), "\\draw[p, bend right = 10] (n3) to node[auto, swap] {4} (n4);");
            Assert.assertEquals(exReader.readLine(), "\\draw[p, bend right = 10] (n4) to node[auto, swap] {3} (n2);");
            Assert.assertEquals(exReader.readLine(), "\\end{tikzpicture}");
            Assert.assertEquals(exReader.readLine(), "");
            Assert.assertEquals(exReader.readLine(), "");
            Assert.assertEquals(exReader.readLine(), "\\end{center}");
            Assert.assertEquals(exReader.readLine(), "");
            Assert.assertEquals(exReader.readLine(), "F\\\"uhren Sie den \\emphasize{Dijkstra} Algorithmus auf diesem Graphen mit dem \\emphasize{Startknoten A} aus. F\\\"ullen Sie dazu die nachfolgende Tabelle aus:\\\\[2ex]");
            Assert.assertEquals(exReader.readLine(), "\\ifprintanswers");
            Assert.assertEquals(exReader.readLine(), "");
            Assert.assertEquals(exReader.readLine(), "\\vspace*{-3ex}");
            Assert.assertEquals(exReader.readLine(), "");
            Assert.assertEquals(exReader.readLine(), "\\else");
            Assert.assertEquals(exReader.readLine(), "\\begin{center}");
            Assert.assertEquals(exReader.readLine(), "");
            Assert.assertEquals(exReader.readLine(), "\\renewcommand{\\arraystretch}{1.5}");
            Assert.assertEquals(exReader.readLine(), "\\begin{tabular}{|*{4}{C{2cm}|}}");
            Assert.assertEquals(exReader.readLine(), "\\hline");
            Assert.assertEquals(exReader.readLine(), "\\textbf{Knoten} & \\textbf{A} &  & \\\\\\hline");
            Assert.assertEquals(exReader.readLine(), "\\textbf{B} &  &  & \\\\\\hline");
            Assert.assertEquals(exReader.readLine(), "\\textbf{C} &  &  & \\\\\\hline");
            Assert.assertEquals(exReader.readLine(), "\\textbf{D} &  &  & \\\\\\hline");
            Assert.assertEquals(exReader.readLine(), "\\end{tabular}");
            Assert.assertEquals(exReader.readLine(), "\\renewcommand{\\arraystretch}{1.0}");
            Assert.assertEquals(exReader.readLine(), "\\end{center}");
            Assert.assertEquals(exReader.readLine(), "");
            Assert.assertEquals(exReader.readLine(), "\\vspace*{1ex}");
            Assert.assertEquals(exReader.readLine(), "");
            Assert.assertEquals(exReader.readLine(), "\\fi");
            Assert.assertEquals(exReader.readLine(), "");
            Assert.assertNull(exReader.readLine());

            Assert.assertEquals(solReader.readLine(), "\\begin{center}");
            Assert.assertEquals(solReader.readLine(), "");
            Assert.assertEquals(solReader.readLine(), "\\renewcommand{\\arraystretch}{1.5}");
            Assert.assertEquals(solReader.readLine(), "\\begin{tabular}{|*{4}{C{2cm}|}}");
            Assert.assertEquals(solReader.readLine(), "\\hline");
            Assert.assertEquals(solReader.readLine(), "\\textbf{Knoten} & \\textbf{A} & \\textbf{C} & \\textbf{D}\\\\\\hline");
            Assert.assertEquals(solReader.readLine(), "\\textbf{B} & $\\infty$ & $\\infty$ & \\cellcolor{black!20}12\\\\\\hline");
            Assert.assertEquals(solReader.readLine(), "\\textbf{C} & \\cellcolor{black!20}5 & \\textbf{--} & \\textbf{--}\\\\\\hline");
            Assert.assertEquals(solReader.readLine(), "\\textbf{D} & $\\infty$ & \\cellcolor{black!20}9 & \\textbf{--}\\\\\\hline");
            Assert.assertEquals(solReader.readLine(), "\\end{tabular}");
            Assert.assertEquals(solReader.readLine(), "\\renewcommand{\\arraystretch}{1.0}");
            Assert.assertEquals(solReader.readLine(), "\\end{center}");
            Assert.assertEquals(solReader.readLine(), "");
            Assert.assertEquals(solReader.readLine(), "\\vspace*{1ex}");
            Assert.assertEquals(solReader.readLine(), "");
            Assert.assertEquals(solReader.readLine(), "Die grau unterlegten Zellen markieren, an welcher Stelle f\\\"ur welchen Knoten die minimale Distanz sicher berechnet worden ist.");
            Assert.assertEquals(solReader.readLine(), "");
            Assert.assertNull(solReader.readLine());
        }
    }

    @Test
    public void encodeHuffman() throws IOException {
        if (Main.STUDENT_MODE) {
            return;
        }
        Main.main(
            new String[]{
                "-a", "tohuff",
                "-e", MainTest.EX_FILE,
                "-t", MainTest.SOL_FILE,
                "-i", "GEIERMEIER",
            }
        );
        try (
            BufferedReader exReader = new BufferedReader(new FileReader(MainTest.EX_FILE));
            BufferedReader solReader = new BufferedReader(new FileReader(MainTest.SOL_FILE));
        ) {
            Assert.assertEquals(exReader.readLine(), "Erzeugen Sie den Huffman Code f\\\"ur das Zielalphabet $\\{0,1\\}$ und den folgenden Eingabetext:\\\\");
            Assert.assertEquals(exReader.readLine(), "\\begin{center}");
            Assert.assertEquals(exReader.readLine(), "GEIERMEIER");
            Assert.assertEquals(exReader.readLine(), "\\end{center}");
            Assert.assertEquals(exReader.readLine(), "");
            Assert.assertEquals(exReader.readLine(), "\\vspace*{1ex}");
            Assert.assertEquals(exReader.readLine(), "");
            Assert.assertEquals(exReader.readLine(), "Geben Sie zus\\\"atzlich zu dem erstellten Code das erzeugte Codebuch an.\\\\[2ex]");
            MainTest.assignmentStart(exReader, solReader);
            Assert.assertEquals(exReader.readLine(), "\\textbf{Codebuch:}\\\\[2ex]");
            Assert.assertEquals(solReader.readLine(), "\\textbf{Codebuch:}\\\\[2ex]");
            final int longestCodeLength = 3;
            final String longestLeftHandSide = "\\code{`M'}";
            int currentNodeNumber =
                MainTest.checkAssignment(
                    0,
                    "\\code{`E'}",
                    Collections.singletonList("\\code{11}"),
                    longestCodeLength,
                    longestLeftHandSide,
                    exReader,
                    solReader
                );
            currentNodeNumber =
                MainTest.checkAssignment(
                    currentNodeNumber,
                    "\\code{`G'}",
                    Collections.singletonList("\\code{100}"),
                    longestCodeLength,
                    longestLeftHandSide,
                    exReader,
                    solReader
                );
            currentNodeNumber =
                MainTest.checkAssignment(
                    currentNodeNumber,
                    "\\code{`I'}",
                    Collections.singletonList("\\code{00}"),
                    longestCodeLength,
                    longestLeftHandSide,
                    exReader,
                    solReader
                );
            currentNodeNumber =
                MainTest.checkAssignment(
                    currentNodeNumber,
                    "\\code{`M'}",
                    Collections.singletonList("\\code{101}"),
                    longestCodeLength,
                    longestLeftHandSide,
                    exReader,
                    solReader
                );
            currentNodeNumber =
                MainTest.checkAssignment(
                    currentNodeNumber,
                    "\\code{`R'}",
                    Collections.singletonList("\\code{01}"),
                    longestCodeLength,
                    longestLeftHandSide,
                    exReader,
                    solReader
                );
            MainTest.assignmentMiddle(exReader, solReader);
            Assert.assertEquals(exReader.readLine(), "\\textbf{Code:}\\\\");
            Assert.assertEquals(solReader.readLine(), "\\textbf{Code:}\\\\");
            Assert.assertEquals(solReader.readLine(), "\\code{1001100110110111001101}");
            MainTest.assignmentEnd(exReader, solReader);
        }
    }

    @Test
    public void fromFloat() throws IOException {
        if (Main.STUDENT_MODE) {
            return;
        }
        final int mantisseLength = 4;
        final int exponentLength = 3;
        final BinaryTestCase[] cases =
            new BinaryTestCase[] {
                new BinaryTestCase("-3,5", new int[] {1,1,0,0,1,1,0,0}),
                new BinaryTestCase("1,375", new int[] {0,0,1,1,0,1,1,0}),
                new BinaryTestCase("-7,0", new int[] {1,1,0,1,1,1,0,0})
            };
        Main.main(
            new String[]{
                "-a", "fromfloat",
                "-e", MainTest.EX_FILE,
                "-t", MainTest.SOL_FILE,
                "-c", String.valueOf(mantisseLength),
                "-d", String.valueOf(exponentLength),
                "-i", MainTest.toBitStringInput(cases)
            }
        );
        try (
            BufferedReader exReader = new BufferedReader(new FileReader(MainTest.EX_FILE));
            BufferedReader solReader = new BufferedReader(new FileReader(MainTest.SOL_FILE));
        ) {
            Assert.assertEquals(exReader.readLine(), Patterns.fromFloat(exponentLength, mantisseLength));
            MainTest.fromBinary(cases, exReader, solReader);
        }
    }

    @Test
    public void fromOnesComplement() throws IOException {
        if (Main.STUDENT_MODE) {
            return;
        }
        final int bitLength = 8;
        final BinaryTestCase[] cases =
            new BinaryTestCase[] {
                new BinaryTestCase("-3", new int[] {1,1,1,1,1,1,0,0}),
                new BinaryTestCase("5", new int[] {0,0,0,0,0,1,0,1}),
                new BinaryTestCase("-111", new int[] {1,0,0,1,0,0,0,0})
            };
        Main.main(
            new String[]{
                "-a", "fromonescompl",
                "-e", MainTest.EX_FILE,
                "-t", MainTest.SOL_FILE,
                "-c", String.valueOf(bitLength),
                "-i", MainTest.toBitStringInput(cases)
            }
        );
        try (
            BufferedReader exReader = new BufferedReader(new FileReader(MainTest.EX_FILE));
            BufferedReader solReader = new BufferedReader(new FileReader(MainTest.SOL_FILE));
        ) {
            Assert.assertEquals(exReader.readLine(), Patterns.fromOnes(bitLength));
            MainTest.fromBinary(cases, exReader, solReader);
        }
    }

    @Test
    public void fromTwosComplement() throws IOException {
        if (Main.STUDENT_MODE) {
            return;
        }
        final int bitLength = 5;
        final BinaryTestCase[] cases =
            new BinaryTestCase[] {
                new BinaryTestCase("-3", new int[] {1,1,1,0,1}),
                new BinaryTestCase("1", new int[] {0,0,0,0,1}),
                new BinaryTestCase("-13", new int[] {1,0,0,1,1})
            };
        Main.main(
            new String[]{
                "-a", "fromtwoscompl",
                "-e", MainTest.EX_FILE,
                "-t", MainTest.SOL_FILE,
                "-c", String.valueOf(bitLength),
                "-i", MainTest.toBitStringInput(cases)
            }
        );
        try (
            BufferedReader exReader = new BufferedReader(new FileReader(MainTest.EX_FILE));
            BufferedReader solReader = new BufferedReader(new FileReader(MainTest.SOL_FILE));
        ) {
            Assert.assertEquals(exReader.readLine(), Patterns.fromTwos(bitLength));
            MainTest.fromBinary(cases, exReader, solReader);
        }
    }

    @Test
    public void hashing() throws IOException {
        if (Main.STUDENT_MODE) {
            return;
        }
        Main.main(
            new String[]{
                "-a", "hashMultiplicationQuadratic",
                "-e", MainTest.EX_FILE,
                "-t", MainTest.SOL_FILE,
                "-i", "11,0.7,7,3\n3,5,1,4,2,1",
                "-p", "solutionSpace"
            }
        );
        try (
            BufferedReader exReader = new BufferedReader(new FileReader(MainTest.EX_FILE));
            BufferedReader solReader = new BufferedReader(new FileReader(MainTest.SOL_FILE));
        ) {
            Assert.assertEquals(exReader.readLine(), "F\\\"ugen Sie die folgenden Werte nacheinander in das unten stehende Array \\code{a} der L\\\"ange 11 unter Verwendung der \\emphasize{Multiplikationsmethode} ($c = 0,70$) mit \\emphasize{quadratischer Sondierung} ($c_1 = 7$, $c_2 = 3$) ein:\\\\");
            Assert.assertEquals(exReader.readLine(), "\\begin{center}");
            Assert.assertEquals(exReader.readLine(), "3, 5, 1, 4, 2, 1.");
            Assert.assertEquals(exReader.readLine(), "\\end{center}");
            Assert.assertEquals(exReader.readLine(), "");
            Assert.assertEquals(exReader.readLine(), "\\vspace*{3ex}");
            Assert.assertEquals(exReader.readLine(), "");
            Assert.assertEquals(exReader.readLine(), "\\ifprintanswers");
            Assert.assertEquals(exReader.readLine(), "");
            Assert.assertEquals(exReader.readLine(), "\\vspace*{-3ex}");
            Assert.assertEquals(exReader.readLine(), "");
            Assert.assertEquals(exReader.readLine(), "\\else");
            Assert.assertEquals(exReader.readLine(), "\\begin{center}");
            Assert.assertEquals(exReader.readLine(), "{\\Large");
            Assert.assertEquals(exReader.readLine(), "\\begin{tikzpicture}");
            Assert.assertEquals(exReader.readLine(), Patterns.ARRAY_STYLE);
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n0) {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n1) [right=of n0] {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n2) [right=of n1] {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n3) [right=of n2] {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n4) [right=of n3] {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n5) [right=of n4] {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n6) [right=of n5] {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n7) [right=of n6] {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n8) [right=of n7] {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n9) [right=of n8] {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n10) [right=of n9] {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\end{tikzpicture}");
            Assert.assertEquals(exReader.readLine(), "}");
            Assert.assertEquals(exReader.readLine(), "\\end{center}");
            Assert.assertEquals(exReader.readLine(), "");
            Assert.assertEquals(exReader.readLine(), "\\vspace*{1ex}");
            Assert.assertEquals(exReader.readLine(), "");
            Assert.assertEquals(exReader.readLine(), "\\fi");
            Assert.assertEquals(exReader.readLine(), "");
            Assert.assertNull(exReader.readLine());

            Assert.assertEquals(solReader.readLine(), "\\begin{center}");
            Assert.assertEquals(solReader.readLine(), "m = 11, c = 0,70, $c_1$ = 7, $c_2$ = 3:\\\\[2ex]");
            Assert.assertEquals(solReader.readLine(), "{\\Large");
            Assert.assertEquals(solReader.readLine(), "\\begin{tikzpicture}");
            Assert.assertEquals(solReader.readLine(), Patterns.ARRAY_STYLE);
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n11) {\\phantom{0}};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n12) [right=of n11] {3};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n13) [right=of n12] {\\phantom{0}};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n14) [right=of n13] {\\phantom{0}};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n15) [right=of n14] {2};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n16) [right=of n15] {5};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n17) [right=of n16] {1};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n18) [right=of n17] {1};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n19) [right=of n18] {4};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n20) [right=of n19] {\\phantom{0}};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n21) [right=of n20] {\\phantom{0}};");
            Assert.assertEquals(solReader.readLine(), "\\end{tikzpicture}");
            Assert.assertEquals(solReader.readLine(), "}");
            Assert.assertEquals(solReader.readLine(), "\\end{center}");
            Assert.assertEquals(solReader.readLine(), "");
            Assert.assertNull(solReader.readLine());
        }
    }

    @Test
    public void insertionsort() throws IOException {
        if (Main.STUDENT_MODE) {
            return;
        }
        Main.main(
            new String[]{"-a", "insertionsort", "-e", MainTest.EX_FILE, "-t", MainTest.SOL_FILE, "-i", "3,5,1,4,2"}
        );
        try (
            BufferedReader exReader = new BufferedReader(new FileReader(MainTest.EX_FILE));
            BufferedReader solReader = new BufferedReader(new FileReader(MainTest.SOL_FILE));
        ) {
            Assert.assertEquals(exReader.readLine(), "Sortieren Sie das folgende Array mithilfe von Insertionsort.");
            Assert.assertEquals(exReader.readLine(), "Geben Sie dazu das Array nach jeder Iteration der \\\"au\\ss{}eren Schleife an.\\\\[2ex]");
            Assert.assertEquals(exReader.readLine(), "\\ifprintanswers");
            Assert.assertEquals(exReader.readLine(), "\\else");
            Assert.assertEquals(exReader.readLine(), "\\begin{tikzpicture}");
            Assert.assertEquals(exReader.readLine(), Patterns.ARRAY_STYLE);
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n0) {3};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n1) [right=of n0] {5};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n2) [right=of n1] {1};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n3) [right=of n2] {4};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n4) [right=of n3] {2};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n30) [below=of n0] {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n31) [right=of n30] {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n32) [right=of n31] {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n33) [right=of n32] {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n34) [right=of n33] {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n35) [below=of n30] {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n36) [right=of n35] {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n37) [right=of n36] {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n38) [right=of n37] {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n39) [right=of n38] {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n40) [below=of n35] {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n41) [right=of n40] {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n42) [right=of n41] {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n43) [right=of n42] {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n44) [right=of n43] {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n45) [below=of n40] {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n46) [right=of n45] {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n47) [right=of n46] {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n48) [right=of n47] {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\node[node] (n49) [right=of n48] {\\phantom{0}};");
            Assert.assertEquals(exReader.readLine(), "\\end{tikzpicture}");
            Assert.assertEquals(exReader.readLine(), "\\fi");
            Assert.assertNull(exReader.readLine());

            Assert.assertEquals(solReader.readLine(), "\\begin{tikzpicture}");
            Assert.assertEquals(solReader.readLine(), Patterns.ARRAY_STYLE);
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n5) {3};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n6) [right=of n5] {5};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n7) [right=of n6] {1};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n8) [right=of n7] {4};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n9) [right=of n8] {2};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n10) [below=of n5] {3};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n11) [right=of n10] {5};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n12) [right=of n11] {1};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n13) [right=of n12] {4};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n14) [right=of n13] {2};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n15) [below=of n10] {1};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n16) [right=of n15] {3};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n17) [right=of n16] {5};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n18) [right=of n17] {4};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n19) [right=of n18] {2};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n20) [below=of n15] {1};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n21) [right=of n20] {3};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n22) [right=of n21] {4};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n23) [right=of n22] {5};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n24) [right=of n23] {2};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n25) [below=of n20] {1};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n26) [right=of n25] {2};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n27) [right=of n26] {3};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n28) [right=of n27] {4};");
            Assert.assertEquals(solReader.readLine(), "\\node[node] (n29) [right=of n28] {5};");
            Assert.assertEquals(solReader.readLine(), "\\end{tikzpicture}");
            Assert.assertNull(solReader.readLine());
        }
    }

    @BeforeMethod
    public void prepare() {
        TikZUtils.reset();
        final File testDir = new File(MainTest.TEST_DIR);
        if (!testDir.exists()) {
            if (!testDir.mkdirs()) {
                throw new IllegalStateException("Cannot init test directory!");
            }
        }
    }

    @Test
    public void toFloat() throws IOException {
        if (Main.STUDENT_MODE) {
            return;
        }
        final int exponentLength = 3;
        final int mantisseLength = 4;
        final BinaryTestCase[] cases =
            new BinaryTestCase[] {
                new BinaryTestCase("-3,5", new int[] {1,1,0,0,1,1,0,0}),
                new BinaryTestCase("1,4", new int[] {0,0,1,1,0,1,1,0}),
                new BinaryTestCase("-7,18", new int[] {1,1,0,1,1,1,0,0})
            };
        Main.main(
            new String[]{
                "-a", "tofloat",
                "-e", MainTest.EX_FILE,
                "-t", MainTest.SOL_FILE,
                "-c", String.valueOf(mantisseLength),
                "-d", String.valueOf(exponentLength),
                "-i", MainTest.toNumberInput(cases)
            }
        );
        try (
            BufferedReader exReader = new BufferedReader(new FileReader(MainTest.EX_FILE));
            BufferedReader solReader = new BufferedReader(new FileReader(MainTest.SOL_FILE));
        ) {
            Assert.assertEquals(exReader.readLine(), Patterns.toFloat(exponentLength, mantisseLength));
            MainTest.toBinary(cases, exReader, solReader);
        }
    }

    @Test
    public void toOnesComplement() throws IOException {
        if (Main.STUDENT_MODE) {
            return;
        }
        final int bitLength = 4;
        final BinaryTestCase[] cases =
            new BinaryTestCase[] {
                new BinaryTestCase("3", new int[] {0,0,1,1}),
                new BinaryTestCase("-1", new int[] {1,1,1,0}),
                new BinaryTestCase("-2", new int[] {1,1,0,1})
            };
        Main.main(
            new String[]{
                "-a", "toonescompl",
                "-e", MainTest.EX_FILE,
                "-t", MainTest.SOL_FILE,
                "-c", String.valueOf(bitLength),
                "-i", MainTest.toNumberInput(cases)
            }
        );
        try (
            BufferedReader exReader = new BufferedReader(new FileReader(MainTest.EX_FILE));
            BufferedReader solReader = new BufferedReader(new FileReader(MainTest.SOL_FILE));
        ) {
            Assert.assertEquals(exReader.readLine(), Patterns.toOnes(bitLength));
            MainTest.toBinary(cases, exReader, solReader);
        }
    }

    @Test
    public void toTwosComplement() throws IOException {
        if (Main.STUDENT_MODE) {
            return;
        }
        final int bitLength = 6;
        final BinaryTestCase[] cases =
            new BinaryTestCase[] {
                new BinaryTestCase("-3", new int[] {1,1,1,1,0,1}),
                new BinaryTestCase("1", new int[] {0,0,0,0,0,1}),
                new BinaryTestCase("-29", new int[] {1,0,0,0,1,1})
            };
        Main.main(
            new String[]{
                "-a", "totwoscompl",
                "-e", MainTest.EX_FILE,
                "-t", MainTest.SOL_FILE,
                "-c", String.valueOf(bitLength),
                "-i", MainTest.toNumberInput(cases)
            }
        );
        try (
            BufferedReader exReader = new BufferedReader(new FileReader(MainTest.EX_FILE));
            BufferedReader solReader = new BufferedReader(new FileReader(MainTest.SOL_FILE));
        ) {
            Assert.assertEquals(exReader.readLine(), Patterns.toTwos(bitLength));
            MainTest.toBinary(cases, exReader, solReader);
        }
    }

    @Test
    public void vigenereDecode() throws IOException {
        if (Main.STUDENT_MODE) {
            return;
        }
        Main.main(
            new String[]{
                "-a", "fromvigenere",
                "-e", MainTest.EX_FILE,
                "-t", MainTest.SOL_FILE,
                "-i", "URKSAK\nSAKRAL\nAKLRSU"
            }
        );
        try (
            BufferedReader exReader = new BufferedReader(new FileReader(MainTest.EX_FILE));
            BufferedReader solReader = new BufferedReader(new FileReader(MainTest.SOL_FILE));
        ) {
            Assert.assertEquals(exReader.readLine(), "Entschl\\\"usseln Sie den Text");
            Assert.assertEquals(exReader.readLine(), "\\begin{center}");
            Assert.assertEquals(exReader.readLine(), "\\code{URKSAK}");
            Assert.assertEquals(exReader.readLine(), "\\end{center}");
            Assert.assertEquals(exReader.readLine(), "unter Benutzung des Schl\\\"usselworts");
            Assert.assertEquals(exReader.readLine(), "\\begin{center}");
            Assert.assertEquals(exReader.readLine(), "\\code{SAKRAL}");
            Assert.assertEquals(exReader.readLine(), "\\end{center}");
            Assert.assertEquals(exReader.readLine(), "auf dem Alphabet");
            Assert.assertEquals(exReader.readLine(), "\\begin{center}");
            Assert.assertEquals(exReader.readLine(), "\\begin{tabular}{|*{6}{C{1.5em}|}}");
            Assert.assertEquals(exReader.readLine(), "\\hline");
            Assert.assertEquals(exReader.readLine(), "0 & 1 & 2 & 3 & 4 & 5\\\\\\hline");
            Assert.assertEquals(exReader.readLine(), "A & K & L & R & S & U\\\\\\hline");
            Assert.assertEquals(exReader.readLine(), "\\end{tabular}");
            Assert.assertEquals(exReader.readLine(), "\\end{center}");
            Assert.assertEquals(exReader.readLine(), "mithilfe der Vigen\\'ere-Verschl\\\"usselung.");
            Assert.assertEquals(exReader.readLine(), "");
            Assert.assertNull(exReader.readLine());

            Assert.assertEquals(solReader.readLine(), "\\code{KRAKAU}");
            Assert.assertEquals(solReader.readLine(), "");
            Assert.assertNull(solReader.readLine());
        }
    }

    @Test
    public void vigenereEncode() throws IOException {
        if (Main.STUDENT_MODE) {
            return;
        }
        Main.main(
            new String[]{
                "-a", "tovigenere",
                "-e", MainTest.EX_FILE,
                "-t", MainTest.SOL_FILE,
                "-i", "KLAUSUR\nSAKRAL\nAKLRSU"
            }
        );
        try (
            BufferedReader exReader = new BufferedReader(new FileReader(MainTest.EX_FILE));
            BufferedReader solReader = new BufferedReader(new FileReader(MainTest.SOL_FILE));
        ) {
            Assert.assertEquals(exReader.readLine(), "Verschl\\\"usseln Sie den Text");
            Assert.assertEquals(exReader.readLine(), "\\begin{center}");
            Assert.assertEquals(exReader.readLine(), "\\code{KLAUSUR}");
            Assert.assertEquals(exReader.readLine(), "\\end{center}");
            Assert.assertEquals(exReader.readLine(), "unter Benutzung des Schl\\\"usselworts");
            Assert.assertEquals(exReader.readLine(), "\\begin{center}");
            Assert.assertEquals(exReader.readLine(), "\\code{SAKRAL}");
            Assert.assertEquals(exReader.readLine(), "\\end{center}");
            Assert.assertEquals(exReader.readLine(), "auf dem Alphabet");
            Assert.assertEquals(exReader.readLine(), "\\begin{center}");
            Assert.assertEquals(exReader.readLine(), "\\begin{tabular}{|*{6}{C{1.5em}|}}");
            Assert.assertEquals(exReader.readLine(), "\\hline");
            Assert.assertEquals(exReader.readLine(), "0 & 1 & 2 & 3 & 4 & 5\\\\\\hline");
            Assert.assertEquals(exReader.readLine(), "A & K & L & R & S & U\\\\\\hline");
            Assert.assertEquals(exReader.readLine(), "\\end{tabular}");
            Assert.assertEquals(exReader.readLine(), "\\end{center}");
            Assert.assertEquals(exReader.readLine(), "mithilfe der Vigen\\'ere-Verschl\\\"usselung.");
            Assert.assertEquals(exReader.readLine(), "");
            Assert.assertNull(exReader.readLine());

            Assert.assertEquals(solReader.readLine(), "\\code{ULKLSKK}");
            Assert.assertEquals(solReader.readLine(), "");
            Assert.assertNull(solReader.readLine());
        }
    }

}
