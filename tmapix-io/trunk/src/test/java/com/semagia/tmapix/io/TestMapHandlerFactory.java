/*
 * Copyright 2009 Lars Heuer (heuer[at]semagia.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.semagia.tmapix.io;

import java.util.Collection;
import java.util.Set;

import org.tinytim.mio.TinyTimMapInputHandler;
import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystemFactory;
import org.tmapi.index.Index;

import com.semagia.mio.IMapHandler;

import junit.framework.TestCase;

/**
 * Tests against the {@link MapHandlerFactory}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class TestMapHandlerFactory extends TestCase {

    private IMapHandler makeMapHandler(String systemFactoryImpl) throws Exception {
        return makeMapHandler(makeTopicMap(systemFactoryImpl));
    }

    private IMapHandler makeMapHandler(TopicMap topicMap) {
        return MapHandlerFactory.createMapInputHandler(topicMap);
    }

    private TopicMap makeTopicMap(String systemFactoryImpl) throws Exception {
        System.setProperty("org.tmapi.core.TopicMapSystemFactory", systemFactoryImpl);
        return TopicMapSystemFactory.newInstance()
                .newTopicMapSystem()
                .createTopicMap("http://tmapix.semagia.com/test-map");
    }

    public void testDetectTinyTim() throws Exception {
        assertTrue(makeMapHandler("org.tinytim.core.TopicMapSystemFactoryImpl") instanceof TinyTimMapInputHandler);
    }

    public void testDetectOntopia() throws Exception {
        assertTrue(makeMapHandler("net.ontopia.topicmaps.impl.tmapi2.TopicMapSystemFactory") instanceof OntopiaMapHandler);
    }

    public void testUnknownTMAPIImplementation() {
        assertTrue(makeMapHandler(new FakeTopicMap()) instanceof TMAPIMapHandler);
    }


    private static class FakeTopicMap implements TopicMap {

        @Override
        public void close() {}

        @Override
        public Association createAssociation(Topic arg0, Topic... arg1) {
            return null;
        }

        @Override
        public Association createAssociation(Topic arg0, Collection<Topic> arg1) {
            return null;
        }

        @Override
        public Locator createLocator(String arg0) {
            return null;
        }

        @Override
        public Topic createTopic() {
            return null;
        }

        @Override
        public Topic createTopicByItemIdentifier(Locator arg0) {
            return null;
        }

        @Override
        public Topic createTopicBySubjectIdentifier(Locator arg0) {
            return null;
        }

        @Override
        public Topic createTopicBySubjectLocator(Locator arg0) {
            return null;
        }

        @Override
        public Set<Association> getAssociations() {
            return null;
        }

        @Override
        public Construct getConstructById(String arg0) {
            return null;
        }

        @Override
        public Construct getConstructByItemIdentifier(Locator arg0) {
            return null;
        }

        @Override
        public <I extends Index> I getIndex(Class<I> arg0) {
            return null;
        }

        @Override
        public Construct getParent() {
            return null;
        }

        @Override
        public Topic getTopicBySubjectIdentifier(Locator arg0) {
            return null;
        }

        @Override
        public Topic getTopicBySubjectLocator(Locator arg0) {
            return null;
        }

        @Override
        public Set<Topic> getTopics() {
            return null;
        }

        @Override
        public void mergeIn(TopicMap arg0) { }

        @Override
        public Topic getReifier() {
            return null;
        }

        @Override
        public void setReifier(Topic arg0) throws ModelConstraintException {
        }

        @Override
        public void addItemIdentifier(Locator arg0) {
        }

        @Override
        public String getId() {
            return null;
        }

        @Override
        public Set<Locator> getItemIdentifiers() {
            return null;
        }

        @Override
        public TopicMap getTopicMap() {
            return null;
        }

        @Override
        public void remove() { }

        @Override
        public void removeItemIdentifier(Locator arg0) { }
        
    }
}
