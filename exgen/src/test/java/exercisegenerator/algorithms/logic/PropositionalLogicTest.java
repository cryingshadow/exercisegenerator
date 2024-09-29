package exercisegenerator.algorithms.logic;

import java.util.*;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.io.*;
import exercisegenerator.structures.logic.*;

public class PropositionalLogicTest {

    @DataProvider
    public Object[][] fromTruthTableData() throws PropositionalFormulaParseException {
        return new Object[][] {
            {
                new TruthTable(Arrays.asList("A", "B"), new boolean[] {false, false, false, true}),
                PropositionalFormula.parse("A && B")
            },
            {
                new TruthTable(Arrays.asList("A", "B"), new boolean[] {false, true, true, true}),
                PropositionalFormula.parse("!A && B || A && !B || A && B")
            },
            {
                new TruthTable(Arrays.asList("A", "B"), new boolean[] {true, false, true, true}),
                PropositionalFormula.parse("!A && !B || A && !B || A && B")
            },
            {
                new TruthTable(Arrays.asList("A", "B"), new boolean[] {true, true, true, false}),
                PropositionalFormula.parse("!A && !B || !A && B || A && !B")
            },
            {
                new TruthTable(
                    Arrays.asList("A", "B", "C"),
                    new boolean[] {false, false, false, true, true, true, true, true}
                ),
                PropositionalFormula.parse(
                    "!A && B && C || A && !B && !C || A && !B && C || A && B && !C || A && B && C"
                )
            },
            {
                new TruthTable(
                    Arrays.asList("A", "B", "C"),
                    new boolean[] {false, true, false, true, false, false, false, true}
                ),
                PropositionalFormula.parse("!A && !B && C || !A && B && C || A && B && C")
            }
        };
    }

    @Test(dataProvider="fromTruthTableData")
    public void fromTruthTableTest(final TruthTable truthTable, final PropositionalFormula formula) {
        Assert.assertEquals(ConversionFromTruthTable.fromTruthTable(truthTable), formula);
    }

    @DataProvider
    public Object[][] toCNFData() throws PropositionalFormulaParseException {
        final PropositionalFormula formula1 = PropositionalFormula.parse("A && B");
        final PropositionalFormula formula2 = PropositionalFormula.parse("A || B");
        final PropositionalFormula formula3 = PropositionalFormula.parse("!A");
        final PropositionalFormula formula4 = PropositionalFormula.parse("(A || !B) && (B || !A)");
        final PropositionalFormula formula5 = PropositionalFormula.parse("!(A && B)");
        final PropositionalFormula formula6 =
            PropositionalFormula.parse("!((A || B || !(B || !C)) && !(B || (!A && (C || !B))))");
        return new Object[][] {
            {formula1, List.of(formula1)},
            {formula2, List.of(formula2)},
            {formula3, List.of(formula3)},
            {formula4, List.of(formula4)},
            {formula5, List.of(formula5, PropositionalFormula.parse("!A || !B"))},
            {
                formula6,
                List.of(
                    formula6,
                    PropositionalFormula.parse("!(A || B || !(B || !C)) || !!(B || (!A && (C || !B)))"),
                    PropositionalFormula.parse("!(A || B || !(B || !C)) || B || (!A && (C || !B))"),
                    PropositionalFormula.parse("(!A && !B && !!(B || !C)) || B || (!A && (C || !B))"),
                    PropositionalFormula.parse("(!A && !B && (B || !C)) || B || (!A && (C || !B))"),
                    PropositionalFormula.parse(
                        "(!A || B || (!A && (C || !B))) && (!B || B || (!A && (C || !B))) && (B || !C || B || (!A && (C || !B)))"
                    ),
                    PropositionalFormula.parse(
                        "(!A || B) && (!B || B || (!A && (C || !B))) && (B || !C || B || (!A && (C || !B)))"
                    ),
                    PropositionalFormula.parse("(!A || B) && 1 && (B || !C || B || (!A && (C || !B)))"),
                    PropositionalFormula.parse("(!A || B) && (B || !C || B || (!A && (C || !B)))"),
                    PropositionalFormula.parse("(!A || B) && (B || !C || (!A && (C || !B)))"),
                    PropositionalFormula.parse("(!A || B) && (!A || B || !C) && (C || !B || B || !C)"),
                    PropositionalFormula.parse("(!A || B) && (C || !B || B || !C)"),
                    PropositionalFormula.parse("(!A || B) && 1"),
                    PropositionalFormula.parse("!A || B")
                )
            }
        };
    }

