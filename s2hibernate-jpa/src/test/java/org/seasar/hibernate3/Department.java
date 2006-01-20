package org.seasar.hibernate3;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratorType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="dept")
public class Department implements Serializable {
	
    private static final long serialVersionUID = -1031433105487668130L;
    
    private int deptno;

    private String dname;

    private String loc;
    
    private int versionNo;
    
    private boolean active;

    public Department() {
    }

    @Id (generate=GeneratorType.NONE)
    public int getDeptno() {
        return this.deptno;
    }

    public void setDeptno(int deptno) {
        this.deptno = deptno;
    }

    public java.lang.String getDname() {
        return this.dname;
    }

    public void setDname(java.lang.String dname) {
        this.dname = dname;
    }

    public java.lang.String getLoc() {
        return this.loc;
    }

    public void setLoc(java.lang.String loc) {
        this.loc = loc;
    }

    public int getVersionNo() {
        return this.versionNo;
    }

    public void setVersionNo(int versionNo) {
        this.versionNo = versionNo;
    }

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

    public boolean equals(Object other) {
        if ( !(other instanceof Department) ) return false;
        Department castOther = (Department) other;
        return this.getDeptno() == castOther.getDeptno();
    }

    public int hashCode() {
        return this.getDeptno();
    }
}
