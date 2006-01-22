package org.seasar.hibernate.jpa;

import javax.persistence.EntityManager;

import org.seasar.extension.unit.S2TestCase;

/**
 * @author higa
 *
 */
public class EntityManagerTest extends S2TestCase {

	private EntityManager em;
	
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		include("j2ee.dicon");
		include("s2hibernate-jpa.dicon");
	}

	public void testLookup() throws Exception {
		assertNotNull("1", em);
	}
	
	public void testFind() throws Exception {
		assertNotNull("1", em.find(Department.class, 10));
	}
	
	public void testUpdateTx() throws Exception {
		Department dept = em.find(Department.class, 10);
		dept.setDname(dept.getDname() + 2);
		em.flush();
	}
}