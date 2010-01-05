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

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.IndexColumn;

/**
 * 
 * @author taedium
 */
@Entity
public class Boy implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private String firstName;

    private String lastName;

    @CollectionOfElements
    private Set<String> nickNames = new HashSet<String>();

    @CollectionOfElements
    private Map<String, Integer> scorePerNickName = new HashMap<String, Integer>();

    @CollectionOfElements
    @IndexColumn(name = "index")
    private int[] favoriteNumbers;

    @CollectionOfElements
    private Set<Toy> favoriteToys = new HashSet<Toy>();

    @CollectionOfElements
    @Enumerated(EnumType.STRING)
    private Set<Character> characters = new HashSet<Character>();

    @CollectionOfElements
    private Set<CountryAttitude> countryAttributes = new HashSet<CountryAttitude>();

    public Set<Character> getCharacters() {
        return characters;
    }

    public void setCharacters(Set<Character> characters) {
        this.characters = characters;
    }

    public Set<CountryAttitude> getCountryAttributes() {
        return countryAttributes;
    }

    public void setCountryAttributes(Set<CountryAttitude> countryAttributes) {
        this.countryAttributes = countryAttributes;
    }

    public Set<Toy> getFavoriteToys() {
        return favoriteToys;
    }

    public void setFavoriteToys(Set<Toy> favoriteToys) {
        this.favoriteToys = favoriteToys;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<String> getNickNames() {
        return nickNames;
    }

    public void setNickNames(Set<String> nickNames) {
        this.nickNames = nickNames;
    }

    public Map<String, Integer> getScorePerNickName() {
        return scorePerNickName;
    }

    public void setScorePerNickName(Map<String, Integer> scorePerNickName) {
        this.scorePerNickName = scorePerNickName;
    }

    public int[] getFavoriteNumbers() {
        return favoriteNumbers;
    }

    public void setFavoriteNumbers(int[] favoriteNumbers) {
        this.favoriteNumbers = favoriteNumbers;
    }

}
