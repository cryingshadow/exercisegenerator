package exercisegenerator;

import java.io.*;
import java.util.*;
import java.util.Optional;
import java.util.stream.*;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.graphs.LabeledNode;
import exercisegenerator.util.*;

//TODO heapsort with trees, additional complete sorting tests

public class MainTest {

    private static class BinaryInput {
        private final int currentNodeNumber;
        private final List<String> exText;
        private final List<String> solText;
        private final BinaryTestCase test;

        private BinaryInput(
            final int currentNodeNumber,
            final BinaryTestCase test,
            final List<String> exText,
            final List<String> solText
        ) {
            this.currentNodeNumber = currentNodeNumber;
            this.test = test;
            this.exText = exText;
            this.solText = solText;
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

    public static final String EX_FILE_NAME;

    public static final String SOL_FILE_NAME;

    private static final String EMPTY_NODE_MATCH;

    private static final String MATCH_MESSAGE_PATTERN;

    private static final List<String> MIDDLE_SPACE = List.of("", "\\vspace*{1ex}", "");

    private static final String NODE_MATCH;

    private static final String NUMBER_MATCH;

    private static final String PHANTOM_MATCH;

    private static final List<String> SOLUTION_SPACE_BEGINNING =
        List.of(
            "\\ifprintanswers",
            "",
            "\\vspace*{-3ex}",
            "",
            "\\else"
        );

    private static final List<String> SOLUTION_SPACE_END =
        List.of(
            "",
            "\\vspace*{1ex}",
            "",
            "\\fi"
        );

    private static final String TEX_SUFFIX;

    static {
        EX_FILE_NAME = "exercise";
        SOL_FILE_NAME = "solution";
        TEX_SUFFIX = "tex";
        EMPTY_NODE_MATCH = "\\\\node\\[node\\]";
        MATCH_MESSAGE_PATTERN = "%s does not match %s";
        NODE_MATCH = "\\\\node\\[node(,fill=black!20)?\\]";
        NUMBER_MATCH = "(-?\\d+)";
        PHANTOM_MATCH = "(\\\\phantom\\{0+\\})?";
    }

    private static void binaryTest(
        final CheckedFunction<BinaryInput, Integer, IOException> algorithm,
        final BinaryTestCase[] cases,
        final List<String> exText,
        final List<String> solText
    ) throws IOException {
        int currentNodeNumber = 0;
        exText.addAll(MainTest.SOLUTION_SPACE_BEGINNING);
        boolean first = true;
        for (final BinaryTestCase test : cases) {
            if (first) {
                first = false;
            } else {
                exText.addAll(MainTest.MIDDLE_SPACE);
                solText.addAll(MainTest.MIDDLE_SPACE);
            }
            currentNodeNumber =
                algorithm.apply(new BinaryInput(currentNodeNumber, test, exText, solText));
        }
        exText.addAll(MainTest.SOLUTION_SPACE_END);
    }

    private static int checkAssignment(
        int currentNodeNumber,
        final String leftHandSide,
        final List<String> rightHandSide,
        final int contentLength,
        final String longestLeftHandSide,
        final List<String> exText,
        final List<String> solText
    ) {
        exText.add(Patterns.beginMinipageForAssignmentLeftHandSide(longestLeftHandSide));
        exText.add("\\begin{flushright}");
        exText.add(Patterns.forAssignmentLeftHandSide(leftHandSide));
        exText.add("\\end{flushright}");
        exText.add("\\end{minipage}");
        exText.add(Patterns.beginMinipageForAssignmentRightHandSide(longestLeftHandSide));
        exText.add("\\begin{tikzpicture}");
        exText.add(Patterns.ARRAY_STYLE);
        exText.add(Patterns.singleEmptyNode(currentNodeNumber++, contentLength));
        for (int i = 1; i < rightHandSide.size(); i++) {
            exText.add(Patterns.rightEmptyNodeToPredecessor(currentNodeNumber++, contentLength));
        }
        exText.add("\\end{tikzpicture}");
        exText.add("\\end{minipage}");

        solText.add(Patterns.beginMinipageForAssignmentLeftHandSide(longestLeftHandSide));
        solText.add("\\begin{flushright}");
        solText.add(Patterns.forAssignmentLeftHandSide(leftHandSide));
        solText.add("\\end{flushright}");
        solText.add("\\end{minipage}");
        solText.add(Patterns.beginMinipageForAssignmentRightHandSide(longestLeftHandSide));
        solText.add("\\begin{tikzpicture}");
        solText.add(Patterns.ARRAY_STYLE);
        solText.add(Patterns.singleNode(currentNodeNumber++, rightHandSide.get(0), contentLength));
        for (int i = 1; i < rightHandSide.size(); i++) {
            solText.add(Patterns.rightNodeToPredecessor(currentNodeNumber++, rightHandSide.get(i)));
        }
        solText.add("\\end{tikzpicture}");
        solText.add("\\end{minipage}");
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
        Assert.assertEquals(reader.readLine(), "\\newcommand{\\var}[1]{\\textit{#1}}");
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

    private static CheckedBiConsumer<BufferedReader, BufferedReader, IOException> fromBinary(
        final BinaryTestCase[] cases,
        final String taskDescription
    ) throws IOException {
        final List<String> exText = new LinkedList<String>();
        final List<String> solText = new LinkedList<String>();
        exText.add(taskDescription);
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
                input.exText,
                input.solText
            ),
            cases,
            exText,
            solText
        );
        return MainTest.simpleComparison(exText, solText);
    }

