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
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import net.emaze.tinytypes.IntTinyType;
import net.emaze.tinytypes.LongTinyType;
import net.emaze.tinytypes.StringTinyType;
import net.emaze.tinytypes.generation.TinyTypesReflector;
import net.emaze.tinytypes.generation.Template;

/**
 *
 * @author rferranti
 */
public class JacksonTinyTypesModule extends SimpleModule {

    public JacksonTinyTypesModule(String locationPattern) {
        super("tinytypes-module", new Version(1, 0, 0, null, "net.emaze", "emaze-petri-tiny-types"));
        this.addSerializer(LongTinyType.class, new LongTinyTypeSerializer());
        this.addSerializer(IntTinyType.class, new IntTinyTypeSerializer());
        this.addSerializer(StringTinyType.class, new StringTinyTypeSerializer());
        this.addKeySerializer(LongTinyType.class, new LongTinyTypeKeySerializer());
        this.addKeySerializer(IntTinyType.class, new IntTinyTypeKeySerializer());
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
            final CtClass cc = pool.makeClass(className);
            cc.setSuperclass(pool.get(JsonDeserializer.class.getName()));
            final String method = Template.of(
                    "public Object deserialize(com.fasterxml.jackson.core.JsonParser jp, com.fasterxml.jackson.databind.DeserializationContext ctxt)",
                    "  throws java.io.IOException, com.fasterxml.jackson.core.JsonProcessingException",
                    "{",
                    "   final %s value = (%sjp.readValueAs(%s.class))%s;",
                    "   return new %s(value);",
                    "}"
            ).format(
                    TinyTypesReflector.nestedType(concreteTinyType).getSimpleName(),
                    TinyTypesReflector.boxCast(concreteTinyType),
                    TinyTypesReflector.nestedType(concreteTinyType).getSimpleName(),
                    TinyTypesReflector.unboxFunctionCall(concreteTinyType),
                    concreteTinyType.getName()
            );
            cc.addMethod(CtNewMethod.make(method, cc));
            return (JsonDeserializer) cc.toClass().newInstance();
        } catch (ClassNotFoundException | CannotCompileException | InstantiationException | IllegalAccessException | NotFoundException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static KeyDeserializer createKeyDeserializer(Class<?> concreteTinyType) {
        try {
            final ClassPool pool = ClassPool.getDefault();
            pool.appendClassPath(new ClassClassPath(concreteTinyType));
            final String className = String.format("net.emaze.tinytypes.integration.%sKeyDeserializer", concreteTinyType.getSimpleName());
            if (pool.getOrNull(className) != null) {
                return (KeyDeserializer) Class.forName(className).newInstance();
            }
            final CtClass cc = pool.makeClass(className);
            cc.setSuperclass(pool.get(KeyDeserializer.class.getName()));
            final String method = Template.of(
                    "public Object deserializeKey(String key, com.fasterxml.jackson.databind.DeserializationContext ctxt)",
                    "  throws java.io.IOException, com.fasterxml.jackson.core.JsonProcessingException",
                    "{",
                    "  return new %s(%s(key)); ",
                    "}"
            ).format(concreteTinyType.getName(), TinyTypesReflector.parseFunction(concreteTinyType));
            cc.addMethod(CtNewMethod.make(method, cc));
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

    public static class StringTinyTypeKeySerializer extends JsonSerializer<StringTinyType> {

        @Override
        public void serialize(StringTinyType st, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeFieldName(st.value);
        }
    }
}
