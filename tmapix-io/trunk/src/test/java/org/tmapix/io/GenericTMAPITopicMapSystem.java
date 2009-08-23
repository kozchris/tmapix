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

import java.util.Set;

import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.Locator;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapExistsException;
import org.tmapi.core.TopicMapSystem;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
class GenericTMAPITopicMapSystem implements TopicMapSystem {

    private final TopicMapSystem _system;
    
    GenericTMAPITopicMapSystem(TopicMapSystem system) {
        _system = system;
    }

    public void close() {
        _system.close();
    }

    public Locator createLocator(String arg0) {
        return _system.createLocator(arg0);
    }

    public TopicMap createTopicMap(Locator arg0) throws TopicMapExistsException {
        return _wrap(_system.createTopicMap(arg0));
    }

    public TopicMap createTopicMap(String arg0) throws TopicMapExistsException {
        return _wrap(_system.createTopicMap(arg0));
    }

    public boolean getFeature(String arg0) throws FeatureNotRecognizedException {
        return _system.getFeature(arg0);
    }

    public Set<Locator> getLocators() {
        return _system.getLocators();
    }

    public Object getProperty(String arg0) {
        return _system.getProperty(arg0);
    }

    public TopicMap getTopicMap(Locator arg0) {
        return _wrap(_system.getTopicMap(arg0));
    }

    public TopicMap getTopicMap(String arg0) {
        return _wrap(_system.getTopicMap(arg0));
    }

    private static TopicMap _wrap(TopicMap topicMap) {
        return new GenericTMAPITopicMap(topicMap);
    }

}
