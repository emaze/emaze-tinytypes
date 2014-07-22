package net.emaze.tinytypes.integration;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.emaze.tinytypes.TinyTypesReflector;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.EnhancedUserType;

/**
 *
 * @author rferranti
 */
public abstract class HibernateTinyType implements EnhancedUserType {

    @Override
    public abstract int[] sqlTypes();

    @Override
    public abstract Class returnedClass();
    
    protected abstract String stringify(Object value);
    protected abstract Object parse(String value);
    protected abstract Serializable create(Object value);
    protected abstract Object unwrap(Object value);


    @Override
    public String objectToSQLString(Object value) {
        return stringify(value);
    }

    @Override
    public String toXMLString(Object value) {
        return stringify(value);
    }

    @Override
    public Object fromXMLString(String xmlValue) {
        return parse(xmlValue);
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
            return create(value);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, sqlTypes()[0]);
        } else {
            st.setObject(index, unwrap(value), sqlTypes()[0]);
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
        return create(cached);
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

}
