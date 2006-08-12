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
package org.seasar.hibernate.jpa.unit.inheritance.singletable;

import java.math.BigDecimal;

import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.hibernate.jpa.unit.HibernateEntityCollectionReaderTestCase;

/**
 * 
 * @author taedium
 */
public class SingleTableCollectionTest extends
        HibernateEntityCollectionReaderTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        addAnnotatedClasses(Fruit.class, Apple.class, Banana.class);
    }

    public void testDefault() throws Exception {
        Fruit fruit = new Fruit();
        fruit.setId(10);
        Apple apple = new Apple();
        apple.setId(20);
        Banana banana = new Banana();
        banana.setId(30);
        persist(fruit, apple, banana);

        DataSet dataSet = read("select f from Fruit f");
        assertEquals(1, dataSet.getTableSize());
        DataTable table = dataSet.getTable("Fruit");
        assertEquals(2, table.getColumnSize());
        assertEquals(3, table.getRowSize());
        {
            DataRow row = table.getRow(0);
            assertEquals(new BigDecimal(10), row.getValue("id"));
            assertEquals("Fruit", row.getValue("DTYPE"));
        }
        {
            DataRow row = table.getRow(1);
            assertEquals(new BigDecimal(20), row.getValue("id"));
            assertEquals("Apple", row.getValue("DTYPE"));
        }
        {
            DataRow row = table.getRow(2);
            assertEquals(new BigDecimal(30), row.getValue("id"));
            assertEquals("Banana", row.getValue("DTYPE"));
        }
    }
}
