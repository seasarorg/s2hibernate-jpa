/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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

import java.sql.Types;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.TemporalType;

import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;
import org.seasar.framework.jpa.metadata.AttributeDesc;
import org.seasar.framework.jpa.util.TemporalTypeUtil;

/**
 * 
 * @author nakamura
 */
public abstract class AbstractHibernateAttributeDesc implements AttributeDesc {

    protected final SessionFactoryImplementor factory;

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

    protected final Type hibernateType;

    public AbstractHibernateAttributeDesc(
            final SessionFactoryImplementor factory, final Type hibernateType,
            final String name, final boolean id, final boolean version) {
        this.factory = factory;
        this.hibernateType = hibernateType;
        this.name = name;
        this.id = id;
        this.version = version;

        type = hibernateType.getReturnedClass();
        sqlType = getSqlType(hibernateType);

        if (type == Date.class || type == Calendar.class) {
            temporalType = TemporalTypeUtil.fromSqlTypeToTemporalType(sqlType);
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
            if (sqlTypes != null && sqlTypes.length == 1) {
                return sqlTypes[0];
            }
        } catch (final Exception ignore) {
        }
        return Types.OTHER;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public Class<?> getElementType() {
        return elementType;
    }

    public int getSqlType() {
        return sqlType;
    }

    public TemporalType getTemporalType() {
        return temporalType;
    }

    public boolean isId() {
        return id;
    }

    public boolean isAssociation() {
        return association;
    }

    public boolean isCollection() {
        return collection;
    }

    public boolean isComponent() {
        return component;
    }

    public boolean isVersion() {
        return version;
    }

}
