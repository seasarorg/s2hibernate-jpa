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
package org.seasar.hibernate.jpa.unit.inheritance.joined;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * 
 * @author taedium
 */
@Entity
public class ProgramExecution implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private String action;

    @ManyToOne
    private File appliesOn;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public File getAppliesOn() {
        return appliesOn;
    }

    public void setAppliesOn(File appliesOn) {
        this.appliesOn = appliesOn;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
