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

import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.FeatureNotSupportedException;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class GenericTMAPITopicMapSystemFactoryOntopia extends TopicMapSystemFactory {

    private final TopicMapSystemFactory _factory;
    
    public GenericTMAPITopicMapSystemFactoryOntopia() {
        _factory = new net.ontopia.topicmaps.impl.tmapi2.TopicMapSystemFactory();
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
        return new GenericTMAPITopicMapSystem(_factory.newTopicMapSystem());
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
}
