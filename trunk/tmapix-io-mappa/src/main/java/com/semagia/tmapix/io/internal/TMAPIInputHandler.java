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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.python.core.ArgParser;
import org.python.core.PyObject;
import org.python.core.PySequence;
import org.python.core.PyTuple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.tmapi.core.Association;
import org.tmapi.core.AssociationRole;
import org.tmapi.core.Locator;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapObject;
import org.tmapi.core.TopicName;
import org.tmapi.core.TopicsMustMergeException;
import org.tmapi.core.Variant;

import com.semagia.tmapix.index.IIdentityIndex;
import com.semagia.tmapix.index.IndexManager;
import com.semagia.tmapix.utils.ReificationUtils;

/**
 * Implementation of the mappa.io.ITMInputHandler.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class TMAPIInputHandler extends PyObject {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private static final String _STRING = "http://www.w3.org/2001/XMLSchema#string";
    private static final String _ANY_URI = "http://www.w3.org/2001/XMLSchema#anyURI"; 
    
    private IIdentityIndex _identityIndex;
    private TopicMap _topicmap;
    
    /**
     * Logger.
     */
    private static Logger LOG = LoggerFactory.getLogger(TMAPIInputHandler.class);

    public TMAPIInputHandler(TopicMap topicmap) {
        super();
        _topicmap = topicmap;
        _identityIndex = IndexManager.getIdentityIndex(topicmap);
    }
    
    private void _update_index() {
        if (!_identityIndex.isAutoUpdated()) {
            _identityIndex.reindex();
        }
    }

    private Locator _create_locator(String loc) {
        return _topicmap.createLocator(loc);
    }
    
    public Topic create_topic(String base) {
        //TODO: Do not ignore base
        return _topicmap.createTopic();
    }
    
    /**
     * This method returns an existing topic that has a subject identifier
     * equals to ``iid`` or an item identifier equals to `iid`. If no such 
     * topic exists, a topic is created and the locator `iid` is added
     * to its item identifiers property. This newly created topic is returned.
     *
     * @param iid
     * @return
     */
    public Topic create_topic_by_iid(String iid) {
        _update_index();
        Locator loc = _create_locator(iid);
        Topic topic = _identityIndex.getTopicByItemIdentifier(loc);
        if (topic == null) {
            topic = _identityIndex.getTopicBySubjectIdentifier(loc);
            if (topic != null) {
                topic.addSourceLocator(loc);
            }
        }
        if (topic == null) {
            topic = _topicmap.createTopic();
            topic.addSourceLocator(loc);
        }
        return topic;
    }
    
    /**
     * This method returns an existing topic that has a subject identifier
     * equals to `sid` or an item identifier equals to `sid`. If no such 
     * topic exists, a topic is created and the locator `sid` is added
     * to its subject identifiers property. This newly created topic is returned.
     *
     * @param sid
     * @return
     */
    public Topic create_topic_by_sid(String sid) {
        _update_index();
        Locator loc = _create_locator(sid);
        Topic topic = _identityIndex.getTopicBySubjectIdentifier(loc);
        if (topic == null) {
            topic = _identityIndex.getTopicByItemIdentifier(loc);
            if (topic != null) {
                topic.addSubjectIdentifier(loc);
            }
        }
        if (topic == null) {
            topic = _topicmap.createTopic();
            topic.addSubjectIdentifier(loc);
        }
        return topic;
    }
    
    /**
     * This method returns an existing topic that has a subject locator
     * equals to `slo`. If no such topic exists, a topic is created and 
     * the locator `slo` is added to its subject locators property. 
     * This newly created topic is returned.
     *
     * @param slo
     * @return
     */
    public Topic create_topic_by_slo(String slo) {
        _update_index();
        Locator loc = _create_locator(slo);
        Topic topic = _identityIndex.getTopicBySubjectLocator(loc);
        if (topic == null) {
            topic = _topicmap.createTopic();
            topic.addSubjectLocator(loc);
        }
        return topic;
    }
    
    /**
     * Adds the item identifier `iid` to the `topic`.
     *
     * If another topic has an equal item identifier or subject identifier,
     * the other topic must be merged with `topic`.
     *
     * @param topic
     * @param iid
     */
    public void add_iid(Topic topic, String iid) {
        try {
            topic.addSourceLocator(_create_locator(iid));
        }
        catch (TopicsMustMergeException ex) {
            topic.mergeIn(ex.getUnmodifiedTopic());
        }
    }
    
    /**
     * Adds the subject identifier `sid` to the `topic`.
     * 
     * If another topic has an equal item identifier or subject identifier,
     * the other topic must be merged with `topic`
     *
     * @param topic
     * @param sid
     */
    public void add_sid(Topic topic, String sid) {
        try {
            topic.addSubjectIdentifier(_create_locator(sid));
        }
        catch (TopicsMustMergeException ex) {
            topic.mergeIn(ex.getUnmodifiedTopic());
        }
    }
    
    /**
     * Adds the subject locator `slo` to the `topic`.
     * 
     * If another topic has an equal subject locator, the other topic must be 
     * merged with `topic`.
     *
     * @param topic
     * @param slo
     */
    public void add_slo(Topic topic, String slo) {
        try {
            topic.addSubjectLocator(_create_locator(slo));
        }
        catch (TopicsMustMergeException ex) {
            topic.mergeIn(ex.getUnmodifiedTopic());
        }
    }
    
    /**
     * Creates a type-instance relationship in the unconstrained scope between
     * the `topic` (which plays the role ``instance``) and the topic ``type``
     * (which plays the role `type`).
     *
     * @param topic
     * @param type
     */
    public void add_type(Topic topic, Topic type) {
        topic.addType(type);
    }
    
    /**
     * Reifies the topic map instance. 
     *
     * @param reifier
     */
    public void reify_tm(Topic reifier) {
        _set_reifier(_topicmap, reifier);
    }
    
    /**
     * Adds an item identifier to the topic map.
     *
     * @param iid
     */
    public void add_tm_iid(String iid) {
        _topicmap.addSourceLocator(_create_locator(iid));
    }
    
    /**
     * Converts a PyObject into a topic.
     *
     * @param pyObj
     * @return
     */
    private static Topic _to_topic(PyObject pyObj) {
        return (Topic) pyObj.__tojava__(Topic.class);
    }
    
    /**
     * Converts a PyObject into an array of topics.
     * If the length of the object is 0, <code>null</code> is returned.
     *
     * @param pyObj
     * @return
     */
    @SuppressWarnings("unchecked")
    private static Collection _to_topics(PyObject pyObj) {
        int length = pyObj.__len__();
        if (length == 0) {
            return null;
        }
        Topic[] topics = new Topic[length];
        for (int i=0; i < length; i++) {
            topics[i] = _to_topic(pyObj.__getitem__(i));
        }
        return Arrays.asList(topics);
    }

    /**
     * Creates an association with the specified `type`, `scope`, 
     * `reifier` and item identifiers `iids`. 
     *
     * @param args
     * @param kw
     */
    public void create_association(PyObject[] args, String[] kw) {
        ArgParser ap = new ArgParser("create_association", args, kw,
                                     new String[] {
                                                "type", "scope", 
                                                "reifier", "iids", "roles"});
        Topic type = _to_topic(ap.getPyObject(0));
        Collection scope = _to_topics(ap.getPyObject(1));
        Topic reifier = _to_topic(ap.getPyObject(2));
        PySequence iids = (PySequence) ap.getPyObject(3);
        PySequence roles = (PySequence) ap.getPyObject(4);
        Association assoc = _topicmap.createAssociation();
        assoc.setType(type);
        _set_reifier(assoc, reifier);
        _set_iids(assoc, iids);
        if (scope != null) {
            for (Iterator iter = scope.iterator(); iter.hasNext();) {
                assoc.addScopingTopic((Topic) iter.next()); 
            }
        }
        _create_roles(assoc, roles);
    }
    
    private void _create_roles(Association parent, PySequence roles) {
        PyObject iter = roles.__iter__();
        for (PyTuple tuple; (tuple = (PyTuple) iter.__iternext__()) != null;) {
            // type, player, reifier, iids
            Topic type = _to_topic(tuple.__getitem__(0));
            Topic player = _to_topic(tuple.__getitem__(1));
            Topic reifier = _to_topic(tuple.__getitem__(2));
            PySequence iids = (PySequence) tuple.__getitem__(3);
            AssociationRole role = parent.createAssociationRole(player, type);
            _set_reifier(role, reifier);
            _set_iids(role, iids);
        }
    }
    
    /**
     * Creates an occurrence with the specified `parent`, `type`, `value`,
     * `scope`, `reifier` and item identifiers `iids`. 
     *
     * @param args
     * @param kw
     */
    public void create_occurrence(PyObject[] args, String[] kw) {
        ArgParser ap = new ArgParser("create_occurrence", args, kw,
                                     new String[] {
                                                "parent", "type", "value",
                                                "scope", "reifier", "iids"});
        Topic parent = _to_topic(ap.getPyObject(0));
        Topic type = _to_topic(ap.getPyObject(1));
        PyTuple valueTuple = (PyTuple) ap.getPyObject(2);
        String value = (String) valueTuple.__getitem__(0).__tojava__(String.class);
        String datatype = (String) valueTuple.__getitem__(1).__tojava__(String.class);
        Collection scope = _to_topics(ap.getPyObject(3));
        Topic reifier = _to_topic(ap.getPyObject(4));
        PySequence iids = (PySequence) ap.getPyObject(5);
        Occurrence occ = null;
        if (_ANY_URI.equals(datatype)) {
            occ = parent.createOccurrence(_create_locator(value), type, scope);
        }
        else {
            occ = parent.createOccurrence(value, type, scope);
            if (!_STRING.equals(datatype)) {
                LOG.warn("The datatype of the occurrence id='" + occ.getObjectId() + "' " +
                        "was converted from '" + datatype + "' into a string");
            }
        }
        _set_reifier(occ, reifier);
        _set_iids(occ, iids);
    }
    
    /**
     * Creates a topic name with the specified `parent`, `type`, `value`,
     * `scope`, `reifier` and item identifiers `iids`.
     *
     * @param args
     * @param kw
     */
    public void create_name(PyObject[] args, String[] kw) {
        ArgParser ap = new ArgParser("create_name", args, kw,
                                     new String[] {
                                                "parent", "type", "value",
                                                "scope", "reifier", "iids", 
                                                "variants"});
        Topic parent = _to_topic(ap.getPyObject(0));
        Topic type = _to_topic(ap.getPyObject(1));
        String value = ap.getString(2);
        Collection scope = _to_topics(ap.getPyObject(3));
        Topic reifier = _to_topic(ap.getPyObject(4));
        PySequence iids = (PySequence) ap.getPyObject(5);
        PySequence variants = (PySequence) ap.getPyObject(6);
        TopicName name = parent.createTopicName(value, type, scope);
        _set_reifier(name, reifier);
        _set_iids(name, iids);
        _create_variants(name, variants);
    }
    
    /**
     * 
     *
     * @param parent
     * @param variants
     */
    private void _create_variants(TopicName parent, PySequence variants) {
        PyObject iter = variants.__iter__();
        for (PyTuple tuple; (tuple = (PyTuple) iter.__iternext__()) != null;) {
            // value, scope, reifier, iids
            PyTuple valueTuple = (PyTuple) tuple.__getitem__(0);
            String value = (String) valueTuple.__getitem__(0).__tojava__(String.class);
            String datatype = (String) valueTuple.__getitem__(1).__tojava__(String.class);
            Collection scope = _to_topics(tuple.__getitem__(1));
            Topic reifier = _to_topic(tuple.__getitem__(2));
            PySequence iids = (PySequence) tuple.__getitem__(3);
            Variant var = null;
            if (_ANY_URI.equals(datatype)) {
                var = parent.createVariant(_create_locator(value), scope);
            }
            else {
                var = parent.createVariant(value, scope);
                if (!_STRING.equals(datatype)) {
                    LOG.warn("The datatype of the variant id='" + var.getObjectId() + "' " +
                            "was converted from '" + datatype + "' into a string");
                }
            }
            _set_reifier(var, reifier);
            _set_iids(var, iids);
        }        
    }
    
    private static void _set_reifier(TopicMapObject tmo, Topic reifier) {
        if (reifier == null) {
            return;
        }
        Topic existing = ReificationUtils.getReifier(tmo);
        if (existing != null) {
            if (existing.equals(reifier)) {
                return;
            }
            existing.mergeIn(reifier);
        }
        else {
            ReificationUtils.createReification(reifier, tmo);
        }
    }
    
    private void _set_iids(TopicMapObject tmo, PySequence iids) {
        PyObject iter = iids.__iter__();
        for (PyObject item; (item = iter.__iternext__()) != null;)  {
            tmo.addSourceLocator(_create_locator((String) item.__tojava__(String.class))); 
        }
    }

}