    @Test(dataProvider="toCNFData")
    public void toCNFTest(final PropositionalFormula formula, final List<PropositionalFormula> expected) {
        Assert.assertEquals(ConversionToCNF.toCNF(formula), expected);
    }

    @DataProvider
    public Object[][] toDNFData() throws PropositionalFormulaParseException {
        final PropositionalFormula formula1 = PropositionalFormula.parse("A && B");
        final PropositionalFormula formula2 = PropositionalFormula.parse("A || B");
        final PropositionalFormula formula3 = PropositionalFormula.parse("!A");
        final PropositionalFormula formula4 = PropositionalFormula.parse("(A && !B) || (B && !A)");
        final PropositionalFormula formula5 = PropositionalFormula.parse("!(A && B)");
        final PropositionalFormula formula6 =
            PropositionalFormula.parse("!((A || B || !(B || !C)) && !(B || (!A && (C || !B))))");
        return new Object[][] {
            {formula1, List.of(formula1)},
            {formula2, List.of(formula2)},
            {formula3, List.of(formula3)},
            {formula4, List.of(formula4)},
            {formula5, List.of(formula5, PropositionalFormula.parse("!A || !B"))},
            {
                formula6,
                List.of(
                    formula6,
                    PropositionalFormula.parse("!(A || B || !(B || !C)) || !!(B || (!A && (C || !B)))"),
                    PropositionalFormula.parse("!(A || B || !(B || !C)) || B || (!A && (C || !B))"),
                    PropositionalFormula.parse("(!A && !B && !!(B || !C)) || B || (!A && (C || !B))"),
                    PropositionalFormula.parse("(!A && !B && (B || !C)) || B || (!A && (C || !B))"),
                    PropositionalFormula.parse("(B && !A && !B) || (!C && !A && !B) || B || (!A && (C || !B))"),
                    PropositionalFormula.parse("(!C && !A && !B) || B || (!A && (C || !B))"),
                    PropositionalFormula.parse("(!C && !A && !B) || B || (C && !A) || (!B && !A)"),
                    PropositionalFormula.parse("B || (C && !A) || (!B && !A)")
                )
            }
        };
    }

    @Test(dataProvider="toDNFData")
    public void toDNFTest(final PropositionalFormula formula, final List<PropositionalFormula> expected) {
        Assert.assertEquals(ConversionToDNF.toDNF(formula), expected);
    }

    @DataProvider
    public Object[][] toTruthTableData() throws PropositionalFormulaParseException {
        return new Object[][] {
            {
                PropositionalFormula.parse("A && B"),
                new TruthTable(Arrays.asList("A", "B"), new boolean[] {false, false, false, true})
            },
            {
                PropositionalFormula.parse("A || B"),
                new TruthTable(Arrays.asList("A", "B"), new boolean[] {false, true, true, true})
            },
            {
                PropositionalFormula.parse("A || !B"),
                new TruthTable(Arrays.asList("A", "B"), new boolean[] {true, false, true, true})
            },
            {
                PropositionalFormula.parse("!(A && B)"),
                new TruthTable(Arrays.asList("A", "B"), new boolean[] {true, true, true, false})
            },
            {
                PropositionalFormula.parse("A || B && C"),
                new TruthTable(
                    Arrays.asList("A", "B", "C"),
                    new boolean[] {false, false, false, true, true, true, true, true}
                )
            },
            {
                PropositionalFormula.parse("!A && C || B && C"),
                new TruthTable(
                    Arrays.asList("A", "B", "C"),
                    new boolean[] {false, true, false, true, false, false, false, true}
                )
            }
        };
    }

    @Test(dataProvider="toTruthTableData")
    public void toTruthTableTest(final PropositionalFormula formula, final TruthTable truthTable) {
        Assert.assertEquals(ConversionToTruthTable.toTruthTable(formula), truthTable);
    }

}
