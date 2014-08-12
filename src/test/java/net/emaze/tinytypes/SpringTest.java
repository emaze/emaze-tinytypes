package net.emaze.tinytypes;

import net.emaze.tinytypes.integration.SpringConverters;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.format.support.DefaultFormattingConversionService;

/**
 *
 * @author rferranti
 */
public class SpringTest {

    @Test
    public void canRegisterTheModule() {
        DefaultFormattingConversionService registry = new DefaultFormattingConversionService();
        SpringConverters.register(registry, "classpath*:/net/emaze/**/*.class");
    }

    @Test
    public void canConvertIntTinyTypeFromString() {
        DefaultFormattingConversionService registry = new DefaultFormattingConversionService();
        SpringConverters.register(registry, "classpath*:/net/emaze/**/*.class");
        SampleIntTinyType got = registry.convert("321", SampleIntTinyType.class);
        Assert.assertEquals(321, got.value);
    }

}
