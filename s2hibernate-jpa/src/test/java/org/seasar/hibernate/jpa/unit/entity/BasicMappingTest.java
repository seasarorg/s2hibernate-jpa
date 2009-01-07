/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.hibernate.jpa.unit.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.hibernate.jpa.unit.HibernateEntityReaderTestCase;

/**
 * 
 * @author taedium
 */
public class BasicMappingTest extends HibernateEntityReaderTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        addAnnotatedClasses(WashingMachine.class, Employee.class, Person.class,
                Datetime.class);
    }

    public void testTransient() throws Exception {
        WashingMachine machine = new WashingMachine();
        machine.setId(10);
        machine.setName("hoge");
        machine.setActive(true);
        persist(machine);

        DataSet dataSet = read(WashingMachine.class, 10);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase("WashingMachine", table.getTableName());
        assertEquals(1, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
    }

    public void testVersion() throws Exception {
        Employee employee = new Employee();
        employee.setId(10);
        persist(employee);

        DataSet dataSet = read(Employee.class, 10);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase("Employee", table.getTableName());
        assertEquals(2, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
        assertNotNull(row.getValue("versionNo"));
    }

    public void testEnum() throws Exception {
        Person person = new Person();
        person.setId(10);
        person.setCharacter1(Character.AGGRESSIVE);
        person.setCharacter2(Character.GENTLE);
        persist(person);

        DataSet dataSet = read(Person.class, 10);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase("Person", table.getTableName());
        assertEquals(3, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
        assertEquals(new BigDecimal(Character.AGGRESSIVE.ordinal()), row
                .getValue("character1"));
        assertEquals(Character.GENTLE.toString(), row.getValue("character2"));
    }

    public void testTemporal() throws Exception {
        Timestamp timestamp = Timestamp.valueOf("1978-10-07 15:16:17");
        Datetime datetime = new Datetime();
        datetime.setId(10);
        datetime.setDate(timestamp);
        datetime.setTime(timestamp);
        datetime.setTimestamp(timestamp);
        persist(datetime);

        DataSet dataSet = read(Datetime.class, 10);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase("Datetime", table.getTableName());
        assertEquals(4, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
        assertEquals(Timestamp.valueOf("1978-10-07 00:00:00"), row
                .getValue("date"));
        assertEquals(Timestamp.valueOf("1970-01-01 15:16:17"), row
                .getValue("time"));
        assertEquals(Timestamp.valueOf("1978-10-07 15:16:17"), row
                .getValue("timestamp"));
    }
}
