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
import java.net.URI;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.tmapi.core.TopicMap;

/**
 * Tests against the {@link CTMTopicMapReader}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 174 $ - $Date: 2010-07-27 10:55:28 +0200 (Di, 27 Jul 2010) $
 */
@RunWith(Parameterized.class)
public class TestCTMTopicMapReader extends AbstractValidCXTMReaderTestCase {

    public TestCTMTopicMapReader(File file, String inputDir, String referenceDir, boolean convertToTMDM) {
        super(file, inputDir, referenceDir, convertToTMDM);
    }

    @Parameters
    public static Collection<Object> makeTestCases() {
        return CXTMTestUtils.findCXTMTests("ctm", "/cxtm/ctm/");
    }

    @Override
    protected TopicMapReader makeReader(final TopicMap tm, final String iri)
            throws Exception {
        final CTMTopicMapReader reader = new CTMTopicMapReader(tm, new File(new URI(iri)), iri);
        return reader;
    }

}