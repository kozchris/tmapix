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

/**
 * Provides constants for RDF syntaxes.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public enum RDFSyntax {
    
    /** 
     * <a href="http://www.w3.org/DesignIssues/Notation3.html">N3</a> syntax.
     */
    N3, 
    
    /** 
     * <a href="http://en.wikipedia.org/wiki/N-Triples">N-Triples</a> syntax.
     */
    NTRIPLES, 

    /** 
     * <a href="http://www.w3.org/TR/rdf-syntax-grammar/">RDF/XML</a> syntax.
     */
    RDFXML,

    /** 
     * <a href="http://www4.wiwiss.fu-berlin.de/bizer/TriG/">TriG</a> syntax.
     */
    TRIG,

    /** 
     * <a href="http://www.hpl.hp.com/techreports/2004/HPL-2004-56.html">TriX</a> syntax.
     */
    TRIX,
    
    /** 
     * <a href="http://www.w3.org/TeamSubmission/turtle/">Turtle</a> syntax.
     */
    TURTLE,

}
