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
package org.seasar.hibernate.jpa.impl;

import java.sql.Connection;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.framework.container.annotation.tiger.DestroyMethod;
import org.seasar.framework.container.annotation.tiger.InitMethod;
import org.seasar.framework.jpa.Dialect;
import org.seasar.framework.jpa.DialectManager;

/**
 * Hibernate用の{@link Dialect}です。
 * 
 * @author koichik
 */
public class S2HibernateDialect implements Dialect {

    /** {@link Dialect}のマネージャー */
    @Binding(bindingType = BindingType.MUST)
    protected DialectManager dialectManager;

    /**
     * {@link DialectManager}に自身を登録します。
     */
    @InitMethod
    public void initialize() {
        dialectManager.addDialect(Session.class, this);
    }

    /**
     * {@link DialectManager}から自身を除去します。
     */
    @DestroyMethod
    public void destroy() {
        dialectManager.removeDialect(Session.class);
    }

    public Connection getConnection(final EntityManager em) {
        final Session session = Session.class.cast(em.getDelegate());
        return session.connection();
    }

    public void detach(final EntityManager em, final Object managedEntity) {
        final Session session = Session.class.cast(em.getDelegate());
        session.evict(managedEntity);
    }

}
