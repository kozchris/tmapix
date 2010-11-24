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
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.DatatypeAware;
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

import com.semagia.mio.ctm.CTMUtils;

/**
 * {@link TopicMapWriter} implementation that is able to serialize topic maps
 * into a 
 * <a href="http://www.isotopicmaps.org/ctm/">Compact Topic Maps (CTM) 1.0</a>
 * representation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @author Hannes Niederhausen
 * @version $Rev$ - $Date$
 */
public class CTMTopicMapWriter extends AbstractBaseTextualTopicMapWriter {

    private static final Reference[] _EMPTY_REFERENCE_ARRAY = new Reference[0];

    private static final Topic[] _EMPTY_TOPIC_ARRAY = new Topic[0];
    private static final Reference _UNTYPED_REFERENCE = Reference.createId("[untyped]");

    private Topic _defaultNameType;
    private boolean _exportIIDs;
    private boolean _keepAbsoluteIIDs;
    private String _title;
    private String _author;
    private String _license;
    private String _comment;
    private final Comparator<Topic> _topicIdComparator;
    private final Map<Topic, Reference> _topic2Reference; //TODO: LRU?
    private final Map<String, String> _prefixes;
    private final Set<String> _imports;
    private final Map<Topic, Collection<Topic>> _topic2Supertypes;
    private Reference _lastReference;

    /**
     * Constructs a new instance using "utf-8" encoding.
     * <p>
     * The base IRI is used to abbreviate IRIs. IRIs with a fragment identifier
     * (like <tt>#my-topic</tt>) are written in an abbreviated from iff they
     * start with the provided base IRI.
     * </p>
     *
     * @param out The stream to write onto.
     * @param baseIRI The base IRI to resolve locators against.
     * @throws IOException In case of an error.
     */
    public CTMTopicMapWriter(final OutputStream out, final String baseIRI) throws IOException {
        this(out, baseIRI, "utf-8");
    }

    /**
     * Constructs a new instance with the specified encoding.
     * <p>
     * The base IRI is used to abbreviate IRIs. IRIs with a fragment identifier
     * (like <tt>#my-topic</tt>) are written in an abbreviated from iff they
     * start with the provided base IRI.
     * </p>
     *
     * @param out The stream to write onto.
     * @param baseIRI The base IRI to resolve locators against.
     * @param encoding The encoding to use.
     * @throws IOException In case of an error, i.e. if the encoding is unsupported.
     */
    public CTMTopicMapWriter(final OutputStream out, final String baseIRI, final String encoding) throws IOException {
        this(new OutputStreamWriter(out, encoding), baseIRI, encoding);
    }

    /**
     * Constructs a new instance.
     *
     * @param writer The writer to use.
     * @param baseIRI The base IRI to resolve locators against.
     * @param encoding The encoding to use.
     */
    private CTMTopicMapWriter(final Writer writer, final String baseIRI, final String encoding) {
        super(writer, baseIRI, encoding);
        _topic2Reference = new HashMap<Topic, Reference>(200);
        _topicIdComparator = new TopicIdComparator();
        _prefixes = new HashMap<String, String>();
        _imports = new HashSet<String>();
        _topic2Supertypes = new HashMap<Topic, Collection<Topic>>();
        setExportItemIdentifiers(false);
    }

    /**
     * Sets the title of the topic map which appears in the header comment of
     * the file.
     *
     * @param title The title of the topic map.
     */
    public void setTitle(String title) {
        _title = title;
    }

    /**
     * Returns the title of the topic map.
     *
     * @return The title or <tt>null</tt> if no title was set.
     */
    public String getTitle() {
        return _title;
    }

    /**
     * Sets the author which appears in the header comment of the file.
     *
     * @param author The author.
     */
    public void setAuthor(String author) {
        _author = author;
    }

    /**
     * Returns the author.
     *
     * @return The author or <tt>null</tt> if no author was set.
     */
    public String getAuthor() {
        return _author;
    }

    /**
     * Sets the license which should appear in the header comment of the file.
     * <p>
     * The license of the topic map. This could be a name or an IRI or both, i.e.
     * "Creative Commons-License <http://creativecommons.org/licenses/by-nc-sa/3.0/>".
     * </p>
     *
     * @param license The license.
     */
    public void setLicense(String license) {
        _license = license;
    }

