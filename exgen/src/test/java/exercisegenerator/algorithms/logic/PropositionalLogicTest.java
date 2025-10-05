package exercisegenerator.algorithms.logic;

import java.util.*;
import java.util.Optional;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.io.*;
import exercisegenerator.structures.logic.*;

public class PropositionalLogicTest {

    private static Set<String> set(final String... variables) {
        return new TreeSet<String>(Arrays.asList(variables));
    }

    @DataProvider
    public Object[][] dpllData() throws PropositionalFormulaParseException {
        final PropositionalVariable a = new PropositionalVariable("A");
        final PropositionalVariable b = new PropositionalVariable("B");
        final PropositionalVariable c = new PropositionalVariable("C");
        return new Object[][] {
            {ClauseSet.EMPTY, new DPLLNode(ClauseSet.EMPTY)},
            {ClauseSet.FALSE, new DPLLNode(ClauseSet.FALSE)},
            {DPLL.parseClauses("{},{A}"), new DPLLNode(DPLL.parseClauses("{},{A}"))},
            {
                DPLL.parseClauses("{A}"),
                new DPLLNode(
                    DPLL.parseClauses("{A}"),
                    Optional.of(new DPLLAssignment(a, true, new DPLLNode(ClauseSet.EMPTY)))
                )
            },
            {
                DPLL.parseClauses("{!A}"),
                new DPLLNode(
                    DPLL.parseClauses("{!A}"),
                    Optional.of(new DPLLAssignment(a, false, new DPLLNode(ClauseSet.EMPTY)))
                )
            },
            {
                DPLL.parseClauses("{A},{!A}"),
                new DPLLNode(
                    DPLL.parseClauses("{A},{!A}"),
                    Optional.of(new DPLLAssignment(a, true, new DPLLNode(ClauseSet.FALSE)))
                )
            },
            {
                DPLL.parseClauses("{A},{!B}"),
                new DPLLNode(
                    DPLL.parseClauses("{A},{!B}"),
                    Optional.of(
                        new DPLLAssignment(
                            a,
                            true,
                            new DPLLNode(
                                DPLL.parseClauses("{!B}"),
                                Optional.of(new DPLLAssignment(b, false, new DPLLNode(ClauseSet.EMPTY)))
                            )
                        )
                    )
                )
            },
            {
                DPLL.parseClauses("{!A,B},{!B,C},{!C,!A},{A,!C},{C,!B},{B,A}"),
                new DPLLNode(
                    DPLL.parseClauses("{!A,B},{!B,C},{!C,!A},{A,!C},{B,A}"),
                    Optional.of(
                        new DPLLAssignment(
                            a,
                            true,
                            new DPLLNode(
                                DPLL.parseClauses("{B},{!B,C},{!C}"),
                                Optional.of(
                                    new DPLLAssignment(
                                        b,
                                        true,
                                        new DPLLNode(
                                            DPLL.parseClauses("{C},{!C}"),
                                            Optional.of(new DPLLAssignment(c, true, new DPLLNode(ClauseSet.FALSE)))
                                        )
                                    )
                                )
                            )
                        )
                    ),
                    Optional.of(
                        new DPLLAssignment(
                            a,
                            false,
                            new DPLLNode(
                                DPLL.parseClauses("{!B,C},{!C},{B}"),
                                Optional.of(
                                    new DPLLAssignment(
                                        b,
                                        true,
                                        new DPLLNode(
                                            DPLL.parseClauses("{C},{!C}"),
                                            Optional.of(new DPLLAssignment(c, true, new DPLLNode(ClauseSet.FALSE)))
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            }
        };
    }

    @Test(dataProvider="dpllData")
    public void dpllTest(final ClauseSet clauses, final DPLLNode expected) {
        Assert.assertEquals(DPLL.INSTANCE.apply(clauses), expected);
    }

    @DataProvider
    public Object[][] fromTruthTableData() throws PropositionalFormulaParseException {
        return new Object[][] {
            {
                new TruthTable(PropositionalLogicTest.set("A", "B"), new boolean[] {false, false, false, true}),
                PropositionalFormula.parse("A && B")
            },
            {
                new TruthTable(PropositionalLogicTest.set("A", "B"), new boolean[] {false, true, true, true}),
                PropositionalFormula.parse("!A && B || A && !B || A && B")
            },
            {
                new TruthTable(PropositionalLogicTest.set("A", "B"), new boolean[] {true, false, true, true}),
                PropositionalFormula.parse("!A && !B || A && !B || A && B")
            },
            {
                new TruthTable(PropositionalLogicTest.set("A", "B"), new boolean[] {true, true, true, false}),
                PropositionalFormula.parse("!A && !B || !A && B || A && !B")
            },
            {
                new TruthTable(
                    PropositionalLogicTest.set("A", "B", "C"),
                    new boolean[] {false, false, false, true, true, true, true, true}
                ),
                PropositionalFormula.parse(
                    "!A && B && C || A && !B && !C || A && !B && C || A && B && !C || A && B && C"
                )
            },
            {
                new TruthTable(
                    PropositionalLogicTest.set("A", "B", "C"),
                    new boolean[] {false, true, false, true, false, false, false, true}
                ),
                PropositionalFormula.parse("!A && !B && C || !A && B && C || A && B && C")
            }
        };
    }

    @Test(dataProvider="fromTruthTableData")
    public void fromTruthTableTest(final TruthTable truthTable, final PropositionalFormula formula) {
        Assert.assertEquals(ConversionFromTruthTable.INSTANCE.apply(truthTable), formula);
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
        final PropositionalFormula formula7 = PropositionalFormula.parse("A + B");
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
                        "(!A || B || (!A && (C || !B))) && (!B || B || (!A && (C || !B))) "
                        + "&& (B || !C || B || (!A && (C || !B)))"
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
            },
            {
                formula7,
                List.of(
                    formula7,
                    PropositionalFormula.parse("!(A <-> B)"),
                    PropositionalFormula.parse("!((A -> B) && (B -> A))"),
                    PropositionalFormula.parse("!((!A || B) && (B -> A))"),
                    PropositionalFormula.parse("!((!A || B) && (!B || A))"),
                    PropositionalFormula.parse("(!(!A || B) || !(!B || A))"),
                    PropositionalFormula.parse("((!!A && !B) || !(!B || A))"),
                    PropositionalFormula.parse("((A && !B) || !(!B || A))"),
                    PropositionalFormula.parse("((A && !B) || (!!B && !A))"),
                    PropositionalFormula.parse("((A && !B) || (B && !A))"),
                    PropositionalFormula.parse("((A || (B && !A)) && (!B || (B && !A)))"),
                    PropositionalFormula.parse("((B || A) && (!A || A) && (!B || (B && !A)))"),
                    PropositionalFormula.parse("((B || A) && 1 && (!B || (B && !A)))"),
                    PropositionalFormula.parse("((B || A) && (!B || (B && !A)))"),
                    PropositionalFormula.parse("((B || A) && (B || !B) && (!A || !B))"),
                    PropositionalFormula.parse("((B || A) && 1 && (!A || !B))"),
                    PropositionalFormula.parse("((B || A) && (!A || !B))")
                )
            }
        };
    }

    @Test(dataProvider="toCNFData")
    public void toCNFTest(final PropositionalFormula formula, final List<PropositionalFormula> expected) {
        Assert.assertEquals(ConversionToCNF.INSTANCE.apply(formula), expected);
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
        Assert.assertEquals(ConversionToDNF.INSTANCE.apply(formula), expected);
    }

    @DataProvider
    public Object[][] toTruthTableData() throws PropositionalFormulaParseException {
        return new Object[][] {
            {
                PropositionalFormula.parse("A && B"),
                new TruthTable(PropositionalLogicTest.set("A", "B"), new boolean[] {false, false, false, true})
            },
            {
                PropositionalFormula.parse("A || B"),
                new TruthTable(PropositionalLogicTest.set("A", "B"), new boolean[] {false, true, true, true})
            },
            {
                PropositionalFormula.parse("A || !B"),
                new TruthTable(PropositionalLogicTest.set("A", "B"), new boolean[] {true, false, true, true})
            },
            {
                PropositionalFormula.parse("!(A && B)"),
                new TruthTable(PropositionalLogicTest.set("A", "B"), new boolean[] {true, true, true, false})
            },
            {
                PropositionalFormula.parse("A || B && C"),
                new TruthTable(
                    PropositionalLogicTest.set("A", "B", "C"),
                    new boolean[] {false, false, false, true, true, true, true, true}
                )
            },
            {
                PropositionalFormula.parse("!A && C || B && C"),
                new TruthTable(
                    PropositionalLogicTest.set("A", "B", "C"),
                    new boolean[] {false, true, false, true, false, false, false, true}
                )
            }
        };
    }

    @Test(dataProvider="toTruthTableData")
    public void toTruthTableTest(final PropositionalFormula formula, final TruthTable truthTable) {
        Assert.assertEquals(ConversionToTruthTable.INSTANCE.apply(formula), truthTable);
    }

}
