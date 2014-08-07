package net.emaze.tinytypes;

import java.io.Serializable;

/**
 *
 * @author rferranti
 */
public abstract class ShortTinyType implements Serializable, Comparable<ShortTinyType> {

    public final short value;

    public ShortTinyType(short value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%s#%s", this.getClass().getSimpleName(), value);
    }

    @Override
    public int compareTo(ShortTinyType other) {
        return Short.compare(this.value, other.value);
    }

    @Override
    public boolean equals(Object rhs) {
        if (rhs == null) {
            return false;
        }
        if (rhs.getClass() != this.getClass()) {
            return false;
        }
        final ShortTinyType other = (ShortTinyType) rhs;
        return this.value == other.value;
    }

    @Override
    public int hashCode() {
        return Short.hashCode(value);
    }

}
