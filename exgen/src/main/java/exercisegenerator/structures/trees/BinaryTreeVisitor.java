package exercisegenerator.structures.trees;

public interface BinaryTreeVisitor<T extends Comparable<T>> {

    void onBackFromLeft(T value);

    void onBackFromRight(T value);

    void onBackFromRoot();

    void onEmptyLeft();

    void onEmptyRight();

    void onEmptyRoot();

    void onLeft(T value);

    void onRight(T value);

    void onRoot(T value);

}
