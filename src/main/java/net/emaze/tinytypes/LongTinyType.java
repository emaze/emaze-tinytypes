package net.emaze.tinytypes;

import java.io.Serializable;

/**
 *
 * @author rferranti
 */
public abstract class LongTinyType implements Serializable, Comparable<LongTinyType> {

    public final long value;

    public LongTinyType(long value) {
        this.value = value;
    }

    @Override
    public int compareTo(LongTinyType other) {
        return Long.compare(this.value, other.value);
    }

    @Override
    public boolean equals(Object rhs) {
        if (rhs == null) {
            return false;
        }
        if (rhs.getClass() != this.getClass()) {
            return false;
        }
        final LongTinyType other = (LongTinyType) rhs;
        return this.value == other.value;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }

}
