package net.emaze.tinytypes.generation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.emaze.tinytypes.ByteTinyType;
import net.emaze.tinytypes.IntTinyType;
import net.emaze.tinytypes.LongTinyType;
import net.emaze.tinytypes.ShortTinyType;
import net.emaze.tinytypes.StringTinyType;
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
            r.put("boxcast", "(Long)");
            r.put("boxfn", "Long.valueOf");
            r.put("nestedtype", "long");
            r.put("parse", "Long.parseLong");
            r.put("stringify", "Long.toString");
            r.put("unboxmethodcall", ".longValue()");
            r.put("sqltype", Integer.toString(StandardBasicTypes.LONG.sqlType()));
            return r;
        }
        if (IntTinyType.class.isAssignableFrom(tinyType)) {
            final Map<String, String> r = new HashMap<>();
            r.put("tinytype", tinyType.getName());
            r.put("boxcast", "(Integer)");
            r.put("boxfn", "Integer.valueOf");
            r.put("nestedtype", "int");
            r.put("parse", "Integer.parseInt");
            r.put("stringify", "Integer.toString");
            r.put("unboxmethodcall", ".intValue()");
            r.put("sqltype", Integer.toString(StandardBasicTypes.INTEGER.sqlType()));
            return r;
        }
        if (ShortTinyType.class.isAssignableFrom(tinyType)) {
            final Map<String, String> r = new HashMap<>();
            r.put("tinytype", tinyType.getName());
            r.put("boxcast", "(Short)");
            r.put("boxfn", "Short.valueOf");
            r.put("nestedtype", "short");
            r.put("parse", "Short.parseShort");
            r.put("stringify", "Short.toString");
            r.put("unboxmethodcall", ".shortValue()");
            r.put("sqltype", Integer.toString(StandardBasicTypes.SHORT.sqlType()));

            return r;
        }
        if (ByteTinyType.class.isAssignableFrom(tinyType)) {
            final Map<String, String> r = new HashMap<>();
            r.put("tinytype", tinyType.getName());
            r.put("boxcast", "(Byte)");
            r.put("boxfn", "Byte.valueOf");
            r.put("nestedtype", "byte");
            r.put("parse", "Byte.parseByte");
            r.put("stringify", "Byte.toString");
            r.put("unboxmethodcall", ".byteValue()");
            r.put("sqltype", Integer.toString(StandardBasicTypes.BYTE.sqlType()));
            return r;
        }
        if (StringTinyType.class.isAssignableFrom(tinyType)) {
            final Map<String, String> r = new HashMap<>();
            r.put("tinytype", tinyType.getName());
            r.put("boxcast", "(String)");
            r.put("boxfn", "");
            r.put("nestedtype", "String");
            r.put("parse", "");
            r.put("stringify", "");
            r.put("unboxmethodcall", "");
            r.put("sqltype", Integer.toString(StandardBasicTypes.TEXT.sqlType()));
            return r;
        }
        throw new IllegalStateException(String.format("unknown tiny type: %s", tinyType));
    }

    private static final ResourcePatternResolver RESOLVER = new PathMatchingResourcePatternResolver();
    private static final MetadataReaderFactory MREADER = new CachingMetadataReaderFactory(RESOLVER);
    private static final Map<String, List<Class<?>>> LOCATION_PATTERN_TO_TYPES_CACHE = new HashMap<>();
    public static final Set<String> TINY_TYPE_NAMES = Stream.of(StringTinyType.class.getName(), LongTinyType.class.getName(), IntTinyType.class.getName()).collect(Collectors.toSet());

    public static synchronized List<Class<?>> scan(String locationPattern) {
        if (LOCATION_PATTERN_TO_TYPES_CACHE.containsKey(locationPattern)) {
            return LOCATION_PATTERN_TO_TYPES_CACHE.get(locationPattern);
        }
        try {
            final List<Class<?>> result = new ArrayList<>();
            for (Resource resource : RESOLVER.getResources(locationPattern)) {
                final ClassMetadata cm = MREADER.getMetadataReader(resource).getClassMetadata();
                final String candidateName = cm.getSuperClassName();
                if (!TINY_TYPE_NAMES.contains(candidateName)) {
                    continue;
                }
                try {
                    result.add(Class.forName(cm.getClassName()));
                } catch (ClassNotFoundException ex) {
                    throw new IllegalStateException(ex);
                }
            }
            LOCATION_PATTERN_TO_TYPES_CACHE.put(locationPattern, result);
            return result;
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }

    }

}
