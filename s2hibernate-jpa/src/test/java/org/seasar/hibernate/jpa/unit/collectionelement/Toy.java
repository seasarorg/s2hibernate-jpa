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
package org.seasar.hibernate.jpa.unit.collectionelement;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.annotations.Parent;

/**
 * 
 * @author taedium
 */
@Embeddable
public class Toy {
    private String name;

    @AttributeOverride(name = "name", column = @Column(name = "brand_name"))
    private Brand brand;

    private String serial;

    @Parent
    private Boy owner;

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boy getOwner() {
        return owner;
    }

    public void setOwner(Boy owner) {
        this.owner = owner;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Toy))
            return false;
        Toy castOther = Toy.class.cast(other);
        return getName().equals(castOther.getName())
                && getBrand().equals(castOther.getBrand())
                && getSerial().equals(castOther.getSerial());
    }

    @Override
    public int hashCode() {
        int result;
        result = getName().hashCode();
        result = 29 * result + getBrand().hashCode();
        result = 29 * result + getSerial().hashCode();
        return result;
    }

}
