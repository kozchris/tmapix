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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.tmapi.core.TopicMap;

import com.semagia.mio.Source;
import com.semagia.mio.Syntax;

/**
 * {@link TopicMapReader} implementation that is able to deserialize XML Topic 
 * Maps (XTM) <a href="http://www.isotopicmaps.org/sam/sam-xtm/">version 2.0</a>.
 * <p>
 * Note: {@link XTMTopicMapReader} is a {@link TopicMapReader} which is able to 
 * parse any XTM version.
 * </p>
 *
 * @deprecated Use {@link XTMTopicMapReader}
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
@Deprecated
public final class XTM20TopicMapReader extends AbstractXTMTopicMapReader {

    /**
     * Constructs a new instance.
     *
     * @param topicMap The topic map to which the content is added to.
     * @param source The source to read the topic map from.
     * @param docIRI The document IRI which is used to resolve IRIs against.
     * @throws IOException If an error occurs. 
     */
    public XTM20TopicMapReader(final TopicMap topicMap, final File source,
            final String docIRI) throws IOException {
        super(topicMap, Syntax.XTM_20, source, docIRI);
    }

    /**
     * Constructs a new instance.
     * <p>
     * The <tt>source</tt> is converted into an absolute IRI which will be
     * utilised as document IRI
     * </p>
     *
     * @param topicMap The topic map to which the content is added to.
     * @param source The source to read the topic map from.
     * @throws IOException If an error occurs. 
     */
    public XTM20TopicMapReader(final TopicMap topicMap, final File source)
            throws IOException {
        super(topicMap, Syntax.XTM_20, source);
    }

    /**
     * Constructs a new instance.
     *
     * @param topicMap The topic map to which the content is added to.
     * @param source The source to read the topic map from.
     * @param docIRI The document IRI which is used to resolve IRIs against.
     */
    public XTM20TopicMapReader(final TopicMap topicMap, final InputStream source,
            final String docIRI) {
        super(topicMap, Syntax.XTM_20, source, docIRI);
    }

    /**
     * Constructs a new instance.
     *
     * @param topicMap The topic map to which the content is added to.
     * @param source The source to read the serialized topic map from.
     */
    public XTM20TopicMapReader(final TopicMap topicMap, final Source source) {
        super(topicMap, Syntax.XTM_20, source);
    }

}
