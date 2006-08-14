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

import org.hibernate.persister.collection.AbstractCollectionPersister;
import org.seasar.extension.dataset.DataTable;
import org.seasar.extension.dataset.impl.DataTableImpl;

/**
 * 
 * @author taedium
 */
public class CollectionElementReader {

    protected final AbstractCollectionPersister persister;

    protected final DataTable table;

    public CollectionElementReader(final Object entity,
            final AbstractCollectionPersister persister) {

        this.persister = persister;
        this.table = new DataTableImpl(persister.getTableName());
        setupColumns();
        setupRow();
    }

    protected void setupColumns() {
        persister.getIdentifierColumnName();
        persister.getElementColumnNames();
        persister.getIdentifierType();
        persister.getElementType();
    }

    protected void setupRow() {

    }

    public DataTable read() {
        return null;
    }
}
