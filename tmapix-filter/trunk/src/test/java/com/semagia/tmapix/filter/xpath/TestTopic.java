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

import java.util.List;

import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

import com.semagia.tmapix.filter.IFilter;

/**
 * {@link org.tmapi.core.Topic} related tests.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class TestTopic extends XPathTestCase {

    public TestTopic(String name) {
        super(name);
    }

    public void testTypeInstanceAxis() {
        final Topic topic = createTopic();
        final Topic type = createTopic();
        final IFilter<Topic> typeFilter = xpath("type");
        final IFilter<Topic> instanceFilter = xpath("instance");
        List<Topic> result = asList(typeFilter.match(topic));
        assertEquals(0, result.size());
        result = asList(instanceFilter.match(type));
        assertEquals(0, result.size());
        topic.addType(type);
        result = asList(typeFilter.match(topic));
        assertEquals(1, result.size());
        assertTrue(result.contains(type));
        result = asList(instanceFilter.match(type));
        assertEquals(1, result.size());
        assertTrue(result.contains(topic));
    }

    public void testSubjectIdentifierAxis() {
        final Topic topic = createTopic();
        final IFilter<Locator> filter = xpath("sid");
        List<Locator> result = asList(filter.match(topic));
        assertEquals(0, result.size());
        assertNull(filter.matchOne(topic));
        final Locator sid1 = createLocator("http://www.semagia.com/1");
        final Locator sid2 = createLocator("http://www.semagia.com/2");
        topic.addSubjectIdentifier(sid1);
        result = asList(filter.match(topic));
        assertEquals(1, result.size());
        assertTrue(result.contains(sid1));
        topic.addSubjectIdentifier(sid2);
        result = asList(filter.match(topic));
        assertEquals(2, result.size());
        assertTrue(result.contains(sid1));
        assertTrue(result.contains(sid2));
    }

    public void testSubjectLocatorAxis() {
        final Topic topic = createTopic();
        final IFilter<Locator> filter = xpath("slo");
        List<Locator> result = asList(filter.match(topic));
        assertEquals(0, result.size());
        assertNull(filter.matchOne(topic));
        final Locator slo1 = createLocator("http://www.semagia.com/1");
        final Locator slo2 = createLocator("http://www.semagia.com/2");
        topic.addSubjectLocator(slo1);
        result = asList(filter.match(topic));
        assertEquals(1, result.size());
        assertTrue(result.contains(slo1));
        topic.addSubjectLocator(slo2);
        result = asList(filter.match(topic));
        assertEquals(2, result.size());
        assertTrue(result.contains(slo1));
        assertTrue(result.contains(slo2));
    }

    public void testRoleAxis() {
        final Topic topic = createTopic();
        final Locator sid1 = createLocator("http://www.semagia.com/1");
        final Locator sid2 = createLocator("http://www.semagia.com/2");
        final Topic roleType = _tm.createTopicBySubjectIdentifier(sid1);
        final Topic roleType2 = _tm.createTopicBySubjectIdentifier(sid2);
        final Association assoc = createAssociation();
        final Association assoc2 = createAssociation();
        IFilter<Role> filter = xpath("role");
        List<Role> result = asList(filter.match(topic));
        assertEquals(0, result.size());
        final Role role = assoc.createRole(roleType, topic);
        result = asList(filter.match(topic));
        assertEquals(1, result.size());
        assertTrue(result.contains(role));
        final Role role2 = assoc2.createRole(roleType2, topic);
        result = asList(filter.match(topic));
        assertEquals(2, result.size());
        assertTrue(result.contains(role));
        assertTrue(result.contains(role2));
        filter = xpath("role[type=sid('" + sid1.getReference() + "')]");
        result = asList(filter.match(topic));
        assertEquals(1, result.size());
        assertTrue(result.contains(role));
        filter = xpath("role[type=sid('" + sid1.getReference() + "') or type=sid('" + sid2.getReference() + "')]");
        result = asList(filter, topic);
        assertEquals(2, result.size());
        assertTrue(result.contains(role));
        assertTrue(result.contains(role2));
        IFilter<Association> parentFilter = xpath("role/..");
        List<Association> parentResult = asList(parentFilter, topic);
        assertEquals(2, parentResult.size());
        assertTrue(parentResult.contains(assoc));
        assertTrue(parentResult.contains(assoc2));
    }

    public void testOccurrenceAxis() {
        final Topic topic = createTopic();
        final Locator sid1 = createLocator("http://www.semagia.com/1");
        final Locator sid2 = createLocator("http://www.semagia.com/2");
        final Topic occType1 = _tm.createTopicBySubjectIdentifier(sid1);
        final Topic occType2 = _tm.createTopicBySubjectIdentifier(sid2);
        IFilter<Occurrence> filter = xpath("occurrence");
        List<Occurrence> result = asList(filter, topic);
        assertEquals(0, result.size());
        final Occurrence occ1 = topic.createOccurrence(occType1, "Occurrence");
        result = asList(filter, topic);
        assertEquals(1, result.size());
        assertTrue(result.contains(occ1));
        final Occurrence occ2 = topic.createOccurrence(occType2, createLocator("http://www.semagia.com/3"));
        result = asList(filter, topic);
        assertEquals(2, result.size());
        assertTrue(result.contains(occ1));
        assertTrue(result.contains(occ2));
        filter = xpath("occurrence[type = sid('" + sid1.getReference() + "')]");
        result = asList(filter, topic);
        assertEquals(1, result.size());
        assertTrue(result.contains(occ1));
        filter = xpath("occurrence[datatype = xsd:string]");
        result = asList(filter, topic);
        assertEquals(1, result.size());
        assertTrue(result.contains(occ1));
        //TODO: The following is currently legal, not sure if it should
        filter = xpath("occurrence[xsd:string]");
        result = asList(filter, topic);
        assertEquals(1, result.size());
        assertTrue(result.contains(occ1));
        filter = xpath("occurrence[datatype = xsd:anyURI]");
        result = asList(filter, topic);
        assertEquals(1, result.size());
        assertTrue(result.contains(occ2));
        //TODO: The following is currently legal, not sure if it should
        filter = xpath("occurrence[xsd:anyURI]");
        result = asList(filter, topic);
        assertEquals(1, result.size());
        assertTrue(result.contains(occ2));
        filter = xpath("occurrence[value = 'Occurrence']");
        result = asList(filter, topic);
        assertEquals(1, result.size());
        assertTrue(result.contains(occ1));
        filter = xpath("occurrence[value = 'http://www.semagia.com/3']");
        result = asList(filter, topic);
        assertEquals(1, result.size());
        assertTrue(result.contains(occ2));
        filter = xpath("occurrence[value = 'http://www.semagia.com/3' and datatype=xsd:string]");
        result = asList(filter, topic);
        assertEquals(0, result.size());
        filter = xpath("occurrence[value = 'http://www.semagia.com/3' and datatype=xsd:anyURI]");
        result = asList(filter, topic);
        assertEquals(1, result.size());
        assertTrue(result.contains(occ2));
        filter = xpath("occurrence[value = 'http://www.semagia.com/3' and datatype='http://www.w3.org/2001/XMLSchema#anyURI']");
        result = asList(filter, topic);
        assertEquals(1, result.size());
        assertTrue(result.contains(occ2));
        IFilter<String> filterValue = xpath("occurrence[datatype=xsd:anyURI]/value");
        assertEquals("http://www.semagia.com/3", filterValue.matchOne(topic));
        filterValue = xpath("occurrence[datatype=xsd:string]/value");
        assertEquals("Occurrence", filterValue.matchOne(topic));
    }

    public void testNameAxis() {
        final Topic topic = createTopic();
        IFilter<Name> filter = xpath("name");
        List<Name> result = asList(filter, topic);
        assertEquals(0, result.size());
        final Name name = topic.createName("Semagia");
        result = asList(filter, topic);
        assertEquals(1, result.size());
        assertTrue(result.contains(name));
        final Name name2 = topic.createName("Name2");
        result = asList(filter, topic);
        assertEquals(2, result.size());
        assertTrue(result.contains(name));
        assertTrue(result.contains(name2));
        filter = xpath("name[value='Semagia']");
        result = asList(filter, topic);
        assertEquals(1, result.size());
        assertTrue(result.contains(name));
        filter = xpath("name[count(./variant) = 0]");
        result = asList(filter, topic);
        assertEquals(2, result.size());
        assertTrue(result.contains(name));
        assertTrue(result.contains(name2));
        name2.createVariant("Variant", createTopic());
        result = asList(filter, topic);
        assertEquals(1, result.size());
        assertTrue(result.contains(name));
        final Name name3 = topic.createName(createTopic(), "Name3");
        filter = xpath("name[default-name(.)]");
        result = asList(filter, topic);
        assertEquals(2, result.size());
        assertTrue(result.contains(name));
        assertTrue(result.contains(name2));
        filter = xpath("name[not(default-name(.))]");
        result = asList(filter, topic);
        assertEquals(1, result.size());
        assertTrue(result.contains(name3));
        IFilter<String> valueFilter = xpath("name/value");
        List<String> valueResult = asList(valueFilter, topic);
        assertEquals(3, valueResult.size());
        assertTrue(valueResult.contains("Semagia"));
        assertTrue(valueResult.contains("Name2"));
        assertTrue(valueResult.contains("Name3"));
        valueFilter = xpath("name/value[.='Semagia']");
        valueResult = asList(valueFilter, topic);
        assertEquals(1, valueResult.size());
        assertTrue(valueResult.contains("Semagia"));
    }
}
