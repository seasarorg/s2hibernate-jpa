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
import org.seasar.framework.util.tiger.CollectionsUtil;
import org.seasar.framework.util.tiger.ReflectionUtil;

/**
 * 
 * @author taedium
 */
public class HibernateEntityReader implements EntityReader {

    protected static final Field PROPERTY_SELECTABLE_FIELD = ClassUtil
            .getDeclaredField(AbstractEntityPersister.class,
                    "propertySelectable");
    static {
        PROPERTY_SELECTABLE_FIELD.setAccessible(true);
    }

    protected AbstractEntityPersister persister;

    protected final EntityManager em;

    protected final DataSet dataSet = new DataSetImpl();

    protected HibernateEntityReader(final EntityManager em) {
        this.em = em;
    }

    public HibernateEntityReader(final EntityManager em, final Object entity,
            final AbstractEntityPersister persiter) {

        this(em);
        this.persister = persiter;
        setupColumns();
        setupRow(entity);
    }

    protected void setupColumns() {
        final String[] tableNames = getPersister()
                .getConstraintOrderedTableNameClosure();
        for (int i = 0; i < tableNames.length; i++) {
            if (dataSet.hasTable(tableNames[i])) {
                continue;
            }
            dataSet.addTable(tableNames[i]);
            setupIdColumns(i);
        }
        setupPropertyColumns();
        setupDiscriminatorColumn();
    }

    protected void setupIdColumns(final int tableIndex) {
        final Type idType = getPersister().getIdentifierType();
        final String tableName = getPersister()
                .getConstraintOrderedTableNameClosure()[tableIndex];
        final DataTable table = dataSet.getTable(tableName);
        final String[] columnNames = getPersister()
                .getContraintOrderedTableKeyColumnClosure()[tableIndex];
        final int[] sqlTypes = idType.sqlTypes(getPersister().getFactory());

        for (int i = 0; i < sqlTypes.length; i++) {
            final String columnName = columnNames[i];
            if (columnName == null && table.hasColumn(columnName)) {
                continue;
            }
            table.addColumn(columnName, ColumnTypes.getColumnType(sqlTypes[i]));
        }
    }

    protected void setupPropertyColumns() {
        final String[] propNames = getPersister().getPropertyNames();
        for (int i = 0; i < propNames.length; i++) {
            final String propName = propNames[i];
            if (!isPropertySelectable(propName)) {
                continue;
            }

            final Type propType = getPersister().getPropertyType(propName);
            if (!isPropertyTypeReadTarget(propType)) {
                continue;
            }

            final String tableName = getPersister().getPropertyTableName(
                    propName);
            final DataTable table = dataSet.getTable(tableName);
            final String[] columnNames = getPersister().getPropertyColumnNames(
                    propName);
            final int[] sqlTypes = propType.sqlTypes(getPersister()
                    .getFactory());

            for (int j = 0; j < sqlTypes.length; j++) {
                final String columnName = columnNames[j];
                if (columnName == null || table.hasColumn(columnName)) {
                    continue;
                }
                table.addColumn(columnName, ColumnTypes
                        .getColumnType(sqlTypes[j]));
            }
        }
    }

    protected void setupDiscriminatorColumn() {
        if (!(getPersister() instanceof SingleTableEntityPersister)) {
            return;
        }
        final String tableName = getPersister().getTableName();
        final DataTable table = dataSet.getTable(tableName);
        final String columnName = getPersister().getDiscriminatorColumnName();
        if (columnName != null) {
            final Type dType = getPersister().getDiscriminatorType();
            final int[] sqlTypes = dType.sqlTypes(getPersister().getFactory());
            if (table.hasColumn(columnName)) {
                return;
            }
            table.addColumn(columnName, ColumnTypes.getColumnType(sqlTypes[0]));
        }
    }

