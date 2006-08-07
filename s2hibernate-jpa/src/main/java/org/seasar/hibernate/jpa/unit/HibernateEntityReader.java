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
package org.seasar.hibernate.jpa.unit;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Types;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.EntityMode;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.type.AbstractComponentType;
import org.hibernate.type.CustomType;
import org.hibernate.type.EmbeddedComponentType;
import org.hibernate.type.EntityType;
import org.hibernate.type.OneToOneType;
import org.hibernate.type.Type;
import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.extension.dataset.impl.DataSetImpl;
import org.seasar.extension.dataset.states.RowStates;
import org.seasar.extension.dataset.types.ColumnTypes;
import org.seasar.framework.jpa.unit.EntityReader;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.FieldUtil;
import org.seasar.framework.util.tiger.CollectionsUtil;

/**
 * 
 * @author taedium
 */
public class HibernateEntityReader implements EntityReader {

    protected final EntityManager em;

    protected final AbstractEntityPersister persister;

    protected final DataSet dataSet = new DataSetImpl();

    protected HibernateEntityReader(final EntityManager em,
            final AbstractEntityPersister persister) {

        this.em = em;
        this.persister = persister;
    }

    public HibernateEntityReader(final Object entity, final EntityManager em,
            final AbstractEntityPersister persister) {

        this(em, persister);
        setupColumns();
        setupRow(entity);
    }

    protected void setupColumns() {
        final String[] tableNames = persister
                .getConstraintOrderedTableNameClosure();
        for (int i = 0; i < tableNames.length; i++) {
            dataSet.addTable(tableNames[i]);
            setupIdColumns(i);
        }
        setupPropertyColumns();
        if (persister instanceof SingleTableEntityPersister) {
            setupDiscriminatorColumn();
        }
    }

    protected void setupIdColumns(final int tableIndex) {
        final DataTable table = dataSet.getTable(tableIndex);
        final Type idType = persister.getIdentifierType();
        final String[] columnNames = persister
                .getContraintOrderedTableKeyColumnClosure()[tableIndex];
        final int[] sqlTypes = idType.sqlTypes(persister.getFactory());

        for (int i = 0; i < sqlTypes.length; i++) {
            final String columnName = columnNames[i];

            assert !table.hasColumn(columnName) : idType.getName() + " : "
                    + columnName;

            table.addColumn(columnName, ColumnTypes.getColumnType(sqlTypes[i]));
        }
    }

    protected void setupPropertyColumns() {
        final String[] propNames = persister.getPropertyNames();
        for (int i = 0; i < propNames.length; i++) {
            final String propName = propNames[i];
            if (!isPropertySelectable(propName)) {
                continue;
            }

            final Type propType = persister.getPropertyType(propName);
            if (!isPropertyTypeReadTarget(propType)) {
                continue;
            }

            final String tableName = persister.getPropertyTableName(propName);
            final DataTable table = dataSet.getTable(tableName);
            final String[] columnNames = persister
                    .getPropertyColumnNames(propName);
            final int[] sqlTypes = propType.sqlTypes(persister.getFactory());

            for (int j = 0; j < sqlTypes.length; j++) {
                final String columnName = columnNames[j];

                assert !table.hasColumn(columnName) : propType.getName()
                        + " : " + columnName;

                table.addColumn(columnName, ColumnTypes
                        .getColumnType(sqlTypes[j]));
            }
        }
    }

    protected void setupDiscriminatorColumn() {
        final String tableName = persister.getTableName();
        final DataTable table = dataSet.getTable(tableName);
        final String columnName = persister.getDiscriminatorColumnName();
        if (columnName != null) {
            final Type dType = persister.getDiscriminatorType();
            final int[] sqlTypes = dType.sqlTypes(persister.getFactory());

            assert sqlTypes.length == 1 : sqlTypes.length;

            table.addColumn(columnName, ColumnTypes.getColumnType(sqlTypes[0]));
        }
    }

