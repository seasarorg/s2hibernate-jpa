/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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
import javax.persistence.EntityManagerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.convention.impl.NamingConventionImpl;
import org.seasar.framework.jpa.PersistenceUnitConfiguration;
import org.seasar.framework.jpa.PersistenceUnitManager;
import org.seasar.framework.jpa.autodetector.MappingFileAutoDetector;
import org.seasar.framework.jpa.autodetector.PersistenceClassAutoDetector;
import org.seasar.hibernate.jpa.entity.Address;
import org.seasar.hibernate.jpa.entity.Customer;
import org.seasar.hibernate.jpa.entity.Department2;
import org.seasar.hibernate.jpa.entity.Employee2;
import org.seasar.hibernate.jpa.entity.Project;
import org.seasar.hibernate.jpa.entity.aaa.Dummy;
import org.seasar.hibernate.jpa.entity.aaa.Dummy2;

/**
 * @author taedium
 */
public class PersistenceUnitConfigurationTest extends S2TestCase {

    private EntityManager em;

    private PersistenceUnitConfiguration configuration;

    private NamingConventionImpl convention;

    @Override
    protected void setUp() throws Exception {
        include("javaee5.dicon");
        include("jpa.dicon");
        configuration = PersistenceUnitConfiguration.class
                .cast(getComponent(PersistenceUnitConfiguration.class));
        convention = new NamingConventionImpl();
        convention.addRootPackageName("org.seasar.hibernate.jpa");
    }

    public void testReadPersistenceXmlFile() throws Exception {
        final Session session = Session.class.cast(em.getDelegate());
        final SessionFactory sf = session.getSessionFactory();
        assertEquals(2, sf.getAllClassMetadata().size());
        assertNotNull(sf.getClassMetadata(Employee.class));
        assertNotNull(sf.getClassMetadata(Department.class));
    }

    public void setUpAddMappingFile() throws Exception {
        configuration
                .addMappingFile("org/seasar/hibernate/jpa/entity/hogeOrm.xml");
    }

    public void testAddMappingFile() throws Exception {
        final Session session = Session.class.cast(em.getDelegate());
        final SessionFactory sf = session.getSessionFactory();
        assertEquals(3, sf.getAllClassMetadata().size());
        assertNotNull(sf.getClassMetadata(Employee.class));
        assertNotNull(sf.getClassMetadata(Department.class));
        assertNotNull(sf.getClassMetadata(Employee2.class));
    }

    public void setUpAddPersistenceClass() throws Exception {
        configuration.addPersistenceClass(Department2.class);
    }

    public void testAddPersistenceClass() throws Exception {
        final Session session = Session.class.cast(em.getDelegate());
        final SessionFactory sf = session.getSessionFactory();
        assertEquals(3, sf.getAllClassMetadata().size());
        assertNotNull(sf.getClassMetadata(Employee.class));
        assertNotNull(sf.getClassMetadata(Department.class));
        assertNotNull(sf.getClassMetadata(Department2.class));
    }

    public void setUpMappingFileAutoDetection() throws Exception {
        MappingFileAutoDetector detector = new MappingFileAutoDetector();
        detector.setNamingConvention(convention);
        detector.init();
        configuration.addMappingFileAutoDetector(detector);
    }

    public void testMappingFileAutoDetection() throws Exception {
        final Session session = Session.class.cast(em.getDelegate());
        final SessionFactory sf = session.getSessionFactory();
        assertEquals(5, sf.getAllClassMetadata().size());
        assertNotNull(sf.getClassMetadata(Employee.class));
        assertNotNull(sf.getClassMetadata(Department.class));
        assertNotNull(sf.getClassMetadata(Employee2.class));
        assertNotNull(sf.getClassMetadata(Address.class));
        assertNotNull(sf.getClassMetadata(Project.class));
    }

    public void setUpMappingFileAutoDetectionSubPackage() throws Exception {
        include("jpa-aaa.dicon");
        MappingFileAutoDetector detector = new MappingFileAutoDetector();
        detector.setNamingConvention(convention);
        detector.init();
        configuration.addMappingFileAutoDetector(detector);
    }

    public void testMappingFileAutoDetectionSubPackage() throws Exception {
        final PersistenceUnitManager pum = PersistenceUnitManager.class
                .cast(getComponent(PersistenceUnitManager.class));
        final EntityManagerFactory emf = pum
                .getEntityManagerFactory("aaaPersistenceUnit");
        final EntityManager em = emf.createEntityManager();
        final Session session = Session.class.cast(em.getDelegate());
        final SessionFactory sf = session.getSessionFactory();
        assertEquals(3, sf.getAllClassMetadata().size());
        assertNotNull(sf.getClassMetadata(Dummy2.class));
    }

    public void setUpPersistenceClassAutoDetection() throws Exception {
        PersistenceClassAutoDetector detector = new PersistenceClassAutoDetector();
        detector.setNamingConvention(convention);
        detector.init();
        configuration.addPersistenceClassAutoDetector(detector);
    }

    public void testPersistenceClassAutoDetection() throws Exception {
        final Session session = Session.class.cast(em.getDelegate());
        final SessionFactory sf = session.getSessionFactory();
        assertEquals(4, sf.getAllClassMetadata().size());
        assertNotNull(sf.getClassMetadata(Employee.class));
        assertNotNull(sf.getClassMetadata(Department.class));
        assertNotNull(sf.getClassMetadata(Customer.class));
        assertNotNull(sf.getClassMetadata(Department2.class));
    }

    public void setUpPersistenceClassAutoDetectionSubPackage() throws Exception {
        include("jpa-aaa.dicon");
        PersistenceClassAutoDetector detector = new PersistenceClassAutoDetector();
        detector.setNamingConvention(convention);
        detector.init();
        configuration.addPersistenceClassAutoDetector(detector);
    }

    public void testPersistenceClassAutoDetectionSubPackage() throws Exception {
        final PersistenceUnitManager pum = PersistenceUnitManager.class
                .cast(getComponent(PersistenceUnitManager.class));
        final EntityManagerFactory emf = pum
                .getEntityManagerFactory("aaaPersistenceUnit");
        final EntityManager em = emf.createEntityManager();
        final Session session = Session.class.cast(em.getDelegate());
        final SessionFactory sf = session.getSessionFactory();
        assertEquals(3, sf.getAllClassMetadata().size());
        assertNotNull(sf.getClassMetadata(Dummy.class));
    }

    public void setUpPackageInfoAutoDetectionTx() throws Exception {
        PersistenceClassAutoDetector detector = new PersistenceClassAutoDetector();
        detector.setNamingConvention(convention);
        detector.init();
        configuration.addPersistenceClassAutoDetector(detector);
    }

    public void testPackageInfoAutoDetectionTx() throws Exception {
        final Session session = Session.class.cast(em.getDelegate());
        session.enableFilter("id_eq");
    }

}
