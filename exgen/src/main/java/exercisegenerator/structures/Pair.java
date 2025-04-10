package exercisegenerator.structures;

import java.util.*;

public class Pair<X, Y> implements Map.Entry<X, Y>, java.io.Serializable {

    private static final long serialVersionUID = 6914181682796480167L;

    public X x;

    public Y y;

    public Pair(final X key, final Y value) {
        this.x = key;
        this.y = value;
    }

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

    @Override
    public X getKey() {
        return this.x;
    }

    @Override
    public Y getValue() {
        return this.y;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.x == null) ? 0 : this.x.hashCode());
        result = prime * result + ((this.y == null) ? 0 : this.y.hashCode());
        return result;
    }

    public X setKey(final X key) {
        final X old = this.x;
        this.x = key;
        return old;
    }

    @Override
    public Y setValue(final Y value) {
        final Y old = this.y;
        this.y = value;
        return old;
    }

    @Override
    public String toString() {
        return String.format(
            "(%s,%s)",
            this.x == null ? "null" : this.x.toString(),
            this.y == null ? "null" : this.y.toString()
        );
    }

}
