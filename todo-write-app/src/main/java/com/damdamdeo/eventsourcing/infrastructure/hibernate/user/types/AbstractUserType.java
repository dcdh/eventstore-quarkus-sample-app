package com.damdamdeo.eventsourcing.infrastructure.hibernate.user.types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.SerializationException;
import org.hibernate.usertype.UserType;
import org.postgresql.util.PGobject;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public abstract class AbstractUserType implements UserType {

    protected abstract Jsonb mapper();

    @Override
    public int[] sqlTypes() {
        return new int[] {
                Types.JAVA_OBJECT
        };
    }

    @Override
    public boolean equals(final Object x, final Object y) throws HibernateException {
        if (x == y) {
            return true;
        }
        if ((x == null) || (y == null)) {
            return false;
        }
        return x.equals(y);
    }

    @Override
    public int hashCode(final Object x) throws HibernateException {
        assert (x != null);
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(final ResultSet resultSet,
                              final String[] names,
                              final SharedSessionContractImplementor session,
                              final Object owner) throws HibernateException, SQLException {
        try {
            final String json = resultSet.getString(names[0]);
            return json == null ? null : mapper().fromJson(json, returnedClass());
        } catch (final JsonbException ex) {
            throw new HibernateException(ex);
        }
    }

    @Override
    public void nullSafeSet(final PreparedStatement st,
                            final Object value,
                            final int index,
                            final SharedSessionContractImplementor session) throws HibernateException, SQLException {
        try {
            final String json = value == null ? null : mapper().toJson(value);
            final PGobject pgo = new PGobject();
            pgo.setType("jsonb");
            pgo.setValue(json);
            st.setObject(index, pgo);
        } catch (final JsonbException ex) {
            throw new HibernateException(ex);
        }
    }

    @Override
    public Object deepCopy(final Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(final Object value) throws HibernateException {
        final Object copy = deepCopy(value);

        if (copy instanceof Serializable) {
            return (Serializable) copy;
        }

        throw new SerializationException(String.format("Cannot serialize '%s', %s is not Serializable.", value, value.getClass()), null);
    }

    @Override
    public Object assemble(final Serializable cached, final Object owner) throws HibernateException {
        return deepCopy(cached);
    }

    @Override
    public Object replace(final Object original, final Object target, final Object owner) throws HibernateException {
        return deepCopy(original);
    }
}
