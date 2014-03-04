package ninja.template;

import java.util.List;

import ninja.Context;
import ninja.Result;
import ninja.i18n.Messages;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.util.logging.Level;
import java.util.logging.Logger;
import ninja.Router;

@Singleton
public class TemplateEngineFreemarkerReverseRouteMethod implements
        TemplateMethodModelEx {

    final Router router;
    final TemplateEngineFreemarkerReverseRouteHelper templateEngineFreemarkerReverseRouteHelper;

    @Inject
    public TemplateEngineFreemarkerReverseRouteMethod(
            Router router,
            TemplateEngineFreemarkerReverseRouteHelper templateEngineFreemarkerReverseRouteHelper) {
        
        this.router = router;
        this.templateEngineFreemarkerReverseRouteHelper = templateEngineFreemarkerReverseRouteHelper;

    }

    public TemplateModel exec(List args) throws TemplateModelException {

       return templateEngineFreemarkerReverseRouteHelper.computeReverseRoute(args);

    }
}
