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
import java.net.URL;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.tmapi.core.TopicMap;
import org.tmapix.io.CXTMTestUtils.Filter;

/**
 * Tests against the {@link JTMTopicMapReader}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
@RunWith(Parameterized.class)
public class TestJTMTopicMapReader extends AbstractValidCXTMReaderTestCase {

    public TestJTMTopicMapReader(File file, String inputDir, String referenceDir, boolean convertToTMDM) {
        super(file, inputDir, referenceDir, convertToTMDM);
    }

    @Parameters
    public static Collection<Object> makeTestCases() {
        return Filter.from("/cxtm/jtm/")
                        .using("jtm")
                        .exclude("subjid-escaping.xtm2.jtm" // uncertain of this one
                                )
                        .filter();
    }

    @Override
    protected TopicMapReader makeReader(final TopicMap tm, final String iri)
            throws Exception {
        final JTMTopicMapReader reader = new JTMTopicMapReader(tm, new URL(iri).openStream(), iri);
        return reader;
    }

}