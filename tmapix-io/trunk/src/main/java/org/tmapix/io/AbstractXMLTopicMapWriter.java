/*
 * Copyright 2008 Lars Heuer (heuer[at]semagia.com)
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
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapix.voc.TMDM;
import org.xml.sax.helpers.AttributesImpl;

import com.semagia.mio.utils.xml.XMLWriter;

/**
 * Abstract superclass for XML serializers.
 * <p>
 * Provides a XML writer and takes care about the encoding.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
abstract class AbstractXMLTopicMapWriter extends AbstractTopicMapWriter {

    protected final AttributesImpl _attrs;

    protected final XMLWriter _out;

    private Topic _typeInstance;
    private Topic _type;
    private Topic _instance;

    /**
     * Creates a new instance using "utf-8" encoding.
     * 
     * @param out The output stream to write onto.
     * @param baseIRI The base IRI.
     * @throws IOException If an error occurs.
     */
    protected AbstractXMLTopicMapWriter(final OutputStream out, final String baseIRI)
            throws IOException {
        this(out, baseIRI, "utf-8");
    }

    /**
     * Creates a new instance.
     * 
     * @param out
     *            The output stream to write onto.
     * @param baseIRI
     *            The base IRI.
     * @param encoding
     *            The encoding to use.
     * @throws IOException
     *             If an error occurs.
     */
    protected AbstractXMLTopicMapWriter(final OutputStream out, final String baseIRI,
            final String encoding) throws IOException {
        super(baseIRI);
        if (encoding == null) {
            throw new IOException("The encoding must not be null");
        }
        _out = new XMLWriter(out, encoding);
        _out.setPrettify(false);
        _attrs = new AttributesImpl();
    }

    /**
     * Enables / disables newlines and indentation of XML elements.
     * (enabled by default)
     *
     * @param prettify <tt>true</tt> to enable prettified XML, otherwise <tt>false</tt>.
     */
    public void setPrettify(boolean prettify) {
        _out.setPrettify(prettify);
    }

    /**
     * Returns if newlines and indentation are enabled.
     *
     * @return <tt>true</tt> if prettified XML is enabled, otherwise <tt>false</tt>.
     */
    public boolean getPrettify() {
        return _out.getPrettify();
    }

    /**
     * Adds name/value tuple to the {@link #_attrs}.
     *
     * @param name The name of the attribute.
     * @param value The value of the attribute.
     */
    protected void addAttribute(final String name, final String value) {
        _attrs.addAttribute("", name, "", "CDATA", value);
    }

    protected void init(final TopicMap topicMap) {
        _typeInstance = topicMap.getTopicBySubjectIdentifier(topicMap.createLocator(TMDM.TYPE_INSTANCE));
        _type = topicMap.getTopicBySubjectIdentifier(topicMap.createLocator(TMDM.TYPE));
        _instance = topicMap.getTopicBySubjectIdentifier(topicMap.createLocator(TMDM.INSTANCE));
    }

    /**
     * Returns if the provided association represents a type-instance relationship.
     *
     * @param assoc The association.
     * @param roles The roles of the association.
     * @return {@code true} if the association represents a type-instance relationship,
     *          otherwise {@code false}.
     */
    protected final boolean isTypeInstanceAssociation(final Association assoc, final Set<Role> roles) { 
        return WriterUtils.isTypeInstanceAssociation(_typeInstance, _type, _instance, assoc, roles);
    }

}
