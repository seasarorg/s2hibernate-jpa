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
package org.seasar.hibernate.jpa.unit.inheritance.mixed;

import java.math.BigDecimal;

import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.hibernate.jpa.unit.HibernateEntityReaderTestCase;

/**
 * 
 * @author taedium
 */
public class MixedSubclassTest extends HibernateEntityReaderTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        addAnnotatedClasses(File.class, Folder.class, Document.class);
    }

    public void testMixSingleTableAndSecondaryTable() throws Exception {
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
                assertEqualsIgnoreCase("FileFolderMixed", table.getTableName());
                assertEquals(2, table.getColumnSize());
                assertEquals(1, table.getRowSize());
                DataRow row = table.getRow(0);
                assertEquals("folder", row.getValue("name"));
                assertEquals("root", row.getValue("parent_name"));
            }
            {
                DataTable table = dataSet.getTable(1);
                assertEqualsIgnoreCase("File", table.getTableName());
                assertEquals(2, table.getColumnSize());
                assertEquals(1, table.getRowSize());
                DataRow row = table.getRow(0);
                assertEquals("folder", row.getValue("name"));
                assertEquals("F", row.getValue("FileType"));
            }
        }
        {
            DataSet dataSet = read(File.class, "document");
            assertEquals(3, dataSet.getTableSize());
            {
                DataTable table = dataSet.getTable(0);
                assertEqualsIgnoreCase("DocumentMixed", table.getTableName());
                assertEquals(2, table.getColumnSize());
                assertEquals(1, table.getRowSize());
                DataRow row = table.getRow(0);
                assertEquals("document", row.getValue("name"));
                assertEquals(new BigDecimal(100), row.getValue("size"));
            }
            {
                DataTable table = dataSet.getTable(1);
                assertEqualsIgnoreCase("FileFolderMixed", table.getTableName());
                assertEquals(2, table.getColumnSize());
                assertEquals(1, table.getRowSize());
                DataRow row = table.getRow(0);
                assertEquals("document", row.getValue("name"));
                assertEquals("folder", row.getValue("parent_name"));
            }
            {
                DataTable table = dataSet.getTable(2);
                assertEqualsIgnoreCase("File", table.getTableName());
                assertEquals(2, table.getColumnSize());
                assertEquals(1, table.getRowSize());
                DataRow row = table.getRow(0);
                assertEquals("document", row.getValue("name"));
                assertEquals("D", row.getValue("FileType"));
            }

        }
    }

}
