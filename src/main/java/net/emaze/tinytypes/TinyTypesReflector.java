package net.emaze.tinytypes;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.hibernate.type.StandardBasicTypes;

/**
 *
 * @author rferranti
 */
public class TinyTypesReflector {

    public static int sqlType(Class<?> tinyType) {
        if (LongTinyType.class.isAssignableFrom(tinyType)) {
            return StandardBasicTypes.LONG.sqlType();
        }
        if (IntTinyType.class.isAssignableFrom(tinyType)) {
            return StandardBasicTypes.INTEGER.sqlType();
        }
        if (StringTinyType.class.isAssignableFrom(tinyType)) {
            return StandardBasicTypes.TEXT.sqlType();
        }
        throw new IllegalStateException(String.format("unknown tiny type: %s", tinyType));
    }

    public static Constructor ctor(Class<?> tinyType) {
        try {
            if (LongTinyType.class.isAssignableFrom(tinyType)) {
                return tinyType.getConstructor(long.class);
            }
            if (IntTinyType.class.isAssignableFrom(tinyType)) {
                return tinyType.getConstructor(int.class);
            }
            if (StringTinyType.class.isAssignableFrom(tinyType)) {
                return tinyType.getConstructor(String.class);
            }
            throw new IllegalStateException(String.format("unknown tiny type: %s", tinyType));
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static <T> T create(Class<T> tinyType, Constructor ctor, Object value) {
        try {
            return (T) ctor.newInstance(value);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static Serializable value(Object st) {
        if (st == null) {
            return null;
        }
        if (st instanceof LongTinyType) {
            return ((LongTinyType) st).value;
        }
        if (st instanceof IntTinyType) {
            return ((IntTinyType) st).value;
        }
        if (st instanceof StringTinyType) {
            return ((StringTinyType) st).value;
        }
        throw new IllegalStateException(String.format("unknown tiny type: %s", st.getClass()));
    }

    public static String toString(Class<?> tinyType, Object source) {
        if (source == null) {
            return null;
        }
        if (source instanceof LongTinyType) {
            return Long.toString(((LongTinyType) source).value);
        }
        if (source instanceof IntTinyType) {
            return Integer.toString(((IntTinyType) source).value);
        }
        if (source instanceof StringTinyType) {
            return ((StringTinyType) source).value;
        }
        throw new IllegalStateException(String.format("unknown tiny type: %s", source.getClass()));
    }

    public static <T> T fromString(Class<T> tinyType, Constructor ctor, String source) {
        if (source == null) {
            return null;
        }
        if (LongTinyType.class.isAssignableFrom(tinyType)) {
            return create(tinyType, ctor, Long.parseLong(source));
        }
        if (IntTinyType.class.isAssignableFrom(tinyType)) {
            return create(tinyType, ctor, Integer.parseInt(source));
        }
        if (StringTinyType.class.isAssignableFrom(tinyType)) {
            return create(tinyType, ctor, source);
        }
        throw new IllegalStateException(String.format("unknown tiny type: %s", source.getClass()));
    }
}
