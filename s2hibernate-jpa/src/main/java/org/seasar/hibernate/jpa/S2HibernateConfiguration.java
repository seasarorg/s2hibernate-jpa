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

import org.seasar.framework.autodetector.ClassAutoDetector.ClassHandler;
import org.seasar.framework.autodetector.ResourceAutoDetector.ResourceHandler;

/**
 * @author taedium
 */
public interface S2HibernateConfiguration {

    void addMappingFile(final String fileName);

    void addMappingFile(final String unitName, final String fileName);

    void addPersistenceClass(final Class<?> clazz);

    void addPersistenceClass(final String unitName, final Class<?> clazz);

    void detectMappingFiles(ResourceHandler visitor);

    void detectMappingFiles(final String unitName, ResourceHandler visitor);

    void detectPersistenceClasses(ClassHandler visitor);

    void detectPersistenceClasses(final String unitName, ClassHandler visitor);

    boolean isAutoDetection();

}
