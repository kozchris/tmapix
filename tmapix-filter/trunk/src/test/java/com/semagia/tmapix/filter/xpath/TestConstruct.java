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

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;

import com.semagia.tmapix.filter.IFilter;

/**
 * {@link org.tmapi.core.Construct} related tests.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestConstruct extends XPathTestCase {

    public TestConstruct(String name) {
        super(name);
    }

    protected void _testConstruct(final Construct c) {
        // self
        assertEquals(c, xpath(".").matchOne(c));
        assertEquals(1, asList(xpath(".").match(c)).size());
        // parent
        assertEquals(c.getParent(), xpath("..").matchOne(c));
        assertEquals(c.getParent(), xpath("parent::*").matchOne(c));
        // item identifiers
        assertEquals(0, c.getItemIdentifiers().size());
        IFilter<Locator> filter = xpath("iid");
        List<Locator> result = asList(filter.match(c));
        assertEquals(0, result.size());
        final Locator iid = createLocator("http://www.semagia.com/xpath#1");
        final Locator iid2 = createLocator("http://www.semagia.com/xpath#2");
        c.addItemIdentifier(iid);
        assertEquals(c, xpath("iid('" + iid.getReference() + "')").matchOne(_tm));
        result = asList(filter.match(c));
        assertEquals(1, result.size());
        assertTrue(result.contains(iid));
        c.removeItemIdentifier(iid);
        result = asList(filter.match(c));
        assertEquals(0, result.size());
        c.addItemIdentifier(iid);
        filter = xpath("iid[.= '" + iid2.getReference() + "']");
        result = asList(filter.match(c));
        assertEquals(0, result.size());
        filter = xpath("iid[.= '" + iid.getReference() + "']");
        result = asList(filter.match(c));
        assertEquals(1, result.size());
        assertTrue(result.contains(iid));
        filter = xpath("iid[.= '" + iid2.getReference() + "' or .= '" + iid.getReference() + "']");
        result = asList(filter.match(c));
        assertEquals(1, result.size());
        assertTrue(result.contains(iid));
        c.addItemIdentifier(iid2);
        result = asList(filter.match(c));
        assertEquals(2, result.size());
        assertTrue(result.contains(iid));
        assertTrue(result.contains(iid2));
    }

    public void testTopicMap() {
        _testConstruct(_tm);
    }

    public void testTopic() {
        _testConstruct(_tm.createTopicBySubjectIdentifier(createLocator("http://www.semagia.com/")));
    }

    public void testAssociation() {
        _testConstruct(createAssociation());
    }

    public void testRole() {
        _testConstruct(createRole());
    }

    public void testOccurrence() {
        _testConstruct(createOccurrence());
    }

    public void testName() {
        _testConstruct(createName());
    }

    public void testVariant() {
        _testConstruct(createVariant());
    }
}
