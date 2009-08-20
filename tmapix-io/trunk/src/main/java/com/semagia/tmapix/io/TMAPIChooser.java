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

import org.tmapi.core.TopicMap;

import com.semagia.mio.IMapHandler;

/**
 * Internal helper class that detects different TMAPI 2.0 implementations.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class TMAPIChooser {

    private static final String _TINYTIM = "org.tinytim";
    private static final String _ONTOPIA = "net.ontopia.topicmaps.impl.tmapi2.";

    static final String TMAPI_SYSTEM_FACTORY = "org.tmapi.core.TopicMapSystemFactory";
    static final String TINYTIM_SYSTEM_FACTORY = "org.tinytim.core.TopicMapSystemFactoryImpl";
    static final String ONTOPIA_SYSTEM_FACTORY = "net.ontopia.topicmaps.impl.tmapi2.TopicMapSystemFactory";

    private TMAPIChooser() {
        // noop.
    }

    static IMapHandler createMapHandler(TopicMap topicMap) {
        if (topicMap == null) {
            throw new IllegalArgumentException("The topic map must not be null");
        }
        if (isTinyTim(topicMap)) {
            return makeTinyTimMapInputHandler(topicMap);
        }
        else if (isOntopia(topicMap)) {
            return makeOntopiaMapInputHandler(topicMap);
        }
        return new TMAPIMapHandler(topicMap);
    }

    static boolean isTinyTim(TopicMap topicMap) {
        return topicMap.getClass().getName().startsWith(_TINYTIM);
    }

    static boolean isOntopia(TopicMap topicMap) {
        return topicMap.getClass().getName().startsWith(_ONTOPIA);
    }

    private static IMapHandler makeTinyTimMapInputHandler(final TopicMap topicMap) {
        return new org.tinytim.mio.TinyTimMapInputHandler(topicMap);
    }

    private static IMapHandler makeOntopiaMapInputHandler(final TopicMap topicMap) {
        return new OntopiaMapHandler(unwrapOntopia(topicMap));
    }

    static net.ontopia.topicmaps.core.TopicMapIF unwrapOntopia(TopicMap topicMap) {
        return ((net.ontopia.topicmaps.impl.tmapi2.TopicMapImpl) topicMap).getWrapped();
    }

}
