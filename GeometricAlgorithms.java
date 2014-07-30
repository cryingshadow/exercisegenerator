import java.io.*;
import java.util.*;

/**
 * Class offering methods for geometric algorithms.
 * @author Stefan Schupp
 * @version $Id$
 */
public abstract class GeometricAlgorithms {
    
    private static int computeConvexHull(ArrayList<Pair<Double,Double>> pointSet, BufferedWriter solWriter)
    throws IOException {
        // find reference point as starting point
        int refIndex = 0;
        int count = 1;
        Pair<Double,Double> refPoint = pointSet.get(0);
        for(int i = 1; i<pointSet.size(); ++i) {
            if(pointSet.get(i).y < refPoint.y || (pointSet.get(i).y == refPoint.y && pointSet.get(i).x < refPoint.x) ) {
                refPoint = pointSet.get(i);
                refIndex = i;
            }
        }
        
        //System.out.println("Referencepoint: " + refPoint.x + ", " + refPoint.y);
        
        // sort Array of Points according to determinant with reference to refPoint
        ArrayList<Pair<Double,Double>> tmp = pointAscendingSort(pointSet, refIndex);
        
        // find missing points
        ArrayList<Pair<Double,Double>> duplicates = new ArrayList<Pair<Double,Double>>();
        for(int i = 0; i < pointSet.size(); ++i) {
            if(!tmp.contains(pointSet.get(i))) {
                duplicates.add(pointSet.get(i));
            }
        }
        
        printPointset(tmp,0,duplicates,solWriter);
        
        //System.out.println("Sorted Array:");
        //int q = 0;
        //while( q < tmp.size()) {
        //    System.out.println(tmp.get(q).x + "," + tmp.get(q).y);
        //    ++q;
        //}
        
        boolean oneOnly = false;
        Stack<Pair<Double,Double>> hull = new Stack<Pair<Double,Double>>();
        for(int i = 0; i < tmp.size(); ++i) {
            if (i > 1) {
                Pair<Double,Double> first = hull.pop();
                Pair<Double,Double> second = hull.peek();
                hull.push(first);
                //System.out.println("First: " + first.x +", " + first.y);
                //System.out.println("Second: " + second.x +", " + second.y);
                //System.out.println("Acutal: " + tmp.get(i).x +", " + tmp.get(i).y);
                while( !oneOnly && direction(second, first, tmp.get(i)) <= 0) {
                    //System.out.println("Pop.");
                    hull.pop();
                    first = hull.pop();
                    //System.out.println("First: " + first.x +", " + first.y);
                    if(!hull.empty()) {
                        second = hull.peek();
                        //System.out.println("Second: " + second.x +", " + second.y);
                    }
                    else {
                        oneOnly = true;
                    }
                    hull.push(first);
                    //System.out.println("Acutal: " + tmp.get(i).x +", " + tmp.get(i).y);
                }
            }
            oneOnly = false;
            hull.push(tmp.get(i));
            printPartialHull(tmp,hull,duplicates,solWriter);
            if(count%2 == 1) {
                solWriter.newLine();
                solWriter.write("\\\\");
                solWriter.newLine();
            }
            ++count;
        }
        
        // add last edge
        hull.push(refPoint);
        printPartialHull(tmp,hull,duplicates,solWriter);
        
        return count;
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
        //System.out.println("RefIndex: " + referenceIndex + " Point: " + pointSet.get(referenceIndex).x + "," + pointSet.get(referenceIndex).y);
        boolean noAdd = false;
        
        // Insertion-Sort the array
        for(int i = 0; i < pointSet.size(); ++i) {
            if(i != referenceIndex) {
                int index = 1;
                if (result.size() > 1) {
                    Pair<Double,Double> tmp = new Pair<Double,Double>(pointSet.get(i).x-result.get(0).x, pointSet.get(i).y-result.get(0).y);
                    Pair<Double,Double> tmp2 = new Pair<Double,Double>(result.get(index).x-result.get(0).x, result.get(index).y-result.get(0).y);
                    Pair<Double,Double> test = new Pair<Double,Double>(1.0,0.0);
                    //Double currAngle = realPolarAngle(result.get(0), tmp);
                    Double currAngle = realPolarAngle(test, tmp);
                    //System.out.println("Index: " + index);
                    while( Double.compare(realPolarAngle(test, tmp2),currAngle) < 0 ) {
                        ++index;
                        if(index == result.size()) {
                            break;
                        }
                        tmp2 = new Pair<Double,Double>(result.get(index).x-result.get(0).x, result.get(index).y-result.get(0).y);
                        //System.out.println("Index: " + index);
                    }
                    if( !(Double.compare(realPolarAngle(test, tmp2),currAngle) < 0) ) {
                        tmp2 = new Pair<Double,Double>(result.get(index).x-result.get(0).x, result.get(index).y-result.get(0).y);
                        // remove duplicates - take the outer one (larger absolute sum of x and y component)
                        //System.out.println("Check for duplicates of current Angle: " + currAngle + " and " + realPolarAngle(test, tmp2) + " : " + (Double.compare(realPolarAngle(test, tmp2),currAngle) == 0) );
                        if (Double.compare(realPolarAngle(test, tmp2),currAngle) == 0) {
                            //System.out.println("Duplicate angle.");
                            // compare coordinates
                            Double aAbsCoord = Math.abs(pointSet.get(i).x) + Math.abs(pointSet.get(i).y);
                            Double bAbsCoord = Math.abs(result.get(index).x) + Math.abs(result.get(index).y);
                            if (aAbsCoord > bAbsCoord) {
                                //System.out.println("Remove " + index);
                                result.remove(index);
                            } else {
                                //System.out.println("Do just not insert.");
                                noAdd = true;
                            }
                        }
                    }
                }
                if(!noAdd) {
                    //System.out.println("Insert (" + pointSet.get(i).x + "," + pointSet.get(i).y + ") at " + index);
                    result.add(index,pointSet.get(i));
                    //System.out.println("Size of list: " + result.size());
                } else {
                    noAdd = false;
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
    private static Double polarAngle(Pair<Double,Double> pointA, Pair<Double,Double> pointB) {
        //System.out.println("Angle [(" + pointA.x + "," + pointA.y + "); (" + pointB.x + "," + pointB.y + ")] = " + (-pointA.y*pointB.x + pointA.x*pointB.y));
        return (-pointA.y*pointB.x + pointA.x*pointB.y);
    }
    
    
    private static Double realPolarAngle(Pair<Double,Double> A, Pair<Double,Double> B) {
        //System.out.println("Angle [(" + A.x + "," + A.y + "); (" + B.x + "," + B.y + ")] = " + Math.acos( (A.x*B.x + A.y*B.y) / (Math.sqrt(A.x*A.x+A.y*A.y) * Math.sqrt(B.x*B.x+B.y*B.y)) ) * (180/Math.PI));
        return Math.acos( (A.x*B.x + A.y*B.y) / (Math.sqrt(A.x*A.x+A.y*A.y) * Math.sqrt(B.x*B.x+B.y*B.y)) ) * (180/Math.PI);
    }
    
    /**
     * Prints the convex hull according to the graham scan algorithm.
     * @param pointSet The set of points as input.
     * @param exerciseWriter The writer for the exercise.
     * @param solutionWriter The writer for the solution.
     */
    public static void printConvexHull(ArrayList<Pair<Double,Double>> pointSet, BufferedWriter exWriter, BufferedWriter solWriter)
    throws IOException {
        exWriter.write("Berechnen Sie die konvexe H\\\"ulle der folgenden Punktmenge. ");
        exWriter.write("Benutzen Sie daf\\\"ur \\emphasize{Grahams Scan} wie in der Vorlesung vorgestellt ");
        exWriter.write("und geben Sie die Teilschritte \\emphasize{nach jeder Iteration}");
        exWriter.write(" (also nach jedem neu hinzugef\"ugten Punkt) an. ");
        exWriter.write("Umkreisen Sie die Punkte, die vom Algorithmus in der Iterationsschleife nicht betrachtet werden.\\\\");
        exWriter.newLine();
        exWriter.write("\\\\");
        exWriter.newLine();
        exWriter.newLine();
        
        // solution
        int count = computeConvexHull(pointSet, solWriter);
        boolean one = false;
        
        while(count > 0) {
            printPointset(pointSet, -1 , null, exWriter);
            if(one) {
                exWriter.newLine();
                exWriter.write("\\\\");
                exWriter.newLine();
            }
            one = !one;
            --count;
        }
    }
    
    
    public static void printPointset(ArrayList<Pair<Double,Double>> pointSet, int reference, ArrayList<Pair<Double,Double>> duplicates, BufferedWriter writer) throws IOException {
        writer.write("\\resizebox{.45\\textwidth}{!}{");
        writer.newLine();
        TikZUtils.printTikzBeginning(TikZStyle.POINTSET, writer);
        writer.newLine();
        for(int i = 0; i < pointSet.size(); ++i) {
            if(reference >= 0) {
                writer.write("\\node at (" + pointSet.get(i).x + ", " + pointSet.get(i).y + ") {\\textbullet "+ i +"};");
                writer.newLine();
            } else {
                writer.write("\\node at (" + pointSet.get(i).x + ", " + pointSet.get(i).y + ") {\\textbullet (" + pointSet.get(i).x.intValue() +"," + pointSet.get(i).y.intValue() +")};");
                writer.newLine();
            }
        }
        if(reference >= 0) {
            for(int i = 0; i < pointSet.size(); ++i) {
                writer.write("\\draw ("+pointSet.get(reference).x+","+pointSet.get(reference).y+")--("+pointSet.get(i).x+","+pointSet.get(i).y+");");
                writer.newLine();
            }
        }
        if(duplicates != null) {
            for(int i = 0; i < duplicates.size(); ++i) {
                writer.write("\\node at (" + duplicates.get(i).x + ", " + duplicates.get(i).y + ") {$\\circ$};");
                writer.newLine();
            }
        }
        TikZUtils.printTikzEnd(writer);
        writer.newLine();
        writer.write("}");
    }
    
    public static void printPartialHull(ArrayList<Pair<Double,Double>> pointSet, Stack<Pair<Double,Double>> hull, ArrayList<Pair<Double,Double>> duplicates, BufferedWriter writer) throws IOException {
        writer.write("\\resizebox{.45\\textwidth}{!}{");
        writer.newLine();
        TikZUtils.printTikzBeginning(TikZStyle.POINTSET, writer);
        for(int i = 0; i < pointSet.size(); ++i) {
            writer.write("\\node at (" + pointSet.get(i).x + ", " + pointSet.get(i).y + ") {\\textbullet};");
            writer.newLine();
        }
        
        if(duplicates != null) {
            for(int i = 0; i < duplicates.size(); ++i) {
                writer.write("\\node at (" + duplicates.get(i).x + ", " + duplicates.get(i).y + ") {$\\circ$};");
                writer.newLine();
            }
        }
        
        ArrayList<String> edges = new ArrayList<String>();
        int index = 0;
        
        Stack<Pair<Double,Double>> clone = (Stack<Pair<Double,Double>>) hull.clone();
        Pair<Double,Double> start = clone.peek();
        while(!clone.empty()) {
            Pair<Double, Double> first = clone.pop();
            if(!clone.empty()) {
                Pair<Double,Double> second = clone.peek();
                edges.add("\\draw ("+second.x+","+second.y+")--("+first.x+","+first.y+");");
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
        writer.write("}");
    }
}
