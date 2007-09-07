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

import java.util.Collection;

import org.tmapi.core.ScopedObject;
import org.tmapi.core.Topic;

/**
 * Provides access to scoped objects.
 * 
 * To get an implementation of this interface use the 
 * {@link IndexManager#getScopeIndex(TopicMap)}
 * method.
 * 
 * @see {@link IndexManager#getScopeIndex(TopicMap)}
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public interface IScopeIndex extends IIndex {

    /**
     * Returns all scoped objects ({@link org.tmapi.core.Association}, 
     * {@link org.tmapi.core.Occurrence}, {@link org.tmapi.core.TopicName}
     * and {@link org.tmapi.core.Variant}) that have the topic in their scope.
     * 
     * Returns all {@link org.tmapi.core.ScopedObject} instances where 
     * the specified {@link org.tmapi.core.Topic} is one of the topics in 
     * the scope.
     *
     * @param topic The topic that must be in the scope of each 
     *              returned ScopedObject. 
     *              If it is <code>null</code> a collection containing 
     *              all ScopedObjects that have the unconstraint scope (empty
     *              scope) will be returned.
     * @return A (maybe empty) collection of scoped objects that have the 
     *          topic in their scope.
     */
    public Collection<ScopedObject> getScopedObjects(Topic topic);

}
