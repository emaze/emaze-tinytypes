package net.emaze.tinytypes;

import java.io.Serializable;

/**
 *
 * @author rferranti
 */
public abstract class ByteTinyType implements Serializable, Comparable<ByteTinyType> {

    public final byte value;

    public ByteTinyType(byte value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%s#%s", this.getClass().getSimpleName(), value);
    }

    @Override
    public int compareTo(ByteTinyType other) {
        return Byte.compare(this.value, other.value);
    }

    @Override
    public boolean equals(Object rhs) {
        if (rhs == null) {
            return false;
        }
        if (rhs.getClass() != this.getClass()) {
            return false;
        }
        final ByteTinyType other = (ByteTinyType) rhs;
        return this.value == other.value;
    }

    @Override
    public int hashCode() {
        return Byte.hashCode(value);
    }

}
