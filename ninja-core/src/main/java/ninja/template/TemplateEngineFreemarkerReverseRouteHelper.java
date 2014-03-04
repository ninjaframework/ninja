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
import ninja.Router;

@Singleton
public class TemplateEngineFreemarkerReverseRouteHelper {

    final Router router;

    @Inject
    public TemplateEngineFreemarkerReverseRouteHelper(Router router) {
        this.router = router;

    }

    public TemplateModel computeReverseRoute(List args) throws TemplateModelException {

        if (args.size() < 2) {

            throw new TemplateModelException(
                    "Please specify at least classname and controller (2 parameters).");

        } else {

            List<String> strings = new ArrayList<>(args.size());

            for (Object o : args) {

                // We currently allow only numbers and strings as arguments
                if (o instanceof String) {
                    strings.add((String) o);
                } if (o instanceof SimpleScalar) {
                    strings.add(((SimpleScalar) o).getAsString());
                } else if (o instanceof SimpleNumber) {
                    strings.add(((SimpleNumber) o).toString());
                }

            }

            try {

                Class<?> clazz = Class.forName(strings.get(0));
                
                Object [] parameterMap = strings.subList(2, strings.size()).toArray();

                String reverseRoute = router.getReverseRoute(
                        clazz,
                        strings.get(1),
                        parameterMap);

                return new SimpleScalar(reverseRoute);
            } catch (ClassNotFoundException ex) {
                throw new TemplateModelException("Error. Cannot find class for String: " + strings.get(0));
            }
        }

    }
}
