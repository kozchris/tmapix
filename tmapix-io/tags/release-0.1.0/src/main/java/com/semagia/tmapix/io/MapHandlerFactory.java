/*
 * Copyright 2009 Lars Heuer (heuer[at]semagia.com). All rights reserved.
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
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
//TODO: Should this be public?
final class MapHandlerFactory {

    private static final String _TINYTIM = "org.tinytim.core.";
    private static final String _ONTOPIA = "net.ontopia.topicmaps.impl.tmapi2.";

    public static IMapHandler createMapInputHandler(final TopicMap topicMap) {
        final String className = topicMap.getClass().getName(); 
        if (className.startsWith(_TINYTIM)) {
            return makeTinyTimMapInputHandler(topicMap);
        }
        else if (className.startsWith(_ONTOPIA)) {
            return makeOntopiaMapInputHandler(topicMap);
        }
        return new TMAPIMapHandler(topicMap);
    }

    private static IMapHandler makeOntopiaMapInputHandler(final TopicMap topicMap) {
        //TODO
        //return new OntopiaMapHandler(((net.ontopia.topicmaps.impl.tmapi2.TopicMapImpl) topicMap).getWrapped());
        return new TMAPIMapHandler(topicMap);
    }

    private static IMapHandler makeTinyTimMapInputHandler(final TopicMap topicMap) {
        return new org.tinytim.mio.TinyTimMapInputHandler(topicMap);
    }

}
