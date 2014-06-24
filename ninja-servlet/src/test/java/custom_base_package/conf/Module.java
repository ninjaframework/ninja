package custom_base_package.conf;

import com.google.inject.AbstractModule;


// Just a dummy for testing.
// Allows to check that custom Ninja module in user's conf directory
// works properly.
public class Module extends AbstractModule {

  
    @Override
    protected void configure() {       

        bind(DummyInterfaceForTesting.class).to(DummyClassForTesting.class);

    }
    
    public static interface DummyInterfaceForTesting {
    }
    
    public static class DummyClassForTesting implements DummyInterfaceForTesting {
    }

}
