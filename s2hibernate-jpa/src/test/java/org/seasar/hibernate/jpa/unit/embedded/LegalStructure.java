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
package org.seasar.hibernate.jpa.unit.embedded;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

/**
 * 
 * @author taedium
 */
@Embeddable
public class LegalStructure {

    private String name;

    @OneToMany(mappedBy = "employer")
    private Set<Manager> topManagement = new HashSet<Manager>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Manager> getTopManagement() {
        return topManagement;
    }

    public void setTopManagement(Set<Manager> topManagement) {
        this.topManagement = topManagement;
    }
}
