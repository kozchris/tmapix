/*
 * Copyright 2008 - 2009 Lars Heuer (heuer[at]semagia.com)
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

/**
 * A {@link TopicMapWriter} implementation that serializes a topic map into
 * a <a href="http://www.isotopicmaps.org/sam/sam-xtm/">XTM 2.0</a> 
 * representation.
 * 
 * @deprecated Use {@link XTM2TopicMapWriter}
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
@Deprecated
public class XTM20TopicMapWriter implements TopicMapWriter {

    private final XTM2TopicMapWriter _writer;

    /**
     * Creates a XTM 2.0 writer using "utf-8" encoding.
     *
     * @param out The stream the XTM is written onto.
     * @param baseIRI The base IRI which is used to resolve IRIs against.
     * @throws IOException If an error occurs.
     */
    public XTM20TopicMapWriter(final OutputStream out, final String baseIRI)
            throws IOException {
        _writer = new XTM2TopicMapWriter(out, baseIRI, XTMVersion.XTM_2_0);
    }

    /**
     * Creates a XTM 2.0 writer.
     *
     * @param out The stream the XTM is written onto.
     * @param baseIRI The base IRI which is used to resolve IRIs against.
     * @param encoding The encoding to use.
     * @throws IOException If an error occurs.
     */
    public XTM20TopicMapWriter(final OutputStream out, final String baseIRI,
            final String encoding) throws IOException {
        _writer = new XTM2TopicMapWriter(out, baseIRI, encoding, XTMVersion.XTM_2_0);
    }

    /* (non-Javadoc)
     * @see org.tmapix.io.TopicMapWriter#write(org.tmapi.core.TopicMap)
     */
    @Override
    public void write(final TopicMap topicMap) throws IOException {
        _writer.write(topicMap);
    }

}
