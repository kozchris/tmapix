/*
 * Copyright 2009 Lars Heuer (heuer[at]semagia.com). All rights reserved.
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
package com.semagia.tmapix.io;

import org.tmapi.core.TopicMap;

import com.semagia.mio.IMapHandler;

/**
 * Factory for {@link IMapHandler}s.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class MapHandlerFactory {

    /**
     * Creates a {@link IMapHandler} for the specified topic map.
     *
     * @param topicMap A topic map.
     * @return A {@link IMapHandler} instance that acts upon the specified topic map.
     */
    public static IMapHandler createMapHandler(final TopicMap topicMap) {
        return TMAPIChooser.createMapHandler(topicMap);
    }

}
