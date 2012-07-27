package ninja;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Singleton;

@Singleton
public class NinjaModuleRegistryImpl implements NinjaModuleRegistry {

	List<Class<NinjaModule>> ninjaModules = new ArrayList<Class<NinjaModule>>();

	@Override
	public void addPlugin(Class<NinjaModule> ninjaApplication) {
		ninjaModules.add(ninjaApplication);

	}

}
