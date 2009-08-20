/*
 * Copyright 2009 Lars Heuer (heuer[at]semagia.com)
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
package com.semagia.tmapix.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tmapi.core.Association;
import org.tmapi.core.DatatypeAware;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;
import org.tmapi.core.Typed;
import org.tmapi.core.Variant;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
final class SignatureGenerator {

    private SignatureGenerator() {
        // noop.
    }

    public static String generateSignature(Reifiable reifiable) {
        if (reifiable instanceof Association) {
            return generateSignature((Association) reifiable);
        }
        else if (reifiable instanceof Role) {
            return generateSignature((Role) reifiable);
        }
        else if (reifiable instanceof Occurrence) {
            return generateSignature((Occurrence) reifiable);
        }
        else if (reifiable instanceof Name) {
            return generateSignature((Name) reifiable);
        }
        else if (reifiable instanceof Variant) {
            return generateSignature((Variant) reifiable);
        }
        else {
            throw new RuntimeException("Internal error: Unknown class: " + reifiable);
        }
    }

    private static String generateTopicSignature(Topic topic) {
        return topic.getId();
    }

    private static String generateScopeSignature(Scoped scoped) {
        final List<Topic> scope = new ArrayList<Topic>(scoped.getScope());
        final String[] scopeKeys = new String[scope.size()];
        for (int i=0; i<scopeKeys.length; i++) {
            scopeKeys[i] = generateTopicSignature(scope.get(i));
        }
        Arrays.sort(scopeKeys);
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<scopeKeys.length; i++) {
            sb.append(scopeKeys[i]);
        }
        return sb.toString();
    }

    private static String generateTypeSignature(Typed typed) {
        return generateTopicSignature(typed.getType());
    }

    private static String generateDatatypedSignature(DatatypeAware datatyped) {
        StringBuilder sb = new StringBuilder();
        sb.append(datatyped.getValue())
            .append('.')
            .append(datatyped.getDatatype().toExternalForm());
        return sb.toString();
    }

    public static String generateSignature(Association assoc) {
        StringBuilder sb = new StringBuilder();
        sb.append(generateTypeSignature(assoc))
            .append('.')
            .append(generateScopeSignature(assoc))
            .append('.');
        final List<Role> roles = new ArrayList<Role>(assoc.getRoles());
        final String[] roleKeys = new String[roles.size()];
        for (int i=0; i<roleKeys.length; i++) {
            roleKeys[i] = generateSignature(roles.get(i));
        }
        Arrays.sort(roleKeys);
        for (int i=0; i<roleKeys.length; i++) {
            sb.append(roleKeys[i]);
        }
        return sb.toString();
    }

    public static String generateSignature(Role role) {
        StringBuilder sb = new StringBuilder();
        sb.append(generateTopicSignature(role.getType()))
            .append('.')
            .append(generateTopicSignature(role.getType()));
        return sb.toString();
    }

    public static String generateSignature(Name name) {
        StringBuilder sb = new StringBuilder();
        sb.append(generateTypeSignature(name))
            .append('.')
            .append(generateScopeSignature(name))
            .append('.')
            .append(name.getValue());
        return sb.toString();
    }

    public static String generateSignature(Occurrence occ) {
        StringBuilder sb = new StringBuilder();
        sb.append(generateTypeSignature(occ))
            .append('.')
            .append(generateScopeSignature(occ))
            .append('.')
            .append(generateDatatypedSignature(occ));
        return sb.toString();
    }

    public static String generateSignature(Variant variant) {
        StringBuilder sb = new StringBuilder();
        sb.append(generateScopeSignature(variant))
            .append('.')
            .append(generateDatatypedSignature(variant));
        return sb.toString();
    }
}
