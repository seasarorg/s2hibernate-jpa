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

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;

import org.seasar.framework.util.ClassTraversal;
import org.seasar.framework.util.JarFileUtil;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.framework.util.ClassTraversal.ClassHandler;
import org.seasar.framework.util.tiger.CollectionsUtil;

/**
 * 
 * @author taedium
 */
public abstract class AbstractClassDetector implements ClassDetector {

    public AbstractClassDetector() {
        strategies.put("file", new FileSystemStrategy());
        strategies.put("jar", new JarFileStrategy());
        strategies.put("zip", new ZipFileStrategy());
    }

    protected Map<String, Strategy> strategies = CollectionsUtil.newHashMap();

    protected Set<String> packageNames = CollectionsUtil.newHashSet();

    protected Set<Class<?>> referenceClasses = CollectionsUtil.newHashSet();

    public void addPackageName(String packageName) {
        packageNames.add(packageName);
    }

    public void addReferenceClass(Class<?> referenceClass) {
        referenceClasses.add(referenceClass);
    }

    public void addStrategy(final String protocol, final Strategy strategy) {
        strategies.put(protocol, strategy);
    }

    public Set<String> getPackageNames() {
        return packageNames;
    }

    public Set<Class<?>> getReferenceClasses() {
        return referenceClasses;
    }

    public Map<String, Strategy> getStrategies() {
        return strategies;
    }

    protected interface Strategy {

        void detect(Class referenceClass, URL url, ClassHandler handler);
    }

    protected class FileSystemStrategy implements Strategy {

        public void detect(final Class referenceClass, final URL url,
                final ClassHandler handler) {
            final File rootDir = getRootDir(referenceClass, url);
            for (String packageName : packageNames) {
                ClassTraversal.forEach(rootDir, packageName, handler);
            }
        }

        protected File getRootDir(final Class referenceClass, final URL url) {
            final String[] names = referenceClass.getName().split("\\.");
            File path = ResourceUtil.getFile(url);
            for (int i = 0; i < names.length; ++i) {
                path = path.getParentFile();
            }
            return path;
        }
    }

    protected class JarFileStrategy implements Strategy {

        public void detect(final Class referenceClass, final URL url,
                final ClassHandler handler) {
            final JarFile jarFile = createJarFile(url);
            ClassTraversal.forEach(jarFile, handler);
        }

        protected JarFile createJarFile(final URL url) {
            final String urlString = ResourceUtil.toExternalForm(url);
            final int pos = urlString.lastIndexOf('!');
            final String jarFileName = urlString.substring(
                    "jar:file:".length(), pos);
            return JarFileUtil.create(new File(jarFileName));
        }
    }

    protected class ZipFileStrategy implements Strategy {

        public void detect(final Class referenceClass, final URL url,
                final ClassHandler handler) {
            final JarFile jarFile = createJarFile(url);
            ClassTraversal.forEach(jarFile, handler);
        }

        protected JarFile createJarFile(final URL url) {
            final String urlString = ResourceUtil.toExternalForm(url);
            final int pos = urlString.lastIndexOf('!');
            final String jarFileName = urlString
                    .substring("zip:".length(), pos);
            return JarFileUtil.create(new File(jarFileName));
        }
    }

}
