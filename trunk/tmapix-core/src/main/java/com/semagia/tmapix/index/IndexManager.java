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
package com.semagia.tmapix.index;

import org.tmapi.core.TopicMap;

import com.semagia.tmapix.index.impl.IIndexManager;

/**
 * Provides access to the indexes of this package.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class IndexManager {
    
    // The implementation class may be changed by an ANT task to provide
    // an index manager that is optimized for a particular TMAPI implementation
    // !!! Keep the complete (qualified) class name here !!!
    static final IIndexManager _IDX_MAN = new com.semagia.tmapix.index.impl.DefaultIndexManager();
    
    private IndexManager() {
        // noop.
    }
    
    /**
     * Returns an initialized {@link IIdentityIndex}.
     *
     * @return A {@link IIdentityIndex} instance.
     */
    public static IIdentityIndex getIdentityIndex(TopicMap topicMap) {
        return _IDX_MAN.getIdentityIndex(topicMap);
    }
    
    /**
     * Returns an initialized {@link ITypeInstanceIndex}.
     *
     * @param topicMap The topic map instance for the index.
     * @return A {@link ITypeInstanceIndex} instance.
     */
    public static ITypeInstanceIndex getTypeInstanceIndex(TopicMap topicMap) {
        return _IDX_MAN.getTypeInstanceIndex(topicMap);
    }
    
    /**
     * Returns an initialized {@link IScopeIndex}.
     *
     * @param topicMap The topic map instance for the index.
     * @return A {@link IScopeIndex} instance.
     */
    public static IScopeIndex getScopeIndex(TopicMap topicMap) {
        return _IDX_MAN.getScopeIndex(topicMap);
    }
}
