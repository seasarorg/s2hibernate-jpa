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

import javax.persistence.EntityManager;

import org.hibernate.persister.entity.JoinedSubclassEntityPersister;

/**
 * 
 * @author taedium
 */
public class JoinedSubclassEntityReader extends AbstractEntityReader {

    private JoinedSubclassEntityPersister persister;

    public JoinedSubclassEntityReader(final Object entity,
            final EntityManager em,
            final JoinedSubclassEntityPersister persister) {

        super(em, persister);
        this.persister = persister;
        setupColumns();
        setupRow(entity);
    }

    protected int getTableSpan() {
        return persister.getTableSpan();
    }

    protected String getSubclassTableName(int index) {
        return persister.getSubclassTableName(index);
    }

}
