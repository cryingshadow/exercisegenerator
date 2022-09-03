package exercisegenerator.structures;

import java.util.*;
import java.util.stream.*;

public class HashList extends ArrayList<Integer> {

    private static final long serialVersionUID = -3161606546742721639L;

    public HashList() {
        super();
    }

    public HashList(final Collection<Integer> c) {
        super(c);
    }

    public HashList(final Integer... values) {
        super(Arrays.asList(values));
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof HashList)) {
            return false;
        }
        final HashList otherList = (HashList)o;
        if (this.size() != otherList.size()) {
            return false;
        }
        for (int i = 0; i < this.size(); i++) {
            if (!this.get(i).equals(otherList.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 97;
        for (int i = 0; i < this.size(); i++) {
            result += this.get(i) * 3 + i * 7;
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("[%s]", this.stream().map(String::valueOf).collect(Collectors.joining(", ")));
    }

}