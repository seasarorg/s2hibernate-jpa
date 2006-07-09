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

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.seasar.framework.container.annotation.tiger.Component;
import org.seasar.framework.util.tiger.CollectionsUtil;
import org.seasar.hibernate.jpa.detector.ClassDetector;

/**
 * 
 * @author taedium
 */
@Component
public class S2HibernateConfiguration implements JpaConfiguration {

    protected Map<String, Set<String>> mappingFiles = CollectionsUtil
            .newHashMap();

    protected Map<String, Set<Class<?>>> annotatedClasses = CollectionsUtil
            .newHashMap();

    public void addMappingFile(final String fileName) {
        addMappingFile(null, fileName);
    }

    public void addMappingFile(final String unitName, final String fileName) {
        if (!mappingFiles.containsKey(unitName)) {
            mappingFiles.put(unitName, new HashSet<String>());
        }
        mappingFiles.get(unitName).add(fileName);
    }

    public Set<String> getMappingFiles() {
        return getMappingFiles(null);
    }

    public Set<String> getMappingFiles(final String unitName) {
        if (mappingFiles.containsKey(unitName)) {
            return mappingFiles.get(unitName);
        }
        return Collections.emptySet();
    }

    public void addAnnotatedClass(final Class<?> clazz) {
        addAnnotatedClass(null, clazz);
    }

    public void addAnnotatedClass(final String unitName, final Class<?> clazz) {

        if (!annotatedClasses.containsKey(unitName)) {
            annotatedClasses.put(unitName, new HashSet<Class<?>>());
        }
        annotatedClasses.get(unitName).add(clazz);
    }

    public void addAnnotatedClasses(final Class<?>[] classes) {
        addAnnotatedClasses(null, classes);
    }

    public void addAnnotatedClasses(final String unitName,
            final Class<?>[] classes) {

        for (final Class<?> clazz : classes) {
            addAnnotatedClass(unitName, clazz);
        }
    }

    public Set<Class<?>> getAnnotatedClasses() {
        return getAnnotatedClasses(null);
    }

    public Set<Class<?>> getAnnotatedClasses(final String unitName) {
        if (annotatedClasses.containsKey(unitName)) {
            return annotatedClasses.get(unitName);
        }
        return Collections.emptySet();
    }

    public void addClassDetector(final ClassDetector classDetector) {
        addClassDetector(null, classDetector);
    }

    public void addClassDetector(final String unitName,
            final ClassDetector classDetector) {

        addAnnotatedClasses(unitName, classDetector.detect());
    }

}
