package exercisegenerator.structures.graphs.petrinets;

import java.util.*;

public class ActivityTrace extends ArrayList<String> implements Comparable<ActivityTrace> {

    private static final long serialVersionUID = 1L;

    @Override
    public int compareTo(final ActivityTrace o) {
        for (int i = 0; i < this.size(); i++) {
            if (i >= o.size()) {
                return 1;
            }
            final int compare = this.get(i).compareTo(o.get(i));
            if (compare != 0) {
                return compare;
            }
        }
        if (o.size() > this.size()) {
            return -1;
        }
        return 0;
    }

}
