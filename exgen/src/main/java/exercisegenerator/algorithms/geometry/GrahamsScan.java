package exercisegenerator.algorithms.geometry;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import org.apache.commons.math3.fraction.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.algorithms.algebra.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.geometry.*;

public class GrahamsScan implements AlgorithmImplementation<List<Point>, List<List<Point>>> {

    public static final GrahamsScan INSTANCE = new GrahamsScan();

    private static BigFraction computeDeterminant(final Point a, final Point b, final Point c) {
        return b.x().subtract(a.x()).multiply(c.y().subtract(a.y())).subtract(
            c.x().subtract(a.x()).multiply(b.y().subtract(a.y()))
        );
    }

    private static double distance(final Point a, final Point b) {
        final BigFraction x = a.x().subtract(b.x());
        final BigFraction y = a.y().subtract(b.y());
        return Math.sqrt(x.multiply(x).add(y.multiply(y)).doubleValue());
    }

    private static int orientation(final Point a, final Point b, final Point c) {
        return GrahamsScan.computeDeterminant(a, b, c).compareTo(BigFraction.ZERO);
    }

    private static int parseOrGenerateNumberOfPoints(final Parameters<Flag> options) {
        return AlgorithmImplementation.parseOrGenerateLength(5, 20, options);
    }

    private static void printPoints(
        final List<Point> points,
        final Optional<Point> reference,
        final List<Point> bluePath,
        final List<Point> greenPath,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printAdjustboxBeginning(writer);
        LaTeXUtils.printTikzBeginning(TikZStyle.POINTSET, writer);
        if (!bluePath.isEmpty()) {
            writer.write("\\draw[thick, blue] ");
            writer.write(
                bluePath
                .stream()
                .map(p -> String.format(Locale.US, "(%.2f,%.2f)", p.x().doubleValue(), p.y().doubleValue()))
                .collect(Collectors.joining(" -- "))
            );
            writer.write(";");
            Main.newLine(writer);
        }
        if (!greenPath.isEmpty()) {
            writer.write("\\draw[thick, green!70!black] ");
            writer.write(
                greenPath
                .stream()
                .map(p -> String.format(Locale.US, "(%.2f,%.2f)", p.x().doubleValue(), p.y().doubleValue()))
                .collect(Collectors.joining(" -- "))
            );
            writer.write(";");
            Main.newLine(writer);
        }
        for (final Point point : points) {
            if (reference.isPresent()) {
                final Point referencePoint = reference.get();
                if (!point.equals(referencePoint)) {
                    writer.write(
                        String.format(
                            Locale.US,
                            "\\draw[thick] (%.2f,%.2f) -- (%.2f,%.2f);",
                            referencePoint.x().doubleValue(),
                            referencePoint.y().doubleValue(),
                            point.x().doubleValue(),
                            point.y().doubleValue()
                        )
                    );
                    Main.newLine(writer);
                }
            }
            writer.write(
                String.format(
                    Locale.US,
                    "\\filldraw[black] (%.2f,%.2f) circle (1pt);",
                    point.x().doubleValue(),
                    point.y().doubleValue(),
                    point.toLaTeX()
                )
            );
            Main.newLine(writer);
            writer.write(
                String.format(
                    Locale.US,
                    "\\node[anchor=west] at (%.2f,%.2f) {$%s$};",
                    point.x().doubleValue(),
                    point.y().doubleValue(),
                    point.toLaTeX()
                )
            );
            Main.newLine(writer);
        }
        LaTeXUtils.printTikzEnd(writer);
        LaTeXUtils.printAdjustboxEnd(writer);
    }

    private GrahamsScan() {}

