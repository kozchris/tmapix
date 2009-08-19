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

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
class CXTMWriterFactory {

    static TopicMapWriter createCXTMTopicMapWriter(TopicMap topicMap, OutputStream out, String base) throws IOException {
        if (TMAPIChooser.isTinyTim(topicMap)) {
            return new TinyTimCXTMWriter(out, base);
        }
        else if (TMAPIChooser.isOntopia(topicMap)) {
            return new OntopiaCXTMWriter(out);
        }
        else if (TMAPIChooser.isGenericTMAPI(topicMap)) {
            if (TMAPIChooser.isTinyTim(((GenericTMAPITopicMap) topicMap).getWrappedTopicMap())) {
                return new GenericTinyTimCXTMWriter(out, base);
            }
            else {
                return new GenericOntopiaCXTMWriter(out);
            }
        }
        throw new IOException("No CXTM serializer found");
    }

    private static class TinyTimCXTMWriter implements TopicMapWriter {

        private final org.tinytim.mio.CXTMTopicMapWriter _writer;

        public TinyTimCXTMWriter(OutputStream out, String base) throws IOException {
            _writer = new org.tinytim.mio.CXTMTopicMapWriter(out, base);
        }

        @Override
        public void write(TopicMap topicMap) throws IOException {
            _writer.write(topicMap); }
        
    }

    private static class GenericTinyTimCXTMWriter extends TinyTimCXTMWriter {

        public GenericTinyTimCXTMWriter(OutputStream out, String base) throws IOException {
            super(out, base);
        }

        @Override
        public void write(TopicMap topicMap) throws IOException {
            super.write(((com.semagia.tmapix.io.GenericTMAPITopicMap)topicMap).getWrappedTopicMap()); 
        }
        
    }

    private static class OntopiaCXTMWriter implements TopicMapWriter {
        
        private final net.ontopia.topicmaps.xml.CanonicalXTMWriter _writer;

        public OntopiaCXTMWriter(OutputStream out) throws IOException {
            _writer = new net.ontopia.topicmaps.xml.CanonicalXTMWriter(out);
        }

        @Override
        public void write(TopicMap topicMap) throws IOException {
            _writer.write(TMAPIChooser.unwrapOntopia(topicMap));
        }

    }

    private static class GenericOntopiaCXTMWriter extends OntopiaCXTMWriter {

        public GenericOntopiaCXTMWriter(OutputStream out) throws IOException {
            super(out);
        }

        @Override
        public void write(TopicMap topicMap) throws IOException {
            super.write(((com.semagia.tmapix.io.GenericTMAPITopicMap)topicMap).getWrappedTopicMap()); 
        }

    }
}
