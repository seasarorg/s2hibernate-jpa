package org.seasar.hibernate.jpa.transaction;

import java.util.Properties;

import javax.transaction.TransactionManager;

import org.hibernate.HibernateException;
import org.hibernate.transaction.TransactionManagerLookup;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

public class S2TransactionManagerLookup implements TransactionManagerLookup {

	public TransactionManager getTransactionManager(Properties props)
			throws HibernateException {

		return (TransactionManager) SingletonS2ContainerFactory.getContainer()
				.getComponent(TransactionManager.class);
	}

	public String getUserTransactionName() {
		return "j2ee.transactionManager";
	}

}
