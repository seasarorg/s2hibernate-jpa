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

import org.seasar.extension.dataset.DataRow;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.DataTable;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.jpa.unit.EntityReader;
import org.seasar.framework.jpa.unit.EntityReaderFactory;
import org.seasar.hibernate.jpa.S2HibernateConfiguration;
import org.seasar.hibernate.jpa.unit.singletable.Child;
import org.seasar.hibernate.jpa.unit.singletable.Company;
import org.seasar.hibernate.jpa.unit.singletable.CompanyPk;
import org.seasar.hibernate.jpa.unit.singletable.Computer;
import org.seasar.hibernate.jpa.unit.singletable.Customer;
import org.seasar.hibernate.jpa.unit.singletable.Department;
import org.seasar.hibernate.jpa.unit.singletable.Employee;
import org.seasar.hibernate.jpa.unit.singletable.KnownClient;
import org.seasar.hibernate.jpa.unit.singletable.Order;
import org.seasar.hibernate.jpa.unit.singletable.OrderLine;
import org.seasar.hibernate.jpa.unit.singletable.Parent;
import org.seasar.hibernate.jpa.unit.singletable.ParentPk;
import org.seasar.hibernate.jpa.unit.singletable.Party;
import org.seasar.hibernate.jpa.unit.singletable.PartyAffiliate;
import org.seasar.hibernate.jpa.unit.singletable.Passport;
import org.seasar.hibernate.jpa.unit.singletable.Project;
import org.seasar.hibernate.jpa.unit.singletable.ProjectPk;
import org.seasar.hibernate.jpa.unit.singletable.SerialNumber;
import org.seasar.hibernate.jpa.unit.singletable.SerialNumberPk;
import org.seasar.hibernate.jpa.unit.singletable.Simple;
import org.seasar.hibernate.jpa.unit.singletable.Store;

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

    private void persist(final Object... entities) throws Exception {
        utx.begin();
        for (final Object entity : entities) {
            em.persist(entity);
        }
        utx.commit();
    }

    private <T> DataSet read(final Class<T> entityClass, final Object id)
            throws Exception {
        utx.begin();
        T entity = em.find(entityClass, id);
        assertNotNull(entity);
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
        assertTrue(dataSet.hasTable("Simple"));

        DataTable table = dataSet.getTable(0);
        assertEquals(2, table.getColumnSize());
        assertEquals(1, table.getRowSize());

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
        assertEquals(1, dataSet.getTableSize());
        assertTrue(dataSet.hasTable("Company"));

        DataTable table = dataSet.getTable(0);
        assertEquals(2, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(999), row.getValue("companyCode"));
        assertEquals("hoge", row.getValue("name"));
    }

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

    public void setUpOneToOneOwnerSide() throws Exception {
        cfg.addAnnotatedClasses(Passport.class, Customer.class);
        register(cfg);
    }

    public void testOneToOneOwnerSide() throws Exception {
        Passport passport = new Passport();
        passport.setId(10);
        passport.setNumber("ABC");
        Customer customer = new Customer();
        customer.setId(90);
        customer.setName("hoge");
        customer.setPassport(passport);
        passport.setOwner(customer);
        persist(passport, customer);

        DataSet dataSet = read(Customer.class, 90);
        assertEquals(1, dataSet.getTableSize());
        assertTrue(dataSet.hasTable("Customer"));

        DataTable table = dataSet.getTable(0);
        assertEquals(3, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(90), row.getValue("id"));
        assertEquals("hoge", row.getValue("name"));
        assertEquals(new BigDecimal(10), row.getValue("passport_id"));
    }

    public void setUpOneToOneInverseSide() throws Exception {
        cfg.addAnnotatedClasses(Passport.class, Customer.class);
        register(cfg);
    }

    public void testOneToOneInverseSide() throws Exception {
        Passport passport = new Passport();
        passport.setId(10);
        passport.setNumber("ABC");
        Customer customer = new Customer();
        customer.setId(90);
        customer.setName("hoge");
        customer.setPassport(passport);
        passport.setOwner(customer);
        persist(passport, customer);

        DataSet dataSet = read(Passport.class, 10);
        assertEquals(1, dataSet.getTableSize());
        assertTrue(dataSet.hasTable("Passport"));

        DataTable table = dataSet.getTable(0);
        assertEquals(2, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
        assertEquals("ABC", row.getValue("number"));
    }

    public void setUpTrueOneToOneOwnerSide() throws Exception {
        cfg.addAnnotatedClasses(Party.class, PartyAffiliate.class);
        register(cfg);
    }

    public void testTrueOneToOneOwnerSide() throws Exception {
        Party party = new Party();
        party.setId(10);
        party.setName("hoge");
        PartyAffiliate affiliate = new PartyAffiliate();
        affiliate.setId(10);
        affiliate.setParty(party);
        affiliate.setAffiliateName("foo");
        persist(party, affiliate);

        DataSet dataSet = read(PartyAffiliate.class, 10);
        assertEquals(1, dataSet.getTableSize());
        assertTrue(dataSet.hasTable("PartyAffiliate"));

        DataTable table = dataSet.getTable(0);
        assertEquals(2, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
        assertEquals("foo", row.getValue("affiliateName"));
    }

    public void setUpOneToOneCompositeFk() throws Exception {
        cfg.addAnnotatedClasses(SerialNumber.class, Computer.class);
        register(cfg);
    }

    public void testOneToOneCompositeFk() throws Exception {
        SerialNumberPk serialNoPk = new SerialNumberPk();
        serialNoPk.setBrand("hoge");
        serialNoPk.setModel("foo");
        SerialNumber serialNo = new SerialNumber();
        serialNo.setId(serialNoPk);
        serialNo.setValue("99999");
        Computer computer = new Computer();
        computer.setId(10);
        computer.setSerialNumber(serialNo);
        persist(serialNo, computer);

        DataSet dataSet = read(Computer.class, 10);
        assertEquals(1, dataSet.getTableSize());
        assertTrue(dataSet.hasTable("Computer"));

        DataTable table = dataSet.getTable(0);
        assertEquals(3, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
        assertEquals("hoge", row.getValue("serialbrand"));
        assertEquals("foo", row.getValue("serialmodel"));
    }

    public void testOneToOneJoinTable() {
        // TODO
    }

    public void setUpTrueOneToOneInverseSide() throws Exception {
        cfg.addAnnotatedClasses(Party.class, PartyAffiliate.class);
        register(cfg);
    }

    public void testTrueOneToOneInverseSide() throws Exception {
        Party party = new Party();
        party.setId(10);
        party.setName("hoge");
        PartyAffiliate affiliate = new PartyAffiliate();
        affiliate.setId(10);
        affiliate.setParty(party);
        affiliate.setAffiliateName("foo");
        persist(party, affiliate);

        DataSet dataSet = read(Party.class, 10);
        assertEquals(1, dataSet.getTableSize());
        assertTrue(dataSet.hasTable("Party"));

        DataTable table = dataSet.getTable(0);
        assertEquals(2, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
        assertEquals("hoge", row.getValue("partyName"));
    }

    public void setUpManyToOneOwnerSide() throws Exception {
        cfg.addAnnotatedClasses(Employee.class, Department.class);
        register(cfg);
    }

    public void testManyToOneOwnerSide() throws Exception {
        Department department = new Department();
        department.setId(10);
        department.setName("hoge");
        Employee employee = new Employee();
        employee.setId(1);
        employee.setName("foo");
        employee.setDepartment(department);
        department.getEmployees().add(employee);
        persist(department, employee);

        DataSet dataSet = read(Employee.class, 1);
        assertEquals(1, dataSet.getTableSize());
        assertTrue(dataSet.hasTable("Employee"));

        DataTable table = dataSet.getTable(0);
        assertEquals(3, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(1), row.getValue("id"));
        assertEquals("foo", row.getValue("name"));
        assertEquals(new BigDecimal(10), row.getValue("department_id"));
    }

    public void setUpManyToOneInverseSide() throws Exception {
        cfg.addAnnotatedClasses(Employee.class, Department.class);
        register(cfg);
    }

    public void testManyToOneInverseSide() throws Exception {
        Department department = new Department();
        department.setId(10);
        department.setName("hoge");
        Employee employee = new Employee();
        employee.setId(1);
        employee.setName("foo");
        employee.setDepartment(department);
        department.getEmployees().add(employee);
        persist(department, employee);

        DataSet dataSet = read(Department.class, 10);
        assertEquals(1, dataSet.getTableSize());
        assertTrue(dataSet.hasTable("Department"));

        DataTable table = dataSet.getTable(0);
        assertEquals(2, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
        assertEquals("hoge", row.getValue("name"));
    }

    public void setUpManyToOneCompositeFk() throws Exception {
        cfg.addAnnotatedClasses(Parent.class, Child.class);
        register(cfg);
    }

    public void testManyToOneCompositeFk() throws Exception {
        ParentPk parentPk = new ParentPk();
        parentPk.setFirstName("hoge");
        parentPk.setLastName("foo");
        Parent parent = new Parent();
        parent.setId(parentPk);
        parent.setAge(50);
        Child child = new Child();
        child.setId(10);
        child.setParent(parent);
        persist(parent, child);

        DataSet dataSet = read(Child.class, 10);
        assertEquals(1, dataSet.getTableSize());
        assertTrue(dataSet.hasTable("Child"));

        DataTable table = dataSet.getTable(0);
        assertEquals(3, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(10), row.getValue("id"));
        assertEquals("hoge", row.getValue("parentFirstName"));
        assertEquals("foo", row.getValue("parentLastName"));
    }

    public void setUpManyToOneNonPk() throws Exception {
        cfg.addAnnotatedClasses(Order.class, OrderLine.class);
        register(cfg);
    }

    public void testManyToOneNonPk() throws Exception {
        Order order = new Order();
        order.setId(10);
        order.setOrderNo("hoge");
        OrderLine orderLine = new OrderLine();
        orderLine.setId(20);
        orderLine.setOrder(order);
        persist(order, orderLine);

        DataSet dataSet = read(OrderLine.class, 20);
        assertEquals(1, dataSet.getTableSize());
        assertTrue(dataSet.hasTable("OrderLine"));

        DataTable table = dataSet.getTable(0);
        assertEquals(2, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(20), row.getValue("id"));
        assertEquals("hoge", row.getValue("orderNo"));
    }

    public void testManyToOneJoinTable() {
        // TODO
    }

    public void setUpManyToMany() throws Exception {
        cfg.addAnnotatedClasses(KnownClient.class, Store.class);
        register(cfg);
    }

    public void testManyToMany() throws Exception {
        KnownClient client = new KnownClient();
        client.setId(10);
        client.setName("hoge");
        Store store = new Store();
        store.setId(20);
        store.setName("foo");
        store.getKnownClients().add(client);
        client.getStores().add(store);
        persist(client, store);

        DataSet dataSet = read(Store.class, 20);
        assertEquals(1, dataSet.getTableSize());
        assertTrue(dataSet.hasTable("Store"));

        DataTable table = dataSet.getTable(0);
        assertEquals(2, table.getColumnSize());
        assertEquals(1, table.getRowSize());

        DataRow row = table.getRow(0);
        assertEquals(new BigDecimal(20), row.getValue("id"));
        assertEquals("foo", row.getValue("name"));
    }

    public void testSecondaryTable() {
        // TODO
    }

    public void testEmbedded() {
        // TODO
    }

    public void testMappedSuperclass() {
        // TODO
    }

    public void testLazyLoad() {
        // TODO
    }

    public void testDescriminatorValue() {
        // TODO
    }

    public void testSingleTableInheritance() {
        // TODO
    }
}
