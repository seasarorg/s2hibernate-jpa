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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.seasar.framework.autodetector.ClassAutoDetector;
import org.seasar.framework.autodetector.ResourceAutoDetector;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.tiger.CollectionsUtil;

/**
 * 
 * @author taedium
 */
public class S2HibernateConfiguration {

    protected static final Logger logger = Logger
            .getLogger(S2HibernateConfiguration.class);

    protected boolean autoDetection;

    protected Map<String, List<String>> mappingFiles = CollectionsUtil
            .newHashMap();

    protected Map<String, List<InputStream>> mappingFileStreams = CollectionsUtil
            .newHashMap();

    protected Map<String, List<Class<?>>> persistenceClasses = CollectionsUtil
            .newHashMap();

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
            final ResourceAutoDetector[] detectors) {
        for (final ResourceAutoDetector detector : detectors) {
            addMappingFileAutoDetector(detector);
        }
    }

    public void addMappingFileAutoDetector(final ResourceAutoDetector detector) {
        addMappingFileAutoDetector(null, detector);
    }

    public void addMappingFileAutoDetector(final String unitName,
            final ResourceAutoDetector detector) {
        autoDetection = true;

        for (final ResourceAutoDetector.Entry entry : detector.detect()) {
            if (logger.isDebugEnabled()) {
                if (unitName == null) {
                    logger.log("DHBNJPA0003", new Object[] { entry.getPath() });
                } else {
                    logger.log("DHBNJPA0004", new Object[] { entry.getPath(),
                            unitName });
                }
            }
            addMappingFileStream(unitName, entry.getInputStream());
        }
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

        for (final Class clazz : detector.detect()) {
            if (logger.isDebugEnabled()) {
                if (unitName == null) {
                    logger.log("DHBNJPA0001", new Object[] { clazz.getName() });
                } else {
                    logger.log("DHBNJPA0002", new Object[] { clazz.getName(),
                            unitName });
                }
            }
            addPersistenceClass(unitName, clazz);
        }
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
        if (mappingFileStreams.containsKey(unitName)) {
            return mappingFileStreams.get(unitName);
        }
        return Collections.emptyList();
    }

    public List<Class<?>> getPersistenceClasses() {
        return getPersistenceClasses(null);
    }

    public List<Class<?>> getPersistenceClasses(final String unitName) {
        if (persistenceClasses.containsKey(unitName)) {
            return persistenceClasses.get(unitName);
        }
        return Collections.emptyList();
    }

    public boolean isAutoDetection() {
        return autoDetection;
    }
}
