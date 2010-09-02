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

import java.io.File;

import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

import org.tmapix.io.TopicMapReader;

/**
 * Abstract test to check if a parser rejects invalid input files.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 255 $ - $Date: 2010-05-19 15:33:26 +0200 (Mi, 19 Mai 2010) $
 */
public abstract class AbstractInvalidCXTMReaderTestCase {

    private final File _file;
    private TopicMapSystem _sys;

    public AbstractInvalidCXTMReaderTestCase(File file) {
        _file = file;
    }

    protected abstract TopicMapReader makeReader(final TopicMap tm, final String iri) throws Exception;

    @Before
    public void setUp() throws Exception {
        _sys = TopicMapSystemFactory.newInstance().newTopicMapSystem();
    }

    @Test
    public void testInvalidCXTM() throws Exception {
        final String iri = _file.toURI().toURL().toExternalForm();
        final TopicMap tm = _sys.createTopicMap(iri);
        final TopicMapReader reader  = makeReader(tm, iri);
        try {
            reader.read();
            fail("Expected an error while parsing " + iri);
        }
        catch (ModelConstraintException ex) {
            // Good.
            // The engine has thrown an exception
        }
        catch (TMAPIXParseException ex) {
            // Good.
            // Parser has thrown an exception
        }
    }

}
