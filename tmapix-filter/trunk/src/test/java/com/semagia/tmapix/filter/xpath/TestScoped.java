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

import java.util.Set;

import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

import com.semagia.tmapix.filter.IFilter;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class TestScoped extends XPathTestCase {

    public TestScoped(String name) {
        super(name);
    }

    protected void _testScoped(final Scoped scoped) {
        IFilter<Topic> filter = xpath("scope");
        IFilter<Boolean> ucsFilter = xpath("in-ucs(.)");
        Set<Topic> result = asSet(filter, scoped);
        assertEquals(scoped.getScope(), result);
        if (!(scoped instanceof Variant)) {
            assertTrue(ucsFilter.matchOne(scoped));
        }
        else {
            assertFalse(ucsFilter.matchOne(scoped));
        }
        final Topic theme = createTopic();
        scoped.addTheme(theme);
        assertTrue(scoped.getScope().contains(theme));
        result = asSet(filter, scoped);
        assertEquals(scoped.getScope(), result);
        assertFalse(ucsFilter.matchOne(scoped));
    }

    public void testAssociation() {
        _testScoped(createAssociation());
    }

    public void testOccurrence() {
        _testScoped(createOccurrence());
    }

    public void testName() {
        _testScoped(createName());
    }

    public void testVariant() {
        _testScoped(createVariant());
    }
}
