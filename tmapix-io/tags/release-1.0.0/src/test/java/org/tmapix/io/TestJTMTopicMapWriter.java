/*
 * Copyright 2008 - 2010 Lars Heuer (heuer[at]semagia.com)
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

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests against the {@link JTMTopicMapWriter}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
@RunWith(Parameterized.class)
public class TestJTMTopicMapWriter extends AbstractCXTMWriterTestCase {

    public TestJTMTopicMapWriter(File file, String inputDir,
            String referenceDir, boolean convertToTMDM) {
        super(file, inputDir, referenceDir, convertToTMDM);
    }

    @Parameters
    public static Collection<Object> makeTestCases() {
        final Collection<Object> result = new ArrayList<Object>();
        result.addAll(CXTMTestUtils.makeLTMTestCases());
        result.addAll(CXTMTestUtils.makeCTMTestCases());
        result.addAll(CXTMTestUtils.makeJTMTestCases());
        result.addAll(CXTMTestUtils.makeSnelloTestCases());
        result.addAll(CXTMTestUtils.makeTMXMLTestCases());
        result.addAll(CXTMTestUtils.makeXTM10TestCases());
        result.addAll(CXTMTestUtils.makeXTM2TestCases());
        return result;
    }

    /* (non-Javadoc)
     * @see org.tmapix.io.AbstractCXTMWriterTestCase#makeWriter(org.tmapi.core.TopicMap, java.lang.String)
     */
    @Override
    protected TopicMapWriter makeWriter(final OutputStream out, String iri)
            throws Exception {
        final JTMTopicMapWriter writer = new JTMTopicMapWriter(out, iri);
        writer.setPrettify(true);
        return writer;
    }

    /* (non-Javadoc)
     * @see org.tmapix.io.AbstractCXTMWriterTestCase#getFileExtension()
     */
    @Override
    protected String getFileExtension() {
        return "jtm";
    }

}
