package net.emaze.tinytypes;

import net.emaze.tinytypes.integration.SpringConverters;
import org.junit.Assert;
import net.emaze.tinytypes.JacksonTest.MarioTinyType;
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
    public void canConvertTypeFromString() {
        DefaultFormattingConversionService registry = new DefaultFormattingConversionService();
        SpringConverters.register(registry, "classpath*:/net/emaze/**/*.class");
        MarioTinyType got = registry.convert("321", MarioTinyType.class);
        Assert.assertEquals(321, got.value);
    }
}
