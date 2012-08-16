package ninja.params;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import ninja.Context;
import ninja.RoutingException;
import ninja.validation.Validator;
import ninja.validation.WithValidator;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;

/**
 * Invokes methods on the controller, extracting arguments out
 *
 * @author James Roper
 */
public class ControllerMethodInvoker {
    private final Method method;
    private final ArgumentExtractor<?>[] argumentExtractors;

    ControllerMethodInvoker(Method method, ArgumentExtractor<?>[] argumentExtractors) {
        this.method = method;
        this.argumentExtractors = argumentExtractors;
    }

    public Object invoke(Object controller, Context context) {
        // Extract arguments
        Object[] arguments = new Object[argumentExtractors.length];
        for (int i = 0; i < argumentExtractors.length; i++) {
            arguments[i] = argumentExtractors[i].extract(context);
        }
        try {
            return method.invoke(controller, arguments);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            } else {
                throw new RuntimeException(e.getCause());
            }
        }
    }

    public static ControllerMethodInvoker build(Method method, Injector injector) {
        // get both the parameters...
        final Class[] paramTypes = method.getParameterTypes();
        // ... and all annotations for the parameters
        final Annotation[][] paramAnnotations = method
                .getParameterAnnotations();

        ArgumentExtractor<?>[] argumentExtractors = new ArgumentExtractor<?>[paramTypes.length];

        // now we skip through the parameters and process the annotations
        for (int i = 0; i < paramTypes.length; i++) {
            try {
                argumentExtractors[i] = getArgumentExtractor(paramTypes[i], paramAnnotations[i],
                        injector);
            } catch (RoutingException e) {
                throw new RoutingException("Error building argument extractor for parameter " + i +
                        " in method " + method.getDeclaringClass().getName() + "." + method.getName() + "()", e);
            }
        }

        // Replace a null extractor with a bodyAs extractor, but make sure there's only one
        boolean bodyAsFound = false;
        for (int i = 0; i < argumentExtractors.length; i++) {
            if (argumentExtractors[i] == null) {
                if (bodyAsFound) {
                    throw new RoutingException("Only one parameter may be deserialised as the body " +
                            method.getDeclaringClass().getName() + "." + method.getName() + "()");
                } else {
                    argumentExtractors[i] = new ArgumentExtractors.BodyAsExtractor(paramTypes[i]);
                    bodyAsFound = true;
                }
            }
        }

        // Now that every parameter has an argument extractor we can run validation on the annotated
        // parameters
        for (int i = 0; i < argumentExtractors.length; i++) {
            argumentExtractors[i] =
                    validateArgumentWithExtractor(paramTypes[i], paramAnnotations[i], injector,
                    argumentExtractors[i]);
        }

        return new ControllerMethodInvoker(method, argumentExtractors);
    }

    private static ArgumentExtractor<?> getArgumentExtractor(Class<?> paramType,
            Annotation[] annotations, Injector injector) {
        // First check if we have a static extractor
        ArgumentExtractor<?> extractor = ArgumentExtractors.getExtractorForType(paramType);

        if (extractor == null) {
            // See if we have a WithArgumentExtractor annotated annotation
            for (Annotation annotation : annotations) {
                WithArgumentExtractor withArgumentExtractor = annotation.annotationType()
                        .getAnnotation(WithArgumentExtractor.class);
                if (withArgumentExtractor != null) {
                    extractor = instantiateComponent(withArgumentExtractor.value(), annotation,
                            paramType, injector);
                    break;
                }
            }
        }

        return extractor;
    }

    private static ArgumentExtractor<?> validateArgumentWithExtractor(Class<?> paramType,
            Annotation[] annotations, Injector injector, ArgumentExtractor<?> extractor) {
        // We have validators that get applied before parsing, and validators
        // that get applied after parsing.
        List<Validator<?>> preParseValidators = new ArrayList<Validator<?>>();
        List<Validator<?>> postParseValidators = new ArrayList<Validator<?>>();

        Class<?> boxedParamType = paramType;
        if (paramType.isPrimitive()) {
            boxedParamType = box(paramType);
        }

        // Now we have an extractor, lets apply validators that are able to validate
        for (Annotation annotation : annotations) {
            WithValidator withValidator = annotation.annotationType()
                    .getAnnotation(WithValidator.class);
            if (withValidator != null) {
                Validator<?> validator = instantiateComponent(withValidator.value(), annotation,
                        paramType, injector);
                // If the validator can validate the extractors type, then it's a pre parse validator
                if (validator.getValidatedType().isAssignableFrom(extractor.getExtractedType())) {
                    preParseValidators.add(validator);
                    // If it can validate the parameter type, it's a post parse validator
                } else if (validator.getValidatedType().isAssignableFrom(boxedParamType)) {
                    postParseValidators.add(validator);
                    // Otherwise, we can't validate with this validator
                } else {
                    throw new RoutingException("Validator for field " + extractor.getFieldName() +
                            " validates type " + validator.getValidatedType() +
                            ", which doesn't match extracted type " + extractor.getExtractedType() +
                            " or parameter type " + paramType);
                }
            }
        }

        // If we have pre parse validators, wrap our extractor in them
        if (!preParseValidators.isEmpty()) {
            extractor = new ValidatingArgumentExtractor(extractor, preParseValidators);
        }

        // Either the extractor extracts a type that matches the param type, or it's a
        // String, and we can lookup a parser to parse it into the param type
        if (!boxedParamType.isAssignableFrom(extractor.getExtractedType())) {
            if (extractor.getFieldName() != null) {
                if (String.class.isAssignableFrom(extractor.getExtractedType())) {
                    // Look up a parser
                    ParamParser<?> parser = ParamParsers.getParamParser(paramType);
                    if (parser == null) {
                        throw new RoutingException("Can't find parameter parser for type "
                                + extractor.getExtractedType() + " on field "
                                + extractor.getFieldName());
                    } else {
                        extractor =
                                new ParsingArgumentExtractor((ArgumentExtractor) extractor, parser);
                    }

                } else {
                    throw new RoutingException("Extracted type " + extractor.getExtractedType()
                            + " for field " + extractor.getFieldName()
                            + " doesn't match parameter type " + paramType);
                }
            }
        }

        // If we have any post parse validators, wrap our extractor in them
        if (!postParseValidators.isEmpty()) {
            extractor = new ValidatingArgumentExtractor(extractor, postParseValidators);
        }

        return extractor;
    }

    private static <T> T instantiateComponent(Class<? extends T> argumentExtractor,
            final Annotation annotation, final Class<?> paramType,
            Injector injector) {
        // Noarg constructor
        Constructor noarg = getNoArgConstructor(argumentExtractor);
        if (noarg != null) {
            try {
                return (T) noarg.newInstance();
            } catch (Exception e) {
                throw new RoutingException(e);
            }
        }
        // Simple case, just takes the annotation
        Constructor simple = getSingleArgConstructor(argumentExtractor, annotation.annotationType());
        if (simple != null) {
            try {
                return (T) simple.newInstance(annotation);
            } catch (Exception e) {
                throw new RoutingException(e);
            }
        }
        // Simple case, just takes the parsed class
        Constructor simpleClass = getSingleArgConstructor(argumentExtractor, Class.class);
        if (simpleClass != null) {
            try {
                return (T) simpleClass.newInstance(paramType);
            } catch (Exception e) {
                throw new RoutingException(e);
            }
        }
        // Complex case, use Guice.  Create a child injector with the annotation in it.
        return injector.createChildInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind((Class<Annotation>) annotation.annotationType()).toInstance(annotation);
                bind(ArgumentClassHolder.class).toInstance(new ArgumentClassHolder(paramType));
            }
        }).getInstance(argumentExtractor);
    }

    private static Constructor getNoArgConstructor(Class<?> clazz) {
        for (Constructor constructor : clazz.getConstructors()) {
            if (constructor.getParameterTypes().length == 0) {
                return constructor;
            }
        }
        return null;
    }

    private static Constructor getSingleArgConstructor(Class<?> clazz, Class<?> arg) {
        for (Constructor constructor : clazz.getConstructors()) {
            if (constructor.getParameterTypes().length == 1) {
                if (constructor.getParameterTypes()[0].isAssignableFrom(arg)) {
                    return constructor;
                }
            }
        }
        return null;
    }

    private static Class<?> box(Class<?> typeToBox) {
        if (typeToBox == int.class) {
            return Integer.class;
        } else if (typeToBox == boolean.class) {
            return Boolean.class;
        } else if (typeToBox == long.class) {
            return Long.class;
        } else if (typeToBox == float.class) {
            return Float.class;
        } else if (typeToBox == double.class) {
            return Double.class;
        }
        throw new IllegalArgumentException("Don't know how to box type of " + typeToBox);
    }


}
