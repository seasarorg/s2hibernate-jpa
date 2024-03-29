/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
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

import java.util.List;

import org.seasar.extension.dataset.DataSet;
import org.seasar.framework.jpa.unit.EntityReader;
import org.seasar.framework.jpa.unit.EntityReaderFactory;

/**
 * 
 * @author taedium
 */
public abstract class HibernateEntityCollectionReaderTestCase extends
        HibernateEntityReaderTestCase {

    protected DataSet read(final String jpql) throws Exception {
        utx.begin();
        List<?> entities = em.createQuery(jpql).getResultList();
        EntityReader reader = EntityReaderFactory.getEntityReader(entities);
        assertNotNull(reader);
        utx.commit();
        return reader.read();
    }

}
