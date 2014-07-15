package net.emaze.tinytypes;

import java.io.Serializable;

/**
 *
 * @author rferranti
 */
public abstract class IntTinyType implements Serializable {

    public final int value;

    public IntTinyType(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object rhs) {
        if (rhs == null) {
            return false;
        }
        if (rhs.getClass() != this.getClass()) {
            return false;
        }
        final IntTinyType other = (IntTinyType) rhs;
        return this.value == other.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

}
