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

import javax.persistence.EntityManager;

import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.seasar.framework.jpa.EntityDesc;
import org.seasar.framework.jpa.EntityDescFactory;
import org.seasar.framework.jpa.unit.EntityReader;
import org.seasar.framework.jpa.unit.EntityReaderProvider;
import org.seasar.hibernate.jpa.metadata.HibernateEntityDesc;

/**
 * 
 * @author taedium
 */
public class HibernateEntityReaderProvider implements EntityReaderProvider {

    private EntityManager em;

    public HibernateEntityReaderProvider(final EntityManager em) {
        this.em = em;
    }

    public EntityReader createEntityReader(final Object entity) {
        if (entity == null) {
            return null;
        }
        final AbstractEntityPersister persister = getAbstractEntityPersister(entity
                .getClass());
        if (persister == null) {
            return null;
        }
        return new HibernateEntityReader(entity, em, persister);
    }

    public EntityReader createEntityReader(final Collection<?> entities) {
        if (entities == null) {
            return null;
        }
        Class<?> entityClass = null;
        for (final Object entity : entities) {
            if (entity != null) {
                entityClass = entity.getClass();
                break;
            }
        }
        if (entityClass == null) {
            return null;
        }
        final AbstractEntityPersister persister = getAbstractEntityPersister(entityClass);
        if (persister == null) {
            return null;
        }
        return new HibernateEntityCollectionReader(entities, em, persister);
    }

    protected AbstractEntityPersister getAbstractEntityPersister(
            final Class<?> entityClass) {
        final EntityDesc entityDesc = EntityDescFactory
                .getEntityDesc(entityClass);
        if (entityDesc == null || !(entityDesc instanceof HibernateEntityDesc)) {
            return null;
        }
        final HibernateEntityDesc<?> hibernateEntityDesc = HibernateEntityDesc.class
                .cast(entityDesc);
        final EntityPersister persister = hibernateEntityDesc.getPersister();
        return AbstractEntityPersister.class.cast(persister);
    }

}
