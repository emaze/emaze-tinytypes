package net.emaze.tinytypes.generation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    public static String boxCast(Class<?> tinyType) {
        if (LongTinyType.class.isAssignableFrom(tinyType)) {
            return "(Long)";
        }
        if (IntTinyType.class.isAssignableFrom(tinyType)) {
            return "(Integer)";
        }
        if (ShortTinyType.class.isAssignableFrom(tinyType)) {
            return "(Short)";
        }
        if (ByteTinyType.class.isAssignableFrom(tinyType)) {
            return "(Byte)";
        }
        if (StringTinyType.class.isAssignableFrom(tinyType)) {
            return "(String)";
        }
        throw new IllegalStateException(String.format("unknown tiny type: %s", tinyType));
    }

    public static String boxFunction(Class<?> tinyType) {
        if (LongTinyType.class.isAssignableFrom(tinyType)) {
            return "Long.valueOf";
        }
        if (IntTinyType.class.isAssignableFrom(tinyType)) {
            return "Integer.valueOf";
        }
        if (ShortTinyType.class.isAssignableFrom(tinyType)) {
            return "Short.valueOf";
        }
        if (ByteTinyType.class.isAssignableFrom(tinyType)) {
            return "Byte.valueOf";
        }
        if (StringTinyType.class.isAssignableFrom(tinyType)) {
            return "";
        }
        throw new IllegalStateException(String.format("unknown tiny type: %s", tinyType));
    }

    public static Class<?> nestedType(Class<?> tinyType) {
        if (LongTinyType.class.isAssignableFrom(tinyType)) {
            return long.class;
        }
        if (IntTinyType.class.isAssignableFrom(tinyType)) {
            return int.class;
        }
        if (ShortTinyType.class.isAssignableFrom(tinyType)) {
            return short.class;
        }
        if (ByteTinyType.class.isAssignableFrom(tinyType)) {
            return byte.class;
        }
        if (StringTinyType.class.isAssignableFrom(tinyType)) {
            return String.class;
        }
        throw new IllegalStateException(String.format("unknown tiny type: %s", tinyType));
    }

    public static String parseFunction(Class<?> tinyType) {
        if (LongTinyType.class.isAssignableFrom(tinyType)) {
            return "Long.parseLong";
        }
        if (IntTinyType.class.isAssignableFrom(tinyType)) {
            return "Integer.parseInt";
        }
        if (ShortTinyType.class.isAssignableFrom(tinyType)) {
            return "Short.parseShort";
        }
        if (ByteTinyType.class.isAssignableFrom(tinyType)) {
            return "Byte.parseByte";
        }
        if (StringTinyType.class.isAssignableFrom(tinyType)) {
            return "";
        }
        throw new IllegalStateException(String.format("unknown tiny type: %s", tinyType));
    }

    public static String stringifyFunction(Class<?> tinyType) {
        if (LongTinyType.class.isAssignableFrom(tinyType)) {
            return "Long.toString";
        }
        if (IntTinyType.class.isAssignableFrom(tinyType)) {
            return "Integer.toString";
        }
        if (ShortTinyType.class.isAssignableFrom(tinyType)) {
            return "Short.toString";
        }
        if (ByteTinyType.class.isAssignableFrom(tinyType)) {
            return "Byte.toString";
        }
        if (StringTinyType.class.isAssignableFrom(tinyType)) {
            return "";
        }
        throw new IllegalStateException(String.format("unknown tiny type: %s", tinyType));
    }

    public static String unboxFunctionCall(Class<?> tinyType) {
        if (LongTinyType.class.isAssignableFrom(tinyType)) {
            return ".longValue()";
        }
        if (IntTinyType.class.isAssignableFrom(tinyType)) {
            return ".intValue()";
        }
        if (ShortTinyType.class.isAssignableFrom(tinyType)) {
            return ".shortValue()";
        }
        if (ByteTinyType.class.isAssignableFrom(tinyType)) {
            return ".byteValue()";
        }
        if (StringTinyType.class.isAssignableFrom(tinyType)) {
            return "";
        }
        throw new IllegalStateException(String.format("unknown tiny type: %s", tinyType));
    }

    public static int sqlType(Class<?> tinyType) {
        if (LongTinyType.class.isAssignableFrom(tinyType)) {
            return StandardBasicTypes.LONG.sqlType();
        }
        if (IntTinyType.class.isAssignableFrom(tinyType)) {
            return StandardBasicTypes.INTEGER.sqlType();
        }
        if (ShortTinyType.class.isAssignableFrom(tinyType)) {
            return StandardBasicTypes.SHORT.sqlType();
        }
        if (ByteTinyType.class.isAssignableFrom(tinyType)) {
            return StandardBasicTypes.BYTE.sqlType();
        }
        if (StringTinyType.class.isAssignableFrom(tinyType)) {
            return StandardBasicTypes.TEXT.sqlType();
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
