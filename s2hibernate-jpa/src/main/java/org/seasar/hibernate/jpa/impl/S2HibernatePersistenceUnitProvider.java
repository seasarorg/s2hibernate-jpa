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
import org.seasar.hibernate.jpa.S2HibernateConfiguration;

/**
 * @author koichik
 */
public class S2HibernatePersistenceUnitProvider implements
        PersistenceUnitProvider {

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
            addMappingFileStreams(unitName, ejb3Cfg);
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

        for (final String fileName : s2HibernateCfg.getMappingFiles()) {
            ejb3Cfg.addResource(fileName);
        }
        for (final String file : s2HibernateCfg.getMappingFiles(unitName)) {
            ejb3Cfg.addResource(file);
        }
    }

    protected void addMappingFileStreams(final String unitName,
            final Ejb3Configuration ejb3Cfg) {

        for (final InputStream is : s2HibernateCfg.getMappingFileStreams()) {
            ejb3Cfg.addInputStream(is);
        }
        for (final InputStream is : s2HibernateCfg
                .getMappingFileStreams(unitName)) {
            ejb3Cfg.addInputStream(is);
        }
    }

    protected void addAnnotatedClasses(final String unitName,
            final Ejb3Configuration ejb3Cfg) {

        for (final Class<?> clazz : s2HibernateCfg.getPersistenceClasses()) {
            ejb3Cfg.addAnnotatedClass(clazz);
        }
        for (final Class<?> clazz : s2HibernateCfg
                .getPersistenceClasses(unitName)) {
            ejb3Cfg.addAnnotatedClass(clazz);
        }
    }

}
