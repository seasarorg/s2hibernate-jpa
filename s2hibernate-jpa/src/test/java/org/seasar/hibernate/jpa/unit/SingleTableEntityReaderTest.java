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
package org.seasar.hibernate.jpa.unit;

import java.math.BigDecimal;

import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.seasar.extension.dataset.DataColumn;
import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.extension.dataset.types.ColumnTypes;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.jpa.unit.EntityReader;
import org.seasar.framework.jpa.unit.EntityReaderFactory;
import org.seasar.hibernate.jpa.S2HibernateConfiguration;
import org.seasar.hibernate.jpa.unit.singletable.Company;
import org.seasar.hibernate.jpa.unit.singletable.CompanyPk;
import org.seasar.hibernate.jpa.unit.singletable.Department;
import org.seasar.hibernate.jpa.unit.singletable.Employee;
import org.seasar.hibernate.jpa.unit.singletable.Simple;

/**
 * 
 * @author taedium
 */
public class SingleTableEntityReaderTest extends S2TestCase {

    private S2HibernateConfiguration cfg = new S2HibernateConfiguration();

    private EntityManager em;

    private UserTransaction utx;

    @Override
    protected void setUp() throws Exception {
        include("s2hibernate-jpa.dicon");
    }

    private void persist(Object... entities) throws Exception {
        utx.begin();
        for (final Object entity : entities) {
            em.persist(entity);
        }
        utx.commit();
    }

    private <T> DataSet read(Class<T> entityClass, Object id) throws Exception {
        utx.begin();
        T entity = em.find(entityClass, id);
        EntityReader reader = EntityReaderFactory.getEntityReader(entity);
        assertTrue(SingleTableEntityReader.class.isInstance(reader));
        utx.commit();
        return reader.read();
    }

    public void setUpSimpleEntity() throws Exception {
        cfg.addAnnotatedClasses(Simple.class);
        register(cfg);
    }

    public void testSimpleEntity() throws Exception {
        Simple simple = new Simple();
        simple.setId(1);
        simple.setName("simple");
        persist(simple);

        DataSet dataSet = read(Simple.class, 1);
        assertEquals(1, dataSet.getTableSize());
        assertNotNull(dataSet.getTable("SIMPLE"));

        DataTable table = dataSet.getTable(0);
        assertEquals(2, table.getColumnSize());
        DataColumn idColumn = table.getColumn("id");
        assertNotNull(idColumn);
        assertEquals(ColumnTypes.BIGDECIMAL, idColumn.getColumnType());
        DataColumn nameColumn = table.getColumn("name");
        assertNotNull(nameColumn);
        assertEquals(ColumnTypes.STRING, nameColumn.getColumnType());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(1), row.getValue("id"));
        assertEquals("simple", row.getValue("name"));
    }

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
        DataTable table = dataSet.getTable(0);
        assertEquals(2, table.getColumnSize());
        assertEquals(2, table.getColumnSize());
        DataColumn idColumn1 = table.getColumn("companyCode");
        assertNotNull(idColumn1);
        assertEquals(ColumnTypes.BIGDECIMAL, idColumn1.getColumnType());
        DataColumn idColumn2 = table.getColumn("name");
        assertNotNull(idColumn2);
        assertEquals(ColumnTypes.STRING, idColumn2.getColumnType());
    }

    public void setUpManyToOne() throws Exception {
        cfg.addAnnotatedClasses(Employee.class, Department.class);
        register(cfg);
    }

    public void testManyToOne() throws Exception {
        Department department = new Department();
        department.setId(10);
        department.setName("hoge");
        Employee employee = new Employee();
        employee.setId(1);
        employee.setName("foo");
        employee.setDepartment(department);

        persist(department, employee);
        DataSet dataSet = read(Employee.class, 1);

        DataTable table = dataSet.getTable(0);
        assertEquals(3, table.getColumnSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(1), row.getValue("id"));
        assertEquals("foo", row.getValue("name"));
        assertEquals(new BigDecimal(10), row.getValue("department_id"));
    }

    public void testManyToOneUsingCompositeJoinKeys() {
    }
}
