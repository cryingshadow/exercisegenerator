package exercisegenerator.structures;

import java.util.*;

/**
 * A simple pair extending Map.Entry.
 * @param <X> The type of the first component.
 * @param <Y> The type of the second component.
 */
public class Pair<X, Y> implements Map.Entry<X, Y>, java.io.Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 6914181682796480167L;

    /**
     * The first component.
     */
    public X x;

    /**
     * The second component.
     */
    public Y y;

    /**
     * Creates a pair with the two specified components.
     * @param key The first component.
     * @param value The second component.
     */
    public Pair(final X key, final Y value) {
        this.x = key;
        this.y = value;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Pair<?, ?> other = (Pair<?, ?>) obj;
        if (this.x == null) {
            if (other.x != null) {
                return false;
            }
        } else if (!this.x.equals(other.x)) {
            return false;
        }
        if (this.y == null) {
            if (other.y != null) {
                return false;
            }
        } else if (!this.y.equals(other.y)) {
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see java.util.Map.Entry#getKey()
     */
    @Override
    public X getKey() {
        return this.x;
    }

    /* (non-Javadoc)
     * @see java.util.Map.Entry#getValue()
     */
    @Override
    public Y getValue() {
        return this.y;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.x == null) ? 0 : this.x.hashCode());
        result = prime * result + ((this.y == null) ? 0 : this.y.hashCode());
        return result;
    }

    /**
     * @param key The first component to set.
     * @return The first component previously stored in this pair.
     */
    public X setKey(final X key) {
        final X old = this.x;
        this.x = key;
        return old;
    }

    /* (non-Javadoc)
     * @see java.util.Map.Entry#setValue(java.lang.Object)
     */
    @Override
    public Y setValue(final Y value) {
        final Y old = this.y;
        this.y = value;
        return old;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return
            "("
            + (this.x == null ? "null" : this.x.toString())
            + ","
            + (this.y == null ? "null" : this.y.toString())
            + ")";
    }

}
