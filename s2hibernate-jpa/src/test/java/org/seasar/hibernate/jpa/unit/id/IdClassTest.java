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
package org.seasar.hibernate.jpa.unit.id;

import java.math.BigDecimal;

import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.hibernate.jpa.unit.EntityReaderTestCase;

/**
 * 
 * @author taedium
 */
public class IdClassTest extends EntityReaderTestCase {

    public void setUpIdClass() throws Exception {
        cfg.addAnnotatedClasses(Project.class);
        register(cfg);
    }

    public void testIdClass() throws Exception {
        ProjectPk pk = new ProjectPk();
        pk.setProjectCode(999);
        pk.setName("hoge");

        Project project = new Project();
        project.setProjectCode(pk.getProjectCode());
        project.setName(pk.getName());
        persist(project);

        DataSet dataSet = read(Project.class, pk);
        assertEquals(1, dataSet.getTableSize());
        assertTrue(dataSet.hasTable("Project"));

        DataTable table = dataSet.getTable(0);
        assertEquals(2, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(999), row.getValue("projectCode"));
        assertEquals("hoge", row.getValue("name"));
    }
}
