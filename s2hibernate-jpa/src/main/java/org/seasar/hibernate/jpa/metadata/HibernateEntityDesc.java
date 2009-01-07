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
package org.seasar.hibernate.jpa.metadata;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.impl.SessionFactoryImpl;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.type.Type;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.jpa.metadata.EntityDesc;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.tiger.CollectionsUtil;
import org.seasar.framework.util.tiger.ReflectionUtil;

/**
 * Hibernate用のエンティティ定義です。
 * 
 * @author koichik
 */
public class HibernateEntityDesc implements EntityDesc {

    /** {@link SessionFactoryImpl}の<code>imports</code>フィールド */
    protected static final Field IMPORTS_FIELD = ClassUtil.getDeclaredField(
            SessionFactoryImpl.class, "imports");
    static {
        IMPORTS_FIELD.setAccessible(true);
    }

    /** エンティティのクラス */
    protected final Class<?> entityClass;

    /** エンティティのクラスの{@link BeanDesc} */
    protected final BeanDesc beanDesc;

    /** セッションファクトリ */
    protected final SessionFactoryImplementor sessionFactory;

    /** エンティティのメタデータ */
    protected final AbstractEntityPersister metadata;

    /** エンティティの名前 */
    protected final String entityName;

    /** エンティティの属性名の配列 */
    protected final String[] attributeNames;

    /** エンティティの属性定義の配列 */
    protected final HibernateAttributeDesc[] attributeDescs;

    /** エンティティの属性名をキー、エンティティの属性を値としたマップ */
    protected final Map<String, HibernateAttributeDesc> attributeDescMap = CollectionsUtil
            .newHashMap();

    /** 主テーブル名 */
    protected final String primaryTableName;

    /** テーブル名の配列 */
    protected final String[] tableNames;

    /** 識別カラム */
    protected final String discriminatorColumnName;

    /** 識別値 */
    protected final String discriminatorValue;

    /** 識別カラムの{@link Types SQL型}が表す値 */
    protected final int discriminatorSqlType;

    /**
     * インスタンスを構築します。
     * 
     * @param entityClass
     *            エンティティクラス
     * @param sessionFactory
     *            セッションファクトリ
     */
    public HibernateEntityDesc(final Class<?> entityClass,
            final SessionFactoryImplementor sessionFactory) {
        this.entityClass = entityClass;
        this.beanDesc = BeanDescFactory.getBeanDesc(entityClass);
        this.sessionFactory = sessionFactory;
        final ClassMetadata classMetadata = sessionFactory
                .getClassMetadata(entityClass);
        this.metadata = AbstractEntityPersister.class.cast(classMetadata);
        this.entityName = resolveEntityName();
        this.attributeNames = createAttributeNames();
        this.attributeDescs = createAttributeDescs();
        this.primaryTableName = metadata.getTableName();
        this.tableNames = createTableNames();
        if (metadata instanceof SingleTableEntityPersister
                && metadata.getDiscriminatorColumnName() != null) {
            this.discriminatorColumnName = metadata
                    .getDiscriminatorColumnName();
            this.discriminatorValue = createDiscriminatorValue();
            this.discriminatorSqlType = createDiscriminatorSqlType();
        } else {
            this.discriminatorColumnName = null;
            this.discriminatorValue = null;
            this.discriminatorSqlType = Types.OTHER;
        }
    }

