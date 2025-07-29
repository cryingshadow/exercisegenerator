package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.math.*;
import java.util.*;

import org.apache.commons.math3.fraction.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.algebra.*;
import exercisegenerator.structures.graphs.petrinets.*;

public class PetriNetFarkasAlgorithm extends PetriNetAlgorithm<List<Matrix>> {

    public static final PetriNetFarkasAlgorithm INSTANCE = new PetriNetFarkasAlgorithm();

    private static final Comparator<BigInteger> COMPARATOR =
        new Comparator<BigInteger>() {

            @Override
            public int compare(final BigInteger o1, final BigInteger o2) {
                return o1.compareTo(o2);
            }

        };

    private static BigInteger gcd(final BigFraction[] row) {
        return PetriNetFarkasAlgorithm.gcd(
            Arrays.stream(row).map(n -> n.abs().getNumerator()).filter(n -> n.compareTo(BigInteger.ZERO) > 0).toList()
        );
    }

    private static BigInteger gcd(final List<BigInteger> numbers) {
        if (numbers.isEmpty()) {
            return BigInteger.ONE;
        }
        final BigInteger max = numbers.stream().max(PetriNetFarkasAlgorithm.COMPARATOR).get();
        final BigInteger min = numbers.stream().min(PetriNetFarkasAlgorithm.COMPARATOR).get();
        if (max.equals(min)) {
            return min;
        }
        return PetriNetFarkasAlgorithm.gcd(
            numbers.stream()
            .<BigInteger>map(number -> number.compareTo(min) > 0 ? number.subtract(min) : number)
            .toList()
        );
    }

    private PetriNetFarkasAlgorithm() {}

    @Override
    public List<Matrix> apply(final PetriNetInput input) {
        final PetriNet net = new PetriNet(input);
        final List<Matrix> result = new LinkedList<Matrix>();
        final Matrix incidence = net.toIncidenceMatrix();
        if (incidence.getNumberOfRows() == 0) {
            return List.of(incidence);
        }
        final List<BigFraction[]> identity = new LinkedList<BigFraction[]>();
        final int numberOfPlaces = incidence.getNumberOfRows();
        for (int i = 0; i < numberOfPlaces; i++) {
            final BigFraction[] column = new BigFraction[numberOfPlaces];
            Arrays.fill(column, BigFraction.ZERO);
            column[i] = BigFraction.ONE;
            identity.add(column);
        }
        Matrix current = incidence.insertColumnsAtIndex(identity, incidence.separatorIndex);
        result.add(current);
        final int numberOfTransitions = incidence.getNumberOfColumns();
        final int numberOfColumns = numberOfPlaces + numberOfTransitions;
        for (int column = 0; column < numberOfTransitions; column++) {
            if (current.getNumberOfRows() == 0) {
                break;
            }
            final List<Integer> negative = new LinkedList<Integer>();
            final List<Integer> positive = new LinkedList<Integer>();
            for (int row = 0; row < current.getNumberOfRows(); row++) {
                final BigFraction coefficient = current.getCoefficient(column, row);
                if (coefficient.compareTo(BigFraction.ZERO) < 0) {
                    negative.add(row);
                } else if (coefficient.compareTo(BigFraction.ZERO) > 0) {
                    positive.add(row);
                }
            }
            for (final Integer rowNeg : negative) {
                for (final Integer rowPos : positive) {
                    final BigFraction[] rowToAdd = new BigFraction[numberOfColumns];
                    final BigFraction negCoefficient = current.getCoefficient(column, rowNeg).abs();
                    final BigFraction posCoefficient = current.getCoefficient(column, rowPos).abs();
                    for (int columnForAddition = 0; columnForAddition < numberOfColumns; columnForAddition++) {
                        rowToAdd[columnForAddition] =
                            current.getCoefficient(columnForAddition, rowNeg)
                            .multiply(posCoefficient)
                            .add(current.getCoefficient(columnForAddition, rowPos).multiply(negCoefficient));
                    }
                    final BigInteger gcd = PetriNetFarkasAlgorithm.gcd(rowToAdd);
                    for (int columnForAddition = 0; columnForAddition < numberOfColumns; columnForAddition++) {
                        rowToAdd[columnForAddition] = rowToAdd[columnForAddition].divide(gcd);
                    }
                    current = current.insertRowsAtIndex(Collections.singletonList(rowToAdd), current.getNumberOfRows());
                }
            }
            for (int row = current.getNumberOfRows() - 1; row >= 0; row--) {
                if (current.getCoefficient(column, row).compareTo(BigFraction.ZERO) != 0) {
                    current = current.removeRowsFromIndex(1, row);
                }
            }
            result.add(current);
        }
        if (current.getNumberOfRows() > 0) {
            current = current.removeColumnsFromIndex(numberOfTransitions, 0);
            result.add(current.setSeparatorIndex(current.getNumberOfColumns()).reduceToBase());
        }
        return result;
    }

    @Override
    public String[] generateTestParameters() {
        return new String[] {};
    }

    @Override
    public void printExercise(
        final PetriNetInput problem,
        final List<Matrix> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Betrachten Sie das folgende Petrinetz $N$:\\\\[2ex]");
        Main.newLine(writer);
        final PetriNet net = new PetriNet(problem);
        LaTeXUtils.printDefaultAdjustboxBeginning(writer);
        net.toTikz(new PetriMarking(), writer);
        LaTeXUtils.printAdjustboxEnd(writer);
        LaTeXUtils.printVerticalProtectedSpace(writer);
        writer.write("Berechnen Sie eine minimale Basis der P-Invarianten von $N$ mithilfe des ");
        writer.write("\\emphasize{Algorithmus von Farkas}.");
        Main.newLine(writer);
        Main.newLine(writer);
    }

    @Override
    public void printSolution(
        final PetriNetInput problem,
        final List<Matrix> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("{\\renewcommand{\\arraystretch}{1.2}");
        Main.newLine(writer);
        Main.newLine(writer);
        writer.write("\\begin{enumerate}");
        Main.newLine(writer);
        for (final Matrix matrix : solution) {
            writer.write("\\item $");
            writer.write(matrix.toLaTeX());
            writer.write("$");
            Main.newLine(writer);
        }
        writer.write("\\end{enumerate}");
        Main.newLine(writer);
        Main.newLine(writer);
        writer.write("\\renewcommand{\\arraystretch}{1}}");
        Main.newLine(writer);
        Main.newLine(writer);
    }

}