    /**
     * Returns the license.
     *
     * @return The license or <tt>null</tt> if no license was set.
     */
    public String getLicense() {
        return _license;
    }

    /**
     * The an additional comment which appears in the header comment of the file.
     * <p>
     * The comment could describe the topic map, or provide an additional 
     * copyright notice, or SVN/CVS keywords etc.
     * </p>
     *
     * @param comment The comment.
     */
    public void setComment(String comment) {
        _comment = comment;
    }

    /**
     * Returns the comment.
     *
     * @return The comment or <tt>null</tt> if no comment was set.
     */
    public String getComment() {
        return _comment;
    }

    /**
     * Adds a prefix to the writer.
     * <p>
     * The writer converts all locators (item identifiers, subject identifiers,
     * subject locators) into QNames which start with the provided 
     * <tt>reference</tt>.
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
    public void addPrefix(String prefix, String reference) {
        if (prefix == null) {
            throw new IllegalArgumentException("The prefix must not be null");
        }
        if (!CTMUtils.isValidId(prefix)) {
            throw new IllegalArgumentException("The prefix is an invalid CTM identifier: " + prefix);
        }
        if (reference == null) {
            throw new IllegalArgumentException("The reference must not be null");
        }
        if (!CTMUtils.isValidIRI(reference)) {
            throw new IllegalArgumentException("The reference is an invalid CTM IRI: " + reference);
        }
        _prefixes.put(prefix, reference);
    }

    /**
     * Removes a prefix mapping.
     *
     * @param prefix The prefix to remove.
     */
    public void removePrefix(String prefix) {
        _prefixes.remove(prefix);
    }

    /**
     * Indicates if the item identifiers of the topics should be exported.
     * <p>
     * By default, this feature is disabled.
     * </p>
     *
     * @param export <tt>true</tt> to export item identifiers, otherwise <tt>false</tt>.
     */
    public void setExportItemIdentifiers(boolean export) {
        setExportItemIdentifiers(export, export);
    }

    /**
     * Indicates if the item identifiers of a topic are exported.
     *
     * @return <tt>true</tt> if the item identifiers are exported, otherwise <tt>false</tt>.
     */
    public boolean getExportItemIdentifiers() {
        return _exportIIDs;
    }

    // Unsure if this feature should be exposed, keep it private currently
    private void setExportItemIdentifiers(boolean export, boolean keepAbsoluteItemIdentifiers) {
        _exportIIDs = export;
        _keepAbsoluteIIDs = keepAbsoluteItemIdentifiers;
    }

    /**
     * {@inheritDoc}
     */
    public void write(final Topic[] topics) throws IOException {
    	throw new UnsupportedOperationException("Not implemented yet.");
    }
    
    /* (non-Javadoc)
     * @see org.tinytim.mio.TopicMapWriter#write(org.tmapi.core.TopicMap)
     */
    public void write(final TopicMap topicMap) throws IOException {
        _defaultNameType = topicMap.getTopicBySubjectIdentifier(topicMap.createLocator(TMDM.TOPIC_NAME));
        _out.write("%encoding \"" + _encoding + "\"");
        _newline();
        _out.write("%version 1.0");
        _writeFileHeader();
        _writeImports();
        _writePrefixes();
        _newline();
        Collection<Topic> topics = new ArrayList<Topic>(topicMap.getTopics());
        Collection<Association> assocs = new ArrayList<Association>(topicMap.getAssociations());
        final TypeInstanceIndex tiIdx = topicMap.getIndex(TypeInstanceIndex.class);
        if (!tiIdx.isOpen()) {
            tiIdx.open();
        }
        if (!tiIdx.isAutoUpdated()) {
            tiIdx.reindex();
        }
        _createSupertypeSubtypeRelationships(tiIdx, topicMap, assocs);
        if (topicMap.getReifier() != null) {
            // Special handling of the tm reifier to avoid an additional 
            // whitespace character in front of the ~
            Topic reifier = topicMap.getReifier();
            _writeSection("Topic Map");
            _out.write("~ ");
            _writeTopicRef(reifier);
            _newline();
            _writeTopic(reifier, false);
            topics.remove(reifier);
        }
        _writeSection("ONTOLOGY");
        Collection<Topic> types = tiIdx.getTopicTypes();
        _writeOntologySection(types, topics, "Topic Types");
        types = tiIdx.getAssociationTypes();
        _writeOntologySection(types, topics, "Association Types");
        types = tiIdx.getRoleTypes();
        _writeOntologySection(types, topics, "Role Types");
        types = tiIdx.getOccurrenceTypes();
        _writeOntologySection(types, topics, "Occurrence Types");
        types = tiIdx.getNameTypes();
        _writeOntologySection(types, topics, "Name Types");
        tiIdx.close();
        final ScopedIndex scopeIdx = topicMap.getIndex(ScopedIndex.class);
        if (!scopeIdx.isOpen()) {
            scopeIdx.open();
        }
        if (!scopeIdx.isAutoUpdated()) {
            scopeIdx.reindex();
        }
        _writeOntologySection(scopeIdx.getAssociationThemes(), topics, "Association Themes");
        _writeOntologySection(scopeIdx.getOccurrenceThemes(), topics, "Occurrence Themes");
        _writeOntologySection(scopeIdx.getNameThemes(), topics, "Name Themes");
        _writeOntologySection(scopeIdx.getVariantThemes(), topics, "Variant Themes");
        scopeIdx.close();
        _newline();
        _writeSection("INSTANCES");
        _writeSection("Topics");
        _writeTopics(topics);
        if (!assocs.isEmpty()) {
            Association[] assocArray = assocs.toArray(new Association[assocs.size()]);
            _writeSection("Associations");
            Arrays.sort(assocArray, super.getAssociationComparator());
            for (Association assoc: assocArray) {
                _writeAssociation(assoc);
            }
        }
        _newline();
        _out.write("# Thanks for using TMAPIX I/O -- http://www.tmapix.org/ :)");
        _newline();
        _out.flush();
        _topic2Reference.clear();
        _topic2Supertypes.clear();
    }

