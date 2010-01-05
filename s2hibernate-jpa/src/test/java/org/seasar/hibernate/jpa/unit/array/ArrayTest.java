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
package org.seasar.hibernate.jpa.unit.array;

import java.math.BigDecimal;

import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.hibernate.jpa.unit.HibernateEntityReaderTestCase;

/**
 * 
 * @author taedium
 */
public class ArrayTest extends HibernateEntityReaderTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        addAnnotatedClasses(Competitor.class, Contest.class);
    }

    public void testOneToMany() throws Exception {
        Competitor competitor1 = new Competitor();
        competitor1.setId(10);
        competitor1.setName("hoge");
        Contest contest = new Contest();
        contest.setId(10);
        contest.setName("foo");
        contest.setCompetitors(new Competitor[] { competitor1 });
        persist(contest, competitor1);

        DataSet dataSet = read(Contest.class, 10);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase("Contest", table.getTableName());
        assertEquals(2, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
        assertEquals("foo", row.getValue("name"));
    }

}
