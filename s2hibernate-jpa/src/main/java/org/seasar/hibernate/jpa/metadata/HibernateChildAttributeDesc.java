/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
 * Hibernateのコンポーネントの属性定義です。
 * 
 * @author nakamura
 */
public class HibernateChildAttributeDesc extends AbstractHibernateAttributeDesc {

    /** このオブジェクトの親であるコンポーネント自身の定義 */
    protected final AttributeDesc parentAttributeDesc;

    /** このオブジェクトの親であるコンポーネントの種類 */
    protected final AbstractComponentType componentType;

    /** 親のコンポーネント内におけるこの属性のインデックス */
    protected final int index;

    /**
     * インスタンスを構築します。
     * 
     * @param factory
     *            セッションファクトリ
     * @param parentAttributeDesc
     *            このオブジェクトの親自身の定義
     * @param componentType
     *            このオブジェクトの親であるコンポーネントの種類
     * @param hibernateType
     *            Hibernateの型
     * @param name
     *            属性の名前
     * @param index
     *            親のコンポーネント内におけるこの属性のインデックス
     */
    public HibernateChildAttributeDesc(final SessionFactoryImplementor factory,
            final AttributeDesc parentAttributeDesc,
            final AbstractComponentType componentType,
            final Type hibernateType, final String name, final int index) {
        super(factory, hibernateType, name, false, false);
        this.parentAttributeDesc = parentAttributeDesc;
        this.componentType = componentType;
        this.index = index;
    }

    public Object getValue(final Object owner) {
        return getValues(owner)[index];
    }

    /**
     * このオブジェクトの親であるコンポーネント内のすべての属性の値を配列で返します。
     * 
     * @param owner
     *            このオブジェクトの親であるコンポーネント
     * @return コンポーネント内のすべての属性の値
     */
    protected Object[] getValues(final Object owner) {
        return componentType.getPropertyValues(owner, EntityMode.POJO);
    }

    public void setValue(final Object owner, final Object value) {
        final Object[] values = getValues(owner);
        values[index] = value;
        componentType.setPropertyValues(owner, values, EntityMode.POJO);
    }

}
