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
package org.seasar.hibernate.jpa.metadata;

import java.sql.Types;
import java.util.Date;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TemporalType;

import org.hibernate.property.BackrefPropertyAccessor;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.jpa.metadata.AttributeDesc;
import org.seasar.framework.jpa.metadata.EntityDesc;
import org.seasar.framework.jpa.metadata.EntityDescFactory;
import org.seasar.hibernate.jpa.Address;
import org.seasar.hibernate.jpa.Department;
import org.seasar.hibernate.jpa.Employee;
import org.seasar.hibernate.jpa.S2HibernateConfiguration;
import org.seasar.hibernate.jpa.unit.indexcoll.Drawer;
import org.seasar.hibernate.jpa.unit.indexcoll.Wardrobe;

/**
 * @author koichik
 */
public class HibernateEntityDescTest extends S2TestCase {

    private EntityManager em;

    @Override
    protected void setUp() throws Exception {
        include("j2ee.dicon");
        include("jpa.dicon");
    }

    public void testDepartment() throws Exception {
        Department dept = new Department();
        EntityDesc entityDesc = EntityDescFactory
                .getEntityDesc(Department.class);
        assertNotNull(entityDesc);
        assertEquals("Dept", entityDesc.getEntityName());
        assertEquals("deptno", entityDesc.getIdAttributeDesc().getName());
        String[] propNames = entityDesc.getAttributeNames();
        assertNotNull(propNames);
        assertEquals(6, propNames.length);
        assertEquals("deptno", propNames[0]);
        assertEquals("dname", propNames[1]);
        assertEquals("loc", propNames[2]);
        assertEquals("versionNo", propNames[3]);
        assertEquals("active", propNames[4]);
        assertEquals("employees", propNames[5]);

        AttributeDesc[] attributes = entityDesc.getAttributeDescs();

        AttributeDesc attribute = attributes[0];
        assertEquals("deptno", attribute.getName());
        assertEquals(Integer.class, attribute.getType());
        assertEquals(Types.INTEGER, attribute.getSqlType());
        assertNull(attribute.getTemporalType());
        assertTrue(attribute.isId());
        assertFalse(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertFalse(attribute.isVersion());
        attribute.setValue(dept, 100);
        assertEquals(100, dept.getDeptno());

        attribute = attributes[1];
        assertEquals("dname", attribute.getName());
        assertEquals(String.class, attribute.getType());
        assertEquals(Types.VARCHAR, attribute.getSqlType());
        assertNull(attribute.getTemporalType());
        assertFalse(attribute.isId());
        assertFalse(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertFalse(attribute.isVersion());

        attribute = attributes[2];
        assertEquals("loc", attribute.getName());
        assertEquals(String.class, attribute.getType());
        assertEquals(Types.VARCHAR, attribute.getSqlType());
        assertNull(attribute.getTemporalType());
        assertFalse(attribute.isId());
        assertFalse(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertFalse(attribute.isVersion());

        attribute = attributes[3];
        assertEquals("versionNo", attribute.getName());
        assertEquals(Integer.class, attribute.getType());
        assertEquals(Types.INTEGER, attribute.getSqlType());
        assertNull(attribute.getTemporalType());
        assertFalse(attribute.isId());
        assertFalse(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertTrue(attribute.isVersion());

        attribute = attributes[4];
        assertEquals("active", attribute.getName());
        assertEquals(Boolean.class, attribute.getType());
        assertEquals(Types.BIT, attribute.getSqlType());
        assertNull(attribute.getTemporalType());
        assertFalse(attribute.isId());
        assertFalse(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertFalse(attribute.isVersion());

        attribute = attributes[5];
        assertEquals("employees", attribute.getName());
        assertEquals(Set.class, attribute.getType());
        assertEquals(Employee.class, attribute.getElementType());
        assertEquals(Types.OTHER, attribute.getSqlType());
        assertNull(attribute.getTemporalType());
        assertFalse(attribute.isId());
        assertTrue(attribute.isAssociation());
        assertTrue(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertFalse(attribute.isVersion());
    }

    public void testEmployee() throws Exception {
        EntityDesc entityDesc = EntityDescFactory.getEntityDesc(Employee.class);
        assertNotNull(entityDesc);
        assertEquals("Emp", entityDesc.getEntityName());
        assertEquals("empno", entityDesc.getIdAttributeDesc().getName());
        String[] propNames = entityDesc.getAttributeNames();
        assertNotNull(propNames);
        assertEquals(10, propNames.length);
        assertEquals("empno", propNames[0]);
        assertEquals("ename", propNames[1]);
        assertEquals("job", propNames[2]);
        assertEquals("mgr", propNames[3]);
        assertEquals("hiredate", propNames[4]);
        assertEquals("sal", propNames[5]);
        assertEquals("comm", propNames[6]);
        assertEquals("tstamp", propNames[7]);
        assertEquals("department", propNames[8]);
        assertEquals("address", propNames[9]);

        AttributeDesc[] attributes = entityDesc.getAttributeDescs();

        AttributeDesc attribute = attributes[0];
        assertEquals("empno", attribute.getName());
        assertEquals(Long.class, attribute.getType());
        assertEquals(Types.BIGINT, attribute.getSqlType());
        assertNull(attribute.getTemporalType());
        assertTrue(attribute.isId());
        assertFalse(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertFalse(attribute.isVersion());

        attribute = attributes[1];
        assertEquals("ename", attribute.getName());
        assertEquals(String.class, attribute.getType());
        assertEquals(Types.VARCHAR, attribute.getSqlType());
        assertNull(attribute.getTemporalType());
        assertFalse(attribute.isId());
        assertFalse(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertFalse(attribute.isVersion());

        attribute = attributes[2];
        assertEquals("job", attribute.getName());
        assertEquals(String.class, attribute.getType());
        assertEquals(Types.VARCHAR, attribute.getSqlType());
        assertNull(attribute.getTemporalType());
        assertFalse(attribute.isId());
        assertFalse(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertFalse(attribute.isVersion());

        attribute = attributes[3];
        assertEquals("mgr", attribute.getName());
        assertEquals(Short.class, attribute.getType());
        assertEquals(Types.SMALLINT, attribute.getSqlType());
        assertNull(attribute.getTemporalType());
        assertFalse(attribute.isId());
        assertFalse(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertFalse(attribute.isVersion());

        attribute = attributes[4];
        assertEquals("hiredate", attribute.getName());
        assertEquals(Date.class, attribute.getType());
        assertEquals(Types.DATE, attribute.getSqlType());
        assertEquals(TemporalType.DATE, attribute.getTemporalType());
        assertFalse(attribute.isId());
        assertFalse(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertFalse(attribute.isVersion());

        attribute = attributes[5];
        assertEquals("sal", attribute.getName());
        assertEquals(Float.class, attribute.getType());
        assertEquals(Types.FLOAT, attribute.getSqlType());
        assertNull(attribute.getTemporalType());
        assertFalse(attribute.isId());
        assertFalse(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertFalse(attribute.isVersion());

        attribute = attributes[6];
        assertEquals("comm", attribute.getName());
        assertEquals(Float.class, attribute.getType());
        assertEquals(Types.FLOAT, attribute.getSqlType());
        assertNull(attribute.getTemporalType());
        assertFalse(attribute.isId());
        assertFalse(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertFalse(attribute.isVersion());

        attribute = attributes[7];
        assertEquals("tstamp", attribute.getName());
        assertEquals(Date.class, attribute.getType());
        assertEquals(Types.TIMESTAMP, attribute.getSqlType());
        assertEquals(TemporalType.TIMESTAMP, attribute.getTemporalType());
        assertFalse(attribute.isId());
        assertFalse(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertFalse(attribute.isVersion());

        attribute = attributes[8];
        assertEquals("department", attribute.getName());
        assertEquals(Department.class, attribute.getType());
        assertEquals(Types.OTHER, attribute.getSqlType());
        assertNull(attribute.getTemporalType());
        assertFalse(attribute.isId());
        assertTrue(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertFalse(attribute.isVersion());

        attribute = attributes[9];
        assertEquals("address", attribute.getName());
        assertEquals(Address.class, attribute.getType());
        assertEquals(Types.OTHER, attribute.getSqlType());
        assertNull(attribute.getTemporalType());
        assertFalse(attribute.isId());
        assertFalse(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertTrue(attribute.isComponent());
        assertFalse(attribute.isVersion());

        AttributeDesc[] children = attribute.getChildAttributeDescs();
        assertNotNull(children);
        assertEquals(2, children.length);

        attribute = children[0];
        assertEquals("city", attribute.getName());
        assertEquals(String.class, attribute.getType());
        assertEquals(Types.VARCHAR, attribute.getSqlType());
        assertNull(attribute.getTemporalType());
        assertFalse(attribute.isId());
        assertFalse(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertFalse(attribute.isVersion());

        attribute = children[1];
        assertEquals("zip", attribute.getName());
        assertEquals(String.class, attribute.getType());
        assertEquals(Types.VARCHAR, attribute.getSqlType());
        assertNull(attribute.getTemporalType());
        assertFalse(attribute.isId());
        assertFalse(attribute.isAssociation());
        assertFalse(attribute.isCollection());
        assertFalse(attribute.isComponent());
        assertFalse(attribute.isVersion());
    }

    public void setUpSingleTableInheritanceTx() throws Exception {
        S2HibernateConfiguration cfg = S2HibernateConfiguration.class
                .cast(getComponent(S2HibernateConfiguration.class));
        cfg.addPersistenceClass("persistenceUnit", Fruit.class);
        cfg.addPersistenceClass("persistenceUnit", Apple.class);
        cfg.addPersistenceClass("persistenceUnit", Basket.class);
    }

    public void testSingleTableInheritanceTx() throws Exception {
        Basket basket = new Basket();
        basket.setId(10);
        Apple apple = new Apple();
        apple.setId(20);
        apple.setAppearance(new Appearance());
        apple.getAppearance().setSize(5);
        apple.getAppearance().setColor("red");
        apple.setBasket(basket);
        em.persist(basket);
        em.persist(apple);

        HibernateEntityDesc entityDesc = (HibernateEntityDesc) EntityDescFactory
                .getEntityDesc(Apple.class);
        assertNotNull(entityDesc);
        assertEquals("Apple", entityDesc.getEntityName());
        assertEquals("id", entityDesc.getIdAttributeDesc().getName());
        String[] propNames = entityDesc.getAttributeNames();
        assertNotNull(propNames);
        assertEquals(3, propNames.length);
        assertEquals("id", propNames[0]);
        assertEquals("appearance", propNames[1]);
        assertEquals("basket", propNames[2]);
        assertTrue(entityDesc.hasPrimaryTableName("Fruit"));
        assertEquals(1, entityDesc.getTableNameSize());
        assertTrue(entityDesc.hasDiscriminatorColumn());
        assertEquals("DTYPE", entityDesc.getDiscriminatorColumnName());
        assertEquals("Apple", entityDesc.getDiscriminatorValue());
        assertEquals(Types.VARCHAR, entityDesc.getDiscriminatorSqlType());

        HibernateAttributeDesc[] attributes = entityDesc.getAttributeDescs();

        HibernateAttributeDesc attribute = attributes[0];
        assertEquals("id", attribute.getName());
        assertTrue(attribute.isSelectable());
        assertTrue(attribute.isReadTarget());
        assertEquals(1, attribute.getTableNameSize());
        assertEquals("Fruit", attribute.getTableName(0));
        assertEquals(1, attribute.getColumnNameSize("Fruit"));
        assertEquals(1, attribute.getSqlTypes().length);
        assertEquals(Types.INTEGER, attribute.getSqlTypes()[0]);
        Object[] values = attribute.getAllValues(apple);
        assertEquals(1, values.length);
        assertEquals(20, values[0]);

        attribute = attributes[1];
        assertEquals("appearance", attribute.getName());
        assertTrue(attribute.isSelectable());
        assertTrue(attribute.isReadTarget());
        assertEquals(1, attribute.getTableNameSize());
        assertEquals("Fruit", attribute.getTableName(0));
        assertEquals(2, attribute.getColumnNameSize("Fruit"));
        assertEquals(2, attribute.getSqlTypes().length);
        assertEquals(Types.INTEGER, attribute.getSqlTypes()[0]);
        assertEquals(Types.VARCHAR, attribute.getSqlTypes()[1]);
        values = attribute.getAllValues(apple);
        assertEquals(2, values.length);
        assertEquals(5, values[0]);
        assertEquals("red", values[1]);

        attribute = attributes[2];
        assertEquals("basket", attribute.getName());
        assertTrue(attribute.isSelectable());
        assertTrue(attribute.isReadTarget());
        assertEquals(1, attribute.getTableNameSize());
        assertEquals("Fruit", attribute.getTableName(0));
        assertEquals(1, attribute.getColumnNameSize("Fruit"));
        assertEquals(1, attribute.getSqlTypes().length);
        assertEquals(Types.INTEGER, attribute.getSqlTypes()[0]);
        values = attribute.getAllValues(apple);
        assertEquals(1, values.length);
        assertEquals(10, values[0]);
    }

    public void setUpJoinedTableInheritanceTx() throws Exception {
        S2HibernateConfiguration cfg = S2HibernateConfiguration.class
                .cast(getComponent(S2HibernateConfiguration.class));
        cfg.addPersistenceClass("persistenceUnit", File.class);
        cfg.addPersistenceClass("persistenceUnit", Folder.class);
    }

    public void testJoinedTableInheritanceTx() throws Exception {
        Folder root = new Folder();
        root.setName("root");
        Folder folder = new Folder();
        folder.setName("folder");
        folder.setParent(root);
        em.persist(root);
        em.persist(folder);

        HibernateEntityDesc entityDesc = (HibernateEntityDesc) EntityDescFactory
                .getEntityDesc(Folder.class);
        assertNotNull(entityDesc);
        assertEquals("Folder", entityDesc.getEntityName());
        assertEquals("name", entityDesc.getIdAttributeDesc().getName());
        String[] propNames = entityDesc.getAttributeNames();
        assertNotNull(propNames);
        assertEquals(3, propNames.length);
        assertEquals("name", propNames[0]);
        assertEquals("parent", propNames[1]);
        assertEquals("children", propNames[2]);
        assertTrue(entityDesc.hasPrimaryTableName("Folder"));
        assertEquals(2, entityDesc.getTableNameSize());
        assertTrue(entityDesc.hasTableName("File"));
        assertTrue(entityDesc.hasTableName("Folder"));
        assertFalse(entityDesc.hasDiscriminatorColumn());

        HibernateAttributeDesc[] attributes = entityDesc.getAttributeDescs();

        HibernateAttributeDesc attribute = attributes[0];
        assertEquals("name", attribute.getName());
        assertTrue(attribute.isSelectable());
        assertTrue(attribute.isReadTarget());
        assertEquals(2, attribute.getTableNameSize());
        assertTrue(attribute.hasTableName("File"));
        assertTrue(attribute.hasTableName("Folder"));
        assertEquals(1, attribute.getColumnNameSize("File"));
        assertEquals(1, attribute.getColumnNameSize("Folder"));
        assertEquals(1, attribute.getSqlTypes().length);
        assertEquals(Types.VARCHAR, attribute.getSqlTypes()[0]);
        Object[] values = attribute.getAllValues(folder);
        assertEquals(1, values.length);
        assertEquals("folder", values[0]);

        attribute = attributes[1];
        assertEquals("parent", attribute.getName());
        assertTrue(attribute.isSelectable());
        assertTrue(attribute.isReadTarget());
        assertEquals(1, attribute.getTableNameSize());
        assertTrue(attribute.hasTableName("File"));
        assertEquals(1, attribute.getColumnNameSize("File"));
        assertEquals(1, attribute.getSqlTypes().length);
        assertEquals(Types.VARCHAR, attribute.getSqlTypes()[0]);
        values = attribute.getAllValues(folder);
        assertEquals(1, values.length);
        assertEquals("root", values[0]);

        attribute = attributes[2];
        assertEquals("children", attribute.getName());
        assertTrue(attribute.isSelectable());
        assertFalse(attribute.isReadTarget());
        assertEquals(1, attribute.getTableNameSize());
        System.out.println(attribute.getTableName(0));
        assertTrue(attribute.hasTableName("Folder"));
        assertEquals(1, attribute.getColumnNameSize("Folder"));
        assertEquals(0, attribute.getSqlTypes().length);
        values = attribute.getAllValues(folder);
        assertEquals(0, values.length);
    }

    public void setUpIndexColumnTx() throws Exception {
        S2HibernateConfiguration cfg = S2HibernateConfiguration.class
                .cast(getComponent(S2HibernateConfiguration.class));
        cfg.addPersistenceClass("persistenceUnit", Wardrobe.class);
        cfg.addPersistenceClass("persistenceUnit", Drawer.class);
    }

    public void testIndexColumnTx() throws Exception {
        Drawer drawer = new Drawer();
        drawer.setId(10);
        Wardrobe wardrobe = new Wardrobe();
        wardrobe.setId(20);
        wardrobe.getDrawers().add(drawer);

        HibernateEntityDesc entityDesc = (HibernateEntityDesc) EntityDescFactory
                .getEntityDesc(Drawer.class);
        assertNotNull(entityDesc);
        assertEquals("Drawer", entityDesc.getEntityName());
        assertEquals("id", entityDesc.getIdAttributeDesc().getName());
        String[] propNames = entityDesc.getAttributeNames();
        assertNotNull(propNames);
        assertEquals(3, propNames.length);
        assertEquals("id", propNames[0]);
        assertEquals("_drawersBackref", propNames[1]);
        assertEquals("_drawersIndexBackref", propNames[2]);
        assertTrue(entityDesc.hasPrimaryTableName("Drawer"));
        assertEquals(1, entityDesc.getTableNameSize());
        assertTrue(entityDesc.hasTableName("Drawer"));
        assertFalse(entityDesc.hasDiscriminatorColumn());

        HibernateAttributeDesc[] attributes = entityDesc.getAttributeDescs();

        HibernateAttributeDesc attribute = attributes[0];
        assertEquals("id", attribute.getName());
        assertTrue(attribute.isSelectable());
        assertTrue(attribute.isReadTarget());
        assertEquals(1, attribute.getTableNameSize());
        assertTrue(attribute.hasTableName("Drawer"));
        assertEquals(1, attribute.getColumnNameSize("Drawer"));
        assertEquals(1, attribute.getSqlTypes().length);
        assertEquals(Types.INTEGER, attribute.getSqlTypes()[0]);
        Object[] values = attribute.getAllValues(drawer);
        assertEquals(1, values.length);
        assertEquals(10, values[0]);

        attribute = attributes[1];
        assertEquals("_drawersBackref", attribute.getName());
        assertFalse(attribute.isSelectable());
        assertTrue(attribute.isReadTarget());
        assertEquals(1, attribute.getTableNameSize());
        assertTrue(attribute.hasTableName("Drawer"));
        assertEquals(1, attribute.getColumnNameSize("Drawer"));
        assertEquals(1, attribute.getSqlTypes().length);
        assertEquals(Types.INTEGER, attribute.getSqlTypes()[0]);
        values = attribute.getAllValues(drawer);
        assertEquals(1, values.length);
        assertEquals(BackrefPropertyAccessor.UNKNOWN, values[0]);

        attribute = attributes[2];
        assertEquals("_drawersIndexBackref", attribute.getName());
        assertFalse(attribute.isSelectable());
        assertTrue(attribute.isReadTarget());
        assertEquals(1, attribute.getTableNameSize());
        assertTrue(attribute.hasTableName("Drawer"));
        assertEquals(1, attribute.getColumnNameSize("Drawer"));
        assertEquals(1, attribute.getSqlTypes().length);
        assertEquals(Types.INTEGER, attribute.getSqlTypes()[0]);
        values = attribute.getAllValues(drawer);
        assertEquals(1, values.length);
        assertEquals(BackrefPropertyAccessor.UNKNOWN, values[0]);
    }
}
