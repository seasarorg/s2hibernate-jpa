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
import org.seasar.framework.jpa.EntityDesc;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.tiger.CollectionsUtil;
import org.seasar.framework.util.tiger.ReflectionUtil;

/**
 * 
 * @author koichik
 */
public class HibernateEntityDesc<ENTITY> implements EntityDesc<ENTITY> {
    protected static final Field IMPORTS_FIELD = ClassUtil.getDeclaredField(
            SessionFactoryImpl.class, "imports");
    static {
        IMPORTS_FIELD.setAccessible(true);
    }

    protected final Class<ENTITY> entityClass;

    protected final BeanDesc beanDesc;

    protected final SessionFactoryImplementor sessionFactory;

    protected final AbstractEntityPersister metadata;

    protected final String entityName;

    protected final String[] attributeNames;

    protected final HibernateAttributeDesc[] attributeDescs;

    protected final Map<String, HibernateAttributeDesc> attributeDescMap = CollectionsUtil
            .newHashMap();

    protected final String primaryTableName;

    protected final String[] tableNames;

    protected final String discriminatorColumnName;

    protected final String discriminatorValue;

    protected final int discriminatorSqlType;

    public HibernateEntityDesc(final Class<ENTITY> entityClass,
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

    public Class<ENTITY> getEntityClass() {
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

    protected String resolveEntityName() {
        final String entityClassName = entityClass.getName();
        final Map<String, String> imports = ReflectionUtil.getValue(
                IMPORTS_FIELD, sessionFactory);
        for (final Entry<String, String> entry : imports.entrySet()) {
            if (entityClassName.equals(entry.getValue())) {
                return entry.getKey();
            }
        }

        return entityClassName;
    }

    protected String[] createAttributeNames() {
        final String[] propertyNames = metadata.getPropertyNames();
        final String[] attributeNames = new String[propertyNames.length + 1];
        attributeNames[0] = metadata.getIdentifierPropertyName();
        System.arraycopy(propertyNames, 0, attributeNames, 1,
                propertyNames.length);
        return attributeNames;
    }

    protected HibernateAttributeDesc[] createAttributeDescs() {
        final String idName = metadata.getIdentifierPropertyName();
        final String[] propertyNames = metadata.getPropertyNames();
        final int versionIndex = metadata.getVersionProperty();
        final HibernateAttributeDesc[] attributeDescs = new HibernateAttributeDesc[propertyNames.length + 1];
        attributeDescs[0] = new HibernateAttributeDesc(sessionFactory,
                metadata, idName, true, false);
        for (int i = 0; i < propertyNames.length; ++i) {
            attributeDescs[i + 1] = new HibernateAttributeDesc(sessionFactory,
                    metadata, propertyNames[i], false, i == versionIndex);
        }
        return attributeDescs;
    }

    protected String[] createTableNames() {
        final String[] original = metadata
                .getConstraintOrderedTableNameClosure();
        final String[] tableNames = new String[original.length];
        System.arraycopy(original, 0, tableNames, 0, original.length);
        return tableNames;
    }

    protected String createDiscriminatorValue() {
        final String dValue = metadata.getDiscriminatorSQLValue();
        if (dValue.length() > 1 && dValue.startsWith("'")
                && dValue.endsWith("'")) {
            return dValue.substring(1, dValue.length() - 1);
        }
        return dValue;
    }

    protected int createDiscriminatorSqlType() {
        final Type dType = metadata.getDiscriminatorType();
        final int[] sqlTypes = dType.sqlTypes(sessionFactory);
        if (sqlTypes != null && sqlTypes.length > 0) {
            return sqlTypes[0];
        }
        return Types.OTHER;
    }

    public String getPrimaryTableName() {
        return primaryTableName;
    }

    public boolean hasPrimaryTableName(final String tableName) {
        return primaryTableName.equalsIgnoreCase(tableName);
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

    public boolean hasDiscriminatorColumn() {
        return discriminatorColumnName != null;
    }

    public String getDiscriminatorColumnName() {
        return discriminatorColumnName;
    }

    public String getDiscriminatorValue() {
        return discriminatorValue;
    }

    public int getDiscriminatorSqlType() {
        return discriminatorSqlType;
    }

}
