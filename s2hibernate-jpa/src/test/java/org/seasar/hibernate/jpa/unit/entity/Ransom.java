/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;

/**
 * 
 * @author taedium
 */
@Entity
public class Ransom implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @Type(type = "org.seasar.hibernate.jpa.unit.entity.ManetaryAmountUserType")
    @Columns(columns = { @Column(name = "amount"), @Column(name = "currency") })
    private ManetaryAmount amount;

    public ManetaryAmount getAmount() {
        return amount;
    }

    public void setAmount(ManetaryAmount amount) {
        this.amount = amount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
