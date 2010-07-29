/*
 * Copyright 2008 Lars Heuer (heuer[at]semagia.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tmapix.io;

import java.util.Iterator;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

/**
 * Internal association utilities.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
final class AssociationUtils {

    private AssociationUtils() {
        // noop.
    }

    public static boolean isTypeInstanceAssociation(final Topic typeInstance, 
            final Topic type, final Topic instance,
            final Association assoc, 
            final Set<Role> roles) {
        if (typeInstance == null
                || type == null 
                || instance == null
                || !assoc.getType().equals(typeInstance)
                || assoc.getReifier() != null
                || !assoc.getScope().isEmpty()
                || roles.size() != 2) {
            return false;
        }
        final Iterator<Role> roleIter = roles.iterator();
        final Role firstRole = roleIter.next();
        final Role secondRole = roleIter.next();
        if (firstRole.getType().equals(type)) {
            return secondRole.getType().equals(instance);
        }
        return secondRole.getType().equals(instance) 
                    && firstRole.getType().equals(type);
    }

}
