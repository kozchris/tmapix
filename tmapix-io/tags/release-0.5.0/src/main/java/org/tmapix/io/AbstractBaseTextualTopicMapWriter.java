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
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.Variant;

import org.tmapix.voc.TMDM;

/**
 * Base class for textual topic map writers which provides some common useful
 * utility methods.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
abstract class AbstractBaseTextualTopicMapWriter extends
        AbstractTextualTopicMapWriter {

    protected static final Topic[] _EMPTY_TOPIC_ARRAY = new Topic[0];
    protected static final String UNTYPED = "[untyped]";
    protected final String _encoding;
    protected final String _baseIRI;
    protected Topic _defaultNameType;
    private final Comparator<Locator> _locatorComparator;
    private Topic _typeInstance;
    private Topic _type;
    private Topic _instance;

    /**
     * 
     *
     * @param stream The stream to write upon, never {@code null}.
     * @param baseIRI The document IRI, not {@code null}.
     * @throws IOException In case of an error.
     */
    protected AbstractBaseTextualTopicMapWriter(final OutputStream stream,
            final String baseIRI) throws IOException {
        this(stream, baseIRI, "utf-8");
    }

    /**
     * 
     *
     * @param stream The stream to write upon, never.
     * @param baseIRI The document IRI.
     * @param encoding The encoding to use.
     * @throws IOException In case of an error.
     */
    protected AbstractBaseTextualTopicMapWriter(final OutputStream stream,
            final String baseIRI, final String encoding) throws IOException {
        this(new OutputStreamWriter(stream, encoding), baseIRI, encoding);
    }

    /**
     * 
     *
     * @param out The writer to use to serialize the stream.
     * @param baseIRI The document IRI.
     * @param encoding The encoding to use.
     */
    protected AbstractBaseTextualTopicMapWriter(final Writer out,
            final String baseIRI, final String encoding) {
        super(out);
        if (encoding == null) {
            throw new IllegalArgumentException("The encoding must not be null");
        }
        _encoding = encoding;
        if (baseIRI == null) {
            throw new IllegalArgumentException("The base IRI must not be null");
        }
        _baseIRI = baseIRI;
        _locatorComparator = new LocatorComparator();
    }

    /**
     * This method must be called to initialize this class.
     * 
     * Commonly, this method is invoked in the {@link #write(TopicMap)} method.
     *
     * @param topicMap A topic map.
     */
    protected void init(final TopicMap topicMap) {
        _defaultNameType = topicMap.getTopicBySubjectIdentifier(topicMap.createLocator(TMDM.TOPIC_NAME));
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

    /**
     * Returns a sorted array of types for the specified topic.
     *
     * @param topic The topic to retrieve the types from.
     * @return A sorted array of types.
     */
    protected Topic[] getTypes(final Topic topic) {
        final Collection<Topic> types_ = topic.getTypes();
        if (types_.isEmpty()) {
            return _EMPTY_TOPIC_ARRAY;
        }
        final Topic[] types = types_.toArray(new Topic[types_.size()]);
        Arrays.sort(types, getTopicIdComparator());
        return types;
    }

    /**
     * Returns a sorted array of themes for the specified scoped construct.
     *
     * @param scoped The scoped construct to retrieve the scope from.
     * @return A sorted array of themes.
     */
    protected Topic[] getThemes(final Scoped scoped) {
        final Collection<Topic> scope = scoped.getScope();
        if (scope.isEmpty()) {
            return _EMPTY_TOPIC_ARRAY;
        }
        final Topic[] themes = scope.toArray(new Topic[scope.size()]);
        Arrays.sort(themes, getTopicIdComparator());
        return themes;
    }

    /**
     * Returns a sorted array of occurrences for the specified topic.
     *
     * @param topic The topic to retrieve the occurrences from.
     * @return A sorted array of occurrences.
     */
    protected Occurrence[] getOccurrences(final Topic topic) {
        final Collection<Occurrence> occs = topic.getOccurrences();
        final Occurrence[] occArray = occs.toArray(new Occurrence[occs.size()]);
        Arrays.sort(occArray, getOccurrenceComparator());
        return occArray;
    }

    /**
     * Returns a sorted array of names for the specified topic.
     *
     * @param topic The topic to retrieve the names from.
     * @return A sorted array of names.
     */
    protected Name[] getNames(final Topic topic) {
        final Collection<Name> names = topic.getNames();
        final Name[] nameArray = names.toArray(new Name[names.size()]);
        Arrays.sort(nameArray, getNameComparator());
        return nameArray;
    }

    /**
     * Returns a sorted array of roles for the specified association.
     *
     * @param assoc The association to retrieve the roles from.
     * @return A sorted array of roles.
     */
    protected Role[] getRoles(final Association assoc) {
        final Collection<Role> roles = assoc.getRoles();
        final Role[] roleArray = roles.toArray(new Role[roles.size()]);
        Arrays.sort(roleArray, getRoleComparator());
        return roleArray;
    }

    /**
     * Returns a sorted array of variants for the specified name.
     *
     * @param name The name to retrieve the variants from.
     * @return A sorted array of variants.
     */
    protected Variant[] getVariants(final Name name) {
        final Collection<Variant> variants = name.getVariants();
        final Variant[] vars = variants.toArray(new Variant[variants.size()]);
        Arrays.sort(vars, getVariantComparator());
        return vars;
    }

    /**
     * Returns a sorted array of subject identifiers for the specified topic.
     *
     * @param topic The topic to retrieve the subject identifiers from.
     * @return A sorted array of subject identifiers.
     */
    protected Locator[] getSubjectIdentifiers(final Topic topic) {
        final Locator[] sids = topic.getSubjectIdentifiers().toArray(new Locator[0]);
        Arrays.sort(sids, _locatorComparator);
        return sids;
    }

    /**
     * Returns a sorted array of subject locators for the specified topic.
     *
     * @param topic The topic to retrieve the subject locators from.
     * @return A sorted array of subject locators.
     */
    protected Locator[] getSubjectLocators(final Topic topic) {
        final Locator[] slos = topic.getSubjectLocators().toArray(new Locator[0]);
        Arrays.sort(slos, _locatorComparator);
        return slos;
    }

    /**
     * Returns a sorted array of item identifiers for the specified topic.
     *
     * @param topic The topic to retrieve the item identifiers from.
     * @return A sorted array of item identifiers.
     */
    protected Locator[] getItemIdentifiers(final Topic topic) {
        final Locator[] iids = topic.getItemIdentifiers().toArray(new Locator[0]);
        Arrays.sort(iids, _locatorComparator);
        return iids;
    }

    /* (non-Javadoc)
     * @see org.tmapix.io.AbstractTextualTopicMapWriter#getDefaultNameType()
     */
    @Override
    protected Topic getDefaultNameType() {
        return _defaultNameType;
    }

    /**
     * Comparator for locators.
     */
    private static class LocatorComparator implements Comparator<Locator> {
        @Override
        public int compare(Locator o1, Locator o2) {
            return o1.getReference().compareTo(o2.getReference());
        }
    }

}
