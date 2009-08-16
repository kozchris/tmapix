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

    protected abstract T createTopicByItemIdentifier(String iri) throws MIOException;

    protected abstract T createTopicBySubjectIdentifier(String iri) throws MIOException;

    protected abstract T createTopicBySubjectLocator(String iri) throws MIOException;

    protected abstract void handleTypeInstance(T instance, T type) throws MIOException;

    protected abstract void handleItemIdentifier(T topic, String iri) throws MIOException;

    protected abstract void handleSubjectIdentifier(T topic, String iri) throws MIOException;

    protected abstract void handleSubjectLocator(T topic, String iri) throws MIOException;

    protected abstract void handleTopicMapItemIdentifier(String iri) throws MIOException;

    protected abstract void handleTopicMapReifier(T reifier) throws MIOException;

    protected abstract void createOccurrence(T parent, 
            T type, String value, String datatype, 
            Collection<T> scope, T reifier, Collection<String> iids) throws MIOException;

    protected abstract void createName(T parent, 
            T type, String value, 
            Collection<T> scope, T reifier, Collection<String> iids, 
            Collection<IVariant<T>> variants) throws MIOException;

    protected abstract void createAssociation(T type, Collection<T> scope, T reifier, 
            Collection<String> iids, Collection<IRole<T>> roles)  throws MIOException;


    public interface IVariant<T> {
        public Iterable<String> getItemIdentifiers();
        public String getValue();
        public String getDatatype();
        public Collection<T> getScope();
        public T getReifier();
    }

    public interface IRole<T> {
        public Iterable<String> getItemIdentifiers();
        public T getType();
        public T getPlayer();
        public T getReifier();
    }

}
