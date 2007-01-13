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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.TemporalType;

import org.hibernate.EntityMode;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.AbstractComponentType;
import org.hibernate.type.CollectionType;
import org.hibernate.type.CustomType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;
import org.seasar.framework.jpa.metadata.AttributeDesc;
import org.seasar.framework.jpa.util.TemporalTypeUtil;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.tiger.CollectionsUtil;
import org.seasar.framework.util.tiger.ReflectionUtil;

/**
 * 
 * @author koichik
 */
public class HibernateAttributeDesc implements AttributeDesc {

    protected static final Field PROPERTY_SELECTABLE_FIELD = ClassUtil
            .getDeclaredField(AbstractEntityPersister.class,
                    "propertySelectable");
    static {
        PROPERTY_SELECTABLE_FIELD.setAccessible(true);
    }

    protected final SessionFactoryImplementor factory;

    protected final AbstractEntityPersister metadata;

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

    protected final int[] sqlTypes;

    protected final String[] tableNames;

    protected final Map<String, String[]> columnNamesMap = CollectionsUtil
            .newHashMap();

    protected final boolean readTarget;

    protected final boolean selectable;

    public HibernateAttributeDesc(final SessionFactoryImplementor factory,
            final AbstractEntityPersister metadata, final String name,
            final boolean id, final boolean version) {
        this.factory = factory;
        this.metadata = metadata;
        this.name = name;
        this.id = id;
        this.version = version;

        if (id) {
            hibernateType = metadata.getIdentifierType();
        } else {
            hibernateType = metadata.getPropertyType(name);
        }
        type = hibernateType.getReturnedClass();
        sqlType = getSqlType(hibernateType);
        sqlTypes = hibernateType.sqlTypes(factory);

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

        if (id) {
            readTarget = true;
        } else {
            readTarget = isReadTargetType(hibernateType);
        }
        selectable = isSelectableAttribute();

        tableNames = createTableNames();
        setupColumnNameMap();
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

    protected String[] createTableNames() {
        if (id) {
            final String[] original = metadata
                    .getConstraintOrderedTableNameClosure();
            final String[] tableNames = new String[original.length];
            System.arraycopy(original, 0, tableNames, 0, original.length);
            return tableNames;
        }
        return new String[] { metadata.getPropertyTableName(name) };
    }

    protected void setupColumnNameMap() {
        if (id) {
            for (int i = 0; i < getTableNameSize(); i++) {
                final String tableName = getTableName(i);
                final String[] originalColumnNames = metadata
                        .getContraintOrderedTableKeyColumnClosure()[i];
                final String[] columnNames = new String[originalColumnNames.length];
                System.arraycopy(originalColumnNames, 0, columnNames, 0,
                        originalColumnNames.length);
                columnNamesMap.put(tableName.toLowerCase(), columnNames);
            }
        } else {
            final String tableName = metadata.getPropertyTableName(name);
            final String[] originalColumnNames = metadata
                    .getPropertyColumnNames(name);
            final String[] columnNames = new String[originalColumnNames.length];
            System.arraycopy(originalColumnNames, 0, columnNames, 0,
                    originalColumnNames.length);
            columnNamesMap.put(tableName.toLowerCase(), columnNames);
        }
    }

    protected boolean isReadTargetType(final Type type) {
        if (type.isCollectionType()) {
            return false;
        }
        if (type.isComponentType()) {
            if (AbstractComponentType.class.cast(type).isEmbedded()) {
                return false;
            }
        }
        if (type.isEntityType()) {
            if (EntityType.class.cast(type).isOneToOne()) {
                return false;
            }
        }
        return true;
    }

    protected boolean isSelectableAttribute() {
        if (id) {
            return true;
        }
        final int index = metadata.getPropertyIndex(name);
        final boolean[] selectable = ReflectionUtil.getValue(
                PROPERTY_SELECTABLE_FIELD, metadata);
        return selectable[index];
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

    public int[] getSqlTypes() {
        return sqlTypes;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public Type getHibernateType() {
        return hibernateType;
    }

    public int getTableNameSize() {
        return tableNames.length;
    }

    public String getTableName(final int index) {
        return tableNames[index];
    }

    public boolean hasTableName(final String tableName) {
        for (int i = 0; i < getTableNameSize(); i++) {
            if (getTableName(i).equalsIgnoreCase(tableName)) {
                return true;
            }
        }
        return false;
    }

    public int getColumnNameSize(final String tableName) {
        return getColumnNames(tableName).length;
    }

    public int getColumnNameSize(final int index) {
        return getColumnNames(index).length;
    }

    public String[] getColumnNames(final String tableName) {
        if (tableName == null) {
            return null;
        }
        return columnNamesMap.get(tableName.toLowerCase());
    }

    public String[] getColumnNames(final int index) {
        return getColumnNames(getTableName(index));
    }

    public boolean isReadTarget() {
        return readTarget;
    }

    public Object[] getAllValues(final Object entity) {
        final List<Object> allValues = CollectionsUtil.newArrayList();
        final Object value = getValue(entity);
        if (id) {
            gatherIdAttributeValues(allValues, hibernateType, value);
        } else {
            gatherAttributeValues(allValues, hibernateType, value);
        }
        return allValues.toArray();
    }

    protected void gatherIdAttributeValues(final List<Object> allValues,
            final Type type, final Object value) {

        if (type.isEntityType()) {
            final EntityType entityType = EntityType.class.cast(type);
            final String name = entityType.getAssociatedEntityName();
            final EntityPersister ep = factory.getEntityPersister(name);
            final Type idType = ep.getIdentifierType();
            final Serializable id = ep.getIdentifier(value, EntityMode.POJO);
            gatherIdAttributeValues(allValues, idType, id);

        } else if (type.isComponentType()) {
            final AbstractComponentType componentType = AbstractComponentType.class
                    .cast(type);
            final Object[] subvalues = componentType.getPropertyValues(value,
                    EntityMode.POJO);
            final Type[] subtypes = componentType.getSubtypes();
            for (int i = 0; i < subtypes.length; i++) {
                gatherIdAttributeValues(allValues, subtypes[i], subvalues[i]);
            }

        } else {
            allValues.add(convert(type, value));
        }
    }

    protected void gatherAttributeValues(final List<Object> allValues,
            final Type type, final Object value) {

        if (value == null) {
            allValues.add(null);
            return;
        }
        if (!isReadTargetType(type)) {
            return;
        }

        if (type.isEntityType()) {
            final EntityType entityType = EntityType.class.cast(type);

            if (entityType.isReferenceToPrimaryKey()) {
                gatherIdAttributeValues(allValues, entityType, value);
            } else {
                final String name = entityType.getAssociatedEntityName();
                final EntityPersister ep = factory.getEntityPersister(name);
                final Type[] subtypes = ep.getPropertyTypes();
                final Object[] subvalue = ep.getPropertyValues(value,
                        EntityMode.POJO);
                for (int i = 0; i < subtypes.length; i++) {
                    gatherAttributeValues(allValues, subtypes[i], subvalue[i]);
                }
            }

        } else if (type.isComponentType()) {
            final AbstractComponentType componentType = AbstractComponentType.class
                    .cast(type);
            final Object[] subvalues = componentType.getPropertyValues(value,
                    EntityMode.POJO);
            final Type[] subtypes = componentType.getSubtypes();
            for (int i = 0; i < subtypes.length; i++) {
                gatherAttributeValues(allValues, subtypes[i], subvalues[i]);
            }

        } else {
            allValues.add(convert(type, value));
        }
    }

    protected Object convert(final Type type, final Object value) {
        if (type instanceof CustomType) {
            if (type.getReturnedClass().isEnum()) {
                final Enum<?> e = Enum.class.cast(value);
                final int[] sqlTypeArray = type.sqlTypes(factory);

                switch (sqlTypeArray[0]) {
                case Types.INTEGER:
                case Types.NUMERIC:
                case Types.SMALLINT:
                case Types.TINYINT:
                case Types.BIGINT:
                case Types.DECIMAL:
                case Types.DOUBLE:
                case Types.FLOAT:
                    return e.ordinal();
                }
                return e.name();
            }
        }
        return value;
    }

}
