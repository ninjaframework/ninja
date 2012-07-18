package ninja;

/**
 * That is virtually the main() class of your Application.
 *
 */
public interface NinjaModule {
    
    public void init();
    
    public void addNinjaModule(NinjaModuleRegistry ninjaModuleRegistry);

}
