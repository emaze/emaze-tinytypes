package net.emaze.tinytypes;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import java.lang.reflect.Constructor;

/**
 *
 * @author rferranti
 */
public class JacksonTinyTypesModule extends SimpleModule {

    public JacksonTinyTypesModule() {
        super("tinytypes-module", new Version(1, 0, 0, null, "net.emaze", "emaze-petri-tiny-types"));
        this.addSerializer(LongTinyType.class, new TinyTypeSerializer());
        this.addSerializer(IntTinyType.class, new TinyTypeSerializer());
        this.addSerializer(StringTinyType.class, new TinyTypeSerializer());
        this.addKeySerializer(LongTinyType.class, new TinyTypeSerializer());
        this.addKeySerializer(IntTinyType.class, new TinyTypeSerializer());
        this.addKeySerializer(StringTinyType.class, new TinyTypeSerializer());
    }

    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);

        context.addDeserializers(new Deserializers.Base() {
            @Override
            public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
                final Class<?> candidateTinyType = type.getRawClass();
                if (LongTinyType.class.isAssignableFrom(candidateTinyType)) {
                    return new TinyTypesDeserializer(candidateTinyType, long.class);
                }
                if (IntTinyType.class.isAssignableFrom(candidateTinyType)) {
                    return new TinyTypesDeserializer(candidateTinyType, int.class);
                }
                if (StringTinyType.class.isAssignableFrom(candidateTinyType)) {
                    return new TinyTypesDeserializer(candidateTinyType, String.class);
                }
                return null;
            }
        });
    }

    public static class TinyTypesDeserializer extends JsonDeserializer<Object> {

        private final Class<?> tinyType;
        private final Class<?> nestedType;

        public TinyTypesDeserializer(Class<?> tinyType, Class<?> nestedType) {
            this.tinyType = tinyType;
            this.nestedType = nestedType;
        }

        @Override
        public Object deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            final Constructor ctor = TinyTypesReflector.ctor(tinyType);
            return TinyTypesReflector.create(tinyType, ctor, jp.readValueAs(nestedType));
        }

    }    

    public static class TinyTypeSerializer extends JsonSerializer<Object> {

        @Override
        public void serialize(Object st, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeObject(TinyTypesReflector.value(st));
        }
    }
}
