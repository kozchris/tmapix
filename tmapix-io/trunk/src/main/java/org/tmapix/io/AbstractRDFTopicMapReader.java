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
import com.semagia.mio.Syntax;

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
            final Syntax syntax, final File source, final String docIRI)
            throws IOException {
        super(topicMap, syntax, source, docIRI);
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
            final Syntax syntax, final File source) throws IOException {
        super(topicMap, syntax, source);
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
            final Syntax syntax, final InputStream source, final String docIRI) {
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
            final Syntax syntax, final Source source) {
        super(topicMap, syntax, source);
    }

    /* (non-Javadoc)
     * @see org.tinytim.mio.RDFTopicMapReader#setMappingSource(java.io.File)
     */
    @Override
    public void setMappingSource(File file) {
        try {
            setMappingSource(file.toURI().toURL());
        }
        catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.tinytim.mio.RDFTopicMapReader#setMappingSource(java.net.URL)
     */
    @Override
    public void setMappingSource(URL url) {
        setMappingSource(url.toExternalForm());
    }

    /* (non-Javadoc)
     * @see org.tinytim.mio.RDFTopicMapReader#setMappingSource(java.lang.String)
     */
    @Override
    public void setMappingSource(String iri) {
        _deserializer.setProperty(Property.RDF2TM_MAPPING_IRI, iri);
    }

    /* (non-Javadoc)
     * @see org.tinytim.mio.RDFTopicMapReader#getMappingSource()
     */
    @Override
    public String getMappingSource() {
        return (String) _deserializer.getProperty(Property.RDF2TM_MAPPING_IRI);
    }

    /* (non-Javadoc)
     * @see org.tinytim.mio.RDFTopicMapReader#getMappingSourceSyntax()
     */
    @Override
    public RDFSyntax getMappingSourceSyntax() {
        final Syntax syntax = (Syntax) _deserializer.getProperty(Property.RDF2TM_MAPPING_SYNTAX);
        return _toMappingSyntax(syntax); 
    }

    /* (non-Javadoc)
     * @see org.tinytim.mio.RDFTopicMapReader#setMappingSourceSyntax(org.tinytim.mio.RDFTopicMapReader.MappingSyntax)
     */
    @Override
    public void setMappingSourceSyntax(RDFSyntax syntax) {
        _deserializer.setProperty(Property.RDF2TM_MAPPING_SYNTAX, _fromMappingSyntax(syntax));
    }

    /**
     * Returns the {@link RDFSyntax} equivalent for the provided mio.Syntax.
     *
     * @param syntax The syntax to convert.
     * @return The MappingSyntax instance.
     */
    private static RDFSyntax _toMappingSyntax(final Syntax syntax) {
        if (syntax == null) {
            return null;
        }
        if (Syntax.N3.equals(syntax)) {
            return RDFSyntax.N3;
        }
        if (Syntax.NTRIPLES.equals(syntax)) {
            return RDFSyntax.NTRIPLES;
        }
        if (Syntax.RDFXML.equals(syntax)) {
            return RDFSyntax.RDFXML;
        }
        if (Syntax.TRIG.equals(syntax)) {
            return RDFSyntax.TRIG;
        }
        if (Syntax.TRIX.equals(syntax)) {
            return RDFSyntax.TRIX;
        }
        if (Syntax.TURTLE.equals(syntax)) {
            return RDFSyntax.TURTLE;
        }
        throw new RuntimeException("Internal error, no MappingSyntax found for " + syntax.getName());
    }


    /**
     * Returns the mio.Syntax equivalent for the provided {@link RDFSyntax}.
     *
     * @param syntax The syntax to translate.
     * @return The translated syntax.
     */
    private static Syntax _fromMappingSyntax(final RDFSyntax syntax) {
        if (syntax == null) {
            return null;
        }
        switch (syntax) {
            case N3: return Syntax.N3;
            case NTRIPLES: return Syntax.NTRIPLES;
            case RDFXML: return Syntax.RDFXML;
            case TRIG: return Syntax.TRIG;
            case TRIX: return Syntax.TRIX;
            case TURTLE: return Syntax.TURTLE;
        }
        throw new RuntimeException("Internal error, no mio.Syntax found for " + syntax);
    }
}
