/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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
package examples.entityreader;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.runner.RunWith;
import org.seasar.framework.jpa.PersistenceUnitConfiguration;
import org.seasar.framework.unit.Seasar2;
import org.seasar.framework.unit.TestContext;

import static org.seasar.framework.unit.S2Assert.*;

/**
 * 
 * @author nakamura
 */
@RunWith(Seasar2.class)
public class FileTest {

    private TestContext context;

    private EntityManager em;

    public void before() throws Exception {
        context.include("jpa.dicon");
        PersistenceUnitConfiguration cfg = context
                .getComponent(PersistenceUnitConfiguration.class);
        cfg.addPersistenceClass(File.class);
        cfg.addPersistenceClass(Document.class);
        cfg.addPersistenceClass(Folder.class);
    }

    public void polymorphicQuery() throws Exception {
        Folder root = new Folder();
        root.setName("root");
        Folder folder = new Folder();
        folder.setName("folder");
        folder.setParent(root);
        Document document = new Document();
        document.setName("document");
        document.setSize(100);
        document.setParent(folder);
        folder.getChildren().add(document);

        em.persist(root);
        em.persist(folder);
        em.persist(document);

        Query query = em.createQuery("SELECT f FROM File f ORDER BY f.name");
        assertEntityEquals(context.getExpected(), query.getResultList());
    }
}
