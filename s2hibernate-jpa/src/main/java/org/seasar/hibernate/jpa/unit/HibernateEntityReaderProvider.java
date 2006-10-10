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

import java.util.Collection;
import java.util.Map;

import org.seasar.framework.container.annotation.tiger.DestroyMethod;
import org.seasar.framework.container.annotation.tiger.InitMethod;
import org.seasar.framework.jpa.metadata.EntityDesc;
import org.seasar.framework.jpa.metadata.EntityDescFactory;
import org.seasar.framework.jpa.unit.EntityReader;
import org.seasar.framework.jpa.unit.EntityReaderFactory;
import org.seasar.framework.jpa.unit.EntityReaderProvider;
import org.seasar.framework.util.tiger.CollectionsUtil;
import org.seasar.hibernate.jpa.metadata.HibernateEntityDesc;

/**
 * @author taedium
 */
public class HibernateEntityReaderProvider implements EntityReaderProvider {

    @InitMethod
    public void register() {
        EntityReaderFactory.addProvider(this);
    }

    @DestroyMethod
    public void unregister() {
        EntityReaderFactory.removeProvider(this);
    }

    public EntityReader createEntityReader(final Object entity) {
        if (entity == null) {
            return null;
        }
        final HibernateEntityDesc<?> entityDesc = getEntityDesc(entity
                .getClass());
        if (entityDesc == null) {
            return null;
        }
        return new HibernateEntityReader(entity, entityDesc);
    }

    public EntityReader createEntityReader(final Collection<?> entities) {
        if (entities == null) {
            return null;
        }

        final Collection<Object> newEntities = flatten(entities);
        if (newEntities.isEmpty()) {
            return null;
        }

        final Map<Class<?>, HibernateEntityDesc<?>> entityDescs = CollectionsUtil
                .newHashMap();
        for (final Object entity : newEntities) {
            final Class<?> entityClass = entity.getClass();
            if (entityDescs.containsKey(entityClass)) {
                continue;
            }
            final HibernateEntityDesc<?> entityDesc = getEntityDesc(entityClass);
            if (entityDescs == null) {
                return null;
            }
            entityDescs.put(entityClass, entityDesc);
        }
        return new HibernateEntityCollectionReader(newEntities, entityDescs);
    }

    protected Collection<Object> flatten(final Collection<?> entities) {
        Collection<Object> newEntities = CollectionsUtil.newArrayList(entities
                .size());
        for (final Object element : entities) {
            if (element instanceof Object[]) {
                for (final Object nested : Object[].class.cast(element)) {
                    newEntities.add(nested);
                }
            } else {
                newEntities.add(element);
            }
        }
        return newEntities;
    }

    protected HibernateEntityDesc<?> getEntityDesc(final Class<?> entityClass) {
        final EntityDesc entityDesc = EntityDescFactory
                .getEntityDesc(entityClass);
        if (entityDesc == null || !(entityDesc instanceof HibernateEntityDesc)) {
            return null;
        }
        return HibernateEntityDesc.class.cast(entityDesc);
    }
}
