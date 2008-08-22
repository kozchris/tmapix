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

import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.DatatypeAware;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.Typed;
import org.tmapi.core.Variant;

/**
 * Provides functions to test objects of their type.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class Utils {

    public static boolean isConstruct(Object obj) {
        return obj instanceof Construct;
    }

    public static boolean isTopicMap(Object obj) {
        return obj instanceof TopicMap;
    }

    public static boolean isTopic(Object obj) {
        return obj instanceof Topic;
    }

    public static boolean isAssociation(Object obj) {
        return obj instanceof Association;
    }

    public static boolean isRole(Object obj) {
        return obj instanceof Role;
    }

    public static boolean isOccurrence(Object obj) {
        return obj instanceof Occurrence;
    }

    public static boolean isName(Object obj) {
        return obj instanceof Name;
    }

    public static boolean isVariant(Object obj) {
        return obj instanceof Variant;
    }

    public static boolean isTyped(Object obj) {
        return obj instanceof Typed;
    }

    public static boolean isScoped(Object obj) {
        return obj instanceof Scoped;
    }

    public static boolean isReifiable(Object obj) {
        return obj instanceof Reifiable;
    }

    public static boolean isDatatypeAware(Object obj) {
        return obj instanceof DatatypeAware;
    }
}
