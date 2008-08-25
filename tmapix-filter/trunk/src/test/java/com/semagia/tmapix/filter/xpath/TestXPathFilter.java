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
 * The Initial Developer of the Original Code is Semagia <http://www.semagia.com/>.
 * All Rights Reserved.
 * 
 * Contributer(s): Lars Heuer <heuer[at]semagia.com>
 *
 */
package com.semagia.tmapix.filter.xpath;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

import com.semagia.tmapix.filter.FilterTestCase;
import com.semagia.tmapix.filter.IFilter;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestXPathFilter extends FilterTestCase {

    public TestXPathFilter(String name) {
        super(name);
    }

    private IFilter<?> xpath(final String xpath) {
        return XPathFilter.create(xpath);
    }

    private void _testThis(final Construct c) throws Exception {
        _testThis(xpath("."), c);
    }

    private void _testParent(final Construct c) throws Exception {
        _testParent(xpath(".."), c);
    }

    private void _testRoles(final Construct c) throws Exception {
        _testRoles(xpath("./role"), c);
    }

    public void testTopicMap() throws Exception {
        _testThis(_tm);
    }

    public void testTopicMapParent() throws Exception {
        _testParent(_tm);
    }

    public void testTopic() throws Exception {
        _testThis(createTopic());
    }

    public void testTopicParent() throws Exception {
        _testParent(createTopic());
    }

    public void testTopicRoles() throws Exception {
        _testRoles(createTopic());
    }

    public void testTopicRoles2() throws Exception {
        final Topic topic = createTopic();
        final Association assoc = createAssociation();
        final Role role = assoc.createRole(createTopic(), topic);
        assertEquals(1, topic.getRolesPlayed().size());
        assertTrue(topic.getRolesPlayed().contains(role));
        _testRoles(topic);
    }

    public void testAssociation() throws Exception {
        _testThis(createAssociation());
    }

    public void testAssociationParent() throws Exception {
        _testParent(createAssociation());
    }

    public void testAssociationRoles() throws Exception {
        _testRoles(createAssociation());
    }

    public void testAssociationRoles2() throws Exception {
        final Association assoc = createAssociation();
        assoc.createRole(createTopic(), createTopic());
        assoc.createRole(createTopic(), createTopic());
        assertEquals(2, assoc.getRoles().size());
        _testRoles(assoc);
    }

    public void testAssociationRoles3() throws Exception {
        final Association assoc = createAssociation();
        final Role role = assoc.createRole(createTopic(), createTopic());
        assoc.createRole(createTopic(), createTopic());
        assertEquals(2, assoc.getRoles().size());
        assertEquals(assoc.getRoles(), asSet((xpath("../role").match(role))));
    }

    public void testRole() throws Exception {
        _testThis(createRole());
    }

    public void testRoleParent() throws Exception {
        _testParent(createRole());
    }
    public void testOccurrence() throws Exception {
        _testThis(createOccurrence());
    }

    public void testOccurrenceParent() throws Exception {
        _testParent(createOccurrence());
    }

    public void testName() throws Exception {
        _testThis(createName());
    }

    public void testNameParent() throws Exception {
        _testParent(createName());
    }

    public void testVariant() throws Exception {
        _testThis(createVariant());
    }

    public void testVariantParent() throws Exception {
        _testParent(createVariant());
    }

    public void testTopicChildren() throws Exception {
        final Topic topic = createTopic();
        final Name name1 = topic.createName("Semagia");
        final Name name2 = topic.createName("semagia");
        final Occurrence occ1 = topic.createOccurrence(createTopic(), "semagia");
        final Occurrence occ2 = topic.createOccurrence(createTopic(), "Semagia");
        Set<Occurrence> occs = new HashSet<Occurrence>();
        occs.add(occ1);
        occs.add(occ2);
        Set<Name> names = new HashSet<Name>();
        names.add(name1);
        names.add(name2);
        Set<Object> all = new HashSet<Object>();
        all.addAll(names);
        all.addAll(occs);
        assertEquals(all, asSet(xpath("./*").match(topic)));
        assertEquals(all, asSet(xpath("child::*").match(topic)));
        assertEquals(names, asSet(xpath("./name").match(topic)));
        assertEquals(names, asSet(xpath("child::name").match(topic)));
        assertEquals(occs, asSet(xpath("./occurrence").match(topic)));
        assertEquals(occs, asSet(xpath("child::occurrence").match(topic)));
        assertEquals(Collections.singleton(occ1), asSet(xpath("child::occurrence[value=\"semagia\"]").match(topic)));
        assertEquals(Collections.singleton(occ1), asSet(xpath("./occurrence[value=\"semagia\"]").match(topic)));
        assertEquals(Collections.singleton(name2), asSet(xpath("child::name[value=\"semagia\"]").match(topic)));
        assertEquals(Collections.singleton(name2), asSet(xpath("./name[value=\"semagia\"]").match(topic)));
        assertEquals(Collections.singleton(name2), asSet(xpath("child::name[value='semagia']").match(topic)));
        assertEquals(Collections.singleton(name2), asSet(xpath("./name[value='semagia']").match(topic)));
    }

    public void testAssociationChildren() throws Exception {
        final Association assoc = _tm.createAssociation(createTopic());
        final Role role1 = assoc.createRole(createTopic(), createTopic());
        final Role role2 = assoc.createRole(createTopic(), createTopic());
        Set<Object> expected = new HashSet<Object>();
        expected.add(role1);
        expected.add(role2);
        assertEquals(expected, asSet(xpath("./*").match(assoc)));
        assertEquals(expected, asSet(xpath("child::*").match(assoc)));
        assertEquals(expected, asSet(xpath("child::role").match(assoc)));
    }

    public void testTopicMapChildren() throws Exception {
        final Topic topic = createTopic();
        final Topic topic2 = createTopic();
        final Topic topic3 = createTopic();
        final Association assoc = _tm.createAssociation(topic3);
        Set<Topic> topics = new HashSet<Topic>();
        topics.add(topic);
        topics.add(topic2);
        topics.add(topic3);
        Set<Association> assocs = new HashSet<Association>();
        assocs.add(assoc);
        Set<Object> all = new HashSet<Object>();
        all.addAll(topics);
        all.addAll(assocs);
        assertEquals(all, asSet(xpath("./*").match(_tm)));
        assertEquals(all, asSet(xpath("child::*").match(_tm)));
        assertEquals(topics, asSet(xpath("./topic").match(_tm)));
        assertEquals(topics, asSet(xpath("child::topic").match(_tm)));
        assertEquals(assocs, asSet(xpath("./association").match(_tm)));
        assertEquals(assocs, asSet(xpath("child::association").match(_tm)));
    }

    public void testSubjectIdentifier() throws Exception {
        String ref = "http://www.semagia.com/";
        Topic topic = _tm.createTopicBySubjectIdentifier(createLocator(ref));
        IFilter<?> filter = xpath("sid('" + ref + "')");
        Set<Object> result = asSet(filter.match(_tm));
        assertEquals(1, result.size());
        assertTrue(result.contains(topic));
    }

    public void testSubjectLocator() throws Exception {
        String ref = "http://www.semagia.com/";
        Topic topic = _tm.createTopicBySubjectLocator(createLocator(ref));
        IFilter<?> filter = xpath("slo('" + ref + "')");
        Set<Object> result = asSet(filter.match(_tm));
        assertEquals(1, result.size());
        assertTrue(result.contains(topic));
    }

    public void testItemIdentifier() throws Exception {
        String ref = "http://www.semagia.com/";
        Topic topic = _tm.createTopicByItemIdentifier(createLocator(ref));
        IFilter<?> filter = xpath("iid('" + ref + "')");
        Set<Object> result = asSet(filter.match(_tm));
        assertEquals(1, result.size());
        assertTrue(result.contains(topic));
    }

    public void testInUCS() throws Exception {
        final Name name = createName();
        IFilter<?> filter = xpath(".[in-ucs(.)]");
        Set<Object> result = asSet(filter.match(name));
        assertEquals(1, result.size());
        assertTrue(result.contains(name));
    }

    public void testInUCS2() throws Exception {
        final Name name = createName();
        IFilter<?> filter = xpath("in-ucs(.)");
        Set<Object> result = asSet(filter.match(name));
        assertEquals(1, result.size());
        assertTrue(result.contains(true));
    }

    public void testInUCS3() throws Exception {
        final Name name = createName();
        IFilter<?> filter = xpath(".[count(./scope) = 0]");
        Set<Object> result = asSet(filter.match(name));
        assertEquals(1, result.size());
        assertTrue(result.contains(name));
    }

    public void testAtomifiyTopic() throws Exception {
        final Topic topic = createTopic();
        topic.createName("Semagia");
        IFilter<?> filter = xpath("atomify(.)");
        assertEquals("Semagia", filter.matchOne(topic));
    }

    public void testAllOccurrences() throws Exception {
        String ref = "http://www.semagia.com/";
        final Topic occType = _tm.createTopicBySubjectIdentifier(createLocator(ref));
        assertEquals(occType, _tm.getTopicBySubjectIdentifier(createLocator(ref)));
        final Topic occType2 = _tm.createTopicBySubjectIdentifier(createLocator(ref+ "#1"));
        final Topic topic1 = createTopic();
        final Topic topic2 = createTopic();
        final Occurrence occ1 = topic1.createOccurrence(occType, "occ1");
        final Occurrence occ2 = topic2.createOccurrence(occType, "occ2");
        final Occurrence occ3 = topic2.createOccurrence(occType2, "occ3");
        IFilter<?> filter = xpath("//occurrence");
        Set<Occurrence> result = new HashSet<Occurrence>(Arrays.asList(occ1, occ2, occ3));
        assertEquals(result, asSet(filter.match(_tm)));
        filter = xpath("//occurrence/type");
        Set<Topic> types = new HashSet<Topic>(Arrays.asList(occType, occType2));
        assertEquals(types, asSet(filter.match(_tm)));
        result.remove(occ1);
        filter = xpath("//occurrence[./value = 'occ2']");
        assertEquals(Collections.singleton(occ2), asSet(filter.match(_tm)));
        result.clear();
        result.add(occ1);
        result.add(occ2);
        filter = xpath("//occurrence[sid('" + ref + "') = ./type]");
        assertEquals(result, asSet(filter.match(_tm)));
    }

}
