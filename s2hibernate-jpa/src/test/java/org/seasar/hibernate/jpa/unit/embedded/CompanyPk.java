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
package org.seasar.hibernate.jpa.unit.embedded;

import java.io.Serializable;

import javax.persistence.Embeddable;

/**
 * 
 * @author taedium
 */
@Embeddable
public class CompanyPk implements Serializable {

    private Integer companyCode;

    private String name;

    public Integer getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(Integer companyCode) {
        this.companyCode = companyCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof CompanyPk))
            return false;
        CompanyPk castOther = CompanyPk.class.cast(other);
        return getCompanyCode().equals(castOther.getCompanyCode())
                && getName().equals(castOther.getName());
    }

    @Override
    public int hashCode() {
        int result;
        result = getCompanyCode().hashCode();
        result = 29 * result + getName().hashCode();
        return result;
    }

}
