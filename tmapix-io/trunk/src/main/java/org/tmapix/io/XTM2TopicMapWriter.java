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
import java.util.Collection;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.DatatypeAware;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.Typed;
import org.tmapi.core.Variant;

import org.tmapix.io.XMLWriter;
import org.tmapix.voc.Namespace;
import org.tmapix.voc.TMDM;
import org.tmapix.voc.XSD;

import org.xml.sax.Attributes;

/**
 * A {@link TopicMapWriter} implementation that serializes a topic map into
 * a <a href="http://www.isotopicmaps.org/sam/sam-xtm/">XTM 2.0</a> 
 * or a <a href="http://www.isotopicmaps.org/sam/sam-xtm/2009-11-19/">XTM 2.1</a> 
 * representation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class XTM2TopicMapWriter extends AbstractXMLTopicMapWriter {

    private Topic _defaultNameType;
    private final XTMVersion _version;
    private boolean _exportIIds = true;

    /**
     * Creates a XTM 2.0 / 2.1 writer using "utf-8" encoding.
     *
     * @param out The output stream to write onto.
     * @param baseIRI The base IRI which is used to resolve IRIs against.
     * @param version The XTM version to use.
     * @throws IOException If an error occurs.
     */
    public XTM2TopicMapWriter(final OutputStream out, final String baseIRI, 
            final XTMVersion version) throws IOException {
        super(out, baseIRI);
        _version = version;
    }

    /**
     * Creates a XTM 2.0 / 2.1 writer.
     *
     * @param out The output stream to write onto.
     * @param baseIRI The base IRI which is used to resolve IRIs against.
     * @param encoding The encoding to use.
     * @param version The XTM version to use.
     * @throws IOException If an error occurs.
     */
    public XTM2TopicMapWriter(final OutputStream out, final String baseIRI,
            final String encoding, final XTMVersion version) throws IOException {
        super(out, baseIRI, encoding);
        _version = version;
    }

    /**
     * Returns if item identifiers are exported (enabeld by default).
     *
     * @return {@code true} if item identifiers are exported, otherwise {@code false}. 
     */
    public boolean getExportItemIdentifiers() {
        return _exportIIds;
    }

    /**
     * Configures the export of item identifiers.
     * 
     * If {@code export} is set to {@code false}, item identifiers are not
     * exported (enabled by default).
     *
     * @param export {@code true} if item identifiers should be exported, 
     *                  otherwise {@code false}.
     */
    public void setExportItemIdentifiers(final boolean export) {
        _exportIIds = export;
    }

    /**
     * Returns the XTM version which this instance writes.
     *
     * @return The XTM version.
     */
    public XTMVersion getVersion() {
        return _version;
    }

    /* (non-Javadoc)
     * @see org.tmapix.io.TopicMapWriter#write(org.tmapi.core.TopicMap)
     */
    @Override
    public void write(final TopicMap topicMap) throws IOException {
        // Cache the default name type. May be null, though
        _defaultNameType = topicMap.getTopicBySubjectIdentifier(topicMap.createLocator(TMDM.TOPIC_NAME));
        _out.startDocument();
        super.addAttribute("xmlns", Namespace.XTM_20);
        super.addAttribute("version", _version == XTMVersion.XTM_2_0 ? "2.0" : "2.1");
        if (_version == XTMVersion.XTM_2_0 && topicMap.getReifier() != null) {
            super.addAttribute("reifier", "#" + super.getId(topicMap.getReifier()));
        }
        _out.startElement("topicMap", _attrs);
        _writeReifier(topicMap);
        _writeItemIdentifiers(topicMap);
        for (Topic topic: topicMap.getTopics()) {
            _writeTopic(topic);
        }
        for (Association assoc: topicMap.getAssociations()) {
            _writeAssociation(assoc);
        }
        _out.endElement("topicMap");
        _out.endDocument();
    }

    private void _writeTopic(final Topic topic) throws IOException {
        // Ignore the topic if it is the default name type and it has no further
        // characteristics
        if (_isDefaultNameType(topic)
                && topic.getReified() == null
                && topic.getSubjectIdentifiers().size() == 1
                && topic.getSubjectLocators().isEmpty()
                && topic.getItemIdentifiers().isEmpty()
                && topic.getRolesPlayed().isEmpty()
                && topic.getTypes().isEmpty()
                && topic.getNames().isEmpty()
                && topic.getOccurrences().isEmpty()) {
            return;
        }
        boolean forceIIds = false;
        Collection<Locator> iids = null;
        Collection<Locator> sids = null;
        Collection<Locator> slos = null;
        _attrs.clear();
        if (_version == XTMVersion.XTM_2_0) {
            super.addAttribute("id", getId(topic));
            iids = topic.getItemIdentifiers();
            sids = topic.getSubjectIdentifiers();
            slos = topic.getSubjectLocators();
        }
        else {
            iids = topic.getItemIdentifiers();
            sids = topic.getSubjectIdentifiers();
            slos = topic.getSubjectLocators();
            forceIIds = sids.isEmpty() && slos.isEmpty();
            if (forceIIds && iids.isEmpty()) {
                super.addAttribute("id", getId(topic));
            }
        }
        _out.startElement("topic", _attrs);
        // item identifiers are written if exportIIds is true or if the
        // writer is in XTM 2.1 mode and the topic has no subject identifiers 
        // or subject locators
        if (_exportIIds || forceIIds) {
            // Cannot use _writeItemIdentifiers since it does nothing if exportIIds is false
            _writeLocators("itemIdentity", iids);
        }
        _writeLocators("subjectIdentifier", sids);
        _writeLocators("subjectLocator", slos);
        for (Topic type: topic.getTypes()) {
            _out.startElement("instanceOf");
            _writeTopicRef(type);
            _out.endElement("instanceOf");
        }
        for (Name name: topic.getNames()) {
            _writeName(name);
        }
        for (Occurrence occ: topic.getOccurrences()) {
            _writeOccurrence(occ);
        }
        _out.endElement("topic");
    }

    private void _writeAssociation(final Association assoc) throws IOException {
        Set<Role> roles = assoc.getRoles();
        if (roles.isEmpty()) {
            return;
        }
        _out.startElement("association", _reifier(assoc));
        _writeReifier(assoc);
        _writeItemIdentifiers(assoc);
        _writeType(assoc);
        _writeScope(assoc);
        for (Role role: roles) {
            _writeRole(role);
        }
        _out.endElement("association");
    }

    private void _writeRole(final Role role) throws IOException {
        _out.startElement("role", _reifier(role));
        _writeReifier(role);
        _writeItemIdentifiers(role);
        _writeType(role);
        _writeTopicRef(role.getPlayer());
        _out.endElement("role");
    }

    private void _writeName(final Name name) throws IOException {
        _out.startElement("name", _reifier(name));
        _writeReifier(name);
        _writeItemIdentifiers(name);
        if (!_isDefaultNameType(name.getType())) {
            _writeType(name);
        }
        _writeScope(name);
        _out.dataElement("value", name.getValue());
        for (Variant variant: name.getVariants()) {
            _writeVariant(variant);
        }
        _out.endElement("name");
    }

    private void _writeVariant(final Variant variant) throws IOException {
        _out.startElement("variant", _reifier(variant));
        _writeReifier(variant);
        _writeItemIdentifiers(variant);
        _writeScope(variant);
        _writeDatatypeAware(variant);
        _out.endElement("variant");
    }

    private void _writeOccurrence(final Occurrence occ) throws IOException {
        _out.startElement("occurrence", _reifier(occ));
        _writeReifier(occ);
        _writeItemIdentifiers(occ);
        _writeType(occ);
        _writeScope(occ);
        _writeDatatypeAware(occ);
        _out.endElement("occurrence");
    }

    private void _writeDatatypeAware(final DatatypeAware datatyped) throws IOException {
        _attrs.clear();
        final String datatype = datatyped.getDatatype().getReference();
        if (XSD.ANY_URI.equals(datatype)) {
            super.addAttribute("href", datatyped.locatorValue().toExternalForm());
            _out.emptyElement("resourceRef", _attrs);
        }
        else {
            if (!XSD.STRING.equals(datatype)) {
                super.addAttribute("datatype", datatyped.getDatatype().toExternalForm());
            }
            _out.dataElement("resourceData", _attrs, datatyped.getValue());
        }
    }

    /**
     * Writes the XTM 2.1 "reifier" element iff {@code reifiable} is reified
     * and the reifier is in XTM 2.1 mode.
     *
     * @param reifiable The reifiable construct.
     * @throws IOException
     */
    private void _writeReifier(final Reifiable reifiable) throws IOException {
        final Topic reifier = reifiable.getReifier();
        if (_version == XTMVersion.XTM_2_1 && reifier != null) {
            _out.startElement("reifier");
            _writeTopicRef(reifier);
            _out.endElement("reifier");
        }
    }

    /**
     * If the <tt>reifiable</tt> is reified and the writer is in XTM 2.0 mode, 
     * this method returns attributes with the a reference to the reifier, 
     * otherwise the attributes will be empty.
     *
     * @param reifiable The reifiable construct.
     * @return 
     */
    private Attributes _reifier(final Reifiable reifiable) {
        final Topic reifier = reifiable.getReifier();
        if (_version == XTMVersion.XTM_2_0 && reifier != null) {
            _attrs.clear();
            super.addAttribute("reifier", "#" + super.getId(reifier));
            return _attrs;
        }
        else {
            return XMLWriter.EMPTY_ATTRS;
        }
    }

    private void _writeTopicRef(final Topic topic) throws IOException {
        _attrs.clear();
        if (_version == XTMVersion.XTM_2_0) {
            super.addAttribute("href", "#" + super.getId(topic));
            _out.emptyElement("topicRef", _attrs);
        }
        else {
            // XTM 2.1
            if (!topic.getSubjectIdentifiers().isEmpty()) {
                super.addAttribute("href", topic.getSubjectIdentifiers().iterator().next().toExternalForm());
                _out.emptyElement("subjectIdentifierRef", _attrs);
            }
            else if (!topic.getSubjectLocators().isEmpty()) {
                super.addAttribute("href", topic.getSubjectLocators().iterator().next().toExternalForm());
                _out.emptyElement("subjectLocatorRef", _attrs);
            }
            else {
                if (!topic.getItemIdentifiers().isEmpty()){
                    super.addAttribute("href", topic.getItemIdentifiers().iterator().next().toExternalForm());
                }
                else {
                    super.addAttribute("href", "#" + super.getId(topic));
                }
                _out.emptyElement("topicRef", _attrs);
            }
        }
    }

    private void _writeType(final Typed typed) throws IOException {
        _out.startElement("type");
        _writeTopicRef(typed.getType());
        _out.endElement("type");
    }

    private void _writeScope(final Scoped scoped) throws IOException {
        final Set<Topic> scope = scoped.getScope();
        if (scope.isEmpty()) {
            return;
        }
        _out.startElement("scope");
        for (Topic theme: scope) {
            _writeTopicRef(theme);
        }
        _out.endElement("scope");
    }

    private void _writeItemIdentifiers(final Construct construct) throws IOException {
        if (_exportIIds) {
            _writeLocators("itemIdentity", construct.getItemIdentifiers());
        }
    }

    private void _writeLocators(final String name, final Iterable<Locator> locs) throws IOException {
        for (Locator loc: locs) {
            _attrs.clear();
            super.addAttribute("href", loc.toExternalForm());
            _out.emptyElement(name, _attrs);
        }
    }

    /**
     * Checks if the specified <tt>topic</tt> is the default TMDM name type.
     *
     * @param topic The topic to check, not <tt>null</tt>.
     * @return <tt>true</tt> if the topic is the default name type, otherwise <tt>false</tt>.
     */
    private boolean _isDefaultNameType(final Topic topic) {
        return topic.equals(_defaultNameType);
    }

}
