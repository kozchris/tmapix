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
import org.tmapi.core.Construct;
import org.tmapi.core.Topic;

import com.semagia.tmapix.filter.IFilter;

/**
 * {@link org.tmapi.core.TopicMap} related tests.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class TestTopicMap extends XPathTestCase {

    public TestTopicMap(String name) {
        super(name);
    }

    public void testTopicAxis() {
        IFilter<Topic> filter = xpath("topic");
        List<Topic> result = asList(filter, _tm);
        assertEquals(0, result.size());
        final Topic topic = createTopic();
        result = asList(filter, _tm);
        assertEquals(1, result.size());
        assertTrue(result.contains(topic));
        filter = xpath("child::topic");
        result = asList(filter, _tm);
        assertEquals(1, result.size());
        assertTrue(result.contains(topic));
    }

    public void testAssociationAxis() {
        IFilter<Association> filter = xpath("association");
        List<Association> result = asList(filter, _tm);
        assertEquals(0, result.size());
        final Association assoc = createAssociation();
        result = asList(filter, _tm);
        assertEquals(1, result.size());
        assertTrue(result.contains(assoc));
        filter = xpath("child::association");
        result = asList(filter, _tm);
        assertEquals(1, result.size());
        assertTrue(result.contains(assoc));
    }

    public void testChildAxis() {
        IFilter<Construct> filter = xpath("child::*");
        List<Construct> result = asList(filter, _tm);
        assertEquals(0, result.size());
        final Association assoc = createAssociation();
        result = asList(filter, _tm);
        assertEquals(2, result.size());
        assertTrue(result.contains(assoc));
        assertTrue(result.contains(assoc.getType()));
        final Topic topic = createTopic();
        result = asList(filter, _tm);
        assertEquals(3, result.size());
        assertTrue(result.contains(assoc));
        assertTrue(result.contains(assoc.getType()));
        assertTrue(result.contains(topic));
    }

}
