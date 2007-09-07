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

import org.tmapi.core.Association;
import org.tmapi.core.AssociationRole;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicName;

/**
 * Provides access to topics by their type and to typed objects by their type.
 * 
 * This interface offers a unified access to retrieve objects by their type
 * and to retrieve topics that are used as type.
 * 
 * To get an implementation of this interface use the 
 * {@link IndexManager#getTypeInstanceIndex(TopicMap)}
 * method.
 * 
 * @see {@link IndexManager#getTypeInstanceIndex(TopicMap)}
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public interface ITypeInstanceIndex extends IIndex {
    
    /**
     * Returns the topics that include the <code>type</code> as one of 
     * their types. 
     *
     * @param type The type of topics to be returned. If <code>type</code> is
     *              <code>null</code> all untyped topics will be returned.
     * @return A (maybe empty) collection of topic instances.
     */
    public Collection<Topic> getTopics(Topic type);
    
    /**
     * Returns the associations that are typed by the {@link org.tmapi.core.Topic}
     * <code>type</code>.
     *
     * @param type The type of associations to be returned. 
     *              If <code>type</code> is <code>null</code> all untyped
     *              associations will be returned.
     * @return A (maybe empty) collection of topic instances.
     */
    public Collection<Association> getAssociations(Topic type);
    
    /**
     * Returns the association roles that are typed by the 
     * {@link org.tmapi.core.Topic} <code>type</code>.
     *
     * @param type The type of association roles to be returned. 
     *              If <code>type</code> is <code>null</code> all untyped
     *              association roles will be returned.
     * @return A (maybe empty) collection of topic instances.
     */
    public Collection<AssociationRole> getRoles(Topic type);
    
    /**
     * Returns the occurrences that are typed by the {@link org.tmapi.core.Topic}
     * <code>type</code>.
     *
     * @param type The type of occurrences to be returned. 
     *              If <code>type</code> is <code>null</code> all untyped
     *              occurrences will be returned.
     * @return A (maybe empty) collection of topic instances.
     */
    public Collection<Occurrence> getOccurrences(Topic type);
    
    /**
     * Returns the topic names that are typed by the {@link org.tmapi.core.Topic}
     * <code>type</code>.
     *
     * @param type The type of topic names to be returned. 
     *              If <code>type</code> is <code>null</code> all untyped
     *              topic names will be returned.
     * @return A (maybe empty) collection of topic instances.
     */
    public Collection<TopicName> getTopicNames(Topic type);
    
    /**
     * Returns the topics that are used as type of {@link org.tmapi.core.Topic}s.
     *
     * @return A (maybe empty) collection of topic instances.
     */
    public Collection<Topic> getTopicTypes();
    
    /**
     * Returns the topics that are used as type of 
     * {@link org.tmapi.core.Association}s.
     *
     * @return A (maybe empty) collection of topic instances.
     */
    public Collection<Topic> getAssociationTypes();
    
    /**
     * Returns the topics that are used as type of 
     * {@link org.tmapi.core.AssociationRole}s. 
     *
     * @return A (maybe empty) collection of topic instances.
     */
    public Collection<Topic> getRoleTypes();
    
    /**
     * Returns the topics that are used as type of 
     * {@link org.tmapi.core.Occurrence}s.
     *
     * @return A (maybe empty) collection of topic instances.
     */
    public Collection<Topic> getOccurrenceTypes();
    
    /**
     * Returns the topics that are used as type of 
     * {@link org.tmapi.core.TopicName}s.
     *
     * @return A (maybe empty) collection of topic instances.
     */
    public Collection<Topic> getTopicNameTypes();

}
