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
package org.seasar.hibernate.jpa.unit.manytomany;

import java.math.BigDecimal;

import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.hibernate.jpa.unit.HibernateEntityReaderTestCase;

/**
 * 
 * @author taedium
 */
public class ManyToManyTest extends HibernateEntityReaderTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        addAnnotatedClasses(KnownClient.class, Store.class);
    }

    public void testManyToManyOwningSide() throws Exception {
        KnownClient client = new KnownClient();
        client.setId(10);
        client.setName("hoge");
        Store store = new Store();
        store.setId(20);
        store.setName("foo");
        store.getKnownClients().add(client);
        client.getStores().add(store);
        persist(client, store);

        DataSet dataSet = read(Store.class, 20);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase(table.getTableName(), "Store");
        assertEquals(2, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(20), row.getValue("id"));
        assertEquals("foo", row.getValue("name"));
    }

    public void testManyToManyInverseSide() throws Exception {
        KnownClient client = new KnownClient();
        client.setId(10);
        client.setName("hoge");
        Store store = new Store();
        store.setId(20);
        store.setName("foo");
        store.getKnownClients().add(client);
        client.getStores().add(store);
        persist(client, store);

        DataSet dataSet = read(KnownClient.class, 10);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase(table.getTableName(), "KnownClient");
        assertEquals(2, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
        assertEquals("hoge", row.getValue("name"));
    }

}