    private void _createSupertypeSubtypeRelationships(final TypeInstanceIndex tiIdx,
            TopicMap tm, Collection<Association> assocs) {
        final Topic supertypeSubtype = tm.getTopicBySubjectIdentifier(tm.createLocator(TMDM.SUPERTYPE_SUBTYPE));
        final Topic supertype = tm.getTopicBySubjectIdentifier(tm.createLocator(TMDM.SUPERTYPE));
        final Topic subtype = tm.getTopicBySubjectIdentifier(tm.createLocator(TMDM.SUBTYPE));
        if (supertypeSubtype == null || supertype == null || subtype == null) {
            return;
        }
        for (Association assoc: tiIdx.getAssociations(supertypeSubtype)) {
            if (!assoc.getScope().isEmpty()) {
                continue;
            }
            if (assoc.getReifier() != null) {
                continue;
            }
            Collection<Role> roles = assoc.getRoles();
            if (roles.size() != 2) {
                continue;
            }
            Topic supertypePlayer = null;
            Topic subtypePlayer = null;
            for (Role role: roles) {
                if (role.getType().equals(supertype)) {
                    supertypePlayer = role.getPlayer();
                }
                else if (role.getType().equals(subtype)) {
                    subtypePlayer = role.getPlayer();
                }
            }
            if (supertypePlayer == null || subtypePlayer == null) {
                continue;
            }
            Collection<Topic> supertypes = _topic2Supertypes.get(subtypePlayer);
            if (supertypes == null) {
                supertypes = new HashSet<Topic>();
                _topic2Supertypes.put(subtypePlayer, supertypes);
            }
            supertypes.add(supertypePlayer);
            assocs.remove(assoc);
        }
    }

    /**
     * Writes the header comment with the optional title, author, license etc.
     * information.
     *
     * @throws IOException In case of an error.
     */
    private void _writeFileHeader() throws IOException {
        _newline();
        _newline();
        _out.write("#(");
        _newline();
        if (_title != null) {
            _out.write("Title:   " + _title);
            _newline();
        }
        if (_author != null) {
            _out.write("Author:  " + _author);
            _newline();
        }
        if (_license != null) {
            _out.write("License: " + _license);
            _newline();
        }
        if (_comment != null) {
            _newline();
            _out.write(_comment);
            _newline();
        }
        _newline();
        _out.write("Generated by TMAPIX I/O -- <http://www.tmapix.org/>");
        _newline();
        _newline();
        _out.write(")#");
    }

    /**
     * Writes the registered prefixes.
     *
     * @throws IOException In case of an error.
     */
    private void _writePrefixes() throws IOException {
        if (_prefixes.isEmpty()) {
            return;
        }
        _writeSection("Prefixes");
        String[] keys = _prefixes.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        for (String ident: keys) {
            _out.write("%prefix " + ident + " <" + _prefixes.get(ident) + ">");
            _newline();
        }
    }

