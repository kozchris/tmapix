/*
 * Copyright 2009 Lars Heuer (heuer[at]semagia.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.semagia.tmapix.io;

import java.net.MalformedURLException;
import java.util.Collection;

import com.semagia.mio.MIOException;
import com.semagia.tmapix.voc.XSD;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.utils.ClassInstanceUtils;
import net.ontopia.topicmaps.utils.MergeUtils;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
class OntopiaMapHandler extends AbstractHamsterMapHandler<TopicIF> {

    private TopicMapIF _tm;
    private TopicMapBuilderIF _builder;

    public OntopiaMapHandler(TopicMapIF topicMap) {
        _tm = topicMap;
        _builder = topicMap.getBuilder();
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.io.AbstractHamsterMapHandler#endTopicMap()
     */
    @Override
    public void endTopicMap() throws MIOException {
        super.endTopicMap();
        ClassInstanceUtils.resolveAssociations1(_tm);
        ClassInstanceUtils.resolveAssociations2(_tm);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.io.HamsterHandler#createAssociation(java.lang.Object, java.util.Collection, java.lang.Object, java.util.Collection, java.util.Collection)
     */
    @Override
    protected void createAssociation(TopicIF type, Collection<TopicIF> scope,
            TopicIF reifier, Collection<String> iids,
            Collection<IRole<TopicIF>> roles) throws MIOException {
        AssociationIF assoc = _builder.makeAssociation(type);
        for (IRole<TopicIF> r: roles) {
            AssociationRoleIF role = _builder.makeAssociationRole(assoc, r.getType(), r.getPlayer());
            _applyItemIdentifiers(role, r.getItemIdentifiers());
            _applyReifier(role, r.getReifier());
        }
        _applyScope(assoc, scope);
        _applyItemIdentifiers(assoc, iids);
        _applyReifier(assoc, reifier);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.io.HamsterHandler#createName(java.lang.Object, java.lang.Object, java.lang.String, java.util.Collection, java.lang.Object, java.util.Collection, java.util.Collection)
     */
    @Override
    protected void createName(TopicIF parent, TopicIF type, String value,
            Collection<TopicIF> scope, TopicIF reifier,
            Collection<String> iids, Collection<IVariant<TopicIF>> variants)
            throws MIOException {
        TopicNameIF name = null;
        if (type == null) {
            name = _builder.makeTopicName(parent, value);
        }
        else {
            name = _builder.makeTopicName(parent, type, value);
        }
        for (IVariant<TopicIF> v: variants) {
            VariantNameIF var = null;
            final String datatype = v.getDatatype();
            if (XSD.ANY_URI.equals(datatype)) {
                var = _builder.makeVariantName(name, _createLocator(v.getValue()), v.getScope());
            }
            else if (XSD.STRING.equals(datatype)) {
                var = _builder.makeVariantName(name, v.getValue(), v.getScope());
            }
            else {
                var = _builder.makeVariantName(name, v.getValue(), _createLocator(datatype), v.getScope());
            }
            _applyItemIdentifiers(var, v.getItemIdentifiers());
            _applyReifier(var, v.getReifier());
        }
        _applyScope(name, scope);
        _applyItemIdentifiers(name, iids);
        _applyReifier(name, reifier);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.io.HamsterHandler#createOccurrence(java.lang.Object, java.lang.Object, java.lang.String, java.lang.String, java.util.Collection, java.lang.Object, java.util.Collection)
     */
    @Override
    protected void createOccurrence(TopicIF parent, TopicIF type, String value,
            String datatype, Collection<TopicIF> scope, TopicIF reifier,
            Collection<String> iids) throws MIOException {
        OccurrenceIF occ = null;
        if (XSD.ANY_URI.equals(datatype)) {
            occ = _builder.makeOccurrence(parent, type, _createLocator(value));
        }
        else if (XSD.STRING.equals(datatype)) {
            occ = _builder.makeOccurrence(parent, type, value);
        }
        else {
            occ = _builder.makeOccurrence(parent, type, value, _createLocator(datatype));
        }
        _applyScope(occ, scope);
        _applyItemIdentifiers(occ, iids);
        _applyReifier(occ, reifier);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.io.HamsterHandler#createTopicByItemIdentifier(java.lang.String)
     */
    @Override
    protected TopicIF createTopicByItemIdentifier(String iri)
            throws MIOException {
        final LocatorIF iid = _createLocator(iri);
        final TMObjectIF existingConstruct = _tm.getObjectByItemIdentifier(iid);
        if (existingConstruct != null) {
            if (existingConstruct instanceof TopicIF) {
                return (TopicIF) existingConstruct;
            }
            else {
                throw new MIOException("The item identifier " + iri + " is already assigned to another construct");
            }
        }
        TopicIF topic = _tm.getTopicBySubjectIdentifier(iid);
        if (topic != null) {
            topic.addItemIdentifier(iid);
        }
        else {
            topic = _builder.makeTopic();
            topic.addItemIdentifier(iid);
        }
        return topic;
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.io.HamsterHandler#createTopicBySubjectIdentifier(java.lang.String)
     */
    @Override
    protected TopicIF createTopicBySubjectIdentifier(String iri)
            throws MIOException {
        final LocatorIF sid = _createLocator(iri);
        TopicIF topic = _tm.getTopicBySubjectIdentifier(sid);
        if (topic != null) {
            return topic;
        }
        final TMObjectIF existingConstruct = _tm.getObjectByItemIdentifier(sid);
        if (existingConstruct != null && existingConstruct instanceof TopicIF) {
            return (TopicIF) existingConstruct;
        }
        else {
            topic = _builder.makeTopic();
            topic.addSubjectIdentifier(sid);
        }
        return topic;
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.io.HamsterHandler#createTopicBySubjectLocator(java.lang.String)
     */
    @Override
    protected TopicIF createTopicBySubjectLocator(String iri)
            throws MIOException {
        final LocatorIF slo = _createLocator(iri);
        TopicIF topic = _tm.getTopicBySubjectLocator(slo);
        if (topic == null) {
            topic = _builder.makeTopic();
            topic.addSubjectLocator(slo);
        }
        return topic;
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.io.HamsterHandler#handleItemIdentifier(java.lang.Object, java.lang.String)
     */
    @Override
    protected void handleItemIdentifier(TopicIF topic, String iri)
            throws MIOException {
        final LocatorIF iid = _createLocator(iri);
        final TMObjectIF existingConstruct = _tm.getObjectByItemIdentifier(iid);
        if (existingConstruct != null) {
            if (existingConstruct instanceof TopicIF) {
                if (!existingConstruct.equals(topic)) {
                    _merge(topic, (TopicIF) existingConstruct);
                    topic = (TopicIF) existingConstruct;
                }
                return;
            }
            else {
                throw new MIOException("The item identifier " + iri + " is already assigned to another construct");
            }
        }
        final TopicIF existingTopic = _tm.getTopicBySubjectIdentifier(iid);
        if (existingTopic != null && !existingTopic.equals(topic)) {
            _merge(topic, existingTopic);
            topic = existingTopic;
        }
        topic.addItemIdentifier(iid);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.io.HamsterHandler#handleSubjectIdentifier(java.lang.Object, java.lang.String)
     */
    @Override
    protected void handleSubjectIdentifier(TopicIF topic, String iri)
            throws MIOException {
        final LocatorIF sid = _createLocator(iri);
        TopicIF existingTopic = _tm.getTopicBySubjectIdentifier(sid);
        if (existingTopic != null) {
            if (!existingTopic.equals(topic)) {
                _merge(topic, existingTopic);
            }
            return;
        }
        final TMObjectIF existing = _tm.getObjectByItemIdentifier(sid);
        if (existing != null && existing instanceof TopicIF) {
            existingTopic = (TopicIF) existing;
            _merge(topic, existingTopic);
            topic = existingTopic;
        }
        topic.addSubjectIdentifier(sid);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.io.HamsterHandler#handleSubjectLocator(java.lang.Object, java.lang.String)
     */
    @Override
    protected void handleSubjectLocator(TopicIF topic, String iri)
            throws MIOException {
        final LocatorIF slo = _createLocator(iri);
        final TopicIF existing = _tm.getTopicBySubjectLocator(slo);
        if (existing != null && !existing.equals(topic)) {
            _merge(topic, existing);
        }
        else {
            topic.addSubjectLocator(slo);
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.io.HamsterHandler#handleTopicMapItemIdentifier(java.lang.String)
     */
    @Override
    protected void handleTopicMapItemIdentifier(String iri) throws MIOException {
        _tm.addItemIdentifier(_createLocator(iri));
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.io.HamsterHandler#handleTopicMapReifier(java.lang.Object)
     */
    @Override
    protected void handleTopicMapReifier(TopicIF reifier) throws MIOException {
        _tm.setReifier(reifier);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.io.HamsterHandler#handleTypeInstance(java.lang.Object, java.lang.Object)
     */
    @Override
    protected void handleTypeInstance(TopicIF instance, TopicIF type)
            throws MIOException {
        instance.addType(type);
    }

    /**
     * 
     *
     * @param iri
     * @return
     * @throws MIOException
     */
    private LocatorIF _createLocator(String iri) throws MIOException {
        try {
            return new URILocator(iri);
        }
        catch (MalformedURLException ex) {
            throw new MIOException(ex);
        }
    }

    /**
     * 
     *
     * @param source
     * @param target
     */
    private void _merge(TopicIF source, TopicIF target) {
        MergeUtils.mergeInto(target, source);
        super.notifyMerge(source, target);
    }

    /**
     * 
     *
     * @param scoped
     * @param scope
     */
    private void _applyScope(ScopedIF scoped, Collection<TopicIF> scope) {
        if (scope != null) {
            for (TopicIF theme: scope) {
                scoped.addTheme(theme);
            }
        }
    }

    /**
     * 
     *
     * @param reifiable
     * @param iids
     * @throws MIOException
     */
    private void _applyItemIdentifiers(ReifiableIF reifiable, Iterable<String> iids) throws MIOException {
        for (String iid: iids) {
            reifiable.addItemIdentifier(_createLocator(iid));
        }
    }

    /**
     * 
     *
     * @param reifiable
     * @param reifier
     */
    private void _applyReifier(ReifiableIF reifiable, TopicIF reifier) {
        if (reifier != null) {
            reifiable.setReifier(reifier);
        }
    }

}
