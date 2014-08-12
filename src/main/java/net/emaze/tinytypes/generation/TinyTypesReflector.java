package net.emaze.tinytypes.generation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javassist.CannotCompileException;
import net.emaze.tinytypes.BooleanTinyType;
import net.emaze.tinytypes.ByteTinyType;
import net.emaze.tinytypes.IntTinyType;
import net.emaze.tinytypes.LongTinyType;
import net.emaze.tinytypes.ShortTinyType;
import net.emaze.tinytypes.StringTinyType;
import net.emaze.tinytypes.flywieghts.Flyweight;
import net.emaze.tinytypes.flywieghts.FlyweightGenerator;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;

/**
 *
 * @author rferranti
 */
public class TinyTypesReflector {

    public static Map<String, String> bindings(Class<?> tinyType) {
        if (LongTinyType.class.isAssignableFrom(tinyType)) {
            final Map<String, String> r = new HashMap<>();
            r.put("tinytype", tinyType.getName());
            r.put("factory", "new " + tinyType.getName());
            r.put("boxcast", "(Long)");
            r.put("boxfn", "Long.valueOf");
            r.put("nestedtype", "long");
            r.put("parse", "Long.parseLong");
            r.put("stringify", "Long.toString");
            r.put("unboxmethodcall", ".longValue()");
            r.put("sqltype", Integer.toString(StandardBasicTypes.LONG.sqlType()));
            r.put("usefw", "false");
            if (tinyType.isAnnotationPresent(Flyweight.class)) {
                final Flyweight f = tinyType.getAnnotation(Flyweight.class);
                r.put("usefw", "true");
                r.put("factory", String.format("net.emaze.tinytypes.gen.%sFlyWeight.fw", tinyType.getSimpleName()));
                r.put("fwminvalue", Integer.toString(f.min()));
                r.put("fwmaxvalue", Integer.toString(f.max()));
                r.put("fwlastarrayindex", Integer.toString(f.max() - f.min()));
                r.put("fwlength", Integer.toString(1 + f.max() - f.min()));
            }
            return r;
        }
        if (IntTinyType.class.isAssignableFrom(tinyType)) {
            final Map<String, String> r = new HashMap<>();
            r.put("tinytype", tinyType.getName());
            r.put("factory", "new " + tinyType.getName());
            r.put("boxcast", "(Integer)");
            r.put("boxfn", "Integer.valueOf");
            r.put("nestedtype", "int");
            r.put("parse", "Integer.parseInt");
            r.put("stringify", "Integer.toString");
            r.put("unboxmethodcall", ".intValue()");
            r.put("sqltype", Integer.toString(StandardBasicTypes.INTEGER.sqlType()));
            r.put("usefw", "false");
            if (tinyType.isAnnotationPresent(Flyweight.class)) {
                final Flyweight f = tinyType.getAnnotation(Flyweight.class);
                r.put("usefw", "true");
                r.put("factory", String.format("net.emaze.tinytypes.gen.%sFlyWeight.fw", tinyType.getSimpleName()));
                r.put("fwminvalue", Integer.toString(f.min()));
                r.put("fwmaxvalue", Integer.toString(f.max()));
                r.put("fwlastarrayindex", Integer.toString(f.max() - f.min()));
                r.put("fwlength", Integer.toString(1 + f.max() - f.min()));
            }
            return r;
        }
        if (ShortTinyType.class.isAssignableFrom(tinyType)) {
            final Map<String, String> r = new HashMap<>();
            r.put("tinytype", tinyType.getName());
            r.put("factory", "new " + tinyType.getName());
            r.put("boxcast", "(Short)");
            r.put("boxfn", "Short.valueOf");
            r.put("nestedtype", "short");
            r.put("parse", "Short.parseShort");
            r.put("stringify", "Short.toString");
            r.put("unboxmethodcall", ".shortValue()");
            r.put("sqltype", Integer.toString(StandardBasicTypes.SHORT.sqlType()));
            r.put("usefw", "false");
            if (tinyType.isAnnotationPresent(Flyweight.class)) {
                final Flyweight f = tinyType.getAnnotation(Flyweight.class);
                r.put("usefw", "true");
                r.put("factory", String.format("net.emaze.tinytypes.gen.%sFlyWeight.fw", tinyType.getSimpleName()));
                r.put("fwminvalue", Integer.toString(f.min()));
                r.put("fwmaxvalue", Integer.toString(f.max()));
                r.put("fwlastarrayindex", Integer.toString(f.max() - f.min()));
                r.put("fwlength", Integer.toString(1 + f.max() - f.min()));
            }
            return r;
        }
        if (ByteTinyType.class.isAssignableFrom(tinyType)) {
            final Map<String, String> r = new HashMap<>();
            r.put("tinytype", tinyType.getName());
            r.put("factory", "new " + tinyType.getName());
            r.put("boxcast", "(Byte)");
            r.put("boxfn", "Byte.valueOf");
            r.put("nestedtype", "byte");
            r.put("parse", "Byte.parseByte");
            r.put("stringify", "Byte.toString");
            r.put("unboxmethodcall", ".byteValue()");
            r.put("sqltype", Integer.toString(StandardBasicTypes.BYTE.sqlType()));
            r.put("usefw", "false");
            if (tinyType.isAnnotationPresent(Flyweight.class)) {
                final Flyweight f = tinyType.getAnnotation(Flyweight.class);
                r.put("usefw", "true");
                r.put("factory", String.format("net.emaze.tinytypes.gen.%sFlyWeight.fw", tinyType.getSimpleName()));
                r.put("fwminvalue", Integer.toString(f.min()));
                r.put("fwmaxvalue", Integer.toString(f.max()));
                r.put("fwlastarrayindex", Integer.toString(f.max() - f.min()));
                r.put("fwlength", Integer.toString(1 + f.max() - f.min()));
            }
            return r;
        }
        if (BooleanTinyType.class.isAssignableFrom(tinyType)) {
            final Map<String, String> r = new HashMap<>();
            r.put("tinytype", tinyType.getName());
            r.put("factory", "new " + tinyType.getName());
            r.put("boxcast", "(Boolean)");
            r.put("boxfn", "Boolean.valueOf");
            r.put("nestedtype", "boolean");
            r.put("parse", "Boolean.parseBoolean");
            r.put("stringify", "Boolean.toString");
            r.put("unboxmethodcall", ".booleanValue()");
            r.put("sqltype", Integer.toString(StandardBasicTypes.BOOLEAN.sqlType()));
            r.put("usefw", "true");
            r.put("factory", String.format("net.emaze.tinytypes.gen.%sFlyWeight.fw", tinyType.getSimpleName()));
            return r;
        }
        if (StringTinyType.class.isAssignableFrom(tinyType)) {
            final Map<String, String> r = new HashMap<>();
            r.put("tinytype", tinyType.getName());
            r.put("factory", "new " + tinyType.getName());
            r.put("boxcast", "(String)");
            r.put("boxfn", "");
            r.put("nestedtype", "String");
            r.put("parse", "");
            r.put("stringify", "");
            r.put("unboxmethodcall", "");
            r.put("sqltype", Integer.toString(StandardBasicTypes.TEXT.sqlType()));
            r.put("usefw", "false");
            return r;
        }
        throw new IllegalStateException(String.format("unknown tiny type: %s", tinyType));
    }

