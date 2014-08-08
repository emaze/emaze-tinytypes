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
        if (cp == null) {
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

            Template.of(
                    "public String stringify(Object source){",
                    "  if(source==null) {",
                    "    return null;",
                    "  }",
                    "  return {stringify}((({tinytype}) source).value);",
                    "}")
                    .with(TinyTypesReflector.bindings(concreteTinyType))
                    .asMethodFor(cc);

            Template.of(
                    "public Object parse(String source){",
                    "  return {factory}({parse}(source));",
                    "}")
                    .with(TinyTypesReflector.bindings(concreteTinyType))
                    .asMethodFor(cc);

            Template.of(
                    "public Object create(Object value) {",
                    "  return {factory}(({boxcast}value){unboxmethodcall}); ",
                    "}")
                    .with(TinyTypesReflector.bindings(concreteTinyType))
                    .asMethodFor(cc);

            Template.of(
                    "public int[] sqlTypes(){",
                    "  return new int[]{ {sqltype} }; ",
                    "}")
                    .with(TinyTypesReflector.bindings(concreteTinyType))
                    .asMethodFor(cc);

            Template.of(
                    "public java.io.Serializable unwrap(Object source){",
                    "  return {boxfn}((({tinytype}) source).value); ",
                    "}")
                    .with(TinyTypesReflector.bindings(concreteTinyType))
                    .asMethodFor(cc);
            Template.of(
                    "public Class returnedClass(){ ",
                    "  return {tinytype}.class; ",
                    "}")
                    .with(TinyTypesReflector.bindings(concreteTinyType))
                    .asMethodFor(cc);

            return (UserType) cc.toClass().newInstance();
        } catch (Exception ex) {
            throw new IllegalStateException(String.format("while generating usertype for %s: ", concreteTinyType), ex);
        }
    }

}