    protected void setupRow(final Object entity) {
        for (int i = 0; i < dataSet.getTableSize(); i++) {
            final DataTable table = dataSet.getTable(i);
            final DataRow row = table.addRow();
            setupIdValues(entity, row, i);
            setupPropertyValues(entity, row, table.getTableName());
            if (persister instanceof SingleTableEntityPersister) {
                setupDiscriminatorValue(row, table.getTableName());
            }

            if (row.getValue(0) == null) {
                row.setState(RowStates.REMOVED);
                table.removeRows();
            } else {
                row.setState(RowStates.UNCHANGED);
            }
        }
    }

    protected void setupIdValues(final Object entity, final DataRow row,
            final int tableIndex) {

        final Type idType = persister.getIdentifierType();
        final Serializable id = persister
                .getIdentifier(entity, getEntityMode());
        final List<Object> values = CollectionsUtil.newArrayList();
        gatherIdValues(values, idType, id);
        final String[] columnNames = persister
                .getContraintOrderedTableKeyColumnClosure()[tableIndex];

        assert values.size() == columnNames.length : values.size() + " : "
                + columnNames.length;

        for (int i = 0; i < columnNames.length; i++) {
            row.setValue(columnNames[i], values.get(i));
        }
    }

    protected void setupPropertyValues(final Object entity, final DataRow row,
            final String tableName) {

        final String[] propNames = persister.getPropertyNames();
        for (int i = 0; i < propNames.length; i++) {
            final String propName = propNames[i];
            if (!isPropertySelectable(propName)) {
                continue;
            }

            final String propTableName = persister
                    .getPropertyTableName(propName);
            if (!tableName.equalsIgnoreCase(propTableName)) {
                continue;
            }

            final Type propType = persister.getPropertyType(propName);
            if (!isPropertyTypeReadTarget(propType)) {
                continue;
            }

            final Object propValue = persister.getPropertyValue(entity,
                    propName, getEntityMode());
            final List<Object> values = CollectionsUtil.newArrayList();
            gatherPropertyValues(values, propType, propValue);
            final String[] columnNames = persister
                    .getPropertyColumnNames(propName);

            assert values.size() == columnNames.length : propName + " : "
                    + values.size() + ", " + columnNames.length;

            for (int j = 0; j < columnNames.length; j++) {
                row.setValue(columnNames[j], values.get(j));
            }
        }
    }

    protected void setupDiscriminatorValue(final DataRow row,
            final String tableName) {

        if (tableName.equalsIgnoreCase(persister.getTableName())) {
            final String columnName = persister.getDiscriminatorColumnName();
            if (columnName != null) {
                final String sqlString = persister.getDiscriminatorSQLValue();
                row.setValue(columnName, sqlStringToString(sqlString));
            }
        }
    }

    protected void gatherIdValues(final List<Object> idValues, final Type type,
            final Object value) {

        if (type.isEntityType()) {
            final EntityType entityType = EntityType.class.cast(type);
            final String entityName = entityType.getAssociatedEntityName();
            final EntityPersister ep = getSession().getEntityPersister(
                    entityName, value);
            final Type idType = ep.getIdentifierType();
            final Serializable id = ep.getIdentifier(value, getEntityMode());
            gatherIdValues(idValues, idType, id);

        } else if (type.isComponentType()) {
            final AbstractComponentType componentType = AbstractComponentType.class
                    .cast(type);
            final Type[] subtypes = componentType.getSubtypes();
            for (int i = 0; i < subtypes.length; i++) {
                final Object subvalue = componentType.getPropertyValue(value,
                        i, getSession());
                gatherIdValues(idValues, subtypes[i], subvalue);
            }

        } else {
            int[] sqlTypes = type.sqlTypes(persister.getFactory());

            assert sqlTypes.length == 1 : sqlTypes.length;

            idValues.add(convert(type, value));
        }
    }

