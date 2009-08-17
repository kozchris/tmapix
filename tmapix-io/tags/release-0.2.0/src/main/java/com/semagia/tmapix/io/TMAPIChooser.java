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

import java.io.IOException;
import java.io.OutputStream;

import org.tmapi.core.TopicMap;

import com.semagia.mio.IMapHandler;

/**
 * Internal helper class that detects different TMAPI 2.0 implementations.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
final class TMAPIChooser {

    private static final String _TINYTIM = "org.tinytim.core.";
    private static final String _ONTOPIA = "net.ontopia.topicmaps.impl.tmapi2.";

    static IMapHandler createMapHandler(TopicMap topicMap) {
        if (topicMap == null) {
            throw new IllegalArgumentException("The topic map must not be null");
        }
        final String className = topicMap.getClass().getName();
        if (isTinyTim(className)) {
            return makeTinyTimMapInputHandler(topicMap);
        }
        else if (isOntopia(className)) {
            return makeOntopiaMapInputHandler(topicMap);
        }
        return new TMAPIMapHandler(topicMap);
    }

    private static boolean isTinyTim(TopicMap topicMap) {
        return isTinyTim(topicMap.getClass().getName());
    }

    private static boolean isTinyTim(String className) {
        return className.startsWith(_TINYTIM);
    }

    private static boolean isOntopia(TopicMap topicMap) {
        return isOntopia(topicMap.getClass().getName());
    }

    private static boolean isOntopia(String className) {
        return className.startsWith(_ONTOPIA);
    }

    private static IMapHandler makeTinyTimMapInputHandler(final TopicMap topicMap) {
        return new org.tinytim.mio.TinyTimMapInputHandler(topicMap);
    }

    private static IMapHandler makeOntopiaMapInputHandler(final TopicMap topicMap) {
        return new OntopiaMapHandler(unwrapOntopia(topicMap));
    }

    private static net.ontopia.topicmaps.core.TopicMapIF unwrapOntopia(TopicMap topicMap) {
        return ((net.ontopia.topicmaps.impl.tmapi2.TopicMapImpl) topicMap).getWrapped();
    }

    static TopicMapWriter createCXTMTopicMapWriter(TopicMap topicMap, OutputStream out, String base) throws IOException {
        if (isTinyTim(topicMap)) {
            return new TinyTimCXTMWriter(out, base);
        }
        else if (isOntopia(topicMap)) {
            return new OntopiaCXTMWriter(out);
        }
        throw new IOException("No CXTM serializer found");
    }

    private static class TinyTimCXTMWriter implements TopicMapWriter {

        private final org.tinytim.mio.CXTMTopicMapWriter _writer;

        public TinyTimCXTMWriter(OutputStream out, String base) throws IOException {
            _writer = new org.tinytim.mio.CXTMTopicMapWriter(out, base);
        }

        /* (non-Javadoc)
         * @see com.semagia.tmapix.io.TopicMapWriter#write(org.tmapi.core.TopicMap)
         */
        @Override
        public void write(TopicMap topicMap) throws IOException {
            _writer.write(topicMap); }
        
    }

    private static class OntopiaCXTMWriter implements TopicMapWriter {
        
        private final net.ontopia.topicmaps.xml.CanonicalXTMWriter _writer;

        public OntopiaCXTMWriter(OutputStream out) throws IOException {
            _writer = new net.ontopia.topicmaps.xml.CanonicalXTMWriter(out);
        }

        @Override
        public void write(TopicMap topicMap) throws IOException {
            _writer.write(unwrapOntopia(topicMap));
        }

    }

}
