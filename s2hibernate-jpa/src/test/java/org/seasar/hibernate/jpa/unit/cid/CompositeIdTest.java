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
package org.seasar.hibernate.jpa.unit.cid;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;

import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.hibernate.jpa.unit.HibernateEntityReaderTestCase;

/**
 * 
 * @author taedium
 */
public class CompositeIdTest extends HibernateEntityReaderTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        addAnnotatedClasses(Parent.class, Child.class, Channel.class,
                Presenter.class, TvMagazin.class);
    }

    public void testManyToOneInCompositePk() throws Exception {
        ParentPk parentPk = new ParentPk();
        parentPk.setFirstName("hoge");
        parentPk.setLastName("foo");
        Parent parent = new Parent();
        parent.setId(parentPk);
        ChildPk childPk = new ChildPk();
        childPk.parent = parent;
        childPk.nthChild = 1;
        Child child = new Child();
        child.setId(childPk);
        persist(parent, child);

        DataSet dataSet = read(Child.class, childPk);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase("Child", table.getTableName());
        assertEquals(3, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals("hoge", row.getValue("parent_firstName"));
        assertEquals("foo", row.getValue("parent_lastName"));
        assertEquals(new BigDecimal(1), row.getValue("nthChild"));
    }

    public void testManyToOneInCompositeId() throws Exception {
        Channel channel = new Channel();
        channel.setId(10);
        Presenter presenter = new Presenter();
        presenter.setName("hoge");
        TvMagazinPk pk = new TvMagazinPk();
        pk.setChannel(channel);
        pk.setPresenter(presenter);
        TvMagazin tvMagazin = new TvMagazin();
        tvMagazin.setId(pk);
        tvMagazin.setTime(Time.valueOf("10:11:12"));
        persist(channel, presenter, tvMagazin);

        DataSet dataSet = read(TvMagazin.class, pk);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase("TvMagazin", table.getTableName());
        assertEquals(3, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("channel_id"));
        assertEquals("hoge", row.getValue("presenter_name"));
        assertEquals(Timestamp.valueOf("1970-01-01 10:11:12"), row
                .getValue("time"));
    }

}
