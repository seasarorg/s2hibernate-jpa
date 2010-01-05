/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
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

import javax.persistence.EntityManagerFactory;

import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.seasar.framework.jpa.metadata.EntityDesc;
import org.seasar.framework.jpa.metadata.EntityDescProvider;

/**
 * Hibernate用の{@link EntityDesc}を提供するコンポーネントの実装クラスです。
 * 
 * @author koichik
 */
public class HibernateEntityDescProvider implements EntityDescProvider {

    /**
     * インスタンスを構築します。
     */
    public HibernateEntityDescProvider() {
    }

    @SuppressWarnings("unchecked")
    public HibernateEntityDesc createEntityDesc(final EntityManagerFactory emf,
            final Class<?> entityClass) {
        final HibernateEntityManagerFactory hemf = HibernateEntityManagerFactory.class
                .cast(emf);
        final SessionFactoryImplementor sessionFactory = SessionFactoryImplementor.class
                .cast(hemf.getSessionFactory());
        final ClassMetadata metadata = sessionFactory
                .getClassMetadata(entityClass);
        if (metadata == null) {
            return null;
        }
        return new HibernateEntityDesc(entityClass, sessionFactory);
    }

}
