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
package org.seasar.hibernate.jpa.unit.indexcoll;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;

/**
 * 
 * @author taedium
 */
@Entity
public class Painter implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @OneToMany
    @MapKey(name = "name")
    @JoinColumn
    private Map<String, Painting> paintings = new HashMap<String, Painting>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Map<String, Painting> getPaintings() {
        return paintings;
    }

    public void setPaintings(Map<String, Painting> paintings) {
        this.paintings = paintings;
    }

}