    @Override
    public List<List<Point>> apply(final List<Point> problem) {
        final List<List<Point>> result = new LinkedList<List<Point>>();
        final Point start = this.findStartCorner(problem);
        final List<Point> sortedList = new ArrayList<Point>(problem);
        sortedList.remove(start);
        Collections.sort(
            sortedList,
            new Comparator<Point>() {

                @Override
                public int compare(final Point p1, final Point p2) {
                    return -GrahamsScan.orientation(start, p1, p2);
                }

            }
        );
        for (int i = 0; i < sortedList.size() - 1; i++) {
            final Point a = sortedList.get(i);
            final Point b = sortedList.get(i + 1);
            if (GrahamsScan.orientation(start, a, b) == 0) {
                if (GrahamsScan.distance(start, a) < GrahamsScan.distance(start, b)) {
                    sortedList.remove(i);
                } else {
                    sortedList.remove(i + 1);
                }
                i--;
            }
        }
        final Stack<Point> stack = new Stack<Point>();
        stack.push(start);
        stack.push(sortedList.removeFirst());
        result.add(new LinkedList<Point>(stack.reversed()));
        for (final Point p : sortedList) {
            Point candidate = stack.pop();
            List<Point> snapshot = new LinkedList<Point>(stack);
            snapshot.add(candidate);
            snapshot.add(p);
            result.add(snapshot.reversed());
            while (stack.size() > 1 && GrahamsScan.orientation(stack.peek(), candidate, p) <= 0) {
                candidate = stack.pop();
                snapshot = new LinkedList<Point>(stack);
                snapshot.add(candidate);
                snapshot.add(p);
                result.add(snapshot.reversed());
            }
            stack.push(candidate);
            stack.push(p);
        }
        return result;
    }

    @Override
    public String commandPrefix() {
        return "Graham";
    }

