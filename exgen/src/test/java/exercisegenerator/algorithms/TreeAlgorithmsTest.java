package exercisegenerator.algorithms;

import java.util.*;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.structures.*;
import exercisegenerator.structures.trees.*;

public class TreeAlgorithmsTest {

    @Test(dataProvider = "data")
    public void bstreeTest(
        final BinaryTree<Integer> tree,
        final Deque<Pair<Integer, Boolean>> operations,
        final List<String> expectedStructures
    ) {
        final List<Pair<BinaryTree<Integer>, BinaryTreeStep>> result = TreeAlgorithms.bstree(tree, operations);
        for (final Pair<BinaryTree<Integer>, BinaryTreeStep> pair : result) {
            Assert.assertTrue(BinaryTreeTest.isWellFormed(pair.x));
        }
        Assert.assertEquals(result.stream().map(Pair::toString).toList(), expectedStructures);
    }

    @DataProvider
    public Object[][] data() {
        return new Object[][] {
            {
                BinaryTree.create(),
                new ArrayDeque<Pair<Integer, Boolean>>(Arrays.asList(new Pair<Integer, Boolean>(1, true))),
                Collections.singletonList("((,1,),ADD)")
            },
            {
                BinaryTree.create(),
                new ArrayDeque<Pair<Integer, Boolean>>(
                    Arrays.asList(
                        new Pair<Integer, Boolean>(5, true),
                        new Pair<Integer, Boolean>(3, true),
                        new Pair<Integer, Boolean>(7, true),
                        new Pair<Integer, Boolean>(2, true),
                        new Pair<Integer, Boolean>(4, true),
                        new Pair<Integer, Boolean>(6, true),
                        new Pair<Integer, Boolean>(8, true)
                    )
                ),
                Arrays.asList(
                    "((,5,),ADD)",
                    "(((,3,),5,),ADD)",
                    "(((,3,),5,(,7,)),ADD)",
                    "((((,2,),3,),5,(,7,)),ADD)",
                    "((((,2,),3,(,4,)),5,(,7,)),ADD)",
                    "((((,2,),3,(,4,)),5,((,6,),7,)),ADD)",
                    "((((,2,),3,(,4,)),5,((,6,),7,(,8,))),ADD)"
                )
            },
            {
                BinaryTree.create(5,3,7,2,4,6,8),
                new ArrayDeque<Pair<Integer, Boolean>>(
                    Arrays.asList(
                        new Pair<Integer, Boolean>(2, false)
                    )
                ),
                Arrays.asList(
                    "(((,3,(,4,)),5,((,6,),7,(,8,))),REMOVE)"
                )
            },
            {
                BinaryTree.create(5,3,7,2,4,6,8),
                new ArrayDeque<Pair<Integer, Boolean>>(
                    Arrays.asList(
                        new Pair<Integer, Boolean>(7, false),
                        new Pair<Integer, Boolean>(5, false)
                    )
                ),
                Arrays.asList(
                    "((((,2,),3,(,4,)),5,((,6,),7,)),REMOVE)",
                    "((((,2,),3,(,4,)),5,((,6,),8,)),REPLACE)",
                    "((((,2,),3,(,4,)),5,(,8,)),REMOVE)",
                    "((((,2,),3,(,4,)),6,(,8,)),REPLACE)"
                )
            }
        };
    }

}
