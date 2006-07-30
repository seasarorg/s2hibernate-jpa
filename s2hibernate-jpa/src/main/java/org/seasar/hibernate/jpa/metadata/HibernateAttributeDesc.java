/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.hibernate.jpa.metadata;

import java.io.Serializable;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.TemporalType;

import org.hibernate.EntityMode;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;
import org.seasar.framework.jpa.AttributeDesc;

/**
 * 
 * @author koichik
 */
public class HibernateAttributeDesc implements AttributeDesc {

    protected final ClassMetadata metadata;

    protected final String name;

    protected final Class<?> type;

    protected final Class<?> elementType;

    protected final int sqlType;

    protected final TemporalType temporalType;

    protected final boolean id;

    protected final boolean association;

    protected final boolean collection;

    protected final boolean component;

    protected final boolean version;

    public HibernateAttributeDesc(final SessionFactoryImplementor factory,
            final ClassMetadata metadata, final String name, final boolean id,
            final boolean version) {
        this.metadata = metadata;
        this.name = name;
        this.id = id;
        this.version = version;

        Type hibernateType = null;
        if (id) {
            hibernateType = metadata.getIdentifierType();
        } else {
            hibernateType = metadata.getPropertyType(name);
        }
        type = hibernateType.getReturnedClass();
        sqlType = getSqlType(hibernateType);
        if (type == Date.class || type == Calendar.class) {
            switch (sqlType) {
            case Types.DATE:
                temporalType = TemporalType.DATE;
                break;
            case Types.TIME:
                temporalType = TemporalType.TIME;
                break;
            case Types.TIMESTAMP:
                temporalType = TemporalType.TIMESTAMP;
                break;
            default:
                temporalType = null;
            }
        } else {
            temporalType = null;
        }

        association = hibernateType.isAssociationType();
        collection = hibernateType.isCollectionType();
        component = hibernateType.isComponentType();

        if (collection) {
            final CollectionType collectionType = CollectionType.class
                    .cast(hibernateType);
            elementType = collectionType.getElementType(factory)
                    .getReturnedClass();
        } else {
            elementType = null;
        }
    }

    protected int getSqlType(final Type hibernateType) {
        try {
            final int[] sqlTypes = hibernateType.sqlTypes(null);
            if (sqlTypes != null && sqlTypes.length > 0) {
                return sqlTypes[0];
            }
        } catch (final Exception ignore) {
        }
        return Types.OTHER;
    }

    /**
     * @see org.seasar.framework.jpa.AttributeDesc#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * @see org.seasar.framework.jpa.AttributeDesc#getType()
     */
    public Class<?> getType() {
        return type;
    }

    public Class<?> getElementType() {
        return elementType;
    }

    /**
     * @see org.seasar.framework.jpa.AttributeDesc#getSqlType()
     */
    public int getSqlType() {
        return sqlType;
    }

    /**
     * @see org.seasar.framework.jpa.AttributeDesc#getTemporalType()
     */
    public TemporalType getTemporalType() {
        return temporalType;
    }

    /**
     * @see org.seasar.framework.jpa.AttributeDesc#isId()
     */
    public boolean isId() {
        return id;
    }

    /**
     * @see org.seasar.framework.jpa.AttributeDesc#isAssociation()
     */
    public boolean isAssociation() {
        return association;
    }

    /**
     * @see org.seasar.framework.jpa.AttributeDesc#isCollection()
     */
    public boolean isCollection() {
        return collection;
    }

    /**
     * @see org.seasar.framework.jpa.AttributeDesc#isCollection()
     */
    public boolean isComponent() {
        return component;
    }

    /**
     * @see org.seasar.framework.jpa.AttributeDesc#isVersion()
     */
    public boolean isVersion() {
        return version;
    }

    public Object getValue(final Object entity) {
        if (id) {
            return metadata.getIdentifier(entity, EntityMode.POJO);
        }
        return metadata.getPropertyValue(entity, name, EntityMode.POJO);
    }

    public void setValue(final Object entity, final Object value) {
        if (id) {
            metadata.setIdentifier(entity, Serializable.class.cast(value),
                    EntityMode.POJO);
        } else {
            metadata.setPropertyValue(entity, name, value, EntityMode.POJO);
        }
    }

}
