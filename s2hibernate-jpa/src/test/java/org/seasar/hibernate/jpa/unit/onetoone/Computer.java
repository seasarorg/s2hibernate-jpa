/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.hibernate.jpa.unit.onetoone;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;

/**
 * 
 * @author taedium
 */
@Entity
public class Computer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @OneToOne
    @JoinColumns( {
            @JoinColumn(name = "serialbrand", referencedColumnName = "brand"),
            @JoinColumn(name = "serialmodel", referencedColumnName = "model") })
    private SerialNumber serialNumber;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SerialNumber getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(SerialNumber serialNumber) {
        this.serialNumber = serialNumber;
    }

}
