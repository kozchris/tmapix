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

import org.tmapi.core.Locator;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMapObject;

/**
 * Provides access to topic map constructs by their identity.
 * 
 * Instead of querying a {@link org.tmapi.core.TopicMap} for objects by
 * their identifier and using the {@link org.tmapi.index.core.TopicsIndex}
 * and {@link org.tmapi.index.core.TopicMapObjectsIndex} this interface
 * provides an unified access.
 * To get an implementation of this interface use the 
 * {@link IndexManager#getIdentityIndex(TopicMap)}
 * method.
 *
 * @see IndexManager#getIdentityIndex(TopicMap)
 *
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public interface IIdentityIndex extends IIndex {

    /**
     * Returns a topic by its subject identifier.
     * 
     * @see #getTopicBySubjectIdentifier(Locator)
     */
    public Topic getTopicBySubjectIdentifier(String subjectIdentifier);
    
    /**
     * Returns a topic by its subject identifier.
     *
     * @param subjectIdentifier The subject identifier of the {@link org.tmapi.core.Topic}
     *          to retrieve.
     * @return A topic instance or <code>null</code> if no topic has the subject 
     *          identifier.
     */
    public Topic getTopicBySubjectIdentifier(Locator subjectIdentifier);
    
    /**
     * Returns a topic by its subject locator.
     * 
     * @see #getTopicBySubjectLocator(Locator)
     */
    public Topic getTopicBySubjectLocator(String subjectLocator);
    
    /**
     * Returns a topic by its subject locator.
     *
     * @param subjectLocator The subject locator of the {@link org.tmapi.core.Topic}
     *          to retrieve.
     * @return A topic instance or <code>null</code> if no topic has the subject 
     *          locator.
     */
    public Topic getTopicBySubjectLocator(Locator subjectLocator);
    
    /**
     * Returns a topic by its item identifier.
     * 
     * @see #getTopicByItemIdentifier(Locator)
     */
    public Topic getTopicByItemIdentifier(String itemIdentifier);
    
    /**
     * Returns a topic by its item identifier.
     * 
     * Equivalent code:
     * <pre>
     *      TopicMapObject tmo = identityIndex.getObjectByItemIdentifier(loc);
     *      Topic topic = tmo instanceof Topic ? (Topic) tmo : null;
     * </pre>
     * 
     * Note: Do not use this method to check if a topic map construct with
     * the specified item identifier exists! A topic map construct with the
     * specified item identifier may exist even if this method returns 
     * <code>null</code>.
     *
     * @param itemIdentifier The item identifier of the {@link org.tmapi.core.Topic}
     *          to retrieve.
     * @return A topic instance or <code>null</code> if no topic has the item 
     *          identifier. 
     */
    public Topic getTopicByItemIdentifier(Locator itemIdentifier);
    
   /**
    * Returns a topic by its object identifier.
    * 
    * Equivalent code:
    * <pre>
    *      TopicMapObject tmo = identityIndex.getObjectById("someId");
    *      Topic topic = tmo instanceof Topic ? (Topic) tmo : null;
    * </pre>
    * or (using TMAPI directly):
    * <pre>
    *      TopicMapObject tmo = topicMap.getObjectById("someId");
    *      Topic topic = tmo instanceof Topic ? (Topic) tmo : null;
    * </pre>
    * 
    * Note: Do not use this method to check if a topic map construct with
    * the specified object identifier exists! A topic map construct with the
    * specified object id may exist even if this method returns <code>null</code>.
    *
    * @param id The object identifier of the {@link org.tmapi.core.Topic} to 
    *               retrieve.
    * @return A topic instance or <code>null</code> if no topic has the object id.
    */
    public Topic getTopicById(String id);
    
    /**
     * Returns an topic map construct by its item identifier.
     *
     * @see #getObjectByItemIdentifier(Locator)
     */
    public TopicMapObject getObjectByItemIdentifier(String itemIdentifier);
    
    /**
     * Returns a topic map object by its item identifier.
     *
     * @param itemIdentifier The item identifier of the {@link org.tmapi.core.TopicMapObject}
     *          to retrieve.
     * @return A topic map object instance or <code>null</code> if no object
     *          has the item identifier.
     */
    public TopicMapObject getObjectByItemIdentifier(Locator itemIdentifier);
    
    /**
     * Returns a topic map object by its object identifier.
     *
     * Equivalent code (using TMAPI directly):
     * <pre>
     *      TopicMapObject tmo = topicMap.getObjectById("someId");
     * </pre>
     *
     * @param id The object identifier of the {@link org.tmapi.core.TopicMapObject}
     *              to retrieve.
     * @return A topic map object instance or <code>null</code> if no object
     *          has the object id.
     */
    public TopicMapObject getObjectById(String id);
}
