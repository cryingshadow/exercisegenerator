package exercisegenerator.structures.trees.btree;

import java.util.*;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.structures.*;

public class BTreeTest {

    private static class Input {
        private final boolean add;
        private final int value;
        private Input(final int value, final boolean add) {
            this.value = value;
            this.add = add;
        }
    }

    @DataProvider
    public Object[][] btreeData() {
        return new Object[][] {
            {
                new BTree<Integer>(2),
                List.of(new Input(3, true)),
                List.of(new Pair<String, String>("{3}", "ADD3"))
            },
            {
                new BTree<Integer>(2).add(2),
                List.of(new Input(4, true)),
                List.of(new Pair<String, String>("{2,4}", "ADD4"))
            },
            {
                new BTree<Integer>(2),
                List.of(
                    new Input(1, true),
                    new Input(2, true),
                    new Input(3, true),
                    new Input(4, true),
                    new Input(5, true),
                    new Input(6, true),
                    new Input(7, true),
                    new Input(8, true),
                    new Input(9, true)
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
                    new Input(1, true),
                    new Input(2, true),
                    new Input(3, true),
                    new Input(4, true),
                    new Input(5, true),
                    new Input(6, true),
                    new Input(7, true),
                    new Input(8, true),
                    new Input(9, true)
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
                List.of(new Input(4, false)),
                List.of(
                    new Pair<String, String>("[.{2,4,6} {1} {3} {5} {7,8,9} ]", "MERGE4"),
                    new Pair<String, String>("[.{2,6} {1} {3,4,5} {7,8,9} ]", "MERGE4"),
                    new Pair<String, String>("[.{2,6} {1} {3,5} {7,8,9} ]", "REMOVE4")
                )
            },
            {
                new BTree<Integer>(2).addAll(List.of(1,2,3,4,5,6,7,8,9)).remove(4),
                List.of(new Input(1, false)),
                List.of(
                    new Pair<String, String>("[.{3,6} {1,2} {5} {7,8,9} ]", "ROTATE_LEFT2"),
                    new Pair<String, String>("[.{3,6} {2} {5} {7,8,9} ]", "REMOVE1")
                )
            },
            {
                new BTree<Integer>(2).addAll(List.of(1,2,3,4,5,6,7,8,9)).remove(4),
                List.of(new Input(2, false)),
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
        final List<Input> operations,
        final List<Pair<String, String>> expectedList
    ) {
        final BTreeSteps<Integer> steps = new BTreeSteps<Integer>();
        for (final Input operation : operations) {
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
