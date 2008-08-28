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

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.tmapi.core.Scoped;

import com.semagia.tmapix.filter.utils.TMAPIUtils;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class InUCSFunction implements Function {

    /* (non-Javadoc)
     * @see org.jaxen.Function#call(org.jaxen.Context, java.util.List)
     */
    @SuppressWarnings("unchecked")
    public Object call(Context ctx, List args) throws FunctionCallException {
        FunctionUtils.checkUnaryArgument(args);
        Object arg = args.get(0);
        return evaluate((List<Object>)arg);
    }

    public static Iterable<Boolean> evaluate(List<Object> arg)
            throws FunctionCallException {
        List<Boolean> result = new ArrayList<Boolean>();
        for (Object obj: arg) {
            if (!TMAPIUtils.isScoped(obj)) {
                throw new FunctionCallException("Expected scoped statements");
            }
            result.add(Boolean.valueOf(((Scoped) obj).getScope().isEmpty()));
        }
        return result;
    }

}
