package org.seasar.hibernate3;

import javax.persistence.EntityManagerFactory;

import org.seasar.extension.unit.S2TestCase;

/**
 * @author higa
 *
 */
public class EntityManagerFactoryTest extends S2TestCase {

	private EntityManagerFactory emf;
	
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		include("j2ee.dicon");
		include("s2hibernate3.dicon");
	}

	public void testLookup() throws Exception {
		assertNotNull("1", emf);
	}
}