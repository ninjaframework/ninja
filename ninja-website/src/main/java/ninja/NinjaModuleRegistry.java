package ninja;

import com.google.inject.ImplementedBy;

@ImplementedBy(NinjaModuleRegistryImpl.class)
public interface NinjaModuleRegistry {
		
	public void addPlugin(Class<NinjaModule> ninjaApplication);

}
