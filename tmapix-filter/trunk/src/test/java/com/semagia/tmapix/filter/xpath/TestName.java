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

import org.tmapi.core.Name;
import org.tmapi.core.Variant;

import com.semagia.tmapix.filter.IFilter;

/**
 * {@link org.tmapi.core.Name} related tests.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class TestName extends XPathTestCase {

    public TestName(String name) {
        super(name);
    }

    public void testVariantAxis() {
        final Name name = createName();
        IFilter<Variant> filter = xpath("variant");
        List<Variant> result = asList(filter, name);
        assertEquals(0, result.size());
        final Variant var = name.createVariant("Variant", createTopic());
        result = asList(filter, name);
        assertEquals(1, result.size());
        assertTrue(result.contains(var));
        final Variant var2 = name.createVariant(createLocator("http://www.semagia.com/"), createTopic());
        result = asList(filter, name);
        assertEquals(2, result.size());
        assertTrue(result.contains(var));
        assertTrue(result.contains(var2));
        filter = xpath("variant[datatype=xsd:anyURI]");
        result = asList(filter, name);
        assertEquals(1, result.size());
        assertTrue(result.contains(var2));
        filter = xpath("variant[datatype=xsd:string]");
        result = asList(filter, name);
        assertEquals(1, result.size());
        assertTrue(result.contains(var));
        filter = xpath("variant[value='Variant']");
        result = asList(filter, name);
        assertEquals(1, result.size());
        assertTrue(result.contains(var));
        filter = xpath("variant[value='Variant' and datatype=xsd:anyURI]");
        result = asList(filter, name);
        assertEquals(0, result.size());
    }
}
