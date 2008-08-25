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

import static com.semagia.tmapix.filter.xpath.ChildAxis.*;
import static com.semagia.tmapix.filter.xpath.Utils.*;

import org.jaxen.DefaultNavigator;
import org.jaxen.JaxenConstants;
import org.jaxen.NamedAccessNavigator;
import org.jaxen.UnsupportedAxisException;
import org.jaxen.XPath;
import org.jaxen.saxpath.SAXPathException;
import org.jaxen.util.SingleObjectIterator;
import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.DatatypeAware;
import org.tmapi.core.Name;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.Typed;

import com.semagia.tmapix.filter.utils.ChainIterator;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
@SuppressWarnings("serial")
final class MapNavigator extends DefaultNavigator 
                implements NamedAccessNavigator {

    private static MapNavigator _INSTANCE = new MapNavigator();

    private MapNavigator() {
        // noop.
    }

    public static MapNavigator getInstance() {
        return _INSTANCE ;
    }

    /* (non-Javadoc)
     * @see org.jaxen.DefaultNavigator#getParentNode(java.lang.Object)
     */
    @Override
    public Object getParentNode(Object ctxNode)
            throws UnsupportedAxisException {
        if (isConstruct(ctxNode)) {
            return ((Construct) ctxNode).getParent();
        }
        return super.getParentNode(ctxNode);
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getAttributeName(java.lang.Object)
     */
    public String getAttributeName(Object arg0) {
        return "";
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getAttributeNamespaceUri(java.lang.Object)
     */
    public String getAttributeNamespaceUri(Object arg0) {
        return "";
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getAttributeQName(java.lang.Object)
     */
    public String getAttributeQName(Object arg0) {
        return "";
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getAttributeStringValue(java.lang.Object)
     */
    public String getAttributeStringValue(Object arg0) {
        return "";
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getCommentStringValue(java.lang.Object)
     */
    public String getCommentStringValue(Object arg0) {
        return "";
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getElementName(java.lang.Object)
     */
    public String getElementName(Object ctxNode) {
        if (isTopic(ctxNode)) {
            return "topic";
        }
        if (isAssociation(ctxNode)) {
            return "association";
        }
        if (isRole(ctxNode)) {
            return "role";
        }
        if (isOccurrence(ctxNode)) {
            return "occurrence";
        }
        if (isName(ctxNode)) {
            return "name";
        }
        if (isVariant(ctxNode)) {
            return "variant";
        }
        if (isTopicMap(ctxNode)) {
            return "topic map";
        }
        return "<unknown-element>";
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getElementNamespaceUri(java.lang.Object)
     */
    public String getElementNamespaceUri(Object arg0) {
        return "";
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getElementQName(java.lang.Object)
     */
    public String getElementQName(Object arg0) {
        return "";
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getElementStringValue(java.lang.Object)
     */
    public String getElementStringValue(Object obj) {
        if (isConstruct(obj)) {
            return ((Construct) obj).getId();
        }
        return obj.toString();
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getNamespacePrefix(java.lang.Object)
     */
    public String getNamespacePrefix(Object arg0) {
        return "";
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getNamespaceStringValue(java.lang.Object)
     */
    public String getNamespaceStringValue(Object arg0) {
        return "";
    }

    /* (non-Javadoc)
     * @see org.jaxen.Navigator#getTextStringValue(java.lang.Object)
     */
    public String getTextStringValue(Object obj) {
        return obj.toString();
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
        return isTopicMap(ctxNode);
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
        return isConstruct(ctxNode);
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
    public boolean isText(Object obj) {
        return obj instanceof String;
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
       throw new UnsupportedAxisException("Unsupported attribute axis '" + arg1 + "'");
    }

    /* (non-Javadoc)
     * @see org.jaxen.NamedAccessNavigator#getChildAxisIterator(java.lang.Object, java.lang.String, java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public Iterator getChildAxisIterator(Object ctxNode, String localName, String namespacePrefix,
            String namespaceURI) throws UnsupportedAxisException {
        // System.out.println("localName: " + localName + ", namespacePrefix: " + namespacePrefix + ", namespaceURI: " + namespaceURI);
        if (isTopic(ctxNode)) {
            Topic topic = (Topic) ctxNode;
            if (isRoleAxis(localName)) {
                return topic.getRolesPlayed().iterator();
            }
            else if (isNameAxis(localName)) {
                return topic.getNames().iterator();
            }
            else if (isOccurrenceAxis(localName)) {
                return topic.getOccurrences().iterator();
            }
            else if (isSubjectIdentifierAxis(localName)) {
                return topic.getSubjectIdentifiers().iterator();
            }
            else if (isSubjectLocatorAxis(localName)) {
                return topic.getSubjectLocators().iterator();
            }
            else if (isItemIdentifierAxis(localName)) {
                return topic.getItemIdentifiers().iterator();
            }
        }
        else if (isAssociation(ctxNode)) {
            Association assoc = (Association) ctxNode;
            if (isRoleAxis(localName)) {
                return assoc.getRoles().iterator();
            }
        }
        else if (isTopicMap(ctxNode)) {
            TopicMap tm = (TopicMap) ctxNode;
            if (isTopicAxis(localName)) {
                return tm.getTopics().iterator();
            }
            else if (isAssociationAxis(localName)) {
                return tm.getAssociations().iterator();
            }
        }
        else if (isPlayerAxis(localName) && isRole(ctxNode)) {
            return new SingleObjectIterator(((Role) ctxNode).getPlayer());
        }
        else if (isItemIdentifierAxis(localName) && isConstruct(ctxNode)) {
            return ((Construct) ctxNode).getItemIdentifiers().iterator();
        }
        else if (isReifierAxis(localName) && isReifiable(ctxNode)) {
            return new SingleObjectIterator(((Reifiable) ctxNode).getReifier());
        }
        else if (isTypeAxis(localName) && isTyped(ctxNode)) {
            return new SingleObjectIterator(((Typed) ctxNode).getType());
        }
        else if (isScopeAxis(localName) && isScoped(ctxNode)) {
            return ((Scoped) ctxNode).getScope().iterator();
        }
        else if (isValueAxis(localName)) {
            if (isName(ctxNode)) {
                return new SingleObjectIterator(((Name) ctxNode).getValue());
            }
            else if (isDatatypeAware(ctxNode)) {
                return new SingleObjectIterator(((DatatypeAware) ctxNode).getValue());
            }
        }
        else if (isDatatypeAxis(localName) && isDatatypeAware(ctxNode)) {
            return new SingleObjectIterator(((DatatypeAware) ctxNode).getDatatype());
        }
        return JaxenConstants.EMPTY_ITERATOR;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator getChildAxisIterator(Object ctxNode)
            throws UnsupportedAxisException {
        // System.out.println("childAxisIterator: " +ctxNode);
        if (isTopic(ctxNode)) {
            Topic topic = (Topic) ctxNode;
            return new ChainIterator(topic.getOccurrences(), topic.getNames());
        }
        else if (isAssociation(ctxNode)) {
            return ((Association) ctxNode).getRoles().iterator();
        }
        else if (isTopicMap(ctxNode)) {
            TopicMap tm = (TopicMap) ctxNode;
            return new ChainIterator(tm.getTopics(), tm.getAssociations());
        }
        else if (isName(ctxNode)) {
            return ((Name) ctxNode).getVariants().iterator();
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
        if (isConstruct(contextNode)) {
            return new SingleObjectIterator(((Construct) contextNode).getParent());
        }
        return super.getParentAxisIterator(contextNode);
    }

}
