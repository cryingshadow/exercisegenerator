package exercisegenerator.algorithms;

import java.util.*;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.structures.*;
import exercisegenerator.structures.trees.*;

public class TreeAlgorithmsTest {

    @DataProvider
    public Object[][] avltreeData() {
        return new Object[][] {
            {
                TreeAlgorithms.AVL_TREE_FACTORY.create(),
                new ArrayDeque<Pair<Integer, Boolean>>(Arrays.asList(new Pair<Integer, Boolean>(1, true))),
                Collections.singletonList("((,1,),ADD1)")
            },
            {
                TreeAlgorithms.AVL_TREE_FACTORY.create(),
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
                    "((,5,),ADD5)",
                    "(((,3,),5,),ADD3)",
                    "(((,3,),5,(,7,)),ADD7)",
                    "((((,2,),3,),5,(,7,)),ADD2)",
                    "((((,2,),3,(,4,)),5,(,7,)),ADD4)",
                    "((((,2,),3,(,4,)),5,((,6,),7,)),ADD6)",
                    "((((,2,),3,(,4,)),5,((,6,),7,(,8,))),ADD8)"
                )
            },
            {
                TreeAlgorithms.AVL_TREE_FACTORY.create(5,3,7,2,4,6,8),
                new ArrayDeque<Pair<Integer, Boolean>>(
                    Arrays.asList(
                        new Pair<Integer, Boolean>(2, false)
                    )
                ),
                Arrays.asList(
                    "(((,3,(,4,)),5,((,6,),7,(,8,))),REMOVE2)"
                )
            },
            {
                TreeAlgorithms.AVL_TREE_FACTORY.create(5,3,7,2,4,6,8),
                new ArrayDeque<Pair<Integer, Boolean>>(
                    Arrays.asList(
                        new Pair<Integer, Boolean>(7, false),
                        new Pair<Integer, Boolean>(5, false)
                    )
                ),
                Arrays.asList(
                    "((((,2,),3,(,4,)),5,((,6,),7,)),REMOVE8)",
                    "((((,2,),3,(,4,)),5,((,6,),8,)),REPLACE7)",
                    "((((,2,),3,(,4,)),5,(,8,)),REMOVE6)",
                    "((((,2,),3,(,4,)),6,(,8,)),REPLACE5)"
                )
            },
            {
                TreeAlgorithms.AVL_TREE_FACTORY.create(5,3,7,2,4),
                new ArrayDeque<Pair<Integer, Boolean>>(
                    Arrays.asList(
                        new Pair<Integer, Boolean>(7, false)
                    )
                ),
                Arrays.asList(
                    "((((,2,),3,(,4,)),5,),REMOVE7)",
                    "(((,2,),3,((,4,),5,)),ROTATE_RIGHT5)"
                )
            },
            {
                TreeAlgorithms.AVL_TREE_FACTORY.create(5,3,7,4),
                new ArrayDeque<Pair<Integer, Boolean>>(
                    Arrays.asList(
                        new Pair<Integer, Boolean>(7, false)
                    )
                ),
                Arrays.asList(
                    "(((,3,(,4,)),5,),REMOVE7)",
                    "((((,3,),4,),5,),ROTATE_LEFT3)",
                    "(((,3,),4,(,5,)),ROTATE_RIGHT5)"
                )
            },
            {
                TreeAlgorithms.AVL_TREE_FACTORY.create(6,3,7,4),
                new ArrayDeque<Pair<Integer, Boolean>>(
                    Arrays.asList(
                        new Pair<Integer, Boolean>(5, true)
                    )
                ),
                Arrays.asList(
                    "(((,3,(,4,(,5,))),6,(,7,)),ADD5)",
                    "((((,3,),4,(,5,)),6,(,7,)),ROTATE_LEFT3)"
                )
            },
            {
                TreeAlgorithms.AVL_TREE_FACTORY.create(7,3,8,4,5),
                new ArrayDeque<Pair<Integer, Boolean>>(
                    Arrays.asList(
                        new Pair<Integer, Boolean>(6, true)
                    )
                ),
                Arrays.asList(
                    "((((,3,),4,(,5,(,6,))),7,(,8,)),ADD6)",
                    "(((((,3,),4,),5,(,6,)),7,(,8,)),ROTATE_LEFT4)",
                    "((((,3,),4,),5,((,6,),7,(,8,))),ROTATE_RIGHT7)"
                )
            },
            {
                TreeAlgorithms.AVL_TREE_FACTORY.create(7,6,5,4,3,2),
                new ArrayDeque<Pair<Integer, Boolean>>(
                    Arrays.asList(
                        new Pair<Integer, Boolean>(1, true)
                    )
                ),
                Arrays.asList(
                    "(((((,1,),2,),3,),4,((,5,),6,(,7,))),ADD1)",
                    "((((,1,),2,(,3,)),4,((,5,),6,(,7,))),ROTATE_RIGHT3)"
                )
            },
            {
                TreeAlgorithms.AVL_TREE_FACTORY.create(9),
                new ArrayDeque<Pair<Integer, Boolean>>(
                    Arrays.asList(
                        new Pair<Integer, Boolean>(9, false)
                    )
                ),
                Arrays.asList(
                    "(,REMOVE9)"
                )
            }
        };
    }

    @Test(dataProvider = "avltreeData")
    public void avltreeTest(
        final AVLTree<Integer> tree,
        final Deque<Pair<Integer, Boolean>> operations,
        final List<String> expectedStructures
    ) {
        final BinaryTreeSteps<Integer> steps = TreeAlgorithms.avltree(tree, operations);
        Assert.assertEquals(steps.stream().map(Pair::toString).toList(), expectedStructures);
    }

    @DataProvider
    public Object[][] bstreeData() {
        return new Object[][] {
            {
                TreeAlgorithms.BINARY_TREE_FACTORY.create(),
                new ArrayDeque<Pair<Integer, Boolean>>(Arrays.asList(new Pair<Integer, Boolean>(1, true))),
                Collections.singletonList("((,1,),ADD1)")
            },
            {
                TreeAlgorithms.BINARY_TREE_FACTORY.create(),
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
                    "((,5,),ADD5)",
                    "(((,3,),5,),ADD3)",
                    "(((,3,),5,(,7,)),ADD7)",
                    "((((,2,),3,),5,(,7,)),ADD2)",
                    "((((,2,),3,(,4,)),5,(,7,)),ADD4)",
                    "((((,2,),3,(,4,)),5,((,6,),7,)),ADD6)",
                    "((((,2,),3,(,4,)),5,((,6,),7,(,8,))),ADD8)"
                )
            },
            {
                TreeAlgorithms.BINARY_TREE_FACTORY.create(5,3,7,2,4,6,8),
                new ArrayDeque<Pair<Integer, Boolean>>(
                    Arrays.asList(
                        new Pair<Integer, Boolean>(2, false)
                    )
                ),
                Arrays.asList(
                    "(((,3,(,4,)),5,((,6,),7,(,8,))),REMOVE2)"
                )
            },
            {
                TreeAlgorithms.BINARY_TREE_FACTORY.create(5,3,7,2,4,6,8),
                new ArrayDeque<Pair<Integer, Boolean>>(
                    Arrays.asList(
                        new Pair<Integer, Boolean>(7, false),
                        new Pair<Integer, Boolean>(5, false)
                    )
                ),
                Arrays.asList(
                    "((((,2,),3,(,4,)),5,((,6,),7,)),REMOVE8)",
                    "((((,2,),3,(,4,)),5,((,6,),8,)),REPLACE7)",
                    "((((,2,),3,(,4,)),5,(,8,)),REMOVE6)",
                    "((((,2,),3,(,4,)),6,(,8,)),REPLACE5)"
                )
            }
        };
    }

    @Test(dataProvider = "bstreeData")
    public void bstreeTest(
        final BinaryTree<Integer> tree,
        final Deque<Pair<Integer, Boolean>> operations,
        final List<String> expectedStructures
    ) {
        final BinaryTreeSteps<Integer> steps = TreeAlgorithms.bstree(tree, operations);
        Assert.assertEquals(steps.stream().map(Pair::toString).toList(), expectedStructures);
    }

}
