package exercisegenerator.structures.trees;

import java.util.*;

public class RedBlackTreeNode<T extends Comparable<T>> extends BinaryTreeNode<T> {

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> boolean hasRedRedConflict(
        final Optional<? extends BinaryTreeNode<T>> node
    ) {
        return node.isPresent() && ((RedBlackTreeNode<T>)node.get()).hasRedRedConflict();
    }

    private static <T extends Comparable<T>> boolean hasTwoBlackChildren(
        final Optional<? extends BinaryTreeNode<T>> node
    ) {
        return node.isEmpty()
            || (RedBlackTreeNode.isBlack(node.get().leftChild) && RedBlackTreeNode.isBlack(node.get().rightChild));
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> boolean isBlack(final Optional<? extends BinaryTreeNode<T>> node) {
        return node.isEmpty() || ((RedBlackTreeNode<T>)node.get()).isBlack;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> boolean isBlackBlack(final Optional<? extends BinaryTreeNode<T>> node) {
        return node.isPresent() && ((RedBlackTreeNode<T>)node.get()).isBlackBlack();
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> boolean isRed(final Optional<? extends BinaryTreeNode<T>> node) {
        return node.isPresent() && !((RedBlackTreeNode<T>)node.get()).isBlack;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> boolean isRedBlack(final Optional<? extends BinaryTreeNode<T>> node) {
        return node.isPresent() && ((RedBlackTreeNode<T>)node.get()).isRedBlack();
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> RedBlackTreeNode<T> setColor(
        final Optional<? extends BinaryTreeNode<T>> optionalNode,
        final boolean isBlack
    ) {
        final RedBlackTreeNode<T> node = (RedBlackTreeNode<T>)optionalNode.get();
        return node.isBlack == isBlack ? node : node.toggleColor();
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> BinaryTreeNode<T> toggleColor(
        final Optional<? extends BinaryTreeNode<T>> node
    ) {
        return ((RedBlackTreeNode<T>)node.get()).toggleColor();
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> Optional<? extends BinaryTreeNode<T>> toggleMark(
        final Optional<? extends BinaryTreeNode<T>> node
    ) {
        return ((RedBlackTreeNode<T>)node.get()).toggleMark();
    }

    final boolean hasMark;

    final boolean isBlack;

    final boolean isRemoved;

    final boolean isRoot;

    RedBlackTreeNode(final T value, final BinaryTreeNodeFactory<T> nodeFactory) {
        this(value, Optional.empty(), Optional.empty(), nodeFactory);
    }

    RedBlackTreeNode(
        final T value,
        final boolean isBlack,
        final boolean isRoot,
        final boolean hasMark,
        final boolean isRemoved,
        final Optional<? extends BinaryTreeNode<T>> leftChild,
        final Optional<? extends BinaryTreeNode<T>> rightChild,
        final BinaryTreeNodeFactory<T> nodeFactory
    ) {
        super(value, leftChild, rightChild, nodeFactory);
        this.isBlack = isBlack;
        this.isRoot = isRoot;
        this.hasMark = hasMark;
        this.isRemoved = isRemoved;
    }

    RedBlackTreeNode(
        final T value,
        final Optional<? extends BinaryTreeNode<T>> leftChild,
        final Optional<? extends BinaryTreeNode<T>> rightChild,
        final BinaryTreeNodeFactory<T> nodeFactory
    ) {
        this(value, false, false, false, false, leftChild, rightChild, nodeFactory);
    }

    @Override
    public BinaryTreeNode<T> setLeftChild(final Optional<? extends BinaryTreeNode<T>> leftChild) {
        return ((RedBlackTreeNode<T>)super.setLeftChild(leftChild)).cloneRedBlackProperties(this);
    }

    @Override
    public BinaryTreeNode<T> setRightChild(final Optional<? extends BinaryTreeNode<T>> rightChild) {
        return ((RedBlackTreeNode<T>)super.setRightChild(rightChild)).cloneRedBlackProperties(this);
    }

    @Override
    public BinaryTreeNode<T> setValue(final T value) {
        return ((RedBlackTreeNode<T>)super.setValue(value)).cloneRedBlackProperties(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    SearchTreeNodeSteps<T> balanceWithSteps() {
        if (this.isRedBlack()) {
            return new SearchTreeNodeSteps<T>(
                this.toggleColor().toggleMark(),
                new SearchTreeStep<T>(SearchTreeStepType.COLOR, this.value)
            );
        }
        if (this.isRedRoot()) {
            return new SearchTreeNodeSteps<T>(
                this.toggleColor(),
                new SearchTreeStep<T>(SearchTreeStepType.COLOR, this.value)
            );
        }
        if (RedBlackTreeNode.isRedBlack(this.leftChild)) {
            return this.asLeftChildren(
                new SearchTreeNodeSteps<T>(
                    ((RedBlackTreeNode<T>)this.leftChild.get()).toggleColor().toggleMark(),
                    new SearchTreeStep<T>(SearchTreeStepType.COLOR, this.leftChild.get().value)
                )
            );
        }
        if (RedBlackTreeNode.isRedBlack(this.rightChild)) {
            return this.asRightChildren(
                new SearchTreeNodeSteps<T>(
                    ((RedBlackTreeNode<T>)this.rightChild.get()).toggleColor().toggleMark(),
                    new SearchTreeStep<T>(SearchTreeStepType.COLOR, this.rightChild.get().value)
                )
            );
        }
        if (RedBlackTreeNode.hasRedRedConflict(this.leftChild)) {
            if (RedBlackTreeNode.isRed(this.rightChild)) {
                return this.addCase1();
            }
            final SearchTreeNodeSteps<T> result = new SearchTreeNodeSteps<T>();
            final RedBlackTreeNode<T> grandParent = this.handleAddCase2Left(result);
            return grandParent.addCase3Left(result);
        }
        if (RedBlackTreeNode.hasRedRedConflict(this.rightChild)) {
            if (RedBlackTreeNode.isRed(this.leftChild)) {
                return this.addCase1();
            }
            final SearchTreeNodeSteps<T> result = new SearchTreeNodeSteps<T>();
            final RedBlackTreeNode<T> grandParent = this.handleAddCase2Right(result);
            return grandParent.addCase3Right(result);
        }
        if (RedBlackTreeNode.isBlackBlack(this.leftChild)) {
            if (RedBlackTreeNode.isRed(this.rightChild)) {
                return this.removeCase1Left();
            }
            if (RedBlackTreeNode.hasTwoBlackChildren(this.rightChild)) {
                return this.removeCase2Left();
            }
            final SearchTreeNodeSteps<T> result = new SearchTreeNodeSteps<T>();
            final RedBlackTreeNode<T> parent = this.handleRemoveCase3Left(result);
            return parent.removeCase4Left(result);
        }
        if (RedBlackTreeNode.isBlackBlack(this.rightChild)) {
            if (RedBlackTreeNode.isRed(this.leftChild)) {
                return this.removeCase1Right();
            }
            if (RedBlackTreeNode.hasTwoBlackChildren(this.leftChild)) {
                return this.removeCase2Right();
            }
            final SearchTreeNodeSteps<T> result = new SearchTreeNodeSteps<T>();
            final RedBlackTreeNode<T> parent = this.handleRemoveCase3Right(result);
            return parent.removeCase4Right(result);
        }
        return new SearchTreeNodeSteps<T>();
    }

    @Override
    BinaryTreeNode<T> removeByReplace(final BinaryTreeNode<T> replace) {
        final RedBlackTreeNode<T> rbReplace = (RedBlackTreeNode<T>)replace;
        return this.isRoot ? rbReplace.toggleRoot() : (this.isBlack ? rbReplace.toggleMark().get() : rbReplace);
    }

    @Override
    Optional<BinaryTreeNode<T>> removeToEmpty() {
        return this.isRoot || !this.isBlack ? Optional.empty() : Optional.of(this.removed());
    }

    @Override
    BinaryTreeNode<T> replace(
        final T value,
        final Optional<? extends BinaryTreeNode<T>> leftChild,
        final Optional<? extends BinaryTreeNode<T>> rightChild
    ) {
        return ((RedBlackTreeNode<T>)super.replace(value, leftChild, rightChild)).cloneRedBlackProperties(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    BinaryTreeNode<T> rotationLeft() {
        final RedBlackTreeNode<T> rotated = (RedBlackTreeNode<T>)super.rotationLeft();
        final RedBlackTreeNode<T> leftChild = (RedBlackTreeNode<T>)rotated.leftChild.get();
        if (leftChild.isRoot) {
            return rotated.toggleRoot().setLeftChild(leftChild.toggleRoot());
        }
        return rotated;
    }

    @Override
    @SuppressWarnings("unchecked")
    BinaryTreeNode<T> rotationRight() {
        final RedBlackTreeNode<T> rotated = (RedBlackTreeNode<T>)super.rotationRight();
        final RedBlackTreeNode<T> rightChild = (RedBlackTreeNode<T>)rotated.rightChild.get();
        if (rightChild.isRoot) {
            return rotated.toggleRoot().setRightChild(rightChild.toggleRoot());
        }
        return rotated;
    }

    RedBlackTreeNode<T> toggleRoot() {
        return new RedBlackTreeNode<T>(
            this.value,
            this.isBlack,
            !this.isRoot,
            this.hasMark,
            this.isRemoved,
            this.leftChild,
            this.rightChild,
            this.nodeFactory
        );
    }

    @Override
    String valueToString() {
        return String.format(
            "%s(%s)%s",
            this.isBlack ? "B" : "R",
            this.isRemoved ? "" : this.value.toString(),
            this.hasMark ? "B" : ""
        );
    }

    @Override
    String valueToTikZ() {
        if (this.isRemoved) {
            return "\\node[b]{};";
        }
        final String style = this.hasMark ? (this.isBlack ? "bb" : "rb") : (this.isBlack ? "b" : "r");
        return String.format("\\node[%s]{%s};", style, this.value.toString());
    }

    private SearchTreeNodeSteps<T> addCase1() {
        final SearchTreeNodeSteps<T> result = new SearchTreeNodeSteps<T>();
        final BinaryTreeNode<T> newNode =
            this
            .toggleColor()
            .setLeftChild(RedBlackTreeNode.toggleColor(this.leftChild))
            .setRightChild(RedBlackTreeNode.toggleColor(this.rightChild));
        result.add(
            new SearchTreeNodeAndStep<T>(
                Optional.of(newNode),
                new SearchTreeStep<T>(
                    SearchTreeStepType.COLOR,
                    List.of(this.leftChild.get().value, this.value, this.rightChild.get().value),
                    "(Fall 1) "
                )
            )
        );
        result.addAll(newNode.balanceWithSteps());
        return result;
    }

    private SearchTreeNodeSteps<T> addCase3(final SearchTreeNodeSteps<T> result, final OperationsMirror mirror) {
        result.addAll(mirror.innerRotate(this, "(Fall 3) "));
        @SuppressWarnings("unchecked")
        final BinaryTreeNode<T> grandParent = (BinaryTreeNode<T>)result.getLast().node().get();
        final Optional<? extends BinaryTreeNode<T>> parent = mirror.getInnerChild(grandParent);
        result.add(
            new SearchTreeNodeAndStep<T>(
                Optional.of(
                    mirror.setInnerChild(
                        ((RedBlackTreeNode<T>)grandParent).toggleColor(),
                        RedBlackTreeNode.toggleColor(parent)
                    )
                ),
                new SearchTreeStep<T>(
                    SearchTreeStepType.COLOR,
                    List.of(grandParent.value, parent.get().value)
                )
            )
        );
        return result;
    }

    private SearchTreeNodeSteps<T> addCase3Left(final SearchTreeNodeSteps<T> result) {
        return this.addCase3(result, BinaryTreeNode.LEFT_IS_OUTSIDE);
    }

    private SearchTreeNodeSteps<T> addCase3Right(final SearchTreeNodeSteps<T> result) {
        return this.addCase3(result, BinaryTreeNode.RIGHT_IS_OUTSIDE);
    }

    private RedBlackTreeNode<T> cloneRedBlackProperties(final RedBlackTreeNode<T> node) {
        return new RedBlackTreeNode<T>(
            this.value,
            node.isBlack,
            node.isRoot,
            node.hasMark,
            node.isRemoved,
            this.leftChild,
            this.rightChild,
            this.nodeFactory
        );
    }

    @SuppressWarnings("unchecked")
    private RedBlackTreeNode<T> handleAddCase2(final SearchTreeNodeSteps<T> result, final OperationsMirror mirror) {
        final BinaryTreeNode<T> child = mirror.getOuterChild(this).get();
        if (RedBlackTreeNode.isRed((Optional<? extends BinaryTreeNode<T>>)mirror.getInnerChild(child))) {
            result.addAll(mirror.asOuterChildren(this, mirror.outerRotate(child, "(Fall 2) ")));
            return (RedBlackTreeNode<T>)result.getLast().node().get();
        }
        return this;
    }

    private RedBlackTreeNode<T> handleAddCase2Left(final SearchTreeNodeSteps<T> result) {
        return this.handleAddCase2(result, BinaryTreeNode.LEFT_IS_OUTSIDE);
    }

    private RedBlackTreeNode<T> handleAddCase2Right(final SearchTreeNodeSteps<T> result) {
        return this.handleAddCase2(result, BinaryTreeNode.RIGHT_IS_OUTSIDE);
    }

    private RedBlackTreeNode<T> handleRemoveCase3(final SearchTreeNodeSteps<T> result, final OperationsMirror mirror) {
        final Optional<? extends BinaryTreeNode<T>> innerChildBefore = mirror.getInnerChild(this);
        if (
            innerChildBefore.isEmpty()
            || RedBlackTreeNode.isBlack(
                (Optional<? extends BinaryTreeNode<T>>)mirror.getInnerChild(innerChildBefore.get())
            )
        ) {
            result.addAll(mirror.asInnerChildren(this, mirror.innerRotate(innerChildBefore.get(), "(Fall 3) ")));
            @SuppressWarnings("unchecked")
            BinaryTreeNode<T> parent = (BinaryTreeNode<T>)result.getLast().node().get();
            final BinaryTreeNode<T> innerChildIntermediate = mirror.getInnerChild(parent).get();
            parent =
                mirror.setInnerChild(
                    parent,
                    ((RedBlackTreeNode<T>)mirror.setInnerChild(
                        innerChildIntermediate,
                        RedBlackTreeNode.toggleColor(mirror.getInnerChild(innerChildIntermediate))
                    )).toggleColor()
                );
            final BinaryTreeNode<T> innerChildAfter = mirror.getInnerChild(parent).get();
            result.add(
                new SearchTreeNodeAndStep<T>(
                    Optional.of(parent),
                    new SearchTreeStep<T>(
                        SearchTreeStepType.COLOR,
                        List.of(innerChildAfter.value, mirror.getInnerChild(innerChildAfter).get().value)
                    )
                )
            );
            return (RedBlackTreeNode<T>)parent;
        }
        return this;
    }

    private RedBlackTreeNode<T> handleRemoveCase3Left(final SearchTreeNodeSteps<T> result) {
        return this.handleRemoveCase3(result, BinaryTreeNode.LEFT_IS_OUTSIDE);
    }

    private RedBlackTreeNode<T> handleRemoveCase3Right(final SearchTreeNodeSteps<T> result) {
        return this.handleRemoveCase3(result, BinaryTreeNode.RIGHT_IS_OUTSIDE);
    }

    private boolean hasRedRedConflict() {
        return !this.isBlack && (RedBlackTreeNode.isRed(this.leftChild) || RedBlackTreeNode.isRed(this.rightChild));
    }

    private boolean isBlackBlack() {
        return this.isBlack && this.hasMark;
    }

    private boolean isRedBlack() {
        return this.hasMark && !this.isBlack;
    }

    private boolean isRedRoot() {
        return this.isRoot && !this.isBlack;
    }

    @SuppressWarnings("unchecked")
    private SearchTreeNodeSteps<T> removeCase1(final OperationsMirror mirror) {
        final SearchTreeNodeSteps<T> result = mirror.outerRotate(this);
        RedBlackTreeNode<T> parent = (RedBlackTreeNode<T>)result.getLast().node().get();
        parent =
            (RedBlackTreeNode<T>)mirror.setOuterChild(
                parent.toggleColor(),
                RedBlackTreeNode.toggleColor(mirror.getOuterChild(parent))
            );
        final BinaryTreeNode<T> outerChild = mirror.getOuterChild(parent).get();
        result.add(
            new SearchTreeNodeAndStep<T>(
                Optional.of(parent),
                new SearchTreeStep<T>(
                    SearchTreeStepType.COLOR,
                    List.of(outerChild.value, parent.value),
                    "(Fall 1) "
                )
            )
        );
        result.addAll(mirror.asOuterChildren(parent, outerChild.balanceWithSteps()));
        result.addAll(((RedBlackTreeNode<T>)result.getLast().node().get()).balanceWithSteps());
        return result;
    }

    private SearchTreeNodeSteps<T> removeCase1Left() {
        return this.removeCase1(BinaryTreeNode.LEFT_IS_OUTSIDE);
    }

    private SearchTreeNodeSteps<T> removeCase1Right() {
        return this.removeCase1(BinaryTreeNode.RIGHT_IS_OUTSIDE);
    }

    private SearchTreeNodeSteps<T> removeCase2(final OperationsMirror mirror) {
        final Optional<? extends BinaryTreeNode<T>> innerChild = mirror.getInnerChild(this);
        return new SearchTreeNodeSteps<T>(
            mirror.setInnerChild(
                mirror.setOuterChild(
                    this.toggleMark().get(),
                    RedBlackTreeNode.toggleMark((Optional<? extends BinaryTreeNode<T>>)mirror.getOuterChild(this))
                ),
                RedBlackTreeNode.toggleColor(innerChild)
            ),
            new SearchTreeStep<T>(SearchTreeStepType.COLOR, innerChild.get().value, "(Fall 2) ")
        );
    }

    private SearchTreeNodeSteps<T> removeCase2Left() {
        return this.removeCase2(BinaryTreeNode.LEFT_IS_OUTSIDE);
    }

    private SearchTreeNodeSteps<T> removeCase2Right() {
        return this.removeCase2(BinaryTreeNode.RIGHT_IS_OUTSIDE);
    }

    private SearchTreeNodeSteps<T> removeCase4(final SearchTreeNodeSteps<T> result, final OperationsMirror mirror) {
        final Optional<? extends BinaryTreeNode<T>> innerChild = mirror.getInnerChild(this);
        final BinaryTreeNode<T> parent =
            mirror.setInnerChild(
                this.setBlack(),
                mirror.setInnerChild(
                    RedBlackTreeNode.setColor(innerChild, this.isBlack),
                    RedBlackTreeNode.toggleColor(mirror.getInnerChild(innerChild.get()))
                )
            );
        final T innerInnerValue = mirror.getInnerChild(innerChild.get()).get().value;
        result.add(
            new SearchTreeNodeAndStep<T>(
                Optional.of(parent),
                new SearchTreeStep<T>(
                    SearchTreeStepType.COLOR,
                    this.isBlack ?
                        List.of(innerInnerValue) :
                            List.of(this.value, innerChild.get().value, innerInnerValue),
                    "(Fall 4) "
                )
            )
        );
        result.addAll(
            mirror.outerRotate(
                mirror.setOuterChild(
                    parent,
                    RedBlackTreeNode.toggleMark((Optional<? extends BinaryTreeNode<T>>)mirror.getOuterChild(parent))
                )
            )
        );
        return result;
    }

    private SearchTreeNodeSteps<T> removeCase4Left(final SearchTreeNodeSteps<T> result) {
        return this.removeCase4(result, BinaryTreeNode.LEFT_IS_OUTSIDE);
    }

    private SearchTreeNodeSteps<T> removeCase4Right(final SearchTreeNodeSteps<T> result) {
        return this.removeCase4(result, BinaryTreeNode.RIGHT_IS_OUTSIDE);
    }

    private RedBlackTreeNode<T> removed() {
        return new RedBlackTreeNode<T>(
            this.value,
            this.isBlack,
            this.isRoot,
            true,
            true,
            this.leftChild,
            this.rightChild,
            this.nodeFactory
        );
    }

    private RedBlackTreeNode<T> setBlack() {
        return new RedBlackTreeNode<T>(
            this.value,
            true,
            this.isRoot,
            this.hasMark,
            this.isRemoved,
            this.leftChild,
            this.rightChild,
            this.nodeFactory
        );
    }

    private RedBlackTreeNode<T> toggleColor() {
        return new RedBlackTreeNode<T>(
            this.value,
            !this.isBlack,
            this.isRoot,
            this.hasMark,
            this.isRemoved,
            this.leftChild,
            this.rightChild,
            this.nodeFactory
        );
    }

    private Optional<? extends BinaryTreeNode<T>> toggleMark() {
        if (this.isRemoved) {
            return Optional.empty();
        }
        if (this.isRoot) {
            return Optional.of(this);
        }
        return Optional.of(
            new RedBlackTreeNode<T>(
                this.value,
                this.isBlack,
                this.isRoot,
                !this.hasMark,
                this.isRemoved,
                this.leftChild,
                this.rightChild,
                this.nodeFactory
            )
        );
    }

}
