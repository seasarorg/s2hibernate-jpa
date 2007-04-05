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
package org.seasar.hibernate.jpa.transaction;

import java.util.Properties;

import javax.transaction.TransactionManager;

import org.hibernate.HibernateException;
import org.hibernate.transaction.TransactionManagerLookup;
import org.seasar.extension.jta.SingletonTransactionManagerProxy;

public class SingletonTransactionManagerProxyLookup implements
        TransactionManagerLookup {

    public TransactionManager getTransactionManager(final Properties props)
            throws HibernateException {
        return new SingletonTransactionManagerProxy();
    }

    public String getUserTransactionName() {
        return "jta.UserTransaction";
    }

}