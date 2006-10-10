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
