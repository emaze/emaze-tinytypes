package net.emaze.tinytypes;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.emaze.tinytypes.integration.JacksonTinyTypesModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author rferranti
 */
public class JacksonTest {

    @Test
    public void canRegisterTheModule() {
        final ObjectMapper m = new ObjectMapper();
        m.registerModule(new JacksonTinyTypesModule("classpath*:/net/emaze/**/*.class"));
    }

    @Test
    public void canSerialize() throws JsonProcessingException {
        final ObjectMapper m = new ObjectMapper();
        m.registerModule(new JacksonTinyTypesModule("classpath*:/net/emaze/**/*.class"));
        final String got = m.writeValueAsString(new SampleIntTinyType(123));
        Assert.assertEquals("123", got);
    }

    @Test
    public void canDeserialize() throws JsonProcessingException, IOException {
        final ObjectMapper m = new ObjectMapper();
        m.registerModule(new JacksonTinyTypesModule("classpath*:/net/emaze/**/*.class"));
        SampleIntTinyType got = m.readValue("123", SampleIntTinyType.class);
        Assert.assertEquals(123, got.value);
    }

    @Test
    public void canSerializeFw() throws JsonProcessingException {
        final ObjectMapper m = new ObjectMapper();
        m.registerModule(new JacksonTinyTypesModule("classpath*:/net/emaze/**/*.class"));
        final String got = m.writeValueAsString(new SampleFlyweightedLongTinyType(123));
        Assert.assertEquals("123", got);
    }

    @Test
    public void canDeserializeFw() throws JsonProcessingException, IOException {
        final ObjectMapper m = new ObjectMapper();
        m.registerModule(new JacksonTinyTypesModule("classpath*:/net/emaze/**/*.class"));
        SampleFlyweightedLongTinyType got = m.readValue("123", SampleFlyweightedLongTinyType.class);
        Assert.assertEquals(123, got.value);
    }
}
