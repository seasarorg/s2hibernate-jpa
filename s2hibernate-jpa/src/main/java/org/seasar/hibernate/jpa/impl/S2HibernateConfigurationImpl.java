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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.seasar.framework.autodetector.ClassAutoDetector;
import org.seasar.framework.autodetector.ResourceAutoDetector;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.ClassTraversal.ClassHandler;
import org.seasar.framework.util.ResourceTraversal.ResourceHandler;
import org.seasar.framework.util.tiger.CollectionsUtil;
import org.seasar.hibernate.jpa.S2HibernateConfiguration;

/**
 * @author taedium
 */
public class S2HibernateConfigurationImpl implements S2HibernateConfiguration {

    protected Map<String, List<String>> mappingFiles = CollectionsUtil
            .newHashMap();

    protected Map<String, List<Class<?>>> persistenceClasses = CollectionsUtil
            .newHashMap();

    protected Map<String, List<ResourceAutoDetector>> mappingFileAutoDetectors = CollectionsUtil
            .newHashMap();

    protected Map<String, List<ClassAutoDetector>> persistenceClassAutoDetectors = CollectionsUtil
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
        if (!persistenceClassAutoDetectors.containsKey(unitName)) {
            persistenceClassAutoDetectors.put(unitName,
                    new ArrayList<ClassAutoDetector>());
        }
        persistenceClassAutoDetectors.get(unitName).add(detector);
    }

    public void detectMappingFiles(final ResourceHandler handler) {
        detectMappingFiles(null, handler);
    }

    public void detectMappingFiles(final String unitName,
            final ResourceHandler handler) {
        if (mappingFiles.containsKey(unitName)) {
            for (final String mappingFile : mappingFiles.get(unitName)) {
                handler.processResource(mappingFile, null);
            }
        }
        if (mappingFileAutoDetectors.containsKey(unitName)) {
            for (final ResourceAutoDetector detector : mappingFileAutoDetectors
                    .get(unitName)) {
                detector.detect(handler);
            }
        }
    }

    public void detectPersistenceClasses(final ClassHandler visitor) {
        detectPersistenceClasses(null, visitor);
    }

    public void detectPersistenceClasses(final String unitName,
            final ClassHandler handler) {
        if (persistenceClasses.containsKey(unitName)) {
            for (final Class<?> clazz : persistenceClasses.get(unitName)) {
                handler.processClass(ClassUtil.getPackageName(clazz), ClassUtil
                        .getShortClassName(clazz));
            }
        }
        if (persistenceClassAutoDetectors.containsKey(unitName)) {
            for (final ClassAutoDetector detector : persistenceClassAutoDetectors
                    .get(unitName)) {
                detector.detect(handler);
            }
        }
    }

    public boolean isAutoDetection() {
        return persistenceClassAutoDetectors.size() > 0
                || mappingFileAutoDetectors.size() > 0;
    }

}
