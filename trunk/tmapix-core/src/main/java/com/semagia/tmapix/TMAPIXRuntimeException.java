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
 * The Initial Developer of the Original Code is Semagia http://semagia.com/. 
 * All Rights Reserved.
 * 
 * Contributer(s): Lars Heuer <heuer[at]semagia.com>
 *
 */
package com.semagia.tmapix;

import org.tmapi.core.TMAPIRuntimeException;

/**
 * Standard TMAPIX runtime exception.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TMAPIXRuntimeException extends TMAPIRuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -4098454225080112576L;

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param msg The exception message.
     * @param throwable The cause of this exception.
     */
    public TMAPIXRuntimeException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param msg The exception message.
     */
    public TMAPIXRuntimeException(String msg) {
        super(msg);
    }

    /**
     * Constructs a new exception that wraps the <code>throwable</code>.
     *
     * @param throwable The cause of this exception.
     * @since 1.0
     */
    public TMAPIXRuntimeException(Throwable throwable) {
        super(throwable);
    }

}