    protected void setupRow(final Object entity) {
        final String tableNames[] = getPersister()
                .getConstraintOrderedTableNameClosure();
        for (int i = 0; i < tableNames.length; i++) {
            final DataTable table = dataSet.getTable(tableNames[i]);
            final DataRow row = table.addRow();
            setupIdValues(entity, row, i);
            setupPropertyValues(entity, row, tableNames[i]);
            setupDiscriminatorValue(row, tableNames[i]);

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

        final Type idType = getPersister().getIdentifierType();
        final Serializable id = getPersister().getIdentifier(entity,
                getEntityMode());
        final List<Object> values = CollectionsUtil.newArrayList();
        gatherIdValues(values, idType, id);
        final String[] columnNames = getPersister()
                .getContraintOrderedTableKeyColumnClosure()[tableIndex];

        assert values.size() == columnNames.length : values.size() + " : "
                + columnNames.length;

        for (int i = 0; i < columnNames.length; i++) {
            if (columnNames[i] == null) {
                continue;
            }
            row.setValue(columnNames[i], values.get(i));
        }
    }

    protected void setupPropertyValues(final Object entity, final DataRow row,
            final String tableName) {

        final String[] propNames = getPersister().getPropertyNames();
        for (int i = 0; i < propNames.length; i++) {
            final String propName = propNames[i];
            if (!isPropertySelectable(propName)) {
                continue;
            }

            final String propTableName = getPersister().getPropertyTableName(
                    propName);
            if (!tableName.equalsIgnoreCase(propTableName)) {
                continue;
            }

            final Type propType = getPersister().getPropertyType(propName);
            if (!isPropertyTypeReadTarget(propType)) {
                continue;
            }

            final Object propValue = getPersister().getPropertyValue(entity,
                    propName, getEntityMode());
            final List<Object> values = CollectionsUtil.newArrayList();
            gatherPropertyValues(values, propType, propValue);
            final String[] columnNames = getPersister().getPropertyColumnNames(
                    propName);

            assert values.size() == columnNames.length : propName + " : "
                    + values.size() + ", " + columnNames.length;

            for (int j = 0; j < columnNames.length; j++) {
                if (columnNames[j] == null) {
                    continue;
                }
                row.setValue(columnNames[j], values.get(j));
            }
        }
    }

    protected void setupDiscriminatorValue(final DataRow row,
            final String tableName) {

        if (!(getPersister() instanceof SingleTableEntityPersister)) {
            return;
        }
        if (tableName.equalsIgnoreCase(getPersister().getTableName())) {
            final String columnName = getPersister()
                    .getDiscriminatorColumnName();
            if (columnName != null) {
                String dValue = getPersister().getDiscriminatorSQLValue();
                if (dValue.length() > 1 && dValue.startsWith("'")
                        && dValue.endsWith("'")) {
                    dValue = dValue.substring(1, dValue.length() - 1);
                }
                row.setValue(columnName, dValue);
            }
        }
    }

    protected void gatherIdValues(final List<Object> idValues, final Type type,
            final Object value) {

        if (type.isEntityType()) {
            final EntityType entityType = EntityType.class.cast(type);
            final String entityName = entityType.getAssociatedEntityName();
            final EntityPersister ep = getPersister().getFactory()
                    .getEntityPersister(entityName);
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
            int[] sqlTypes = type.sqlTypes(getPersister().getFactory());

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
            int[] sqlTypes = type.sqlTypes(getPersister().getFactory());

            assert sqlTypes.length == 1 : sqlTypes.length;

            propValues.add(convert(type, value));
        }
    }

    protected Object convert(final Type type, final Object value) {
        if (type instanceof CustomType) {
            if (Enum.class.isAssignableFrom(type.getReturnedClass())) {
                final Enum e = Enum.class.cast(value);
                final int[] sqlTypes = type.sqlTypes(getPersister()
                        .getFactory());

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
        final int popertyIndex = getPersister().getPropertyIndex(propertyName);
        final boolean[] propertySelectable = ReflectionUtil.getValue(
                PROPERTY_SELECTABLE_FIELD, getPersister());
        return propertySelectable[popertyIndex];
    }

    protected boolean isPropertyTypeReadTarget(final Type propType) {
        if (propType.isCollectionType()) {
            return false;
        }
        if (propType.isComponentType()) {
            if (AbstractComponentType.class.cast(propType).isEmbedded()) {
                return false;
            }
        }
        if (propType.isEntityType()) {
            if (EntityType.class.cast(propType).isOneToOne()) {
                OneToOneType oneToOneType = OneToOneType.class.cast(propType);
                return oneToOneType.isReferenceToPrimaryKey();
            }
        }
        return true;
    }

    protected AbstractEntityPersister getPersister() {
        return persister;
    }

    protected SessionImplementor getSession() {
        return SessionImplementor.class.cast(em.getDelegate());
    }

    protected EntityMode getEntityMode() {
        if (getSession().isOpen()) {
            return getSession().getEntityMode();
        }
        final SessionFactoryImplementor sf = getPersister().getFactory();
        return sf.getSettings().getDefaultEntityMode();
    }

    public DataSet read() {
        return dataSet;
    }
}
