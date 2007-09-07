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

import com.semagia.tmapix.TMAPIXRuntimeException;

/**
 * Loads {@link ITopicMapAwareHelperObject} instances.
 * 
 * @author Lars Heuer (heuer[at]semagia.com)
 * @copyright 2005 - 2006 Semagia <a href="http://semagia.com/">semagia.com</a>
 * @license <a href="http://www.mozilla.org/MPL/">Mozilla Public License</a>
 * @version $Rev$ - $Date$
 * @since 1.0
 */
public class TopicMapAwareHelperObjectLoader {

    /**
     * Returns an intialized helper object.
     *
     * @param topicMap The topic map that is used to initialize the object.
     * @param className The fully qualified class name to load.
     * @return An initialized helper object instance.  
     * @throws TMAPIXRuntimeException If the loading of the class fails.
     * @since 1.0
     */
    public static ITopicMapAwareHelperObject getObject(TopicMap topicMap, String className) 
                                    throws TMAPIXRuntimeException {
        ITopicMapAwareHelperObject obj = null;
        try {
            obj = (ITopicMapAwareHelperObject) Class.forName(className).newInstance();
            obj.initialize(topicMap);
        }
        catch (ClassNotFoundException ex) {
            throw new TMAPIXRuntimeException("The class " + className + " was not found", ex);
        }
        catch (InstantiationException ex) {
            throw new TMAPIXRuntimeException("Initialiaziation of " + className + " failed", ex);
        }
        catch (IllegalAccessException ex) {
            throw new TMAPIXRuntimeException("Constructor of " + className + " is not accessible", ex);
        }
        return obj;
    }
}
