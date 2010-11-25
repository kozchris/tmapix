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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.Variant;
import org.tmapi.index.ScopedIndex;
import org.tmapi.index.TypeInstanceIndex;

import org.tmapix.voc.TMDM;
import org.tmapix.voc.XSD;
import org.tmapix.voc.XTM10;

/**
 * {@link TopicMapWriter} implementation that is able to serialize topic maps
 * into a 
 * <a href="http://www.ontopia.net/download/ltm.html">Linear Topic Maps Notation (LTM) 1.3</a>
 * representation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class LTMTopicMapWriter extends AbstractBaseTextualTopicMapWriter {

    private String _lastReference;
    private Topic _xtmDisplayName;
    private Topic _xtmSortName;
    private Topic _tmdmSortName;
    private final Map<Topic, String> _topic2Reference;
    private final Map<String, String> _sidPrefixes;
    private final Map<String, String> _sloPrefixes;

    /**
     * Creates a LTM writer, using "utf-8" encoding.
     *
     * @param out The stream the LTM is written onto.
     * @param baseIRI The base IRI which is used to resolve IRIs against.
     * @throws IOException If an error occurs.
     */
    public LTMTopicMapWriter(OutputStream out, String baseIRI) throws IOException {
        this(out, baseIRI, "utf-8");
    }

    /**
     * Creates a LTM writer.
     *
     * @param out The stream the LTM is written onto.
     * @param baseIRI The base IRI which is used to resolve IRIs against.
     * @param encoding The encoding to use.
     * @throws IOException If an error occurs.
     */
    public LTMTopicMapWriter(OutputStream out, String baseIRI, String encoding)
        throws IOException {
        super(out, baseIRI, encoding);
        _topic2Reference = new HashMap<Topic, String>();
        _sidPrefixes = new HashMap<String, String>();
        _sloPrefixes = new HashMap<String, String>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final Topic... topics) throws IOException {
    	throw new UnsupportedOperationException("Not implemented yet.");
    }

    /* (non-Javadoc)
     * @see org.tmapix.io.TopicMapWriter#write(org.tmapi.core.TopicMap)
     */
    public void write(final TopicMap topicMap) throws IOException {
        super.init(topicMap);
        _xtmDisplayName = topicMap.getTopicBySubjectIdentifier(topicMap.createLocator(XTM10.DISPLAY));
        _xtmSortName = topicMap.getTopicBySubjectIdentifier(topicMap.createLocator(XTM10.SORT));
        _tmdmSortName = topicMap.getTopicBySubjectIdentifier(topicMap.createLocator(TMDM.SORT));
        
        _out.write("@\"" + _encoding + "\"");
        super.newline();
        _out.write("#VERSION \"1.3\"");
        super.newline();
        _writeFileHeader();
        _writePrefixes();
        final Collection<Topic> topics = new ArrayList<Topic>(topicMap.getTopics());
        
        final boolean omitDefaultNameType = _defaultNameType != null
                                            && _defaultNameType.getTypes().isEmpty()
                                            && _defaultNameType.getReified() == null
                                            && _defaultNameType.getNames().isEmpty()
                                            && _defaultNameType.getOccurrences().isEmpty()
                                            && _defaultNameType.getSubjectIdentifiers().size() == 1
                                            && _defaultNameType.getSubjectLocators().isEmpty()
                                            && _defaultNameType.getRolesPlayed().isEmpty();
        if (omitDefaultNameType) {
            topics.remove(_defaultNameType);
        }
        
        if (topicMap.getReifier() != null) {
            _writeSection("Topic Map");
            _out.write("#TOPICMAP");
            _writeReifier(topicMap);
            super.newline();
            _writeTopic(topicMap.getReifier(), false);
            topics.remove(topicMap.getReifier());
        }
        _writeSection("ONTOLOGY");
        final TypeInstanceIndex tiIdx = topicMap.getIndex(TypeInstanceIndex.class);
        if (!tiIdx.isOpen()) {
            tiIdx.open();
        }
        if (!tiIdx.isAutoUpdated()) {
            tiIdx.reindex();
        }
        _writeOntologySection(tiIdx.getTopicTypes(), topics, "Topic Types");
        _writeOntologySection(tiIdx.getAssociationTypes(), topics, "Association Types");
        _writeOntologySection(tiIdx.getRoleTypes(), topics, "Role Types");
        _writeOntologySection(tiIdx.getOccurrenceTypes(), topics, "Occurrence Types");
        Collection<Topic> nameTypes = tiIdx.getNameTypes();
        if (omitDefaultNameType) {
            // Need a copy since the returned collection is read-only
            nameTypes = new ArrayList<Topic>(nameTypes);
            nameTypes.remove(_defaultNameType);
        }
        _writeOntologySection(nameTypes, topics, "Name Types");
        tiIdx.close();
        final ScopedIndex scopedIdx = topicMap.getIndex(ScopedIndex.class);
        if (!scopedIdx.isOpen()) {
            scopedIdx.open();
        }
        if (!scopedIdx.isAutoUpdated()) {
            scopedIdx.reindex();
        }
        _writeOntologySection(scopedIdx.getAssociationThemes(), topics, "Association Themes");
        _writeOntologySection(scopedIdx.getOccurrenceThemes(), topics, "Occurrence Themes");
        _writeOntologySection(scopedIdx.getNameThemes(), topics, "Name Themes");
        _writeOntologySection(scopedIdx.getVariantThemes(), topics, "Variant Themes");
        scopedIdx.close();
        _writeSection("INSTANCES");
        _writeSection("Topics");
        _writeTopics(topics);
        _writeSection("Associations");
        for (Association assoc: topicMap.getAssociations()) {
            _writeAssociation(assoc);
        }
        super.newline();
        _out.write("/* Thanks for using TMAPIX I/O :) */");
        super.newline();
        _out.flush();
        _sloPrefixes.clear();
        _sidPrefixes.clear();
        _topic2Reference.clear();
    }

    /**
     * Adds a subject identifier prefix to the writer.
     * <p>
     * The writer converts all subject identifiers into QNames which start 
     * with the provided <tt>reference</tt>.
     * </p>
     * <p>
     * I.e. if a prefix "wp" is set to "http://en.wikipedia.org/wiki", a 
     * subject identifier like "http://en.wikipedia.org/wiki/John_Lennon" is 
     * converted into a QName "wp:John_Lennon".
     * </p>
     *
     * @param prefix The prefix to add, an existing prefix with the same name
     *                  will be overridden.
     * @param reference The IRI to which the prefix should be assigned to.
     */
    public void addSubjectIdentifierPrefix(String prefix, String reference) {
        if (prefix == null) {
            throw new IllegalArgumentException("The prefix must not be null");
        }
        if (!_isValidId(prefix)) {
            throw new IllegalArgumentException("The prefix is an invalid LTM identifier: " + prefix);
        }
        if (reference == null) {
            throw new IllegalArgumentException("The IRI must not be null");
        }
        if (!_isValidIRI(reference)) {
            throw new IllegalArgumentException("The IRI is invalid: " + reference);
        }
        if (_sloPrefixes.containsKey(prefix)) {
            throw new IllegalArgumentException("The prefix is already used as subject locator");
        }
        _sidPrefixes.put(prefix, reference);
    }

    /**
     * Removes a subject identifier prefix mapping.
     *
     * @param prefix The prefix to remove.
     */
    public void removeSubjectIdentifierPrefix(String prefix) {
        _sidPrefixes.remove(prefix);
    }

    /**
     * Adds a subject locator prefix to the writer.
     * <p>
     * The writer converts all subject locators into QNames which start 
     * with the provided <tt>reference</tt>.
     * </p>
     * <p>
     * I.e. if a prefix "wp" is set to "http://en.wikipedia.org/wiki", a 
     * subject locator like "http://en.wikipedia.org/wiki/John_Lennon" is 
     * converted into a QName "wp:John_Lennon".
     * </p>
     *
     * @param prefix The prefix to add, an existing prefix with the same name
     *                  will be overridden.
     * @param reference The IRI to which the prefix should be assigned to.
     */
    public void addSubjectLocatorPrefix(String prefix, String reference) {
        if (prefix == null) {
            throw new IllegalArgumentException("The prefix must not be null");
        }
        if (!_isValidId(prefix)) {
            throw new IllegalArgumentException("The prefix is an invalid LTM identifier: " + prefix);
        }
        if (reference == null) {
            throw new IllegalArgumentException("The IRI must not be null");
        }
        if (!_isValidIRI(reference)) {
            throw new IllegalArgumentException("The IRI is invalid: " + reference);
        }
        if (_sidPrefixes.containsKey(prefix)) {
            throw new IllegalArgumentException("The prefix is already used as subject identifier");
        }
        _sloPrefixes.put(prefix, reference);
    }

    /**
     * Removes a subject locator prefix mapping.
     *
     * @param prefix The prefix to remove.
     */
    public void removeSubjectLocatorPrefix(String prefix) {
        _sloPrefixes.remove(prefix);
    }

    /**
     * Writes the registered prefixes.
     *
     * @throws IOException In case of an error.
     */
    private void _writePrefixes() throws IOException {
        if (_sloPrefixes.isEmpty() && _sidPrefixes.isEmpty()) {
            return;
        }
        _writeSection("Prefixes");
        _writePrefixes(_sidPrefixes, '@');
        _writePrefixes(_sloPrefixes, '%');
    }
    
    /**
     * Writes the provided prefixes.
     *
     * @param prefixes The prefixes to write.
     * @param locIndicator The kind of prefix, either <tt>%</tt> or <tt>@</tt>.
     * @throws IOException In case of an error.
     */
    private void _writePrefixes(final Map<String, String> prefixes, final char locIndicator) throws IOException {
        String[] keys = prefixes.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        for (String ident: keys) {
            _out.write("#PREFIX " + ident + " " + locIndicator);
            _writeString(prefixes.get(ident));
            super.newline();
        }
    }

    /**
     * Writes the header comment with the optional title, author, license etc.
     * information.
     *
     * @throws IOException In case of an error.
     */
    private void _writeFileHeader() throws IOException {
        super.newline();
        super.newline();
        _out.write("/*");
        super.newline();
        _out.write(" *  ");
        _out.write("Generated by TMAPIX I/O <http://www.tmapix.org/>");
        super.newline();
        _out.write(" */");
    }

    /**
     * Writes a section name.
     *
     * @param name The section name to write.
     * @throws IOException In case of an error.
     */
    private void _writeSection(final String name) throws IOException {
        super.newline();
        super.newline();
        _out.write("/* -- " + name + " */");
        super.newline();
    }

    /**
     * If <tt>topics</tt> is not empty, the topics will be removed from 
     * <tt>allTopics</tt> and written out under the specified section 
     * <tt>title</tt>. 
     *
     * @param topics The topics to serialize.
     * @param allTopics A collection of topics where the <tt>topics</tt> should be removed from.
     * @param title The title of the ontology section.
     * @throws IOException In case of an error.
     */
    private void _writeOntologySection(final Collection<Topic> topics, Collection<Topic> allTopics, String title) throws IOException {
        if (!topics.isEmpty()) {
            allTopics.removeAll(topics);
            _writeSection(title);
            _writeTopics(topics);
        }
    }

    /**
     * Sorts the specified collection of topics and serializes it.
     *
     * @param topics An unordered collection of topics.
     * @throws IOException In case of an error.
     */
    private void _writeTopics(final Collection<Topic> topics) throws IOException {
        _lastReference = null;
        Topic[] topicArray = topics.toArray(new Topic[topics.size()]);
        Arrays.sort(topicArray, super.getTopicComparator());
        for (Topic topic: topicArray) {
            _writeTopic(topic, true);
        }
    }

    /**
     * Serializes a the specified topic.
     *
     * @param topic The topic to serialize.
     * @throws IOException In case of an error.
     */
    private void _writeTopic(final Topic topic, final boolean topicTypeHeader) throws IOException {
        final Topic[] types = getTypes(topic);
        if (topicTypeHeader) {
            _writeTopicHeader(types);
        }
        _out.write('[');
        _writeTopicRef(topic);
        final boolean hasTypes = types.length > 0;
        final int namesStart = hasTypes ? 0 : 1;
        final Name[] names = super.getNames(topic);
        if (hasTypes) {
            _out.write(" :");
            for (Topic type: types) {
                _out.write(' ');
                _writeTopicRef(type);
            }
        }
        else if (names.length > 0) {
            _out.write(' ');
            _writeName(names[0]);
        }
        for (int i=namesStart; i<names.length; i++) {
            if (i>0) {
                super.newline();
                super.indent();
            }
            _writeName(names[i]);
        }
        final Locator[] slos = super.getSubjectLocators(topic);
        if (slos.length > 1) {
            super.newline();
            super.indent();
            _out.write("/* The topic has more than one subject locator. Writing just one */");
        }
        if (slos.length > 0) {
            super.newline();
            super.indent();
            _out.write('%');
            _writeString(slos[0].toExternalForm());
        }
        for (Locator sid: super.getSubjectIdentifiers(topic)) {
            super.newline();
            super.indent();
            _out.write('@');
            _writeString(sid.toExternalForm());
        }
        _out.write(']');
        super.newline();
        for (Occurrence occ: super.getOccurrences(topic)) {
            _writeOccurrence(occ);
        }
    }

    /**
     * Writes a header with the very first topic type.
     * 
     * The header is written if the the first topic type is not equal to 
     * the last written header.
     *
     * @param types The topic types.
     * @throws IOException In case of an error.
     */
    private void _writeTopicHeader(Topic[] types) throws IOException {
        final String reference = types.length > 0 ? getTopicReference(types[0]) : UNTYPED;
        if (!reference.equals(_lastReference)) {
            _writeSection("TT: " + reference);
            _lastReference = reference;
        }
    }

    /**
     * Serializes the specified occurrence.
     *
     * @param occ The occurrence to serialize
     * @param wantSemicolon Indicates if a semicolon should be written.
     * @throws IOException In case of an error.
     */
    private void _writeOccurrence(final Occurrence occ) throws IOException {
        final boolean isURI = XSD.ANY_URI.equals(occ.getDatatype().getReference());
        final boolean isString = isURI ? false : XSD.STRING.equals(occ.getDatatype().getReference());
        if (!(isURI || isString)) {
            _out.write("/* The occurrence '" + occ.getId() + "' has neither a xsd:string nor xsd:anyURI datatype */");
            return;
        }
        super.indent();
        _out.write('{');
        _writeTopicRef(occ.getParent());
        _out.write(", ");
        _writeTopicRef(occ.getType());
        _out.write(", ");
        if (isURI) {
            _writeString(occ.locatorValue().toExternalForm());
        }
        else {
            _out.write("[[");
            final char[] ch = occ.getValue().toCharArray();
            for (int i=0; i<ch.length; i++) {
                switch(ch[i]) {
                    case ']': _out.write("\\u00005D"); break;
                    default: _out.write(ch[i]);
                }
            }
            _out.write("]]");
        }
        _out.write('}');
        _writeScope(occ);
        _writeReifier(occ);
        super.newline();
    }

    /**
     * Serializes the specified name.
     *
     * @param name The name to serialize.
     * @param wantSemicolon Indicates if a semicolon should be written.
     * @throws IOException In case of an error.
     */
    private void _writeName(final Name name) throws IOException {
        if (!name.getType().equals(_defaultNameType)) {
            _out.write("/* The name '" + name.getId() + "' uses a type which is not equal to tmdm:topic-name */");
            return;
        }
        _out.write("= ");
        _writeString(name.getValue());
        Variant displayName = null;
        Variant sortName = null;
        final int nameScopeSize = name.getScope().size();
        final Variant[] variants = super.getVariants(name);
        for (Variant variant: variants) {
            final Collection<Topic> variantScope = variant.getScope();
            if (variant.getReifier() != null 
                    || variantScope.size() - nameScopeSize != 1
                    || !XSD.STRING.equals(variant.getDatatype().getReference())) {
                continue;
            }
            if (displayName == null 
                    && variantScope.contains(_xtmDisplayName)) {
                displayName = variant;
                if (sortName != null) {
                    break;
                }
                else {
                    continue;
                }
            }
            if (sortName == null 
                    && ((variantScope.contains(_xtmSortName) 
                            || variantScope.contains(_tmdmSortName)))) {
                sortName = variant;
                if (displayName != null) {
                    break;
                }
            }
        }
        if (sortName != null || displayName != null) {
            _out.write(';');
        }
        if (sortName != null) {
            _out.write(' ');
            _writeString(sortName.getValue());
        }
        if (displayName != null) {
            _out.write("; ");
            _writeString(displayName.getValue());
        }
        _writeScope(name);
        _writeReifier(name);
        for (Variant variant: variants) {
            if (variant.equals(displayName) || variant.equals(sortName)) {
                continue;
            }
            _out.write(' ');
            _writeVariant(variant);
        }
    }

    /**
     * Serializes the specified variant.
     *
     * @param variant The variant to serialize.
     * @throws IOException In case of an error.
     */
    private void _writeVariant(final Variant variant) throws IOException {
        if (XSD.STRING.equals(variant.getDatatype().toExternalForm())) {
            _out.write('(');
            _writeString(variant.getValue());
            _writeScope(variant);
            _writeReifier(variant);
            _out.write(')');
        }
        else {
            _out.write("/* The variant '" + variant.getId() + "' has no xsd:string datatype */");
        }
    }

    /**
     * Serializes the scope of the scoped construct if the scope is not 
     * unconstrained.
     *
     * @param scoped The scoped construct from which the scope should be written.
     * @throws IOException In case of an error.
     */
    private void _writeScope(final Scoped scoped) throws IOException {
        final Topic[] themes = getThemes(scoped);
        if (themes.length == 0) {
            return;
        }
        _out.write(" /");
        _writeTopicRef(themes[0]);
        for (int i=1; i<themes.length; i++) {
            _out.write(' ');
            _writeTopicRef(themes[i]);
        }
    }

    /**
     * Writes a string.
     * 
     * This method converts <tt>"</tt> within the string to <tt>""</tt>.
     *
     * @param str The string to write.
     * @throws IOException In case of an error.
     */
    private void _writeString(final String str) throws IOException {
        final char[] ch = str.toCharArray();
        _out.write('"');
        for (int i=0; i<ch.length; i++) {
            switch (ch[i]) {
                case '"': _out.write('"');
                default: _out.write(ch[i]);
            }
        }
        _out.write('"');
    }

    private void _writeTopicRef(final Topic topic) throws IOException {
        _out.write(getTopicReference(topic));
    }

    /**
     * Writes the reifier iff <tt>reifiable</tt> is reified.
     *
     * @param reifiable The reifiable construct.
     * @throws IOException If an error occurs.
     */
    private void _writeReifier(final Reifiable reifiable) throws IOException {
        if (reifiable.getReifier() != null) {
            _out.write(" ~ ");
            _writeTopicRef(reifiable.getReifier());
        }
    }

    /**
     * Serializes the specified association.
     *
     * @param assoc The association to serialize.
     * @throws IOException In case of an error.
     */
    private void _writeAssociation(final Association assoc) throws IOException {
        if (super.isTypeInstanceAssociation(assoc, assoc.getRoles())) {
            return;
        }
        super.newline();
        _writeTopicRef(assoc.getType());
        _out.write('(');
        Role[] roles = super.getRoles(assoc);
        _writeRole(roles[0]);
        for (int i=1; i<roles.length; i++) {
            _out.write(", ");
            _writeRole(roles[i]);
        }
        _out.write(')');
        _writeScope(assoc);
        _writeReifier(assoc);
        super.newline();
    }

    /**
     * Serializes the specified association role.
     *
     * @param role The association role to serialize.
     * @throws IOException In case of an error.
     */
    private void _writeRole(final Role role) throws IOException {
        _writeTopicRef(role.getPlayer());
        _out.write(": ");
        _writeTopicRef(role.getType());
        _writeReifier(role);
    }
    

    /* (non-Javadoc)
     * @see org.tmapix.io.AbstractTextualTopicMapWriter#getTopicReference(org.tmapi.core.Topic)
     */
    @Override
    protected String getTopicReference(final Topic topic) {
        String id = _topic2Reference.get(topic);
        if (id == null) {
            id = _generateTopicReference(topic);
            _topic2Reference.put(topic, id);
        }
        return id;
    }

    private String _generateTopicReference(final Topic topic) {
        final List<String> refs = new ArrayList<String>();
        for (Locator iid: topic.getItemIdentifiers()) {
            String addr = iid.toExternalForm();
            int idx = addr.indexOf('#');
            if (idx > 0) {
                String id = addr.substring(idx+1);
                if (_isValidId(id)) {
                    refs.add(id);
                }
            }
        }
        if (refs.isEmpty() && !_sidPrefixes.isEmpty()) {
            for (Locator sid: topic.getSubjectIdentifiers()) {
                for (Entry<String, String> entry: _sidPrefixes.entrySet()) {
                    String externalForm = sid.toExternalForm();
                    if (externalForm.startsWith(entry.getValue())) {
                        refs.add(entry.getKey() + ":" + externalForm.substring(entry.getValue().length()));
                    }
                }
            }
        }
        if (refs.isEmpty() && !_sloPrefixes.isEmpty()) {
            for (Locator slo: topic.getSubjectLocators()) {
                for (Entry<String, String> entry: _sloPrefixes.entrySet()) {
                    String externalForm = slo.toExternalForm();
                    if (externalForm.startsWith(entry.getValue())) {
                        refs.add(entry.getKey() + ":" + externalForm.substring(entry.getValue().length()));
                    }
                }
            }
        }
        return refs.isEmpty() ? "t-" + topic.getId() : refs.get(0);
    }

    private boolean _isValidId(final String id) {
        if (id.length() == 0) {
            return false;
        }
        final char[] ch = id.toCharArray();
        char c = ch[0];
        if (!((c >= 'A' && c <= 'Z') 
                || (c >= 'a' && c <= 'z') 
                || c == '_')) {
            return false;
        }
        for (int i=1; i<ch.length; i++) {
            c = ch[i];
            if (!((c >= 'A' && c <= 'Z')
                    || (c >= 'a' && c <= 'z')
                    || (c >= '0' && c <= '9') 
                    || c == '_' 
                    || c == '-' 
                    || c == '.')) {
                return false;
            }
        }
        return true;
    }

    private boolean _isValidIRI(String iri) {
        if (iri.length() == 0) {
            return false;
        }
        final char c = iri.charAt(0);
        if (!((c >= 'A' && c <= 'Z') 
                || (c >= 'a' && c <= 'z') 
                || c == '#')) {
            return false;
        }
        return true;
    }

}
