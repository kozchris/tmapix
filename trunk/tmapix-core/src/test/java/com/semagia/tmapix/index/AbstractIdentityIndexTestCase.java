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

import org.tmapi.core.Association;
import org.tmapi.core.AssociationRole;
import org.tmapi.core.Locator;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicName;
import org.tmapi.core.Variant;

import com.semagia.tmapix.index.IIdentityIndex;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public abstract class AbstractIdentityIndexTestCase extends IndexTestCase {

    private IIdentityIndex _idx;

    protected abstract IIdentityIndex _getIdentityIndex(TopicMap tm);
    
    /* (non-Javadoc)
     * @see com.semagia.tmapix.ToolsTestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _idx = _getIdentityIndex(_topicMap);
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
    
    public void testInvalidId() throws Exception {
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        String invalidId = _topicMap.getObjectId() + "-semagia";
        assertNull(_idx.getObjectById(invalidId));
    }
    
    public void testObjectByIdTopicMap() throws Exception {
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        String id = _topicMap.getObjectId();
        TopicMap tm = (TopicMap) _idx.getObjectById(id);
        assertEquals(_topicMap, tm);
    }
    
    public void testObjectByIdTopic() throws Exception {
        Topic topic = _topicMap.createTopic();
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        String id = topic.getObjectId();
        Topic t = (Topic) _idx.getObjectById(id);
        assertEquals(topic, t);
    }
    
    public void testObjectByIdAssociation() throws Exception {
        Association assoc = _topicMap.createAssociation();
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        String id = assoc.getObjectId();
        Association a = (Association) _idx.getObjectById(id);
        assertEquals(assoc, a);
    }
    
    public void testObjectByIdRole() throws Exception {
        Association assoc = _topicMap.createAssociation();
        AssociationRole role = assoc.createAssociationRole(_topicMap.createTopic(), 
                                                            _topicMap.createTopic());
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        String id = role.getObjectId();
        AssociationRole r = (AssociationRole) _idx.getObjectById(id);
        assertEquals(role, r);
    }
    
    public void testObjectByIdOccurrence() throws Exception {
        Topic topic = _topicMap.createTopic();
        Occurrence occ = topic.createOccurrence("Semagia", _topicMap.createTopic(), null);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        String id = occ.getObjectId();
        Occurrence o = (Occurrence) _idx.getObjectById(id);
        assertEquals(occ, o);
    }
    
    public void testObjectByIdTopicName() throws Exception {
        Topic topic = _topicMap.createTopic();
        TopicName name = topic.createTopicName("Semagia", null);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        String id = name.getObjectId();
        TopicName tn = (TopicName) _idx.getObjectById(id);
        assertEquals(name, tn);
    }
    
    public void testObjectByIdVariant() throws Exception {
        Topic topic = _topicMap.createTopic();
        TopicName name = topic.createTopicName("Semagia", null);
        Variant var = name.createVariant("Semagia Test", null);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        String id = var.getObjectId();
        Variant v = (Variant) _idx.getObjectById(id);
        assertEquals(var, v);
    }
    
    public void testTopicById() throws Exception {
        Association assoc = _topicMap.createAssociation();
        String notTopicId = assoc.getObjectId();
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertNull(_idx.getTopicById(notTopicId));
        
        Topic topic = _topicMap.createTopic();
        String topicId = topic.getObjectId();
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertEquals(topic, _idx.getTopicById(topicId));
    }
    
    public void testObjectByItemIdentifier() throws Exception {
        Association assoc = _topicMap.createAssociation();
        String reference = "http://semagia.com/tmapitools/testItemIdentifier";
        Locator loc = _topicMap.createLocator(reference);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertNull(_idx.getObjectByItemIdentifier(reference));
        assertNull(_idx.getObjectByItemIdentifier(loc));

        assoc.addSourceLocator(loc);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertEquals(assoc, _idx.getObjectByItemIdentifier(reference));
        assertEquals(assoc, _idx.getObjectByItemIdentifier(loc));
    }
    
    public void testTopicByItemIdentifierInvalid() throws Exception {
        Association assoc = _topicMap.createAssociation();
        String reference = "http://semagia.com/tmapitools/testItemIdentifier";
        Locator loc = _topicMap.createLocator(reference);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertNull(_idx.getTopicByItemIdentifier(reference));
        assertNull(_idx.getTopicByItemIdentifier(loc));

        assoc.addSourceLocator(loc);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertNull(_idx.getTopicByItemIdentifier(reference));
        assertNull(_idx.getTopicByItemIdentifier(loc));
    }
    
    public void testTopicByItemIdentifier() throws Exception {
        Topic topic = _topicMap.createTopic();
        String reference = "http://semagia.com/tmapitools/testItemIdentifier";
        Locator loc = _topicMap.createLocator(reference);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertNull(_idx.getObjectByItemIdentifier(reference));
        assertNull(_idx.getObjectByItemIdentifier(loc));

        topic.addSourceLocator(loc);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertEquals(topic, _idx.getObjectByItemIdentifier(reference));
        assertEquals(topic, _idx.getObjectByItemIdentifier(loc));
    }
    
    public void testTopicBySubjectIdentifier() throws Exception {
        Topic topic = _topicMap.createTopic();
        String reference = "http://semagia.com/tmapitools/testSubjectIdentifier";
        Locator loc = _topicMap.createLocator(reference);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertNull(_idx.getTopicBySubjectIdentifier(reference));
        assertNull(_idx.getTopicBySubjectIdentifier(loc));

        topic.addSubjectIdentifier(loc);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertEquals(topic, _idx.getTopicBySubjectIdentifier(reference));
        assertEquals(topic, _idx.getTopicBySubjectIdentifier(loc));
    }
    
    public void testTopicBySubjectLocator() throws Exception {
        Topic topic = _topicMap.createTopic();
        String reference = "http://semagia.com/tmapitools/testSubjectLocator";
        Locator loc = _topicMap.createLocator(reference);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertNull(_idx.getTopicBySubjectLocator(reference));
        assertNull(_idx.getTopicBySubjectLocator(loc));

        topic.addSubjectLocator(loc);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertEquals(topic, _idx.getTopicBySubjectLocator(reference));
        assertEquals(topic, _idx.getTopicBySubjectLocator(loc));
    }
    
}
