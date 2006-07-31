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
import javax.transaction.UserTransaction;

import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.jpa.unit.EntityReader;
import org.seasar.framework.jpa.unit.EntityReaderFactory;
import org.seasar.hibernate.jpa.S2HibernateConfiguration;

/**
 * 
 * @author taedium
 */
public abstract class EntityReaderTestCase extends S2TestCase {

    protected S2HibernateConfiguration cfg = new S2HibernateConfiguration();

    protected EntityManager em;

    protected UserTransaction utx;

    @Override
    protected void setUp() throws Exception {
        include("s2hibernate-jpa.dicon");
    }

    protected void persist(final Object... entities) throws Exception {
        utx.begin();
        for (final Object entity : entities) {
            em.persist(entity);
        }
        utx.commit();
    }

    protected <T> DataSet read(final Class<T> entityClass, final Object id)
            throws Exception {
        utx.begin();
        T entity = em.find(entityClass, id);
        assertNotNull(entity);
        EntityReader reader = EntityReaderFactory.getEntityReader(entity);
        System.out.println(reader.getClass().getName());
        utx.commit();
        return reader.read();
    }

}
