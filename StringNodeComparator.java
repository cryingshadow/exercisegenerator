import java.util.*;

/**
 * String node comparator based on String.compareTo.
 * @author cryingshadow
 * @version $Id$
 */
public class StringNodeComparator implements Comparator<Node<String>> {

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(Node<String> o1, Node<String> o2) {
        return o1.getLabel().compareTo(o2.getLabel());
    }

}
