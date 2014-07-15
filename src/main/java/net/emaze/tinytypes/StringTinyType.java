package net.emaze.tinytypes;

import java.io.Serializable;

/**
 *
 * @author rferranti
 */
public abstract class StringTinyType implements Serializable {

    public final String value;

    public StringTinyType(String value) {
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
        final StringTinyType other = (StringTinyType) rhs;
        return this.value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
