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
package org.seasar.hibernate.jpa.unit.manytoone;

import java.math.BigDecimal;

import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.hibernate.jpa.unit.EntityReaderTestCase;

/**
 * 
 * @author taedium
 */
public class ManyToOneTest extends EntityReaderTestCase {

    public void setUpManyToOneOwningSide() throws Exception {
        cfg.addAnnotatedClasses(Employee.class, Department.class);
        register(cfg);
    }

    public void testManyToOneOwningSide() throws Exception {
        Department department = new Department();
        department.setId(10);
        department.setName("hoge");
        Employee employee = new Employee();
        employee.setId(1);
        employee.setName("foo");
        employee.setDepartment(department);
        department.getEmployees().add(employee);
        persist(department, employee);

        DataSet dataSet = read(Employee.class, 1);
        assertEquals(1, dataSet.getTableSize());
        assertTrue(dataSet.hasTable("Employee"));

        DataTable table = dataSet.getTable(0);
        assertEquals(3, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(1), row.getValue("id"));
        assertEquals("foo", row.getValue("name"));
        assertEquals(new BigDecimal(10), row.getValue("department_id"));
    }

    public void setUpManyToOneInverseSide() throws Exception {
        cfg.addAnnotatedClasses(Employee.class, Department.class);
        register(cfg);
    }

    public void testManyToOneInverseSide() throws Exception {
        Department department = new Department();
        department.setId(10);
        department.setName("hoge");
        Employee employee = new Employee();
        employee.setId(1);
        employee.setName("foo");
        employee.setDepartment(department);
        department.getEmployees().add(employee);
        persist(department, employee);

        DataSet dataSet = read(Department.class, 10);
        assertEquals(1, dataSet.getTableSize());
        assertTrue(dataSet.hasTable("Department"));

        DataTable table = dataSet.getTable(0);
        assertEquals(2, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
        assertEquals("hoge", row.getValue("name"));
    }

    public void setUpManyToOneCompositeFk() throws Exception {
        cfg.addAnnotatedClasses(Parent.class, Child.class);
        register(cfg);
    }

    public void testManyToOneCompositeFk() throws Exception {
        ParentPk parentPk = new ParentPk();
        parentPk.setFirstName("hoge");
        parentPk.setLastName("foo");
        Parent parent = new Parent();
        parent.setId(parentPk);
        parent.setAge(50);
        Child child = new Child();
        child.setId(10);
        child.setParent(parent);
        persist(parent, child);

        DataSet dataSet = read(Child.class, 10);
        assertEquals(1, dataSet.getTableSize());
        assertTrue(dataSet.hasTable("Child"));

        DataTable table = dataSet.getTable(0);
        assertEquals(3, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
        assertEquals("hoge", row.getValue("parentFirstName"));
        assertEquals("foo", row.getValue("parentLastName"));
    }

    public void setUpManyToOneNonPk() throws Exception {
        cfg.addAnnotatedClasses(Order.class, OrderLine.class);
        register(cfg);
    }

    public void testManyToOneNonPk() throws Exception {
        Order order = new Order();
        order.setId(10);
        order.setOrderNo("hoge");
        OrderLine orderLine = new OrderLine();
        orderLine.setId(20);
        orderLine.setOrder(order);
        persist(order, orderLine);

        DataSet dataSet = read(OrderLine.class, 20);
        assertEquals(1, dataSet.getTableSize());
        assertTrue(dataSet.hasTable("OrderLine"));

        DataTable table = dataSet.getTable(0);
        assertEquals(2, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(20), row.getValue("id"));
        assertEquals("hoge", row.getValue("orderNo"));
    }
}
