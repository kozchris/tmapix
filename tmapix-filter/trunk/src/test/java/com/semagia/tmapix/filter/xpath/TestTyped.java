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

import org.tmapi.core.Topic;
import org.tmapi.core.Typed;

import com.semagia.tmapix.filter.IFilter;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class TestTyped extends XPathTestCase {

    public TestTyped(String name) {
        super(name);
    }

    protected void _testTyped(Typed typed) {
        final IFilter<Topic> filter = xpath("type");
        assertEquals(typed.getType(), filter.matchOne(typed));
        List<Topic> result = asList(filter.match(typed));
        assertEquals(1, result.size());
        final Topic newType = createTopic();
        typed.setType(newType);
        assertEquals(newType, filter.matchOne(typed));
    }

    public void testAssociation() {
        _testTyped(createAssociation());
    }

    public void testRole() {
        _testTyped(createRole());
    }

    public void testOccurrence() {
        _testTyped(createOccurrence());
    }

    public void testName() {
        _testTyped(createName());
    }
}
