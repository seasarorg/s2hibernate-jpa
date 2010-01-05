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
package org.seasar.hibernate.jpa.unit.entity;

import java.math.BigDecimal;
import java.util.Currency;

import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.hibernate.jpa.unit.HibernateEntityReaderTestCase;

/**
 * 
 * @author taedium
 */
public class HibernateAnnotationTest extends HibernateEntityReaderTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        addAnnotatedClasses(Flight.class, Ransom.class);
    }

    public void testFormula() throws Exception {
        Flight flight = new Flight();
        flight.setId(10);
        flight.setMaxAltitude(100);
        persist(flight);

        DataSet dataSet = read(Flight.class, 10);

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase("Flight", table.getTableName());
        assertEquals(2, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
        assertEquals(new BigDecimal(100), row.getValue("maxAltitude"));
    }

    public void testCompositeType() throws Exception {
        ManetaryAmount amount = new ManetaryAmount(new BigDecimal(1000000),
                Currency.getInstance("JPY"));
        Ransom ransom = new Ransom();
        ransom.setId(10);
        ransom.setAmount(amount);
        persist(ransom);

        DataSet dataSet = read(Ransom.class, 10);

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase("Ransom", table.getTableName());
        assertEquals(3, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
        assertEquals(new BigDecimal(1000000), row.getValue("amount"));
        assertEquals("JPY", row.getValue("currency"));
    }

}
