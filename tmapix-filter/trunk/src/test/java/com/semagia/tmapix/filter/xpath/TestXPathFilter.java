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

import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

import com.semagia.tmapix.filter.FilterTestCase;
import com.semagia.tmapix.filter.IFilter;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
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
        _testRoles(xpath("./roles"), c);
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
        assertEquals(collection(assoc.getRoles()), asCollection((xpath("../roles").match(role))));
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
}
