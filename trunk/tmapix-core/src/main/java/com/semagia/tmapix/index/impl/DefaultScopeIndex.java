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

import java.util.Collection;

import org.tmapi.core.ScopedObject;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.index.TMAPIIndexException;
import org.tmapi.index.core.ScopedObjectsIndex;

import com.semagia.tmapix.TMAPIXRuntimeException;
import com.semagia.tmapix.index.IScopeIndex;
import com.semagia.tmapix.internal.ITopicMapAwareHelperObject;

/**
 * {@link com.semagia.tmapix.index.IScopeIndex} implementation that 
 * uses / wraps the {@link org.tmapi.index.core.ScopedObjectsIndex}.
 * This implementation works for all TMAPI implementations.
 * 
 * <p>
 * Note: Do not use this class directly, use 
 * {@link com.semagia.tmapix.index.IndexManager#getScopeIndex(TopicMap)}
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
class DefaultScopeIndex implements IScopeIndex, ITopicMapAwareHelperObject {

    private ScopedObjectsIndex _scopeIdx;
    private boolean _isAutoUpdated;
    

    /* (non-Javadoc)
     * @see com.semagia.tmapix.internal.ITopicMapAwareHelperObject#initialize(org.tmapi.core.TopicMap)
     */
    public void initialize(TopicMap topicMap) {
        try {
            _scopeIdx = (ScopedObjectsIndex) topicMap.getHelperObject(ScopedObjectsIndex.class);
            _scopeIdx.open();
            _isAutoUpdated = _scopeIdx.getFlags().isAutoUpdated();
        }
        catch (TMAPIException ex) {
            ex.printStackTrace();
            throw new TMAPIXRuntimeException("Initialization of index failed", ex);
        }
    }
    
    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IScopeIndex#getScopedObjects(org.tmapi.core.Topic)
     */
    @SuppressWarnings("unchecked")
    public Collection<ScopedObject> getScopedObjects(Topic topic) {
        return _scopeIdx.getScopedObjectsByScopingTopic(topic);
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
        if (_isAutoUpdated) {
            return;
        }
        try {
            _scopeIdx.reindex();
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
        try {
            _scopeIdx.close();
            _scopeIdx = null;
        }
        catch (TMAPIIndexException ex) {
            ex.printStackTrace();
            throw new TMAPIXRuntimeException("Closing the index failed", ex);
        }
    }
}
