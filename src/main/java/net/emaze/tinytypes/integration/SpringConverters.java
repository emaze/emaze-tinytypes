package net.emaze.tinytypes.integration;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.bytecode.SignatureAttribute;
import javassist.bytecode.SignatureAttribute.ClassSignature;
import javassist.bytecode.SignatureAttribute.ClassType;
import javassist.bytecode.SignatureAttribute.TypeParameter;
import net.emaze.tinytypes.generation.TinyTypesReflector;
import net.emaze.tinytypes.generation.Template;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;

/**
 *
 * @author rferranti
 */
public class SpringConverters {

    public static void register(FormatterRegistry registry, String locationPattern) {
        for (Class<?> tinyType : TinyTypesReflector.scan(locationPattern)) {
            registry.addConverter(createConverter(tinyType));
        }
    }

    private static Converter<?, ?> createConverter(Class<?> concreteTinyType) {

        try {
            final ClassPool pool = ClassPool.getDefault();
            pool.appendClassPath(new ClassClassPath(concreteTinyType));
            final String className = String.format("net.emaze.tinytypes.gen.StringTo%sConverter", concreteTinyType.getSimpleName());
            if (pool.getOrNull(className) != null) {
                return (Converter<?, ?>) Class.forName(className).newInstance();
            }
            final CtClass cc = pool.makeClass(className);
            cc.addInterface(pool.get(Converter.class.getName()));
            cc.setGenericSignature(new ClassSignature(new TypeParameter[0], ClassType.OBJECT, new SignatureAttribute.ClassType[]{
                new ClassType(Converter.class.getName(), new SignatureAttribute.TypeArgument[]{
                    new SignatureAttribute.TypeArgument(new ClassType(String.class.getName())),
                    new SignatureAttribute.TypeArgument(new ClassType(concreteTinyType.getName())),})
            }).encode());
            final String method = Template.of(
                    "public %s convert(String source) {",
                    "  return new %s(%s(source));",
                    "}"
            ).format(
                    concreteTinyType.getName(),
                    concreteTinyType.getName(),
                    TinyTypesReflector.parseFunction(concreteTinyType)
            );

            cc.addMethod(CtNewMethod.make(method, cc));
            final String bridgeMethod = Template.of(
                    "public Object convert(Object source) {",
                    "  return (%s)convert((String)source);",
                    "}"
            ).format(
                    concreteTinyType.getName()
            );
            cc.addMethod(CtNewMethod.make(bridgeMethod, cc));
            final Object instance = cc.toClass().newInstance();
            return (Converter<?, ?>) instance;
        } catch (ClassNotFoundException | CannotCompileException | InstantiationException | NotFoundException | IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }

}
