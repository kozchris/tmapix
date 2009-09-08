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
package org.tmapix.io;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.tmapi.core.Association;
import org.tmapi.core.DatatypeAware;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;
import org.tmapi.core.Typed;
import org.tmapi.core.Variant;

/**
 * Abstract topic map writer useful to write textual topic maps like CTM or
 * LTM.
 * <p>
 * This class provides mainly access to some default comparators.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
abstract class AbstractTextualTopicMapWriter implements TopicMapWriter {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTextualTopicMapWriter.class);

    protected final Writer _out;
    private char[] _indent;

    private final Comparator<Topic> _topicIdComparator;
    private final Comparator<Topic> _topicComparator;
    private final Comparator<Association> _associationComparator;
    private final Comparator<Role> _roleComparator;
    private final Comparator<Occurrence> _occurrenceComparator;
    private final Comparator<Name> _nameComparator;
    private final Comparator<Variant> _variantComparator;
    private final Comparator<Collection<Topic>> _topicCollectionComparator;

    protected AbstractTextualTopicMapWriter(Writer out) {
        _out = out;
        _topicIdComparator = new TopicIdComparator();
        _topicComparator = new TopicComparator();
        _associationComparator = new AssociationComparator();
        _roleComparator = new RoleComparator();
        _occurrenceComparator = new OccurrenceComparator();
        _nameComparator = new NameComparator();
        _variantComparator = new VariantComparator();
        _topicCollectionComparator = new TopicCollectionComparator();
        setIdentation(4);
    }

    /**
     * Sets the identation level, by default the identation level is set to 4
     * which means that four whitespace characters are written.
     * <p>
     * If the size is set to <tt>0</tt>, no identation will be done.
     * </p>
     * @param level The identation level.
     */
    public void setIdentation(int level) {
        if (_indent == null || _indent.length != level) {
            _indent = new char[level];
            Arrays.fill(_indent, ' ');
        }
    }

    /**
     * Returns the identation level.
     *
     * @return The number of whitespaces which are written in front of a 
     *          statement within a topic block.
     */
    public int getIdentation() {
        return _indent.length;
    }

    /**
     * Returns a comparator that compares topic identifiers.
     *
     * @return A comparator.
     */
    protected Comparator<Topic> getTopicIdComparator() {
        return _topicIdComparator;
    }

    /**
     * Returns a compartor that compares topics.
     *
     * @return A topic comparator.
     */
    protected Comparator<Topic> getTopicComparator() {
        return _topicComparator;
    }

    /**
     * Returns a comparator that compares associations.
     *
     * @return An association comparator.
     */
    protected Comparator<Association> getAssociationComparator() {
        return _associationComparator;
    }

    /**
     * Returns a comparator that compares roles.
     *
     * @return A role comparator.
     */
    protected Comparator<Role> getRoleComparator() {
        return _roleComparator;
    }

    /**
     * Returns a comparator that compares occurrences.
     *
     * @return An occurrence comparator.
     */
    protected Comparator<Occurrence> getOccurrenceComparator() {
        return _occurrenceComparator;
    }

    /**
     * Returns a comparator that compares names.
     *
     * @return A name comparator.
     */
    protected Comparator<Name> getNameComparator() {
        return _nameComparator;
    }

    /**
     * Returns a comparator that compares variants.
     *
     * @return A variant comparator.
     */
    protected Comparator<Variant> getVariantComparator() {
        return _variantComparator;
    }

    /**
     * Returns a reference to the provided topic.
     * <p>
     * The reference to the topic must stay stable during the serialization of
     * the topic map.
     * </p>
     *
     * @param topic The topic to retrieve a reference for.
     * @return A reference to the specified topic.
     */
    protected abstract Comparable<?> getTopicReference(final Topic topic);

    /**
     * Returns the topic with the subject identifier 
     * <tt>http://psi.topicmaps.org/iso13250/model/topic-name</tt>.
     * <p>
     * Note: This method is invoked after the invocation of 
     * {@link #write(org.tmapi.core.TopicMap)}, at least from this class.
     * </p>
     *
     * @return The topic which represents the default name type or <tt>null</tt> 
     *          if no such topic exists.
     */
    protected abstract Topic getDefaultNameType();

    /**
     * Writes the identation.
     *
     * @throws IOException In case of an error.
     */
    protected void indent() throws IOException {
        _out.write(_indent);
    }

    /**
     * Writes a line break.
     *
     * @throws IOException In case of an error.
     */
    protected void newline() throws IOException {
        _out.write('\n');
    }


    /*
     * Comparators
     */

    /**
     * Compares topics by their id.
     */
    private class TopicIdComparator implements Comparator<Topic> {

        public TopicIdComparator() {
        }

        @SuppressWarnings("unchecked")
        @Override
        public int compare(Topic o1, Topic o2) {
            final Comparable comp1 = getTopicReference(o1);
            final Comparable comp2 = getTopicReference(o2);
            return comp1.compareTo(comp2);
        }
    }

    private class TopicComparator implements Comparator<Topic> {

        @SuppressWarnings("unchecked")
        @Override
        public int compare(Topic o1, Topic o2) {
            int res = _topicCollectionComparator.compare(o1.getTypes(), o2.getTypes());
            if (res == 0) {
                final Comparable comp1 = getTopicReference(o1);
                final Comparable comp2 = getTopicReference(o2);
                return comp1.compareTo(comp2);
            }
            return res;
        }
    }

    /**
     * Abstract comparator that provides some utility methods which handle common 
     * comparisons.
     */
    private abstract class AbstractComparator<T> implements Comparator<T> {
        int compareString(String o1, String o2) {
            if (o1 == null && o2 != null) {
                LOG.debug("The first string value is null");
                return -1;
            }
            if (o1 != null && o2 == null) {
                LOG.debug("The second string value is null");
                return +1;
            }
            return o1.compareTo(o2);
        }
        /**
         * Extracts the type of the typed Topic Maps constructs and compares
         * the topics.
         *
         * @param o1 The first typed Topic Maps construct.
         * @param o2 The second typed Topic Maps construct.
         * @return A negative integer, zero, or a positive integer as the 
         *          first argument is less than, equal to, or greater than the 
         *          second.
         */
        int compareType(Typed o1, Typed o2) {
            return _topicIdComparator.compare(o1.getType(), o2.getType());
        }
        /**
         * Extracts the scope of the scoped Topic Maps constructs and compares
         * them.
         *
         * @param o1 The first scoped Topic Maps construct.
         * @param o2 The second scoped Topic Maps construct.
         * @return A negative integer, zero, or a positive integer as the 
         *          first argument is less than, equal to, or greater than the 
         *          second.
         */
        int compareScope(Scoped o1, Scoped o2) {
            return _topicCollectionComparator.compare(o1.getScope(), o2.getScope());
        }
        /**
         * Extracts the reifier of the reifiable Topic Maps constructs and 
         * compares them.
         *
         * @param o1 The first reifiable Topic Maps construct.
         * @param o2 The second reifiable Topic Maps construct.
         * @return A negative integer, zero, or a positive integer as the 
         *          first argument is less than, equal to, or greater than the 
         *          second.
         */
        int compareReifier(Reifiable o1, Reifiable o2) {
            Topic reifier1 = o1.getReifier();
            Topic reifier2 = o2.getReifier();
            if (reifier1 == reifier2) {
                return 0;
            }
            int res = 0;
            if (reifier1 == null) {
                res = reifier2 == null ? 0 : -1;
            }
            else if (reifier2 == null) {
                res = 1;
            }
            return res != 0 ? res : _topicIdComparator.compare(reifier1, reifier2);
        }
    }

    /**
     * Enhances the {@link AbstractComparator} with a method to compare the
     * value and datatype of an occurrence or variant.
     */
    private abstract class AbstractDatatypeAwareComparator<T extends DatatypeAware> extends AbstractComparator<T> {
        /**
         * Compares the value and datatype of the occurrences / variants.
         *
         * @param o1 The first occurrence / variant.
         * @param o2 The second occurrence / variant.
         * @return A negative integer, zero, or a positive integer as the 
         *          first argument is less than, equal to, or greater than the 
         *          second.
         */
        protected int compareValueDatatype(DatatypeAware o1, DatatypeAware o2) {
            int res = o1.getDatatype().getReference().compareTo(o2.getDatatype().getReference());
            if (res == 0) {
                res = o1.getValue().compareTo(o2.getValue());
            }
            return res;
        }
    }

    /**
     * Association comparator.
     * 
     */
    private final class AssociationComparator extends AbstractComparator<Association> {

        private Comparator<Collection<Role>> _roleSetComparator;

        AssociationComparator() {
            _roleSetComparator = new RolesComparator();
        }

        @Override
        public int compare(Association o1, Association o2) {
            if (o1 == o2) {
                return 0;
            }
            int res = compareType(o1, o2);
            if (res == 0) {
                res = _roleSetComparator.compare(o1.getRoles(), o2.getRoles());
                if (res == 0) {
                    res = compareScope(o1, o2);
                }
            }
            return res;
        }
    }

    /**
     * Role comparator which ignores the parent association. This comparator
     * is meant to be used for roles where the parent is known to be equal or
     * unequal.
     */
    private class RoleComparator extends AbstractComparator<Role> {

        @Override
        public int compare(Role o1, Role o2) {
            if (o1 == o2) {
                return 0;
            }
            int res = compareType(o1, o2);
            if (res == 0) {
                res = _topicIdComparator.compare(o1.getPlayer(), o2.getPlayer());
            }
            return res;
        }
    }

    /**
     * Occurrence comparator.
     * - Occs in the UCS are less than ones with a special scope.
     * - Occs which are not reified are less than ones which are reified.
     */
    protected class OccurrenceComparator extends AbstractDatatypeAwareComparator<Occurrence> {

        @Override
        public int compare(Occurrence o1, Occurrence o2) {
            if (o1 == o2) {
                return 0;
            }
            int res = compareType(o1, o2);
            if (res == 0) {
                res = compareScope(o1, o2);
                if (res == 0) {
                    res = compareReifier(o1, o2);
                    if (res == 0) {
                        res = compareValueDatatype(o1, o2);
                    }
                }
            }
            return res;
        }
    }

    /**
     * Name comparator.
     * - Names with the default name type are less than names with a non-standard type.
     * - Names in the UCS are less than ones with a special scope.
     * - Names with no variants are less than ones with variants
     * - Names which are not reified are less than ones which are reified.
     */
    private final class NameComparator extends AbstractComparator<Name> {

        @Override
        public int compare(Name o1, Name o2) {
            if (o1 == o2) {
                return 0;
            }
            int res = compareType(o1, o2);
            if (res == 0) {
                res = compareScope(o1, o2);
                if (res == 0) {
                    res = o1.getVariants().size() - o2.getVariants().size();
                    if (res == 0) {
                        res = compareReifier(o1, o2);
                        if (res == 0) {
                            res = compareString(o1.getValue(), o2.getValue());
                        }
                    }
                }
            }
            return res;
        }

        @Override
        int compareType(Typed o1, Typed o2) {
            final Topic type1 = o1.getType();
            final Topic type2 = o2.getType();
            final Topic defaultNameType = getDefaultNameType();
            int res = 0;
            if (defaultNameType != null) {
                if (type1.equals(defaultNameType)) {
                    res = type2.equals(type1) ? 0 : -1;
                }
                else if (type2.equals(getDefaultNameType())) {
                    res = 1;
                }
            }
            return res != 0 ? res : super.compareType(o1, o2);
        }
    }

    /**
     * Variant comparator.
     * - Variants with a lesser scope size are less.
     * - Variants which are not reified are less than ones which are reified.
     */
    protected class VariantComparator extends AbstractDatatypeAwareComparator<Variant> {

        @Override
        public int compare(Variant o1, Variant o2) {
            if (o1 == o2) {
                return 0;
            }
            int res = compareScope(o1, o2);
            if (res == 0) {
                res = compareReifier(o1, o2);
                if (res == 0) {
                    res = compareValueDatatype(o1, o2);
                }
            }
            return res;
        }
    }

    /**
     * Comparator which compares the size of the provided set.
     * 
     * Iff the size of the sets are equal, another comparison method is used
     * to compare the content of the sets.
     */
    private abstract class AbstractCollectionComparator<T> implements Comparator<Collection<T>> {

        @Override
        public int compare(Collection<T> o1, Collection<T> o2) {
            final int s1 = o1.size();
            final int s2 = o2.size();
            int res = s1 - s2;
            if (res == 0) {
                res = compareContent(o1, o2, s1);
            }
            return res;
        }

        /**
         * Called iff the size of the collections is equal.
         * 
         * This method is used to compare the content of the collections.
         *
         * @param o1 The first collection.
         * @param o2 The second collection.
         * @param size The size of the collection(s).
         * @return A negative integer, zero, or a positive integer as the 
         *          first argument is less than, equal to, or greater than the 
         *          second.
         */
        abstract int compareContent(Collection<T> o1, Collection<T> o2, int size);
    }

    /**
     * Compares role sets. The parent of the roles is ignored! 
     */
    private final class RolesComparator extends AbstractCollectionComparator<Role> {

        private RoleComparator _roleCmp; 

        RolesComparator() {
            _roleCmp = new RoleComparator();
        }

        @Override
        int compareContent(Collection<Role> o1, Collection<Role> o2, int size) {
            int res = 0;
            Role[] roles1 = o1.toArray(new Role[size]);
            Role[] roles2 = o2.toArray(new Role[size]);
            Arrays.sort(roles1, _roleCmp);
            Arrays.sort(roles2, _roleCmp);
            for (int i=0; i < size && res == 0; i++) {
                res = _roleCmp.compare(roles1[i], roles2[i]);
            }
            return res;
        }
    }

    /**
     * Compares the the size and maybe the content of two collections of topics.
     */
    private final class TopicCollectionComparator extends AbstractCollectionComparator<Topic> {

        @Override
        int compareContent(Collection<Topic> o1, Collection<Topic> o2, int size) {
            int res = 0 ;
            Topic[] topics1 = o1.toArray(new Topic[size]);
            Topic[] topics2 = o2.toArray(new Topic[size]);
            Arrays.sort(topics1, _topicIdComparator);
            Arrays.sort(topics2, _topicIdComparator);
            for (int i=0; i < size && res == 0; i++) {
                res = _topicIdComparator.compare(topics1[i], topics2[i]);
            }
            return res;
        }
    }

}
