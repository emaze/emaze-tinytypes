package net.emaze.tinytypes;

import net.emaze.tinytypes.integration.JacksonTinyTypesModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.emaze.tinytypes.generation.TinyTypesReflector;
import org.junit.Test;

/**
 *
 * @author rferranti
 */
public class JacksonTest {

    public static class MarioTinyType extends LongTinyType {

        public MarioTinyType(long value) {
            super(value);
        }

    }

    @Test
    public void canRegisterTheModule() {
        ObjectMapper m = new ObjectMapper();
        m.registerModule(new JacksonTinyTypesModule("classpath*:/net/emaze/**/*.class"));
    }
}
