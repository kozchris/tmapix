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
import org.tmapix.io.CXTMTestUtils.Filter;

/**
 * Tests against the {@link TMXMLTopicMapWriter}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
@RunWith(Parameterized.class)
public class TestTMXMLTopicMapWriter extends AbstractCXTMWriterTestCase {

    public TestTMXMLTopicMapWriter(File file, String inputDir,
            String referenceDir, boolean convertToTMDM) {
        super(file, inputDir, referenceDir, convertToTMDM);
    }

    @Parameters
    public static Collection<Object> makeTestCases() {
        final Collection<Object> result = new ArrayList<Object>(); 
        result.addAll(Filter.from("/cxtm/tmxml/")
                .using("xml")
                .exclude("merge.xml"    // Does not work since one topic has more than one iid
                                        // and TM/XML allows max. one iid
                        )
                .filter());
        result.addAll(Filter.from("/cxtm/ltm/")
                .using("ltm")
                .exclude("unescapeUnicode2-1.3.ltm", // TODO: Problem: The writer writes &#56319;&#57343; to the output and causes parsing problems
                        // Multiple iids:
                        "duplsubjind.ltm",
                        "duplsubjind2.ltm",
                        "include-two-collide.ltm",
                        "include-two-levels.ltm",
                        "include.ltm",
                        "includeloop.ltm",
                        "includemerge.ltm",
                        "merge-subject-locator.ltm",
                        "mergedouble.ltm",
                        "mergeloop.ltm",
                        "mergeltm-with-baseuri.ltm",
                        "mergeltm.ltm",
                        "mergeltm2.ltm",
                        "mergeltm3.ltm",
                        "mergextm.ltm",
                        "mio-bug-11.ltm",
                        "reify-association-1.3.ltm",
                        "reify-basename-1.3.ltm",
                        "reify-occurrence-1.3.ltm",
                        "reify-role-1.3.ltm",
                        "reify-variant-1.3.ltm",
                        "reifytm-1.3.ltm"
                        )
                .filter());
        return result;
    }

    /* (non-Javadoc)
     * @see org.tmapix.io.AbstractCXTMWriterTestCase#makeWriter(org.tmapi.core.TopicMap, java.lang.String)
     */
    @Override
    protected TopicMapWriter makeWriter(final OutputStream out, String iri)
            throws Exception {
        final TMXMLTopicMapWriter writer = new TMXMLTopicMapWriter(out, iri);
        writer.setPrettify(true);
        return writer;
    }

    /* (non-Javadoc)
     * @see org.tmapix.io.AbstractCXTMWriterTestCase#getFileExtension()
     */
    @Override
    protected String getFileExtension() {
        return "xml";
    }

}
