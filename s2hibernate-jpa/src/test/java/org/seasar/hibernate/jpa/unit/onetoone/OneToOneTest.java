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
package org.seasar.hibernate.jpa.unit.onetoone;

import java.math.BigDecimal;

import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.hibernate.jpa.unit.HibernateEntityReaderTestCase;

/**
 * 
 * @author taedium
 */
public class OneToOneTest extends HibernateEntityReaderTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        addAnnotatedClasses(Passport.class, Customer.class, Party.class,
                PartyAffiliate.class, SerialNumber.class, Computer.class);
    }

    public void testOneToOneOwningSide() throws Exception {
        Passport passport = new Passport();
        passport.setId(10);
        passport.setNumber("ABC");
        Customer customer = new Customer();
        customer.setId(90);
        customer.setName("hoge");
        customer.setPassport(passport);
        passport.setOwner(customer);
        persist(passport, customer);

        DataSet dataSet = read(Customer.class, 90);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase(table.getTableName(), "Customer");
        assertEquals(3, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(90), row.getValue("id"));
        assertEquals("hoge", row.getValue("name"));
        assertEquals(new BigDecimal(10), row.getValue("passport_id"));
    }

    public void testOneToOneInverseSide() throws Exception {
        Passport passport = new Passport();
        passport.setId(10);
        passport.setNumber("ABC");
        Customer customer = new Customer();
        customer.setId(90);
        customer.setName("hoge");
        customer.setPassport(passport);
        passport.setOwner(customer);
        persist(passport, customer);

        DataSet dataSet = read(Passport.class, 10);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase(table.getTableName(), "Passport");
        assertEquals(2, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
        assertEquals("ABC", row.getValue("number"));
    }

    public void testTrueOneToOneOwningSide() throws Exception {
        Party party = new Party();
        party.setId(10);
        party.setName("hoge");
        PartyAffiliate affiliate = new PartyAffiliate();
        affiliate.setId(10);
        affiliate.setParty(party);
        affiliate.setAffiliateName("foo");
        persist(party, affiliate);

        DataSet dataSet = read(PartyAffiliate.class, 10);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase(table.getTableName(), "PartyAffiliate");
        assertEquals(2, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
        assertEquals("foo", row.getValue("affiliateName"));
    }

    public void testTrueOneToOneInverseSide() throws Exception {
        Party party = new Party();
        party.setId(10);
        party.setName("hoge");
        PartyAffiliate affiliate = new PartyAffiliate();
        affiliate.setId(10);
        affiliate.setParty(party);
        affiliate.setAffiliateName("foo");
        persist(party, affiliate);

        DataSet dataSet = read(Party.class, 10);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase(table.getTableName(), "Party");
        assertEquals(2, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
        assertEquals("hoge", row.getValue("partyName"));
    }

    public void testOneToOneCompositeFk() throws Exception {
        SerialNumberPk serialNoPk = new SerialNumberPk();
        serialNoPk.setBrand("hoge");
        serialNoPk.setModel("foo");
        SerialNumber serialNo = new SerialNumber();
        serialNo.setId(serialNoPk);
        serialNo.setValue("99999");
        Computer computer = new Computer();
        computer.setId(10);
        computer.setSerialNumber(serialNo);
        persist(serialNo, computer);

        DataSet dataSet = read(Computer.class, 10);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase(table.getTableName(), "Computer");
        assertEquals(3, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
        assertEquals("hoge", row.getValue("serialbrand"));
        assertEquals("foo", row.getValue("serialmodel"));
    }
}
