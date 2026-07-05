package exercisegenerator.algorithms.learning;

import java.util.*;
import java.util.stream.*;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.structures.graphs.petrinets.*;

public class AlphaAlgorithmTest {

    private static final FootprintMatrix FOOTPRINT2 =
        new FootprintMatrix()
        .put(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
        .put(AlphaAlgorithm.END_ACTIVITY, AlphaAlgorithm.END_ACTIVITY, FootprintRelation.EXCLUDED)
        .put("a", "a", FootprintRelation.EXCLUDED)
        .put("b", "b", FootprintRelation.EXCLUDED)
        .put("c", "c", FootprintRelation.EXCLUDED)
        .put("d", "d", FootprintRelation.EXCLUDED)
        .put("e", "e", FootprintRelation.EXCLUDED)
        .put("a", "b", FootprintRelation.FOLLOWED)
        .put("a", "c", FootprintRelation.FOLLOWED)
        .put("a", "d", FootprintRelation.EXCLUDED)
        .put("a", "e", FootprintRelation.EXCLUDED)
        .put("b", "a", FootprintRelation.PRECDEDED)
        .put("b", "c", FootprintRelation.MUTUAL)
        .put("b", "d", FootprintRelation.MUTUAL)
        .put("b", "e", FootprintRelation.FOLLOWED)
        .put("c", "a", FootprintRelation.PRECDEDED)
        .put("c", "b", FootprintRelation.MUTUAL)
        .put("c", "d", FootprintRelation.MUTUAL)
        .put("c", "e", FootprintRelation.FOLLOWED)
        .put("d", "a", FootprintRelation.EXCLUDED)
        .put("d", "b", FootprintRelation.MUTUAL)
        .put("d", "c", FootprintRelation.MUTUAL)
        .put("d", "e", FootprintRelation.EXCLUDED)
        .put("e", "a", FootprintRelation.EXCLUDED)
        .put("e", "b", FootprintRelation.PRECDEDED)
        .put("e", "c", FootprintRelation.PRECDEDED)
        .put("e", "d", FootprintRelation.EXCLUDED)
        .put(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.END_ACTIVITY, FootprintRelation.EXCLUDED)
        .put(AlphaAlgorithm.END_ACTIVITY, AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
        .put(AlphaAlgorithm.START_ACTIVITY, "a", FootprintRelation.FOLLOWED)
        .put(AlphaAlgorithm.END_ACTIVITY, "a", FootprintRelation.EXCLUDED)
        .put("a", AlphaAlgorithm.START_ACTIVITY, FootprintRelation.PRECDEDED)
        .put("a", AlphaAlgorithm.END_ACTIVITY, FootprintRelation.EXCLUDED)
        .put(AlphaAlgorithm.START_ACTIVITY, "b", FootprintRelation.EXCLUDED)
        .put(AlphaAlgorithm.END_ACTIVITY, "b", FootprintRelation.EXCLUDED)
        .put("b", AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
        .put("b", AlphaAlgorithm.END_ACTIVITY, FootprintRelation.EXCLUDED)
        .put(AlphaAlgorithm.START_ACTIVITY, "c", FootprintRelation.EXCLUDED)
        .put(AlphaAlgorithm.END_ACTIVITY, "c", FootprintRelation.EXCLUDED)
        .put("c", AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
        .put("c", AlphaAlgorithm.END_ACTIVITY, FootprintRelation.EXCLUDED)
        .put(AlphaAlgorithm.START_ACTIVITY, "d", FootprintRelation.EXCLUDED)
        .put(AlphaAlgorithm.END_ACTIVITY, "d", FootprintRelation.EXCLUDED)
        .put("d", AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
        .put("d", AlphaAlgorithm.END_ACTIVITY, FootprintRelation.EXCLUDED)
        .put(AlphaAlgorithm.START_ACTIVITY, "e", FootprintRelation.EXCLUDED)
        .put(AlphaAlgorithm.END_ACTIVITY, "e", FootprintRelation.PRECDEDED)
        .put("e", AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
        .put("e", AlphaAlgorithm.END_ACTIVITY, FootprintRelation.FOLLOWED);


    @DataProvider
    public Object[][] computeAdmissibleActivitySubsetsData() {
        return new Object[][] {
            {
                new FootprintMatrix()
                .put(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.END_ACTIVITY, AlphaAlgorithm.END_ACTIVITY, FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.END_ACTIVITY, FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.END_ACTIVITY, AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED),
                Set.of(
                    Set.of(AlphaAlgorithm.START_ACTIVITY),
                    Set.of(AlphaAlgorithm.END_ACTIVITY),
                    Set.of(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.END_ACTIVITY)
                )
            },
            {
                new FootprintMatrix()
                .put(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.END_ACTIVITY, AlphaAlgorithm.END_ACTIVITY, FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.END_ACTIVITY, FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.END_ACTIVITY, AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
                .put("a", "a", FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.START_ACTIVITY, "a", FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.END_ACTIVITY, "a", FootprintRelation.EXCLUDED)
                .put("a", AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
                .put("a", AlphaAlgorithm.END_ACTIVITY, FootprintRelation.EXCLUDED),
                Set.of(
                    Set.of(AlphaAlgorithm.START_ACTIVITY),
                    Set.of(AlphaAlgorithm.END_ACTIVITY),
                    Set.of("a"),
                    Set.of(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.END_ACTIVITY),
                    Set.of(AlphaAlgorithm.START_ACTIVITY, "a"),
                    Set.of("a", AlphaAlgorithm.END_ACTIVITY),
                    Set.of("a", AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.END_ACTIVITY)
                )
            },
            {
                new FootprintMatrix()
                .put(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.END_ACTIVITY, AlphaAlgorithm.END_ACTIVITY, FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.END_ACTIVITY, FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.END_ACTIVITY, AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
                .put("a", "a", FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.START_ACTIVITY, "a", FootprintRelation.FOLLOWED)
                .put(AlphaAlgorithm.END_ACTIVITY, "a", FootprintRelation.PRECDEDED)
                .put("a", AlphaAlgorithm.START_ACTIVITY, FootprintRelation.PRECDEDED)
                .put("a", AlphaAlgorithm.END_ACTIVITY, FootprintRelation.FOLLOWED),
                Set.of(
                    Set.of(AlphaAlgorithm.START_ACTIVITY),
                    Set.of(AlphaAlgorithm.END_ACTIVITY),
                    Set.of("a"),
                    Set.of(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.END_ACTIVITY)
                )
            },
            {
                new FootprintMatrix()
                .put(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.END_ACTIVITY, AlphaAlgorithm.END_ACTIVITY, FootprintRelation.EXCLUDED)
                .put("a", "a", FootprintRelation.EXCLUDED)
                .put("b", "b", FootprintRelation.EXCLUDED)
                .put("c", "c", FootprintRelation.MUTUAL)
                .put("a", "b", FootprintRelation.FOLLOWED)
                .put("a", "c", FootprintRelation.FOLLOWED)
                .put("b", "a", FootprintRelation.PRECDEDED)
                .put("b", "c", FootprintRelation.PRECDEDED)
                .put("c", "a", FootprintRelation.PRECDEDED)
                .put("c", "b", FootprintRelation.FOLLOWED)
                .put(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.END_ACTIVITY, FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.END_ACTIVITY, AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.START_ACTIVITY, "a", FootprintRelation.FOLLOWED)
                .put(AlphaAlgorithm.END_ACTIVITY, "a", FootprintRelation.PRECDEDED)
                .put("a", AlphaAlgorithm.START_ACTIVITY, FootprintRelation.PRECDEDED)
                .put("a", AlphaAlgorithm.END_ACTIVITY, FootprintRelation.FOLLOWED)
                .put(AlphaAlgorithm.START_ACTIVITY, "b", FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.END_ACTIVITY, "b", FootprintRelation.PRECDEDED)
                .put("b", AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
                .put("b", AlphaAlgorithm.END_ACTIVITY, FootprintRelation.FOLLOWED)
                .put(AlphaAlgorithm.START_ACTIVITY, "c", FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.END_ACTIVITY, "c", FootprintRelation.EXCLUDED)
                .put("c", AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
                .put("c", AlphaAlgorithm.END_ACTIVITY, FootprintRelation.EXCLUDED),
                Set.of(
                    Set.of(AlphaAlgorithm.START_ACTIVITY),
                    Set.of(AlphaAlgorithm.END_ACTIVITY),
                    Set.of("a"),
                    Set.of("b"),
                    Set.of(AlphaAlgorithm.START_ACTIVITY, "b"),
                    Set.of(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.END_ACTIVITY)
                )
            },
            {
                AlphaAlgorithmTest.FOOTPRINT2,
                Set.of(
                    Set.of(AlphaAlgorithm.START_ACTIVITY),
                    Set.of("a"),
                    Set.of("b"),
                    Set.of("c"),
                    Set.of("d"),
                    Set.of("e"),
                    Set.of(AlphaAlgorithm.END_ACTIVITY),
                    Set.of(AlphaAlgorithm.START_ACTIVITY, "b"),
                    Set.of(AlphaAlgorithm.START_ACTIVITY, "c"),
                    Set.of(AlphaAlgorithm.START_ACTIVITY, "d"),
                    Set.of(AlphaAlgorithm.START_ACTIVITY, "e"),
                    Set.of(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.END_ACTIVITY),
                    Set.of("a", "d"),
                    Set.of("a", "e"),
                    Set.of("a", AlphaAlgorithm.END_ACTIVITY),
                    Set.of("b", AlphaAlgorithm.END_ACTIVITY),
                    Set.of("c", AlphaAlgorithm.END_ACTIVITY),
                    Set.of("d", "e"),
                    Set.of("d", AlphaAlgorithm.END_ACTIVITY),
                    Set.of(AlphaAlgorithm.START_ACTIVITY, "b", AlphaAlgorithm.END_ACTIVITY),
                    Set.of(AlphaAlgorithm.START_ACTIVITY, "c", AlphaAlgorithm.END_ACTIVITY),
                    Set.of(AlphaAlgorithm.START_ACTIVITY, "d", "e"),
                    Set.of(AlphaAlgorithm.START_ACTIVITY, "d", AlphaAlgorithm.END_ACTIVITY),
                    Set.of("a", "d", "e"),
                    Set.of("a", "d", AlphaAlgorithm.END_ACTIVITY)
                )
            }
        };
    }

    @Test(dataProvider="computeAdmissibleActivitySubsetsData")
    public void computeAdmissibleActivitySubsetsTest(
        final FootprintMatrix footprint,
        final Set<Set<String>> expected
    ) {
        Assert.assertEquals(AlphaAlgorithm.computeAdmissibleActivitySubsets(footprint), expected);
    }

    @DataProvider
    public Object[][] computeAdmissiblePlacesData() {
        return new Object[][] {
            {
                Set.of(),
                new FootprintMatrix()
                .put(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.END_ACTIVITY, AlphaAlgorithm.END_ACTIVITY, FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.END_ACTIVITY, FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.END_ACTIVITY, AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED),
                Set.of()
            },
            {
                Set.of(
                    Set.of(AlphaAlgorithm.START_ACTIVITY),
                    Set.of(AlphaAlgorithm.END_ACTIVITY),
                    Set.of("a"),
                    Set.of(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.END_ACTIVITY)
                ),
                new FootprintMatrix()
                .put(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.END_ACTIVITY, AlphaAlgorithm.END_ACTIVITY, FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.END_ACTIVITY, FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.END_ACTIVITY, AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
                .put("a", "a", FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.START_ACTIVITY, "a", FootprintRelation.FOLLOWED)
                .put(AlphaAlgorithm.END_ACTIVITY, "a", FootprintRelation.PRECDEDED)
                .put("a", AlphaAlgorithm.START_ACTIVITY, FootprintRelation.PRECDEDED)
                .put("a", AlphaAlgorithm.END_ACTIVITY, FootprintRelation.FOLLOWED),
                Set.of(
                    new PlaceCandidate(
                        Set.of(AlphaAlgorithm.START_ACTIVITY),
                        Set.of("a")
                    ),
                    new PlaceCandidate(
                        Set.of("a"),
                        Set.of(AlphaAlgorithm.END_ACTIVITY)
                    )
                )
            },
            {
                Set.of(
                    Set.of(AlphaAlgorithm.START_ACTIVITY),
                    Set.of(AlphaAlgorithm.END_ACTIVITY),
                    Set.of("a"),
                    Set.of("b"),
                    Set.of(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.END_ACTIVITY)
                ),
                new FootprintMatrix()
                .put(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.END_ACTIVITY, AlphaAlgorithm.END_ACTIVITY, FootprintRelation.EXCLUDED)
                .put("a", "a", FootprintRelation.EXCLUDED)
                .put("b", "b", FootprintRelation.EXCLUDED)
                .put("a", "b", FootprintRelation.MUTUAL)
                .put("b", "a", FootprintRelation.MUTUAL)
                .put(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.END_ACTIVITY, FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.END_ACTIVITY, AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.START_ACTIVITY, "a", FootprintRelation.FOLLOWED)
                .put(AlphaAlgorithm.END_ACTIVITY, "a", FootprintRelation.PRECDEDED)
                .put("a", AlphaAlgorithm.START_ACTIVITY, FootprintRelation.PRECDEDED)
                .put("a", AlphaAlgorithm.END_ACTIVITY, FootprintRelation.FOLLOWED)
                .put(AlphaAlgorithm.START_ACTIVITY, "b", FootprintRelation.FOLLOWED)
                .put(AlphaAlgorithm.END_ACTIVITY, "b", FootprintRelation.PRECDEDED)
                .put("b", AlphaAlgorithm.START_ACTIVITY, FootprintRelation.PRECDEDED)
                .put("b", AlphaAlgorithm.END_ACTIVITY, FootprintRelation.FOLLOWED),
                Set.of(
                    new PlaceCandidate(
                        Set.of(AlphaAlgorithm.START_ACTIVITY),
                        Set.of("a")
                    ),
                    new PlaceCandidate(
                        Set.of("a"),
                        Set.of(AlphaAlgorithm.END_ACTIVITY)
                    ),
                    new PlaceCandidate(
                        Set.of(AlphaAlgorithm.START_ACTIVITY),
                        Set.of("b")
                    ),
                    new PlaceCandidate(
                        Set.of("b"),
                        Set.of(AlphaAlgorithm.END_ACTIVITY)
                    )
                )
            },
            {
                Set.of(
                    Set.of(AlphaAlgorithm.START_ACTIVITY),
                    Set.of(AlphaAlgorithm.END_ACTIVITY),
                    Set.of("a"),
                    Set.of("b"),
                    Set.of("a", "b"),
                    Set.of(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.END_ACTIVITY)
                ),
                new FootprintMatrix()
                .put(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.END_ACTIVITY, AlphaAlgorithm.END_ACTIVITY, FootprintRelation.EXCLUDED)
                .put("a", "a", FootprintRelation.EXCLUDED)
                .put("b", "b", FootprintRelation.EXCLUDED)
                .put("a", "b", FootprintRelation.EXCLUDED)
                .put("b", "a", FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.END_ACTIVITY, FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.END_ACTIVITY, AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.START_ACTIVITY, "a", FootprintRelation.FOLLOWED)
                .put(AlphaAlgorithm.END_ACTIVITY, "a", FootprintRelation.PRECDEDED)
                .put("a", AlphaAlgorithm.START_ACTIVITY, FootprintRelation.PRECDEDED)
                .put("a", AlphaAlgorithm.END_ACTIVITY, FootprintRelation.FOLLOWED)
                .put(AlphaAlgorithm.START_ACTIVITY, "b", FootprintRelation.FOLLOWED)
                .put(AlphaAlgorithm.END_ACTIVITY, "b", FootprintRelation.PRECDEDED)
                .put("b", AlphaAlgorithm.START_ACTIVITY, FootprintRelation.PRECDEDED)
                .put("b", AlphaAlgorithm.END_ACTIVITY, FootprintRelation.FOLLOWED),
                Set.of(
                    new PlaceCandidate(
                        Set.of(AlphaAlgorithm.START_ACTIVITY),
                        Set.of("a", "b")
                    ),
                    new PlaceCandidate(
                        Set.of("a", "b"),
                        Set.of(AlphaAlgorithm.END_ACTIVITY)
                    )
                )
            },
            {
                Set.of(
                    Set.of(AlphaAlgorithm.START_ACTIVITY),
                    Set.of(AlphaAlgorithm.END_ACTIVITY),
                    Set.of("a"),
                    Set.of("b"),
                    Set.of(AlphaAlgorithm.START_ACTIVITY, "b"),
                    Set.of(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.END_ACTIVITY)
                ),
                new FootprintMatrix()
                .put(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.END_ACTIVITY, AlphaAlgorithm.END_ACTIVITY, FootprintRelation.EXCLUDED)
                .put("a", "a", FootprintRelation.EXCLUDED)
                .put("b", "b", FootprintRelation.EXCLUDED)
                .put("c", "c", FootprintRelation.MUTUAL)
                .put("a", "b", FootprintRelation.FOLLOWED)
                .put("a", "c", FootprintRelation.FOLLOWED)
                .put("b", "a", FootprintRelation.PRECDEDED)
                .put("b", "c", FootprintRelation.PRECDEDED)
                .put("c", "a", FootprintRelation.PRECDEDED)
                .put("c", "b", FootprintRelation.FOLLOWED)
                .put(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.END_ACTIVITY, FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.END_ACTIVITY, AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.START_ACTIVITY, "a", FootprintRelation.FOLLOWED)
                .put(AlphaAlgorithm.END_ACTIVITY, "a", FootprintRelation.PRECDEDED)
                .put("a", AlphaAlgorithm.START_ACTIVITY, FootprintRelation.PRECDEDED)
                .put("a", AlphaAlgorithm.END_ACTIVITY, FootprintRelation.FOLLOWED)
                .put(AlphaAlgorithm.START_ACTIVITY, "b", FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.END_ACTIVITY, "b", FootprintRelation.PRECDEDED)
                .put("b", AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
                .put("b", AlphaAlgorithm.END_ACTIVITY, FootprintRelation.FOLLOWED)
                .put(AlphaAlgorithm.START_ACTIVITY, "c", FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.END_ACTIVITY, "c", FootprintRelation.EXCLUDED)
                .put("c", AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
                .put("c", AlphaAlgorithm.END_ACTIVITY, FootprintRelation.EXCLUDED),
                Set.of(
                    new PlaceCandidate(
                        Set.of(AlphaAlgorithm.START_ACTIVITY),
                        Set.of("a")
                    ),
                    new PlaceCandidate(
                        Set.of("a"),
                        Set.of(AlphaAlgorithm.END_ACTIVITY)
                    ),
                    new PlaceCandidate(
                        Set.of("a"),
                        Set.of("b")
                    ),
                    new PlaceCandidate(
                        Set.of("b"),
                        Set.of(AlphaAlgorithm.END_ACTIVITY)
                    )
                )
            },
            {
                Set.of(
                    Set.of(AlphaAlgorithm.START_ACTIVITY),
                    Set.of("a"),
                    Set.of("b"),
                    Set.of("c"),
                    Set.of("d"),
                    Set.of("e"),
                    Set.of(AlphaAlgorithm.END_ACTIVITY),
                    Set.of(AlphaAlgorithm.START_ACTIVITY, "b"),
                    Set.of(AlphaAlgorithm.START_ACTIVITY, "c"),
                    Set.of(AlphaAlgorithm.START_ACTIVITY, "d"),
                    Set.of(AlphaAlgorithm.START_ACTIVITY, "e"),
                    Set.of(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.END_ACTIVITY),
                    Set.of("a", "d"),
                    Set.of("a", "e"),
                    Set.of("a", AlphaAlgorithm.END_ACTIVITY),
                    Set.of("b", AlphaAlgorithm.END_ACTIVITY),
                    Set.of("c", AlphaAlgorithm.END_ACTIVITY),
                    Set.of("d", "e"),
                    Set.of("d", AlphaAlgorithm.END_ACTIVITY),
                    Set.of(AlphaAlgorithm.START_ACTIVITY, "b", AlphaAlgorithm.END_ACTIVITY),
                    Set.of(AlphaAlgorithm.START_ACTIVITY, "c", AlphaAlgorithm.END_ACTIVITY),
                    Set.of(AlphaAlgorithm.START_ACTIVITY, "d", "e"),
                    Set.of(AlphaAlgorithm.START_ACTIVITY, "d", AlphaAlgorithm.END_ACTIVITY),
                    Set.of("a", "d", "e"),
                    Set.of("a", "d", AlphaAlgorithm.END_ACTIVITY)
                ),
                AlphaAlgorithmTest.FOOTPRINT2,
                Set.of(
                    new PlaceCandidate(
                        Set.of(AlphaAlgorithm.START_ACTIVITY),
                        Set.of("a")
                    ),
                    new PlaceCandidate(
                        Set.of("a"),
                        Set.of("b")
                    ),
                    new PlaceCandidate(
                        Set.of("a"),
                        Set.of("c")
                    ),
                    new PlaceCandidate(
                        Set.of("b"),
                        Set.of("e")
                    ),
                    new PlaceCandidate(
                        Set.of("c"),
                        Set.of("e")
                    ),
                    new PlaceCandidate(
                        Set.of("e"),
                        Set.of(AlphaAlgorithm.END_ACTIVITY)
                    )
                )
            }
        };
    }

    @Test(dataProvider="computeAdmissiblePlacesData")
    public void computeAdmissiblePlacesTest(
        final Set<Set<String>> firstCandidates,
        final FootprintMatrix footprint,
        final Set<PlaceCandidate> expected
    ) {
        Assert.assertEquals(AlphaAlgorithm.computeAdmissiblePlaces(firstCandidates, footprint), expected);
    }

    @DataProvider
    public Object[][] computeFootprintData() {
        return new Object[][] {
            {
                Stream.of(
                    Stream.of("a").collect(Collectors.toCollection(ActivityTrace::new)),
                    Stream.of("a", "b").collect(Collectors.toCollection(ActivityTrace::new)),
                    Stream.of("a", "c", "b").collect(Collectors.toCollection(ActivityTrace::new)),
                    Stream.of("a", "c", "c", "b").collect(Collectors.toCollection(ActivityTrace::new)),
                    Stream.of("a", "c", "c", "c", "b").collect(Collectors.toCollection(ActivityTrace::new))
                ).collect(Collectors.toCollection(EventLog::new)),
                new FootprintMatrix()
                .put(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.END_ACTIVITY, AlphaAlgorithm.END_ACTIVITY, FootprintRelation.EXCLUDED)
                .put("a", "a", FootprintRelation.EXCLUDED)
                .put("b", "b", FootprintRelation.EXCLUDED)
                .put("c", "c", FootprintRelation.MUTUAL)
                .put("a", "b", FootprintRelation.FOLLOWED)
                .put("a", "c", FootprintRelation.FOLLOWED)
                .put("b", "a", FootprintRelation.PRECDEDED)
                .put("b", "c", FootprintRelation.PRECDEDED)
                .put("c", "a", FootprintRelation.PRECDEDED)
                .put("c", "b", FootprintRelation.FOLLOWED)
                .put(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.END_ACTIVITY, FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.END_ACTIVITY, AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.START_ACTIVITY, "a", FootprintRelation.FOLLOWED)
                .put(AlphaAlgorithm.END_ACTIVITY, "a", FootprintRelation.PRECDEDED)
                .put("a", AlphaAlgorithm.START_ACTIVITY, FootprintRelation.PRECDEDED)
                .put("a", AlphaAlgorithm.END_ACTIVITY, FootprintRelation.FOLLOWED)
                .put(AlphaAlgorithm.START_ACTIVITY, "b", FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.END_ACTIVITY, "b", FootprintRelation.PRECDEDED)
                .put("b", AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
                .put("b", AlphaAlgorithm.END_ACTIVITY, FootprintRelation.FOLLOWED)
                .put(AlphaAlgorithm.START_ACTIVITY, "c", FootprintRelation.EXCLUDED)
                .put(AlphaAlgorithm.END_ACTIVITY, "c", FootprintRelation.EXCLUDED)
                .put("c", AlphaAlgorithm.START_ACTIVITY, FootprintRelation.EXCLUDED)
                .put("c", AlphaAlgorithm.END_ACTIVITY, FootprintRelation.EXCLUDED)
            },
            {
                Stream.of(
                    Stream.of("a", "b", "c", "e").collect(Collectors.toCollection(ActivityTrace::new)),
                    Stream.of("a", "c", "b", "e").collect(Collectors.toCollection(ActivityTrace::new)),
                    Stream.of("a", "b", "c", "d", "b", "c", "e").collect(Collectors.toCollection(ActivityTrace::new)),
                    Stream.of("a", "c", "b", "d", "b", "c", "e").collect(Collectors.toCollection(ActivityTrace::new)),
                    Stream.of("a", "b", "c", "d", "c", "b", "e").collect(Collectors.toCollection(ActivityTrace::new)),
                    Stream.of("a", "c", "b", "d", "c", "b", "d", "b", "c", "e")
                    .collect(Collectors.toCollection(ActivityTrace::new))
                ).collect(Collectors.toCollection(EventLog::new)),
                AlphaAlgorithmTest.FOOTPRINT2
            }
        };
    }

    @Test(dataProvider="computeFootprintData")
    public void computeFootprintTest(
        final EventLog data,
        final FootprintMatrix expected
    ) {
        Assert.assertEquals(AlphaAlgorithm.computeFootprint(data), expected);
    }

}
