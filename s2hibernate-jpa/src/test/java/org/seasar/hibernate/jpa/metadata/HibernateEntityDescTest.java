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

import java.sql.Types;
import java.util.Date;
import java.util.Set;

import javax.persistence.TemporalType;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.jpa.AttributeDesc;
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
        Department dept = new Department();
        EntityDesc<Department> entityDesc = EntityDescFactory
                .getEntityDesc(Department.class);
        assertNotNull(entityDesc);
        assertEquals("Dept", entityDesc.getEntityName());
        assertEquals("deptno", entityDesc.getIdAttributeDesc().getName());
        String[] propNames = entityDesc.getAttributeNames();
        assertNotNull(propNames);
        assertEquals(6, propNames.length);
        assertEquals("deptno", propNames[0]);
        assertEquals("dname", propNames[1]);
        assertEquals("loc", propNames[2]);
        assertEquals("versionNo", propNames[3]);
        assertEquals("active", propNames[4]);
        assertEquals("employees", propNames[5]);

        AttributeDesc[] attributes = entityDesc.getAttributeDescs();

        AttributeDesc attribute = attributes[0];
        assertEquals("deptno", attribute.getName());
        assertEquals(Integer.class, attribute.getType());
        assertEquals(Types.INTEGER, attribute.getSqlType());
        assertNull(attribute.getTemporalType());
        assertTrue(attribute.isId());
        assertFalse(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertFalse(attribute.isVersion());
        attribute.setValue(dept, 100);
        assertEquals(100, dept.getDeptno());

        attribute = attributes[1];
        assertEquals("dname", attribute.getName());
        assertEquals(String.class, attribute.getType());
        assertEquals(Types.VARCHAR, attribute.getSqlType());
        assertNull(attribute.getTemporalType());
        assertFalse(attribute.isId());
        assertFalse(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertFalse(attribute.isVersion());

        attribute = attributes[2];
        assertEquals("loc", attribute.getName());
        assertEquals(String.class, attribute.getType());
        assertEquals(Types.VARCHAR, attribute.getSqlType());
        assertNull(attribute.getTemporalType());
        assertFalse(attribute.isId());
        assertFalse(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertFalse(attribute.isVersion());

        attribute = attributes[3];
        assertEquals("versionNo", attribute.getName());
        assertEquals(Integer.class, attribute.getType());
        assertEquals(Types.INTEGER, attribute.getSqlType());
        assertNull(attribute.getTemporalType());
        assertFalse(attribute.isId());
        assertFalse(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertTrue(attribute.isVersion());

        attribute = attributes[4];
        assertEquals("active", attribute.getName());
        assertEquals(Boolean.class, attribute.getType());
        assertEquals(Types.BIT, attribute.getSqlType());
        assertNull(attribute.getTemporalType());
        assertFalse(attribute.isId());
        assertFalse(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertFalse(attribute.isVersion());

        attribute = attributes[5];
        assertEquals("employees", attribute.getName());
        assertEquals(Set.class, attribute.getType());
        assertEquals(Employee.class, attribute.getElementType());
        assertEquals(Types.OTHER, attribute.getSqlType());
        assertNull(attribute.getTemporalType());
        assertFalse(attribute.isId());
        assertTrue(attribute.isAssociation());
        assertTrue(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertFalse(attribute.isVersion());
    }

    public void testEmployee() throws Exception {
        EntityDesc<Employee> entityDesc = EntityDescFactory
                .getEntityDesc(Employee.class);
        assertNotNull(entityDesc);
        assertEquals("Emp", entityDesc.getEntityName());
        assertEquals("empno", entityDesc.getIdAttributeDesc().getName());
        String[] propNames = entityDesc.getAttributeNames();
        assertNotNull(propNames);
        assertEquals(9, propNames.length);
        assertEquals("empno", propNames[0]);
        assertEquals("ename", propNames[1]);
        assertEquals("job", propNames[2]);
        assertEquals("mgr", propNames[3]);
        assertEquals("hiredate", propNames[4]);
        assertEquals("sal", propNames[5]);
        assertEquals("comm", propNames[6]);
        assertEquals("tstamp", propNames[7]);
        assertEquals("department", propNames[8]);

        AttributeDesc[] attributes = entityDesc.getAttributeDescs();

        AttributeDesc attribute = attributes[0];
        assertEquals("empno", attribute.getName());
        assertEquals(Long.class, attribute.getType());
        assertEquals(Types.BIGINT, attribute.getSqlType());
        assertNull(attribute.getTemporalType());
        assertTrue(attribute.isId());
        assertFalse(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertFalse(attribute.isVersion());

        attribute = attributes[1];
        assertEquals("ename", attribute.getName());
        assertEquals(String.class, attribute.getType());
        assertEquals(Types.VARCHAR, attribute.getSqlType());
        assertNull(attribute.getTemporalType());
        assertFalse(attribute.isId());
        assertFalse(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertFalse(attribute.isVersion());

        attribute = attributes[2];
        assertEquals("job", attribute.getName());
        assertEquals(String.class, attribute.getType());
        assertEquals(Types.VARCHAR, attribute.getSqlType());
        assertNull(attribute.getTemporalType());
        assertFalse(attribute.isId());
        assertFalse(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertFalse(attribute.isVersion());

        attribute = attributes[3];
        assertEquals("mgr", attribute.getName());
        assertEquals(Short.class, attribute.getType());
        assertEquals(Types.SMALLINT, attribute.getSqlType());
        assertNull(attribute.getTemporalType());
        assertFalse(attribute.isId());
        assertFalse(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertFalse(attribute.isVersion());

        attribute = attributes[4];
        assertEquals("hiredate", attribute.getName());
        assertEquals(Date.class, attribute.getType());
        assertEquals(Types.DATE, attribute.getSqlType());
        assertEquals(TemporalType.DATE, attribute.getTemporalType());
        assertFalse(attribute.isId());
        assertFalse(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertFalse(attribute.isVersion());

        attribute = attributes[5];
        assertEquals("sal", attribute.getName());
        assertEquals(Float.class, attribute.getType());
        assertEquals(Types.FLOAT, attribute.getSqlType());
        assertNull(attribute.getTemporalType());
        assertFalse(attribute.isId());
        assertFalse(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertFalse(attribute.isVersion());

        attribute = attributes[6];
        assertEquals("comm", attribute.getName());
        assertEquals(Float.class, attribute.getType());
        assertEquals(Types.FLOAT, attribute.getSqlType());
        assertNull(attribute.getTemporalType());
        assertFalse(attribute.isId());
        assertFalse(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertFalse(attribute.isVersion());

        attribute = attributes[7];
        assertEquals("tstamp", attribute.getName());
        assertEquals(Date.class, attribute.getType());
        assertEquals(Types.TIMESTAMP, attribute.getSqlType());
        assertEquals(TemporalType.TIMESTAMP, attribute.getTemporalType());
        assertFalse(attribute.isId());
        assertFalse(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertFalse(attribute.isVersion());

        attribute = attributes[8];
        assertEquals("department", attribute.getName());
        assertEquals(Department.class, attribute.getType());
        assertEquals(Types.OTHER, attribute.getSqlType());
        assertNull(attribute.getTemporalType());
        assertFalse(attribute.isId());
        assertTrue(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertFalse(attribute.isVersion());
    }

}
