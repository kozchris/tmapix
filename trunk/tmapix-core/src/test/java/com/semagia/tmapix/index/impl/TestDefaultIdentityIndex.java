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

import com.semagia.tmapix.index.AbstractIdentityIndexTestCase;
import com.semagia.tmapix.index.IIdentityIndex;
import com.semagia.tmapix.index.impl.DefaultIdentityIndex;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestDefaultIdentityIndex extends AbstractIdentityIndexTestCase {

    public static void main(String[] args) {
        junit.awtui.TestRunner.run(TestDefaultIdentityIndex.class);
    }
    
    /* (non-Javadoc)
     * @see com.semagia.tmapix.index.AbstractIdentityIndexTestCase#_getIdentityIndex(org.tmapi.core.TopicMap)
     */
    @Override
    protected IIdentityIndex _getIdentityIndex(TopicMap tm) {
        DefaultIdentityIndex idx = new DefaultIdentityIndex();
        idx.initialize(tm);
        return idx;
    }

}
