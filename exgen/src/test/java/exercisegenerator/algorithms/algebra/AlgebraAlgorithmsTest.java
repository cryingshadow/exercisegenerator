package exercisegenerator.algorithms.algebra;

import java.util.*;

import org.apache.commons.math3.fraction.*;
import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.structures.algebra.*;

public class AlgebraAlgorithmsTest {

    @DataProvider
    public Object[][] gaussJordanData() {
        final Matrix problem1 =
            new Matrix(
                new int[][] {
                    {3,5,4,2},
                    {2,1,-1,1},
                    {7,1,1,2}
                },
                3
            );
        final Matrix problem2 =
            new Matrix(
                new int[][] {
                    {5, 6, -8, 4, -8, 4},
                    {0, -8, 2, 0, 7, 9},
                    {7, -10, 4, 8, 6, 2},
                    {5, 3, -10, 10, 1, 2},
                    {4, -7, 0, 4, 4, 7},
                    {8, 10, 2, 8, 5, 1}
                },
                5
            );
        final Matrix problem3 =
            new Matrix(
                new int[][] {
                    {0, 0, 10, -8, 0, 6},
                    {4, 0, 8, -3, 5, 2}
                },
                5
            );
        final Matrix problem4 =
            new Matrix(
                new int[][] {
                    {2, 3, 4, 1, 0, 0},
                    {5, 6, 7, 0, 1, 0},
                    {8, 9, 1, 0, 0, 1}
                },
                3
            );
        final Matrix problem5 =
            new Matrix(
                new int[][] {
                    {1, 0, 1, 1, 0, 0},
                    {-1, 1, 0, 0, 1, 0},
                    {0, 0, 0, 0, 0, 1}
                },
                3
            );
        return new Object[][] {
            {
                problem1,
                List.of(
                    problem1,
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(5,3),new BigFraction(4,3),new BigFraction(2,3)},
                            {new BigFraction(2),new BigFraction(1),new BigFraction(-1),new BigFraction(1)},
                            {new BigFraction(7),new BigFraction(1),new BigFraction(1),new BigFraction(2)}
                        },
                        3
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(5,3),new BigFraction(4,3),new BigFraction(2,3)},
                            {new BigFraction(0),new BigFraction(-7,3),new BigFraction(-11,3),new BigFraction(-1,3)},
                            {new BigFraction(7),new BigFraction(1),new BigFraction(1),new BigFraction(2)}
                        },
                        3
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(5,3),new BigFraction(4,3),new BigFraction(2,3)},
                            {new BigFraction(0),new BigFraction(-7,3),new BigFraction(-11,3),new BigFraction(-1,3)},
                            {new BigFraction(0),new BigFraction(-32,3),new BigFraction(-25,3),new BigFraction(-8,3)}
                        },
                        3
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(5,3),new BigFraction(4,3),new BigFraction(2,3)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(11,7),new BigFraction(1,7)},
                            {new BigFraction(0),new BigFraction(-32,3),new BigFraction(-25,3),new BigFraction(-8,3)}
                        },
                        3
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(-9,7),new BigFraction(3,7)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(11,7),new BigFraction(1,7)},
                            {new BigFraction(0),new BigFraction(-32,3),new BigFraction(-25,3),new BigFraction(-8,3)}
                        },
                        3
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(-9,7),new BigFraction(3,7)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(11,7),new BigFraction(1,7)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(59,7),new BigFraction(-8,7)}
                        },
                        3
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(-9,7),new BigFraction(3,7)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(11,7),new BigFraction(1,7)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-8,59)}
                        },
                        3
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(105,413)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(11,7),new BigFraction(1,7)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-8,59)}
                        },
                        3
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(105,413)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(147,413)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-8,59)}
                        },
                        3
                    )
                )
            },
            {
                problem2,
                List.of(
                    problem2,
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(6,5),new BigFraction(-8,5),new BigFraction(4,5),new BigFraction(-8,5),new BigFraction(4,5)},
                            {new BigFraction(0),new BigFraction(-8),new BigFraction(2),new BigFraction(0),new BigFraction(7),new BigFraction(9)},
                            {new BigFraction(7),new BigFraction(-10),new BigFraction(4),new BigFraction(8),new BigFraction(6),new BigFraction(2)},
                            {new BigFraction(5),new BigFraction(3),new BigFraction(-10),new BigFraction(10),new BigFraction(1),new BigFraction(2)},
                            {new BigFraction(4),new BigFraction(-7),new BigFraction(0),new BigFraction(4),new BigFraction(4),new BigFraction(7)},
                            {new BigFraction(8),new BigFraction(10),new BigFraction(2),new BigFraction(8),new BigFraction(5),new BigFraction(1)}
                        },
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(6,5),new BigFraction(-8,5),new BigFraction(4,5),new BigFraction(-8,5),new BigFraction(4,5)},
                            {new BigFraction(0),new BigFraction(-8),new BigFraction(2),new BigFraction(0),new BigFraction(7),new BigFraction(9)},
                            {new BigFraction(0),new BigFraction(-92,5),new BigFraction(76,5),new BigFraction(12,5),new BigFraction(86,5),new BigFraction(-18,5)},
                            {new BigFraction(5),new BigFraction(3),new BigFraction(-10),new BigFraction(10),new BigFraction(1),new BigFraction(2)},
                            {new BigFraction(4),new BigFraction(-7),new BigFraction(0),new BigFraction(4),new BigFraction(4),new BigFraction(7)},
                            {new BigFraction(8),new BigFraction(10),new BigFraction(2),new BigFraction(8),new BigFraction(5),new BigFraction(1)}
                        },
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(6,5),new BigFraction(-8,5),new BigFraction(4,5),new BigFraction(-8,5),new BigFraction(4,5)},
                            {new BigFraction(0),new BigFraction(-8),new BigFraction(2),new BigFraction(0),new BigFraction(7),new BigFraction(9)},
                            {new BigFraction(0),new BigFraction(-92,5),new BigFraction(76,5),new BigFraction(12,5),new BigFraction(86,5),new BigFraction(-18,5)},
                            {new BigFraction(0),new BigFraction(-3),new BigFraction(-2),new BigFraction(6),new BigFraction(9),new BigFraction(-2)},
                            {new BigFraction(4),new BigFraction(-7),new BigFraction(0),new BigFraction(4),new BigFraction(4),new BigFraction(7)},
                            {new BigFraction(8),new BigFraction(10),new BigFraction(2),new BigFraction(8),new BigFraction(5),new BigFraction(1)}
                        },
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(6,5),new BigFraction(-8,5),new BigFraction(4,5),new BigFraction(-8,5),new BigFraction(4,5)},
                            {new BigFraction(0),new BigFraction(-8),new BigFraction(2),new BigFraction(0),new BigFraction(7),new BigFraction(9)},
                            {new BigFraction(0),new BigFraction(-92,5),new BigFraction(76,5),new BigFraction(12,5),new BigFraction(86,5),new BigFraction(-18,5)},
                            {new BigFraction(0),new BigFraction(-3),new BigFraction(-2),new BigFraction(6),new BigFraction(9),new BigFraction(-2)},
                            {new BigFraction(0),new BigFraction(-59,5),new BigFraction(32,5),new BigFraction(4,5),new BigFraction(52,5),new BigFraction(19,5)},
                            {new BigFraction(8),new BigFraction(10),new BigFraction(2),new BigFraction(8),new BigFraction(5),new BigFraction(1)}
                        },
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(6,5),new BigFraction(-8,5),new BigFraction(4,5),new BigFraction(-8,5),new BigFraction(4,5)},
                            {new BigFraction(0),new BigFraction(-8),new BigFraction(2),new BigFraction(0),new BigFraction(7),new BigFraction(9)},
                            {new BigFraction(0),new BigFraction(-92,5),new BigFraction(76,5),new BigFraction(12,5),new BigFraction(86,5),new BigFraction(-18,5)},
                            {new BigFraction(0),new BigFraction(-3),new BigFraction(-2),new BigFraction(6),new BigFraction(9),new BigFraction(-2)},
                            {new BigFraction(0),new BigFraction(-59,5),new BigFraction(32,5),new BigFraction(4,5),new BigFraction(52,5),new BigFraction(19,5)},
                            {new BigFraction(0),new BigFraction(2,5),new BigFraction(74,5),new BigFraction(8,5),new BigFraction(89,5),new BigFraction(-27,5)}
                        },
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(6,5),new BigFraction(-8,5),new BigFraction(4,5),new BigFraction(-8,5),new BigFraction(4,5)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(-1,4),new BigFraction(0),new BigFraction(-7,8),new BigFraction(-9,8)},
                            {new BigFraction(0),new BigFraction(-92,5),new BigFraction(76,5),new BigFraction(12,5),new BigFraction(86,5),new BigFraction(-18,5)},
                            {new BigFraction(0),new BigFraction(-3),new BigFraction(-2),new BigFraction(6),new BigFraction(9),new BigFraction(-2)},
                            {new BigFraction(0),new BigFraction(-59,5),new BigFraction(32,5),new BigFraction(4,5),new BigFraction(52,5),new BigFraction(19,5)},
                            {new BigFraction(0),new BigFraction(2,5),new BigFraction(74,5),new BigFraction(8,5),new BigFraction(89,5),new BigFraction(-27,5)}
                        },
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(-13,10),new BigFraction(4,5),new BigFraction(-11,20),new BigFraction(43,20)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(-1,4),new BigFraction(0),new BigFraction(-7,8),new BigFraction(-9,8)},
                            {new BigFraction(0),new BigFraction(-92,5),new BigFraction(76,5),new BigFraction(12,5),new BigFraction(86,5),new BigFraction(-18,5)},
                            {new BigFraction(0),new BigFraction(-3),new BigFraction(-2),new BigFraction(6),new BigFraction(9),new BigFraction(-2)},
                            {new BigFraction(0),new BigFraction(-59,5),new BigFraction(32,5),new BigFraction(4,5),new BigFraction(52,5),new BigFraction(19,5)},
                            {new BigFraction(0),new BigFraction(2,5),new BigFraction(74,5),new BigFraction(8,5),new BigFraction(89,5),new BigFraction(-27,5)}
                        },
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(-13,10),new BigFraction(4,5),new BigFraction(-11,20),new BigFraction(43,20)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(-1,4),new BigFraction(0),new BigFraction(-7,8),new BigFraction(-9,8)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(53,5),new BigFraction(12,5),new BigFraction(11,10),new BigFraction(-243,10)},
                            {new BigFraction(0),new BigFraction(-3),new BigFraction(-2),new BigFraction(6),new BigFraction(9),new BigFraction(-2)},
                            {new BigFraction(0),new BigFraction(-59,5),new BigFraction(32,5),new BigFraction(4,5),new BigFraction(52,5),new BigFraction(19,5)},
                            {new BigFraction(0),new BigFraction(2,5),new BigFraction(74,5),new BigFraction(8,5),new BigFraction(89,5),new BigFraction(-27,5)}
                        },
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(-13,10),new BigFraction(4,5),new BigFraction(-11,20),new BigFraction(43,20)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(-1,4),new BigFraction(0),new BigFraction(-7,8),new BigFraction(-9,8)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(53,5),new BigFraction(12,5),new BigFraction(11,10),new BigFraction(-243,10)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(-11,4),new BigFraction(6),new BigFraction(51,8),new BigFraction(-43,8)},
                            {new BigFraction(0),new BigFraction(-59,5),new BigFraction(32,5),new BigFraction(4,5),new BigFraction(52,5),new BigFraction(19,5)},
                            {new BigFraction(0),new BigFraction(2,5),new BigFraction(74,5),new BigFraction(8,5),new BigFraction(89,5),new BigFraction(-27,5)}
                        },
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(-13,10),new BigFraction(4,5),new BigFraction(-11,20),new BigFraction(43,20)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(-1,4),new BigFraction(0),new BigFraction(-7,8),new BigFraction(-9,8)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(53,5),new BigFraction(12,5),new BigFraction(11,10),new BigFraction(-243,10)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(-11,4),new BigFraction(6),new BigFraction(51,8),new BigFraction(-43,8)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(69,20),new BigFraction(4,5),new BigFraction(3,40),new BigFraction(-379,40)},
                            {new BigFraction(0),new BigFraction(2,5),new BigFraction(74,5),new BigFraction(8,5),new BigFraction(89,5),new BigFraction(-27,5)}
                        },
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(-13,10),new BigFraction(4,5),new BigFraction(-11,20),new BigFraction(43,20)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(-1,4),new BigFraction(0),new BigFraction(-7,8),new BigFraction(-9,8)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(53,5),new BigFraction(12,5),new BigFraction(11,10),new BigFraction(-243,10)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(-11,4),new BigFraction(6),new BigFraction(51,8),new BigFraction(-43,8)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(69,20),new BigFraction(4,5),new BigFraction(3,40),new BigFraction(-379,40)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(149,10),new BigFraction(8,5),new BigFraction(363,20),new BigFraction(-99,20)}
                        },
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(-13,10),new BigFraction(4,5),new BigFraction(-11,20),new BigFraction(43,20)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(-1,4),new BigFraction(0),new BigFraction(-7,8),new BigFraction(-9,8)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(12,53),new BigFraction(11,106),new BigFraction(-243,106)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(-11,4),new BigFraction(6),new BigFraction(51,8),new BigFraction(-43,8)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(69,20),new BigFraction(4,5),new BigFraction(3,40),new BigFraction(-379,40)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(149,10),new BigFraction(8,5),new BigFraction(363,20),new BigFraction(-99,20)}
                        },
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(58,53),new BigFraction(-22,53),new BigFraction(-44,53)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(-1,4),new BigFraction(0),new BigFraction(-7,8),new BigFraction(-9,8)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(12,53),new BigFraction(11,106),new BigFraction(-243,106)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(-11,4),new BigFraction(6),new BigFraction(51,8),new BigFraction(-43,8)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(69,20),new BigFraction(4,5),new BigFraction(3,40),new BigFraction(-379,40)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(149,10),new BigFraction(8,5),new BigFraction(363,20),new BigFraction(-99,20)}
                        },
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(58,53),new BigFraction(-22,53),new BigFraction(-44,53)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(3,53),new BigFraction(-45,53),new BigFraction(-90,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(12,53),new BigFraction(11,106),new BigFraction(-243,106)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(-11,4),new BigFraction(6),new BigFraction(51,8),new BigFraction(-43,8)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(69,20),new BigFraction(4,5),new BigFraction(3,40),new BigFraction(-379,40)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(149,10),new BigFraction(8,5),new BigFraction(363,20),new BigFraction(-99,20)}
                        },
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(58,53),new BigFraction(-22,53),new BigFraction(-44,53)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(3,53),new BigFraction(-45,53),new BigFraction(-90,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(12,53),new BigFraction(11,106),new BigFraction(-243,106)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(351,53),new BigFraction(353,53),new BigFraction(-619,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(69,20),new BigFraction(4,5),new BigFraction(3,40),new BigFraction(-379,40)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(149,10),new BigFraction(8,5),new BigFraction(363,20),new BigFraction(-99,20)}
                        },
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(58,53),new BigFraction(-22,53),new BigFraction(-44,53)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(3,53),new BigFraction(-45,53),new BigFraction(-90,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(12,53),new BigFraction(11,106),new BigFraction(-243,106)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(351,53),new BigFraction(353,53),new BigFraction(-619,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1,53),new BigFraction(-15,53),new BigFraction(-83,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(149,10),new BigFraction(8,5),new BigFraction(363,20),new BigFraction(-99,20)}
                        },
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(58,53),new BigFraction(-22,53),new BigFraction(-44,53)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(3,53),new BigFraction(-45,53),new BigFraction(-90,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(12,53),new BigFraction(11,106),new BigFraction(-243,106)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(351,53),new BigFraction(353,53),new BigFraction(-619,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1,53),new BigFraction(-15,53),new BigFraction(-83,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-94,53),new BigFraction(880,53),new BigFraction(1548,53)}
                        },
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(58,53),new BigFraction(-22,53),new BigFraction(-44,53)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(3,53),new BigFraction(-45,53),new BigFraction(-90,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(12,53),new BigFraction(11,106),new BigFraction(-243,106)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(353,351),new BigFraction(-619,351)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1,53),new BigFraction(-15,53),new BigFraction(-83,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-94,53),new BigFraction(880,53),new BigFraction(1548,53)}
                        },
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-532,351),new BigFraction(386,351)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(3,53),new BigFraction(-45,53),new BigFraction(-90,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(12,53),new BigFraction(11,106),new BigFraction(-243,106)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(353,351),new BigFraction(-619,351)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1,53),new BigFraction(-15,53),new BigFraction(-83,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-94,53),new BigFraction(880,53),new BigFraction(1548,53)}
                        },
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-532,351),new BigFraction(386,351)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-106,117),new BigFraction(-187,117)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(12,53),new BigFraction(11,106),new BigFraction(-243,106)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(353,351),new BigFraction(-619,351)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1,53),new BigFraction(-15,53),new BigFraction(-83,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-94,53),new BigFraction(880,53),new BigFraction(1548,53)}
                        },
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-532,351),new BigFraction(386,351)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-106,117),new BigFraction(-187,117)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-29,234),new BigFraction(-443,234)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(353,351),new BigFraction(-619,351)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1,53),new BigFraction(-15,53),new BigFraction(-83,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-94,53),new BigFraction(880,53),new BigFraction(1548,53)}
                        },
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-532,351),new BigFraction(386,351)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-106,117),new BigFraction(-187,117)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-29,234),new BigFraction(-443,234)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(353,351),new BigFraction(-619,351)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-106,351),new BigFraction(-538,351)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-94,53),new BigFraction(880,53),new BigFraction(1548,53)}
                        },
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-532,351),new BigFraction(386,351)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-106,117),new BigFraction(-187,117)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-29,234),new BigFraction(-443,234)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(353,351),new BigFraction(-619,351)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-106,351),new BigFraction(-538,351)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(6454,351),new BigFraction(9154,351)}
                        },
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-532,351),new BigFraction(386,351)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-106,117),new BigFraction(-187,117)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-29,234),new BigFraction(-443,234)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(353,351),new BigFraction(-619,351)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(269,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(6454,351),new BigFraction(9154,351)}
                        },
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(466,53)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-106,117),new BigFraction(-187,117)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-29,234),new BigFraction(-443,234)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(353,351),new BigFraction(-619,351)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(269,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(6454,351),new BigFraction(9154,351)}
                        },
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(466,53)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-29,234),new BigFraction(-443,234)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(353,351),new BigFraction(-619,351)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(269,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(6454,351),new BigFraction(9154,351)}
                        },
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(466,53)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-67,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(353,351),new BigFraction(-619,351)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(269,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(6454,351),new BigFraction(9154,351)}
                        },
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(466,53)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-67,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-364,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(269,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(6454,351),new BigFraction(9154,351)}
                        },
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(466,53)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(3)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-67,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-364,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(269,53)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(0),new BigFraction(-3564,53)}
                        },
                        5
                    )
                )
            },
            {
                problem3,
                List.of(
                    problem3,
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(4),new BigFraction(0),new BigFraction(8),new BigFraction(-3),new BigFraction(5),new BigFraction(2)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(10),new BigFraction(-8),new BigFraction(0),new BigFraction(6)}
                        },
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(2),new BigFraction(-3,4),new BigFraction(5,4),new BigFraction(1,2)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(10),new BigFraction(-8),new BigFraction(0),new BigFraction(6)}
                        },
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(2),new BigFraction(0),new BigFraction(-3,4),new BigFraction(5,4),new BigFraction(1,2)},
                            {new BigFraction(0),new BigFraction(10),new BigFraction(0),new BigFraction(-8),new BigFraction(0),new BigFraction(6)}
                        },
                        new int[] {0,2,1,3,4},
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(2),new BigFraction(0),new BigFraction(-3,4),new BigFraction(5,4),new BigFraction(1,2)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-4,5),new BigFraction(0),new BigFraction(3,5)}
                        },
                        new int[] {0,2,1,3,4},
                        5
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(17,20),new BigFraction(5,4),new BigFraction(-7,10)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(-4,5),new BigFraction(0),new BigFraction(3,5)}
                        },
                        new int[] {0,2,1,3,4},
                        5
                    )
                )
            },
            {
                problem4,
                List.of(
                    problem4,
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(3, 2),new BigFraction(2),new BigFraction(1, 2),new BigFraction(0),new BigFraction(0)},
                            {new BigFraction(5),new BigFraction(6),new BigFraction(7),new BigFraction(0),new BigFraction(1),new BigFraction(0)},
                            {new BigFraction(8),new BigFraction(9),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1)}
                        },
                        3
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(3, 2),new BigFraction(2),new BigFraction(1, 2),new BigFraction(0),new BigFraction(0)},
                            {new BigFraction(0),new BigFraction(-3, 2),new BigFraction(-3),new BigFraction(-5, 2),new BigFraction(1),new BigFraction(0)},
                            {new BigFraction(8),new BigFraction(9),new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(1)}
                        },
                        3
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(3, 2),new BigFraction(2),new BigFraction(1, 2),new BigFraction(0),new BigFraction(0)},
                            {new BigFraction(0),new BigFraction(-3, 2),new BigFraction(-3),new BigFraction(-5, 2),new BigFraction(1),new BigFraction(0)},
                            {new BigFraction(0),new BigFraction(-3),new BigFraction(-15),new BigFraction(-4),new BigFraction(0),new BigFraction(1)}
                        },
                        3
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(3, 2),new BigFraction(2),new BigFraction(1, 2),new BigFraction(0),new BigFraction(0)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(2),new BigFraction(5, 3),new BigFraction(-2, 3),new BigFraction(0)},
                            {new BigFraction(0),new BigFraction(-3),new BigFraction(-15),new BigFraction(-4),new BigFraction(0),new BigFraction(1)}
                        },
                        3
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(-1),new BigFraction(-2),new BigFraction(1),new BigFraction(0)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(2),new BigFraction(5, 3),new BigFraction(-2, 3),new BigFraction(0)},
                            {new BigFraction(0),new BigFraction(-3),new BigFraction(-15),new BigFraction(-4),new BigFraction(0),new BigFraction(1)}
                        },
                        3
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(-1),new BigFraction(-2),new BigFraction(1),new BigFraction(0)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(2),new BigFraction(5, 3),new BigFraction(-2, 3),new BigFraction(0)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(-9),new BigFraction(1),new BigFraction(-2),new BigFraction(1)}
                        },
                        3
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(-1),new BigFraction(-2),new BigFraction(1),new BigFraction(0)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(2),new BigFraction(5, 3),new BigFraction(-2, 3),new BigFraction(0)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-1, 9),new BigFraction(2, 9),new BigFraction(-1, 9)}
                        },
                        3
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-19, 9),new BigFraction(11, 9),new BigFraction(-1, 9)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(2),new BigFraction(5, 3),new BigFraction(-2, 3),new BigFraction(0)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-1, 9),new BigFraction(2, 9),new BigFraction(-1, 9)}
                        },
                        3
                    ),
                    new Matrix(
                        new BigFraction[][] {
                            {new BigFraction(1),new BigFraction(0),new BigFraction(0),new BigFraction(-19, 9),new BigFraction(11, 9),new BigFraction(-1, 9)},
                            {new BigFraction(0),new BigFraction(1),new BigFraction(0),new BigFraction(17, 9),new BigFraction(-10, 9),new BigFraction(2, 9)},
                            {new BigFraction(0),new BigFraction(0),new BigFraction(1),new BigFraction(-1, 9),new BigFraction(2, 9),new BigFraction(-1, 9)}
                        },
                        3
                    )
                )
            },
            {
                problem5,
                List.of(
                    problem5,
                    new Matrix(
                        new int[][] {
                            {1,0,1,1,0,0},
                            {0,1,1,1,1,0},
                            {0,0,0,0,0,1}
                        },
                        3
                    )
                )
            }
        };
    }

    @Test(dataProvider="gaussJordanData")
    public void gaussJordanTest(final Matrix problem, final List<Matrix> expected) {
        final List<Matrix> result = GaussJordanAlgorithm.gaussJordan(problem);
        Assert.assertEquals(result, expected);
    }

    @DataProvider
    public Object[][] matrixArithmeticData() {
        final Matrix matrix1 =
            new Matrix(
                new int[][] {
                    {3,5,4},
                    {2,1,-1},
                    {7,1,1}
                },
                3
            );
        final Matrix matrix2 =
            new Matrix(
                new int[][] {
                    {5, 6, -8},
                    {0, -8, 2},
                    {7, -10, 4}
                },
                3
            );
        final Matrix matrix3 =
            new Matrix(
                new int[][] {
                    {10, -8, 3},
                    {4, 2, 1}
                },
                3
            );
        final Matrix matrix4 =
            new Matrix(
                new int[][] {
                    {2, 3},
                    {5, 6},
                    {8, 9}
                },
                2
            );
        final Matrix matrix5 =
            new Matrix(
                new int[][] {
                    {2, 3, 4},
                    {5, 6, 7},
                    {8, 9, 1}
                },
                3
            );
        final Matrix matrix6 =
            new Matrix(
                new BigFraction[][] {
                    {new BigFraction(-19, 9),new BigFraction(11, 9),new BigFraction(-1, 9)},
                    {new BigFraction(17, 9),new BigFraction(-10, 9),new BigFraction(2, 9)},
                    {new BigFraction(-1, 9),new BigFraction(2, 9),new BigFraction(-1, 9)}
                },
                3
            );
        final Matrix matrix7 =
            new Matrix(
                new int[][] {
                    {2, 1},
                    {1, 2}
                },
                2
            );
        final Matrix matrix8 =
            new Matrix(
                new int[][] {
                    {1, 5},
                    {3, 1}
                },
                2
            );
        final Matrix matrix9 =
            new Matrix(
                new int[][] {
                    {-2, -1},
                    {-1, -2}
                },
                2
            );
        return new Object[][] {
            {
                new MatrixMultiplication(matrix1, matrix2),
                List.of(
                    new MatrixMultiplication(matrix1, matrix2),
                    new Matrix(
                        new int[][] {
                            {43,-62,2},
                            {3,14,-18},
                            {42,24,-50}
                        },
                        3
                    )
                )
            },
            {
                new MatrixMultiplication(matrix5, matrix6),
                List.of(
                    new MatrixMultiplication(matrix5, matrix6),
                    new Matrix(
                        new int[][] {
                            {1,0,0},
                            {0,1,0},
                            {0,0,1}
                        },
                        3
                    )
                )
            },
            {
                new MatrixMultiplication(matrix6, matrix5),
                List.of(
                    new MatrixMultiplication(matrix6, matrix5),
                    new Matrix(
                        new int[][] {
                            {1,0,0},
                            {0,1,0},
                            {0,0,1}
                        },
                        3
                    )
                )
            },
            {
                new MatrixAddition(matrix1, matrix2),
                List.of(
                    new MatrixAddition(matrix1, matrix2),
                    new Matrix(
                        new int[][] {
                            {8,11,-4},
                            {2,-7,1},
                            {14,-9,5}
                        },
                        3
                    )
                )
            },
            {
                new MatrixMultiplication(new MatrixMultiplication(matrix4, matrix3), matrix5),
                List.of(
                    new MatrixMultiplication(new MatrixMultiplication(matrix4, matrix3), matrix5),
                    new MatrixMultiplication(
                        new Matrix(
                            new int[][] {
                                {32,-10,9},
                                {74,-28,21},
                                {116,-46,33}
                            },
                            3
                        ),
                        matrix5
                    ),
                    new Matrix(
                        new int[][] {
                            {86,117,67},
                            {176,243,121},
                            {266,369,175}
                        },
                        3
                    )
                )
            },
            {
                new MatrixMultiplication(matrix8, new MatrixMultiplication(matrix7, matrix9)),
                List.of(
                    new MatrixMultiplication(matrix8, new MatrixMultiplication(matrix7, matrix9)),
                    new MatrixMultiplication(
                        matrix8,
                        new Matrix(
                            new int[][] {
                                {-5,-4},
                                {-4,-5}
                            },
                            2
                        )
                    ),
                    new Matrix(
                        new int[][] {
                            {-25,-29},
                            {-19,-17}
                        },
                        2
                    )
                )
            },
            {
                new MatrixMultiplication(matrix8, new MatrixAddition(matrix7, matrix9)),
                List.of(
                    new MatrixMultiplication(matrix8, new MatrixAddition(matrix7, matrix9)),
                    new MatrixMultiplication(
                        matrix8,
                        new Matrix(
                            new int[][] {
                                {0,0},
                                {0,0}
                            },
                            2
                        )
                    ),
                    new Matrix(
                        new int[][] {
                            {0,0},
                            {0,0}
                        },
                        2
                    )
                )
            }
        };
    }

    @Test(dataProvider="matrixArithmeticData")
    public void matrixArithmeticTest(final MatrixTerm problem, final List<MatrixTerm> expected) {
        final List<MatrixTerm> result = MatrixArithmeticAlgorithm.applyMatrixArithmetic(problem);
        Assert.assertEquals(result, expected);
    }

}
