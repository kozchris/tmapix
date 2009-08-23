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

import org.tmapix.io.TMAPIChooser;

import junit.framework.TestSuite;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestTinyTim extends AllTests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public TestTinyTim() {
        super();
        System.setProperty(TMAPIChooser.TMAPI_SYSTEM_FACTORY, TMAPIChooser.TINYTIM_SYSTEM_FACTORY);
    }

    public static TestSuite suite() {
        return new TestTinyTim();
    }
}
