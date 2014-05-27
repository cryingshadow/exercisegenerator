import java.util.*;

/**
 * @author thiemann, cryingshadow
 *
 * A simple Pair, can be used to build ListOfMapEntries such that we can iterate over "duplicating Maps".
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

    /**
     * Creates a pair with the two specified components.
     * @param key The first component.
     * @param value The second component.
     */
    public Pair(X key, Y value) {
        this.x = key;
        this.y = value;
    }

    /* (non-Javadoc)
     * @see java.util.Map.Entry#setValue(java.lang.Object)
     */
    @Override
    public Y setValue(Y value) {
        final Y old = this.y;
        this.y = value;
        return old;
    }

    /**
     * @param key The first component to set.
     * @return The first component previously stored in this pair.
     */
    public X setKey(X key) {
        final X old = this.x;
        this.x = key;
        return old;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
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

    /**
     * Flips the two components of a pair where both components have the same (super) type.
     * @param pair The pair to flip.
     */
    public static <Z> void flip(Pair<Z, Z> pair) {
        final Z key = pair.getKey();
        final Z value = pair.getValue();
        pair.setValue(key);
        pair.setKey(value);
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

    /**
     * {@inheritDoc}
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
     * @author Sebastian Weise
     * @return A new pair with the same components as the current pair. The components are not copied.
     */
    public Pair<X, Y> shallowCopy() {
        return new Pair<X, Y>(this.x, this.y);
    }

}
