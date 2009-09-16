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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import org.tmapi.core.Typed;
import org.tmapi.core.Variant;

import org.tmapix.voc.Namespace;
import org.tmapix.voc.TMDM;
import org.tmapix.voc.XSD;

/**
 * {@link TopicMapFragmentWriter} implementation that is able to serialize a 
 * topic map into the 
 * <a href="http://www.ontopia.net/topicmaps/tmxml.html">TM/XML</a>
 * format.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class TMXMLTopicMapWriter extends AbstractXMLTopicMapWriter implements TopicMapFragmentWriter {

    private static final String _PREFIX_TMDM = "iso";
    private static final String _PREFIX_TMXML = "tm";
    private static final String _UNTYPED_TOPIC = _PREFIX_TMXML + ":topic";
    private static final String _DEFAULT_NAME_TYPE = _PREFIX_TMDM + ":topic-name";
    private static final String _TYPE_INSTANCE = _PREFIX_TMDM + ":type-instance";
    private static final String _TYPE = _PREFIX_TMDM + ":type";
    private static final String _INSTANCE = _PREFIX_TMDM + ":instance";
    private static final String
        _EL_VALUE = _PREFIX_TMXML + ":value",
        _EL_SID = _PREFIX_TMXML + ":identifier",
        _EL_SLO = _PREFIX_TMXML + ":locator",
        _EL_VARIANT = _PREFIX_TMXML + ":variant"
        ;

    private String _rootElement = "topicmap";
    private Locator _defaultNameTypeLocator;
    private final Map<String, String> _prefix2IRI;
    private final Map<String, String> _iri2Prefix;
    private final Map<Topic, String> _topic2Reference;
    private final Set<String> _exportedAssocIds;
    private int _prefixCounter = 0;

    /**
     * Creates a TM/XML writer using "utf-8" encoding.
     *
     * @param out The stream the TM/XML is written onto.
     * @param baseIRI The base IRI which is used to resolve IRIs against.
     * @throws IOException If an error occurs.
     */
    public TMXMLTopicMapWriter(final OutputStream out, final String baseIRI)
            throws IOException {
        this(out, baseIRI, "utf-8");
    }

    /**
     * Creates a TM/XML writer.
     *
     * @param out The stream the TM/XML is written onto.
     * @param baseIRI The base IRI which is used to resolve IRIs against.
     * @param encoding The encoding to use.
     * @throws IOException If an error occurs.
     */
    public TMXMLTopicMapWriter(final OutputStream out, final String baseIRI,
            final String encoding) throws IOException {
        super(out, baseIRI, encoding);
        _prefix2IRI = new HashMap<String, String>();
        _iri2Prefix = new HashMap<String, String>();
        _topic2Reference = new HashMap<Topic, String>();
        _exportedAssocIds = new HashSet<String>();
        registerPrefix(_PREFIX_TMXML, Namespace.TMXML);
        registerPrefix(_PREFIX_TMDM, Namespace.TMDM_MODEL);
    }

    /* (non-Javadoc)
     * @see org.tmapix.io.TopicMapWriter#write(org.tmapi.core.TopicMap)
     */
    @Override
    public void write(final TopicMap topicMap) throws IOException {
        _write(topicMap.getTopics(), topicMap);
    }

    /* (non-Javadoc)
     * @see org.tmapix.io.TopicMapFragmentWriter#write(org.tmapi.core.Topic[])
     */
    @Override
    public void write(final Topic... topics) throws IOException {
        if (topics == null) {
            throw new IllegalArgumentException("The topic array must not be null");
        }
        _write(Arrays.asList(topics), null);
    }

    /* (non-Javadoc)
     * @see org.tmapix.io.TopicMapFragmentWriter#write(java.lang.Iterable)
     */
    @Override
    public void write(final Iterable<Topic> topics) throws IOException {
        if (topics == null) {
            throw new IllegalArgumentException("The topics must not be null");
        }
        _write(topics, null);
    }

    /**
     * Sets the root element, by default it is <tt>topicmap</tt>.
     *
     * @param root The name of the root element.
     */
    public void setRootElement(final String root) {
        if (root == null) {
            throw new IllegalArgumentException("The root element must not be null");
        }
        if (root.length() == 0) {
            throw new IllegalArgumentException("The root element must not be an empty string");
        }
        _rootElement = root;
    }

    /**
     * Returns the root element.
     *
     * @return The name of the root element.
     */
    public String getRootElement() {
        return _rootElement;
    }

    /**
     * Registeres a prefix / IRI tuple (XML namespace).
     * 
     * This method can be used if to avoid the generation of prefixes.
     *
     * @param prefix The prefix.
     * @param reference An IRI reference.
     */
    public void registerPrefix(final String prefix, final String reference) {
        if (prefix == null) {
            throw new IllegalArgumentException("The prefix must not be null");
        }
        if (prefix.length() == 0) {
            throw new IllegalArgumentException("The prefix must not be empty");
        }
        if (reference == null) {
            throw new IllegalArgumentException("The reference must not be null");
        }
        if (reference.length() == 0) {
            throw new IllegalArgumentException("The reference must not be empty");
        }
        _prefix2IRI.put(prefix, reference);
        _iri2Prefix.put(reference, prefix);
    }

    /**
     * Main entry point for writing TM/XML.
     * 
     * This method generates the prefixes and writes TM/XML.
     *
     * @param topics The topics to write.
     * @param topicMap The topic map to which all topics belong to or <tt>null</tt>.
     * @throws IOException In case of an error.
     */
    private void _write(final Iterable<Topic> topics, final TopicMap topicMap) throws IOException {
        _registerPrefixes(topics);
        final Iterator<Topic> iter = topics.iterator();
        if (iter.hasNext()) {
            final Topic topic = iter.next();
            final TopicMap tm = topic.getTopicMap();
            // Assuming that the locator can be used for all topics even if
            // they originate from different topic maps.
            _defaultNameTypeLocator = tm.createLocator(TMDM.TOPIC_NAME);
            _startTopicMap(topicMap);
            _writeTopic(topic);
            while (iter.hasNext()) {
                _writeTopic(iter.next());
            }
            _endTopicMap();
        }
        else {
            _out.emptyElement(_rootElement);
        }
        _prefix2IRI.clear();
        _iri2Prefix.clear();
        _topic2Reference.clear();
        _exportedAssocIds.clear();
    }

    /**
     * Generates the prefixes for the specified topics (incl. their played 
     * roles and occurrences and names).
     *
     * @param topics The topics to generate the prefixes for.
     */
    private void _registerPrefixes(final Iterable<Topic> topics) {
        for (Topic topic: topics) {
            _registerPrefixes(topic);
        }
    }

    /**
     * Generates the prefixes for the specified topic (incl. its played 
     * roles and occurrences and names).
     *
     * @param topic The topic to generate the prefixes for.
     */
    private void _registerPrefixes(final Topic topic) {
        _generateTopicReferences(topic.getTypes());
        for (Occurrence occ: topic.getOccurrences()) {
            _generatePrefixes(occ);
        }
        for (Name name: topic.getNames()) {
            _generatePrefixes(name);
            for (Variant variant: name.getVariants()) {
                _generatePrefixes(variant);
            }
        }
        for (Role rolePlayed: topic.getRolesPlayed()) {
            Association assoc = rolePlayed.getParent();
            _generatePrefixes(assoc);
            for (Role role: assoc.getRoles()) {
                _generatePrefixes(role);
            }
        }
    }

    /**
     * Generates the prefixes for the specified construct.
     *
     * @param reifiable The construct to generate the prefixes for.
     */
    private void _generatePrefixes(final Reifiable reifiable) {
        if (reifiable instanceof Typed) {
            _getTopicReference(((Typed) reifiable).getType());
        }
        if (reifiable instanceof Scoped) {
            _generateTopicReferences(((Scoped) reifiable).getScope());
        }
        if (reifiable.getReifier() != null) {
            _getTopicReference(reifiable.getReifier());
        }
    }

    /**
     * Writes the namespaces and the start tag.
     *
     * @param topicMap A topic map or <tt>null</tt> if one of the fragment writer
     *                  methods was used.
     * @throws IOException In case of an error.
     */
    private void _startTopicMap(final Reifiable topicMap) throws IOException {
        final String[] keys = _prefix2IRI.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        for (String key: keys) {
            super.addAttribute("xmlns:" + key, _prefix2IRI.get(key));
        }
        if (topicMap != null) {
            _addReifier(topicMap);
        }
        _out.startDocument();
        _out.startElement(_rootElement, _attrs);
    }

    /**
     * Writes the end tag.
     *
     * @throws IOException In case of an error.
     */
    private void _endTopicMap() throws IOException {
        _out.endElement(_rootElement);
        _out.endDocument();
    }

    /**
     * Writes the specified topic.
     *
     * @param topic The topic to serialize.
     * @throws IOException In case of an error.
     */
    private void _writeTopic(final Topic topic) throws IOException {
        final String[] types = _getTypes(topic);
        final String[] sids = _getSubjectIdentifiers(topic);
        final String[] slos = _getSubjectLocators(topic);
        if (topic.getSubjectIdentifiers().contains(_defaultNameTypeLocator)
                && sids.length == 1
                && slos.length == 0
                && topic.getRolesPlayed().isEmpty()
                && topic.getOccurrences().isEmpty()
                && topic.getNames().isEmpty()
                && types.length == 0) {
            return;
        }
        final String element = types.length > 0 ? types[0] : _UNTYPED_TOPIC;
        // Only add id iff no sids and slos are available
        if (sids.length == 0 && slos.length == 0) {
            _attrs.clear();
            super.addAttribute("id", super.getId(topic));
            _out.startElement(element, _attrs);
        }
        else {
            _out.startElement(element);
        }
        for (String sid: sids) {
            _out.dataElement(_EL_SID, sid);
        }
        for (String slo: slos) {
            _out.dataElement(_EL_SLO, slo);
        }
        for (Name name: topic.getNames()) {
            _writeName(name);
        }
        for (Occurrence occ: topic.getOccurrences()) {
            _writeOccurrence(occ);
        }
        // Type-instance relationships
        for (int i=1; i<types.length; i++) {
            _attrs.clear();
            super.addAttribute("role", _INSTANCE);
            super.addAttribute("otherrole", _TYPE);
            super.addAttribute("topicref", types[i]);
            _out.emptyElement(_TYPE_INSTANCE, _attrs);
        }
        for (Role playedRole: topic.getRolesPlayed()) {
            final Association assoc = playedRole.getParent();
            if (_exportedAssocIds.contains(assoc.getId())) {
                continue;
            }
            _exportedAssocIds.add(assoc.getId());
            _addScopeAndReifier(assoc);
            _addToAttributes("role", playedRole.getType());
            final String assocElement = _type(assoc);
            final int arity = assoc.getRoles().size();
            if (arity == 1) {
                _out.emptyElement(assocElement, _attrs);
            }
            else if (arity == 2) {
                Role otherRole = null;
                for (Role role: assoc.getRoles()) {
                    if (!role.equals(playedRole)) {
                        otherRole = role;
                        break;
                    }
                }
                _addToAttributes("topicref", otherRole.getType());
                _addToAttributes("otherrole", otherRole.getPlayer());
                _out.emptyElement(assocElement, _attrs);
            }
            else {
                // n-ary association
                _out.startElement(assocElement, _attrs);
                for (Role role: assoc.getRoles()) {
                    if (role.equals(playedRole)) {
                        continue;
                    }
                    _attrs.clear();
                    _addToAttributes("topicref", role.getPlayer());
                    _out.emptyElement(_type(role), _attrs);
                }
                
                _out.endElement(assocElement);
            }
        }
        _out.endElement(element);
    }

    /**
     * Generates topic references to the specified topics. The played roles and
     * occurrences / names are ignored.
     *
     * @param topics The topics to generate prefixes for.
     */
    private void _generateTopicReferences(final Iterable<Topic> topics) {
        for (Topic topic: topics) {
            _getTopicReference(topic);
        }
    }

    /**
     * Generates prefixes for the specified topic. The played roles and
     * occurrences / names are ignored.
     *
     * @param topic The topic generate a reference to.
     * @return The generated reference.
     */
    private String _generateReference(final Topic topic) {
        final String[] sids = _getSubjectIdentifiers(topic);
        if (sids.length > 0) {
            return _getSubjectIdentifierReference(sids[0]);
        }
        return super.getId(topic);
    }

    /**
     * Returns a reference to the specified topic.
     *
     * @param topic The topic get the reference to.
     * @return The reference (either a QName or an id)
     */
    private String _getTopicReference(final Topic topic) {
        String ref = _topic2Reference.get(topic);
        if (ref == null) {
            ref =_generateReference(topic);
            _topic2Reference.put(topic, ref);
        }
        return ref;
    }

    /**
     * Writes the specified occurrence.
     *
     * @param occ The occurrence to serialize.
     * @throws IOException In case of an error.
     */
    private void _writeOccurrence(final Occurrence occ) throws IOException {
        _attrs.clear();
        _addScopeAndReifier(occ);
        _addDatatype(occ);
        _out.dataElement(_type(occ), _attrs, occ.getValue());
    }

    /**
     * Writes the specified name.
     *
     * @param name The name to serialize.
     * @throws IOException In case of an error.
     */
    private void _writeName(final Name name) throws IOException {
        _attrs.clear();
        _addScopeAndReifier(name);
        final String element = _nameType(name);
        _out.startElement(element, _attrs);
        _out.dataElement(_EL_VALUE, name.getValue());
        for (Variant variant: name.getVariants()) {
            _writeVariant(variant);
        }
        _out.endElement(element);
    }

    /**
     * Writes the specified variant.
     *
     * @param variant The variant to serialize.
     * @throws IOException In case of an error.
     */
    private void _writeVariant(final Variant variant) throws IOException {
        _attrs.clear();
        _addScopeAndReifier(variant);
        _addDatatype(variant);
        _out.dataElement(_EL_VARIANT, _attrs, variant.getValue());
    }

    /**
     * Returns topic references to the types of the topic.
     *
     * @param topic The topic.
     * @return A (maybe empty) sorted array of topic references.
     */
    private String[] _getTypes(final Topic topic) {
        final Topic[] types = topic.getTypes().toArray(new Topic[0]);
        final String[] typeArray = new String[types.length];
        for (int i=0; i<types.length; i++) {
            typeArray[0] = _getTopicReference(types[i]);
        }
        Arrays.sort(typeArray);
        return typeArray;
    }

    /**
     * Returns the subject identifiers of the topic.
     *
     * @param topic The topic.
     * @return A (maybe empty) sorted array of IRIs.
     */
    private String[] _getSubjectIdentifiers(final Topic topic) {
        final Locator[] sids = topic.getSubjectIdentifiers().toArray(new Locator[0]);
        final String[] sidArray = new String[sids.length];
        for (int i=0; i<sids.length; i++) {
            sidArray[i] = sids[i].toExternalForm();
        }
        Arrays.sort(sidArray);
        return sidArray;
    }

    /**
     * Returns a QName based on the specified subject identifier.
     *
     * @param sid The subject identifier.
     * @return A QName.
     */
    private String _getSubjectIdentifierReference(final String sid) {
        final int length = sid.length();
        final char lastChar = sid.charAt(length-1);
        final int slash = sid.substring(0, lastChar == '/' ? length-2 : length-1).lastIndexOf('/');
        final int hash = lastChar == '#' ? -1 : sid.lastIndexOf('#');
        final int pos = Math.max(slash, hash);
        final String localname = sid.substring(pos + 1);
        final String ref = sid.substring(0, pos + 1);
        String prefix = _iri2Prefix.get(ref);
        if (prefix == null) {
            final int lastSlash = ref.lastIndexOf('/');
            final int previousSlash = ref.lastIndexOf('/', lastSlash > 0 ? lastSlash-1 : lastSlash);
            if (lastSlash > -1 && previousSlash > -1) {
                prefix = ref.substring(previousSlash+1, lastSlash);
                if (super.isValidNCName(prefix) && !_prefix2IRI.containsKey(prefix)) {
                    registerPrefix(prefix, ref);
                }
                else {
                    prefix = null;
                }
            }
        }
        if (prefix == null) {
            prefix = "ns-" + _prefixCounter;
            _prefixCounter++;
            while (_prefix2IRI.containsKey(prefix)) {
                prefix = "ns-" + _prefixCounter;
                _prefixCounter++;
            }
            registerPrefix(prefix, ref);
        }
        return prefix + ":" + localname;
    }

    /**
     * Retuns the subject locators of the topic.
     *
     * @param topic The topic.
     * @return A (maybe empty) sorted array of subject locators.
     */
    private String[] _getSubjectLocators(final Topic topic) {
        final Locator[] slos = topic.getSubjectLocators().toArray(new Locator[0]);
        final String[] sloArray = new String[slos.length];
        for (int i=0; i<slos.length; i++) {
            sloArray[i] = slos[i].toExternalForm();
        }
        Arrays.sort(sloArray);
        return sloArray;
    }

    /**
     * Returns a reference to the name type.
     *
     * @param name The name.
     * @return A reference to the name type.
     */
    private String _nameType(final Name name) {
        if (name.getType().getSubjectIdentifiers().contains(_defaultNameTypeLocator)) {
            return _DEFAULT_NAME_TYPE;
        }
        return _getTopicReference(name.getType());
    }

    /**
     * Returns a reference to the type of the typed construct.
     *
     * @param typed The typed construct.
     * @return A reference to the type of the construct.
     */
    private String _type(final Typed typed) {
        return _getTopicReference(typed.getType());
    }

    /**
     * Adds the scope and reifier to the attributes.
     *
     * @param tmc The construct.
     */
    private void _addScopeAndReifier(final Scoped tmc) {
        _addScope(tmc);
        _addReifier((Reifiable)tmc);
    }

    /**
     * Adds the scope of the scoped construct to the attributes.
     *
     * @param scoped The scoped construct.
     */
    private void _addScope(final Scoped scoped) {
        final Topic[] themes = scoped.getScope().toArray(new Topic[0]);
        if (themes.length == 0) {
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(_getTopicReference(themes[0]));
        for (int i=1; i<themes.length; i++) {
            sb.append(' ');
            sb.append(_getTopicReference(themes[i]));
        }
        super.addAttribute("scope", sb.toString());
    }

    /**
     * Adds the reifier (if any) to the attributes.
     *
     * @param reifiable The reifiable construct.
     */
    private void _addReifier(final Reifiable reifiable) {
        if (reifiable.getReifier() != null) {
            _addToAttributes("reifier", reifiable.getReifier());
        }
    }

    /**
     * Adds the datatype (if necessary) to the attributes.
     *
     * @param datatyped The datatyped construct.
     */
    private void _addDatatype(final DatatypeAware datatyped) {
        final String datatype = datatyped.getDatatype().toExternalForm();
        if (!XSD.STRING.equals(datatype)) {
            super.addAttribute("datatype", datatype);
        }
    }

    /**
     * Adds a reference to the specified topic to the attributes.
     *
     * @param name The name of the attribute.
     * @param topic The topic.
     */
    private void _addToAttributes(final String name, final Topic topic) {
        super.addAttribute(name, _getTopicReference(topic));
    }

}
