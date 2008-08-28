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

import org.jaxen.JaxenException;
import org.jaxen.SimpleFunctionContext;
import org.jaxen.XPathFunctionContext;
import org.tmapi.core.TMAPIRuntimeException;

import com.semagia.tmapix.filter.IFilter;
import com.semagia.tmapix.filter.xpath.fun.AtomifyFunction;
import com.semagia.tmapix.filter.xpath.fun.DefaultNameFunction;
import com.semagia.tmapix.filter.xpath.fun.IidFunction;
import com.semagia.tmapix.filter.xpath.fun.InUCSFunction;
import com.semagia.tmapix.filter.xpath.fun.SidFunction;
import com.semagia.tmapix.filter.xpath.fun.SloFunction;

/**
 * {@link IFilter} factory that uses <a href="http://www.w3.org/TR/xpath">XPath 1.0</a>
 * expressions.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public final class XPathFilter {

    static {
        SimpleFunctionContext ctx = (SimpleFunctionContext) XPathFunctionContext.getInstance();
        ctx.registerFunction("", "sid", new SidFunction());
        ctx.registerFunction("", "slo", new SloFunction());
        ctx.registerFunction("", "iid", new IidFunction());
        ctx.registerFunction("", "in-ucs", new InUCSFunction());
        ctx.registerFunction("", "default-name", new DefaultNameFunction());
        ctx.registerFunction("", "atomify", new AtomifyFunction());
    }

    /**
     * Creates a new {@link IFilter} instance.
     *
     * @param xpath The XPath which should be evaluated.
     * @return A {@link IFilter} instance.
     * @throws TMAPIRuntimeException In case of an error (i.e. syntax error).
     */
    public static <T> IFilter<T> create(final String xpath) throws TMAPIRuntimeException {
        try {
            return new MapXPathFilter<T>(xpath, MapNavigator.getInstance());
        }
        catch (JaxenException ex) {
            throw new TMAPIRuntimeException(ex);
        }
    }

}
