/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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
package org.seasar.hibernate.jpa.unit.inheritance.singletable;

import java.math.BigDecimal;

import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.hibernate.jpa.unit.HibernateEntityReaderTestCase;

/**
 * 
 * @author taedium
 */
public class SingleTableTest extends HibernateEntityReaderTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        addAnnotatedClasses(Fruit.class, Apple.class, Trash.class,
                PaperTrash.class);
    }

    public void testDefault() throws Exception {
        Fruit fruit = new Fruit();
        fruit.setId(10);
        Apple apple = new Apple();
        apple.setId(20);
        persist(fruit, apple);
        {
            DataSet dataSet = read(Fruit.class, 10);
            assertEquals(1, dataSet.getTableSize());
            DataTable table = dataSet.getTable(0);
            assertEqualsIgnoreCase("Fruit", table.getTableName());
            assertEquals(2, table.getColumnSize());
            assertEquals(1, table.getRowSize());
            DataRow row = table.getRow(0);
            assertEquals(new BigDecimal(10), row.getValue("id"));
            assertEquals("Fruit", row.getValue("DTYPE"));
        }
        {
            DataSet dataSet = read(Fruit.class, 20);
            assertEquals(1, dataSet.getTableSize());
            DataTable table = dataSet.getTable(0);
            assertEqualsIgnoreCase("Fruit", table.getTableName());
            assertEquals(2, table.getColumnSize());
            assertEquals(1, table.getRowSize());
            DataRow row = table.getRow(0);
            assertEquals(new BigDecimal(20), row.getValue("id"));
            assertEquals("Apple", row.getValue("DTYPE"));
        }
    }

    public void testIntegerDiscriminator() throws Exception {
        PaperTrash paperTrash = new PaperTrash();
        paperTrash.setId(10);
        persist(paperTrash);

        DataSet dataSet = read(PaperTrash.class, 10);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase("Trash", table.getTableName());
        assertEquals(2, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
        assertEquals(new BigDecimal(4), row.getValue("DTYPE"));
    }

}
