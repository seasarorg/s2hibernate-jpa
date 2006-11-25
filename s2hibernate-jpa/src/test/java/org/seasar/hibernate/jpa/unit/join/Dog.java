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
package org.seasar.hibernate.jpa.unit.join;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;

/**
 * 
 * @author taedium
 */
@Entity
@SecondaryTable(name = "DogThoroughbred", pkJoinColumns = {
        @PrimaryKeyJoinColumn(name = "dt_name", referencedColumnName = "name"),
        @PrimaryKeyJoinColumn(name = "dt_ownerName", referencedColumnName = "ownerName") })
public class Dog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private DogPk id;

    private int weight;

    @Column(table = "DogThoroughbred")
    private String thoroughbredName;

    public DogPk getId() {
        return id;
    }

    public void setId(DogPk id) {
        this.id = id;
    }

    public String getThoroughbredName() {
        return thoroughbredName;
    }

    public void setThoroughbredName(String thoroughbredName) {
        this.thoroughbredName = thoroughbredName;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
