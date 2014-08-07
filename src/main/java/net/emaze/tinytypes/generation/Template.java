package net.emaze.tinytypes.generation;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

/**
 *
 * @author rferranti
 */
public class Template {

    private final Stream<String> vs;
    private final Map<String, String> bindings;

    public Template(Stream<String> vs) {
        this.vs = vs;
        this.bindings = new HashMap<>();
    }

    public static Template of(String... vs) {
        return new Template(Stream.of(vs));
    }

    public Template with(Map<String, String> bindings) {
        this.bindings.putAll(bindings);
        return this;
    }

    public String render() {
        String merged = vs.collect(Collectors.joining());
        for (Map.Entry<String, String> entry : bindings.entrySet()) {
            merged = merged.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return merged;
    }

    public CtMethod asMethodFor(CtClass cc) throws CannotCompileException {
        final CtMethod method = CtNewMethod.make(render(), cc);
        cc.addMethod(method);
        return method;
    }

}
