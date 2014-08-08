package net.emaze.tinytypes;

import net.emaze.tinytypes.flywieghts.Flyweight;

/**
 *
 * @author rferranti
 */
@Flyweight(min = 100, max = 1000)
public class SampleFlyweightedLongTinyType extends LongTinyType {

    public SampleFlyweightedLongTinyType(long value) {
        super(value);
    }

    public static SampleFlyweightedLongTinyType of(long value) {
        return new SampleFlyweightedLongTinyType(value);
    }

}
