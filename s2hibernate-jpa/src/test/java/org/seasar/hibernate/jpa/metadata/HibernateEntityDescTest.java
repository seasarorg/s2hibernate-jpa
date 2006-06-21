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

import java.util.Date;
import java.util.Set;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.jpa.EntityDesc;
import org.seasar.framework.jpa.EntityDescFactory;
import org.seasar.hibernate.jpa.Department;
import org.seasar.hibernate.jpa.Employee;

/**
 * 
 * @author koichik
 */
public class HibernateEntityDescTest extends S2TestCase {

    @Override
    protected void setUp() throws Exception {
        include("j2ee.dicon");
        include("s2hibernate-jpa.dicon");
    }

    public void testDepartment() throws Exception {
        EntityDesc<Department> entityDesc = EntityDescFactory
                .getEntityDesc(Department.class);
        assertNotNull(entityDesc);
        assertEquals("Dept", entityDesc.getName());
        assertEquals("deptno", entityDesc.getIdPropertyName());
        String[] propNames = entityDesc.getPropertyNames();
        assertNotNull(propNames);
        assertEquals(5, propNames.length);
        assertEquals("dname", propNames[0]);
        assertEquals("loc", propNames[1]);
        assertEquals("versionNo", propNames[2]);
        assertEquals("active", propNames[3]);
        assertEquals("employees", propNames[4]);

        assertEquals(Integer.class, entityDesc.getPropertyClass("deptno"));
        assertEquals(String.class, entityDesc.getPropertyClass("dname"));
        assertEquals(String.class, entityDesc.getPropertyClass("loc"));
        assertEquals(Integer.class, entityDesc.getPropertyClass("versionNo"));
        assertEquals(Boolean.class, entityDesc.getPropertyClass("active"));
        assertEquals(Set.class, entityDesc.getPropertyClass("employees"));

        assertFalse(entityDesc.isAssociationProperty("deptno"));
        assertFalse(entityDesc.isAssociationProperty("dname"));
        assertFalse(entityDesc.isAssociationProperty("loc"));
        assertFalse(entityDesc.isAssociationProperty("versionNo"));
        assertFalse(entityDesc.isAssociationProperty("active"));
        assertTrue(entityDesc.isAssociationProperty("employees"));

        assertFalse(entityDesc.isCollectionProperty("deptno"));
        assertFalse(entityDesc.isCollectionProperty("dname"));
        assertFalse(entityDesc.isCollectionProperty("loc"));
        assertFalse(entityDesc.isCollectionProperty("versionNo"));
        assertFalse(entityDesc.isCollectionProperty("active"));
        assertTrue(entityDesc.isCollectionProperty("employees"));

        Department dept = new Department();
        entityDesc.setPropertyValue(dept, "dname", "Hoge");
        assertEquals("Hoge", entityDesc.getPropertyValue(dept, "dname"));
    }

    public void testEmployee() throws Exception {
        EntityDesc<Employee> entityDesc = EntityDescFactory
                .getEntityDesc(Employee.class);
        assertNotNull(entityDesc);
        assertEquals("Emp", entityDesc.getName());
        assertEquals("empno", entityDesc.getIdPropertyName());
        String[] propNames = entityDesc.getPropertyNames();
        assertNotNull(propNames);
        assertEquals(8, propNames.length);
        assertEquals("ename", propNames[0]);
        assertEquals("job", propNames[1]);
        assertEquals("mgr", propNames[2]);
        assertEquals("hiredate", propNames[3]);
        assertEquals("sal", propNames[4]);
        assertEquals("comm", propNames[5]);
        assertEquals("tstamp", propNames[6]);
        assertEquals("department", propNames[7]);

        assertEquals(Long.class, entityDesc.getPropertyClass("empno"));
        assertEquals(String.class, entityDesc.getPropertyClass("ename"));
        assertEquals(String.class, entityDesc.getPropertyClass("job"));
        assertEquals(Short.class, entityDesc.getPropertyClass("mgr"));
        assertEquals(Date.class, entityDesc.getPropertyClass("hiredate"));
        assertEquals(Float.class, entityDesc.getPropertyClass("sal"));
        assertEquals(Float.class, entityDesc.getPropertyClass("comm"));
        assertEquals(Date.class, entityDesc.getPropertyClass("tstamp"));
        assertEquals(Department.class, entityDesc
                .getPropertyClass("department"));

        assertFalse(entityDesc.isAssociationProperty("empno"));
        assertFalse(entityDesc.isAssociationProperty("ename"));
        assertFalse(entityDesc.isAssociationProperty("job"));
        assertFalse(entityDesc.isAssociationProperty("mgr"));
        assertFalse(entityDesc.isAssociationProperty("hiredate"));
        assertFalse(entityDesc.isAssociationProperty("sal"));
        assertFalse(entityDesc.isAssociationProperty("comm"));
        assertFalse(entityDesc.isAssociationProperty("tstamp"));
        assertTrue(entityDesc.isAssociationProperty("department"));

        assertFalse(entityDesc.isCollectionProperty("empno"));
        assertFalse(entityDesc.isCollectionProperty("ename"));
        assertFalse(entityDesc.isCollectionProperty("job"));
        assertFalse(entityDesc.isCollectionProperty("mgr"));
        assertFalse(entityDesc.isCollectionProperty("hiredate"));
        assertFalse(entityDesc.isCollectionProperty("sal"));
        assertFalse(entityDesc.isCollectionProperty("comm"));
        assertFalse(entityDesc.isCollectionProperty("tstamp"));
        assertFalse(entityDesc.isCollectionProperty("department"));
    }

}
