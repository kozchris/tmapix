/*
 * Copyright 2008 Lars Heuer (heuer[at]semagia.com)
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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.tmapi.core.TopicMap;

import com.semagia.mio.Property;
import com.semagia.mio.Source;

/**
 * Common superclass for {@link RDFTopicMapReader}s.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
abstract class AbstractRDFTopicMapReader extends AbstractTopicMapReader
        implements RDFTopicMapReader {

    /**
     * 
     *
     * @param topicMap
     * @param syntax
     * @param source
     * @param docIRI
     * @throws IOException
     */
    protected AbstractRDFTopicMapReader(final TopicMap topicMap,
            final com.semagia.mio.Syntax syntax, final File source, final String docIRI)
            throws IOException {
        super(topicMap, syntax, source, docIRI);
        setMappingSource(source);
        setMappingSourceSyntax(_toRDFSyntax(syntax));
    }

    /**
     * 
     * 
     * @param topicMap
     * @param syntax
     * @param source
     * @throws IOException
     */
    protected AbstractRDFTopicMapReader(final TopicMap topicMap,
            final com.semagia.mio.Syntax syntax, final File source) throws IOException {
        super(topicMap, syntax, source);
        setMappingSource(source);
        setMappingSourceSyntax(_toRDFSyntax(syntax));
    }

    /**
     * 
     *
     * @param topicMap
     * @param syntax
     * @param source
     * @param docIRI
     */
    protected AbstractRDFTopicMapReader(final TopicMap topicMap,
            final com.semagia.mio.Syntax syntax, final InputStream source, final String docIRI) {
        super(topicMap, syntax, source, docIRI);
    }

    /**
     * 
     *
     * @param topicMap
     * @param syntax
     * @param source
     */
    protected AbstractRDFTopicMapReader(final TopicMap topicMap,
            final com.semagia.mio.Syntax syntax, final Source source) {
        super(topicMap, syntax, source);
        if (source.getIRI() != null) {
            setMappingSource(source.getIRI());
            setMappingSourceSyntax(_toRDFSyntax(syntax));
        }
    }

    /* (non-Javadoc)
     * @see org.tinytim.mio.RDFTopicMapReader#setMappingSource(java.io.File)
     */
    public void setMappingSource(File file) {
        try {
            setMappingSource(file.toURI().toURL());
        }
        catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.tmapix.io.RDFTopicMapReader#setMappingSource(java.net.URL)
     */
    public void setMappingSource(URL url) {
        setMappingSource(url.toExternalForm());
    }

    /* (non-Javadoc)
     * @see org.tmapix.io.RDFTopicMapReader#setMappingSource(java.lang.String)
     */
    public void setMappingSource(String iri) {
        _deserializer.setProperty(Property.RDF2TM_MAPPING_IRI, iri);
    }

    /* (non-Javadoc)
     * @see org.tmapix.io.RDFTopicMapReader#getMappingSource()
     */
    public String getMappingSource() {
        return (String) _deserializer.getProperty(Property.RDF2TM_MAPPING_IRI);
    }

    /* (non-Javadoc)
     * @see org.tmapix.io.RDFTopicMapReader#getMappingSourceSyntax()
     */
    public RDFSyntax getMappingSourceSyntax() {
        final com.semagia.mio.Syntax syntax = (com.semagia.mio.Syntax) _deserializer.getProperty(Property.RDF2TM_MAPPING_SYNTAX);
        return _toRDFSyntax(syntax); 
    }

    /* (non-Javadoc)
     * @see org.tmapix.io.RDFTopicMapReader#setMappingSourceSyntax(org.tmapix.io.RDFSyntax)
     */
    public void setMappingSourceSyntax(RDFSyntax syntax) {
        _deserializer.setProperty(Property.RDF2TM_MAPPING_SYNTAX, _toMIOSyntax(syntax));
    }

    /**
     * Returns the {@link RDFSyntax} equivalent for the provided mio.Syntax.
     *
     * @param syntax The syntax to convert.
     * @return The MappingSyntax instance.
     */
    private static RDFSyntax _toRDFSyntax(final com.semagia.mio.Syntax syntax) {
        if (syntax == null) {
            return null;
        }
        if (com.semagia.mio.Syntax.N3.equals(syntax)) {
            return RDFSyntax.N3;
        }
        if (com.semagia.mio.Syntax.NTRIPLES.equals(syntax)) {
            return RDFSyntax.NTRIPLES;
        }
        if (com.semagia.mio.Syntax.RDFXML.equals(syntax)) {
            return RDFSyntax.RDFXML;
        }
        if (com.semagia.mio.Syntax.TRIG.equals(syntax)) {
            return RDFSyntax.TRIG;
        }
        if (com.semagia.mio.Syntax.TRIX.equals(syntax)) {
            return RDFSyntax.TRIX;
        }
        if (com.semagia.mio.Syntax.TURTLE.equals(syntax)) {
            return RDFSyntax.TURTLE;
        }
        if (com.semagia.mio.Syntax.CRTM.equals(syntax)) {
            return RDFSyntax.CRTM;
        }
        throw new RuntimeException("Internal error, no MappingSyntax found for " + syntax.getName());
    }


    /**
     * Returns the mio.Syntax equivalent for the provided {@link RDFSyntax}.
     *
     * @param syntax The syntax to translate.
     * @return The translated syntax.
     */
    private static com.semagia.mio.Syntax _toMIOSyntax(final RDFSyntax syntax) {
        if (syntax == null) {
            return null;
        }
        switch (syntax) {
            case N3: return com.semagia.mio.Syntax.N3;
            case NTRIPLES: return com.semagia.mio.Syntax.NTRIPLES;
            case RDFXML: return com.semagia.mio.Syntax.RDFXML;
            case TRIG: return com.semagia.mio.Syntax.TRIG;
            case TRIX: return com.semagia.mio.Syntax.TRIX;
            case TURTLE: return com.semagia.mio.Syntax.TURTLE;
            case CRTM: return com.semagia.mio.Syntax.CRTM;
        }
        throw new RuntimeException("Internal error, no mio.Syntax found for " + syntax);
    }
}
