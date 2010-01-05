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
package org.seasar.hibernate.jpa.unit.manytoone;

import java.math.BigDecimal;
import java.util.Collection;

import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.framework.jpa.unit.EntityReader;
import org.seasar.framework.jpa.unit.EntityReaderFactory;
import org.seasar.hibernate.jpa.unit.HibernateEntityCollectionReaderTestCase;

/**
 * 
 * @author taedium
 */
public class ManyToOneCollectionTest extends
        HibernateEntityCollectionReaderTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        addAnnotatedClasses(Department.class, Employee.class);
    }

    public void testManyToOneOwningSide() throws Exception {
        utx.begin();
        Department department = new Department();
        department.setId(10);
        department.setName("hoge");
        em.persist(department);
        {
            Employee employee = new Employee();
            employee.setId(1);
            employee.setName("foo");
            employee.setDepartment(department);
            department.getEmployees().add(employee);
            em.persist(employee);
        }
        {
            Employee employee = new Employee();
            employee.setId(2);
            employee.setName("foo2");
            employee.setDepartment(department);
            department.getEmployees().add(employee);
            em.persist(employee);
        }
        utx.commit();

        utx.begin();
        Collection<Employee> employees = em.find(Department.class, 10)
                .getEmployees();
        EntityReader reader = EntityReaderFactory.getEntityReader(employees);
        DataSet dataSet = reader.read();
        utx.commit();

        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertTrue(table.getTableName().equalsIgnoreCase("Employee"));
        assertEquals(3, table.getColumnSize());
        assertEquals(2, table.getRowSize());

        {
            DataRow row = table.getRow(0);
            assertEquals(new BigDecimal(1), row.getValue("id"));
            assertEquals("foo", row.getValue("name"));
            assertEquals(new BigDecimal(10), row.getValue("department_id"));
        }
        {
            DataRow row = table.getRow(1);
            assertEquals(new BigDecimal(2), row.getValue("id"));
            assertEquals("foo2", row.getValue("name"));
            assertEquals(new BigDecimal(10), row.getValue("department_id"));
        }
    }

    public void testManyToOneInverseSide() throws Exception {
        utx.begin();
        {
            Department department = new Department();
            department.setId(10);
            department.setName("hoge");
            Employee employee = new Employee();
            employee.setId(1);
            employee.setName("foo");
            employee.setDepartment(department);
            department.getEmployees().add(employee);
            em.persist(department);
            em.persist(employee);
        }
        {
            Department department = new Department();
            department.setId(20);
            department.setName("hoge2");
            Employee employee = new Employee();
            employee.setId(2);
            employee.setName("foo2");
            employee.setDepartment(department);
            department.getEmployees().add(employee);
            em.persist(department);
            em.persist(employee);
        }
        utx.commit();

        DataSet dataSet = read("select d from Department d order by id");

        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertTrue(table.getTableName().equalsIgnoreCase("Department"));
        assertEquals(2, table.getColumnSize());
        assertEquals(2, table.getRowSize());

        {
            DataRow row = table.getRow(0);
            assertEquals(new BigDecimal(10), row.getValue("id"));
            assertEquals("hoge", row.getValue("name"));
        }
        {
            DataRow row = table.getRow(1);
            assertEquals(new BigDecimal(20), row.getValue("id"));
            assertEquals("hoge2", row.getValue("name"));
        }
    }

    public void testCrossJoinResultList() throws Exception {
        utx.begin();
        {
            Department department = new Department();
            department.setId(10);
            department.setName("hoge");
            Employee employee = new Employee();
            employee.setId(1);
            employee.setName("foo");
            employee.setDepartment(department);
            department.getEmployees().add(employee);
            em.persist(department);
            em.persist(employee);
        }
        {
            Department department = new Department();
            department.setId(20);
            department.setName("hoge2");
            Employee employee = new Employee();
            employee.setId(2);
            employee.setName("foo2");
            employee.setDepartment(department);
            department.getEmployees().add(employee);
            em.persist(department);
            em.persist(employee);
        }
        utx.commit();

        DataSet dataSet = read("select e, d from Employee e, Department d order by e.id");
        assertEquals(2, dataSet.getTableSize());
        {
            DataTable table = dataSet.getTable("Employee");
            assertEquals(3, table.getColumnSize());
            assertEquals(4, table.getRowSize());
            DataRow row = table.getRow(0);
            assertEquals(new BigDecimal(1), row.getValue("id"));
            assertEquals("foo", row.getValue("name"));
            assertEquals(new BigDecimal(10), row.getValue("department_id"));
            DataRow row2 = table.getRow(1);
            assertEquals(new BigDecimal(1), row2.getValue("id"));
            assertEquals("foo", row2.getValue("name"));
            assertEquals(new BigDecimal(10), row2.getValue("department_id"));
            DataRow row3 = table.getRow(2);
            assertEquals(new BigDecimal(2), row3.getValue("id"));
            assertEquals("foo2", row3.getValue("name"));
            assertEquals(new BigDecimal(20), row3.getValue("department_id"));
            DataRow row4 = table.getRow(3);
            assertEquals(new BigDecimal(2), row4.getValue("id"));
            assertEquals("foo2", row4.getValue("name"));
            assertEquals(new BigDecimal(20), row4.getValue("department_id"));
        }
        {
            DataTable table = dataSet.getTable("Department");
            assertEquals(2, table.getColumnSize());
            assertEquals(4, table.getRowSize());
            DataRow row = table.getRow(0);
            assertEquals(new BigDecimal(10), row.getValue("id"));
            assertEquals("hoge", row.getValue("name"));
            DataRow row2 = table.getRow(1);
            assertEquals(new BigDecimal(20), row2.getValue("id"));
            assertEquals("hoge2", row2.getValue("name"));
            DataRow row3 = table.getRow(2);
            assertEquals(new BigDecimal(10), row3.getValue("id"));
            assertEquals("hoge", row3.getValue("name"));
            DataRow row4 = table.getRow(3);
            assertEquals(new BigDecimal(20), row4.getValue("id"));
            assertEquals("hoge2", row4.getValue("name"));
        }
    }
}
