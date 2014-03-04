package ninja.jetbrick;

import com.google.inject.AbstractModule;
import jetbrick.template.JetEngine;
import ninja.jetbrick.template.JetTemplateEngine;
import ninja.template.TemplateEngine;

public class NinjaJetbrickModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(JetEngine.class).toProvider(JetEngineProvider.class);
        bind(TemplateEngine.class).to(JetTemplateEngine.class);
    }
}