    private static final ResourcePatternResolver RESOLVER = new PathMatchingResourcePatternResolver();
    private static final MetadataReaderFactory MREADER = new CachingMetadataReaderFactory(RESOLVER);
    private static final Map<String, List<Class<?>>> LOCATION_PATTERN_TO_TYPES_CACHE = new HashMap<>();
    public static final Set<String> TINY_TYPE_NAMES = Stream.of(
            StringTinyType.class.getName(),
            LongTinyType.class.getName(),
            IntTinyType.class.getName(),
            ShortTinyType.class.getName(),
            ByteTinyType.class.getName(),
            BooleanTinyType.class.getName()
    ).collect(Collectors.toSet());

    public static synchronized List<Class<?>> scan(String locationPattern) {
        if (LOCATION_PATTERN_TO_TYPES_CACHE.containsKey(locationPattern)) {
            return LOCATION_PATTERN_TO_TYPES_CACHE.get(locationPattern);
        }
        try {
            final List<Class<?>> tinyTypes = new ArrayList<>();
            for (Resource resource : RESOLVER.getResources(locationPattern)) {
                final ClassMetadata cm = MREADER.getMetadataReader(resource).getClassMetadata();
                final String candidateName = cm.getSuperClassName();
                if (!TINY_TYPE_NAMES.contains(candidateName)) {
                    continue;
                }
                tinyTypes.add(Class.forName(cm.getClassName()));
            }
            LOCATION_PATTERN_TO_TYPES_CACHE.put(locationPattern, tinyTypes);
            for (Class<?> tinyType : tinyTypes) {
                FlyweightGenerator.createIfNeeded(tinyType);
            }
            return tinyTypes;
        } catch (ClassNotFoundException | CannotCompileException | IOException ex) {
            throw new IllegalStateException(ex);
        }

    }

}
