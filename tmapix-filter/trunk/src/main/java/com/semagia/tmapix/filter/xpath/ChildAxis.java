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

/**
 * Provides functions to determinate axes.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date:$
 */
final class ChildAxis {

    public static boolean isTopicAxis(String name) {
        return "topic".equals(name);
    }

    public static boolean isAssociationAxis(String name) {
        return "association".equals(name);
    }

    public static boolean isRoleAxis(String name) {
        return "role".equals(name);
    }

    public static boolean isOccurrenceAxis(String name) {
        return "occurrence".equals(name);
    }

    public static boolean isNameAxis(String name) {
        return "name".equals(name);
    }

    public static boolean isReifierAxis(String name) {
        return "reifier".equals(name);
    }

    public static boolean isTypeAxis(String name) {
        return "type".equals(name);
    }

    public static boolean isScopeAxis(String name) {
        return "scope".equals(name);
    }

    public static boolean isValueAxis(String name) {
        return "value".equals(name);
    }

}
