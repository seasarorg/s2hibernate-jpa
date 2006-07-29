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
import java.util.Map.Entry;

import org.hibernate.EntityMode;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.type.ComponentType;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.Type;
import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.extension.dataset.impl.DataSetImpl;
import org.seasar.extension.dataset.states.RowStates;
import org.seasar.extension.dataset.types.ColumnTypes;
import org.seasar.framework.jpa.unit.EntityReader;
import org.seasar.framework.util.ArrayMap;
import org.seasar.framework.util.CaseInsensitiveMap;
import org.seasar.hibernate.jpa.metadata.HibernateEntityDesc;

/**
 * 
 * @author taedium
 */
public class SingleTableEntityReader implements EntityReader {

    final private SingleTableEntityPersister persister;

    final private SessionFactoryImplementor sessionFactory;

    final private EntityMode entityMode;

    private DataSet dataSet = new DataSetImpl();

    public SingleTableEntityReader(final Object entity,
            final HibernateEntityDesc<?> entityDesc,
            final SingleTableEntityPersister persister) {
        this.persister = persister;
        this.sessionFactory = entityDesc.getSessionFactory();
        this.entityMode = sessionFactory.getSettings().getDefaultEntityMode();
        setupColumns();
        setupRow(entity);
    }

    protected void setupColumns() {
        for (int i = 0; i < persister.getSubclassTableSpan(); i++) {
            dataSet.addTable(persister.getSubclassTableName(i));
        }
        setupIdColumns();
        setupPropertyColumns();
    }

    protected void setupIdColumns() {
        final Type type = persister.getIdentifierType();
        final DataTable table = read().getTable(persister.getTableName());
        final int[] sqlTypes = type.sqlTypes(sessionFactory);
        final String[] columnNames = persister.getIdentifierColumnNames();
        for (int i = 0; i < columnNames.length; i++) {
            final String columnName = columnNames[i];
            int sqlType = Types.OTHER;
            if (sqlTypes != null && sqlTypes.length > i) {
                sqlType = sqlTypes[i];
            }
            table.addColumn(columnName, ColumnTypes.getColumnType(sqlType));
        }
    }

    protected void setupPropertyColumns() {
        final String[] propNames = persister.getPropertyNames();
        for (int i = 0; i < propNames.length; i++) {
            final String propName = propNames[i];
            final String tableName = persister.getPropertyTableName(propName);
            final Type propertyType = persister.getPropertyType(propName);
            final int[] sqlTypes = propertyType.sqlTypes(sessionFactory);
            final DataTable table = dataSet.getTable(tableName);
            final String[] columnNames = persister
                    .getPropertyColumnNames(propName);

            for (int j = 0; j < columnNames.length; j++) {
                final String columnName = columnNames[j];
                int sqlType = Types.OTHER;
                if (sqlTypes != null && sqlTypes.length > j) {
                    sqlType = sqlTypes[j];
                }
                table.addColumn(columnName, ColumnTypes.getColumnType(sqlType));
            }
        }
    }

    protected void setupRow(Object entity) {
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

    protected void setupIdRow(Object entity, DataRow row) {
        final Serializable id = persister.getIdentifier(entity, entityMode);
        final Type idType = persister.getIdentifierType();
        final ArrayMap idColumnValues = getIdColumnValues(id, idType);
        for (int i = 0; i < idColumnValues.size(); i++) {
            final Entry entry = idColumnValues.getEntry(i);
            final String columnName = String.class.cast(entry.getKey());
            final Object value = entry.getValue();
            row.setValue(columnName, value);
        }
    }

    protected void setupPropertyRow(Object entity, DataTable table, DataRow row) {
        final String[] propNames = persister.getPropertyNames();
        for (int i = 0; i < propNames.length; i++) {
            final String propName = propNames[i];
            final String tableName = persister.getPropertyTableName(propName);
            if (!tableName.equals(table.getTableName())) {
                return;
            }
            final String[] columnNames = persister
                    .getPropertyColumnNames(propName);

            for (int j = 0; j < columnNames.length; j++) {
                final String columnName = columnNames[j];
                final Type propType = persister.getPropertyType(propName);
                final Object value = persister.getPropertyValue(entity, i,
                        entityMode);
                row.setValue(columnName, convert(propType, value, j));
            }
        }
    }

    protected ArrayMap getIdColumnValues(Serializable id, Type idType) {
        final ArrayMap columnValues = new CaseInsensitiveMap();

        if (idType.isComponentType()) {
            final ComponentType componentType = ComponentType.class
                    .cast(idType);
            final String[] columnNames = persister.getIdentifierColumnNames();
            for (int i = 0; i < columnNames.length; i++) {
                final String columnName = columnNames[i];
                // TODO
                final Object value = componentType.getPropertyValue(id, i,
                        entityMode);
                columnValues.put(columnName, value);
            }
        } else {
            final String columnName = persister.getIdentifierColumnNames()[0];
            columnValues.put(columnName, id);
        }

        return columnValues;
    }

    protected Object convert(Type hibernateType, Object value, int index) {
        if (value == null) {
            return null;
        }
        if (ManyToOneType.class.isInstance(hibernateType)) {
            final ManyToOneType manyToOneType = ManyToOneType.class
                    .cast(hibernateType);
            if (manyToOneType.isReferenceToPrimaryKey()) {
                final String entityName = manyToOneType
                        .getAssociatedEntityName();
                final EntityPersister ep = sessionFactory
                        .getEntityPersister(entityName);
                final Serializable id = ep.getIdentifier(value, entityMode);
                final Type idType = ep.getIdentifierType();
                final ArrayMap idColumnValues = getIdColumnValues(id, idType);
                return idColumnValues.get(index);
            }
        }
        return value;
    }

    public DataSet read() {
        return dataSet;
    }
}
