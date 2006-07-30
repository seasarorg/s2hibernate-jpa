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

import javax.persistence.EntityManager;

import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.JoinedSubclassEntityPersister;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.persister.entity.UnionSubclassEntityPersister;
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
        final EntityDesc entityDesc = EntityDescFactory.getEntityDesc(entity
                .getClass());
        if (entityDesc == null
                || !HibernateEntityDesc.class.isInstance(entityDesc)) {
            return null;
        }

        final HibernateEntityDesc<?> hibernateEntityDesc = HibernateEntityDesc.class
                .cast(entityDesc);
        final EntityPersister persister = hibernateEntityDesc.getPersister();

        return createEntityReader(entity, hibernateEntityDesc, persister);
    }

    protected EntityReader createEntityReader(final Object entity,
            final HibernateEntityDesc<?> entityDesc,
            final EntityPersister persister) {

        if (persister instanceof SingleTableEntityPersister) {
            final SingleTableEntityPersister p = SingleTableEntityPersister.class
                    .cast(persister);
            return new SingleTableEntityReader(entity, em, entityDesc, p);

        } else if (persister instanceof JoinedSubclassEntityPersister) {
            final JoinedSubclassEntityPersister p = JoinedSubclassEntityPersister.class
                    .cast(persister);
            return new JoinedSubclassEntityReader(entity, em, entityDesc, p);

        } else if (persister instanceof UnionSubclassEntityPersister) {
            final UnionSubclassEntityPersister p = UnionSubclassEntityPersister.class
                    .cast(persister);
            return new UnionSubclassEntityReader(entity, em, entityDesc, p);
        }

        return null;
    }
}
