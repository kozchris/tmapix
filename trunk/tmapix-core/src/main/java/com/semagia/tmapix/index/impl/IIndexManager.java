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
 * The Original Code is Semagia TMAPI Tools.
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
 * <em>Internal</em> interface that must be implemented by index mangers.
 * 
 * @see com.semagia.tmapix.index.IndexManager
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public interface IIndexManager {

    /**
     * Returns an initialized {@link IIdentityIndex}.
     *
     * @param topicMap A topic map instance.
     * @return A {@link IIdentityIndex} instance.
     */
    public IIdentityIndex getIdentityIndex(TopicMap topicMap);
    
    /**
     * Returns an initialized {@link ITypeInstanceIndex}.
     *
     * @param topicMap The topic map instance for the index.
     * @return A {@link ITypeInstanceIndex} instance.
     */
    public ITypeInstanceIndex getTypeInstanceIndex(TopicMap topicMap);
    
    /**
     * Returns an initialized {@link IScopeIndex}.
     *
     * @param topicMap The topic map instance for the index.
     * @return A {@link IScopeIndex} instance.
     */
    public IScopeIndex getScopeIndex(TopicMap topicMap);
    
}
