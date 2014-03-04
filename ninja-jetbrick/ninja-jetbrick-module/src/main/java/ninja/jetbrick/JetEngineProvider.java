package ninja.jetbrick;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import jetbrick.template.JetEngine;
import ninja.i18n.Messages;
import ninja.template.TemplateEngineManager;
import ninja.utils.NinjaProperties;
import org.slf4j.Logger;

import java.util.Properties;

@Singleton
public class JetEngineProvider implements Provider<JetEngine> {

    private final Messages messages;
    private final Logger logger;
    private final NinjaProperties ninjaProperties;
    private final TemplateEngineManager templateEngineManager;
    private JetEngine cachedEngine;

    @Inject
    public JetEngineProvider(Messages messages, Logger logger, NinjaProperties ninjaProperties, TemplateEngineManager templateEngineManager) {
        this.messages = messages;
        this.logger = logger;
        this.ninjaProperties = ninjaProperties;
        this.templateEngineManager = templateEngineManager;
    }

    @Override
    public JetEngine get() {
        if (cachedEngine == null) {
            synchronized (this) {
                if (cachedEngine == null) {
                    cachedEngine = createNewEngine();
                }
            }
        }
        return cachedEngine;
    }

    private JetEngine createNewEngine() {
        Properties properties = new Properties();
        properties.put("import.packages", "models.** , services.**");
        properties.put("template.suffix", ".html");
        if (!ninjaProperties.isDev()) {
            properties.put("template.reloadable", "false");
        } else {
            properties.put("template.reloadable", "true");
        }

        if (ninjaProperties.isProd()) {
            properties.put("compile.strategy", "precompile");
        } else {
            properties.put("compile.strategy", "auto");
        }

        //properties.put("import.tags" , "ninja.jetbrick.JetTags");
        properties.put("import.tags", "ninja.jetbrick.JetTags");

        JetEngine engine = JetEngine.create(properties);
        logger.info("jetEngine created , with properties : {}", properties);
        return engine;
    }
}
