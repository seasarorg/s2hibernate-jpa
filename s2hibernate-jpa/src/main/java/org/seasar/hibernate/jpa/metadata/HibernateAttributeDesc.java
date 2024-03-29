/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
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
import java.util.List;
import java.util.Map;

import org.hibernate.EntityMode;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.AbstractComponentType;
import org.hibernate.type.CustomType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.tiger.CollectionsUtil;
import org.seasar.framework.util.tiger.ReflectionUtil;

/**
 * Hibernateのエンティティの属性定義です。
 * 
 * @author koichik
 */
public class HibernateAttributeDesc extends AbstractHibernateAttributeDesc {

    /** {@link AbstractEntityPersister}の<code>propertySelectable</code>フィールド */
    protected static final Field PROPERTY_SELECTABLE_FIELD = ClassUtil
            .getDeclaredField(AbstractEntityPersister.class,
                    "propertySelectable");
    static {
        PROPERTY_SELECTABLE_FIELD.setAccessible(true);
    }

    /** エンティティのメタデータ */
    protected final AbstractEntityPersister metadata;

    /** {@link Types SQL型}の値の配列 */
    protected final int[] sqlTypes;

    /** テーブル名の配列 */
    protected final String[] tableNames;

    /** テーブル名をキー、カラム名の配列を値とするマップ */
    protected final Map<String, String[]> columnNamesMap = CollectionsUtil
            .newHashMap();

    /** このオブジェクトが読まれる対象かどうかを表すフラグ */
    protected final boolean readTarget;

    /** このオブジェクトの値が取得可能かどうかを表すフラグ */
    protected final boolean selectable;

    /**
     * インスタンスを構築します。
     * 
     * @param factory
     *            セッションファクトリ
     * @param metadata
     *            エンティティのメタデータ
     * @param name
     *            属性名
     * @param id
     *            このオブジェクトがIDであるかどうかを表すフラグ
     * @param version
     *            このオブジェクトがバージョン番号であるかどうかを表すフラグ
     */
    public HibernateAttributeDesc(final SessionFactoryImplementor factory,
            final AbstractEntityPersister metadata, final String name,
            final boolean id, final boolean version) {
        super(factory, id ? metadata.getIdentifierType() : metadata
                .getPropertyType(name), name, id, version);

        this.metadata = metadata;
        sqlTypes = hibernateType.sqlTypes(factory);

        if (id) {
            readTarget = true;
        } else {
            readTarget = isReadTargetType(hibernateType);
        }

        selectable = isSelectableAttribute();
        tableNames = createTableNames();
        setupColumnNameMap();
    }

    /**
     * テーブル名の配列を作成します。
     * 
     * @return テーブル名の配列
     */
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

    /**
     * テーブル名をキー、カラム名の配列を値とするマップを設定します。
     */
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

    /**
     * 指定された<code>type</code>が読まれる対象の場合<code>true</code>を返します。
     * 
     * @param type
     *            Hibernateの型
     * @return 指定された<code>type</code>が読まれる対象の場合<code>true</code>、そうでない場合<code>false</code>
     */
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

    /**
     * このオブジェクトの値が取得可能な場合<code>true</code>を返します。
     * 
     * @return このオブジェクトの値が取得可能な場合<code>true</code>、そうでない場合<code>false</code>
     */
    protected boolean isSelectableAttribute() {
        if (id) {
            return true;
        }
        final int index = metadata.getPropertyIndex(name);
        final boolean[] selectable = ReflectionUtil.getValue(
                PROPERTY_SELECTABLE_FIELD, metadata);
        return selectable[index];
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

    /**
     * {@link Types SQL型}の値の配列を返します。
     * 
     * @return {@link Types SQL型}の値の配列
     */
    public int[] getSqlTypes() {
        return sqlTypes;
    }

    /**
     * このオブジェクトの値が取得可能な場合<code>true</code>を返します。
     * 
     * @return このオブジェクトの値が取得可能な場合<code>true</code>、そうでない場合<code>false</code>
     */
    public boolean isSelectable() {
        return selectable;
    }

    /**
     * Hibernateの型を返します。
     * 
     * @return Hibernateの型
     */
    public Type getHibernateType() {
        return hibernateType;
    }

    /**
     * テーブル名の数を返します。
     * 
     * @return テーブル名の数
     */
    public int getTableNameSize() {
        return tableNames.length;
    }

    /**
     * 指定されたインデックスに対応するテーブル名を返します。
     * 
     * @param index
     *            インデックス
     * @return テーブル名
     */
    public String getTableName(final int index) {
        return tableNames[index];
    }

    /**
     * 指定したテーブル名を持つ場合<code>true</code>を返します。
     * 
     * @param tableName
     *            テーブル名
     * @return 指定したテーブル名を持つ場合<code>true</code>、そうでない場合<code>false</code>
     */
    public boolean hasTableName(final String tableName) {
        for (int i = 0; i < getTableNameSize(); i++) {
            if (getTableName(i).equalsIgnoreCase(tableName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 指定されたテーブルに属するカラムの数を返します。
     * 
     * @param tableName
     *            テーブル名
     * @return 指定されたテーブルに属するカラムの数
     */
    public int getColumnNameSize(final String tableName) {
        return getColumnNames(tableName).length;
    }

    /**
     * 指定されたインデックスに対応するカラムの数を返します。
     * 
     * @param index
     *            インデックス
     * @return カラムの数
     */
    public int getColumnNameSize(final int index) {
        return getColumnNames(index).length;
    }

    /**
     * 指定したテーブルに属するカラムの配列を返します。
     * 
     * @param tableName
     *            テーブル名
     * @return カラム名の配列
     */
    public String[] getColumnNames(final String tableName) {
        if (tableName == null) {
            return null;
        }
        return columnNamesMap.get(tableName.toLowerCase());
    }

    /**
     * 指定されたインデックスに対応するテーブルのカラム名の配列を返します。
     * 
     * @param index
     *            インデックス
     * @return カラム名の配列
     */
    public String[] getColumnNames(final int index) {
        return getColumnNames(getTableName(index));
    }

    /**
     * このオブジェクトが読まれる対象の場合<code>true</code>を返します。
     * 
     * @return このオブジェクトが読まれる対象の場合<code>true</code>、そうでない場合<code>false</code>
     */
    public boolean isReadTarget() {
        return readTarget;
    }

    /**
     * エンティティからこのオブジェクトに対応するすべての値を抽出して返します。
     * 
     * @param entity
     *            エンティティ
     * @return このオブジェクトに対応するすべての値
     */
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

    /**
     * <code>value</code>からIDの値を集め、<code>allValues</code>に格納します。
     * 
     * @param allValues
     *            IDの値のリスト
     * @param type
     *            Hibernateの型
     * @param value
     *            エンティティ、IDを表すコンポーネント、またはIDの値
     */
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

    /**
     * <code>value</code>からプロパティの値を集め、<code>allValues</code>に格納します。
     * 
     * @param allValues
     *            プロパティの値のリスト
     * @param type
     *            Hibernateの型
     * @param value
     *            エンティティ、コンポーネント、またはプロパティの値
     */
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

    /**
     * Hibernateの型に基づき値を変換します。
     * 
     * @param type
     *            Hibernateの型
     * @param value
     *            値
     * @return 変換された値
     */
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
