package exercisegenerator.algorithms.optimization;

import java.util.*;
import java.util.Optional;
import java.util.function.*;

import org.apache.commons.math3.fraction.*;
import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.algebra.*;
import exercisegenerator.structures.optimization.*;

public class OptimizationAlgorithmsTest {

    @DataProvider
    public Object[][] dpData() {
        final int[] weights = new int[] {2,1,3};
        final int[] values = new int[] {7,4,6};
        return new Object[][] {
            {
                new int[][] {
                    {0,0,0},
                    {0,1,1},
                    {0,1,2}
                },
                new DPHeading(
                    1,
                    (Function<Integer,String>)i -> i.toString(),
                    (Function<Integer,List<String>>)i -> List.of(LaTeXUtils.bold(i.toString()))
                ),
                new DPHeading(
                    1,
                    (Function<Integer,String>)i -> i.toString(),
                    (Function<Integer,List<String>>)i -> List.of(LaTeXUtils.bold(i.toString()))
                ),
                Optional.of(KnapsackAlgorithm.traceback(new int[] {1,1})),
                new String[][] {
                    {"${}^*$", "\\textbf{0}", null, "\\textbf{1}", null, "\\textbf{2}", null},
                    {"\\textbf{0}", "0", null, "0", null, "0", null},
                    {"\\textbf{1}", "0", "$\\uparrow$", "1", "$\\leftarrow$", "1", null},
                    {"\\textbf{2}", "0", null, "1", "$\\uparrow$", "2", "$\\leftarrow$"}
                }
            },
            {
                new int[][] {
                    {0,0,0},
                    {0,1,1},
                    {0,1,2}
                },
                new DPHeading(
                    1,
                    (Function<Integer,String>)i -> i.toString(),
                    (Function<Integer,List<String>>)i -> List.of(LaTeXUtils.bold(i.toString()))
                ),
                new DPHeading(
                    1,
                    (Function<Integer,String>)i -> i.toString(),
                    (Function<Integer,List<String>>)i -> List.of(LaTeXUtils.bold(i.toString()))
                ),
                Optional.of(KnapsackAlgorithm.traceback(new int[] {1,2})),
                new String[][] {
                    {"${}^*$", "\\textbf{0}", null, "\\textbf{1}", null, "\\textbf{2}", null},
                    {"\\textbf{0}", "0", null, "0", null, "0", null},
                    {"\\textbf{1}", "0", "$\\uparrow$", "1", null, "1", null},
                    {"\\textbf{2}", "0", "$\\uparrow$", "1", "$\\leftarrow$", "2", "$\\leftarrow$"}
                }
            },
            {
                new int[][] {
                    {0,0,0},
                    {0,1,1},
                    {0,1,1}
                },
                new DPHeading(
                    1,
                    (Function<Integer,String>)i -> i.toString(),
                    (Function<Integer,List<String>>)i -> List.of(LaTeXUtils.bold(i.toString()))
                ),
                new DPHeading(
                    1,
                    (Function<Integer,String>)i -> i.toString(),
                    (Function<Integer,List<String>>)i -> List.of(LaTeXUtils.bold(i.toString()))
                ),
                Optional.of(KnapsackAlgorithm.traceback(new int[] {1,3})),
                new String[][] {
                    {"${}^*$", "\\textbf{0}", null, "\\textbf{1}", null, "\\textbf{2}", null},
                    {"\\textbf{0}", "0", null, "0", "$\\leftarrow$", "0", null},
                    {"\\textbf{1}", "0", null, "1", "$\\uparrow$", "1", "$\\leftarrow$"},
                    {"\\textbf{2}", "0", null, "1", null, "1", "$\\uparrow$"}
                }
            },
            {
                new int[][] {
                    {0,0,0},
                    {0,1,1},
                    {0,1,1}
                },
                new DPHeading(
                    1,
                    (Function<Integer,String>)i -> i.toString(),
                    (Function<Integer,List<String>>)i ->
                        List.of(i == 0 ? "" : LaTeXUtils.bold(String.valueOf("AB".charAt(i - 1))))
                ),
                new DPHeading(
                    1,
                    (Function<Integer,String>)i -> i.toString(),
                    (Function<Integer,List<String>>)i ->
                        List.of(i == 0 ? "" : LaTeXUtils.bold(String.valueOf("AC".charAt(i - 1))))
                ),
                Optional.of(LCSAlgorithm.TRACEBACK),
                new String[][] {
                    {"${}^*$", "", null, "\\textbf{A}", null, "\\textbf{C}", null},
                    {"", "0", null, "0", null, "0", null},
                    {"\\textbf{A}", "0", null, "1", "$\\nwarrow$", "1", "$\\leftarrow$"},
                    {"\\textbf{B}", "0", null, "1", null, "1", "$\\uparrow$"}
                }
            },
            {
                new int[][] {
                    {3,2,1,0},
                    {4,5,6,7},
                    {-1,-2,-3,-4}
                },
                new DPHeading(
                    3,
                    (Function<Integer,String>)i -> {
                        switch (i) {
                        case 0:
                            return "$\\mathbf{w_i}$";
                        case 1:
                            return "$\\mathbf{v_i}$";
                        default:
                            return "";
                        }
                    },
                    (Function<Integer,List<String>>)i ->
                        List.of(
                            String.valueOf(weights[i]),
                            String.valueOf(values[i]),
                            LaTeXUtils.bold(String.valueOf(i))
                        )
                ),
                new DPHeading(
                    1,
                    (Function<Integer,String>)i -> "",
                    (Function<Integer,List<String>>)i -> List.of(LaTeXUtils.bold(i.toString()))
                ),
                Optional.of(
                    new DPTracebackFunction() {
                        @Override
                        public List<DPDirection> apply(final DPPosition position) {
                            return List.of();
                        }
                    }
                ),
                new String[][] {
                    {"$\\mathbf{w_i}$", "$\\mathbf{v_i}$", "${}^*$", "\\textbf{0}", null, "\\textbf{1}", null, "\\textbf{2}", null, "\\textbf{3}", null},
                    {"2", "7", "\\textbf{0}", "3", null, "2", null, "1", null, "0", null},
                    {"1", "4", "\\textbf{1}", "4", null, "5", null, "6", null, "7", null},
                    {"3", "6", "\\textbf{2}", "-1", null, "-2", null, "-3", null, "-4", null}
                }
            }
        };
    }

