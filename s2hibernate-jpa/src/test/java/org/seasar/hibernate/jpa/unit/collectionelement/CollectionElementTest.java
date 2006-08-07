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
package org.seasar.hibernate.jpa.unit.collectionelement;

import java.math.BigDecimal;

import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.hibernate.jpa.unit.HibernateEntityReaderTestCase;

/**
 * 
 * @author taedium
 */
public class CollectionElementTest extends HibernateEntityReaderTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        addAnnotatedClasses(Boy.class, Country.class);
    }

    public void testSimpleElement() throws Exception {
        Boy boy = new Boy();
        boy.setId(10);
        boy.setFirstName("hoge");
        boy.setLastName("foo");
        boy.getNickNames().add("hogehoge");
        boy.getNickNames().add("foofoo");
        boy.getScorePerNickName().put("hoge", 100);
        boy.getScorePerNickName().put("foo", 200);
        int[] favoriteNumbers = new int[4];
        for (int i = 0; i < favoriteNumbers.length - 1; i++) {
            favoriteNumbers[i] = i * 3;
        }
        boy.setFavoriteNumbers(favoriteNumbers);
        boy.getCharacters().add(Character.GENTLE);
        boy.getCharacters().add(Character.AGGERSSIVE);
        persist(boy);

        read(Boy.class, 10);
        DataSet dataSet = read(Boy.class, 10);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase("Boy", table.getTableName());
        assertEquals(3, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
        assertEquals("hoge", row.getValue("firstName"));
        assertEquals("foo", row.getValue("lastName"));
    }

    public void testCompositeElement() throws Exception {
        Boy boy = new Boy();
        boy.setId(10);
        boy.setFirstName("hoge");
        boy.setLastName("foo");
        Toy toy = new Toy();
        toy.setName("aaa");
        toy.setBrand(new Brand());
        toy.getBrand().setName("bbb");
        toy.setSerial("ccc");
        persist(boy);

        read(Boy.class, 10);
        DataSet dataSet = read(Boy.class, 10);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase("Boy", table.getTableName());
        assertEquals(3, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
        assertEquals("hoge", row.getValue("firstName"));
        assertEquals("foo", row.getValue("lastName"));
    }

    public void testAttributedJoin() throws Exception {
        Country country = new Country();
        country.setId(1);
        country.setName("aaa");

        Boy boy = new Boy();
        boy.setId(10);
        boy.setFirstName("hoge");
        boy.setLastName("foo");
        CountryAttitude attitude = new CountryAttitude();
        attitude.setCountry(country);
        attitude.setLikes(true);
        boy.getCountryAttributes().add(attitude);
        persist(country, boy);

        DataSet dataSet = read(Boy.class, 10);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase("Boy", table.getTableName());
        assertEquals(3, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
        assertEquals("hoge", row.getValue("firstName"));
        assertEquals("foo", row.getValue("lastName"));
    }

}
