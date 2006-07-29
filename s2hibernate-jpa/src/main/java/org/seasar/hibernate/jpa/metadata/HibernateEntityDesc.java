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
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.impl.SessionFactoryImpl;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.EntityPersister;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.jpa.AttributeDesc;
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

    protected final ClassMetadata metadata;

    protected final EntityPersister persister;

    protected final String entityName;

    protected final String[] attributeNames;

    protected final AttributeDesc[] attributeDescs;

    protected final Map<String, AttributeDesc> attributeDescMap = CollectionsUtil
            .newHashMap();

    public HibernateEntityDesc(final Class<ENTITY> entityClass,
            final SessionFactoryImplementor sessionFactory) {
        this.entityClass = entityClass;
        this.beanDesc = BeanDescFactory.getBeanDesc(entityClass);
        this.sessionFactory = sessionFactory;
        this.metadata = sessionFactory.getClassMetadata(entityClass);
        this.persister = sessionFactory.getEntityPersister(entityClass
                .getName());
        this.entityName = resolveEntityName();
        this.attributeNames = createAttributeNames();
        this.attributeDescs = createAttributeDescs();
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

    public AttributeDesc getIdAttributeDesc() {
        return attributeDescs[0];
    }

    public AttributeDesc getAttributeDesc(final String attributeName) {
        return attributeDescMap.get(attributeName);
    }

    public AttributeDesc[] getAttributeDescs() {
        return attributeDescs;
    }

    public SessionFactoryImplementor getSessionFactory() {
        return sessionFactory;
    }

    public EntityPersister getPersister() {
        return persister;
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

    protected AttributeDesc[] createAttributeDescs() {
        final String idName = metadata.getIdentifierPropertyName();
        final String[] propertyNames = metadata.getPropertyNames();
        final int versionIndex = metadata.getVersionProperty();
        final AttributeDesc[] attributeDescs = new AttributeDesc[propertyNames.length + 1];
        attributeDescs[0] = new HibernateAttributeDesc(sessionFactory,
                metadata, idName, true, false);
        for (int i = 0; i < propertyNames.length; ++i) {
            attributeDescs[i + 1] = new HibernateAttributeDesc(sessionFactory,
                    metadata, propertyNames[i], false, i == versionIndex);
        }
        return attributeDescs;
    }

}
