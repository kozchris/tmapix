/*
 * Copyright 2009 Lars Heuer (heuer[at]semagia.com). All rights reserved.
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
package org.tmapix.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.IdentityConstraintException;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.Variant;
import org.tmapix.io.internal.utils.MergeUtils;
import org.tmapix.io.internal.utils.SignatureGenerator;

import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.AbstractHamsterMapHandler;
import com.semagia.mio.voc.TMDM;
import com.semagia.mio.voc.XSD;

/**
 * {@link com.semagia.mio.IMapHandler} implementation that works upon any
 * TMAPI 2.0 compatible Topic Maps engine.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class TMAPIMapHandler extends AbstractHamsterMapHandler<Topic> {

    /**
     * Represents the unconstrained scope.
     */
    private final static Collection<Topic> _UCS = Collections.emptySet();
    
    private TopicMap _tm;
    private final Locator _defaultNameType;
    private Collection<DelayedRoleEvents> _delayedRoleEvents;

    /**
     * 
     *
     * @param topicMap
     */
    public TMAPIMapHandler(TopicMap topicMap) {
        _tm = topicMap;
        _defaultNameType = _tm.createLocator(TMDM.TOPIC_NAME);
        _delayedRoleEvents = new ArrayList<DelayedRoleEvents>();
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.io.HamsterHandler#createTopicByItemIdentifier(java.lang.String)
     */
    @Override
    protected Topic createTopicByItemIdentifier(String iri) {
        return _tm.createTopicByItemIdentifier(_createLocator(iri));
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.io.HamsterHandler#createTopicBySubjectIdentifier(java.lang.String)
     */
    @Override
    protected Topic createTopicBySubjectIdentifier(String iri) {
        return _tm.createTopicBySubjectIdentifier(_createLocator(iri));
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.io.HamsterHandler#createTopicBySubjectLocator(java.lang.String)
     */
    @Override
    protected Topic createTopicBySubjectLocator(String iri) {
        return _tm.createTopicBySubjectLocator(_createLocator(iri));
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.io.HamsterHandler#handleTypeInstance(java.lang.Object, java.lang.Object)
     */
    @Override
    protected void handleTypeInstance(Topic instance, Topic type) {
        instance.addType(type);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.io.HamsterHandler#handleItemIdentifier(java.lang.Object, java.lang.String)
     */
    @Override
    protected void handleItemIdentifier(Topic topic, String iri) throws MIOException {
        final Locator iid = _createLocator(iri);
        final Construct existing = _tm.getConstructByItemIdentifier(iid); 
        if (existing != null) {
            if (existing instanceof Topic) {
                if (!existing.equals(topic)) {
                    _merge(topic, (Topic) existing);
                    topic = (Topic) existing;
                }
                return;
            }
            else {
                throw new MIOException("The item identifier '" + iri + "' is already assigned to another construct");
            }
        }
        final Topic existingTopic = _tm.getTopicBySubjectIdentifier(iid);
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
    protected void handleSubjectIdentifier(Topic topic, String iri)
            throws MIOException {
        final Locator sid = _createLocator(iri);
        Topic existingTopic = _tm.getTopicBySubjectIdentifier(sid);
        if (existingTopic != null) {
            if (!existingTopic.equals(topic)) {
                _merge(topic, existingTopic);
            }
            return;
        }
        final Construct existing = _tm.getConstructByItemIdentifier(sid);
        if (existing != null && existing instanceof Topic) {
            existingTopic = (Topic) existing;
            _merge(topic, existingTopic);
            topic = existingTopic;
        }
        topic.addSubjectIdentifier(sid);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.io.HamsterHandler#handleSubjectLocator(java.lang.Object, java.lang.String)
     */
    @Override
    protected void handleSubjectLocator(Topic topic, String iri)
            throws MIOException {
        final Locator slo = _createLocator(iri);
        final Topic existingTopic = _tm.getTopicBySubjectLocator(slo);
        if (existingTopic != null && !existingTopic.equals(topic)) {
            _merge(topic, existingTopic);
            return;
        }
        topic.addSubjectLocator(slo);
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
    protected void handleTopicMapReifier(Topic reifier) throws MIOException {
        _tm.setReifier(reifier);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.io.HamsterHandler#createAssociation(java.lang.Object, java.util.Collection, java.lang.Object, java.util.Collection, java.util.Collection)
     */
    @Override
    protected void createAssociation(Topic type, Collection<Topic> scope, Topic reifier,
            Collection<String> iids, Collection<IRole<Topic>> roles)
            throws MIOException {
        Association assoc = _tm.createAssociation(type, _scope(scope));
        for (IRole<Topic> r: roles) {
            Role role = assoc.createRole(r.getType(), r.getPlayer());
            if (!r.getItemIdentifiers().isEmpty() || r.getReifier() != null) {
                _delayedRoleEvents.add(new DelayedRoleEvents(role, r.getReifier(), r.getItemIdentifiers()));
            }
        }
        _applyItemIdentifiers(assoc, iids);
        _applyReifier(assoc, reifier);
        if (!_delayedRoleEvents.isEmpty()) {
            for (DelayedRoleEvents evt: _delayedRoleEvents) {
                _applyReifier(evt.getRole(), evt.getReifier());
                _applyItemIdentifiers(evt.getRole(), evt.getItemIdentifiers());
            }
            _delayedRoleEvents.clear();
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.io.HamsterHandler#createName(java.lang.Object, java.lang.Object, java.lang.String, java.util.Collection, java.lang.Object, java.util.Collection, java.util.Collection)
     */
    @Override
    protected void createName(Topic parent, Topic type, String value,
            Collection<Topic> scope, Topic reifier, Collection<String> iids,
            Collection<IVariant<Topic>> variants) throws MIOException {
        Name name = parent.createName(type != null ? type : _defaultNameType(), value, _scope(scope));
        _applyItemIdentifiers(name, iids);
        _applyReifier(name, reifier);
        final Set<Topic> nameScope = name.getScope();
        for (IVariant<Topic> v: variants) {
            if (nameScope.containsAll(v.getScope())) {
                throw new MIOException("The variant's scope is not a superset of the name's scope");
            }
            Variant variant = null;
            final String datatype = v.getDatatype();
            if (XSD.ANY_URI.equals(datatype)) {
                variant = name.createVariant(_createLocator(v.getValue()), v.getScope());
            }
            else if (XSD.STRING.equals(datatype)) {
                variant = name.createVariant(v.getValue(), v.getScope());
            }
            else {
                variant = name.createVariant(v.getValue(), _createLocator(datatype), v.getScope());
            }
            _applyItemIdentifiers(variant, v.getItemIdentifiers());
            _applyReifier(variant, v.getReifier());
        }
    }

    /**
     * 
     *
     * @return
     */
    private Topic _defaultNameType() {
        return _tm.createTopicBySubjectIdentifier(_defaultNameType);
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.io.HamsterHandler#createOccurrence(java.lang.Object, java.lang.Object, java.lang.String, java.lang.String, java.util.Collection, java.lang.Object, java.util.Collection)
     */
    @Override
    protected void createOccurrence(Topic parent, Topic type, String value,
            String datatype, Collection<Topic> scope, Topic reifier,
            Collection<String> iids) throws MIOException {
        Occurrence occ = null;
        if (XSD.ANY_URI.equals(datatype)) {
            occ = parent.createOccurrence(type, _createLocator(value), _scope(scope));
        }
        else if (XSD.STRING.equals(datatype)) {
            occ = parent.createOccurrence(type, value, _scope(scope));
        }
        else {
            occ = parent.createOccurrence(type, value, _createLocator(datatype), _scope(scope));
        }
        _applyItemIdentifiers(occ, iids);
        _applyReifier(occ, reifier);
    }

    /**
     * Returns the provided collection or an empty collection if <tt>scope</tt> 
     * is <tt>null</tt>.
     *
     * @param scope A collection of topics or <tt>null</tt>.
     * @return A collection of topics.
     */
    private Collection<Topic> _scope(Collection<Topic> scope) {
        return scope != null ? scope : _UCS;
    }

    /**
     * Creates a locator with the specified address.
     *
     * @param iri The IRI.
     * @return A locator.
     */
    private Locator _createLocator(String iri) {
        return _tm.createLocator(iri);
    }

    /**
     * 
     *
     * @param reifiable
     * @param iids
     * @throws MIOException 
     */
    private void _applyItemIdentifiers(Reifiable reifiable, Iterable<String> iids) throws MIOException {
        for (String iid: iids) {
            try {
                reifiable.addItemIdentifier(_createLocator(iid));
            }
            catch (IdentityConstraintException ex) {
                final Construct existing = ex.getExisting();
                if (_mergable(reifiable, existing)) {
                    _merge((Reifiable) existing, reifiable);
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
    private void _applyReifier(Reifiable reifiable, Topic reifier) throws MIOException {
        if (reifier == null) {
            return;
        }
        try {
            reifiable.setReifier(reifier);
        }
        catch (ModelConstraintException ex) {
            final Reifiable existingReifiable = reifier.getReified();
            if (_mergable(reifiable, existingReifiable)) {
                _merge(existingReifiable, reifiable);
            }
            else {
                throw new MIOException(ex);
            }
        }
    }

    private boolean _mergable(Reifiable reifiable, Construct construct) {
        return construct instanceof Reifiable 
                && _mergable(reifiable, (Reifiable) construct);
    }

    private boolean _mergable(Reifiable reifiableA, Reifiable reifiableB) {
        boolean res = (reifiableA.getClass().equals(reifiableB.getClass())
                        && SignatureGenerator.generateSignature(reifiableA)
                            .equals(SignatureGenerator.generateSignature(reifiableB))
                        );
        if (res && reifiableA instanceof Role) {
            res = SignatureGenerator.generateSignature((Reifiable) reifiableA.getParent())
                            .equals(SignatureGenerator.generateSignature((Reifiable) reifiableB.getParent()));
        }
        if (res && reifiableA instanceof Variant) {
            Name parentA = (Name) reifiableA.getParent();
            Name parentB = (Name) reifiableB.getParent();
            res = parentA.getParent().equals(parentB.getParent())
                        && SignatureGenerator.generateSignature(parentA).equals(SignatureGenerator.generateSignature(parentB));
        }
        return res;
    }

    private void _merge(Reifiable source, Reifiable target) {
        MergeUtils.merge(source, target);
    }

    /**
     * 
     *
     * @param source
     * @param target
     */
    private void _merge(Topic source, Topic target) {
        target.mergeIn(source);
        super.notifyMerge(source, target);
    }

    private static final class DelayedRoleEvents {
        private final Role _role;
        private final Topic _reifier;
        private final Collection<String> _iids;
        public DelayedRoleEvents(Role role, Topic reifier, Collection<String> iids) {
            _role = role;
            _reifier = reifier;
            _iids = iids;
        }
        public Role getRole() {
            return _role;
        }
        public Topic getReifier() {
            return _reifier;
        }
        public Collection<String> getItemIdentifiers() {
            return _iids;
        }
    }

}
