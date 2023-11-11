package exercisegenerator.algorithms.optimization;

import java.util.*;
import java.util.Optional;
import java.util.function.*;

import org.apache.commons.math3.fraction.*;
import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.structures.optimization.*;

public class OptimizationAlgorithmsTest {

    @DataProvider
    public Object[][] dpData() {
        return new Object[][] {
            {
                new int[][] {
                    {0,0,0},
                    {0,1,1},
                    {0,1,2}
                },
                (Function<Integer,String>)i -> i.toString(),
                (Function<Integer,String>)i -> i.toString(),
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
                (Function<Integer,String>)i -> i.toString(),
                (Function<Integer,String>)i -> i.toString(),
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
                (Function<Integer,String>)i -> i.toString(),
                (Function<Integer,String>)i -> i.toString(),
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
                (Function<Integer,String>)i -> i == 0 ? "" : String.valueOf("AB".charAt(i - 1)),
                (Function<Integer,String>)i -> i == 0 ? "" : String.valueOf("AC".charAt(i - 1)),
                Optional.of(LCSAlgorithm.TRACEBACK),
                new String[][] {
                    {"${}^*$", "", null, "\\textbf{A}", null, "\\textbf{C}", null},
                    {"", "0", null, "0", null, "0", null},
                    {"\\textbf{A}", "0", null, "1", "$\\nwarrow$", "1", "$\\leftarrow$"},
                    {"\\textbf{B}", "0", null, "1", null, "1", "$\\uparrow$"}
                }
            }
        };
    }

