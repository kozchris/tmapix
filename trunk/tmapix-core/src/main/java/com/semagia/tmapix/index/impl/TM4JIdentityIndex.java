/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 * 
 * The Original Code is Semagia TMAPIX.
 * 
 * The Initial Developer of the Original Code is Semagia http://semagia.com/. 
 * All Rights Reserved.
 * 
 * Contributer(s): Lars Heuer <heuer[at]semagia.com>
 *
 */
package com.semagia.tmapix.index.impl;

import org.tm4j.net.LocatorFactory;
import org.tm4j.tmapi.core.TMAPILocatorImpl;
import org.tm4j.tmapi.core.TMAPITopicMapImpl;
import org.tm4j.tmapi.helpers.Wrapper;
import org.tm4j.topicmap.TopicMap;

import org.tmapi.core.Locator;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMapObject;

import com.semagia.tmapix.TMAPIXRuntimeException;
import com.semagia.tmapix.index.IIdentityIndex;
import com.semagia.tmapix.internal.ITopicMapAwareHelperObject;

/**
 * {@link IIdentityIndex} implementation that acts on a TM4J 
 * {@link org.tm4j.topicmap.TopicMap} instance.
 * 
 * <p>
 * This index is always kept in sync with the topic map.
 * This implementation only works with a TM4J TMAPI implementation and should
 * be faster and use less memory than an index that works on top of TMAPI.
 * </p>
 * 
 * <p>
 * Note: Do not use this class directly, use 
 * {@link com.semagia.tmapix.index.IndexManager#getIdentityIndex(TopicMap)}
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
class TM4JIdentityIndex implements IIdentityIndex,
        ITopicMapAwareHelperObject {

    /**
     * The native TM4J topic map.
     */
    private TopicMap _tm;
    /**
     * The wrapped TM4J topic map (implements the TMAPI interface)
     */
    private TMAPITopicMapImpl _tmapiTM;
    /**
     * The native TM4J locator factory.
     */
    private LocatorFactory _locFactory;
    

    /* (non-Javadoc)
     * @see com.semagia.tmapix.internal.ITopicMapAwareHelperObject#initialize(org.tmapi.core.TopicMap)
     */
    public void initialize(org.tmapi.core.TopicMap topicMap) {
        _tmapiTM = (TMAPITopicMapImpl) topicMap;
        _tm = _tmapiTM.getWrapped();
        _locFactory = _tm.getLocatorFactory();
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IObjectIdentityIndex#getTopicBySubjectIdentifier(java.lang.String)
     */
    public Topic getTopicBySubjectIdentifier(String subjectIdentifier) {
        return _getTopicBySubjectIdentifier(_createLocator(subjectIdentifier));
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IObjectIdentityIndex#getTopicBySubjectIdentifier(org.tmapi.core.Locator)
     */
    public Topic getTopicBySubjectIdentifier(Locator subjectIdentifier) {
        return _getTopicBySubjectIdentifier(_createLocator(subjectIdentifier));
    }
    
    private Topic _getTopicBySubjectIdentifier(org.tm4j.net.Locator subjectIdentifier) {
        return Wrapper.wrap(_tm.getTopicBySubjectIndicator(subjectIdentifier), _tmapiTM);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IObjectIdentityIndex#getTopicBySubjectLocator(java.lang.String)
     */
    public Topic getTopicBySubjectLocator(String subjectLocator) {
        return _getTopicBySubjectLocator(_createLocator(subjectLocator));
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IObjectIdentityIndex#getTopicBySubjectLocator(org.tmapi.core.Locator)
     */
    public Topic getTopicBySubjectLocator(Locator subjectLocator) {
        return _getTopicBySubjectLocator(_createLocator(subjectLocator));
    }
    
    private Topic _getTopicBySubjectLocator(org.tm4j.net.Locator subjectLocator) {
        return Wrapper.wrap(_tm.getTopicBySubject(subjectLocator), _tmapiTM);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IObjectIdentityIndex#getTopicByItemIdentifier(java.lang.String)
     */
    public Topic getTopicByItemIdentifier(String itemIdentifier) {
        TopicMapObject tmo = getObjectByItemIdentifier(itemIdentifier);
        return tmo instanceof Topic ? (Topic) tmo : null;
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IObjectIdentityIndex#getTopicByItemIdentifier(org.tmapi.core.Locator)
     */
    public Topic getTopicByItemIdentifier(Locator itemIdentifier) {
        TopicMapObject tmo = getObjectByItemIdentifier(itemIdentifier);
        return tmo instanceof Topic ? (Topic) tmo : null;
    }
    
    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IObjectIdentityIndex#getTopicById(java.lang.String)
     */
    public Topic getTopicById(String id) {
        TopicMapObject tmo = getObjectById(id);
        return tmo instanceof Topic ? (Topic) tmo : null; 
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IObjectIdentityIndex#getObjectByItemIdentifier(java.lang.String)
     */
    public TopicMapObject getObjectByItemIdentifier(String itemIdentifier) {
        return _getObjectByItemIdentifier(_createLocator(itemIdentifier));
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IObjectIdentityIndex#getObjectByItemIdentifier(org.tmapi.core.Locator)
     */
    public TopicMapObject getObjectByItemIdentifier(Locator itemIdentifier) {
        return _getObjectByItemIdentifier(_createLocator(itemIdentifier));
    }
    
    private TopicMapObject _getObjectByItemIdentifier(org.tm4j.net.Locator itemIdentifier) {
        return (TopicMapObject) Wrapper.wrap(_tm.getObjectBySourceLocator(itemIdentifier), _tmapiTM);
    }
   
    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IObjectIdentityIndex#getObjectById(java.lang.String)
     */
    public TopicMapObject getObjectById(String id) {
        return (TopicMapObject) Wrapper.wrap(_tm.getObjectByID(id), _tmapiTM);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IIndex#isAutoUpdated()
     */
    public boolean isAutoUpdated() {
        return true;
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IIndex#reindex()
     */
    public void reindex() {
        // noop.
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IIndex#close()
     */
    public void close() {
        // noop.
    }
    
    /**
     * Converts a {@link org.tmapi.core.Locator} into a TM4J Locator.
     *
     * @param loc Locator instance to convert.
     * @return A TM4J locator instance.
     */
    private org.tm4j.net.Locator _createLocator(Locator loc) {
        return ((TMAPILocatorImpl) loc).getWrapped();
    }

    /**
     * Creates a TM4J {@link org.tm4j.net.Locator}.
     *
     * @param reference The address of the locator ({@link org.tmapi.core.Locator#getReference()})
     * @return A TM4J locator instance
     */
    private org.tm4j.net.Locator _createLocator(String reference) {
        try {
            return _locFactory.createLocator("URI", reference);
        }
        catch (org.tm4j.net.LocatorFactoryException ex) {
            ex.printStackTrace();
            throw new TMAPIXRuntimeException("Failure while creating a locator", ex);
        }
    }
}
