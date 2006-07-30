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
package org.seasar.hibernate.jpa.unit.singletable;

import java.io.Serializable;

/**
 * 
 * @author taedium
 */
public class ProjectPk implements Serializable {

    private Integer projectCode;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(Integer projectCode) {
        this.projectCode = projectCode;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ProjectPk))
            return false;
        ProjectPk castOther = ProjectPk.class.cast(other);
        return getProjectCode().equals(castOther.getProjectCode())
                && getName().equals(castOther.getName());
    }

    @Override
    public int hashCode() {
        int result;
        result = getProjectCode().hashCode();
        result = 29 * result + getName().hashCode();
        return result;
    }
}
