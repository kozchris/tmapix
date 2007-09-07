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
package com.semagia.tmapix.internal;

import org.tmapi.core.TopicMap;

/**
 * <em>Internal</em> interface that is mainly used to initialize
 * a class that is constructed by its class name.
 * <pre>
 *      ITopicMapAwareHelperObject obj = (ITopicMapAwareHelperObject) Class.forName("foo").newInstance();
 *      obj.initialize(topicMap);
 * </pre>
 * In fact this interface has nearly the same purpose as 
 * {@link org.tmapi.core.TopicMapSystem.ConfigurableHelperObject} but is more
 * lightweight.
 * 
 * @author Lars Heuer (heuer[at]semagia.com)
 * @copyright 2005 - 2006 Semagia <a href="http://semagia.com/">semagia.com</a>
 * @license <a href="http://www.mozilla.org/MPL/">Mozilla Public License</a>
 * @version $Rev$ - $Date$
 * @since 1.0
 */
public interface ITopicMapAwareHelperObject {

    /**
     * Initializes this object with a topic map.
     *
     * @param topicMap A {@link org.tmapi.core.TopicMap} instance.
     * @since 1.0
     */
    public void initialize(TopicMap topicMap);
}
