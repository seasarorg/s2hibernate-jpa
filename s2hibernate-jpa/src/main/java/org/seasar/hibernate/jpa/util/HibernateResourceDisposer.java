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
package org.seasar.hibernate.jpa.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.hibernate.reflection.java.JavaXFactory;
import org.hibernate.validator.ClassValidator;
import org.seasar.framework.util.tiger.ReflectionUtil;

/**
 * Hibernateのリソースを破棄するクラスです。
 * 
 * @author koichik
 */
public class HibernateResourceDisposer {

    /**
     * Hibernateのリソースを破棄します。
     * <p>
     * このメソッドは{@link JavaXFactory#reset()}を呼び出します。}
     * </p>
     */
    public static void dispose() {
        final Field field = ReflectionUtil.getDeclaredField(
                ClassValidator.class, "reflectionManager");
        field.setAccessible(true);
        final Object reflectionManager = ReflectionUtil.getStaticValue(field);
        final Method method = ReflectionUtil.getMethod(JavaXFactory.class,
                "reset");
        ReflectionUtil.invoke(method, reflectionManager);
    }
}
