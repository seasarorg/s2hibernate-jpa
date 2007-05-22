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

import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.persistence.TemporalType;

import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.type.AbstractComponentType;
import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;
import org.seasar.framework.jpa.metadata.AttributeDesc;
import org.seasar.framework.jpa.util.TemporalTypeUtil;
import org.seasar.framework.util.tiger.CollectionsUtil;

/**
 * Hibernateの永続オブジェクトの属性定義を表す抽象クラスです。
 * 
 * @author nakamura
 */
public abstract class AbstractHibernateAttributeDesc implements AttributeDesc {

    /** セッションファクトリ */
    protected final SessionFactoryImplementor factory;

    /** 属性の名前 */
    protected final String name;

    /** 型 */
    protected final Class<?> type;

    /** このオブジェクトがコレクションを表す場合の要素の型 */
    protected final Class<?> elementType;

    /** {@link Types SQL型}を表す値 */
    protected final int sqlType;

    /** 時制の種類 */
    protected final TemporalType temporalType;

    /** このオブジェクトがIDを表すかどうかのフラグ */
    protected final boolean id;

    /** このオブジェクトが関連を表すかどうかのフラグ */
    protected final boolean association;

    /** このオブジェクトがコレクションを表すかどうかのフラグ */
    protected final boolean collection;

    /** このオブジェクトがコンポーネントを表すかどうかのフラグ */
    protected final boolean component;

    /** このオブジェクトがバージョン番号を表すかどうかのフラグ */
    protected final boolean version;

    /** Hibernateの型 */
    protected final Type hibernateType;

    /** このオブジェクトがコンポーネントを表す場合、コンポーネントの属性の配列 */
    protected final AttributeDesc[] childAttributeDescs;

    /** このオブジェクトがコンポーネントを表す場合、コンポーネントの属性名をキー、コンポーネントの属性を値とするマップ */
    protected final Map<String, AttributeDesc> childAttributeDescMap = CollectionsUtil
            .newHashMap();

    /**
     * インスタンスを構築します。
     * 
     * @param factory
     *            セッションファクトリ
     * @param hibernateType
     *            Hibernateの型
     * @param name
     *            属性の名前
     * @param id
     *            このオブジェクトがIDを表すかどうかのフラグ
     * @param version
     *            このオブジェクトがバージョン番号を表すかどうかのフラグ
     */
    public AbstractHibernateAttributeDesc(
            final SessionFactoryImplementor factory, final Type hibernateType,
            final String name, final boolean id, final boolean version) {
        this.factory = factory;
        this.hibernateType = hibernateType;
        this.name = name;
        this.id = id;
        this.version = version;

        type = hibernateType.getReturnedClass();
        sqlType = getSqlType(hibernateType);

        if (type == Date.class || type == Calendar.class) {
            temporalType = TemporalTypeUtil.fromSqlTypeToTemporalType(sqlType);
        } else {
            temporalType = null;
        }

        association = hibernateType.isAssociationType();
        collection = hibernateType.isCollectionType();
        component = hibernateType.isComponentType();

        if (collection) {
            final CollectionType collectionType = CollectionType.class
                    .cast(hibernateType);
            elementType = collectionType.getElementType(factory)
                    .getReturnedClass();
        } else {
            elementType = null;
        }

        if (component) {
            childAttributeDescs = createChildAttributeDescs();
        } else {
            childAttributeDescs = new AttributeDesc[] {};
        }

    }

    /**
     * Hibernateの型に対応する{@link Types SQL型}の値を返します。
     * 
     * @param hibernateType
     *            Hibernateの型
     * @return SQL型の値
     */
    protected int getSqlType(final Type hibernateType) {
        try {
            final int[] sqlTypes = hibernateType.sqlTypes(null);
            if (sqlTypes != null && sqlTypes.length == 1) {
                return sqlTypes[0];
            }
        } catch (final Exception ignore) {
        }
        return Types.OTHER;
    }

    /**
     * このオブジェクトがコンポーネントを表す場合コンポーネントの属性の配列を返します。
     * 
     * @return このオブジェクトがコンポーネントを表す場合コンポーネントの属性の配列、表さない場合空の配列
     */
    protected AttributeDesc[] createChildAttributeDescs() {
        final AbstractComponentType componentType = AbstractComponentType.class
                .cast(hibernateType);
        final Type[] subtypes = componentType.getSubtypes();
        final AttributeDesc[] childAttributeDescs = new AttributeDesc[subtypes.length];
        for (int i = 0; i < subtypes.length; i++) {
            final String componentPropName = componentType.getPropertyNames()[i];
            final AttributeDesc attribute = new HibernateChildAttributeDesc(
                    factory, this, componentType, subtypes[i],
                    componentPropName, i);
            childAttributeDescs[i] = attribute;
            childAttributeDescMap.put(componentPropName, attribute);
        }
        return childAttributeDescs;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public Class<?> getElementType() {
        return elementType;
    }

    public int getSqlType() {
        return sqlType;
    }

    public TemporalType getTemporalType() {
        return temporalType;
    }

    public boolean isId() {
        return id;
    }

    public boolean isAssociation() {
        return association;
    }

    public boolean isCollection() {
        return collection;
    }

    public boolean isComponent() {
        return component;
    }

    public boolean isVersion() {
        return version;
    }

    public AttributeDesc[] getChildAttributeDescs() {
        return childAttributeDescs;
    }

    public AttributeDesc getChildAttributeDesc(final String name) {
        return childAttributeDescMap.get(name);
    }
}
