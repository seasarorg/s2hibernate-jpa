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
package org.seasar.hibernate.jpa.metadata;

import org.hibernate.EntityMode;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.type.AbstractComponentType;
import org.hibernate.type.Type;
import org.seasar.framework.jpa.metadata.AttributeDesc;

/**
 * 
 * @author nakamura
 */
public class HibernateChildAttributeDesc extends
        AbstractHibernateAttributeDesc {

    protected final AttributeDesc parentAttributeDesc;

    protected final AbstractComponentType componentType;

    protected final int index;

    public HibernateChildAttributeDesc(
            final SessionFactoryImplementor factory,
            final AttributeDesc parentAttributeDesc,
            final AbstractComponentType componentType,
            final Type hibernateType, final String name, int index) {
        super(factory, hibernateType, name, false, false);
        this.parentAttributeDesc = parentAttributeDesc;
        this.componentType = componentType;
        this.index = index;
    }

    public Object getValue(Object entity) {
        if (parentAttributeDesc.getName() == null) {
            return null;
        }
        Object component = parentAttributeDesc.getValue(entity);
        return componentType.getPropertyValues(component, EntityMode.POJO)[index];
    }

    public void setValue(Object entity, Object value) {
        throw new UnsupportedOperationException("setValue");
    }

    public AttributeDesc[] getChildAttributeDescs() {
        return new AttributeDesc[] {};
    }

    public AttributeDesc getChildAttributeDesc(final String name) {
        return null;
    }
}
