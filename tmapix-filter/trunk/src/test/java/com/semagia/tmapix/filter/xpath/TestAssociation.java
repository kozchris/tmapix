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
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

import com.semagia.tmapix.filter.IFilter;

/**
 * {@link org.tmapi.core.Association} and {@link org.tmapi.core.Role} related 
 * tests.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestAssociation extends XPathTestCase {

    public TestAssociation(String name) {
        super(name);
    }

    public void testRoleAxis() {
        final Association assoc = createAssociation();
        final Topic type1 = createTopic();
        final Topic type2 = createTopic();
        final Topic player1 = createTopic();
        final Topic player2 = createTopic();
        IFilter<Role> filter = xpath("role");
        List<Role> result = asList(filter, assoc);
        assertEquals(0, result.size());
        final Role role1 = assoc.createRole(type1, player1);
        result = asList(filter, assoc);
        assertEquals(1, result.size());
        assertTrue(result.contains(role1));
        final Role role2 = assoc.createRole(type2, player2);
        result = asList(filter, assoc);
        assertEquals(2, result.size());
        assertTrue(result.contains(role1));
        assertTrue(result.contains(role2));
        IFilter<Topic> roleTypeFilter = xpath("role/type");
        List<Topic> roleTypes = asList(roleTypeFilter, assoc);
        assertEquals(2, roleTypes.size());
        assertTrue(roleTypes.contains(type1));
        assertTrue(roleTypes.contains(type2));
        IFilter<Topic> rolePlayerFilter = xpath("role/player");
        List<Topic> rolePlayers = asList(rolePlayerFilter, assoc);
        assertEquals(2, rolePlayers.size());
        assertTrue(rolePlayers.contains(player1));
        assertTrue(rolePlayers.contains(player2));
    }

    public void testChildAxis() {
        final Association assoc = createAssociation();
        IFilter<Role> filter = xpath("child::*");
        List<Role> result = asList(filter, assoc);
        assertEquals(0, result.size());
        final Role role1 = assoc.createRole(createTopic(), createTopic());
        result = asList(filter, assoc);
        assertEquals(1, result.size());
        assertTrue(result.contains(role1));
        final Role role2 = assoc.createRole(createTopic(), createTopic());
        result = asList(filter, assoc);
        assertEquals(2, result.size());
        assertTrue(result.contains(role1));
        assertTrue(result.contains(role2));
        filter = xpath("child::role");
        asList(filter, assoc);
        assertEquals(2, result.size());
        assertTrue(result.contains(role1));
        assertTrue(result.contains(role2));
    }

}
