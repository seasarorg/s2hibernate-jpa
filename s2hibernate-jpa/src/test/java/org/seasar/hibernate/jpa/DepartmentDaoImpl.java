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
