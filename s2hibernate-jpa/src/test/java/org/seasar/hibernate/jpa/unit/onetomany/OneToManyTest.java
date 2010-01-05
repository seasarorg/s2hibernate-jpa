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
package org.seasar.hibernate.jpa.unit.onetomany;

import java.math.BigDecimal;

import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.hibernate.jpa.unit.HibernateEntityReaderTestCase;

/**
 * 
 * @author taedium
 */
public class OneToManyTest extends HibernateEntityReaderTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        addAnnotatedClasses(Employee.class, Department.class);
    }

    public void testOneToMany() throws Exception {
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

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase(table.getTableName(), "Department");
        assertEquals(2, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
        assertEquals("hoge", row.getValue("name"));
    }
}
