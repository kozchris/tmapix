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

import org.tmapi.core.Locator;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapObject;
import org.tmapi.index.Index;
import org.tmapi.index.TMAPIIndexException;
import org.tmapi.index.core.TopicMapObjectsIndex;
import org.tmapi.index.core.TopicsIndex;

import com.semagia.tmapix.TMAPIXRuntimeException;
import com.semagia.tmapix.index.IIdentityIndex;
import com.semagia.tmapix.internal.ITopicMapAwareHelperObject;

/**
 * {@link com.semagia.tmapix.index.IIdentityIndex} implementation that
 * uses the {@link org.tmapi.index.core.TopicsIndex} and 
 * {@link org.tmapi.index.core.TopicMapObjectsIndex}.
 * This implementation works for all TMAPI implementations.
 * 
 * <p>
 * Note: Do not use this class directly, use 
 * {@link com.semagia.tmapix.index.IndexManager#getIdentityIndex(TopicMap)}
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
class DefaultIdentityIndex implements IIdentityIndex, ITopicMapAwareHelperObject {

    private TopicMap _tm;
    private TopicsIndex _tIdx;
    private TopicMapObjectsIndex _tmoIdx;
    /**
     * Flag if ALL indexes are autoupdated.
     */
    private boolean _isAutoUpdated;
    

    /* (non-Javadoc)
     * @see com.semagia.tmapix.internal.ITopicMapAwareHelperObject#initialize(org.tmapi.core.TopicMap)
     */
    public void initialize(TopicMap topicMap) {
        _tm = topicMap;
        try {
            _tIdx = (TopicsIndex) _getIndex(TopicsIndex.class);
            _tmoIdx = (TopicMapObjectsIndex) _getIndex(TopicMapObjectsIndex.class);
            _isAutoUpdated = _tIdx.getFlags().isAutoUpdated() && _tmoIdx.getFlags().isAutoUpdated();
        }
        catch (TMAPIException ex) {
            ex.printStackTrace();
            throw new TMAPIXRuntimeException("Initializing indexes failed", ex); 
        }
    }
    
    /**
     * Returns an opened index.
     */
    private Index _getIndex(Class cls) throws TMAPIException {
        Index idx = (Index) _tm.getHelperObject(cls);
        idx.open();
        return idx;
    }
    
    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IObjectIndex#getTopicBySubjectIdentifier(java.lang.String)
     */
    public Topic getTopicBySubjectIdentifier(String subjectIdentifier) {
        return getTopicBySubjectIdentifier(_createLocator(subjectIdentifier));
    }
    
    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IObjectIndex#getTopicBySubjectIdentifier(org.tmapi.core.Locator)
     */
    public Topic getTopicBySubjectIdentifier(Locator subjectIdentifier) {
        return _tIdx.getTopicBySubjectIdentifier(subjectIdentifier);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IObjectIndex#getTopicBySubjectLocator(java.lang.String)
     */
    public Topic getTopicBySubjectLocator(String subjectLocator) {
        return getTopicBySubjectLocator(_createLocator(subjectLocator));
    }
    
    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IObjectIndex#getTopicBySubjectLocator(org.tmapi.core.Locator)
     */
    public Topic getTopicBySubjectLocator(Locator subjectLocator) {
        return _tIdx.getTopicBySubjectLocator(subjectLocator);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IObjectIndex#getTopicByItemIdentifier(java.lang.String)
     */
    public Topic getTopicByItemIdentifier(String itemIdentifier) {
        return getTopicByItemIdentifier(_createLocator(itemIdentifier));
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IObjectIndex#getTopicByItemIdentifier(org.tmapi.core.Locator)
     */
    public Topic getTopicByItemIdentifier(Locator itemIdentifier) {
        TopicMapObject tmo = getObjectByItemIdentifier(itemIdentifier);
        return tmo instanceof Topic ? (Topic) tmo : null;
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IObjectIndex#getTopicById(java.lang.String)
     */
    public Topic getTopicById(String id) {
        TopicMapObject tmo = getObjectById(id);
        return tmo instanceof Topic ? (Topic) tmo : null;
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IObjectIndex#getObjectByItemIdentifier(java.lang.String)
     */
    public TopicMapObject getObjectByItemIdentifier(String itemIdentifier) {
        return getObjectByItemIdentifier(_createLocator(itemIdentifier));
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IObjectIndex#getObjectByItemIdentifier(org.tmapi.core.Locator)
     */
    public TopicMapObject getObjectByItemIdentifier(Locator itemIdentifier) {
        return _tmoIdx.getTopicMapObjectBySourceLocator(itemIdentifier);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IObjectIndex#getObjectById(java.lang.String)
     */
    public TopicMapObject getObjectById(String id) {
        return _tm.getObjectById(id);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IIndex#isAutoUpdated()
     */
    public boolean isAutoUpdated() {
        return _isAutoUpdated;
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IIndex#reindex()
     */
    public void reindex() {
        try {
            if (!_tIdx.getFlags().isAutoUpdated()) {
                _tIdx.reindex();
            }
            if (!_tmoIdx.getFlags().isAutoUpdated()) {
                _tmoIdx.reindex();
            }
        }
        catch (TMAPIIndexException ex) {
            ex.printStackTrace();
            throw new TMAPIXRuntimeException("Reindexing failed", ex);
        }
    }
    
    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IIndex#close()
     */
    public void close() {
        _tm = null;
        try {
            _tIdx.close();
            _tIdx = null;
            _tmoIdx.close();
            _tmoIdx = null;
        }
        catch (TMAPIIndexException ex) {
            ex.printStackTrace();
            throw new TMAPIXRuntimeException("Closing the index failed", ex);
        }
    }

    /**
     * Creates a locator from a string. 
     *
     * @param reference The address of the locator ({@link org.tmapi.core.Locator#getReference()})
     * @return A Locator instance.
     */
    private Locator _createLocator(String reference) {
        return _tm.createLocator(reference);
    }
}
