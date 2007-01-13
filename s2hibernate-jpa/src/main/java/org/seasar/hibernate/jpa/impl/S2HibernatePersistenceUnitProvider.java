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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.ejb.HibernatePersistence;
import org.seasar.framework.container.annotation.tiger.DestroyMethod;
import org.seasar.framework.container.annotation.tiger.InitMethod;
import org.seasar.framework.jpa.PersistenceUnitManager;
import org.seasar.framework.jpa.PersistenceUnitProvider;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.framework.util.ClassTraversal.ClassHandler;
import org.seasar.framework.util.ResourceTraversal.ResourceHandler;
import org.seasar.framework.util.tiger.ReflectionUtil;
import org.seasar.hibernate.jpa.S2HibernateConfiguration;

/**
 * @author koichik
 */
public class S2HibernatePersistenceUnitProvider implements
        PersistenceUnitProvider {

    private static final Logger logger = Logger
            .getLogger(S2HibernatePersistenceUnitProvider.class);

    protected PersistenceUnitManager persistenceUnitManager;

    protected S2HibernateConfiguration s2HibernateCfg;

    public void setPersistenceUnitManager(
            PersistenceUnitManager persistenceUnitManager) {
        this.persistenceUnitManager = persistenceUnitManager;
    }

    public void setS2HibernateConfiguration(
            final S2HibernateConfiguration s2HibernateCfg) {
        this.s2HibernateCfg = s2HibernateCfg;
    }

    @InitMethod
    public void register() {
        persistenceUnitManager.addProvider(this);
    }

    @DestroyMethod
    public void unregister() {
        persistenceUnitManager.removeProvider(this);
    }

    public EntityManagerFactory createEntityManagerFactory(final String unitName) {
        final Ejb3Configuration ejb3Cfg = new Ejb3Configuration();
        final Map<String, String> map = new HashMap<String, String>();
        if (s2HibernateCfg != null) {
            addMappingFiles(unitName, ejb3Cfg);
            addAnnotatedClasses(unitName, ejb3Cfg);
            if (s2HibernateCfg.isAutoDetection()) {
                map.put(HibernatePersistence.AUTODETECTION, "");
            }
        }
        ejb3Cfg.configure(unitName, map);
        return ejb3Cfg.buildEntityManagerFactory();
    }

    protected void addMappingFiles(final String unitName,
            final Ejb3Configuration ejb3Cfg) {
        final ResourceHandler handler = new ResourceHandler() {

            public void processResource(final String path, final InputStream is) {
                if (logger.isDebugEnabled()) {
                    if (unitName == null) {
                        logger.log("DHBNJPA0003", new Object[] { path });
                    } else {
                        logger.log("DHBNJPA0004",
                                new Object[] { path, unitName });
                    }
                }
                if (is != null) {
                    ejb3Cfg.addInputStream(is);
                } else if (!StringUtil.isEmpty(path)) {
                    ejb3Cfg.addResource(path);
                }
            }
        };
        s2HibernateCfg.detectMappingFiles(handler);
        if (!StringUtil.isEmpty(unitName)) {
            s2HibernateCfg.detectMappingFiles(unitName, handler);
        }
    }

    protected void addAnnotatedClasses(final String unitName,
            final Ejb3Configuration ejb3Cfg) {
        final ClassHandler handler = new ClassHandler() {

            public void processClass(final String packageName,
                    final String shortClassName) {
                final String className = ClassUtil.concatName(packageName,
                        shortClassName);
                Class<?> clazz = ReflectionUtil.forNameNoException(className);
                if (logger.isDebugEnabled()) {
                    if (unitName == null) {
                        logger.log("DHBNJPA0001", new Object[] { className });
                    } else {
                        logger.log("DHBNJPA0002", new Object[] { className,
                                unitName });
                    }
                }
                ejb3Cfg.addAnnotatedClass(clazz);
            }
        };
        s2HibernateCfg.detectPersistenceClasses(handler);
        if (!StringUtil.isEmpty(unitName)) {
            s2HibernateCfg.detectPersistenceClasses(unitName, handler);
        }
    }

}
