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

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.seasar.framework.jpa.EntityDesc;
import org.seasar.framework.jpa.EntityDescProvider;

/**
 * 
 * @author koichik
 */
public class HibernateEntityDescProvider implements EntityDescProvider {

    public Object getContextKey(final EntityManager em) {
        final Session session = Session.class.cast(em.getDelegate());
        return session.getSessionFactory();
    }

    /**
     * @see org.seasar.framework.jpa.EntityDescProvider#createEntityDesc(java.lang.Class)
     */
    public EntityDesc createEntityDesc(final Class<?> entityClass,
            final Object contextKey) {
        final SessionFactory factory = SessionFactory.class.cast(contextKey);
        final ClassMetadata metadata = factory.getClassMetadata(entityClass);
        if (metadata == null) {
            return null;
        }
        return new HibernateEntityDesc(entityClass, metadata);
    }

}
