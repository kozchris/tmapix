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

import java.util.Collection;

import org.tmapi.core.Association;
import org.tmapi.core.AssociationRole;
import org.tmapi.core.Occurrence;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicName;
import org.tmapi.index.Index;
import org.tmapi.index.TMAPIIndexException;
import org.tmapi.index.core.AssociationRolesIndex;
import org.tmapi.index.core.AssociationsIndex;
import org.tmapi.index.core.OccurrencesIndex;
import org.tmapi.index.core.TopicNamesIndex;
import org.tmapi.index.core.TopicsIndex;

import com.semagia.tmapix.TMAPIXRuntimeException;
import com.semagia.tmapix.index.ITypeInstanceIndex;
import com.semagia.tmapix.internal.ITopicMapAwareHelperObject;

/**
 * {@link ITypeInstanceIndex} implementation that uses the default TMAPI
 * indexes.
 * This implementation works for all TMAPI implementations.
 * 
 * <p>
 * Note: Do not use this class directly, use 
 * {@link com.semagia.tmapix.index.IndexManager#getTypeInstanceIndex(TopicMap)}
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
class DefaultTypeInstanceIndex implements ITypeInstanceIndex, ITopicMapAwareHelperObject {
    
    private TopicsIndex _tIdx;
    private AssociationsIndex _assocIdx;
    private AssociationRolesIndex _roleIdx;
    private OccurrencesIndex _occIdx;
    private TopicNamesIndex _tnIdx;
    /**
     * Flag if ALL indexes are autoupdated.
     */
    private boolean _isAutoUpdated;
    
    
    /* (non-Javadoc)
     * @see com.semagia.tmapix.internal.ITopicMapAwareHelperObject#initialize(org.tmapi.core.TopicMap)
     */
    public void initialize(TopicMap topicMap) {
        try {
            _tIdx = (TopicsIndex) _getIndex(topicMap, TopicsIndex.class);
            _assocIdx = (AssociationsIndex) _getIndex(topicMap, AssociationsIndex.class);
            _roleIdx = (AssociationRolesIndex) _getIndex(topicMap, AssociationRolesIndex.class);
            _occIdx = (OccurrencesIndex) _getIndex(topicMap, OccurrencesIndex.class);
            _tnIdx = (TopicNamesIndex) _getIndex(topicMap, TopicNamesIndex.class);
            _isAutoUpdated = _tIdx.getFlags().isAutoUpdated()
                                && _assocIdx.getFlags().isAutoUpdated()
                                && _roleIdx.getFlags().isAutoUpdated()
                                && _occIdx.getFlags().isAutoUpdated()
                                && _tnIdx.getFlags().isAutoUpdated();
        }
        catch (TMAPIException ex) {
            ex.printStackTrace();
            throw new TMAPIXRuntimeException("Initializing indexes failed", ex);
        }
    }
    
    /**
     * 
     *
     * @param topicMap
     * @param cls
     * @return
     * @throws TMAPIException
     * @since 1.0
     */
    private Index _getIndex(TopicMap topicMap, Class cls) throws TMAPIException {
        Index idx = (Index) topicMap.getHelperObject(cls);
        idx.open();
        return idx;
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.ITypeInstanceIndex#getTopics(org.tmapi.core.Topic)
     */
    @SuppressWarnings("unchecked")
    public Collection<Topic> getTopics(Topic type) {
        return _tIdx.getTopicsByType(type);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.ITypeInstanceIndex#getAssociations(org.tmapi.core.Topic)
     */
    @SuppressWarnings("unchecked")
    public Collection<Association> getAssociations(Topic type) {
        return _assocIdx.getAssociationsByType(type);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.ITypeInstanceIndex#getRole(org.tmapi.core.Topic)
     */
    @SuppressWarnings("unchecked")
    public Collection<AssociationRole> getRoles(Topic type) {
        return _roleIdx.getAssociationRolesByType(type);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.ITypeInstanceIndex#getOccurrences(org.tmapi.core.Topic)
     */
    @SuppressWarnings("unchecked")
    public Collection<Occurrence> getOccurrences(Topic type) {
        return _occIdx.getOccurrencesByType(type);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.ITypeInstanceIndex#getTopicNames(org.tmapi.core.Topic)
     */
    @SuppressWarnings("unchecked")
    public Collection<TopicName> getTopicNames(Topic type) {
        return _tnIdx.getTopicNamesByType(type);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.ITypeInstanceIndex#getTopicTypes()
     */
    @SuppressWarnings("unchecked")
    public Collection<Topic> getTopicTypes() {
        return _tIdx.getTopicTypes();
    }
    
    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.ITypeInstanceIndex#getAssociationTypes()
     */
    @SuppressWarnings("unchecked")
    public Collection<Topic> getAssociationTypes() {
        return _assocIdx.getAssociationTypes();
    }
    
    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.ITypeInstanceIndex#getRoleTypes()
     */
    @SuppressWarnings("unchecked")
    public Collection<Topic> getRoleTypes() {
        return _roleIdx.getAssociationRoleTypes();
    }
    
    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.ITypeInstanceIndex#getOccurrenceTypes()
     */
    @SuppressWarnings("unchecked")
    public Collection<Topic> getOccurrenceTypes() {
        return _occIdx.getOccurrenceTypes();
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.ITypeInstanceIndex#getTopicNameTypes()
     */
    @SuppressWarnings("unchecked")
    public Collection<Topic> getTopicNameTypes() {
        return _tnIdx.getTopicNameTypes();
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
            _updateIndex(_tnIdx);
        }
        catch (TMAPIIndexException ex) {
            ex.printStackTrace();
            throw new TMAPIXRuntimeException("Reindexing failed", ex);
        }
    }
    
    /**
     * Updates an index iff it is not autoupdated.
     *
     * @param idx The index to update.
     * @throws TMAPIIndexException If reindexing fails.
     */
    private void _updateIndex(Index idx) throws TMAPIIndexException {
        if (idx.getFlags().isAutoUpdated()) {
            return;
        }
        idx.reindex();
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.IIndex#close()
     */
    public void close() {
        try {
            _tIdx.close();
            _tIdx = null;
            _assocIdx.close();
            _assocIdx = null;
            _roleIdx.close();
            _roleIdx = null;
            _occIdx.close();
            _occIdx = null;
            _tnIdx.close();
            _tnIdx = null;
        }
        catch (TMAPIIndexException ex) {
            ex.printStackTrace();
            throw new TMAPIXRuntimeException("Closing the index failed", ex);
        }
    }

}
