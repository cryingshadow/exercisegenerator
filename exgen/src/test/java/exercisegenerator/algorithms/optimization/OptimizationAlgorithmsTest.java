package exercisegenerator.algorithms.optimization;

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
            },
            {
                problem2,
                List.of(
                    problem2,
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(6,5),new Fraction(-8,5),new Fraction(4,5),new Fraction(-8,5),new Fraction(4,5)},
                            {new Fraction(0),new Fraction(-8),new Fraction(2),new Fraction(0),new Fraction(7),new Fraction(9)},
                            {new Fraction(7),new Fraction(-10),new Fraction(4),new Fraction(8),new Fraction(6),new Fraction(2)},
                            {new Fraction(5),new Fraction(3),new Fraction(-10),new Fraction(10),new Fraction(1),new Fraction(2)},
                            {new Fraction(4),new Fraction(-7),new Fraction(0),new Fraction(4),new Fraction(4),new Fraction(7)},
                            {new Fraction(8),new Fraction(10),new Fraction(2),new Fraction(8),new Fraction(5),new Fraction(1)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(6,5),new Fraction(-8,5),new Fraction(4,5),new Fraction(-8,5),new Fraction(4,5)},
                            {new Fraction(0),new Fraction(-8),new Fraction(2),new Fraction(0),new Fraction(7),new Fraction(9)},
                            {new Fraction(0),new Fraction(-92,5),new Fraction(76,5),new Fraction(12,5),new Fraction(86,5),new Fraction(-18,5)},
                            {new Fraction(5),new Fraction(3),new Fraction(-10),new Fraction(10),new Fraction(1),new Fraction(2)},
                            {new Fraction(4),new Fraction(-7),new Fraction(0),new Fraction(4),new Fraction(4),new Fraction(7)},
                            {new Fraction(8),new Fraction(10),new Fraction(2),new Fraction(8),new Fraction(5),new Fraction(1)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(6,5),new Fraction(-8,5),new Fraction(4,5),new Fraction(-8,5),new Fraction(4,5)},
                            {new Fraction(0),new Fraction(-8),new Fraction(2),new Fraction(0),new Fraction(7),new Fraction(9)},
                            {new Fraction(0),new Fraction(-92,5),new Fraction(76,5),new Fraction(12,5),new Fraction(86,5),new Fraction(-18,5)},
                            {new Fraction(0),new Fraction(-3),new Fraction(-2),new Fraction(6),new Fraction(9),new Fraction(-2)},
                            {new Fraction(4),new Fraction(-7),new Fraction(0),new Fraction(4),new Fraction(4),new Fraction(7)},
                            {new Fraction(8),new Fraction(10),new Fraction(2),new Fraction(8),new Fraction(5),new Fraction(1)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(6,5),new Fraction(-8,5),new Fraction(4,5),new Fraction(-8,5),new Fraction(4,5)},
                            {new Fraction(0),new Fraction(-8),new Fraction(2),new Fraction(0),new Fraction(7),new Fraction(9)},
                            {new Fraction(0),new Fraction(-92,5),new Fraction(76,5),new Fraction(12,5),new Fraction(86,5),new Fraction(-18,5)},
                            {new Fraction(0),new Fraction(-3),new Fraction(-2),new Fraction(6),new Fraction(9),new Fraction(-2)},
                            {new Fraction(0),new Fraction(-59,5),new Fraction(32,5),new Fraction(4,5),new Fraction(52,5),new Fraction(19,5)},
                            {new Fraction(8),new Fraction(10),new Fraction(2),new Fraction(8),new Fraction(5),new Fraction(1)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(6,5),new Fraction(-8,5),new Fraction(4,5),new Fraction(-8,5),new Fraction(4,5)},
                            {new Fraction(0),new Fraction(-8),new Fraction(2),new Fraction(0),new Fraction(7),new Fraction(9)},
                            {new Fraction(0),new Fraction(-92,5),new Fraction(76,5),new Fraction(12,5),new Fraction(86,5),new Fraction(-18,5)},
                            {new Fraction(0),new Fraction(-3),new Fraction(-2),new Fraction(6),new Fraction(9),new Fraction(-2)},
                            {new Fraction(0),new Fraction(-59,5),new Fraction(32,5),new Fraction(4,5),new Fraction(52,5),new Fraction(19,5)},
                            {new Fraction(0),new Fraction(2,5),new Fraction(74,5),new Fraction(8,5),new Fraction(89,5),new Fraction(-27,5)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(6,5),new Fraction(-8,5),new Fraction(4,5),new Fraction(-8,5),new Fraction(4,5)},
                            {new Fraction(0),new Fraction(1),new Fraction(-1,4),new Fraction(0),new Fraction(-7,8),new Fraction(-9,8)},
                            {new Fraction(0),new Fraction(-92,5),new Fraction(76,5),new Fraction(12,5),new Fraction(86,5),new Fraction(-18,5)},
                            {new Fraction(0),new Fraction(-3),new Fraction(-2),new Fraction(6),new Fraction(9),new Fraction(-2)},
                            {new Fraction(0),new Fraction(-59,5),new Fraction(32,5),new Fraction(4,5),new Fraction(52,5),new Fraction(19,5)},
                            {new Fraction(0),new Fraction(2,5),new Fraction(74,5),new Fraction(8,5),new Fraction(89,5),new Fraction(-27,5)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(0),new Fraction(-13,10),new Fraction(4,5),new Fraction(-11,20),new Fraction(43,20)},
                            {new Fraction(0),new Fraction(1),new Fraction(-1,4),new Fraction(0),new Fraction(-7,8),new Fraction(-9,8)},
                            {new Fraction(0),new Fraction(-92,5),new Fraction(76,5),new Fraction(12,5),new Fraction(86,5),new Fraction(-18,5)},
                            {new Fraction(0),new Fraction(-3),new Fraction(-2),new Fraction(6),new Fraction(9),new Fraction(-2)},
                            {new Fraction(0),new Fraction(-59,5),new Fraction(32,5),new Fraction(4,5),new Fraction(52,5),new Fraction(19,5)},
                            {new Fraction(0),new Fraction(2,5),new Fraction(74,5),new Fraction(8,5),new Fraction(89,5),new Fraction(-27,5)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(0),new Fraction(-13,10),new Fraction(4,5),new Fraction(-11,20),new Fraction(43,20)},
                            {new Fraction(0),new Fraction(1),new Fraction(-1,4),new Fraction(0),new Fraction(-7,8),new Fraction(-9,8)},
                            {new Fraction(0),new Fraction(0),new Fraction(53,5),new Fraction(12,5),new Fraction(11,10),new Fraction(-243,10)},
                            {new Fraction(0),new Fraction(-3),new Fraction(-2),new Fraction(6),new Fraction(9),new Fraction(-2)},
                            {new Fraction(0),new Fraction(-59,5),new Fraction(32,5),new Fraction(4,5),new Fraction(52,5),new Fraction(19,5)},
                            {new Fraction(0),new Fraction(2,5),new Fraction(74,5),new Fraction(8,5),new Fraction(89,5),new Fraction(-27,5)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(0),new Fraction(-13,10),new Fraction(4,5),new Fraction(-11,20),new Fraction(43,20)},
                            {new Fraction(0),new Fraction(1),new Fraction(-1,4),new Fraction(0),new Fraction(-7,8),new Fraction(-9,8)},
                            {new Fraction(0),new Fraction(0),new Fraction(53,5),new Fraction(12,5),new Fraction(11,10),new Fraction(-243,10)},
                            {new Fraction(0),new Fraction(0),new Fraction(-11,4),new Fraction(6),new Fraction(51,8),new Fraction(-43,8)},
                            {new Fraction(0),new Fraction(-59,5),new Fraction(32,5),new Fraction(4,5),new Fraction(52,5),new Fraction(19,5)},
                            {new Fraction(0),new Fraction(2,5),new Fraction(74,5),new Fraction(8,5),new Fraction(89,5),new Fraction(-27,5)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(0),new Fraction(-13,10),new Fraction(4,5),new Fraction(-11,20),new Fraction(43,20)},
                            {new Fraction(0),new Fraction(1),new Fraction(-1,4),new Fraction(0),new Fraction(-7,8),new Fraction(-9,8)},
                            {new Fraction(0),new Fraction(0),new Fraction(53,5),new Fraction(12,5),new Fraction(11,10),new Fraction(-243,10)},
                            {new Fraction(0),new Fraction(0),new Fraction(-11,4),new Fraction(6),new Fraction(51,8),new Fraction(-43,8)},
                            {new Fraction(0),new Fraction(0),new Fraction(69,20),new Fraction(4,5),new Fraction(3,40),new Fraction(-379,40)},
                            {new Fraction(0),new Fraction(2,5),new Fraction(74,5),new Fraction(8,5),new Fraction(89,5),new Fraction(-27,5)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(0),new Fraction(-13,10),new Fraction(4,5),new Fraction(-11,20),new Fraction(43,20)},
                            {new Fraction(0),new Fraction(1),new Fraction(-1,4),new Fraction(0),new Fraction(-7,8),new Fraction(-9,8)},
                            {new Fraction(0),new Fraction(0),new Fraction(53,5),new Fraction(12,5),new Fraction(11,10),new Fraction(-243,10)},
                            {new Fraction(0),new Fraction(0),new Fraction(-11,4),new Fraction(6),new Fraction(51,8),new Fraction(-43,8)},
                            {new Fraction(0),new Fraction(0),new Fraction(69,20),new Fraction(4,5),new Fraction(3,40),new Fraction(-379,40)},
                            {new Fraction(0),new Fraction(0),new Fraction(149,10),new Fraction(8,5),new Fraction(363,20),new Fraction(-99,20)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(0),new Fraction(-13,10),new Fraction(4,5),new Fraction(-11,20),new Fraction(43,20)},
                            {new Fraction(0),new Fraction(1),new Fraction(-1,4),new Fraction(0),new Fraction(-7,8),new Fraction(-9,8)},
                            {new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(12,53),new Fraction(11,106),new Fraction(-243,106)},
                            {new Fraction(0),new Fraction(0),new Fraction(-11,4),new Fraction(6),new Fraction(51,8),new Fraction(-43,8)},
                            {new Fraction(0),new Fraction(0),new Fraction(69,20),new Fraction(4,5),new Fraction(3,40),new Fraction(-379,40)},
                            {new Fraction(0),new Fraction(0),new Fraction(149,10),new Fraction(8,5),new Fraction(363,20),new Fraction(-99,20)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(58,53),new Fraction(-22,53),new Fraction(-44,53)},
                            {new Fraction(0),new Fraction(1),new Fraction(-1,4),new Fraction(0),new Fraction(-7,8),new Fraction(-9,8)},
                            {new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(12,53),new Fraction(11,106),new Fraction(-243,106)},
                            {new Fraction(0),new Fraction(0),new Fraction(-11,4),new Fraction(6),new Fraction(51,8),new Fraction(-43,8)},
                            {new Fraction(0),new Fraction(0),new Fraction(69,20),new Fraction(4,5),new Fraction(3,40),new Fraction(-379,40)},
                            {new Fraction(0),new Fraction(0),new Fraction(149,10),new Fraction(8,5),new Fraction(363,20),new Fraction(-99,20)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(58,53),new Fraction(-22,53),new Fraction(-44,53)},
                            {new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(3,53),new Fraction(-45,53),new Fraction(-90,53)},
                            {new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(12,53),new Fraction(11,106),new Fraction(-243,106)},
                            {new Fraction(0),new Fraction(0),new Fraction(-11,4),new Fraction(6),new Fraction(51,8),new Fraction(-43,8)},
                            {new Fraction(0),new Fraction(0),new Fraction(69,20),new Fraction(4,5),new Fraction(3,40),new Fraction(-379,40)},
                            {new Fraction(0),new Fraction(0),new Fraction(149,10),new Fraction(8,5),new Fraction(363,20),new Fraction(-99,20)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(58,53),new Fraction(-22,53),new Fraction(-44,53)},
                            {new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(3,53),new Fraction(-45,53),new Fraction(-90,53)},
                            {new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(12,53),new Fraction(11,106),new Fraction(-243,106)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(351,53),new Fraction(353,53),new Fraction(-619,53)},
                            {new Fraction(0),new Fraction(0),new Fraction(69,20),new Fraction(4,5),new Fraction(3,40),new Fraction(-379,40)},
                            {new Fraction(0),new Fraction(0),new Fraction(149,10),new Fraction(8,5),new Fraction(363,20),new Fraction(-99,20)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(58,53),new Fraction(-22,53),new Fraction(-44,53)},
                            {new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(3,53),new Fraction(-45,53),new Fraction(-90,53)},
                            {new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(12,53),new Fraction(11,106),new Fraction(-243,106)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(351,53),new Fraction(353,53),new Fraction(-619,53)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(1,53),new Fraction(-15,53),new Fraction(-83,53)},
                            {new Fraction(0),new Fraction(0),new Fraction(149,10),new Fraction(8,5),new Fraction(363,20),new Fraction(-99,20)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(58,53),new Fraction(-22,53),new Fraction(-44,53)},
                            {new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(3,53),new Fraction(-45,53),new Fraction(-90,53)},
                            {new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(12,53),new Fraction(11,106),new Fraction(-243,106)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(351,53),new Fraction(353,53),new Fraction(-619,53)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(1,53),new Fraction(-15,53),new Fraction(-83,53)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(-94,53),new Fraction(880,53),new Fraction(1548,53)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(58,53),new Fraction(-22,53),new Fraction(-44,53)},
                            {new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(3,53),new Fraction(-45,53),new Fraction(-90,53)},
                            {new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(12,53),new Fraction(11,106),new Fraction(-243,106)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(353,351),new Fraction(-619,351)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(1,53),new Fraction(-15,53),new Fraction(-83,53)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(-94,53),new Fraction(880,53),new Fraction(1548,53)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(-532,351),new Fraction(386,351)},
                            {new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(3,53),new Fraction(-45,53),new Fraction(-90,53)},
                            {new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(12,53),new Fraction(11,106),new Fraction(-243,106)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(353,351),new Fraction(-619,351)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(1,53),new Fraction(-15,53),new Fraction(-83,53)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(-94,53),new Fraction(880,53),new Fraction(1548,53)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(-532,351),new Fraction(386,351)},
                            {new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(-106,117),new Fraction(-187,117)},
                            {new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(12,53),new Fraction(11,106),new Fraction(-243,106)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(353,351),new Fraction(-619,351)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(1,53),new Fraction(-15,53),new Fraction(-83,53)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(-94,53),new Fraction(880,53),new Fraction(1548,53)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(-532,351),new Fraction(386,351)},
                            {new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(-106,117),new Fraction(-187,117)},
                            {new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(-29,234),new Fraction(-443,234)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(353,351),new Fraction(-619,351)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(1,53),new Fraction(-15,53),new Fraction(-83,53)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(-94,53),new Fraction(880,53),new Fraction(1548,53)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(-532,351),new Fraction(386,351)},
                            {new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(-106,117),new Fraction(-187,117)},
                            {new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(-29,234),new Fraction(-443,234)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(353,351),new Fraction(-619,351)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(-106,351),new Fraction(-538,351)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(-94,53),new Fraction(880,53),new Fraction(1548,53)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(-532,351),new Fraction(386,351)},
                            {new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(-106,117),new Fraction(-187,117)},
                            {new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(-29,234),new Fraction(-443,234)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(353,351),new Fraction(-619,351)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(-106,351),new Fraction(-538,351)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(6454,351),new Fraction(9154,351)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(-532,351),new Fraction(386,351)},
                            {new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(-106,117),new Fraction(-187,117)},
                            {new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(-29,234),new Fraction(-443,234)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(353,351),new Fraction(-619,351)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(269,53)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(6454,351),new Fraction(9154,351)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(466,53)},
                            {new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(-106,117),new Fraction(-187,117)},
                            {new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(-29,234),new Fraction(-443,234)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(353,351),new Fraction(-619,351)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(269,53)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(6454,351),new Fraction(9154,351)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(466,53)},
                            {new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(3)},
                            {new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(-29,234),new Fraction(-443,234)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(353,351),new Fraction(-619,351)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(269,53)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(6454,351),new Fraction(9154,351)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(466,53)},
                            {new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(3)},
                            {new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(-67,53)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(353,351),new Fraction(-619,351)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(269,53)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(6454,351),new Fraction(9154,351)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(466,53)},
                            {new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(3)},
                            {new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(-67,53)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(-364,53)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(269,53)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(6454,351),new Fraction(9154,351)}
                        }
                    ),
                    new LinearSystemOfEquations(
                        new Fraction[][] {
                            {new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(466,53)},
                            {new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(3)},
                            {new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(0),new Fraction(-67,53)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(0),new Fraction(-364,53)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(1),new Fraction(269,53)},
                            {new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(0),new Fraction(-3564,53)}
                        }
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
        final SimplexSolution result = SimplexAlgorithm.simplex(problem);
        Assert.assertEquals(result, expected);
    }

}
