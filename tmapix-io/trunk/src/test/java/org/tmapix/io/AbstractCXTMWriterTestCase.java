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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

import org.tmapix.io.diff_match_patch.Patch;

import com.semagia.mio.MIOException;
import com.semagia.mio.Source;

/**
 * Abstract test to validate writers against CXTM files.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public abstract class AbstractCXTMWriterTestCase {

    private final String _inputDir;
    private final String _referenceDir;
    private final boolean _convertToTMDM;
    private final File _file;
    private TopicMapSystem _sys;

    public AbstractCXTMWriterTestCase(File file, String inputDir, String referenceDir, boolean convertToTMDM) {
        _inputDir = inputDir;
        _referenceDir = referenceDir;
        _file = file;
        _convertToTMDM = convertToTMDM;
    }

    private TopicMapReader _makeReader(final TopicMap tm, final String iri) throws Exception {
        final String ext = iri.substring(iri.lastIndexOf(".")+1);
        return _getReaderByFileExtension(tm, new Source(iri), ext);
    }

    private TopicMapReader _getReaderByFileExtension(final TopicMap tm, final Source src,
            final String ext) {
        TopicMapReader reader = null;
        if ("ltm".equalsIgnoreCase(ext)) {
            final LTMTopicMapReader reader_ = new LTMTopicMapReader(tm, src);
            reader_.setLegacyMode(true);
            reader = reader_;
        }
        else if ("xtm".equalsIgnoreCase(ext)) {
            final XTMTopicMapReader reader_ = new XTMTopicMapReader(tm, src);
            reader_.setValidation(true);
            reader = reader_;
        }
        else if ("jtm".equalsIgnoreCase(ext)) {
            reader = new JTMTopicMapReader(tm, src);
        }
        else if ("stm".equalsIgnoreCase(ext)) {
            reader = new SnelloTopicMapReader(tm, src);
        }
        else if ("xml".equalsIgnoreCase(ext)) {
            final TMXMLTopicMapReader reader_ = new TMXMLTopicMapReader(tm, src);
            reader_.setValidation(true);
            reader = reader_;
        }
        else if ("ctm".equalsIgnoreCase(ext)) {
            reader = new CTMTopicMapReader(tm, src);
        }
        else {
            throw new IllegalArgumentException("No reader found for '" + ext + "'");
        }
        return reader;
    }

    protected abstract TopicMapWriter makeWriter(final OutputStream out, final String iri) throws Exception;
    
    protected abstract String getFileExtension();

    private InputStream _makeInputStream(final ByteArrayOutputStream out) {
        return new ByteArrayInputStream(out.toByteArray());
    }

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

    @Before
    public void setUp() throws Exception {
        _sys = TopicMapSystemFactory.newInstance().newTopicMapSystem();
    }

    @Test
    public void testValidCXTM() throws Exception {
        final String iri = _file.toURI().toURL().toExternalForm();
        TopicMap tm = _sys.createTopicMap(iri);
        final String referenceFile = _file.toURI().toURL().getFile().replace("/" + _inputDir + "/", "/" + _referenceDir + "/") + ".cxtm";
        TopicMapReader reader = _makeReader(tm, iri);
        reader.read();
        if (_convertToTMDM) {
            XTM10Utils.convertToTMDM(tm);
        }
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final TopicMapWriter writer = makeWriter(out, iri);
        writer.write(tm);
        tm.remove();
        tm = _sys.createTopicMap(iri);
        final InputStream in = _makeInputStream(out);
        reader = _getReaderByFileExtension(tm, new Source(in, iri), getFileExtension());
        try {
            reader.read();
        }
        catch (Throwable ex) {
            if (ex instanceof TMAPIRuntimeException && ex.getCause() instanceof MIOException) {
                ex = ex.getCause();
            }
            fail("Parsing failed for <" + iri + "> \n" + _getStackTrace(ex) + "\nCause: " + _getStackTrace(ex.getCause()) + "\n --- Written topic map:\n" + out.toString("utf-8"));
        }
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        final TopicMapWriter cxtmWriter = CXTMWriterFactory.createCXTMTopicMapWriter(tm, result, iri);
        cxtmWriter.write(tm);
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
            fail(iri + ":\n" + dmp.patch_toText(patches) + "\n - - -\nExpected: " + expectedString + "\n, got: " + resultString + "\n Written topic map: \n" + out.toString("utf-8"));
        }
    }

}
