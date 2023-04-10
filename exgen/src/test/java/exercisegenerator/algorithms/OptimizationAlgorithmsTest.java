package exercisegenerator.algorithms;

import java.util.*;

import org.apache.commons.math3.fraction.*;
import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.structures.optimization.*;

public class OptimizationAlgorithmsTest {

    @DataProvider
    public Object[][] gaussJordanData() {
        final LinearSystemOfEquations problem1 =
            new LinearSystemOfEquations(
                new int[][] {
                    {3,5,4,2},
                    {2,1,-1,1},
                    {7,1,1,2}
                }
            );
        return new Object[][] {
            {
                problem1,
                List.of(
                    problem1,
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(5,3),new Fraction(4,3),new Fraction(2,3)},
                            {new Fraction(2),new Fraction(1),new Fraction(-1),new Fraction(1)},
                            {new Fraction(7),new Fraction(1),new Fraction(1),new Fraction(2)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(5,3),new Fraction(4,3),new Fraction(2,3)},
                            {new Fraction(0),new Fraction(-7,3),new Fraction(-11,3),new Fraction(-1,3)},
                            {new Fraction(7),new Fraction(1),new Fraction(1),new Fraction(2)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(5,3),new Fraction(4,3),new Fraction(2,3)},
                            {new Fraction(0),new Fraction(-7,3),new Fraction(-11,3),new Fraction(-1,3)},
                            {new Fraction(0),new Fraction(-32,3),new Fraction(-25,3),new Fraction(-8,3)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(5,3),new Fraction(4,3),new Fraction(2,3)},
                            {new Fraction(0),new Fraction(1),new Fraction(11,7),new Fraction(1,7)},
                            {new Fraction(0),new Fraction(-32,3),new Fraction(-25,3),new Fraction(-8,3)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(0),new Fraction(-9,7),new Fraction(3,7)},
                            {new Fraction(0),new Fraction(1),new Fraction(11,7),new Fraction(1,7)},
                            {new Fraction(0),new Fraction(-32,3),new Fraction(-25,3),new Fraction(-8,3)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(0),new Fraction(-9,7),new Fraction(3,7)},
                            {new Fraction(0),new Fraction(1),new Fraction(11,7),new Fraction(1,7)},
                            {new Fraction(0),new Fraction(0),new Fraction(59,7),new Fraction(-8,7)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(0),new Fraction(-9,7),new Fraction(3,7)},
                            {new Fraction(0),new Fraction(1),new Fraction(11,7),new Fraction(1,7)},
                            {new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(-8,59)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(105,413)},
                            {new Fraction(0),new Fraction(1),new Fraction(11,7),new Fraction(1,7)},
                            {new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(-8,59)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(105,413)},
                            {new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(147,413)},
                            {new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(-8,59)}
                        }
                    )
                )
            }
        };
    }

    @Test(dataProvider="gaussJordanData")
    public void gaussJordanTest(final LinearSystemOfEquations problem, final List<LinearSystemOfEquations> expected) {
        final List<LinearSystemOfEquations> result = OptimizationAlgorithms.gaussJordan(problem);
        Assert.assertEquals(result, expected);
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
        final int[][] result = OptimizationAlgorithms.knapsack(problem);
        Assert.assertTrue(
            Arrays.deepEquals(result, expected),
            String.format("\nExpected: %s\nActual:   %s\n", Arrays.deepToString(expected), Arrays.deepToString(result))
        );
    }

    @DataProvider
    public Object[][] simplexData() {
        final Fraction[] target1 = new Fraction[] {new Fraction(2), new Fraction(3)};
        final Fraction[] target2 = new Fraction[] {new Fraction(1), new Fraction(1)};
        return new Object[][] {
            {
                new SimplexProblem(
                    target1,
                    new Fraction[][] {
                        {new Fraction(1),new Fraction(0),new Fraction(18,5)},
                        {new Fraction(1),new Fraction(2),new Fraction(5)},
                        {new Fraction(1),new Fraction(1),new Fraction(4)},
                        {new Fraction(2),new Fraction(1),new Fraction(15,2)},
                        {new Fraction(0),new Fraction(1),new Fraction(2)}
                    }
                ),
                new SimplexSolution(
                    List.of(
                        new SimplexTableau(
                            new SimplexProblem(
                                target1,
                                new Fraction[][] {
                                    {new Fraction(1),new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(18,5)},
                                    {new Fraction(1),new Fraction(2),new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(5)},
                                    {new Fraction(1),new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(4)},
                                    {new Fraction(2),new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(15,2)},
                                    {new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(2)},
                                    {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0)},
                                    {new Fraction(2),new Fraction(3),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0)}
                                }
                            ),
                            new int[] {2,3,4,5,6},
                            new Fraction[] {null,new Fraction(5,2),new Fraction(4),new Fraction(15,2),new Fraction(2)},
                            4,
                            1
                        ),
                        new SimplexTableau(
                            new SimplexProblem(
                                target1,
                                new Fraction[][] {
                                    {new Fraction(1),new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(18,5)},
                                    {new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(-2),new Fraction(1)},
                                    {new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(-1),new Fraction(2)},
                                    {new Fraction(2),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(-1),new Fraction(11,2)},
                                    {new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(2)},
                                    {new Fraction(0),new Fraction(3),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(3),new Fraction(6)},
                                    {new Fraction(2),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(-3),new Fraction(0)}
                                }
                            ),
                            new int[] {2,3,4,5,1},
                            new Fraction[] {new Fraction(18,5),new Fraction(1),new Fraction(2),new Fraction(11,4),null},
                            1,
                            0
                        ),
                        new SimplexTableau(
                            new SimplexProblem(
                                target1,
                                new Fraction[][] {
                                    {new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(-1),new Fraction(0),new Fraction(0),new Fraction( 2),new Fraction(13,5)},
                                    {new Fraction(1),new Fraction(0),new Fraction(0),new Fraction( 1),new Fraction(0),new Fraction(0),new Fraction(-2),new Fraction(1)},
                                    {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(-1),new Fraction(1),new Fraction(0),new Fraction( 1),new Fraction(1)},
                                    {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(-2),new Fraction(0),new Fraction(1),new Fraction( 3),new Fraction(7,2)},
                                    {new Fraction(0),new Fraction(1),new Fraction(0),new Fraction( 0),new Fraction(0),new Fraction(0),new Fraction( 1),new Fraction(2)},
                                    {new Fraction(2),new Fraction(3),new Fraction(0),new Fraction( 2),new Fraction(0),new Fraction(0),new Fraction(-1),new Fraction(8)},
                                    {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(-2),new Fraction(0),new Fraction(0),new Fraction( 1),new Fraction(0)}
                                }
                            ),
                            new int[] {2,0,4,5,1},
                            new Fraction[] {new Fraction(13,10),null,new Fraction(1),new Fraction(7,6),new Fraction(2)},
                            2,
                            6
                        ),
                        new SimplexTableau(
                            new SimplexProblem(
                                target1,
                                new Fraction[][] {
                                    {new Fraction(0),new Fraction(0),new Fraction(1),new Fraction( 1),new Fraction(-2),new Fraction(0),new Fraction(0),new Fraction(3,5)},
                                    {new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(-1),new Fraction( 2),new Fraction(0),new Fraction(0),new Fraction(3)},
                                    {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(-1),new Fraction( 1),new Fraction(0),new Fraction(1),new Fraction(1)},
                                    {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction( 1),new Fraction(-3),new Fraction(1),new Fraction(0),new Fraction(1,2)},
                                    {new Fraction(0),new Fraction(1),new Fraction(0),new Fraction( 1),new Fraction(-1),new Fraction(0),new Fraction(0),new Fraction(1)},
                                    {new Fraction(2),new Fraction(3),new Fraction(0),new Fraction( 1),new Fraction( 1),new Fraction(0),new Fraction(0),new Fraction(9)},
                                    {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(-1),new Fraction(-1),new Fraction(0),new Fraction(0),new Fraction(0)}
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
                    new Fraction[][] {
                        {new Fraction(1),new Fraction(2),new Fraction(5)},
                        {new Fraction(-1),new Fraction(-1),new Fraction(-2)},
                        {new Fraction(2),new Fraction(1),new Fraction(7)}
                    }
                ),
                new SimplexSolution(
                    List.of(
                        new SimplexTableau(
                            new SimplexProblem(
                                target2,
                                new Fraction[][] {
                                    {new Fraction(1),new Fraction(2),new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(5)},
                                    {new Fraction(-1),new Fraction(-1),new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(-2)},
                                    {new Fraction(2),new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(7)},
                                    {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0)},
                                    {new Fraction(1),new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0)}
                                }
                            ),
                            new int[] {2,3,4},
                            new Fraction[] {new Fraction(5),null,new Fraction(7,2)},
                            2,
                            0
                        ),
                        new SimplexTableau(
                            new SimplexProblem(
                                target2,
                                new Fraction[][] {
                                    {new Fraction(0),new Fraction(3,2),new Fraction(1),new Fraction(0),new Fraction(-1,2),new Fraction(3,2)},
                                    {new Fraction(0),new Fraction(-1,2),new Fraction(0),new Fraction(1),new Fraction(1,2),new Fraction(3,2)},
                                    {new Fraction(1),new Fraction(1,2),new Fraction(0),new Fraction(0),new Fraction(1,2),new Fraction(7,2)},
                                    {new Fraction(1),new Fraction(1,2),new Fraction(0),new Fraction(0),new Fraction(1,2),new Fraction(7,2)},
                                    {new Fraction(0),new Fraction(1,2),new Fraction(0),new Fraction(0),new Fraction(-1,2),new Fraction(0)}
                                }
                            ),
                            new int[] {2,3,0},
                            new Fraction[] {new Fraction(1),null,new Fraction(7)},
                            0,
                            1
                        ),
                        new SimplexTableau(
                            new SimplexProblem(
                                target2,
                                new Fraction[][] {
                                    {new Fraction(0),new Fraction(1),new Fraction(2,3),new Fraction(0),new Fraction(-1,3),new Fraction(1)},
                                    {new Fraction(0),new Fraction(0),new Fraction(1,3),new Fraction(1),new Fraction(1,3),new Fraction(2)},
                                    {new Fraction(1),new Fraction(0),new Fraction(-1,3),new Fraction(0),new Fraction(2,3),new Fraction(3)},
                                    {new Fraction(1),new Fraction(1),new Fraction(1,3),new Fraction(0),new Fraction(1,3),new Fraction(4)},
                                    {new Fraction(0),new Fraction(0),new Fraction(-1,3),new Fraction(0),new Fraction(-1,3),new Fraction(0)}
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
                    new Fraction[][] {
                        {new Fraction(-2),new Fraction(1),new Fraction(1)},
                        {new Fraction(1),new Fraction(-2),new Fraction(1)}
                    }
                ),
                new SimplexSolution(
                    List.of(
                        new SimplexTableau(
                            new SimplexProblem(
                                target2,
                                new Fraction[][] {
                                    {new Fraction(-2),new Fraction( 1),new Fraction(1),new Fraction(0),new Fraction(1)},
                                    {new Fraction( 1),new Fraction(-2),new Fraction(0),new Fraction(1),new Fraction(1)},
                                    {new Fraction( 0),new Fraction( 0),new Fraction(0),new Fraction(0),new Fraction(0)},
                                    {new Fraction( 1),new Fraction( 1),new Fraction(0),new Fraction(0),new Fraction(0)}
                                }
                            ),
                            new int[] {2,3},
                            new Fraction[] {null,new Fraction(1)},
                            1,
                            0
                        ),
                        new SimplexTableau(
                            new SimplexProblem(
                                target2,
                                new Fraction[][] {
                                    {new Fraction(0),new Fraction(-3),new Fraction(1),new Fraction( 2),new Fraction(3)},
                                    {new Fraction(1),new Fraction(-2),new Fraction(0),new Fraction( 1),new Fraction(1)},
                                    {new Fraction(1),new Fraction(-2),new Fraction(0),new Fraction( 1),new Fraction(1)},
                                    {new Fraction(0),new Fraction( 3),new Fraction(0),new Fraction(-1),new Fraction(0)}
                                }
                            ),
                            new int[] {2,0},
                            new Fraction[] {null,null},
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
                    new Fraction[][] {
                        {new Fraction(1),new Fraction(-1),new Fraction(-1)},
                        {new Fraction(-1),new Fraction(2),new Fraction(-1)}
                    }
                ),
                new SimplexSolution(
                    List.of(
                        new SimplexTableau(
                            new SimplexProblem(
                                target2,
                                new Fraction[][] {
                                    {new Fraction( 1),new Fraction(-1),new Fraction(1),new Fraction(0),new Fraction(-1)},
                                    {new Fraction(-1),new Fraction( 2),new Fraction(0),new Fraction(1),new Fraction(-1)},
                                    {new Fraction( 0),new Fraction( 0),new Fraction(0),new Fraction(0),new Fraction(0)},
                                    {new Fraction( 1),new Fraction( 1),new Fraction(0),new Fraction(0),new Fraction(0)}
                                }
                            ),
                            new int[] {2,3},
                            new Fraction[] {null,null},
                            -1,
                            1
                        ),
                        new SimplexTableau(
                            new SimplexProblem(
                                target2,
                                new Fraction[][] {
                                    {new Fraction(-1),new Fraction(1),new Fraction(-1),new Fraction(0),new Fraction(1)},
                                    {new Fraction( 1),new Fraction(0),new Fraction( 2),new Fraction(1),new Fraction(-3)},
                                    {new Fraction(-1),new Fraction(1),new Fraction(-1),new Fraction(0),new Fraction(1)},
                                    {new Fraction( 2),new Fraction(0),new Fraction( 1),new Fraction(0),new Fraction(0)}
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
                        Assert.assertEquals(resultTableau.quotients[i], expectedTableau.quotients[i], msg);
                    }
                }
            }
            Assert.assertEquals(resultTableau.problem.target, expectedTableau.problem.target, msg);
            Assert.assertEquals(resultTableau.problem.matrix.length, expectedTableau.problem.matrix.length, msg);
            for (int i = 0; i < resultTableau.problem.matrix.length; i++) {
                Assert.assertEquals(
                    resultTableau.problem.matrix[i],
                    expectedTableau.problem.matrix[i],
                    msg + ", Zeile " + i
                );
            }
        }
    }

}
