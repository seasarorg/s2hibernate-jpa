/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.hibernate.jpa.unit;

import java.util.ArrayList;
import java.util.Collection;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.jpa.unit.EntityReader;
import org.seasar.framework.jpa.unit.EntityReaderFactory;
import org.seasar.hibernate.jpa.Department;
import org.seasar.hibernate.jpa.Employee;

/**
 * 
 * @author taedium
 */
public class HibernateEntityReaderProviderTest extends S2TestCase {

    @Override
    protected void setUp() throws Exception {
        include("jpa.dicon");
    }

    public void testEntity() throws Exception {
        EntityReader entityReader = EntityReaderFactory
                .getEntityReader(new Department());
        assertNotNull(entityReader);

        entityReader = EntityReaderFactory.getEntityReader(new Employee());
        assertNotNull(entityReader);
    }

    public void testNotEntity() throws Exception {
        EntityReader entityReader = EntityReaderFactory
                .getEntityReader(new Object());
        assertNull(entityReader);
    }

    public void testNull() throws Exception {
        EntityReader entityReader = EntityReaderFactory.getEntityReader(null);
        assertNull(entityReader);
    }

    public void testCollection() throws Exception {
        Collection<Department> entities = new ArrayList<Department>();
        entities.add(new Department());
        entities.add(new Department());
        EntityReader entityReader = EntityReaderFactory
                .getEntityReader(entities);
        assertNotNull(entityReader);
    }

    public void testEmptyCollection() throws Exception {
        Collection<Department> entities = new ArrayList<Department>();
        EntityReader entityReader = EntityReaderFactory
                .getEntityReader(entities);
        assertNull(entityReader);
    }

}
