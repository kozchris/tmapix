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
package org.tmapix.io;

import java.util.Collection;
import java.util.Set;

import org.tinytim.core.TopicMapSystemFactoryImpl;
import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.FeatureNotSupportedException;
import org.tmapi.core.IdentityConstraintException;
import org.tmapi.core.Locator;
import org.tmapi.core.MalformedIRIException;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapExistsException;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;
import org.tmapi.index.Index;

/**
 * Internal test class to test the {@link org.tmapix.io.CXTMTopicMapWriter}.
 * 
 * This class wraps tinyTiM and the generic CXTMTopicMapWriter is used instead
 * of tinyTiM's own CXTMWriter.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class CXTMWriterTopicMapSystemFactoryTinyTim extends TopicMapSystemFactory {

    private final TopicMapSystemFactory _factory;
    
    public CXTMWriterTopicMapSystemFactoryTinyTim() {
        _factory = new TopicMapSystemFactoryImpl();
    }

    public boolean equals(Object arg0) {
        return _factory.equals(arg0);
    }

    public boolean getFeature(String arg0) throws FeatureNotRecognizedException {
        return _factory.getFeature(arg0);
    }

    public Object getProperty(String arg0) {
        return _factory.getProperty(arg0);
    }

    public boolean hasFeature(String arg0) {
        return _factory.hasFeature(arg0);
    }

    public int hashCode() {
        return _factory.hashCode();
    }

    public TopicMapSystem newTopicMapSystem() throws TMAPIException {
        return new CXTMWriterTopicMapSystem(_factory.newTopicMapSystem());
    }

    public void setFeature(String arg0, boolean arg1)
            throws FeatureNotSupportedException, FeatureNotRecognizedException {
        _factory.setFeature(arg0, arg1);
    }

    public void setProperty(String arg0, Object arg1) {
        _factory.setProperty(arg0, arg1);
    }

    public String toString() {
        return _factory.toString();
    }

    private static final class CXTMWriterTopicMapSystem implements TopicMapSystem {

        private final TopicMapSystem _sys;

        public void close() {
            _sys.close();
        }

        public Locator createLocator(String arg0) throws MalformedIRIException {
            return _sys.createLocator(arg0);
        }

        public TopicMap createTopicMap(Locator arg0)
                throws TopicMapExistsException {
            return _wrap(_sys.createTopicMap(arg0));
        }

        public TopicMap createTopicMap(String arg0)
                throws TopicMapExistsException {
            return _wrap(_sys.createTopicMap(arg0));
        }

        public boolean getFeature(String arg0)
                throws FeatureNotRecognizedException {
            return _sys.getFeature(arg0);
        }

        public Set<Locator> getLocators() {
            return _sys.getLocators();
        }

        public Object getProperty(String arg0) {
            return _sys.getProperty(arg0);
        }

        public TopicMap getTopicMap(Locator arg0) {
            return _wrap(_sys.getTopicMap(arg0));
        }

        public TopicMap getTopicMap(String arg0) {
            return _wrap(_sys.getTopicMap(arg0));
        }

        public CXTMWriterTopicMapSystem(TopicMapSystem sys) {
            _sys = sys;
        }

        private static TopicMap _wrap(TopicMap topicMap) {
            return new CXTMWriterTopicMapTinyTim(topicMap);
        }
    }

    public static final class CXTMWriterTopicMapTinyTim implements TopicMap {

        private final TopicMap _tm;

        public CXTMWriterTopicMapTinyTim(TopicMap topicMap) {
            _tm = topicMap;
        }

        public TopicMap getWrappedTopicMap() {
            return _tm;
        }

        public void addItemIdentifier(Locator arg0)
                throws ModelConstraintException {
            _tm.addItemIdentifier(arg0);
        }

        public void close() {
            _tm.close();
        }

        public Association createAssociation(Topic arg0, Collection<Topic> arg1)
                throws ModelConstraintException {
            return _tm.createAssociation(arg0, arg1);
        }

        public Association createAssociation(Topic arg0, Topic... arg1)
                throws ModelConstraintException {
            return _tm.createAssociation(arg0, arg1);
        }

        public Locator createLocator(String arg0) throws MalformedIRIException {
            return _tm.createLocator(arg0);
        }

        public Topic createTopic() {
            return _tm.createTopic();
        }

        public Topic createTopicByItemIdentifier(Locator arg0)
                throws IdentityConstraintException, ModelConstraintException {
            return _tm.createTopicByItemIdentifier(arg0);
        }

        public Topic createTopicBySubjectIdentifier(Locator arg0)
                throws ModelConstraintException {
            return _tm.createTopicBySubjectIdentifier(arg0);
        }

        public Topic createTopicBySubjectLocator(Locator arg0)
                throws ModelConstraintException {
            return _tm.createTopicBySubjectLocator(arg0);
        }

        public boolean equals(Object arg0) {
            return _tm.equals(arg0);
        }

        public Set<Association> getAssociations() {
            return _tm.getAssociations();
        }

        public Construct getConstructById(String arg0) {
            return _tm.getConstructById(arg0);
        }

        public Construct getConstructByItemIdentifier(Locator arg0) {
            return _tm.getConstructByItemIdentifier(arg0);
        }

        public String getId() {
            return _tm.getId();
        }

        public <I extends Index> I getIndex(Class<I> arg0) {
            return _tm.getIndex(arg0);
        }

        public Set<Locator> getItemIdentifiers() {
            return _tm.getItemIdentifiers();
        }

        public Locator getLocator() {
            return _tm.getLocator();
        }

        public Construct getParent() {
            return _tm.getParent();
        }

        public Topic getReifier() {
            return _tm.getReifier();
        }

        public Topic getTopicBySubjectIdentifier(Locator arg0) {
            return _tm.getTopicBySubjectIdentifier(arg0);
        }

        public Topic getTopicBySubjectLocator(Locator arg0) {
            return _tm.getTopicBySubjectLocator(arg0);
        }

        public TopicMap getTopicMap() {
            return _tm.getTopicMap();
        }

        public Set<Topic> getTopics() {
            return _tm.getTopics();
        }

        public int hashCode() {
            return _tm.hashCode();
        }

        public void mergeIn(TopicMap arg0) throws ModelConstraintException {
            _tm.mergeIn(arg0);
        }
        
        public void clear() {
            _tm.clear();
        }

        public void remove() {
            _tm.remove();
        }

        public void removeItemIdentifier(Locator arg0) {
            _tm.removeItemIdentifier(arg0);
        }

        public void setReifier(Topic arg0) throws ModelConstraintException {
            _tm.setReifier(arg0);
        }
        
    }
}
