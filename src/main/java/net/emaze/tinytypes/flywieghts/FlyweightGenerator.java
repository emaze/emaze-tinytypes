package net.emaze.tinytypes.flywieghts;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import net.emaze.tinytypes.generation.Template;
import net.emaze.tinytypes.generation.TinyTypesReflector;

/**
 *
 * @author rferranti
 */
public class FlyweightGenerator {

    public static void createIfNeeded(Class<?> concreteTinyType) throws CannotCompileException {
        final Map<String, String> bindings = TinyTypesReflector.bindings(concreteTinyType);
        if (bindings.get("usefw").equals("false")) {
            return;
        }
        final ClassPool pool = ClassPool.getDefault();
        pool.appendClassPath(new ClassClassPath(concreteTinyType));
        final String className = String.format("net.emaze.tinytypes.gen.%sFlyWeight", concreteTinyType.getSimpleName());
        if (pool.getOrNull(className) != null) {
            return;
        }
        final CtClass cc = pool.makeClass(className);
        Template.of(
                "private static {tinytype}[] data;")
                .with(bindings)
                .asFieldFor(cc);

        Template.of(
                "public static void init(){",
                "    data = new {tinytype}[{fwlength}];",
                "    for (int i = 0; i != {fwlastarrayindex}; ++i) {",
                "        data[i] = new {tinytype}(({nestedtype})i+{fwminvalue});",
                "    }",
                "}")
                .with(bindings)
                .asMethodFor(cc);
        Template.of(
                "public static {tinytype} fw({nestedtype} v) {",
                "    if (v > {fwmaxvalue} || v < {fwminvalue}) {",
                "        return new {tinytype}(v);",
                "    }",
                "    return data[(int)(v - {fwminvalue})];",
                "}")
                .with(bindings)
                .asMethodFor(cc);

        cc.debugWriteFile("/tmp");
        try {
            Class cls = cc.toClass();
            cls.getMethod("init").invoke(cls);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
