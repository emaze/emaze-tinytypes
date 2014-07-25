package net.emaze.tinytypes;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author rferranti
 */
public class TinyTypesReflector {

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

}
