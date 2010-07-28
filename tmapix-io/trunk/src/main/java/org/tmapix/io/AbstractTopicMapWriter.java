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

import java.util.Iterator;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapix.voc.TMDM;

/**
 * Common, abstract superclass for {@link TopicMapWriter} implementations.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
abstract class AbstractTopicMapWriter implements TopicMapWriter {

    protected final String _baseIRI;
    private Topic _typeInstance;
    private Topic _type;
    private Topic _instance;
    private boolean _checkForTypeInstanceAssociations;

    protected AbstractTopicMapWriter(final String baseIRI) {
        if (baseIRI == null) {
            throw new IllegalArgumentException("The base IRI must not be null");
        }
        _baseIRI = baseIRI;
    }

    /**
     * Returns an identifier for the topic.
     * <p>
     * The algorithm tries to avoid to use the internal identifier which may
     * cause yet another item identifier. If the topic has an item identifier
     * which starts with the specified IRI provided in the constructor, the 
     * algorithm tries to use the fragment identifier.
     * </p>
     *
     * @param topic The topic to return an identifier for.
     * @return An identifier, never <tt>null</tt>.
     */
    protected String getId(final Topic topic) {
        String id = null;
        for (Locator loc: topic.getItemIdentifiers()) {
            String reference = loc.getReference();
            if (!reference.startsWith(_baseIRI)) {
                continue;
            }
            int fragIdx =  reference.indexOf('#');
            if (fragIdx < 0) {
                continue;
            }
            id = reference.substring(fragIdx+1);
            if (id.startsWith("id-")) {
                id = null;
            }
            if (id != null && isValidNCName(id)) {
                break;
            }
        }
        return id != null ? id : "id-" + topic.getId();
    }

    protected static boolean isValidNCName(final String ident) {
        return XMLChar.isValidNCName(ident);
    }

    protected void init(final TopicMap topicMap) {
        _typeInstance = topicMap.getTopicBySubjectIdentifier(topicMap.createLocator(TMDM.TYPE_INSTANCE));
        _type = topicMap.getTopicBySubjectIdentifier(topicMap.createLocator(TMDM.TYPE));
        _instance = topicMap.getTopicBySubjectIdentifier(topicMap.createLocator(TMDM.INSTANCE));
        _checkForTypeInstanceAssociations = _typeInstance != null && _type != null && _instance != null;
    }

    /**
     * Returns if the provided association represents a type-instance relationship.
     *
     * @param assoc The association.
     * @param roles The roles of the association.
     * @return {@code true} if the association represents a type-instance relationship,
     *          otherwise {@code false}.
     */
    protected final boolean isTypeInstanceAssociation(final Association assoc, 
            final Set<Role> roles) {
        if (!_checkForTypeInstanceAssociations 
                || !assoc.getType().equals(_typeInstance)
                || assoc.getReifier() != null
                || !assoc.getScope().isEmpty()
                || roles.size() != 2) {
            return false;
        }
        final Iterator<Role> roleIter = roles.iterator();
        final Role firstRole = roleIter.next();
        final Role secondRole = roleIter.next();
        if (firstRole.getType().equals(_type)) {
            return secondRole.getType().equals(_instance);
        }
        return secondRole.getType().equals(_instance) 
                    && firstRole.getType().equals(_type);
    }
}
