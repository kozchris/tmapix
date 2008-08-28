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
 * @version $Rev$ - $Date$
 */
final class ChildAxis {

    private ChildAxis() {
        // noop.
    }

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

    public static boolean isReifiedAxis(String name) {
        return "reified".equals(name);
    }

    public static boolean isTypeAxis(String name) {
        return "type".equals(name);
    }

    public static boolean isInstanceAxis(String name) {
        return "instance".equals(name);
    }

    public static boolean isSupertypeAxis(String name) {
        return "supertype".equals(name);
    }

    public static boolean isSubtypeAxis(String name) {
        return "subtype".equals(name);
    }

    public static boolean isPlayerAxis(String name) {
        return "player".equals(name);
    }

    public static boolean isScopeAxis(String name) {
        return "scope".equals(name);
    }

    public static boolean isVariantAxis(String name) {
        return "variant".equals(name);
    }

    public static boolean isValueAxis(String name) {
        return "value".equals(name);
    }

    public static boolean isDatatypeAxis(String name) {
        return "datatype".equals(name);
    }

    public static boolean isItemIdentifierAxis(String name) {
        return "iid".equals(name);
    }

    public static boolean isSubjectIdentifierAxis(String name) {
        return "sid".equals(name);
    }

    public static boolean isSubjectLocatorAxis(String name) {
        return "slo".equals(name);
    }

}
