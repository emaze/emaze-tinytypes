package net.emaze.tinytypes;

import java.io.Serializable;

/**
 *
 * @author rferranti
 */
public abstract class StringTinyType implements Serializable, Comparable<StringTinyType> {

    public final String value;

    protected StringTinyType(String value) {
        if (value == null) {
            throw new IllegalArgumentException("value must be not null");
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%s#%s", this.getClass().getSimpleName(), value);
    }

    @Override
    public int compareTo(StringTinyType other) {
        return value.compareTo(other.value);
    }

    @Override
    public boolean equals(Object rhs) {
        if (rhs == null) {
            return false;
        }
        if (rhs.getClass() != this.getClass()) {
            return false;
        }
        final StringTinyType other = (StringTinyType) rhs;
        return this.value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
