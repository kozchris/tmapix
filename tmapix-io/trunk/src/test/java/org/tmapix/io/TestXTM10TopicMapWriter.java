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
 * Tests against the {@link XTM10TopicMapWriter}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
@RunWith(Parameterized.class)
public class TestXTM10TopicMapWriter extends AbstractCXTMWriterTestCase {

    public TestXTM10TopicMapWriter(File file, String inputDir,
            String referenceDir, boolean convertToTMDM) {
        super(file, inputDir, referenceDir, convertToTMDM);
    }

    @Parameters
    public static Collection<Object> makeTestCases() {
        final Collection<Object> result = new ArrayList<Object>(); 
        result.addAll(Filter.from("/cxtm/xtm1/")
                .using("xtm")
                .exclude("eliots-xtm-test.xtm", // topic map starts not with <topicMap
                        "subjid-escaping.xtm" // Uncertain about this one
                        ,
                         // Excluding these tms since they cause problems with iids
                         // Either the writer adds an iid or it exports not enough iids
                        "association-reifier.xtm",
                        "association-untyped.xtm",
                        "bug-53.xtm",
                        "bug-55.xtm",
                        "bug-56.xtm",
                        "bug-57.xtm",
                        "bug660.xtm",
                        "instanceof-equiv.xtm",
                        "itemid-association.xtm",
                        "itemid-name.xtm",
                        "itemid-occurrence.xtm",
                        "itemid-tm.xtm",
                        "itemid-variant.xtm",
                        "merge-indicator.xtm",
                        "merge-itemid.xtm",
                        "merge-subject.xtm",
                        "merge-subjid.xtm",
                        "merge-subjloc.xtm",
                        "merge-three-way.xtm",
                        "merge-topicref.xtm",
                        "mergemap-xmlbase.xtm",
                        "mergemap.xtm",
                        "mergemap2.xtm",
                        "name-duplicate-merge.xtm",
                        "name-scope-duplicate-merged.xtm",
                        "occurrence-scope-duplicate-merged.xtm",
                        "occurrences.xtm",
                        "reification-bug-1.xtm",
                        "reification-bug-2.xtm",
                        "resourcedata.xtm",
                        "subjectindref.xtm",
                        "tm-reifier.xtm",
                        "topic-as-subj-ind-1.xtm",
                        "topic-as-subj-ind-2.xtm",
                        "whitespace.xtm",
                        "xmlbase-empty-base.xtm",
                        "xmlbase-problem.xtm",
                        "xmlbase-problem2.xtm",
                        "xmlbase.xtm"
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
        final XTM10TopicMapWriter writer = new XTM10TopicMapWriter(out, iri);
        writer.setPrettify(true);
        return writer;
    }

    /* (non-Javadoc)
     * @see org.tmapix.io.AbstractCXTMWriterTestCase#getFileExtension()
     */
    @Override
    protected String getFileExtension() {
        return "xtm";
    }

}
