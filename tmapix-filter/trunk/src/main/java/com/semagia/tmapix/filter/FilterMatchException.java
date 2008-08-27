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

/**
 * Exception thrown if a <tt>IFilter</tt> instance cannot be applied to 
 * a construct.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class FilterMatchException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 6801914927584947555L;

    /**
     * Initializes the exception with the specified message.
     *
     * @param msg The message explaining the error.
     */
    public FilterMatchException(String msg) {
        super(msg);
    }

    /**
     * Initializes the exception with the specified cause.
     *
     * @param cause The cause.
     */
    public FilterMatchException(Throwable cause) {
        super(cause);
    }

    /**
     * Initializes the exception with the specified message and cause.
     *
     * @param msg The message explaining the error.
     * @param cause The cause.
     */
    public FilterMatchException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
