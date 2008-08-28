/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 * 
 * The Original Code is Semagia TMAPIX.
 * 
 * The Initial Developer of the Original Code is Semagia <http://www.semagia.com/>.
 * All Rights Reserved.
 * 
 * Contributer(s): Lars Heuer <heuer[at]semagia.com>
 *
 */
package com.semagia.tmapix.filter.xpath;

import java.util.List;

import org.jaxen.Context;
import org.jaxen.ContextSupport;
import org.jaxen.FunctionContext;
import org.jaxen.JaxenException;
import org.jaxen.JaxenHandler;
import org.jaxen.NamedAccessNavigator;
import org.jaxen.NamespaceContext;
import org.jaxen.Navigator;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.SimpleVariableContext;
import org.jaxen.VariableContext;
import org.jaxen.XPath;
import org.jaxen.XPathFunctionContext;
import org.jaxen.expr.XPathExpr;
import org.jaxen.saxpath.SAXPathException;
import org.jaxen.saxpath.XPathReader;
import org.jaxen.saxpath.helpers.XPathReaderFactory;
import org.jaxen.util.SingletonList;
import org.tmapi.core.Construct;

import com.semagia.tmapix.filter.FilterMatchException;
import com.semagia.tmapix.filter.IFilter;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
final class MapXPathFilter<T> implements IFilter<T>, XPath {

    private static final long serialVersionUID = 2094579141495856043L;

    private ContextSupport _support;
    private NamedAccessNavigator _navigator;
    private XPathExpr _xpathExpr;
    private NamespaceContext _nsContext;
    private VariableContext _varContext;
    

    MapXPathFilter(final String xpath, final NamedAccessNavigator navigator) throws JaxenException {
        XPathReader reader;
        try {
            reader = XPathReaderFactory.createReader();
        }
        catch (SAXPathException ex) {
            throw new JaxenException(ex);
        }
        JaxenHandler handler = new JaxenHandler();
        handler.setXPathFactory(new MapPathFactory());
        reader.setXPathHandler(handler);
        try {
            reader.parse(xpath);
        }
        catch (SAXPathException ex) {
            throw new JaxenException(ex);
        }
        _xpathExpr = handler.getXPathExpr();
        _navigator = navigator;
        _nsContext = new SimpleNamespaceContext();
        _varContext = new SimpleVariableContext();
        _support = new ContextSupport(
                _nsContext,
                getFunctionContext(), 
                _varContext,
                _navigator);
        addNamespace("xsd", "http://www.w3.org/2001/XMLSchema#");
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.filter.IFilter#match(org.tmapi.core.Construct)
     */
    @SuppressWarnings("unchecked")
    public Iterable<T> match(final Construct context) throws FilterMatchException {
        try {
            return (Iterable<T>) selectList(context);
        }
        catch (JaxenException ex) {
            throw new FilterMatchException(ex);
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.tmapix.filter.IFilter#firstMatch(org.tmapi.core.Construct)
     */
    @SuppressWarnings("unchecked")
    public T matchOne(final Construct context)
            throws FilterMatchException {
        try {
            return (T) selectSingleNode(context);
        }
        catch (JaxenException ex) {
            throw new FilterMatchException(ex);
        }
    }

    public void addNamespace(String prefix, String uri) throws JaxenException {
        NamespaceContext nsContext = _nsContext;
        if (nsContext instanceof SimpleNamespaceContext) {
            ((SimpleNamespaceContext) nsContext).addNamespace(prefix, uri );
            return;
        }
        throw new JaxenException("Operation not permitted while using a non-simple namespace context.");
    }

    public boolean booleanValueOf(Object arg0) throws JaxenException {
        throw new UnsupportedOperationException();
    }

    public Object evaluate(Object context) throws JaxenException {
        return selectNodes(context);
    }

    public FunctionContext getFunctionContext() {
        return XPathFunctionContext.getInstance();
    }

    public NamespaceContext getNamespaceContext() {
        return _nsContext;
    }

    public Navigator getNavigator() {
        return _navigator;
    }

    public VariableContext getVariableContext() {
        return _varContext;
    }

    /* (non-Javadoc)
     * @see org.jaxen.XPath#selectNodes(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public List<T> selectNodes(Object ctxNode) throws JaxenException {
        return (List<T>) selectList(ctxNode);
    }

    /* (non-Javadoc)
     * @see org.jaxen.XPath#selectSingleNode(java.lang.Object)
     */
    public Object selectSingleNode(Object ctxNode) throws JaxenException {
        for(Object obj: selectList(ctxNode)) {
            return obj;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private List<Object> selectList(Object ctxNode) throws JaxenException {
        return _xpathExpr.asList(getContext(ctxNode));
    }

    @SuppressWarnings("unchecked")
    private Context getContext(Object node) {
        if (node instanceof Context) {
            return (Context) node;
        }
        Context fullContext = new Context(_support);
        if (node instanceof List) {
            fullContext.setNodeSet((List) node);
        }
        else {
            List list = new SingletonList(node);
            fullContext.setNodeSet(list);
        }
        return fullContext;
    }

    /* (non-Javadoc)
     * @see org.jaxen.XPath#setFunctionContext(org.jaxen.FunctionContext)
     */
    public void setFunctionContext(FunctionContext arg0) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.jaxen.XPath#setNamespaceContext(org.jaxen.NamespaceContext)
     */
    public void setNamespaceContext(NamespaceContext arg0) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.jaxen.XPath#setVariableContext(org.jaxen.VariableContext)
     */
    public void setVariableContext(VariableContext arg0) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.jaxen.XPath#numberValueOf(java.lang.Object)
     */
    public Number numberValueOf(Object arg0) throws JaxenException {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.jaxen.XPath#stringValueOf(java.lang.Object)
     */
    public String stringValueOf(Object arg0) throws JaxenException {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.jaxen.XPath#valueOf(java.lang.Object)
     */
    public String valueOf(Object arg0) throws JaxenException {
        throw new UnsupportedOperationException();
    }
}
