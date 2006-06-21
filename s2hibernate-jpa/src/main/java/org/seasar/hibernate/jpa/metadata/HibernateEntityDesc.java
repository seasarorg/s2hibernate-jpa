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

import org.hibernate.EntityMode;
import org.hibernate.impl.SessionFactoryImpl;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.EntityPersister;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.jpa.EntityDesc;
import org.seasar.framework.util.ClassUtil;
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

    protected Class<ENTITY> entityClass;

    protected BeanDesc beanDesc;

    protected SessionFactoryImpl sessionFactory;

    protected ClassMetadata metadata;

    protected EntityPersister persister;

    protected String name;

    public HibernateEntityDesc(final Class<ENTITY> entityClass,
            final SessionFactoryImpl sessionFactory) {
        this.entityClass = entityClass;
        this.beanDesc = BeanDescFactory.getBeanDesc(entityClass);
        this.sessionFactory = sessionFactory;
        this.metadata = sessionFactory.getClassMetadata(entityClass);
        this.persister = sessionFactory.getEntityPersister(entityClass
                .getName());
        setup();
    }

    public String getName() {
        return name;
    }

    public String getIdPropertyName() {
        return metadata.getIdentifierPropertyName();
    }

    public Class<?> getIdPropertyClass() {
        return metadata.getIdentifierType().getReturnedClass();
    }

    public String[] getPropertyNames() {
        return metadata.getPropertyNames();
    }

    public Class<?> getPropertyClass(final String propertyName) {
        return metadata.getPropertyType(propertyName).getReturnedClass();

    }

    public boolean isAssociationProperty(final String propertyName) {
        return metadata.getPropertyType(propertyName).isAssociationType();
    }

    public boolean isCollectionProperty(final String propertyName) {
        return metadata.getPropertyType(propertyName).isCollectionType();
    }

    @SuppressWarnings("unchecked")
    public <T> T getPropertyValue(final ENTITY entity, final String propertyName) {
        return (T) metadata.getPropertyValue(entity, propertyName,
                EntityMode.POJO);
    }

    public void setPropertyValue(final ENTITY entity,
            final String propertyName, Object value) {
        metadata.setPropertyValue(entity, propertyName, value, EntityMode.POJO);
    }

    protected void setup() {
        setupName();
    }

    protected void setupName() {
        final String entityClassName = entityClass.getName();
        final Map<String, String> imports = ReflectionUtil.getValue(
                IMPORTS_FIELD, sessionFactory);
        for (final Entry<String, String> entry : imports.entrySet()) {
            if (entityClassName.equals(entry.getValue())) {
                this.name = entry.getKey();
                return;
            }
        }

        this.name = entityClassName;
    }
}
