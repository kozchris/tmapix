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
package com.semagia.tmapix.filter.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public class ChainIterator<T> implements Iterator<T> {

    private Iterator<T>[] _iterators;
    private int _current;

    @SuppressWarnings("unchecked")
    public ChainIterator(Iterable<T> ... iterables) {
        if (iterables == null || iterables.length == 0) {
            throw new IllegalArgumentException();
        }
        _iterators = new Iterator[iterables.length];
        for (int i=0; i<iterables.length; i++) {
            _iterators[i] = iterables[i].iterator();
        }
    }

    public ChainIterator(Iterator<T> ... iterators) {
        if (iterators == null || iterators.length == 0) {
            throw new IllegalArgumentException();
        }
        _iterators = iterators;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        boolean next = false;
        if (_current < _iterators.length) {
            next = _iterators[_current].hasNext();
            while (!next && _current+1 < _iterators.length) {
                _current++;
                next = _iterators[_current].hasNext();
            }
        }
        return next;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public T next() {
        if (_current >= _iterators.length) {
            throw new NoSuchElementException();
        }
        return _iterators[_current].next();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
