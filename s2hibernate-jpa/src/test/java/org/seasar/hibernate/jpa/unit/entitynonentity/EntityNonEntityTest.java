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
package org.seasar.hibernate.jpa.unit.entitynonentity;

import java.math.BigDecimal;

import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.hibernate.jpa.unit.HibernateEntityReaderTestCase;

/**
 * 
 * @author taedium
 */
public class EntityNonEntityTest extends HibernateEntityReaderTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        addAnnotatedClasses(Voice.class, GSM.class);
    }

    public void test() throws Exception {
        GSM gsm = new GSM();
        gsm.setNumber(1);
        gsm.setSpecies("hoge");
        gsm.setId(2);
        gsm.setNumeric(true);
        gsm.setBrand("foo");
        gsm.setFrequency(3);
        persist(gsm);

        DataSet dataSet = read(GSM.class, 2);
        assertEquals(2, dataSet.getTableSize());
        {
            DataTable table = dataSet.getTable(0);
            assertEqualsIgnoreCase("GSM", table.getTableName());
            assertEquals(3, table.getColumnSize());
            assertEquals(1, table.getRowSize());
            DataRow row = table.getRow(0);
            assertEquals(new BigDecimal(2), row.getValue("id"));
            assertEquals(true, row.getValue("numeric"));
            assertEquals(new BigDecimal(3), row.getValue("frequency"));
        }
        {
            DataTable table = dataSet.getTable(1);
            assertEqualsIgnoreCase("Voice", table.getTableName());
            assertEquals(2, table.getColumnSize());
            assertEquals(1, table.getRowSize());
            DataRow row = table.getRow(0);
            assertEquals(new BigDecimal(2), row.getValue("id"));
            assertEquals(new BigDecimal(1), row.getValue("number"));
        }

    }
}
