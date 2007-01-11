/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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
public class RegionalArticlePk implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer code;

    private String name;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof RegionalArticlePk))
            return false;
        RegionalArticlePk castOther = RegionalArticlePk.class.cast(other);
        return getCode().equals(castOther.getCode())
                && getName().equals(castOther.getName());
    }

    @Override
    public int hashCode() {
        int result;
        result = getCode().hashCode();
        result = 29 * result + getName().hashCode();
        return result;
    }

}
