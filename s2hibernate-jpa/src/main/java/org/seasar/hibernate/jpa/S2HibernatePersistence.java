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

    @SuppressWarnings("unchecked")
    public EntityManagerFactory createEntityManagerFactory(String unitName,
            Map map) {

        final Ejb3Configuration hibernateCfg = new Ejb3Configuration();

        try {
            map = map == null ? new HashMap() : new HashMap(map);
            final ClassLoader loader = Thread.currentThread()
                    .getContextClassLoader();
            final Enumeration<URL> xmls = loader
                    .getResources("META-INF/persistence.xml");

            while (xmls.hasMoreElements()) {
                final URL url = xmls.nextElement();
                final AnnotationConfiguration annCfg = hibernateCfg
                        .getHibernateConfiguration();
                final List<PersistenceMetadata> metadataFiles = PersistenceXmlLoader
                        .deploy(url, map, annCfg.getEntityResolver());
                for (PersistenceMetadata metadata : metadataFiles) {
                    if (IMPLEMENTATION_NAME.equalsIgnoreCase(metadata
                            .getProvider())) {
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

        final JpaConfiguration jpaCfg = getJpaConfiguration();
        if (jpaCfg != null) {
            addMappingFiles(unitName, hibernateCfg, jpaCfg);
            addAnnotatedClasses(unitName, hibernateCfg, jpaCfg);
        }

        map.put(HibernatePersistence.PROVIDER, HibernatePersistence.class
                .getName());
        return hibernateCfg.createEntityManagerFactory(unitName, map);
    }

    protected JpaConfiguration getJpaConfiguration() {
        if (SingletonS2ContainerFactory.hasContainer()) {
            final S2Container container = SingletonS2ContainerFactory
                    .getContainer();
            if (container.hasComponentDef(JpaConfiguration.class)) {
                final Object cfg = container
                        .getComponent(JpaConfiguration.class);
                return JpaConfiguration.class.cast(cfg);
            }
        }
        return null;
    }

    protected void addMappingFiles(final String unitName,
            final Ejb3Configuration hibernateCfg, final JpaConfiguration jpaCfg) {

        for (final String file : jpaCfg.getMappingFiles()) {
            hibernateCfg.addResource(file);
        }
        for (final String file : jpaCfg.getMappingFiles(unitName)) {
            hibernateCfg.addResource(file);
        }
    }

    protected void addAnnotatedClasses(final String unitName,
            final Ejb3Configuration hibernateCfg, final JpaConfiguration jpaCfg) {

        for (final Class<?> clazz : jpaCfg.getAnnotatedClasses()) {
            hibernateCfg.addAnnotatedClass(clazz);
        }
        for (final Class<?> clazz : jpaCfg.getAnnotatedClasses(unitName)) {
            hibernateCfg.addAnnotatedClass(clazz);
        }
    }
}
