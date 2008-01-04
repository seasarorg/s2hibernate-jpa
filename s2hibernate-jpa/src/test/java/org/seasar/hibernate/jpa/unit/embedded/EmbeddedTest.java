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
package org.seasar.hibernate.jpa.unit.embedded;

import java.math.BigDecimal;

import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.hibernate.jpa.unit.HibernateEntityReaderTestCase;

/**
 * 
 * @author taedium
 */
public class EmbeddedTest extends HibernateEntityReaderTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        addAnnotatedClasses(RegionalArticle.class, AddressType.class,
                Person.class, VanillaSwap.class, Book.class,
                InternetProvider.class, Manager.class);
    }

    public void testCompositeId() throws Exception {
        RegionalArticlePk pk = new RegionalArticlePk();
        pk.setCode(999);
        pk.setName("hoge");
        RegionalArticle regionalArticle = new RegionalArticle();
        regionalArticle.setId(pk);
        persist(regionalArticle);

        DataSet dataSet = read(RegionalArticle.class, pk);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase("RegionalArticle", table.getTableName());
        assertEquals(2, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(999), row.getValue("code"));
        assertEquals("hoge", row.getValue("name"));
    }

    public void testManyToOneInsideComponent() throws Exception {
        AddressType addressType = new AddressType();
        addressType.setId(10);
        addressType.setName("aaa");
        Address address = new Address();
        address.setCity("bbb");
        address.setCountry(new Country());
        address.getCountry().setCountryName("ccc");
        address.setType(addressType);
        Person person = new Person();
        person.setId(20);
        person.setName("ddd");
        person.setAddress(address);
        person.setBornIn(new Country());
        person.getBornIn().setCountryName("eee");
        persist(addressType, person);

        DataSet dataSet = read(Person.class, 20);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase("Person", table.getTableName());
        assertEquals(6, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(20), row.getValue("id"));
        assertEquals("ddd", row.getValue("name"));
        assertEquals("bbb", row.getValue("city"));
        assertEquals("ccc", row.getValue("countryName"));
        assertEquals("eee", row.getValue("bornCountryName"));
        assertEquals(new BigDecimal(10), row.getValue("type_id"));
    }

    public void testMappedSuperclass() throws Exception {
        FixedLeg fixedLeg = new FixedLeg();
        fixedLeg.setPaymentFrequency(Leg.Frequency.ANNUALY);
        fixedLeg.setRate(5.1);
        VanillaSwap vanillaSwap = new VanillaSwap();
        vanillaSwap.setId(10);
        vanillaSwap.setFixedLeg(fixedLeg);
        persist(vanillaSwap);

        DataSet dataSet = read(VanillaSwap.class, 10);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase("VanillaSwap", table.getTableName());
        assertEquals(3, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
        assertEquals(new BigDecimal(0), row.getValue("paymentFrequency"));
        assertEquals(0, new BigDecimal("5.1").compareTo(BigDecimal.class
                .cast(row.getValue("rate"))));
    }

    public void testEmbeddedInSecdondaryTable() throws Exception {
        Book book = new Book();
        book.setId(10);
        book.setName("hoge");
        book.setSummary(new Summary());
        book.getSummary().setSize(5);
        book.getSummary().setText("foo");
        persist(book);

        DataSet dataSet = read(Book.class, 10);
        assertEquals(2, dataSet.getTableSize());

        {
            DataTable table = dataSet.getTable(0);
            assertEqualsIgnoreCase("BookSummary", table.getTableName());
            assertEquals(3, table.getColumnSize());
            assertEquals(1, table.getRowSize());
            DataRow row = table.getRow(0);
            assertEquals(new BigDecimal(10), row.getValue("id"));
            assertEquals(new BigDecimal(5), row.getValue("size"));
            assertEquals("foo", row.getValue("text"));
        }
        {
            DataTable table = dataSet.getTable(1);
            assertEqualsIgnoreCase("Book", table.getTableName());
            assertEquals(2, table.getColumnSize());
            assertEquals(1, table.getRowSize());
            DataRow row = table.getRow(0);
            assertEquals(new BigDecimal(10), row.getValue("id"));
            assertEquals("hoge", row.getValue("name"));
        }
    }

    public void testEmbeddedAndOneToMany() throws Exception {
        Manager manager = new Manager();
        manager.setId(10);
        manager.setName("hoge");
        LegalStructure legalStructure = new LegalStructure();
        legalStructure.setName("foo");
        legalStructure.getTopManagement().add(manager);
        InternetProvider internetProvider = new InternetProvider();
        internetProvider.setId(30);
        internetProvider.setOwner(legalStructure);
        persist(internetProvider, manager);

        DataSet dataSet = read(InternetProvider.class, 30);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase("InternetProvider", table.getTableName());
        assertEquals(2, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(30), row.getValue("id"));
        assertEquals("foo", row.getValue("name"));
    }

}
