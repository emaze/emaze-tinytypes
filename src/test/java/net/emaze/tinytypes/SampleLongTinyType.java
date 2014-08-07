package net.emaze.tinytypes;

/**
 *
 * @author rferranti
 */
public class SampleLongTinyType extends LongTinyType {

    public SampleLongTinyType(long value) {
        super(value);
    }

    public static SampleLongTinyType of(long value) {
        return new SampleLongTinyType(value);
    }

}
