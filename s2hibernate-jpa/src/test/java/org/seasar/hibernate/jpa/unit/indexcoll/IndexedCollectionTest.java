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
package org.seasar.hibernate.jpa.unit.indexcoll;

import java.math.BigDecimal;

import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.hibernate.jpa.unit.HibernateEntityReaderTestCase;

/**
 * 
 * @author taedium
 */
public class IndexedCollectionTest extends HibernateEntityReaderTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        addAnnotatedClasses(Software.class, Version.class, Drawer.class,
                Wardrobe.class, Atmosphere.class, Gas.class, Painter.class,
                Painting.class);
    }

    public void testIndexColumnOwningSide() throws Exception {
        Drawer drawer = new Drawer();
        drawer.setId(10);
        Wardrobe wardrobe = new Wardrobe();
        wardrobe.setId(20);
        wardrobe.getDrawers().add(drawer);
        persist(wardrobe);

        DataSet dataSet = read(Wardrobe.class, 20);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase("Wardrobe", table.getTableName());
        assertEquals(1, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(20), row.getValue("id"));
    }

    public void testIndexColumnCollectionedSide() throws Exception {
        Drawer drawer = new Drawer();
        drawer.setId(10);
        Wardrobe wardrobe = new Wardrobe();
        wardrobe.setId(20);
        wardrobe.getDrawers().add(drawer);
        persist(wardrobe);
        {
            DataSet dataSet = read(Drawer.class, 10);
            assertEquals(1, dataSet.getTableSize());

            DataTable table = dataSet.getTable(0);
            assertEqualsIgnoreCase("Drawer", table.getTableName());
            assertEquals(1, table.getColumnSize());
            assertEquals(1, table.getRowSize());

            DataRow row = table.getRow(0);
            assertEquals(new BigDecimal(10), row.getValue("id"));
        }
    }

    public void testMapKeyOwningSide() throws Exception {
        Software software = new Software();
        software.setName("hoge");
        Version version = new Version();
        version.setId(10);
        version.setCodeName("foo");
        software.getVersions().put("foo", version);
        version.setSoftware(software);
        persist(software, version);

        DataSet dataSet = read(Version.class, 10);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase("Version", table.getTableName());
        assertEquals(3, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
        assertEquals("foo", row.getValue("codeName"));
        assertEquals("hoge", row.getValue("software_name"));
    }

    public void testMapKeyInverseSide() throws Exception {
        Software software = new Software();
        software.setName("hoge");
        Version version = new Version();
        version.setId(10);
        version.setCodeName("foo");
        software.getVersions().put("foo", version);
        persist(software, version);

        DataSet dataSet = read(Software.class, "hoge");
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase("Software", table.getTableName());
        assertEquals(1, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals("hoge", row.getValue("name"));
    }

    public void testMapKeyAndIdClassMapHoldingSide() throws Exception {
        Painting painting = new Painting();
        painting.setName("hoge");
        painting.setPainter("foo");
        painting.setSizeX(30);
        painting.setSizeY(80);
        Painter painter = new Painter();
        painter.setId(10);
        painter.getPaintings().put("hoge", painting);
        persist(painter, painting);

        DataSet dataSet = read(Painter.class, 10);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase("Painter", table.getTableName());
        assertEquals(1, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
    }

    public void testMapKeyAndIdClassMappedSide() throws Exception {
        Painting painting = new Painting();
        painting.setName("hoge");
        painting.setPainter("foo");
        painting.setSizeX(30);
        painting.setSizeY(80);
        Painter painter = new Painter();
        painter.setId(10);
        painter.getPaintings().put("hoge", painting);
        persist(painter, painting);

        PaintingPk pk = new PaintingPk();
        pk.setName("hoge");
        pk.setPainter("foo");
        DataSet dataSet = read(Painting.class, pk);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase("Painting", table.getTableName());
        assertEquals(4, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals("hoge", row.getValue("name"));
        assertEquals("foo", row.getValue("painter"));
        assertEquals(new BigDecimal(30), row.getValue("sizex"));
        assertEquals(new BigDecimal(80), row.getValue("sizey"));

    }

    public void testRealMapKeyMapHolidingSide() throws Exception {
        Gas gas = new Gas();
        gas.setId(10);
        gas.setName("oxygen");
        Atmosphere atmosphere = new Atmosphere();
        atmosphere.setId(10);
        atmosphere.getGasses().put("100%", gas);
        persist(gas, atmosphere);

        DataSet dataSet = read(Atmosphere.class, 10);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase("Atmosphere", table.getTableName());
        assertEquals(1, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
    }

    public void testRealMapKeyMappedSide() throws Exception {
        Gas gas = new Gas();
        gas.setId(10);
        gas.setName("oxygen");
        Atmosphere atmosphere = new Atmosphere();
        atmosphere.setId(10);
        atmosphere.getGasses().put("100%", gas);
        persist(gas, atmosphere);

        DataSet dataSet = read(Gas.class, 10);
        assertEquals(1, dataSet.getTableSize());

        DataTable table = dataSet.getTable(0);
        assertEqualsIgnoreCase("Gas", table.getTableName());
        assertEquals(2, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
        assertEquals("oxygen", row.getValue("name"));
    }
}