    @Test(dataProvider="dpData")
    public void dpTableTest(
        final int[][] solution,
        final DPHeading rowHeading,
        final DPHeading columnHeading,
        final Optional<DPTracebackFunction> optionalTraceback,
        final String[][] expected
    ) {
        final String[][] result =
            OptimizationAlgorithms.toDPSolutionTable(solution, rowHeading, columnHeading, optionalTraceback);
        Assert.assertTrue(
            Arrays.deepEquals(result, expected),
            String.format("\nExpected: %s\nActual:   %s\n", Arrays.deepToString(expected), Arrays.deepToString(result))
        );
    }

    @DataProvider
    public Object[][] knapsackData() {
        return new Object[][] {
            {
                new KnapsackProblem(
                    new int[] {1,2,3},
                    new int[] {3,2,1},
                    3
                ),
                new int[][] {
                    {0,0,0,0},
                    {0,3,3,3},
                    {0,3,3,5},
                    {0,3,3,5}
                }
            },
            {
                new KnapsackProblem(
                    new int[] {4,3,3},
                    new int[] {3,2,2},
                    6
                ),
                new int[][] {
                    {0,0,0,0,0,0,0},
                    {0,0,0,0,3,3,3},
                    {0,0,0,2,3,3,3},
                    {0,0,0,2,3,3,4}
                }
            }
        };
    }

    @Test(dataProvider="knapsackData")
    public void knapsackTest(final KnapsackProblem problem, final int[][] expected) {
        final int[][] result = KnapsackAlgorithm.INSTANCE.apply(problem);
        Assert.assertTrue(
            Arrays.deepEquals(result, expected),
            String.format("\nExpected: %s\nActual:   %s\n", Arrays.deepToString(expected), Arrays.deepToString(result))
        );
    }

