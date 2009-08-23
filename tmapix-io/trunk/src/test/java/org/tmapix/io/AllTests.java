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

import junit.framework.TestSuite;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class AllTests extends TestSuite {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public AllTests() {
        super();
        addTestSuite(TestMapHandlerFactory.class);
        addTestSuite(TestTMAPIMapHandler.class);
        addTest(TestCTMTopicMapReader.suite());
        addTest(TestJTMTopicMapReader.suite());
        addTest(TestN3TopicMapReader.suite());
        addTest(TestNTriplesTopicMapReader.suite());
        addTest(TestRDFXMLTopicMapReader.suite());
        addTest(TestLTMTopicMapReader.suite());
        addTest(TestTMXMLTopicMapReader.suite());
        addTest(TestTMXMLValidatingTopicMapReader.suite());
        addTest(TestSnelloTopicMapReader.suite());
        addTest(TestXTM10TopicMapReader.suite());
        //addTest(TestXTM10ValidatingTopicMapReader.suite());
        addTest(TestXTM20TopicMapReader.suite());
        addTest(TestXTM20ValidatingTopicMapReader.suite());
    }

    public static TestSuite suite() {
        return new AllTests();
    }
}
