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
package org.seasar.hibernate.jpa.unit;

import java.util.Collection;

import javax.persistence.EntityManager;

import org.hibernate.persister.entity.AbstractEntityPersister;

/**
 * 
 * @author taedium
 */
public class HibernateEntityCollectionReader extends HibernateEntityReader {

    public HibernateEntityCollectionReader(final Collection<?> entities,
            final EntityManager em, final AbstractEntityPersister persister) {

        super(em, persister);
        setupColumns();
        for (Object entity : entities) {
            setupRow(entity);
        }
    }
}
