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
package org.seasar.hibernate.jpa.unit.inheritance.mixed;

import java.math.BigDecimal;

import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.hibernate.jpa.unit.HibernateEntityCollectionReaderTestCase;

/**
 * 
 * @author taedium
 */
public class MixedSubclassCollectionTest extends
        HibernateEntityCollectionReaderTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        addAnnotatedClasses(File.class, Folder.class, Document.class);
    }

    public void testPolymorphicQuery() throws Exception {
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

        DataSet dataSet = read("select f from File f order by name");
        assertEquals(3, dataSet.getTableSize());

        {
            DataTable table = dataSet.getTable("FileFolderMixed");
            assertEquals(2, table.getColumnSize());
            assertEquals(3, table.getRowSize());
            DataRow row = table.getRow(0);
            assertEquals("document", row.getValue("name"));
            assertEquals("folder", row.getValue("parent_name"));
            DataRow row2 = table.getRow(1);
            assertEquals("folder", row2.getValue("name"));
            assertEquals("root", row2.getValue("parent_name"));
            DataRow row3 = table.getRow(2);
            assertEquals("root", row3.getValue("name"));
            assertEquals(null, row3.getValue("parent_name"));
        }
        {
            DataTable table = dataSet.getTable("DocumentMixed");
            assertEquals(2, table.getColumnSize());
            assertEquals(1, table.getRowSize());
            DataRow row = table.getRow(0);
            assertEquals("document", row.getValue("name"));
            assertEquals(new BigDecimal(100), row.getValue("size"));
        }
        {
            DataTable table = dataSet.getTable("File");
            assertEquals(2, table.getColumnSize());
            assertEquals(3, table.getRowSize());
            DataRow row = table.getRow(0);
            assertEquals("document", row.getValue("name"));
            assertEquals("D", row.getValue("FileType"));
            DataRow row2 = table.getRow(1);
            assertEquals("folder", row2.getValue("name"));
            assertEquals("F", row2.getValue("FileType"));
            DataRow row3 = table.getRow(2);
            assertEquals("root", row3.getValue("name"));
            assertEquals("F", row3.getValue("FileType"));
        }
    }

}
