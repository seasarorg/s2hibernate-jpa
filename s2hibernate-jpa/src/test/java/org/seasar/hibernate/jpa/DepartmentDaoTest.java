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
package org.seasar.hibernate.jpa;

import org.seasar.extension.unit.S2TestCase;

public class DepartmentDaoTest extends S2TestCase {

    DepartmentDaoImpl dao;

    @Override
    protected void setUp() throws Exception {
        include("DepartmentDaoTest.dicon");
    }

    public void testTx() {
        assertNotNull(dao.em1);
        assertNotNull(dao.em2);
        assertNotNull(dao.em3);

        Department dept1 = dao.getDepartment1(10);
        assertEquals(10, dept1.getDeptno());

        Department dept2 = dao.getDepartment2(10);
        assertSame(dept1, dept2);

        Department dept3 = dao.getDepartment3(10);
        assertSame(dept1, dept3);
    }
}
