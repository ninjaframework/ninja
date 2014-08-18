package conf;


// Just a dummy for testing.
// Allows to check that custom Ninja serlvet module in user's conf directory
// is pucked up by the bootstrap process correctly
public class ServletModule extends com.google.inject.servlet.ServletModule {
    
    @Override
    protected void configureServlets() {

        bind(DummyInterfaceForTesting.class).to(DummyClassForTesting.class);

    }
    
    public static interface DummyInterfaceForTesting {
    }
    
    public static class DummyClassForTesting implements DummyInterfaceForTesting {
    }
    
}
