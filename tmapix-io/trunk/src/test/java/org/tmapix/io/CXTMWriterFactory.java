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

import java.io.IOException;
import java.io.OutputStream;

import org.tmapi.core.TopicMap;
import org.tmapix.io.TMAPIChooser;
import org.tmapix.io.TopicMapWriter;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
class CXTMWriterFactory implements ITestConstants {

    private static final String _MAJORTOM = "de.topicmapslab.majortom";

    static TopicMapWriter createCXTMTopicMapWriter(TopicMap topicMap, OutputStream out, String base) throws IOException {
        if (TMAPIChooser.isTinyTim(topicMap)) {
            return new TinyTimCXTMWriter(out, base);
        }
        else if (TMAPIChooser.isOntopia(topicMap)) {
            return new OntopiaCXTMWriter(out);
        }
        /*
        else if (topicMap.getClass().getName().startsWith(_MAJORTOM)) {
            return new MajortomCXTMWriter(out, base);
        }
        */
        else if (isGenericTMAPI(topicMap)) {
            if (TMAPIChooser.isTinyTim(((GenericTMAPITopicMap) topicMap).getWrappedTopicMap())) {
                return new GenericTinyTimCXTMWriter(out, base);
            }
            else {
                return new GenericOntopiaCXTMWriter(out);
            }
        }
        throw new IOException("No CXTM serializer found");
    }

    static boolean isGenericTMAPI(TopicMap topicMap) {
        return isGenericTMAPI(topicMap.getClass().getName());
    }

    private static boolean isGenericTMAPI(String className) {
        return className.startsWith(GENERIC_PACKAGE);
    }

    private static TopicMap _unwrap(TopicMap topicMap) {
        return ((GenericTMAPITopicMap)topicMap).getWrappedTopicMap();
    }

/*
    private static class MajortomCXTMWriter implements TopicMapWriter {

        private final de.topicmapslab.majortom.io.CXTMTopicMapWriter _writer;

        public MajortomCXTMWriter(OutputStream out, String base) throws IOException {
            _writer = new de.topicmapslab.majortom.io.CXTMTopicMapWriter(out, base);
        }

        @Override
        public void write(TopicMap topicMap) throws IOException {
            try {
                _writer.write(topicMap); 
            }
            catch (Exception ex) {
                final IOException ioex = new IOException("Unexpected error");
                ioex.initCause(ex);
                throw ioex;
            }
        }
        
    }
*/

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
            super.write(_unwrap(topicMap)); 
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
            super.write(_unwrap(topicMap)); 
        }

    }
}
