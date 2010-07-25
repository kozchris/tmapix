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
import java.util.ArrayList;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.tmapi.core.TopicMap;
import org.tmapix.io.CXTMTestUtils.Filter;

/**
 * Tests against the {@link TMXMLTopicMapReader}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 255 $ - $Date: 2010-05-19 15:33:26 +0200 (Mi, 19 Mai 2010) $
 */
@RunWith(Parameterized.class)
public class TestXTMTopicMapReaderValidating extends AbstractValidCXTMReaderTestCase {

    public TestXTMTopicMapReaderValidating(File file, String inputDir, String referenceDir, boolean convertToTMDM) {
        super(file, inputDir, referenceDir, convertToTMDM);
    }

    private static Collection<Object> _makeXTM10TestCases() {
        return Filter.from("/cxtm/xtm1/")
                    .using("xtm")
                    .exclude("eliots-xtm-test.xtm", // topic map starts not with <topicMap
                             "subjid-escaping.xtm" // Uncertain about this one
                            )
                            .convertToTMDM()
                            .filter();
    }

    private static Collection<Object> _makeXTM2TestCases() {
        return Filter.from("/cxtm/xtm2/", "/cxtm/xtm21/")
                      .using("xtm")
                      .exclude("subjid-escaping.xtm" // Uncertain about this one
                              )
                      .filter();
    }

    @Parameters
    public static Collection<Object> makeTestCases() {
        final Collection<Object> result = new ArrayList<Object>(); 
        result.addAll(_makeXTM10TestCases());
        result.addAll(_makeXTM2TestCases());
        return result;
    }

    @Override
    protected TopicMapReader makeReader(final TopicMap tm, final String iri)
            throws Exception {
        final XTMTopicMapReader reader = new XTMTopicMapReader(tm, new URL(iri).openStream(), iri);
        reader.setValidation(true);
        return reader;
    }

}