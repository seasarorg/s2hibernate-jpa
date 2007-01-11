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
package org.seasar.hibernate.jpa.entity;

import java.io.Serializable;

/**
 * 
 * @author taedium
 */
public class Employee2 implements Serializable {

    private static final long serialVersionUID = 163038061712063854L;

    private long empno;

    private String ename;

    public long getEmpno() {
        return empno;
    }

    public void setEmpno(long empno) {
        this.empno = empno;
    }

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Employee2))
            return false;
        Employee2 castOther = (Employee2) other;
        return this.getEmpno() == castOther.getEmpno();
    }

    @Override
    public int hashCode() {
        return (int) this.getEmpno();
    }
}
