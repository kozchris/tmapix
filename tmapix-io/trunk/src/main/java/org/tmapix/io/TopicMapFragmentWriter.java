/*
 * Copyright 2009 Lars Heuer (heuer[at]semagia.com)
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

import java.io.IOException;

import org.tmapi.core.Topic;

/**
 * {@link TopicMapWriter} specialisation that is able to write a subset of 
 * topics from one or more topic maps.
 * <p>
 * Implementations should not assume that the topics originate from one and
 * the same topic map.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev:$ - $Date:$
 */
public interface TopicMapFragmentWriter extends TopicMapWriter {

    /**
     * Writes the specified topics.
     *
     * @param topics The topics to serialize.
     * @throws IOException In case of an error.
     */
    public void write(final Iterable<Topic> topics) throws IOException;

    /**
     * Writes the specified topics. 
     *
     * @param topics The topics to serialize.
     * @throws IOException In case of an error.
     */
    public void write(final Topic...topics) throws IOException;

}
