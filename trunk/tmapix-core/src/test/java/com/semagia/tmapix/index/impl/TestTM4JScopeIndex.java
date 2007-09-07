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
package com.semagia.tmapix.index.impl;

import org.tmapi.core.TopicMap;

import com.semagia.tmapix.index.AbstractScopeIndexTestCase;
import com.semagia.tmapix.index.IScopeIndex;
import com.semagia.tmapix.index.impl.TM4JScopeIndex;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestTM4JScopeIndex extends AbstractScopeIndexTestCase {

    public static void main(String[] args) {
        junit.awtui.TestRunner.run(TestTM4JScopeIndex.class);
    }
    
    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.AbstractScopeIndexTestCase#_getScopeIndex(org.tmapi.core.TopicMap)
     */
    @Override
    protected IScopeIndex _getScopeIndex(TopicMap tm) {
        try {
            TM4JScopeIndex idx = new TM4JScopeIndex();
            idx.initialize(tm);
            return idx;
        }
        catch (Exception ex) {
            fail("TM4J is not available");
            return null;
        }
    }

}
