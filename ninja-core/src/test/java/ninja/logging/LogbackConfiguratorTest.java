package ninja.logging;

import ninja.utils.NinjaProperties;
import org.junit.Test;
import static org.junit.Assert.*;
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
