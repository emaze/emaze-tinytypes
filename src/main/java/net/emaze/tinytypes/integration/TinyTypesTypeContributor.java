package net.emaze.tinytypes.integration;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewMethod;
import net.emaze.tinytypes.IntTinyType;
import net.emaze.tinytypes.LongTinyType;
import net.emaze.tinytypes.StringTinyType;
import net.emaze.tinytypes.generation.TinyTypesReflector;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.metamodel.spi.TypeContributions;
import org.hibernate.metamodel.spi.TypeContributor;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.StandardBasicTypes;
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
            if (LongTinyType.class.isAssignableFrom(concreteTinyType)) {
                cc.addMethod(CtNewMethod.make(String.format("public String stringify(Object source){ if(source==null)return null; return Long.toString(((%s) source).value); }", concreteName), cc));
                cc.addMethod(CtNewMethod.make(String.format("public Object parse(String source){ return new %s(Long.parseLong(source)); }", concreteName), cc));
                cc.addMethod(CtNewMethod.make(String.format("public Object create(Object value){ return new %s(((Long)value).longValue()); }", concreteName), cc));
                cc.addMethod(CtNewMethod.make(String.format("public int[] sqlTypes(){ return new int[]{ %d}; }", StandardBasicTypes.LONG.sqlType()), cc));
                cc.addMethod(CtNewMethod.make(String.format("public java.io.Serializable unwrap(Object source){ return Long.valueOf(((%s) source).value); }", concreteName), cc));
            } else if (IntTinyType.class.isAssignableFrom(concreteTinyType)) {
                cc.addMethod(CtNewMethod.make(String.format("public String stringify(Object source){ if(source==null)return null; return Integer.toString(((%s) source).value); }", concreteName), cc));
                cc.addMethod(CtNewMethod.make(String.format("public Object parse(String source){ return new %s(Integer.parseInt(source)); }", concreteName), cc));
                cc.addMethod(CtNewMethod.make(String.format("public Object create(Object value){ return new %s(((Integer)value).intValue()); }", concreteName), cc));
                cc.addMethod(CtNewMethod.make(String.format("public int[] sqlTypes(){ return new int[]{ %d}; }", StandardBasicTypes.INTEGER.sqlType()), cc));
                cc.addMethod(CtNewMethod.make(String.format("public java.io.Serializable unwrap(Object source){ return Integer.valueOf(((%s) source).value); }", concreteName), cc));
            } else if (StringTinyType.class.isAssignableFrom(concreteTinyType)) {
                cc.addMethod(CtNewMethod.make(String.format("public String stringify(Object source){ if(source==null)return null; return ((%s) source).value; }", concreteName), cc));
                cc.addMethod(CtNewMethod.make(String.format("public Object parse(String source){ return new %s(source); }", concreteName), cc));
                cc.addMethod(CtNewMethod.make(String.format("public Object create(Object value){ return new %s((String)value); }", concreteName), cc));
                cc.addMethod(CtNewMethod.make(String.format("public int[] sqlTypes(){ return new int[]{ %d}; }", StandardBasicTypes.TEXT.sqlType()), cc));
                cc.addMethod(CtNewMethod.make(String.format("public java.io.Serializable unwrap(Object source){ return ((%s) source).value; }", concreteName), cc));
            }
            cc.addMethod(CtNewMethod.make(String.format("public Class returnedClass(){ return %s.class; }", concreteName), cc));
            return (UserType) cc.toClass().newInstance();
        } catch (Exception ex) {
            throw new IllegalStateException(String.format("while generating usertype for %s: ", concreteTinyType), ex);
        }
    }

}
