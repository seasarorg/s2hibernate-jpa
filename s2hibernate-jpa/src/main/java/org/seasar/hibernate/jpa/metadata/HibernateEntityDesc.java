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

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.metadata.ClassMetadata;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.jpa.EntityDesc;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.StringUtil;

/**
 * 
 * @author koichik
 */
public class HibernateEntityDesc implements EntityDesc {
    protected Class<?> entityClass;

    protected BeanDesc beanDesc;

    protected ClassMetadata metadata;

    protected String name;

    protected boolean fieldAccess;

    public HibernateEntityDesc(final Class<?> entityClass,
            final ClassMetadata metadata) {
        this.entityClass = entityClass;
        this.beanDesc = BeanDescFactory.getBeanDesc(entityClass);
        this.metadata = metadata;
        setup();
    }

    public String getName() {
        return name;
    }

    public boolean isFieldAccess() {
        return fieldAccess;
    }

    public String getIdPropertyName() {
        return metadata.getIdentifierPropertyName();
    }

    public String[] getPropertyNames() {
        return metadata.getPropertyNames();
    }

    protected void setup() {
        setupName();
        setupFieldAccess();
    }

    protected void setupName() {
        final Entity entity = entityClass.getAnnotation(Entity.class);
        if (entity != null) {
            final String name = entity.name();
            if (!StringUtil.isEmpty(name)) {
                this.name = name;
                return;
            }
        }

        this.name = StringUtil.decapitalize(ClassUtil
                .getShortClassName(entityClass));
    }

    protected void setupFieldAccess() {
        final String idName = metadata.getIdentifierPropertyName();
        final Field field = beanDesc.getField(idName);
        if (field != null) {
            final Id id = field.getAnnotation(Id.class);
            if (id != null) {
                fieldAccess = true;
            }
        }
    }
}
