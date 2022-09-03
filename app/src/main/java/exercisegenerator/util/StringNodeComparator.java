package exercisegenerator.util;

import java.util.*;

import exercisegenerator.structures.*;

/**
 * String node comparator based on String.compareTo.
 * @author Thomas Stroeder
 * @version 1.0
 */
public class StringNodeComparator implements Comparator<Node<String>> {

    @Override
    public int compare(final Node<String> o1, final Node<String> o2) {
        return o1.label.orElse("").compareTo(o2.label.orElse(""));
    }

}
