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
package com.semagia.tmapix.io;

import org.tinytim.mio.TinyTimMapInputHandler;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystemFactory;

import com.semagia.mio.IMapHandler;

import junit.framework.TestCase;

/**
 * Tests against the {@link MapHandlerFactory}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev$ - $Date$
 */
public class TestMapHandlerFactory extends TestCase implements ITestConstants {
    
    private String _tmSysFactoryValue;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _tmSysFactoryValue = System.getProperty(TMAPIChooser.TMAPI_SYSTEM_FACTORY);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if (_tmSysFactoryValue != null) {
            System.setProperty(TMAPIChooser.TMAPI_SYSTEM_FACTORY, _tmSysFactoryValue);
        }
        else {
            System.clearProperty(TMAPIChooser.TMAPI_SYSTEM_FACTORY);
        }
    }

    private IMapHandler makeMapHandler(String systemFactoryImpl) throws Exception {
        return makeMapHandler(makeTopicMap(systemFactoryImpl));
    }

    private IMapHandler makeMapHandler(TopicMap topicMap) {
        return MapHandlerFactory.createMapHandler(topicMap);
    }

    private TopicMap makeTopicMap(String systemFactoryImpl) throws Exception {
        System.setProperty(TMAPIChooser.TMAPI_SYSTEM_FACTORY, systemFactoryImpl);
        return TopicMapSystemFactory.newInstance()
                .newTopicMapSystem()
                .createTopicMap("http://tmapix.semagia.com/test-map");
    }

    public void testDetectTinyTim() throws Exception {
        assertTrue(makeMapHandler(TMAPIChooser.TINYTIM_SYSTEM_FACTORY) instanceof TinyTimMapInputHandler);
    }

    public void testDetectOntopia() throws Exception {
        assertTrue(makeMapHandler(TMAPIChooser.ONTOPIA_SYSTEM_FACTORY) instanceof OntopiaMapHandler);
    }

    public void testUnknownTMAPIImplementation() throws Exception {
        assertTrue(makeMapHandler(GENERIC_SYSTEM_FACTORY_TINYTIM) instanceof TMAPIMapHandler);
    }

    public void testUnknownTMAPIImplementation2() throws Exception {
        assertTrue(makeMapHandler(GENERIC_SYSTEM_FACTORY_ONTOPIA) instanceof TMAPIMapHandler);
    }

    public void testIgnoreTinyTim() throws Exception {
        assertTrue(MapHandlerFactory.createTMAPIMapHandler(makeTopicMap(TMAPIChooser.TINYTIM_SYSTEM_FACTORY)) instanceof TMAPIMapHandler);
    }

    public void testIgnoreOntopia() throws Exception {
        assertTrue(MapHandlerFactory.createTMAPIMapHandler(makeTopicMap(TMAPIChooser.ONTOPIA_SYSTEM_FACTORY)) instanceof TMAPIMapHandler);
    }

}
