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
import java.util.Set;

import javax.persistence.EntityManagerFactory;

import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.ejb.HibernatePersistence;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.framework.container.annotation.tiger.DestroyMethod;
import org.seasar.framework.container.annotation.tiger.InitMethod;
import org.seasar.framework.jpa.PersistenceUnitManager;
import org.seasar.framework.jpa.PersistenceUnitProvider;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.ClassTraversal.ClassHandler;
import org.seasar.framework.util.ResourceTraversal.ResourceHandler;
import org.seasar.framework.util.tiger.CollectionsUtil;
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

    @Binding(bindingType = BindingType.MUST)
    public void setPersistenceUnitManager(
            final PersistenceUnitManager persistenceUnitManager) {
        this.persistenceUnitManager = persistenceUnitManager;
    }

    @Binding(bindingType = BindingType.MAY)
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
        s2HibernateCfg.detectMappingFiles(unitName, new MappingFileHandler(
                unitName, ejb3Cfg));
    }

    protected void addAnnotatedClasses(final String unitName,
            final Ejb3Configuration ejb3Cfg) {
        s2HibernateCfg.detectPersistenceClasses(unitName,
                new PersistenceClassHandler(unitName, ejb3Cfg));
    }

    public class MappingFileHandler implements ResourceHandler {

        protected String unitName;

        protected Ejb3Configuration ejb3Cfg;

        public MappingFileHandler(final String unitName,
                final Ejb3Configuration ejb3Cfg) {
            this.unitName = unitName;
            this.ejb3Cfg = ejb3Cfg;
        }

        public void processResource(final String path, final InputStream is) {
            if (logger.isDebugEnabled()) {
                logger.log("DHBNJPA0004", new Object[] { path, unitName });
            }
            if (is != null) {
                ejb3Cfg.addInputStream(is);
            } else {
                ejb3Cfg.addResource(path);
            }
        }

    }

    public class PersistenceClassHandler implements ClassHandler {

        protected String unitName;

        protected Ejb3Configuration ejb3Cfg;

        protected final Set<String> packageNames = CollectionsUtil.newHashSet();

        public PersistenceClassHandler(final String unitName,
                final Ejb3Configuration ejb3Cfg) {
            this.unitName = unitName;
            this.ejb3Cfg = ejb3Cfg;
        }

        public void processClass(final String packageName,
                final String shortClassName) {
            final String className = ClassUtil.concatName(packageName,
                    shortClassName);
            final Class<?> clazz = ReflectionUtil.forNameNoException(className);
            if (logger.isDebugEnabled()) {
                logger.log("DHBNJPA0002", new Object[] { className, unitName });
            }
            ejb3Cfg.addAnnotatedClass(clazz);
            if (!packageNames.contains(packageName)) {
                addPackageInfo(packageName);
            }
        }

        protected void addPackageInfo(final String packageName) {
            packageNames.add(packageName);
            final String pkgInfoName = ClassUtil.concatName(packageName,
                    "package-info");
            final Class<?> pkgInfoClass = ReflectionUtil
                    .forNameNoException(pkgInfoName);
            if (pkgInfoClass != null) {
                if (logger.isDebugEnabled()) {
                    logger.log("DHBNJPA0006", new Object[] { packageName,
                            unitName });
                }
                ejb3Cfg.addPackage(packageName);
            }
        }

    }

}
