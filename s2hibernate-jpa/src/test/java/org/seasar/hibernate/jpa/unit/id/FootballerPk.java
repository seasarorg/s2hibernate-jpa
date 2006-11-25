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
package org.seasar.hibernate.jpa.unit.id;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.seasar.hibernate.jpa.unit.cid.ParentPk;

/**
 * 
 * @author taedium
 */
@Embeddable
public class FootballerPk implements Serializable {

    private static final long serialVersionUID = 1L;

    private String firstName;

    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ParentPk))
            return false;
        ParentPk castOther = ParentPk.class.cast(other);
        return getFirstName().equals(castOther.getFirstName())
                && getLastName().equals(castOther.getLastName());
    }

    @Override
    public int hashCode() {
        int result;
        result = getFirstName().hashCode();
        result = 29 * result + getLastName().hashCode();
        return result;
    }
}
