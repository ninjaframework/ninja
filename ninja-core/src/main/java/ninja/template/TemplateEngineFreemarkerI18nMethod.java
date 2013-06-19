package ninja.template;

import java.util.List;

import ninja.Context;
import ninja.Result;
import ninja.i18n.Messages;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class TemplateEngineFreemarkerI18nMethod implements
        TemplateMethodModelEx {

    final Messages messages;
    final Context context;
    final Optional<Result> result;

    public TemplateEngineFreemarkerI18nMethod(Messages messages,
                                              Context context,
                                              Result result) {
        this.messages = messages;
        this.context = context;
        this.result = Optional.of(result);

    }

    public TemplateModel exec(List args) throws TemplateModelException {

        if (args.size() == 0) {

            throw new TemplateModelException(
                    "Using i18n without any key is not possible.");

        } else if (args.size() == 1) {

            Optional<String> optionalString = messages
                    .get(((SimpleScalar) args.get(0)).getAsString(), context,
                            result);

            if (!optionalString.isPresent()) {
                throw new TemplateModelException(
                        "Could not find translated message for: "
                                + (((SimpleScalar) args.get(0)).getAsString()));

            } else {
                return new SimpleScalar(optionalString.get());
            }

        } else if (args.size() > 1) {

            List<String> strings = Lists.newArrayList();
            
            for (Object o : args) {
                
                // We currently allow only numbers and strings as arguments
                if (o instanceof SimpleScalar) {
                    strings.add(((SimpleScalar) o).getAsString());
                } else if  (o instanceof SimpleNumber) {
                    strings.add(((SimpleNumber) o).toString());
                }
                
            }

            Optional<String> optionalString = messages.get(strings.get(0),
                    context, result, strings.subList(1, strings.size())
                            .toArray());

            if (!optionalString.isPresent()) {
                throw new TemplateModelException(
                        "Could not find translated message for: "
                                + (((SimpleScalar) args.get(0)).getAsString()));

            } else {
                return new SimpleScalar(optionalString.get());
            }

        } else {

            throw new TemplateModelException(
                    "Using i18n without any key is not possible.");

        }

    }
}
