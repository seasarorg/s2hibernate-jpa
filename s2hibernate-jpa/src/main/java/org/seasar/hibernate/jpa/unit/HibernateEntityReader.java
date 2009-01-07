/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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

import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.extension.dataset.impl.DataSetImpl;
import org.seasar.extension.dataset.states.RowStates;
import org.seasar.extension.dataset.types.ColumnTypes;
import org.seasar.framework.jpa.unit.EntityReader;
import org.seasar.hibernate.jpa.metadata.HibernateAttributeDesc;
import org.seasar.hibernate.jpa.metadata.HibernateEntityDesc;

/**
 * エンティティをデータセットとして読み取るクラスです。
 * 
 * @author taedium
 */
public class HibernateEntityReader implements EntityReader {

    private HibernateEntityDesc entityDesc;

    /** データセット */
    protected final DataSet dataSet = new DataSetImpl();

    /**
     * インスタンスを構築します。
     */
    protected HibernateEntityReader() {
    }

    /**
     * インスタンスを構築します。
     * 
     * @param entity
     *            エンティティ
     * @param entityDesc
     *            <code>entity</code>のエンティティ定義
     */
    public HibernateEntityReader(final Object entity,
            final HibernateEntityDesc entityDesc) {

        this.entityDesc = entityDesc;
        setupColumns();
        setupRow(entity);
    }

    /**
     * カラムを設定します。
     */
    protected void setupColumns() {
        for (int i = 0; i < getEntityDesc().getTableNameSize(); i++) {
            final String tableName = getEntityDesc().getTableName(i);
            if (!dataSet.hasTable(tableName)) {
                dataSet.addTable(tableName);
            }
        }
        setupAttributeColumns();
        setupDiscriminatorColumn();
    }

    /**
     * エンティティの属性に対応するカラムを設定します。
     */
    protected void setupAttributeColumns() {
        final HibernateAttributeDesc[] attributes = getEntityDesc()
                .getAttributeDescs();

        for (int i = 0; i < attributes.length; i++) {
            final HibernateAttributeDesc attribute = attributes[i];

            if (!(attribute.isSelectable() && attribute.isReadTarget())) {
                continue;
            }

            for (int j = 0; j < attribute.getTableNameSize(); j++) {
                final String tableName = attribute.getTableName(j);
                final String[] columnNames = attribute
                        .getColumnNames(tableName);
                final int[] sqlTypes = attribute.getSqlTypes();

                assert columnNames.length == sqlTypes.length : columnNames.length
                        + ", " + sqlTypes.length;

                final DataTable table = dataSet.getTable(tableName);

                for (int k = 0; k < sqlTypes.length; k++) {
                    final String columnName = columnNames[k];
                    if (columnName == null || table.hasColumn(columnName)) {
                        continue;
                    }
                    table.addColumn(columnName, ColumnTypes
                            .getColumnType(sqlTypes[k]));
                }
            }
        }
    }

    /**
     * 識別カラムを設定します。
     */
    protected void setupDiscriminatorColumn() {
        if (!getEntityDesc().hasDiscriminatorColumn()) {
            return;
        }
        final String tableName = getEntityDesc().getPrimaryTableName();
        final String columnName = getEntityDesc().getDiscriminatorColumnName();
        final DataTable table = dataSet.getTable(tableName);
        if (!table.hasColumn(columnName)) {
            final int sqlType = getEntityDesc().getDiscriminatorSqlType();
            table.addColumn(columnName, ColumnTypes.getColumnType(sqlType));
        }
    }

    /**
     * 行を設定します。
     * 
     * @param entity
     *            エンティティ
     */
    protected void setupRow(final Object entity) {
        for (int i = 0; i < getEntityDesc().getTableNameSize(); i++) {
            final String tableName = getEntityDesc().getTableName(i);
            final DataTable table = dataSet.getTable(tableName);
            final DataRow row = table.addRow();
            setupAttributeValues(entity, row, tableName);
            setupDiscriminatorValue(row, tableName);
            row.setState(RowStates.UNCHANGED);
        }
    }

    /**
     * エンティティの属性値を行に設定します。
     * 
     * @param entity
     *            エンティティ
     * @param row
     *            行
     * @param tableName
     *            テーブル名
     */
    protected void setupAttributeValues(final Object entity, final DataRow row,
            final String tableName) {

        final HibernateAttributeDesc[] attributes = getEntityDesc()
                .getAttributeDescs();

        for (int i = 0; i < attributes.length; i++) {
            final HibernateAttributeDesc attribute = attributes[i];

            if (!(attribute.hasTableName(tableName))) {
                continue;
            }
            if (!(attribute.isSelectable() && attribute.isReadTarget())) {
                continue;
            }

            final String[] columnNames = attribute.getColumnNames(tableName);
            final Object[] values = attribute.getAllValues(entity);

            for (int j = 0; j < columnNames.length; j++) {
                if (columnNames[j] != null) {
                    Object value = (j < values.length) ? values[j] : null;
                    row.setValue(columnNames[j], value);
                }
            }
        }
    }

    /**
     * 識別値を行に設定します。
     * 
     * @param row
     *            行
     * @param tableName
     *            テーブル名
     */
    protected void setupDiscriminatorValue(final DataRow row,
            final String tableName) {

        if (getEntityDesc().hasDiscriminatorColumn()
                && getEntityDesc().hasPrimaryTableName(tableName)) {
            final String columnName = getEntityDesc()
                    .getDiscriminatorColumnName();
            row.setValue(columnName, getEntityDesc().getDiscriminatorValue());
        }

    }

    /**
     * エンティティ定義を返します。
     * 
     * @return エンティティ定義
     */
    protected HibernateEntityDesc getEntityDesc() {
        return entityDesc;
    }

    public DataSet read() {
        return dataSet;
    }
}
