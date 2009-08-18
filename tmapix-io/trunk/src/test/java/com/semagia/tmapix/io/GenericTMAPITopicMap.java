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

import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.index.Index;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
final class GenericTMAPITopicMap implements TopicMap {

    private final TopicMap _tm;

    GenericTMAPITopicMap(TopicMap topicMap) {
        _tm = topicMap;
    }

    TopicMap getWrappedTopicMap() {
        return _tm;
    }

    public void addItemIdentifier(Locator arg0) {
        _tm.addItemIdentifier(arg0);
    }

    public void close() {
        _tm.close();
    }

    public Association createAssociation(Topic arg0, Collection<Topic> arg1) {
        return _tm.createAssociation(arg0, arg1);
    }

    public Association createAssociation(Topic arg0, Topic... arg1) {
        return _tm.createAssociation(arg0, arg1);
    }

    public Locator createLocator(String arg0) {
        return _tm.createLocator(arg0);
    }

    public Topic createTopic() {
        return _tm.createTopic();
    }

    public Topic createTopicByItemIdentifier(Locator arg0) {
        return _tm.createTopicByItemIdentifier(arg0);
    }

    public Topic createTopicBySubjectIdentifier(Locator arg0) {
        return _tm.createTopicBySubjectIdentifier(arg0);
    }

    public Topic createTopicBySubjectLocator(Locator arg0) {
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

    public void mergeIn(TopicMap arg0) {
        _tm.mergeIn(arg0);
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
