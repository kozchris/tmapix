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

/**
 * A simple interface for indexes.
 * 
 * This interface is more lightweight than the {@link org.tmapi.index.Index}
 * pendant.
 * Differences:
 * <ul>
 *   <li>No need to open an index</li>
 *   <li>No checked exceptions</li>
 *   <li><em>isAutoUpdated</em> property is attached to the index; no need
 *          to fetch {@link org.tmapi.index.IndexFlags} from the index 
 *          ({@link org.tmapi.index.Index#getFlags()})</li> 
 * </ul>  
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public interface IIndex {

    /**
     * Returns if this index is automatically kept in sync. with the 
     * topic map values.
     *
     * @return <code>true</code> if the index synchronizes itself with the 
     *          underlying topic map, otherwise <code>false</code>
     */
    public boolean isAutoUpdated();
    
    /**
     * Resynchronizes this index with the data in the topic map.
     * 
     * Indexes that are automatically kept in sync should ignore this.
     */
    public void reindex();
    
    /**
     * Closes the index.
     * 
     * This operation is optional but useful to release resources.
     * After closing the index must not be used further.
     */
    public void close();
}
