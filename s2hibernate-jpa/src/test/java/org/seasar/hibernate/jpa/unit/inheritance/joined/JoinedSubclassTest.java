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
package org.seasar.hibernate.jpa.unit.inheritance.joined;

import java.math.BigDecimal;

import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.hibernate.jpa.unit.HibernateEntityReaderTestCase;

/**
 * 
 * @author taedium
 */
public class JoinedSubclassTest extends HibernateEntityReaderTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        addAnnotatedClasses(File.class, Folder.class, Document.class,
                ProgramExecution.class, Clothing.class, Sweater.class,
                Customer.class, ValuedCustomer.class);
    }

    public void testDefault() throws Exception {
        Folder root = new Folder();
        root.setName("root");
        Folder folder = new Folder();
        folder.setName("folder");
        folder.setParent(root);
        Document document = new Document();
        document.setName("document");
        document.setSize(100);
        document.setParent(folder);
        folder.getChildren().add(document);
        persist(root, folder, document);

        {
            DataSet dataSet = read(File.class, "folder");
            assertEquals(2, dataSet.getTableSize());

            {
                DataTable table = dataSet.getTable(0);
                assertEqualsIgnoreCase("Folder", table.getTableName());
                assertEquals(1, table.getColumnSize());
                assertEquals(1, table.getRowSize());
                DataRow row = table.getRow(0);
                assertEquals("folder", row.getValue("name"));
            }
            {
                DataTable table = dataSet.getTable(1);
                assertEqualsIgnoreCase("File", table.getTableName());
                assertEquals(2, table.getColumnSize());
                assertEquals(1, table.getRowSize());
                DataRow row = table.getRow(0);
                assertEquals("folder", row.getValue("name"));
                assertEquals("root", row.getValue("parent_name"));
            }
        }
        {
            DataSet dataSet = read(File.class, "document");
            assertEquals(2, dataSet.getTableSize());
            {
                DataTable table = dataSet.getTable(0);
                assertEqualsIgnoreCase("Document", table.getTableName());
                assertEquals(2, table.getColumnSize());
                assertEquals(1, table.getRowSize());
                DataRow row = table.getRow(0);
                assertEquals("document", row.getValue("name"));
                assertEquals(new BigDecimal(100), row.getValue("size"));
            }
            {
                DataTable table = dataSet.getTable(1);
                assertEqualsIgnoreCase("File", table.getTableName());
                assertEquals(2, table.getColumnSize());
                assertEquals(1, table.getRowSize());
                DataRow row = table.getRow(0);
                assertEquals("document", row.getValue("name"));
                assertEquals("folder", row.getValue("parent_name"));
            }
        }
    }

    public void testManyToOneOnAbstract() throws Exception {
        File file = new Folder();
        file.setName("hoge");
        ProgramExecution programExecution = new ProgramExecution();
        programExecution.setId(10);
        programExecution.setAction("foo");
        programExecution.setAppliesOn(file);
        persist(file, programExecution);

        DataSet dataSet = read(ProgramExecution.class, 10);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase("ProgramExecution", table.getTableName());
        assertEquals(3, table.getColumnSize());
        assertEquals(1, table.getRowSize());
        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
        assertEquals("foo", row.getValue("action"));
        assertEquals("hoge", row.getValue("appliesOn_name"));
    }

    public void testPrimaryKeyJoin() throws Exception {
        Sweater sweater = new Sweater();
        sweater.setId(10);
        sweater.setSize(5);
        sweater.setColor("red");
        sweater.setSweat(true);
        persist(sweater);

        DataSet dataSet = read(Sweater.class, 10);
        assertEquals(2, dataSet.getTableSize());
        {
            DataTable table = dataSet.getTable(0);
            assertEqualsIgnoreCase("Sweater", table.getTableName());
            assertEquals(2, table.getColumnSize());
            assertEquals(1, table.getRowSize());
            DataRow row = table.getRow(0);
            assertEquals(new BigDecimal(10), row.getValue("clothing_id"));
            assertEquals(true, row.getValue("sweat"));
        }
        {
            DataTable table = dataSet.getTable(1);
            assertEqualsIgnoreCase("Clothing", table.getTableName());
            assertEquals(3, table.getColumnSize());
            assertEquals(1, table.getRowSize());
            DataRow row = table.getRow(0);
            assertEquals(new BigDecimal(10), row.getValue("id"));
            assertEquals(new BigDecimal(5), row.getValue("size"));
            assertEquals("red", row.getValue("color"));
        }
    }

    public void testCompositePrimaryKeyJoin() throws Exception {
        CustomerPk pk = new CustomerPk();
        pk.setName("hoge");
        pk.setCode("foo");
        ValuedCustomer valuedCustomer = new ValuedCustomer();
        valuedCustomer.setId(pk);
        valuedCustomer.setRank(5);
        persist(valuedCustomer);

        DataSet dataSet = read(ValuedCustomer.class, pk);
        assertEquals(2, dataSet.getTableSize());
        {
            DataTable table = dataSet.getTable(0);
            assertEqualsIgnoreCase("valuedCustomer", table.getTableName());
            assertEquals(3, table.getColumnSize());
            assertEquals(1, table.getRowSize());
            DataRow row = table.getRow(0);
            assertEquals("hoge", row.getValue("vc_name"));
            assertEquals("foo", row.getValue("vc_code"));
            assertEquals(new BigDecimal(5), row.getValue("rank"));
        }
        {
            DataTable table = dataSet.getTable(1);
            assertEqualsIgnoreCase("Customer", table.getTableName());
            assertEquals(3, table.getColumnSize());
            assertEquals(1, table.getRowSize());
            DataRow row = table.getRow(0);
            assertEquals("hoge", row.getValue("name"));
            assertEquals("foo", row.getValue("code"));
            assertEquals(null, row.getValue("address"));
        }
    }
}
