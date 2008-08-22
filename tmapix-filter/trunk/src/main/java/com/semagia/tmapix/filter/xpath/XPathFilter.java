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

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.Navigator;
import org.tmapi.core.Construct;
import org.tmapi.core.TMAPIRuntimeException;

import com.semagia.tmapix.filter.FilterMatchException;
import com.semagia.tmapix.filter.IFilter;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
@SuppressWarnings("serial")
public final class XPathFilter<T> extends BaseXPath implements IFilter<T> {

    XPathFilter(final String xpath, final Navigator navigator) throws JaxenException {
        super(xpath, navigator);
    }

    XPathFilter(final String xpath) throws JaxenException {
        this(xpath, TopicMapNavigator.getInstance());
    }

    /**
     * 
     *
     * @param <T>
     * @param xpath
     * @return
     * @throws TMAPIRuntimeException
     */
    public static <T> IFilter<T> create(final String xpath) throws TMAPIRuntimeException {
        try {
            return new XPathFilter<T>(xpath);
        }
        catch (Exception ex) {
            throw new TMAPIRuntimeException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public Iterable<T> match(final Construct context) throws FilterMatchException {
        Object result = null;
        try {
            result = super.evaluate(context);
        }
        catch (JaxenException ex) {
            throw new FilterMatchException(ex);
        }
        return (Iterable<T>) result;
    }

}
