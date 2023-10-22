package exercisegenerator.structures.trees.btree;

import java.util.*;

import org.testng.*;
import org.testng.annotations.*;

public class BTreeTest {

    private static class Input {
        private final boolean add;
        private final int index;
        private final int value;
        private Input(final int value, final boolean add, final int index) {
            this.value = value;
            this.add = add;
            this.index = index;

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
                newTree = newTree.add(operation.value).getLast().x;
            } else {
                oldTree.remove(operation.value);
                newTree = newTree.remove(operation.value).getLast().x;
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
