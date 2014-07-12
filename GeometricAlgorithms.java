import java.io.*;
import java.util.*;

/**
 * Class offering methods for geometric algorithms.
 * @author Stefan Schupp
 * @version $Id$
 */
public abstract class GeometricAlgorithms {
    
    private static Stack<Pair<Double,Double>> computeConvexHull(ArrayList<Pair<Double,Double>> pointSet, BufferedWriter solWriter)
    throws IOException {
        // find reference point as starting point
        int refIndex = 0;
        Pair<Double,Double> refPoint = pointSet.get(0);
        for(int i = 1; i<pointSet.size(); ++i) {
            if(pointSet.get(i).y < refPoint.y || (pointSet.get(i).y == refPoint.y && pointSet.get(i).x < refPoint.x) ) {
                refPoint = pointSet.get(i);
                refIndex = i;
            }
        }
        
        System.out.println("Referencepoint: " + refPoint.x + ", " + refPoint.y);
        
        // sort Array of Points according to determinant with reference to refPoint
        ArrayList<Pair<Double,Double>> tmp = pointAscendingSort(pointSet, refIndex);
        
        System.out.println("Array is sorted, start hull calculation.");
        
        Stack<Pair<Double,Double>> hull = new Stack<Pair<Double,Double>>();
        for(int i = 0; i < pointSet.size(); ++i) {
            if (i > 2) {
                Pair<Double,Double> first = hull.pop();
                Pair<Double,Double> second = hull.peek();
                hull.push(first);
                System.out.println("First: " + first.x +", " + first.y);
                System.out.println("Second: " + second.x +", " + second.y);
                while( direction(second, first, tmp.get(i)) <= 0) {
                    System.out.println("Pop.");
                    hull.pop();
                }
            }
            hull.push(tmp.get(i));
            TikZUtils.printBeginning(TikZUtils.CENTER, solWriter);
            printPartialHull(pointSet,hull,solWriter);
            TikZUtils.printEnd(TikZUtils.CENTER, solWriter);
            solWriter.newLine();
        }
        
        // add last edge
        hull.push(refPoint);
        TikZUtils.printBeginning(TikZUtils.CENTER, solWriter);
        printPartialHull(pointSet,hull,solWriter);
        TikZUtils.printEnd(TikZUtils.CENTER, solWriter);
        
        return hull;
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
    private static Double direction(Pair<Double, Double> A, Pair<Double,Double> B, Pair<Double,Double> C) {
        Pair<Double,Double> firstSegment = new Pair<Double,Double>(B.x-A.x, B.y-A.y);
        Pair<Double,Double> secondSegment = new Pair<Double,Double>(C.x-B.x, C.y-B.y);
        return (polarAngle(firstSegment, secondSegment));
    }
    
    /**
     * Sorts the given pointset according to the reference point in ascending order.
     * @param pointSet The pointset.
     * @param reference The reference point.
     * @return a sorted pointset
     */
    private static ArrayList<Pair<Double,Double>> pointAscendingSort(ArrayList<Pair<Double,Double>> pointSet, int referenceIndex) {
        ArrayList<Pair<Double,Double>> result = new ArrayList<Pair<Double,Double>>();
        result.add(pointSet.get(referenceIndex));
        
        // Insertion-Sort the array
        for(int i = 0; i < pointSet.size(); ++i) {
            if(i != referenceIndex) {
                int index = 1;
                Double currAngle = polarAngle(result.get(0), pointSet.get(i));
                while( index < result.size() && polarAngle(result.get(0), result.get(index)) < currAngle ) {
                    ++index;
                }
                result.add(index,pointSet.get(i));
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
    private static Double polarAngle(Pair<Double,Double> pointA, Pair<Double,Double> pointB) {
        System.out.println("Angle [(" + pointA.x + "," + pointA.y + "); (" + pointB.x + "," + pointB.y + ")] = " + (-pointA.y*pointB.x + pointA.x*pointB.y));
        return (-pointA.y*pointB.x + pointA.x*pointB.y);
    }
    
    
    /**
     * Prints the convex hull according to the graham scan algorithm.
     * @param pointSet The set of points as input.
     * @param exerciseWriter The writer for the exercise.
     * @param solutionWriter The writer for the solution.
     */
    public static void printConvexHull(ArrayList<Pair<Double,Double>> pointSet, BufferedWriter exWriter, BufferedWriter solWriter)
    throws IOException {
        exWriter.write("Berechnen Sie die konvexe H\\\"ulle der folgenden Punktmenge.");
        exWriter.write("Benutzen Sie daf\\\"ur Grahams' Scan und geben Sie die Teilschritte");
        exWriter.write("der  \\\"au{\\ss}eren Schleife an.");
        exWriter.newLine();
        exWriter.newLine();
        TikZUtils.printBeginning(TikZUtils.CENTER, exWriter);
        printPointset(pointSet, exWriter);
        exWriter.newLine();
        TikZUtils.printEnd(TikZUtils.CENTER, exWriter);
        
        
        // solution
        Stack<Pair<Double,Double>> hull = new Stack<Pair<Double,Double>>();
        hull = computeConvexHull(pointSet, solWriter);
        
        System.out.println("Hull:");
        while(!hull.empty()) {
            Pair<Double,Double> point = hull.pop();
            System.out.println(point.x + ", " + point.y);
        }
        
    }
    
    
    public static void printPointset(ArrayList<Pair<Double,Double>> pointSet, BufferedWriter writer) throws IOException {
        for(int i = 0; i < pointSet.size(); ++i) {
            writer.write("\\node at (" + pointSet.get(i).x + ", " + pointSet.get(i).y + ") {\\textbullet}");
            writer.newLine();
        }
    }
    
    public static void printPartialHull(ArrayList<Pair<Double,Double>> pointSet, Stack<Pair<Double,Double>> hull, BufferedWriter writer) throws IOException {
        TikZUtils.printTikzBeginning(TikZStyle.GRAPH, writer);
        for(int i = 0; i < pointSet.size(); ++i) {
            writer.write("\\node at (" + pointSet.get(i).x + ", " + pointSet.get(i).y + ") {\\textbullet};");
            writer.newLine();
        }
        
        ArrayList<String> edges = new ArrayList<String>();
        int index = 0;
        
        Stack<Pair<Double,Double>> clone = (Stack<Pair<Double,Double>>) hull.clone();
        Pair<Double,Double> start = clone.peek();
        while(!clone.empty()) {
            Pair<Double, Double> first = clone.pop();
            if(!clone.empty()) {
                Pair<Double,Double> second = clone.peek();
                edges.add("\\draw[latex-latex, thin] ("+second.x+","+second.y+")--("+first.x+","+first.y+")");
                writer.write("\\node at (" + first.x + ", " + first.y + ") {\\textbullet};");
                writer.newLine();
            }
            ++index;
        }
        
        for(int i = 0; i < edges.size(); ++i) {
            writer.write(edges.get(i));
            writer.newLine();
        }
        writer.write(";");
        writer.newLine();
        TikZUtils.printTikzEnd(writer);
        writer.newLine();
    }
}
