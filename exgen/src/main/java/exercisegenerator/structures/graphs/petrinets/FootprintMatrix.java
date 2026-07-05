package exercisegenerator.structures.graphs.petrinets;

import java.util.*;
import java.util.function.*;

import exercisegenerator.algorithms.learning.*;
import exercisegenerator.io.*;

public class FootprintMatrix implements BiFunction<String, String, FootprintRelation> {

    private final Map<String, Map<String, FootprintRelation>> matrix;

    public FootprintMatrix() {
        this.matrix = new TreeMap<String, Map<String, FootprintRelation>>();
    }

    @Override
    public FootprintRelation apply(final String firstActivity, final String secondActivity) {
        return this.matrix.get(firstActivity).get(secondActivity);
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof final FootprintMatrix m) {
            return this.matrix.equals(m.matrix);
        }
        return false;
    }

    public Set<String> getActivities() {
        return this.matrix.keySet();
    }

    @Override
    public int hashCode() {
        return this.matrix.hashCode() * 2;
    }

    public void merge(final String firstActivity, final String secondActivity, final FootprintRelation relation) {
        if (!this.matrix.containsKey(firstActivity)) {
            this.matrix.put(firstActivity, new TreeMap<String, FootprintRelation>());
        }
        final Map<String, FootprintRelation> row = this.matrix.get(firstActivity);
        if (row.containsKey(secondActivity)) {
            row.put(secondActivity, row.get(secondActivity).merge(relation));
        } else {
            row.put(secondActivity, relation);
        }
    }

    public FootprintMatrix put(
        final String firstActivity,
        final String secondActivity,
        final FootprintRelation relation
    ) {
        if (!this.matrix.containsKey(firstActivity)) {
            this.matrix.put(firstActivity, new TreeMap<String, FootprintRelation>());
        }
        this.matrix.get(firstActivity).put(secondActivity, relation);
        return this;
    }

    public String[][] toMatrix() {
        final String[][] result = new String[this.matrix.size() + 1][this.matrix.size() + 1];
        result[0][0] = "";
        result[1][0] = String.format("$%s$", AlphaAlgorithm.START_ACTIVITY);
        result[0][1] = result[1][0];
        result[this.matrix.size()][0] = String.format("$%s$", AlphaAlgorithm.END_ACTIVITY);
        result[0][this.matrix.size()] = result[this.matrix.size()][0];
        int index = 2;
        for (final String activity : this.matrix.keySet()) {
            if (AlphaAlgorithm.START_ACTIVITY.equals(activity) || AlphaAlgorithm.END_ACTIVITY.equals(activity)) {
                continue;
            }
            result[index][0] = activity;
            result[0][index] = activity;
            index++;
        }
        result[1][1] =
            LaTeXUtils.inlineMath(this.apply(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.START_ACTIVITY).symbol);
        result[1][this.matrix.size()] =
            LaTeXUtils.inlineMath(this.apply(AlphaAlgorithm.START_ACTIVITY, AlphaAlgorithm.END_ACTIVITY).symbol);
        result[this.matrix.size()][this.matrix.size()] =
            LaTeXUtils.inlineMath(this.apply(AlphaAlgorithm.END_ACTIVITY, AlphaAlgorithm.END_ACTIVITY).symbol);
        result[this.matrix.size()][1] =
            LaTeXUtils.inlineMath(this.apply(AlphaAlgorithm.END_ACTIVITY, AlphaAlgorithm.START_ACTIVITY).symbol);
        for (int row = 2; row < result.length - 1; row++) {
            result[1][row] = LaTeXUtils.inlineMath(this.apply(AlphaAlgorithm.START_ACTIVITY, result[0][row]).symbol);
            result[row][1] = LaTeXUtils.inlineMath(this.apply(result[0][row], AlphaAlgorithm.START_ACTIVITY).symbol);
            result[this.matrix.size()][row] =
                LaTeXUtils.inlineMath(this.apply(AlphaAlgorithm.END_ACTIVITY, result[0][row]).symbol);
            result[row][this.matrix.size()] =
                LaTeXUtils.inlineMath(this.apply(result[0][row], AlphaAlgorithm.END_ACTIVITY).symbol);
            for (int column = 2; column < result.length - 1; column++) {
                result[row][column] = LaTeXUtils.inlineMath(this.apply(result[row][0], result[0][column]).symbol);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return Arrays.deepToString(this.toMatrix());
    }

}
