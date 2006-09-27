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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.seasar.framework.autodetector.ClassAutoDetector;
import org.seasar.framework.autodetector.ResourceAutoDetector;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.tiger.CollectionsUtil;
import org.seasar.hibernate.jpa.S2HibernateConfiguration;

/**
 * @author taedium
 */
public class S2HibernateConfigurationImpl implements S2HibernateConfiguration {

    protected static final Logger logger = Logger
            .getLogger(S2HibernateConfigurationImpl.class);

    protected Map<String, List<String>> mappingFiles = CollectionsUtil
            .newHashMap();

    protected Map<String, List<InputStream>> mappingFileStreams = CollectionsUtil
            .newHashMap();

    protected Map<String, List<Class<?>>> persistenceClasses = CollectionsUtil
            .newHashMap();

    protected Map<String, List<ResourceAutoDetector>> mappingFileAutoDetectors = CollectionsUtil
            .newHashMap();

    protected Map<String, List<ClassAutoDetector>> persistenceClassAutoDetectors = CollectionsUtil
            .newHashMap();

    protected boolean autoDetection;

    public void addMappingFile(final String fileName) {
        addMappingFile(null, fileName);
    }

    public void addMappingFile(final String unitName, final String fileName) {
        if (!mappingFiles.containsKey(unitName)) {
            mappingFiles.put(unitName, new ArrayList<String>());
        }
        mappingFiles.get(unitName).add(fileName);
    }

    public void addMappingFileStream(final InputStream inputStream) {
        addMappingFileStream(null, inputStream);
    }

    public void addMappingFileStream(final String unitName,
            final InputStream inputStream) {
        if (!mappingFileStreams.containsKey(unitName)) {
            mappingFileStreams.put(unitName, new ArrayList<InputStream>());
        }
        mappingFileStreams.get(unitName).add(inputStream);
    }

    public void addPersistenceClass(final Class<?> clazz) {
        addPersistenceClass(null, clazz);
    }

    public void addPersistenceClass(final String unitName, final Class<?> clazz) {
        if (!persistenceClasses.containsKey(unitName)) {
            persistenceClasses.put(unitName, new ArrayList<Class<?>>());
        }
        persistenceClasses.get(unitName).add(clazz);
    }

    public void setMappingFileAutoDetector(
            final ResourceAutoDetector[] resourceAutoDetectors) {
        for (final ResourceAutoDetector detector : resourceAutoDetectors) {
            addMappingFileAutoDetector(detector);
        }
    }

    public void addMappingFileAutoDetector(final ResourceAutoDetector detector) {
        addMappingFileAutoDetector(null, detector);
    }

    public void addMappingFileAutoDetector(final String unitName,
            final ResourceAutoDetector detector) {
        autoDetection = true;
        if (!mappingFileAutoDetectors.containsKey(unitName)) {
            mappingFileAutoDetectors.put(unitName,
                    new ArrayList<ResourceAutoDetector>());
        }
        mappingFileAutoDetectors.get(unitName).add(detector);
    }

    public void setPersistenceClassAutoDetector(
            final ClassAutoDetector[] detectors) {
        for (final ClassAutoDetector detector : detectors) {
            addPersistenceClassAutoDetector(detector);
        }
    }

    public void addPersistenceClassAutoDetector(final ClassAutoDetector detector) {
        addPersistenceClassAutoDetector(null, detector);
    }

    public void addPersistenceClassAutoDetector(final String unitName,
            final ClassAutoDetector detector) {
        autoDetection = true;
        if (!persistenceClassAutoDetectors.containsKey(unitName)) {
            persistenceClassAutoDetectors.put(unitName,
                    new ArrayList<ClassAutoDetector>());
        }
        persistenceClassAutoDetectors.get(unitName).add(detector);
    }

    public List<String> getMappingFiles() {
        return getMappingFiles(null);
    }

    public List<String> getMappingFiles(final String unitName) {
        if (mappingFiles.containsKey(unitName)) {
            return mappingFiles.get(unitName);
        }
        return Collections.emptyList();
    }

    public List<InputStream> getMappingFileStreams() {
        return getMappingFileStreams(null);
    }

    public List<InputStream> getMappingFileStreams(final String unitName) {
        final List<InputStream> result = CollectionsUtil.newArrayList();
        if (mappingFileStreams.containsKey(unitName)) {
            result.addAll(mappingFileStreams.get(unitName));
        }
        if (mappingFileAutoDetectors.containsKey(unitName)) {
            result.addAll(detectMappingFileStreams(null));
        }
        return result;
    }

    public List<Class<?>> getPersistenceClasses() {
        return getPersistenceClasses(null);
    }

    public List<Class<?>> getPersistenceClasses(final String unitName) {
        final List<Class<?>> result = CollectionsUtil.newArrayList();
        if (persistenceClasses.containsKey(unitName)) {
            result.addAll(persistenceClasses.get(unitName));
        }
        if (persistenceClassAutoDetectors.containsKey(unitName)) {
            result.addAll(detectPersistenceClasses(unitName));
        }
        return result;
    }

    public boolean isAutoDetection() {
        return autoDetection;
    }

    protected List<InputStream> detectMappingFileStreams(final String unitName) {
        final List<InputStream> result = CollectionsUtil.newArrayList();
        for (final ResourceAutoDetector detector : mappingFileAutoDetectors
                .get(unitName)) {
            for (final ResourceAutoDetector.Entry entry : detector.detect()) {
                if (logger.isDebugEnabled()) {
                    if (unitName == null) {
                        logger.log("DHBNJPA0003", new Object[] { entry
                                .getPath() });
                    } else {
                        logger.log("DHBNJPA0004", new Object[] {
                                entry.getPath(), unitName });
                    }
                }
                result.add(entry.getInputStream());
            }
        }
        return result;
    }

    protected List<Class<?>> detectPersistenceClasses(final String unitName) {
        final List<Class<?>> result = CollectionsUtil.newArrayList();
        for (final ClassAutoDetector detector : persistenceClassAutoDetectors
                .get(unitName)) {
            for (final Class clazz : detector.detect()) {
                if (logger.isDebugEnabled()) {
                    if (unitName == null) {
                        logger.log("DHBNJPA0001", new Object[] { clazz
                                .getName() });
                    } else {
                        logger.log("DHBNJPA0002", new Object[] {
                                clazz.getName(), unitName });
                    }
                }
                result.add(clazz);
            }
        }
        return result;
    }

}
