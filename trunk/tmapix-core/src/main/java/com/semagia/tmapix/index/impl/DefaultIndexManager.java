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

import org.tmapi.core.TopicMap;

import com.semagia.tmapix.index.IIdentityIndex;
import com.semagia.tmapix.index.IScopeIndex;
import com.semagia.tmapix.index.ITypeInstanceIndex;

/**
 * Default index manager that works for all TMAPI implementations.
 * 
 * <p>
 * Note: Do not use this class directly, use 
 * {@link com.semagia.tmapix.index.IndexManager}
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class DefaultIndexManager implements IIndexManager {

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.impl.IIndexManager#getIdentityIndex(org.tmapi.core.TopicMap)
     */
    public IIdentityIndex getIdentityIndex(TopicMap topicMap) {
        DefaultIdentityIndex idx = new DefaultIdentityIndex();
        idx.initialize(topicMap);
        return idx;
    }
    
    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.impl.IIndexManager#getTypeInstanceIndex(org.tmapi.core.TopicMap)
     */
    public ITypeInstanceIndex getTypeInstanceIndex(TopicMap topicMap) {
        DefaultTypeInstanceIndex idx = new DefaultTypeInstanceIndex();
        idx.initialize(topicMap);
        return idx;
    }
    
    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.impl.IIndexManager#getScopeIndex(org.tmapi.core.TopicMap)
     */
    public IScopeIndex getScopeIndex(TopicMap topicMap) {
        DefaultScopeIndex idx = new DefaultScopeIndex();
        idx.initialize(topicMap);
        return idx;
    }
}