    @DataProvider
    public Object[][] lcsData() {
        return new Object[][] {
            {
                new LCSProblem("REGENRINNE", "INDERINNEN"),
                new int[][] {
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,1,1,1,1,1,1},
                    {0,0,0,0,1,1,1,1,1,2,2},
                    {0,0,0,0,1,1,1,1,1,2,2},
                    {0,0,0,0,1,1,1,1,1,2,2},
                    {0,0,1,1,1,1,1,2,2,2,3},
                    {0,0,1,1,1,2,2,2,2,2,3},
                    {0,1,1,1,1,2,3,3,3,3,3},
                    {0,1,2,2,2,2,3,4,4,4,4},
                    {0,1,2,2,2,2,3,4,5,5,5},
                    {0,1,2,2,3,3,3,4,5,6,6},
                }
            }
        };
    }

    @Test(dataProvider="lcsData")
    public void lcsTest(final LCSProblem problem, final int[][] expected) {
        final int[][] result = LCSAlgorithm.INSTANCE.apply(problem);
        Assert.assertTrue(
            Arrays.deepEquals(result, expected),
            String.format("\nExpected: %s\nActual:   %s\n", Arrays.deepToString(expected), Arrays.deepToString(result))
        );
    }

    @DataProvider
    public Object[][] simplexData() {
        final BigFraction[] target1 = new BigFraction[] {new BigFraction(2), new BigFraction(3)};
        final BigFraction[] target2 = new BigFraction[] {new BigFraction(1), new BigFraction(1)};
        final BigFraction[] target3 = new BigFraction[] {new BigFraction(1), new BigFraction(2), new BigFraction(3)};
        final SimplexProblem problem1 =
            new SimplexProblem(
                target1,
                new Matrix(
                    new BigFraction[][] {
                        {new BigFraction(1),new BigFraction(0),new BigFraction(18,5)},
                        {new BigFraction(1),new BigFraction(2),new BigFraction(5)},
                        {new BigFraction(1),new BigFraction(1),new BigFraction(4)},
                        {new BigFraction(2),new BigFraction(1),new BigFraction(15,2)},
                        {new BigFraction(0),new BigFraction(1),new BigFraction(2)}
                    },
                    2
                ),
                List.of()
            );
        final SimplexProblem problem2 =
            new SimplexProblem(
                target2,
                new Matrix(
                    new BigFraction[][] {
                        {new BigFraction(1),new BigFraction(2),new BigFraction(5)},
                        {new BigFraction(-1),new BigFraction(-1),new BigFraction(-2)},
                        {new BigFraction(2),new BigFraction(1),new BigFraction(7)}
                    },
                    2
                ),
                List.of()
            );
        final SimplexProblem problem3 =
            new SimplexProblem(
                target2,
                new Matrix(
                    new BigFraction[][] {
                        {new BigFraction(-2),new BigFraction(1),new BigFraction(1)},
                        {new BigFraction(1),new BigFraction(-2),new BigFraction(1)}
                    },
                    2
                ),
                List.of()
            );
        final SimplexProblem problem4 =
            new SimplexProblem(
                target2,
                new Matrix(
                    new BigFraction[][] {
                        {new BigFraction(1),new BigFraction(-1),new BigFraction(-1)},
                        {new BigFraction(-1),new BigFraction(2),new BigFraction(-1)}
                    },
                    2
                ),
                List.of()
            );
        final SimplexProblem problem5 =
            new SimplexProblem(
                target3,
                new Matrix(
                    new BigFraction[][] {
                        {new BigFraction(1),new BigFraction(0),new BigFraction(1),new BigFraction(3)},
                        {new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(4)},
                        {new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(5)},
                        {new BigFraction(2),new BigFraction(1),new BigFraction(3),new BigFraction(10)}
                    },
                    3
                ),
                List.of(0,1,2)
            );
        return new Object[][] {
            {
                problem1,
                new SimplexSolution(
                    List.of(
                        new Pair<SimplexProblem, List<SimplexTableau>>(
                            problem1,
                            List.of(
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target1,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(18,5)},
                                                {new BigFraction(1),new BigFraction(2),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(5)},
                                                {new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(4)},
                                                {new BigFraction(2),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(15,2)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(2)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)},
                                                {new BigFraction(2),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                            },
                                            7
                                        ),
                                        List.of()
                                    ),
                                    new int[] {2,3,4,5,6},
                                    new BigFraction[] {null,new BigFraction(5,2),new BigFraction(4),new BigFraction(15,2),new BigFraction(2)},
                                    4,
                                    1
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target1,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(18,5)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-2),new BigFraction(1)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-1),new BigFraction(2)},
                                                {new BigFraction(2),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-1),new BigFraction(11,2)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(2)},
                                                {new BigFraction(0),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(3),new BigFraction(6)},
                                                {new BigFraction(2),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-3),new BigFraction(0)}
                                            },
                                            7
                                        ),
                                        List.of()
                                    ),
                                    new int[] {2,3,4,5,1},
                                    new BigFraction[] {new BigFraction(18,5),new BigFraction(1),new BigFraction(2),new BigFraction(11,4),null},
                                    1,
                                    0
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target1,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction( 2),new BigFraction(13,5)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction( 1),new BigFraction(0),new BigFraction(0),new BigFraction(-2),new BigFraction(1)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(1),new BigFraction(0),new BigFraction( 1),new BigFraction(1)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-2),new BigFraction(0),new BigFraction(1),new BigFraction( 3),new BigFraction(7,2)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction( 0),new BigFraction(0),new BigFraction(0),new BigFraction( 1),new BigFraction(2)},
                                                {new BigFraction(2),new BigFraction(3),new BigFraction(0),new BigFraction( 2),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(8)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-2),new BigFraction(0),new BigFraction(0),new BigFraction( 1),new BigFraction(0)}
                                            },
                                            7
                                        ),
                                        List.of()
                                    ),
                                    new int[] {2,0,4,5,1},
                                    new BigFraction[] {new BigFraction(13,10),null,new BigFraction(1),new BigFraction(7,6),new BigFraction(2)},
                                    2,
                                    6
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target1,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction( 1),new BigFraction(-2),new BigFraction(0),new BigFraction(0),new BigFraction(3,5)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction( 2),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction( 1),new BigFraction(0),new BigFraction(1),new BigFraction(1)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction( 1),new BigFraction(-3),new BigFraction(1),new BigFraction(0),new BigFraction(1,2)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction( 1),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction(2),new BigFraction(3),new BigFraction(0),new BigFraction( 1),new BigFraction( 1),new BigFraction(0),new BigFraction(0),new BigFraction(9)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                            },
                                            7
                                        ),
                                        List.of()
                                    ),
                                    new int[] {2,0,6,5,1},
                                    null,
                                    -1,
                                    -1
                                )
                            )
                        )
                    ),
                    SimplexAnswer.SOLVED
                )
            },
            {
                problem2,
                new SimplexSolution(
                    List.of(
                        new Pair<SimplexProblem, List<SimplexTableau>>(
                            problem2,
                            List.of(
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target2,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(1),new BigFraction(2),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(5)},
                                                {new BigFraction(-1),new BigFraction(-1),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-2)},
                                                {new BigFraction(2),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(7)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)},
                                                {new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                            },
                                            5
                                        ),
                                        List.of()
                                    ),
                                    new int[] {2,3,4},
                                    new BigFraction[] {new BigFraction(5),null,new BigFraction(7,2)},
                                    2,
                                    0
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target2,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(0),new BigFraction(3,2),new BigFraction(1),new BigFraction(0),new BigFraction(-1,2),new BigFraction(3,2)},
                                                {new BigFraction(0),new BigFraction(-1,2),new BigFraction(0),new BigFraction(1),new BigFraction(1,2),new BigFraction(3,2)},
                                                {new BigFraction(1),new BigFraction(1,2),new BigFraction(0),new BigFraction(0),new BigFraction(1,2),new BigFraction(7,2)},
                                                {new BigFraction(1),new BigFraction(1,2),new BigFraction(0),new BigFraction(0),new BigFraction(1,2),new BigFraction(7,2)},
                                                {new BigFraction(0),new BigFraction(1,2),new BigFraction(0),new BigFraction(0),new BigFraction(-1,2),new BigFraction(0)}
                                            },
                                            5
                                        ),
                                        List.of()
                                    ),
                                    new int[] {2,3,0},
                                    new BigFraction[] {new BigFraction(1),null,new BigFraction(7)},
                                    0,
                                    1
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target2,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(2,3),new BigFraction(0),new BigFraction(-1,3),new BigFraction(1)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(1,3),new BigFraction(1),new BigFraction(1,3),new BigFraction(2)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(-1,3),new BigFraction(0),new BigFraction(2,3),new BigFraction(3)},
                                                {new BigFraction(1),new BigFraction(1),new BigFraction(1,3),new BigFraction(0),new BigFraction(1,3),new BigFraction(4)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(-1,3),new BigFraction(0),new BigFraction(-1,3),new BigFraction(0)}
                                            },
                                            5
                                        ),
                                        List.of()
                                    ),
                                    new int[] {1,3,0},
                                    null,
                                    -1,
                                    -1
                                )
                            )
                        )
                    ),
                    SimplexAnswer.SOLVED
                )
            },
            {
                problem3,
                new SimplexSolution(
                    List.of(
                        new Pair<SimplexProblem, List<SimplexTableau>>(
                            problem3,
                            List.of(
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target2,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(-2),new BigFraction( 1),new BigFraction(1),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction( 1),new BigFraction(-2),new BigFraction(0),new BigFraction(1),new BigFraction(1)},
                                                {new BigFraction( 0),new BigFraction( 0),new BigFraction(0),new BigFraction(0),new BigFraction(0)},
                                                {new BigFraction( 1),new BigFraction( 1),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                            },
                                            4
                                        ),
                                        List.of()
                                    ),
                                    new int[] {2,3},
                                    new BigFraction[] {null,new BigFraction(1)},
                                    1,
                                    0
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target2,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(0),new BigFraction(-3),new BigFraction(1),new BigFraction( 2),new BigFraction(3)},
                                                {new BigFraction(1),new BigFraction(-2),new BigFraction(0),new BigFraction( 1),new BigFraction(1)},
                                                {new BigFraction(1),new BigFraction(-2),new BigFraction(0),new BigFraction( 1),new BigFraction(1)},
                                                {new BigFraction(0),new BigFraction( 3),new BigFraction(0),new BigFraction(-1),new BigFraction(0)}
                                            },
                                            4
                                        ),
                                        List.of()
                                    ),
                                    new int[] {2,0},
                                    new BigFraction[] {null,null},
                                    -1,
                                    1
                                )
                            )
                        )
                    ),
                    SimplexAnswer.UNBOUNDED
                )
            },
            {
                problem4,
                new SimplexSolution(
                    List.of(
                        new Pair<SimplexProblem, List<SimplexTableau>>(
                            problem4,
                            List.of(
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target2,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction( 1),new BigFraction(-1),new BigFraction(1),new BigFraction(0),new BigFraction(-1)},
                                                {new BigFraction(-1),new BigFraction( 2),new BigFraction(0),new BigFraction(1),new BigFraction(-1)},
                                                {new BigFraction( 0),new BigFraction( 0),new BigFraction(0),new BigFraction(0),new BigFraction(0)},
                                                {new BigFraction( 1),new BigFraction( 1),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                            },
                                            4
                                        ),
                                        List.of()
                                    ),
                                    new int[] {2,3},
                                    new BigFraction[] {null,null},
                                    -1,
                                    1
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target2,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(-1),new BigFraction(1),new BigFraction(-1),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction( 1),new BigFraction(0),new BigFraction( 2),new BigFraction(1),new BigFraction(-3)},
                                                {new BigFraction(-1),new BigFraction(1),new BigFraction(-1),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction( 2),new BigFraction(0),new BigFraction( 1),new BigFraction(0),new BigFraction(0)}
                                            },
                                            4
                                        ),
                                        List.of()
                                    ),
                                    new int[] {1,3},
                                    null,
                                    -1,
                                    -1
                                )
                            )
                        )
                    ),
                    SimplexAnswer.UNSOLVABLE
                )
            },
            {
                problem5,
                new SimplexSolution(
                    List.of(
                        new Pair<SimplexProblem, List<SimplexTableau>>(
                            new SimplexProblem(
                                target3,
                                new Matrix(
                                    new BigFraction[][] {
                                        {new BigFraction(1),new BigFraction(0),new BigFraction(1),new BigFraction(3)},
                                        {new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(4)},
                                        {new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(5)},
                                        {new BigFraction(2),new BigFraction(1),new BigFraction(3),new BigFraction(10)},
                                        {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(2)},
                                        {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(2)}
                                    },
                                    3
                                ),
                                List.of(0,1,2)
                            ),
                            List.of(
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(4)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(5)},
                                                {new BigFraction(2),new BigFraction(1),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(10)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)},
                                                {new BigFraction(1),new BigFraction(2),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                            },
                                            7
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {3,4,5,6},
                                    new BigFraction[] {new BigFraction(3),null,new BigFraction(5),new BigFraction(10,3)},
                                    0,
                                    2
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction( 1),new BigFraction(0),new BigFraction(1),new BigFraction( 1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction( 1),new BigFraction(1),new BigFraction(0),new BigFraction( 0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(4)},
                                                {new BigFraction(-1),new BigFraction(1),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(2)},
                                                {new BigFraction(-1),new BigFraction(1),new BigFraction(0),new BigFraction(-3),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(1)},
                                                {new BigFraction( 3),new BigFraction(0),new BigFraction(3),new BigFraction( 3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(9)},
                                                {new BigFraction(-2),new BigFraction(2),new BigFraction(0),new BigFraction(-3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                            },
                                            7
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {2,4,5,6},
                                    new BigFraction[] {null,new BigFraction(4),new BigFraction(2),new BigFraction(1)},
                                    3,
                                    1
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction( 1),new BigFraction(0),new BigFraction(1),new BigFraction( 1),new BigFraction(0),new BigFraction(0),new BigFraction( 0),new BigFraction(3)},
                                                {new BigFraction( 2),new BigFraction(0),new BigFraction(0),new BigFraction( 3),new BigFraction(1),new BigFraction(0),new BigFraction(-1),new BigFraction(3)},
                                                {new BigFraction( 0),new BigFraction(0),new BigFraction(0),new BigFraction( 2),new BigFraction(0),new BigFraction(1),new BigFraction(-1),new BigFraction(1)},
                                                {new BigFraction(-1),new BigFraction(1),new BigFraction(0),new BigFraction(-3),new BigFraction(0),new BigFraction(0),new BigFraction( 1),new BigFraction(1)},
                                                {new BigFraction( 1),new BigFraction(2),new BigFraction(3),new BigFraction(-3),new BigFraction(0),new BigFraction(0),new BigFraction( 2),new BigFraction(11)},
                                                {new BigFraction( 0),new BigFraction(0),new BigFraction(0),new BigFraction( 3),new BigFraction(0),new BigFraction(0),new BigFraction(-2),new BigFraction(0)}
                                            },
                                            7
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {2,4,5,1},
                                    new BigFraction[] {new BigFraction(3),new BigFraction(1),new BigFraction(1,2),null},
                                    2,
                                    3
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction( 1),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-1,2),new BigFraction( 1,2),new BigFraction(5,2)},
                                                {new BigFraction( 2),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-3,2),new BigFraction( 1,2),new BigFraction(3,2)},
                                                {new BigFraction( 0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction( 1,2),new BigFraction(-1,2),new BigFraction(1,2)},
                                                {new BigFraction(-1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction( 3,2),new BigFraction(-1,2),new BigFraction(5,2)},
                                                {new BigFraction( 1),new BigFraction(2),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction( 3,2),new BigFraction( 1,2),new BigFraction(25,2)},
                                                {new BigFraction( 0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-3,2),new BigFraction(-1,2),new BigFraction(0)}
                                            },
                                            7
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {2,4,3,1},
                                    null,
                                    -1,
                                    -1
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(4)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(5)},
                                                {new BigFraction(2),new BigFraction(1),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(10)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(2)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)},
                                                {new BigFraction(1),new BigFraction(2),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                            },
                                            8
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {3,4,5,6,7},
                                    new BigFraction[] {new BigFraction(3),null,new BigFraction(5),new BigFraction(10,3),null},
                                    0,
                                    2
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction( 1),new BigFraction(0),new BigFraction(1),new BigFraction( 1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction( 1),new BigFraction(1),new BigFraction(0),new BigFraction( 0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(4)},
                                                {new BigFraction(-1),new BigFraction(1),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(2)},
                                                {new BigFraction(-1),new BigFraction(1),new BigFraction(0),new BigFraction(-3),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction( 0),new BigFraction(1),new BigFraction(0),new BigFraction( 0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(2)},
                                                {new BigFraction( 3),new BigFraction(0),new BigFraction(3),new BigFraction( 3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(9)},
                                                {new BigFraction(-2),new BigFraction(2),new BigFraction(0),new BigFraction(-3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                            },
                                            8
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {2,4,5,6,7},
                                    new BigFraction[] {null,new BigFraction(4),new BigFraction(2),new BigFraction(1),new BigFraction(2)},
                                    3,
                                    1
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction( 1),new BigFraction(0),new BigFraction(1),new BigFraction( 1),new BigFraction(0),new BigFraction(0),new BigFraction( 0),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction( 2),new BigFraction(0),new BigFraction(0),new BigFraction( 3),new BigFraction(1),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction( 0),new BigFraction(0),new BigFraction(0),new BigFraction( 2),new BigFraction(0),new BigFraction(1),new BigFraction(-1),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction(-1),new BigFraction(1),new BigFraction(0),new BigFraction(-3),new BigFraction(0),new BigFraction(0),new BigFraction( 1),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction( 1),new BigFraction(0),new BigFraction(0),new BigFraction( 3),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(1),new BigFraction(1)},
                                                {new BigFraction( 1),new BigFraction(2),new BigFraction(3),new BigFraction(-3),new BigFraction(0),new BigFraction(0),new BigFraction( 2),new BigFraction(0),new BigFraction(11)},
                                                {new BigFraction( 0),new BigFraction(0),new BigFraction(0),new BigFraction( 3),new BigFraction(0),new BigFraction(0),new BigFraction(-2),new BigFraction(0),new BigFraction(0)}
                                            },
                                            8
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {2,4,5,1,7},
                                    new BigFraction[] {new BigFraction(3),new BigFraction(1),new BigFraction(1,2),null,new BigFraction(1,3)},
                                    4,
                                    3
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction( 2,3),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction( 1,3),new BigFraction(-1,3),new BigFraction(8,3)},
                                                {new BigFraction(   1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(   0),new BigFraction(  -1),new BigFraction(2)},
                                                {new BigFraction(-2,3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-1,3),new BigFraction(-2,3),new BigFraction(1,3)},
                                                {new BigFraction(   0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(   0),new BigFraction(   1),new BigFraction(2)},
                                                {new BigFraction( 1,3),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-1,3),new BigFraction( 1,3),new BigFraction(1,3)},
                                                {new BigFraction(   2),new BigFraction(2),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(   1),new BigFraction(   1),new BigFraction(12)},
                                                {new BigFraction(  -1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(  -1),new BigFraction(  -1),new BigFraction(0)}
                                            },
                                            8
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {2,4,5,1,3},
                                    null,
                                    -1,
                                    -1
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(4)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(5)},
                                                {new BigFraction(2),new BigFraction(1),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(10)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(2)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(2)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)},
                                                {new BigFraction(1),new BigFraction(2),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                            },
                                            9
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {3,4,5,6,7,8},
                                    new BigFraction[] {new BigFraction(3),null,new BigFraction(5),new BigFraction(10,3),null,new BigFraction(2)},
                                    5,
                                    2
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(1)},
                                                {new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(4)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(3)},
                                                {new BigFraction(2),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-3),new BigFraction(4)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(2)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(2)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(3),new BigFraction(6)},
                                                {new BigFraction(1),new BigFraction(2),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-3),new BigFraction(0)}
                                            },
                                            9
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {3,4,5,6,7,2},
                                    new BigFraction[] {null,new BigFraction(4),new BigFraction(3),new BigFraction(4),new BigFraction(2),null},
                                    4,
                                    1
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(1)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(2)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-1),new BigFraction(-1),new BigFraction(1)},
                                                {new BigFraction(2),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-1),new BigFraction(-3),new BigFraction(2)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(2)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(2)},
                                                {new BigFraction(0),new BigFraction(2),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(2),new BigFraction(3),new BigFraction(10)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-2),new BigFraction(-3),new BigFraction(0)}
                                            },
                                            9
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {3,4,5,6,1,2},
                                    new BigFraction[] {new BigFraction(1),new BigFraction(2),null,new BigFraction(1),null,null},
                                    0,
                                    0
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(1)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(1),new BigFraction(1)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-1),new BigFraction(-1),new BigFraction(1)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-2),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-1),new BigFraction(-1),new BigFraction(0)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(2)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(2)},
                                                {new BigFraction(1),new BigFraction(2),new BigFraction(3),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(2),new BigFraction(2),new BigFraction(11)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-2),new BigFraction(-2),new BigFraction(0)}
                                            },
                                            9
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {0,4,5,6,1,2},
                                    null,
                                    -1,
                                    -1
                                )
                            )
                        ),
                        new Pair<SimplexProblem, List<SimplexTableau>>(
                            new SimplexProblem(
                                target3,
                                new Matrix(
                                    new BigFraction[][] {
                                        {new BigFraction(1),new BigFraction( 0),new BigFraction(1),new BigFraction(3)},
                                        {new BigFraction(1),new BigFraction( 1),new BigFraction(0),new BigFraction(4)},
                                        {new BigFraction(0),new BigFraction( 1),new BigFraction(1),new BigFraction(5)},
                                        {new BigFraction(2),new BigFraction( 1),new BigFraction(3),new BigFraction(10)},
                                        {new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(-3)},
                                        {new BigFraction(1),new BigFraction( 0),new BigFraction(0),new BigFraction(0)}
                                    },
                                    3
                                ),
                                List.of(0,1,2)
                            ),
                            List.of(
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(1),new BigFraction( 0),new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction(1),new BigFraction( 1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(4)},
                                                {new BigFraction(0),new BigFraction( 1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(5)},
                                                {new BigFraction(2),new BigFraction( 1),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(10)},
                                                {new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-3)},
                                                {new BigFraction(0),new BigFraction( 0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)},
                                                {new BigFraction(1),new BigFraction( 2),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                            },
                                            8
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {3,4,5,6,7},
                                    new BigFraction[] {null,new BigFraction(4),new BigFraction(5),new BigFraction(10),null},
                                    1,
                                    1
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(4)},
                                                {new BigFraction(-1),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(3),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(6)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(1)},
                                                {new BigFraction(2),new BigFraction(2),new BigFraction(0),new BigFraction(0),new BigFraction(2),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(8)},
                                                {new BigFraction(-1),new BigFraction(0),new BigFraction(3),new BigFraction(0),new BigFraction(-2),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                            },
                                            8
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {3,1,5,6,7},
                                    new BigFraction[] {new BigFraction(3),null,new BigFraction(1),new BigFraction(2),null},
                                    2,
                                    2
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(2),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(2)},
                                                {new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(4)},
                                                {new BigFraction(-1),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction(4),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(2),new BigFraction(-3),new BigFraction(1),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(1)},
                                                {new BigFraction(-1),new BigFraction(2),new BigFraction(3),new BigFraction(0),new BigFraction(-1),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(11)},
                                                {new BigFraction(2),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-3),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                            },
                                            8
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {3,1,2,6,7},
                                    new BigFraction[] {new BigFraction(1),new BigFraction(4),null,new BigFraction(3,4),new BigFraction(1)},
                                    3,
                                    0
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(1,2),new BigFraction(-1,2),new BigFraction(0),new BigFraction(1,2)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1,2),new BigFraction(3,4),new BigFraction(-1,4),new BigFraction(0),new BigFraction(13,4)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-1,2),new BigFraction(1,4),new BigFraction(1,4),new BigFraction(0),new BigFraction(7,4)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1,2),new BigFraction(-3,4),new BigFraction(1,4),new BigFraction(0),new BigFraction(3,4)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1,2),new BigFraction(3,4),new BigFraction(-1,4),new BigFraction(1),new BigFraction(1,4)},
                                                {new BigFraction(1),new BigFraction(2),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(3,2),new BigFraction(1,2),new BigFraction(0),new BigFraction(25,2)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-3,2),new BigFraction(-1,2),new BigFraction(0),new BigFraction(0)}
                                            },
                                            8
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {3,1,2,0,7},
                                    null,
                                    -1,
                                    -1
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(4)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(5)},
                                                {new BigFraction(2),new BigFraction(1),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(10)},
                                                {new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-3)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)},
                                                {new BigFraction(1),new BigFraction(2),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                            },
                                            9
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {3,4,5,6,7,8},
                                    new BigFraction[] {null,new BigFraction(4),new BigFraction(5),new BigFraction(10),null,null},
                                    1,
                                    1
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(4)},
                                                {new BigFraction(-1),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(3),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(6)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0)},
                                                {new BigFraction(2),new BigFraction(2),new BigFraction(0),new BigFraction(0),new BigFraction(2),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(8)},
                                                {new BigFraction(-1),new BigFraction(0),new BigFraction(3),new BigFraction(0),new BigFraction(-2),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                            },
                                            9
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {3,1,5,6,7,8},
                                    new BigFraction[] {new BigFraction(3),null,new BigFraction(1),new BigFraction(2),null,null},
                                    2,
                                    2
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(2),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(2)},
                                                {new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(4)},
                                                {new BigFraction(-1),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction(4),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(2),new BigFraction(-3),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0)},
                                                {new BigFraction(-1),new BigFraction(2),new BigFraction(3),new BigFraction(0),new BigFraction(-1),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(11)},
                                                {new BigFraction(2),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                            },
                                            9
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {3,1,2,6,7,8},
                                    new BigFraction[] {new BigFraction(1),new BigFraction(4),null,new BigFraction(3,4),new BigFraction(1),new BigFraction(0)},
                                    5,
                                    0
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(-2),new BigFraction(2)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(4)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(1)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(2),new BigFraction(-3),new BigFraction(1),new BigFraction(0),new BigFraction(-4),new BigFraction(3)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-1),new BigFraction(1)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0)},
                                                {new BigFraction(1),new BigFraction(2),new BigFraction(3),new BigFraction(0),new BigFraction(-1),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(2),new BigFraction(11)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-3),new BigFraction(0),new BigFraction(0),new BigFraction(-2),new BigFraction(0)}
                                            },
                                            9
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {3,1,2,6,7,0},
                                    new BigFraction[] {new BigFraction(2),new BigFraction(4),null,new BigFraction(3,2),new BigFraction(1),null},
                                    4,
                                    4
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(-1),new BigFraction(-1),new BigFraction(1)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(2)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-3),new BigFraction(1),new BigFraction(-2),new BigFraction(-2),new BigFraction(1)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-1),new BigFraction(1)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0)},
                                                {new BigFraction(1),new BigFraction(2),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(3),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(12)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-3),new BigFraction(0),new BigFraction(-1),new BigFraction(-1),new BigFraction(0)}
                                            },
                                            9
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {3,1,2,6,4,0},
                                    null,
                                    -1,
                                    -1
                                )
                            )
                        ),
                        new Pair<SimplexProblem, List<SimplexTableau>>(
                            new SimplexProblem(
                                target3,
                                new Matrix(
                                    new BigFraction[][] {
                                        {new BigFraction(1),new BigFraction(0),new BigFraction( 1),new BigFraction(3)},
                                        {new BigFraction(1),new BigFraction(1),new BigFraction( 0),new BigFraction(4)},
                                        {new BigFraction(0),new BigFraction(1),new BigFraction( 1),new BigFraction(5)},
                                        {new BigFraction(2),new BigFraction(1),new BigFraction( 3),new BigFraction(10)},
                                        {new BigFraction(0),new BigFraction(1),new BigFraction( 0),new BigFraction(2)},
                                        {new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(-3)}
                                    },
                                    3
                                ),
                                List.of(0,1,2)
                            ),
                            List.of(
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(4)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(5)},
                                                {new BigFraction(2),new BigFraction(1),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(10)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(2)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-3)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)},
                                                {new BigFraction(1),new BigFraction(2),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                            },
                                            9
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {3,4,5,6,7,8},
                                    new BigFraction[] {new BigFraction(3),null,new BigFraction(5),new BigFraction(10,3),null,null},
                                    0,
                                    2
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(4)},
                                                {new BigFraction(-1),new BigFraction(1),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(2)},
                                                {new BigFraction(-1),new BigFraction(1),new BigFraction(0),new BigFraction(-3),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(2)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0)},
                                                {new BigFraction(3),new BigFraction(0),new BigFraction(3),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(9)},
                                                {new BigFraction(-2),new BigFraction(2),new BigFraction(0),new BigFraction(-3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                            },
                                            9
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {2,4,5,6,7,8},
                                    new BigFraction[] {null,new BigFraction(4),new BigFraction(2),new BigFraction(1),new BigFraction(2),null},
                                    3,
                                    1
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction(2),new BigFraction(0),new BigFraction(0),new BigFraction(3),new BigFraction(1),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(2),new BigFraction(0),new BigFraction(1),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction(-1),new BigFraction(1),new BigFraction(0),new BigFraction(-3),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(1),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0)},
                                                {new BigFraction(1),new BigFraction(2),new BigFraction(3),new BigFraction(-3),new BigFraction(0),new BigFraction(0),new BigFraction(2),new BigFraction(0),new BigFraction(0),new BigFraction(11)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(-2),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                            },
                                            9
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {2,4,5,1,7,8},
                                    new BigFraction[] {new BigFraction(3),new BigFraction(1),new BigFraction(1,2),null,new BigFraction(1,3),new BigFraction(0)},
                                    5,
                                    3
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(3)},
                                                {new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(-3),new BigFraction(3)},
                                                {new BigFraction(-2),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-1),new BigFraction(0),new BigFraction(-2),new BigFraction(1)},
                                                {new BigFraction(2),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(3),new BigFraction(1)},
                                                {new BigFraction(-2),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(1),new BigFraction(-3),new BigFraction(1)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0)},
                                                {new BigFraction(4),new BigFraction(2),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(2),new BigFraction(0),new BigFraction(3),new BigFraction(11)},
                                                {new BigFraction(-3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-2),new BigFraction(0),new BigFraction(-3),new BigFraction(0)}
                                            },
                                            9
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {2,4,5,1,7,3},
                                    null,
                                    -1,
                                    -1
                                )
                            )
                        ),
                        new Pair<SimplexProblem, List<SimplexTableau>>(
                            new SimplexProblem(
                                target3,
                                new Matrix(
                                    new BigFraction[][] {
                                        {new BigFraction( 1),new BigFraction( 0),new BigFraction(1),new BigFraction(3)},
                                        {new BigFraction( 1),new BigFraction( 1),new BigFraction(0),new BigFraction(4)},
                                        {new BigFraction( 0),new BigFraction( 1),new BigFraction(1),new BigFraction(5)},
                                        {new BigFraction( 2),new BigFraction( 1),new BigFraction(3),new BigFraction(10)},
                                        {new BigFraction( 0),new BigFraction(-1),new BigFraction(0),new BigFraction(-3)},
                                        {new BigFraction(-1),new BigFraction( 0),new BigFraction(0),new BigFraction(-1)},
                                        {new BigFraction( 0),new BigFraction( 0),new BigFraction(1),new BigFraction(1)}
                                    },
                                    3
                                ),
                                List.of(0,1,2)
                            ),
                            List.of(
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(4)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(5)},
                                                {new BigFraction(2),new BigFraction(1),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(10)},
                                                {new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-3)},
                                                {new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-1)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)},
                                                {new BigFraction(1),new BigFraction(2),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                            },
                                            9
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {3,4,5,6,7,8},
                                    new BigFraction[] {null,new BigFraction(4),new BigFraction(5),new BigFraction(10),null,null},
                                    1,
                                    1
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(4)},
                                                {new BigFraction(-1),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(3),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(6)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-1)},
                                                {new BigFraction(2),new BigFraction(2),new BigFraction(0),new BigFraction(0),new BigFraction(2),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(8)},
                                                {new BigFraction(-1),new BigFraction(0),new BigFraction(3),new BigFraction(0),new BigFraction(-2),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                            },
                                            9
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {3,1,5,6,7,8},
                                    new BigFraction[] {new BigFraction(3),new BigFraction(4),null,new BigFraction(6),new BigFraction(1),null},
                                    4,
                                    0
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(2)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(2)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(3),new BigFraction(0),new BigFraction(-2),new BigFraction(0),new BigFraction(1),new BigFraction(-1),new BigFraction(0),new BigFraction(5)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0)},
                                                {new BigFraction(1),new BigFraction(2),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(7)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(3),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0)}
                                            },
                                            9
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {3,1,5,6,0,8},
                                    new BigFraction[] {new BigFraction(2),null,new BigFraction(2),new BigFraction(5,3),null,null},
                                    3,
                                    2
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-1,3),new BigFraction(0),new BigFraction(-1,3),new BigFraction(-2,3),new BigFraction(0),new BigFraction(1,3)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(2,3),new BigFraction(1),new BigFraction(-1,3),new BigFraction(4,3),new BigFraction(0),new BigFraction(1,3)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-2,3),new BigFraction(0),new BigFraction(1,3),new BigFraction(-1,3),new BigFraction(0),new BigFraction(5,3)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0)},
                                                {new BigFraction(1),new BigFraction(2),new BigFraction(3),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(1),new BigFraction(-2),new BigFraction(0),new BigFraction(12)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-1),new BigFraction(2),new BigFraction(0),new BigFraction(0)}
                                            },
                                            9
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {3,1,5,2,0,8},
                                    new BigFraction[] {null,null,new BigFraction(1,4),null,new BigFraction(1),new BigFraction(0)},
                                    5,
                                    7
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(1,3),new BigFraction(0),new BigFraction(-1,3),new BigFraction(0),new BigFraction(2,3),new BigFraction(1,3)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(3)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-2,3),new BigFraction(1),new BigFraction(-1,3),new BigFraction(0),new BigFraction(-4,3),new BigFraction(1,3)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-1,3),new BigFraction(0),new BigFraction(1,3),new BigFraction(0),new BigFraction(1,3),new BigFraction(5,3)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(1)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0)},
                                                {new BigFraction(1),new BigFraction(2),new BigFraction(3),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(2),new BigFraction(12)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(-2),new BigFraction(0)}
                                            },
                                            9
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {3,1,5,2,0,7},
                                    null,
                                    -1,
                                    -1
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(4)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(5)},
                                                {new BigFraction(2),new BigFraction(1),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(10)},
                                                {new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-3)},
                                                {new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-1)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(1)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)},
                                                {new BigFraction(1),new BigFraction(2),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                            },
                                            10
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {3,4,5,6,7,8,9},
                                    new BigFraction[] {null,new BigFraction(4),new BigFraction(5),new BigFraction(10),null,null,null},
                                    1,
                                    1
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(4)},
                                                {new BigFraction(-1),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(3),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(6)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-1)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(1)},
                                                {new BigFraction(2),new BigFraction(2),new BigFraction(0),new BigFraction(0),new BigFraction(2),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(8)},
                                                {new BigFraction(-1),new BigFraction(0),new BigFraction(3),new BigFraction(0),new BigFraction(-2),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                            },
                                            10
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {3,1,5,6,7,8,9},
                                    new BigFraction[] {new BigFraction(3),new BigFraction(4),null,new BigFraction(6),new BigFraction(1),null,null},
                                    4,
                                    0
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(2)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(2)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(3),new BigFraction(0),new BigFraction(-2),new BigFraction(0),new BigFraction(1),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(5)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(1)},
                                                {new BigFraction(1),new BigFraction(2),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(7)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(3),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                            },
                                            10
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {3,1,5,6,0,8,9},
                                    new BigFraction[] {new BigFraction(2),null,new BigFraction(2),new BigFraction(5,3),null,null,new BigFraction(1)},
                                    6,
                                    2
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(-1),new BigFraction(1)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-1),new BigFraction(1)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-2),new BigFraction(0),new BigFraction(1),new BigFraction(-1),new BigFraction(0),new BigFraction(-3),new BigFraction(2)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(1)},
                                                {new BigFraction(1),new BigFraction(2),new BigFraction(3),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(3),new BigFraction(10)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-3),new BigFraction(0)}
                                            },
                                            10
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {3,1,5,6,0,8,2},
                                    new BigFraction[] {null,null,new BigFraction(1),null,new BigFraction(1),new BigFraction(0),null},
                                    5,
                                    7
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-1),new BigFraction(1)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(-1),new BigFraction(1)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(1),new BigFraction(-3),new BigFraction(2)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(1)},
                                                {new BigFraction(1),new BigFraction(2),new BigFraction(3),new BigFraction(0),new BigFraction(2),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(3),new BigFraction(10)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-2),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(-3),new BigFraction(0)}
                                            },
                                            10
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {3,1,5,6,0,7,2},
                                    null,
                                    -1,
                                    -1
                                )
                            )
                        ),
                        new Pair<SimplexProblem, List<SimplexTableau>>(
                            new SimplexProblem(
                                target3,
                                new Matrix(
                                    new BigFraction[][] {
                                        {new BigFraction( 1),new BigFraction( 0),new BigFraction( 1),new BigFraction(3)},
                                        {new BigFraction( 1),new BigFraction( 1),new BigFraction( 0),new BigFraction(4)},
                                        {new BigFraction( 0),new BigFraction( 1),new BigFraction( 1),new BigFraction(5)},
                                        {new BigFraction( 2),new BigFraction( 1),new BigFraction( 3),new BigFraction(10)},
                                        {new BigFraction( 0),new BigFraction(-1),new BigFraction( 0),new BigFraction(-3)},
                                        {new BigFraction(-1),new BigFraction( 0),new BigFraction( 0),new BigFraction(-1)},
                                        {new BigFraction( 0),new BigFraction( 0),new BigFraction(-1),new BigFraction(-2)}
                                    },
                                    3
                                ),
                                List.of(0,1,2)
                            ),
                            List.of(
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(4)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(5)},
                                                {new BigFraction(2),new BigFraction(1),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(10)},
                                                {new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-3)},
                                                {new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-1)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-2)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)},
                                                {new BigFraction(1),new BigFraction(2),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                            },
                                            10
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {3,4,5,6,7,8,9},
                                    new BigFraction[] {null,new BigFraction(4),new BigFraction(5),new BigFraction(10),null,null,null},
                                    1,
                                    1
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(4)},
                                                {new BigFraction(-1),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(3),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(6)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-1)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-2)},
                                                {new BigFraction(2),new BigFraction(2),new BigFraction(0),new BigFraction(0),new BigFraction(2),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(8)},
                                                {new BigFraction(-1),new BigFraction(0),new BigFraction(3),new BigFraction(0),new BigFraction(-2),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                            },
                                            10
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {3,1,5,6,7,8,9},
                                    new BigFraction[] {new BigFraction(3),new BigFraction(4),null,new BigFraction(6),new BigFraction(1),null,null},
                                    4,
                                    0
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(2)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(2)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(3),new BigFraction(0),new BigFraction(-2),new BigFraction(0),new BigFraction(1),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(5)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-2)},
                                                {new BigFraction(1),new BigFraction(2),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(7)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(3),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                            },
                                            10
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {3,1,5,6,0,8,9},
                                    new BigFraction[] {new BigFraction(2),null,new BigFraction(2),new BigFraction(5,3),null,null,null},
                                    3,
                                    2
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-1,3),new BigFraction(0),new BigFraction(-1,3),new BigFraction(-2,3),new BigFraction(0),new BigFraction(0),new BigFraction(1,3)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(2,3),new BigFraction(1),new BigFraction(-1,3),new BigFraction(4,3),new BigFraction(0),new BigFraction(0),new BigFraction(1,3)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-2,3),new BigFraction(0),new BigFraction(1,3),new BigFraction(-1,3),new BigFraction(0),new BigFraction(0),new BigFraction(5,3)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-2,3),new BigFraction(0),new BigFraction(1,3),new BigFraction(-1,3),new BigFraction(0),new BigFraction(1),new BigFraction(-1,3)},
                                                {new BigFraction(1),new BigFraction(2),new BigFraction(3),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(1),new BigFraction(-2),new BigFraction(0),new BigFraction(0),new BigFraction(12)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-1),new BigFraction(2),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                            },
                                            10
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {3,1,5,2,0,8,9},
                                    new BigFraction[] {null,null,new BigFraction(1,2),null,new BigFraction(1),new BigFraction(0),null},
                                    5,
                                    4
                                ),
                                new SimplexTableau(
                                    new SimplexProblem(
                                        target3,
                                        new Matrix(
                                            new BigFraction[][] {
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-1,3),new BigFraction(-1,3),new BigFraction(1,3),new BigFraction(0),new BigFraction(1,3)},
                                                {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-1,3),new BigFraction(2,3),new BigFraction(-2,3),new BigFraction(0),new BigFraction(1,3)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1,3),new BigFraction(1,3),new BigFraction(2,3),new BigFraction(0),new BigFraction(5,3)},
                                                {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(0),new BigFraction(1)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1,3),new BigFraction(1,3),new BigFraction(2,3),new BigFraction(1),new BigFraction(-1,3)},
                                                {new BigFraction(1),new BigFraction(2),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-1),new BigFraction(1),new BigFraction(0),new BigFraction(12)},
                                                {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(1),new BigFraction(-1),new BigFraction(0),new BigFraction(0)}
                                            },
                                            10
                                        ),
                                        List.of(0,1,2)
                                    ),
                                    new int[] {3,1,5,2,0,4,9},
                                    null,
                                    -1,
                                    -1
                                )
                            )
                        )
                    ),
                    SimplexAnswer.SOLVED
                )
            }
        };
    }

    @Test(dataProvider="simplexData")
    public void simplexTest(final SimplexProblem problem, final SimplexSolution expected) {
        final SimplexSolution result = SimplexAlgorithm.INSTANCE.apply(problem);
        Assert.assertEquals(result, expected);
    }

}
