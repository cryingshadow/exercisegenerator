package exercisegenerator;

import java.io.*;
import java.util.*;
import java.util.Optional;
import java.util.stream.*;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.io.*;
import exercisegenerator.util.*;

//TODO heapsort with trees, additional complete sorting tests

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

    public static final String EX_FILE;

    public static final String EX_FILE_NAME;

    public static final String SOL_FILE;

    public static final String SOL_FILE_NAME;

    public static final String TEST_DIR;

    private static final String EMPTY_NODE_MATCH;

    private static final String MATCH_MESSAGE_PATTERN;

    private static final String NODE_MATCH;

    private static final String NUMBER_MATCH;

    private static final String PHANTOM_MATCH;

    static {
        TEST_DIR = "C:\\Daten\\Test\\exgen";
        EX_FILE_NAME = "ex.tex";
        SOL_FILE_NAME = "sol.tex";
        EX_FILE = MainTest.TEST_DIR + "\\" + MainTest.EX_FILE_NAME;
        SOL_FILE = MainTest.TEST_DIR + "\\" + MainTest.SOL_FILE_NAME;
        EMPTY_NODE_MATCH = "\\\\node\\[node\\]";
        MATCH_MESSAGE_PATTERN = "%s does not match %s";
        NODE_MATCH = "\\\\node\\[node(,fill=black!20)?\\]";
        NUMBER_MATCH = "(-?\\d+)";
        PHANTOM_MATCH = "(\\\\phantom\\{0+\\})?";
    }

    @AfterMethod
    public static void cleanUp() {
        final File testDir = new File(MainTest.TEST_DIR);
        for (final File file : testDir.listFiles()) {
            file.delete();
        }
    }

    @BeforeMethod
    public static void prepare() {
        TikZUtils.reset();
        final File testDir = new File(MainTest.TEST_DIR);
        if (!testDir.exists()) {
            if (!testDir.mkdirs()) {
                throw new IllegalStateException("Cannot init test directory!");
            }
        }
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

    private static void binaryTest(
        final CheckedFunction<BinaryInput, Integer, IOException> algorithm,
        final BinaryTestCase[] cases,
        final BufferedReader exReader,
        final BufferedReader solReader
    ) throws IOException {
        int currentNodeNumber = 0;
        MainTest.solutionSpaceBeginning(exReader, solReader);
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
        MainTest.solutionSpaceEnd(exReader, solReader);
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

    private static int checkEmptyNodeRowAndReturnNextNodeNumber(
        final String firstLine,
        final BufferedReader reader,
        final int initialNodeNumber,
        final Optional<Integer> optionalBelowOf,
        final int length
    ) throws IOException {
        int nodeNumber = initialNodeNumber;
        if (optionalBelowOf.isPresent()) {
            final String belowRegex =
                String.format(
                    "%s \\(n%d\\) \\[below=of n%d\\] \\{%s\\};",
                    MainTest.EMPTY_NODE_MATCH,
                    nodeNumber++,
                    optionalBelowOf.get(),
                    MainTest.PHANTOM_MATCH
                );
            Assert.assertTrue(
                firstLine.matches(belowRegex),
                String.format(MainTest.MATCH_MESSAGE_PATTERN, firstLine, belowRegex)
            );
        } else {
            final String simpleRegex =
                String.format(
                    "%s \\(n%d\\) \\{%s\\};",
                    nodeNumber++,
                    MainTest.PHANTOM_MATCH,
                    MainTest.EMPTY_NODE_MATCH
                );
            Assert.assertTrue(
                firstLine.matches(simpleRegex),
                String.format(MainTest.MATCH_MESSAGE_PATTERN, firstLine, simpleRegex)
            );
        }
        for (int i = 1; i < length; i++) {
            final String line = reader.readLine();
            final String rightRegex =
                String.format(
                    "%s \\(n%d\\) \\[right=of n%d\\] \\{%s\\};",
                    MainTest.EMPTY_NODE_MATCH,
                    nodeNumber,
                    nodeNumber - 1,
                    MainTest.PHANTOM_MATCH
                );
            Assert.assertTrue(
                line.matches(rightRegex),
                String.format(MainTest.MATCH_MESSAGE_PATTERN, line, rightRegex)
            );
            nodeNumber++;
        }
        return nodeNumber;
    }

    private static void checkExerciseTitle(final BufferedReader reader) throws IOException {
        Assert.assertEquals(reader.readLine(), "{\\large Aufgabe}\\\\[3ex]");
        Assert.assertEquals(reader.readLine(), "");
    }

    private static void checkLaTeXEpilogue(final BufferedReader reader) throws IOException {
        Assert.assertEquals(reader.readLine(), "");
        String nextLine = reader.readLine();
        while (nextLine != null && nextLine.isBlank()) {
            nextLine = reader.readLine();
        }
        Assert.assertEquals(nextLine, "\\end{document}");
        Assert.assertEquals(reader.readLine(), "");
        Assert.assertNull(reader.readLine());
    }

    private static void checkLaTeXPreamble(final BufferedReader reader) throws IOException {
        Assert.assertEquals(reader.readLine(), "\\documentclass{article}");
        Assert.assertEquals(reader.readLine(), "");
        Assert.assertEquals(reader.readLine(), "\\usepackage[ngerman]{babel}");
        Assert.assertEquals(reader.readLine(), "\\usepackage[T1]{fontenc}");
        Assert.assertEquals(reader.readLine(), "\\usepackage[table]{xcolor}");
        Assert.assertEquals(reader.readLine(), "\\usepackage[a4paper,margin=2cm]{geometry}");
        Assert.assertEquals(reader.readLine(), "\\usepackage{tikz}");
        Assert.assertEquals(
            reader.readLine(),
            "\\usetikzlibrary{arrows,shapes.misc,shapes.arrows,shapes.multipart,shapes.geometric,chains,matrix,positioning,scopes,decorations.pathmorphing,decorations.pathreplacing,shadows,calc,trees,backgrounds}"
        );
        Assert.assertEquals(reader.readLine(), "\\usepackage{tikz-qtree}");
        Assert.assertEquals(reader.readLine(), "\\usepackage{calc}");
        Assert.assertEquals(reader.readLine(), "\\usepackage{array}");
        Assert.assertEquals(reader.readLine(), "\\usepackage{amsmath}");
        Assert.assertEquals(reader.readLine(), "\\usepackage{enumerate}");
        Assert.assertEquals(reader.readLine(), "");
        Assert.assertEquals(
            reader.readLine(),
            "\\newcolumntype{C}[1]{>{\\centering\\let\\newline\\\\\\arraybackslash\\hspace{0pt}}m{#1}}"
        );
        Assert.assertEquals(reader.readLine(), "");
        Assert.assertEquals(reader.readLine(), "\\setlength{\\parindent}{0pt}");
        Assert.assertEquals(reader.readLine(), "");
        Assert.assertEquals(reader.readLine(), "\\newcommand{\\code}[1]{\\textnormal{\\texttt{#1}}}");
        Assert.assertEquals(reader.readLine(), "\\newcommand{\\emphasize}[1]{\\textbf{#1}}");
        Assert.assertEquals(reader.readLine(), "\\newcommand*\\circled[1]{\\tikz[baseline=(char.base)]{");
        Assert.assertEquals(reader.readLine(), "            \\node[shape=circle,draw,inner sep=2pt] (char) {#1};}}");
        Assert.assertEquals(reader.readLine(), "");
        Assert.assertEquals(reader.readLine(), "\\begin{document}");
        Assert.assertEquals(reader.readLine(), "");
    }

    private static int checkNodeRowWithRandomNumbersAndReturnNextNodeNumber(
        final BufferedReader reader,
        final int initialNodeNumber,
        final Optional<Integer> optionalBelowOf,
        final int length
    ) throws IOException {
        return MainTest.checkNodeRowWithRandomNumbersAndReturnNextNodeNumber(
            reader.readLine(),
            reader,
            initialNodeNumber,
            optionalBelowOf,
            length
        );
    }

    private static int checkNodeRowWithRandomNumbersAndReturnNextNodeNumber(
        final String firstLine,
        final BufferedReader reader,
        final int initialNodeNumber,
        final Optional<Integer> optionalBelowOf,
        final int length
    ) throws IOException {
        int nodeNumber = initialNodeNumber;
        if (optionalBelowOf.isPresent()) {
            final String belowRegex =
                String.format(
                    "%s \\(n%d\\) \\[below=of n%d\\] \\{%s%s\\};",
                    MainTest.NODE_MATCH,
                    nodeNumber++,
                    optionalBelowOf.get(),
                    MainTest.PHANTOM_MATCH,
                    MainTest.NUMBER_MATCH
                );
            Assert.assertTrue(
                firstLine.matches(belowRegex),
                String.format(MainTest.MATCH_MESSAGE_PATTERN, firstLine, belowRegex)
            );
        } else {
            final String simpleRegex =
                String.format(
                    "%s \\(n%d\\) \\{%s%s\\};",
                    MainTest.NODE_MATCH,
                    nodeNumber++,
                    MainTest.PHANTOM_MATCH,
                    MainTest.NUMBER_MATCH
                );
            Assert.assertTrue(
                firstLine.matches(simpleRegex),
                String.format(MainTest.MATCH_MESSAGE_PATTERN, firstLine, simpleRegex)
            );
        }
        for (int i = 1; i < length; i++) {
            final String line = reader.readLine();
            final String rightRegex =
                String.format(
                    "%s \\(n%d\\) \\[right=(0\\.1 )?of n%d\\] \\{%s%s\\};",
                    MainTest.NODE_MATCH,
                    nodeNumber,
                    nodeNumber - 1,
                    MainTest.PHANTOM_MATCH,
                    MainTest.NUMBER_MATCH
                );
            Assert.assertTrue(
                line.matches(rightRegex),
                String.format(MainTest.MATCH_MESSAGE_PATTERN, line, rightRegex)
            );
            nodeNumber++;
        }
        return nodeNumber;
    }

    private static void checkSolutionTitle(final BufferedReader reader) throws IOException {
        Assert.assertEquals(reader.readLine(), "{\\large L\\\"osung}\\\\[3ex]");
        Assert.assertEquals(reader.readLine(), "");
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

    private static void solutionSpaceBeginning(final BufferedReader exReader, final BufferedReader solReader)
    throws IOException {
        Assert.assertEquals(exReader.readLine(), "\\ifprintanswers");
        Assert.assertEquals(exReader.readLine(), "");
        Assert.assertEquals(exReader.readLine(), "\\vspace*{-3ex}");
        Assert.assertEquals(exReader.readLine(), "");
        Assert.assertEquals(exReader.readLine(), "\\else");
    }

    private static void solutionSpaceEnd(final BufferedReader exReader, final BufferedReader solReader)
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


    @Test
    public void decodeHuffman() throws IOException {
        Main.main(
            new String[]{
                "-a", "fromhuff",
                "-x", Main.EMBEDDED,
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
            Assert.assertEquals(
                exReader.readLine(),
                "Erzeugen Sie den Quelltext aus dem nachfolgenden Huffman Code mit dem angegebenen Codebuch:\\\\"
            );
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
        Main.main(
            new String[]{
                "-a", "dijkstra",
                "-x", Main.EMBEDDED,
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
            Assert.assertEquals(
                exReader.readLine(),
                "[scale=2.4, node/.style={circle,draw=black,thin,inner sep=5pt}, >=stealth, p/.style={->, thin, shorten <=2pt, shorten >=2pt}]"
            );
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
            Assert.assertEquals(
                exReader.readLine(),
                "F\\\"uhren Sie den \\emphasize{Dijkstra} Algorithmus auf diesem Graphen mit dem \\emphasize{Startknoten A} aus. F\\\"ullen Sie dazu die nachfolgende Tabelle aus:\\\\[2ex]"
            );
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
            Assert.assertEquals(
                solReader.readLine(),
                "\\textbf{Knoten} & \\textbf{A} & \\textbf{C} & \\textbf{D}\\\\\\hline"
            );
            Assert.assertEquals(
                solReader.readLine(),
                "\\textbf{B} & $\\infty$ & $\\infty$ & \\cellcolor{black!20}12\\\\\\hline"
            );
            Assert.assertEquals(
                solReader.readLine(),
                "\\textbf{C} & \\cellcolor{black!20}5 & \\textbf{--} & \\textbf{--}\\\\\\hline"
            );
            Assert.assertEquals(
                solReader.readLine(),
                "\\textbf{D} & $\\infty$ & \\cellcolor{black!20}9 & \\textbf{--}\\\\\\hline"
            );
            Assert.assertEquals(solReader.readLine(), "\\end{tabular}");
            Assert.assertEquals(solReader.readLine(), "\\renewcommand{\\arraystretch}{1.0}");
            Assert.assertEquals(solReader.readLine(), "\\end{center}");
            Assert.assertEquals(solReader.readLine(), "");
            Assert.assertEquals(solReader.readLine(), "\\vspace*{1ex}");
            Assert.assertEquals(solReader.readLine(), "");
            Assert.assertEquals(
                solReader.readLine(),
                "Die grau unterlegten Zellen markieren, an welcher Stelle f\\\"ur welchen Knoten die minimale Distanz sicher berechnet worden ist."
            );
            Assert.assertEquals(solReader.readLine(), "");
            Assert.assertNull(solReader.readLine());
        }
    }

    @Test
    public void encodeHuffman() throws IOException {
        Main.main(
            new String[]{
                "-a", "tohuff",
                "-x", Main.EMBEDDED,
                "-e", MainTest.EX_FILE,
                "-t", MainTest.SOL_FILE,
                "-i", "GEIERMEIER",
            }
        );
        try (
            BufferedReader exReader = new BufferedReader(new FileReader(MainTest.EX_FILE));
            BufferedReader solReader = new BufferedReader(new FileReader(MainTest.SOL_FILE));
        ) {
            Assert.assertEquals(
                exReader.readLine(),
                "Erzeugen Sie den Huffman Code f\\\"ur das Zielalphabet $\\{0,1\\}$ und den folgenden Eingabetext:\\\\"
            );
            Assert.assertEquals(exReader.readLine(), "\\begin{center}");
            Assert.assertEquals(exReader.readLine(), "GEIERMEIER");
            Assert.assertEquals(exReader.readLine(), "\\end{center}");
            Assert.assertEquals(exReader.readLine(), "");
            Assert.assertEquals(exReader.readLine(), "\\vspace*{1ex}");
            Assert.assertEquals(exReader.readLine(), "");
            Assert.assertEquals(
                exReader.readLine(),
                "Geben Sie zus\\\"atzlich zu dem erstellten Code das erzeugte Codebuch an.\\\\[2ex]"
            );
            MainTest.solutionSpaceBeginning(exReader, solReader);
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
            MainTest.solutionSpaceEnd(exReader, solReader);
        }
    }

    @Test
    public void fromFloat() throws IOException {
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
                "-x", Main.EMBEDDED,
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
                "-x", Main.EMBEDDED,
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
                "-x", Main.EMBEDDED,
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
        Main.main(
            new String[]{
                "-a", "hashMultiplicationQuadratic",
                "-x", Main.EMBEDDED,
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
            final int contentLength = 1;
            int nodeNumber = 0;
            Assert.assertEquals(
                exReader.readLine(),
                "F\\\"ugen Sie die folgenden Werte nacheinander in das unten stehende Array \\code{a} der L\\\"ange 11 unter Verwendung der \\emphasize{Multiplikationsmethode} ($c = 0,70$) mit \\emphasize{quadratischer Sondierung} ($c_1 = 7$, $c_2 = 3$) ein:\\\\"
            );
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
            Assert.assertEquals(exReader.readLine(), Patterns.singleEmptyNode(nodeNumber++, contentLength));
            for (int i = 1; i < 11; i++) {
                Assert.assertEquals(
                    exReader.readLine(),
                    Patterns.rightEmptyNodeToPredecessor(nodeNumber++, contentLength)
                );
            }
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
            Assert.assertEquals(solReader.readLine(), Patterns.singleEmptyNode(nodeNumber++, contentLength));
            Assert.assertEquals(solReader.readLine(), Patterns.rightNodeToPredecessor(nodeNumber++, "3"));
            Assert.assertEquals(
                solReader.readLine(),
                Patterns.rightEmptyNodeToPredecessor(nodeNumber++, contentLength)
            );
            Assert.assertEquals(
                solReader.readLine(),
                Patterns.rightEmptyNodeToPredecessor(nodeNumber++, contentLength)
            );
            Assert.assertEquals(solReader.readLine(), Patterns.rightNodeToPredecessor(nodeNumber++, "2"));
            Assert.assertEquals(solReader.readLine(), Patterns.rightNodeToPredecessor(nodeNumber++, "5"));
            Assert.assertEquals(solReader.readLine(), Patterns.rightNodeToPredecessor(nodeNumber++, "1"));
            Assert.assertEquals(solReader.readLine(), Patterns.rightNodeToPredecessor(nodeNumber++, "1"));
            Assert.assertEquals(solReader.readLine(), Patterns.rightNodeToPredecessor(nodeNumber++, "4"));
            Assert.assertEquals(
                solReader.readLine(),
                Patterns.rightEmptyNodeToPredecessor(nodeNumber++, contentLength)
            );
            Assert.assertEquals(
                solReader.readLine(),
                Patterns.rightEmptyNodeToPredecessor(nodeNumber++, contentLength)
            );
            Assert.assertEquals(solReader.readLine(), "\\end{tikzpicture}");
            Assert.assertEquals(solReader.readLine(), "}");
            Assert.assertEquals(solReader.readLine(), "\\end{center}");
            Assert.assertEquals(solReader.readLine(), "");
            Assert.assertNull(solReader.readLine());
        }
    }

    @Test
    public void insertionsort() throws IOException {
        Main.main(
            new String[]{
                "-a", "insertionsort",
                "-x", Main.EMBEDDED,
                "-e", MainTest.EX_FILE,
                "-t", MainTest.SOL_FILE,
                "-i", "3,5,1,4,2"
            }
        );
        try (
            BufferedReader exReader = new BufferedReader(new FileReader(MainTest.EX_FILE));
            BufferedReader solReader = new BufferedReader(new FileReader(MainTest.SOL_FILE));
        ) {
            final int contentLength = 1;
            int nodeNumber = 0;
            Assert.assertEquals(exReader.readLine(), "Sortieren Sie das folgende Array mithilfe von Insertionsort.");
            Assert.assertEquals(
                exReader.readLine(),
                "Geben Sie dazu das Array nach jeder Iteration der \\\"au\\ss{}eren Schleife an.\\\\[2ex]"
            );

            MainTest.solutionSpaceBeginning(exReader, solReader);

            Assert.assertEquals(exReader.readLine(), "\\begin{tikzpicture}");
            Assert.assertEquals(exReader.readLine(), Patterns.ARRAY_STYLE);
            Assert.assertEquals(exReader.readLine(), Patterns.singleNode(nodeNumber++, "3", contentLength));
            Assert.assertEquals(exReader.readLine(), Patterns.rightNodeToPredecessor(nodeNumber++, "5"));
            Assert.assertEquals(exReader.readLine(), Patterns.rightNodeToPredecessor(nodeNumber++, "1"));
            Assert.assertEquals(exReader.readLine(), Patterns.rightNodeToPredecessor(nodeNumber++, "4"));
            Assert.assertEquals(exReader.readLine(), Patterns.rightNodeToPredecessor(nodeNumber++, "2"));
            int belowOf = 0;
            for (int i = 0; i < 4; i++) {
                Assert.assertEquals(exReader.readLine(), Patterns.belowEmptyNode(nodeNumber++, belowOf, contentLength));
                belowOf = nodeNumber - 1;
                for (int j = 0; j < 4; j++) {
                    Assert.assertEquals(
                        exReader.readLine(),
                        Patterns.rightEmptyNodeToPredecessor(nodeNumber++, contentLength)
                    );
                }
            }
            Assert.assertEquals(exReader.readLine(), "\\end{tikzpicture}");

            Assert.assertEquals(solReader.readLine(), "\\begin{tikzpicture}");
            Assert.assertEquals(solReader.readLine(), Patterns.ARRAY_STYLE);
            belowOf = nodeNumber;
            Assert.assertEquals(solReader.readLine(), Patterns.singleNode(nodeNumber++, "3", contentLength));
            Assert.assertEquals(solReader.readLine(), Patterns.rightNodeToPredecessor(nodeNumber++, "5"));
            Assert.assertEquals(solReader.readLine(), Patterns.rightNodeToPredecessor(nodeNumber++, "1"));
            Assert.assertEquals(solReader.readLine(), Patterns.rightNodeToPredecessor(nodeNumber++, "4"));
            Assert.assertEquals(solReader.readLine(), Patterns.rightNodeToPredecessor(nodeNumber++, "2"));
            Assert.assertEquals(solReader.readLine(), Patterns.belowNode(nodeNumber++, belowOf, 3, contentLength));
            belowOf = nodeNumber - 1;
            Assert.assertEquals(solReader.readLine(), Patterns.rightNodeToPredecessor(nodeNumber++, "5"));
            Assert.assertEquals(solReader.readLine(), Patterns.rightNodeToPredecessor(nodeNumber++, "1"));
            Assert.assertEquals(solReader.readLine(), Patterns.rightNodeToPredecessor(nodeNumber++, "4"));
            Assert.assertEquals(solReader.readLine(), Patterns.rightNodeToPredecessor(nodeNumber++, "2"));
            Assert.assertEquals(solReader.readLine(), Patterns.belowNode(nodeNumber++, belowOf, 1, contentLength));
            belowOf = nodeNumber - 1;
            Assert.assertEquals(solReader.readLine(), Patterns.rightNodeToPredecessor(nodeNumber++, "3"));
            Assert.assertEquals(solReader.readLine(), Patterns.rightNodeToPredecessor(nodeNumber++, "5"));
            Assert.assertEquals(solReader.readLine(), Patterns.rightNodeToPredecessor(nodeNumber++, "4"));
            Assert.assertEquals(solReader.readLine(), Patterns.rightNodeToPredecessor(nodeNumber++, "2"));
            Assert.assertEquals(solReader.readLine(), Patterns.belowNode(nodeNumber++, belowOf, 1, contentLength));
            belowOf = nodeNumber - 1;
            Assert.assertEquals(solReader.readLine(), Patterns.rightNodeToPredecessor(nodeNumber++, "3"));
            Assert.assertEquals(solReader.readLine(), Patterns.rightNodeToPredecessor(nodeNumber++, "4"));
            Assert.assertEquals(solReader.readLine(), Patterns.rightNodeToPredecessor(nodeNumber++, "5"));
            Assert.assertEquals(solReader.readLine(), Patterns.rightNodeToPredecessor(nodeNumber++, "2"));
            Assert.assertEquals(solReader.readLine(), Patterns.belowNode(nodeNumber++, belowOf, 1, contentLength));
            Assert.assertEquals(solReader.readLine(), Patterns.rightNodeToPredecessor(nodeNumber++, "2"));
            Assert.assertEquals(solReader.readLine(), Patterns.rightNodeToPredecessor(nodeNumber++, "3"));
            Assert.assertEquals(solReader.readLine(), Patterns.rightNodeToPredecessor(nodeNumber++, "4"));
            Assert.assertEquals(solReader.readLine(), Patterns.rightNodeToPredecessor(nodeNumber++, "5"));
            Assert.assertEquals(solReader.readLine(), "\\end{tikzpicture}");

            MainTest.solutionSpaceEnd(exReader, solReader);
        }
    }

    @Test
    public void quicksortStandalone() throws IOException {
        Main.main(
            new String[]{
                "-a", "quicksort",
                "-e", MainTest.EX_FILE,
                "-t", MainTest.SOL_FILE,
                "-l", "5"
            }
        );
        try (
            BufferedReader exReader = new BufferedReader(new FileReader(MainTest.EX_FILE));
            BufferedReader solReader = new BufferedReader(new FileReader(MainTest.SOL_FILE));
        ) {
            final int length = 5;
            int nodeNumber = 0;
            MainTest.checkLaTeXPreamble(exReader);
            MainTest.checkExerciseTitle(exReader);
            Assert.assertEquals(exReader.readLine(), "Sortieren Sie das folgende Array mithilfe von Quicksort.");
            Assert.assertEquals(
                exReader.readLine(),
                "Geben Sie dazu das Array nach jeder Partition-Operation an und markieren Sie das jeweils verwendete Pivot-Element.\\\\[2ex]"
            );
            Assert.assertEquals(exReader.readLine(), "\\begin{tikzpicture}");
            Assert.assertEquals(exReader.readLine(), Patterns.ARRAY_STYLE);
            int belowOf = nodeNumber;
            nodeNumber =
                MainTest.checkNodeRowWithRandomNumbersAndReturnNextNodeNumber(
                    exReader,
                    nodeNumber,
                    Optional.empty(),
                    length
                );
            String nextLine = exReader.readLine();
            while (nextLine != null && !nextLine.equals("\\end{tikzpicture}")) {
                final int storeNodeNumber = nodeNumber;
                nodeNumber =
                    MainTest.checkEmptyNodeRowAndReturnNextNodeNumber(
                        nextLine,
                        exReader,
                        nodeNumber,
                        Optional.of(belowOf),
                        length
                    );
                belowOf = storeNodeNumber;
                nextLine = exReader.readLine();
            }
            Assert.assertEquals(nextLine, "\\end{tikzpicture}");
            MainTest.checkLaTeXEpilogue(exReader);

            MainTest.checkLaTeXPreamble(solReader);
            MainTest.checkSolutionTitle(solReader);
            Assert.assertEquals(solReader.readLine(), "\\begin{tikzpicture}");
            Assert.assertEquals(solReader.readLine(), Patterns.ARRAY_STYLE);
            belowOf = nodeNumber;
            nodeNumber =
                MainTest.checkNodeRowWithRandomNumbersAndReturnNextNodeNumber(
                    solReader,
                    nodeNumber,
                    Optional.empty(),
                    length
                );
            nextLine = solReader.readLine();
            while (nextLine != null && !nextLine.equals("\\end{tikzpicture}")) {
                final int storeNodeNumber = nodeNumber;
                nodeNumber =
                    MainTest.checkNodeRowWithRandomNumbersAndReturnNextNodeNumber(
                        nextLine,
                        solReader,
                        nodeNumber,
                        Optional.of(belowOf),
                        length
                    );
                belowOf = storeNodeNumber;
                nextLine = solReader.readLine();
            }
            Assert.assertEquals(nextLine, "\\end{tikzpicture}");
            MainTest.checkLaTeXEpilogue(solReader);
        }
    }

    @Test
    public void toFloat() throws IOException {
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
                "-x", Main.EMBEDDED,
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
                "-x", Main.EMBEDDED,
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
                "-x", Main.EMBEDDED,
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
        Main.main(
            new String[]{
                "-a", "fromvigenere",
                "-x", Main.EMBEDDED,
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
        Main.main(
            new String[]{
                "-a", "tovigenere",
                "-x", Main.EMBEDDED,
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
