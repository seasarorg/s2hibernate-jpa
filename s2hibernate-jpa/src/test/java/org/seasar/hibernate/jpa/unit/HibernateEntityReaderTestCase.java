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
package org.seasar.hibernate.jpa.unit;

import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.jpa.PersistenceUnitConfiguration;
import org.seasar.framework.jpa.unit.EntityReader;
import org.seasar.framework.jpa.unit.EntityReaderFactory;

/**
 * @author taedium
 */
public abstract class HibernateEntityReaderTestCase extends S2TestCase {

    protected PersistenceUnitConfiguration cfg;

    protected EntityManager em;

    protected UserTransaction utx;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        include("jpa.dicon");
        cfg = PersistenceUnitConfiguration.class
                .cast(getComponent(PersistenceUnitConfiguration.class));
    }

    protected void addAnnotatedClasses(Class<?>... classes) {
        for (final Class<?> clazz : classes) {
            cfg.addPersistenceClass("persistenceUnit", clazz);
        }
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
        utx.commit();
        return reader.read();
    }

    protected void assertEqualsIgnoreCase(final String expected,
            final String actual) {

        assertEqualsIgnoreCase(null, expected, actual);
    }

    protected void assertEqualsIgnoreCase(final String message,
            final String expected, final String actual) {
        String lowerExpected = null;
        if (expected != null) {
            lowerExpected = expected.toLowerCase();
        }
        String lowerActual = null;
        if (actual != null) {
            lowerActual = actual.toLowerCase();
        }
        assertEquals(message, lowerExpected, lowerActual);
    }
}
