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
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.seasar.framework.container.annotation.tiger.DestroyMethod;
import org.seasar.framework.container.annotation.tiger.InitMethod;
import org.seasar.framework.jpa.EntityDesc;
import org.seasar.framework.jpa.EntityDescFactory;
import org.seasar.framework.jpa.EntityDescProvider;

/**
 * @author koichik
 */
public class HibernateEntityDescProvider implements EntityDescProvider {

    protected SessionFactoryImplementor sessionFactory;

    public HibernateEntityDescProvider(final EntityManager em) {
        final Session session = Session.class.cast(em.getDelegate());
        sessionFactory = SessionFactoryImplementor.class.cast(session
                .getSessionFactory());
    }

    @InitMethod
    public void register() {
        EntityDescFactory.addProvider(this);
    }

    @DestroyMethod
    public void unregister() {
        EntityDescFactory.removeProvider(this);
    }

    @SuppressWarnings("unchecked")
    public <ENTITY> EntityDesc<ENTITY> createEntityDesc(
            final Class<ENTITY> entityClass) {
        final ClassMetadata metadata = sessionFactory
                .getClassMetadata(entityClass);
        if (metadata == null) {
            return null;
        }
        return new HibernateEntityDesc(entityClass, sessionFactory);
    }

}