    protected void gatherPropertyValues(final List<Object> propValues,
            final Type type, final Object value) {

        if (value == null) {
            propValues.add(null);
            return;
        }

        if (!isPropertyTypeReadTarget(type)) {
            return;
        }

        if (type.isEntityType()) {
            final EntityType entityType = EntityType.class.cast(type);

            if (entityType.isReferenceToPrimaryKey()) {
                gatherIdValues(propValues, entityType, value);
            } else {
                final String entityName = entityType.getAssociatedEntityName();
                final EntityPersister ep = getSession().getEntityPersister(
                        entityName, propValues);
                final Type[] subtypes = ep.getPropertyTypes();
                for (int i = 0; i < subtypes.length; i++) {
                    final Object subvalue = ep.getPropertyValue(value, i,
                            getEntityMode());
                    gatherPropertyValues(propValues, subtypes[i], subvalue);
                }
            }

        } else if (type.isComponentType()) {
            final AbstractComponentType componentType = AbstractComponentType.class
                    .cast(type);
            final Type[] subtypes = componentType.getSubtypes();
            for (int i = 0; i < subtypes.length; i++) {
                final Object subvalue = componentType.getPropertyValue(value,
                        i, getSession());
                gatherPropertyValues(propValues, subtypes[i], subvalue);
            }

        } else {
            int[] sqlTypes = type.sqlTypes(persister.getFactory());

            assert sqlTypes.length == 1 : sqlTypes.length;

            propValues.add(convert(type, value));
        }
    }

    protected Object convert(final Type type, final Object value) {
        if (type instanceof CustomType) {
            if (Enum.class.isAssignableFrom(type.getReturnedClass())) {
                final Enum e = Enum.class.cast(value);
                final int[] sqlTypes = type.sqlTypes(persister.getFactory());

                assert sqlTypes.length == 1;

                if (isOrdinal(sqlTypes[0])) {
                    return e.ordinal();
                }
                return e.toString();
            }
        }
        return value;
    }

    protected boolean isOrdinal(int sqlType) {
        switch (sqlType) {
        case Types.INTEGER:
        case Types.NUMERIC:
        case Types.SMALLINT:
        case Types.TINYINT:
        case Types.BIGINT:
        case Types.DECIMAL:
        case Types.DOUBLE:
        case Types.FLOAT:
            return true;
        }
        return false;
    }

    protected boolean isPropertySelectable(final String propertyName) {
        final int popertyIndex = persister.getPropertyIndex(propertyName);
        // final Field field = ReflectionUtil.getDeclaredField(
        // AbstractEntityPersister.class, "propertySelectable");
        // field.setAccessible(true);
        // final boolean[] propertySelectable = ReflectionUtil.getValue(field,
        // persister);
        final Field field = ClassUtil.getDeclaredField(
                AbstractEntityPersister.class, "propertySelectable");
        field.setAccessible(true);
        final boolean[] propertySelectable = (boolean[]) FieldUtil.get(field,
                persister);
        return propertySelectable[popertyIndex];
    }

    protected boolean isPropertyTypeReadTarget(final Type propType) {
        if (propType.isCollectionType()) {
            return false;
        }
        if (propType instanceof EmbeddedComponentType) {
            return false;
        }
        if (propType instanceof OneToOneType) {
            OneToOneType oneToOneType = OneToOneType.class.cast(propType);
            return oneToOneType.isReferenceToPrimaryKey();
        }
        return true;
    }

    protected String sqlStringToString(String sqlString) {
        if (sqlString.length() > 1) {
            return sqlString.substring(1, sqlString.length() - 1);
        }
        return sqlString;
    }

    protected SessionImplementor getSession() {
        return SessionImplementor.class.cast(em.getDelegate());
    }

    protected EntityMode getEntityMode() {
        if (getSession().isOpen()) {
            return getSession().getEntityMode();
        }
        final SessionFactoryImplementor sf = persister.getFactory();
        return sf.getSettings().getDefaultEntityMode();
    }

    public DataSet read() {
        return dataSet;
    }
}
