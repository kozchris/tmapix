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
import java.util.Collections;

import org.tmapi.core.Association;
import org.tmapi.core.AssociationRole;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicName;

import com.semagia.tmapix.index.ITypeInstanceIndex;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public abstract class AbstractTypeInstanceIndexTestCase extends IndexTestCase {

    private ITypeInstanceIndex _idx;

    protected abstract ITypeInstanceIndex _getTypeInstanceIndex(TopicMap tm);
    
    /* (non-Javadoc)
     * @see com.semagia.tmapix.ToolsTestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _idx = _getTypeInstanceIndex(_topicMap);
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        _idx.close();
        _idx = null;
    }
    
    public void testTopicsByType() throws Exception {
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertEquals(Collections.EMPTY_SET, _idx.getTopics(null));
        Topic type = _topicMap.createTopic();
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertEquals(Collections.EMPTY_SET, _idx.getTopics(type));
        Topic topic = _topicMap.createTopic();
        topic.addType(type);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        Collection<Topic> topics = _idx.getTopics(null);
        assertTrue(topics.contains(type));
        assertEquals(1, topics.size());
        topics = _idx.getTopics(type);
        assertTrue(topics.contains(topic));
        assertEquals(1, topics.size());
    }
    
    public void testAssociationsByType() throws Exception {
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertEquals(Collections.EMPTY_SET, _idx.getAssociations(null));
        Topic type = _topicMap.createTopic();
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertEquals(Collections.EMPTY_SET, _idx.getAssociations(type));
        Association assoc = _topicMap.createAssociation();
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        Collection<Association> assocs = _idx.getAssociations(null);
        assertTrue(assocs.contains(assoc));
        assertEquals(1, assocs.size());
        assoc.setType(type);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        
        assocs = _idx.getAssociations(null);
        assertFalse(assocs.contains(assoc));
        assertEquals(0, assocs.size());
        
        assocs = _idx.getAssociations(type);
        assertTrue(assocs.contains(assoc));
        assertEquals(1, assocs.size());
        
        assoc.setType(null);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        
        assocs = _idx.getAssociations(type);
        assertTrue(assocs.isEmpty());
    }
    
    
    public void testRolesByType() throws Exception {
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertEquals(Collections.EMPTY_SET, _idx.getRoles(null));
        Topic type = _topicMap.createTopic();
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertEquals(Collections.EMPTY_SET, _idx.getRoles(type));
        Association assoc = _topicMap.createAssociation();
        AssociationRole role = assoc.createAssociationRole(_topicMap.createTopic(),
                                                            null);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        Collection<AssociationRole> roles = _idx.getRoles(null);
        assertTrue(roles.contains(role));
        assertEquals(1, roles.size());
        role.setType(type);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        
        roles = _idx.getRoles(null);
        assertFalse(roles.contains(role));
        assertEquals(0, roles.size());
        
        roles = _idx.getRoles(type);
        assertTrue(roles.contains(role));
        assertEquals(1, roles.size());
        
        role.setType(null);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        
        roles = _idx.getRoles(type);
        assertTrue(roles.isEmpty());
    }
    
    public void testOccurrencesByType() throws Exception {
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertEquals(Collections.EMPTY_SET, _idx.getOccurrences(null));
        Topic type = _topicMap.createTopic();
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertEquals(Collections.EMPTY_SET, _idx.getOccurrences(type));
        Topic topic = _topicMap.createTopic();
        Occurrence occ = topic.createOccurrence("Semagia", null, null);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        Collection<Occurrence> occs = _idx.getOccurrences(null);
        assertTrue(occs.contains(occ));
        assertEquals(1, occs.size());
        occ.setType(type);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        
        occs = _idx.getOccurrences(null);
        assertFalse(occs.contains(occ));
        assertEquals(0, occs.size());
        
        occs = _idx.getOccurrences(type);
        assertTrue(occs.contains(occ));
        assertEquals(1, occs.size());
        
        occ.setType(null);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        
        occs = _idx.getOccurrences(type);
        assertTrue(occs.isEmpty());
    }
    
    public void testTopicNamesByType() throws Exception {
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertEquals(Collections.EMPTY_SET, _idx.getOccurrences(null));
        Topic type = _topicMap.createTopic();
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertEquals(Collections.EMPTY_SET, _idx.getOccurrences(type));
        Topic topic = _topicMap.createTopic();
        TopicName name = topic.createTopicName("Semagia", null, null);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        Collection<TopicName> names = _idx.getTopicNames(null);
        assertTrue(names.contains(name));
        assertEquals(1, names.size());
        
        if (!_XTM1_1()) {
            return;
        }

        name.setType(type);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        
        names = _idx.getTopicNames(null);
        assertFalse(names.contains(name));
        assertEquals(0, names.size());
        
        names = _idx.getTopicNames(type);
        assertTrue(names.contains(name));
        assertEquals(1, names.size());
        
        name.setType(null);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        
        names = _idx.getTopicNames(type);
        assertTrue(names.isEmpty());
    }
    
    public void testTopicTypes() throws Exception {
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertTrue(_idx.getTopicTypes().isEmpty());
        Topic type = _topicMap.createTopic();
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertTrue(_idx.getTopicTypes().isEmpty());
        Topic topic = _topicMap.createTopic();
        topic.addType(type);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        Collection<Topic> topics = _idx.getTopicTypes();
        assertTrue(topics.contains(type));
        assertEquals(1, topics.size());
        topics = _idx.getTopicTypes();
        
        topic.removeType(type);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        topics = _idx.getTopicTypes();
        assertTrue(topics.isEmpty());
    }
    
    public void testAssociationTypes() throws Exception {
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertTrue(_idx.getAssociationTypes().isEmpty());
        Topic type = _topicMap.createTopic();
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertTrue(_idx.getAssociationTypes().isEmpty());
        Association assoc = _topicMap.createAssociation();
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        Collection<Topic> topics = _idx.getAssociationTypes();
        assertTrue(topics.isEmpty());
        assoc.setType(type);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
       
        topics = _idx.getAssociationTypes();
        assertTrue(topics.contains(type));
        assertEquals(1, topics.size());
        
        assoc.setType(null);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
       
        topics = _idx.getAssociationTypes();
        assertTrue(topics.isEmpty());
    }
    
    public void testRoleTypes() throws Exception {
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertTrue(_idx.getRoleTypes().isEmpty());
        Topic type = _topicMap.createTopic();
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertTrue(_idx.getRoleTypes().isEmpty());
        Association assoc = _topicMap.createAssociation();
        AssociationRole role = assoc.createAssociationRole(_topicMap.createTopic(), 
                                                            null);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        Collection<Topic> topics = _idx.getRoleTypes();
        assertTrue(topics.isEmpty());
        role.setType(type);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
       
        topics = _idx.getRoleTypes();
        assertTrue(topics.contains(type));
        assertEquals(1, topics.size());
        
        role.setType(null);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
       
        topics = _idx.getRoleTypes();
        assertTrue(topics.isEmpty());
    }
    
    public void testOccurrenceTypes() throws Exception {
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertTrue(_idx.getOccurrenceTypes().isEmpty());
        Topic type = _topicMap.createTopic();
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertTrue(_idx.getOccurrenceTypes().isEmpty());
        Topic topic = _topicMap.createTopic();
        Occurrence occ = topic.createOccurrence("Semagia", null, null);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        Collection<Topic> topics = _idx.getOccurrenceTypes();
        assertTrue(topics.isEmpty());
        occ.setType(type);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
       
        topics = _idx.getOccurrenceTypes();
        assertTrue(topics.contains(type));
        assertEquals(1, topics.size());
        
        occ.setType(null);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
       
        topics = _idx.getOccurrenceTypes();
        assertTrue(topics.isEmpty());
    }
    
    public void testTopicNameTypes() throws Exception {
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertTrue(_idx.getTopicNameTypes().isEmpty());
        Topic type = _topicMap.createTopic();
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertTrue(_idx.getTopicNameTypes().isEmpty());
        Topic topic = _topicMap.createTopic();
        TopicName name = topic.createTopicName("Semagia", null, null);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        Collection<Topic> topics = _idx.getTopicNameTypes();
        assertTrue(topics.isEmpty());
        
        if (!_XTM1_1()) {
            return;
        }
        
        name.setType(type);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
       
        topics = _idx.getTopicNameTypes();
        assertTrue(topics.contains(type));
        assertEquals(1, topics.size());
        
        name.setType(null);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
       
        topics = _idx.getTopicNameTypes();
        assertTrue(topics.isEmpty());
    }
}
