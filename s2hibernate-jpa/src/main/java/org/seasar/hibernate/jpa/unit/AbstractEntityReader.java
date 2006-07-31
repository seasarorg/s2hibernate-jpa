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
import java.sql.Types;

import javax.persistence.EntityManager;

import org.hibernate.EntityMode;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.ComponentType;
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

/**
 * 
 * @author taedium
 */
public abstract class AbstractEntityReader implements EntityReader {

    final private EntityManager em;

    final private AbstractEntityPersister persister;

    private DataSet dataSet = new DataSetImpl();

    public AbstractEntityReader(final EntityManager em,
            final AbstractEntityPersister persister) {

        this.em = em;
        this.persister = persister;
    }

    protected abstract int getTableSpan();

    protected abstract String getSubclassTableName(int index);

    protected void setupColumns() {
        for (int i = 0; i < getTableSpan(); i++) {
            dataSet.addTable(getSubclassTableName(i));
        }
        setupIdColumns();
        setupPropertyColumns();
    }

    protected void setupIdColumns() {
        final Type idType = persister.getIdentifierType();
        final DataTable table = read().getTable(persister.getTableName());
        final int[] sqlTypes = idType.sqlTypes(getSession().getFactory());
        final String[] columnNames = persister.getIdentifierColumnNames();
        for (int i = 0; i < columnNames.length; i++) {
            final String columnName = columnNames[i];
            int sqlType = Types.OTHER;
            if (sqlTypes != null && sqlTypes.length > i) {
                sqlType = sqlTypes[i];
            }

            assert !table.hasColumn(columnName) : columnName.toString() + " "
                    + idType.getName();
            table.addColumn(columnName, ColumnTypes.getColumnType(sqlType));
        }
    }

    protected void setupPropertyColumns() {
        final String[] propNames = persister.getPropertyNames();
        for (int i = 0; i < propNames.length; i++) {
            final String propName = propNames[i];
            final String tableName = persister.getPropertyTableName(propName);
            final Type propType = persister.getPropertyType(propName);
            final int[] sqlTypes = propType.sqlTypes(getSession().getFactory());
            final DataTable table = dataSet.getTable(tableName);
            final String[] columnNames = persister
                    .getPropertyColumnNames(propName);

            if (!isReadablePropertyType(propType)) {
                continue;
            }

            for (int j = 0; j < columnNames.length; j++) {
                final String columnName = columnNames[j];
                if (sqlTypes == null || sqlTypes.length - 1 < j) {
                    break;
                }
                assert !table.hasColumn(columnName) : columnName.toString()
                        + " " + propType.toString();
                table.addColumn(columnName, ColumnTypes
                        .getColumnType(sqlTypes[j]));
            }
        }
    }

    protected void setupRow(final Object entity) {
        for (int i = 0; i < dataSet.getTableSize(); i++) {
            final DataTable table = dataSet.getTable(i);
            final DataRow row = table.addRow();
            setupIdRow(entity, row);
            setupPropertyRow(entity, table, row);
            row.setState(RowStates.UNCHANGED);
            if (row.getValue(0) == null) {
                row.setState(RowStates.REMOVED);
                table.removeRows();
            }
        }
    }

    protected void setupIdRow(final Object entity, final DataRow row) {
        final Serializable id = persister
                .getIdentifier(entity, getEntityMode());
        final Type idType = persister.getIdentifierType();

        if (idType.isComponentType()) {
            final ComponentType componentType = ComponentType.class
                    .cast(idType);
            final String[] columnNames = persister.getIdentifierColumnNames();
            for (int i = 0; i < columnNames.length; i++) {
                final String columnName = columnNames[i];
                final Object value = componentType.getPropertyValue(id, i,
                        getEntityMode());
                row.setValue(columnName, value);
            }
        } else {
            final String columnName = persister.getIdentifierColumnNames()[0];
            row.setValue(columnName, id);
        }
    }

    protected void setupPropertyRow(final Object entity, final DataTable table,
            final DataRow row) {

        final String[] propNames = persister.getPropertyNames();
        for (int i = 0; i < propNames.length; i++) {
            final String propName = propNames[i];
            final String tableName = persister.getPropertyTableName(propName);
            final Type propType = persister.getPropertyType(propName);
            final Object value = persister.getPropertyValue(entity, propName,
                    getEntityMode());
            final String[] columnNames = persister
                    .getPropertyColumnNames(propName);

            if (!tableName.equals(table.getTableName())) {
                continue;
            }
            if (!isReadablePropertyType(propType)) {
                continue;
            }

            for (int j = 0; j < columnNames.length; j++) {
                final String columnName = columnNames[j];
                row.setValue(columnName, convert(propType, value, j));
            }
        }
    }

    protected Object convert(final Type hibernateType, final Object value,
            final int index) {

        if (value == null) {
            return null;
        }

        if (hibernateType.isEntityType()) {
            final EntityType entityType = EntityType.class.cast(hibernateType);
            final String entityName = entityType.getAssociatedEntityName();
            final EntityPersister ep = getSession().getEntityPersister(
                    entityName, value);

            if (entityType.isReferenceToPrimaryKey()) {
                final Serializable id = ep
                        .getIdentifier(value, getEntityMode());
                final Type idType = ep.getIdentifierType();
                if (idType instanceof ComponentType) {
                    ComponentType componentType = ComponentType.class
                            .cast(idType);
                    return componentType.getPropertyValue(id, index,
                            getSession());
                }
                return id;
            }
            return ep.getPropertyValue(value, index, getEntityMode());
        }
        return value;
    }

    protected boolean isReadablePropertyType(final Type propType) {
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

    protected SessionImplementor getSession() {
        return SessionImplementor.class.cast(em.getDelegate());
    }

    protected EntityMode getEntityMode() {
        return getSession().getEntityMode();
    }

    public DataSet read() {
        return dataSet;
    }
}
