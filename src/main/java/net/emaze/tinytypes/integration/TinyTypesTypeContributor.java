package net.emaze.tinytypes.integration;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewMethod;
import net.emaze.tinytypes.generation.Template;
import net.emaze.tinytypes.generation.TinyTypesReflector;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.metamodel.spi.TypeContributions;
import org.hibernate.metamodel.spi.TypeContributor;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.usertype.UserType;
import org.jboss.logging.Logger;

/**
 *
 * @author rferranti
 */
public class TinyTypesTypeContributor implements TypeContributor {

    private final Logger logger = Logger.getLogger(TinyTypesTypeContributor.class);
    private static final String LOCATION_PATTERN_KEY = "hibernate.tinytypes.location.pattern";

    @Override
    public void contribute(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        final ConfigurationService configuration = serviceRegistry.getService(ConfigurationService.class);
        final String cp = configuration.getSetting(LOCATION_PATTERN_KEY, String.class, null);
        if(cp == null){
            throw new IllegalStateException(String.format("%s must be set (i.e: %s)", LOCATION_PATTERN_KEY, "classpath*:/net/emaze/**/*.class"));
        }
        for (Class<?> tinyType : TinyTypesReflector.scan(cp)) {
            logger.info(String.format("found %s", tinyType.getSimpleName()));
            final UserType type = createHibernateType(tinyType);
            typeContributions.contributeType(type, new String[]{tinyType.getName()});
            logger.info(String.format("created and registered %s", type));
        }
    }

    private static UserType createHibernateType(Class<?> concreteTinyType) {
        try {
            final ClassPool pool = ClassPool.getDefault();
            pool.appendClassPath(new ClassClassPath(concreteTinyType));
            final String className = String.format("net.emaze.tinytypes.gen.Hibernate%s", concreteTinyType.getSimpleName());
            if (pool.getOrNull(className) != null) {
                return (UserType) Class.forName(className).newInstance();
            }
            final CtClass cc = pool.makeClass(className);
            cc.setSuperclass(pool.get(HibernateTinyType.class.getName()));
            final String concreteName = concreteTinyType.getName();

            final String stringify = Template.of(
                    "public String stringify(Object source){",
                    "  if(source==null) {",
                    "    return null;",
                    "  }",
                    "  return %s(((%s) source).value);",
                    "}"
            ).format(TinyTypesReflector.stringifyFunction(concreteTinyType), concreteName);
            cc.addMethod(CtNewMethod.make(stringify, cc));

            final String parse = Template.of(
                    "public Object parse(String source){",
                    "  return new %s(%s(source));",
                    "}"
            ).format(concreteName, TinyTypesReflector.parseFunction(concreteTinyType));
            cc.addMethod(CtNewMethod.make(parse, cc));

            final String create = Template.of(
                    "public Object create(Object value) {",
                    "  return new %s((%svalue)%s); ",
                    "}"
            ).format(concreteName, TinyTypesReflector.boxCast(concreteTinyType), TinyTypesReflector.unboxFunctionCall(concreteTinyType));
            cc.addMethod(CtNewMethod.make(create, cc));

            final String sqlTypes = Template.of(
                    "public int[] sqlTypes(){",
                    "  return new int[]{ %d }; ",
                    "}"
            ).format(TinyTypesReflector.sqlType(concreteTinyType));

            cc.addMethod(CtNewMethod.make(sqlTypes, cc));

            final String unwrap = Template.of(
                    "public java.io.Serializable unwrap(Object source){",
                    "  return %s(((%s) source).value); ",
                    "}"
            ).format(TinyTypesReflector.boxFunction(concreteTinyType), concreteName);

            cc.addMethod(CtNewMethod.make(unwrap, cc));

            cc.addMethod(CtNewMethod.make(String.format("public Class returnedClass(){ return %s.class; }", concreteName), cc));
            return (UserType) cc.toClass().newInstance();
        } catch (Exception ex) {
            throw new IllegalStateException(String.format("while generating usertype for %s: ", concreteTinyType), ex);
        }
    }

}
