package ninja.servlet.conf;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * Application Module Stub to be used in unit tests
 *
 * @author avarabyeu
 */
public class Module extends AbstractModule {

    public static final String TEST_CONSTANT_NAME = "application.main.module.constant";

    @Override
    protected void configure() {
        binder().bindConstant().annotatedWith(Names.named(TEST_CONSTANT_NAME)).to(true);
    }
}
