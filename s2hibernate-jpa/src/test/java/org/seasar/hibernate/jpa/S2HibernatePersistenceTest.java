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
package org.seasar.hibernate.jpa;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.hibernate.jpa.sub.Address;
import org.seasar.hibernate.jpa.sub.Department2;
import org.seasar.hibernate.jpa.sub.Employee2;

/**
 * 
 * @author taedium
 */
public class S2HibernatePersistenceTest extends S2TestCase {

    private EntityManager em;

    @Override
    protected void setUp() throws Exception {
        include("javaee5.dicon");
        include("s2hibernate-jpa.dicon");
    }

    public void testReadPersistenceXmlFile() throws Exception {
        final Session session = Session.class.cast(em.getDelegate());
        final SessionFactory sf = session.getSessionFactory();
        assertEquals(2, sf.getAllClassMetadata().size());
        assertNotNull(sf.getClassMetadata(Employee.class));
        assertNotNull(sf.getClassMetadata(Department.class));
    }

    public void setUpAddMappingFile() throws Exception {
        include("S2HibernatePersistenceTest.dicon");
    }

    public void testAddMappingFile() throws Exception {
        final Session session = Session.class.cast(em.getDelegate());
        final SessionFactory sf = session.getSessionFactory();
        assertEquals(3, sf.getAllClassMetadata().size());
        assertNotNull(sf.getClassMetadata(Employee.class));
        assertNotNull(sf.getClassMetadata(Department.class));
        assertNotNull(sf.getClassMetadata(Employee2.class));
    }

    public void setUpAddAnnotatedClass() throws Exception {
        include("S2HibernatePersistenceTest2.dicon");
    }

    public void testAddAnnotatedClass() throws Exception {
        final Session session = Session.class.cast(em.getDelegate());
        final SessionFactory sf = session.getSessionFactory();
        assertEquals(3, sf.getAllClassMetadata().size());
        assertNotNull(sf.getClassMetadata(Employee.class));
        assertNotNull(sf.getClassMetadata(Department.class));
        assertNotNull(sf.getClassMetadata(Department2.class));
    }

    public void setUpAddResourceAutoDetector() throws Exception {
        include("S2HibernatePersistenceTest3.dicon");
    }

    public void testAddResourceAutoDetector() throws Exception {
        final Session session = Session.class.cast(em.getDelegate());
        final SessionFactory sf = session.getSessionFactory();
        assertEquals(4, sf.getAllClassMetadata().size());
        assertNotNull(sf.getClassMetadata(Employee.class));
        assertNotNull(sf.getClassMetadata(Department.class));
        assertNotNull(sf.getClassMetadata(Employee2.class));
        assertNotNull(sf.getClassMetadata(Address.class));
    }
}