    /**
     * Writes the registered imports.
     *
     * @throws IOException In case of an error.
     */
    private void _writeImports() throws IOException {
        if (_imports.isEmpty()) {
            return;
        }
        _writeSection("Included Topic Maps");
        String[] imports = _imports.toArray(new String[_imports.size()]);
        Arrays.sort(imports);
        for (String imp: imports) {
            _out.write("%include ");
            _writeLocator(imp);
            _newline();
        }
    }

    /**
     * If <tt>topics</tt> is not empty, the topics will be removed from 
     * <tt>allTopics</tt> and written out under the specified section <tt>title</tt>. 
     *
     * @param topics The topics to serialize.
     * @param allTopics A collection of topics where the <tt>topics</tt> should be removed from.
     * @param title The title of the ontology section.
     * @throws IOException In case of an error.
     */
    private void _writeOntologySection(Collection<Topic> topics, Collection<Topic> allTopics, String title) throws IOException {
        if (topics.isEmpty()) {
            return;
        }
        allTopics.removeAll(topics);
        _writeSection(title);
        _writeTopics(topics);
    }

    /**
     * Sorts the specified collection of topics and serializes it.
     *
     * @param topics An unordered collection of topics.
     * @throws IOException In case of an error.
     */
    private void _writeTopics(Collection<Topic> topics) throws IOException {
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
    private void _writeTopic(Topic topic, boolean topicTypeHeader) throws IOException {
        final Reference mainIdentity = getTopicReference(topic);
        Topic[] types = _getTypes(topic);
        if (topicTypeHeader) {
            if (types.length > 0) {
                Reference ref = getTopicReference(types[0]);
                if (!ref.equals(_lastReference)) {
                    _writeSection("TT: " + ref);
                    _lastReference = ref;
                }
            }
            else if (_UNTYPED_REFERENCE != _lastReference) {
                _writeSection("TT: " + _UNTYPED_REFERENCE);
                _lastReference = _UNTYPED_REFERENCE;
            }
        }
        boolean wantSemicolon = false;
        _newline();
        _writeTopicRef(mainIdentity);
        _out.write(' ');
        for (Topic type: types) {
            _writeTypeInstance(type, wantSemicolon);
            wantSemicolon = true;
        }
        for (Topic supertype: _getSupertypes(topic)) {
            _writeSupertypeSubtype(supertype, wantSemicolon);
            wantSemicolon = true;
        }

        for (Name name: _getNames(topic)) {
            _writeName(name, wantSemicolon);
            wantSemicolon = true;
        }
        for (Occurrence occ: _getOccurrences(topic)) {
            _writeOccurrence(occ, wantSemicolon);
            wantSemicolon = true;
        }
        for (Reference sid: _getSubjectIdentifiers(topic)) {
            _writeTopicRef(sid, wantSemicolon);
            wantSemicolon = true;
        }
        for (Reference slo: _getSubjectLocators(topic)) {
            _writeTopicRef(slo, wantSemicolon);
            wantSemicolon = true;
        }
        if (_exportIIDs) {
            for (Reference iid: _getItemIdentifiers(topic)) {
                _writeTopicRef(iid, wantSemicolon);
                wantSemicolon = true;
            }
        }
        _out.write('.');
        _newline();
    }

    /**
     * Returns a sorted array of subject identifiers for the specified topic.
     * <p>
     * The main identity (the one which starts the topic block) is removed
     * is not part of the array iff the main identity is a subject identifier.
     * </p>
     *
     * @param topic The topic to retrieve the subject identifiers from.
     * @return A (maybe empty) sorted array of subject identifiers.
     */
    private Reference[] _getSubjectIdentifiers(Topic topic) {
        return _getLocators(topic, Reference.SID);
    }

    /**
     * Returns a sorted array of subject locators for the specified topic.
     * <p>
     * The main identity (the one which starts the topic block) is removed
     * is not part of the array iff the main identity is a subject locator.
     * </p>
     *
     * @param topic The topic to retrieve the subject locators from.
     * @return A (maybe empty) sorted array of subject locators.
     */
    private Reference[] _getSubjectLocators(Topic topic) {
        return _getLocators(topic, Reference.SLO);
    }

    /**
     * Returns a sorted array of item identifiers for the specified topic.
     * <p>
     * The main identity (the one which starts the topic block) is removed
     * is not part of the array iff the main identity is an item identifier.
     * </p>
     *
     * @param topic The topic to retrieve the item identifiers from.
     * @return A (maybe empty) sorted array of item identifiers.
     */
    private Reference[] _getItemIdentifiers(Topic topic) {
        Collection<Locator> iids = topic.getItemIdentifiers();
        if (iids.isEmpty()) {
            return _EMPTY_REFERENCE_ARRAY;
        }
        Collection<Reference> refs = new ArrayList<Reference>(iids.size());
        for (Locator iid: iids) {
            refs.add(Reference.createItemIdentifier(iid));
        }
        Reference mainIdentity = getTopicReference(topic);
        if (!refs.remove(mainIdentity)
                && mainIdentity.type == Reference.ID) {
            String iri = _baseIRI + "#" + mainIdentity.reference;
            for (Reference r: refs) {
                if (r.reference.equals(iri)) {
                    refs.remove(r);
                    break;
                }
            }
        }
        Reference[] refArray = refs.toArray(new Reference[refs.size()]);
        Arrays.sort(refArray);
        return refArray;
    }

    /**
     * Returns a sorted array of {@link Reference}s which represent the 
     * provided locators.
     * <p>
     * The main identity is not part of the array.
     * </p>
     *
     * @param topic The topic.
     * @param kind Either {@link Reference#SID} or {@link Reference#SLO}.
     * @return A (maybe empty) sorted array.
     */
    private Reference[] _getLocators(Topic topic, int kind) {
        Set<Locator> locs = kind == Reference.SID ? topic.getSubjectIdentifiers()
                                                  : topic.getSubjectLocators();
        if (locs.isEmpty()) {
            return _EMPTY_REFERENCE_ARRAY;
        }
        Collection<Reference> refs = new ArrayList<Reference>(locs.size());
        for (Locator loc: locs) {
            refs.add(new Reference(kind, loc));
        }
        refs.remove(getTopicReference(topic)); 
        Reference[] refArray = refs.toArray(new Reference[refs.size()]);
        Arrays.sort(refArray);
        return refArray;
    }

    /**
     * Returns a sorted array of types for the specified topic.
     *
     * @param topic The topic to retrieve the types from.
     * @return A sorted array of types.
     */
    private Topic[] _getTypes(Topic topic) {
        Set<Topic> types_ = topic.getTypes();
        Topic[] types = types_.toArray(new Topic[types_.size()]);
        Arrays.sort(types, _topicIdComparator);
        return types;
    }

    /**
     * Returns a sorted array of supertypes for the specified topic.
     *
     * @param topic The topic to retrieve the supertypes from.
     * @return A sorted array of supertypes.
     */
    private Topic[] _getSupertypes(Topic topic) {
        Collection<Topic> supertypes_ = _topic2Supertypes.get(topic);
        if (supertypes_ == null) {
            return _EMPTY_TOPIC_ARRAY;
        }
        Topic[] supertypes = supertypes_.toArray(new Topic[supertypes_.size()]);
        Arrays.sort(supertypes, _topicIdComparator);
        return supertypes;
    }

    /**
     * Returns a sorted array of names for the specified topic.
     *
     * @param topic The topic to retrieve the names from.
     * @return A sorted array of names.
     */
    private Name[] _getNames(Topic topic) {
        Collection<Name> names = topic.getNames();
        Name[] nameArray = names.toArray(new Name[names.size()]);
        Arrays.sort(nameArray, super.getNameComparator());
        return nameArray;
    }

    /**
     * Returns a sorted array of occurrences for the specified topic.
     *
     * @param topic The topic to retrieve the occurrences from.
     * @return A sorted array of occurrences.
     */
    private Occurrence[] _getOccurrences(Topic topic) {
        Collection<Occurrence> occs = topic.getOccurrences();
        Occurrence[] occArray = occs.toArray(new Occurrence[occs.size()]);
        Arrays.sort(occArray, super.getOccurrenceComparator());
        return occArray;
    }

    /**
     * Writes a type-instance relationship via "isa".
     *
     * @param type The type to write.
     * @param wantSemicolon Indicates if a semicolon should be written.
     * @throws IOException In case of an error.
     */
    private void _writeTypeInstance(Topic type, boolean wantSemicolon) throws IOException {
        _writeSemicolon(wantSemicolon);
        _out.write("isa ");
        _writeTopicRef(type);
    }

    /**
     * Writes a supertype-subtype relationship via "isa".
     *
     * @param supertype The supertype to write.
     * @param wantSemicolon Indicates if a semicolon should be written.
     * @throws IOException In case of an error.
     */
    private void _writeSupertypeSubtype(Topic supertype, boolean wantSemicolon) throws IOException {
        _writeSemicolon(wantSemicolon);
        _out.write("ako ");
        _writeTopicRef(supertype);
    }

    /**
     * Serializes the specified occurrence.
     *
     * @param occ The occurrence to serialize
     * @param wantSemicolon Indicates if a semicolon should be written.
     * @throws IOException In case of an error.
     */
    private void _writeOccurrence(final Occurrence occ, final boolean wantSemicolon)  throws IOException {
        _writeSemicolon(wantSemicolon);
        _writeTopicRef(occ.getType());
        _out.write(": ");
        _writeLiteral(occ);
        _writeScope(occ);
        _writeReifier(occ);
    }

    /**
     * Serializes the specified name.
     *
     * @param name The name to serialize.
     * @param wantSemicolon Indicates if a semicolon should be written.
     * @throws IOException In case of an error.
     */
    private void _writeName(final Name name, final boolean wantSemicolon)  throws IOException {
        _writeSemicolon(wantSemicolon);
        _out.write("- ");
        Topic type = name.getType();
        if (!type.equals(_defaultNameType)) {
            _writeTopicRef(type);
            _out.write(": ");
        }
        _writeString(name.getValue());
        _writeScope(name);
        _writeReifier(name);
        Variant[] variants = name.getVariants().toArray(new Variant[0]);
        Arrays.sort(variants, super.getVariantComparator());
        for (Variant variant: variants) {
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
        _out.write(" (");
        _writeLiteral(variant);
        _writeScope(variant);
        _writeReifier(variant);
        _out.write(')');
    }

    /**
     * Serializes the specified association.
     *
     * @param assoc The association to serialize.
     * @throws IOException In case of an error.
     */
    private void _writeAssociation(final Association assoc) throws IOException {
        _newline();
        _writeTopicRef(assoc.getType());
        _out.write('(');
        Role[] roles = assoc.getRoles().toArray(new Role[0]);
        Arrays.sort(roles, super.getRoleComparator());
        _writeRole(roles[0]);
        for (int i=1; i<roles.length; i++) {
            _out.write(", ");
            _writeRole(roles[i]);
        }
        _out.write(')');
        _writeScope(assoc);
        _writeReifier(assoc);
        _newline();
    }

    /**
     * Serializes the specified association role.
     *
     * @param role The association role to serialize.
     * @throws IOException In case of an error.
     */
    private void _writeRole(Role role) throws IOException {
        _writeTopicRef(role.getType());
        _out.write(": ");
        _writeTopicRef(role.getPlayer());
        _writeReifier(role);
    }

    /**
     * Writes a semicolon and a newline character iff <tt>wantSemicolon</tt> is
     * <tt>true</tt>.
     * <p>
     * If a semicolon is written, optional whitespaces are written to ident the
     * next statement.
     * </p>
     *
     * @param wantSemicolon Indicates if a semicolon should be written.
     * @throws IOException In case of an error.
     */
    private void _writeSemicolon(boolean wantSemicolon) throws IOException {
        if (wantSemicolon) {
            _out.write(';');
            _newline();
            super.indent();
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
        Set<Topic> scope = scoped.getScope(); 
        if (!scope.isEmpty()) {
            Topic[] themes = scope.toArray(new Topic[scope.size()]);
            Arrays.sort(themes, _topicIdComparator);
            _out.write(" @");
            _writeTopicRef(themes[0]);
            for (int i=1; i<themes.length; i++) {
                _out.write(", ");
                _writeTopicRef(themes[i]);
            }
        }
    }

    /**
     * Writes the reifier iff <tt>reifiable</tt> is reified.
     *
     * @param reifiable The reifiable construct.
     * @throws IOException If an error occurs.
     */
    private void _writeReifier(Reifiable reifiable) throws IOException {
        Topic reifier = reifiable.getReifier();
        if (reifier == null) {
            return;
        }
        _out.write(" ~ ");
        _writeTopicRef(reifier);
    }

    /**
     * Writes a literal.
     * 
     * If the datatype is xsd:anyURI or xsd:string, the datatype is omitted.
     * If the datatype is natively supported by CTM (like xsd:integer, xsd:decimal)
     * the quotes and the datatype are omitted. 
     *
     * @param lit The literal to serialize.
     * @throws IOException In case of an error.
     */
    private void _writeLiteral(final DatatypeAware dt) throws IOException {
        final String datatype = dt.getDatatype().toExternalForm();
        final String value = dt.getValue();
        if (XSD.ANY_URI.equals(datatype)) {
            _writeLocator(value);
        }
        else if (XSD.STRING.equals(datatype)) {
            _writeString(value);
        }
        else if (CTMUtils.isNativeDatatype(datatype)) {
            _out.write(value);
        }
        else {
            _writeString(value);
            _out.write("^^");
            _writeLocator(datatype); 
        }
    }

    /**
     * Writes a topic reference for the specified topic.
     *
     * @param topic The topic to serialize.
     * @throws IOException In case of an error.
     */
    private void _writeTopicRef(Topic topic) throws IOException {
        _writeTopicRef(getTopicReference(topic));
    }

    /**
     * Writes the specied topic reference without whitespaces in front.
     *
     * @param topicRef The topic reference to serialize.
     * @throws IOException In case of an error.
     */
    private void _writeTopicRef(Reference topicRef) throws IOException {
        _writeTopicRef(topicRef, false);
    }

    /**
     * Writes the specified topic reference.
     *
     * @param topicRef The topic reference to write.
     * @param wantSemicolon Indicates if a semicolon should be written.
     * @throws IOException In case of an error.
     */
    private void _writeTopicRef(Reference topicRef, boolean wantSemicolon) throws IOException {
        _writeSemicolon(wantSemicolon);
        switch (topicRef.type) {
            case Reference.ID:
                _out.write(topicRef.reference);
                return;
            case Reference.IID:
                _out.write('^');
                break;
            case Reference.SLO:
                _out.write("= ");
                break;
            case Reference.SID:
                break;
            default:
                throw new RuntimeException("Internal error: Cannot match topic reference type " + topicRef.type);
        }
        _writeLocator(topicRef.reference);
    }

    /**
     * Serializes the provided <tt>string</tt>.
     * <p>
     * This method recognizes characters which have to be escaped.
     * </p>
     *
     * @param string The string to write.
     * @throws IOException In case of an error.
     */
    private void _writeString(String string) throws IOException {
        CTMUtils.writeString(_out, string);
    }

    /**
     * Writes the specified locator <tt>reference</tt> which has been 
     * externalized..
     * <p>
     * If the reference starts with the base IRI followed by a hash ('#'), the
     * reference is abbreviated.
     * </p>
     *
     * @param reference The reference to write.
     * @throws IOException In case of an error.
     */
    private void _writeLocator(String reference) throws IOException {
        // If the reference starts with the base IRI and is followed by a #
        // a relative reference is written
        if (reference.startsWith(_baseIRI)) {
            String tmp = reference.substring(_baseIRI.length());
            if (tmp.charAt(0) == '#') {
                _out.write('<' + tmp + '>');
                return;
            }
        }
        // If no relative IRI was written, check the registered prefixes and
        // write a QName if a prefix matches
        for (Map.Entry<String, String> entry: _prefixes.entrySet()) {
            String iri = entry.getValue();
            if (reference.startsWith(iri)) {
                String localPart = reference.substring(iri.length());
                if (CTMUtils.isValidLocalPart(localPart)) {
                    _out.write(entry.getKey());
                    _out.write(':');
                    _out.write(localPart);
                    return;
                }
            }
        }
        // No relative IRI and no QName was written, write the reference as it is
        _out.write('<' + reference + '>');
    }

    /**
     * Returns a reference to the specified <tt>topic</tt>.
     *
     * @param topic The topic to generate a reference for.
     * @return A reference to the specified topic.
     */
    private Reference _generateTopicReference(final Topic topic) {
        Reference ref = null;
        Collection<Reference> refs = new ArrayList<Reference>();
        for (Locator iid: topic.getItemIdentifiers()) {
            String addr = iid.toExternalForm();
            int idx = addr.indexOf('#');
            if (idx > 0) {
                String id = addr.substring(idx+1);
                if (_isValidId(id) && !CTMUtils.isKeyword(id)) {
                    if (_keepAbsoluteIIDs && !addr.startsWith(_baseIRI)) {
                        refs.add(Reference.createItemIdentifier(iid));
                    }
                    else {
                        refs.add(Reference.createId(id));
                    }
                }
                else {
                    refs.add(Reference.createItemIdentifier(iid));
                }
            }
            else {
                refs.add(Reference.createItemIdentifier(iid));
            }
        }
        if (refs.isEmpty()) {
            for (Locator sid: topic.getSubjectIdentifiers()) {
                refs.add(Reference.createSubjectIdentifier(sid));
            }
        }
        if (refs.isEmpty()) {
            for (Locator sid: topic.getSubjectLocators()) {
                refs.add(Reference.createSubjectLocator(sid));
            }
        }
        if (!refs.isEmpty()) {
            Reference[] refArray = refs.toArray(new Reference[refs.size()]);
            Arrays.sort(refArray);
            ref = refArray[0];
        }
        else {
            ref = Reference.createItemIdentifier("#" + topic.getId());
        }
        return ref;
    }

    /**
     * Returns if the provided <tt>id</tt> is a valid CTM topic identifier.
     *
     * @param id The id to check.
     * @return <tt>true</tt> if the id is valid, otherwise <tt>false</tt>.
     */
    private static boolean _isValidId(String id) {
        return CTMUtils.isValidId(id);
    }

    /**
     * Writes a EOL character.
     *
     * @throws IOException In case of an error.
     */
    private void _newline() throws IOException {
        _out.write('\n');
    }

    /**
     * Writes a section name.
     *
     * @param name The section name to write.
     * @throws IOException In case of an error.
     */
    private void _writeSection(String name) throws IOException {
        _newline();
        _newline();
        _out.write("#-- " + name);
        _newline();
    }

    /**
     * Represents a reference to a topic.
     */
    private static final class Reference implements Comparable<Reference> {
        static final int 
            ID = 0,
            SID = 1,
            SLO = 2,
            IID = 3;
        final int type;
        final String reference;

        private Reference(int type, String reference) {
            this.type = type;
            this.reference = reference;
        }

        public Reference(int type, Locator loc) {
            this(type, loc.toExternalForm());
        }

        public static Reference createId(String reference) {
            return new Reference(ID, reference);
        }
        
        public static Reference createSubjectIdentifier(Locator reference) {
            return new Reference(SID, reference);
        }

        public static Reference createSubjectLocator(Locator reference) {
            return new Reference(SLO, reference);
        }

        public static Reference createItemIdentifier(String string) {
            return new Reference(IID, string);
        }

        public static Reference createItemIdentifier(Locator reference) {
            return createItemIdentifier(reference.toExternalForm());
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            StringBuilder buff = new StringBuilder();
            switch (type) {
                case Reference.ID:
                    return reference;
                case Reference.IID:
                    buff.append('^');
                    break;
                case Reference.SID:
                    break;
                case Reference.SLO:
                    buff.append("= ");
                    break;
            }
            buff.append('<');
            buff.append(reference);
            buff.append('>');
            return buff.toString();
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Reference)) {
                return false;
            }
            Reference other = (Reference) obj;
            return type == other.type && reference.equals(other.reference); 
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return type + reference.hashCode();
        }

        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(Reference o) {
            int res = type - o.type;
            if (res == 0) {
                res = reference.compareTo(o.reference);
            }
            return res;
        }

    }


    /*
     * Comparators.
     */

    private class TopicIdComparator implements Comparator<Topic> {

        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Topic o1, Topic o2) {
            return getTopicReference(o1).compareTo(getTopicReference(o2));
        }
    }

    /* (non-Javadoc)
     * @see org.tmapix.io.AbstractTextualTopicMapWriter#getDefaultNameType()
     */
    @Override
    protected Topic getDefaultNameType() {
        return _defaultNameType;
    }

    /* (non-Javadoc)
     * @see org.tmapix.io.AbstractTextualTopicMapWriter#getTopicReference(org.tmapi.core.Topic)
     */
    @Override
    protected Reference getTopicReference(Topic topic) {
        Reference ref = _topic2Reference.get(topic);
        if (ref == null) {
            ref = _generateTopicReference(topic);
            _topic2Reference.put(topic, ref);
        }
        return ref;
    }

}
