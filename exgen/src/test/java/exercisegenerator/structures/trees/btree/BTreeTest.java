package exercisegenerator.structures.trees.btree;

import java.util.*;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.structures.*;

public class BTreeTest {

    private static class Input {
        private static int nextIndex = 0;
        private final boolean add;
        private final int index;
        private final int value;
        private Input(final int value, final boolean add) {
            this(value, add, Input.nextIndex++);
        }
        private Input(final int value, final boolean add, final int index) {
            this.value = value;
            this.add = add;
            this.index = index;

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

    @Test(dataProvider="referenceOps")
    public void reference(final List<Input> operations) {
        final IntBTree oldTree = new IntBTree(2);
        BTree<Integer> newTree = new BTree<Integer>(2);
        Assert.assertEquals(newTree, oldTree.toBTree());
        for (final Input operation : operations) {
            if (operation.add) {
                oldTree.add(operation.value);
                newTree = newTree.add(operation.value);
            } else {
                oldTree.remove(operation.value);
                newTree = newTree.remove(operation.value);
            }
            Assert.assertEquals(newTree, oldTree.toBTree(), "Step " + operation.index);
        }
    }

    @DataProvider
    public Object[][] referenceOps() {
        return new Object[][] {
            {
                List.of(
                    new Input(3, true, 1),
                    new Input(13, true, 2),
                    new Input(17, true, 3),
                    new Input(7, true, 4),
                    new Input(1, true, 5),
                    new Input(9, true, 6),
                    new Input(4, true, 7),
                    new Input(6, true, 8),
                    new Input(8, true, 9),
                    new Input(3, true, 10),
                    new Input(2, true, 11),
                    new Input(10, true, 12),
                    new Input(11, true, 13),
                    new Input(12, true, 14),
                    new Input(3, true, 15),
                    new Input(5, true, 16),
                    new Input(5, false, 17),
                    new Input(3, false, 18),
                    new Input(3, false, 19),
                    new Input(3, false, 20),
                    new Input(13, false, 21),
                    new Input(1, false, 22),
                    new Input(17, false, 23),
                    new Input(9, false, 24),
                    new Input(8, false, 25),
                    new Input(7, false, 26),
                    new Input(6, false, 27),
                    new Input(2, false, 28),
                    new Input(3, false, 29),
                    new Input(4, false, 30),
                    new Input(10, false, 31),
                    new Input(11, false, 32),
                    new Input(12, false, 33)
                )
            },
            {
                List.of(
                    new Input(3, false, 18),
                    new Input(3, true, 1),
                    new Input(13, true, 2),
                    new Input(3, false, 19),
                    new Input(17, true, 3),
                    new Input(1, true, 5),
                    new Input(9, true, 6),
                    new Input(6, true, 8),
                    new Input(7, true, 4),
                    new Input(3, true, 10),
                    new Input(6, false, 27),
                    new Input(2, true, 11),
                    new Input(10, true, 12),
                    new Input(11, true, 13),
                    new Input(11, false, 32),
                    new Input(8, true, 9),
                    new Input(12, true, 14),
                    new Input(3, true, 15),
                    new Input(5, true, 16),
                    new Input(13, false, 21),
                    new Input(1, false, 22),
                    new Input(17, false, 23),
                    new Input(9, false, 24),
                    new Input(8, false, 25),
                    new Input(7, false, 26),
                    new Input(5, false, 17),
                    new Input(3, false, 20),
                    new Input(2, false, 28),
                    new Input(3, false, 29),
                    new Input(10, false, 31),
                    new Input(12, false, 33),
                    new Input(4, true, 7),
                    new Input(4, false, 30)
                )
            }
        };
    }

}
