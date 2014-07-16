package net.emaze.tinytypes;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.DynamicParameterizedType;
import org.hibernate.usertype.EnhancedUserType;

/**
 *
 * @author rferranti
 */
public class HibernateTinyType implements EnhancedUserType, DynamicParameterizedType {

    public static final String TYPE =  "net.emaze.tinytypes.HibernateTinyType";
    private Class<?> tinyType;
    private Constructor ctor;
    private int[] sqlTypes;

    @Override
    public void setParameterValues(Properties properties) {
        try {
            final String returnedClass = (String) properties.get(RETURNED_CLASS);
            this.tinyType = Class.forName(returnedClass);
            this.ctor = TinyTypesReflector.ctor(tinyType);
            this.sqlTypes = new int[]{TinyTypesReflector.sqlType(tinyType)};
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public String objectToSQLString(Object value) {
        return TinyTypesReflector.toString(tinyType, value);
    }

    @Override
    public String toXMLString(Object value) {
        return TinyTypesReflector.toString(tinyType, value);
    }

    @Override
    public Object fromXMLString(String xmlValue) {
        return TinyTypesReflector.fromString(tinyType, ctor, xmlValue);
    }

    @Override
    public int[] sqlTypes() {
        return sqlTypes;
    }

    @Override
    public Class returnedClass() {
        return tinyType;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return x == null ? y == null : x.equals(y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x == null ? 0 : x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        final Object value = rs.getObject(names[0]);
        if (rs.wasNull()) {
            return null;
        } else {
            return TinyTypesReflector.create(tinyType, ctor, value);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, sqlTypes[0]);
        } else {
            st.setObject(index, TinyTypesReflector.value(value), sqlTypes[0]);
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return TinyTypesReflector.value(value);
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return TinyTypesReflector.create(tinyType, ctor, cached);
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

}
