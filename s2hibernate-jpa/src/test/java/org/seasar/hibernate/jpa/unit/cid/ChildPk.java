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
package org.seasar.hibernate.jpa.unit.cid;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

/**
 * 
 * @author taedium
 */
@Embeddable
public class ChildPk implements Serializable {

    public int nthChild;

    @ManyToOne
    public Parent parent;

    public int getNthChild() {
        return nthChild;
    }

    public void setNthChild(int nthChild) {
        this.nthChild = nthChild;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ChildPk))
            return false;
        ChildPk castOther = ChildPk.class.cast(other);
        return getNthChild() == (castOther.getNthChild())
                && getParent().equals(castOther.getParent());
    }

    @Override
    public int hashCode() {
        int result;
        result = getNthChild();
        result = 29 * result + getParent().hashCode();
        return result;
    }
}
