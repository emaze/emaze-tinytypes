package net.emaze.tinytypes.flywieghts;

import java.util.Map;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import net.emaze.tinytypes.generation.Template;
import net.emaze.tinytypes.generation.TinyTypesReflector;
import org.jboss.logging.Logger;

/**
 *
 * @author rferranti
 */
public class FlyweightGenerator {

    private static final Logger logger = Logger.getLogger(FlyweightGenerator.class);

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
        switch (bindings.get("nestedtype")) {
            case "boolean":
                createBooleanFlyweight(bindings, cc);
                break;
            case "string":
                createStringFlyweight(bindings, cc);
                break;
            default:
                createNumericFlyweight(bindings, cc);
                break;
        }
        final Class<?> cls = cc.toClass();
        logger.info(String.format("flyweight generated for %s size:%s minvalue:%s, maxvalue:%s",
                concreteTinyType.getSimpleName(),
                bindings.get("fwlength"),
                bindings.get("fwminvalue"),
                bindings.get("fwmaxvalue")
        ));

    }

    private static void createNumericFlyweight(final Map<String, String> bindings, final CtClass cc) throws CannotCompileException {
        Template.of(
                "private static {tinytype}[] data = new {tinytype}[{fwlength}];")
                .with(bindings)
                .asFieldFor(cc);

        
        Template.of(
                "{",
                "    for (int i = 0; i != {fwlastarrayindex}; ++i) {",
                "        data[i] = new {tinytype}(({nestedtype})i+{fwminvalue});",
                "    }",
                "}")
                .with(bindings)
                .asStaticInitializerFor(cc);
        
        Template.of(
                "public static {tinytype} fw({nestedtype} v) {",
                "    if (v > {fwmaxvalue} || v < {fwminvalue}) {",
                "        return new {tinytype}(v);",
                "    }",
                "    return data[(int)(v - {fwminvalue})];",
                "}")
                .with(bindings)
                .asMethodFor(cc);
    }

    private static void createBooleanFlyweight(Map<String, String> bindings, CtClass cc) throws CannotCompileException {
        Template.of(
                "private static {tinytype}[] data = new {tinytype}[]{ new {tinytype}(false), new {tinytype}(true) };")
                .with(bindings)
                .asFieldFor(cc);

        Template.of(
                "public static {tinytype} fw({nestedtype} v) {",
                "    return data[v ? 1: 0];",
                "}")
                .with(bindings)
                .asMethodFor(cc);
    }

    private static void createStringFlyweight(Map<String, String> bindings, CtClass cc) throws CannotCompileException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