    @Test(dataProvider="dpData")
    public void dpTableTest(
        final int[][] solution,
        final Function<Integer, String> rowHeading,
        final Function<Integer, String> columnHeading,
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
    public Object[][] gaussJordanData() {
        final LinearSystemOfEquations problem1 =
            new LinearSystemOfEquations(
                new int[][] {
                    {3,5,4,2},
                    {2,1,-1,1},
                    {7,1,1,2}
                }
            );
        final LinearSystemOfEquations problem2 =
            new LinearSystemOfEquations(
                new int[][] {
                    {5, 6, -8, 4, -8, 4},
                    {0, -8, 2, 0, 7, 9},
                    {7, -10, 4, 8, 6, 2},
                    {5, 3, -10, 10, 1, 2},
                    {4, -7, 0, 4, 4, 7},
                    {8, 10, 2, 8, 5, 1}
                }
            );
        final LinearSystemOfEquations problem3 =
            new LinearSystemOfEquations(
                new int[][] {
                    {0, 0, 10, -8, 0, 6},
                    {4, 0, 8, -3, 5, 2}
                }
            );
        return new Object[][] {
            {
                problem1,
                List.of(
                    problem1,
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(5,3),new BigFraction(4,3),new BigFraction(2,3)},
                            {new BigFraction(2),new BigFraction(1),new BigFraction(-1),new BigFraction(1)},
                            {new BigFraction(7),new BigFraction(1),new BigFraction(1),new BigFraction(2)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(5,3),new BigFraction(4,3),new BigFraction(2,3)},
                            {new BigFraction(0),new BigFraction(-7,3),new BigFraction(-11,3),new BigFraction(-1,3)},
                            {new BigFraction(7),new BigFraction(1),new BigFraction(1),new BigFraction(2)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(5,3),new BigFraction(4,3),new BigFraction(2,3)},
                            {new BigFraction(0),new BigFraction(-7,3),new BigFraction(-11,3),new BigFraction(-1,3)},
                            {new BigFraction(0),new BigFraction(-32,3),new BigFraction(-25,3),new BigFraction(-8,3)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(5,3),new BigFraction(4,3),new BigFraction(2,3)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(11,7),new BigFraction(1,7)},
                            {new BigFraction(0),new BigFraction(-32,3),new BigFraction(-25,3),new BigFraction(-8,3)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(-9,7),new BigFraction(3,7)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(11,7),new BigFraction(1,7)},
                            {new BigFraction(0),new BigFraction(-32,3),new BigFraction(-25,3),new BigFraction(-8,3)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(-9,7),new BigFraction(3,7)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(11,7),new BigFraction(1,7)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(59,7),new BigFraction(-8,7)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(-9,7),new BigFraction(3,7)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(11,7),new BigFraction(1,7)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-8,59)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(105,413)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(11,7),new BigFraction(1,7)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-8,59)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(105,413)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(147,413)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-8,59)}
                        }
                    )
                )
            },
            {
                problem2,
                List.of(
                    problem2,
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(6,5),new BigFraction(-8,5),new BigFraction(4,5),new BigFraction(-8,5),new BigFraction(4,5)},
                            {new BigFraction(0),new BigFraction(-8),new BigFraction(2),new BigFraction(0),new BigFraction(7),new BigFraction(9)},
                            {new BigFraction(7),new BigFraction(-10),new BigFraction(4),new BigFraction(8),new BigFraction(6),new BigFraction(2)},
                            {new BigFraction(5),new BigFraction(3),new BigFraction(-10),new BigFraction(10),new BigFraction(1),new BigFraction(2)},
                            {new BigFraction(4),new BigFraction(-7),new BigFraction(0),new BigFraction(4),new BigFraction(4),new BigFraction(7)},
                            {new BigFraction(8),new BigFraction(10),new BigFraction(2),new BigFraction(8),new BigFraction(5),new BigFraction(1)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(6,5),new BigFraction(-8,5),new BigFraction(4,5),new BigFraction(-8,5),new BigFraction(4,5)},
                            {new BigFraction(0),new BigFraction(-8),new BigFraction(2),new BigFraction(0),new BigFraction(7),new BigFraction(9)},
                            {new BigFraction(0),new BigFraction(-92,5),new BigFraction(76,5),new BigFraction(12,5),new BigFraction(86,5),new BigFraction(-18,5)},
                            {new BigFraction(5),new BigFraction(3),new BigFraction(-10),new BigFraction(10),new BigFraction(1),new BigFraction(2)},
                            {new BigFraction(4),new BigFraction(-7),new BigFraction(0),new BigFraction(4),new BigFraction(4),new BigFraction(7)},
                            {new BigFraction(8),new BigFraction(10),new BigFraction(2),new BigFraction(8),new BigFraction(5),new BigFraction(1)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(6,5),new BigFraction(-8,5),new BigFraction(4,5),new BigFraction(-8,5),new BigFraction(4,5)},
                            {new BigFraction(0),new BigFraction(-8),new BigFraction(2),new BigFraction(0),new BigFraction(7),new BigFraction(9)},
                            {new BigFraction(0),new BigFraction(-92,5),new BigFraction(76,5),new BigFraction(12,5),new BigFraction(86,5),new BigFraction(-18,5)},
                            {new BigFraction(0),new BigFraction(-3),new BigFraction(-2),new BigFraction(6),new BigFraction(9),new BigFraction(-2)},
                            {new BigFraction(4),new BigFraction(-7),new BigFraction(0),new BigFraction(4),new BigFraction(4),new BigFraction(7)},
                            {new BigFraction(8),new BigFraction(10),new BigFraction(2),new BigFraction(8),new BigFraction(5),new BigFraction(1)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(6,5),new BigFraction(-8,5),new BigFraction(4,5),new BigFraction(-8,5),new BigFraction(4,5)},
                            {new BigFraction(0),new BigFraction(-8),new BigFraction(2),new BigFraction(0),new BigFraction(7),new BigFraction(9)},
                            {new BigFraction(0),new BigFraction(-92,5),new BigFraction(76,5),new BigFraction(12,5),new BigFraction(86,5),new BigFraction(-18,5)},
                            {new BigFraction(0),new BigFraction(-3),new BigFraction(-2),new BigFraction(6),new BigFraction(9),new BigFraction(-2)},
                            {new BigFraction(0),new BigFraction(-59,5),new BigFraction(32,5),new BigFraction(4,5),new BigFraction(52,5),new BigFraction(19,5)},
                            {new BigFraction(8),new BigFraction(10),new BigFraction(2),new BigFraction(8),new BigFraction(5),new BigFraction(1)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(6,5),new BigFraction(-8,5),new BigFraction(4,5),new BigFraction(-8,5),new BigFraction(4,5)},
                            {new BigFraction(0),new BigFraction(-8),new BigFraction(2),new BigFraction(0),new BigFraction(7),new BigFraction(9)},
                            {new BigFraction(0),new BigFraction(-92,5),new BigFraction(76,5),new BigFraction(12,5),new BigFraction(86,5),new BigFraction(-18,5)},
                            {new BigFraction(0),new BigFraction(-3),new BigFraction(-2),new BigFraction(6),new BigFraction(9),new BigFraction(-2)},
                            {new BigFraction(0),new BigFraction(-59,5),new BigFraction(32,5),new BigFraction(4,5),new BigFraction(52,5),new BigFraction(19,5)},
                            {new BigFraction(0),new BigFraction(2,5),new BigFraction(74,5),new BigFraction(8,5),new BigFraction(89,5),new BigFraction(-27,5)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(6,5),new BigFraction(-8,5),new BigFraction(4,5),new BigFraction(-8,5),new BigFraction(4,5)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(-1,4),new BigFraction(0),new BigFraction(-7,8),new BigFraction(-9,8)},
                            {new BigFraction(0),new BigFraction(-92,5),new BigFraction(76,5),new BigFraction(12,5),new BigFraction(86,5),new BigFraction(-18,5)},
                            {new BigFraction(0),new BigFraction(-3),new BigFraction(-2),new BigFraction(6),new BigFraction(9),new BigFraction(-2)},
                            {new BigFraction(0),new BigFraction(-59,5),new BigFraction(32,5),new BigFraction(4,5),new BigFraction(52,5),new BigFraction(19,5)},
                            {new BigFraction(0),new BigFraction(2,5),new BigFraction(74,5),new BigFraction(8,5),new BigFraction(89,5),new BigFraction(-27,5)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(-13,10),new BigFraction(4,5),new BigFraction(-11,20),new BigFraction(43,20)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(-1,4),new BigFraction(0),new BigFraction(-7,8),new BigFraction(-9,8)},
                            {new BigFraction(0),new BigFraction(-92,5),new BigFraction(76,5),new BigFraction(12,5),new BigFraction(86,5),new BigFraction(-18,5)},
                            {new BigFraction(0),new BigFraction(-3),new BigFraction(-2),new BigFraction(6),new BigFraction(9),new BigFraction(-2)},
                            {new BigFraction(0),new BigFraction(-59,5),new BigFraction(32,5),new BigFraction(4,5),new BigFraction(52,5),new BigFraction(19,5)},
                            {new BigFraction(0),new BigFraction(2,5),new BigFraction(74,5),new BigFraction(8,5),new BigFraction(89,5),new BigFraction(-27,5)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(-13,10),new BigFraction(4,5),new BigFraction(-11,20),new BigFraction(43,20)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(-1,4),new BigFraction(0),new BigFraction(-7,8),new BigFraction(-9,8)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(53,5),new BigFraction(12,5),new BigFraction(11,10),new BigFraction(-243,10)},
                            {new BigFraction(0),new BigFraction(-3),new BigFraction(-2),new BigFraction(6),new BigFraction(9),new BigFraction(-2)},
                            {new BigFraction(0),new BigFraction(-59,5),new BigFraction(32,5),new BigFraction(4,5),new BigFraction(52,5),new BigFraction(19,5)},
                            {new BigFraction(0),new BigFraction(2,5),new BigFraction(74,5),new BigFraction(8,5),new BigFraction(89,5),new BigFraction(-27,5)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(-13,10),new BigFraction(4,5),new BigFraction(-11,20),new BigFraction(43,20)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(-1,4),new BigFraction(0),new BigFraction(-7,8),new BigFraction(-9,8)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(53,5),new BigFraction(12,5),new BigFraction(11,10),new BigFraction(-243,10)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(-11,4),new BigFraction(6),new BigFraction(51,8),new BigFraction(-43,8)},
                            {new BigFraction(0),new BigFraction(-59,5),new BigFraction(32,5),new BigFraction(4,5),new BigFraction(52,5),new BigFraction(19,5)},
                            {new BigFraction(0),new BigFraction(2,5),new BigFraction(74,5),new BigFraction(8,5),new BigFraction(89,5),new BigFraction(-27,5)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(-13,10),new BigFraction(4,5),new BigFraction(-11,20),new BigFraction(43,20)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(-1,4),new BigFraction(0),new BigFraction(-7,8),new BigFraction(-9,8)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(53,5),new BigFraction(12,5),new BigFraction(11,10),new BigFraction(-243,10)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(-11,4),new BigFraction(6),new BigFraction(51,8),new BigFraction(-43,8)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(69,20),new BigFraction(4,5),new BigFraction(3,40),new BigFraction(-379,40)},
                            {new BigFraction(0),new BigFraction(2,5),new BigFraction(74,5),new BigFraction(8,5),new BigFraction(89,5),new BigFraction(-27,5)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(-13,10),new BigFraction(4,5),new BigFraction(-11,20),new BigFraction(43,20)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(-1,4),new BigFraction(0),new BigFraction(-7,8),new BigFraction(-9,8)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(53,5),new BigFraction(12,5),new BigFraction(11,10),new BigFraction(-243,10)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(-11,4),new BigFraction(6),new BigFraction(51,8),new BigFraction(-43,8)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(69,20),new BigFraction(4,5),new BigFraction(3,40),new BigFraction(-379,40)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(149,10),new BigFraction(8,5),new BigFraction(363,20),new BigFraction(-99,20)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(-13,10),new BigFraction(4,5),new BigFraction(-11,20),new BigFraction(43,20)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(-1,4),new BigFraction(0),new BigFraction(-7,8),new BigFraction(-9,8)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(12,53),new BigFraction(11,106),new BigFraction(-243,106)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(-11,4),new BigFraction(6),new BigFraction(51,8),new BigFraction(-43,8)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(69,20),new BigFraction(4,5),new BigFraction(3,40),new BigFraction(-379,40)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(149,10),new BigFraction(8,5),new BigFraction(363,20),new BigFraction(-99,20)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(58,53),new BigFraction(-22,53),new BigFraction(-44,53)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(-1,4),new BigFraction(0),new BigFraction(-7,8),new BigFraction(-9,8)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(12,53),new BigFraction(11,106),new BigFraction(-243,106)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(-11,4),new BigFraction(6),new BigFraction(51,8),new BigFraction(-43,8)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(69,20),new BigFraction(4,5),new BigFraction(3,40),new BigFraction(-379,40)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(149,10),new BigFraction(8,5),new BigFraction(363,20),new BigFraction(-99,20)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(58,53),new BigFraction(-22,53),new BigFraction(-44,53)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(3,53),new BigFraction(-45,53),new BigFraction(-90,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(12,53),new BigFraction(11,106),new BigFraction(-243,106)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(-11,4),new BigFraction(6),new BigFraction(51,8),new BigFraction(-43,8)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(69,20),new BigFraction(4,5),new BigFraction(3,40),new BigFraction(-379,40)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(149,10),new BigFraction(8,5),new BigFraction(363,20),new BigFraction(-99,20)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(58,53),new BigFraction(-22,53),new BigFraction(-44,53)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(3,53),new BigFraction(-45,53),new BigFraction(-90,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(12,53),new BigFraction(11,106),new BigFraction(-243,106)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(351,53),new BigFraction(353,53),new BigFraction(-619,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(69,20),new BigFraction(4,5),new BigFraction(3,40),new BigFraction(-379,40)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(149,10),new BigFraction(8,5),new BigFraction(363,20),new BigFraction(-99,20)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(58,53),new BigFraction(-22,53),new BigFraction(-44,53)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(3,53),new BigFraction(-45,53),new BigFraction(-90,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(12,53),new BigFraction(11,106),new BigFraction(-243,106)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(351,53),new BigFraction(353,53),new BigFraction(-619,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1,53),new BigFraction(-15,53),new BigFraction(-83,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(149,10),new BigFraction(8,5),new BigFraction(363,20),new BigFraction(-99,20)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(58,53),new BigFraction(-22,53),new BigFraction(-44,53)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(3,53),new BigFraction(-45,53),new BigFraction(-90,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(12,53),new BigFraction(11,106),new BigFraction(-243,106)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(351,53),new BigFraction(353,53),new BigFraction(-619,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1,53),new BigFraction(-15,53),new BigFraction(-83,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-94,53),new BigFraction(880,53),new BigFraction(1548,53)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(58,53),new BigFraction(-22,53),new BigFraction(-44,53)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(3,53),new BigFraction(-45,53),new BigFraction(-90,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(12,53),new BigFraction(11,106),new BigFraction(-243,106)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(353,351),new BigFraction(-619,351)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1,53),new BigFraction(-15,53),new BigFraction(-83,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-94,53),new BigFraction(880,53),new BigFraction(1548,53)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-532,351),new BigFraction(386,351)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(3,53),new BigFraction(-45,53),new BigFraction(-90,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(12,53),new BigFraction(11,106),new BigFraction(-243,106)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(353,351),new BigFraction(-619,351)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1,53),new BigFraction(-15,53),new BigFraction(-83,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-94,53),new BigFraction(880,53),new BigFraction(1548,53)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-532,351),new BigFraction(386,351)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-106,117),new BigFraction(-187,117)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(12,53),new BigFraction(11,106),new BigFraction(-243,106)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(353,351),new BigFraction(-619,351)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1,53),new BigFraction(-15,53),new BigFraction(-83,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-94,53),new BigFraction(880,53),new BigFraction(1548,53)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-532,351),new BigFraction(386,351)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-106,117),new BigFraction(-187,117)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-29,234),new BigFraction(-443,234)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(353,351),new BigFraction(-619,351)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1,53),new BigFraction(-15,53),new BigFraction(-83,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-94,53),new BigFraction(880,53),new BigFraction(1548,53)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-532,351),new BigFraction(386,351)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-106,117),new BigFraction(-187,117)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-29,234),new BigFraction(-443,234)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(353,351),new BigFraction(-619,351)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-106,351),new BigFraction(-538,351)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-94,53),new BigFraction(880,53),new BigFraction(1548,53)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-532,351),new BigFraction(386,351)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-106,117),new BigFraction(-187,117)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-29,234),new BigFraction(-443,234)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(353,351),new BigFraction(-619,351)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-106,351),new BigFraction(-538,351)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(6454,351),new BigFraction(9154,351)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-532,351),new BigFraction(386,351)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-106,117),new BigFraction(-187,117)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-29,234),new BigFraction(-443,234)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(353,351),new BigFraction(-619,351)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(269,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(6454,351),new BigFraction(9154,351)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(466,53)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-106,117),new BigFraction(-187,117)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-29,234),new BigFraction(-443,234)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(353,351),new BigFraction(-619,351)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(269,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(6454,351),new BigFraction(9154,351)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(466,53)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-29,234),new BigFraction(-443,234)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(353,351),new BigFraction(-619,351)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(269,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(6454,351),new BigFraction(9154,351)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(466,53)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-67,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(353,351),new BigFraction(-619,351)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(269,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(6454,351),new BigFraction(9154,351)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(466,53)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-67,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-364,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(269,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(6454,351),new BigFraction(9154,351)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(466,53)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-67,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-364,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(269,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-3564,53)}
                        }
                    )
                )
            },
            {
                problem3,
                List.of(
                    problem3,
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(4),new BigFraction(0),new BigFraction(8),new BigFraction(-3),new BigFraction(5),new BigFraction(2)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(10),new BigFraction(-8),new BigFraction(0),new BigFraction(6)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(2),new BigFraction(-3,4),new BigFraction(5,4),new BigFraction(1,2)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(10),new BigFraction(-8),new BigFraction(0),new BigFraction(6)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(2),new BigFraction(0),new BigFraction(-3,4),new BigFraction(5,4),new BigFraction(1,2)},
                            {new BigFraction(0),new BigFraction(10),new BigFraction(0),new BigFraction(-8),new BigFraction(0),new BigFraction(6)}
                        },
                        new int[] {0,2,1,3,4}
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(2),new BigFraction(0),new BigFraction(-3,4),new BigFraction(5,4),new BigFraction(1,2)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-4,5),new BigFraction(0),new BigFraction(3,5)}
                        },
                        new int[] {0,2,1,3,4}
                    ),
                    new LinearSystemOfEquations(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(17,20),new BigFraction(5,4),new BigFraction(-7,10)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-4,5),new BigFraction(0),new BigFraction(3,5)}
                        },
                        new int[] {0,2,1,3,4}
                    )
                )
            }
        };
    }

    @Test(dataProvider="gaussJordanData")
    public void gaussJordanTest(final LinearSystemOfEquations problem, final List<LinearSystemOfEquations> expected) {
        final List<LinearSystemOfEquations> result = GaussJordanAlgorithm.gaussJordan(problem);
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
        final int[][] result = KnapsackAlgorithm.knapsack(problem);
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
        final int[][] result = LCSAlgorithm.lcs(problem);
        Assert.assertTrue(
            Arrays.deepEquals(result, expected),
            String.format("\nExpected: %s\nActual:   %s\n", Arrays.deepToString(expected), Arrays.deepToString(result))
        );
    }

    @DataProvider
    public Object[][] simplexData() {
        final BigFraction[] target1 = new BigFraction[] {new BigFraction(2), new BigFraction(3)};
        final BigFraction[] target2 = new BigFraction[] {new BigFraction(1), new BigFraction(1)};
        return new Object[][] {
            {
                new SimplexProblem(
                    target1,
                    new BigFraction[][] {
                        {new BigFraction(1),new BigFraction(0),new BigFraction(18,5)},
                        {new BigFraction(1),new BigFraction(2),new BigFraction(5)},
                        {new BigFraction(1),new BigFraction(1),new BigFraction(4)},
                        {new BigFraction(2),new BigFraction(1),new BigFraction(15,2)},
                        {new BigFraction(0),new BigFraction(1),new BigFraction(2)}
                    }
                ),
                new SimplexSolution(
                    List.of(
                        new SimplexTableau(
                            new SimplexProblem(
                                target1,
                                new BigFraction[][] {
                                    {new BigFraction(1),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(18,5)},
                                    {new BigFraction(1),new BigFraction(2),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(5)},
                                    {new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(4)},
                                    {new BigFraction(2),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(15,2)},
                                    {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(2)},
                                    {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)},
                                    {new BigFraction(2),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                }
                            ),
                            new int[] {2,3,4,5,6},
                            new BigFraction[] {null,new BigFraction(5,2),new BigFraction(4),new BigFraction(15,2),new BigFraction(2)},
                            4,
                            1
                        ),
                        new SimplexTableau(
                            new SimplexProblem(
                                target1,
                                new BigFraction[][] {
                                    {new BigFraction(1),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(18,5)},
                                    {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-2),new BigFraction(1)},
                                    {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-1),new BigFraction(2)},
                                    {new BigFraction(2),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-1),new BigFraction(11,2)},
                                    {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(2)},
                                    {new BigFraction(0),new BigFraction(3),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(3),new BigFraction(6)},
                                    {new BigFraction(2),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-3),new BigFraction(0)}
                                }
                            ),
                            new int[] {2,3,4,5,1},
                            new BigFraction[] {new BigFraction(18,5),new BigFraction(1),new BigFraction(2),new BigFraction(11,4),null},
                            1,
                            0
                        ),
                        new SimplexTableau(
                            new SimplexProblem(
                                target1,
                                new BigFraction[][] {
                                    {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction( 2),new BigFraction(13,5)},
                                    {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction( 1),new BigFraction(0),new BigFraction(0),new BigFraction(-2),new BigFraction(1)},
                                    {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(1),new BigFraction(0),new BigFraction( 1),new BigFraction(1)},
                                    {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-2),new BigFraction(0),new BigFraction(1),new BigFraction( 3),new BigFraction(7,2)},
                                    {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction( 0),new BigFraction(0),new BigFraction(0),new BigFraction( 1),new BigFraction(2)},
                                    {new BigFraction(2),new BigFraction(3),new BigFraction(0),new BigFraction( 2),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(8)},
                                    {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-2),new BigFraction(0),new BigFraction(0),new BigFraction( 1),new BigFraction(0)}
                                }
                            ),
                            new int[] {2,0,4,5,1},
                            new BigFraction[] {new BigFraction(13,10),null,new BigFraction(1),new BigFraction(7,6),new BigFraction(2)},
                            2,
                            6
                        ),
                        new SimplexTableau(
                            new SimplexProblem(
                                target1,
                                new BigFraction[][] {
                                    {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction( 1),new BigFraction(-2),new BigFraction(0),new BigFraction(0),new BigFraction(3,5)},
                                    {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction( 2),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                                    {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction( 1),new BigFraction(0),new BigFraction(1),new BigFraction(1)},
                                    {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction( 1),new BigFraction(-3),new BigFraction(1),new BigFraction(0),new BigFraction(1,2)},
                                    {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction( 1),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(1)},
                                    {new BigFraction(2),new BigFraction(3),new BigFraction(0),new BigFraction( 1),new BigFraction( 1),new BigFraction(0),new BigFraction(0),new BigFraction(9)},
                                    {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-1),new BigFraction(-1),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
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
                    new BigFraction[][] {
                        {new BigFraction(1),new BigFraction(2),new BigFraction(5)},
                        {new BigFraction(-1),new BigFraction(-1),new BigFraction(-2)},
                        {new BigFraction(2),new BigFraction(1),new BigFraction(7)}
                    }
                ),
                new SimplexSolution(
                    List.of(
                        new SimplexTableau(
                            new SimplexProblem(
                                target2,
                                new BigFraction[][] {
                                    {new BigFraction(1),new BigFraction(2),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(5)},
                                    {new BigFraction(-1),new BigFraction(-1),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-2)},
                                    {new BigFraction(2),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(7)},
                                    {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)},
                                    {new BigFraction(1),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                }
                            ),
                            new int[] {2,3,4},
                            new BigFraction[] {new BigFraction(5),null,new BigFraction(7,2)},
                            2,
                            0
                        ),
                        new SimplexTableau(
                            new SimplexProblem(
                                target2,
                                new BigFraction[][] {
                                    {new BigFraction(0),new BigFraction(3,2),new BigFraction(1),new BigFraction(0),new BigFraction(-1,2),new BigFraction(3,2)},
                                    {new BigFraction(0),new BigFraction(-1,2),new BigFraction(0),new BigFraction(1),new BigFraction(1,2),new BigFraction(3,2)},
                                    {new BigFraction(1),new BigFraction(1,2),new BigFraction(0),new BigFraction(0),new BigFraction(1,2),new BigFraction(7,2)},
                                    {new BigFraction(1),new BigFraction(1,2),new BigFraction(0),new BigFraction(0),new BigFraction(1,2),new BigFraction(7,2)},
                                    {new BigFraction(0),new BigFraction(1,2),new BigFraction(0),new BigFraction(0),new BigFraction(-1,2),new BigFraction(0)}
                                }
                            ),
                            new int[] {2,3,0},
                            new BigFraction[] {new BigFraction(1),null,new BigFraction(7)},
                            0,
                            1
                        ),
                        new SimplexTableau(
                            new SimplexProblem(
                                target2,
                                new BigFraction[][] {
                                    {new BigFraction(0),new BigFraction(1),new BigFraction(2,3),new BigFraction(0),new BigFraction(-1,3),new BigFraction(1)},
                                    {new BigFraction(0),new BigFraction(0),new BigFraction(1,3),new BigFraction(1),new BigFraction(1,3),new BigFraction(2)},
                                    {new BigFraction(1),new BigFraction(0),new BigFraction(-1,3),new BigFraction(0),new BigFraction(2,3),new BigFraction(3)},
                                    {new BigFraction(1),new BigFraction(1),new BigFraction(1,3),new BigFraction(0),new BigFraction(1,3),new BigFraction(4)},
                                    {new BigFraction(0),new BigFraction(0),new BigFraction(-1,3),new BigFraction(0),new BigFraction(-1,3),new BigFraction(0)}
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
                    new BigFraction[][] {
                        {new BigFraction(-2),new BigFraction(1),new BigFraction(1)},
                        {new BigFraction(1),new BigFraction(-2),new BigFraction(1)}
                    }
                ),
                new SimplexSolution(
                    List.of(
                        new SimplexTableau(
                            new SimplexProblem(
                                target2,
                                new BigFraction[][] {
                                    {new BigFraction(-2),new BigFraction( 1),new BigFraction(1),new BigFraction(0),new BigFraction(1)},
                                    {new BigFraction( 1),new BigFraction(-2),new BigFraction(0),new BigFraction(1),new BigFraction(1)},
                                    {new BigFraction( 0),new BigFraction( 0),new BigFraction(0),new BigFraction(0),new BigFraction(0)},
                                    {new BigFraction( 1),new BigFraction( 1),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                }
                            ),
                            new int[] {2,3},
                            new BigFraction[] {null,new BigFraction(1)},
                            1,
                            0
                        ),
                        new SimplexTableau(
                            new SimplexProblem(
                                target2,
                                new BigFraction[][] {
                                    {new BigFraction(0),new BigFraction(-3),new BigFraction(1),new BigFraction( 2),new BigFraction(3)},
                                    {new BigFraction(1),new BigFraction(-2),new BigFraction(0),new BigFraction( 1),new BigFraction(1)},
                                    {new BigFraction(1),new BigFraction(-2),new BigFraction(0),new BigFraction( 1),new BigFraction(1)},
                                    {new BigFraction(0),new BigFraction( 3),new BigFraction(0),new BigFraction(-1),new BigFraction(0)}
                                }
                            ),
                            new int[] {2,0},
                            new BigFraction[] {null,null},
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
                    new BigFraction[][] {
                        {new BigFraction(1),new BigFraction(-1),new BigFraction(-1)},
                        {new BigFraction(-1),new BigFraction(2),new BigFraction(-1)}
                    }
                ),
                new SimplexSolution(
                    List.of(
                        new SimplexTableau(
                            new SimplexProblem(
                                target2,
                                new BigFraction[][] {
                                    {new BigFraction( 1),new BigFraction(-1),new BigFraction(1),new BigFraction(0),new BigFraction(-1)},
                                    {new BigFraction(-1),new BigFraction( 2),new BigFraction(0),new BigFraction(1),new BigFraction(-1)},
                                    {new BigFraction( 0),new BigFraction( 0),new BigFraction(0),new BigFraction(0),new BigFraction(0)},
                                    {new BigFraction( 1),new BigFraction( 1),new BigFraction(0),new BigFraction(0),new BigFraction(0)}
                                }
                            ),
                            new int[] {2,3},
                            new BigFraction[] {null,null},
                            -1,
                            1
                        ),
                        new SimplexTableau(
                            new SimplexProblem(
                                target2,
                                new BigFraction[][] {
                                    {new BigFraction(-1),new BigFraction(1),new BigFraction(-1),new BigFraction(0),new BigFraction(1)},
                                    {new BigFraction( 1),new BigFraction(0),new BigFraction( 2),new BigFraction(1),new BigFraction(-3)},
                                    {new BigFraction(-1),new BigFraction(1),new BigFraction(-1),new BigFraction(0),new BigFraction(1)},
                                    {new BigFraction( 2),new BigFraction(0),new BigFraction( 1),new BigFraction(0),new BigFraction(0)}
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
        final SimplexSolution result = SimplexAlgorithm.simplex(problem);
        Assert.assertEquals(result, expected);
    }

}
