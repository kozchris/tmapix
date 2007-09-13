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
 * The Initial Developer of the Original Code is Semagia http://www.semagia.com/. 
 * All Rights Reserved.
 * 
 * Contributer(s): Lars Heuer <heuer[at]semagia.com>
 *
 */
package com.semagia.tmapix.io.internal;

import java.util.Iterator;

import org.python.util.PythonInterpreter;

import org.tmapi.core.Association;
import org.tmapi.core.AssociationRole;
import org.tmapi.core.Locator;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;
import org.tmapi.core.TopicName;
import org.tmapi.core.Variant;

import com.semagia.tmapix.index.IIdentityIndex;
import com.semagia.tmapix.index.IndexManager;
import com.semagia.tmapix.utils.TMAPIFeatures;

import junit.framework.TestCase;

/**
 * Tests against the TMAPIInputHandler.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestTMAPIInputHandler extends TestCase {

    private static final String _XS_STRING = "http://www.w3.org/2001/XMLSchema#string";
    private static final String _XS_ANY_URI = "http://www.w3.org/2001/XMLSchema#anyURI"; 
    private static final String _XS_INTEGER = "http://www.w3.org/2001/XMLSchema#integer";

    private static final String _BASE_URI = "http://tmapix.semagia.com/test/importer";
    private TopicMapSystem _sys;
    private TopicMap _tm;
    private IIdentityIndex _identityIndex;
    private PythonInterpreter _py;
    private TMAPIInputHandler _inputHandler;

    public void setUp() throws Exception {
        TopicMapSystemFactory factory = null;
        factory = TopicMapSystemFactory.newInstance();
        TMAPIFeatures.setXTM10(factory, false);
        TMAPIFeatures.setAutomerge(factory, false);
        TMAPIFeatures.setReadOnly(factory, false);
        if (!TMAPIFeatures.setXTM11(factory, true)) {
            System.out.println("XTM 1.1 is not supported, some tests may fail");
        }
        if (!TMAPIFeatures.setTNC(factory, false)) {
            System.out.println("Disabling the Topic Name Constraint is not supported, some tests may fail");
        }
        _sys = factory.newTopicMapSystem();
        _tm = _sys.createTopicMap(_BASE_URI);
        _identityIndex = IndexManager.getIdentityIndex(_tm);
        _py = new PythonInterpreter();
        _inputHandler = new TMAPIInputHandler(_tm);
        _py.set("handler", _inputHandler);
        _py.exec("import mappa");
    }
    
    public void tearDown() throws Exception {
        super.tearDown();
        for (Iterator iter = _sys.getBaseLocators().iterator(); iter.hasNext();) {
           TopicMap tm = _sys.getTopicMap((Locator) iter.next());
           tm.remove();
        }
    }

    private void _updateIndex() {
        if (!_identityIndex.isAutoUpdated()) {
            _identityIndex.reindex();
        }
    }

    private Topic topicBySubjectIdentifier(String sid) {
        _updateIndex();
        return _identityIndex.getTopicBySubjectIdentifier(sid);
    }
    
    private Topic topicBySubjectLocator(String slo) {
        _updateIndex();
        return _identityIndex.getTopicBySubjectLocator(slo);
    }
    
    private Topic topicByItemIdentifier(String iid) {
        _updateIndex();
        return _identityIndex.getTopicByItemIdentifier(iid);
    }
    
    public void testTopicBySubjectIdentifier() {
        String sid = "http://psi.semagia.com/semagia";
        String py = "handler.create_topic_by_sid('" + sid + "')";
        Topic topic = topicBySubjectIdentifier(sid);
        assertNull(topic);
        _py.exec(py);
        topic = topicBySubjectIdentifier(sid);
        assertNotNull(topic);
        assertEquals(1, topic.getSubjectIdentifiers().size());
        assertEquals(sid, ((Locator) topic.getSubjectIdentifiers().iterator().next()).getReference());
    }
    
    public void testTopicBySubjectIdentifierExisting() {
        String sid = "http://psi.semagia.com/semagia";
        String py = "handler.create_topic_by_sid('" + sid + "')";
        Topic topic = topicBySubjectIdentifier(sid);
        assertNull(topic);
        topic = _tm.createTopic();
        topic.addSourceLocator(_tm.createLocator(sid));
        assertEquals(topic, topicByItemIdentifier(sid));
        _py.exec(py);
        Topic topic2 = topicBySubjectIdentifier(sid);
        assertNotNull(topic2);
        assertEquals(topic, topic2);
        assertEquals(1, topic.getSubjectIdentifiers().size());
        assertEquals(sid, ((Locator) topic.getSubjectIdentifiers().iterator().next()).getReference());
    }
    
    public void testTopicByItemIdentifier() {
        String iid = "http://psi.semagia.com/semagia";
        String py = "handler.create_topic_by_iid('" + iid + "')";
        Topic topic = topicByItemIdentifier(iid);
        assertNull(topic);
        _py.exec(py);
        topic = topicByItemIdentifier(iid);
        assertNotNull(topic);
        assertEquals(1, topic.getSourceLocators().size());
        assertEquals(iid, ((Locator) topic.getSourceLocators().iterator().next()).getReference());
    }

    public void testTopicByItemIdentifierExistingSID() {
        String iid = "http://psi.semagia.com/semagia";
        String py = "handler.create_topic_by_iid('" + iid + "')";
        Topic topic = topicByItemIdentifier(iid);
        assertNull(topic);
        topic = _tm.createTopic();
        topic.addSubjectIdentifier(_tm.createLocator(iid));
        assertEquals(topic, topicBySubjectIdentifier(iid));
        _py.exec(py);
        Topic topic2 = topicByItemIdentifier(iid);
        assertNotNull(topic2);
        assertEquals(topic, topic2);
        assertEquals(1, topic2.getSourceLocators().size());
        assertEquals(iid, ((Locator) topic2.getSourceLocators().iterator().next()).getReference());
    }
    
    public void testTopicBySubjectLocator() {
        String slo = "http://www.semagia.com/";
        String py = "handler.create_topic_by_slo('" + slo + "')";
        Topic topic = topicBySubjectLocator(slo);
        assertNull(topic);
        _py.exec(py);
        topic = topicBySubjectLocator(slo);
        assertNotNull(topic);
        assertEquals(1, topic.getSubjectLocators().size());
        assertEquals(slo, ((Locator) topic.getSubjectLocators().iterator().next()).getReference());
    }
    
    public void testTopicAddSubjectIdentifier() {
        String sid = "http://psi.semagia.com/semagia";
        String py = "handler.add_sid(topic, '" + sid + "')";
        Topic topic = _tm.createTopic();
        assertEquals(0, topic.getSubjectIdentifiers().size());
        _py.set("topic", topic);
        _py.exec(py);
        Topic topic2 = topicBySubjectIdentifier(sid);
        assertNotNull(topic2);
        assertEquals(topic, topic2);
        assertEquals(1, topic.getSubjectIdentifiers().size());
        assertEquals(sid, ((Locator) topic.getSubjectIdentifiers().iterator().next()).getReference());
    }
    
    public void testTopicAddSubjectIdentifierExisting() {
        String sid = "http://psi.semagia.com/semagia";
        String py = "handler.add_sid(topic, '" + sid + "')";
        Topic existing = _tm.createTopic();
        existing.addSubjectIdentifier(_tm.createLocator(sid));
        assertEquals(existing, topicBySubjectIdentifier(sid));
        Topic topic = _tm.createTopic();
        assertEquals(0, topic.getSubjectIdentifiers().size());
        assertEquals(2, _tm.getTopics().size());
        _py.set("topic", topic);
        _py.exec(py);
        assertEquals(1, _tm.getTopics().size());
        Topic topic2 = topicBySubjectIdentifier(sid);
        assertNotNull(topic2);
        assertEquals(topic, topic2);
        assertEquals(1, topic.getSubjectIdentifiers().size());
        assertEquals(sid, ((Locator) topic.getSubjectIdentifiers().iterator().next()).getReference());
    }
    
    public void testTopicAddItemIdentifier() {
        String iid = "http://psi.semagia.com/semagia";
        String py = "handler.add_iid(topic, '" + iid + "')";
        Topic topic = _tm.createTopic();
        assertEquals(0, topic.getSourceLocators().size());
        _py.set("topic", topic);
        _py.exec(py);
        Topic topic2 = topicByItemIdentifier(iid);
        assertNotNull(topic2);
        assertEquals(topic, topic2);
        assertEquals(1, topic.getSourceLocators().size());
        assertEquals(iid, ((Locator) topic.getSourceLocators().iterator().next()).getReference());
    }
    
    public void testTopicAddItemIdentifierExisting() {
        String iid = "http://www.semagia.com/tmapix.map#test";
        String py = "handler.add_iid(topic, '" + iid + "')";
        Topic existing = _tm.createTopic();
        existing.addSourceLocator(_tm.createLocator(iid));
        assertEquals(existing, topicByItemIdentifier(iid));
        Topic topic = _tm.createTopic();
        assertEquals(2, _tm.getTopics().size());
        _py.set("topic", topic);
        _py.exec(py);
        assertEquals(1, _tm.getTopics().size());
        Topic topic2 = topicByItemIdentifier(iid);
        assertNotNull(topic2);
        assertEquals(topic, topic2);
        assertEquals(1, topic.getSourceLocators().size());
        assertEquals(iid, ((Locator) topic.getSourceLocators().iterator().next()).getReference());
    }
    
    public void testTopicAddSubjectLocator() {
        String slo = "http://www.semagia.com/";
        String py = "handler.add_slo(topic, '" + slo + "')";
        Topic topic = _tm.createTopic();
        assertEquals(0, topic.getSubjectLocators().size());
        _py.set("topic", topic);
        _py.exec(py);
        Topic topic2 = topicBySubjectLocator(slo);
        assertNotNull(topic2);
        assertEquals(topic, topic2);
        assertEquals(1, topic.getSubjectLocators().size());
        assertEquals(slo, ((Locator) topic.getSubjectLocators().iterator().next()).getReference());
    }
    
    public void testTopicAddSubjectLocatorExisting() {
        String slo = "http://www.semagia.com/";
        String py = "handler.add_slo(topic, '" + slo + "')";
        Topic existing = _tm.createTopic();
        existing.addSubjectLocator(_tm.createLocator(slo));
        assertEquals(existing, topicBySubjectLocator(slo));
        Topic topic = _tm.createTopic();
        assertEquals(0, topic.getSubjectLocators().size());
        assertEquals(2, _tm.getTopics().size());
        _py.set("topic", topic);
        _py.exec(py);
        assertEquals(1, _tm.getTopics().size());
        Topic topic2 = topicBySubjectLocator(slo);
        assertNotNull(topic2);
        assertEquals(topic, topic2);
        assertEquals(1, topic.getSubjectLocators().size());
        assertEquals(slo, ((Locator) topic.getSubjectLocators().iterator().next()).getReference());
    }
    
    public void testTopicAddType() {
        String py = "handler.add_type(topic, type)";
        Topic topic = _tm.createTopic();
        Topic type = _tm.createTopic();
        assertEquals(0, topic.getTypes().size());
        _py.set("topic", topic);
        _py.set("type", type);
        _py.exec(py);
        assertEquals(1, topic.getTypes().size());
        assertTrue(topic.getTypes().contains(type));
    }
    
    public void testReifyTopicMap() {
        String py = "handler.reify_tm(topic)";
        assertNull(_tm.getReifier());
        Topic topic = _tm.createTopic();
        _py.set("topic", topic);
        _py.exec(py);
        assertNotNull(_tm.getReifier());
        assertEquals(topic, _tm.getReifier());
    }

    public void testTopicMapAddItemIdentifier() {
        String py = "handler.add_tm_iid('http://www.semagia.com/map1#tm')";
        assertEquals(0, _tm.getSourceLocators().size());
        _py.exec(py);
        assertEquals(1, _tm.getSourceLocators().size());
        assertEquals("http://www.semagia.com/map1#tm", ((Locator)_tm.getSourceLocators().iterator().next()).getReference());
    }
    
    public void testReifyTopicMapExistingReifier() {
        String py = "handler.reify_tm(topic)";
        assertNull(_tm.getReifier());
        Topic topic = _tm.createTopic();
        _py.set("topic", topic);
        _py.exec(py);
        assertNotNull(_tm.getReifier());
        assertEquals(topic, _tm.getReifier());
        Topic topic2 = _tm.createTopic();
        assertFalse(topic.equals(topic2));
        assertEquals(2, _tm.getTopics().size());
        _py.set("topic", topic2);
        _py.exec(py);
        assertEquals(1, _tm.getTopics().size());
        assertNotNull(_tm.getReifier());
    }
    
    public void testCreateAssociation() {
        // type, scope, reifier, iids, roles
        String py = "roles = [(role_type1, player1, None, ()), (role_type2, player2, None, ())]\n" +
                "handler.create_association(type, (), None, (), roles)";
        Topic type = _tm.createTopic();
        Topic role_type1 = _tm.createTopic();
        Topic player1 = _tm.createTopic();
        Topic role_type2 = _tm.createTopic();
        Topic player2 = _tm.createTopic();
        assertEquals(0, _tm.getAssociations().size());
        _py.set("type", type);
        _py.set("role_type1", role_type1);
        _py.set("player1", player1);
        _py.set("role_type2", role_type2);
        _py.set("player2", player2);
        _py.exec(py);
        assertEquals(1, _tm.getAssociations().size());
        Association assoc = (Association) _tm.getAssociations().iterator().next();
        assertEquals(2, assoc.getAssociationRoles().size());
        assertEquals(1, player1.getRolesPlayed().size());
        assertEquals(1, player2.getRolesPlayed().size());
        AssociationRole role1 = (AssociationRole) player1.getRolesPlayed().iterator().next();
        assertEquals(role_type1, role1.getType());
        AssociationRole role2 = (AssociationRole) player2.getRolesPlayed().iterator().next();
        assertEquals(role_type2, role2.getType());
    }
    
    public void testCreateAssociationWithScope() {
        // type, scope, reifier, iids, roles
        String py = "roles = [(role_type1, player1, None, ()), (role_type2, player2, None, ())]\n" +
                "handler.create_association(type, [theme1, theme2], None, (), roles)";
        Topic type = _tm.createTopic();
        Topic role_type1 = _tm.createTopic();
        Topic player1 = _tm.createTopic();
        Topic role_type2 = _tm.createTopic();
        Topic player2 = _tm.createTopic();
        Topic theme1 = _tm.createTopic();
        Topic theme2 = _tm.createTopic();  
        assertEquals(0, _tm.getAssociations().size());
        _py.set("type", type);
        _py.set("role_type1", role_type1);
        _py.set("player1", player1);
        _py.set("role_type2", role_type2);
        _py.set("player2", player2);
        _py.set("theme1", theme1);
        _py.set("theme2", theme2);
        _py.exec(py);
        assertEquals(1, _tm.getAssociations().size());
        Association assoc = (Association) _tm.getAssociations().iterator().next();
        assertEquals(2, assoc.getAssociationRoles().size());
        assertEquals(2, assoc.getScope().size());
        assertTrue(assoc.getScope().contains(theme1));
        assertTrue(assoc.getScope().contains(theme2));
    }
    
    public void testCreateAssociationWithReifier() {
        // type, scope, reifier, iids, roles
        String py = "roles = [(role_type1, player1, None, ()), (role_type2, player2, None, ())]\n" +
                "handler.create_association(type, (), reifier, (), roles)";
        Topic type = _tm.createTopic();
        Topic role_type1 = _tm.createTopic();
        Topic player1 = _tm.createTopic();
        Topic role_type2 = _tm.createTopic();
        Topic player2 = _tm.createTopic();
        Topic reifier = _tm.createTopic();
        assertEquals(0, _tm.getAssociations().size());
        _py.set("type", type);
        _py.set("role_type1", role_type1);
        _py.set("player1", player1);
        _py.set("role_type2", role_type2);
        _py.set("player2", player2);
        _py.set("reifier", reifier);
        _py.exec(py);
        assertEquals(1, _tm.getAssociations().size());
        Association assoc = (Association) _tm.getAssociations().iterator().next();
        assertEquals(2, assoc.getAssociationRoles().size());
        assertEquals(reifier, assoc.getReifier());
    }
    
    public void testCreateAssociationWithRoleReified() {
        // type, scope, reifier, iids, roles
        String py = "roles = [(role_type, player, reifier, ())]\n" +
                "handler.create_association(type, (), None, (), roles)";
        Topic type = _tm.createTopic();
        Topic role_type = _tm.createTopic();
        Topic player = _tm.createTopic();
        Topic reifier = _tm.createTopic();
        assertEquals(0, _tm.getAssociations().size());
        _py.set("type", type);
        _py.set("role_type", role_type);
        _py.set("player", player);
        _py.set("reifier", reifier);
        _py.exec(py);
        assertEquals(1, _tm.getAssociations().size());
        Association assoc = (Association) _tm.getAssociations().iterator().next();
        assertEquals(1, assoc.getAssociationRoles().size());
        AssociationRole role  = (AssociationRole) assoc.getAssociationRoles().iterator().next(); 
        assertEquals(reifier, role.getReifier());
    }
    
    public void testCreateOccurrenceString() {
        // parent, type, value, scope, reifier, iids
        String py = "handler.create_occurrence(parent, type, ('Semagia', '" + _XS_STRING + "'), (), None, ())";
        Topic parent = _tm.createTopic();
        Topic type = _tm.createTopic();
        assertEquals(0, parent.getOccurrences().size());
        _py.set("parent", parent);
        _py.set("type", type);
        _py.exec(py);
        assertEquals(1, parent.getOccurrences().size());
        Occurrence occ = (Occurrence) parent.getOccurrences().iterator().next();
        assertNull(occ.getResource());
        assertEquals("Semagia", occ.getValue());
        assertEquals(type, occ.getType());
        assertNull(occ.getReifier());
        assertTrue(occ.getSourceLocators().isEmpty());
    }
    
    public void testCreateOccurrenceInteger() {
        // parent, type, value, scope, reifier, iids
        String py = "handler.create_occurrence(parent, type, ('1', '" + _XS_INTEGER + "'), (), None, ())";
        Topic parent = _tm.createTopic();
        Topic type = _tm.createTopic();
        assertEquals(0, parent.getOccurrences().size());
        _py.set("parent", parent);
        _py.set("type", type);
        _py.exec(py);
        assertEquals(1, parent.getOccurrences().size());
        Occurrence occ = (Occurrence) parent.getOccurrences().iterator().next();
        assertNull(occ.getResource());
        assertEquals("1", occ.getValue());
        assertEquals(type, occ.getType());
        assertNull(occ.getReifier());
        assertTrue(occ.getSourceLocators().isEmpty());
    }
    
    public void testCreateOccurrenceAnyURI() {
        // parent, type, value, scope, reifier, iids
        String py = "handler.create_occurrence(parent, type, ('http://www.semagia.com/', '" + _XS_ANY_URI + "'), (), None, ())";
        Topic parent = _tm.createTopic();
        Topic type = _tm.createTopic();
        assertEquals(0, parent.getOccurrences().size());
        _py.set("parent", parent);
        _py.set("type", type);
        _py.exec(py);
        assertEquals(1, parent.getOccurrences().size());
        Occurrence occ = (Occurrence) parent.getOccurrences().iterator().next();
        assertNull(occ.getValue());
        assertEquals("http://www.semagia.com/", occ.getResource().getReference());
        assertEquals(type, occ.getType());
        assertNull(occ.getReifier());
        assertTrue(occ.getSourceLocators().isEmpty());
    }
    
    public void testCreateOccurrenceWithReifier() {
        // parent, type, value, scope, reifier, iids
        String py = "handler.create_occurrence(parent, type, ('Semagia', '" + _XS_STRING + "'), (), reifier, ())";
        Topic parent = _tm.createTopic();
        Topic type = _tm.createTopic();
        Topic reifier = _tm.createTopic();
        assertEquals(0, parent.getOccurrences().size());
        _py.set("parent", parent);
        _py.set("type", type);
        _py.set("reifier", reifier);
        _py.exec(py);
        assertEquals(1, parent.getOccurrences().size());
        Occurrence occ = (Occurrence) parent.getOccurrences().iterator().next();
        assertNull(occ.getResource());
        assertEquals("Semagia", occ.getValue());
        assertEquals(type, occ.getType());
        assertEquals(reifier, occ.getReifier());
    }
    
    public void testCreateOccurrenceWithScope() {
        // parent, type, value, scope, reifier, iids
        String py = "handler.create_occurrence(parent, type, ('Semagia', '" + _XS_STRING + "'), [theme1, theme2], None, ())";
        Topic parent = _tm.createTopic();
        Topic type = _tm.createTopic();
        Topic theme1 = _tm.createTopic();
        Topic theme2 = _tm.createTopic();
        assertEquals(0, parent.getOccurrences().size());
        _py.set("parent", parent);
        _py.set("type", type);
        _py.set("theme1", theme1);
        _py.set("theme2", theme2);
        _py.exec(py);
        assertEquals(1, parent.getOccurrences().size());
        Occurrence occ = (Occurrence) parent.getOccurrences().iterator().next();
        assertNull(occ.getResource());
        assertEquals("Semagia", occ.getValue());
        assertEquals(type, occ.getType());
        assertEquals(2, occ.getScope().size());
        assertTrue(occ.getScope().contains(theme1));
        assertTrue(occ.getScope().contains(theme2));
    }
    
    public void testCreateName() {
        // parent, type, value, scope, reifier, iids, variants
        String py = "handler.create_name(parent, type, 'Semagia', (), None, (), ())";
        Topic parent = _tm.createTopic();
        Topic type = _tm.createTopic();
        assertEquals(0, parent.getTopicNames().size());
        _py.set("parent", parent);
        _py.set("type", type);
        _py.exec(py);
        assertEquals(1, parent.getTopicNames().size());
        TopicName name = (TopicName) parent.getTopicNames().iterator().next();
        assertEquals("Semagia", name.getValue());
        assertEquals(type, name.getType());
        assertNull(name.getReifier());
        assertTrue(name.getVariants().isEmpty());
        assertTrue(name.getSourceLocators().isEmpty());
    }
    
    public void testCreateNameWithReifier() {
        // parent, type, value, scope, reifier, iids, variants
        String py = "handler.create_name(parent, type, 'Semagia', (), reifier, (), ())";
        Topic parent = _tm.createTopic();
        Topic type = _tm.createTopic();
        Topic reifier = _tm.createTopic();
        assertEquals(0, parent.getTopicNames().size());
        _py.set("parent", parent);
        _py.set("type", type);
        _py.set("reifier", reifier);
        _py.exec(py);
        assertEquals(1, parent.getTopicNames().size());
        TopicName name = (TopicName) parent.getTopicNames().iterator().next();
        assertEquals("Semagia", name.getValue());
        assertEquals(type, name.getType());
        assertEquals(reifier, name.getReifier());
        assertTrue(name.getVariants().isEmpty());
    }
    
    public void testCreateNameWithItemIdentifiers() {
        // parent, type, value, scope, reifier, iids, variants
        String py = "handler.create_name(parent, type, 'Semagia', (), None," +
                "('http://www.example.org/map#a', 'http://www.example.org/map#b'), ())";
        Topic parent = _tm.createTopic();
        Topic type = _tm.createTopic();
        assertEquals(0, parent.getTopicNames().size());
        _py.set("parent", parent);
        _py.set("type", type);
        _py.exec(py);
        assertEquals(1, parent.getTopicNames().size());
        TopicName name = (TopicName) parent.getTopicNames().iterator().next();
        assertEquals("Semagia", name.getValue());
        assertEquals(type, name.getType());
        assertNull(name.getReifier());
        assertTrue(name.getVariants().isEmpty());
        assertEquals(2, name.getSourceLocators().size());
        Locator iid1 = _tm.createLocator("http://www.example.org/map#a");
        Locator iid2 = _tm.createLocator("http://www.example.org/map#b");
        assertTrue(name.getSourceLocators().contains(iid1));
        assertTrue(name.getSourceLocators().contains(iid2));
    }
    
    public void testCreateNameWithScope() {
        // parent, type, value, scope, reifier, iids, variants
        String py = "scope = [theme1, theme2]\n" +
                "handler.create_name(parent, type, 'Semagia', scope, None, (), ())";
        Topic parent = _tm.createTopic();
        Topic type = _tm.createTopic();
        Topic theme1 = _tm.createTopic();
        Topic theme2 = _tm.createTopic();        
        assertEquals(0, parent.getTopicNames().size());
        _py.set("parent", parent);
        _py.set("type", type);
        _py.set("theme1", theme1);
        _py.set("theme2", theme2);
        _py.exec(py);
        assertEquals(1, parent.getTopicNames().size());
        TopicName name = (TopicName) parent.getTopicNames().iterator().next();
        assertTrue(name.getVariants().isEmpty());
        assertEquals(2, name.getScope().size());
        assertTrue(name.getScope().contains(theme1));
        assertTrue(name.getScope().contains(theme2));
    }
    
    public void testCreateNameWithVariants() {
        String py = "scope = [theme1, theme2]\n" +
                "vars = [(('semagia', '" + _XS_STRING + "'), scope, reifier, ())," +
                        "(('http://www.semagia.com/', '" + _XS_ANY_URI + "'), scope, None, ())," +
                        "(('1', '" + _XS_INTEGER + "'), scope, None, ())]\n" +
                // parent, type, value, scope, reifier, iids, variants
                "handler.create_name(parent, type, 'Semagia', (), None, (), vars)";
        Topic parent = _tm.createTopic();
        Topic type = _tm.createTopic();
        Topic reifier = _tm.createTopic();
        Topic theme1 = _tm.createTopic();
        Topic theme2 = _tm.createTopic();        
        assertEquals(0, parent.getTopicNames().size());
        _py.set("parent", parent);
        _py.set("type", type);
        _py.set("theme1", theme1);
        _py.set("theme2", theme2);
        _py.set("reifier", reifier);
        _py.exec(py);
        assertEquals(1, parent.getTopicNames().size());
        TopicName name = (TopicName) parent.getTopicNames().iterator().next();
        assertEquals(3, name.getVariants().size());
        assertEquals(1, reifier.getReified().size());
        Variant var1 = (Variant) reifier.getReified().iterator().next();
        assertNull(var1.getResource());
        assertEquals("semagia", var1.getValue());
        for (Iterator iter = name.getVariants().iterator(); iter.hasNext();) {
            Variant var = (Variant) iter.next();
            assertTrue(var.getScope().contains(theme1));
            assertTrue(var.getScope().contains(theme2));
            if (var.equals(var1)) {
                continue;
            }
            assertEquals(0, var.getSourceLocators().size());
            assertNull(var.getReifier());
            if (var.getResource() == null) {
                assertEquals("1", var.getValue());
            }
            else {
                assertEquals("http://www.semagia.com/", var.getResource().getReference());
            }
        }
    }
}