    private static CheckedBiConsumer<BufferedReader, BufferedReader, IOException> simpleComparison(
        final List<String> exText,
        final List<String> solText
    ) {
        return (exReader, solReader) -> {
            for (final String line : exText) {
                Assert.assertEquals(exReader.readLine(), line);
            }
            for (final String line : solText) {
                Assert.assertEquals(solReader.readLine(), line);
            }
        };
    }

    private static CheckedBiConsumer<BufferedReader, BufferedReader, IOException> toBinary(
        final BinaryTestCase[] cases,
        final String taskDescription
    ) throws IOException {
        final List<String> exText = new LinkedList<String>();
        final List<String> solText = new LinkedList<String>();
        exText.add(taskDescription);
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
                input.exText,
                input.solText
            ),
            cases,
            exText,
            solText
        );
        return MainTest.simpleComparison(exText, solText);
    }

    private static String toBitStringInput(final BinaryTestCase[] cases) {
        return Arrays.stream(cases)
            .map(c -> Arrays.stream(c.binaryNumber).mapToObj(n -> String.valueOf(n)).collect(Collectors.joining()))
            .collect(Collectors.joining(";"));
    }

    private static String toNumberInput(final BinaryTestCase[] cases) {
        return Arrays.stream(cases).map(c -> c.number).collect(Collectors.joining(";"));
    }

    private final List<File> tmpFiles = new LinkedList<File>();

    @AfterMethod
    public void cleanUp() {
        for (final File file : this.tmpFiles) {
            file.delete();
        }
        this.tmpFiles.clear();
    }

    @Test
    public void decodeHuffman() throws IOException {
        this.harness(
            new String[] {
                "-a", Algorithm.FROM_HUFFMAN.name,
                "-x", Main.EMBEDDED_EXAM,
                "-i", "01100001100001111101",
                "-o", "'I':\"00\",'K':\"01\",'L':\"100\",'M':\"101\",'N':\"110\",'U':\"111\""
            },
            MainTest.simpleComparison(
                List.of(
                    "Erzeugen Sie den Quelltext aus dem nachfolgenden Huffman Code mit dem angegebenen Codebuch:\\\\",
                    "\\begin{center}",
                    "\\code{01100001100001111101}",
                    "\\end{center}",
                    "",
                    "\\vspace*{1ex}",
                    "",
                    "\\textbf{Codebuch:}",
                    "\\begin{align*}",
                    "\\code{`I'} &= \\code{\\textquotedbl{}00\\textquotedbl{}}\\\\",
                    "\\code{`K'} &= \\code{\\textquotedbl{}01\\textquotedbl{}}\\\\",
                    "\\code{`L'} &= \\code{\\textquotedbl{}100\\textquotedbl{}}\\\\",
                    "\\code{`M'} &= \\code{\\textquotedbl{}101\\textquotedbl{}}\\\\",
                    "\\code{`N'} &= \\code{\\textquotedbl{}110\\textquotedbl{}}\\\\",
                    "\\code{`U'} &= \\code{\\textquotedbl{}111\\textquotedbl{}}\\\\",
                    "\\end{align*}",
                    "",
                    "\\vspace*{-3ex}",
                    "",
                    "\\textbf{Quelltext:}\\\\[2ex]"
                ),
                List.of("\\code{KLINIKUM}")
            )
        );
    }

    @Test
    public void dijkstra() throws IOException {
        this.harness(
            new String[] {
                "-a", Algorithm.DIJKSTRA.name,
                "-x", Main.EMBEDDED_EXAM,
                "-p", "solutionSpace",
                "-i", " A , |2, B \n5| , | , |3\n C ,4| , D",
                "-o", "A"
            },
            MainTest.simpleComparison(
                List.of(
                    "Betrachten Sie den folgenden Graphen:\\\\[2ex]",
                    "\\begin{center}",
                    "\\begin{tikzpicture}",
                    "[scale=2.4, node/.style={circle,draw=black,thin,inner sep=5pt}, >=stealth, p/.style={->, thin, shorten <=2pt, shorten >=2pt}]",
                    "\\node[node] (n1) at (0.0,1.0) {A};",
                    "\\node[node] (n2) at (1.0,1.0) {B};",
                    "\\node[node] (n3) at (0.0,0.0) {C};",
                    "\\node[node] (n4) at (1.0,0.0) {D};",
                    "\\draw[p, bend right = 10] (n1) to node[auto, swap] {5} (n3);",
                    "\\draw[p, bend right = 10] (n2) to node[auto, swap] {2} (n1);",
                    "\\draw[p, bend right = 10] (n3) to node[auto, swap] {4} (n4);",
                    "\\draw[p, bend right = 10] (n4) to node[auto, swap] {3} (n2);",
                    "\\end{tikzpicture}",
                    "",
                    "",
                    "\\end{center}",
                    "",
                    "F\\\"uhren Sie den \\emphasize{Dijkstra} Algorithmus auf diesem Graphen mit dem \\emphasize{Startknoten A} aus. F\\\"ullen Sie dazu die nachfolgende Tabelle aus:\\\\[2ex]",
                    "\\ifprintanswers",
                    "",
                    "\\vspace*{-3ex}",
                    "",
                    "\\else",
                    "\\begin{center}",
                    "",
                    "\\renewcommand{\\arraystretch}{1.5}",
                    "\\begin{tabular}{|*{4}{C{2cm}|}}",
                    "\\hline",
                    "\\textbf{Knoten} & \\textbf{A} &  & \\\\\\hline",
                    "\\textbf{B} &  &  & \\\\\\hline",
                    "\\textbf{C} &  &  & \\\\\\hline",
                    "\\textbf{D} &  &  & \\\\\\hline",
                    "\\end{tabular}",
                    "\\renewcommand{\\arraystretch}{1.0}",
                    "\\end{center}",
                    "",
                    "\\vspace*{1ex}",
                    "",
                    "\\fi"
                ),
                List.of(
                    "\\begin{center}",
                    "",
                    "\\renewcommand{\\arraystretch}{1.5}",
                    "\\begin{tabular}{|*{4}{C{2cm}|}}",
                    "\\hline",
                    "\\textbf{Knoten} & \\textbf{A} & \\textbf{C} & \\textbf{D}\\\\\\hline",
                    "\\textbf{B} & $\\infty$ & $\\infty$ & \\cellcolor{black!20}12\\\\\\hline",
                    "\\textbf{C} & \\cellcolor{black!20}5 & \\textbf{--} & \\textbf{--}\\\\\\hline",
                    "\\textbf{D} & $\\infty$ & \\cellcolor{black!20}9 & \\textbf{--}\\\\\\hline",
                    "\\end{tabular}",
                    "\\renewcommand{\\arraystretch}{1.0}",
                    "\\end{center}",
                    "",
                    "\\vspace*{1ex}",
                    "",
                    "Die grau unterlegten Zellen markieren, an welcher Stelle f\\\"ur welchen Knoten die minimale Distanz sicher berechnet worden ist."
                )
            )
        );
    }

    @Test
    public void encodeHuffman() throws IOException {
        final int longestCodeLength = 3;
        final String longestLeftHandSide = "\\code{`M'}";
        final List<String> exText = new LinkedList<String>();
        final List<String> solText = new LinkedList<String>();
        exText.addAll(
            List.of(
                "Erzeugen Sie den Huffman Code f\\\"ur das Zielalphabet $\\{0,1\\}$ und den folgenden Eingabetext:\\\\",
                "\\begin{center}",
                "GEIERMEIER",
                "\\end{center}",
                "",
                "\\vspace*{1ex}",
                "",
                "Geben Sie zus\\\"atzlich zu dem erstellten Code das erzeugte Codebuch an.\\\\[2ex]"
            )
        );
        exText.addAll(MainTest.SOLUTION_SPACE_BEGINNING);
        exText.add("\\textbf{Codebuch:}\\\\[2ex]");
        solText.add("\\textbf{Codebuch:}\\\\[2ex]");
        int currentNodeNumber =
            MainTest.checkAssignment(
                0,
                "\\code{`E'}",
                Collections.singletonList("\\code{11}"),
                longestCodeLength,
                longestLeftHandSide,
                exText,
                solText
            );
        currentNodeNumber =
            MainTest.checkAssignment(
                currentNodeNumber,
                "\\code{`G'}",
                Collections.singletonList("\\code{100}"),
                longestCodeLength,
                longestLeftHandSide,
                exText,
                solText
            );
        currentNodeNumber =
            MainTest.checkAssignment(
                currentNodeNumber,
                "\\code{`I'}",
                Collections.singletonList("\\code{00}"),
                longestCodeLength,
                longestLeftHandSide,
                exText,
                solText
            );
        currentNodeNumber =
            MainTest.checkAssignment(
                currentNodeNumber,
                "\\code{`M'}",
                Collections.singletonList("\\code{101}"),
                longestCodeLength,
                longestLeftHandSide,
                exText,
                solText
            );
        currentNodeNumber =
            MainTest.checkAssignment(
                currentNodeNumber,
                "\\code{`R'}",
                Collections.singletonList("\\code{01}"),
                longestCodeLength,
                longestLeftHandSide,
                exText,
                solText
            );
        exText.addAll(MainTest.MIDDLE_SPACE);
        exText.add("\\textbf{Code:}\\\\");
        exText.addAll(MainTest.SOLUTION_SPACE_END);
        solText.addAll(MainTest.MIDDLE_SPACE);
        solText.add("\\textbf{Code:}\\\\");
        solText.add("\\code{1001100110110111001101}");
        this.harness(
            new String[] {
                "-a", Algorithm.TO_HUFFMAN.name,
                "-x", Main.EMBEDDED_EXAM,
                "-i", "GEIERMEIER",
            },
            MainTest.simpleComparison(exText, solText)
        );
    }

    @Test
    public void encodeHuffmanLaTeXEscaping() throws IOException {
        final int longestCodeLength = 3;
        final String longestLeftHandSide = "\\code{`M'}";
        final List<String> exText = new LinkedList<String>();
        final List<String> solText = new LinkedList<String>();
        exText.addAll(
            List.of(
                "Erzeugen Sie den Huffman Code f\\\"ur das Zielalphabet $\\{0,1\\}$ und den folgenden Eingabetext:\\\\",
                "\\begin{center}",
                "\\textbackslash{}\\&\\%\\&\\textasciicircum{}\\_\\&\\%\\&\\textasciicircum{}",
                "\\end{center}",
                "",
                "\\vspace*{1ex}",
                "",
                "Geben Sie zus\\\"atzlich zu dem erstellten Code das erzeugte Codebuch an.\\\\[2ex]"
            )
        );
        exText.addAll(MainTest.SOLUTION_SPACE_BEGINNING);
        exText.add("\\textbf{Codebuch:}\\\\[2ex]");
        solText.add("\\textbf{Codebuch:}\\\\[2ex]");
        int currentNodeNumber =
            MainTest.checkAssignment(
                0,
                "\\code{`\\%'}",
                Collections.singletonList("\\code{00}"),
                longestCodeLength,
                longestLeftHandSide,
                exText,
                solText
            );
        currentNodeNumber =
            MainTest.checkAssignment(
                currentNodeNumber,
                "\\code{`\\&'}",
                Collections.singletonList("\\code{11}"),
                longestCodeLength,
                longestLeftHandSide,
                exText,
                solText
            );
        currentNodeNumber =
            MainTest.checkAssignment(
                currentNodeNumber,
                "\\code{`\\textbackslash{}'}",
                Collections.singletonList("\\code{100}"),
                longestCodeLength,
                longestLeftHandSide,
                exText,
                solText
            );
        currentNodeNumber =
            MainTest.checkAssignment(
                currentNodeNumber,
                "\\code{`\\textasciicircum{}'}",
                Collections.singletonList("\\code{01}"),
                longestCodeLength,
                longestLeftHandSide,
                exText,
                solText
            );
        currentNodeNumber =
            MainTest.checkAssignment(
                currentNodeNumber,
                "\\code{`\\_'}",
                Collections.singletonList("\\code{101}"),
                longestCodeLength,
                longestLeftHandSide,
                exText,
                solText
            );
        exText.addAll(MainTest.MIDDLE_SPACE);
        exText.add("\\textbf{Code:}\\\\");
        exText.addAll(MainTest.SOLUTION_SPACE_END);
        solText.addAll(MainTest.MIDDLE_SPACE);
        solText.add("\\textbf{Code:}\\\\");
        solText.add("\\code{1001100110110111001101}");
        this.harness(
            new String[] {
                "-a", Algorithm.TO_HUFFMAN.name,
                "-x", Main.EMBEDDED_EXAM,
                "-i", "\\&%&^_&%&^",
            },
            MainTest.simpleComparison(exText, solText)
        );
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
        this.harness(
            new String[] {
                "-a", Algorithm.FROM_FLOAT.name,
                "-x", Main.EMBEDDED_EXAM,
                "-c", String.valueOf(mantisseLength),
                "-d", String.valueOf(exponentLength),
                "-i", MainTest.toBitStringInput(cases)
            },
            MainTest.fromBinary(cases, Patterns.fromFloat(exponentLength, mantisseLength))
        );
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
        this.harness(
            new String[] {
                "-a", Algorithm.FROM_ONES_COMPLEMENT.name,
                "-x", Main.EMBEDDED_EXAM,
                "-c", String.valueOf(bitLength),
                "-i", MainTest.toBitStringInput(cases)
            },
            MainTest.fromBinary(cases, Patterns.fromOnes(bitLength))
        );
    }

    @Test
    public void fromTruthTable() throws IOException {
        final List<String> exText = new LinkedList<String>();
        final List<String> solText = new LinkedList<String>();
        final List<String> firstTable =
            List.of(
                "\\begin{center}",
                "\\begin{tabular}{|*{4}{C{1em}|}C{4em}|}",
                "\\hline",
                "\\var{A} & \\var{B} & \\var{C} & \\var{D} & \\textit{Formel}\\\\\\hline",
                "\\code{0} & \\code{0} & \\code{0} & \\code{0} & \\code{1}\\\\\\hline",
                "\\code{0} & \\code{0} & \\code{0} & \\code{1} & \\code{0}\\\\\\hline",
                "\\code{0} & \\code{0} & \\code{1} & \\code{0} & \\code{0}\\\\\\hline",
                "\\code{0} & \\code{0} & \\code{1} & \\code{1} & \\code{0}\\\\\\hline",
                "\\code{0} & \\code{1} & \\code{0} & \\code{0} & \\code{0}\\\\\\hline",
                "\\code{0} & \\code{1} & \\code{0} & \\code{1} & \\code{1}\\\\\\hline",
                "\\code{0} & \\code{1} & \\code{1} & \\code{0} & \\code{0}\\\\\\hline",
                "\\code{0} & \\code{1} & \\code{1} & \\code{1} & \\code{0}\\\\\\hline",
                "\\code{1} & \\code{0} & \\code{0} & \\code{0} & \\code{0}\\\\\\hline",
                "\\code{1} & \\code{0} & \\code{0} & \\code{1} & \\code{1}\\\\\\hline",
                "\\code{1} & \\code{0} & \\code{1} & \\code{0} & \\code{0}\\\\\\hline",
                "\\code{1} & \\code{0} & \\code{1} & \\code{1} & \\code{0}\\\\\\hline",
                "\\code{1} & \\code{1} & \\code{0} & \\code{0} & \\code{0}\\\\\\hline",
                "\\code{1} & \\code{1} & \\code{0} & \\code{1} & \\code{0}\\\\\\hline",
                "\\code{1} & \\code{1} & \\code{1} & \\code{0} & \\code{1}\\\\\\hline",
                "\\code{1} & \\code{1} & \\code{1} & \\code{1} & \\code{0}\\\\\\hline",
                "\\end{tabular}",
                "\\end{center}"
            );
        final List<String> secondTable =
            List.of(
                "\\begin{center}",
                "\\begin{tabular}{|*{3}{C{1em}|}C{4em}|}",
                "\\hline",
                "\\var{A} & \\var{B} & \\var{C} & \\textit{Formel}\\\\\\hline",
                "\\code{0} & \\code{0} & \\code{0} & \\code{0}\\\\\\hline",
                "\\code{0} & \\code{0} & \\code{1} & \\code{0}\\\\\\hline",
                "\\code{0} & \\code{1} & \\code{0} & \\code{1}\\\\\\hline",
                "\\code{0} & \\code{1} & \\code{1} & \\code{1}\\\\\\hline",
                "\\code{1} & \\code{0} & \\code{0} & \\code{0}\\\\\\hline",
                "\\code{1} & \\code{0} & \\code{1} & \\code{1}\\\\\\hline",
                "\\code{1} & \\code{1} & \\code{0} & \\code{0}\\\\\\hline",
                "\\code{1} & \\code{1} & \\code{1} & \\code{1}\\\\\\hline",
                "\\end{tabular}",
                "\\end{center}"
            );
        exText.add("Geben Sie zu den folgenden Wahrheitstabellen jeweils eine aussagenlogische Formel an:\\\\");
        exText.addAll(MainTest.SOLUTION_SPACE_BEGINNING);
        exText.addAll(firstTable);
        exText.addAll(Patterns.middleSpace("4cm"));
        exText.addAll(secondTable);
        exText.addAll(MainTest.SOLUTION_SPACE_END);

        solText.addAll(firstTable);
        solText.addAll(
            List.of(
                "\\begin{align*}",
                " & \\neg\\var{A} \\wedge \\neg\\var{B} \\wedge \\neg\\var{C} \\wedge \\neg\\var{D}\\\\",
                "\\vee & \\neg\\var{A} \\wedge \\var{B} \\wedge \\neg\\var{C} \\wedge \\var{D}\\\\",
                "\\vee & \\var{A} \\wedge \\neg\\var{B} \\wedge \\neg\\var{C} \\wedge \\var{D}\\\\",
                "\\vee & \\var{A} \\wedge \\var{B} \\wedge \\var{C} \\wedge \\neg\\var{D}",
                "\\end{align*}"
            )
        );
        solText.addAll(MainTest.MIDDLE_SPACE);
        solText.addAll(secondTable);
        solText.addAll(
            List.of(
                "\\begin{align*}",
                " & \\neg\\var{A} \\wedge \\var{B} \\wedge \\neg\\var{C}\\\\",
                "\\vee & \\neg\\var{A} \\wedge \\var{B} \\wedge \\var{C}\\\\",
                "\\vee & \\var{A} \\wedge \\neg\\var{B} \\wedge \\var{C}\\\\",
                "\\vee & \\var{A} \\wedge \\var{B} \\wedge \\var{C}",
                "\\end{align*}"
            )
        );
        this.harness(
            new String[] {
                "-a", Algorithm.FROM_TRUTH_TABLE.name,
                "-x", Main.EMBEDDED_EXAM,
                "-i", "A,B,C,D;1000010001000010\nA,B,C;00110101"
            },
            MainTest.simpleComparison(exText, solText)
        );
    }

    @Test
    public void fromTruthTableConstant() throws IOException {
        final List<String> exText = new LinkedList<String>();
        final List<String> solText = new LinkedList<String>();
        final List<String> table =
            List.of(
                "\\begin{center}",
                "\\begin{tabular}{|C{4em}|}",
                "\\hline",
                "\\textit{Formel}\\\\\\hline",
                "\\code{1}\\\\\\hline",
                "\\end{tabular}",
                "\\end{center}"
            );
        exText.add("Geben Sie zu der folgenden Wahrheitstabelle eine aussagenlogische Formel an:\\\\");
        exText.addAll(MainTest.SOLUTION_SPACE_BEGINNING);
        exText.addAll(table);
        exText.addAll(MainTest.SOLUTION_SPACE_END);

        solText.addAll(table);
        solText.addAll(
            List.of(
                "\\begin{align*}",
                " & \\code{1}",
                "\\end{align*}"
            )
        );
        this.harness(
            new String[] {
                "-a", Algorithm.FROM_TRUTH_TABLE.name,
                "-x", Main.EMBEDDED_EXAM,
                "-i", ";1"
            },
            MainTest.simpleComparison(exText, solText)
        );
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
        this.harness(
            new String[] {
                "-a", Algorithm.FROM_TWOS_COMPLEMENT.name,
                "-x", Main.EMBEDDED_EXAM,
                "-c", String.valueOf(bitLength),
                "-i", MainTest.toBitStringInput(cases)
            },
            MainTest.fromBinary(cases, Patterns.fromTwos(bitLength))
        );
    }

    @Test
    public void hashing() throws IOException {
        final int contentLength = 1;
        int nodeNumber = 0;
        final List<String> exText = new LinkedList<String>();
        exText.addAll(
            List.of(
                "F\\\"ugen Sie die folgenden Werte nacheinander in das unten stehende Array \\code{a} der L\\\"ange 11 unter Verwendung der \\emphasize{Multiplikationsmethode} ($c = 0,70$) mit \\emphasize{quadratischer Sondierung} ($c_1 = 7$, $c_2 = 3$) ein:\\\\",
                "\\begin{center}",
                "3, 5, 1, 4, 2, 1.",
                "\\end{center}",
                "",
                "\\vspace*{3ex}",
                ""
            )
        );
        exText.addAll(MainTest.SOLUTION_SPACE_BEGINNING);
        exText.addAll(
            List.of(
                "\\begin{center}",
                "{\\Large",
                "\\begin{tikzpicture}",
                Patterns.ARRAY_STYLE,
                Patterns.singleEmptyNode(nodeNumber++, contentLength)
            )
        );
        for (int i = 1; i < 11; i++) {
            exText.add(Patterns.rightEmptyNodeToPredecessor(nodeNumber++, contentLength));
        }
        exText.addAll(
            List.of(
                "\\end{tikzpicture}",
                "}",
                "\\end{center}"
            )
        );
        exText.addAll(MainTest.SOLUTION_SPACE_END);
        this.harness(
            new String[] {
                "-a", Algorithm.HASH_MULT_QUAD.name,
                "-x", Main.EMBEDDED_EXAM,
                "-i", "11,0.7,7,3\n3,5,1,4,2,1",
                "-p", "solutionSpace"
            },
            MainTest.simpleComparison(
                exText,
                List.of(
                    "\\begin{center}",
                    "m = 11, c = 0,70, $c_1$ = 7, $c_2$ = 3:\\\\[2ex]",
                    "{\\Large",
                    "\\begin{tikzpicture}",
                    Patterns.ARRAY_STYLE,
                    Patterns.singleEmptyNode(nodeNumber++, contentLength),
                    Patterns.rightNodeToPredecessor(nodeNumber++, "3"),
                    Patterns.rightEmptyNodeToPredecessor(nodeNumber++, contentLength),
                    Patterns.rightEmptyNodeToPredecessor(nodeNumber++, contentLength),
                    Patterns.rightNodeToPredecessor(nodeNumber++, "2"),
                    Patterns.rightNodeToPredecessor(nodeNumber++, "5"),
                    Patterns.rightNodeToPredecessor(nodeNumber++, "1"),
                    Patterns.rightNodeToPredecessor(nodeNumber++, "1"),
                    Patterns.rightNodeToPredecessor(nodeNumber++, "4"),
                    Patterns.rightEmptyNodeToPredecessor(nodeNumber++, contentLength),
                    Patterns.rightEmptyNodeToPredecessor(nodeNumber++, contentLength),
                    "\\end{tikzpicture}",
                    "}",
                    "\\end{center}"
                )
            )
        );
    }

    @Test
    public void insertionsort() throws IOException {
        final int contentLength = 1;
        int nodeNumber = 0;
        final List<String> exText = new LinkedList<String>();
        final List<String> solText = new LinkedList<String>();
        exText.add("Sortieren Sie das folgende Array mithilfe von Insertionsort.");
        exText.add("Geben Sie dazu das Array nach jeder Iteration der \\\"au\\ss{}eren Schleife an.\\\\[2ex]");
        exText.addAll(MainTest.SOLUTION_SPACE_BEGINNING);
        exText.addAll(
            List.of(
                "\\begin{tikzpicture}",
                Patterns.ARRAY_STYLE,
                Patterns.singleNode(nodeNumber++, "3", contentLength),
                Patterns.rightNodeToPredecessor(nodeNumber++, "5"),
                Patterns.rightNodeToPredecessor(nodeNumber++, "1"),
                Patterns.rightNodeToPredecessor(nodeNumber++, "4"),
                Patterns.rightNodeToPredecessor(nodeNumber++, "2")
            )
        );
        int belowOf = 0;
        for (int i = 0; i < 4; i++) {
            exText.add(Patterns.belowEmptyNode(nodeNumber++, belowOf, contentLength));
            belowOf = nodeNumber - 1;
            for (int j = 0; j < 4; j++) {
                exText.add(Patterns.rightEmptyNodeToPredecessor(nodeNumber++, contentLength));
            }
        }
        exText.add("\\end{tikzpicture}");
        exText.addAll(MainTest.SOLUTION_SPACE_END);

        solText.add("\\begin{tikzpicture}");
        solText.add(Patterns.ARRAY_STYLE);
        belowOf = nodeNumber;
        solText.add(Patterns.singleNode(nodeNumber++, "3", contentLength));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "5"));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "1"));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "4"));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "2"));
        solText.add(Patterns.belowNode(nodeNumber++, belowOf, 3, contentLength));
        belowOf = nodeNumber - 1;
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "5"));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "1"));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "4"));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "2"));
        solText.add(Patterns.belowNode(nodeNumber++, belowOf, 1, contentLength));
        belowOf = nodeNumber - 1;
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "3"));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "5"));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "4"));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "2"));
        solText.add(Patterns.belowNode(nodeNumber++, belowOf, 1, contentLength));
        belowOf = nodeNumber - 1;
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "3"));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "4"));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "5"));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "2"));
        solText.add(Patterns.belowNode(nodeNumber++, belowOf, 1, contentLength));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "2"));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "3"));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "4"));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "5"));
        solText.add("\\end{tikzpicture}");
        this.harness(
            new String[] {
                "-a", Algorithm.INSERTIONSORT.name,
                "-x", Main.EMBEDDED_EXAM,
                "-i", "3,5,1,4,2"
            },
            MainTest.simpleComparison(exText, solText)
        );
    }

    @BeforeMethod
    public void prepare() {
        LaTeXUtils.reset();
        LabeledNode.resetIDs();
    }

    @Test
    public void quicksortStandalone() throws IOException {
        this.harness(
            new String[] {
                "-a", Algorithm.QUICKSORT.name,
                "-l", "5"
            },
            (exReader, solReader) -> {
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
        );
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
        this.harness(
            new String[] {
                "-a", Algorithm.TO_FLOAT.name,
                "-x", Main.EMBEDDED_EXAM,
                "-c", String.valueOf(mantisseLength),
                "-d", String.valueOf(exponentLength),
                "-i", MainTest.toNumberInput(cases)
            },
            MainTest.toBinary(cases, Patterns.toFloat(exponentLength, mantisseLength))
        );
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
        this.harness(
            new String[] {
                "-a", Algorithm.TO_ONES_COMPLEMENT.name,
                "-x", Main.EMBEDDED_EXAM,
                "-c", String.valueOf(bitLength),
                "-i", MainTest.toNumberInput(cases)
            },
            MainTest.toBinary(cases, Patterns.toOnes(bitLength))
        );
    }

    @Test
    public void toTruthTable() throws IOException {
        final List<String> exText = new LinkedList<String>();
        final List<String> solText = new LinkedList<String>();
        exText.add(
            "Geben Sie die jeweiligen Wahrheitstabellen zu den folgenden aussagenlogischen Formeln an:\\\\"
        );
        exText.addAll(MainTest.SOLUTION_SPACE_BEGINNING);
        exText.addAll(
            List.of(
                "\\[((\\var{A} \\wedge \\var{B}) \\vee (\\neg\\var{A} \\wedge \\var{C}))\\]",
                "\\begin{center}",
                "{\\Large",
                "\\begin{tabular}{|*{3}{C{1em}|}C{4em}|}",
                "\\hline",
                "\\var{A} & \\var{B} & \\var{C} & \\textit{Formel}\\\\\\hline"
            )
        );
        for (int i = 0; i < 8; i++) {
            exText.add(" &  &  & \\\\\\hline");
        }
        exText.addAll(
            List.of(
                "\\end{tabular}",
                "}",
                "\\end{center}"
            )
        );
        exText.addAll(MainTest.MIDDLE_SPACE);
        exText.addAll(
            List.of(
                "\\[(((\\var{D} \\wedge ((\\var{A} \\wedge \\neg\\var{B}) \\vee (\\neg\\var{A} \\wedge \\var{B}))) \\vee (\\neg\\var{D} \\wedge ((\\var{A} \\wedge \\var{B}) \\vee (\\neg\\var{A} \\wedge \\neg\\var{B})))) \\wedge ((\\var{C} \\wedge \\var{A} \\wedge \\var{B}) \\vee (\\neg\\var{C} \\wedge (\\neg\\var{A} \\vee \\neg\\var{B}))))\\]",
                "\\begin{center}",
                "{\\Large",
                "\\begin{tabular}{|*{4}{C{1em}|}C{4em}|}",
                "\\hline",
                "\\var{A} & \\var{B} & \\var{C} & \\var{D} & \\textit{Formel}\\\\\\hline"
            )
        );
        for (int i = 0; i < 16; i++) {
            exText.add(" &  &  &  & \\\\\\hline");
        }
        exText.addAll(
            List.of(
                "\\end{tabular}",
                "}",
                "\\end{center}"
            )
        );
        exText.addAll(MainTest.SOLUTION_SPACE_END);

        solText.addAll(
            List.of(
                "\\[((\\var{A} \\wedge \\var{B}) \\vee (\\neg\\var{A} \\wedge \\var{C}))\\]",
                "\\begin{center}",
                "{\\Large",
                "\\begin{tabular}{|*{3}{C{1em}|}C{4em}|}",
                "\\hline",
                "\\var{A} & \\var{B} & \\var{C} & \\textit{Formel}\\\\\\hline",
                "\\code{0} & \\code{0} & \\code{0} & \\code{0}\\\\\\hline",
                "\\code{0} & \\code{0} & \\code{1} & \\code{1}\\\\\\hline",
                "\\code{0} & \\code{1} & \\code{0} & \\code{0}\\\\\\hline",
                "\\code{0} & \\code{1} & \\code{1} & \\code{1}\\\\\\hline",
                "\\code{1} & \\code{0} & \\code{0} & \\code{0}\\\\\\hline",
                "\\code{1} & \\code{0} & \\code{1} & \\code{0}\\\\\\hline",
                "\\code{1} & \\code{1} & \\code{0} & \\code{1}\\\\\\hline",
                "\\code{1} & \\code{1} & \\code{1} & \\code{1}\\\\\\hline",
                "\\end{tabular}",
                "}",
                "\\end{center}"
            )
        );
        solText.addAll(MainTest.MIDDLE_SPACE);
        solText.addAll(
            List.of(
                "\\[(((\\var{D} \\wedge ((\\var{A} \\wedge \\neg\\var{B}) \\vee (\\neg\\var{A} \\wedge \\var{B}))) \\vee (\\neg\\var{D} \\wedge ((\\var{A} \\wedge \\var{B}) \\vee (\\neg\\var{A} \\wedge \\neg\\var{B})))) \\wedge ((\\var{C} \\wedge \\var{A} \\wedge \\var{B}) \\vee (\\neg\\var{C} \\wedge (\\neg\\var{A} \\vee \\neg\\var{B}))))\\]",
                "\\begin{center}",
                "{\\Large",
                "\\begin{tabular}{|*{4}{C{1em}|}C{4em}|}",
                "\\hline",
                "\\var{A} & \\var{B} & \\var{C} & \\var{D} & \\textit{Formel}\\\\\\hline",
                "\\code{0} & \\code{0} & \\code{0} & \\code{0} & \\code{1}\\\\\\hline",
                "\\code{0} & \\code{0} & \\code{0} & \\code{1} & \\code{0}\\\\\\hline",
                "\\code{0} & \\code{0} & \\code{1} & \\code{0} & \\code{0}\\\\\\hline",
                "\\code{0} & \\code{0} & \\code{1} & \\code{1} & \\code{0}\\\\\\hline",
                "\\code{0} & \\code{1} & \\code{0} & \\code{0} & \\code{0}\\\\\\hline",
                "\\code{0} & \\code{1} & \\code{0} & \\code{1} & \\code{1}\\\\\\hline",
                "\\code{0} & \\code{1} & \\code{1} & \\code{0} & \\code{0}\\\\\\hline",
                "\\code{0} & \\code{1} & \\code{1} & \\code{1} & \\code{0}\\\\\\hline",
                "\\code{1} & \\code{0} & \\code{0} & \\code{0} & \\code{0}\\\\\\hline",
                "\\code{1} & \\code{0} & \\code{0} & \\code{1} & \\code{1}\\\\\\hline",
                "\\code{1} & \\code{0} & \\code{1} & \\code{0} & \\code{0}\\\\\\hline",
                "\\code{1} & \\code{0} & \\code{1} & \\code{1} & \\code{0}\\\\\\hline",
                "\\code{1} & \\code{1} & \\code{0} & \\code{0} & \\code{0}\\\\\\hline",
                "\\code{1} & \\code{1} & \\code{0} & \\code{1} & \\code{0}\\\\\\hline",
                "\\code{1} & \\code{1} & \\code{1} & \\code{0} & \\code{1}\\\\\\hline",
                "\\code{1} & \\code{1} & \\code{1} & \\code{1} & \\code{0}\\\\\\hline",
                "\\end{tabular}",
                "}",
                "\\end{center}"
            )
        );
        this.harness(
            new String[] {
                "-a", Algorithm.TO_TRUTH_TABLE.name,
                "-x", Main.EMBEDDED_EXAM,
                "-i", "A && B || !A && C\n((D && ((A && !B) || (!A && B))) || (!D && ((A && B) || (!A && !B)))) && ((C && A && B) || (!C && (!A || !B)))"
            },
            MainTest.simpleComparison(exText, solText)
        );
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
        this.harness(
            new String[] {
                "-a", Algorithm.TO_TWOS_COMPLEMENT.name,
                "-x", Main.EMBEDDED_EXAM,
                "-c", String.valueOf(bitLength),
                "-i", MainTest.toNumberInput(cases)
            },
            MainTest.toBinary(cases, Patterns.toTwos(bitLength))
        );
    }

    @Test
    public void vigenereDecode() throws IOException {
        this.harness(
            new String[] {
                "-a", Algorithm.FROM_VIGENERE.name,
                "-x", Main.EMBEDDED_EXAM,
                "-i", "URKSAK\nSAKRAL\nAKLRSU"
            },
            MainTest.simpleComparison(
                List.of(
                    "Entschl\\\"usseln Sie den Text",
                    "\\begin{center}",
                    "\\code{URKSAK}",
                    "\\end{center}",
                    "unter Benutzung des Schl\\\"usselworts",
                    "\\begin{center}",
                    "\\code{SAKRAL}",
                    "\\end{center}",
                    "auf dem Alphabet",
                    "\\begin{center}",
                    "\\begin{tabular}{|*{6}{C{1.5em}|}}",
                    "\\hline",
                    "0 & 1 & 2 & 3 & 4 & 5\\\\\\hline",
                    "A & K & L & R & S & U\\\\\\hline",
                    "\\end{tabular}",
                    "\\end{center}",
                    "mithilfe der Vigen\\'ere-Verschl\\\"usselung."
                ),
                List.of("\\code{KRAKAU}")
            )
        );
    }

    @Test
    public void vigenereEncode() throws IOException {
        this.harness(
            new String[]{
                "-a", Algorithm.TO_VIGENERE.name,
                "-x", Main.EMBEDDED_EXAM,
                "-i", "KLAUSUR\nSAKRAL\nAKLRSU"
            },
            MainTest.simpleComparison(
                List.of(
                    "Verschl\\\"usseln Sie den Text",
                    "\\begin{center}",
                    "\\code{KLAUSUR}",
                    "\\end{center}",
                    "unter Benutzung des Schl\\\"usselworts",
                    "\\begin{center}",
                    "\\code{SAKRAL}",
                    "\\end{center}",
                    "auf dem Alphabet",
                    "\\begin{center}",
                    "\\begin{tabular}{|*{6}{C{1.5em}|}}",
                    "\\hline",
                    "0 & 1 & 2 & 3 & 4 & 5\\\\\\hline",
                    "A & K & L & R & S & U\\\\\\hline",
                    "\\end{tabular}",
                    "\\end{center}",
                    "mithilfe der Vigen\\'ere-Verschl\\\"usselung."
                ),
                List.of("\\code{ULKLSKK}")
            )
        );
    }

    private File createTmpFile(final String prefix, final String suffix) throws IOException {
        final File result = File.createTempFile(prefix, suffix);
        this.tmpFiles.add(result);
        return result;
    }

    private void harness(final String[] args, final CheckedBiConsumer<BufferedReader, BufferedReader, IOException> test)
    throws IOException {
        final File tmpExFile = this.createTmpFile(MainTest.EX_FILE_NAME, MainTest.TEX_SUFFIX);
        final File tmpSolFile = this.createTmpFile(MainTest.SOL_FILE_NAME, MainTest.TEX_SUFFIX);
        final String[] mainArgs = new String[args.length + 4];
        System.arraycopy(args, 0, mainArgs, 0, args.length);
        mainArgs[args.length] = "-e";
        mainArgs[args.length + 1] = tmpExFile.getAbsolutePath();
        mainArgs[args.length + 2] = "-t";
        mainArgs[args.length + 3] = tmpSolFile.getAbsolutePath();
        Main.main(mainArgs);
        try (
            BufferedReader exReader = new BufferedReader(new FileReader(tmpExFile));
            BufferedReader solReader = new BufferedReader(new FileReader(tmpSolFile));
        ) {
            test.accept(exReader, solReader);
            Assert.assertEquals(exReader.readLine(), "");
            Assert.assertNull(exReader.readLine());
            Assert.assertEquals(solReader.readLine(), "");
            Assert.assertNull(solReader.readLine());
        }
    }

}
