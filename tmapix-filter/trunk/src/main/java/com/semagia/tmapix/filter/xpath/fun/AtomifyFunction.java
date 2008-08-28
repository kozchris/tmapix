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
package com.semagia.tmapix.filter.xpath.fun;

import java.util.ArrayList;
import java.util.List;

import static com.semagia.tmapix.filter.utils.TMAPIUtils.*;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.tmapi.core.Construct;
import org.tmapi.core.DatatypeAware;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Topic;

import com.semagia.tmapix.filter.voc.TMDM;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class AtomifyFunction implements Function {

    /* (non-Javadoc)
     * @see org.jaxen.Function#call(org.jaxen.Context, java.util.List)
     */
    @SuppressWarnings("unchecked")
    public Object call(Context ctx, List args) throws FunctionCallException {
        FunctionUtils.checkUnaryArgument(args);
        Object arg = args.get(0);
        return evaluate((List<Object>)arg);
    }

    public static Iterable<String> evaluate(List<Object> arg)
            throws FunctionCallException {
        List<String> result = new ArrayList<String>();
        String value = null;
        for (Object obj: arg) {
            value = null;
            if (isName(obj)) {
                value = ((Name) obj).getValue();
            }
            else if (isDatatypeAware(obj)) {
                value = ((DatatypeAware) obj).getValue();
            }
            else if (isTopic(obj)) {
                Topic topic = (Topic) obj;
                Locator loc = topic.getTopicMap().createLocator(TMDM.TOPIC_NAME);
                for (Name name: topic.getNames()) {
                    if (name.getType().getSubjectIdentifiers().contains(loc)) {
                        value = name.getValue();
                    }
                }
            }
            else if (isLocator(obj)) {
                value = ((Locator) obj).getReference();
            }
            if (value == null) {
                value = isConstruct(obj) ? ((Construct) obj).getId() : obj.toString();
            }
            result.add(value);
        }
        return result;
    }

}