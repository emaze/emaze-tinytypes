package net.emaze.tinytypes;

/**
 *
 * @author rferranti
 */
public class SampleIntTinyType extends IntTinyType {

    public SampleIntTinyType(int value) {
        super(value);
    }

    public static SampleIntTinyType of(int value) {
        return new SampleIntTinyType(value);
    }

}
