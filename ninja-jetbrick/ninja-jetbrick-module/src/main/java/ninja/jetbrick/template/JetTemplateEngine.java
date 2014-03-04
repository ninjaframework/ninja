package ninja.jetbrick.template;

import com.google.common.base.CaseFormat;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import jetbrick.template.JetEngine;
import jetbrick.template.JetTemplate;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.Router;
import ninja.i18n.Lang;
import ninja.i18n.Messages;
import ninja.template.TemplateEngine;
import ninja.utils.ResponseStreams;
import org.slf4j.Logger;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class JetTemplateEngine implements TemplateEngine {

    private final static String FILE_SUFFIX = ".html";

    private final Messages messages;

    private final Lang lang;

    private final JetEngine jetEngine;

    private final JetTemplateEngineHelper helper;

    private final JetExceptionHandler exceptionHandler;

    private final Logger logger;

    @Inject
    private Router router;

    @Inject
    public JetTemplateEngine(Messages messages, Lang lang, JetEngine jetEngine, JetTemplateEngineHelper jetbrickTemplateEngineHelper, JetExceptionHandler exceptionHandler, Logger ninjaLogger) {
        this.messages = messages;
        this.lang = lang;
        this.jetEngine = jetEngine;
        this.helper = jetbrickTemplateEngineHelper;
        this.exceptionHandler = exceptionHandler;
        this.logger = ninjaLogger;
    }

    @Override
    public void invoke(Context context, Result result) {
        Object object = result.getRenderable();
        ResponseStreams responseStreams = context.finalizeHeaders(result);

        Map map;
        if (object == null) {
            map = Maps.newHashMap();
        } else if (object instanceof Map) {
            map = (Map) object;
        } else {
            String realClassNameLowerCamelCase = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, object.getClass().getSimpleName());
            map = Maps.newHashMap();
            map.put(realClassNameLowerCamelCase, object);
        }

        Optional<String> language = lang.getLanguage(context, Optional.of(result));
        Map<String, Object> renderArgs = new HashMap<>();
        if (language.isPresent()) {
            renderArgs.put("lang", language.get());
        }

        if (!context.getSession().isEmpty()) {
            renderArgs.put("session", context.getSession().getData());
        }

        renderArgs.put("contextPath", context.getContextPath());


        // ========= flash ============
        Map<String, String> flash = new HashMap<>();
        for (Map.Entry<String, String> entry : context.getFlashScope().getCurrentFlashCookieData().entrySet()) {

            String messageValue = null;

            Optional<String> messageValueOptional = messages.get(entry.getValue(), context, Optional.of(result));

            if (!messageValueOptional.isPresent()) {
                messageValue = entry.getValue();
            } else {
                messageValue = messageValueOptional.get();
            }
            flash.put(entry.getKey(), messageValue);
        }
        renderArgs.put("flash", flash);

        map.putAll(renderArgs);


        logger.info("router = {}", router);


        String templateName = helper.getTemplateForResult(context.getRoute(), result, FILE_SUFFIX);

        logger.info("templateName = {}", templateName);

        try {
            PrintWriter writer = new PrintWriter(responseStreams.getWriter());
            logger.info("jetEngine = {} , templateName = {}", jetEngine, templateName);
            JetTemplate template = jetEngine.getTemplate(templateName);


            template.render(map, writer);

            writer.flush();
            writer.close();
        } catch (Exception e) {
            handleServerError(context, e);
        }

    }


    private void handleServerError(Context context, Exception e) {
        ResponseStreams outStream = context.finalizeHeaders(Results.internalServerError());

        JetEngine engine = JetEngine.create();


        exceptionHandler.handleException(e, null, outStream);
        e.printStackTrace();
    }

    @Override
    public String getSuffixOfTemplatingEngine() {
        return FILE_SUFFIX;
    }

    @Override
    public String getContentType() {
        return "text/html";
    }
}
