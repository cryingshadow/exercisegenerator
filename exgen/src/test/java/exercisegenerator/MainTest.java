package exercisegenerator;

import java.io.*;
import java.util.*;
import java.util.Optional;
import java.util.stream.*;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.graphs.Vertex;
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
        private final String bitString;
        private final String value;

        private BinaryTestCase(final String value, final String bitString) {
            this.value = value;
            this.bitString = bitString;
        }
    }

    static final String EX_FILE_NAME;

    static final String SOL_FILE_NAME;

    private static final String EMPTY_NODE_MATCH;

    private static final String MATCH_MESSAGE_PATTERN;

    private static final String NODE_MATCH;

    private static final String NUMBER_MATCH;

    private static final String PHANTOM_MATCH;

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
        exText.addAll(Patterns.SOLUTION_SPACE_BEGINNING);
        boolean first = true;
        for (final BinaryTestCase test : cases) {
            if (first) {
                first = false;
            } else {
                exText.addAll(Patterns.MIDDLE_SPACE);
                solText.addAll(Patterns.MIDDLE_SPACE);
            }
            currentNodeNumber =
                algorithm.apply(new BinaryInput(currentNodeNumber, test, exText, solText));
        }
        exText.addAll(Patterns.SOLUTION_SPACE_END);
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
        final List<String> preamble =
            List.of(
                "\\documentclass{article}",
                "",
                "\\usepackage[ngerman]{babel}",
                "\\usepackage[T1]{fontenc}",
                "\\usepackage[table]{xcolor}",
                "\\usepackage[a4paper,margin=2cm]{geometry}",
                "\\usepackage{tikz}",
                "\\usetikzlibrary{arrows,shapes.misc,shapes.arrows,shapes.multipart,shapes.geometric,chains,matrix,positioning,scopes,decorations.pathmorphing,decorations.pathreplacing,shadows,calc,trees,backgrounds}",
                "\\usepackage{tikz-qtree}",
                "\\usepackage{calc}",
                "\\usepackage{array}",
                "\\usepackage{amsmath}",
                "\\usepackage{enumitem}",
                "\\usepackage{seqsplit}",
                "\\usepackage{multicol}",
                "\\usepackage[output-decimal-marker={,}]{siunitx}",
                "",
                "\\newcolumntype{C}[1]{>{\\centering\\let\\newline\\\\\\arraybackslash\\hspace{0pt}}m{#1}}",
                "",
                "\\setlength{\\parindent}{0pt}",
                "",
                "\\newcommand{\\code}[1]{\\textnormal{\\texttt{#1}}}",
                "\\newcommand{\\codeseq}[1]{{\\ttfamily\\seqsplit{#1}}}",
                "\\newcommand{\\emphasize}[1]{\\textbf{#1}}",
                "\\newcommand*{\\circled}[1]{\\tikz[baseline=(char.base)]{",
                "            \\node[shape=circle,draw,inner sep=2pt] (char) {#1};}}",
                "\\newcommand{\\var}[1]{\\textit{#1}}",
                "",
                "\\begin{document}",
                ""
            );
        for (final String line : preamble) {
            Assert.assertEquals(reader.readLine(), line);
        }
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
            .map(c -> Patterns.toCode(c.bitString))
            .sorted((s1, s2) -> Integer.compare(s2.length(), s1.length()))
            .findFirst()
            .get();
        MainTest.binaryTest(
            input -> MainTest.checkAssignment(
                input.currentNodeNumber,
                Patterns.toCode(input.test.bitString),
                Collections.singletonList(input.test.value),
                Arrays.stream(cases)
                    .mapToInt(
                        test -> test.value
                            .replaceAll("''", "\"")
                            .replaceAll("\\\\[^\\\\\\{]+\\{\\}", "M")
                            .replaceAll("\\\\.", "M")
                            .length()
                    ).max()
                    .getAsInt(),
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
            int i = 1;
            for (final String line : exText) {
                Assert.assertEquals(exReader.readLine(), line, String.format("error at line %d", i++));
            }
            i = 1;
            for (final String line : solText) {
                Assert.assertEquals(solReader.readLine(), line, String.format("error at line %d", i++));
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
            .map(c -> c.value)
            .sorted((s1, s2) -> Integer.compare(s2.length(), s1.length()))
            .findFirst()
            .get();
        MainTest.binaryTest(
            input -> MainTest.checkAssignment(
                input.currentNodeNumber,
                input.test.value,
                Patterns.toRightHandSide(input.test.bitString),
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
            .map(
                testCase -> testCase.bitString
                    .chars()
                    .mapToObj(c -> String.valueOf((char)c))
                    .collect(Collectors.joining())
            ).collect(Collectors.joining(";"));
    }

    private static String toValueInput(final BinaryTestCase[] cases) {
        return Arrays.stream(cases).map(c -> c.value).collect(Collectors.joining(";"));
    }

    private final List<File> tmpFiles = new LinkedList<File>();

    @Test
    public void avltree() throws IOException {
        final List<String> exText = new LinkedList<String>();
        exText.addAll(
            List.of(
                "Betrachten Sie den folgenden \\emphasize{AVL-Baum}:\\\\",
                "\\begin{minipage}[t]{7cm}",
                "\\begin{center}",
                "\\begin{tikzpicture}",
                "[every tree node/.style={circle,draw=black,thick,inner sep=5pt}, sibling distance=10pt, level distance=30pt, edge from parent/.style={draw, edge from parent path={(\\tikzparentnode) -- (\\tikzchildnode)}}]",
                "\\Tree [.5 [.3 1 4 ] [.7 6 8 ] ]",
                "\\end{tikzpicture}",
                "~\\\\*\\vspace*{1ex}",
                "\\end{center}",
                "\\end{minipage}"
            )
        );
        exText.addAll(Patterns.MIDDLE_SPACE);
        exText.addAll(
            List.of(
                "F\\\"uhren Sie beginnend mit diesem Baum die folgenden Operationen aus und geben Sie die entstehenden B\\\"aume nach jeder \\emphasize{Einf\\\"uge-}, \\emphasize{L\\\"osch-}, \\emphasize{Ersetzungs-} und \\emphasize{Rotations-}Operation an:\\\\[2ex]",
                "\\begin{enumerate}",
                "\\item 2 einf\\\"ugen\\\\",
                "\\item 5 l\\\"oschen\\\\",
                "\\end{enumerate}"
            )
        );
        this.harness(
            new String[] {
                "-a", Algorithm.AVLTREE.name,
                "-x", Main.EMBEDDED_EXAM,
                "-i", "3,1,5,4,7,6,8;2,~5"
            },
            MainTest.simpleComparison(
                exText,
                List.of(
                    "\\begin{minipage}[t]{7cm}",
                    "f\\\"uge 2 ein\\\\[-2ex]",
                    "\\begin{center}",
                    "\\begin{tikzpicture}",
                    "[every tree node/.style={circle,draw=black,thick,inner sep=5pt}, sibling distance=10pt, level distance=30pt, edge from parent/.style={draw, edge from parent path={(\\tikzparentnode) -- (\\tikzchildnode)}}]",
                    "\\Tree [.5 [.3 [.1 \\edge[draw=none];\\node[draw=none]{}; 2 ] 4 ] [.7 6 8 ] ]",
                    "\\end{tikzpicture}",
                    "~\\\\*\\vspace*{1ex}",
                    "\\end{center}",
                    "\\end{minipage}",
                    "\\begin{minipage}[t]{7cm}",
                    "entferne 6\\\\[-2ex]",
                    "\\begin{center}",
                    "\\begin{tikzpicture}",
                    "[every tree node/.style={circle,draw=black,thick,inner sep=5pt}, sibling distance=10pt, level distance=30pt, edge from parent/.style={draw, edge from parent path={(\\tikzparentnode) -- (\\tikzchildnode)}}]",
                    "\\Tree [.5 [.3 [.1 \\edge[draw=none];\\node[draw=none]{}; 2 ] 4 ] [.7 \\edge[draw=none];\\node[draw=none]{}; 8 ] ]",
                    "\\end{tikzpicture}",
                    "~\\\\*\\vspace*{1ex}",
                    "\\end{center}",
                    "\\end{minipage}",
                    "",
                    "~\\\\",
                    "",
                    "\\begin{minipage}[t]{7cm}",
                    "ersetze 5\\\\[-2ex]",
                    "\\begin{center}",
                    "\\begin{tikzpicture}",
                    "[every tree node/.style={circle,draw=black,thick,inner sep=5pt}, sibling distance=10pt, level distance=30pt, edge from parent/.style={draw, edge from parent path={(\\tikzparentnode) -- (\\tikzchildnode)}}]",
                    "\\Tree [.6 [.3 [.1 \\edge[draw=none];\\node[draw=none]{}; 2 ] 4 ] [.7 \\edge[draw=none];\\node[draw=none]{}; 8 ] ]",
                    "\\end{tikzpicture}",
                    "~\\\\*\\vspace*{1ex}",
                    "\\end{center}",
                    "\\end{minipage}"
                )
            )
        );
    }

    @Test
    public void bellmanFord() throws IOException {
        this.harness(
            new String[] {
                "-a", Algorithm.BELLMAN_FORD.name,
                "-x", Main.EMBEDDED_EXAM,
                "-i", "!A\n"
                    + " A , |2, B \n"
                    + "5| , | , |3\n"
                    + " C ,4| , D "
            },
            MainTest.simpleComparison(
                List.of(
                    "Betrachten Sie den folgenden Graphen:\\\\",
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
                    "\\end{center}",
                    "",
                    "F\\\"uhren Sie den \\emphasize{Bellman-Ford}-Algorithmus auf diesem Graphen mit dem \\emphasize{Startknoten A} aus.",
                    "F\\\"ullen Sie dazu die nachfolgenden Tabellen aus:\\\\[2ex]",
                    "\\ifprintanswers",
                    "",
                    "\\vspace*{-3ex}",
                    "",
                    "\\else",
                    "\\begin{center}",
                    "",
                    "\\renewcommand{\\arraystretch}{1.5}",
                    "\\begin{tabular}{|c|*{4}{C{16mm}|}}",
                    "\\hline",
                    "\\textbf{Knoten} & \\textbf{A} & \\textbf{B} & \\textbf{C} & \\textbf{D}\\\\\\hline",
                    "\\textbf{Distanz/Vorg\\\"anger} &  &  &  & \\\\\\hline",
                    "\\end{tabular}",
                    "",
                    "\\vspace*{1ex}",
                    "",
                    "\\begin{tabular}{|c|*{4}{C{16mm}|}}",
                    "\\hline",
                    "\\textbf{Knoten} & \\textbf{A} & \\textbf{B} & \\textbf{C} & \\textbf{D}\\\\\\hline",
                    "\\textbf{Distanz/Vorg\\\"anger} &  &  &  & \\\\\\hline",
                    "\\end{tabular}",
                    "",
                    "\\vspace*{1ex}",
                    "",
                    "\\begin{tabular}{|c|*{4}{C{16mm}|}}",
                    "\\hline",
                    "\\textbf{Knoten} & \\textbf{A} & \\textbf{B} & \\textbf{C} & \\textbf{D}\\\\\\hline",
                    "\\textbf{Distanz/Vorg\\\"anger} &  &  &  & \\\\\\hline",
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
                    "\\begin{tabular}{|c|*{4}{C{16mm}|}}",
                    "\\hline",
                    "\\textbf{Knoten} & \\textbf{A} & \\textbf{B} & \\textbf{C} & \\textbf{D}\\\\\\hline",
                    "\\textbf{Distanz/Vorg\\\"anger} & $0$ & $\\infty$ & $\\infty$ & $\\infty$\\\\\\hline",
                    "\\end{tabular}",
                    "",
                    "\\vspace*{1ex}",
                    "",
                    "\\begin{tabular}{|c|*{4}{C{16mm}|}}",
                    "\\hline",
                    "\\textbf{Knoten} & \\textbf{A} & \\textbf{B} & \\textbf{C} & \\textbf{D}\\\\\\hline",
                    "\\textbf{Distanz/Vorg\\\"anger} & $0$ & $12/D$ & $5/A$ & $9/C$\\\\\\hline",
                    "\\end{tabular}",
                    "",
                    "\\vspace*{1ex}",
                    "",
                    "\\begin{tabular}{|c|*{4}{C{16mm}|}}",
                    "\\hline",
                    "\\textbf{Knoten} & \\textbf{A} & \\textbf{B} & \\textbf{C} & \\textbf{D}\\\\\\hline",
                    "\\textbf{Distanz/Vorg\\\"anger} & $0$ & $12/D$ & $5/A$ & $9/C$\\\\\\hline",
                    "\\end{tabular}",
                    "\\renewcommand{\\arraystretch}{1.0}",
                    "\\end{center}"
                )
            )
        );
    }

    @Test
    public void breadthFirstSearch() throws IOException {
        this.harness(
            new String[] {
                "-a", Algorithm.BFS.name,
                "-x", Main.EMBEDDED_EXAM,
                "-i", "!A\n"
                    + " A ,1| , B , | , C \n"
                    + "1|1, | ,1| , | , |1\n"
                    + " D , |1, E ,1| , F \n"
                    + " |1, | ,1| , | ,1|1\n"
                    + " G , |1, H ,1| , I \n"
            },
            MainTest.simpleComparison(
                List.of(
                    "Betrachten Sie den folgenden Graphen:\\\\",
                    "\\begin{center}",
                    "\\begin{tikzpicture}",
                    "[scale=2.4, node/.style={circle,draw=black,thin,inner sep=5pt}, >=stealth, p/.style={->, thin, shorten <=2pt, shorten >=2pt}]",
                    "\\node[node] (n1) at (0.0,2.0) {A};",
                    "\\node[node] (n2) at (1.0,2.0) {B};",
                    "\\node[node] (n3) at (2.0,2.0) {C};",
                    "\\node[node] (n4) at (0.0,1.0) {D};",
                    "\\node[node] (n5) at (1.0,1.0) {E};",
                    "\\node[node] (n6) at (2.0,1.0) {F};",
                    "\\node[node] (n7) at (0.0,0.0) {G};",
                    "\\node[node] (n8) at (1.0,0.0) {H};",
                    "\\node[node] (n9) at (2.0,0.0) {I};",
                    "\\draw[p, bend right = 10] (n1) to node[auto, swap] {} (n2);",
                    "\\draw[p, bend right = 10] (n1) to node[auto, swap] {} (n4);",
                    "\\draw[p, bend right = 10] (n2) to node[auto, swap] {} (n5);",
                    "\\draw[p, bend right = 10] (n4) to node[auto, swap] {} (n1);",
                    "\\draw[p, bend right = 10] (n5) to node[auto, swap] {} (n4);",
                    "\\draw[p, bend right = 10] (n5) to node[auto, swap] {} (n6);",
                    "\\draw[p, bend right = 10] (n5) to node[auto, swap] {} (n8);",
                    "\\draw[p, bend right = 10] (n6) to node[auto, swap] {} (n3);",
                    "\\draw[p, bend right = 10] (n6) to node[auto, swap] {} (n9);",
                    "\\draw[p, bend right = 10] (n7) to node[auto, swap] {} (n4);",
                    "\\draw[p, bend right = 10] (n8) to node[auto, swap] {} (n7);",
                    "\\draw[p, bend right = 10] (n8) to node[auto, swap] {} (n9);",
                    "\\draw[p, bend right = 10] (n9) to node[auto, swap] {} (n6);",
                    "\\end{tikzpicture}",
                    "\\end{center}",
                    "",
                    "F\\\"uhren Sie eine \\emphasize{Breitensuche} auf diesem Graphen mit dem \\emphasize{Startknoten A} aus. Geben Sie dazu die Knoten in der Reihenfolge an, in der sie durch die Breitensuche gefunden werden. Nehmen Sie an, dass der Algorithmus die Kanten in der alphabetischen Reihenfolge ihrer Zielknoten durchl\\\"auft."
                ),
                List.of(
                    "A, B, D, E, F, H, C, I, G"
                )
            )
        );
    }

    @Test
    public void bstree() throws IOException {
        final List<String> exText = new LinkedList<String>();
        exText.addAll(
            List.of(
                "Betrachten Sie den folgenden \\emphasize{Bin\\\"ar-Suchbaum}:\\\\",
                "\\begin{minipage}[t]{7cm}",
                "\\begin{center}",
                "\\begin{tikzpicture}",
                "[every tree node/.style={circle,draw=black,thick,inner sep=5pt}, sibling distance=10pt, level distance=30pt, edge from parent/.style={draw, edge from parent path={(\\tikzparentnode) -- (\\tikzchildnode)}}]",
                "\\Tree [.3 1 [.5 4 [.7 6 8 ] ] ]",
                "\\end{tikzpicture}",
                "~\\\\*\\vspace*{1ex}",
                "\\end{center}",
                "\\end{minipage}"
            )
        );
        exText.addAll(Patterns.MIDDLE_SPACE);
        exText.addAll(
            List.of(
                "F\\\"uhren Sie beginnend mit diesem Baum die folgenden Operationen aus und geben Sie die entstehenden B\\\"aume nach jeder \\emphasize{Einf\\\"uge-}, \\emphasize{L\\\"osch-} und \\emphasize{Ersetzungs-}Operation an:\\\\[2ex]",
                "\\begin{enumerate}",
                "\\item 2 einf\\\"ugen\\\\",
                "\\item 5 l\\\"oschen\\\\",
                "\\end{enumerate}"
            )
        );
        this.harness(
            new String[] {
                "-a", Algorithm.BIN_SEARCH_TREE.name,
                "-x", Main.EMBEDDED_EXAM,
                "-i", "3,1,5,4,7,6,8;2,~5"
            },
            MainTest.simpleComparison(
                exText,
                List.of(
                    "\\begin{minipage}[t]{7cm}",
                    "f\\\"uge 2 ein\\\\[-2ex]",
                    "\\begin{center}",
                    "\\begin{tikzpicture}",
                    "[every tree node/.style={circle,draw=black,thick,inner sep=5pt}, sibling distance=10pt, level distance=30pt, edge from parent/.style={draw, edge from parent path={(\\tikzparentnode) -- (\\tikzchildnode)}}]",
                    "\\Tree [.3 [.1 \\edge[draw=none];\\node[draw=none]{}; 2 ] [.5 4 [.7 6 8 ] ] ]",
                    "\\end{tikzpicture}",
                    "~\\\\*\\vspace*{1ex}",
                    "\\end{center}",
                    "\\end{minipage}",
                    "\\begin{minipage}[t]{7cm}",
                    "entferne 6\\\\[-2ex]",
                    "\\begin{center}",
                    "\\begin{tikzpicture}",
                    "[every tree node/.style={circle,draw=black,thick,inner sep=5pt}, sibling distance=10pt, level distance=30pt, edge from parent/.style={draw, edge from parent path={(\\tikzparentnode) -- (\\tikzchildnode)}}]",
                    "\\Tree [.3 [.1 \\edge[draw=none];\\node[draw=none]{}; 2 ] [.5 4 [.7 \\edge[draw=none];\\node[draw=none]{}; 8 ] ] ]",
                    "\\end{tikzpicture}",
                    "~\\\\*\\vspace*{1ex}",
                    "\\end{center}",
                    "\\end{minipage}",
                    "",
                    "~\\\\",
                    "",
                    "\\begin{minipage}[t]{7cm}",
                    "ersetze 5\\\\[-2ex]",
                    "\\begin{center}",
                    "\\begin{tikzpicture}",
                    "[every tree node/.style={circle,draw=black,thick,inner sep=5pt}, sibling distance=10pt, level distance=30pt, edge from parent/.style={draw, edge from parent path={(\\tikzparentnode) -- (\\tikzchildnode)}}]",
                    "\\Tree [.3 [.1 \\edge[draw=none];\\node[draw=none]{}; 2 ] [.6 4 [.7 \\edge[draw=none];\\node[draw=none]{}; 8 ] ] ]",
                    "\\end{tikzpicture}",
                    "~\\\\*\\vspace*{1ex}",
                    "\\end{center}",
                    "\\end{minipage}"
                )
            )
        );
    }

    @Test
    public void btree() throws IOException {
        final List<String> exText = new LinkedList<String>();
        exText.addAll(
            List.of(
                "Betrachten Sie den folgenden \\emphasize{B-Baum} mit Grad $t = 2$:\\\\",
                "\\begin{center}",
                "\\begin{tikzpicture}",
                "[every tree node/.style={rounded corners,draw=black,thick,inner sep=5pt}, "
                    + "sibling distance=10pt, level distance=30pt, edge from parent/.style="
                    + "{draw, edge from parent path={(\\tikzparentnode) -- (\\tikzchildnode)}}]",
                "\\Tree [.{3,5} {1} {4} {6,7,8} ];",
                "\\end{tikzpicture}",
                "\\end{center}"
            )
        );
        exText.addAll(Patterns.MIDDLE_SPACE);
        exText.addAll(
            List.of(
                "F\\\"uhren Sie beginnend mit diesem Baum die folgenden Operationen aus und geben Sie die dabei jeweils entstehenden B\\\"aume nach jeder ",
                "\\begin{itemize}\\item \\emphasize{Aufteilung}\\item \\emphasize{Diebstahloperation}\\item \\emphasize{Einf\\\"ugeoperation}\\item \\emphasize{L\\\"oschoperation}\\item \\emphasize{Rotation}\\item \\emphasize{Verschmelzung}\\end{itemize}",
                "an:\\\\",
                "\\begin{enumerate}",
                "\\item 2 einf\\\"ugen\\\\",
                "\\item 5 l\\\"oschen\\\\",
                "\\end{enumerate}"
            )
        );
        this.harness(
            new String[] {
                "-a", Algorithm.BTREE.name,
                "-x", Main.EMBEDDED_EXAM,
                "-i", "3,1,5,4,7,6,8;2,~5"
            },
            MainTest.simpleComparison(
                exText,
                List.of(
                    "\\begin{minipage}{\\columnwidth}",
                    "\\vspace*{1ex}",
                    "Schritt 1:\\\\[-2ex]",
                    "\\begin{center}",
                    "f\\\"uge 2 ein\\\\[2ex]",
                    "\\begin{tikzpicture}",
                    "[every tree node/.style={rounded corners,draw=black,thick,inner sep=5pt}, "
                        + "sibling distance=10pt, level distance=30pt, edge from parent/.style="
                        + "{draw, edge from parent path={(\\tikzparentnode) -- (\\tikzchildnode)}}]",
                    "\\Tree [.{3,5} {1,2} {4} {6,7,8} ];",
                    "\\end{tikzpicture}",
                    "\\end{center}",
                    "\\end{minipage}",
                    "\\begin{minipage}{\\columnwidth}",
                    "\\vspace*{1ex}",
                    "Schritt 2:\\\\[-2ex]",
                    "\\begin{center}",
                    "stehle Nachfolger von 5\\\\[2ex]",
                    "\\begin{tikzpicture}",
                    "[every tree node/.style={rounded corners,draw=black,thick,inner sep=5pt}, "
                        + "sibling distance=10pt, level distance=30pt, edge from parent/.style="
                        + "{draw, edge from parent path={(\\tikzparentnode) -- (\\tikzchildnode)}}]",
                    "\\Tree [.{3,6} {1,2} {4} {6,7,8} ];",
                    "\\end{tikzpicture}",
                    "\\end{center}",
                    "\\end{minipage}",
                    "\\begin{minipage}{\\columnwidth}",
                    "\\vspace*{1ex}",
                    "Schritt 3:\\\\[-2ex]",
                    "\\begin{center}",
                    "entferne 6\\\\[2ex]",
                    "\\begin{tikzpicture}",
                    "[every tree node/.style={rounded corners,draw=black,thick,inner sep=5pt}, "
                        + "sibling distance=10pt, level distance=30pt, edge from parent/.style="
                        + "{draw, edge from parent path={(\\tikzparentnode) -- (\\tikzchildnode)}}]",
                    "\\Tree [.{3,6} {1,2} {4} {7,8} ];",
                    "\\end{tikzpicture}",
                    "\\end{center}",
                    "\\end{minipage}"
                )
            )
        );
    }

    @Test
    public void bucketsort() throws IOException {
        final int contentLength = 1;
        int nodeNumber = 0;
        final List<String> exText = new LinkedList<String>();
        final List<String> solText = new LinkedList<String>();
        exText.add(
            "Sortieren Sie das folgende Array mit ganzen Zahlen von 0 bis 99 mithilfe von Bucketsort mit 10 Buckets."
        );
        exText.add("Geben Sie dazu die Buckets vor deren Sortierung sowie das Ergebnisarray an.\\\\[2ex]");
        exText.addAll(Patterns.SOLUTION_SPACE_BEGINNING);
        exText.addAll(
            List.of(
                "\\begin{tikzpicture}",
                Patterns.ARRAY_STYLE,
                Patterns.singleNode(nodeNumber++, "33", contentLength),
                Patterns.rightNodeToPredecessor(nodeNumber++, "13"),
                Patterns.rightNodeToPredecessor(nodeNumber++, "55"),
                Patterns.rightNodeToPredecessor(nodeNumber++, "11"),
                Patterns.rightNodeToPredecessor(nodeNumber++, "44"),
                Patterns.rightNodeToPredecessor(nodeNumber++, "22"),
                "\\end{tikzpicture}"
            )
        );
        exText.addAll(Patterns.SOLUTION_SPACE_END);

        solText.addAll(
            List.of(
                "\\begin{tikzpicture}",
                Patterns.ARRAY_STYLE,
                Patterns.singleNode(nodeNumber++, "33", contentLength),
                Patterns.rightNodeToPredecessor(nodeNumber++, "13"),
                Patterns.rightNodeToPredecessor(nodeNumber++, "55"),
                Patterns.rightNodeToPredecessor(nodeNumber++, "11"),
                Patterns.rightNodeToPredecessor(nodeNumber++, "44"),
                Patterns.rightNodeToPredecessor(nodeNumber++, "22"),
                "\\end{tikzpicture}",
                "",
                "\\begin{tikzpicture}",
                Patterns.BORDERLESS_STYLE
            )
        );
        int belowOf = nodeNumber;
        solText.add(Patterns.singleNode(nodeNumber++, "0: ", contentLength));
        solText.add(Patterns.belowNode(nodeNumber++, belowOf++, "1: 13, 11"));
        solText.add(Patterns.belowNode(nodeNumber++, belowOf++, "2: 22"));
        solText.add(Patterns.belowNode(nodeNumber++, belowOf++, "3: 33"));
        solText.add(Patterns.belowNode(nodeNumber++, belowOf++, "4: 44"));
        solText.add(Patterns.belowNode(nodeNumber++, belowOf++, "5: 55"));
        solText.add(Patterns.belowNode(nodeNumber++, belowOf++, "6: "));
        solText.add(Patterns.belowNode(nodeNumber++, belowOf++, "7: "));
        solText.add(Patterns.belowNode(nodeNumber++, belowOf++, "8: "));
        solText.add(Patterns.belowNode(nodeNumber++, belowOf++, "9: "));
        solText.addAll(
            List.of(
                "\\end{tikzpicture}",
                "",
                "\\begin{tikzpicture}",
                Patterns.ARRAY_STYLE,
                Patterns.singleNode(nodeNumber++, "11", contentLength),
                Patterns.rightNodeToPredecessor(nodeNumber++, "13"),
                Patterns.rightNodeToPredecessor(nodeNumber++, "22"),
                Patterns.rightNodeToPredecessor(nodeNumber++, "33"),
                Patterns.rightNodeToPredecessor(nodeNumber++, "44"),
                Patterns.rightNodeToPredecessor(nodeNumber++, "55"),
                "\\end{tikzpicture}"
            )
        );
        this.harness(
            new String[] {
                "-a", Algorithm.BUCKETSORT.name,
                "-x", Main.EMBEDDED_EXAM,
                "-i", "33,13,55,11,44,22\n0;99;10"
            },
            MainTest.simpleComparison(exText, solText)
        );
    }

    @AfterMethod
    public void cleanUp() {
        for (final File file : this.tmpFiles) {
            file.delete();
        }
        this.tmpFiles.clear();
    }

    @Test
    public void countingsort() throws IOException {
        final int contentLength = 1;
        int nodeNumber = 0;
        final List<String> exText = new LinkedList<String>();
        final List<String> solText = new LinkedList<String>();
        exText.add("Sortieren Sie das folgende Array mit ganzen Zahlen von 0 bis 9 mithilfe von Countingsort.");
        exText.add("Geben Sie dazu das Z\\\"ahlarray sowie das Ergebnisarray an.\\\\[2ex]");
        exText.addAll(Patterns.SOLUTION_SPACE_BEGINNING);
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
        exText.add(Patterns.belowEmptyNode(nodeNumber++, belowOf, contentLength));
        belowOf = nodeNumber - 1;
        for (int j = 0; j < 9; j++) {
            exText.add(Patterns.rightEmptyNodeToPredecessor(nodeNumber++, contentLength));
        }
        exText.add(Patterns.belowEmptyNode(nodeNumber++, belowOf, contentLength));
        belowOf = nodeNumber - 1;
        for (int j = 0; j < 4; j++) {
            exText.add(Patterns.rightEmptyNodeToPredecessor(nodeNumber++, contentLength));
        }
        exText.add("\\end{tikzpicture}");
        exText.addAll(Patterns.SOLUTION_SPACE_END);

        solText.add("\\begin{tikzpicture}");
        solText.add(Patterns.ARRAY_STYLE);
        belowOf = nodeNumber;
        solText.add(Patterns.singleNode(nodeNumber++, "3", contentLength));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "5"));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "1"));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "4"));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "2"));
        solText.add(Patterns.belowNode(nodeNumber++, belowOf, 0, contentLength));
        belowOf = nodeNumber - 1;
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "1"));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "1"));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "1"));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "1"));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "1"));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "0"));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "0"));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "0"));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "0"));
        solText.add(Patterns.belowNode(nodeNumber++, belowOf, 1, contentLength));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "2"));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "3"));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "4"));
        solText.add(Patterns.rightNodeToPredecessor(nodeNumber++, "5"));
        solText.add("\\end{tikzpicture}");
        this.harness(
            new String[] {
                "-a", Algorithm.COUNTINGSORT.name,
                "-x", Main.EMBEDDED_EXAM,
                "-i", "3,5,1,4,2\n0;9"
            },
            MainTest.simpleComparison(exText, solText)
        );
    }

    @Test
    public void decodeHamming() throws IOException {
        this.harness(
            new String[] {
                "-a", Algorithm.FROM_HAMMING.name,
                "-x", Main.EMBEDDED_EXAM,
                "-i", "100110110101111",
            },
            MainTest.simpleComparison(
                List.of(
                    "Dekodieren Sie den folgenden \\emphasize{Hamming-Code}:\\\\[2ex]",
                    "\\codeseq{100110110101111}"
                ),
                List.of("\\codeseq{00010101111}")
            )
        );

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
                    "Erzeugen Sie den Quelltext aus dem nachfolgenden \\emphasize{Huffman-Code} mit dem angegebenen Codebuch:\\\\[2ex]",
                    "\\codeseq{01100001100001111101}",
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
    public void decodeHuffmanLongComma() throws IOException {
        this.harness(
            new String[] {
                "-a", Algorithm.FROM_HUFFMAN.name,
                "-x", Main.EMBEDDED_EXAM,
                "-i", "100111110000000110001110101000001110111101101111010010111010011100111010100001011010111110101011110101111111101101010010001000010010110",
                "-o", "' ':\"101\",',':\"01110\",'A':\"11101\",'B':\"01111\",'C':\"10000\",'E':\"1111\",'G':\"10001\",'H':\"10010\",'I':\"000\",'K':\"10011\",'L':\"11100\",'N':\"001\",'R':\"0110\",'S':\"010\",'T':\"110\""
            },
            MainTest.simpleComparison(
                List.of(
                    "Erzeugen Sie den Quelltext aus dem nachfolgenden \\emphasize{Huffman-Code} mit dem angegebenen Codebuch:\\\\[2ex]",
                    "\\codeseq{100111110000000110001110101000001110111101101111010010111010011100111010100001011010111110101011110101111111101101010010001000010010110}",
                    "",
                    "\\vspace*{1ex}",
                    "",
                    "\\textbf{Codebuch:}",
                    "\\begin{align*}",
                    "\\code{` '} &= \\code{\\textquotedbl{}101\\textquotedbl{}}\\\\",
                    "\\code{`,'} &= \\code{\\textquotedbl{}01110\\textquotedbl{}}\\\\",
                    "\\code{`A'} &= \\code{\\textquotedbl{}11101\\textquotedbl{}}\\\\",
                    "\\code{`B'} &= \\code{\\textquotedbl{}01111\\textquotedbl{}}\\\\",
                    "\\code{`C'} &= \\code{\\textquotedbl{}10000\\textquotedbl{}}\\\\",
                    "\\code{`E'} &= \\code{\\textquotedbl{}1111\\textquotedbl{}}\\\\",
                    "\\code{`G'} &= \\code{\\textquotedbl{}10001\\textquotedbl{}}\\\\",
                    "\\code{`H'} &= \\code{\\textquotedbl{}10010\\textquotedbl{}}\\\\",
                    "\\code{`I'} &= \\code{\\textquotedbl{}000\\textquotedbl{}}\\\\",
                    "\\code{`K'} &= \\code{\\textquotedbl{}10011\\textquotedbl{}}\\\\",
                    "\\code{`L'} &= \\code{\\textquotedbl{}11100\\textquotedbl{}}\\\\",
                    "\\code{`N'} &= \\code{\\textquotedbl{}001\\textquotedbl{}}\\\\",
                    "\\code{`R'} &= \\code{\\textquotedbl{}0110\\textquotedbl{}}\\\\",
                    "\\code{`S'} &= \\code{\\textquotedbl{}010\\textquotedbl{}}\\\\",
                    "\\code{`T'} &= \\code{\\textquotedbl{}110\\textquotedbl{}}\\\\",
                    "\\end{align*}",
                    "",
                    "\\vspace*{-3ex}",
                    "",
                    "\\textbf{Quelltext:}\\\\[2ex]"
                ),
                List.of("\\code{KLINGT INTERESSANT, IST ES ABER NICHT}")
            )
        );
    }

    @Test
    public void depthFirstSearch() throws IOException {
        this.harness(
            new String[] {
                "-a", Algorithm.DFS.name,
                "-x", Main.EMBEDDED_EXAM,
                "-i", "!A\n"
                    + " A ,1| , B , | , C \n"
                    + "1|1, | ,1| , | , |1\n"
                    + " D , |1, E ,1| , F \n"
                    + " |1, | ,1| , | ,1|1\n"
                    + " G , |1, H ,1| , I \n"
            },
            MainTest.simpleComparison(
                List.of(
                    "Betrachten Sie den folgenden Graphen:\\\\",
                    "\\begin{center}",
                    "\\begin{tikzpicture}",
                    "[scale=2.4, node/.style={circle,draw=black,thin,inner sep=5pt}, >=stealth, p/.style={->, thin, shorten <=2pt, shorten >=2pt}]",
                    "\\node[node] (n1) at (0.0,2.0) {A};",
                    "\\node[node] (n2) at (1.0,2.0) {B};",
                    "\\node[node] (n3) at (2.0,2.0) {C};",
                    "\\node[node] (n4) at (0.0,1.0) {D};",
                    "\\node[node] (n5) at (1.0,1.0) {E};",
                    "\\node[node] (n6) at (2.0,1.0) {F};",
                    "\\node[node] (n7) at (0.0,0.0) {G};",
                    "\\node[node] (n8) at (1.0,0.0) {H};",
                    "\\node[node] (n9) at (2.0,0.0) {I};",
                    "\\draw[p, bend right = 10] (n1) to node[auto, swap] {} (n2);",
                    "\\draw[p, bend right = 10] (n1) to node[auto, swap] {} (n4);",
                    "\\draw[p, bend right = 10] (n2) to node[auto, swap] {} (n5);",
                    "\\draw[p, bend right = 10] (n4) to node[auto, swap] {} (n1);",
                    "\\draw[p, bend right = 10] (n5) to node[auto, swap] {} (n4);",
                    "\\draw[p, bend right = 10] (n5) to node[auto, swap] {} (n6);",
                    "\\draw[p, bend right = 10] (n5) to node[auto, swap] {} (n8);",
                    "\\draw[p, bend right = 10] (n6) to node[auto, swap] {} (n3);",
                    "\\draw[p, bend right = 10] (n6) to node[auto, swap] {} (n9);",
                    "\\draw[p, bend right = 10] (n7) to node[auto, swap] {} (n4);",
                    "\\draw[p, bend right = 10] (n8) to node[auto, swap] {} (n7);",
                    "\\draw[p, bend right = 10] (n8) to node[auto, swap] {} (n9);",
                    "\\draw[p, bend right = 10] (n9) to node[auto, swap] {} (n6);",
                    "\\end{tikzpicture}",
                    "\\end{center}",
                    "",
                    "F\\\"uhren Sie eine \\emphasize{Tiefensuche} auf diesem Graphen mit dem \\emphasize{Startknoten A} aus. Geben Sie dazu die Knoten in der Reihenfolge an, in der sie durch die Tiefensuche gefunden werden. Nehmen Sie an, dass der Algorithmus die Kanten in der alphabetischen Reihenfolge ihrer Zielknoten durchl\\\"auft."
                ),
                List.of(
                    "A, B, E, D, F, C, I, H, G"
                )
            )
        );
    }

    @Test
    public void dijkstra() throws IOException {
        final CheckedBiConsumer<BufferedReader, BufferedReader, IOException> test =
            MainTest.simpleComparison(
                List.of(
                    "Betrachten Sie den folgenden Graphen:\\\\",
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
                    "\\end{center}",
                    "",
                    "F\\\"uhren Sie den \\emphasize{Dijkstra} Algorithmus auf diesem Graphen mit dem \\emphasize{Startknoten A} aus.",
                    "F\\\"ullen Sie dazu die nachfolgende Tabelle aus:\\\\[2ex]",
                    "\\ifprintanswers",
                    "",
                    "\\vspace*{-3ex}",
                    "",
                    "\\else",
                    "\\begin{center}",
                    "",
                    "\\renewcommand{\\arraystretch}{1.5}",
                    "\\begin{tabular}{|*{4}{C{16mm}|}}",
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
                    "\\begin{tabular}{|*{4}{C{16mm}|}}",
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
            );
        this.harness(
            new String[] {
                "-a", Algorithm.DIJKSTRA.name,
                "-x", Main.EMBEDDED_EXAM,
                "-p", "solutionSpace",
                "-i", " A , |2, B \n5| , | , |3\n C ,4| , D",
                "-o", "A"
            },
            test
        );
        this.prepare();
        this.harness(
            new String[] {
                "-a", Algorithm.DIJKSTRA.name,
                "-x", Main.EMBEDDED_EXAM,
                "-p", "solutionSpace",
                "-i", "!A\n"
                    + " A , |2, B \n"
                    + "5| , | , |3\n"
                    + " C ,4| , D "
            },
            test
        );
    }

    @Test
    public void encodeHamming() throws IOException {
        this.harness(
            new String[] {
                "-a", Algorithm.TO_HAMMING.name,
                "-x", Main.EMBEDDED_EXAM,
                "-i", "0101",
            },
            MainTest.simpleComparison(
                List.of(
                    "Geben Sie den \\emphasize{Hamming-Code} f\\\"ur die folgende bin\\\"are Nachricht an:\\\\[2ex]",
                    "\\codeseq{0101}"
                ),
                List.of("\\codeseq{0100101}")
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
                "Erzeugen Sie den \\emphasize{Huffman-Code} f\\\"ur das Zielalphabet $\\{0,1\\}$ und den folgenden Eingabetext:\\\\",
                "\\begin{center}",
                "GEIERMEIER",
                "\\end{center}",
                "",
                "\\vspace*{1ex}",
                "",
                "Geben Sie zus\\\"atzlich zu dem erstellten Code das erzeugte Codebuch an.\\\\[2ex]"
            )
        );
        exText.addAll(Patterns.SOLUTION_SPACE_BEGINNING);
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
        exText.addAll(Patterns.MIDDLE_SPACE);
        exText.add("\\textbf{Code:}\\\\");
        exText.addAll(Patterns.SOLUTION_SPACE_END);
        solText.addAll(Patterns.MIDDLE_SPACE);
        solText.add("\\textbf{Code:}\\\\");
        solText.add("\\code{100} \\code{11} \\code{00} \\code{11} \\code{01} \\code{101} \\code{11} \\code{00} \\code{11} \\code{01}");
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
                "Erzeugen Sie den \\emphasize{Huffman-Code} f\\\"ur das Zielalphabet $\\{0,1\\}$ und den folgenden Eingabetext:\\\\",
                "\\begin{center}",
                "\\textbackslash{}\\&\\%\\&\\textasciicircum{}\\_\\&\\%\\&\\textasciicircum{}",
                "\\end{center}",
                "",
                "\\vspace*{1ex}",
                "",
                "Geben Sie zus\\\"atzlich zu dem erstellten Code das erzeugte Codebuch an.\\\\[2ex]"
            )
        );
        exText.addAll(Patterns.SOLUTION_SPACE_BEGINNING);
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
        exText.addAll(Patterns.MIDDLE_SPACE);
        exText.add("\\textbf{Code:}\\\\");
        exText.addAll(Patterns.SOLUTION_SPACE_END);
        solText.addAll(Patterns.MIDDLE_SPACE);
        solText.add("\\textbf{Code:}\\\\");
        solText.add("\\code{100} \\code{11} \\code{00} \\code{11} \\code{01} \\code{101} \\code{11} \\code{00} \\code{11} \\code{01}");
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
    public void encodeHuffmanLong() throws IOException {
        final int longestCodeLength = 5;
        final String longestLeftHandSide = "\\code{`M'}";
        final List<String> exText = new LinkedList<String>();
        final List<String> solText = new LinkedList<String>();
        exText.addAll(
            List.of(
                "Erzeugen Sie den \\emphasize{Huffman-Code} f\\\"ur das Zielalphabet $\\{0,1\\}$ und den folgenden Eingabetext:\\\\",
                "\\begin{center}",
                "RHABARBERBARBARABARBARBARENBARTBARBIER",
                "\\end{center}",
                "",
                "\\vspace*{1ex}",
                "",
                "Geben Sie zus\\\"atzlich zu dem erstellten Code das erzeugte Codebuch an.\\\\[2ex]"
            )
        );
        exText.addAll(Patterns.SOLUTION_SPACE_BEGINNING);
        exText.add("\\textbf{Codebuch:}");
        exText.add("\\begin{multicols}{2}");
        solText.add("\\textbf{Codebuch:}");
        solText.add("\\begin{multicols}{2}");
        int currentNodeNumber =
            MainTest.checkAssignment(
                0,
                "\\code{`A'}",
                Collections.singletonList("\\code{01}"),
                longestCodeLength,
                longestLeftHandSide,
                exText,
                solText
            );
        currentNodeNumber =
            MainTest.checkAssignment(
                currentNodeNumber,
                "\\code{`B'}",
                Collections.singletonList("\\code{10}"),
                longestCodeLength,
                longestLeftHandSide,
                exText,
                solText
            );
        currentNodeNumber =
            MainTest.checkAssignment(
                currentNodeNumber,
                "\\code{`E'}",
                Collections.singletonList("\\code{000}"),
                longestCodeLength,
                longestLeftHandSide,
                exText,
                solText
            );
        currentNodeNumber =
            MainTest.checkAssignment(
                currentNodeNumber,
                "\\code{`H'}",
                Collections.singletonList("\\code{00100}"),
                longestCodeLength,
                longestLeftHandSide,
                exText,
                solText
            );
        currentNodeNumber =
            MainTest.checkAssignment(
                currentNodeNumber,
                "\\code{`I'}",
                Collections.singletonList("\\code{00101}"),
                longestCodeLength,
                longestLeftHandSide,
                exText,
                solText
            );
        currentNodeNumber =
            MainTest.checkAssignment(
                currentNodeNumber,
                "\\code{`N'}",
                Collections.singletonList("\\code{00110}"),
                longestCodeLength,
                longestLeftHandSide,
                exText,
                solText
            );
        currentNodeNumber =
            MainTest.checkAssignment(
                currentNodeNumber,
                "\\code{`R'}",
                Collections.singletonList("\\code{11}"),
                longestCodeLength,
                longestLeftHandSide,
                exText,
                solText
            );
        currentNodeNumber =
            MainTest.checkAssignment(
                currentNodeNumber,
                "\\code{`T'}",
                Collections.singletonList("\\code{00111}"),
                longestCodeLength,
                longestLeftHandSide,
                exText,
                solText
            );
        exText.add("\\end{multicols}");
        solText.add("\\end{multicols}");
        exText.addAll(Patterns.MIDDLE_SPACE);
        exText.add("\\textbf{Code:}\\\\");
        exText.addAll(Patterns.SOLUTION_SPACE_END);
        solText.addAll(Patterns.MIDDLE_SPACE);
        solText.add("\\textbf{Code:}\\\\");
        solText.add("\\code{11} \\code{00100} \\code{01} \\code{10} \\code{01} \\code{11} \\code{10} \\code{000} \\code{11} \\code{10} \\code{01} \\code{11} \\code{10} \\code{01} \\code{11} \\code{01} \\code{10} \\code{01} \\code{11} \\code{10} \\code{01} \\code{11} \\code{10} \\code{01} \\code{11} \\code{000} \\code{00110} \\code{10} \\code{01} \\code{11} \\code{00111} \\code{10} \\code{01} \\code{11} \\code{10} \\code{00101} \\code{000} \\code{11}");
        this.harness(
            new String[] {
                "-a", Algorithm.TO_HUFFMAN.name,
                "-x", Main.EMBEDDED_EXAM,
                "-i", "RHABARBERBARBARABARBARBARENBARTBARBIER",
            },
            MainTest.simpleComparison(exText, solText)
        );
    }

    @Test
    public void fromASCII() throws IOException {
        final BinaryTestCase[] cases =
            new BinaryTestCase[] {
                new BinaryTestCase("D", "01000100"),
                new BinaryTestCase("e", "01100101"),
                new BinaryTestCase("?", "00111111")
            };
        this.harness(
            new String[] {
                "-a", Algorithm.FROM_ASCII.name,
                "-x", Main.EMBEDDED_EXAM,
                "-i", MainTest.toValueInput(cases)
            },
            MainTest.toBinary(cases, Patterns.FROM_ASCII)
        );
    }

    @Test
    public void fromFloat() throws IOException {
        final int mantisseLength = 4;
        final int exponentLength = 3;
        final BinaryTestCase[] cases =
            new BinaryTestCase[] {
                new BinaryTestCase("-3,5", "11001100"),
                new BinaryTestCase("1,375", "00110110"),
                new BinaryTestCase("-7,0", "11011100")
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
                new BinaryTestCase("-3", "11111100"),
                new BinaryTestCase("5", "00000101"),
                new BinaryTestCase("-111", "10010000")
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
        exText.addAll(Patterns.SOLUTION_SPACE_BEGINNING);
        exText.addAll(firstTable);
        exText.addAll(Patterns.middleSpace("4cm"));
        exText.addAll(secondTable);
        exText.addAll(Patterns.SOLUTION_SPACE_END);

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
        solText.addAll(Patterns.MIDDLE_SPACE);
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
        exText.addAll(Patterns.SOLUTION_SPACE_BEGINNING);
        exText.addAll(table);
        exText.addAll(Patterns.SOLUTION_SPACE_END);

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
                new BinaryTestCase("-3", "11101"),
                new BinaryTestCase("1", "00001"),
                new BinaryTestCase("-13", "10011")
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
    public void gaussJordan() throws IOException {
        this.harness(
            new String[] {
                "-a", Algorithm.GAUSS_JORDAN.name,
                "-x", Main.EMBEDDED_EXAM,
                "-i", "2,1,1,5;-1,-2,4,7;4,0,-2,3"
            },
            MainTest.simpleComparison(
                List.of(
                    "Gegeben sei das folgende \\emphasize{lineare Gleichungssystem}:\\\\[2ex]",
                    "$\\begin{array}{*{7}c}",
                    "2x_{1} & + & x_{2} & + & x_{3} & = & 5\\\\",
                    "-x_{1} & - & 2x_{2} & + & 4x_{3} & = & 7\\\\",
                    "4x_{1} &  &  & - & 2x_{3} & = & 3\\\\",
                    "\\end{array}$\\\\",
                    "",
                    "\\vspace*{1ex}",
                    "",
                    "L\\\"osen Sie dieses lineare Gleichungssystem mithilfe des \\emphasize{Gau\\ss{}-Jordan-Algorithmus}."
                ),
                List.of(
                    "{\\renewcommand{\\arraystretch}{1.2}",
                    "",
                    "\\begin{enumerate}",
                    "\\item $\\left(\\begin{array}{*{3}c|c}",
                    "2 & 1 & 1 & 5\\\\",
                    "-1 & -2 & 4 & 7\\\\",
                    "4 & 0 & -2 & 3\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{3}c|c}",
                    "1 & \\frac{1}{2} & \\frac{1}{2} & \\frac{5}{2}\\\\",
                    "-1 & -2 & 4 & 7\\\\",
                    "4 & 0 & -2 & 3\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{3}c|c}",
                    "1 & \\frac{1}{2} & \\frac{1}{2} & \\frac{5}{2}\\\\",
                    "0 & -\\frac{3}{2} & \\frac{9}{2} & \\frac{19}{2}\\\\",
                    "4 & 0 & -2 & 3\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{3}c|c}",
                    "1 & \\frac{1}{2} & \\frac{1}{2} & \\frac{5}{2}\\\\",
                    "0 & -\\frac{3}{2} & \\frac{9}{2} & \\frac{19}{2}\\\\",
                    "0 & -2 & -4 & -7\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{3}c|c}",
                    "1 & \\frac{1}{2} & \\frac{1}{2} & \\frac{5}{2}\\\\",
                    "0 & 1 & -3 & -\\frac{19}{3}\\\\",
                    "0 & -2 & -4 & -7\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{3}c|c}",
                    "1 & 0 & 2 & \\frac{17}{3}\\\\",
                    "0 & 1 & -3 & -\\frac{19}{3}\\\\",
                    "0 & -2 & -4 & -7\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{3}c|c}",
                    "1 & 0 & 2 & \\frac{17}{3}\\\\",
                    "0 & 1 & -3 & -\\frac{19}{3}\\\\",
                    "0 & 0 & -10 & -\\frac{59}{3}\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{3}c|c}",
                    "1 & 0 & 2 & \\frac{17}{3}\\\\",
                    "0 & 1 & -3 & -\\frac{19}{3}\\\\",
                    "0 & 0 & 1 & \\frac{59}{30}\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{3}c|c}",
                    "1 & 0 & 0 & \\frac{26}{15}\\\\",
                    "0 & 1 & -3 & -\\frac{19}{3}\\\\",
                    "0 & 0 & 1 & \\frac{59}{30}\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{3}c|c}",
                    "1 & 0 & 0 & \\frac{26}{15}\\\\",
                    "0 & 1 & 0 & -\\frac{13}{30}\\\\",
                    "0 & 0 & 1 & \\frac{59}{30}\\\\",
                    "\\end{array}\\right)$",
                    "\\end{enumerate}",
                    "",
                    "\\renewcommand{\\arraystretch}{1}}",
                    "",
                    "\\vspace*{1ex}",
                    "",
                    "Ergebnis: $x_{1} = \\frac{26}{15}$, $x_{2} = -\\frac{13}{30}$, $x_{3} = \\frac{59}{30}$"
                )
            )
        );
    }

    @Test
    public void gaussJordanUnsolvable() throws IOException {
        this.harness(
            new String[] {
                "-a", Algorithm.GAUSS_JORDAN.name,
                "-x", Main.EMBEDDED_EXAM,
                "-i", "5,6,-8,4,-8,4;0,-8,2,0,7,9;7,-10,4,8,6,2;5,3,-10,10,1,2;4,-7,0,4,4,7;8,10,2,8,5,1"
            },
            MainTest.simpleComparison(
                List.of(
                    "Gegeben sei das folgende \\emphasize{lineare Gleichungssystem}:\\\\[2ex]",
                    "$\\begin{array}{*{11}c}",
                    "5x_{1} & + & 6x_{2} & - & 8x_{3} & + & 4x_{4} & - & 8x_{5} & = & 4\\\\",
                    " &  & -8x_{2} & + & 2x_{3} &  &  & + & 7x_{5} & = & 9\\\\",
                    "7x_{1} & - & 10x_{2} & + & 4x_{3} & + & 8x_{4} & + & 6x_{5} & = & 2\\\\",
                    "5x_{1} & + & 3x_{2} & - & 10x_{3} & + & 10x_{4} & + & x_{5} & = & 2\\\\",
                    "4x_{1} & - & 7x_{2} &  &  & + & 4x_{4} & + & 4x_{5} & = & 7\\\\",
                    "8x_{1} & + & 10x_{2} & + & 2x_{3} & + & 8x_{4} & + & 5x_{5} & = & 1\\\\",
                    "\\end{array}$\\\\",
                    "",
                    "\\vspace*{1ex}",
                    "",
                    "L\\\"osen Sie dieses lineare Gleichungssystem mithilfe des \\emphasize{Gau\\ss{}-Jordan-Algorithmus}."
                ),
                List.of(
                    "{\\renewcommand{\\arraystretch}{1.2}",
                    "",
                    "\\begin{enumerate}",
                    "\\item $\\left(\\begin{array}{*{5}c|c}",
                    "5 & 6 & -8 & 4 & -8 & 4\\\\",
                    "0 & -8 & 2 & 0 & 7 & 9\\\\",
                    "7 & -10 & 4 & 8 & 6 & 2\\\\",
                    "5 & 3 & -10 & 10 & 1 & 2\\\\",
                    "4 & -7 & 0 & 4 & 4 & 7\\\\",
                    "8 & 10 & 2 & 8 & 5 & 1\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{5}c|c}",
                    "1 & \\frac{6}{5} & -\\frac{8}{5} & \\frac{4}{5} & -\\frac{8}{5} & \\frac{4}{5}\\\\",
                    "0 & -8 & 2 & 0 & 7 & 9\\\\",
                    "7 & -10 & 4 & 8 & 6 & 2\\\\",
                    "5 & 3 & -10 & 10 & 1 & 2\\\\",
                    "4 & -7 & 0 & 4 & 4 & 7\\\\",
                    "8 & 10 & 2 & 8 & 5 & 1\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{5}c|c}",
                    "1 & \\frac{6}{5} & -\\frac{8}{5} & \\frac{4}{5} & -\\frac{8}{5} & \\frac{4}{5}\\\\",
                    "0 & -8 & 2 & 0 & 7 & 9\\\\",
                    "0 & -\\frac{92}{5} & \\frac{76}{5} & \\frac{12}{5} & \\frac{86}{5} & -\\frac{18}{5}\\\\",
                    "5 & 3 & -10 & 10 & 1 & 2\\\\",
                    "4 & -7 & 0 & 4 & 4 & 7\\\\",
                    "8 & 10 & 2 & 8 & 5 & 1\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{5}c|c}",
                    "1 & \\frac{6}{5} & -\\frac{8}{5} & \\frac{4}{5} & -\\frac{8}{5} & \\frac{4}{5}\\\\",
                    "0 & -8 & 2 & 0 & 7 & 9\\\\",
                    "0 & -\\frac{92}{5} & \\frac{76}{5} & \\frac{12}{5} & \\frac{86}{5} & -\\frac{18}{5}\\\\",
                    "0 & -3 & -2 & 6 & 9 & -2\\\\",
                    "4 & -7 & 0 & 4 & 4 & 7\\\\",
                    "8 & 10 & 2 & 8 & 5 & 1\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{5}c|c}",
                    "1 & \\frac{6}{5} & -\\frac{8}{5} & \\frac{4}{5} & -\\frac{8}{5} & \\frac{4}{5}\\\\",
                    "0 & -8 & 2 & 0 & 7 & 9\\\\",
                    "0 & -\\frac{92}{5} & \\frac{76}{5} & \\frac{12}{5} & \\frac{86}{5} & -\\frac{18}{5}\\\\",
                    "0 & -3 & -2 & 6 & 9 & -2\\\\",
                    "0 & -\\frac{59}{5} & \\frac{32}{5} & \\frac{4}{5} & \\frac{52}{5} & \\frac{19}{5}\\\\",
                    "8 & 10 & 2 & 8 & 5 & 1\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{5}c|c}",
                    "1 & \\frac{6}{5} & -\\frac{8}{5} & \\frac{4}{5} & -\\frac{8}{5} & \\frac{4}{5}\\\\",
                    "0 & -8 & 2 & 0 & 7 & 9\\\\",
                    "0 & -\\frac{92}{5} & \\frac{76}{5} & \\frac{12}{5} & \\frac{86}{5} & -\\frac{18}{5}\\\\",
                    "0 & -3 & -2 & 6 & 9 & -2\\\\",
                    "0 & -\\frac{59}{5} & \\frac{32}{5} & \\frac{4}{5} & \\frac{52}{5} & \\frac{19}{5}\\\\",
                    "0 & \\frac{2}{5} & \\frac{74}{5} & \\frac{8}{5} & \\frac{89}{5} & -\\frac{27}{5}\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{5}c|c}",
                    "1 & \\frac{6}{5} & -\\frac{8}{5} & \\frac{4}{5} & -\\frac{8}{5} & \\frac{4}{5}\\\\",
                    "0 & 1 & -\\frac{1}{4} & 0 & -\\frac{7}{8} & -\\frac{9}{8}\\\\",
                    "0 & -\\frac{92}{5} & \\frac{76}{5} & \\frac{12}{5} & \\frac{86}{5} & -\\frac{18}{5}\\\\",
                    "0 & -3 & -2 & 6 & 9 & -2\\\\",
                    "0 & -\\frac{59}{5} & \\frac{32}{5} & \\frac{4}{5} & \\frac{52}{5} & \\frac{19}{5}\\\\",
                    "0 & \\frac{2}{5} & \\frac{74}{5} & \\frac{8}{5} & \\frac{89}{5} & -\\frac{27}{5}\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{5}c|c}",
                    "1 & 0 & -\\frac{13}{10} & \\frac{4}{5} & -\\frac{11}{20} & \\frac{43}{20}\\\\",
                    "0 & 1 & -\\frac{1}{4} & 0 & -\\frac{7}{8} & -\\frac{9}{8}\\\\",
                    "0 & -\\frac{92}{5} & \\frac{76}{5} & \\frac{12}{5} & \\frac{86}{5} & -\\frac{18}{5}\\\\",
                    "0 & -3 & -2 & 6 & 9 & -2\\\\",
                    "0 & -\\frac{59}{5} & \\frac{32}{5} & \\frac{4}{5} & \\frac{52}{5} & \\frac{19}{5}\\\\",
                    "0 & \\frac{2}{5} & \\frac{74}{5} & \\frac{8}{5} & \\frac{89}{5} & -\\frac{27}{5}\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{5}c|c}",
                    "1 & 0 & -\\frac{13}{10} & \\frac{4}{5} & -\\frac{11}{20} & \\frac{43}{20}\\\\",
                    "0 & 1 & -\\frac{1}{4} & 0 & -\\frac{7}{8} & -\\frac{9}{8}\\\\",
                    "0 & 0 & \\frac{53}{5} & \\frac{12}{5} & \\frac{11}{10} & -\\frac{243}{10}\\\\",
                    "0 & -3 & -2 & 6 & 9 & -2\\\\",
                    "0 & -\\frac{59}{5} & \\frac{32}{5} & \\frac{4}{5} & \\frac{52}{5} & \\frac{19}{5}\\\\",
                    "0 & \\frac{2}{5} & \\frac{74}{5} & \\frac{8}{5} & \\frac{89}{5} & -\\frac{27}{5}\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{5}c|c}",
                    "1 & 0 & -\\frac{13}{10} & \\frac{4}{5} & -\\frac{11}{20} & \\frac{43}{20}\\\\",
                    "0 & 1 & -\\frac{1}{4} & 0 & -\\frac{7}{8} & -\\frac{9}{8}\\\\",
                    "0 & 0 & \\frac{53}{5} & \\frac{12}{5} & \\frac{11}{10} & -\\frac{243}{10}\\\\",
                    "0 & 0 & -\\frac{11}{4} & 6 & \\frac{51}{8} & -\\frac{43}{8}\\\\",
                    "0 & -\\frac{59}{5} & \\frac{32}{5} & \\frac{4}{5} & \\frac{52}{5} & \\frac{19}{5}\\\\",
                    "0 & \\frac{2}{5} & \\frac{74}{5} & \\frac{8}{5} & \\frac{89}{5} & -\\frac{27}{5}\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{5}c|c}",
                    "1 & 0 & -\\frac{13}{10} & \\frac{4}{5} & -\\frac{11}{20} & \\frac{43}{20}\\\\",
                    "0 & 1 & -\\frac{1}{4} & 0 & -\\frac{7}{8} & -\\frac{9}{8}\\\\",
                    "0 & 0 & \\frac{53}{5} & \\frac{12}{5} & \\frac{11}{10} & -\\frac{243}{10}\\\\",
                    "0 & 0 & -\\frac{11}{4} & 6 & \\frac{51}{8} & -\\frac{43}{8}\\\\",
                    "0 & 0 & \\frac{69}{20} & \\frac{4}{5} & \\frac{3}{40} & -\\frac{379}{40}\\\\",
                    "0 & \\frac{2}{5} & \\frac{74}{5} & \\frac{8}{5} & \\frac{89}{5} & -\\frac{27}{5}\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{5}c|c}",
                    "1 & 0 & -\\frac{13}{10} & \\frac{4}{5} & -\\frac{11}{20} & \\frac{43}{20}\\\\",
                    "0 & 1 & -\\frac{1}{4} & 0 & -\\frac{7}{8} & -\\frac{9}{8}\\\\",
                    "0 & 0 & \\frac{53}{5} & \\frac{12}{5} & \\frac{11}{10} & -\\frac{243}{10}\\\\",
                    "0 & 0 & -\\frac{11}{4} & 6 & \\frac{51}{8} & -\\frac{43}{8}\\\\",
                    "0 & 0 & \\frac{69}{20} & \\frac{4}{5} & \\frac{3}{40} & -\\frac{379}{40}\\\\",
                    "0 & 0 & \\frac{149}{10} & \\frac{8}{5} & \\frac{363}{20} & -\\frac{99}{20}\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{5}c|c}",
                    "1 & 0 & -\\frac{13}{10} & \\frac{4}{5} & -\\frac{11}{20} & \\frac{43}{20}\\\\",
                    "0 & 1 & -\\frac{1}{4} & 0 & -\\frac{7}{8} & -\\frac{9}{8}\\\\",
                    "0 & 0 & 1 & \\frac{12}{53} & \\frac{11}{106} & -\\frac{243}{106}\\\\",
                    "0 & 0 & -\\frac{11}{4} & 6 & \\frac{51}{8} & -\\frac{43}{8}\\\\",
                    "0 & 0 & \\frac{69}{20} & \\frac{4}{5} & \\frac{3}{40} & -\\frac{379}{40}\\\\",
                    "0 & 0 & \\frac{149}{10} & \\frac{8}{5} & \\frac{363}{20} & -\\frac{99}{20}\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{5}c|c}",
                    "1 & 0 & 0 & \\frac{58}{53} & -\\frac{22}{53} & -\\frac{44}{53}\\\\",
                    "0 & 1 & -\\frac{1}{4} & 0 & -\\frac{7}{8} & -\\frac{9}{8}\\\\",
                    "0 & 0 & 1 & \\frac{12}{53} & \\frac{11}{106} & -\\frac{243}{106}\\\\",
                    "0 & 0 & -\\frac{11}{4} & 6 & \\frac{51}{8} & -\\frac{43}{8}\\\\",
                    "0 & 0 & \\frac{69}{20} & \\frac{4}{5} & \\frac{3}{40} & -\\frac{379}{40}\\\\",
                    "0 & 0 & \\frac{149}{10} & \\frac{8}{5} & \\frac{363}{20} & -\\frac{99}{20}\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{5}c|c}",
                    "1 & 0 & 0 & \\frac{58}{53} & -\\frac{22}{53} & -\\frac{44}{53}\\\\",
                    "0 & 1 & 0 & \\frac{3}{53} & -\\frac{45}{53} & -\\frac{90}{53}\\\\",
                    "0 & 0 & 1 & \\frac{12}{53} & \\frac{11}{106} & -\\frac{243}{106}\\\\",
                    "0 & 0 & -\\frac{11}{4} & 6 & \\frac{51}{8} & -\\frac{43}{8}\\\\",
                    "0 & 0 & \\frac{69}{20} & \\frac{4}{5} & \\frac{3}{40} & -\\frac{379}{40}\\\\",
                    "0 & 0 & \\frac{149}{10} & \\frac{8}{5} & \\frac{363}{20} & -\\frac{99}{20}\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{5}c|c}",
                    "1 & 0 & 0 & \\frac{58}{53} & -\\frac{22}{53} & -\\frac{44}{53}\\\\",
                    "0 & 1 & 0 & \\frac{3}{53} & -\\frac{45}{53} & -\\frac{90}{53}\\\\",
                    "0 & 0 & 1 & \\frac{12}{53} & \\frac{11}{106} & -\\frac{243}{106}\\\\",
                    "0 & 0 & 0 & \\frac{351}{53} & \\frac{353}{53} & -\\frac{619}{53}\\\\",
                    "0 & 0 & \\frac{69}{20} & \\frac{4}{5} & \\frac{3}{40} & -\\frac{379}{40}\\\\",
                    "0 & 0 & \\frac{149}{10} & \\frac{8}{5} & \\frac{363}{20} & -\\frac{99}{20}\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{5}c|c}",
                    "1 & 0 & 0 & \\frac{58}{53} & -\\frac{22}{53} & -\\frac{44}{53}\\\\",
                    "0 & 1 & 0 & \\frac{3}{53} & -\\frac{45}{53} & -\\frac{90}{53}\\\\",
                    "0 & 0 & 1 & \\frac{12}{53} & \\frac{11}{106} & -\\frac{243}{106}\\\\",
                    "0 & 0 & 0 & \\frac{351}{53} & \\frac{353}{53} & -\\frac{619}{53}\\\\",
                    "0 & 0 & 0 & \\frac{1}{53} & -\\frac{15}{53} & -\\frac{83}{53}\\\\",
                    "0 & 0 & \\frac{149}{10} & \\frac{8}{5} & \\frac{363}{20} & -\\frac{99}{20}\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{5}c|c}",
                    "1 & 0 & 0 & \\frac{58}{53} & -\\frac{22}{53} & -\\frac{44}{53}\\\\",
                    "0 & 1 & 0 & \\frac{3}{53} & -\\frac{45}{53} & -\\frac{90}{53}\\\\",
                    "0 & 0 & 1 & \\frac{12}{53} & \\frac{11}{106} & -\\frac{243}{106}\\\\",
                    "0 & 0 & 0 & \\frac{351}{53} & \\frac{353}{53} & -\\frac{619}{53}\\\\",
                    "0 & 0 & 0 & \\frac{1}{53} & -\\frac{15}{53} & -\\frac{83}{53}\\\\",
                    "0 & 0 & 0 & -\\frac{94}{53} & \\frac{880}{53} & \\frac{1548}{53}\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{5}c|c}",
                    "1 & 0 & 0 & \\frac{58}{53} & -\\frac{22}{53} & -\\frac{44}{53}\\\\",
                    "0 & 1 & 0 & \\frac{3}{53} & -\\frac{45}{53} & -\\frac{90}{53}\\\\",
                    "0 & 0 & 1 & \\frac{12}{53} & \\frac{11}{106} & -\\frac{243}{106}\\\\",
                    "0 & 0 & 0 & 1 & \\frac{353}{351} & -\\frac{619}{351}\\\\",
                    "0 & 0 & 0 & \\frac{1}{53} & -\\frac{15}{53} & -\\frac{83}{53}\\\\",
                    "0 & 0 & 0 & -\\frac{94}{53} & \\frac{880}{53} & \\frac{1548}{53}\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{5}c|c}",
                    "1 & 0 & 0 & 0 & -\\frac{532}{351} & \\frac{386}{351}\\\\",
                    "0 & 1 & 0 & \\frac{3}{53} & -\\frac{45}{53} & -\\frac{90}{53}\\\\",
                    "0 & 0 & 1 & \\frac{12}{53} & \\frac{11}{106} & -\\frac{243}{106}\\\\",
                    "0 & 0 & 0 & 1 & \\frac{353}{351} & -\\frac{619}{351}\\\\",
                    "0 & 0 & 0 & \\frac{1}{53} & -\\frac{15}{53} & -\\frac{83}{53}\\\\",
                    "0 & 0 & 0 & -\\frac{94}{53} & \\frac{880}{53} & \\frac{1548}{53}\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{5}c|c}",
                    "1 & 0 & 0 & 0 & -\\frac{532}{351} & \\frac{386}{351}\\\\",
                    "0 & 1 & 0 & 0 & -\\frac{106}{117} & -\\frac{187}{117}\\\\",
                    "0 & 0 & 1 & \\frac{12}{53} & \\frac{11}{106} & -\\frac{243}{106}\\\\",
                    "0 & 0 & 0 & 1 & \\frac{353}{351} & -\\frac{619}{351}\\\\",
                    "0 & 0 & 0 & \\frac{1}{53} & -\\frac{15}{53} & -\\frac{83}{53}\\\\",
                    "0 & 0 & 0 & -\\frac{94}{53} & \\frac{880}{53} & \\frac{1548}{53}\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{5}c|c}",
                    "1 & 0 & 0 & 0 & -\\frac{532}{351} & \\frac{386}{351}\\\\",
                    "0 & 1 & 0 & 0 & -\\frac{106}{117} & -\\frac{187}{117}\\\\",
                    "0 & 0 & 1 & 0 & -\\frac{29}{234} & -\\frac{443}{234}\\\\",
                    "0 & 0 & 0 & 1 & \\frac{353}{351} & -\\frac{619}{351}\\\\",
                    "0 & 0 & 0 & \\frac{1}{53} & -\\frac{15}{53} & -\\frac{83}{53}\\\\",
                    "0 & 0 & 0 & -\\frac{94}{53} & \\frac{880}{53} & \\frac{1548}{53}\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{5}c|c}",
                    "1 & 0 & 0 & 0 & -\\frac{532}{351} & \\frac{386}{351}\\\\",
                    "0 & 1 & 0 & 0 & -\\frac{106}{117} & -\\frac{187}{117}\\\\",
                    "0 & 0 & 1 & 0 & -\\frac{29}{234} & -\\frac{443}{234}\\\\",
                    "0 & 0 & 0 & 1 & \\frac{353}{351} & -\\frac{619}{351}\\\\",
                    "0 & 0 & 0 & 0 & -\\frac{106}{351} & -\\frac{538}{351}\\\\",
                    "0 & 0 & 0 & -\\frac{94}{53} & \\frac{880}{53} & \\frac{1548}{53}\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{5}c|c}",
                    "1 & 0 & 0 & 0 & -\\frac{532}{351} & \\frac{386}{351}\\\\",
                    "0 & 1 & 0 & 0 & -\\frac{106}{117} & -\\frac{187}{117}\\\\",
                    "0 & 0 & 1 & 0 & -\\frac{29}{234} & -\\frac{443}{234}\\\\",
                    "0 & 0 & 0 & 1 & \\frac{353}{351} & -\\frac{619}{351}\\\\",
                    "0 & 0 & 0 & 0 & -\\frac{106}{351} & -\\frac{538}{351}\\\\",
                    "0 & 0 & 0 & 0 & \\frac{6454}{351} & \\frac{9154}{351}\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{5}c|c}",
                    "1 & 0 & 0 & 0 & -\\frac{532}{351} & \\frac{386}{351}\\\\",
                    "0 & 1 & 0 & 0 & -\\frac{106}{117} & -\\frac{187}{117}\\\\",
                    "0 & 0 & 1 & 0 & -\\frac{29}{234} & -\\frac{443}{234}\\\\",
                    "0 & 0 & 0 & 1 & \\frac{353}{351} & -\\frac{619}{351}\\\\",
                    "0 & 0 & 0 & 0 & 1 & \\frac{269}{53}\\\\",
                    "0 & 0 & 0 & 0 & \\frac{6454}{351} & \\frac{9154}{351}\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{5}c|c}",
                    "1 & 0 & 0 & 0 & 0 & \\frac{466}{53}\\\\",
                    "0 & 1 & 0 & 0 & -\\frac{106}{117} & -\\frac{187}{117}\\\\",
                    "0 & 0 & 1 & 0 & -\\frac{29}{234} & -\\frac{443}{234}\\\\",
                    "0 & 0 & 0 & 1 & \\frac{353}{351} & -\\frac{619}{351}\\\\",
                    "0 & 0 & 0 & 0 & 1 & \\frac{269}{53}\\\\",
                    "0 & 0 & 0 & 0 & \\frac{6454}{351} & \\frac{9154}{351}\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{5}c|c}",
                    "1 & 0 & 0 & 0 & 0 & \\frac{466}{53}\\\\",
                    "0 & 1 & 0 & 0 & 0 & 3\\\\",
                    "0 & 0 & 1 & 0 & -\\frac{29}{234} & -\\frac{443}{234}\\\\",
                    "0 & 0 & 0 & 1 & \\frac{353}{351} & -\\frac{619}{351}\\\\",
                    "0 & 0 & 0 & 0 & 1 & \\frac{269}{53}\\\\",
                    "0 & 0 & 0 & 0 & \\frac{6454}{351} & \\frac{9154}{351}\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{5}c|c}",
                    "1 & 0 & 0 & 0 & 0 & \\frac{466}{53}\\\\",
                    "0 & 1 & 0 & 0 & 0 & 3\\\\",
                    "0 & 0 & 1 & 0 & 0 & -\\frac{67}{53}\\\\",
                    "0 & 0 & 0 & 1 & \\frac{353}{351} & -\\frac{619}{351}\\\\",
                    "0 & 0 & 0 & 0 & 1 & \\frac{269}{53}\\\\",
                    "0 & 0 & 0 & 0 & \\frac{6454}{351} & \\frac{9154}{351}\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{5}c|c}",
                    "1 & 0 & 0 & 0 & 0 & \\frac{466}{53}\\\\",
                    "0 & 1 & 0 & 0 & 0 & 3\\\\",
                    "0 & 0 & 1 & 0 & 0 & -\\frac{67}{53}\\\\",
                    "0 & 0 & 0 & 1 & 0 & -\\frac{364}{53}\\\\",
                    "0 & 0 & 0 & 0 & 1 & \\frac{269}{53}\\\\",
                    "0 & 0 & 0 & 0 & \\frac{6454}{351} & \\frac{9154}{351}\\\\",
                    "\\end{array}\\right)$",
                    "\\item $\\left(\\begin{array}{*{5}c|c}",
                    "1 & 0 & 0 & 0 & 0 & \\frac{466}{53}\\\\",
                    "0 & 1 & 0 & 0 & 0 & 3\\\\",
                    "0 & 0 & 1 & 0 & 0 & -\\frac{67}{53}\\\\",
                    "0 & 0 & 0 & 1 & 0 & -\\frac{364}{53}\\\\",
                    "0 & 0 & 0 & 0 & 1 & \\frac{269}{53}\\\\",
                    "0 & 0 & 0 & 0 & 0 & -\\frac{3564}{53}\\\\",
                    "\\end{array}\\right)$",
                    "\\end{enumerate}",
                    "",
                    "\\renewcommand{\\arraystretch}{1}}",
                    "",
                    "\\vspace*{1ex}",
                    "",
                    "Ergebnis: unl\\\"osbar"
                )
            )
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
        exText.addAll(Patterns.SOLUTION_SPACE_BEGINNING);
        exText.addAll(
            List.of(
                "\\begin{center}",
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
                "\\end{center}"
            )
        );
        exText.addAll(Patterns.SOLUTION_SPACE_END);
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
        exText.addAll(Patterns.SOLUTION_SPACE_BEGINNING);
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
        exText.addAll(Patterns.SOLUTION_SPACE_END);

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

    @Test
    public void knapsack() throws IOException {
        this.harness(
            new String[] {
                "-a", Algorithm.KNAPSACK.name,
                "-x", Main.EMBEDDED_EXAM,
                "-p", PreprintMode.SOLUTION_SPACE.text,
                "-i", "4,3,3;3,2,2;7"
            },
            MainTest.simpleComparison(
                List.of(
                    "Gegeben sei ein Rucksack mit \\emphasize{maximaler Tragkraft} 7 sowie 3 "
                    + "\\emphasize{Gegenst\\\"ande}. ",
                    "Der $i$-te Gegenstand soll hierbei ein Gewicht von $w_i$ und einen Wert von $c_i$ haben. ",
                    "Bestimmen Sie mit Hilfe des Algorithmus zum L\\\"osen des Rucksackproblems mittels dynamischer "
                    + "Programmierung den maximalen Gesamtwert der Gegenst\\\"ande, die der Rucksack tragen kann (das "
                    + "Gesamtgewicht der mitgef\\\"uhrten Gegenst\\\"ande \\\"ubersteigt nicht die Tragkraft des "
                    + "Rucksacks). ",
                    "Die \\emphasize{Gewichte} seien dabei $w_{1} = 4$, $w_{2} = 3$ und $w_{3} = 3$. ",
                    "Die \\emphasize{Werte} seien $c_{1} = 3$, $c_{2} = 2$ und $c_{3} = 2$. ",
                    "Geben Sie zudem die vom Algorithmus bestimmte Tabelle \\texttt{C} und die mitzunehmenden "
                    + "Gegenst\\\"ande an.",
                    "",
                    "\\ifprintanswers",
                    "",
                    "\\vspace*{-3ex}",
                    "",
                    "\\else",
                    "\\begin{center}",
                    "{\\Large",
                    "\\begin{tabular}{|C{5mm}|*{8}{C{5mm}C{7mm}|}}",
                    "\\hline",
                    "${}^*$ & \\textbf{0} &  & \\textbf{1} &  & \\textbf{2} &  & \\textbf{3} &  & \\textbf{4} &  & \\textbf{5} &  & \\textbf{6} &  & \\textbf{7} & \\\\\\hline",
                    "\\textbf{0} &  &  &  &  &  &  &  &  &  &  &  &  &  &  &  & \\\\\\hline",
                    "\\textbf{1} &  &  &  &  &  &  &  &  &  &  &  &  &  &  &  & \\\\\\hline",
                    "\\textbf{2} &  &  &  &  &  &  &  &  &  &  &  &  &  &  &  & \\\\\\hline",
                    "\\textbf{3} &  &  &  &  &  &  &  &  &  &  &  &  &  &  &  & \\\\\\hline",
                    "\\end{tabular}",
                    "",
                    "}",
                    "${}^*$ Gegenstand/Kapazit\\\"at",
                    "\\end{center}",
                    "",
                    "Gegenst\\\"ande:\\\\[2ex]",
                    "Wert:\\\\[2ex]",
                    "",
                    "\\vspace*{1ex}",
                    "",
                    "\\fi",
                    ""
                ),
                List.of(
                    "Die Tabelle \\texttt{C} wird vom Algorithmus wie folgt gef\\\"ullt:",
                    "",
                    "\\begin{center}",
                    "{\\Large",
                    "\\begin{tabular}{|C{5mm}|*{8}{C{5mm}C{7mm}|}}",
                    "\\hline",
                    "${}^*$ & \\textbf{0} &  & \\textbf{1} &  & \\textbf{2} &  & \\textbf{3} &  & \\textbf{4} &  & \\textbf{5} &  & \\textbf{6} &  & \\textbf{7} & \\\\\\hline",
                    "\\textbf{0} & 0 &  & 0 &  & 0 &  & 0 &  & 0 &  & 0 &  & 0 &  & 0 & \\\\\\hline",
                    "\\textbf{1} & 0 & $\\uparrow$ & 0 & $\\leftarrow$ & 0 & $\\leftarrow$ & 0 & $\\leftarrow$ & 3 & $\\leftarrow$ & 3 &  & 3 &  & 3 & \\\\\\hline",
                    "\\textbf{2} & 0 &  & 0 &  & 0 &  & 2 &  & 3 & $\\uparrow$ & 3 & $\\leftarrow$ & 3 & $\\leftarrow$ & 5 & $\\leftarrow$\\\\\\hline",
                    "\\textbf{3} & 0 &  & 0 &  & 0 &  & 2 &  & 3 &  & 3 &  & 4 &  & 5 & $\\uparrow$\\\\\\hline",
                    "\\end{tabular}",
                    "",
                    "}",
                    "${}^*$ Gegenstand/Kapazit\\\"at",
                    "\\end{center}",
                    "",
                    "Gegenst\\\"ande: $\\{1,2\\}$\\\\[2ex]",
                    "Wert: 5\\\\[2ex]"
                )
            )
        );
    }

    @BeforeMethod
    public void prepare() {
        LaTeXUtils.reset();
        Vertex.resetIDs();
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
    public void redBlackTree() throws IOException {
        final List<String> exText = new LinkedList<String>();
        exText.addAll(
            List.of(
                "Betrachten Sie den folgenden \\emphasize{Rot-Schwarz-Baum}:\\\\",
                "\\begin{minipage}[t]{7cm}",
                "\\begin{center}",
                "\\begin{tikzpicture}",
                "[every tree node/.style={circle,draw=gray,thick,inner sep=5pt}, b/.style={rectangle,draw=black,thick,inner sep=5pt}, r/.style={circle,draw=gray,thick,inner sep=5pt}, bb/.style={rectangle,general shadow={draw=black,shadow xshift=.5ex,shadow yshift=.5ex},draw=black,fill=white,thick,inner sep=5pt}, rb/.style={circle,draw=black,dashed,thick,inner sep=5pt}, sibling distance=10pt, level distance=30pt, edge from parent/.style={draw,edge from parent path={(\\tikzparentnode) -- (\\tikzchildnode)}}]",
                "\\Tree [.\\node[b]{3}; \\node[b]{1}; [.\\node[r]{5}; \\node[b]{4}; [.\\node[b]{7}; \\node[r]{6}; \\node[r]{8}; ] ] ]",
                "\\end{tikzpicture}",
                "~\\\\*\\vspace*{1ex}",
                "\\end{center}",
                "\\end{minipage}"
            )
        );
        exText.addAll(Patterns.MIDDLE_SPACE);
        exText.addAll(
            List.of(
                "F\\\"uhren Sie beginnend mit diesem Baum die folgenden Operationen aus und geben Sie die entstehenden B\\\"aume nach jeder \\emphasize{Einf\\\"uge-}, \\emphasize{L\\\"osch-}, \\emphasize{Umf\\\"arbe-}, \\emphasize{Ersetzungs-} und \\emphasize{Rotations-}Operation an:\\\\[2ex]",
                "\\begin{enumerate}",
                "\\item 2 einf\\\"ugen\\\\",
                "\\item 5 l\\\"oschen\\\\",
                "\\end{enumerate}"
            )
        );
        this.harness(
            new String[] {
                "-a", Algorithm.RED_BLACK_TREE.name,
                "-x", Main.EMBEDDED_EXAM,
                "-i", "3,1,5,4,7,6,8;2,~5"
            },
            MainTest.simpleComparison(
                exText,
                List.of(
                    "\\begin{minipage}[t]{7cm}",
                    "f\\\"uge 2 ein\\\\[-2ex]",
                    "\\begin{center}",
                    "\\begin{tikzpicture}",
                    "[every tree node/.style={circle,draw=gray,thick,inner sep=5pt}, b/.style={rectangle,draw=black,thick,inner sep=5pt}, r/.style={circle,draw=gray,thick,inner sep=5pt}, bb/.style={rectangle,general shadow={draw=black,shadow xshift=.5ex,shadow yshift=.5ex},draw=black,fill=white,thick,inner sep=5pt}, rb/.style={circle,draw=black,dashed,thick,inner sep=5pt}, sibling distance=10pt, level distance=30pt, edge from parent/.style={draw,edge from parent path={(\\tikzparentnode) -- (\\tikzchildnode)}}]",
                    "\\Tree [.\\node[b]{3}; [.\\node[b]{1}; \\edge[draw=none];\\node[draw=none]{}; \\node[r]{2}; ] [.\\node[r]{5}; \\node[b]{4}; [.\\node[b]{7}; \\node[r]{6}; \\node[r]{8}; ] ] ]",
                    "\\end{tikzpicture}",
                    "~\\\\*\\vspace*{1ex}",
                    "\\end{center}",
                    "\\end{minipage}",
                    "\\begin{minipage}[t]{7cm}",
                    "entferne 6\\\\[-2ex]",
                    "\\begin{center}",
                    "\\begin{tikzpicture}",
                    "[every tree node/.style={circle,draw=gray,thick,inner sep=5pt}, b/.style={rectangle,draw=black,thick,inner sep=5pt}, r/.style={circle,draw=gray,thick,inner sep=5pt}, bb/.style={rectangle,general shadow={draw=black,shadow xshift=.5ex,shadow yshift=.5ex},draw=black,fill=white,thick,inner sep=5pt}, rb/.style={circle,draw=black,dashed,thick,inner sep=5pt}, sibling distance=10pt, level distance=30pt, edge from parent/.style={draw,edge from parent path={(\\tikzparentnode) -- (\\tikzchildnode)}}]",
                    "\\Tree [.\\node[b]{3}; [.\\node[b]{1}; \\edge[draw=none];\\node[draw=none]{}; \\node[r]{2}; ] [.\\node[r]{5}; \\node[b]{4}; [.\\node[b]{7}; \\edge[draw=none];\\node[draw=none]{}; \\node[r]{8}; ] ] ]",
                    "\\end{tikzpicture}",
                    "~\\\\*\\vspace*{1ex}",
                    "\\end{center}",
                    "\\end{minipage}",
                    "",
                    "~\\\\",
                    "",
                    "\\begin{minipage}[t]{7cm}",
                    "ersetze 5\\\\[-2ex]",
                    "\\begin{center}",
                    "\\begin{tikzpicture}",
                    "[every tree node/.style={circle,draw=gray,thick,inner sep=5pt}, b/.style={rectangle,draw=black,thick,inner sep=5pt}, r/.style={circle,draw=gray,thick,inner sep=5pt}, bb/.style={rectangle,general shadow={draw=black,shadow xshift=.5ex,shadow yshift=.5ex},draw=black,fill=white,thick,inner sep=5pt}, rb/.style={circle,draw=black,dashed,thick,inner sep=5pt}, sibling distance=10pt, level distance=30pt, edge from parent/.style={draw,edge from parent path={(\\tikzparentnode) -- (\\tikzchildnode)}}]",
                    "\\Tree [.\\node[b]{3}; [.\\node[b]{1}; \\edge[draw=none];\\node[draw=none]{}; \\node[r]{2}; ] [.\\node[r]{6}; \\node[b]{4}; [.\\node[b]{7}; \\edge[draw=none];\\node[draw=none]{}; \\node[r]{8}; ] ] ]",
                    "\\end{tikzpicture}",
                    "~\\\\*\\vspace*{1ex}",
                    "\\end{center}",
                    "\\end{minipage}"
                )
            )
        );
    }

    @Test
    public void simplex() throws IOException {
        this.harness(
            new String[] {
                "-a", Algorithm.SIMPLEX.name,
                "-x", Main.EMBEDDED_EXAM,
                "-p", PreprintMode.SOLUTION_SPACE.text,
                "-i", "1,1;1,0,5;0,1,7/2;2,-1,0"
            },
            MainTest.simpleComparison(
                List.of(
                    "Gegeben sei das folgende \\emphasize{lineare Programm} in Standard-Maximum-Form:\\\\",
                    "Maximiere $z(\\mathbf{x}) = x_{1} + x_{2}$\\\\",
                    "unter den folgenden Nebenbedingungen:\\\\",
                    "$\\begin{array}{*{5}c}",
                    "x_{1} &  &  & \\leq & 5\\\\",
                    " &  & x_{2} & \\leq & \\frac{7}{2}\\\\",
                    "2x_{1} & - & x_{2} & \\leq & 0\\\\",
                    "\\end{array}$\\\\",
                    "$x_{1}, x_{2} \\geq 0$\\\\[2ex]",
                    "L\\\"osen Sie dieses lineare Programm mithilfe des \\emphasize{Simplex-Algorithmus}. F\\\"ullen "
                    + "Sie dazu die nachfolgenden Simplex-Tableaus und geben Sie eine optimale Belegung f\\\"ur die "
                    + "Variablen $x_{1}, x_{2}$ an oder begr\\\"unden Sie, warum es keine solche optimale Belegung gibt.",
                    "",
                    "\\ifprintanswers",
                    "\\else",
                    "{\\renewcommand{\\arraystretch}{1.5}",
                    "",
                    "\\begin{tabular}{|*{9}{C{9mm}|}}",
                    "\\hline",
                    " & $c_j$ &  &  &  &  &  &  & \\\\\\hline",
                    "$c_i^B\\downarrow$ & $B$ & $x_{1}$ & $x_{2}$ & $x_{3}$ & $x_{4}$ & $x_{5}$ & $b_i$ & $q_i$\\\\\\hline",
                    " &  &  &  &  &  &  &  & \\\\\\hline",
                    " &  &  &  &  &  &  &  & \\\\\\hline",
                    " &  &  &  &  &  &  &  & \\\\\\hline",
                    " & $z_j$ &  &  &  &  &  &  & \\\\\\hline",
                    " & {\\scriptsize $c_j - z_j$} &  &  &  &  &  &  & \\\\\\hline",
                    "\\end{tabular}",
                    "",
                    "\\begin{tabular}{|*{9}{C{9mm}|}}",
                    "\\hline",
                    " & $c_j$ &  &  &  &  &  &  & \\\\\\hline",
                    "$c_i^B\\downarrow$ & $B$ & $x_{1}$ & $x_{2}$ & $x_{3}$ & $x_{4}$ & $x_{5}$ & $b_i$ & $q_i$\\\\\\hline",
                    " &  &  &  &  &  &  &  & \\\\\\hline",
                    " &  &  &  &  &  &  &  & \\\\\\hline",
                    " &  &  &  &  &  &  &  & \\\\\\hline",
                    " & $z_j$ &  &  &  &  &  &  & \\\\\\hline",
                    " & {\\scriptsize $c_j - z_j$} &  &  &  &  &  &  & \\\\\\hline",
                    "\\end{tabular}",
                    "",
                    "\\begin{tabular}{|*{9}{C{9mm}|}}",
                    "\\hline",
                    " & $c_j$ &  &  &  &  &  &  & \\\\\\hline",
                    "$c_i^B\\downarrow$ & $B$ & $x_{1}$ & $x_{2}$ & $x_{3}$ & $x_{4}$ & $x_{5}$ & $b_i$ & $q_i$\\\\\\hline",
                    " &  &  &  &  &  &  &  & \\\\\\hline",
                    " &  &  &  &  &  &  &  & \\\\\\hline",
                    " &  &  &  &  &  &  &  & \\\\\\hline",
                    " & $z_j$ &  &  &  &  &  &  & \\\\\\hline",
                    " & {\\scriptsize $c_j - z_j$} &  &  &  &  &  &  & \\\\\\hline",
                    "\\end{tabular}",
                    "",
                    "\\renewcommand{\\arraystretch}{1}}",
                    "",
                    "\\vspace*{1ex}",
                    "",
                    "Ergebnis:",
                    "",
                    "\\vspace*{2ex}",
                    "",
                    "\\fi",
                    ""
                ),
                List.of(
                    "{\\renewcommand{\\arraystretch}{1.5}",
                    "",
                    "\\begin{tabular}{|*{9}{C{9mm}|}}",
                    "\\hline",
                    " & $c_j$ & $1$ & $1$ & $0$ & $0$ & $0$ &  & \\\\\\hline",
                    "$c_i^B\\downarrow$ & $B$ & $x_{1}$ & $x_{2}$ & $x_{3}$ & $x_{4}$ & $x_{5}$ & $b_i$ & $q_i$\\\\\\hline",
                    "$0$ & $x_{3}$ & $1$ & $0$ & $1$ & $0$ & $0$ & $5$ & $5$\\\\\\hline",
                    "$0$ & $x_{4}$ & $0$ & $1$ & $0$ & $1$ & $0$ & $\\frac{7}{2}$ & \\\\\\hline",
                    "$0$ & $x_{5}$ & $2$ & $-1$ & $0$ & $0$ & $1$ & $0$ & $0$\\\\\\hline",
                    " & $z_j$ & $0$ & $0$ & $0$ & $0$ & $0$ & $0$ & \\\\\\hline",
                    " & {\\scriptsize $c_j - z_j$} & $1$ & $1$ & $0$ & $0$ & $0$ &  & \\\\\\hline",
                    "\\end{tabular}",
                    "",
                    "\\begin{tabular}{|*{9}{C{9mm}|}}",
                    "\\hline",
                    " & $c_j$ & $1$ & $1$ & $0$ & $0$ & $0$ &  & \\\\\\hline",
                    "$c_i^B\\downarrow$ & $B$ & $x_{1}$ & $x_{2}$ & $x_{3}$ & $x_{4}$ & $x_{5}$ & $b_i$ & $q_i$\\\\\\hline",
                    "$0$ & $x_{3}$ & $0$ & $\\frac{1}{2}$ & $1$ & $0$ & $-\\frac{1}{2}$ & $5$ & $10$\\\\\\hline",
                    "$0$ & $x_{4}$ & $0$ & $1$ & $0$ & $1$ & $0$ & $\\frac{7}{2}$ & $\\frac{7}{2}$\\\\\\hline",
                    "$1$ & $x_{1}$ & $1$ & $-\\frac{1}{2}$ & $0$ & $0$ & $\\frac{1}{2}$ & $0$ & \\\\\\hline",
                    " & $z_j$ & $1$ & $-\\frac{1}{2}$ & $0$ & $0$ & $\\frac{1}{2}$ & $0$ & \\\\\\hline",
                    " & {\\scriptsize $c_j - z_j$} & $0$ & $\\frac{3}{2}$ & $0$ & $0$ & $-\\frac{1}{2}$ &  & \\\\\\hline",
                    "\\end{tabular}",
                    "",
                    "\\begin{tabular}{|*{9}{C{9mm}|}}",
                    "\\hline",
                    " & $c_j$ & $1$ & $1$ & $0$ & $0$ & $0$ &  & \\\\\\hline",
                    "$c_i^B\\downarrow$ & $B$ & $x_{1}$ & $x_{2}$ & $x_{3}$ & $x_{4}$ & $x_{5}$ & $b_i$ & $q_i$\\\\\\hline",
                    "$0$ & $x_{3}$ & $0$ & $0$ & $1$ & $-\\frac{1}{2}$ & $-\\frac{1}{2}$ & $\\frac{13}{4}$ & \\\\\\hline",
                    "$1$ & $x_{2}$ & $0$ & $1$ & $0$ & $1$ & $0$ & $\\frac{7}{2}$ & \\\\\\hline",
                    "$1$ & $x_{1}$ & $1$ & $0$ & $0$ & $\\frac{1}{2}$ & $\\frac{1}{2}$ & $\\frac{7}{4}$ & \\\\\\hline",
                    " & $z_j$ & $1$ & $1$ & $0$ & $\\frac{3}{2}$ & $\\frac{1}{2}$ & $\\frac{21}{4}$ & \\\\\\hline",
                    " & {\\scriptsize $c_j - z_j$} & $0$ & $0$ & $0$ & $-\\frac{3}{2}$ & $-\\frac{1}{2}$ &  & \\\\\\hline",
                    "\\end{tabular}",
                    "",
                    "\\renewcommand{\\arraystretch}{1}}",
                    "",
                    "\\vspace*{1ex}",
                    "",
                    "Ergebnis: $x_{1}^* = \\frac{7}{4}$, $x_{2}^* = \\frac{7}{2}$"
                )
            )
        );
    }

    @Test
    public void toASCII() throws IOException {
        final BinaryTestCase[] cases =
            new BinaryTestCase[] {
                new BinaryTestCase("B", "01000010"),
                new BinaryTestCase("c", "01100011"),
                new BinaryTestCase("!", "00100001"),
                new BinaryTestCase("''", "00100010"),
                new BinaryTestCase("\\textvisiblespace{}", "00100000"),
                new BinaryTestCase("\\&", "00100110"),
                new BinaryTestCase("\\%", "00100101"),
                new BinaryTestCase("\\$", "00100100"),
                new BinaryTestCase("\\_", "01011111"),
                new BinaryTestCase("\\{", "01111011"),
                new BinaryTestCase("\\}", "01111101"),
                new BinaryTestCase("\\textbackslash{}", "01011100"),
                new BinaryTestCase("\\textasciicircum{}", "01011110"),
                new BinaryTestCase("\\textasciitilde{}", "01111110"),
                new BinaryTestCase("\\#", "00100011")
            };
        this.harness(
            new String[] {
                "-a", Algorithm.TO_ASCII.name,
                "-x", Main.EMBEDDED_EXAM,
                "-i", MainTest.toBitStringInput(cases)
            },
            MainTest.fromBinary(cases, Patterns.TO_ASCII)
        );
    }

    @Test
    public void toFloat() throws IOException {
        final int exponentLength = 3;
        final int mantisseLength = 4;
        final BinaryTestCase[] cases =
            new BinaryTestCase[] {
                new BinaryTestCase("-3,5", "11001100"),
                new BinaryTestCase("1,4", "00110110"),
                new BinaryTestCase("-7,18", "11011100")
            };
        this.harness(
            new String[] {
                "-a", Algorithm.TO_FLOAT.name,
                "-x", Main.EMBEDDED_EXAM,
                "-c", String.valueOf(mantisseLength),
                "-d", String.valueOf(exponentLength),
                "-i", MainTest.toValueInput(cases)
            },
            MainTest.toBinary(cases, Patterns.toFloat(exponentLength, mantisseLength))
        );
    }

    @Test
    public void toOnesComplement() throws IOException {
        final int bitLength = 4;
        final BinaryTestCase[] cases =
            new BinaryTestCase[] {
                new BinaryTestCase("3", "0011"),
                new BinaryTestCase("-1", "1110"),
                new BinaryTestCase("-2", "1101")
            };
        this.harness(
            new String[] {
                "-a", Algorithm.TO_ONES_COMPLEMENT.name,
                "-x", Main.EMBEDDED_EXAM,
                "-c", String.valueOf(bitLength),
                "-i", MainTest.toValueInput(cases)
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
        exText.addAll(Patterns.SOLUTION_SPACE_BEGINNING);
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
        exText.addAll(Patterns.MIDDLE_SPACE);
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
        exText.addAll(Patterns.SOLUTION_SPACE_END);

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
        solText.addAll(Patterns.MIDDLE_SPACE);
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
                new BinaryTestCase("-3", "111101"),
                new BinaryTestCase("1", "000001"),
                new BinaryTestCase("-29", "100011")
            };
        this.harness(
            new String[] {
                "-a", Algorithm.TO_TWOS_COMPLEMENT.name,
                "-x", Main.EMBEDDED_EXAM,
                "-c", String.valueOf(bitLength),
                "-i", MainTest.toValueInput(cases)
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
