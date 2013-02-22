package ninja;

import javax.servlet.ServletContextEvent;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * define in web.xml:
 * 
 * <listener>
 *   <listener-class>ninja.NinjaServletListener</listener-class>
 * </listener>
 *  
 * @author zoza
 * 
 */
public class NinjaServletListener extends GuiceServletContextListener {
    
private NinjaBootstap ninjaBootstap;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ninjaBootstap = new NinjaBootstap();
        ninjaBootstap.boot();
        super.contextInitialized(servletContextEvent);
    }
   
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ninjaBootstap.shutdown();
        super.contextDestroyed(servletContextEvent);
    }
   
    @Override
    protected Injector getInjector() {
        return ninjaBootstap.getInjector();
    }

}
