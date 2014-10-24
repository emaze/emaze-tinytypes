package net.emaze.tinytypes;

/**
 *
 * @author rferranti
 */
public class SampleByteTinyType extends ByteTinyType {

    public SampleByteTinyType(byte value) {
        super(value);
    }

    public static SampleByteTinyType of(byte value) {
        return new SampleByteTinyType(value);
    }

}
