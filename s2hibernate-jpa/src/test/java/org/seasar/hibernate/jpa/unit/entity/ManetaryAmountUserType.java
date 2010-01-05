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
package org.seasar.hibernate.jpa.unit.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Currency;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;

/**
 * 
 * @author taedium
 */
public class ManetaryAmountUserType implements CompositeUserType {

    public Object deepCopy(Object value) throws HibernateException {
        final ManetaryAmount ma = ManetaryAmount.class.cast(value);
        return new ManetaryAmount(ma.getAmount(), ma.getCurrency());
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y) {
            return true;
        }
        if (x == null || y == null) {
            return false;
        }
        final ManetaryAmount max = ManetaryAmount.class.cast(x);
        final ManetaryAmount may = ManetaryAmount.class.cast(y);
        return max.getAmount().equals(may.getAmount())
                && max.getCurrency().equals(may.getCurrency());
    }

    public int hashCode(Object x) throws HibernateException {
        return ManetaryAmount.class.cast(x).getAmount().hashCode();
    }

    public boolean isMutable() {
        return true;
    }

    public Class<?> returnedClass() {
        return ManetaryAmount.class;
    }

    public Object assemble(Serializable cached, SessionImplementor session,
            Object owner) throws HibernateException {
        return deepCopy(cached);
    }

    public Serializable disassemble(Object value, SessionImplementor session)
            throws HibernateException {
        return Serializable.class.cast(deepCopy(value));
    }

    public String[] getPropertyNames() {
        return new String[] { "amount", "currency" };
    }

    public Type[] getPropertyTypes() {
        return new Type[] { Hibernate.BIG_DECIMAL, Hibernate.CURRENCY };
    }

    public Object getPropertyValue(Object component, int property)
            throws HibernateException {
        final ManetaryAmount ma = ManetaryAmount.class.cast(component);
        return property == 0 ? ma.getAmount() : ma.getCurrency();
    }

    public Object nullSafeGet(ResultSet rs, String[] names,
            SessionImplementor session, Object owner)
            throws HibernateException, SQLException {
        final BigDecimal amount = (BigDecimal) Hibernate.BIG_DECIMAL
                .nullSafeGet(rs, names[0]);
        final Currency currency = (Currency) Hibernate.CURRENCY.nullSafeGet(rs,
                names[1]);
        if (amount == null) {
            return null;
        }
        return new ManetaryAmount(amount, currency);
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index,
            SessionImplementor session) throws HibernateException, SQLException {
        final ManetaryAmount ma = ManetaryAmount.class.cast(value);
        final BigDecimal amount = ma == null ? null : ma.getAmount();
        final Currency currency = ma == null ? null : ma.getCurrency();
        Hibernate.BIG_DECIMAL.nullSafeSet(st, amount, index);
        Hibernate.CURRENCY.nullSafeSet(st, currency, index + 1);
    }

    public Object replace(Object original, Object target,
            SessionImplementor session, Object owner) throws HibernateException {
        return deepCopy(original);
    }

    public void setPropertyValue(Object component, int property, Object value)
            throws HibernateException {
        final ManetaryAmount ma = ManetaryAmount.class.cast(component);
        if (property == 0) {
            ma.setAmount(BigDecimal.class.cast(value));
        } else {
            ma.setCurrency(Currency.class.cast(value));
        }
    }

}
