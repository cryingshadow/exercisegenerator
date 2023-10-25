package exercisegenerator.algorithms.trees;

import java.util.*;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.structures.*;
import exercisegenerator.structures.trees.*;
import exercisegenerator.structures.trees.btree.*;

public class TreeAlgorithmsTest {

    @DataProvider
    public Object[][] avltreeData() {
        return new Object[][] {
            {
                TreeAlgorithms.AVL_TREE_FACTORY.create(),
                new ArrayDeque<TreeOperation<Integer>>(Arrays.asList(new TreeOperation<Integer>(1, true))),
                Collections.singletonList("((,1,),ADD1)")
            },
            {
                TreeAlgorithms.AVL_TREE_FACTORY.create(),
                new ArrayDeque<TreeOperation<Integer>>(
                    Arrays.asList(
                        new TreeOperation<Integer>(5, true),
                        new TreeOperation<Integer>(3, true),
                        new TreeOperation<Integer>(7, true),
                        new TreeOperation<Integer>(2, true),
                        new TreeOperation<Integer>(4, true),
                        new TreeOperation<Integer>(6, true),
                        new TreeOperation<Integer>(8, true)
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
                new ArrayDeque<TreeOperation<Integer>>(
                    Arrays.asList(
                        new TreeOperation<Integer>(2, false)
                    )
                ),
                Arrays.asList(
                    "(((,3,(,4,)),5,((,6,),7,(,8,))),REMOVE2)"
                )
            },
            {
                TreeAlgorithms.AVL_TREE_FACTORY.create(5,3,7,2,4,6,8),
                new ArrayDeque<TreeOperation<Integer>>(
                    Arrays.asList(
                        new TreeOperation<Integer>(7, false),
                        new TreeOperation<Integer>(5, false)
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
                new ArrayDeque<TreeOperation<Integer>>(
                    Arrays.asList(
                        new TreeOperation<Integer>(7, false)
                    )
                ),
                Arrays.asList(
                    "((((,2,),3,(,4,)),5,),REMOVE7)",
                    "(((,2,),3,((,4,),5,)),ROTATE_RIGHT5)"
                )
            },
            {
                TreeAlgorithms.AVL_TREE_FACTORY.create(5,3,7,4),
                new ArrayDeque<TreeOperation<Integer>>(
                    Arrays.asList(
                        new TreeOperation<Integer>(7, false)
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
                new ArrayDeque<TreeOperation<Integer>>(
                    Arrays.asList(
                        new TreeOperation<Integer>(5, true)
                    )
                ),
                Arrays.asList(
                    "(((,3,(,4,(,5,))),6,(,7,)),ADD5)",
                    "((((,3,),4,(,5,)),6,(,7,)),ROTATE_LEFT3)"
                )
            },
            {
                TreeAlgorithms.AVL_TREE_FACTORY.create(7,3,8,4,5),
                new ArrayDeque<TreeOperation<Integer>>(
                    Arrays.asList(
                        new TreeOperation<Integer>(6, true)
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
                new ArrayDeque<TreeOperation<Integer>>(
                    Arrays.asList(
                        new TreeOperation<Integer>(1, true)
                    )
                ),
                Arrays.asList(
                    "(((((,1,),2,),3,),4,((,5,),6,(,7,))),ADD1)",
                    "((((,1,),2,(,3,)),4,((,5,),6,(,7,))),ROTATE_RIGHT3)"
                )
            },
            {
                TreeAlgorithms.AVL_TREE_FACTORY.create(9),
                new ArrayDeque<TreeOperation<Integer>>(
                    Arrays.asList(
                        new TreeOperation<Integer>(9, false)
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
        final Deque<TreeOperation<Integer>> operations,
        final List<String> expectedStructures
    ) {
        final BinaryTreeSteps<Integer> steps = AVLTreeAlgorithm.avltree(tree, operations);
        Assert.assertEquals(steps.stream().map(Pair::toString).toList(), expectedStructures);
    }

    @DataProvider
    public Object[][] bstreeData() {
        return new Object[][] {
            {
                TreeAlgorithms.BINARY_TREE_FACTORY.create(),
                new ArrayDeque<TreeOperation<Integer>>(Arrays.asList(new TreeOperation<Integer>(1, true))),
                Collections.singletonList("((,1,),ADD1)")
            },
            {
                TreeAlgorithms.BINARY_TREE_FACTORY.create(),
                new ArrayDeque<TreeOperation<Integer>>(
                    Arrays.asList(
                        new TreeOperation<Integer>(5, true),
                        new TreeOperation<Integer>(3, true),
                        new TreeOperation<Integer>(7, true),
                        new TreeOperation<Integer>(2, true),
                        new TreeOperation<Integer>(4, true),
                        new TreeOperation<Integer>(6, true),
                        new TreeOperation<Integer>(8, true)
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
                new ArrayDeque<TreeOperation<Integer>>(
                    Arrays.asList(
                        new TreeOperation<Integer>(2, false)
                    )
                ),
                Arrays.asList(
                    "(((,3,(,4,)),5,((,6,),7,(,8,))),REMOVE2)"
                )
            },
            {
                TreeAlgorithms.BINARY_TREE_FACTORY.create(5,3,7,2,4,6,8),
                new ArrayDeque<TreeOperation<Integer>>(
                    Arrays.asList(
                        new TreeOperation<Integer>(7, false),
                        new TreeOperation<Integer>(5, false)
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
        final Deque<TreeOperation<Integer>> operations,
        final List<String> expectedStructures
    ) {
        final BinaryTreeSteps<Integer> steps = BinarySearchTreeAlgorithm.bstree(tree, operations);
        Assert.assertEquals(steps.stream().map(Pair::toString).toList(), expectedStructures);
    }

    @DataProvider
    public Object[][] btreeData() {
        return new Object[][] {
            {
                new BTree<Integer>(2),
                List.of(new TreeOperation<Integer>(3, true)),
                List.of(new Pair<String, String>("{3}", "ADD3"))
            },
            {
                new BTree<Integer>(2).add(2),
                List.of(new TreeOperation<Integer>(4, true)),
                List.of(new Pair<String, String>("{2,4}", "ADD4"))
            },
            {
                new BTree<Integer>(2),
                List.of(
                    new TreeOperation<Integer>(1, true),
                    new TreeOperation<Integer>(2, true),
                    new TreeOperation<Integer>(3, true),
                    new TreeOperation<Integer>(4, true),
                    new TreeOperation<Integer>(5, true),
                    new TreeOperation<Integer>(6, true),
                    new TreeOperation<Integer>(7, true),
                    new TreeOperation<Integer>(8, true),
                    new TreeOperation<Integer>(9, true)
                ),
                List.of(
                    new Pair<String, String>("{1}", "ADD1"),
                    new Pair<String, String>("{1,2}", "ADD2"),
                    new Pair<String, String>("{1,2,3}", "ADD3"),
                    new Pair<String, String>("[.{2} {1} {3} ]", "SPLIT2"),
                    new Pair<String, String>("[.{2} {1} {3,4} ]", "ADD4"),
                    new Pair<String, String>("[.{2} {1} {3,4,5} ]", "ADD5"),
                    new Pair<String, String>("[.{2,4} {1} {3} {5} ]", "SPLIT4"),
                    new Pair<String, String>("[.{2,4} {1} {3} {5,6} ]", "ADD6"),
                    new Pair<String, String>("[.{2,4} {1} {3} {5,6,7} ]", "ADD7"),
                    new Pair<String, String>("[.{2,4,6} {1} {3} {5} {7} ]", "SPLIT6"),
                    new Pair<String, String>("[.{2,4,6} {1} {3} {5} {7,8} ]", "ADD8"),
                    new Pair<String, String>("[.{4} [.{2} {1} {3} ] [.{6} {5} {7,8} ] ]", "SPLIT4"),
                    new Pair<String, String>("[.{4} [.{2} {1} {3} ] [.{6} {5} {7,8,9} ] ]", "ADD9")
                )
            },
            {
                new BTree<Integer>(3),
                List.of(
                    new TreeOperation<Integer>(1, true),
                    new TreeOperation<Integer>(2, true),
                    new TreeOperation<Integer>(3, true),
                    new TreeOperation<Integer>(4, true),
                    new TreeOperation<Integer>(5, true),
                    new TreeOperation<Integer>(6, true),
                    new TreeOperation<Integer>(7, true),
                    new TreeOperation<Integer>(8, true),
                    new TreeOperation<Integer>(9, true)
                ),
                List.of(
                    new Pair<String, String>("{1}", "ADD1"),
                    new Pair<String, String>("{1,2}", "ADD2"),
                    new Pair<String, String>("{1,2,3}", "ADD3"),
                    new Pair<String, String>("{1,2,3,4}", "ADD4"),
                    new Pair<String, String>("{1,2,3,4,5}", "ADD5"),
                    new Pair<String, String>("[.{3} {1,2} {4,5} ]", "SPLIT3"),
                    new Pair<String, String>("[.{3} {1,2} {4,5,6} ]", "ADD6"),
                    new Pair<String, String>("[.{3} {1,2} {4,5,6,7} ]", "ADD7"),
                    new Pair<String, String>("[.{3} {1,2} {4,5,6,7,8} ]", "ADD8"),
                    new Pair<String, String>("[.{3,6} {1,2} {4,5} {7,8} ]", "SPLIT6"),
                    new Pair<String, String>("[.{3,6} {1,2} {4,5} {7,8,9} ]", "ADD9")
                )
            },
            {
                new BTree<Integer>(2).addAll(List.of(1,2,3,4,5,6,7,8,9)),
                List.of(new TreeOperation<Integer>(4, false)),
                List.of(
                    new Pair<String, String>("[.{2,4,6} {1} {3} {5} {7,8,9} ]", "MERGE4"),
                    new Pair<String, String>("[.{2,6} {1} {3,4,5} {7,8,9} ]", "MERGE4"),
                    new Pair<String, String>("[.{2,6} {1} {3,5} {7,8,9} ]", "REMOVE4")
                )
            },
            {
                new BTree<Integer>(2).addAll(List.of(1,2,3,4,5,6,7,8,9)).remove(4),
                List.of(new TreeOperation<Integer>(1, false)),
                List.of(
                    new Pair<String, String>("[.{3,6} {1,2} {5} {7,8,9} ]", "ROTATE_LEFT2"),
                    new Pair<String, String>("[.{3,6} {2} {5} {7,8,9} ]", "REMOVE1")
                )
            },
            {
                new BTree<Integer>(2).addAll(List.of(1,2,3,4,5,6,7,8,9)).remove(4),
                List.of(new TreeOperation<Integer>(2, false)),
                List.of(
                    new Pair<String, String>("[.{3,6} {1} {3,5} {7,8,9} ]", "STEAL_RIGHT2"),
                    new Pair<String, String>("[.{3,6} {1} {5} {7,8,9} ]", "REMOVE3")
                )
            }
        };
    }

    @Test(dataProvider="btreeData")
    public void btreeTest(
        BTree<Integer> tree,
        final List<TreeOperation<Integer>> operations,
        final List<Pair<String, String>> expectedList
    ) {
        final BTreeSteps<Integer> steps = new BTreeSteps<Integer>();
        for (final TreeOperation<Integer> operation : operations) {
            if (operation.add) {
                steps.addAll(tree.addWithSteps(operation.value));
            } else {
                steps.addAll(tree.removeWithSteps(operation.value));
            }
            tree = steps.getLast().x;
        }
        Assert.assertEquals(steps.size(), expectedList.size());
        final Iterator<BTreeAndStep<Integer>> stepIterator = steps.iterator();
        final Iterator<Pair<String, String>> expectedIterator = expectedList.iterator();
        while (stepIterator.hasNext()) {
            final BTreeAndStep<Integer> step = stepIterator.next();
            final Pair<String, String> expected = expectedIterator.next();
            Assert.assertEquals(step.x.toString(), expected.x);
            Assert.assertEquals(step.y.toString(), expected.y);
        }
    }

}
