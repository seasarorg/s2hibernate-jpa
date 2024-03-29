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
package org.seasar.hibernate.jpa.unit.indexcoll;

import java.io.Serializable;

/**
 * 
 * @author taedium
 */
public class PaintingPk implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String painter;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPainter() {
        return painter;
    }

    public void setPainter(String painter) {
        this.painter = painter;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof PaintingPk))
            return false;
        PaintingPk castOther = PaintingPk.class.cast(other);
        return getName().equals(castOther.getName())
                && getPainter().equals(castOther.getPainter());
    }

    @Override
    public int hashCode() {
        int result;
        result = getName().hashCode();
        result = 29 * result + getPainter().hashCode();
        return result;
    }
}
