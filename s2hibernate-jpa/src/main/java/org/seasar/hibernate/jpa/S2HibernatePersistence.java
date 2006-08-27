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
package org.seasar.hibernate.jpa;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.spi.PersistenceUnitInfo;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.ejb.HibernatePersistence;
import org.hibernate.ejb.packaging.PersistenceMetadata;
import org.hibernate.ejb.packaging.PersistenceXmlLoader;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

/**
 * 
 * @author taedium
 */
public class S2HibernatePersistence implements
        javax.persistence.spi.PersistenceProvider {

    private static final String IMPLEMENTATION_NAME = S2HibernatePersistence.class
            .getName();

    private static final String PERSISTENCE_FILE_PATH = "META-INF/persistence.xml";

    @SuppressWarnings("unchecked")
    public EntityManagerFactory createEntityManagerFactory(String unitName,
            Map map) {

        try {
            map = map == null ? new HashMap() : new HashMap(map);
            final ClassLoader loader = Thread.currentThread()
                    .getContextClassLoader();
            final Enumeration<URL> xmls = loader
                    .getResources(PERSISTENCE_FILE_PATH);

            while (xmls.hasMoreElements()) {
                final URL url = xmls.nextElement();
                final Ejb3Configuration hibernateCfg = new Ejb3Configuration();
                final AnnotationConfiguration annCfg = hibernateCfg
                        .getHibernateConfiguration();
                final List<PersistenceMetadata> metadataFiles = PersistenceXmlLoader
                        .deploy(url, map, annCfg.getEntityResolver());
                for (PersistenceMetadata metadata : metadataFiles) {
                    if (IMPLEMENTATION_NAME.equalsIgnoreCase(metadata
                            .getProvider())
                            && metadata.getName().equals(unitName)) {
                        return createEntityManagerFactory(unitName, map,
                                hibernateCfg);
                    }
                }
            }
            return null;

        } catch (Exception e) {
            if (e instanceof PersistenceException) {
                throw (PersistenceException) e;
            }
            throw new PersistenceException(e);
        }
    }

    public EntityManagerFactory createContainerEntityManagerFactory(
            PersistenceUnitInfo info, Map map) {

        throw new UnsupportedOperationException(
                "createContainerEntityManagerFactory");
    }

    @SuppressWarnings("unchecked")
    protected EntityManagerFactory createEntityManagerFactory(
            final String unitName, final Map map,
            final Ejb3Configuration hibernateCfg) {

        final S2HibernateConfiguration s2HibernateCfg = getS2HibernateConfiguration();
        if (s2HibernateCfg != null) {
            addMappingFiles(unitName, hibernateCfg, s2HibernateCfg);
            addMappingFileStreams(unitName, hibernateCfg, s2HibernateCfg);
            addAnnotatedClasses(unitName, hibernateCfg, s2HibernateCfg);
            if (s2HibernateCfg.isAutoDetection()) {
                map.put(HibernatePersistence.AUTODETECTION, "");
            }
        }

        map.put(HibernatePersistence.PROVIDER, HibernatePersistence.class
                .getName());
        return hibernateCfg.createEntityManagerFactory(unitName, map);
    }

    protected S2HibernateConfiguration getS2HibernateConfiguration() {
        if (SingletonS2ContainerFactory.hasContainer()) {
            final S2Container container = SingletonS2ContainerFactory
                    .getContainer();
            if (container.hasComponentDef(S2HibernateConfiguration.class)) {
                final Object cfg = container
                        .getComponent(S2HibernateConfiguration.class);
                return S2HibernateConfiguration.class.cast(cfg);
            }
        }
        return null;
    }

    protected void addMappingFiles(final String unitName,
            final Ejb3Configuration hibernateCfg,
            final S2HibernateConfiguration s2HibernateCfg) {

        for (final String fileName : s2HibernateCfg.getMappingFiles()) {
            hibernateCfg.addResource(fileName);
        }
        for (final String file : s2HibernateCfg.getMappingFiles(unitName)) {
            hibernateCfg.addResource(file);
        }
    }

    protected void addMappingFileStreams(final String unitName,
            final Ejb3Configuration hibernateCfg,
            final S2HibernateConfiguration s2HibernateCfg) {

        for (final InputStream is : s2HibernateCfg.getMappingFileStreams()) {
            hibernateCfg.addInputStream(is);
        }
        for (final InputStream is : s2HibernateCfg
                .getMappingFileStreams(unitName)) {
            hibernateCfg.addInputStream(is);
        }
    }

    protected void addAnnotatedClasses(final String unitName,
            final Ejb3Configuration hibernateCfg,
            final S2HibernateConfiguration s2HibernateCfg) {

        for (final Class<?> clazz : s2HibernateCfg.getPersistenceClasses()) {
            hibernateCfg.addAnnotatedClass(clazz);
        }
        for (final Class<?> clazz : s2HibernateCfg
                .getPersistenceClasses(unitName)) {
            hibernateCfg.addAnnotatedClass(clazz);
        }
    }
}
