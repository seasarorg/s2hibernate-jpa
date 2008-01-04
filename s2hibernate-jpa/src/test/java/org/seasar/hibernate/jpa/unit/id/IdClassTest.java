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
package org.seasar.hibernate.jpa.unit.id;

import java.math.BigDecimal;

import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.hibernate.jpa.unit.HibernateEntityReaderTestCase;

/**
 * 
 * @author taedium
 */
public class IdClassTest extends HibernateEntityReaderTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        addAnnotatedClasses(Project.class, Tree.class, FirTree.class,
                Footballer.class, GoalKeeper.class, Tower.class);
    }

    public void testIdInMappedSuperclass() throws Exception {
        FirTree tree = new FirTree();
        tree.setId(10);
        persist(tree);
        DataSet dataSet = read(FirTree.class, 10);
        assertEquals(1, dataSet.getTableSize());
        assertTrue(dataSet.hasTable("FirTree"));
        DataTable table = dataSet.getTable(0);
        assertEquals(1, table.getColumnSize());
        assertEquals(1, table.getRowSize());
        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
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

    public void testIdClassInInheritance() throws Exception {
        FootballerPk pk = new FootballerPk();
        pk.setFirstName("hoge");
        pk.setLastName("foo");
        GoalKeeper goalKeeper = new GoalKeeper();
        goalKeeper.setFirstName(pk.getFirstName());
        goalKeeper.setLastName(pk.getLastName());
        goalKeeper.setClub("bar");
        persist(goalKeeper);

        DataSet dataSet = read(GoalKeeper.class, pk);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase("Footballer", table.getTableName());
        assertEquals(4, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals("hoge", row.getValue("firstName"));
        assertEquals("foo", row.getValue("lastName"));
        assertEquals("bar", row.getValue("club"));
        assertEquals("GoalKeeper", row.getValue("DTYPE"));
    }

    public void testIdClassInMappedSuperclass() throws Exception {
        Location pk = new Location();
        pk.setLongitude(50.5);
        pk.setLatitude(40.8);
        Tower tower = new Tower();
        tower.setLongitude(pk.getLongitude());
        tower.setLatitude(pk.getLatitude());
        persist(tower);

        DataSet dataSet = read(Tower.class, pk);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase("Tower", table.getTableName());
        assertEquals(2, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(50.5d), row.getValue("longitude"));
        assertEquals(0, new BigDecimal("40.8").compareTo(BigDecimal.class
                .cast(row.getValue("latitude"))));
    }
}
