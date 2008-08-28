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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.tmapi.core.Construct;
import org.tmapi.core.TMAPITestCase;

import com.semagia.tmapix.filter.IFilter;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class XPathTestCase extends TMAPITestCase {

    public XPathTestCase(String name) {
        super(name);
    }

    protected <T> IFilter<T> xpath(final String path) {
        return XPathFilter.create(path);
    }

    protected static <T> Set<T> asSet(final IFilter<T> filter, final Construct c) {
        return asSet(filter.match(c));
    }

    protected static <T> Set<T> asSet(final Iterable<T> iterable) {
        Set<T> set = new HashSet<T>();
        for (T obj: iterable) {
            set.add(obj);
        }
        return set;
    }

    protected static <T> List<T> asList(final IFilter<T> filter, final Construct c) {
        return asList(filter.match(c));
    }

    protected static <T> List<T> asList(Iterable<T> iterable) {
        if (iterable instanceof List) {
            return (List<T>) iterable;
        }
        List<T> list = new ArrayList<T>();
        for (T obj: iterable) {
            list.add(obj);
        }
        return list;
    }
}
