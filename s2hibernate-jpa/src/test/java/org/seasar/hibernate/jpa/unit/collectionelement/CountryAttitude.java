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
package org.seasar.hibernate.jpa.unit.collectionelement;

import javax.persistence.Embeddable;

/**
 * 
 * @author taedium
 */
@Embeddable
public class CountryAttitude {

    private Country country;

    private boolean likes;

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public boolean isLikes() {
        return likes;
    }

    public void setLikes(boolean likes) {
        this.likes = likes;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof CountryAttitude))
            return false;
        CountryAttitude castOther = CountryAttitude.class.cast(other);
        return getCountry().equals(castOther.getCountry());
    }

    @Override
    public int hashCode() {
        return getCountry().hashCode();
    }

}
