package exercisegenerator.algorithms.geometry;

import java.io.*;
import java.util.*;

import org.apache.commons.math3.fraction.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.algorithms.algebra.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.geometry.*;

public class GrahamsScan implements AlgorithmImplementation<List<Point>, List<List<Point>>> {

    public static final GrahamsScan INSTANCE = new GrahamsScan();

    /**
     * Prints the convex hull according to the graham scan algorithm.
     * @param pointSet The set of points as input.
     * @param mode The preprint mode.
     * @param exWriter The writer for the exercise.
     * @param solWriter The writer for the solution.
     * @throws IOException If some error occurs during output.
     */
    public static void printConvexHull(
        final ArrayList<Pair<Double,Double>> pointSet,
        final PreprintMode mode,
        final Parameters options,
        final BufferedWriter exWriter,
        final BufferedWriter solWriter
    ) throws IOException {
        exWriter.write("Berechnen Sie die konvexe H\\\"ulle der folgenden Punktmenge. Benutzen ");
        exWriter.write("Sie daf\\\"ur \\emphasize{Grahams Scan} wie \\emphasize{in der Vorlesung} vorgestellt ");
        exWriter.write("und geben Sie die Teilschritte \\emphasize{nach jeder Iteration}");
        exWriter.write(" (also nach jedem neu hinzugef\\\"ugten Punkt) an. Umkreisen ");
        exWriter.write("Sie die Punkte, die vom Algorithmus in der Iterationsschleife nicht betrachtet werden.\\\\");
        Main.newLine(exWriter);
        exWriter.write("\\\\");
        Main.newLine(exWriter);
        Main.newLine(exWriter);
        // solution
        int count = GrahamsScan.computeConvexHull(pointSet, solWriter);
        switch (mode) {
            case SOLUTION_SPACE:
                LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, exWriter);
                break;
            case NEVER:
                count = 1;
                break;
            case ALWAYS:
                // do nothing
        }
        boolean one = false;
        while (count > 0) {
            GrahamsScan.printPointset(pointSet, -1 , null, exWriter);
            if (one) {
                Main.newLine(exWriter);
                exWriter.write("\\\\");
                Main.newLine(exWriter);
            }
            one = !one;
            --count;
        }
        if (mode == PreprintMode.SOLUTION_SPACE) {
            LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), options, exWriter);
        }
    }

    public static void printPartialHull(
        final ArrayList<Pair<Double,Double>> pointSet,
        final Stack<Pair<Double,Double>> hull,
        final ArrayList<Pair<Double,Double>> duplicates,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("\\resizebox{.45\\textwidth}{!}{");
        Main.newLine(writer);
        LaTeXUtils.printTikzBeginning(TikZStyle.POINTSET, writer);
        for (int i = 0; i < pointSet.size(); ++i) {
            writer.write("\\node at (" + pointSet.get(i).x + ", " + pointSet.get(i).y + ") {\\textbullet};");
            Main.newLine(writer);
        }
        if (duplicates != null) {
            for (int i = 0; i < duplicates.size(); ++i) {
                writer.write("\\node at (" + duplicates.get(i).x + ", " + duplicates.get(i).y + ") {$\\circ$};");
                Main.newLine(writer);
            }
        }
        final ArrayList<String> edges = new ArrayList<String>();
//        int index = 0;
        @SuppressWarnings("unchecked")
        final
        Stack<Pair<Double,Double>> clone = (Stack<Pair<Double,Double>>)hull.clone();
//        Pair<Double,Double> start = clone.peek();
        while (!clone.empty()) {
            final Pair<Double, Double> first = clone.pop();
            if (!clone.empty()) {
                final Pair<Double,Double> second = clone.peek();
                edges.add("\\draw (" + second.x + "," + second.y + ")--(" + first.x + "," + first.y + ");");
                writer.write("\\node at (" + first.x + ", " + first.y + ") {\\textbullet};");
                Main.newLine(writer);
            }
//            ++index;
        }
        for (int i = 0; i < edges.size(); ++i) {
            writer.write(edges.get(i));
            Main.newLine(writer);
        }
        writer.write(";");
        Main.newLine(writer);
        LaTeXUtils.printTikzEnd(writer);
        writer.write("}");
    }

    public static void printPointset(
        final ArrayList<Pair<Double,Double>> pointSet,
        final int reference,
        final ArrayList<Pair<Double,Double>> duplicates,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("\\resizebox{.45\\textwidth}{!}{");
        Main.newLine(writer);
        LaTeXUtils.printTikzBeginning(TikZStyle.POINTSET, writer);
        Main.newLine(writer);
        for (int i = 0; i < pointSet.size(); ++i) {
            if (reference >= 0) {
                writer.write("\\node at (" + pointSet.get(i).x);
                writer.write(", " + pointSet.get(i).y);
                writer.write(") {\\textbullet " + i);
                writer.write("};");
                Main.newLine(writer);
            } else {
                writer.write("\\node at (" + pointSet.get(i).x);
                writer.write(", " + pointSet.get(i).y);
                writer.write(") {\\textbullet (" + pointSet.get(i).x.intValue());
                writer.write("," + pointSet.get(i).y.intValue());
                writer.write(")};");
                Main.newLine(writer);
            }
        }
        if (reference >= 0) {
            for (int i = 0; i < pointSet.size(); ++i) {
                writer.write("\\draw (" + pointSet.get(reference).x);
                writer.write("," + pointSet.get(reference).y);
                writer.write(")--(" + pointSet.get(i).x);
                writer.write("," + pointSet.get(i).y);
                writer.write(");");
                Main.newLine(writer);
            }
        }
        if (duplicates != null) {
            for (int i = 0; i < duplicates.size(); ++i) {
                writer.write("\\node at (" + duplicates.get(i).x + ", " + duplicates.get(i).y + ") {$\\circ$};");
                Main.newLine(writer);
            }
        }
        LaTeXUtils.printTikzEnd(writer);
        Main.newLine(writer);
        writer.write("}");
    }

    private static int computeConvexHull(final ArrayList<Pair<Double,Double>> pointSet, final BufferedWriter solWriter)
    throws IOException {
        // find reference point as starting point
        int refIndex = 0;
        int count = 1;
        Pair<Double,Double> refPoint = pointSet.get(0);
        //System.out.println("Refpoint is: (" + refPoint.x + "," + refPoint.y + ")");
        for (int i = 1; i < pointSet.size(); ++i) {
            //System.out.println("Consider: (" + pointSet.get(i).x + "," + pointSet.get(i).y + ")");
            //System.out.println("First constraint: " + (pointSet.get(i).y < refPoint.y) );
//            System.out.println(
//                "Second constraint: a) "
//                + (Double.compare(pointSet.get(i).y,refPoint.y) == 0)
//                + ", b) "
//                + (pointSet.get(i).x < refPoint.x)
//            );
            if (
                    Double.compare(pointSet.get(i).y, refPoint.y) < 0
                || (
                    Double.compare(pointSet.get(i).y,refPoint.y) == 0
                    && Double.compare(pointSet.get(i).x, refPoint.x) < 0
                )
            ) {
                refPoint = pointSet.get(i);
                //System.out.println("Refpoint is: (" + refPoint.x + "," + refPoint.y + ")");
                refIndex = i;
            }
        }
        //System.out.println("Referencepoint: " + refPoint.x + ", " + refPoint.y);
        // sort Array of Points according to determinant with reference to refPoint
        final ArrayList<Pair<Double,Double>> tmp = GrahamsScan.pointAscendingSort(pointSet, refIndex);
        // find missing points
        final ArrayList<Pair<Double,Double>> duplicates = new ArrayList<Pair<Double,Double>>();
        for (int i = 0; i < pointSet.size(); ++i) {
            if (!tmp.contains(pointSet.get(i))) {
                duplicates.add(pointSet.get(i));
            }
        }
        GrahamsScan.printPointset(tmp, 0, duplicates, solWriter);
        //System.out.println("Sorted Array:");
        //int q = 0;
        //while( q < tmp.size()) {
        //    System.out.println(tmp.get(q).x + "," + tmp.get(q).y);
        //    ++q;
        //}
        boolean oneOnly = false;
        final Stack<Pair<Double,Double>> hull = new Stack<Pair<Double,Double>>();
        for (int i = 0; i < tmp.size(); ++i) {
            if (i > 1) {
                Pair<Double,Double> first = hull.pop();
                Pair<Double,Double> second = hull.peek();
                hull.push(first);
                //System.out.println("First: " + first.x +", " + first.y);
                //System.out.println("Second: " + second.x +", " + second.y);
                //System.out.println("Acutal: " + tmp.get(i).x +", " + tmp.get(i).y);
                while (!oneOnly && GrahamsScan.direction(second, first, tmp.get(i)) <= 0) {
                    //System.out.println("Pop.");
                    hull.pop();
                    first = hull.pop();
                    //System.out.println("First: " + first.x +", " + first.y);
                    if (!hull.empty()) {
                        second = hull.peek();
                        //System.out.println("Second: " + second.x +", " + second.y);
                    } else {
                        oneOnly = true;
                    }
                    hull.push(first);
                    //System.out.println("Acutal: " + tmp.get(i).x +", " + tmp.get(i).y);
                }
            }
            oneOnly = false;
            hull.push(tmp.get(i));
            if (i>=1) {
                GrahamsScan.printPartialHull(tmp,hull,duplicates,solWriter);
            }
            if (count%2 == 1) {
                Main.newLine(solWriter);
                solWriter.write("\\\\");
                Main.newLine(solWriter);
            }
            ++count;
        }
        // add last edge
        hull.push(refPoint);
        GrahamsScan.printPartialHull(tmp,hull,duplicates,solWriter);
        return count;
    }

    private static BigFraction computeDeterminant(final Point a, final Point b, final Point c) {
        return b.x().subtract(a.x()).multiply(c.y().subtract(a.y())).subtract(
            c.x().subtract(a.x()).multiply(b.y().subtract(a.y()))
        );
    }

    /**
     * Computes the direction of the bow between the points A, B and C. A value
     * larger than 0 reflects a bowing to the left while a value <= 0 reflects a
     * turn to the right.
     * @param A First point.
     * @param B Second point.
     * @param C Third point.
     * @return Turn direction
     *
     */
    private static Double direction(
        final Pair<Double, Double> A,
        final Pair<Double,Double> B,
        final Pair<Double,Double> C
    ) {
        final Pair<Double,Double> firstSegment = new Pair<Double,Double>(B.x-A.x, B.y-A.y);
        final Pair<Double,Double> secondSegment = new Pair<Double,Double>(C.x-B.x, C.y-B.y);
        return (GrahamsScan.polarAngle(firstSegment, secondSegment));
    }

    private static ArrayList<Pair<Double, Double>> generateConvexHullProblem(final Parameters options) {
        final ArrayList<Pair<Double,Double>> input = new ArrayList<Pair<Double,Double>>();
        final int numOfPoints;
        if (options.containsKey(Flag.LENGTH)) {
            numOfPoints = Integer.parseInt(options.get(Flag.LENGTH));
        } else {
            numOfPoints = Main.RANDOM.nextInt(16) + 5;
        }
        for (int i = 0; i < numOfPoints; ++i) {
            input.add(new Pair<Double,Double>((double)Main.RANDOM.nextInt(11), (double)Main.RANDOM.nextInt(11)));
        }
        return input;
    }

    private static List<Point> generatePointSet(final Parameters options) {
        final int numberOfPoints = GrahamsScan.parseOrGenerateNumberOfPoints(options);
        final List<Point> result = new LinkedList<Point>();
        for (int i = 0; i < numberOfPoints; i++) {
            result.add(new Point(new BigFraction(Main.RANDOM.nextInt(11)), new BigFraction(Main.RANDOM.nextInt(11))));
        }
        return result;
    }

    private static ArrayList<Pair<Double, Double>> parseConvexHullProblem(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        String line = null;
        final ArrayList<Pair<Double,Double>> input = new ArrayList<Pair<Double,Double>>();
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            final String[] splitted = line.split(",");
            input.add(
                new Pair<Double,Double>(
                    Double.parseDouble(splitted[0].trim()),
                    Double.parseDouble(splitted[1].trim())
                )
            );
        }
        return input;
    }

    private static ArrayList<Pair<Double, Double>> parseOrGenerateConvexHullProblem(final Parameters options)
    throws IOException {
        return new ParserAndGenerator<ArrayList<Pair<Double, Double>>>(
            GrahamsScan::parseConvexHullProblem,
            GrahamsScan::generateConvexHullProblem
        ).getResult(options);
    }

    private static int parseOrGenerateNumberOfPoints(final Parameters options) {
        if (options.containsKey(Flag.LENGTH)) {
            return Integer.parseInt(options.get(Flag.LENGTH));
        }
        return Main.RANDOM.nextInt(16) + 5;
    }

    private static List<Point> parsePointSet(final BufferedReader reader, final Parameters options) throws IOException {
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
        return result;
    }

    /**
     * Sorts the given pointset according to the reference point in ascending order.
     * @param pointSet The pointset.
     * @param referenceIndex The reference point.
     * @return a sorted pointset
     */
    private static ArrayList<Pair<Double,Double>> pointAscendingSort(
        final ArrayList<Pair<Double,Double>> pointSet,
        final int referenceIndex
    ) {
        final ArrayList<Pair<Double,Double>> result = new ArrayList<Pair<Double,Double>>();
        result.add(pointSet.get(referenceIndex));
//        System.out.println(
//            "RefIndex: "
//            + referenceIndex
//            + " Point: "
//            + pointSet.get(referenceIndex).x
//            + ","
//            + pointSet.get(referenceIndex).y
//        );
        boolean add = true;
        // X-Axis as a reference (note: no angle can be smaller than this as we select the refPoint to be with smallest
        // y-coordinate
        final Pair<Double,Double> xAxis = new Pair<Double,Double>(1.0,0.0);
        // Insertion-Sort the array
        for (int i = 0; i < pointSet.size(); ++i) {
            if (i != referenceIndex) {
                int index = 1;
                if (result.size() > 1) {
                    final Pair<Double,Double> newVector =
                        new Pair<Double,Double>(pointSet.get(i).x-result.get(0).x, pointSet.get(i).y-result.get(0).y);
                    Pair<Double,Double> currVector =
                        new Pair<Double,Double>(
                            result.get(index).x-result.get(0).x,
                            result.get(index).y-result.get(0).y
                        );
                    final Double currAngle = GrahamsScan.realPolarAngle(xAxis, newVector);
                    //System.out.println("Index: " + index);
                    // find insertion pos
                    while (Double.compare(GrahamsScan.realPolarAngle(xAxis, currVector),currAngle) < 0) {
                        ++index;
                        if (index == result.size()) {
                            break;
                        }
                        currVector =
                            new Pair<Double,Double>(
                                result.get(index).x-result.get(0).x,
                                result.get(index).y-result.get(0).y
                            );
                        //System.out.println("Index: " + index);
                    }
                    if (!(Double.compare(GrahamsScan.realPolarAngle(xAxis, currVector),currAngle) < 0)) {
                        currVector =
                            new Pair<Double,Double>(
                                result.get(index).x-result.get(0).x,
                                result.get(index).y-result.get(0).y
                            );
                        // remove duplicates - take the outer one (distance to refpoint)
//                        System.out.println(
//                            "Check for duplicates of current Angle: "
//                            + currAngle
//                            + " and "
//                            + realPolarAngle(test, tmp2)
//                            + " : "
//                            + (Double.compare(realPolarAngle(test, tmp2),currAngle) == 0)
//                        );
                        if (Double.compare(GrahamsScan.realPolarAngle(xAxis, currVector),currAngle) == 0) {
                            //System.out.println("Duplicate angle.");
                            // compare coordinates
                            final Double currDistance =
                                Math.sqrt( Math.pow(result.get(index).x, 2) + Math.pow(result.get(index).y, 2) );
                            final Double newDistance =
                                Math.sqrt( Math.pow(pointSet.get(i).x, 2) + Math.pow(pointSet.get(i).y, 2) );

                            if (Double.compare(currDistance, newDistance) < 0) {
                                //System.out.println("Remove " + index);
                                result.remove(index);
                            } else {
                                //System.out.println("Do just not insert.");
                                add = false;
                            }
                        }
                    }
                }
                if (add) {
                    //System.out.println("Insert (" + pointSet.get(i).x + "," + pointSet.get(i).y + ") at " + index);
                    result.add(index,pointSet.get(i));
                    //System.out.println("Size of list: " + result.size());
                } else {
                    add = true;
                }
            }
        }
        return result;
    }

    /**
     * Computes the polar angle of two points.
     * @param pointA The first point.
     * @param pointB The second point.
     * @return polar angle
     */
    private static Double polarAngle(final Pair<Double,Double> pointA, final Pair<Double,Double> pointB) {
//        System.out.println(
//            "Angle [("
//            + pointA.x
//            + ","
//            + pointA.y
//            + "); ("
//            + pointB.x
//            + ","
//            + pointB.y
//            + ")] = "
//            + (-pointA.y*pointB.x + pointA.x*pointB.y)
//        );
        return (-pointA.y*pointB.x + pointA.x*pointB.y);
    }

    private static Double realPolarAngle(final Pair<Double,Double> A, final Pair<Double,Double> B) {
//        System.out.println(
//            "Angle [("
//            + A.x
//            + ","
//            + A.y
//            + "); ("
//            + B.x
//            + ","
//            + B.y
//            + ")] = "
//            + Math.acos((A.x * B.x + A.y * B.y) / (Math.sqrt(A.x * A.x + A.y * A.y) * Math.sqrt(B.x * B.x + B.y * B.y)))
//              * (180 / Math.PI)
//        );
        return
            Math.acos((A.x * B.x + A.y * B.y) / (Math.sqrt(A.x * A.x + A.y * A.y) * Math.sqrt(B.x * B.x + B.y * B.y)))
            * (180 / Math.PI);
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
                    return Double.compare(start.angleTo(p1), start.angleTo(p2));
                }

            }
        );
        final Stack<Point> stack = new Stack<Point>();
        stack.push(start);
        stack.push(sortedList.removeFirst());
        result.add(new LinkedList<Point>(stack));
        for (final Point p : sortedList) {
            Point candidate = stack.pop();
            List<Point> snapshot = new LinkedList<Point>(stack);
            snapshot.add(candidate);
            snapshot.add(p);
            result.add(snapshot);
            while (
                stack.size() > 1
                && GrahamsScan.computeDeterminant(stack.peek(), candidate, p).compareTo(BigFraction.ZERO) <= 0
            ) {
                candidate = stack.pop();
                snapshot = new LinkedList<Point>(stack);
                snapshot.add(candidate);
                snapshot.add(p);
                result.add(snapshot);
            }
            stack.push(p);
        }
        return result;
    }

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final ArrayList<Pair<Double,Double>> pointSet =
            GrahamsScan.parseOrGenerateConvexHullProblem(input.options);
        GrahamsScan.printConvexHull(
            pointSet,
            PreprintMode.parsePreprintMode(input.options),
            input.options,
            input.exerciseWriter,
            input.solutionWriter
        );
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    @Override
    public List<Point> parseOrGenerateProblem(final Parameters options) throws IOException {
        return new ParserAndGenerator<List<Point>>(
            GrahamsScan::parsePointSet,
            GrahamsScan::generatePointSet
        ).getResult(options);
    }

    @Override
    public void printExercise(
        final List<Point> problem,
        final List<List<Point>> solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void printSolution(
        final List<Point> problem,
        final List<List<Point>> solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        // TODO Auto-generated method stub

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
