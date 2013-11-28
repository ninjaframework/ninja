package conf;

import javax.inject.Singleton;

import ninja.lifecycle.Start;
import ninja.utils.NinjaProperties;

import com.google.inject.Inject;

import dao.SetupDao;

@Singleton
public class StartupActions {
    
    @Inject
    SetupDao setupDao;

    private NinjaProperties ninjaProperties;

    @Inject
    public StartupActions(NinjaProperties ninjaProperties) {
        this.ninjaProperties = ninjaProperties;
    }
    
    @Start(order=100)
    public void generateDummyDataWhenInTest() {
        
        if (!ninjaProperties.isProd()) {
            
            setupDao.setup();
            
        }
        
    }

}
