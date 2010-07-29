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

/**
 * Tests against the {@link LTMTopicMapReader}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
@RunWith(Parameterized.class)
public class TestLTMTopicMapReaderInvalid extends AbstractInvalidCXTMReaderTestCase {

    public TestLTMTopicMapReaderInvalid(final File file) {
        super(file);
    }

    @Parameters
    public static Collection<Object> makeTestCases() {
        return CXTMTestUtils.findInvalidCXTMTests("ltm", "/cxtm/ltm/");
    }

    @Override
    protected TopicMapReader makeReader(final TopicMap tm, final String iri)
            throws Exception {
        final LTMTopicMapReader reader = new LTMTopicMapReader(tm, new URL(iri).openStream(), iri);
        reader.setLegacyMode(true);
        return reader;
    }

}