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
package net.ontopia.topicmaps.io;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;

import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.AbstractHamsterMapHandler;
import com.semagia.mio.voc.TMDM;
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
import net.ontopia.topicmaps.core.UniquenessViolationException;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.utils.ClassInstanceUtils;
import net.ontopia.topicmaps.utils.KeyGenerator;
import net.ontopia.topicmaps.utils.MergeUtils;

/**
 * {@link IMapHandler} implementation that works upon Ontopia's native API.
 */
public class OntopiaMapHandler extends AbstractHamsterMapHandler<TopicIF> {

    private TopicMapIF _tm;
    private TopicMapBuilderIF _builder;
    private Collection<DelayedEvents> _delayedEvents;

    public OntopiaMapHandler(TopicMapIF topicMap) {
        _tm = topicMap;
        _builder = topicMap.getBuilder();
        _delayedEvents = new ArrayList<DelayedEvents>();
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
        _applyScope(assoc, scope);
        for (IRole<TopicIF> r: roles) {
            AssociationRoleIF role = _builder.makeAssociationRole(assoc, r.getType(), r.getPlayer());
            if (r.getReifier() != null || !r.getItemIdentifiers().isEmpty()) {
                _delayedEvents.add(new DelayedEvents(role, r.getReifier(), r.getItemIdentifiers()));
            }
        }
        _applyReifier(assoc, reifier);
        _applyItemIdentifiers(assoc, iids);
        if (!_delayedEvents.isEmpty()) {
            for (DelayedEvents evt: _delayedEvents) {
                _applyReifier(evt.getReifiable(), evt.getReifier());
                _applyItemIdentifiers(evt.getReifiable(), evt.getItemIdentifiers());
            }
            _delayedEvents.clear();
        }
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
        if (value == null) {
            throw new MIOException("The name value must not be null");
        }
        if (type == null) {
            type = _defaultNameType();
        }
        name = _builder.makeTopicName(parent, type, value);
        _applyScope(name, scope);
        _applyItemIdentifiers(name, iids);
        _applyReifier(name, reifier);
        @SuppressWarnings("unchecked")
        final Collection<TopicIF> nameScope = name.getScope();
        for (IVariant<TopicIF> v: variants) {
            if (nameScope.containsAll(v.getScope())) {
                throw new MIOException("The variant's scope is not a superset of the parent's scope.");
            }
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
    }

    private TopicIF _defaultNameType() throws MIOException {
        return createTopicBySubjectIdentifier(TMDM.TOPIC_NAME);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.io.HamsterHandler#createOccurrence(java.lang.Object, java.lang.Object, java.lang.String, java.lang.String, java.util.Collection, java.lang.Object, java.util.Collection)
     */
    @Override
    protected void createOccurrence(TopicIF parent, TopicIF type, String value,
            String datatype, Collection<TopicIF> scope, TopicIF reifier,
            Collection<String> iids) throws MIOException {
        if (type == null) {
            throw new MIOException("The occurrence type must not be null");
        }
        if (value == null) {
            throw new MIOException("The occurrence value must not be null");
        }
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
        if (existing != null && existing instanceof TopicIF && !existing.equals(topic)) {
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
    private static LocatorIF _createLocator(String iri) throws MIOException {
        try {
            return new URILocator(iri);
        }
        catch (MalformedURLException ex) {
            throw new MIOException(ex);
        }
    }

    /**
     * Merges the <tt>source</tt> into the <tt>target</tt>.
     *
     * @param source The source topic (will be removed).
     * @param target The target topic.
     */
    private void _merge(TopicIF source, TopicIF target) {
        MergeUtils.mergeInto(target, source);
        super.notifyMerge(source, target);
    }

    /**
     * Sets the scope of the scoped construct.
     *
     * @param scoped The scoped construct.
     * @param scope A collection of topics or <tt>null</tt>.
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
            try {
                reifiable.addItemIdentifier(_createLocator(iid));
            }
            catch (UniquenessViolationException ex) {
                final TMObjectIF existing = reifiable.getTopicMap().getObjectByItemIdentifier(_createLocator(iid));
                if (_mergable(reifiable, existing)) {
                    _merge((ReifiableIF) existing, reifiable);
                }
                else {
                    throw new MIOException(ex);
                }
            }
        }
    }

    /**
     * 
     *
     * @param reifiable
     * @param reifier
     * @throws MIOException 
     */
    private void _applyReifier(ReifiableIF reifiable, TopicIF reifier) throws MIOException {
        if (reifier == null) {
            return;
        }
        if (reifier.getReified() != null) {
            final ReifiableIF existing = reifier.getReified();
            if (existing.equals(reifiable)) {
                return;
            }
            if (_mergable(reifiable, existing)) {
                _merge(existing, reifiable);
            }
            else {
                throw new MIOException("The topic reifies another construct");
            }
        }
        else {
            reifiable.setReifier(reifier);
        }
    }

    private static boolean _mergable(ReifiableIF reifiableA, TMObjectIF tmo) {
        return tmo instanceof ReifiableIF 
                && _mergable(reifiableA, (ReifiableIF) tmo);
    }

    private static boolean _mergable(ReifiableIF reifiableA, ReifiableIF reifiableB) {
        boolean res = reifiableA.getClass().equals(reifiableB.getClass()) 
                        && KeyGenerator.makeKey(reifiableA).equals(KeyGenerator.makeKey(reifiableB));
        if (reifiableA instanceof AssociationRoleIF) {
            res = res && KeyGenerator.makeAssociationKey(((AssociationRoleIF) reifiableA).getAssociation())
                            .equals(KeyGenerator.makeAssociationKey(((AssociationRoleIF) reifiableB).getAssociation()));
        }
        else if (reifiableA instanceof VariantNameIF) {
            final TopicNameIF parentA = ((VariantNameIF) reifiableA).getTopicName();
            final TopicNameIF parentB = ((VariantNameIF) reifiableB).getTopicName();
            res = res && parentA.getTopic().equals(parentB.getTopic())
                      && KeyGenerator.makeTopicNameKey(parentA).equals(KeyGenerator.makeTopicNameKey(parentB));
        }
        return res;
    }

    private void _merge(ReifiableIF source, ReifiableIF target) {
        if (target instanceof AssociationRoleIF && 
                !((AssociationRoleIF) target).getAssociation().equals(((AssociationRoleIF) source).getAssociation())) {
            MergeUtils.mergeInto(((AssociationRoleIF) target).getAssociation(), ((AssociationRoleIF) source).getAssociation()); 
        }
        else {
            MergeUtils.mergeInto(target, source);
        }
    }

    private static final class DelayedEvents {
        private final ReifiableIF _reifiable;
        private final TopicIF _reifier;
        private final Collection<String> _iids;
        public DelayedEvents(ReifiableIF reifiable, TopicIF reifier, Collection<String> iids) {
            _reifiable = reifiable;
            _reifier = reifier;
            _iids = iids;
        }
        public ReifiableIF getReifiable() {
            return _reifiable;
        }
        public TopicIF getReifier() {
            return _reifier;
        }
        public Collection<String> getItemIdentifiers() {
            return _iids;
        }
    }

}
