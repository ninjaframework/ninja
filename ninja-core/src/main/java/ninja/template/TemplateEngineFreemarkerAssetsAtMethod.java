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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import ninja.AssetsController;
import ninja.Router;

@Singleton
public class TemplateEngineFreemarkerAssetsAtMethod implements
        TemplateMethodModelEx {

    final Router router;
    final TemplateEngineFreemarkerReverseRouteHelper templateEngineFreemarkerReverseRouteHelper;

    @Inject
    public TemplateEngineFreemarkerAssetsAtMethod(
            Router router,
            TemplateEngineFreemarkerReverseRouteHelper templateEngineFreemarkerReverseRouteHelper) {
        this.router = router;
        this.templateEngineFreemarkerReverseRouteHelper = templateEngineFreemarkerReverseRouteHelper;

    }

    public TemplateModel exec(List args) throws TemplateModelException {

       List argsWithControllerAndMethod = new ArrayList(args.size() + 2);
       argsWithControllerAndMethod.add(AssetsController.class.getName());
       argsWithControllerAndMethod.add("serveStatic");
       argsWithControllerAndMethod.add("fileName");
       argsWithControllerAndMethod.addAll(args);
       
       return templateEngineFreemarkerReverseRouteHelper.computeReverseRoute(
               argsWithControllerAndMethod);

    }
}
