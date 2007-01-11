/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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
import org.seasar.hibernate.jpa.unit.HibernateEntityReaderTestCase;

/**
 * 
 * @author taedium
 */
public class ManyToOneTest extends HibernateEntityReaderTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        addAnnotatedClasses(Employee.class, Department.class, Parent.class,
                Child.class, Order.class, OrderLine.class, Node.class);
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

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase(table.getTableName(), "Employee");
        assertEquals(3, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(1), row.getValue("id"));
        assertEquals("foo", row.getValue("name"));
        assertEquals(new BigDecimal(10), row.getValue("department_id"));
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

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase(table.getTableName(), "Child");
        assertEquals(3, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
        assertEquals("hoge", row.getValue("parentFirstName"));
        assertEquals("foo", row.getValue("parentLastName"));
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

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase(table.getTableName(), "OrderLine");
        assertEquals(2, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(20), row.getValue("id"));
        assertEquals("hoge", row.getValue("orderNo"));
    }

    public void testSelf() throws Exception {
        Node parent = new Node();
        parent.setId(10);
        parent.setName("parent");
        Node child = new Node();
        child.setId(20);
        child.setName("child");
        child.setParent(parent);
        persist(parent, child);

        DataSet dataSet = read(Node.class, 20);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase(table.getTableName(), "Node");
        assertEquals(3, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(20), row.getValue("id"));
        assertEquals("child", row.getValue("name"));
        assertEquals(new BigDecimal(10), row.getValue("parent_id"));
    }
}
