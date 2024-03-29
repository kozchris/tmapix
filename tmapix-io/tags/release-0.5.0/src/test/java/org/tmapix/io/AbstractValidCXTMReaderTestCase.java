/*
 * Copyright 2007 - 2010 Lars Heuer (heuer[at]semagia.com)
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

import org.tmapix.io.diff_match_patch.Patch;

import com.semagia.mio.MIOException;

/**
 * Abstract test to validate parsers against CXTM files.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 268 $ - $Date: 2010-05-23 17:39:56 +0200 (So, 23 Mai 2010) $
 */
public abstract class AbstractValidCXTMReaderTestCase {

    private final String _inputDir;
    private final String _referenceDir;
    private final boolean _convertToTMDM;
    private final File _file;
    private TopicMapSystem _sys;

    public AbstractValidCXTMReaderTestCase(File file, String inputDir, String referenceDir, boolean convertToTMDM) {
        _inputDir = inputDir;
        _referenceDir = referenceDir;
        _file = file;
        _convertToTMDM = convertToTMDM;
    }

    protected abstract TopicMapReader makeReader(final TopicMap tm, final String iri) throws Exception;

    private static String _getStackTrace(final Throwable t) {
        if (t == null) {
            return "null";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }

    /**
     * Called after parsing a file successfully (and checking that the
     * produced CXTM is correct).
     * 
     * Does nothing by default.
     *
     * @param reader The reader.
     */
    protected void afterParsing(final TopicMapReader reader) {
        // noop.
    }


    @Before
    public void setUp() throws Exception {
        _sys = TopicMapSystemFactory.newInstance().newTopicMapSystem();
    }

    @Test
    public void testValidCXTM() throws Exception {
        final String iri = _file.toURI().toURL().toExternalForm();
        final TopicMap tm = _sys.createTopicMap(iri);
        final String referenceFile = _file.toURI().toURL().getFile().replace("/" + _inputDir + "/", "/" + _referenceDir + "/") + ".cxtm";
        final TopicMapReader reader = makeReader(tm, iri);
        // Enclosed in try-catch block since we execute this test as 
        // parameterized JUnit test and these tests do not provide information about
        // the file name
        try {
            reader.read();
        }
        catch (Throwable ex) {
            if (ex instanceof TMAPIXParseException && ex.getCause() instanceof MIOException) {
                ex = ex.getCause();
            }
            fail("Parsing failed for <" + iri + "> \n" + _getStackTrace(ex) + "\nCause: " + _getStackTrace(ex.getCause()));
        }
        if (_convertToTMDM) {
            XTM10Utils.convertToTMDM(tm);
        }
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        final TopicMapWriter writer = CXTMWriterFactory.createCXTMTopicMapWriter(tm, result, iri);
        try {
            writer.write(tm);
        }
        catch (Throwable ex) {
            fail("CXTM serialisation failed for <" + iri + "> \n" + _getStackTrace(ex) + "\nCause: " + _getStackTrace(ex.getCause()));
        }
        final ByteArrayOutputStream expected = new ByteArrayOutputStream();
        final InputStream tmp = new FileInputStream(referenceFile);
        int b;
        while ((b = tmp.read()) != -1) {
            expected.write(b);
        }
        byte[] res = result.toByteArray();
        byte[] ref = expected.toByteArray();
        if (!Arrays.equals(res, ref)) {
            final String expectedString = expected.toString("utf-8");
            final String resultString = result.toString("utf-8");
            diff_match_patch dmp = new diff_match_patch();
            LinkedList<Patch> patches = dmp.patch_make(expectedString, resultString); 
            fail(iri + ":\n" + dmp.patch_toText(patches) + "\n - - -\nExpected: " + expectedString + "\n, got: " + resultString);
        }
        afterParsing(reader);
    }

}
