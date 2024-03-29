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
import org.tmapix.io.internal.utils.XTM10Utils;

import com.semagia.mio.Source;
import com.semagia.mio.Syntax;

/**
 * {@link TopicMapReader} implementation that is able to deserialize XML Topic 
 * Maps (XTM) <a href="http://www.topicmaps.org/xtm/1.0/">version 1.0</a>,
 * <a href="http://www.isotopicmaps.org/sam/sam-xtm/">version 2.0</a> and
 * <a href="http://www.itscj.ipsj.or.jp/sc34/open/1378.htm">version 2.1</a>.
 * <p>
 * This reader detects automatically the XTM version.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class XTMTopicMapReader extends AbstractXTMTopicMapReader {

    private boolean _convertToTMDM;

    /**
     * Constructs a new instance.
     *
     * @param topicMap The topic map to which the content is added to.
     * @param source The source to read the topic map from.
     * @param docIRI The document IRI which is used to resolve IRIs against.
     * @throws IOException If an error occurs. 
     */
    public XTMTopicMapReader(final TopicMap topicMap, final File source,
            final String docIRI) throws IOException {
        super(topicMap, Syntax.XTM, source, docIRI);
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
    public XTMTopicMapReader(final TopicMap topicMap, final File source) throws IOException {
        super(topicMap, Syntax.XTM, source);
    }

    /**
     * Constructs a new instance.
     *
     * @param topicMap The topic map to which the content is added to.
     * @param source The source to read the topic map from.
     * @param docIRI The document IRI which is used to resolve IRIs against.
     */
    public XTMTopicMapReader(final TopicMap topicMap, final InputStream source,
            final String docIRI) {
        super(topicMap, Syntax.XTM, source, docIRI);
    }

    /**
     * Constructs a new instance.
     *
     * @param topicMap The topic map to which the content is added to.
     * @param source The source to read the serialized topic map from.
     */
    public XTMTopicMapReader(final TopicMap topicMap, final Source source) {
        super(topicMap, Syntax.XTM, source);
    }

    /**
     * Enables / disables translation of XTM 1.0 PSIs and the XTM 1.0
     * reification mechanism to the TMDM equivalent.
     * <p>
     * The reader will convert XTM 1.0 PSIs like 
     * <tt>http://www.topicmaps.org/xtm/1.0/core.xtm#class-instance</tt>, 
     * <tt>http://www.topicmaps.org/xtm/1.0/core.xtm#class</tt> etc. and the 
     * XTM 1.0 reification mechanism to the TMDM equivalent (disabled by default). 
     * </p>
     *
     * @param translate <tt>true</tt> to enable the translation, 
     *          <tt>false</tt> to disable the translation.
     */
    public void setTranslateToTMDM(boolean translate) {
        _convertToTMDM = translate;
    }

    /**
     * Returns if the reader translates XTM 1.0 topic maps into the TMDM 
     * equivalent.
     *
     * @return <tt>true</tt> if the XTM 1.0 is translated, otherwise <tt>false</tt>.
     */
    public boolean isTranslatingToTMDM() {
        return _convertToTMDM;
    }

    /* (non-Javadoc)
     * @see org.tmapix.io.AbstractTopicMapReader#postProcess()
     */
    @Override
    protected void postProcess() {
        if (_convertToTMDM && super._tm != null) {
            XTM10Utils.convertToTMDM(super._tm);
        }
    }

}