    public String getEntityName() {
        return entityName;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public String[] getAttributeNames() {
        return attributeNames;
    }

    public HibernateAttributeDesc getIdAttributeDesc() {
        return attributeDescs[0];
    }

    public HibernateAttributeDesc getAttributeDesc(final String attributeName) {
        return attributeDescMap.get(attributeName);
    }

    public HibernateAttributeDesc[] getAttributeDescs() {
        return attributeDescs;
    }

    /**
     * エンティティのクラスからエンティティの名前を解決します。
     * 
     * @return 解決されたエンティティの名前
     */
    protected String resolveEntityName() {
        final String entityClassName = entityClass.getName();
        final Map<String, String> imports = ReflectionUtil.getValue(
                IMPORTS_FIELD, sessionFactory);
        for (final Entry<String, String> entry : imports.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            if (!entityClassName.equals(key) && entityClassName.equals(value)) {
                return key;
            }
        }

        return entityClassName;
    }

    /**
     * エンティティの属性名の配列を作成します。
     * 
     * @return 属性名の配列
     */
    protected String[] createAttributeNames() {
        final String[] propertyNames = metadata.getPropertyNames();
        final String[] attributeNames = new String[propertyNames.length + 1];
        attributeNames[0] = metadata.getIdentifierPropertyName();
        System.arraycopy(propertyNames, 0, attributeNames, 1,
                propertyNames.length);
        return attributeNames;
    }

    /**
     * エンティティの属性定義の配列を作成します。
     * 
     * @return 属性定義の配列
     */
    protected HibernateAttributeDesc[] createAttributeDescs() {
        final String idName = metadata.getIdentifierPropertyName();
        final String[] propertyNames = metadata.getPropertyNames();
        final int versionIndex = metadata.getVersionProperty();
        final HibernateAttributeDesc[] attributeDescs = new HibernateAttributeDesc[propertyNames.length + 1];
        attributeDescs[0] = new HibernateAttributeDesc(sessionFactory,
                metadata, idName, true, false);
        attributeDescMap.put(idName, attributeDescs[0]);
        for (int i = 0; i < propertyNames.length; ++i) {
            attributeDescs[i + 1] = new HibernateAttributeDesc(sessionFactory,
                    metadata, propertyNames[i], false, i == versionIndex);
            attributeDescMap.put(propertyNames[i], attributeDescs[i + 1]);
        }
        return attributeDescs;
    }

    /**
     * テーブル名の配列を作成します。
     * 
     * @return テーブル名の配列
     */
    protected String[] createTableNames() {
        final String[] original = metadata
                .getConstraintOrderedTableNameClosure();
        final String[] tableNames = new String[original.length];
        System.arraycopy(original, 0, tableNames, 0, original.length);
        return tableNames;
    }

    /**
     * 識別値を作成します。
     * 
     * @return 識別値
     */
    protected String createDiscriminatorValue() {
        final String dValue = metadata.getDiscriminatorSQLValue();
        if (dValue.length() > 1 && dValue.startsWith("'")
                && dValue.endsWith("'")) {
            return dValue.substring(1, dValue.length() - 1);
        }
        return dValue;
    }

    /**
     * 識別カラムの{@link Types SQL型}が表す値を作成します。
     * 
     * @return 識別カラムの{@link Types SQL型}が表す値
     */
    protected int createDiscriminatorSqlType() {
        final Type dType = metadata.getDiscriminatorType();
        final int[] sqlTypes = dType.sqlTypes(sessionFactory);
        if (sqlTypes != null && sqlTypes.length > 0) {
            return sqlTypes[0];
        }
        return Types.OTHER;
    }

    /**
     * 主テーブルの名前を返します。
     * 
     * @return 主テーブルの名前
     */
    public String getPrimaryTableName() {
        return primaryTableName;
    }

    /**
     * 指定したテーブル名と主テーブル名が等しい場合、<code>true</code>を返します。
     * 
     * @param tableName
     *            テーブル名
     * @return <code>tableName</code>が主テーブルと等しい場合、<code>true</code>
     */
    public boolean hasPrimaryTableName(final String tableName) {
        return primaryTableName.equalsIgnoreCase(tableName);
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
     * インデックスを指定してテーブルの名前を取得します。
     * 
     * @param index
     *            インデックス
     * @return テーブルの名前
     */
    public String getTableName(final int index) {
        return tableNames[index];
    }

    /**
     * テーブル名を持っている場合<code>true</code>を返します。
     * 
     * @param tableName
     *            テーブルの名前
     * @return テーブル名を持っている場合<code>true</code>、そうでない場合<code>false</code>
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
     * 識別カラム名を持っている場合<code>true</code>を返します。
     * 
     * @return 識別カラム名を持っている場合<code>true</code>、そうでない場合<code>false</code>
     */
    public boolean hasDiscriminatorColumn() {
        return discriminatorColumnName != null;
    }

    /**
     * 識別カラム名を返します。
     * 
     * @return 識別カラム名
     */
    public String getDiscriminatorColumnName() {
        return discriminatorColumnName;
    }

    /**
     * 識別値を返します。
     * 
     * @return 識別値
     */
    public String getDiscriminatorValue() {
        return discriminatorValue;
    }

    /**
     * 識別カラムの{@link Types SQL型}が表す値を返します。
     * 
     * @return 識別カラムの{@link Types SQL型}が表す値
     */
    public int getDiscriminatorSqlType() {
        return discriminatorSqlType;
    }

}
