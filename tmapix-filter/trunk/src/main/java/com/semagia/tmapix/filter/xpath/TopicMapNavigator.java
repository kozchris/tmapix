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

import java.util.Iterator;

import org.jaxen.DefaultNavigator;
import org.jaxen.JaxenConstants;
import org.jaxen.NamedAccessNavigator;
import org.jaxen.UnsupportedAxisException;
import org.jaxen.XPath;
import org.jaxen.saxpath.SAXPathException;
import org.jaxen.util.SingleObjectIterator;
import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Name;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
@SuppressWarnings("serial")
final class TopicMapNavigator extends DefaultNavigator 
                implements NamedAccessNavigator {

    private static TopicMapNavigator _INSTANCE = new TopicMapNavigator();

    private TopicMapNavigator() {
        // noop.
    }

    public static TopicMapNavigator getInstance() {
        return _INSTANCE ;
    }

    /* (non-Javadoc)
     * @see org.jaxen.DefaultNavigator#getParentNode(java.lang.Object)
     */
    @Override
    public Object getParentNode(Object contextNode)
            throws UnsupportedAxisException {
        if (Utils.isConstruct(contextNode)) {
            return ((Construct) contextNode).getParent();
        }
        return super.getParentNode(contextNode);
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getAttributeName(java.lang.Object)
     */
    public String getAttributeName(Object arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getAttributeNamespaceUri(java.lang.Object)
     */
    public String getAttributeNamespaceUri(Object arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getAttributeQName(java.lang.Object)
     */
    public String getAttributeQName(Object arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getAttributeStringValue(java.lang.Object)
     */
    public String getAttributeStringValue(Object arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getCommentStringValue(java.lang.Object)
     */
    public String getCommentStringValue(Object arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getElementName(java.lang.Object)
     */
    public String getElementName(Object arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getElementNamespaceUri(java.lang.Object)
     */
    public String getElementNamespaceUri(Object arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getElementQName(java.lang.Object)
     */
    public String getElementQName(Object arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getElementStringValue(java.lang.Object)
     */
    public String getElementStringValue(Object arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getNamespacePrefix(java.lang.Object)
     */
    public String getNamespacePrefix(Object arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getNamespaceStringValue(java.lang.Object)
     */
    public String getNamespaceStringValue(Object arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getTextStringValue(java.lang.Object)
     */
    public String getTextStringValue(Object arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#isAttribute(java.lang.Object)
     */
    public boolean isAttribute(Object arg0) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#isComment(java.lang.Object)
     */
    public boolean isComment(Object arg0) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#isDocument(java.lang.Object)
     */
    public boolean isDocument(Object ctxNode) {
        return Utils.isTopicMap(ctxNode);
    }

    /* (non-Javadoc)
     * @see org.jaxen.DefaultNavigator#getDocumentNode(java.lang.Object)
     */
    @Override
    public Object getDocumentNode(Object ctxNode) {
        return ((Construct) ctxNode).getTopicMap();
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#isElement(java.lang.Object)
     */
    public boolean isElement(Object ctxNode) {
        return Utils.isConstruct(ctxNode);
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#isNamespace(java.lang.Object)
     */
    public boolean isNamespace(Object arg0) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#isProcessingInstruction(java.lang.Object)
     */
    public boolean isProcessingInstruction(Object arg0) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#isText(java.lang.Object)
     */
    public boolean isText(Object arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#parseXPath(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public XPath parseXPath(String xpath) throws SAXPathException {
        return new XPathFilter(xpath, this);
    }

    /* (non-Javadoc)
     * @see org.jaxen.NamedAccessNavigator#getAttributeAxisIterator(java.lang.Object, java.lang.String, java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public Iterator getAttributeAxisIterator(Object arg0, String arg1,
            String arg2, String arg3) throws UnsupportedAxisException {
       return null;
    }

    /* (non-Javadoc)
     * @see org.jaxen.NamedAccessNavigator#getChildAxisIterator(java.lang.Object, java.lang.String, java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public Iterator getChildAxisIterator(Object contextNode, String localName, String namespacePrefix,
            String namespaceURI) throws UnsupportedAxisException {
        //System.out.println("localName: " + localName + ", namespacePrefix: " + namespacePrefix + ", namespaceURI: " + namespaceURI);
        if (Utils.isTopic(contextNode)) {
            Topic topic = (Topic) contextNode;
            if ("roles".equals(localName)) {
                return topic.getRolesPlayed().iterator();
            }
            else if ("names".equals(localName)) {
                return topic.getNames().iterator();
            }
            else if ("occurrences".equals(localName)) {
                return topic.getOccurrences().iterator();
            }
        }
        else if (Utils.isAssociation(contextNode)) {
            if ("roles".equals(localName)) {
                return ((Association) contextNode).getRoles().iterator();
            }
            return IteratorUtils.getChildren((Association) contextNode);
        }
        else if (Utils.isTopicMap(contextNode)) {
            return IteratorUtils.getChildren((TopicMap) contextNode);
        }
        else if (Utils.isName(contextNode)) {
            return IteratorUtils.getChildren((Name) contextNode);
        }
        return JaxenConstants.EMPTY_ITERATOR;
    }

    /* (non-Javadoc)
     * @see org.jaxen.DefaultNavigator#getParentAxisIterator(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Iterator getParentAxisIterator(Object contextNode)
            throws UnsupportedAxisException {
        if (Utils.isConstruct(contextNode)) {
            return new SingleObjectIterator(((Construct) contextNode).getParent());
        }
        return super.getParentAxisIterator(contextNode);
    }

}
