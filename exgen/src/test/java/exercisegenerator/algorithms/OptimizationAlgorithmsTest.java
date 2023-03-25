package exercisegenerator.algorithms;

import java.util.*;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.structures.optimization.*;

public class OptimizationAlgorithmsTest {

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
        final int[][] result = OptimizationAlgorithms.knapsack(problem);
        Assert.assertTrue(
            Arrays.deepEquals(result, expected),
            String.format("\nExpected: %s\nActual:   %s\n", Arrays.deepToString(expected), Arrays.deepToString(result))
        );
    }

    @DataProvider
    public Object[][] simplexData() {
        final double[] target1 = new double[] {2, 3};
        final double[] target2 = new double[] {1, 1};
        return new Object[][] {
            {
                new SimplexProblem(
                    target1,
                    new double[][] {
                        {1,0,3.6},
                        {1,2,5},
                        {1,1,4},
                        {2,1,7.5},
                        {0,1,2}
                    }
                ),
                new SimplexSolution(
                    List.of(
                        new SimplexTableau(
                            new SimplexProblem(
                                target1,
                                new double[][] {
                                    {1,0,1,0,0,0,0,3.6},
                                    {1,2,0,1,0,0,0,5},
                                    {1,1,0,0,1,0,0,4},
                                    {2,1,0,0,0,1,0,7.5},
                                    {0,1,0,0,0,0,1,2},
                                    {0,0,0,0,0,0,0,0},
                                    {2,3,0,0,0,0,0,0}
                                }
                            ),
                            new int[] {2,3,4,5,6},
                            new Double[] {null,2.5,4.0,7.5,2.0},
                            4,
                            1
                        ),
                        new SimplexTableau(
                            new SimplexProblem(
                                target1,
                                new double[][] {
                                    {1,0,1,0,0,0,0,3.6},
                                    {1,0,0,1,0,0,-2,1},
                                    {1,0,0,0,1,0,-1,2},
                                    {2,0,0,0,0,1,-1,5.5},
                                    {0,1,0,0,0,0,1,2},
                                    {0,3,0,0,0,0,3,6},
                                    {2,0,0,0,0,0,-3,0}
                                }
                            ),
                            new int[] {2,3,4,5,1},
                            new Double[] {3.6,1.0,2.0,2.75,null},
                            1,
                            0
                        ),
                        new SimplexTableau(
                            new SimplexProblem(
                                target1,
                                new double[][] {
                                    {0,0,1,-1,0,0, 2,2.6},
                                    {1,0,0, 1,0,0,-2,1},
                                    {0,0,0,-1,1,0, 1,1},
                                    {0,0,0,-2,0,1, 3,3.5},
                                    {0,1,0, 0,0,0, 1,2},
                                    {2,3,0, 2,0,0,-1,8},
                                    {0,0,0,-2,0,0, 1,0}
                                }
                            ),
                            new int[] {2,0,4,5,1},
                            new Double[] {1.3,null,1.0,3.5/3,2.0},
                            2,
                            6
                        ),
                        new SimplexTableau(
                            new SimplexProblem(
                                target1,
                                new double[][] {
                                    {0,0,1, 1,-2,0,0,0.6},
                                    {1,0,0,-1, 2,0,0,3},
                                    {0,0,0,-1, 1,0,1,1},
                                    {0,0,0, 1,-3,1,0,0.5},
                                    {0,1,0, 1,-1,0,0,1},
                                    {2,3,0, 1, 1,0,0,9},
                                    {0,0,0,-1,-1,0,0,0}
                                }
                            ),
                            new int[] {2,0,6,5,1},
                            null,
                            -1,
                            -1
                        )
                    ),
                    SimplexAnswer.SOLVED
                )
            },
            {
                new SimplexProblem(
                    target2,
                    new double[][] {
                        {1,2,5},
                        {-1,-1,-2},
                        {2,1,7}
                    }
                ),
                new SimplexSolution(
                    List.of(
                        new SimplexTableau(
                            new SimplexProblem(
                                target2,
                                new double[][] {
                                    {1,2,1,0,0,5},
                                    {-1,-1,0,1,0,-2},
                                    {2,1,0,0,1,7},
                                    {0,0,0,0,0,0},
                                    {1,1,0,0,0,0}
                                }
                            ),
                            new int[] {2,3,4},
                            new Double[] {5.0,null,3.5},
                            2,
                            0
                        ),
                        new SimplexTableau(
                            new SimplexProblem(
                                target2,
                                new double[][] {
                                    {0, 1.5,1,0,-0.5,1.5},
                                    {0,-0.5,0,1, 0.5,1.5},
                                    {1, 0.5,0,0, 0.5,3.5},
                                    {1, 0.5,0,0, 0.5,3.5},
                                    {0, 0.5,0,0,-0.5,0}
                                }
                            ),
                            new int[] {2,3,0},
                            new Double[] {1.0,null,7.0},
                            0,
                            1
                        ),
                        new SimplexTableau(
                            new SimplexProblem(
                                target2,
                                new double[][] {
                                    {0,1, 2.0/3.0,0,-1.0/3.0,1},
                                    {0,0, 1.0/3.0,1, 1.0/3.0,2},
                                    {1,0,-1.0/3.0,0, 2.0/3.0,3},
                                    {1,1, 1.0/3.0,0, 1.0/3.0,4},
                                    {0,0,-1.0/3.0,0,-1.0/3.0,0}
                                }
                            ),
                            new int[] {1,3,0},
                            null,
                            -1,
                            -1
                        )
                    ),
                    SimplexAnswer.SOLVED
                )
            },
            {
                new SimplexProblem(
                    target2,
                    new double[][] {
                        {-2,1,1},
                        {1,-2,1}
                    }
                ),
                new SimplexSolution(
                    List.of(
                        new SimplexTableau(
                            new SimplexProblem(
                                target2,
                                new double[][] {
                                    {-2, 1,1,0,1},
                                    { 1,-2,0,1,1},
                                    { 0, 0,0,0,0},
                                    { 1, 1,0,0,0}
                                }
                            ),
                            new int[] {2,3},
                            new Double[] {null,1.0},
                            1,
                            0
                        ),
                        new SimplexTableau(
                            new SimplexProblem(
                                target2,
                                new double[][] {
                                    {0,-3,1, 2,3},
                                    {1,-2,0, 1,1},
                                    {1,-2,0, 1,1},
                                    {0, 3,0,-1,0}
                                }
                            ),
                            new int[] {2,0},
                            new Double[] {null,null},
                            -1,
                            1
                        )
                    ),
                    SimplexAnswer.UNBOUNDED
                )
            },
            {
                new SimplexProblem(
                    target2,
                    new double[][] {
                        {1,-1,-1},
                        {-1,2,-1}
                    }
                ),
                new SimplexSolution(
                    List.of(
                        new SimplexTableau(
                            new SimplexProblem(
                                target2,
                                new double[][] {
                                    { 1,-1,1,0,-1},
                                    {-1, 2,0,1,-1},
                                    { 0, 0,0,0,0},
                                    { 1, 1,0,0,0}
                                }
                            ),
                            new int[] {2,3},
                            new Double[] {null,null},
                            -1,
                            1
                        ),
                        new SimplexTableau(
                            new SimplexProblem(
                                target2,
                                new double[][] {
                                    {-1,1,-1,0,1},
                                    { 1,0, 2,1,-3},
                                    {-1,1,-1,0,1},
                                    { 2,0, 1,0,0}
                                }
                            ),
                            new int[] {1,3},
                            null,
                            -1,
                            -1
                        )
                    ),
                    SimplexAnswer.UNSOLVABLE
                )
            }
        };
    }

    @Test(dataProvider="simplexData")
    public void simplexTest(final SimplexProblem problem, final SimplexSolution expected) {
        final SimplexSolution result = OptimizationAlgorithms.simplex(problem);
        Assert.assertEquals(result.answer, expected.answer);
        Assert.assertEquals(result.tableaus.size(), expected.tableaus.size());
        final Iterator<SimplexTableau> resultIterator = result.tableaus.iterator();
        final Iterator<SimplexTableau> expectedIterator = expected.tableaus.iterator();
        int tableauNr = 0;
        while (resultIterator.hasNext()) {
            final SimplexTableau resultTableau = resultIterator.next();
            final SimplexTableau expectedTableau = expectedIterator.next();
            tableauNr++;
            final String msg = "Fehler bei Tableau Nr. " + tableauNr;
            Assert.assertEquals(resultTableau.pivotColumn, expectedTableau.pivotColumn, msg);
            Assert.assertEquals(resultTableau.pivotRow, expectedTableau.pivotRow, msg);
            Assert.assertEquals(resultTableau.basicVariables, expectedTableau.basicVariables, msg);
            if (expectedTableau.quotients == null) {
                Assert.assertNull(resultTableau.quotients, msg);
            } else {
                Assert.assertEquals(resultTableau.quotients.length, expectedTableau.quotients.length, msg);
                for (int i = 0; i < resultTableau.quotients.length; i++) {
                    if (expectedTableau.quotients[i] == null) {
                        Assert.assertNull(resultTableau.quotients[i], msg);
                    } else {
                        Assert.assertEquals(resultTableau.quotients[i], expectedTableau.quotients[i], 0.015625, msg);
                    }
                }
            }
            Assert.assertEquals(resultTableau.problem.target, expectedTableau.problem.target, 0.015625, msg);
            Assert.assertEquals(resultTableau.problem.matrix.length, expectedTableau.problem.matrix.length, msg);
            for (int i = 0; i < resultTableau.problem.matrix.length; i++) {
                Assert.assertEquals(
                    resultTableau.problem.matrix[i],
                    expectedTableau.problem.matrix[i],
                    0.015625,
                    msg + ", Zeile " + i
                );
            }
        }
    }

}
