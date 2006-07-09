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
package org.seasar.hibernate.jpa.detector;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.util.ArrayUtil;
import org.seasar.framework.util.ClassUtil;
import org.seasar.hibernate.jpa.detector.sub.Bar;
import org.seasar.hibernate.jpa.detector.sub.Foo;

/**
 * 
 * @author nakamura
 */
public class AnnotatedClassDetectorTest extends S2TestCase {

    public void testDetect() throws Exception {
        final AnnotatedClassDetector detector = new AnnotatedClassDetector();
        detector.addAnnotation(Entity.class);
        detector.addAnnotation(MappedSuperclass.class);
        detector.addAnnotation(Embeddable.class);
        detector.addPackageName(ClassUtil.getPackageName(getClass()));
        detector.addReferenceClass(getClass());

        final Class<?>[] detected = detector.detect();
        assertEquals(2, detected.length);
        assertEquals(true, ArrayUtil.contains(detected, Hoge.class));
        assertEquals(true, ArrayUtil.contains(detected, Foo.class));
        assertEquals(false, ArrayUtil.contains(detected, Bar.class));
    }

    public void testDetect2() throws Exception {
        final AnnotatedClassDetector detector = new AnnotatedClassDetector();
        detector.addPackageName(ClassUtil.getPackageName(getClass()));
        detector.addReferenceClass(getClass());

        final Class<?>[] detected = detector.detect();
        assertEquals(0, detected.length);
    }

}
