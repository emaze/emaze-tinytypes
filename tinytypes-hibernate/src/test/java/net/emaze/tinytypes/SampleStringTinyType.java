package net.emaze.tinytypes;

/**
 *
 * @author rferranti
 */
public class SampleStringTinyType extends StringTinyType {

    public SampleStringTinyType(String value) {
        super(value);
    }

    public static SampleStringTinyType of(String value) {
        return new SampleStringTinyType(value);
    }

}
