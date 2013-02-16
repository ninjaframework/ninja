/**
 * 
 */
package ninja;

import javax.servlet.ServletContext;

import com.google.inject.AbstractModule;
import com.google.inject.servlet.ServletModule;

/**
 * @author zoza
 *
 */
public abstract class NinjaAppAbstractModule extends AbstractModule{

private ServletContext servletContext;
    
    public NinjaAppAbstractModule(ServletContext servletContext) {
        this.setServletContext(servletContext);
    }
    
    @Override
    protected final void configure() {
       setup();
       install(setupServlets());
        
    }

    /**
     * @return the servletContext
     */
    public ServletContext getServletContext() {
        return servletContext;
    }
    /**
     * @param servletContext the servletContext to set
     */
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
    protected abstract void setup(); 
    protected abstract ServletModule setupServlets();

}
