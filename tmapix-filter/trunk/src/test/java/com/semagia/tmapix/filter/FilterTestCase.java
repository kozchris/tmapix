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
package com.semagia.tmapix.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.TMAPITestCase;
import org.tmapi.core.Topic;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public abstract class FilterTestCase extends TMAPITestCase {

    public FilterTestCase(String name) {
        super(name);
    }

    protected Object singleResult(Iterable<?> iterable) {
        final Iterator<?> it = iterable.iterator();
        assertTrue("Expected an element", it.hasNext());
        Object obj = it.next();
        try {
            it.next();
            fail("Expected a singleton result");
        }
        catch (Exception ex) {
            // noop.
        }
        return obj;
    }

    protected void _testThis(IFilter<?> filter, Construct obj) throws Exception {
        assertEquals(obj, singleResult(filter.match(obj)));
    }

    protected void _testParent(IFilter<?> filter, Construct obj) throws Exception {
        assertEquals(obj.getParent(), singleResult(filter.match(obj)));
    }

    protected void _testRoles(IFilter<?> filter, Construct obj) throws Exception {
        Set<Object> coll = asSet(filter.match(obj));
        if (obj instanceof Topic) {
            assertEquals(((Topic) obj).getRolesPlayed(), coll);
        }
        else if (obj instanceof Association) {
            assertEquals(((Association) obj).getRoles(), coll);
        }
        else {
            assertTrue(coll.isEmpty());
        }
    }

    protected Collection<?> collection(Collection<?> coll) {
        return new ArrayList<Object>(coll);
    }

    protected Set<Object> asSet(Iterable<?> match) {
        Set<Object> res = new HashSet<Object>();
        Iterator<?> iter = match.iterator();
        while (iter.hasNext()) {
            res.add(iter.next());
        }
        return res;
    }

}
