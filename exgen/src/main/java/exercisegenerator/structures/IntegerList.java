package exercisegenerator.structures;

import java.util.*;
import java.util.stream.*;

import exercisegenerator.io.*;

public class IntegerList extends ArrayList<Integer> {

    private static final long serialVersionUID = -3161606546742721639L;

    public static List<ItemWithTikZInformation<Integer>> toTikZList(final IntegerList[] array) {
        return Arrays.stream(array)
            .map(list -> list.isEmpty() ?
                new ItemWithTikZInformation<Integer>() :
                    new ItemWithTikZInformation<Integer>(Optional.of(list.get(0))))
            .toList();
    }

    public static String[] toVerticalStringArray(final IntegerList[] array) {
        final String[] result = new String[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] =
                String.format("%d: %s", i, array[i].stream().map(String::valueOf).collect(Collectors.joining(", ")));
        }
        return result;
    }

    public IntegerList() {
        super();
    }

    public IntegerList(final Collection<Integer> c) {
        super(c);
    }

    public IntegerList(final Integer... values) {
        super(Arrays.asList(values));
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof IntegerList)) {
            return false;
        }
        final IntegerList otherList = (IntegerList)o;
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