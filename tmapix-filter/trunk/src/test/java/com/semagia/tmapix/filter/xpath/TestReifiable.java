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

import org.tmapi.core.Reifiable;
import org.tmapi.core.Topic;

import com.semagia.tmapix.filter.IFilter;

/**
 * {@link org.tmapi.core.Reifiable} related tests.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestReifiable extends XPathTestCase {

    public TestReifiable(String name) {
        super(name);
    }

    protected void _testReifiable(Reifiable reifiable) {
        IFilter<Topic> reifierFilter = xpath("reifier");
        IFilter<Reifiable> reifiedFilter = xpath("reified");
        final Topic reifier = createTopic();
        assertEquals(1, asList(reifierFilter.match(reifiable)).size());
        assertNull(reifierFilter.matchOne(reifiable));
        assertEquals(1, asList(reifiedFilter.match(reifier)).size());
        assertNull(reifiedFilter.matchOne(reifier));
        reifiable.setReifier(reifier);
        assertEquals(reifier, reifierFilter.matchOne(reifiable));
        assertEquals(reifiable, reifiedFilter.matchOne(reifier));
        reifiable.setReifier(null);
        assertNull(reifierFilter.matchOne(reifiable));
        assertNull(reifiedFilter.matchOne(reifier));
    }

    public void testTopicMap() {
        _testReifiable(_tm);
    }

    public void testAssociation() {
        _testReifiable(createAssociation());
    }

    public void testRole() {
        _testReifiable(createRole());
    }

    public void testOccurrence() {
        _testReifiable(createOccurrence());
    }

    public void testName() {
        _testReifiable(createName());
    }

    public void testVariant() {
        _testReifiable(createVariant());
    }
}
