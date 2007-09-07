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
package com.semagia.tmapix.index;

import com.semagia.tmapix.index.impl.TestDefaultIdentityIndex;
import com.semagia.tmapix.index.impl.TestDefaultScopeIndex;
import com.semagia.tmapix.index.impl.TestDefaultTypeInstanceIndex;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class IndexTestSuite extends TestSuite {

    public static void main(String[] args) {
        junit.awtui.TestRunner.main(new String[] { IndexTestSuite.class.getName()});
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(TestDefaultIdentityIndex.class);
        suite.addTestSuite(TestDefaultScopeIndex.class);
        suite.addTestSuite(TestDefaultTypeInstanceIndex.class);
        return suite;
    }

}
