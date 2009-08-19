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

import com.semagia.tmapix.voc.XSD;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
class MergeUtils {

    private MergeUtils() {
        // noop.
    }

    static Reifiable merge(Reifiable source, Reifiable target) {
        if (source.equals(target)) {
            throw new RuntimeException("Internal error: Trying to merge equal constructs: " + source);
        }
        if (source instanceof Association) {
            return _merge((Association) source, (Association) target);
        }
        else if (source instanceof Role) {
            return _merge((Role) source, (Role) target);
        }
        else if (source instanceof Occurrence) {
            return _merge((Occurrence) source, (Occurrence) target);
        }
        else if (source instanceof Name) {
            return _merge((Name) source, (Name) target);
        }
        else if (source instanceof Variant) {
            return _merge((Variant) source, (Variant) target);
        }
        else {
            throw new RuntimeException("Internal error: Unexpected argument " + source + " " + target);
        }
    }

    private static Association _merge(Association source, Association target) {
        _handleExisting(source, target);
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
        source.remove();
        return target;
    }

    private static void _handleExisting(Reifiable source, Reifiable target) {
        _moveItemIdentifiers(source, target);
        _moveReifier(source, target);
    }

    private static Role _merge(Role source, Role target) {
        if (source.getParent().equals(target.getParent())) {
            _handleExisting(source, target);
            source.remove();
        }
        else {
            _merge(source.getParent(), target.getParent());
        }
        return target;
    }

    private static Occurrence _merge(Occurrence source, Occurrence target) {
        _handleExisting(source, target);
        source.remove();
        return target;
    }

    private static Name _merge(Name source, Name target) {
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
        return target;
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

    private static Variant _merge(Variant source, Variant target) {
        _handleExisting(source, target);
        source.remove();
        return target;
    }

    private static void _moveItemIdentifiers(Reifiable source, Reifiable target) {
        for (Locator iid: new ArrayList<Locator>(source.getItemIdentifiers())) {
            source.removeItemIdentifier(iid);
            target.addItemIdentifier(iid);
        }
    }

    private static void _moveReifier(Reifiable source, Reifiable target) {
        if (source.getReifier() != null) {
            Topic reifier = source.getReifier();
            source.setReifier(null);
            target.setReifier(reifier);
        }
    }

}
