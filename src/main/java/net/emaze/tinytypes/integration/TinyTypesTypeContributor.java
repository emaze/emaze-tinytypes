package net.emaze.tinytypes.integration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewMethod;
import net.emaze.tinytypes.IntTinyType;
import net.emaze.tinytypes.LongTinyType;
import net.emaze.tinytypes.StringTinyType;
import org.hibernate.metamodel.spi.TypeContributions;
import org.hibernate.metamodel.spi.TypeContributor;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.UserType;
import org.jboss.logging.Logger;
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
public class TinyTypesTypeContributor implements TypeContributor {

    private ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    private MetadataReaderFactory mreader = new CachingMetadataReaderFactory(this.resolver);
    private final Logger logger = Logger.getLogger(TinyTypesTypeContributor.class);

    @Override
    public void contribute(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        for (Class<?> tinyType : scan(StringTinyType.class.getName(), LongTinyType.class.getName(), IntTinyType.class.getName())) {
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
            final CtClass cc = pool.makeClass(String.format("net.emaze.tinytypes.integration.Hibernate%s", concreteTinyType.getSimpleName()));
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

    private List<Class<?>> scan(String... classNames) {
        final Set<String> names = new HashSet<>(Arrays.asList(classNames));
        try {

            List<Class<?>> result = new ArrayList<>();
            for (Resource resource : resolver.getResources("classpath*:/net/emaze/**/*.class")) {
                final ClassMetadata cm = mreader.getMetadataReader(resource).getClassMetadata();
                final String candidateName = cm.getSuperClassName();
                if (names.contains(candidateName)) {
                    try {
                        result.add(Class.forName(cm.getClassName()));
                    } catch (ClassNotFoundException ex) {
                        throw new IllegalStateException(ex);
                    }
                }
            }
            return result;

        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }

    }

}
