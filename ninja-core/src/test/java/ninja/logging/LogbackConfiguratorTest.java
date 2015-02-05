/**
 * Copyright (C) 2012-2015 the original author or authors.
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

package ninja.logging;

import ninja.utils.NinjaProperties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LogbackConfiguratorTest {
    
    @Mock
    NinjaProperties ninjaProperties;


    @Test
    public void testThatASystemPropertySkipsInitializationFromApplicationConf() {
        
        System.setProperty(
                LogbackConfigurator.LOGBACK_CONFIGURATION_FILE_PROPERTY, 
                "/srv/logback_prod.xml");
        
        LogbackConfigurator.initConfiguration(ninjaProperties);
        
        Mockito.verify(ninjaProperties, Mockito.never()).get(Matchers.anyString());
        
        //clean up
        System.clearProperty(LogbackConfigurator.LOGBACK_CONFIGURATION_FILE_PROPERTY);  
        
    }
    
    @Test
    public void testThatConfigurationFromApplicationConfWorks() {
        
        Mockito.when(ninjaProperties.get(
                LogbackConfigurator.LOGBACK_CONFIGURATION_FILE_PROPERTY))
                .thenReturn("logback_production.xml");
        
        LogbackConfigurator.initConfiguration(ninjaProperties);
        
        Mockito.verify(ninjaProperties).get(LogbackConfigurator.LOGBACK_CONFIGURATION_FILE_PROPERTY);

        
    }
    
}
