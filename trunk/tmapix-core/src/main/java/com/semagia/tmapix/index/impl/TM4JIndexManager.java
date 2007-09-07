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
 * Index manager for <a href="http://tm4j.org/">TM4J</a>.
 * 
 * <p>
 * Note: Do not use this class directly, use 
 * {@link com.semagia.tmapix.index.IndexManager}
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TM4JIndexManager implements IIndexManager {
    
    public TM4JIndexManager() {
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.impl.IIndexManager#getIdentityIndex(org.tmapi.core.TopicMap)
     */
    public IIdentityIndex getIdentityIndex(TopicMap topicMap) {
        TM4JIdentityIndex idx = new TM4JIdentityIndex();
        idx.initialize(topicMap);
        return idx;
    }
    
    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.impl.IIndexManager#getTypeInstanceIndex(org.tmapi.core.TopicMap)
     */
    public ITypeInstanceIndex getTypeInstanceIndex(TopicMap topicMap) {
        TM4JTypeInstanceIndex idx = new TM4JTypeInstanceIndex();
        idx.initialize(topicMap);
        return idx;
    }
    
    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.impl.IIndexManager#getScopeIndex(org.tmapi.core.TopicMap)
     */
    public IScopeIndex getScopeIndex(TopicMap topicMap) {
        TM4JScopeIndex idx = new TM4JScopeIndex();
        idx.initialize(topicMap);
        return idx;
    }
}
