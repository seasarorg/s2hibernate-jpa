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
package org.seasar.hibernate.jpa.metadata;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.jpa.metadata.EntityDesc;
import org.seasar.framework.jpa.metadata.EntityDescFactory;
import org.seasar.hibernate.jpa.Department;
import org.seasar.hibernate.jpa.Employee;

/**
 * 
 * @author koichik
 */
public class HibernateEntityDescProviderTest extends S2TestCase {

    @Override
    protected void setUp() throws Exception {
        include("j2ee.dicon");
        include("jpa.dicon");
    }

    public void testEntity() throws Exception {
        EntityDesc entityDesc = EntityDescFactory
                .getEntityDesc(Department.class);
        assertNotNull(entityDesc);

        entityDesc = EntityDescFactory.getEntityDesc(Employee.class);
        assertNotNull(entityDesc);
    }

    public void testNotEntity() throws Exception {
        EntityDesc entityDesc = EntityDescFactory.getEntityDesc(Object.class);
        assertNull(entityDesc);
    }

}
