/*
 * Copyright 2009 - 2010 Lars Heuer (heuer[at]semagia.com)
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
package org.tmapix.io.internal.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;
import org.tmapix.voc.XSD;

/**
 * Internal class to merge various Topic Maps constructs.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class MergeUtils {

    private MergeUtils() {
        // noop.
    }

    public static void merge(Reifiable source, Reifiable target) {
        if (source.equals(target)) {
            throw new RuntimeException("Internal error: Trying to merge equal constructs: " + source);
        }
        if (source instanceof Association) {
            _merge((Association) source, (Association) target);
        }
        else if (source instanceof Role) {
            _merge((Role) source, (Role) target);
        }
        else if (source instanceof Occurrence) {
            _merge((Occurrence) source, (Occurrence) target);
        }
        else if (source instanceof Name) {
            _merge((Name) source, (Name) target);
        }
        else if (source instanceof Variant) {
            _merge((Variant) source, (Variant) target);
        }
        else {
            throw new RuntimeException("Internal error: Unexpected argument " + source + " " + target);
        }
    }

    private static void _merge(Association source, Association target) {
        _handleExisting(source, target);
        Map<String, Role> sigs = new HashMap<String, Role>();
        for (Role role: target.getRoles()) {
            sigs.put(SignatureGenerator.generateSignature(role), role);
        }
        for (Role role: new ArrayList<Role>(source.getRoles())) {
            _handleExisting(role, sigs.get(SignatureGenerator.generateSignature(role)));
            role.remove();
        }
        source.remove();
    }

    private static void _handleExisting(Reifiable source, Reifiable target) {
        _moveItemIdentifiers(source, target);
        if (source.getReifier() == null) {
            return;
        }
        if (target.getReifier() != null) {
            Topic reifier = source.getReifier();
            source.setReifier(null);
            target.getReifier().mergeIn(reifier);
        }
        else {
            Topic reifier = source.getReifier();
            source.setReifier(null);
            target.setReifier(reifier);
        }
    }

    private static void _merge(Role source, Role target) {
        final Association sourceParent = source.getParent();
        final Association targetParent = target.getParent();
        _handleExisting(sourceParent, targetParent);
        _moveRoles(sourceParent, targetParent);
        if (!sourceParent.equals(targetParent)) {
            sourceParent.remove();
        }
    }
    
    private static void _moveRoles(Association source, Association target) {
        if (source.equals(target)) {
            return;
        }
        Map<String, Role> sigs = new HashMap<String, Role>();
        for (Role role: target.getRoles()) {
            sigs.put(SignatureGenerator.generateSignature(role), role);
        }
        for (Role role: new ArrayList<Role>(source.getRoles())) {
            Role existing = sigs.get(SignatureGenerator.generateSignature(role));
            if (existing != null) {
                _handleExisting(role, existing);
            }
            else {
                Role targetRole = target.createRole(role.getType(), role.getPlayer());
                _handleExisting(role, targetRole);
            }
        }
    }

    private static void _merge(Occurrence source, Occurrence target) {
        _handleExisting(source, target);
        source.remove();
    }

    private static void _merge(Name source, Name target) {
        _handleExisting(source, target);
        Map<String, Variant> sigs = new HashMap<String, Variant>();
        for (Variant variant: target.getVariants()) {
            sigs.put(SignatureGenerator.generateSignature(variant), variant);
        }
        for (Variant variant: new ArrayList<Variant>(source.getVariants())) {
            Variant existing = sigs.get(SignatureGenerator.generateSignature(variant));
            if (existing != null) {
                _merge(variant, existing);
            }
            else {
                _copy(variant, target);
            }
        }
        source.remove();
    }

    private static void _copy(Variant variant, Name name) {
        final String datatype = variant.getDatatype().getReference();
        Variant var = null;
        if (XSD.ANY_URI.equals(datatype)) {
            var = name.createVariant(variant.locatorValue(), variant.getScope());
        }
        else if (XSD.STRING.equals(datatype)) {
            var = name.createVariant(variant.getValue(), variant.getScope());
        }
        else {
            var = name.createVariant(variant.getValue(), variant.getDatatype(), variant.getScope());
        }
        _handleExisting(variant, var);
    }

    private static void _merge(Variant source, Variant target) {
        _handleExisting(source, target);
        source.remove();
    }

    private static void _moveItemIdentifiers(Reifiable source, Reifiable target) {
        for (Locator iid: new ArrayList<Locator>(source.getItemIdentifiers())) {
            source.removeItemIdentifier(iid);
            target.addItemIdentifier(iid);
        }
    }

}
