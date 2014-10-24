package net.emaze.tinytypes.integration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import java.util.Map;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import net.emaze.tinytypes.BooleanTinyType;
import net.emaze.tinytypes.ByteTinyType;
import net.emaze.tinytypes.IntTinyType;
import net.emaze.tinytypes.LongTinyType;
import net.emaze.tinytypes.ShortTinyType;
import net.emaze.tinytypes.StringTinyType;
import net.emaze.tinytypes.generation.Template;
import net.emaze.tinytypes.generation.TinyTypesReflector;

/**
 *
 * @author rferranti
 */
public class JacksonTinyTypesModule extends SimpleModule {

    public JacksonTinyTypesModule(String locationPattern) {
        super("tinytypes-module", new Version(1, 0, 0, null, "net.emaze", "emaze-petri-tiny-types"));
        this.addSerializer(LongTinyType.class, new LongTinyTypeSerializer());
        this.addSerializer(IntTinyType.class, new IntTinyTypeSerializer());
        this.addSerializer(ShortTinyType.class, new ShortTinyTypeSerializer());
        this.addSerializer(ByteTinyType.class, new ByteTinyTypeSerializer());
        this.addSerializer(BooleanTinyType.class, new BooleanTinyTypeSerializer());
        this.addSerializer(StringTinyType.class, new StringTinyTypeSerializer());
        this.addKeySerializer(LongTinyType.class, new LongTinyTypeKeySerializer());
        this.addKeySerializer(IntTinyType.class, new IntTinyTypeKeySerializer());
        this.addKeySerializer(ShortTinyType.class, new ShortTinyTypeKeySerializer());
        this.addKeySerializer(ByteTinyType.class, new ByteTinyTypeKeySerializer());
        this.addKeySerializer(BooleanTinyType.class, new BooleanTinyTypeKeySerializer());
        this.addKeySerializer(StringTinyType.class, new StringTinyTypeKeySerializer());
        for (Class<?> tinyType : TinyTypesReflector.scan(locationPattern)) {
            this.addDeserializer(tinyType, createDeserializer(tinyType));
            this.addKeyDeserializer(tinyType, createKeyDeserializer(tinyType));
        }
    }

    private static JsonDeserializer createDeserializer(Class<?> concreteTinyType) {
        try {
            final ClassPool pool = ClassPool.getDefault();
            pool.appendClassPath(new ClassClassPath(concreteTinyType));
            final String className = String.format("net.emaze.tinytypes.gen.%sDeserializer", concreteTinyType.getSimpleName());
            if (pool.getOrNull(className) != null) {
                return (JsonDeserializer) Class.forName(className).newInstance();
            }
            final Map<String, String> bindings = TinyTypesReflector.bindings(concreteTinyType);
            final CtClass cc = pool.makeClass(className);
            cc.setSuperclass(pool.get(JsonDeserializer.class.getName()));
            Template.of(
                    "public Object deserialize(com.fasterxml.jackson.core.JsonParser jp, com.fasterxml.jackson.databind.DeserializationContext ctxt)",
                    "  throws java.io.IOException, com.fasterxml.jackson.core.JsonProcessingException",
                    "{",
                    "   final {nestedtype} value = ({boxcast}jp.readValueAs({nestedtype}.class)){unboxmethodcall};",
                    "   return {factory}(value);",
                    "}")
                    .with(bindings)
                    .asMethodFor(cc);
            return (JsonDeserializer) cc.toClass().newInstance();
        } catch (ClassNotFoundException | CannotCompileException | InstantiationException | IllegalAccessException | NotFoundException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static KeyDeserializer createKeyDeserializer(Class<?> concreteTinyType) {
        try {
            final ClassPool pool = ClassPool.getDefault();
            pool.appendClassPath(new ClassClassPath(concreteTinyType));
            final String className = String.format("net.emaze.tinytypes.gen.%sKeyDeserializer", concreteTinyType.getSimpleName());
            if (pool.getOrNull(className) != null) {
                return (KeyDeserializer) Class.forName(className).newInstance();
            }
            final Map<String, String> bindings = TinyTypesReflector.bindings(concreteTinyType);
            final CtClass cc = pool.makeClass(className);
            cc.setSuperclass(pool.get(KeyDeserializer.class.getName()));

            Template.of(
                    "public Object deserializeKey(String key, com.fasterxml.jackson.databind.DeserializationContext ctxt)",
                    "  throws java.io.IOException, com.fasterxml.jackson.core.JsonProcessingException",
                    "{",
                    "  return {factory}({parse}(key)); ",
                    "}")
                    .with(bindings)
                    .asMethodFor(cc);
            return (KeyDeserializer) cc.toClass().newInstance();
        } catch (ClassNotFoundException | CannotCompileException | InstantiationException | IllegalAccessException | NotFoundException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static class LongTinyTypeSerializer extends JsonSerializer<LongTinyType> {

        @Override
        public void serialize(LongTinyType st, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeObject(st.value);
        }
    }

    public static class IntTinyTypeSerializer extends JsonSerializer<IntTinyType> {

        @Override
        public void serialize(IntTinyType st, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeObject(st.value);
        }
    }

    public static class ShortTinyTypeSerializer extends JsonSerializer<ShortTinyType> {

        @Override
        public void serialize(ShortTinyType st, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeObject(st.value);
        }
    }

    public static class ByteTinyTypeSerializer extends JsonSerializer<ByteTinyType> {

        @Override
        public void serialize(ByteTinyType st, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeObject(st.value);
        }
    }

    public static class BooleanTinyTypeSerializer extends JsonSerializer<BooleanTinyType> {

        @Override
        public void serialize(BooleanTinyType st, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeObject(st.value);
        }
    }

    public static class StringTinyTypeSerializer extends JsonSerializer<StringTinyType> {

        @Override
        public void serialize(StringTinyType st, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeObject(st.value);
        }
    }

    public static class LongTinyTypeKeySerializer extends JsonSerializer<LongTinyType> {

        @Override
        public void serialize(LongTinyType st, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeFieldName(Long.toString(st.value));
        }
    }

    public static class IntTinyTypeKeySerializer extends JsonSerializer<IntTinyType> {

        @Override
        public void serialize(IntTinyType st, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeFieldName(Integer.toString(st.value));
        }
    }

    public static class ShortTinyTypeKeySerializer extends JsonSerializer<ShortTinyType> {

        @Override
        public void serialize(ShortTinyType st, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeFieldName(Short.toString(st.value));
        }
    }

    public static class ByteTinyTypeKeySerializer extends JsonSerializer<ByteTinyType> {

        @Override
        public void serialize(ByteTinyType st, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeFieldName(Byte.toString(st.value));
        }
    }

    public static class BooleanTinyTypeKeySerializer extends JsonSerializer<BooleanTinyType> {

        @Override
        public void serialize(BooleanTinyType st, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeFieldName(Boolean.toString(st.value));
        }
    }

    public static class StringTinyTypeKeySerializer extends JsonSerializer<StringTinyType> {

        @Override
        public void serialize(StringTinyType st, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeFieldName(st.value);
        }
    }
}
