package net.emaze.tinytypes;

/**
 *
 * @author rferranti
 */
public class SampleShortTinyType extends ShortTinyType {

    public SampleShortTinyType(short value) {
        super(value);
    }

    public static SampleShortTinyType of(short value) {
        return new SampleShortTinyType(value);
    }

}
