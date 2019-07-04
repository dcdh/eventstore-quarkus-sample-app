/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.damdamdeo.eventsourcing.infrastructure.hibernate.user.types;

import com.damdamdeo.order.domain.event.PayloadAdapter;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;
import org.postgresql.util.PGobject;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

/**
 * A {@link UserType} that persists objects as JSONB.
 * <p>
 * Unlike the default JPA object mapping, {@code JSONBUserType} can also be used
 * for properties that do not implement {@link Serializable}.
 * <p>
 * Users intending to use this type for mutable non-<code>Collection</code>
 * objects should override {@link #deepCopyValue(Object)} to correctly return a
 * <u>copy</u> of the object.
 */
public class JSONBUserType extends CollectionUserType implements ParameterizedType {

    private static final Jsonb MAPPER = JsonbBuilder.create(new JsonbConfig()
            .withFormatting(true)
            .withAdapters(new PayloadAdapter()));
    private static final String JSONB_TYPE = "jsonb";
    public static final String CLASS = "CLASS";
    private Class returnedClass;

    @Override
    public Class<Object> returnedClass() {
        return Object.class;
    }

    @Override
    public Object nullSafeGet(final ResultSet resultSet,
                              final String[] names,
                              final SharedSessionContractImplementor session,
                              final Object owner) throws HibernateException, SQLException {
        try {
            final String json = resultSet.getString(names[0]);
            return json == null ? null : MAPPER.fromJson(json, returnedClass);
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
            final String json = value == null ? null : MAPPER.toJson(value);
            // otherwise PostgreSQL won't recognize the type
            final PGobject pgo = new PGobject();
            pgo.setType(JSONB_TYPE);
            pgo.setValue(json);
            st.setObject(index, pgo);
        } catch (final JsonbException ex) {
            throw new HibernateException(ex);
        }
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.JAVA_OBJECT};
    }

    @Override
    protected Object deepCopyValue(final Object value) {
        return value;
    }

    @Override
    public void setParameterValues(final Properties parameters) {
        final String clazz = (String) parameters.get(CLASS);
        try {
            returnedClass = ReflectHelper.classForName(clazz);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class: " + clazz
                    + " is not a known class type.");
        }
    }

}