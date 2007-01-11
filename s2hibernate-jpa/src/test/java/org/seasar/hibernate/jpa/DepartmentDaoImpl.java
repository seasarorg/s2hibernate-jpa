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
package org.seasar.hibernate.jpa;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class DepartmentDaoImpl implements DepartmentDao {

    @PersistenceContext
    EntityManager em1;

    @PersistenceContext(name = "entityManagerFactory")
    EntityManager em2;

    @PersistenceContext(unitName = "persistenceUnit")
    EntityManager em3;

    public DepartmentDaoImpl() {
    }

    public Department getDepartment1(int id) {
        return em1.find(Department.class, id);
    }

    public Department getDepartment2(int id) {
        return em2.find(Department.class, id);
    }

    public Department getDepartment3(int id) {
        return em3.find(Department.class, id);
    }
}