    @Override
    public List<Point> generateProblem(final Parameters<Flag> options) {
        final int numberOfPoints = GrahamsScan.parseOrGenerateNumberOfPoints(options);
        final List<Point> result = new LinkedList<Point>();
        for (int i = 0; i < numberOfPoints; i++) {
            result.add(new Point(new BigFraction(Main.RANDOM.nextInt(11)), new BigFraction(Main.RANDOM.nextInt(11))));
        }
        return result;
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    @Override
    public List<List<Point>> parseProblems(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        String line = reader.readLine();
        final List<Point> result = new LinkedList<Point>();
        while (line != null) {
            if (!line.isBlank()) {
                final String[] points = line.split(";");
                for (final String point : points) {
                    final String[] coordinates = point.split(",");
                    if (coordinates.length != 2) {
                        throw new IOException("Non-2-d-point detected!");
                    }
                    result.add(
                        new Point(
                            AlgebraAlgorithms.parseRationalNumber(coordinates[0]),
                            AlgebraAlgorithms.parseRationalNumber(coordinates[1])
                        )
                    );
                }
            }
            line = reader.readLine();
        }
        return List.of(result);
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<List<Point>> problems,
        final List<List<List<Point>>> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("F\\\"uhren Sie den Graham Scan f\\\"ur jede der folgenden Listen an Punkten durch. Geben Sie ");
        writer.write("dazu jeweils den Stack (inklusive aktueller Kandidaten) nach dem Hinzuf\\\"ugen der ersten ");
        writer.write("beiden Punkte sowie nach jeder weiteren \\\"Anderung an.");
        Main.newLine(writer);
    }

    @Override
    public void printBeforeSingleProblemInstance(
        final List<Point> problem,
        final List<List<Point>> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("F\\\"uhren Sie den Graham Scan f\\\"ur die folgende Liste an Punkten durch. Geben Sie dazu den ");
        writer.write("Stack (inklusive aktueller Kandidaten) nach dem Hinzuf\\\"ugen der ersten beiden Punkte sowie ");
        writer.write("nach jeder weiteren \\\"Anderung an.");
        Main.newLine(writer);
    }

    @Override
    public void printProblemInstance(
        final List<Point> problem,
        final List<List<Point>> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        Main.newLine(writer);
        LaTeXUtils.printMinipageBeginning("0.2\\linewidth", writer);
        writer.write("Punktliste:");
        Main.newLine(writer);
        LaTeXUtils.printBeginning(LaTeXUtils.ITEMIZE, writer);
        for (final Point point : problem) {
            writer.write("\\item $");
            writer.write(point.toLaTeX());
            writer.write("$");
            Main.newLine(writer);
        }
        LaTeXUtils.printEnd(LaTeXUtils.ITEMIZE, writer);
        LaTeXUtils.printMinipageEnd(writer);
        LaTeXUtils.printMinipageBeginning("0.7\\linewidth", writer);
        GrahamsScan.printPoints(problem, Optional.empty(), List.of(), List.of(), writer);
        LaTeXUtils.printMinipageEnd(writer);
    }

    @Override
    public void printSolutionInstance(
        final List<Point> problem,
        final List<List<Point>> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        final Point start = solution.getFirst().getLast();
        final boolean verbose = options.containsKey(Flag.VARIANT) && options.getAsInt(Flag.VARIANT) == 1;
        final int[] pagebreakCounters = LaTeXUtils.parsePagebreakCountersForSolution(options);
        int steps = 0;
        int counterIndex = 0;
        if (verbose) {
            writer.write("Wir nehmen den Punkt mit der kleinsten Y-Koordinate als Startpunkt und sortieren die ");
            writer.write("Punkte gem\\\"a\\ss{} ihrer Winkel zu diesem Startpunkt. Bei Punkten mit gleichem Winkel ");
            writer.write("entfernen wir alle bis auf denjenigen mit dem größten Abstand zum Startpunkt.");
            Main.newLine(writer);
            Main.newLine(writer);
            GrahamsScan.printPoints(problem, Optional.of(start), List.of(), List.of(), writer);
            Main.newLine(writer);
            writer.write("Dann initialisieren wir den Stack mit den ersten zwei Punkten und prüfen bei jedem ");
            writer.write("weiteren Punkt, ob der Winkel zwischen den letzten drei Punkten nach links oder rechts ");
            writer.write("geht.");
            Main.newLine(writer);
            Main.newLine(writer);
        } else {
            LaTeXUtils.beginMulticols(2, Optional.empty(), writer);
        }
        LaTeXUtils.printBeginning(LaTeXUtils.ENUMERATE, writer);
        for (final List<Point> stack : solution) {
            if (counterIndex < pagebreakCounters.length && steps >= pagebreakCounters[counterIndex]) {
                writer.write("\\newpage");
                Main.newLine(writer);
                Main.newLine(writer);
                steps = 0;
                counterIndex++;
            }
            writer.write("\\item \\begin{minipage}[t]{\\columnwidth}");
            Main.newLine(writer);
            writer.write("\\begin{itemize}[label={}]");
            Main.newLine(writer);
            for (final Point point : stack) {
                writer.write("\\item $");
                writer.write(point.toLaTeX());
                writer.write("$");
                Main.newLine(writer);
            }
            LaTeXUtils.printEnd(LaTeXUtils.ITEMIZE, writer);
            LaTeXUtils.printMinipageEnd(writer);
            if (verbose && stack.size() > 2) {
                Main.newLine(writer);
                GrahamsScan.printPoints(
                    problem,
                    Optional.empty(),
                    stack.stream().skip(1).toList(),
                    stack.stream().limit(2).toList(),
                    writer
                );
            }
            steps++;
        }
        LaTeXUtils.printEnd(LaTeXUtils.ENUMERATE, writer);
        if (verbose) {
            Main.newLine(writer);
            writer.write("Schlie\\ss{}lich stellen die verbleibenden Punkte im Stack die konvexe H\\\"ulle dar.");
            Main.newLine(writer);
            Main.newLine(writer);
            GrahamsScan.printPoints(
                problem,
                Optional.empty(),
                Stream.concat(Stream.of(start), solution.getLast().stream()).toList(),
                List.of(),
                writer
            );
        } else {
            LaTeXUtils.endMulticols(writer);
        }
    }

    @Override
    public void printSolutionSpace(
        final List<Point> problem,
        final List<List<Point>> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        //do nothing
    }

    private Point findStartCorner(final List<Point> problem) {
        Point result = problem.getFirst();
        BigFraction minY = result.y();
        BigFraction minX = result.x();
        for (final Point p : problem) {
            final int compareValue = p.y().compareTo(minY);
            if (compareValue < 0 || compareValue == 0 && p.x().compareTo(minX) < 0) {
                result = p;
                minY = result.y();
                minX = result.x();
            }
        }
        return result;
    }

}
