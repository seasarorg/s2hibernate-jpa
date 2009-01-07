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
package org.seasar.hibernate.jpa.unit.join;

import java.math.BigDecimal;

import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.hibernate.jpa.unit.HibernateEntityReaderTestCase;

/**
 * 
 * @author taedium
 */
public class JoinTest extends HibernateEntityReaderTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        addAnnotatedClasses(Cat.class, Life.class, Dog.class);
    }

    public void testCompositePK() throws Exception {
        DogPk pk = new DogPk();
        pk.setName("hoge");
        pk.setOwnerName("foo");
        Dog dog = new Dog();
        dog.setId(pk);
        dog.setWeight(30);
        dog.setThoroughbredName("bar");
        persist(dog);

        DataSet dataSet = read(Dog.class, pk);
        assertEquals(2, dataSet.getTableSize());
        {
            DataTable table = dataSet.getTable(0);
            assertEqualsIgnoreCase("DogThoroughbred", table.getTableName());
            assertEquals(3, table.getColumnSize());
            assertEquals(1, table.getRowSize());
            DataRow row = table.getRow(0);
            assertEquals("hoge", row.getValue("dt_name"));
            assertEquals("foo", row.getValue("dt_ownerName"));
            assertEquals("bar", row.getValue("thoroughbredName"));
        }
        {
            DataTable table = dataSet.getTable(1);
            assertEqualsIgnoreCase("Dog", table.getTableName());
            assertEquals(3, table.getColumnSize());
            assertEquals(1, table.getRowSize());
            DataRow row = table.getRow(0);
            assertEquals("hoge", row.getValue("name"));
            assertEquals("foo", row.getValue("ownerName"));
            assertEquals(new BigDecimal(30), row.getValue("weight"));
        }
    }

    public void testManyToOne() throws Exception {
        Cat cat = new Cat();
        cat.setId(10);
        cat.setName("hoge");
        Life life = new Life();
        life.setId(20);
        life.setDuration(8);
        life.setFullDescription("foo");
        life.setOwner(cat);
        persist(cat, life);

        DataSet dataSet = read(Life.class, 20);
        assertEquals(2, dataSet.getTableSize());
        {
            DataTable table = dataSet.getTable(0);
            assertEqualsIgnoreCase("ExtendedLife", table.getTableName());
            assertEquals(3, table.getColumnSize());
            assertEquals(1, table.getRowSize());
            DataRow row = table.getRow(0);
            assertEquals(new BigDecimal(20), row.getValue("id"));
            assertEquals(new BigDecimal(10), row.getValue("owner_id"));
            assertEquals("foo", row.getValue("fullDescription"));
        }
        {
            DataTable table = dataSet.getTable(1);
            assertEqualsIgnoreCase("Life", table.getTableName());
            assertEquals(2, table.getColumnSize());
            assertEquals(1, table.getRowSize());
            DataRow row = table.getRow(0);
            assertEquals(new BigDecimal(20), row.getValue("id"));
            assertEquals(new BigDecimal(8), row.getValue("duration"));
        }
    }
}
