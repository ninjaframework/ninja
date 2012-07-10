package ninja;

import java.util.ArrayList;
import java.util.List;

import ninja.application.ApplicationRoutes;
import ninja.lifecycle.LifecycleService;
import ninja.lifecycle.LifecycleSupport;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class NinjaBootup {
    
    private final String APPLICATION_GUICE_MODULE_CONVENTION_LOCATION = "conf.Module";
    private final String ROUTES_CONVENTION_LOCATION = "conf.Routes";
	
	/**
	 * Main injector for the class.
	 */
	private Injector injector;
	
	public NinjaBootup() {
		try {		    
		    List<Module> modulesToLoad = new ArrayList<Module>();  

            // Bind lifecycle support
            modulesToLoad.add(LifecycleSupport.getModule());

			// Get base configuration of Ninja:
			Class ninjaConfigurationClass = Configuration.class;
			Module ninjaConfiguration = (Module) ninjaConfigurationClass.newInstance();
			modulesToLoad.add(ninjaConfiguration);

			// Load main application module:
			if (doesClassExist(APPLICATION_GUICE_MODULE_CONVENTION_LOCATION)) {
			    Class applicationConfigurationClass = Class.forName(APPLICATION_GUICE_MODULE_CONVENTION_LOCATION);
	            Module applicationConfiguration = (Module) applicationConfigurationClass.newInstance();
	            modulesToLoad.add(applicationConfiguration);
			}


			// And let the injector generate all instances and stuff:
			injector = Guice.createInjector(modulesToLoad);


	         // Init routes
            if (doesClassExist(ROUTES_CONVENTION_LOCATION)) {
                Class clazz = Class.forName(ROUTES_CONVENTION_LOCATION);
                ApplicationRoutes applicationRoutes = (ApplicationRoutes) injector.getInstance(clazz);
                
                //System.out.println("init routes");
                Router router = injector.getInstance(Router.class);
                
                applicationRoutes.init(router);
                router.compileRoutes();
            }
	

		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public Injector getInjector() {
		
		return injector;
		
	}

    public void shutdown() {
        injector.getInstance(LifecycleService.class).stop();
    }
	
	
	/**
	 * TODO => I want to live somewhere else...
	 * 
	 * 
	 */
	private boolean doesClassExist(String nameWithPackage) {
	    
	    boolean exists = false;
	    
	    try {
            Class.forName(nameWithPackage, false, this.getClass().getClassLoader());
            exists = true;
        } catch (ClassNotFoundException e) {
            exists = false;
        }
	    
	    return exists;
	    
	}

}
