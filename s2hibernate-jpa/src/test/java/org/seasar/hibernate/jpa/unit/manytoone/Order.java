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
package org.seasar.hibernate.jpa.unit.manytoone;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * 
 * @author taedium
 */
@Entity(name = "Orders")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private String orderNo;

    @OneToMany(mappedBy = "order")
    private Collection<OrderLine> orderLines = new HashSet<OrderLine>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Collection<OrderLine> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(Collection<OrderLine> orderLines) {
        this.orderLines = orderLines;
    }

}
