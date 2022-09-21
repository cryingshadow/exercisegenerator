package exercisegenerator.algorithms;

import java.util.*;

import org.testng.*;
import org.testng.annotations.*;

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
        Assert.assertEquals(PropositionalLogic.fromTruthTable(truthTable), formula);
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
        Assert.assertEquals(PropositionalLogic.toTruthTable(formula), truthTable);
    }

}
