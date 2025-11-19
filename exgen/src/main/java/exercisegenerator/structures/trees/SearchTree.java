package exercisegenerator.structures.trees;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import org.apache.commons.math3.fraction.*;

import exercisegenerator.*;
import exercisegenerator.io.*;

public interface SearchTree<T extends Comparable<T>> extends Iterable<T> {

    public static String formatOperations(final List<String> operations) {
        final StringWriter result = new StringWriter();
        try (BufferedWriter writer = new BufferedWriter(result)) {
            LaTeXUtils.printBeginning(LaTeXUtils.ITEMIZE, writer);
            for (final String operation : operations) {
                writer.write(LaTeXUtils.ITEM);
                writer.write(" \\emphasize{");
                writer.write(operation);
                writer.write("}");
                Main.newLine(writer);
            }
            LaTeXUtils.printEnd(LaTeXUtils.ITEMIZE, writer);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
        return result.toString();
    }

    default SearchTree<T> add(final T value) {
        final SearchTreeSteps<T> steps = this.addWithSteps(value);
        if (steps.isEmpty()) {
            return this;
        }
        return steps.get(steps.size() - 1).tree();
    }

    SearchTreeSteps<T> addWithSteps(final T value);

    default boolean contains(final T value) {
        return this.containsAll(Collections.singleton(value));
    }

    default boolean containsAll(final Collection<? extends T> values) {
        if (this.isEmpty()) {
            return values.isEmpty();
        }
        return this.root().get().containsAll(values);
    }

    default int getHeight() {
        return this.root().isEmpty() ? 0 : this.root().get().getHeight();
    }

    BigFraction getHorizontalFillingDegree();

    default Optional<T> getMax() {
        return this.stream().reduce((x,y) -> y);
    }

    default Optional<T> getMin() {
        return this.stream().findFirst();
    }

    default String getName() {
        return this.getName(false);
    }

    String getName(boolean genitive);

    String getOperations();

    Optional<String> getSamePageWidth();

    default TikZStyle getTikZStyle() {
        return TikZStyle.TREE;
    }

    default List<T> getValues() {
        return this.stream().toList();
    }

    default boolean isEmpty() {
        return this.root().isEmpty();
    }

    @Override
    default Iterator<T> iterator() {
        return this.stream().iterator();
    }

    default SearchTree<T> remove(final T value) {
        final SearchTreeSteps<T> steps = this.removeWithSteps(value);
        return steps.get(steps.size() - 1).tree();
    }

    SearchTreeSteps<T> removeWithSteps(final T value);

    Optional<? extends SearchTreeNode<T>> root();

    default int size() {
        if (this.isEmpty()) {
            return 0;
        }
        return this.root().get().size();
    }

    default Stream<T> stream() {
        if (this.isEmpty()) {
            return Stream.empty();
        }
        return this.root().get().stream();
    }

    String toTikZ();

}
