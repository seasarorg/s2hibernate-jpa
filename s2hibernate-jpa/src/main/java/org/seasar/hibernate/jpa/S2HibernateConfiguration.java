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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.seasar.framework.autodetector.ResourceAutoDetector;
import org.seasar.framework.container.annotation.tiger.Component;
import org.seasar.framework.util.tiger.CollectionsUtil;

/**
 * 
 * @author taedium
 */
@Component
public class S2HibernateConfiguration {

    private Map<String, List<String>> mappingFiles = CollectionsUtil
            .newHashMap();

    private Map<String, List<Class<?>>> annotatedClasses = CollectionsUtil
            .newHashMap();

    private Map<String, ResourceAutoDetector> resourceAutoDetectors = CollectionsUtil
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

    public void addAnnotatedClass(final Class<?> clazz) {
        addAnnotatedClass(null, clazz);
    }

    public void addAnnotatedClass(final String unitName, final Class<?> clazz) {
        if (!annotatedClasses.containsKey(unitName)) {
            annotatedClasses.put(unitName, new ArrayList<Class<?>>());
        }
        annotatedClasses.get(unitName).add(clazz);
    }

    public void addResourceAutoDetector(final ResourceAutoDetector detector) {

        addResourceAutoDetector(null, detector);
    }

    public void addResourceAutoDetector(final String unitName,
            final ResourceAutoDetector detector) {

        resourceAutoDetectors.put(unitName, detector);
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

    public List<Class<?>> getAnnotatedClasses() {
        return getAnnotatedClasses(null);
    }

    public List<Class<?>> getAnnotatedClasses(final String unitName) {
        if (annotatedClasses.containsKey(unitName)) {
            return annotatedClasses.get(unitName);
        }
        return Collections.emptyList();
    }

    public ResourceAutoDetector getRsourceAutoDetector() {
        return getRsourceAutoDetector(null);
    }

    public ResourceAutoDetector getRsourceAutoDetector(final String unitName) {
        return resourceAutoDetectors.get(unitName);
    }
}
