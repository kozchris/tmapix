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

import org.tmapi.core.Construct;

/**
 * Immutable filter which can be used to be get evaluated against a 
 * {@link org.tmapi.core.Construct}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public interface IFilter<T> {

    /**
     * Applies this filter instance to the specified <tt>context</tt>.
     *
     * @param context The starting point for the filter.
     * @return A (maybe empty) iterable, never <tt>null</tt>.
     * @throws FilterMatchException
     */
    public Iterable<T> match(Construct context) throws FilterMatchException;

    /**
     * Applies this filter instance to the specified <tt>context</tt> and
     * returns one result item.
     * <p>
     * If the result contains more than one item, the first item from the result
     * is chosen. Note that the returned result item may change between two 
     * invocations of the {@link #matchOne(Construct)} method even
     * if the underlying dataset has not been modified.
     * </p>
     *
     * @param context The starting point for the filter.
     * @return The result item or <tt>null</tt> if no result was found.
     * @throws FilterMatchException
     */
    public T matchOne(Construct context) throws FilterMatchException;

}
