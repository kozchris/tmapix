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
 * Tests against the {@link CTMTopicMapWriter}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
@RunWith(Parameterized.class)
public class TestCTMTopicMapWriter extends AbstractCXTMWriterTestCase {

    public TestCTMTopicMapWriter(File file, String inputDir,
            String referenceDir, boolean convertToTMDM) {
        super(file, inputDir, referenceDir, convertToTMDM);
    }

    @Parameters
    public static Collection<Object> makeTestCases() {
        final Collection<Object> result = new ArrayList<Object>(); 
        result.addAll(Filter.from("/cxtm/xtm2/", "/cxtm/xtm21/")
                .using("xtm")
                .exclude("subjid-escaping.xtm",
                        // Constructs != topic which have an iid
                        "association-duplicate-iid.xtm",
                        "association-duplicate-iid2.xtm",
                        "association-duplicate-iid3.xtm",
                        "association-duplicate-reified2.xtm",
                        "association-duplicate-reified3.xtm",
                        "association-duplicate-reified4.xtm",
                        "itemid-association.xtm",
                        "itemid-name.xtm",
                        "itemid-occurrence.xtm",
                        "itemid-role.xtm",
                        "itemid-tm.xtm",
                        "itemid-variant.xtm",
                        "mergemap-itemid.xtm",
                        "name-duplicate-iid.xtm",
                        "name-duplicate-reified3.xtm",
                        "name-duplicate-reified4.xtm",
                        "occurrence-duplicate-iid.xtm",
                        "occurrence-duplicate-iid2.xtm",
                        "role-duplicate-iid.xtm",
                        "role-duplicate-iid2.xtm",
                        "variant-duplicate-iid.xtm"
                        ).filter());
        result.addAll(Filter.from("/cxtm/tmxml/")
                .using("xml")
                .exclude("occurrence-datetime.xml" // Parser fails :(
                        ).filter());
        result.addAll(CXTMTestUtils.makeSnelloTestCases());
        result.addAll(Filter.from("/cxtm/ltm/")
                .using("ltm")
                .exclude(// Constructs != topic which have an iid
                        "mergextm.ltm",
                        "mio-bug-11.ltm",
                        "reify-association-1.3.ltm",
                        "reify-basename-1.3.ltm",
                        "reify-occurrence-1.3.ltm",
                        "reify-role-1.3.ltm",
                        "reify-variant-1.3.ltm",
                        "reifytm-1.3.ltm"
                        ).filter());
        return result;
    }
    
    /* (non-Javadoc)
     * @see org.tmapix.io.AbstractCXTMWriterTestCase#makeWriter(org.tmapi.core.TopicMap, java.lang.String)
     */
    @Override
    protected TopicMapWriter makeWriter(final OutputStream out, String iri)
            throws Exception {
        final CTMTopicMapWriter writer = new CTMTopicMapWriter(out, iri);
        writer.setExportItemIdentifiers(true);
        return writer;
    }

    /* (non-Javadoc)
     * @see org.tmapix.io.AbstractCXTMWriterTestCase#getFileExtension()
     */
    @Override
    protected String getFileExtension() {
        return "ctm";
    }

}
