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
package com.semagia.tmapix.index.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.tm4j.tmapi.core.TMAPITopicMapImpl;
import org.tm4j.tmapi.helpers.Wrapper;
import org.tm4j.topicmap.BaseName;
import org.tm4j.topicmap.index.Index;
import org.tm4j.topicmap.index.IndexException;
import org.tm4j.topicmap.index.IndexManager;
import org.tm4j.topicmap.index.UnsupportedIndexException;
import org.tm4j.topicmap.index.basic.AssociationTypesIndex;
import org.tm4j.topicmap.index.basic.MemberTypesIndex;
import org.tm4j.topicmap.index.basic.OccurrenceTypesIndex;
import org.tm4j.topicmap.index.basic.TopicTypesIndex;

import org.tmapi.core.Association;
import org.tmapi.core.AssociationRole;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicName;

import com.semagia.tmapix.TMAPIXRuntimeException;
import com.semagia.tmapix.index.ITypeInstanceIndex;
import com.semagia.tmapix.internal.ITopicMapAwareHelperObject;

/**
 * {@link ITypeInstanceIndex} implementation that acts on a TM4J 
 * {@link org.tm4j.topicmap.TopicMap} instance.
 * 
 * <p>
 * Note: Do not use this class directly, use 
 * {@link com.semagia.tmapix.index.IndexManager#getTypeInstanceIndex(TopicMap)}
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
class TM4JTypeInstanceIndex implements ITypeInstanceIndex,
        ITopicMapAwareHelperObject {

    /**
     * The wrapped TM4J topic map (implements the TMAPI interface)
     */
    private TMAPITopicMapImpl _tmapiTM;
    /**
     * Native TM4J Index Manager.
     */
    private IndexManager _idxMan;
    /**
     * Native TM4J topic types index.
     */
    private TopicTypesIndex _tIdx;
    /**
     * Native TM4J assoc. types index.
     */
    private AssociationTypesIndex _assocIdx;
    /**
     * Native TM4J member types index.
     */
    private MemberTypesIndex _roleIdx;
    /**
     * Native TM4J occ. types index.
     */
    private OccurrenceTypesIndex _occIdx;
    /**
     * Flag if ALL indexes are autoupdated.
     */
    private boolean _isAutoUpdated;
    
    
    /* (non-Javadoc)
     * @see com.semagia.tmapix.internal.ITopicMapAwareHelperObject#initialize(org.tmapi.core.TopicMap)
     */
    public void initialize(TopicMap topicMap) {
        _tmapiTM = ((TMAPITopicMapImpl) topicMap);
        _idxMan = _tmapiTM.getWrapped().getIndexManager();
        try {
            _tIdx = _idxMan.getTopicTypesIndex();
            _tIdx.open();
            _assocIdx = _idxMan.getAssociationTypesIndex();
            _assocIdx.open();
            _roleIdx = _idxMan.getMemberTypesIndex();
            _roleIdx.open();
            _occIdx = _idxMan.getOccurrenceTypesIndex();
            _occIdx.open();
            _isAutoUpdated = _idxMan.getIndexMeta(TopicTypesIndex.class).isAutomaticallyUpdated()
                                && _idxMan.getIndexMeta(AssociationTypesIndex.class).isAutomaticallyUpdated()
                                && _idxMan.getIndexMeta(MemberTypesIndex.class).isAutomaticallyUpdated()
                                && _idxMan.getIndexMeta(OccurrenceTypesIndex.class).isAutomaticallyUpdated();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new TMAPIXRuntimeException("Initializing indexes failed", ex);
        }
    }
    
    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.ITypeInstanceIndex#getTopics(org.tmapi.core.Topic)
     */
    @SuppressWarnings("unchecked")
    public Collection<Topic> getTopics(Topic type) {
        return Wrapper.wrap(_tIdx.getTopicsOfType(Wrapper.unwrap(type)), _tmapiTM);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.ITypeInstanceIndex#getAssociations(org.tmapi.core.Topic)
     */
    @SuppressWarnings("unchecked")
    public Collection<Association> getAssociations(Topic type) {
        return Wrapper.wrap(_assocIdx.getAssociationsOfType(Wrapper.unwrap(type)), _tmapiTM);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.ITypeInstanceIndex#getRole(org.tmapi.core.Topic)
     */
    @SuppressWarnings("unchecked")
    public Collection<AssociationRole> getRoles(Topic type) {
        // TODO: Verify this
        return Wrapper.wrap(_roleIdx.getMembersOfType(Wrapper.unwrap(type)), _tmapiTM);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.ITypeInstanceIndex#getOccurrences(org.tmapi.core.Topic)
     */
    @SuppressWarnings("unchecked")
    public Collection<Occurrence> getOccurrences(Topic type) {
        return Wrapper.wrap(_occIdx.getOccurrencesOfType(Wrapper.unwrap(type)), _tmapiTM);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.ITypeInstanceIndex#getTopicNames(org.tmapi.core.Topic)
     */
    @SuppressWarnings("unchecked")
    public Collection<TopicName> getTopicNames(Topic type) {
        if (type != null) {
            // TM4J v0.9.8 does not support typed topic names
            return Collections.EMPTY_SET;
        }
        else {
            // TODO: Optimize this!
            // Since all topic names are untyped and TM4J does not
            // provide an index for this purpose, we need to walk through
            // all topics and collect their names. :(
            Collection<BaseName> names = new ArrayList<BaseName>();
            org.tm4j.topicmap.Topic topic = null;
            for (Iterator iter = _tmapiTM.getWrapped().getTopicsIterator(); iter.hasNext();) {
                topic = (org.tm4j.topicmap.Topic) iter.next();
                names.addAll(topic.getNames());
            }
            return Wrapper.wrap(names, _tmapiTM);
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.ITypeInstanceIndex#getTopicTypes()
     */
    @SuppressWarnings("unchecked")
    public Collection<Topic> getTopicTypes() {
        return Wrapper.wrap(_tIdx.getTopicTypes(), _tmapiTM);
    }
    
    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.ITypeInstanceIndex#getAssociationTypes()
     */
    @SuppressWarnings("unchecked")
    public Collection<Topic> getAssociationTypes() {
        return Wrapper.wrap(_assocIdx.getAssociationTypes(), _tmapiTM);
    }
    
    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.ITypeInstanceIndex#getRoleTypes()
     */
    @SuppressWarnings("unchecked")
    public Collection<Topic> getRoleTypes() {
        // TODO: Verify this
        return Wrapper.wrap(_roleIdx.getMemberTypes(), _tmapiTM);
    }
    
    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.ITypeInstanceIndex#getOccurrenceTypes()
     */
    @SuppressWarnings("unchecked")
    public Collection<Topic> getOccurrenceTypes() {
        return Wrapper.wrap(_occIdx.getOccurrenceTypes(), _tmapiTM);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.ITypeInstanceIndex#getTopicNameTypes()
     */
    public Collection<Topic> getTopicNameTypes() {
        // TM4J v0.9.8 does not support typed topic names
        return Collections.emptySet();
    }
    
    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IIndex#isAutoUpdated()
     */
    public boolean isAutoUpdated() {
        return _isAutoUpdated;
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IIndex#reindex()
     */
    public void reindex() {
        try {
            _updateIndex(_tIdx);
            _updateIndex(_assocIdx);
            _updateIndex(_roleIdx);
            _updateIndex(_occIdx);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new TMAPIXRuntimeException("Reindexing failed", ex);
        }
    }
    
    /**
     * Updates an index iff it is not autoupdated.
     *
     * @param idx The index to update.
     * @throws UnsupportedIndexException
     * @throws IndexException
     */
    private void _updateIndex(Index idx) throws UnsupportedIndexException, IndexException{
        if (_idxMan.getIndexMeta(idx.getClass()).isAutomaticallyUpdated()) {
            return;
        }
        idx.reindex();
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IIndex#close()
     */
    public void close() {
        _tmapiTM = null;
        _idxMan = null;
        try {
            _tIdx.close();
            _tIdx = null;
            _assocIdx.close();
            _assocIdx = null;
            _roleIdx.close();
            _roleIdx = null;
            _occIdx.close();
            _occIdx = null;
        }
        catch (IndexException ex) {
            ex.printStackTrace();
            throw new TMAPIXRuntimeException("Closing the index failed", ex);
        }
    }
}
