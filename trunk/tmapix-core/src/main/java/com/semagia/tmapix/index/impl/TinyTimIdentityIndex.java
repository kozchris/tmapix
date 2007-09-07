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

import org.tinyTIM.LocatorImpl;
import org.tinyTIM.TopicMapImpl;

import org.tmapi.core.Locator;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapObject;

import com.semagia.tmapix.index.IIdentityIndex;
import com.semagia.tmapix.internal.ITopicMapAwareHelperObject;

/**
 * {@link com.semagia.tmapix.index.IIdentityIndex} implementation that
 * acts on a tinyTiM {@link org.tinytim.TopicMapImpl} instance.
 * This index is always kept in sync with the topic map.
 * This implementation only works with a tinyTiM TMAPI implementation and should
 * be faster and use less memory than an index that works on top of TMAPI.
 * 
 * <p>
 * Note: Do not use this class directly, use 
 * {@link com.semagia.tmapix.index.IndexManager#getIdentityIndex(TopicMap)}
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
class TinyTimIdentityIndex implements IIdentityIndex, ITopicMapAwareHelperObject {

    /**
     * The tinyTiM topic map instance to act on.
     */
    private TopicMapImpl _tm;
    

    /* (non-Javadoc)
     * @see com.semagia.tmapix.internal.ITopicMapAwareHelperObject#initialize(org.tmapi.core.TopicMap)
     */
    public void initialize(TopicMap topicMap) {
        _tm = (TopicMapImpl) topicMap;
    }
    
    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IObjectIdentityIndex#getTopicBySubjectIdentifier(java.lang.String)
     */
    public Topic getTopicBySubjectIdentifier(String subjectIdentifier) {
        return getTopicBySubjectIdentifier(_createLocator(subjectIdentifier));
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IObjectIdentityIndex#getTopicBySubjectIdentifier(org.tmapi.core.Locator)
     */
    public Topic getTopicBySubjectIdentifier(Locator subjectIdentifier) {
        return _tm.getTopicBySubjectIdentifier(subjectIdentifier);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IObjectIdentityIndex#getTopicBySubjectLocator(java.lang.String)
     */
    public Topic getTopicBySubjectLocator(String subjectLocator) {
        return getTopicBySubjectLocator(_createLocator(subjectLocator));
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IObjectIdentityIndex#getTopicBySubjectLocator(org.tmapi.core.Locator)
     */
    public Topic getTopicBySubjectLocator(Locator subjectLocator) {
        return _tm.getTopicBySubjectLocator(subjectLocator);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IObjectIdentityIndex#getTopicByItemIdentifier(java.lang.String)
     */
    public Topic getTopicByItemIdentifier(String itemIdentifier) {
        return getTopicByItemIdentifier(_createLocator(itemIdentifier));
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
        return getObjectByItemIdentifier(_createLocator(itemIdentifier));
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IObjectIdentityIndex#getObjectByItemIdentifier(org.tmapi.core.Locator)
     */
    public TopicMapObject getObjectByItemIdentifier(Locator itemIdentifier) {
        return _tm.getTopicMapObjectBySourceLocator(itemIdentifier);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IObjectIdentityIndex#getObjectById(java.lang.String)
     */
    public TopicMapObject getObjectById(String id) {
        return _tm.getObjectById(id);
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
     * Creates a locator from a string. 
     *
     * @param reference The address of the locator ({@link org.tmapi.core.Locator#getReference()})
     * @return A Locator instance.
     */
    private Locator _createLocator(String reference) {
        return new LocatorImpl(reference);
    }
}
