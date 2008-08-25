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

import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.tmapi.core.Construct;
import org.tmapi.core.TopicMap;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class IidFunction implements Function {

    /* (non-Javadoc)
     * @see org.jaxen.Function#call(org.jaxen.Context, java.util.List)
     */
    @SuppressWarnings("unchecked")
    public Object call(Context ctx, List args) throws FunctionCallException {
        FunctionUtils.checkUnaryArgument(args);
        Object arg = args.get(0);
        FunctionUtils.checkString(arg);
        return evaluate(ctx, (String) arg);
    }

    public static Construct evaluate(Context ctx, String arg)
            throws FunctionCallException {
        TopicMap tm = FunctionUtils.getTopicMap(ctx.getNodeSet());
        return tm.getConstructByItemIdentifier(tm.createLocator(arg));
    }

}
