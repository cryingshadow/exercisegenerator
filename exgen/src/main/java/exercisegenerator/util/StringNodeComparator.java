package exercisegenerator.util;

import java.util.*;

import exercisegenerator.structures.*;
import exercisegenerator.structures.graphs.*;

/**
 * String node comparator based on String.compareTo.
 */
public class StringNodeComparator implements Comparator<LabeledNode<String>> {

    @Override
    public int compare(final LabeledNode<String> o1, final LabeledNode<String> o2) {
        return o1.label.orElse("").compareTo(o2.label.orElse(""));
    }

}
