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
package org.seasar.hibernate.jpa.impl;

import javax.persistence.EntityManager;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.jpa.Dialect;
import org.seasar.hibernate.jpa.Department;

/**
 * 
 * @author koichik
 */
public class S2HibernateDialectTest extends S2TestCase {

    private EntityManager em;

    private Dialect dialect;

    @Override
    protected void setUp() throws Exception {
        include("javaee5.dicon");
        include("jpa.dicon");
    }

    public void testGetConnectionTx() throws Exception {
        assertNotNull(dialect.getConnection(em));
    }

    public void testDetachTx() throws Exception {
        Department dept1 = em.find(Department.class, 10);
        dialect.detach(em, dept1);
        assertFalse(em.contains(dept1));
        Department dept2 = em.find(Department.class, 10);
        assertNotSame(dept1, dept2);
    }

}
