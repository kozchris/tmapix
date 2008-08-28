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
package com.semagia.tmapix.filter.xpath.fun;

import java.util.List;

import org.jaxen.FunctionCallException;
import org.tmapi.core.Construct;
import org.tmapi.core.TopicMap;

import com.semagia.tmapix.filter.utils.TMAPIUtils;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
final class FunctionUtils {

    public static void checkUnaryArgument(List<?> args) throws FunctionCallException {
        if (args.size() != 1) {
            throw new FunctionCallException("Expected one argument, got " + args.size());
        }
    }

    public static void checkString(Object arg) throws FunctionCallException {
        if (!(arg instanceof String)) {
            throw new FunctionCallException("Expected a string argument");
        }
    }

    public static TopicMap getTopicMap(List<?> nodeList) {
        for (Object obj: nodeList) {
            if (TMAPIUtils.isConstruct(obj)) {
                return ((Construct) obj).getTopicMap();
            }
        }
        throw new RuntimeException("No topic map in the context available");
    }
}
