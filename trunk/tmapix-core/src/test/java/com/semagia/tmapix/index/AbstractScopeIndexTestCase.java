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

import java.util.ArrayList;
import java.util.Collection;

import org.tmapi.core.Association;
import org.tmapi.core.Occurrence;
import org.tmapi.core.ScopedObject;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicName;
import org.tmapi.core.Variant;

import com.semagia.tmapix.index.IScopeIndex;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public abstract class AbstractScopeIndexTestCase extends IndexTestCase {

    private IScopeIndex _idx;

    protected abstract IScopeIndex _getScopeIndex(TopicMap tm);
    
    /* (non-Javadoc)
     * @see com.semagia.tmapix.ToolsTestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _idx = _getScopeIndex(_topicMap);
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
    
    private void _testScopedObject(ScopedObject obj) throws Exception {
        Topic topic = _topicMap.createTopic();
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        assertTrue(_idx.getScopedObjects(topic).isEmpty());
        Collection<ScopedObject> objects = _idx.getScopedObjects(null);
        assertFalse(objects.isEmpty());
        assertEquals(1, objects.size());
        assertTrue(objects.contains(obj));
        assertTrue(_idx.getScopedObjects(topic).isEmpty());
        
        obj.addScopingTopic(topic);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }        
        objects = _idx.getScopedObjects(topic);
        assertFalse(objects.isEmpty());
        assertEquals(1, objects.size());
        assertTrue(objects.contains(obj));
        assertTrue(_idx.getScopedObjects(null).isEmpty());
        
        obj.removeScopingTopic(topic);
        if (!_idx.isAutoUpdated()) {
            _idx.reindex();
        }
        objects = _idx.getScopedObjects(topic);
        assertTrue(objects.isEmpty());
        assertEquals(0, objects.size());
        assertFalse(objects.contains(obj));
        assertFalse(_idx.getScopedObjects(null).isEmpty());
        assertTrue(_idx.getScopedObjects(null).contains(obj));
        
    }
    
    public void testAssociation() throws Exception {
        Association assoc = _topicMap.createAssociation();
        _testScopedObject(assoc);
    }
    
    public void testOccurrence() throws Exception {
        Topic topic = _topicMap.createTopic();
        Occurrence occ = topic.createOccurrence("Semagia", _topicMap.createTopic(), null);
        _testScopedObject(occ);
    }
    
    public void testTopicName() throws Exception {
        Topic topic = _topicMap.createTopic();
        TopicName name = topic.createTopicName("Semagia", null);
        _testScopedObject(name);
    }
    
    public void testVariant() throws Exception {
        Topic topic = _topicMap.createTopic();
        Collection<Topic> scope = new ArrayList<Topic>();
        scope.add(_topicMap.createTopic());
        TopicName name = topic.createTopicName("Semagia", scope);
        Variant var = name.createVariant("Semagia Variant", null);
        _testScopedObject(var);
    }
}
