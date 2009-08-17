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
package com.semagia.tmapix.io;

import java.util.Collection;

import com.semagia.mio.MIOException;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
abstract class HamsterHandler<T> {

    /**
     * Returns either an existing topic with the specified item identifier
     * or creates a topic with the specified item identifier.
     * 
     * @param iri An absolute IRI representing an item identifier.
     * @return A topic with the item identifier <tt>iri</tt>.
     * @throws MIOException In case of an error.
     */
    protected abstract T createTopicByItemIdentifier(String iri)
            throws MIOException;

    /**
     * Returns either an existing topic with the specified subject identifier
     * or creates a topic with the specified subject identifier.
     * 
     * @param iri An absolute IRI representing a subject identifier.
     * @return A topic with the subject identifier <tt>iri</tt>.
     * @throws MIOException In case of an error.
     */
    protected abstract T createTopicBySubjectIdentifier(String iri)
            throws MIOException;

    /**
     * Returns either an existing topic with the specified subject locator
     * or creates a topic with the specified subject locator.
     * 
     * @param iri An absolute IRI representing a subject locator.
     * @return A topic with the subject identifier <tt>iri</tt>.
     * @throws MIOException In case of an error.
     */
    protected abstract T createTopicBySubjectLocator(String iri)
            throws MIOException;

    /**
     * Creates a tmdm:type-instance relationship between <tt>instance</tt> and
     * <tt>type</tt>.
     * 
     * @param instance The topic that should play the tmdm:instance role.
     * @param type The topic that should play the tmdm:type role.
     * @throws MIOException In case of an error.
     */
    protected abstract void handleTypeInstance(T instance, T type)
            throws MIOException;

    /**
     * 
     * 
     * @param topic
     * @param iri
     * @throws MIOException In case of an error.
     */
    protected abstract void handleItemIdentifier(T topic, String iri)
            throws MIOException;

    /**
     * 
     * 
     * @param topic
     * @param iri
     * @throws MIOException In case of an error.
     */
    protected abstract void handleSubjectIdentifier(T topic, String iri)
            throws MIOException;

    /**
     * 
     * 
     * @param topic
     * @param iri
     * @throws MIOException In case of an error.
     */
    protected abstract void handleSubjectLocator(T topic, String iri)
            throws MIOException;

    /**
     * 
     * 
     * @param iri
     * @throws MIOException In case of an error.
     */
    protected abstract void handleTopicMapItemIdentifier(String iri)
            throws MIOException;

    /**
     * 
     * 
     * @param reifier
     * @throws MIOException In case of an error.
     */
    protected abstract void handleTopicMapReifier(T reifier)
            throws MIOException;

    /**
     * 
     * 
     * @param parent
     * @param type
     * @param value
     * @param datatype
     * @param scope
     * @param reifier
     * @param iids
     * @throws MIOException In case of an error.
     */
    protected abstract void createOccurrence(T parent, T type, String value,
            String datatype, Collection<T> scope, T reifier,
            Collection<String> iids) throws MIOException;

    /**
     * 
     * 
     * @param parent
     * @param type
     * @param value
     * @param scope
     * @param reifier
     * @param iids
     * @param variants
     * @throws MIOException In case of an error.
     */
    protected abstract void createName(T parent, T type, String value,
            Collection<T> scope, T reifier, Collection<String> iids,
            Collection<IVariant<T>> variants) throws MIOException;

    /**
     * 
     * 
     * @param type
     * @param scope
     * @param reifier
     * @param iids
     * @param roles
     * @throws MIOException In case of an error.
     */
    protected abstract void createAssociation(T type, Collection<T> scope,
            T reifier, Collection<String> iids, Collection<IRole<T>> roles)
            throws MIOException;

    /**
     * 
     * 
     */
    public interface IVariant<T> {

        public Iterable<String> getItemIdentifiers();

        public String getValue();

        public String getDatatype();

        public Collection<T> getScope();

        public T getReifier();
    }

    /**
     * 
     * 
     */
    public interface IRole<T> {

        public Iterable<String> getItemIdentifiers();

        public T getType();

        public T getPlayer();

        public T getReifier();
    }

}
