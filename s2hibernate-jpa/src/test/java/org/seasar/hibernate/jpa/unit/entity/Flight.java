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
package org.seasar.hibernate.jpa.unit.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Formula;

/**
 * 
 * @author taedium
 */
@Entity
public class Flight implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @Formula("maxAltitude * 1000")
    long maxAltitudeInMilimeter;

    long maxAltitude;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public long getMaxAltitude() {
        return maxAltitude;
    }

    public void setMaxAltitude(long maxAltitude) {
        this.maxAltitude = maxAltitude;
    }

    public long getMaxAltitudeInMilimeter() {
        return maxAltitudeInMilimeter;
    }

    public void setMaxAltitudeInMilimeter(long maxAltitudeInMilimeter) {
        this.maxAltitudeInMilimeter = maxAltitudeInMilimeter;
    }
}
