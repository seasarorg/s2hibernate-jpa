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
package org.seasar.hibernate.jpa.unit.embedded;

import java.math.BigDecimal;

import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.hibernate.jpa.unit.EntityReaderTestCase;

/**
 * 
 * @author taedium
 */
public class EmbeddedTest extends EntityReaderTestCase {

    public void setUpEmbeddedId() throws Exception {
        cfg.addAnnotatedClasses(Company.class);
        register(cfg);
    }

    public void testEmbeddedId() throws Exception {
        CompanyPk pk = new CompanyPk();
        pk.setCompanyCode(999);
        pk.setName("hoge");
        Company company = new Company();
        company.setId(pk);
        persist(company);

        DataSet dataSet = read(Company.class, pk);
        assertEquals(1, dataSet.getTableSize());
        assertTrue(dataSet.hasTable("Company"));

        DataTable table = dataSet.getTable(0);
        assertEquals(2, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(999), row.getValue("companyCode"));
        assertEquals("hoge", row.getValue("name"));
    }
}
