package net.emaze.tinytypes;

/**
 *
 * @author rferranti
 */
public class SampleBooleanTinyType extends BooleanTinyType {

    public SampleBooleanTinyType(boolean value) {
        super(value);
    }

    public static SampleBooleanTinyType of(boolean value) {
        return new SampleBooleanTinyType(value);
    }

}
